
package lib.kalu.mediaplayer.core.player.video;

import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

import lib.kalu.mediaplayer.core.component.ComponentApi;
import lib.kalu.mediaplayer.listener.OnPlayerEpisodeListener;
import lib.kalu.mediaplayer.listener.OnPlayerEventListener;
import lib.kalu.mediaplayer.listener.OnPlayerProgressListener;
import lib.kalu.mediaplayer.listener.OnPlayerWindowListener;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public interface VideoPlayerApiListener extends VideoPlayerApiBase, VideoPlayerApiBuried {

    default void callWindow(@PlayerType.WindowType.Value int state) {

        // component
        try {
            ViewGroup viewGroup = getBaseComponentViewGroup();
            int childCount = viewGroup.getChildCount();
            if (childCount <= 0)
                throw new Exception("not find component");
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                if (!(childAt instanceof ComponentApi))
                    continue;
                ((ComponentApi) childAt).callWindow(state);
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiBase => callWindow => " + e.getMessage());
            }
        }

        // listener
        try {
            OnPlayerWindowListener windowListener = getOnPlayerWindowListener();
            if (null != windowListener) {
                windowListener.onWindow(state);
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiBase => callWindow => " + e.getMessage());
            }
        }

        // 埋点
        onBuriedWindow(state);
    }


    default void callEvent(@PlayerType.EventType.Value int state) {
        callEvent(true, true, state);
    }

    default void callEvent(boolean callPlayer, boolean callComponent, @PlayerType.EventType.Value int state) {

        // component
        try {
            if (!callComponent)
                throw new Exception("warning: callComponent false");
            ViewGroup viewGroup = getBaseComponentViewGroup();
            int childCount = viewGroup.getChildCount();
            if (childCount <= 0)
                throw new Exception("not find component");
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                if (!(childAt instanceof ComponentApi))
                    continue;
                ((ComponentApi) childAt).callEvent(state);
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiBase => callEvent => " + e.getMessage());
            }
        }

        // listener
        try {
            if (!callPlayer)
                throw new Exception("warning: callPlayer false");
            OnPlayerEventListener eventListener = getOnPlayerEventListener();
            if (null == eventListener)
                throw new Exception("warning: eventListener null");
            eventListener.onEvent(state);
            if (state == PlayerType.EventType.START) {
                eventListener.onStart();
            } else if (state == PlayerType.EventType.COMPLETE) {
                eventListener.onComplete();
            } else if (state == PlayerType.EventType.PAUSE) {
                eventListener.onPause();
            } else if (state == PlayerType.EventType.RESUME) {
                eventListener.onResume();
            } else if (state == PlayerType.EventType.ERROR) {
                eventListener.onError(null);
            } else if (state == PlayerType.EventType.BUFFERING_START) {
                eventListener.onBufferingStart();
            } else if (state == PlayerType.EventType.BUFFERING_STOP) {
                eventListener.onBufferingStop();
            } else if (state == PlayerType.EventType.PREPARE) {
                eventListener.onPrepare();
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiBase => callEvent => " + e.getMessage());
            }
        }
    }

    default void callProgress(long trySeeDuration, long position, long duration) {

        // component
        try {
            ViewGroup viewGroup = getBaseComponentViewGroup();
            int childCount = viewGroup.getChildCount();
            if (childCount <= 0)
                throw new Exception("not find component");
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                if (!(childAt instanceof ComponentApi))
                    continue;
                ((ComponentApi) childAt).onUpdateProgress(false, trySeeDuration, position, duration);
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiComponent => callProgress => " + e.getMessage());
            }
        }

        // listenr
        try {
            OnPlayerProgressListener progressListener = getOnPlayerProgressListener();
            if (null != progressListener) {
                progressListener.onProgress(position, duration);
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiListener => callProgress => " + e.getMessage());
            }
        }
    }

    default void callSubtitle(int kernel, CharSequence result) {

        // component
        try {
            ViewGroup viewGroup = getBaseComponentViewGroup();
            int childCount = viewGroup.getChildCount();
            if (childCount <= 0)
                throw new Exception("not find component");
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                if (!(childAt instanceof ComponentApi))
                    continue;
                ((ComponentApi) childAt).onUpdateSubtitle(kernel, result);
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiComponent => callSubtitle => " + e.getMessage());
            }
        }

//        // listenr
//        try {
//            OnPlayerProgressListener progressListener = getOnPlayerProgressListener();
//            if (null != progressListener) {
//                progressListener.onProgress(position, duration);
//            }
//        } catch (Exception e) {
//            LogUtil.log("VideoPlayerApiListener => callProgress => " + e.getMessage());
//        }
    }

    default void callSpeed(int kernel, CharSequence result) {

        // component
        try {
            ViewGroup viewGroup = getBaseComponentViewGroup();
            int childCount = viewGroup.getChildCount();
            if (childCount <= 0)
                throw new Exception("not find component");
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                if (!(childAt instanceof ComponentApi))
                    continue;
                ((ComponentApi) childAt).onUpdateSpeed(kernel, result);
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiComponent => callSpeed => " + e.getMessage());
            }
        }

//        // listenr
//        try {
//            OnPlayerProgressListener progressListener = getOnPlayerProgressListener();
//            if (null != progressListener) {
//                progressListener.onProgress(position, duration);
//            }
//        } catch (Exception e) {
//            LogUtil.log("VideoPlayerApiListener => callSpeed => " + e.getMessage());
//        }
    }

    /**************/

    default void clearOnPlayerListener() {
        setOnPlayerEventListener(null);
        setOnPlayerWindowListener(null);
        setOnPlayerProgressListener(null);
    }

    /**************/

    HashMap<VideoPlayerApiBase, OnPlayerEventListener> mOnPlayerEventListener = new HashMap<>();

    default OnPlayerEventListener getOnPlayerEventListener() {
        try {
            OnPlayerEventListener listener = mOnPlayerEventListener.get(this);
            if (null == listener)
                throw new Exception("warning: listener null");
            return listener;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiListener => getOnPlayerEventListener => Exception " + e.getMessage());
            }
            return null;
        }
    }

    default void setOnPlayerEventListener(OnPlayerEventListener l) {
        mOnPlayerEventListener.put(this, l);
    }


    /**************/

    HashMap<VideoPlayerApiBase, OnPlayerProgressListener> mOnPlayerProgressListener = new HashMap<>();

    default OnPlayerProgressListener getOnPlayerProgressListener() {
        try {
            OnPlayerProgressListener listener = mOnPlayerProgressListener.get(this);
            if (null == listener)
                throw new Exception("warning: listener null");
            return listener;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiListener => getOnPlayerProgressListener => Exception " + e.getMessage());
            }
            return null;
        }
    }

    default void setOnPlayerProgressListener(OnPlayerProgressListener l) {
        mOnPlayerProgressListener.put(this, l);
    }

    /**************/

    HashMap<VideoPlayerApiBase, OnPlayerWindowListener> mOnPlayerWindowListener = new HashMap<>();

    default OnPlayerWindowListener getOnPlayerWindowListener() {
        try {
            OnPlayerWindowListener listener = mOnPlayerWindowListener.get(this);
            if (null == listener)
                throw new Exception("warning: listener null");
            return listener;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiListener => getOnPlayerWindowListener => Exception " + e.getMessage());
            }
            return null;
        }
    }

    default void setOnPlayerWindowListener(OnPlayerWindowListener l) {
        mOnPlayerWindowListener.put(this, l);
    }

    /**************/

    HashMap<VideoPlayerApiBase, OnPlayerEpisodeListener> mOnPlayerEpisodeListener = new HashMap<>();

    default OnPlayerEpisodeListener getOnPlayerEpisodeListener() {
        try {
            OnPlayerEpisodeListener listener = mOnPlayerEpisodeListener.get(this);
            if (null == listener)
                throw new Exception("warning: listener null");
            return listener;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiListener => getOnPlayerEpisodeListener => Exception " + e.getMessage());
            }
            return null;
        }
    }

    default void setOnPlayerEpisodeListener(OnPlayerEpisodeListener l) {
        mOnPlayerEpisodeListener.put(this, l);
    }
}
