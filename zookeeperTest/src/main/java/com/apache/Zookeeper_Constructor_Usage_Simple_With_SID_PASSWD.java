/**     
 * @FileName: Zookeeper_Constructor_Usage_Simple_With_SID_PASSWD.java   
 * @Package:com.apache   
 * @Description: TODO  
 * @author: LUCKY    
 * @date:2016年1月21日 下午8:43:03   
 * @version V1.0     
 */
package com.apache;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

/**  
 * @ClassName: Zookeeper_Constructor_Usage_Simple_With_SID_PASSWD   
 * @Description: 同步的创建节点
 * @author: LUCKY  
 * @date:2016年1月21日 下午8:43:03     
 */
public class Zookeeper_Constructor_Usage_Simple_With_SID_PASSWD implements Watcher {

	private static CountDownLatch connectedSemaphore=new CountDownLatch(1);
	
	public static void main(String[] args) throws Exception {
		ZooKeeper zooKeeper=new ZooKeeper("100.66.162.37:2181", 5000, new Zookeeper_Constructor_Usage_Simple_With_SID_PASSWD());
		connectedSemaphore.await();
		long sessionId=zooKeeper.getSessionId();
		byte[] passwd=zooKeeper.getSessionPasswd();
		//利用不合法的会话ID和会话密钥
		zooKeeper=new ZooKeeper("100.66.162.37:2181", 5000, new Zookeeper_Constructor_Usage_Simple_With_SID_PASSWD(), sessionId, "test".getBytes());
		//利用合法的会话ID和会话密钥
		zooKeeper=new ZooKeeper("100.66.162.37:2181", 5000, new Zookeeper_Constructor_Usage_Simple_With_SID_PASSWD(), sessionId, passwd);
		Thread.sleep(Integer.MAX_VALUE);
	}
	
	/* (non-Javadoc)   
	 * @param event   
	 * @see org.apache.zookeeper.Watcher#process(org.apache.zookeeper.WatchedEvent)   
	 */  
	public void process(WatchedEvent event) {
		System.out.println("Receive watched event:"+event);
		if(KeeperState.SyncConnected==event.getState()){
			System.out.println("连接成功了");
			connectedSemaphore.countDown();
		}
	}

}
