/**     
 * @FileName: Exist_API_Sync_Usage.java   
 * @Package:com.apache   
 * @Description: TODO  
 * @author: LUCKY    
 * @date:2016年1月22日 下午5:26:39   
 * @version V1.0     
 */
package com.apache;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

/**
 * @ClassName: Exist_API_Sync_Usage
 * @Description: 同步判断节点是否已经存在
 * @author: LUCKY
 * @date:2016年1月22日 下午5:26:39
 */
public class Exist_API_Sync_Usage implements Watcher {

	private static CountDownLatch countDownLatch = new CountDownLatch(1);
	private static ZooKeeper zooKeeper = null;

	public static void main(String[] args) throws Exception {
		zooKeeper = new ZooKeeper("100.66.162.37:2181", 5000,
				new Exist_API_Sync_Usage());
		countDownLatch.await();
		String path = "/feifei";
		zooKeeper.exists(path, true);
		zooKeeper.create(path, "".getBytes(), Ids.OPEN_ACL_UNSAFE,
				CreateMode.PERSISTENT);
		zooKeeper.setData(path, "123".getBytes(), -1);
		zooKeeper.create(path + "/c1", "".getBytes(), Ids.OPEN_ACL_UNSAFE,
				CreateMode.PERSISTENT);
		
		zooKeeper.delete(path + "/c1", -1);
		zooKeeper.delete(path, -1);
		Thread.sleep(Integer.MAX_VALUE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @param event
	 * 
	 * @see
	 * org.apache.zookeeper.Watcher#process(org.apache.zookeeper.WatchedEvent)
	 */
	public void process(WatchedEvent event) {
		if (KeeperState.SyncConnected == event.getState()) {
			if (EventType.None == event.getType() && null == event.getPath()) {
				countDownLatch.countDown();
			} else if (EventType.NodeCreated == event.getType()) {
				System.out.println("Node" + event.getPath() + "created");
				try {
					zooKeeper.exists(event.getPath(), true);
				} catch (KeeperException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (EventType.NodeDataChanged == event.getType()) {
				System.out.println("node" + event.getPath() + "datachanged");
				try {
					zooKeeper.exists(event.getPath(), true);
				} catch (KeeperException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if (event.getType()==EventType.NodeDeleted) {
				System.out.println("node" + event.getPath() + "datadelete");
				try {
					zooKeeper.exists(event.getPath(), true);
				} catch (KeeperException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

}