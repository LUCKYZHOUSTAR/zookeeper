/**     
 * @FileName: AuthSample.java   
 * @Package:com.apache   
 * @Description: TODO  
 * @author: LUCKY    
 * @date:2016年1月22日 下午6:28:40   
 * @version V1.0     
 */
package com.apache;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

/**
 * @ClassName: AuthSample
 * @Description: 权限控制例子
 * @author: LUCKY
 * @date:2016年1月22日 下午6:28:40
 */
public class AuthSample {

	final static String PATH = "";

	public static void main(String[] args) throws Exception {
		ZooKeeper zooKeeper = new ZooKeeper("100.66.162.37:2181", 5000,
				new Zookeeper_GetChildren_API_Sync_Usage00());
		zooKeeper.addAuthInfo("digest", "foo:true".getBytes());
		
		zooKeeper.create(PATH, "init".getBytes(), Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL);
		Thread.sleep(Integer.MAX_VALUE);
	}
}
