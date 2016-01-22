/**     
 * @FileName: zookeeperTest.java   
 * @Package:com.test   
 * @Description: TODO  
 * @author: LUCKY    
 * @date:2016年1月11日 上午9:41:46   
 * @version V1.0     
 */
package com.test;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

/**
 * @ClassName: zookeeperTest
 * @Description: zookeeper入门实例
 * @author: LUCKY
 * @date:2016年1月11日 上午9:41:46
 */
public class zookeeperTest {

	public static void main(String[] args) throws IOException, KeeperException,
			InterruptedException {
		// 创建一个Zookeeper实例，第一个参数为目标服务器地址和端口
		ZooKeeper zk = new ZooKeeper("100.66.162.148:2181", 5000,
				new Watcher() {

					// 监控所有被触发的事件
					public void process(WatchedEvent arg0) {
						// dosomething
					}

				});

		// 在创建一个Data节点，数据是mydata，不进行ACL权限控制，节点为永久性的（即使客户端shutdown了也不会消失）
		zk.create("/Data", "mydata".getBytes(), Ids.OPEN_ACL_UNSAFE,
				CreateMode.PERSISTENT);

		// 在Data下面创建一个childnone znode数据为childnone，不进行ACL权限控制
		zk.create("/Data/childnone", "childnone".getBytes(),
				Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

		// 取得Data节点下的子节点名称，返回list《String》

		List<String> childroonsList = zk.getChildren("/Data", true);

		// 取得/Data/childnone节点下的数据，返回byte[]
		byte[] datas = zk.getData("/Data/childnone", true, null);
		//修改节点/Data/childone下的数据，第三个参数为版本，如果是-1的话，那会无视被修改的数据版本，直接改掉
		
		zk.setData("/Data/childnone", "childonemodify".getBytes(), -1);
		
		//删除/Data/childone这个节点，第二个参数为版本，－1的话直接删除，无视版本
//		zk.delete("/Data/childnone", -1);
		      
		//关闭session
		zk.close();

	}
}
