
package lib.kalu.mediaplayer.core.player.video;

import android.view.View;
import android.view.ViewGroup;

import java.util.concurrent.ExecutionException;

import lib.kalu.mediaplayer.PlayerSDK;
import lib.kalu.mediaplayer.bean.args.StartArgs;
import lib.kalu.mediaplayer.bean.info.PlayInfo;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.buried.PlayBuried;
import lib.kalu.mediaplayer.core.component.ComponentApi;
import lib.kalu.mediaplayer.listener.OnPlayerBandwidthListener;
import lib.kalu.mediaplayer.listener.OnPlayerEpisodeListener;
import lib.kalu.mediaplayer.listener.OnPlayerEventListener;
import lib.kalu.mediaplayer.listener.OnPlayerPlaybackChangedListener;
import lib.kalu.mediaplayer.listener.OnPlayerProgressListener;
import lib.kalu.mediaplayer.listener.OnPlayerScreenOrientationChangeListener;
import lib.kalu.mediaplayer.listener.OnPlayerStuckListener;
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
            try {

                ViewGroup viewGroup = getBaseComponentViewGroup();
                int childCount = viewGroup.getChildCount();
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "callComponentScreenOrientation -> childCount = " + childCount);
                }
                if (childCount > 0) {
                    for (int i = 0; i < childCount; i++) {
                        View childAt = viewGroup.getChildAt(i);
                        if (null == childAt)
                            continue;
                        if (!(childAt instanceof ComponentApi))
                            continue;
                        ((ComponentApi) childAt).onUpdateScreenOrientation(value);
                    }
                }
            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "callComponentScreenOrientation -> " + e.getMessage());
                }
            }
        }

        // listener
        if (callPlayer) {
            try {
                OnPlayerScreenOrientationChangeListener listener = getPlayerScreenOrientationChangeListener();
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "callPlayerScreenOrientation -> value = " + value + ", listener = " + listener);
                }

                if (null == listener)
                    return;
                if (value == PlayerType.ScreenOrientation.PORTRAIT) {
                    listener.onPortrait();
                } else if (value == PlayerType.ScreenOrientation.LANDSPACE) {
                    listener.onLandspace();
                }

            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "callPlayerScreenOrientation -> " + e.getMessage());
                }
            }
        }
    }

    default void callVolume(boolean callPlayer, boolean callComponent, float volume) {

        if (callComponent) {
            try {

                ViewGroup viewGroup = getBaseComponentViewGroup();
                int childCount = viewGroup.getChildCount();
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "callComponentVolume -> childCount = " + childCount);
                }
                if (childCount > 0) {
                    for (int i = 0; i < childCount; i++) {
                        View childAt = viewGroup.getChildAt(i);
                        if (null == childAt)
                            continue;
                        if (!(childAt instanceof ComponentApi))
                            continue;
                        ((ComponentApi) childAt).onUpdateVolume(volume);
                    }
                }
            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "callComponentVolume -> " + e.getMessage());
                }
            }
        }

        if (callPlayer) {
        }
    }

    default void callWindow(@PlayerType.WindowType.Value int state) {

        // component
        try {
            ViewGroup viewGroup = getBaseComponentViewGroup();
            int childCount = viewGroup.getChildCount();
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callWindow -> childCount = " + childCount);
            }
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    View childAt = viewGroup.getChildAt(i);
                    if (null == childAt)
                        continue;
                    if (!(childAt instanceof ComponentApi))
                        continue;
                    ((ComponentApi) childAt).onUpdateWindow(state);
                }
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callComponentWindowState -> " + e.getMessage());
            }
        }

        // listener
        try {
            OnPlayerWindowStateChangeListener onPlayerWindowStateChangeListener = getPlayerWindowStateChangeListener();
            if (null == onPlayerWindowStateChangeListener) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "callPlayerWindowStateChanged -> warning: onPlayerWindowStateChangeListener null");
                }
                return;
            }
            onPlayerWindowStateChangeListener.onState(state);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callPlayerWindowStateChanged -> " + e.getMessage());
            }
        }

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
            try {
                ViewGroup viewGroup = getBaseComponentViewGroup();
                int childCount = viewGroup.getChildCount();

                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "callEvent -> callComponentEvent -> childCount = " + childCount);
                }
                if (childCount > 0) {
                    for (int i = 0; i < childCount; i++) {
                        View childAt = viewGroup.getChildAt(i);
                        if (null == childAt)
                            continue;
//                if (LogUtil.DEBUG) {
//                    LogUtil.log(TAG, "callEvent -> callComponentEvent -> playState = " + playState + ", childCount = " + childCount + ", index = " + i + ", childAt = " + childAt);
//                }
                        if (!(childAt instanceof ComponentApi))
                            continue;
                        ((ComponentApi) childAt).onUpdateEvent(playState);
                    }
                }
            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "callEvent -> callComponentEvent -> " + e.getMessage());
                }
            }
        }

        // listener
        if (callPlayer) {
            try {
                OnPlayerEventListener onPlayerEventListener = getPlayerEventListener();
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "callEvent -> callPlayerEvent -> onPlayerEventListener = " + onPlayerEventListener);
                }
                if (null == onPlayerEventListener)
                    return;

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
                    LogUtil.log(TAG, "callEvent -> callPlayerEvent -> " + e.getMessage());
                }
            }
        }
    }

    default void callProgress(long trySeeDuration, long position, long duration) {

//        if (LogUtil.DEBUG) {
//            LogUtil.log(TAG, "callProgress -> trySeeDuration = " + trySeeDuration + ", position = " + position + ", duration = " + duration);
//        }

        // component
        try {
            ViewGroup viewGroup = getBaseComponentViewGroup();
            int childCount = viewGroup.getChildCount();
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callComponentProgress -> childCount = " + childCount);
            }
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    View childAt = viewGroup.getChildAt(i);
                    if (null == childAt)
                        continue;
                    if (!(childAt instanceof ComponentApi))
                        continue;
                    ((ComponentApi) childAt).onUpdateProgress(false, trySeeDuration, position, duration);
                }
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callComponentProgress -> " + e.getMessage());
            }
        }

        // listener
        try {
            OnPlayerProgressListener onPlayerProgressListener = getPlayerProgressListener();
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callPlayerProgress -> warning: onPlayerProgressListener null");
            }
            if (null == onPlayerProgressListener)
                return;
            onPlayerProgressListener.onProgress(trySeeDuration, position, duration);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callPlayerProgress -> " + e.getMessage());
            }
        }
    }

    default void callSubtitle(int kernel, CharSequence value) {

        // component
        try {
            ViewGroup viewGroup = getBaseComponentViewGroup();
            int childCount = viewGroup.getChildCount();
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callComponentSubtitle -> childCount = " + childCount);
            }
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    View childAt = viewGroup.getChildAt(i);
                    if (null == childAt)
                        continue;
                    if (!(childAt instanceof ComponentApi))
                        continue;
                    ((ComponentApi) childAt).onUpdateSubtitle(kernel, value);
                }
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callComponentSubtitle -> " + e.getMessage());
            }
        }

        // listener
    }

    default void callBandwidth(int kernel, long totalLoadTimeMs, long estimateKBs, long realAvgKBs) {

        // component
        try {
            ViewGroup viewGroup = getBaseComponentViewGroup();
            int childCount = viewGroup.getChildCount();
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callComponentSubtitle -> childCount = " + childCount);
            }
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    View childAt = viewGroup.getChildAt(i);
                    if (null == childAt)
                        continue;
                    if (!(childAt instanceof ComponentApi))
                        continue;
                    ((ComponentApi) childAt).onUpdateBandwidth(kernel, totalLoadTimeMs, estimateKBs, realAvgKBs);
                }
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callComponentBandwidth -> " + e.getMessage());
            }
        }

        // listener
        try {
            OnPlayerBandwidthListener listener = getOnPlayerBandwidthListener();
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callPlayerBandwidth -> kernel = " + kernel + ", totalLoadTimeMs = " + totalLoadTimeMs + ", estimateKBs = " + estimateKBs + ", realAvgKBs = " + realAvgKBs + ", listener = " + listener);
            }
            if (null != listener) {
                listener.onBandwidth(kernel, totalLoadTimeMs, estimateKBs, realAvgKBs);
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callComponentBandwidth -> " + e.getMessage());
            }
        }
    }

    default void callStuckNet(int kernel, long videoBitrate, long netBitrate) {

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "callStuckNet -> kernel = " + kernel + ", videoBitrate = " + videoBitrate + ", netBitrate = " + netBitrate);
        }

        // component
        try {
            ViewGroup viewGroup = getBaseComponentViewGroup();
            int childCount = viewGroup.getChildCount();
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callStuckNet -> childCount = " + childCount);
            }
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    View childAt = viewGroup.getChildAt(i);
                    if (null == childAt)
                        continue;
                    if (!(childAt instanceof ComponentApi))
                        continue;
                    ((ComponentApi) childAt).onUpdateStuckNet(kernel, videoBitrate, netBitrate);
                }
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callStuckNet -> " + e.getMessage());
            }
        }

        // listener
        try {
            OnPlayerStuckListener listener = getOnPlayerStuckListener();
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callStuckNet -> listener = " + listener);
            }
            if (null == listener)
                return;
            listener.onStuckNet(kernel, videoBitrate, netBitrate);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callStuckNet -> " + e.getMessage());
            }
        }
    }

    default void callPlaybackSpeed(int kernel, float value) {

        // listener
        try {
            if (value <= 0) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "callPlayerPlaybackSpeed -> error: value <= 0, value = " + value);
                }
                return;
            }
            OnPlayerPlaybackChangedListener onPlayerPlaybackChangedListener = getOnPlayerPlaybackChangedListener();
            if (null == onPlayerPlaybackChangedListener) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "callPlayerPlaybackSpeed -> warning: onPlayerSpeedChangedListener null");
                }
                return;
            }
            onPlayerPlaybackChangedListener.onSpeed(value);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "callPlayerPlaybackSpeed -> " + e.getMessage());
            }
        }
    }

    default void callPlayerEpisode(int position, int count) {
        try {
            OnPlayerEpisodeListener onPlayerEpisodeListener = getPlayerEpisodeListener();
            if (null == onPlayerEpisodeListener) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "callPlayerEpisode -> warning: onPlayerEpisodeListener null");
                }
                return;
            }
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
            if (null == onPlayerVisibilityChangedListener) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "callPlayerVisibilityChanged -> warning: onPlayerVisibilityChangedListener null");
                }
                return;
            }
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
            if (null == onPlayerWindowVisibilityChangedListener) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "callPlayerWindowVisibilityChanged -> warning: onPlayerWindowVisibilityChangedListener null");
                }
                return;
            }
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
            if (null == onPlayerWindowAttachChangedListener) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "callPlayerWindowAttachChanged -> warning: onPlayerWindowAttachChangedListener null");
                }
                return;
            }
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
            if (null == startArgs) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "callBuried -> error: startArgs null");
                }
                return;
            }

            PlayBuried playBuried = PlayerSDK.playBuried;
            if (null == playBuried) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "callBuried -> error: playBuried null");
                }
                return;
            }

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
