/**     
 * @FileName: DrmIPWatcher.java   
 * @Package:com.curator   
 * @Description: TODO  
 * @author: LUCKY    
 * @date:2016年1月24日 下午1:28:48   
 * @version V1.0     
 */
package com.curator;

import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**  
 * @ClassName: DrmIPWatcher   
 * @Description: drm值监控事件处理机制
 * @author: LUCKY  
 * @date:2016年1月24日 下午1:28:48     
 */
public class DrmIPWatcher implements CuratorWatcher{

	private static Logger logger=LoggerFactory.getLogger(DrmIPWatcher.class);
	private DrmZookeeperClient client;
	private Object drmObj;
	private String param;
	private boolean isroot;
	/**  
	 * @param client
	 * @param drmObj
	 * @param param
	 * @param isroot   
	 */
	public DrmIPWatcher(DrmZookeeperClient client, Object drmObj, String param,
			boolean isroot) {
		super();
		this.client = client;
		this.drmObj = drmObj;
		this.param = param;
		this.isroot = isroot;
	}
	
	
	
	public void process(WatchedEvent event) throws Exception {
		
		try {
			logger.info(event.toString());
			
			if(event.getType()==EventType.NodeDataChanged){
				String path=event.getPath();
				String value=client.readPath(path);
				if(isroot){
					String ip="";
					String ippath=path+"/"+ip;
					client.writePath(ippath,value);
				}else {								
				}
			}
		} catch (Exception e) {
		}finally{
			//对某个path进行wather
			//监听只是一次性的事件，需要重新注册
			client.watherPath(event.getPath(), this);
		}
	}
	
	
	
}
