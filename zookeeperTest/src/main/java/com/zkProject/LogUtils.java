package com.zkProject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtils {
    private static final Logger perfLogger = LoggerFactory.getLogger("PERF");

    public static void dumpPerf(String dumpValue) {
        if (perfLogger.isInfoEnabled())
            perfLogger.info(dumpValue);
    }

    public static void debug(Logger logger, String msg, Object[] params) {
        if (logger.isDebugEnabled())
            logger.debug(String.format(msg, params));
    }

    public static void debug(Logger logger, String msg) {
        if (logger.isDebugEnabled())
            logger.debug(msg);
    }

    public static void info(Logger logger, String msg, Object[] params) {
        if (logger.isInfoEnabled())
            logger.info(String.format(msg, params));
    }

    public static void info(Logger logger, String msg) {
        if (logger.isInfoEnabled())
            logger.info(msg);
    }

    public static void warn(Logger logger, String msg, Object[] params) {
        if (logger.isWarnEnabled())
            logger.warn(String.format(msg, params));
    }

    public static void warn(Logger logger, Throwable e, String msg, Object[] params) {
        if (logger.isWarnEnabled())
            logger.warn(String.format(msg, params), e);
    }

    public static void error(Logger logger, Throwable e, String msg, Object[] params) {
        logger.error(String.format(msg, params), e);
    }

    public static void error(Logger logger, String msg, Object[] params) {
        logger.error(String.format(msg, params));
    }

    public static String getStackTrace(Throwable throwable) {
        if (throwable == null) {
            return "";
        }
        Writer sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}
