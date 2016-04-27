package com.zkProject;

/**
 * @author LUCKY
 *zkClient的配置类信息
 */
public class ZkConfig {

    private String ROOT_PATH  = "Rooter";
    private String CONNECT_IP = "100.66.163.251:2180";
    private int    TIME_OUT   = 10000;
    private int    SLEEP_TIME = 1000;
    private int    RETRY_TIME = 3;

    
    public ZkConfig(){}
    public ZkConfig(String root_path, String zkAddr) {
        this(root_path, zkAddr, 10000);
    }

    public ZkConfig(String root_path, String zkAddr, int tIME_OUT) {
        this(root_path, zkAddr, tIME_OUT, 3000);
    }

    public ZkConfig(String root_path, String zkAddr, int tIME_OUT, int sleep_time) {
        this(root_path, zkAddr, tIME_OUT, sleep_time, 3);
    }

    public ZkConfig(String root_path, String zkAddr, int tIME_OUT, int sleep_time, int retry_time) {
        ROOT_PATH = root_path;
        CONNECT_IP = zkAddr;
        TIME_OUT = tIME_OUT;
        SLEEP_TIME = sleep_time;
        RETRY_TIME = retry_time;
    }

    public int getSLEEP_TIME() {
        return SLEEP_TIME;
    }

    public void setSLEEP_TIME(int sLEEP_TIME) {
        SLEEP_TIME = sLEEP_TIME;
    }

    public int getRETRY_TIME() {
        return RETRY_TIME;
    }

    public void setRETRY_TIME(int rETRY_TIME) {
        RETRY_TIME = rETRY_TIME;
    }

    public String getROOT_PATH() {
        return ROOT_PATH;
    }

    public void setROOT_PATH(String rOOT_PATH) {
        ROOT_PATH = rOOT_PATH;
    }

    public String getCONNECT_IP() {
        return CONNECT_IP;
    }

    public void setCONNECT_IP(String cONNECT_IP) {
        CONNECT_IP = cONNECT_IP;
    }

    public int getTIME_OUT() {
        return TIME_OUT;
    }

    public void setTIME_OUT(int tIME_OUT) {
        TIME_OUT = tIME_OUT;
    }

}
