/**     
 * @FileName: master.java   
 * @Package:com.test   
 * @Description: TODO  
 * @author: LUCKY    
 * @date:2016年1月15日 下午7:54:58   
 * @version V1.0     
 */
package com.test;

import java.io.IOException;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

/**
 * @ClassName: master
 * @Description: 实现一个maste的watcher
 * @author: LUCKY
 * @date:2016年1月15日 下午7:54:58
 */
public class master implements Watcher {

	ZooKeeper zk;
	String hostPort;

	/**  
	 *    
	 */
	public master(String hostPort) {
		this.hostPort = hostPort;
	}

	void startZk() throws IOException {
		zk = new ZooKeeper(hostPort, 15000, this);
	}

	public void process(WatchedEvent event) {
		System.out.println(event);
	}

	void stopZk() throws Exception {
		zk.close();
	}

	public static void main(String[] args) throws Exception {
		master m = new master("100.66.162.90:2180");
		m.startZk();

		Thread.sleep(60000);
		m.stopZk();
	}
}
