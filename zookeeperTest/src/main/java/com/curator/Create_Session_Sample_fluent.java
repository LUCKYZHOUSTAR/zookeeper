/**     
 * @FileName: Create_Session_Sample_fluent.java   
 * @Package:com.curator   
 * @Description: TODO  
 * @author: LUCKY    
 * @date:2016年1月22日 下午7:09:43   
 * @version V1.0     
 */
package com.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**  
 * @ClassName: Create_Session_Sample_fluent   
 * @Description: fluent创建的zookeeper的客户端的操作
 * @author: LUCKY  
 * @date:2016年1月22日 下午7:09:43     
 */
public class Create_Session_Sample_fluent {

	public static void main(String[] args) throws Exception {
		RetryPolicy retryPolicy=new ExponentialBackoffRetry(1000, 3);
		CuratorFramework curatorFramework=CuratorFrameworkFactory.builder().connectString("")
				.sessionTimeoutMs(5000)
				.retryPolicy(retryPolicy)
				.build();
		
		CuratorFramework curatorFramework2=CuratorFrameworkFactory.builder()
				.connectString("")
				.retryPolicy(retryPolicy)
				//以后只能针对该命名空间进行相关的操作
				.namespace("base")
				.build();
		
		//如果没有设置节点属性的话，那么curator默认创建的是持久化节点，内容是默认的是空
		curatorFramework.create().forPath("");
		curatorFramework.create().forPath("", "1230".getBytes());
		curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath("");
		//为了避免在创建节点之前,总是检查节点是否存在，或者报NoNodeException异常的信息
		curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("");
		curatorFramework.checkExists();
		curatorFramework.start();
	}
}
