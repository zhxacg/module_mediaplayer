package lib.kalu.mediaplayer.core.player.video;

import android.content.Context;

import androidx.annotation.FloatRange;

import java.util.List;

import lib.kalu.mediaplayer.bean.args.StartArgs;
import lib.kalu.mediaplayer.bean.info.TrackInfo;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.collect.HlsSpanList;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelFactoryManager;
import lib.kalu.mediaplayer.error.NetworkError;
import lib.kalu.mediaplayer.error.UrlEmptyError;
import lib.kalu.mediaplayer.proxy.Proxy;
import lib.kalu.mediaplayer.proxy.ProxyTrack;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.util.NetworkUtil;
import lib.kalu.mediaplayer.util.PlayStateUtil;
import lib.kalu.mediaplayer.util.SpeedUtil;

public interface VideoPlayerApiKernel extends VideoPlayerApiListener,
        VideoPlayerApiComponent,
        VideoPlayerApiRender,
        VideoPlayerApiDevice,
        VideoPlayerApiCall, VideoPlayerApiBase {

    String TAG = "VideoPlayerApiKernel22";

    default boolean isDoWindowing() {
        try {
            VideoKernelApi videoKernel = getVideoKernel();
            if (null == videoKernel)
                throw new Exception("error: videoKernel null");
            return videoKernel.isDoWindowing();
        } catch (Exception e) {
            return false;
        }
    }

    default void setDoWindowing(boolean v) {
        try {
            VideoKernelApi videoKernel = getVideoKernel();
            if (null == videoKernel)
                throw new Exception("error: videoKernel null");
            videoKernel.setDoWindowing(v);
        } catch (Exception e) {
        }
    }

    @Override
    default void start(StartArgs startArgs) {
        try {
            callEvent(PlayerType.EventType.INIT);
            Context context = getBaseContext();
            boolean connected = NetworkUtil.isConnected(context);
            if (!connected)
                throw new NetworkError();
            boolean containsMainUrl = startArgs.containsMainUrl();
            if (!containsMainUrl)
                throw new UrlEmptyError("error: containsMainUrl false");
            // 1
            boolean log = startArgs.isLog();
            LogUtil.setLogger(log);
            // 2
            boolean initRelease = startArgs.isInitRelease();
            if (initRelease) {
                release(false, false, false);
            } else {
                stop(false, true);
            }
            // 3
            setScreenKeep(true);
            // 4
            checkKernelNull(startArgs, false);
            // 5
            checkRenderNull(startArgs, false);
            // 6
            attachRenderKernel();
            // 7
            initStartArgs(startArgs);
            // 8
            initDecoder();
        } catch (NetworkError e) {
            callEvent(PlayerType.EventType.ERROR_NETWORK);
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "start -> " + e.getMessage());
            }
        } catch (UrlEmptyError e) {
            callEvent(PlayerType.EventType.ERROR_URL_EMPTY);
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "start -> " + e.getMessage());
            }
        } catch (Exception e) {
            callEvent(PlayerType.EventType.ERROR_INIT);
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "start -> " + e.getMessage());
            }
        }
    }

    default long getDuration() {
        try {
            VideoKernelApi kernel = getVideoKernel();
            long duration = kernel.getDuration();
            if (duration < 0L) {
                duration = 0L;
            }
            return duration;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "getDuration -> " + e.getMessage());
            }
            return 0L;
        }
    }

    default long getPosition() {
        try {
            VideoKernelApi kernel = getVideoKernel();
            long position = kernel.getPosition();
            if (position < 0L) {
                position = 0L;
            }
            return position;
        } catch (Exception e) {
            return 0L;
        }
    }

    default float getVolume() {
        try {
            VideoKernelApi kernel = getVideoKernel();
            return kernel.getVolume();
        } catch (Exception e) {
            return 0f;
        }
    }

    default void setVolume(@FloatRange(from = 0f, to = 1f) float left, @FloatRange(from = 0f, to = 1f) float right) {

        callVolume(true, true, Math.min(left, right));

        try {
            VideoKernelApi kernel = getVideoKernel();
            kernel.setVolume(left, right);
        } catch (Exception e) {
        }
    }

    default void closeVolume() {

        callVolume(true, true, 0f);

        try {
            VideoKernelApi kernel = getVideoKernel();
            kernel.setVolume(0f, 0f);
        } catch (Exception e) {
        }
    }

    default void openVolume() {

        callVolume(true, true, 1f);

        try {
            VideoKernelApi kernel = getVideoKernel();
            kernel.setVolume(1f, 1f);
        } catch (Exception e) {
        }
    }

    default void toggle() {
        toggle(true);
    }

    default void toggle(boolean callEvent) {
        try {
            boolean playing = isPlaying();
            if (playing) {
                pause(callEvent);
            } else {
                resume(callEvent);
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "toggle -> " + e.getMessage());
            }
        }
    }

    default void resume() {
        resume(true);
    }

    default void resume(boolean callEvent) {
        setScreenKeep(true);
        resumeKernel(callEvent);
    }

    default void pause() {
        pause(true);
    }

    default void pause(boolean callEvent) {
        setScreenKeep(false);
        pauseKernel(callEvent);
    }

    default void stop() {
        stop(true, false);
    }

    default void stop(boolean callEvent, boolean fromInit) {
        setScreenKeep(false);
        stopKernel(callEvent, fromInit);
    }

    default void release() {
        release(true, true, true);
    }

    default void release(boolean callEvent, boolean isFromUser, boolean clearListener) {
        try {
            if (clearListener) {
                clearPlayerListener();
            }
            releaseRender();
            releaseKernel(isFromUser);
            if (!callEvent)
                throw new Exception("warning: callEvent false");
            callEvent(PlayerType.EventType.RELEASE);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "release -> " + e.getMessage());
            }
        }
    }

    default void restart() {
        try {
            StartArgs startArgs = getStartArgs();
            if (null == startArgs)
                throw new Exception("error: args null");
            boolean containsMainUrl = startArgs.containsMainUrl();
            if (!containsMainUrl)
                throw new Exception("error: containsMainUrl false");
            callEvent(PlayerType.EventType.RESTART);
            long playWhenReadySeekToPosition = startArgs.getPlayWhenReadySeekToPosition();
            if (playWhenReadySeekToPosition > 0) {
                StartArgs newArgs = startArgs.newBuilder()
                        .setPlayWhenReadySeekToPosition(0L)
                        .build();
                start(newArgs);
            } else {
                start(startArgs);
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "restart -> " + e.getMessage());
            }
        }
    }

    default void restartSeekToPosition() {
        try {
            StartArgs startArgs = getStartArgs();
            if (null == startArgs)
                throw new Exception("error: args null");
            boolean containsMainUrl = startArgs.containsMainUrl();
            if (!containsMainUrl)
                throw new Exception("error: containsMainUrl false");
            callEvent(PlayerType.EventType.RESTART);

            long position = 0L;
            boolean live = isLiveStream();
            if (!live) {
                position = getPosition();
            }
            StartArgs newArgs = startArgs.newBuilder()
                    .setPlayWhenReadySeekToPosition(position)
                    .build();
            start(newArgs);
            if (live) {
                seekToDefaultPosition();
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "restartSeekToPosition -> " + e.getMessage());
            }
        }
    }

    default long getTrySeeDuration() {
        try {
            VideoKernelApi kernel = getVideoKernel();
            return kernel.getTrySeeDuration();
        } catch (Exception e) {
            return 0L;
        }
    }

    default long getPlayWhenReadySeekToPosition() {
        try {
            VideoKernelApi kernel = getVideoKernel();
            return kernel.getPlayWhenReadySeekToPosition();
        } catch (Exception e) {
            return 0L;
        }
    }

    default void seekTo(long position) {
        try {
            if (position < 0) {
                position = 0;
            }
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            boolean prepared = isPrepared();
            if (!prepared)
                throw new Exception("warning: prepared false");
            kernel.seekTo(position);
            setScreenKeep(true);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "seekTo -> " + e.getMessage());
            }
        }
    }

    default void seekToDefaultPosition() {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            boolean prepared = isPrepared();
            if (!prepared)
                throw new Exception("warning: prepared false");
            kernel.seekToDefaultPosition();
            setScreenKeep(true);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "seekToDefaultPosition -> " + e.getMessage());
            }
        }
    }

    default void fastRewind(long step) {
        try {
            if (step < 0)
                return;
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            long position = kernel.getPosition();
            long nextPosition = position - step;
            if (nextPosition < 0L) {
                nextPosition = 0L;
            }
            kernel.seekTo(nextPosition);
            setScreenKeep(true);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "fastRewind -> " + e.getMessage());
            }
        }
    }

    default void fastForward(long step) {
        try {
            if (step < 0)
                return;
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            long position = kernel.getPosition();
            long duration = kernel.getDuration();
            long nextPosition = position + step;
            if (nextPosition > duration) {
                nextPosition = duration;
            }
            kernel.seekTo(nextPosition);
            setScreenKeep(true);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "fastForward -> " + e.getMessage());
            }
        }
    }

    default boolean isLiveStream() {
        try {
            VideoKernelApi kernel = getVideoKernel();
            return kernel.isLiveStream();
        } catch (Exception e) {
            return false;
        }
    }

    default boolean isUseCache() {
        try {
            VideoKernelApi kernel = getVideoKernel();
            return kernel.isUseCache();
        } catch (Exception e) {
            return false;
        }
    }

    default boolean isPlaying() {
        try {
            VideoKernelApi kernel = getVideoKernel();
            return kernel.isPlaying();
        } catch (Exception e) {
            return false;
        }
    }

    default boolean isPrepared() {
        try {
            VideoKernelApi kernel = getVideoKernel();
            return kernel.isPrepared();
        } catch (Exception e) {
            return false;
        }
    }

    default void setSpeed(float speed) {
        try {
            VideoKernelApi kernel = getVideoKernel();
            kernel.setSpeed(speed);
        } catch (Exception e) {
        }
    }

    default float getSpeed() {
        try {
            VideoKernelApi kernel = getVideoKernel();
            return kernel.getSpeed();
        } catch (Exception e) {
            return 1.0f;
        }
    }

    /*********************/

    default void initStartArgs(StartArgs args) {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            kernel.setStartArgs(args);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "initStartArgs -> " + e.getMessage());
            }
        }
    }

    default void initDecoder() {
        try {
            StartArgs startArgs = getStartArgs();
            if (null == startArgs)
                throw new Exception("error: startArgs null");
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            Context context = getBaseContext();
            kernel.initHandler();
            kernel.checkDecoder(context, startArgs);
            kernel.initDecoder(context, startArgs);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "initDecoder -> " + e.getMessage());
            }
        }
    }

    default void releaseKernel(boolean isFromUser) {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            // 埋点
            onBuriedRelease();
            //
            kernel.releaseDecoder(isFromUser);
            setVideoKernel(null);
            setScreenKeep(false);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "releaseKernel -> " + e.getMessage());
            }
        }
    }

    default void stopKernel(boolean callEvent, boolean fromInit) {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            // 埋点
            boolean prepared = isPrepared();
            if (prepared) {
                onBuriedStop(fromInit);
            }
            kernel.stop();
            if (!callEvent)
                throw new Exception("warning: callEvent false");
            callEvent(PlayerType.EventType.STOP);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "stopKernel -> " + e.getMessage());
            }
        }
    }

    default void pauseKernel(boolean callEvent) {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            boolean prepared = isPrepared();
            if (!prepared)
                throw new Exception("warning: prepared false");
            boolean playing = isPlaying();
            if (!playing)
                throw new Exception("warning: playing false");
            // 埋点
            onBuriedPause();
            // 执行
            kernel.pause();
            if (!callEvent)
                throw new Exception("warning: callEvent false");
            callEvent(PlayerType.EventType.PAUSE);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "pauseKernel -> " + e.getMessage());
            }
        }
    }

    default void resumeKernel(boolean callEvent) {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            boolean prepared = isPrepared();
            if (!prepared)
                throw new Exception("warning: prepared false");
            boolean playing = isPlaying();
            if (playing)
                throw new Exception("warning: playing true");
            // 埋点
            onBuriedResume();
            // 执行
            kernel.resume();
            setScreenKeep(true);
            if (!callEvent)
                throw new Exception("warning: callEvent false");
            callEvent(PlayerType.EventType.RESUME);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "resumeKernel -> " + e.getMessage());
            }
        }
    }

    /***************************/

    default void checkKernelNull(StartArgs args, boolean release) {
        try {
            if (release) {
                releaseKernel(false);
            }
            if (null != getVideoKernel())
                throw new Exception("warning: getVideoKernel not null");
            //
            int kernelType = args.getKernelType();
            //
            VideoKernelApi kernelApi = VideoKernelFactoryManager.getKernel(kernelType);
            setVideoKernel(kernelApi);
            //
            kernelApi.setPlayerApi((VideoPlayerApi) this);
            kernelApi.setKernelApi(new VideoKernelApiEvent() {

                @Override
                public void onUpdateProgress(long trySeeDuration, long position, long duration) {
                    try {
                        callProgress(trySeeDuration, position, duration);
                        if (trySeeDuration <= 0L)
                            throw new Exception("waning: trySeeDuration<=0L");
                        if (position < 0L)
                            throw new Exception("waning: position<0L");
                        if (position < trySeeDuration)
                            throw new Exception("waning: position<trySeeDuration");

                        // 埋点
                        onBuriedTrySeeEnd();

                        // 试看结束
                        stop();
                        callEvent(PlayerType.EventType.TRY_SEE_END);
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onUpdateSubtitle(int kernel, CharSequence result) {
//                    VideoKernelApiEvent.super.onUpdateSubtitle(kernel, language, result);
                    callSubtitle(kernel, result);
                }

                @Override
                public void onUpdateNetSpeed(int kernel) {
                    try {
                        boolean showSpeed = args.isShowSpeed();
                        if (!showSpeed)
                            throw new Exception("warning: showSpeed false");
                        String speed = SpeedUtil.getNetSpeed(getBaseContext());
                        if (speed.isEmpty())
                            throw new Exception("warning: speed isEmpty");
                        callNetSpeed(kernel, speed);
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onEvent(@PlayerType.KernelType.Value int kernel, @PlayerType.EventType.Value int playState) {

                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "onEvent = " + kernel + ", playState = " + playState);
                    }

                    // 透传
                    callEvent(playState);

                    // 播放错误
                    boolean error = PlayStateUtil.isError(playState);
                    if (error) {
                        // 埋点
                        onBuriedError(playState);
                        // 执行
                        setScreenKeep(false);
                        return;
                    }

                    switch (playState) {
                        //
                        case PlayerType.EventType.INIT:
                            //
                            boolean showSpeed = args.isShowSpeed();
                            if (showSpeed) {
                                kernelApi.sendMessageSpeedUpdate(kernel, false);
                            }
                            //
                            StartArgs.TimeoutConfiguration timeoutConfiguration = args.getTimeoutConfiguration();
                            int connectTimeout = timeoutConfiguration.getConnectTimeoutMs();
                            @PlayerType.KernelType.Value
                            int kernelType = args.getKernelType();
                            long timeMillis = System.currentTimeMillis();
                            kernelApi.sendMessageConnectTimeout(kernelType, timeMillis, connectTimeout, false);
                            break;
                        // 缓冲开始
                        case PlayerType.EventType.BUFFERING_START:
                            // 埋点
                            onBuriedBufferingStart();
                            // 检测：缓冲超时
                            StartArgs.BufferingConfiguration bufferingConfiguration = args.getBufferingConfiguration();
                            if (null != bufferingConfiguration) {
                                long maxBufferingTimeoutMs = bufferingConfiguration.getMaxBufferingTimeoutMs();
                                if (maxBufferingTimeoutMs > 0L) {
                                    kernelApi.startMessageBufferingTimeout(kernel, maxBufferingTimeoutMs);
                                }
                            }
                            break;
                        // 缓冲结束
                        case PlayerType.EventType.BUFFERING_STOP:
                            // 埋点
                            onBuriedBufferingStop();
                            //
                            kernelApi.closeMessagesBufferingTimeout();
                            break;
                        // 视频首帧
                        case PlayerType.EventType.VIDEO_RENDERING_START:
                            // 埋点
                            onBuriedVideoRenderingStart();
                            //
                            kernelApi.removeMessagesSpeedUpdate();
                            //
                            kernelApi.removeMessagesConnectTimeout();
                            //
                            kernelApi.sendMessageProgressUpdate(kernel, false);
                            break;
                        // 播放开始-默认
                        case PlayerType.EventType.START:
                            // 埋点
                            onBuriedStart();
                            // ijk需要刷新RenderView
                            initRenderView();
//                          // 检查View是否可见
                            checkVideoVisibility();
                            break;
                        // 快进
                        case PlayerType.EventType.SEEK_START_FORWARD:
                            // 埋点
                            onBuriedSeekStartForward();
                            break;
                        // 快退
                        case PlayerType.EventType.SEEK_START_REWIND:
                            // 埋点
                            onBuriedSeekStartRewind();
                            break;
                        // 快进
                        case PlayerType.EventType.SEEK_FINISH:
                            // 埋点
                            onBuriedSeekFinish();
                            break;
                        //
                        case PlayerType.EventType.PAUSE:
                            // 停止轮训
                            kernelApi.removeMessagesProgressUpdate();
                            break;
                        //
                        case PlayerType.EventType.RESUME:
                            // 停止轮训
                            kernelApi.sendMessageProgressUpdate(kernel, false);
                            break;
                        // 播放结束
                        case PlayerType.EventType.END:
                            // 埋点
                            onBuriedComplete();
                            // 关闭屏幕常亮
                            setScreenKeep(false);
                            //
                            boolean looping = args.isLooping();
                            if (looping) {
                                restart();
                            }
//                            // 多剧集
//                            int episodeItemCount = args.getEpisodeItemCount();
//                            OnPlayerEpisodeListener onPlayerEpisodeListener = getOnPlayerEpisodeListener();
//                            if (episodeItemCount > 0 && null != onPlayerEpisodeListener) {
//                                int episodePlayingIndex = args.getEpisodePlayingIndex();
//                                int nextPlayIndex = episodePlayingIndex + 1;
//                                if (nextPlayIndex >= episodeItemCount) {
//                                    onPlayerEpisodeListener.onEnd();
//                                } else {
//                                    onPlayerEpisodeListener.onEpisode(nextPlayIndex);
//                                }
//                            }
//                            // 单剧集
//                            else {
//                                boolean looping = args.isLooping();
//                                if (looping) {
//                                    restart();
//                                }
//                            }

                            break;
                    }
                }

                @Override
                public void onVideoFormatChanged(int kernel, int rotation, int scaleType, int width, int height, int bitrate) {

                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "setKernelEvent -> onVideoFormatChanged -> kernel = " + kernel + ", width = " + width + ", height = " + height + ", rotation = " + ", scaleType = " + scaleType);
                    }

                    setVideoFormat(kernel, rotation, scaleType, width, height, bitrate);
                }
            });
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "checkKernelNull -> " + e.getMessage());
            }
        }
    }

    default boolean toggleTrack(TrackInfo trackInfo) {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            return kernel.toggleTrack(trackInfo);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "toggleTrack -> " + e.getMessage());
            }
            return false;
        }
    }

    default List<TrackInfo> getTrackInfoAll() {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            return kernel.getTrackInfoAll();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "getTrackInfoAll -> " + e.getMessage());
            }
            return null;
        }
    }

    default List<TrackInfo> getTrackInfoVideo() {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            List<TrackInfo> trackInfoVideo = kernel.getTrackInfoVideo();
            if (null == trackInfoVideo)
                throw new Exception("warning: trackInfoVideo null");

            StartArgs startArgs = getStartArgs();
            if (null != startArgs) {
                Proxy proxy = startArgs.getProxy();
                if (null != proxy) {
                    ProxyTrack proxyTrack = proxy.getProxyTrack();
                    if (null != proxyTrack) {
                        proxyTrack.formatVideoTrackInfo(trackInfoVideo, startArgs);
                    }
                }
            }

            return trackInfoVideo;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "getTrackInfoVideo -> " + e.getMessage());
            }
            return null;
        }
    }

    default List<TrackInfo> getTrackInfoAudio() {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            List<TrackInfo> trackInfoAudio = kernel.getTrackInfoAudio();
            if (null == trackInfoAudio)
                throw new Exception("warning: trackInfoAudio null");

            StartArgs startArgs = getStartArgs();
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "getTrackInfoAudio -> startArgs = " + startArgs);
            }
            if (null != startArgs) {
                Proxy proxy = startArgs.getProxy();
                if (null != proxy) {
                    ProxyTrack proxyTrack = proxy.getProxyTrack();
                    if (null != proxyTrack) {
                        proxyTrack.formatAudioTrackInfo(trackInfoAudio, startArgs);
                    }
                }
            }

            return trackInfoAudio;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "getTrackInfoAudio -> " + e.getMessage());
            }
            return null;
        }
    }

    default List<TrackInfo> getTrackInfoSubtitle() {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            List<TrackInfo> trackInfoSubtitle = kernel.getTrackInfoSubtitle();
            if (null == trackInfoSubtitle)
                throw new Exception("warning: trackInfoSubtitle null");

            StartArgs startArgs = getStartArgs();
            if (null != startArgs) {
                Proxy proxy = startArgs.getProxy();
                if (null != proxy) {
                    ProxyTrack proxyTrack = proxy.getProxyTrack();
                    if (null != proxyTrack) {
                        proxyTrack.formatSubtitleTrackInfo(trackInfoSubtitle, startArgs);
                    }
                }
            }

            return trackInfoSubtitle;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "getTrackInfoSubtitle -> " + e.getMessage());
            }
            return null;
        }
    }

    default HlsSpanList getSegments() {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            return kernel.getSegments();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "getSegments -> " + e.getMessage());
            }
            return null;
        }
    }

    default long[] getSegmentsMs() {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            return kernel.getSegmentsMs();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "getSegments -> " + e.getMessage());
            }
            return null;
        }
    }
}
