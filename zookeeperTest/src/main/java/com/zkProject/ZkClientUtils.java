package com.zkProject;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author LUCKY
 *zkClients的工具类操作
 */
public class ZkClientUtils {

    private static Logger           logger  = LoggerFactory.getLogger(ZkClientUtils.class);
    private CuratorFramework        client;
    private static CountDownLatch   latch   = new CountDownLatch(1);
    private static Set<String>      nodeSet = new CopyOnWriteArraySet<String>();
    private ConnectionStateListener listener;
    private ZkConfig                zkConfig;

    public ZkClientUtils() throws Exception {
        init();
    }

    public void init() throws Exception {
        //启动客户端操作
        client.start();
        latch.await();
        //开始写入根节点操作
        if (!isPathExists(zkConfig.getROOT_PATH())) {
            //如果根节点没有存在的话，就开始写入根节点操作
            createPath(zkConfig.getROOT_PATH(), CreateMode.PERSISTENT);
            writePath(zkConfig.getROOT_PATH(), "");
        }
    }

    public boolean register(ZkObject zkObject, boolean addSet) {
        String path = "/"+zkConfig.getROOT_PATH() + "/" + zkObject.getNodeName() + "."
                      + zkObject.getAttributeName();
        if (addSet && nodeSet.contains(path)) {
            return false;
        }

        try {
            if (!isPathExists(path)) {
                createPath(path, CreateMode.PERSISTENT);
                writePath(path, zkObject.getAttributeValue());

            } else {
                String value = readPath(path);
                //如果不相等的话
                if (!value.equals(zkObject.getAttributeValue())) {
                    ReflectionUtils.writeField(zkObject.getAttributeName(), zkObject.getObj(),
                        zkObject.getAttributeValue());
                    zkObject.setAttributeValue(value);
                }
            }
            watcherPath(path, new ZkObjWatcher(this, zkObject, false));
            if (addSet) {
                nodeSet.add(path);
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public ZkClientUtils(final ZkConfig zkConfig) {
        this.zkConfig = zkConfig;
        this.client = getClient(zkConfig);
        client.start();
    }

    public CuratorFramework getClient(ZkConfig zkConfig) {
        this.client = CuratorFrameworkFactory
            .builder()
            .connectString(zkConfig.getCONNECT_IP())
            .connectionTimeoutMs(zkConfig.getTIME_OUT())
            .retryPolicy(
                new ExponentialBackoffRetry(zkConfig.getSLEEP_TIME(), zkConfig.getRETRY_TIME()))
            .build();
        this.listener = new StateEventListener();
        client.getConnectionStateListenable().addListener(listener);
        return client;
    }

    public void createPath(String path, CreateMode mode) throws Exception {
        client.create().creatingParentsIfNeeded().withMode(mode).forPath(path);
    }

    public boolean isPathExists(String path) throws Exception {
        Stat serverStat = client.checkExists().forPath(path);
        if (serverStat != null) {
            return true;
        } else {
            return false;
        }
    }

    public void deletePath(String path) throws Exception {
        client.delete().forPath(path);
    }

    public void writePath(String path, String value) throws Exception {
        client.setData().forPath(path, value.getBytes());
    }

    public String readPath(String path) throws Exception {
        byte[] result = client.getData().forPath(path);
        return new String(result, Charset.forName("utf-8"));
    }

    public void closeConnection(CuratorFramework client) {
        if (client != null) {
            client.close();
        }
    }

    public List<String> getChildrens(String path) throws Exception {
        return client.getChildren().forPath(path);
    }

    public String watcherPath(String path, CuratorWatcher watcher) throws Exception {
        byte[] buffer = client.getData().usingWatcher(watcher).forPath(path);
        return new String(buffer);
    }

    /**
     * 当失去连接后，重新初始化操作
     */
    public void reinit() {

        try {
            closeConnection(client);
            init();
        } catch (Exception e) {
            logger.info("restart zkclient exception", e);
        }
    }

    //服务器注册状态监听操作
    final class StateEventListener implements ConnectionStateListener {

        public void stateChanged(CuratorFramework curatorframework, ConnectionState connectionstate) {
            if (connectionstate == ConnectionState.CONNECTED) {
                logger.info("zkclient already start");
                latch.countDown();
            } else if (connectionstate == ConnectionState.LOST) {
                //重新启动操作
                logger.info("zkclient start lost");
                logger.info("关闭连接操作");
                reinit();
                logger.info("restart zkclient");
            }
        }

    }
}
