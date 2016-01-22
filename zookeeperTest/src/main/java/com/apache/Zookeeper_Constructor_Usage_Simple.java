/**     
 * @FileName: Zookeeper_Constructor_Usage_Simple.java   
 * @Package:com.apache   
 * @Description: TODO  
 * @author: LUCKY    
 * @date:2016年1月21日 下午8:28:50   
 * @version V1.0     
 */
package com.apache;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

/**  
 * @ClassName: Zookeeper_Constructor_Usage_Simple   
 * @Description: JAVA的API创建最简单的zookeeper会话实例
 * @author: LUCKY  
 * @date:2016年1月21日 下午8:28:50     
 */
public class Zookeeper_Constructor_Usage_Simple implements Watcher {

	private static CountDownLatch connectedSemaphore=new CountDownLatch(1);
	
	public static void main(String[] args) throws Exception {
		ZooKeeper zooKeeper=new ZooKeeper("100.66.162.37:2181",5000,new Zookeeper_Constructor_Usage_Simple());
		System.out.println(zooKeeper.getState());
		try {
			connectedSemaphore.await();
			System.out.println("会话ID是"+zooKeeper.getSessionId()+"会话密钥是"+zooKeeper.getSessionPasswd());
		} catch (Exception e) {
			System.out.println("zookeeper session established");
		}
	}
	
	/* (non-Javadoc)   
	 * @param event   
	 * @see org.apache.zookeeper.Watcher#process(org.apache.zookeeper.WatchedEvent)   
	 */  
	public void process(WatchedEvent event) {

		System.out.println("Receive watched event:"+event);
		if(KeeperState.SyncConnected==event.getState()){
			
			connectedSemaphore.countDown();
		}
	}

}
