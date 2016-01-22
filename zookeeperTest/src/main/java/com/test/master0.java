/**     
 * @FileName: master.java   
 * @Package:com.test   
 * @Description: TODO  
 * @author: LUCKY    
 * @date:2016年1月15日 下午7:54:58   
 * @version V1.0     
 */
package com.test;

import java.io.IOException;
import java.util.Random;

import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * @ClassName: master
 * @Description: TODO
 * @author: LUCKY
 * @date:2016年1月15日 下午7:54:58
 */
public class master0 implements Watcher {

	ZooKeeper zk;
	String hostPort;
	boolean isLeader = false;
	private Random random = new Random(this.hashCode());
	private String serverId = Integer.toHexString(random.nextInt());

	/**  
	 *    
	 */
	public master0(String hostPort) {
		this.hostPort = hostPort;
	}

	void startZk() throws IOException {
		zk = new ZooKeeper(hostPort, 15000, this);
	}

	public void process(WatchedEvent event) {
		System.out.println(event);
	}

	void stopZk() throws Exception {
		zk.close();
	}

	void runForMaster() {
		// while (true) {
		// try {
		// zk.create("/master", serverId.getBytes(), Ids.OPEN_ACL_UNSAFE,
		// CreateMode.EPHEMERAL);
		// isLeader = true;
		// break;
		// } catch (KeeperException e) {
		// isLeader = false;
		// break;
		// } catch (InterruptedException e) {
		// }
		// if (checkMaster())
		// break;
		// }

		zk.create("/path", serverId.getBytes(), Ids.OPEN_ACL_UNSAFE,
				CreateMode.EPHEMERAL, masterCreateCallback, null);
	}

	void checkMaster() {
		// while (true) {
		// try {
		// Stat stat = new Stat();
		// byte[] data = zk.getData("/master", true, stat);
		// isLeader = new String(data).equals(serverId);
		// System.out.println(isLeader);
		// return true;
		// } catch (NoNodeException e) {
		// //没有master，所以在试一次
		// return false;
		// }catch (ConnectionLossException e) {
		// // TODO: handle exception
		// }
		//
		// }

		zk.getData("/path", false, masterCheckCallback, null);
	}

	DataCallback masterCheckCallback = new DataCallback() {

		public void processResult(int rc, String path, Object ctx, byte[] data,
				Stat stat) {
			switch (Code.get(rc)) {
			case CONNECTIONLOSS:
				checkMaster();
				return;
			case NONODE:
				runForMaster();
				return;

			default:
				break;
			}
		}
	};

	StringCallback masterCreateCallback = new StringCallback() {

		public void processResult(int rc, String path, Object ctx, String name) {
			System.out.println(Code.get(rc));
			switch (Code.get(rc)) {
			case CONNECTIONLOSS:
				checkMaster();
				return;
			case OK:
				isLeader = true;
				break;
			default:
				isLeader = false;

			}
			System.out.println("i am " + (isLeader ? "" : "not the leader"));

		}
	};

	// String serverId=Integer.toHexString(Random)
	public static void main(String[] args) throws Exception {
		master0 m = new master0("100.66.162.90:2180");
		m.startZk();
		m.runForMaster();
		if (m.isLeader) {
			System.out.println("i am a leader");
			Thread.sleep(6000);
		} else {
			System.out.println("some one else is the leader");
		}

		m.stopZk();
		//

		// m.stopZk();
	}
}
