/**     
 * @FileName: Create_Session_Sample.java   
 * @Package:com.curator   
 * @Description: TODO  
 * @author: LUCKY    
 * @date:2016年1月22日 下午6:54:11   
 * @version V1.0     
 */
package com.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**  
 * @ClassName: Create_Session_Sample   
 * @Description: 用curator创建一个zookeeper客户端
 * @author: LUCKY  
 * @date:2016年1月22日 下午6:54:11     
 */
public class Create_Session_Sample {

	public static void main(String[] args) throws Exception {
		RetryPolicy retryPolicy=new ExponentialBackoffRetry(1000, 2);
		CuratorFramework client=CuratorFrameworkFactory.newClient("", 5000, 3000, retryPolicy);
		client.start();
		Thread.sleep(Integer.MAX_VALUE);
		
		
	}
}
