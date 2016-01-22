/**     
 * @FileName: Zookeeper_Create_API_ASync_Usage.java   
 * @Package:com.apache   
 * @Description: TODO  
 * @author: LUCKY    
 * @date:2016年1月21日 下午8:57:13   
 * @version V1.0     
 */
package com.apache;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

/**
 * @ClassName: Zookeeper_Create_API_ASync_Usage
 * @Description: 异步的创建节点
 * @author: LUCKY
 * @date:2016年1月21日 下午8:57:13
 */
public class Zookeeper_Create_API_ASync_Usage implements Watcher {

	private static CountDownLatch connectedComprehord = new CountDownLatch(1);

	public static void main(String[] args) throws Exception {
		ZooKeeper zooKeeper = new ZooKeeper("100.66.162.37:2181", 5000,
				new Zookeeper_Create_API_ASync_Usage());
		connectedComprehord.await();
		zooKeeper.create("/zk-test-ephemeral-", "".getBytes(),
				Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,  new IStringCallback(), "我是一个上下文");
		zooKeeper.create("/zk-test-ephemeral-", "".getBytes(),
				Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL,  new IStringCallback(), "我是一个上下文");
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
			System.out.println("连接成功" + event.getType());
			connectedComprehord.countDown();
		}
	}


}


class IStringCallback implements StringCallback {
	/* (non-Javadoc)   
	 * @param rc 服务的响应码
	 * @param path  回调前的路径
	 * @param ctx     回调前设置的上下文的信息
	 * @param name   回调后的路径
	 * @see org.apache.zookeeper.AsyncCallback.StringCallback#processResult(int, java.lang.String, java.lang.Object, java.lang.String)   
	 */  
	public void processResult(int rc, String path, Object ctx, String name) {

		//需要判断各种的状态
		switch (Code.get(rc)) {
		case CONNECTIONLOSS:
			
			break;

		default:
			break;
		}
		System.out.println("创建的结果为" + rc + "路径为" + path + "," + ctx
				+ "真正的路径名称为" + name);
	}

}



