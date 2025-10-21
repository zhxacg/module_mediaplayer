package lib.kalu.exoplayer2.util;

import android.util.Log;

import androidx.annotation.Keep;

@Keep
public final class ExoLogUtil {

    private static String mTag = "MP_EXO";
    static boolean DEBUG = false;

    public static void setDebug(boolean enable) {
        DEBUG = enable;
    }

    public static void log(String message) {
        log(message, null);
    }

    public static void log(String message, Throwable throwable) {
        if (DEBUG) {
            if (null != message && !message.isBlank()) {
                if (null == throwable) {
                    Log.e(mTag, message);
                } else {
                    Log.e(mTag, message, throwable);
                }
            }
        }
    }
}
