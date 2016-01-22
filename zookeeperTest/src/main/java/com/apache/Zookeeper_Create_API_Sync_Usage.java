/**     
 * @FileName: Zookeeper_Create_API_Sync_Usage.java   
 * @Package:com.apache   
 * @Description: TODO  
 * @author: LUCKY    
 * @date:2016年1月21日 下午8:51:15   
 * @version V1.0     
 */
package com.apache;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

/**  
 * @ClassName: Zookeeper_Create_API_Sync_Usage   
 * @Description: TODO  
 * @author: LUCKY  
 * @date:2016年1月21日 下午8:51:15     
 */
public class Zookeeper_Create_API_Sync_Usage implements Watcher{

	private static CountDownLatch connectedSemaphore=new CountDownLatch(1);
	
	
	
	public static void main(String[] args) throws Exception {
		ZooKeeper zooKeeper=new ZooKeeper("100.66.162.37:2181", 5000, new Zookeeper_Create_API_Sync_Usage());
		connectedSemaphore.await();
		//临时节点
		String path1=zooKeeper.create("/zk-test-ephemeral-", "".getBytes(),Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		System.out.println("创建的节点为"+path1);
		//临时顺序节点
		String path2=zooKeeper.create("/zk-test-ephemeral-", "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		System.out.println("创建的节点为"+path2);

	}
	
	/* (non-Javadoc)   
	 * @param event   
	 * @see org.apache.zookeeper.Watcher#process(org.apache.zookeeper.WatchedEvent)   
	 */  
	public void process(WatchedEvent event) {
		if(KeeperState.SyncConnected==event.getState()){
			connectedSemaphore.countDown();
		}
	}

}
