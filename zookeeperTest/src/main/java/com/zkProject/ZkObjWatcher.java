package com.zkProject;

import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZkObjWatcher implements CuratorWatcher {
    private static Logger logger = LoggerFactory.getLogger(ZkObject.class);
    private ZkClientUtils zkClientUtils;
    private ZkObject      zkObject;
    private boolean       isRoot;

    public ZkObjWatcher(ZkClientUtils zkClientUtils, ZkObject zkObject, boolean isRoot) {
        this.zkClientUtils = zkClientUtils;
        this.zkObject = zkObject;
        this.isRoot = isRoot;
    }




    public void process(WatchedEvent event) throws Exception {
        logger.info(event.toString());
        if ((event.getState() == Watcher.Event.KeeperState.Disconnected)
            || (event.getState() == Watcher.Event.KeeperState.Expired)) {
            return;
        }
        if (zkClientUtils == null) {
            return;
        }

        if (event.getType() == EventType.NodeDataChanged) {
            try {
                String path = event.getPath();
                String value = zkClientUtils.readPath(path);
                //要反射的对象，属性和值操作
                ReflectionUtils.writeField(zkObject.getAttributeName(), zkObject.getObj(),
                    zkObject.getAttributeValue());
                logger.info(zkClientUtils.readPath(path));
            } catch (Exception e) {
                logger.info("wather node exception",e);
            } finally {
                zkClientUtils.watcherPath(event.getPath(), this);
            }
        }
    }

}
