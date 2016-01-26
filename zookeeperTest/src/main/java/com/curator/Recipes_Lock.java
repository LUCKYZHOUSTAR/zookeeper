/**     
 * @FileName: Recipes_Lock.java   
 * @Package:com.curator   
 * @Description: TODO  
 * @author: LUCKY    
 * @date:2016年1月24日 上午11:32:05   
 * @version V1.0     
 */
package com.curator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**  
 * @ClassName: Recipes_Lock   
 * @Description: 分布式锁
 * @author: LUCKY  
 * @date:2016年1月24日 上午11:32:05     
 */
public class Recipes_Lock {
	static String lock_path = "/curator_recipes_lock_path";
    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("100.66.162.37:2181")
            .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
    
    public static void main(String[] args) {
		client.start();
		final InterProcessMutex lock=new InterProcessMutex(client, lock_path);
		final CountDownLatch countDownLatch=new CountDownLatch(1);
		for(int i = 0; i < 30; i++){
			new Thread(new Runnable() {
				public void run() {
					try {
						countDownLatch.await();
						lock.acquire();
					} catch ( Exception e ) {}
					SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss|SSS");
					String orderNo = sdf.format(new Date());
					System.out.println("生成的订单号是 : "+orderNo);
					try {
						lock.release();
					} catch ( Exception e ) {}
				}
			}).start();
			
			countDownLatch.countDown();
			
	}
}
}
