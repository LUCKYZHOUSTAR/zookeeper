/**     
 * @FileName: Zookeeper_Deleat_API_ASync_Usage.java   
 * @Package:com.apache   
 * @Description: TODO  
 * @author: LUCKY    
 * @date:2016年1月22日 上午9:06:03   
 * @version V1.0     
 */
package com.apache;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback.VoidCallback;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

/**
 * @ClassName: Zookeeper_Deleat_API_ASync_Usage
 * @Description: 删除API操作
 * @author: LUCKY
 * @date:2016年1月22日 上午9:06:03
 */
public class Zookeeper_Deleat_API_ASync_Usage implements Watcher {

	private static CountDownLatch countDownLatch = new CountDownLatch(1);

	public static void main(String[] args) throws Exception {
		ZooKeeper zooKeeper = new ZooKeeper("100.66.162.37:2181", 5000,
				new Zookeeper_Deleat_API_ASync_Usage());
		countDownLatch.await();
//		zooKeeper.create("/test", "test".getBytes(), Ids.OPEN_ACL_UNSAFE,
//				CreateMode.PERSISTENT);
//		List<String> childs = zooKeeper.getChildren("/", true);
		System.out.println();
//		zooKeeper.delete("/test", -1);
		//在zookeeper中节点有版本的该概念，-1代表任何的版本的version都会删除掉
		zooKeeper.delete("/test", -1, new deleteCallback(), "上下文的有关信息操作");
		System.out.println("执行完了删除的操作");
	}

	public void process(WatchedEvent event) {

		System.out.println("收到了监听的通知信息" + event);
		if (KeeperState.SyncConnected == event.getState()) {
		
			countDownLatch.countDown();
			System.out.println("连接已经成功了，开始执行zookeeper的下面的操作");
		}
	}

}


//删除成功的回调函数
class deleteCallback implements VoidCallback{

	/* (non-Javadoc)   
	 * @param rc
	 * @param path
	 * @param ctx   
	 * @see org.apache.zookeeper.AsyncCallback.VoidCallback#processResult(int, java.lang.String, java.lang.Object)   
	 */  
	public void processResult(int rc, String path, Object ctx) {
		//输出附带的上下文的相关信息
		System.out.println(ctx);
		switch (Code.get(rc)) {
		//连接丢失的情况
		case CONNECTIONLOSS:
			break;
		case OK:
			System.out.println("删除成功的信息");
			break;
		case NONODE:
			break;
		default:
			break;
		}
		
	}
	
}
