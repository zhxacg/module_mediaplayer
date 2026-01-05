
package lib.kalu.mediaplayer.core.player.video;

import android.view.View;
import android.view.ViewGroup;

import lib.kalu.mediaplayer.bean.args.StartArgs;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.core.component.ComponentApi;
import lib.kalu.mediaplayer.listener.OnPlayerEpisodeListener;
import lib.kalu.mediaplayer.listener.OnPlayerEventListener;
import lib.kalu.mediaplayer.listener.OnPlayerPlaybackChangedListener;
import lib.kalu.mediaplayer.listener.OnPlayerProgressListener;
import lib.kalu.mediaplayer.listener.OnPlayerScreenOrientationChangeListener;
import lib.kalu.mediaplayer.listener.OnPlayerVisibilityChangedListener;
import lib.kalu.mediaplayer.listener.OnPlayerWindowAttachChangedListener;
import lib.kalu.mediaplayer.listener.OnPlayerWindowStateChangeListener;
import lib.kalu.mediaplayer.listener.OnPlayerWindowVisibilityChangedListener;
import lib.kalu.mediaplayer.proxy.Proxy;
import lib.kalu.mediaplayer.proxy.ProxyBuried;
import lib.kalu.mediaplayer.util.LogUtil;

public interface VideoPlayerApiCall extends VideoPlayerApiBase, VideoPlayerApiListener {

    String TAG = "VideoPlayerApiCall22";

    default void callWindow(@PlayerType.WindowType.Value int state) {

        // component
        callComponentWindowState(state);

        // listener
        callPlayerWindowStateChanged(state);

        // 埋点
        onBuriedWindow(state);
    }


    default void callEvent(@PlayerType.EventType.Value int state) {
        callEvent(true, true, state);
    }

    default void callEvent(boolean callPlayer, boolean callComponent, @PlayerType.EventType.Value int state) {

        // component
        if (callComponent) {
            callComponentEvent(state);
        }

        // listener
        if (callPlayer) {
            callPlayerEvent(state);
        }
    }

    default void callScreenLandspace(boolean callPlayer, boolean callComponent) {

        // component
        if (callComponent) {
            callComponentScreenOrientation(false);
        }

        // listener
        if (callPlayer) {
            callPlayerScreenOrientationChanged(false);
        }
    }

    default void callScreenPortrait(boolean callPlayer, boolean callComponent) {

        // component
        if (callComponent) {
            callComponentScreenOrientation(true);
        }

        // listener
        if (callPlayer) {
            callPlayerScreenOrientationChanged(true);
        }
    }

    default void callProgress(long trySeeDuration, long position, long duration) {

        // component
        callComponentProgress(trySeeDuration, position, duration);

        // listener
        callPlayerProgress(trySeeDuration, position, duration);
    }

    default void callSubtitle(int kernel, CharSequence value) {

        // component
        callComponentSubtitle(kernel, value);

        // listener
    }

    default void callNetSpeed(int kernel, CharSequence value) {

        // component
        callComponentNetSpeed(kernel, value);
    }

    default void callPlaybackSpeed(int kernel, float value) {

        // listener
        callPlayerPlaybackSpeed(value);
    }

    default void callComponentScreenLandspace() {
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
                ((ComponentApi) childAt).callScreenLandspace();
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callComponentScreenLandspace -> " + e.getMessage());
            }
        }
    }

    default void callComponentScreenOrientation(boolean isPortrait) {
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
                if (isPortrait) {
                    ((ComponentApi) childAt).callScreenPortrait();
                } else {
                    ((ComponentApi) childAt).callScreenLandspace();
                }
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callComponentScreenOrientation -> " + e.getMessage());
            }
        }
    }

    default void callComponentSubtitle(int kernel, CharSequence value) {
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
                ((ComponentApi) childAt).onUpdateSubtitle(kernel, value);
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callComponentSubtitle -> " + e.getMessage());
            }
        }
    }

    default void callComponentNetSpeed(int kernel, CharSequence value) {
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
                ((ComponentApi) childAt).onUpdateNetSpeed(kernel, value);
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callComponentSpeed -> " + e.getMessage());
            }
        }
    }

    default void callComponentProgress(long trySeeDuration, long position, long duration) {
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
                LogUtil.log(TAG, "callComponentProgress -> " + e.getMessage());
            }
        }
    }

    default void callComponentEvent(@PlayerType.EventType.Value int state) {
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
                ((ComponentApi) childAt).callEvent(state);
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callComponentEvent -> " + e.getMessage());
            }
        }
    }

    default void callComponentWindowState(@PlayerType.WindowType.Value int state) {
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
                LogUtil.log(TAG, "callComponentWindowState -> " + e.getMessage());
            }
        }
    }

    default void callPlayerEvent(@PlayerType.EventType.Value int state) {
        try {
            OnPlayerEventListener onPlayerEventListener = getPlayerEventListener();
            if (null == onPlayerEventListener)
                throw new Exception("warning: eventListener null");
            onPlayerEventListener.onEvent(state);
            if (state == PlayerType.EventType.START) {
                onPlayerEventListener.onStart();
            } else if (state == PlayerType.EventType.COMPLETE) {
                onPlayerEventListener.onComplete();
            } else if (state == PlayerType.EventType.PAUSE) {
                onPlayerEventListener.onPause();
            } else if (state == PlayerType.EventType.RESUME) {
                onPlayerEventListener.onResume();
            } else if (state == PlayerType.EventType.ERROR) {
                onPlayerEventListener.onError(null);
            } else if (state == PlayerType.EventType.BUFFERING_START) {
                onPlayerEventListener.onBufferingStart();
            } else if (state == PlayerType.EventType.BUFFERING_STOP) {
                onPlayerEventListener.onBufferingStop();
            } else if (state == PlayerType.EventType.PREPARE) {
                onPlayerEventListener.onPrepare();
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callPlayerEvent -> " + e.getMessage());
            }
        }
    }

    default void callPlayerWindowStateChanged(@PlayerType.WindowType.Value int state) {
        try {
            OnPlayerWindowStateChangeListener onPlayerWindowStateChangeListener = getPlayerWindowStateChangeListener();
            if (null == onPlayerWindowStateChangeListener)
                throw new Exception("warning: onPlayerWindowStateChangeListener null");
            onPlayerWindowStateChangeListener.onState(state);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callPlayerWindowStateChanged -> " + e.getMessage());
            }
        }
    }

    default void callPlayerPlaybackSpeed(float value) {
        try {
            if (value <= 0)
                throw new Exception("error: value <= 0, value = " + value);
            OnPlayerPlaybackChangedListener onPlayerPlaybackChangedListener = getOnPlayerPlaybackChangedListener();
            if (null == onPlayerPlaybackChangedListener)
                throw new Exception("warning: onPlayerSpeedChangedListener null");
            onPlayerPlaybackChangedListener.onSpeed(value);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callPlayerPlaybackSpeed -> " + e.getMessage());
            }
        }
    }

    default void callPlayerProgress(long trySeeDuration, long position, long duration) {
        try {
            OnPlayerProgressListener onPlayerProgressListener = getPlayerProgressListener();
            if (null == onPlayerProgressListener)
                throw new Exception("warning: onPlayerProgressListener null");
            onPlayerProgressListener.onProgress(trySeeDuration, position, duration);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callPlayerProgress -> " + e.getMessage());
            }
        }
    }

    default void callPlayerEpisode(int position, int count) {
        try {
            OnPlayerEpisodeListener onPlayerEpisodeListener = getPlayerEpisodeListener();
            if (null == onPlayerEpisodeListener)
                throw new Exception("warning: onPlayerEpisodeListener null");
            onPlayerEpisodeListener.onEpisode(position, count);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callPlayerEpisode -> " + e.getMessage());
            }
        }
    }

    default void callPlayerVisibilityChanged(int visibility) {
        try {
            OnPlayerVisibilityChangedListener onPlayerVisibilityChangedListener = getPlayerVisibilityChangedListener();
            if (null == onPlayerVisibilityChangedListener)
                throw new Exception("warning: onPlayerVisibilityChangedListener null");
            onPlayerVisibilityChangedListener.onVisibilityChanged(visibility);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callPlayerVisibilityChanged -> " + e.getMessage());
            }
        }
    }

    default void callPlayerWindowVisibilityChanged(int visibility) {
        try {
            OnPlayerWindowVisibilityChangedListener onPlayerWindowVisibilityChangedListener = getPlayerWindowVisibilityChangedListener();
            if (null == onPlayerWindowVisibilityChangedListener)
                throw new Exception("warning: onPlayerWindowVisibilityChangedListener null");
            onPlayerWindowVisibilityChangedListener.onWindowVisibilityChanged(visibility);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callPlayerWindowVisibilityChanged -> " + e.getMessage());
            }
        }
    }

    default void callPlayerWindowAttachChanged(boolean isAttach) {
        try {
            OnPlayerWindowAttachChangedListener onPlayerWindowAttachChangedListener = getPlayerWindowAttachChangedListener();
            if (null == onPlayerWindowAttachChangedListener)
                throw new Exception("warning: onPlayerWindowAttachChangedListener null");
            if (isAttach) {
                onPlayerWindowAttachChangedListener.onAttachedToWindow();
            } else {
                onPlayerWindowAttachChangedListener.onDetachedFromWindow();
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callPlayerWindowAttachChanged -> " + e.getMessage());
            }
        }
    }

    default void callPlayerScreenOrientationChanged(boolean isPortrait) {
        try {
            OnPlayerScreenOrientationChangeListener onPlayerScreenOrientationChangeListener = getPlayerScreenOrientationChangeListener();
            if (null == onPlayerScreenOrientationChangeListener)
                throw new Exception("warning: onPlayerScreenOrientationChangeListener null");
            if (isPortrait) {
                onPlayerScreenOrientationChangeListener.onPortrait();
            } else {
                onPlayerScreenOrientationChangeListener.onLandspace();
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callPlayerScreenOrientationChanged -> " + e.getMessage());
            }
        }
    }

    /***************/

    default void onBuriedVideoRenderingStart() {
        callBuried("onVideoRenderingStart");
    }

    default void onBuriedStart() {
        callBuried("onStart");
    }

    default void onBuriedError(@PlayerType.EventType.Value int code) {
        callBuried("onError");
    }

    default void onBuriedPause() {
        callBuried("onPause");
    }

    default void onBuriedResume() {
        callBuried("onResume");
    }

    default void onBuriedComplete() {
        callBuried("onComplete");
    }

    default void onBuriedStop() {
        callBuried("onStop");
    }

    default void onBuriedBufferingStart() {
        callBuried("onBufferingStart");
    }

    default void onBuriedBufferingStop() {
        callBuried("onBufferingStop");
    }

    default void onBuriedSeekStartForward() {
        callBuried("onSeekStartForward");
    }

    default void onBuriedSeekStartRewind() {
        callBuried("onSeekStartRewind");
    }

    default void onBuriedSeekFinish() {
        callBuried("onSeekFinish");
    }

    default void onBuriedWindow(@PlayerType.WindowType.Value int type) {
        callBuried("onWindow");
    }

    default void callBuried(String name) {
        try {
            StartArgs startArgs = getStartArgs();
            if (null == startArgs)
                throw new Exception("error: startArgs null");
            Proxy proxy = startArgs.getProxy();
            if (null == proxy)
                throw new Exception("error: proxy null");
            ProxyBuried proxyBuried = proxy.getProxyBuried();
            if (null == proxyBuried)
                throw new Exception("error: proxyBuried null");
            long position = ((VideoPlayerApi) this).getPosition();
            if (position < 0L) {
                position = -1L;
            }
            long duration = ((VideoPlayerApi) this).getDuration();
            if (duration < 0L) {
                duration = -1L;
            }
            proxyBuried.onCall(name, startArgs, position, duration);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callBuried -> Exception " + e.getMessage());
            }
        }
    }
}
