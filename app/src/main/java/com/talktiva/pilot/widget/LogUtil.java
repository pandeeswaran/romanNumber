package com.talktiva.pilot.widget;

import android.util.Log;

class LogUtil {

    public static final String PREFIX = "Sohan";

    public static void v(String TAG, String msg) {
        if (!Constants.DEBUG) return;
        Log.v(TAG, PREFIX + msg);
    }

    public static void d(String TAG, String msg) {
        if (!Constants.DEBUG) return;
        Log.d(TAG, PREFIX + msg);
    }

    public static void i(String TAG, String msg) {
        Log.i(TAG, PREFIX + msg);
    }

    public static void w(String TAG, String msg) {
        Log.w(TAG, PREFIX + msg);
    }

    public static void e(String TAG, String msg) {
        Log.e(TAG, PREFIX + msg);
    }
}