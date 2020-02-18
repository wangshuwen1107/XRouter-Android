package cn.cheney.xrouter.core.util;

import android.util.Log;

public class Logger {

    private static final String TAG = "XRoute";

    public static void d(String msg) {
        Log.d(TAG, msg);
    }

    public static void w(String msg) {
        Log.w(TAG, msg);
    }

    public static void e(String msg) {
        Log.e(TAG, msg);
    }
}
