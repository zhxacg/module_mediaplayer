package lib.kalu.mediax.util;

import android.util.Log;

import androidx.annotation.Keep;

@Keep
public final class MediaLogUtil {

    private static String mTag = "MP_MEDIA3";
    public static boolean DEBUG = false;

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
