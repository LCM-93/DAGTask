package com.cm.daglib.utils;

import android.util.Log;

/**
 * ****************************************************************
 * Author: LiChenMing.Chaman
 * Date: 2021/4/19 5:09 PM
 * Desc:
 * *****************************************************************
 */
public class LogUtils {
    private static final String TAG = "DAGTask";
    public static boolean DEBUG = true;

    public static void W(String msg) {
        if (DEBUG) Log.w(TAG, msg);
    }

    public static void E(String msg) {
        if (DEBUG) Log.e(TAG, msg);
    }

    public static void D(String msg) {
        if (DEBUG) Log.d(TAG, msg);
    }

    public static void I(String msg) {
        if (DEBUG) Log.i(TAG, msg);
    }

    public static void V(String msg) {
        if (DEBUG) Log.v(TAG, msg);
    }
}
