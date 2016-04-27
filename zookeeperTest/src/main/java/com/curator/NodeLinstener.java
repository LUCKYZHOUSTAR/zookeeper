package com.curator;

import java.util.concurrent.CountDownLatch;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;

public class NodeLinstener {

    private static CountDownLatch latch = new CountDownLatch(1);
    private CuratorFramework      client;

    public void Connect() throws Exception {
        client = CuratorFrameworkFactory.builder().connectionTimeoutMs(5000)
            .connectString("100.66.163.251:2180").retryPolicy(new RetryNTimes(3, 4000)).build();
        client.getConnectionStateListenable().addListener(new ConnectionStateListener() {

            public void stateChanged(CuratorFramework client, ConnectionState state) {
                if (state == ConnectionState.CONNECTED) {
                    latch.countDown();
                    System.out.println("连接成功");
                } else if (state == ConnectionState.LOST) {
                    System.out.println("连接失败");
                }
            }
        });
        client.start();
        latch.await();
        client.getConnectionStateListenable().addListener(new ConnectionStateListener() {

            public void stateChanged(CuratorFramework curatorframework,
                                     ConnectionState connectionstate) {
                if (connectionstate == connectionstate.CONNECTED) {
                    System.out.println("连接成功了");
                    latch.countDown();
                } else if (connectionstate == connectionstate.LOST) {
                    System.out.println("连接失败了");
                }

            }
        });
//        client.create().forPath("/QIANG");
        client.getData().usingWatcher(new dataListener()).forPath("/QIANG");

    }

    class dataListener implements CuratorWatcher {

        public void process(WatchedEvent watchedevent) throws Exception {
            if (watchedevent.getType() == EventType.NodeDataChanged) {
                System.out.println("节点信息改变了" + watchedevent.getPath());
                client.getData().usingWatcher(new dataListener()).forPath(watchedevent.getPath());
            }

        }

    }

    public static void main(String[] args) throws Exception {
        new NodeLinstener().Connect();
        Thread.sleep(Integer.MAX_VALUE);
    }
}
