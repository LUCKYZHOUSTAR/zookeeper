package com.zkProject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author LUCKY
 *节点对象
 */
public class ZkObject{
    private static Logger logger = LoggerFactory.getLogger(ZkObject.class);

    private String NodeName;
    private String AttributeName;
    private String AttributeValue;
    //要反射的对象操作
    private Object obj;

    public ZkObject(String attributeName, String attributeValue, String nodeName,Object obj) {
        AttributeName = attributeName;
        AttributeValue = attributeValue;
        this.obj = obj;
        this.NodeName=nodeName;
    }

    public String getNodeName() {
        return NodeName;
    }

    public void setNodeName(String nodeName) {
        NodeName = nodeName;
    }

    public String getAttributeName() {
        return AttributeName;
    }

    public void setAttributeName(String attributeName) {
        AttributeName = attributeName;
    }

    public String getAttributeValue() {
        return AttributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        AttributeValue = attributeValue;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

   

    
    
}
