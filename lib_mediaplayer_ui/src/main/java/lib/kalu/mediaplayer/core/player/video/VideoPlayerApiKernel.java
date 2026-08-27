package lib.kalu.mediaplayer.core.player.video;

import android.content.Context;

import androidx.annotation.FloatRange;

import java.util.List;

import lib.kalu.mediaplayer.bean.args.StartArgs;
import lib.kalu.mediaplayer.bean.args.UrlArgs;
import lib.kalu.mediaplayer.bean.configuration.RetryConfiguration;
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

public interface VideoPlayerApiKernel extends VideoPlayerApiListener,
        VideoPlayerApiComponent,
        VideoPlayerApiRender,
        VideoPlayerApiDevice,
        VideoPlayerApiCall, VideoPlayerApiBase {

    String TAG = "VideoPlayerApiKernel22";

    default boolean isDoWindowing() {
        try {
            VideoKernelApi videoKernel = getVideoKernel();
            if (null == videoKernel) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "isDoWindowing -> error: videoKernel null");
                }
                return false;
            }
            return videoKernel.isDoWindowing();
        } catch (Exception e) {
            return false;
        }
    }

    default void setDoWindowing(boolean v) {
        try {
            VideoKernelApi videoKernel = getVideoKernel();
            if (null == videoKernel) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "setDoWindowing -> error: videoKernel null");
                }
                return;
            }
            videoKernel.setDoWindowing(v);
        } catch (Exception e) {
        }
    }

    @Override
    default void start(StartArgs startArgs) {
        try {

            // fixbug
            boolean prepared = isPrepared();
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "start -> prepared = " + prepared);
            }
            if (prepared) {
                stop(false);
                release(false, false);
            }

            // 初始化kernel
            initKernel(startArgs);
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "start -> initKernel, videoKernel = " + getVideoKernel());
            }

            // 初始化Render
            initRender(startArgs);
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "start -> initRender, videoRender = " + getVideoRender());
            }

            // 关联 kernel & Render
            attachRenderKernel();
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "start -> attachRenderKernel");
            }

            // 初始化参数
            initStartArgs(startArgs);
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "start -> initStartArgs, startArgs = " + getStartArgs());
            }

            //
            setScreenKeep(true);
            callEvent(PlayerType.EventType.INIT);

            //
            Context context = getBaseContext();
            boolean connected = NetworkUtil.isConnected(context);
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "start -> connected = " + connected);
            }

            if (!connected)
                throw new NetworkError();
            boolean containsVideoUrl = startArgs.containsVideoUrl();
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "start -> containsVideoUrl = " + containsVideoUrl);
            }

            if (!containsVideoUrl)
                throw new UrlEmptyError("error: containsVideoUrl false");

            // 8
            initDecoder();
        } catch (NetworkError e) {
            callEvent(PlayerType.EventType.ERROR_NETWORK);
            stop(false);
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "start -> " + e.getMessage());
            }
        } catch (UrlEmptyError e) {
            callEvent(PlayerType.EventType.ERROR_URL_EMPTY);
            stop(false);
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "start -> " + e.getMessage());
            }
        } catch (Exception e) {
            callEvent(PlayerType.EventType.ERROR_INIT);
            stop(false);
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

    default void resume(boolean callEvent) {
        setScreenKeep(true);
        resumeKernel(callEvent);
    }


    default void pause(boolean callEvent) {
        setScreenKeep(false);
        pauseKernel(callEvent);
    }

    default void stop(boolean callEvent) {
        setScreenKeep(false);
        stopKernel(callEvent);
    }


    default void release(boolean callEvent, boolean clearListener) {
        try {
            if (clearListener) {
                clearPlayerListener();
            }
            if (callEvent) {
                callEvent(PlayerType.EventType.RELEASE);
            }
            releaseKernel();
            releaseRender();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "release -> " + e.getMessage());
            }
        }
    }

    default void restart() {
        try {
            StartArgs startArgs = getStartArgs();
            if (null == startArgs) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "restart -> error: args null");
                }
                return;
            }
            boolean containsVideoUrl = startArgs.containsVideoUrl();
            if (!containsVideoUrl) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "restart -> error: containsVideoUrl false");
                }
                return;
            }
            StartArgs newArgs = startArgs.newBuilderSelf()
                    .setPlayWhenReadySeekToPosition(0L)
                    .build();
            start(newArgs);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "restart -> " + e.getMessage());
            }
        }
    }

    default void restartSeekToPosition() {
        try {
            StartArgs startArgs = getStartArgs();
            if (null == startArgs) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "restart -> error: args null");
                }
                return;
            }
            boolean containsVideoUrl = startArgs.containsVideoUrl();
            if (!containsVideoUrl) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "restart -> error: containsVideoUrl false");
                }
                return;
            }

            long position = 0L;
            boolean live = isLiveStream();
            if (!live) {
                position = getPosition();
            }
            StartArgs newArgs = startArgs.newBuilderSelf()
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
            if (null == kernel) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "seekTo -> warning: kernel null");
                }
                return;
            }
            boolean prepared = isPrepared();
            if (!prepared) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "seekTo -> warning: prepared false");
                }
                return;
            }
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
            if (null == kernel) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "seekTo -> warning: kernel null");
                }
                return;
            }
            boolean prepared = isPrepared();
            if (!prepared) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "seekTo -> warning: prepared false");
                }
                return;
            }
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
            if (null == kernel) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "fastRewind -> warning: kernel null");
                }
                return;
            }
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
            if (null == kernel) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "fastForward -> warning: kernel null");
                }
                return;
            }
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
            VideoKernelApi videoKernel = getVideoKernel();
            if (null == videoKernel) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "isLiveStream -> error: videoKernel null");
                }
                return false;
            }
            return videoKernel.isLiveStream();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "isLiveStream -> Exception: " + e.getMessage());
            }
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
            if (null == kernel) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "initStartArgs -> warning: kernel null");
                }
                return;
            }
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
            if (null == startArgs) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "initDecoder -> error: startArgs null");
                }
                return;
            }
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "initDecoder -> warning: kernel null");
                }
                return;
            }
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

    default void releaseKernel() {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "releaseKernel -> warning: kernel null");
                }
                return;
            }
            // 埋点
            onBuriedRelease();
            //
            kernel.removeAllMessages();
            kernel.releaseDecoder();
            setVideoKernel(null);
            setScreenKeep(false);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "releaseKernel -> " + e.getMessage());
            }
        }
    }

    default void stopKernel(boolean callEvent) {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "stopKernel -> warning: kernel null");
                }
                return;
            }
            // 埋点
            boolean prepared = isPrepared();
            if (prepared) {
                onBuriedStop();
            }
            if (callEvent) {
                callEvent(PlayerType.EventType.STOP);
            }
            kernel.removeAllMessages();
            kernel.stop();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "stopKernel -> " + e.getMessage());
            }
        }
    }

    default void pauseKernel(boolean callEvent) {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "pauseKernel -> warning: kernel null");
                }
                return;
            }
            boolean prepared = isPrepared();
            if (!prepared) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "pauseKernel -> warning: prepared false");
                }
                return;
            }
            boolean playing = isPlaying();
            if (!playing) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "pauseKernel -> warning: playing false");
                }
                return;
            }
            // 埋点
            onBuriedPause();
            // 执行
            kernel.pause();
            if (callEvent) {
                callEvent(PlayerType.EventType.PAUSE);
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "pauseKernel -> " + e.getMessage());
            }
        }
    }

    default void resumeKernel(boolean callEvent) {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "resumeKernel -> warning: kernel null");
                }
                return;
            }
            boolean prepared = isPrepared();
            if (!prepared) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "resumeKernel -> warning: prepared false");
                }
                return;
            }
            boolean playing = isPlaying();
            if (playing) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "resumeKernel -> warning: playing true");
                }
                return;
            }
            // 埋点
            onBuriedResume();
            // 执行
            kernel.resume();
            setScreenKeep(true);
            if (callEvent) {
                callEvent(PlayerType.EventType.RESUME);
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "resumeKernel -> " + e.getMessage());
            }
        }
    }

    /***************************/

    default void initKernel(StartArgs startArgs) {
        try {

            if (null != getVideoKernel()) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "initKernel -> warning: getVideoKernel not null");
                }
                return;
            }

            //
            int kernelType = startArgs.getKernelType();
            int connectTimeoutMs = startArgs.getTimeoutConfiguration().getConnectTimeoutMs();
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "initKernel -> kernelType = " + kernelType + ", connectTimeoutMs = " + connectTimeoutMs);
            }

            //
            VideoKernelApi kernelApi = VideoKernelFactoryManager.getKernel(kernelType);
            setVideoKernel(kernelApi);
            //
            kernelApi.setPlayerApi((VideoPlayerApi) this);
            kernelApi.setKernelApi(new VideoKernelApiEvent() {

                @Override
                public void onUpdateProgress(long trySeeDuration, long position, long duration) {

//                    if (LogUtil.DEBUG) {
//                        LogUtil.log(TAG, "initKernel -> onUpdateProgress, trySeeDuration = " + trySeeDuration + ", position = " + position + ", duration = " + duration);
//                    }

                    callProgress(trySeeDuration, position, duration);

                    try {
                        if (trySeeDuration <= 0L) {
                            return;
                        }
                        if (position < 0L) {
                            return;
                        }
                        if (position < trySeeDuration) {
                            return;
                        }

                        // 埋点
                        onBuriedTrySeeEnd();

                        // 试看结束
                        callEvent(PlayerType.EventType.TRY_SEE_END);
                        stop(true);
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onUpdateSubtitle(int kernel, CharSequence result) {
//                    VideoKernelApiEvent.super.onUpdateSubtitle(kernel, language, result);
                    callSubtitle(kernel, result);
                }

                @Override
                public void onUpdateBandwidth(int kernel, long totalLoadTimeMs, long netKBps, long curKBps) {
                    callBandwidth(kernel, totalLoadTimeMs, netKBps, curKBps);
                }

                @Override
                public void onUpdateStuckNet(int kernel, long videoBitrate, long netBitrate) {
                    callStuckNet(kernel, videoBitrate, netBitrate);
                }

                @Override
                public void onEvent(@PlayerType.KernelType.Value int kernel, @PlayerType.EventType.Value int playState) {

                    boolean errorNeedRetry = PlayStateUtil.isErrorNeedRetry(playState);
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "initKernel -> onEvent -> kernel = " + kernel + ", playState = " + playState + ", errorNeedRetry = " + errorNeedRetry);
                    }

                    // 播放错误, 检查重试策略
                    if (errorNeedRetry) {
                        StartArgs retryStartArgs = nextRetryStartArgs(kernel, playState);
                        if (null == retryStartArgs) {
                            nextEvent(kernel, playState);
                        } else {
                            callEvent(PlayerType.EventType.ERROR_RELOAD_RETRY);
                            start(retryStartArgs);
                        }
                    } else {
                        nextEvent(kernel, playState);
                    }
                }

                @Override
                public void onVideoFormatChanged(int kernel, int rotation, int scaleType, int width, int height, int bitrate) {

                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "initKernel -> onVideoFormatChanged -> kernel = " + kernel + ", width = " + width + ", height = " + height + ", rotation = " + ", scaleType = " + scaleType);
                    }

                    setVideoFormat(kernel, rotation, scaleType, width, height, bitrate);
                }
            });
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "initKernel -> " + e.getMessage());
            }
        }
    }

    default boolean toggleTrack(TrackInfo trackInfo) {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "toggleTrack -> warning: kernel null");
                }
                return false;
            }
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
            if (null == kernel) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "getTrackInfoAll -> warning: kernel null");
                }
                return null;
            }
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
            if (null == kernel) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "getTrackInfoVideo -> warning: kernel null");
                }
                return null;
            }
            List<TrackInfo> trackInfoVideo = kernel.getTrackInfoVideo();
            if (null == trackInfoVideo) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "getTrackInfoVideo -> warning: trackInfoVideo null");
                }
                return null;
            }

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
            if (null == kernel) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "getTrackInfoAudio -> warning: kernel null");
                }
                return null;
            }
            List<TrackInfo> trackInfoAudio = kernel.getTrackInfoAudio();
            if (null == trackInfoAudio) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "getTrackInfoAudio -> warning: trackInfoAudio null");
                }
                return null;
            }

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
            if (null == kernel) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "getTrackInfoSubtitle -> warning: kernel null");
                }
                return null;
            }
            List<TrackInfo> trackInfoSubtitle = kernel.getTrackInfoSubtitle();
            if (null == trackInfoSubtitle) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "getTrackInfoSubtitle -> warning: trackInfoSubtitle null");
                }
                return null;
            }

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
            if (null == kernel) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "getSegments -> warning: kernel null");
                }
                return null;
            }
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
            if (null == kernel) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "getSegmentsMs -> warning: kernel null");
                }
                return null;
            }
            return kernel.getSegmentsMs();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "getSegmentsMs -> " + e.getMessage());
            }
            return null;
        }
    }

    default void nextEvent(@PlayerType.KernelType.Value int kernelType, @PlayerType.EventType.Value int playState) {

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "nextEvent -> kernelType = " + kernelType + ", playState = " + playState);
        }

        VideoKernelApi videoKernel = getVideoKernel();
        if (null == videoKernel)
            return;

        // 透传
        callEvent(playState);


        // 播放错误
        if (PlayStateUtil.isError(playState)) {
            // 埋点
            onBuriedError(playState);
            // 执行
            setScreenKeep(false);
        }
        // // 检测：启播超时      case PlayerType.EventType.INIT:
        // 初始化准备
        else if (playState == PlayerType.EventType.READY) {
            if (LogUtil.DEBUG) {
                String url = getStartArgs().getUrl();
                LogUtil.log(TAG, "nextEvent,  PlayerType.EventType.READY, url = " + url);
            }

            // TODO: 2026/8/14
//                                    boolean showSpeed = args.isShowSpeed();
//                                    if (showSpeed) {
//                                        kernelApi.sendMessageSpeedUpdate(kernel, false);
//                                    }
            long timeMillis = System.currentTimeMillis();
            int connectTimeoutMs = getStartArgs().getTimeoutConfiguration().getConnectTimeoutMs();
            videoKernel.removeAllMessages();
            videoKernel.sendMessageConnectTimeout(kernelType, timeMillis, connectTimeoutMs, false);
        }
        // 轮训：视频进度条
        else if (playState == PlayerType.EventType.MEDIA_INFO_PREPARE) {
            videoKernel.removeAllMessages();
            videoKernel.sendMessageProgressUpdate(kernelType, false);
        }
        // 视频：首帧画面
        else if (playState == PlayerType.EventType.MEDIA_INFO_VIDEO_RENDERING_START) {
            onBuriedVideoRenderingStart();
        }
        // 缓冲开始
        else if (playState == PlayerType.EventType.MEDIA_INFO_BUFFERING_START) {
            int connectTimeoutMs = getStartArgs().getTimeoutConfiguration().getConnectTimeoutMs();
            onBuriedBufferingStart();
            videoKernel.startMessageBufferingTimeout(kernelType, connectTimeoutMs);
        }
        // 缓冲结束
        else if (playState == PlayerType.EventType.MEDIA_INFO_BUFFERING_STOP) {
            onBuriedBufferingStop();
            videoKernel.closeMessagesBufferingTimeout();
        }
        // 播放开始-默认
        else if (playState == PlayerType.EventType.START) {
            // 埋点
            onBuriedStart();
            // ijk需要刷新RenderView
            initRenderView();
            // 检查View是否可见
            checkVideoVisibility();
        }
        // 快进
        else if (playState == PlayerType.EventType.MEDIA_INFO_UPDATE_SEEK_START_FORWARD) {
            onBuriedSeekStartForward();
        }
        // 快退
        else if (playState == PlayerType.EventType.MEDIA_INFO_UPDATE_SEEK_START_REWIND) {
            onBuriedSeekStartRewind();
        }
        // 快进 & 快退 OK
        else if (playState == PlayerType.EventType.MEDIA_INFO_UPDATE_SEEK_FINISH) {
            onBuriedSeekFinish();
        }
        // 暂停
        else if (playState == PlayerType.EventType.PAUSE) {
            videoKernel.removeMessagesProgressUpdate();
        }
        // 恢复播放
        else if (playState == PlayerType.EventType.RESUME) {
            videoKernel.sendMessageProgressUpdate(kernelType, false);
        }
        // 播放结束
        else if (playState == PlayerType.EventType.END) {
            // 埋点
            onBuriedComplete();
            // 关闭屏幕常亮
            setScreenKeep(false);
            //
            boolean looping = getStartArgs().isLooping();
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
        }
    }

    default StartArgs nextRetryStartArgs(@PlayerType.KernelType.Value int kernelType, @PlayerType.EventType.Value int playState) {

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "nextRetryStartArgs -> kernelType = " + kernelType + ", playState = " + playState);
        }

        try {

            VideoKernelApi videoKernel = getVideoKernel();
            if (null == videoKernel)
                return null;

            // 暂停外部轮训消息
            videoKernel.removeAllMessages();

            String oldUrl = getStartArgs().getUrl();
            RetryConfiguration oldRetryConfiguration = getStartArgs().getRetryConfiguration();
            List<RetryConfiguration.RetryUrl> retryUrls = oldRetryConfiguration.getRetryUrls();

            // todo 2026-08-27 当前重试多路流 仅针对 androidx media exoplayer
            if (kernelType != PlayerType.KernelType.MEDIA_V3 && retryUrls.isEmpty())
                return null;

            int retryUrlsCount = retryUrls.size();
            int retryIndex = retryUrlsCount - 1;
            for (int i = 0; i < retryUrlsCount; i++) {
                RetryConfiguration.RetryUrl retryUrl = retryUrls.get(i);
                String url = retryUrl.getUrl();
                if (null == url)
                    continue;
                if (url.isEmpty())
                    continue;
                if (!url.equals(oldUrl))
                    continue;
                retryIndex = i;
                break;
            }

            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "nextRetryStartArgs, RetryConfiguration, retryIndex = " + retryIndex + ", retryUrlsCount = " + retryUrlsCount + ", oldUrl = " + oldUrl + ", retryUrls = " + retryUrls);
            }

            if (retryIndex + 1 >= retryUrlsCount)
                return null;

            Proxy nextRetryProxy = getStartArgs().getRetryConfiguration().getRetryUrls().get(retryIndex).getProxy();
            String nextRetryUrl = getStartArgs().getRetryConfiguration().getRetryUrls().get(retryIndex).getUrl();
            UrlArgs newRetryUrlArgs = getStartArgs().getUrlArgs().newBuilderSelf().setUrl(nextRetryUrl).build();
            return getStartArgs().newBuilderSelf()
                    .setUrl(newRetryUrlArgs)
                    .setProxy(nextRetryProxy)
                    .build();
        } catch (Exception e) {
            return null;
        }
    }
}
