/**     
 * @FileName: NodeCache_Sample.java   
 * @Package:com.curator   
 * @Description: TODO  
 * @author: LUCKY    
 * @date:2016年1月24日 上午10:48:26   
 * @version V1.0     
 */
package com.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**  
 * @ClassName: NodeCache_Sample   
 * @Description: 节点的监听 
 * @author: LUCKY  
 * @date:2016年1月24日 上午10:48:26     
 */
public class NodeCache_Sample {
	static String Path = "/chaochao/123456";
	static CuratorFramework client = CuratorFrameworkFactory.builder()
			.connectString("100.66.162.36:2181").sessionTimeoutMs(5000)
			.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
	
	public static void main(String[] args) throws Exception {
		client.start();
		client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(Path,"init".getBytes());
		//NodeCache用于监听指定zookeeper数据节点本身的变化，指的是内容上的变化
		final NodeCache cache=new NodeCache(client, Path, false);
		//如果为true的话，那么在第一次启动的时候就会从zookeeper上获取该节点下的数据内容，并保存在cache中
		cache.start(true);
		cache.getListenable().addListener(new NodeCacheListener() {	
			public void nodeChanged() throws Exception {
				System.out.println("节点数据已经被更新了"+new String(cache.getCurrentData().getData()));
			}
		});
		
		client.setData().forPath(Path, "u".getBytes());
		Thread.sleep(1000);
		client.delete().deletingChildrenIfNeeded().forPath(Path);
		Thread.sleep(Integer.MAX_VALUE);
	}
}
