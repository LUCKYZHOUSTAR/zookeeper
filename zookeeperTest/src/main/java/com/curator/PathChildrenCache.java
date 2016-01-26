/**     
 * @FileName: PathChildrenCache.java   
 * @Package:com.curator   
 * @Description: TODO  
 * @author: LUCKY    
 * @date:2016年1月24日 上午10:59:51   
 * @version V1.0     
 */
package com.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;


/**  
 * @ClassName: PathChildrenCache   
 * @Description: TODO  
 * @author: LUCKY  
 * @date:2016年1月24日 上午10:59:51     
 */
public class PathChildrenCache {
	static String Path = "/zzz-booo";
	static CuratorFramework client = CuratorFrameworkFactory.builder()
			.connectString("100.66.162.37:2181").sessionTimeoutMs(5000)
			.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
	
	public static void main(String[] args) throws Exception {
		client.start();
		
		//用來子节点列表的增删改查进行监听，但是无法对本身节点或者二级节点进行监听
		org.apache.curator.framework.recipes.cache.PathChildrenCache cache=new org.apache.curator.framework.recipes.cache.PathChildrenCache(client, Path, true);
		cache.start(StartMode.POST_INITIALIZED_EVENT);
		cache.getListenable().addListener(new PathChildrenCacheListener() {
			
			public void childEvent(CuratorFramework client, PathChildrenCacheEvent event)
					throws Exception {
				switch (event.getType()) {
				case CHILD_ADDED:
					System.out.println("节点被创建了"+event.getData());
					break;
				case CHILD_UPDATED:
					System.out.println("节点被更新了"+event.getData());
					break;
				case CHILD_REMOVED:
					System.out.println("节点被删除了"+event.getData());
					break;
				default:
					break;
				}
			}
		});
	
		client.create().withMode(CreateMode.PERSISTENT).forPath(Path);
		Thread.sleep(1000);
		client.create().withMode(CreateMode.PERSISTENT).forPath(Path+"/C1");
		Thread.sleep(1000);
		client.delete().forPath(Path+"/C1");
		Thread.sleep(1000);
		client.delete().forPath(Path);
		Thread.sleep(Integer.MAX_VALUE);
		
	}
}
