package cn.cheney.xrouter.plugin.utils;

import org.gradle.api.logging.Logger;

public class XLogger {

    private static Logger sLogger;

    public static void init(Logger logger) {
        sLogger = logger;
    }

    public static void debug(String msg) {
        System.out.println("D/" + "XRouterPlugin" + ": " + msg);
    }
}
