/**     
 * @FileName: ChildrenCache.java   
 * @Package:com.apache   
 * @Description: TODO  
 * @author: LUCKY    
 * @date:2016年1月16日 下午5:32:09   
 * @version V1.0     
 */
package com.apache;

import java.util.ArrayList;
import java.util.List;

/**  
 * @ClassName: ChildrenCache   
 * @Description: 辅助缓存用来处理task和worker的改变
 * @author: LUCKY  
 * @date:2016年1月16日 下午5:32:09     
 */
public class ChildrenCache {

	protected List<String> children;
	/**  
	 *    
	 */
	public ChildrenCache() {
		this.children=null;
	}
	/**  
	 * @param children   
	 */
	public ChildrenCache(List<String> children) {
		this.children = children;
	}
	public List<String> getChildren() {
		return children;
	}
	
	List<String> addedAndSet(List<String> newChildren){
		ArrayList<String> diff=null;
		if(children==null){
			diff=new ArrayList<String>(newChildren);
		}else {
			for(String s:newChildren){
				if(!children.contains(s)){
					if(diff==null){
						diff=new ArrayList<String>();
					}
					//把没有包含的放进diff中
					diff.add(s);
				}
			}
		}
		this.children=newChildren;
		//返回不相同的
		return diff;
	}
	
	List<String> removeAndSet(List<String> newChildren){
		List<String> diff=null;
		if(children!=null){
			for(String s:children){
				if(!newChildren.contains(s)){
					if(diff==null){
						diff=new ArrayList<String>();
					}
					diff.add(s);
				}
			}
		}
		
		this.children=newChildren;
	     return diff;
	}
}
