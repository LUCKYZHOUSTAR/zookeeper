/**     
 * @FileName: Create_Node_Background_Sample.java   
 * @Package:com.curator   
 * @Description: TODO  
 * @author: LUCKY    
 * @date:2016年1月24日 上午10:23:45   
 * @version V1.0     
 */
package com.curator;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**  
 * @ClassName: Create_Node_Background_Sample   
 * @Description: TODO  
 * @author: LUCKY  
 * @date:2016年1月24日 上午10:23:45     
 */
public class Create_Node_Background_Sample {
	static String Path = "/789553/6548";
	static CuratorFramework client = CuratorFrameworkFactory.builder()
			.connectString("100.66.162.37:2181").sessionTimeoutMs(5000)
			.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
	
	static CountDownLatch countDownLatch=new CountDownLatch(2);
	static ExecutorService tp=Executors.newFixedThreadPool(2);
	
	
	public static void main(String[] args) throws Exception {
		client.start();
		System.out.println("main thread "+Thread.currentThread().getName());
		
		//此处传入了自定义的Executor，代表异步的创建节点的信息
		client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).inBackground(new BackgroundCallback() {
			
			public void processResult(CuratorFramework client, CuratorEvent event)
					throws Exception {
				System.out.println("event[code"+event.getResultCode()+", type:"+event.getType()+"]");
				System.out.println("Thread of processresult"+Thread.currentThread().getName());
				countDownLatch.countDown();
				
			}
		},tp).forPath(Path, "init".getBytes());
		
		
		//此处并没有传入自定义的Exector
		client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).inBackground(new BackgroundCallback() {
			
			public void processResult(CuratorFramework client, CuratorEvent event)
					throws Exception {
				System.out.println("event[code"+event.getResultCode()+", type:"+event.getType()+"]");
				System.out.println("Thread of processresult"+Thread.currentThread().getName());
				countDownLatch.countDown();
				
			}
		}).forPath(Path, "init".getBytes());
		
		countDownLatch.await();
		tp.shutdown();
	}
	
}
