/**     
 * @FileName: Del_Data_Sample.java   
 * @Package:com.curator   
 * @Description: TODO  
 * @author: LUCKY    
 * @date:2016年1月22日 下午7:40:58   
 * @version V1.0     
 */
package com.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * @ClassName: Del_Data_Sample
 * @Description: 使用curator进行删除的操作
 * @author: LUCKY
 * @date:2016年1月22日 下午7:40:58
 */
public class Del_Data_Sample {

	static String Path = "/789553/6548";
	static CuratorFramework client = CuratorFrameworkFactory.builder()
			.connectString("100.66.162.37:2181").sessionTimeoutMs(5000)
			.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();

	public static void main(String[] args) throws Exception {

		client.start();
		client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
		.forPath(Path,"init".getBytes());
		Stat stat=new Stat();
		client.getData().storingStatIn(stat).forPath(Path);
		//递归删除子节点的相关的信息
		client.delete().deletingChildrenIfNeeded().withVersion(stat.getVersion()).forPath(Path);
	}
}
