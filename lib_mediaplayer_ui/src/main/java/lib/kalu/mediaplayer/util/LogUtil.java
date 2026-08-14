package lib.kalu.mediaplayer.util;

import android.util.Log;

import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.PlayerSDK;
import lib.kalu.vlc.util.VlcLogUtil;

public final class LogUtil {

    private static String mTag = "MP_COMMON";
    public static boolean DEBUG = false;

    public static void setEnable(boolean v) {
        DEBUG = v;
        setIJkLogger(DEBUG);
        setVlcLogger(DEBUG);
        setExoV2Logger(DEBUG);
        setMediaxV3Logger(DEBUG);
    }

    private static void setMediaxV3Logger(boolean v) {
        try {
            lib.kalu.mediax.util.MediaLogUtil.setDebug(v);
        } catch (Exception e) {
        }
    }

    private static void setExoV2Logger(boolean v) {
        try {
            lib.kalu.mediax.util.MediaLogUtil.setDebug(v);
        } catch (Exception e) {
        }
    }

    private static void setVlcLogger(boolean v) {
        try {
            Class.forName("lib.kalu.vlc.util.VlcLogUtil");
            VlcLogUtil.setLogger(v);
        } catch (Exception e) {
        }
    }

    private static void setIJkLogger(boolean v) {
        try {
            Class.forName("lib.kalu.ijkplayer.util.IjkLogUtil");
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoIjkPlayer -> initOptions -> step2");
            }
            lib.kalu.ijkplayer.util.IjkLogUtil.setLogger(v);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoIjkPlayer -> initOptions -> step2 Exception " + e.getMessage());
            }
        }
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

    public static void log(String msg, @Nullable Throwable throwable) {
        if (DEBUG) {
            Log.e(mTag, msg, throwable);
        }
    }

    public static void log(String tag, String msg, @Nullable Throwable throwable) {
        if (DEBUG) {
            Log.e(mTag, tag + " -> " + msg, throwable);
        }
    }
}
