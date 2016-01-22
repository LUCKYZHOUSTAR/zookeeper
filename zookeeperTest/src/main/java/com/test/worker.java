/**     
 * @FileName: worker.java   
 * @Package:com.test   
 * @Description: TODO  
 * @author: LUCKY    
 * @date:2016年1月16日 下午1:34:04   
 * @version V1.0     
 */
package com.test;

import java.io.IOException;
import java.util.Random;

import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**  
 * @ClassName: worker   
 * @Description: TODO  
 * @author: LUCKY  
 * @date:2016年1月16日 下午1:34:04     
 */
public class worker implements Watcher {

	
	private static final Logger log=LoggerFactory.getLogger(worker.class);
	ZooKeeper zk;
	String hostPort;
	String status;
	private Random random = new Random(this.hashCode());
	String serverId=Integer.toHexString(random.nextInt());
	
	/**  
	 *    
	 */
	public worker(String hostPort) {
		this.hostPort=hostPort;
	}
	
	void startZk() throws IOException {
		zk=new ZooKeeper(hostPort, 15000, this);
	}
	
	
	/* (non-Javadoc)   
	 * 
	 * @param event   
	 * @see org.apache.zookeeper.Watcher#process(org.apache.zookeeper.WatchedEvent)   
	 */  
	public void process(WatchedEvent event) {
		log.info(event.toString()+","+hostPort);
		
	}
	
	void register(){
		zk.create("/workers/worker-"+serverId,"Idel".getBytes(),Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL,createWorkerCallback,null);
	}

	
	 StringCallback createWorkerCallback = new StringCallback() {
	        public void processResult(int rc, String path, Object ctx,
	                                  String name) {
	            switch (Code.get(rc)) {
	            case CONNECTIONLOSS:
	            	//出现异常的话，重新注册
	                register(); 
	                break;
	            case OK:
	                log.info("Registered successfully: " + serverId);
	                break;
	            case NODEEXISTS:
	            	log.warn("Already registered: " + serverId);
	                break;
	            default:
	            	log.error("Something went wrong: "
	                + KeeperException.create(Code.get(rc), path));
	            }
	        }
	 };
	 
	 
	 
	 StatCallback statusUpdateCallback=new StatCallback() {
		
		public void processResult(int rc, String path, Object ctx, Stat stat) {
			switch (Code.get(rc)) {
			case CONNECTIONLOSS:
				
				break;

			default:
				break;
			}
			
		}
	};
	String name;
	synchronized private void updateStatus(String status){
		if(status==this.status){
			zk.setData("/workers/"+name, status.getBytes(), -1, statusUpdateCallback, status);
		}
	}
	
	public void setStatus(String status){
		this.status=status;
		updateStatus(status);
	}
	 public static void main(String[] args) throws IOException, InterruptedException {
		 worker w = new worker("100.66.162.90:2180");
	        w.startZk();
	        w.register();
	        Thread.sleep(30000);
	}
}
