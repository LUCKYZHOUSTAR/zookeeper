package com.zkProject;

public class Test {

    private String log = "log";

    //需要定义为Public,属性名称首字母也需要小写操作
    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        System.out.println("我重启了哈");
        log = log;
    }

    public void init() throws Exception {
        ZkClientUtils clientUtils = new ZkClientUtils(new ZkConfig());
        clientUtils.register(new ZkObject("log", "重写了哈", "Test", this), false);

    }

    public static void main(String[] args) throws Exception {
        Test test = new Test();
        test.init();
        Thread.sleep(Integer.MAX_VALUE);
    }
}
