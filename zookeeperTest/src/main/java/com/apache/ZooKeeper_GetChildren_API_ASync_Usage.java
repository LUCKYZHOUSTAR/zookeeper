/**     
 * @FileName: ZooKeeper_GetChildren_API_ASync_Usage.java   
 * @Package:com.apache   
 * @Description: TODO  
 * @author: LUCKY    
 * @date:2016年1月22日 上午10:07:18   
 * @version V1.0     
 */
package com.apache;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback.Children2Callback;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * @ClassName: ZooKeeper_GetChildren_API_ASync_Usage
 * @Description: 异步获取子节点的信息
 * @author: LUCKY
 * @date:2016年1月22日 上午10:07:18
 */
public class ZooKeeper_GetChildren_API_ASync_Usage implements Watcher {
	private static CountDownLatch countDownLatch = new CountDownLatch(1);
	private static ZooKeeper zooKeeper = null;

	public static void main(String[] args) throws Exception {
		zooKeeper = new ZooKeeper("100.66.162.37:2181", 5000,
				new ZooKeeper_GetChildren_API_ASync_Usage());
		countDownLatch.await();
		List<String> list1 = zooKeeper.getChildren("/", new defaultWatcher());
		System.out.println(list1.toString());

		//也可以对节点信息注册监听，那么当节点信息改变的时候机会触发
		/*
		 *     None (-1),
            NodeCreated (1),
            NodeDeleted (2),
            NodeDataChanged (3),
            NodeChildrenChanged (4);
		 */
		
		List<String> list2 = zooKeeper.getChildren("/", true, new Stat());
		System.out.println(list2.toString());

		//创建成功的回调函数的操作
		Children2Callback childCallBack = new Children2Callback() {
			public void processResult(int rc, String path, Object ctx,
					List<String> children, Stat stat) {
				System.out.println(children.toString());
				System.out.println(stat.toString());
				// TODO Auto-generated method stub
				switch (Code.get(rc)) {
				// 连接丢失的情况，会重新连接
				case CONNECTIONLOSS:

					break;
				// 正常的情况
				case OK:
					System.out.println(children.toString());
					break;
				// 节点已经存在的情况
				case NODEEXISTS:
					break;
				default:
					break;
				}
			}

		};

		zooKeeper.getChildren("/", true, childCallBack, "传递进去的上下文的有关的信息操作");
	
		Thread.sleep(Integer.MAX_VALUE);
	}

	// 重新获取列表的相关信息的操作
	public void getChildren(String path) throws KeeperException,
			InterruptedException {
		System.out.println(zooKeeper.getChildren(path, new defaultWatcher()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @param event
	 * 
	 * @see
	 * org.apache.zookeeper.Watcher#process(org.apache.zookeeper.WatchedEvent)
	 */
	public void process(WatchedEvent event) {
		// TODO Auto-generated method stub
		if (KeeperState.SyncConnected == event.getState()) {
			if (event.getType() == EventType.None && null == event.getPath()) {
				countDownLatch.countDown();
			} else if (event.getType() == EventType.NodeChildrenChanged) {
				// 重新获取列表的相关信息
				try {
					getChildren(event.getPath());
				} catch (KeeperException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

}

// 自定义的监听getchildren的变化
class defaultWatcher implements Watcher {
	/*
	 * (non-Javadoc)
	 * 
	 * @param event
	 * 
	 * @see
	 * org.apache.zookeeper.Watcher#process(org.apache.zookeeper.WatchedEvent)
	 */
	public void process(WatchedEvent event) {
		// 节点列表变动后，需要重新的查询获取新的列表的相关信息
		if (event.getType() == EventType.NodeChildrenChanged) {

		}
	}

}
