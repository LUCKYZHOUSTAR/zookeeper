/**     
 * @FileName: Master.java   
 * @Package:com.apache   
 * @Description: TODO  
 * @author: LUCKY    
 * @date:2016年1月16日 下午5:18:45   
 * @version V1.0     
 *//*
package com.apache;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.apache.zookeeper.AsyncCallback.Children2Callback;
import org.apache.zookeeper.AsyncCallback.ChildrenCallback;
import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.AsyncCallback.VoidCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

*//**
 * @ClassName: Master
 * @Description: 这个类用来描述主从模式的结构 master需要了解所有的额worker，并且决定分配给他们新的task
 *               整个流程是这样的，master获取所有空闲的worker列表，，并且监听每一个worker的改变
 *               同时master也需要获取所有的任务，并且监听任务的状态信息。 对于每一个新的任务，都需要分配给空闲的worker
 * 
 *               在执行master之前，zookeeper需要获取一个主的master
 * 
 *               客户端的状态有三种分别是：运行、被获取、没有被选中
 *               运行代表所有的节点争取主master，被选中代表有一个主节点，没有被选中，说的是 备份的主master的状态信息
 *               由于worker可能会down掉，所以master也需要去重新分配任务，
 *               主master同样也会down掉，所以备份的主master需要替代主master，去重新安排worker 和task之间的关系
 * 
 * @author: LUCKY
 * @date:2016年1月16日 下午5:18:45
 *//*
public class Master implements Watcher, Closeable {

	private static final Logger LOG = LoggerFactory.getLogger(Master.class);

	// master的三种状态信息
	enum MasterStates {
		RUNNING, ELECTED, NOTELECTED
	};

	// 状态变量
	private volatile MasterStates state = MasterStates.RUNNING;

	public MasterStates getState() {
		return state;
	}

	private Random random = new Random(this.hashCode());
	private ZooKeeper zk;
	private String hostPort;
	private String serverId = Integer.toHexString(random.nextInt());
	private volatile boolean connected = false;
	private volatile boolean expired = false;
	protected ChildrenCache tasksCache;
	protected ChildrenCache workersCache;

	Master(String hostPort) {
		this.hostPort = hostPort;
	}

	void startZK() throws IOException {
		zk = new ZooKeeper(hostPort, 15000, this);
	}

	void stopZK() throws InterruptedException, IOException {
		zk.close();
	}

	
	 * 这个方法实现了watcher的接口，我们用它来处理一些不同状态的会话zookeeper客户端的监听
	 
	public void process(WatchedEvent event) {
		LOG.info("Processing event:" + event.toString());
		if (event.getType() == Event.EventType.None)
			;
		{
			switch (event.getState()) {
			// 连接成功
			case SyncConnected:
				// 连接成功
				connected = true;
				break;
			case Disconnected:
				connected = false;
				break;
			case Expired:
				connected = false;
				expired = true;
				LOG.error("连接超时");
			default:
				break;
			}
		}

	}

	public void bootStrap() {
		createParent("/workers", new byte[0]);
		createParent("/assign", new byte[0]);
		createParent("/tasks", new byte[0]);
		createParent("/status", new byte[0]);
	}

	void createParent(String path, byte[] data) {
		zk.create(path, data, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT,
				createParentCallBack, data);
	}

	// 创建节点的异步回调
	StringCallback createParentCallBack = new StringCallback() {

		public void processResult(int rc, String path, Object ctx, String name) {
			switch (Code.get(rc)) {
			// 连接中断的话，会重新连接
			case CONNECTIONLOSS:
				createParent(path, (byte[]) ctx);
				break;
			case OK:
				LOG.info("创建完成父节点");

			case NODEEXISTS:
				LOG.warn("父节点已经被创建了" + path);
			default:
				LOG.error("出错了：" + KeeperException.create(Code.get(rc), path));
				break;
			}
		}
	};

	// 返回master的状态
	boolean isConnected() {
		return connected;
	}

	// 返回过期的信息
	boolean isExpired() {
		return expired;
	}

	
	 * * 整个流程是这样的，我们尽力去创建master节点，如果成功的话，就会做为一个主节点 当然了在这个过程中也需要注意一些异常的信息处理
	 * 
	 * 首先我们可能回得到一个丢失连接的异常，在我们得到回应之前。
	 * 因此我们需要去获取master信息，如果能够获取到，那么就证明该master是主节点，否则的话，我们就在此创建
	 * 
	 * * 第二种情况，如果发现节点已经存在，我们需要在此节点上放置一个监听
	 

	// 创建节点的回调函数
	StringCallback masterCreateCallback = new StringCallback() {

		public void processResult(int rc, String path, Object ctx, String name) {
			switch (Code.get(rc)) {
			// 连接丢失
			case CONNECTIONLOSS:
				checkMaster();
				break;

			default:
				break;
			}

		}
	};

	// 检查节点的信息情况
	void checkMaster() {

		zk.getData("/master", false, masterCheckCallback, null);
	}

	// 检查节点的回调函数
	DataCallback masterCheckCallback = new DataCallback() {

		public void processResult(int rc, String path, Object ctx, byte[] data,
				Stat stat) {
			switch (Code.get(rc)) {
			// 连接失败，重新检查
			case CONNECTIONLOSS:
				checkMaster();
				break;
			// 节点不存在的话，会重新争取
			case NONODE:
				runForMaster();
			case OK:
				if (serverId.equals(new String(data))) {
					state = MasterStates.ELECTED;

				}
			default:
				break;
			}

		}
	};

	// 去创建一个master
	public void runForMaster() {
		LOG.info("争取创建一个master");
		zk.create("/master", serverId.getBytes(), Ids.OPEN_ACL_UNSAFE,
				CreateMode.EPHEMERAL, masterCreateCallback, null);
	}

	void takeLeadership() {
		LOG.info("获得worker的列表信息");

	}

	void getWorkers(){
		zk.getChildren("/workers", cb, ctx);
	}

	// worker状态改变的回调函数
	ChildrenCallback workerGetChildrenCallback = new ChildrenCallback() {

		public void processResult(int rc, String path, Object ctx,
				List<String> children) {
			switch (Code.get(rc)) {
			// 连接异常的话，就重新获取worker列表
			case CONNECTIONLOSS:
				getWorkers();
				break;
			case OK:
				// 成功的话，就进行分配工作
				LOG.info("成功获取workers的列表" + children.size() + "workers");
				// 分配任务
				reassignAndSet(children);
			default:
				LOG.error(
						"获取所有的子worker失败" + KeeperException.create(Code.get(rc)),
						path);
				break;
			}

		}
	};

	// 分配任务
	void reassignAndSet(List<String> children) {
		// 先添加的worker的信息
		List<String> toProcess;
		// 如果现在还没有worker的话，就填充操作
		if (workersCache == null) {
			workersCache = new ChildrenCache(children);
			toProcess = null;

		} else {
			// 如果有的话，就需要删除并且重新设置
			LOG.info("删除重置操作");
			toProcess = workersCache.removeAndSet(children);
		}

		if (toProcess != null) {
			// 遍历新添加的worker的信息
			for (String worker : toProcess) {

			}
		}
	}

	// 获取空闲的任务
	void getAbsentWorkerTasks(String worker){
		zk.getChildren("/assign/"+worker,false,)
	}

	ChildrenCallback workerAssignmentCallback = new ChildrenCallback() {

		public void processResult(int rc, String path, Object ctx,
				List<String> children) {
			switch (Code.get(rc)) {
			case CONNECTIONLOSS:
				getAbsentWorkerTasks(path);
				break;
			case OK:
				LOG.info("成功获取任务的列表" + children.size() + "tasks");
				for (String task : children) {

				}
			default:
				break;
			}

		}
	};

	// 重新分配任务跟没有任务的工人
	void getDataReassign(String path, String task) {
		zk.getData(path, false, getDataReassignCallback, task);
	}

	DataCallback getDataReassignCallback = new DataCallback() {

		public void processResult(int rc, String path, Object ctx, byte[] data,
				Stat stat) {
			switch (Code.get(rc)) {
			case CONNECTIONLOSS:
				getDataReassign(path, (String) ctx);
				break;
			case OK:

			default:
				break;
			}
		}
	};

	void recreateTask(RecreateTaskCtx ctx) {
		zk.create("/tasks/" + ctx.task, ctx.data, Ids.OPEN_ACL_UNSAFE,
				CreateMode.PERSISTENT, recreateTaskCallback, ctx);
	}

	StringCallback recreateTaskCallback = new StringCallback() {

		public void processResult(int rc, String path, Object ctx, String name) {
			switch (Code.get(rc)) {
			case CONNECTIONLOSS:
				recreateTask((RecreateTaskCtx) ctx);

				break;
			case OK:
				deleteAssignment(((RecreateTaskCtx) ctx).path);

				break;
			case NODEEXISTS:
				LOG.info("Node exists already, but if it hasn't been deleted, "
						+ "then it will eventually, so we keep trying: " + path);
				recreateTask((RecreateTaskCtx) ctx);

				break;
			default:
				LOG.error("Something wwnt wrong when recreating task",
						KeeperException.create(Code.get(rc)));
			}

		}
	};

	void deleteAssignment(String path) {
		zk.delete(path, -1, taskDeletionCallback, null);
	}

	VoidCallback taskDeletionCallback = new VoidCallback() {
		public void processResult(int rc, String path, Object rtx) {
			switch (Code.get(rc)) {
			case CONNECTIONLOSS:
				deleteAssignment(path);
				break;
			case OK:
				LOG.info("Task correctly deleted: " + path);
				break;
			default:
				LOG.error("Failed to delete task data"
						+ KeeperException.create(Code.get(rc), path));
			}
		}
	};

	class RecreateTaskCtx {
		String path;
		String task;
		byte[] data;

		RecreateTaskCtx(String path, String task, byte[] data) {
			this.path = path;
			this.task = task;
			this.data = data;
		}
	}

	DataCallback taskDataCallback = new DataCallback() {
		public void processResult(int rc, String path, Object ctx, byte[] data,
				Stat stat) {
			switch (Code.get(rc)) {
			case CONNECTIONLOSS:
				getTaskData((String) ctx);

				break;
			case OK:
				
				 * Choose worker at random.
				 
				List<String> list = workersCache.getList();
				String designatedWorker = list.get(random.nextInt(list.size()));

				
				 * Assign task to randomly chosen worker.
				 
				String assignmentPath = "/assign/" + designatedWorker + "/"
						+ (String) ctx;
				LOG.info("Assignment path: " + assignmentPath);
				createAssignment(assignmentPath, data);

				break;
			default:
				LOG.error("当尽力去得到任务数据的时候出错了",
						KeeperException.create(Code.get(rc), path));
			}
		}
	};

	
	
	void assignTasks(List<String> tasks) {
		for (String task : tasks) {
			getTaskData(task);
		}
	}

	void getTaskData(String task) {
		zk.getData("/tasks/" + task, false, taskDataCallback, task);
	}

	void createAssignment(String path, byte[] data) {
		zk.create(path, data, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT,
				assignTaskCallback, data);
	}

	StringCallback assignTaskCallback = new StringCallback() {
		public void processResult(int rc, String path, Object ctx, String name) {
			switch (Code.get(rc)) {
			case CONNECTIONLOSS:
				createAssignment(path, (byte[]) ctx);

				break;
			case OK:
				LOG.info("Task assigned correctly: " + name);
				deleteTask(name.substring(name.lastIndexOf("/") + 1));

				break;
			case NODEEXISTS:
				LOG.warn("Task already assigned");

				break;
			default:
				LOG.error("Error when trying to assign task.",
						KeeperException.create(Code.get(rc), path));
			}
		}
	};

	// 一旦分配了任务，就需要删除任务
	void deleteTask(String name) {
		zk.delete("/tasks/" + name, -1, taskDeleteCallback, null);
	}

	VoidCallback taskDeleteCallback = new VoidCallback() {
		public void processResult(int rc, String path, Object ctx) {
			switch (Code.get(rc)) {
			case CONNECTIONLOSS:
				deleteTask(path);

				break;
			case OK:
				LOG.info("成功删除 " + path);

				break;
			case NONODE:
				LOG.info("任务已经被成功的删除");

				break;
			default:
				LOG.error("出错了" + KeeperException.create(Code.get(rc), path));
			}
		}
	};

	public void close() throws IOException {
		if (zk != null) {
			try {
				zk.close();
			} catch (InterruptedException e) {
				LOG.warn("Interrupted while closing ZooKeeper session.", e);
			}
		}
	}

}
*/