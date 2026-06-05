
package lib.kalu.mediaplayer.core.player.video;

import android.view.View;
import android.view.ViewGroup;

import lib.kalu.mediaplayer.PlayerSDK;
import lib.kalu.mediaplayer.bean.args.ConfigArgs;
import lib.kalu.mediaplayer.bean.args.StartArgs;
import lib.kalu.mediaplayer.bean.info.PlayInfo;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.buried.PlayBuried;
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
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.util.PlayStateUtil;

public interface VideoPlayerApiCall extends VideoPlayerApiBase, VideoPlayerApiListener {

    String TAG = "VideoPlayerApiCall22";

    default void callScreenOrientation(boolean callPlayer, boolean callComponent, @PlayerType.ScreenOrientation.Value int value) {

        // component
        if (callComponent) {
            callComponentScreenOrientation(value);
        }

        // listener
        if (callPlayer) {
            callPlayerScreenOrientation(value);
        }
    }

    default void callComponentScreenOrientation(@PlayerType.ScreenOrientation.Value int value) {
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
                ((ComponentApi) childAt).onUpdateScreenOrientation(value);
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callComponentScreenOrientation -> " + e.getMessage());
            }
        }
    }

    default void callPlayerScreenOrientation(@PlayerType.ScreenOrientation.Value int value) {
        try {
            OnPlayerScreenOrientationChangeListener onPlayerScreenOrientationChangeListener = getPlayerScreenOrientationChangeListener();
            if (null == onPlayerScreenOrientationChangeListener)
                throw new Exception("warning: onPlayerScreenOrientationChangeListener null");
            if (value == PlayerType.ScreenOrientation.PORTRAIT) {
                onPlayerScreenOrientationChangeListener.onPortrait();
            } else if (value == PlayerType.ScreenOrientation.LANDSPACE) {
                onPlayerScreenOrientationChangeListener.onLandspace();
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callPlayerScreenOrientation -> " + e.getMessage());
            }
        }
    }

    default void callVolume(boolean callPlayer, boolean callComponent, float volume) {

        if (callComponent) {
            callComponentVolume(volume);
        }

        if (callPlayer) {
            callPlayerVolume(volume);
        }
    }

    default void callComponentVolume(float volume) {
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
                ((ComponentApi) childAt).onUpdateVolume(volume);
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callComponentVolume -> " + e.getMessage());
            }
        }
    }

    default void callPlayerVolume(float volume) {

    }

    default void callWindow(@PlayerType.WindowType.Value int state) {

        // component
        callComponentWindowState(state);

        // listener
        callPlayerWindowStateChanged(state);

        // 埋点
        onBuriedWindow(state);
    }


    default void callEvent(@PlayerType.EventType.Value int playState) {
        callEvent(true, true, playState);
    }

    default void callEvent(boolean callPlayer, boolean callComponent, @PlayerType.EventType.Value int playState) {

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "callEvent -> callPlayer = " + callPlayer + ", callComponent = " + callComponent + ", playState = " + playState);
        }

        // component
        if (callComponent) {
            callComponentEvent(playState);
        }

        // listener
        if (callPlayer) {
            callPlayerEvent(playState);
        }
    }

    default void callProgress(long trySeeDuration, long position, long duration) {

//        if (LogUtil.DEBUG) {
//            LogUtil.log(TAG, "callProgress -> trySeeDuration = " + trySeeDuration + ", position = " + position + ", duration = " + duration);
//        }

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

//        if (LogUtil.DEBUG) {
//            LogUtil.log(TAG, "callComponentProgress -> trySeeDuration = " + trySeeDuration + ", position = " + position + ", duration = " + duration);
//        }

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

    default void callComponentEvent(@PlayerType.EventType.Value int playState) {

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "callComponentEvent -> playState = " + playState);
        }

        try {
            ViewGroup viewGroup = getBaseComponentViewGroup();
            int childCount = viewGroup.getChildCount();
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callComponentEvent -> playState = " + playState + ", childCount = " + childCount);
            }
            if (childCount <= 0)
                throw new Exception("not find component");
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "callComponentEvent -> playState = " + playState + ", childCount = " + childCount + ", index = " + i + ", childAt = " + childAt);
                }
                if (!(childAt instanceof ComponentApi))
                    continue;
                ((ComponentApi) childAt).onUpdateEvent(playState);
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
                ((ComponentApi) childAt).onUpdateWindow(state);
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callComponentWindowState -> " + e.getMessage());
            }
        }
    }

    default void callPlayerEvent(@PlayerType.EventType.Value int playState) {
        try {
            OnPlayerEventListener onPlayerEventListener = getPlayerEventListener();
            if (null == onPlayerEventListener)
                throw new Exception("warning: eventListener null");
            onPlayerEventListener.onEvent(playState);

            boolean error = PlayStateUtil.isError(playState);
            if (error) {
                onPlayerEventListener.onError(playState);
            } else if (playState == PlayerType.EventType.START) {
                onPlayerEventListener.onStart();
            } else if (playState == PlayerType.EventType.END) {
                onPlayerEventListener.onComplete();
            } else if (playState == PlayerType.EventType.PAUSE) {
                onPlayerEventListener.onPause();
            } else if (playState == PlayerType.EventType.RESUME) {
                onPlayerEventListener.onResume();
            } else if (playState == PlayerType.EventType.MEDIA_INFO_BUFFERING_START) {
                onPlayerEventListener.onBufferingStart();
            } else if (playState == PlayerType.EventType.MEDIA_INFO_BUFFERING_STOP) {
                onPlayerEventListener.onBufferingStop();
            } else if (playState == PlayerType.EventType.MEDIA_INFO_PREPARE) {
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


    /***************/

    default void onBuriedVideoRenderingStart() {
        callBuried(PlayerType.BuriedType.VIDEO_RENDERING_START);
    }

    default void onBuriedStart() {
        callBuried(PlayerType.BuriedType.START);
    }

    default void onBuriedError(@PlayerType.EventType.Value int code) {
        boolean prepared = ((VideoPlayerApi) this).isPrepared();
        if (prepared) {
            callBuried(PlayerType.BuriedType.ERROR_PREPARE);
        } else {
            callBuried(PlayerType.BuriedType.ERROR_PLAY);
        }
    }

    default void onBuriedTrySeeEnd() {
        callBuried(PlayerType.BuriedType.TRY_SEE_END);
    }

    default void onBuriedPause() {
        callBuried(PlayerType.BuriedType.PAUSE);
    }

    default void onBuriedResume() {
        callBuried(PlayerType.BuriedType.RESUME);
    }

    default void onBuriedComplete() {
        callBuried(PlayerType.BuriedType.COMPLETED);
    }

    default void onBuriedStop() {
        callBuried(PlayerType.BuriedType.STOP);
    }

    default void onBuriedRelease() {
        callBuried(PlayerType.BuriedType.RELEASE);
    }

    default void onBuriedBufferingStart() {
        callBuried(PlayerType.BuriedType.BUFFERING_START);
    }

    default void onBuriedBufferingStop() {
        callBuried(PlayerType.BuriedType.BUFFERING_STOP);
    }

    default void onBuriedSeekStartForward() {
        callBuried(PlayerType.BuriedType.SEEK_START_FORWARD);
    }

    default void onBuriedSeekStartRewind() {
        callBuried(PlayerType.BuriedType.SEEK_START_REWIND);
    }

    default void onBuriedSeekFinish() {
        callBuried(PlayerType.BuriedType.SEEK_FINISH);
    }

    default void onBuriedWindow(@PlayerType.WindowType.Value int type) {
        callBuried(PlayerType.BuriedType.UPDATE_WINDOW);
    }

    default void onBuriedSubtitleOffsetMs(int offsetMs) {
        callBuried(PlayerType.BuriedType.UPDATE_SUBTITLE_OFFSET_MS, offsetMs);
    }

    default void callBuried(@PlayerType.BuriedType int value, Object... objs) {

        try {

            StartArgs startArgs = getStartArgs();
            if (null == startArgs)
                throw new Exception("error: startArgs null");

            //
            ConfigArgs configArgs = PlayerSDK.getInstance().getConfigArgs();
            if (null == configArgs)
                throw new Exception("error: configArgs null");

            PlayBuried playBuried = configArgs.getPlayBuried();
            if (null == playBuried)
                throw new Exception("error: playBuried null");

            long position = ((VideoPlayerApi) this).getPosition();
            if (position < 0L) {
                position = 0L;
            }
            long duration = ((VideoPlayerApi) this).getDuration();
            if (duration < 0L) {
                duration = 0L;
            }

            long playWhenReadySeekToPosition = startArgs.getPlayWhenReadySeekToPosition();
            long tryseeDuration = startArgs.getTrySeeDuration();
            boolean prepared = ((VideoPlayerApi) this).isPrepared();
            boolean live = ((VideoPlayerApi) this).isLiveStream();
            float speed = ((VideoPlayerApi) this).getSpeed();
            int scale = ((VideoPlayerApi) this).getVideoScale();
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callBuried -> buriedType = " + value + ", position = " + position + ", duration = " + duration + ", speed = " + speed + ", scale = " + scale + ", live = " + live + ", tryseeDuration = " + tryseeDuration + ", prepared = " + prepared + ", playWhenReadySeekToPosition = " + playWhenReadySeekToPosition);
            }

            if (value == PlayerType.BuriedType.UPDATE_SUBTITLE_OFFSET_MS) {
                playBuried.onCall(value, startArgs, new PlayInfo(playWhenReadySeekToPosition, tryseeDuration, "", prepared, ((int) objs[0]), live, position, duration, speed, scale));
            } else if (value == PlayerType.BuriedType.STOP) {
                boolean fromInit = ((boolean) objs[0]);
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "callBuried -> fromInit = " + fromInit);
                }
                String stopReason = fromInit ? "stopFromInit" : "stopFromUser";
                playBuried.onCall(value, startArgs, new PlayInfo(playWhenReadySeekToPosition, tryseeDuration, stopReason, prepared, 0, live, position, duration, speed, scale));
            } else {
                playBuried.onCall(value, startArgs, new PlayInfo(playWhenReadySeekToPosition, tryseeDuration, "", prepared, 0, live, position, duration, speed, scale));
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callBuried -> Exception " + e.getMessage());
            }
        }
    }
}
