package com.zookeeperTest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.slf4j.LoggerFactory;

import com.zkProject.ZkClientUtils;
import com.zkProject.ZkConfig;
import com.zkProject.ZkObject;

public class SetLogLevel {

    private static final org.slf4j.Logger logger         = LoggerFactory
                                                             .getLogger(SetLogLevel.class);

    private String                        logLevelConfig = "INFO";

    private Set<String>                   acceptLevels   = new HashSet(Arrays.asList(new String[] {
            "TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL" }));

    private ZkClientUtils                 zkClientUtils;

    public void init() {
        ZkClientUtils clientUtils = new ZkClientUtils(new ZkConfig());
        clientUtils.register(new ZkObject("logLevelConfig", "重写了哈", "SetLogLevel", this), false);
    }

    public String getLogLevelConfig() {
        return logLevelConfig;
    }

    public void setLogLevelConfig(String logLevelConfig) {

        if (logger.isInfoEnabled()) {
            logger.info("接受到推送的日志配置" + logLevelConfig);
        }
        String[] pairs = StringUtils.split(logLevelConfig, ",;\n\r");
        if (pairs != null) {
            Arrays.sort(pairs);
            for (String nameAndLevel : pairs) {
                String[] nameLevelPair = StringUtils.split(nameAndLevel, ":=");
                if ((nameLevelPair != null) && (nameAndLevel.length() >= 2)) {
                    String loggerName = StringUtils.trim(nameLevelPair[0]);
                    String levelName = StringUtils.trim(nameLevelPair[1]);
                    setLogLevel(loggerName, levelName);
                } else if (logger.isInfoEnabled()) {
                    logger.info("Unrecognized format [" + nameAndLevel + "]");
                }
            }
        }

        this.logLevelConfig = logLevelConfig;
    }

    private void setLogLevel(String loggerName, String level) {
        org.apache.log4j.Logger log = null;
        if ("root".equalsIgnoreCase(loggerName)) {
            log = LogManager.getRootLogger();
        } else if (StringUtils.isNotBlank(loggerName)) {
            log = org.apache.log4j.Logger.getLogger(loggerName);
        }

        if ((log != null) && (StringUtils.isNotBlank(level))) {
            if (this.acceptLevels.contains(level.toUpperCase())) {
                if (logger.isInfoEnabled()) {
                    logger.info("Set logger [" + log.getName() + "] to [" + level + "]");
                }
                log.setLevel(Level.toLevel(level.toUpperCase()));
            } else if (logger.isInfoEnabled()) {
                logger.info("unaccept level[" + level + "]");
            }
        } else if (logger.isInfoEnabled()) {
            logger.info("Logger [" + loggerName + "] is null or level [" + level + "] is blank");
        }
    }
    
    
    public static void main(String[] args) throws Exception {
        SetLogLevel logLevel=new SetLogLevel();
        logLevel.init();
        Thread.sleep(Integer.MAX_VALUE);
    }
}
