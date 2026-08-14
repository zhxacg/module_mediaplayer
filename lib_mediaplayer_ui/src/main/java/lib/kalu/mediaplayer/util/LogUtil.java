package lib.kalu.mediaplayer.util;

import android.util.Log;

import androidx.annotation.Nullable;

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
        boolean classExists = isClassExists("lib.kalu.mediax.util.MediaLogUtil");
        if (!classExists)
            return;
        lib.kalu.mediax.util.MediaLogUtil.setDebug(v);
    }

    private static void setExoV2Logger(boolean v) {
        boolean classExists = isClassExists("lib.kalu.exoplayer2.util.ExoLogUtil");
        if (!classExists)
            return;
        lib.kalu.exoplayer2.util.ExoLogUtil.setDebug(v);
    }

    private static void setVlcLogger(boolean v) {
        boolean classExists = isClassExists("lib.kalu.vlc.util.VlcLogUtil");
        if (!classExists)
            return;
        lib.kalu.vlc.util.VlcLogUtil.setLogger(v);
    }

    private static void setIJkLogger(boolean v) {
        boolean classExists = isClassExists("lib.kalu.ijkplayer.util.IjkLogUtil");
        if (!classExists)
            return;
        lib.kalu.ijkplayer.util.IjkLogUtil.setLogger(v);
    }

    /**
     * 检查类是否存在，不初始化类（不执行static静态代码块）
     *
     * @param className 全限定类名 例：androidx.media3.exoplayer.ExoPlayer
     * @return true存在
     */
    private static boolean isClassExists(String className) {
        try {
            // 第二个参数 false：不执行类初始化(static块不会跑)
            Class<?> clazz = Class.forName(className, false, ClassLoader.getSystemClassLoader());
            return clazz != null;
        } catch (ClassNotFoundException e) {
            return false;
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
