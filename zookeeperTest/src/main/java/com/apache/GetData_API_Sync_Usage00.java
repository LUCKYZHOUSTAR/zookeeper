/**     
 * @FileName: GetData_API_Sync_Usage00.java   
 * @Package:com.apache   
 * @Description: TODO  
 * @author: LUCKY    
 * @date:2016年1月22日 下午1:11:01   
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
 * @ClassName: GetData_API_Sync_Usage00   
 * @Description: TODO  
 * @author: LUCKY  
 * @date:2016年1月22日 下午1:11:01     
 */
public class GetData_API_Sync_Usage00  implements Watcher{

	
	private static CountDownLatch countDownLatch = new CountDownLatch(1);
	private static ZooKeeper zooKeeper = null;
	private static Stat stat=new Stat();
	
	public static void main(String[] args) throws Exception {
		String path="/";
		zooKeeper = new ZooKeeper("100.66.162.37:2181", 5000,
				new Zookeeper_GetChildren_API_Sync_Usage00());
		
//		countDownLatch.await();
		
		System.out.println(new String(zooKeeper.getData("/123", true, stat)));
		System.out.println(stat.getCversion()+","+stat.getVersion()+"."+stat.getCzxid());
		
		Thread.sleep(Integer.MAX_VALUE);
	}
	/* (non-Javadoc)   
	 * @param event   
	 * @see org.apache.zookeeper.Watcher#process(org.apache.zookeeper.WatchedEvent)   
	 */  
	public void process(WatchedEvent event) {
		if(KeeperState.SyncConnected==event.getState()){
			if(EventType.None==event.getType() && null== event.getPath()){
				countDownLatch.countDown();
				
			}else if (event.getType()==EventType.NodeDataChanged) {
				try {
					System.out.println(event.getPath());
					System.out.println(new String(zooKeeper.getData("/123", true, stat)));
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
