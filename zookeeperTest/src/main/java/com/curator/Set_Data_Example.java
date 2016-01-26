/**     
 * @FileName: Set_Data_Example.java   
 * @Package:com.curator   
 * @Description: TODO  
 * @author: LUCKY    
 * @date:2016年1月24日 上午10:09:02   
 * @version V1.0     
 */
package com.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**  
 * @ClassName: Set_Data_Example   
 * @Description: 更新数据
 * @author: LUCKY  
 * @date:2016年1月24日 上午10:09:02     
 */
public class Set_Data_Example {

	static String path="";
	static CuratorFramework client=CuratorFrameworkFactory.builder()
			.retryPolicy(new RetryNTimes(5, 1000))
			.connectionTimeoutMs(10000)
			//设置权限
			.authorization("digest", "foo:true".getBytes())
			.connectString("100.66.162.37:2181")
			.build();
	
	
	public static void main(String[] args) throws Exception {
		client.start();
		client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path,"".getBytes());
		Stat stat=new Stat();
		client.getData().storingStatIn(stat).forPath(path);
		System.out.println("成功的更新了数据"+stat.toString());
		client.setData().withVersion(stat.getVersion()).forPath(path);
	}
	
}
