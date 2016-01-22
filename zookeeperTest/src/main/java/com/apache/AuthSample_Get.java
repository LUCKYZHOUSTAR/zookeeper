/**     
 * @FileName: AuthSample_Get.java   
 * @Package:com.apache   
 * @Description: TODO  
 * @author: LUCKY    
 * @date:2016年1月22日 下午6:33:32   
 * @version V1.0     
 */
package com.apache;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

/**
 * @ClassName: AuthSample_Get
 * @Description: zookeeper中的权限判断
 * @author: LUCKY
 * @date:2016年1月22日 下午6:33:32
 */
public class AuthSample_Get implements Watcher {

	final static String PATH = "/321";

	public static void main(String[] args) throws IOException, Exception,
			InterruptedException {
		ZooKeeper zooKeeper = new ZooKeeper("100.66.162.37:2181", 5000,
				new AuthSample_Get());
		zooKeeper.addAuthInfo("digest", "foo:true".getBytes());
		zooKeeper.create(PATH, "init".getBytes(), Ids.CREATOR_ALL_ACL,
				CreateMode.EPHEMERAL);
		ZooKeeper zooKeeper2=new   ZooKeeper("100.66.162.37:2181", 5000,
				new AuthSample_Get());
		zooKeeper2.getData(PATH, false, null);

	}

	/* (non-Javadoc)   
	 * @param event   
	 * @see org.apache.zookeeper.Watcher#process(org.apache.zookeeper.WatchedEvent)   
	 */  
	public void process(WatchedEvent event) {
		// TODO Auto-generated method stub
		
	}
}
