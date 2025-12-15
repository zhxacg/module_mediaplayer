package lib.kalu.mediaplayer.util;

import android.util.Log;

import androidx.annotation.Nullable;

public final class LogUtil {

    private static String mTag = "MP_COMMON";
    public static boolean DEBUG = false;

    public static void setLogger(boolean v) {
        DEBUG = v;
    }

    public static boolean isLog() {
        return DEBUG;
    }

    public static void log(String message) {
        if (DEBUG) {
            Log.e(mTag, message);
        }
    }

    public static void log(String tag, String msg) {
        if (DEBUG) {
            Log.e(mTag, tag + " -> " + msg);
        }
    }

    public static void log(String message, @Nullable Throwable throwable) {
        if (DEBUG) {
            Log.e(mTag, message, throwable);
        }
    }
}
