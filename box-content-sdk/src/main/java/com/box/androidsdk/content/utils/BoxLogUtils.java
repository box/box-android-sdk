package com.box.androidsdk.content.utils;

import java.util.Map;

public class BoxLogUtils {

    private static Logger sLogger = new BoxLogger();

    public static void setLogger(Logger logger) {
        sLogger = logger;
    }

    public static Logger getLogger(Logger logger) {
        return sLogger;
    }

    public static boolean getIsLoggingEnabled() {
        return sLogger.getIsLoggingEnabled();
    }

    public static void i(String tag, String msg) {
        sLogger.i(tag, msg);
    }

    public static void i(String tag, String msg, Map<String, String> map) {
      sLogger.i(tag, msg, map);
    }

    public static void d(String tag, String msg) {
        sLogger.d(tag, msg);
    }

    public static void e(String tag, String msg) {
      sLogger.e(tag, msg);
    }

    public static void e(String tag, Throwable t) {
       sLogger.e(tag, t);
    }

    public static void e(String tag, String msg, Throwable t) {
        sLogger.e(tag, msg, t);
    }

    public static void nonFatalE(String tag, String msg, Throwable t) {
        sLogger.nonFatalE(tag, msg, t);
    }
}
