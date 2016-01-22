/**     
 * @FileName: SetData_API_Sync_Usage.java   
 * @Package:com.apache   
 * @Description: TODO  
 * @author: LUCKY    
 * @date:2016年1月22日 下午1:57:54   
 * @version V1.0     
 */
package com.apache;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * @ClassName: SetData_API_Sync_Usage
 * @Description: 同步更新节点信息
 * @author: LUCKY
 * @date:2016年1月22日 下午1:57:54
 */
public class SetData_API_Sync_Usage implements Watcher {

	private static CountDownLatch countDownLatch = new CountDownLatch(1);
	private static ZooKeeper zooKeeper = null;
	private static Stat stat = new Stat();

	public static void main(String[] args) throws Exception {
		zooKeeper = new ZooKeeper("100.66.162.37:2181", 5000,
				new Zookeeper_GetChildren_API_Sync_Usage00());
		String path = "/999";
		countDownLatch.await();
		zooKeeper.create(path, "123".getBytes(), Ids.OPEN_ACL_UNSAFE,
				CreateMode.EPHEMERAL);
		zooKeeper.getData(path, true, null);
		Stat stat = zooKeeper.setData(path, "456".getBytes(), -1);
		System.out.println(stat.getAversion()+","+stat.getCzxid()+","+stat.getMtime());
		Stat stat2=zooKeeper.setData(path, "456".getBytes(), stat.getVersion());
		System.out.println(stat2.getVersion()+","+stat2.getCzxid());
		
		zooKeeper.setData(path, "456".getBytes(), stat.getVersion());
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

		if(KeeperState.SyncConnected==event.getState()){
			if(EventType.None==event.getType() && null== event.getPath()){
				countDownLatch.countDown();
			}
		}
	}

}
