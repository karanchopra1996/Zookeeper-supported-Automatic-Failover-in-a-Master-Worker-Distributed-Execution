import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class Key {
	private ZooKeeper zk;                     // ZooKeeper connected to Workers
	private static Object syncObject = null;  // Used to suspend a worker

	/**
	 * Is the constructor that accepts a worker's ZooKeeper object and sets up
	 * a synchronization object with itself.
	 * @param zk_init a calling worker's ZooKeeper object
	 */
	public Key(ZooKeeper zk_init) {
		this.zk = zk_init;
		syncObject = this;
	}

	/**
	 * This is your homework assignment.
	 *
	 * Tries to obtain the key on /lock. If not, waits on syncObject (= this).
	 */
	public void lock() {
		while (true) {
			try {
				// Attempt to create an ephemeral znode named "/lock"
				String lock = zk.create("/lock",
						null,
						Ids.OPEN_ACL_UNSAFE,
						CreateMode.EPHEMERAL);

				// Upon successful creation of "/lock", the lock is acquired
				if (lock != null && lock.equals("/lock")) {
					System.out.println(lock + " acquired");
					return;
				}
				System.err.println(lock + " error"); // shouldn't happen

			} catch (KeeperException.NodeExistsException e) {
				// "/lock" already exists, indicating it is locked by someone else
				System.err.println("/lock locked already by someone else");
				try {
					// Set a watcher on "/lock" to wait for its deletion
					Stat lockStat = zk.exists("/lock", lockWatcher);
					if (lockStat != null) {
						synchronized (syncObject) {
							syncObject.wait();
						}
						System.out.println("/lock notified");
					}
				} catch (Exception another) {
					System.err.println(another.toString());
					continue;
				}
			} catch (Exception others) {
				System.err.println(others.toString());
				continue;
			}
		}
	}

	/**
	 * This is your homework assignment.
	 *
	 * Is invoked upon a watcher event: when "/lock" is deleted.
	 */
	Watcher lockWatcher = new Watcher() {
		public void process(WatchedEvent event) {
			System.out.println(event.toString());
			if (event.getType() == EventType.NodeDeleted) {
				// "/lock" was deleted, indicating the lock is released
				synchronized (syncObject) {
					syncObject.notify();
				}
				System.out.println("/lock unlocked informed");
			}
		}
	};

	/**
	 * This is your homework assignment.
	 *
	 * Unlocks the key on "/lock" by deleting the znode.
	 */
	public void unlock() {
		try {
			zk.delete("/lock", 0);
		} catch (Exception e) {
			System.err.println(e.toString());
			return;
		}
		System.out.println("/lock released");
	}
}