/**     
 * @FileName: CreateNode.java   
 * @Package:com.test   
 * @Description: TODO  
 * @author: LUCKY    
 * @date:2016年2月2日 下午5:33:19   
 * @version V1.0     
 */
package com.test;

import java.util.concurrent.CountDownLatch;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;

/**
 * @ClassName: CreateNode
 * @Description: TODO
 * @author: LUCKY
 * @date:2016年2月2日 下午5:33:19
 */
public class CreateNode {

	private static CountDownLatch latch = new CountDownLatch(1);
	private static final String zkAddress = "100.66.162.36:2181,100.66.162.36:2180,100.66.162.36:2182";

	public static void main(String[] args) throws Exception {
		CuratorFramework client = CuratorFrameworkFactory.builder()
				.connectionTimeoutMs(5000).connectString(zkAddress)
				.retryPolicy(new RetryNTimes(3, 1000)).build();

		ConnectionStateListener listener = new ConnectionStateListener() {

			public void stateChanged(CuratorFramework client,
					ConnectionState newState) {
				// 连接成功
				if (newState == ConnectionState.CONNECTED) {
					latch.countDown();
					System.out.println("----------连接成功了");
				} else if (newState == ConnectionState.LOST) {
					System.out.println("连接丢失了");
					// 重新连接
				}

			}
		};

		client.getConnectionStateListenable().addListener(listener);
		client.start();
		latch.await();

		client.delete().guaranteed().deletingChildrenIfNeeded()
				.forPath("/app1");
		client.delete().guaranteed().deletingChildrenIfNeeded()
				.forPath("/tasks");

		client.delete().guaranteed().deletingChildrenIfNeeded()
				.forPath("/assign");
		client.delete().guaranteed().deletingChildrenIfNeeded()
				.forPath("/status");

		client.delete().guaranteed().deletingChildrenIfNeeded()
				.forPath("/DrmConfMQControl.httpServiceReload");

		Thread.sleep(Integer.MAX_VALUE);
	}

}
