/**     
 * @FileName: GetData_API_Sync_Usage.java   
 * @Package:com.apache   
 * @Description: TODO  
 * @author: LUCKY    
 * @date:2016年1月22日 下午1:00:09   
 * @version V1.0     
 */
package com.apache;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * @ClassName: GetData_API_Sync_Usage
 * @Description: 获取节点数据信息
 * @author: LUCKY
 * @date:2016年1月22日 下午1:00:09
 */
public class GetData_API_Sync_Usage implements Watcher {

	private static CountDownLatch countDownLatch = new CountDownLatch(1);
	private static ZooKeeper zooKeeper = null;

	public static void main(String[] args) throws InterruptedException,
			Exception {
		zooKeeper = new ZooKeeper("100.66.162.37:2181", 5000,
				new GetData_API_Sync_Usage());
		countDownLatch.await();
		byte[] content = zooKeeper.getData("/", true, new Stat());
		System.out.println(content.toString());
	}

	public void getDatas() throws KeeperException, InterruptedException {
		byte[] content = zooKeeper.getData("/", true, new Stat());
		System.out.println(content.toString());
	}

	public void process(WatchedEvent event) {
		// 连接成功的标识状态
		if (KeeperState.SyncConnected == event.getState()) {
			countDownLatch.countDown();

			// 连接成功后，对节点的内容进行监听
			// 如果数据节点的内容进行修改后，就需要重新获取节点的相关的信息
			if (event.getType() == EventType.NodeDataChanged) {
				try {
					getDatas();
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
