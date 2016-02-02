/**     
 * @FileName: testCurtor.java   
 * @Package:com.curator   
 * @Description: TODO  
 * @author: LUCKY    
 * @date:2016年1月26日 下午8:34:09   
 * @version V1.0     
 */
package com.curator;

import java.util.concurrent.CountDownLatch;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;

/**
 * @ClassName: testCurtor
 * @Description: TODO
 * @author: LUCKY
 * @date:2016年1月26日 下午8:34:09
 */
public class testCurtor {

	private CountDownLatch countDownLatch = new CountDownLatch(1);
	private static CuratorFramework client = null;
	final static String PATH = "/HUOHUO";

	public static void main(String[] args) throws Exception {
		client = CuratorFrameworkFactory.builder()
				.connectString("100.66.162.36:2181").sessionTimeoutMs(5000)
				.retryPolicy(new RetryNTimes(5, 1000)).build();
		client.start();
		createPath();
		System.out.println(getPath(PATH));
		Thread.sleep(Integer.MAX_VALUE);
	}

	public static void createPath() throws Exception {
		client.create().creatingParentsIfNeeded()
				.forPath(PATH, "霍霍".getBytes());
	}

	public static String getPath(String path) throws Exception {
		byte[] buffer = client.getData().usingWatcher(nodeWatcher)
				.forPath(path);
		return new String(buffer);
	}

	static CuratorWatcher nodeWatcher = new CuratorWatcher() {

		public void process(WatchedEvent event) throws Exception {

			if (event.getType() == EventType.NodeCreated) {
				System.out.println("节点被创建了");
			} else if (event.getType() == EventType.NodeDataChanged) {
				System.out.println("更改后的数据为" + getPath(event.getPath()));
			}
		}
	};
}
