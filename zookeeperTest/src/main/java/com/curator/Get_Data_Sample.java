/**     
 * @FileName: Get_Data_Sample.java   
 * @Package:com.curator   
 * @Description: TODO  
 * @author: LUCKY    
 * @date:2016年1月24日 上午9:52:02   
 * @version V1.0     
 */
package com.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**  
 * @ClassName: Get_Data_Sample   
 * @Description: TODO  
 * @author: LUCKY  
 * @date:2016年1月24日 上午9:52:02     
 */
public class Get_Data_Sample {

	static String path="/wangerni";
	static CuratorFramework client=CuratorFrameworkFactory.builder()
			.connectString("100.66.162.37:2181")
			.sessionTimeoutMs(5000)
			.retryPolicy(new ExponentialBackoffRetry(1000, 3))
			//代表將會以強強作爲根節點
			//代表將會以強強作爲根節點
			//代表将会以强强
			.namespace("qiangqiang")
			.build();
	
	public static void main(String[] args) throws Exception {
		client.start();
		client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
		Stat stat=new Stat();
		client.getData().storingStatIn(stat).forPath(path);
		System.out.println(stat.toString());
		System.out.println(client.getNamespace());
	}
}
