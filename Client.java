import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class Client implements Watcher, Closeable {
    private ZooKeeper zk;
    private String hostPort;
    private volatile boolean connected = false;
    private volatile boolean expired = false;
    private int nTasksSubmitted;
    private int nTasksCompleted = 0;

    public Client(String hostPort, int numTasks) {
        this.hostPort = hostPort;
        this.nTasksSubmitted = numTasks;
    }

    public void startZK() throws IOException {
        zk = new ZooKeeper(hostPort, 15000, this);
    }

    public void process(WatchedEvent e) {
        System.out.println(e.toString() + ", " + hostPort);
        if (e.getType() == Event.EventType.None) {
            switch (e.getState()) {
                case SyncConnected:
                    connected = true;
                    break;
                case Disconnected:
                    connected = false;
                    break;
                case Expired:
                    expired = true;
                    connected = false;
                    System.err.println("Session expired");
                default:
                    break;
            }
        }
    }

    @Override
    public void close() throws IOException {
        System.out.println("Closing");
        try {
            zk.close();
        } catch (InterruptedException e) {
            System.err.println("ZooKeeper interrupted while closing");
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean isExpired() {
        return expired;
    }

    public static void main(String args[]) throws Exception {
        // Create a new instance of Client
        Client client = new Client(args[0], Integer.parseInt(args[1]));

        // Start the ZooKeeper connection
        client.startZK();

        // Wait until the client is connected to ZooKeeper
        while (!client.isConnected()) {
            Thread.sleep(100);
        }
        System.out.println("connected");

        // Create a worker node, bag of tasks, and confirm the completion of tasks
        client.createWorkerNode();
        client.createBagOfTasks();
        client.confirmEmptyBag();
    }

    private String pid = Long.toHexString(ProcessHandle.current().pid());

    private void createWorkerNode() throws Exception {
        // Create a persistent znode under "/workers" to represent the worker
        zk.create("/workers", pid.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println("/workers created by client " + pid);
    }

    private void createBagOfTasks() throws Exception {
        // Create a persistent znode under "/tasks" to represent the bag of tasks
        zk.create("/tasks", pid.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println("/tasks created by client " + pid);

        // Create sequential znodes under "/tasks" to represent submitted tasks
        for (int i = 0; i < nTasksSubmitted; i++) {
            String taskID = zk.create("/tasks/task-", "submitted".getBytes(), Ids.OPEN_ACL_UNSAFE,
                    CreateMode.PERSISTENT_SEQUENTIAL);
            System.out.println(taskID + " submitted");
        }
    }

    private void confirmEmptyBag() throws Exception {
        // Watch for task deletion and track the completion count
        for (int i = 0; i < nTasksSubmitted; i++) {
            zk.exists("/tasks/task-000000000" + i, taskWatcher);
            System.out.println("/tasks/task-000000000" + i + " under watch");
        }

        // Wait until all tasks are completed
        while (nTasksCompleted < nTasksSubmitted)
            Thread.sleep(1000);

        System.out.println("all tasks deleted");

        // Delete the "/tasks" znode
        zk.delete("/tasks", 0);

        // Wait until all workers have signed off
        while (true) {
            List<String> workers = zk.getChildren("/workers", false, null);
            if (workers == null || workers.size() == 0)
                break;
        }
        System.out.println("all workers signed off");

        // Delete the "/workers" znode
        zk.delete("/workers", 0);
    }

    Watcher taskWatcher = new Watcher() {
        public void process(WatchedEvent event) {
            System.out.println(event.toString());
            if (event.getType() == EventType.NodeDeleted) {
                // Increment the completed task count when a task is deleted
                nTasksCompleted++;
                System.out.println("deleted");
            } else {
                try {
                    // Set a new watcher when a task is not deleted
                    zk.exists(event.getPath(), taskWatcher);
                } catch (Exception exception) {
                }
            }
        }
    };
}
