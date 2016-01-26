/**     
 * @FileName: Recipes_MasterSelect.java   
 * @Package:com.curator   
 * @Description: master选举策略
 * @author: LUCKY    
 * @date:2016年1月24日 上午11:14:43   
 * @version V1.0     
 */
package com.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**  
 * @ClassName: Recipes_MasterSelect   
 * @Description: TODO  
 * @author: LUCKY  
 * @date:2016年1月24日 上午11:14:43     
 */
public class Recipes_MasterSelect {

	static String master_path="/curator_recipes_master_path";
	static CuratorFramework client = CuratorFrameworkFactory.builder()
			.connectString("100.66.162.37:2181").sessionTimeoutMs(5000)
			.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
	
	public static void main(String[] args) throws Exception {
		client.start();
		
		LeaderSelector selector=new LeaderSelector(client, master_path, new LeaderSelectorListenerAdapter() {
			//leader的监听，当竞争到master后，会自动的回调该方法，一旦执行完takeLeadership方法后，就会立即释放master的权利
			public void takeLeadership(CuratorFramework client) throws Exception {
				System.out.println("成为master绝色");
				Thread.sleep(3000);
				System.out.println("完成master角色，释放master权利");
			}
		});
		
		selector.autoRequeue();
		selector.start();
		Thread.sleep(Integer.MAX_VALUE);
	}
}
