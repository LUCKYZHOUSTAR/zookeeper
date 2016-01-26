package com.curator;


/**
 * drm 封装
 * @author jin.qian
 * @version $Id: AppDrmNode.java, v 0.1 2015年12月28日 下午9:35:23 jin.qian Exp $
 */
public class AppDrmNode {
    /** drm对象的类 */
    private Object obj;
    /** 推送的属性值 */
    private String parmname;
    /** 默认值 */
    private String value;
    /** 短类名 */
    private String classname;
    
    public AppDrmNode(Object obj, String parmname, String value) {
        super();
        this.obj = obj;
        this.parmname = parmname;
        this.value = value;
        this.classname = obj.getClass().getSimpleName();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((classname == null) ? 0 : classname.hashCode());
        result = prime * result + ((obj == null) ? 0 : obj.hashCode());
        result = prime * result + ((parmname == null) ? 0 : parmname.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AppDrmNode other = (AppDrmNode) obj;
        if (classname == null) {
            if (other.classname != null)
                return false;
        } else if (!classname.equals(other.classname))
            return false;
        if (this.obj == null) {
            if (other.obj != null)
                return false;
        } else if (!this.obj.equals(other.obj))
            return false;
        if (parmname == null) {
            if (other.parmname != null)
                return false;
        } else if (!parmname.equals(other.parmname))
            return false;
        return true;
    }
    public Object getObj() {
        return obj;
    }
    public void setObj(Object obj) {
        this.obj = obj;
    }
    public String getParmname() {
        return parmname;
    }
    public void setParmname(String parmname) {
        this.parmname = parmname;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getClassname() {
        return classname;
    }
    public void setClassname(String classname) {
        this.classname = classname;
    }
}
