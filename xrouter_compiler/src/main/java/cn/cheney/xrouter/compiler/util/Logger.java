package cn.cheney.xrouter.compiler.util;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

public class Logger {

    private static Messager sMessager;

    public static void init(Messager messager) {
        sMessager = messager;
    }

    public static void d(String msg) {
        if (null == sMessager) {
            return;
        }
        sMessager.printMessage(Diagnostic.Kind.NOTE, "XRouter>>>  " + msg);
    }

    public static void e(String msg) {
        if (null == sMessager) {
            return;
        }
        sMessager.printMessage(Diagnostic.Kind.ERROR, "XRouter>>>  " + msg);
    }

}
