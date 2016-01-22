/**     
 * @FileName: SetData_API_ASync_Usage.java   
 * @Package:com.apache   
 * @Description: TODO  
 * @author: LUCKY    
 * @date:2016年1月22日 下午3:28:06   
 * @version V1.0     
 */
package com.apache;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**  
 * @ClassName: SetData_API_ASync_Usage   
 * @Description: 异步的更新数据操作
 * @author: LUCKY  
 * @date:2016年1月22日 下午3:28:06     
 */
public class SetData_API_ASync_Usage implements Watcher {

	
	private static CountDownLatch countDownLatch = new CountDownLatch(1);
	private static ZooKeeper zooKeeper = null;
	private static Stat stat = new Stat();
	
	
	public static void main(String[] args) throws Exception {
		zooKeeper = new ZooKeeper("100.66.162.37:2181", 5000,
				new Zookeeper_GetChildren_API_Sync_Usage00());
		countDownLatch.wait();
		
		String path="/666";
		zooKeeper.create(path, "123".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		
		
		//用来异步回调更新的数据方法
		StatCallback setData=new StatCallback() {
			
			public void processResult(int rc, String path, Object ctx, Stat stat) {
				switch (Code.get(rc)) {
				case OK:
					System.out.println("更新成功了");
					break;

				default:
					break;
				}
				
			}
		};
		zooKeeper.setData(path, "456".getBytes(), -1, setData, "我是上下文的信息");
		Thread.sleep(Integer.MAX_VALUE);
	}
	
	
	
	
	/* (non-Javadoc)   
	 * @param event   
	 * @see org.apache.zookeeper.Watcher#process(org.apache.zookeeper.WatchedEvent)   
	 */  
	public void process(WatchedEvent event) {
		if(KeeperState.SyncConnected==event.getState()){
			countDownLatch.countDown();
		}
		
	}

}
