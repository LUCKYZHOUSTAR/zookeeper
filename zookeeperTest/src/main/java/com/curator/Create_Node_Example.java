/**     
 * @FileName: Create_Node_Example.java   
 * @Package:com.curator   
 * @Description: TODO  
 * @author: LUCKY    
 * @date:2016年1月22日 下午7:27:03   
 * @version V1.0     
 */
package com.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * @ClassName: Create_Node_Example
 * @Description: 利用curator来递归创建父节点
 * @author: LUCKY
 * @date:2016年1月22日 下午7:27:03
 */
public class Create_Node_Example {
	static String path="/haolu";
	static RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
	static CuratorFramework client = CuratorFrameworkFactory.builder()
			.connectString("100.66.162.37:2181").retryPolicy(retryPolicy).sessionTimeoutMs(5000)
			.build();

	public static void main(String[] args) throws Exception {
		client.start();
		client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
		.forPath(path,"init".getBytes());
		
		Thread.sleep(Integer.MAX_VALUE);
		
		//删除方法测试---------------
		//递归删除一个节点，并递归删除他的所有的子节点
		client.delete().deletingChildrenIfNeeded().forPath("");
		//删除一个节点，强制指定版本进行删除
		client.delete().withVersion(-1).forPath("");
		//强制保证删除
		//该方法是一个保障措施，只要客户端会话有效，那么就会在后台持续进行删除，直到删除成功
		//尤其是应用在master选举过程中，由于网络的失败，而导致
		client.delete().guaranteed().forPath("");
		
	}
	
	
}
