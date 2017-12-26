package com.android.gastove.service;

//import android.os.SystemProperties;
import android.util.Log;

/**
 * class of debug for ECOMode.
 * <pre>
 * this class use for debug ECOMode package side.<br>
 * </pre>
 * @version 0.0
 * @since 0.0
 * @author FSI
 */
public class PritLog {
    static final String TAG = "DTrecordin";
    static final int DEBUG_FLAG_OFF = 0;
    static final int DEBUG_FLAG_ON = 1;
    static boolean ECO_FLAG = true;
    static {
        updateDebugFlag();
    }

    public static boolean isVerboseEnabled() {
        return ECO_FLAG;
    }

    public static boolean isDebugEnabled() {
        return ECO_FLAG;
    }

    public static void v(String msg) {
        if (ECO_FLAG) {
            Log.v(TAG, msg);
        }
    }

    public static void v(String msg, Throwable t) {
        if (ECO_FLAG) {
            Log.v(TAG, msg, t);
        }
    }

    public static void d(String msg) {
        if (ECO_FLAG) {
            Log.d(TAG, msg);
        }
    }

    public static void d(String msg, Throwable t) {
        if (ECO_FLAG) {
            Log.d(TAG, msg, t);
        }
    }

    public static void i(String msg) {
        if (ECO_FLAG) {
            Log.i(TAG, msg);
        }
    }

    public static void i(String msg, Throwable t) {
        if (ECO_FLAG) {
            Log.i(TAG, msg, t);
        }
    }

    public static void w(String msg) {
        if (ECO_FLAG) {
            Log.w(TAG, msg);
        }
    }

    public static void w(String msg, Throwable t) {
        if (ECO_FLAG) {
            Log.w(TAG, msg, t);
        }
    }

    public static void e(String msg) {
            Log.e(TAG, msg);
    }

    public static void e(String msg, Throwable t) {
            Log.e(TAG, msg, t);
    }

    public static void updateDebugFlag() {

        try {
            if (DEBUG_FLAG_ON  == 1) {
                ECO_FLAG = true;
            } else {
                ECO_FLAG = false;
            }
        } catch ( Exception e ) {
            Log.e( TAG, "catch exception." );
            e.printStackTrace();
        }
    }
}

