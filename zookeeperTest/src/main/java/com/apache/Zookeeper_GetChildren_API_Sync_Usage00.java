/**     
 * @FileName: Zookeeper_GetChildren_API_Sync_Usage.java   
 * @Package:com.apache   
 * @Description: TODO  
 * @author: LUCKY    
 * @date:2016年1月22日 上午9:38:22   
 * @version V1.0     
 */
package com.apache;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

/**
 * @ClassName: Zookeeper_GetChildren_API_Sync_Usage
 * @Description: 获取子节点列表的api
 * @author: LUCKY
 * @date:2016年1月22日 上午9:38:22
 */
public class Zookeeper_GetChildren_API_Sync_Usage00 implements Watcher {

	private static CountDownLatch countDownLatch = new CountDownLatch(1);
	private static ZooKeeper zooKeeper = null;

	public static void main(String[] args) throws Exception {
		zooKeeper = new ZooKeeper("100.66.162.37:2181", 5000,
				new Zookeeper_GetChildren_API_Sync_Usage00());
		countDownLatch.await();

		//如果是true的话，则会使用默认的监听配置
		List<String> list1 = zooKeeper.getChildren("/", true);
		System.out.println("第一个获取的节点数据有" + list1.toString());

		Thread.sleep(Integer.MAX_VALUE);

	}

	public static void getChilrenList(String path)
			throws KeeperException, InterruptedException {
		List<String> list1 = zooKeeper.getChildren(path, true);
		System.out.println("通过watcher监听获取的数据有" + list1.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @param event
	 * 
	 * @see
	 * org.apache.zookeeper.Watcher#process(org.apache.zookeeper.WatchedEvent)
	 */
	// 注册的watcher监听
	public void process(WatchedEvent event) {
		System.out.println("Receive watched event:" + event);
		if (KeeperState.SyncConnected == event.getState()) {
			System.out.println("连接成功了");
			if (event.getType() == EventType.None && null == event.getPath()) {
				countDownLatch.countDown();
				//子节点改变后的监听配置
			}else if (event.getType()==EventType.NodeChildrenChanged) {
				try {
					getChilrenList(event.getPath());
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
