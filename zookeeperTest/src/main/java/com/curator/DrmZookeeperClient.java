/**     
 * @FileName: DrmZookeeperClient.java   
 * @Package:com.curator   
 * @Description: TODO  
 * @author: LUCKY    
 * @date:2016年1月24日 下午12:22:13   
 * @version V1.0     
 */
package com.curator;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: DrmZookeeperClient
 * @Description:封装的Zookeeper的客户端的操作
 * @author: LUCKY
 * @date:2016年1月24日 下午12:22:13
 */
public class DrmZookeeperClient {
	private static Logger logger = LoggerFactory
			.getLogger(DrmZookeeperClient.class);

	private String zkAddress = "100.66.154.223:2181";
	private static final String NAMESPACE_STRING = "";
	private int timeout = 10000;
	private ConnectionStateListener listener;
	private CuratorFramework curator;
	private String appName = "mqagent";

	private Set<String> confset = new CopyOnWriteArraySet<String>();

	private static final CountDownLatch latch = new CountDownLatch(1);

	public static void main(String[] args) throws Exception {
		DrmZookeeperClient client = new DrmZookeeperClient();
		client.init();
	}

	public void init() throws Exception {
		this.listener = new StateEventListener();
		curator = CuratorFrameworkFactory.builder().connectString(zkAddress)
				.retryPolicy(new RetryNTimes(5, 1000))
				.connectionTimeoutMs(timeout).build();
		// 为该客户端注册监听
		curator.getConnectionStateListenable().addListener(listener);
		// 启动该客户端操作
		curator.start();
		// 等待连接
		latch.await();

		List<String> chilerenList = curator.getChildren().forPath("/ZOOKEEPER");
		System.out.println(chilerenList.size());
	}

	// 检查某个节点是否存在
	public boolean isPathExist(String path) throws Exception {
		Stat serverStat = curator.checkExists().forPath(path);
		if (serverStat == null) {
			return false;
		} else {
			return true;
		}
	}

	public void deletePath(String path) throws Exception {
		curator.delete().forPath(path);
	}

	public void createPath(String path, CreateMode mode) throws Exception {
		curator.create().creatingParentsIfNeeded()// 创建的路径
				.withMode(mode).withACL(Ids.OPEN_ACL_UNSAFE).forPath(path);
	}

	public void writePath(String path, String value) throws Exception {
		curator.setData().forPath(path, value.getBytes());
	}

	public String readPath(String path) throws Exception {
		byte[] buffer = curator.getData().forPath(path);
		return new String(buffer);
	}

	public String watherPath(String path, CuratorWatcher watcher)
			throws Exception {
		byte[] buffer = curator.getData().usingWatcher(watcher).forPath(path);
		return new String(buffer);
	}

	public boolean confRegist(AppDrmNode drmNode, boolean addset) {
		String path = appName + "/" + drmNode.getClassname() + "."
				+ drmNode.getParmname();
		String ip = "";
		String ippath = path + "/" + ip;
		if (confset.contains(drmNode)) {
			logger.error("重复注册节点");
			return false;
		}

		// 初始化父节点
		try {
			if (!isPathExist(path)) {
				createPath(path, CreateMode.PERSISTENT);
				writePath(path, drmNode.getValue());
			} else {
				String value = readPath(path);
				// 处理值
				if (!value.equals(drmNode.getValue())) {
					// ReflectionUtils.writeField(drmNode.getParmname(),
					// drmNode.getObj(), value);

				}
			}

			// 监控根目录
			watherPath(
					path,
					new DrmIPWatcher(this, drmNode.getObj(), drmNode
							.getParmname(), true));
			// ippath
			if (isPathExist(ippath)) {
				deletePath(ippath);
			}

			// 初始化临时节点值，以及当前值
			createPath(ippath, CreateMode.EPHEMERAL);
			writePath(ippath, drmNode.getValue());
			// 挂载临时节点
			// 挂载临时节点监听
			watherPath(
					ippath,
					new DrmIPWatcher(this, drmNode.getObj(), drmNode
							.getParmname(), false));
			if (addset) {
				confset.add("");
			}
		} catch (Exception e) {
			logger.error("注册drm异常");
			return false;
		}

		return true;
	}

	// 重新初始化操作
	private void reinit() {
		try {
			unregister();
			init();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void unregister() throws Exception {
		try {
			// 关闭该客户端操作
			curator.close();
			curator = null;
		} catch (Exception e) {
			logger.warn("unregister failed");
			throw e;
		}
	}

	// 服务器注册状态监听
	final class StateEventListener implements ConnectionStateListener {

		public void stateChanged(CuratorFramework client,
				ConnectionState newState) {
			if (newState == ConnectionState.CONNECTED) {
				logger.info("connection established");
				latch.countDown();
			} else if (newState == ConnectionState.LOST) {
				logger.info("connection  lost,waiting for reconnect");
				try {
					// 失去连接后重新初始化操作
					logger.info("re-initing");
					// 重新初始化操作
					reinit();
					logger.info("re-inited");

				} catch (Exception e) {
					logger.error("re-inited", e);
				}
			}
		}

	}

}
