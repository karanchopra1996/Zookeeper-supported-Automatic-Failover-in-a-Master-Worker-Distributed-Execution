We need jar files from

1)
apache-zookeeper-3.7.1-bin/lib

2)
https://www.apache.org/dyn/closer.lua/logging/log4j/2.18.0/apache-log4j-2.18.0-bin.tar.gz


This project uses Zookeeper to implement an automatic failover mechanism in a master-worker
distributed execution of graph-bridge programs. Client.java, (i.e., the master) connects to ZooKeeper;
submits 10 different tasks, each executing GraphBride.java with a different graph size; and waits for all
tasks to be completed by remote worker processes. Each worker which should be implemented in
Worker.java joins Zookeeper; repetitively starts a new task; and fails over a task if it is not done within
100 seconds. Distributed synchronization must be implemented in Key.java to have all the workers access
the bag of tasks exclusively.


Cleint.java 
Connects to ZooKeeper at a given TCP port in args[0], creates the /workers
and the /tasks nodes persistently; submits 10 tasks under /tasks where each
task has “submitted” as its data and is identified with task-000000000d
where d = 1, 2, …, 10; launches 10 event watchers, each checking a task
deletion from /tasks; and finally deletes /workers and /tasks from ZooKeeper
upon no more tasks under /tasks.


Worker.java 
Joins ZooKeeper at a given TCP port in args[0]; registers itself as worker-
00000000d where d = 1, 2, …, 10 under /workers; and repeats picking up a
new task until /tasks becomes empty. For each task picked up, Worker.java
checks its data: “submitted” allows the worker to update its data with the
current timestamp and to launch a new GraphBridge program, otherwise the
data should be a past timestamp when someone else picked up this task. In
that case, Worker.java checks if the task is overdue beyond 100,000msec. If
so, let’s restart this task. Otherwise, Worker.java simply leaves the current
task execution, assuming that the task may be stalled.


Key.java
Implements lock( ) and unlock( ). They are used for each worker to
exclusively access the /tasks node when picking up and updating a task-
000000000d in a non-interruptive fashion. The lock( ) method creates the
/lock node. If the node has been already created, (i.e., someone else has
locked), the worker launches an event watcher and waits on itself. The event
watcher is woken up upon a deletion of /lock and notify the worker. The
unlock( ) method simply deletes the /lock node.
GraphBridge.java Is a Java application to be executed by Worker.java. It receives the number
of vertices, (say N) in args[0]; generates a random graph with N vertices;
and find all graph bridges using depth-first search. You don’t have to
understand the details of the algorithm.


Detailed explanantion:

Worker.java: The Worker class comprises various methods that serve different purposes within the class. The startZK() method initializes the ZooKeeper session by establishing a connection with the ZooKeeper server and setting a session timeout. The process(WatchedEvent e) method handles ZooKeeper events by printing event information and updating the connection status accordingly. The close() method is responsible for closing the ZooKeeper connection. The isConnected() and isExpired() methods verify the connection and session expiration status, respectively. The register() method registers the Worker under the "/workers" znode in ZooKeeper, creating an ephemeral sequential znode to uniquely identify the Worker. The pickupTask() method retrieves tasks from the "/tasks" znode, selecting a task marked as "submitted" or an overdue task. The runTask(String taskID) method executes a task by launching a Java process with specific arguments and waiting for its completion. The finishTask(String taskID) method marks a task as completed by deleting its corresponding znode. Finally, the taskWatcher instance monitors changes in the "/tasks" znode and prints incoming watch events. These methods collectively enable the Worker to establish a connection with ZooKeeper, register as a worker, pick up and execute tasks, and effectively handle ZooKeeper events and session management.


Key.java: The Key class demonstrates a locking mechanism implemented using Apache ZooKeeper. It includes methods such as lock(), unlock(), and lockWatcher to achieve this functionality. In the lock() method, the code attempts to create an ephemeral znode named "/lock" using zk.create(). If the creation is successful, it acquires the lock. If the znode already exists, indicating that it is locked by someone else, the method sets a watcher using zk.exists() to wait for the deletion of the "/lock" znode. Once the znode is deleted, the watcher notifies the waiting worker to resume its operation. The method continues its attempts to acquire the lock until it succeeds. The unlock() method releases the lock by deleting the "/lock" znode using zk.delete(). Additionally, the lockWatcher is a watcher instance that monitors events related to the "/lock" znode. When the znode is deleted, the watcher informs the waiting worker.Establishes a locking mechanism using ZooKeeper, allowing workers to acquire and release a lock on the "/lock" znode.







