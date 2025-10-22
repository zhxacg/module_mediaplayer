package lib.kalu.mediaplayer.core.kernel.video.mediax;

import android.content.Context;
import android.net.Uri;
import android.os.Looper;
import android.view.Surface;

import androidx.annotation.Nullable;
import androidx.media3.common.C;
import androidx.media3.common.Format;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaLibraryInfo;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.PlaybackParameters;
import androidx.media3.common.Player;
import androidx.media3.common.TrackGroup;
import androidx.media3.common.TrackSelectionOverride;
import androidx.media3.common.TrackSelectionParameters;
import androidx.media3.common.Tracks;
import androidx.media3.common.VideoSize;
import androidx.media3.common.text.Cue;
import androidx.media3.common.text.CueGroup;
import androidx.media3.common.util.Clock;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.database.StandaloneDatabaseProvider;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DataSpec;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.datasource.FileDataSource;
import androidx.media3.datasource.cache.CacheDataSink;
import androidx.media3.datasource.cache.CacheDataSource;
import androidx.media3.datasource.cache.CacheKeyFactory;
import androidx.media3.datasource.cache.CacheSpan;
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor;
import androidx.media3.datasource.cache.SimpleCache;
import androidx.media3.exoplayer.DecoderReuseEvaluation;
import androidx.media3.exoplayer.DefaultLoadControl;
import androidx.media3.exoplayer.DefaultRenderersFactory;
import androidx.media3.exoplayer.ExoPlaybackException;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.Renderer;
import androidx.media3.exoplayer.RenderersFactory;
import androidx.media3.exoplayer.analytics.AnalyticsListener;
import androidx.media3.exoplayer.analytics.DefaultAnalyticsCollector;
import androidx.media3.exoplayer.hls.HlsManifest;
import androidx.media3.exoplayer.hls.playlist.HlsMediaPlaylist;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.exoplayer.source.LoadEventInfo;
import androidx.media3.exoplayer.source.MediaLoadData;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.MergingMediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.exoplayer.source.SingleSampleMediaSource;
import androidx.media3.exoplayer.text.TextOutput;
import androidx.media3.exoplayer.text.TextRenderer;
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.exoplayer.trackselection.TrackSelector;
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter;

import com.google.common.collect.ImmutableList;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableSet;

import lib.kalu.mediaplayer.PlayerSDK;
import lib.kalu.mediaplayer.bean.args.StartArgs;
import lib.kalu.mediaplayer.bean.cache.Cache;
import lib.kalu.mediaplayer.bean.info.HlsSpanInfo;
import lib.kalu.mediaplayer.bean.info.TrackInfo;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.VideoBasePlayer;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediax.subtitle.OffsetMsTextRenderer;

@UnstableApi
public final class VideoMediaxPlayer extends VideoBasePlayer {

    private boolean isVideoSizeChanged = false;
    private boolean isPrepared = false;
    private boolean isBuffering = false;
    private boolean mPlayWhenReadySeeking = false;
    private boolean mSeeking = false;

    private HlsManifest mHlsManifest;
    private SimpleCache mSimpleCache;
    private ExoPlayer mExoPlayer;

    @Override
    public ExoPlayer getPlayer() {
        return mExoPlayer;
    }

    @Override
    public void releaseDecoder(boolean isFromUser) {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            if (isFromUser) {
                setEvent(null);
            }
            clear();
            unRegistListener();
            release();
            LogUtil.log("VideoMediaxPlayer => releaseDecoder => completed");
        } catch (Exception e) {
            LogUtil.log("VideoMediaxPlayer => releaseDecoder => " + e.getMessage());
        }
    }

    @Override
    public void createDecoder(Context context, StartArgs args) {
        try {
            if (null != mExoPlayer)
                throw new Exception("warning: null != mExoPlayer");
            if (null == args)
                throw new Exception("error: args null");
            LogUtil.log("VideoMediaxPlayer => createDecoder =>");
            ExoPlayer.Builder builder = new ExoPlayer.Builder(context)
                    // 播放器调试和诊断相关的配置项
                    .setUsePlatformDiagnostics(false)
                    // 创建渲染器工厂
                    .setRenderersFactory(new DefaultRenderersFactory(context) {
                        @Override
                        protected void buildTextRenderers(Context context, TextOutput textOutput, Looper looper, int i, ArrayList<Renderer> arrayList) {
//                            super.buildTextRenderers(context, textOutput, looper, i, arrayList);
//                            ((TextRenderer) Iterables.getLast(arrayList)).experimentalSetLegacyDecodingEnabled(true);
                            TextRenderer textRenderer = new TextRenderer(textOutput, looper);
                            textRenderer.experimentalSetLegacyDecodingEnabled(true);
                            arrayList.add(textRenderer);
                        }
                    })
                    .setMediaSourceFactory(new DefaultMediaSourceFactory(context)
                            .experimentalParseSubtitlesDuringExtraction(true))
                    // 监听
                    .setAnalyticsCollector(new DefaultAnalyticsCollector(Clock.DEFAULT))
                    // 配置带宽测量器
                    .setBandwidthMeter(new DefaultBandwidthMeter.Builder(context)
                            // 初始带宽估算为5Mbps（5,000,000 bps）
                            .setInitialBitrateEstimate(5_000_000)
                            .build())
                    // 缓冲缓存
                    .setLoadControl(new DefaultLoadControl.Builder()
                            // minBufferMs 最小缓冲时长的参数，单位为毫秒
                            // maxBufferMs 限制最大缓冲时长的参数，单位是毫秒
                            // bufferForPlaybackMs 如果设置 bufferForPlaybackMs 为 5000，那么播放器会在开始播放前先缓冲 5 秒钟的媒体数据
                            // bufferForPlaybackAfterRebufferMs 用于指定在播放过程中出现重新缓冲（Rebuffer）后，为了保证后续播放流畅，需要再次缓冲的时长，单位同样是毫秒
                            .setBufferDurationsMs(10_0000, 10_0000, 5000, 5000)
                            .build())
                    // 自适应码率
                    .setTrackSelector(new DefaultTrackSelector(context, DefaultTrackSelector.Parameters.getDefaults(context)
                            .buildUpon()
//                            // 主字幕轨道
//                            .setPreferredTextRoleFlags(C.ROLE_FLAG_MAIN)
//                            // 主音频轨道
//                            .setPreferredAudioRoleFlags(C.ROLE_FLAG_MAIN)
//                            // 主视频轨道
//                            .setPreferredVideoRoleFlags(C.ROLE_FLAG_MAIN)
//                            // 音频禁止混合 MIME 类型切换（如视频+音频单独切换）
//                            .setAllowAudioMixedMimeTypeAdaptiveness(false)
//                            // 视频禁止混合 MIME 类型切换（如视频+音频单独切换）
//                            .setAllowVideoMixedMimeTypeAdaptiveness(true)
//                            // 音频禁止非无缝切换
//                            .setAllowAudioNonSeamlessAdaptiveness(false)
//                            // 视频禁止非无缝切换
//                            .setAllowVideoNonSeamlessAdaptiveness(false)
//                            // 音频混合声道数量的自适应性
//                            .setAllowAudioMixedChannelCountAdaptiveness(true)
//                            // 音频混合采样率自适应
//                            .setAllowAudioMixedSampleRateAdaptiveness(true)
//                            // 音频混合时解码器支持自适应
//                            .setAllowAudioMixedDecoderSupportAdaptiveness(true)
//                            // 音频混合时解码器支持自适应
//                            .setAllowVideoMixedDecoderSupportAdaptiveness(true)
                            .build(),
                            new AdaptiveTrackSelection.Factory(
                                    10000,// 至少 10 秒后才允许升码率
                                    25000, // 最多 2.5 秒后允许降码率
                                    25000, //
                                    0.7F))); //

            int decoderType = args.getDecoderType();
            LogUtil.log("VideoMediaxPlayer => createDecoder => decoderType = " + decoderType);
            // only_ffmpeg
            if (decoderType == PlayerType.DecoderType.ONLY_FFMPEG) {
                LogUtil.log("VideoMediaxPlayer => createDecoder => only_ffmpeg");
                Class<?> clazz = Class.forName("lib.kalu.mediax.renderers.VideoFFmpegAudioFFmpegRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_codec
            else if (decoderType == PlayerType.DecoderType.ONLY_CODEC) {
                LogUtil.log("VideoMediaxPlayer => createDecoder => only_codec");
                Class<?> clazz = Class.forName("lib.kalu.mediax.renderers.VideoCodecAudioCodecRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // video_codec_audio_ffmpeg
            else if (decoderType == PlayerType.DecoderType.ONLY_VIDEO_CODEC_AUDIO_FFMPEG) {
                LogUtil.log("VideoMediaxPlayer => createDecoder => only_video_codec_audio_ffmpeg");
                Class<?> clazz = Class.forName("lib.kalu.mediax.renderers.VideoCodecAudioFFmpegRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_video_ffmpeg_audio_codec
            else if (decoderType == PlayerType.DecoderType.ONLY_VIDEO_FFMPEG_AUDIO_CODEC) {
                LogUtil.log("VideoMediaxPlayer => createDecoder => only_video_ffmpeg_audio_codec");
                Class<?> clazz = Class.forName("lib.kalu.mediax.renderers.VideoFFmpegAudioCodecRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_audio_ffmpeg
            else if (decoderType == PlayerType.DecoderType.ONLY_AUDIO_FFMPEG) {
                LogUtil.log("VideoMediaxPlayer => createDecoder => only_audio_ffmpeg");
                Class<?> clazz = Class.forName("lib.kalu.mediax.renderers.OnlyAudioFFmpegRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_video_ffmpeg
            else if (decoderType == PlayerType.DecoderType.ONLY_VIDEO_FFMPEG) {
                LogUtil.log("VideoMediaxPlayer => createDecoder => only_video_ffmpeg");
                Class<?> clazz = Class.forName("lib.kalu.mediax.renderers.OnlyVideoFFmpegRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_audio_codec
            else if (decoderType == PlayerType.DecoderType.ONLY_AUDIO_CODEC) {
                LogUtil.log("VideoMediaxPlayer => createDecoder => only_audio_codec");
                Class<?> clazz = Class.forName("lib.kalu.mediax.renderers.OnlyAudioCodecRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_video_codec
            else if (decoderType == PlayerType.DecoderType.ONLY_VIDEO_CODEC) {
                LogUtil.log("VideoMediaxPlayer => createDecoder => only_video_codec");
                Class<?> clazz = Class.forName("lib.kalu.mediax.renderers.OnlyVideoCodecRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // all
            else {
                LogUtil.log("VideoMediaxPlayer => createDecoder => only_video_codec");
                Class<?> clazz = Class.forName("lib.kalu.mediax.renderers.BaseRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }

            mExoPlayer = builder.build();
            LogUtil.log("VideoMediaxPlayer => createDecoder => mExoPlayer = " + mExoPlayer);
            registListener();

            //播放器日志
//        if (mExoPlayer.getTrackSelector() instanceof MappingTrackSelector) {
//            mExoPlayer.addAnalyticsListener(new EventLogger((MappingTrackSelector) mExoPlayer.getTrackSelector(), "ExoPlayer"));
//        }
            LogUtil.log("VideoMediaxPlayer => createDecoder => completed");
        } catch (Exception e) {
            LogUtil.log("VideoMediaxPlayer => createDecoder => " + e.getMessage());
        }
    }

    @Override
    public void startDecoder(Context context, StartArgs args) {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            if (null == args)
                throw new Exception("error: args null");
            String url = args.getUrl();
            if (null == url)
                throw new Exception("error: url null");

            onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.INIT_READY);

            MediaSource mediaSource = buildSource(context, args);
            mExoPlayer.setMediaSource(mediaSource);
            boolean prepareAsync = args.isPrepareAsync();
            if (prepareAsync) {
                mExoPlayer.prepare();
            } else {
                mExoPlayer.prepare();
            }
            LogUtil.log("VideoMediaxPlayer => startDecoder => completed");
        } catch (Exception e) {
            onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.ERROR_BUILD_SOURCE);
            LogUtil.log("VideoMediaxPlayer => startDecoder => Exception " + e.getMessage());
        }
    }

    @Override
    public void initOptions(Context context, StartArgs args) {

        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer warning: null");
            boolean mute = args.isMute();
            if (mute) {
                mExoPlayer.setVolume(0f);
            } else {
                mExoPlayer.setVolume(1f);
            }
            boolean looping = args.isLooping();
            mExoPlayer.setRepeatMode(looping ? Player.REPEAT_MODE_ALL : Player.REPEAT_MODE_OFF);
            boolean playWhenReady = args.isPlayWhenReady();
            mExoPlayer.setPlayWhenReady(playWhenReady);
        } catch (Exception e) {
            LogUtil.log("VideoMediaxPlayer => initOptions => Exception step1 " + e.getMessage());
        }

        try {
            if (null == mExoPlayer)
                throw new Exception("error: mExoPlayer null");
            int seekParameters = args.getSeekType();
            if (seekParameters == PlayerType.SeekType.EXO_CLOSEST_SYNC) {
                mExoPlayer.setSeekParameters(androidx.media3.exoplayer.SeekParameters.CLOSEST_SYNC);
            } else if (seekParameters == PlayerType.SeekType.EXO_PREVIOUS_SYNC) {
                mExoPlayer.setSeekParameters(androidx.media3.exoplayer.SeekParameters.PREVIOUS_SYNC);
            } else if (seekParameters == PlayerType.SeekType.EXO_NEXT_SYNC) {
                mExoPlayer.setSeekParameters(androidx.media3.exoplayer.SeekParameters.NEXT_SYNC);
            } else if (seekParameters == PlayerType.SeekType.EXO_EXACT) {
                mExoPlayer.setSeekParameters(androidx.media3.exoplayer.SeekParameters.EXACT);
            } else {
                mExoPlayer.setSeekParameters(androidx.media3.exoplayer.SeekParameters.DEFAULT);
            }
        } catch (Exception e) {
            LogUtil.log("VideoMediaxPlayer => initOptions => Exception step2 " + e.getMessage());
        }

        // log
        try {
            if (null == mExoPlayer)
                throw new Exception("error: mExoPlayer null");
            if (null == args)
                throw new Exception("error: args null");
            boolean log = args.isLog();
            lib.kalu.mediax.util.MediaLogUtil.setDebug(log);
        } catch (Exception e) {
            LogUtil.log("VideoMediaxPlayer => initOptions => Exception step3 " + e.getMessage());
        }
    }

    @Override
    public void setSurface(Surface surface, int w, int h) {
        try {
            if (null == mExoPlayer)
                throw new Exception("error: mExoPlayer null");
            mExoPlayer.clearVideoSurface();
            if (null == surface)
                throw new Exception("error: surface null");
            mExoPlayer.setVideoSurface(surface);
            LogUtil.log("VideoMediaxPlayer => setSurface => completed");
        } catch (Exception e) {
            LogUtil.log("VideoMediaxPlayer => setSurface => " + e.getMessage());
        }
    }

    /**
     * 是否正在播放
     */
    @Override
    public boolean isPlaying() {
        try {
            if (!isPrepared)
                throw new Exception("mPrepared warning: false");
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            int state = mExoPlayer.getPlaybackState();
            if (state == Player.STATE_BUFFERING || state == Player.STATE_READY) {
                return mExoPlayer.getPlayWhenReady();
            } else if (state == Player.STATE_IDLE || state == Player.STATE_ENDED) {
                return false;
            } else {
                throw new Exception("not find");
            }
        } catch (Exception e) {
            LogUtil.log("VideoMediaxPlayer => isPlaying => " + e.getMessage());
            return false;
        }
    }

    @Override
    public void seekTo(long seek) {
        try {

            if (seek < 0L)
                throw new Exception("error: seek<0");
            if (null == mExoPlayer)
                throw new Exception("error: mMediaPlayer null");
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");

            long duration = getDuration();
            if (duration > 0L && seek > duration) {
                seek = duration;
            }

            mSeeking = true;
            long position = getPosition();
            onEvent(PlayerType.KernelType.MEDIA_V3, seek < position ? PlayerType.EventType.SEEK_START_REWIND : PlayerType.EventType.SEEK_START_FORWARD);
            mExoPlayer.seekTo(seek);
            LogUtil.log("VideoMediaxPlayer => seekTo =>");
        } catch (Exception e) {
            LogUtil.log("VideoMediaxPlayer => seekTo => " + e.getMessage());
        }
    }

    /**
     * 获取当前播放的位置
     */
    @Override
    public long getPosition() {
        try {
            if (!isPrepared)
                throw new Exception("mPrepared warning: false");
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            long currentPosition = mExoPlayer.getCurrentPosition();
            if (currentPosition < 0)
                throw new Exception("currentPosition warning: " + currentPosition);
            return currentPosition;
        } catch (Exception e) {
//            MPLogUtil.log("VideoMediaxPlayer => getPosition => " + e.getMessage());
            return 0L;
        }
    }

    /**
     * 获取视频总时长
     */
    @Override
    public long getDuration() {
        try {
            if (!isPrepared)
                throw new Exception("mPrepared warning: false");
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            long duration = mExoPlayer.getDuration();
            if (duration <= 0)
                throw new Exception("duration warning: " + duration);
            return duration;
        } catch (Exception e) {
//            MPLogUtil.log("VideoMediaxPlayer => getDuration => " + e.getMessage());
            return 0L;
        }
    }

    @Override
    public boolean isPrepared() {
        return isPrepared;
    }

    /**
     * 设置播放速度
     */
    @Override
    public boolean setSpeed(float speed) {
        try {
            if (null == mExoPlayer)
                throw new Exception("mMediaPlayer error: null");
            PlaybackParameters playbackParameters = mExoPlayer.getPlaybackParameters();
            if (null != playbackParameters) {
                playbackParameters = playbackParameters.withSpeed(speed);
            } else {
                playbackParameters = new PlaybackParameters(speed);
            }
            mExoPlayer.setPlaybackParameters(playbackParameters);
            return true;
        } catch (Exception e) {
            LogUtil.log("VideoMediaxPlayer => setSpeed => " + e.getMessage());
            return false;
        }
    }

    @Override
    public void setVolume(float v1, float v2) {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            float volume = Math.max(v1, v2);
            if (volume < 0)
                throw new Exception("error: volume < 0");
            mExoPlayer.setVolume(volume);
        } catch (Exception e) {
            LogUtil.log("VideoMediaxPlayer => setVolume => " + e.getMessage());
        }
    }

    @Override
    public void registListener() {
        try {
            if (null == mExoPlayer)
                throw new Exception("error: mExoPlayer null");
            mExoPlayer.addAnalyticsListener(mAnalyticsListener);
        } catch (Exception e) {
            LogUtil.log("VideoMediaxPlayer => registListener => Exception " + e.getMessage());
        }
    }

    @Override
    public void unRegistListener() {
        try {
            if (null == mExoPlayer)
                throw new Exception("error: mExoPlayer null");
            mExoPlayer.removeAnalyticsListener(mAnalyticsListener);
            mExoPlayer.setPlaybackParameters(null);
        } catch (Exception e) {
            LogUtil.log("VideoMediaxPlayer => unRegistListener => Exception " + e.getMessage());
        }
    }

    @Override
    public void release() {
        try {
            if (null != mSimpleCache) {
                mSimpleCache.release();
            }
            if (null != mHlsManifest) {
                mHlsManifest = null;
            }
            if (null == mExoPlayer)
                throw new Exception("error: mExoPlayer null");
            mExoPlayer.setVideoSurface(null);
            mExoPlayer.release();
            mExoPlayer = null;
        } catch (Exception e) {
            LogUtil.log("VideoMediaxPlayer => release => " + e.getMessage());
        }
    }

    @Override
    public boolean isBuffering() {
        return isBuffering;
    }

    /**
     * 播放
     */
    @Override
    public void start() {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            mExoPlayer.play();
        } catch (Exception e) {
            LogUtil.log("VideoMediaxPlayer => start => " + e.getMessage());
        }
    }

    /**
     * 暂停
     */
    @Override
    public void pause() {
        try {
            if (!isPrepared)
                throw new Exception("mPrepared warning: false");
            if (null == mExoPlayer)
                throw new Exception("mMediaPlayer error: null");
            mExoPlayer.pause();
        } catch (Exception e) {
            LogUtil.log("VideoMediaxPlayer => pause => " + e.getMessage());
        }
    }

    /**
     * 停止
     */
    @Override
    public void stop() {
        clear();
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            mExoPlayer.stop();
//            mExoPlayer.reset();
        } catch (Exception e) {
            LogUtil.log("VideoMediaxPlayer => stop => " + e.getMessage());
        }
    }

    /************************/

    private MediaSource buildSource(Context context, StartArgs args) throws Exception {

        try {
            String url = args.getUrl();
            if (null == url)
                throw new Exception("warning: url null");
            if (url.isEmpty())
                throw new Exception("warning: url isEmpty");

            int contentType;
            String lowerCase = url.toLowerCase();
            // dash
            if (lowerCase.endsWith(PlayerType.SchemeType._MPD)) {
                contentType = C.CONTENT_TYPE_DASH;
            }
            // hls
            else if (lowerCase.endsWith(PlayerType.SchemeType._M3U) || lowerCase.endsWith(PlayerType.SchemeType._M3U8)) {
                contentType = C.CONTENT_TYPE_HLS;
            }
            // SmoothStreaming
            else if (lowerCase.matches(PlayerType.SchemeType._MATCHES)) {
                contentType = C.CONTENT_TYPE_SS;
            }
            // rtmp
            else if (lowerCase.startsWith(PlayerType.SchemeType.RTMP)) {
                contentType = -100;
            }
            // mp4
            else if (lowerCase.startsWith(PlayerType.SchemeType._MP4)) {
                contentType = -200;
            }
            // rtsp
            else if (lowerCase.startsWith(PlayerType.SchemeType.RTSP)) {
                contentType = C.CONTENT_TYPE_RTSP;
            }
            // other
            else {
                contentType = C.CONTENT_TYPE_OTHER;
            }


            // 创建数据源工厂
            DefaultHttpDataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory()
                    .setUserAgent(MediaLibraryInfo.VERSION_SLASHY)
                    .setConnectTimeoutMs((int) args.getConnectTimout())
                    .setReadTimeoutMs((int) args.getConnectTimout())
                    .setAllowCrossProtocolRedirects(true)
                    .setKeepPostFor302Redirects(true);

            Cache cache = PlayerSDK.init().getPlayerBuilder().getCache();
            boolean enable = cache.isEnable();
            if (lowerCase.startsWith(PlayerType.SchemeType.FILE)) {
                enable = false;
            } else if (args.isLive()) {
                enable = false;
            }


            DataSource.Factory dataSource;
            // 开启缓存
            if (enable) {

                if (null == mSimpleCache) {

                    boolean external = cache.isExternal();
                    File dir;
                    if (external) {
                        File externalCacheDir = context.getExternalCacheDir();
                        dir = new File(externalCacheDir, cache.getDir());
                    } else {
                        File cacheDir = context.getCacheDir();
                        dir = new File(cacheDir, cache.getDir());
                    }

                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    int size = cache.getSize();
                    mSimpleCache = new SimpleCache(dir,
                            //
                            new LeastRecentlyUsedCacheEvictor(size * 1024 * 1024),
                            //
                            new StandaloneDatabaseProvider(context)
                    );
                }


                dataSource = new CacheDataSource.Factory()
                        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
                        .setCache(mSimpleCache)
                        // 网络请求工厂
                        .setUpstreamDataSourceFactory(dataSourceFactory)
                        // 缓存读取工厂
                        .setCacheReadDataSourceFactory(new FileDataSource.Factory())
                        // 写入数据到缓存
                        .setCacheWriteDataSinkFactory(new CacheDataSink.Factory()
                                .setFragmentSize(CacheDataSink.DEFAULT_FRAGMENT_SIZE)
                                .setCache(mSimpleCache))
                        // 自定义缓存键
                        .setCacheKeyFactory(new CacheKeyFactory() {
                            @Override
                            public String buildCacheKey(DataSpec dataSpec) {
                                String subUrl = dataSpec.uri.toString();
                                if (subUrl.endsWith(PlayerType.MarkType.M3U8)) {
                                    return subUrl;
                                } else if (subUrl.endsWith(PlayerType.MarkType.TS)) {
                                    return subUrl;
                                } else {
                                    return url;
                                }
                            }
                        });
            }
            // 关闭缓存
            else {
                dataSource = new DefaultDataSource.Factory(context, dataSourceFactory);
            }

            //
            List<MediaSource> mediaSources = new LinkedList<>();

            // 视频轨道
            ArrayList<String> vUrls = new ArrayList<>();
            vUrls.add(url);
            List<String> extraTrackVideo = args.getExtraTrackVideo();
            if (null != extraTrackVideo) {
                for (String vUrl : extraTrackVideo) {
                    vUrls.add(vUrl);
                }
            }
            for (String vUrl : vUrls) {
                // rtmp
                if (contentType == -100) {
                    Class<?> cls = Class.forName("ext.rtmp.RtmpDataSource");
                    DataSource.Factory factory = (DataSource.Factory) cls.newInstance();
                    LogUtil.log("VideoMediaxPlayer => buildSource => rtmp");
                    ProgressiveMediaSource source = new ProgressiveMediaSource.Factory(factory).createMediaSource(new MediaItem.Builder()
                            .setUri(Uri.parse(vUrl))
                            .build());
                    mediaSources.add(source);
                }
                // rtsp
                else if (contentType == C.CONTENT_TYPE_RTSP) {
                    Class<?> cls = Class.forName("androidx.media3.exoplayer.rtsp.RtspMediaSource$Factory");
                    Constructor<?> constructor = cls.getDeclaredConstructor(DataSource.Factory.class);
                    constructor.setAccessible(true);
                    Object object = constructor.newInstance(dataSource);
                    LogUtil.log("VideoMediaxPlayer => buildSource => rtsp");
                    MediaSource source = ((MediaSource.Factory) object).createMediaSource(new MediaItem.Builder()
                            .setUri(Uri.parse(vUrl))
                            .build());
                    mediaSources.add(source);
                }
                // dash
                else if (contentType == C.CONTENT_TYPE_DASH) {
                    Class<?> cls = Class.forName("androidx.media3.exoplayer.dash.DashMediaSource$Factory");
                    Constructor<?> constructor = cls.getDeclaredConstructor(DataSource.Factory.class);
                    constructor.setAccessible(true);
                    Object object = constructor.newInstance(dataSource);
                    LogUtil.log("VideoMediaxPlayer => buildSource => dash");
                    MediaSource source = ((MediaSource.Factory) object).createMediaSource(new MediaItem.Builder()
                            .setUri(Uri.parse(vUrl))
                            .build());
                    mediaSources.add(source);
                }
                // hls
                else if (contentType == C.CONTENT_TYPE_HLS) {
                    Class<?> cls = Class.forName("androidx.media3.exoplayer.hls.HlsMediaSource$Factory");
                    Constructor<?> constructor = cls.getDeclaredConstructor(DataSource.Factory.class);
                    constructor.setAccessible(true);
                    Object object = constructor.newInstance(dataSource);
                    LogUtil.log("VideoMediaxPlayer => buildSource => hls");

                    MediaSource source = ((MediaSource.Factory) object).createMediaSource(new MediaItem.Builder()
                            .setUri(Uri.parse(vUrl))
                            .build());
                    mediaSources.add(source);
                }
                // SmoothStreaming
                else if (contentType == C.CONTENT_TYPE_SS) {
                    Class<?> cls = Class.forName("androidx.media3.exoplayer.smoothstreaming.SsMediaSource$Factory");
                    Constructor<?> constructor = cls.getDeclaredConstructor(DataSource.Factory.class);
                    constructor.setAccessible(true);
                    Object object = constructor.newInstance(dataSource);
                    LogUtil.log("VideoMediaxPlayer => buildSource => SmoothStreaming");
                    MediaSource source = ((MediaSource.Factory) object).createMediaSource(new MediaItem.Builder()
                            .setUri(Uri.parse(vUrl))
                            .build());
                    mediaSources.add(source);
                }
                // mp4
                else if (contentType == -200) {
                    LogUtil.log("VideoMediaxPlayer => buildSource => mp4");
                    ProgressiveMediaSource source = new ProgressiveMediaSource.Factory(dataSource).createMediaSource(new MediaItem.Builder()
                            .setUri(Uri.parse(vUrl))
                            .build());
                    mediaSources.add(source);
                }
                // other
                else {
                    LogUtil.log("VideoMediaxPlayer => buildSource => other");
                    MediaSource source = new DefaultMediaSourceFactory(dataSource).createMediaSource(new MediaItem.Builder()
                            .setUri(Uri.parse(vUrl))
                            .build());
                    mediaSources.add(source);
                }
            }

            // 音频轨道
            List<String> extraTrackAudio = args.getExtraTrackAudio();
            if (null != extraTrackAudio) {
                for (String aUrl : extraTrackAudio) {
                    MediaSource source = new DefaultMediaSourceFactory(dataSourceFactory)
                            .createMediaSource(new MediaItem.Builder()
                                    .setUri(aUrl)
//                                    .setMimeType(track.getMimeType())
//                                    .setMediaId(track.getLabel())
                                    .build());
                    //
                    mediaSources.add(source);
                }
            }

            // 字幕轨道
            List<TrackInfo> extraTrackSubtitle = args.getExtraTrackSubtitle();
            if (null != extraTrackSubtitle) {
                for (TrackInfo track : extraTrackSubtitle) {
                    int roleFlags = track.getRoleFlags();
                    if (roleFlags == -1)
                        continue;
                    String sutitleUrl = track.getUrl();
                    if (null == sutitleUrl)
                        continue;
                    if (sutitleUrl.isEmpty())
                        continue;
                    String mimeType = track.getMimeType();
                    if (null == mimeType)
                        continue;
                    if (mimeType.isEmpty())
                        continue;
                    String language = track.getLanguage();
                    if (null == language)
                        continue;
                    if (language.isEmpty())
                        continue;
                    MediaItem.SubtitleConfiguration subtitleConfig = new MediaItem.SubtitleConfiguration.Builder(Uri.parse(sutitleUrl))
                            .setSelectionFlags(C.SELECTION_FLAG_AUTOSELECT)
                            .setMimeType(mimeType) // 也可以用 MimeTypes.APPLICATION_SUBRIP
                            .setLanguage(language)
                            .setRoleFlags(roleFlags)
                            .setLabel(track.getLabel())
                            .setId(track.getId())
                            .setSelectionFlags(track.getSelectionFlags())
                            .build();
                    SingleSampleMediaSource source = new SingleSampleMediaSource.Factory(dataSource)
                            .createMediaSource(subtitleConfig, C.TIME_UNSET);
                    //
                    mediaSources.add(source);
                }
            }

            return new MergingMediaSource(mediaSources.toArray(new MediaSource[0]));

        } catch (Exception e) {
            throw e;
        }
    }

    private final AnalyticsListener mAnalyticsListener = new AnalyticsListener() {

        @Override
        public void onTimelineChanged(AnalyticsListener.EventTime eventTime, int i) {
            Object manifest = mExoPlayer.getCurrentManifest();
            LogUtil.log("VideoMediaxPlayer => onTimelineChanged => manifest = " + manifest);
            if (manifest instanceof HlsManifest) {
                mHlsManifest = (HlsManifest) manifest;
            }
        }

        @Override
        public void onPlayerErrorChanged(EventTime eventTime, @Nullable PlaybackException e) {
            LogUtil.log("VideoMediaxPlayer => onPlayerErrorChanged => message = " + e.getMessage(), e);
        }

        @Override
        public void onPlayWhenReadyChanged(AnalyticsListener.EventTime eventTime, boolean playWhenReady, int reason) {
//        MPLogUtil.log("VideoMediaxPlayer => onPlayWhenReadyChanged => playWhenReady = " + playWhenReady + ", reason = " + reason);
        }

        @Override
        public void onPlayerError(AnalyticsListener.EventTime eventTime, PlaybackException error) {
            try {
                if (null == error)
                    throw new Exception("PlaybackException error: null");
                if (!(error instanceof ExoPlaybackException))
                    throw new Exception("PlaybackException error: not instanceof ExoPlaybackException");
                stop();
                onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.STOP);
                onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.ERROR);
            } catch (Exception e) {
                LogUtil.log("VideoMediaxPlayer => onPlayerError => error = " + error.getMessage());
            }
        }

        public void onEvents(Player player, AnalyticsListener.Events events) {
//                    MediaLogUtil.log("VideoMediaxPlayer => onEvents => isPlaying = " + player.isPlaying());
        }

        @Override
        public void onVideoSizeChanged(AnalyticsListener.EventTime eventTime, VideoSize videoSize) {
            LogUtil.log("VideoMediaxPlayer => onVideoSizeChanged => width = " + videoSize.width + ", height = " + videoSize.height);
        }

        @Override
        public void onIsPlayingChanged(AnalyticsListener.EventTime eventTime, boolean isPlaying) {
            LogUtil.log("VideoMediaxPlayer => onIsPlayingChanged => isPlaying = " + isPlaying);
        }

        @Override
        public void onLoadError(EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData, IOException e, boolean b) {
            stop();
            onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.STOP);
            onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.ERROR);
        }

        @Override
        public void onPlaybackStateChanged(AnalyticsListener.EventTime eventTime, int state) {

            // 播放错误
            if (state == Player.STATE_IDLE) {
                LogUtil.log("VideoMediaxPlayer => onPlaybackStateChanged -> state[Player.STATE_IDLE] = " + state);
            }
            // 播放完成
            else if (state == Player.STATE_ENDED) {
                LogUtil.log("VideoMediaxPlayer => onPlaybackStateChanged -> state[Player.STATE_ENDED] = " + state);
                onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.COMPLETE);
            }
            // 播放开始
            else if (state == Player.STATE_READY) {
                LogUtil.log("VideoMediaxPlayer => onPlaybackStateChanged -> state[Player.STATE_READY] = " + state);
                try {
                    if (!isPrepared)
                        throw new Exception("warning: isPrepared false");

                    // buffering
                    if (isBuffering) {
                        LogUtil.log("VideoMediaxPlayer => onPlaybackStateChanged -> state[Player.STATE_READY] -> buffering");
                        isBuffering = false;
                        onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.BUFFERING_STOP);
                    }
                    // seeking
                    else if (mSeeking) {
                        LogUtil.log("VideoMediaxPlayer => onPlaybackStateChanged -> state[Player.STATE_READY] -> seeking");
                        mSeeking = false;
                        onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.SEEK_FINISH);

                        // 起播快进
                        if (mPlayWhenReadySeeking) {
                            mPlayWhenReadySeeking = false;
                            onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.START);
                            // 立即播放
                            boolean playWhenReady = isPlayWhenReady();
                            onEvent(PlayerType.KernelType.MEDIA_V3, playWhenReady ? PlayerType.EventType.START_PLAY_WHEN_READY_TRUE : PlayerType.EventType.START_PLAY_WHEN_READY_FALSE);
                            if (playWhenReady) {
                                boolean playing = isPlaying();
                                if (playing)
                                    throw new Exception("warning: isPlaying true");
                                start();
                            } else {
                                pause();
                                onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.PAUSE_PlAY_WHEN_READY);
                            }
                        }
                        // 正常快进&快退
                        else {

                        }
                    }
                    // start ready
                    else {
                        LogUtil.log("VideoMediaxPlayer => onPlaybackStateChanged -> state[Player.STATE_READY] -> start ready");

                        boolean playWhenReady = isPlayWhenReady();
                        onEvent(PlayerType.KernelType.MEDIA_V3, playWhenReady ? PlayerType.EventType.START_PLAY_WHEN_READY_TRUE : PlayerType.EventType.START_PLAY_WHEN_READY_FALSE);
                        if (playWhenReady) {
                            boolean playing = isPlaying();
                            if (playing)
                                throw new Exception("warning: isPlaying true");
                            start();
                        } else {
                            pause();
                            onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.PAUSE);
                        }
                    }

                } catch (Exception e) {
                    LogUtil.log("VideoMediaxPlayer => onPlaybackStateChanged -> state[Player.STATE_READY] -> Exception " + e.getMessage());
                }
            }
            // 播放缓冲
            else if (state == Player.STATE_BUFFERING) {
                LogUtil.log("VideoMediaxPlayer => onPlaybackStateChanged -> state[Player.STATE_BUFFERING] = " + state);
                try {
                    if (!isPrepared)
                        throw new Exception("mPrepared warning: false");
                    isBuffering = true;
                    onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.BUFFERING_START);
                } catch (Exception e) {
                    LogUtil.log("VideoMediaxPlayer => onPlaybackStateChanged -> state[Player.STATE_BUFFERING] -> Exception " + state);
                }
            }
            // ????
            else {
                LogUtil.log("VideoMediaxPlayer => onPlaybackStateChanged -> state[????] = " + state);
            }
        }

        @Override
        public void onVideoInputFormatChanged(AnalyticsListener.EventTime eventTime, Format format, @Nullable DecoderReuseEvaluation decoderReuseEvaluation) {
            LogUtil.log("VideoMediaxPlayer => onVideoInputFormatChanged[出画面] => width = " + format.width + ", height = " + format.height);

            // 视频信息
            try {
                StartArgs args = getStartArgs();
                if (null == args)
                    throw new Exception("error: args null");
                @PlayerType.ScaleType.Value
                int scaleType = args.getscaleType();
                int rotation = args.getRotation();
//                int rotation = (videoSize.unappliedRotationDegrees > 0 ? videoSize.unappliedRotationDegrees : PlayerType.RotationType.DEFAULT);
                onVideoFormatChanged(PlayerType.KernelType.MEDIA_V3, rotation, scaleType, format.width, format.height, format.bitrate);
            } catch (Exception e) {
                LogUtil.log("VideoMediaxPlayer => onVideoInputFormatChanged => " + e.getMessage());
            }

            // 起播快进??
            try {
                if (isPrepared)
                    throw new Exception("warning: isPrepared true");
                isPrepared = true;
                onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.PREPARE);
                onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.VIDEO_RENDERING_START);
                long seek = getPlayWhenReadySeekToPosition();
                // 立即播放
                if (seek <= 0L) {
                    onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.START);
                }
                // 起播快进
                else {
                    onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.SEEK_START_FORWARD);
                    mPlayWhenReadySeeking = true;
                    seekTo(seek);
                }
            } catch (Exception e) {
                LogUtil.log("VideoMediaxPlayer => onVideoInputFormatChanged => Exception " + e.getMessage());
            }
        }

        @Override
        public void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime, Object output, long renderTimeMs) {
            LogUtil.log("VideoMediaxPlayer => onRenderedFirstFrame =>");
        }

        @Override
        public void onAudioInputFormatChanged(AnalyticsListener.EventTime eventTime, Format format, @Nullable DecoderReuseEvaluation decoderReuseEvaluation) {
            LogUtil.log("VideoMediaxPlayer => onAudioInputFormatChanged =>");
        }

        @Override
        public void onSeekStarted(EventTime eventTime) {
            LogUtil.log("VideoMediaxPlayer => onSeekStarted =>");
        }

        @Override
        public void onSeekBackIncrementChanged(EventTime eventTime, long l) {
            LogUtil.log("VideoMediaxPlayer => onSeekBackIncrementChanged =>");
        }

        @Override
        public void onSeekForwardIncrementChanged(EventTime eventTime, long l) {
            LogUtil.log("VideoMediaxPlayer => onSeekForwardIncrementChanged =>");
        }

        @Override
        public void onCues(EventTime eventTime, CueGroup cueGroup) {
            LogUtil.log("VideoMediaxPlayer => onCues => cueGroup = " + cueGroup);
        }

        @Override
        public void onCues(EventTime eventTime, List<Cue> cues) {
            LogUtil.log("VideoMediaxPlayer => onCues => cues = " + cues);

//            //
//            String language = null;
//            try {
//                androidx.media3.common.Tracks tracks = mExoPlayer.getCurrentTracks();
//                ImmutableList<Tracks.Group> groups = tracks.getGroups();
//                int groupCount = groups.size();
//                for (int groupIndex = 0; groupIndex < groupCount; groupIndex++) {
//                    Tracks.Group group = groups.get(groupIndex);
//                    if (null == group)
//                        continue;
//
//                    boolean isGroupSelected = group.isSelected();
//                    if (!isGroupSelected)
//                        continue;
//                    int trackCount = group.length;
//                    for (int trackIndex = 0; trackIndex < trackCount; trackIndex++) {
//                        //
//                        Format format = group.getTrackFormat(trackIndex);
//                        // 字幕轨道
//                        int trackType = group.getType();
//                        if (trackType == C.TRACK_TYPE_TEXT) {
//                            // 轨道是否被选中
//                            boolean isTrackSelected = group.isTrackSelected(trackIndex);
//                            if (isTrackSelected) {
//                                language = format.language;
//                                break;
//                            }
//                        }
//                    }
//                }
//                if (null == language)
//                    throw new Exception();
//            } catch (Exception e) {
//                language = "null";
//            }

            try {
                if (null == cues)
                    throw new Exception();
                if (cues.size() == 0)
                    throw new Exception();

                //
                for (Cue cue : cues) {
                    if (null != cue.text && cue.text.length() > 0) {
                        onUpdateSubtitle(PlayerType.KernelType.MEDIA_V3, cue.text);
                    }
                }
            } catch (Exception e) {
                onUpdateSubtitle(PlayerType.KernelType.MEDIA_V3, null);
            }
        }


        /**
         * 切换轨道
         * @param eventTime
         * @param trackSelectionParameters
         */
        @Override
        public void onTrackSelectionParametersChanged(EventTime eventTime, TrackSelectionParameters trackSelectionParameters) {
            LogUtil.log("VideoMediaxPlayer => onTrackSelectionParametersChanged => trackSelectionParameters = " + trackSelectionParameters);

//
//            int rendererCount = mExoPlayer.getRendererCount();
//            for(int i=0;i<rendererCount;i++){
//                int rendererType = mExoPlayer.getRendererType(i);
//                LogUtil.log("VideoMediaxPlayer => onTrackSelectionParametersChanged => i = "+i+", rendererType = "+rendererType);
//                mExoPlayer.getRenderer(i)
//            .(videoRendererIndex, true) // 禁用视频渲染器
//            player.setRendererDisabled(videoRendererIndex, false) // 重新启用

//            DefaultTrackSelector trackSelector = (DefaultTrackSelector) mExoPlayer.getTrackSelector();
//            DefaultTrackSelector.Parameters.Builder parameters = trackSelector.buildUponParameters();
//            // 找到视频渲染器的索引
//            for (int i = 0; i < trackSelector.getCurrentMappedTrackInfo().getRendererCount(); i++) {
//                if (trackSelector.getCurrentMappedTrackInfo().getRendererType(i) == C.TRACK_TYPE_VIDEO) {
//                    videoIndex = i;
//                    break;
//                }
//            }
//            // 禁用视频渲染器
//            parameters.setRendererDisabled(videoIndex, true);
//            trackSelector.setParameters(parameters);

        }

        /**
         * 切换轨道 完成
         * @param eventTime
         * @param tracks
         */
        @Override
        public void onTracksChanged(EventTime eventTime, Tracks tracks) {
            LogUtil.log("VideoMediaxPlayer => onTracksChanged => tracks = " + tracks);

//            if (videoIndex != -100) {
//                videoIndex = -100;
//                DefaultTrackSelector trackSelector = (DefaultTrackSelector) mExoPlayer.getTrackSelector();
//                DefaultTrackSelector.Parameters.Builder parameters = trackSelector.buildUponParameters();
//                // 找到视频渲染器的索引
//                for (int i = 0; i < trackSelector.getCurrentMappedTrackInfo().getRendererCount(); i++) {
//                    if (trackSelector.getCurrentMappedTrackInfo().getRendererType(i) == C.TRACK_TYPE_VIDEO) {
//                        videoIndex = i;
//                        LogUtil.log("VideoMediaxPlayer => onTracksChanged => i = " + i);
//                        break;
//                    }
//                }
//                // 禁用视频渲染器
//                parameters.setRendererDisabled(videoIndex, true);
//                trackSelector.setParameters(parameters);
//            }
        }

        @Override
        public void onSurfaceSizeChanged(EventTime eventTime, int i, int i1) {
            LogUtil.log("VideoMediaxPlayer => onSurfaceSizeChanged => i = " + i + ", i1 = " + i1);
        }
    };

    /*********/


//    @Override
//    public boolean toggleTrackLanguageSubtitle(String language) {
//        try {
//            if (null == language)
//                throw new Exception("warning: language null");
//            if (language.isEmpty())
//                throw new Exception("warning: language empty");
//            if (null == mExoPlayer)
//                throw new Exception("error: mExoPlayer null");
//            TrackSelector trackSelector = mExoPlayer.getTrackSelector();
//            TrackSelectionParameters selectionParameters = trackSelector.getParameters()
//                    .buildUpon()
//                    .setPreferredTextLanguage(language)
//                    .build();
//            trackSelector.setParameters(selectionParameters);
//            return true;
//        } catch (Exception e) {
//            LogUtil.log("VideoMediaxPlayer => toggleTrackLanguageSubtitle => " + e.getMessage());
//            return false;
//        }
//    }
//
//    @Override
//    public boolean toggleTrackLanguageAudio(String language) {
//        try {
//            if (null == language)
//                throw new Exception("warning: language null");
//            if (language.isEmpty())
//                throw new Exception("warning: language empty");
//            if (null == mExoPlayer)
//                throw new Exception("error: mExoPlayer null");
//            TrackSelector trackSelector = mExoPlayer.getTrackSelector();
//            TrackSelectionParameters selectionParameters = trackSelector.getParameters()
//                    .buildUpon()
//                    .setPreferredAudioLanguage(language)
//                    .build();
//            trackSelector.setParameters(selectionParameters);
//            return true;
//        } catch (Exception e) {
//            LogUtil.log("VideoMediaxPlayer => toggleTrackLanguageAudio => " + e.getMessage());
//            return false;
//        }
//    }
//
//    @Override
//    public boolean toggleTrackRoleFlagSubtitle(int roleFlag) {
//        try {
//            if (roleFlag == -1)
//                throw new Exception("error: roleFlag == -1");
//            if (null == mExoPlayer)
//                throw new Exception("error: mExoPlayer null");
//            TrackSelector trackSelector = mExoPlayer.getTrackSelector();
//            TrackSelectionParameters selectionParameters = trackSelector.getParameters()
//                    .buildUpon()
//                    .setPreferredTextRoleFlags(roleFlag)
//                    .build();
//            trackSelector.setParameters(selectionParameters);
//            return true;
//        } catch (Exception e) {
//            LogUtil.log("VideoMediaxPlayer => toggleTrackRoleFlagSubtitle => " + e.getMessage());
//            return false;
//        }
//    }
//
//    @Override
//    public boolean toggleTrackRoleFlagAudio(int roleFlag) {
//        try {
//            if (roleFlag == -1)
//                throw new Exception("error: roleFlag == -1");
//            if (null == mExoPlayer)
//                throw new Exception("error: mExoPlayer null");
//            TrackSelector trackSelector = mExoPlayer.getTrackSelector();
//            TrackSelectionParameters selectionParameters = trackSelector.getParameters()
//                    .buildUpon()
//                    .setPreferredAudioRoleFlags(roleFlag)
//                    .build();
//            trackSelector.setParameters(selectionParameters);
//            return true;
//        } catch (Exception e) {
//            LogUtil.log("VideoMediaxPlayer => toggleTrackRoleFlagAudio => " + e.getMessage());
//            return false;
//        }
//    }
//
//    @Override
//    public boolean toggleTrackRoleFlagVideo(int roleFlag) {
//        try {
//            if (roleFlag == -1)
//                throw new Exception("error: roleFlag == -1");
//            if (null == mExoPlayer)
//                throw new Exception("error: mExoPlayer null");
//            TrackSelector trackSelector = mExoPlayer.getTrackSelector();
//            TrackSelectionParameters selectionParameters = trackSelector.getParameters()
//                    .buildUpon()
//                    .setPreferredVideoRoleFlags(roleFlag)
//                    .build();
//            trackSelector.setParameters(selectionParameters);
//            return true;
//        } catch (Exception e) {
//            LogUtil.log("VideoMediaxPlayer => toggleTrackRoleFlagVideo => " + e.getMessage());
//            return false;
//        }
//    }
    @Override
    public boolean toggleTrack(TrackInfo trackInfo) {
        try {
            if (null == trackInfo)
                throw new Exception("error: trackArgs null");
            int groupIndex = trackInfo.getGroupIndex();
            if (groupIndex == -1)
                throw new Exception("error: groupIndex == -1");
            int trackIndex = trackInfo.getTrackIndex();
            if (trackIndex == -1)
                throw new Exception("error: trackIndex == -1");
            if (null == mExoPlayer)
                throw new Exception("error: mExoPlayer null");
            Tracks tracks = mExoPlayer.getCurrentTracks();
            ImmutableList<Tracks.Group> tracksGroups = tracks.getGroups();
            TrackGroup trackGroup = tracksGroups.get(groupIndex).getMediaTrackGroup();

            TrackSelector trackSelector = mExoPlayer.getTrackSelector();
            TrackSelectionParameters selectionParameters = trackSelector.getParameters()
                    .buildUpon()
                    .setOverrideForType(new TrackSelectionOverride(trackGroup, trackIndex))
                    .build();
            trackSelector.setParameters(selectionParameters);
            return true;
        } catch (Exception e) {
            LogUtil.log("VideoMediaxPlayer => toggleTrack => " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<TrackInfo> getTrackInfo(int type) {


        try {
            if (null == mExoPlayer)
                throw new Exception("error: mExoPlayer null");

            //
            LinkedList<TrackInfo> list = new LinkedList<>();

            //
            androidx.media3.common.Tracks tracks = mExoPlayer.getCurrentTracks();
            ImmutableList<androidx.media3.common.Tracks.Group> groups = tracks.getGroups();
            int groupCount = groups.size();
            for (int groupIndex = 0; groupIndex < groupCount; groupIndex++) {
                Tracks.Group group = groups.get(groupIndex);
                if (null == group)
                    continue;

                int trackType = group.getType();
                int trackCount = group.length;
                // 是否支持自适应播放
                boolean isGroupAdaptiveSupported = group.isAdaptiveSupported();
                boolean isGroupSelected = group.isSelected();
                boolean isGroupSupported = group.isSupported();
                for (int trackIndex = 0; trackIndex < trackCount; trackIndex++) {
                    //
                    Format format = group.getTrackFormat(trackIndex);
                    // 轨道是否支持
                    boolean isTrackSupported = group.isTrackSupported(trackIndex);
                    // 轨道是否被选中
                    boolean isTrackSelected = group.isTrackSelected(trackIndex);


                    TrackInfo trackInfo = new TrackInfo();

                    // 视频轨道
                    if (type == 1 && trackType == C.TRACK_TYPE_VIDEO) {

                        trackInfo.setBitrate(format.bitrate);
                        trackInfo.setWidth(format.width);
                        trackInfo.setHeight(format.height);

//                        object.put("frameRate", format.frameRate);
//                        object.put("rotationDegrees", format.rotationDegrees);
//                        object.put("pixelWidthHeightRatio", format.pixelWidthHeightRatio);
//                        object.put("projectionData", format.projectionData);
//                        object.put("stereoMode", format.stereoMode);
//                        object.put("colorInfo", format.colorInfo);
//                        object.put("maxSubLayers", format.maxSubLayers);
                    }
                    // 音频轨道
                    else if (type == 2 && trackType == C.TRACK_TYPE_AUDIO) {
//                        object.put("channelCount", format.channelCount);
//                        object.put("sampleRate", format.sampleRate);
//                        object.put("pcmEncoding", format.pcmEncoding);
//                        object.put("encoderDelay", format.encoderDelay);
//                        object.put("encoderPadding", format.encoderPadding);
                    }
                    // 字幕轨道
                    else if (type == 3 && trackType == C.TRACK_TYPE_TEXT) {
//                        object.put("accessibilityChannel", format.accessibilityChannel);
                        //  object.put("cueReplacementBehavior", format.cueReplacementBehavior);
                    }
                    // 媒体信息
                    else if (type == 4 && trackType == C.TRACK_TYPE_METADATA) {
                    }
                    // 视频轨道
                    else if (type == -1 && trackType == C.TRACK_TYPE_VIDEO) {

                        trackInfo.setBitrate(format.bitrate);
                        trackInfo.setWidth(format.width);
                        trackInfo.setHeight(format.height);

//                        object.put("frameRate", format.frameRate);
//                        object.put("rotationDegrees", format.rotationDegrees);
//                        object.put("pixelWidthHeightRatio", format.pixelWidthHeightRatio);
//                        object.put("projectionData", format.projectionData);
//                        object.put("stereoMode", format.stereoMode);
//                        object.put("colorInfo", format.colorInfo);
//                        object.put("maxSubLayers", format.maxSubLayers);
                    }
                    // 音频轨道
                    else if (type == -1 && trackType == C.TRACK_TYPE_AUDIO) {
//                        object.put("channelCount", format.channelCount);
//                        object.put("sampleRate", format.sampleRate);
//                        object.put("pcmEncoding", format.pcmEncoding);
//                        object.put("encoderDelay", format.encoderDelay);
//                        object.put("encoderPadding", format.encoderPadding);
                    }
                    // 字幕轨道
                    else if (type == -1 && trackType == C.TRACK_TYPE_TEXT) {
//                        object.put("accessibilityChannel", format.accessibilityChannel);
                        //   object.put("cueReplacementBehavior", format.cueReplacementBehavior);
                    }
                    // 媒体信息
                    else if (type == -1 && trackType == C.TRACK_TYPE_METADATA) {
                        // LogUtil.log("VideoExo2Player => getTrackInfo[C.TRACK_TYPE_METADATA] => groupCount = " + groupCount + ", groupIndex = " + groupIndex + ", trackCount = " + trackCount + ", trackIndex = " + trackIndex + ", trackType = " + trackType + ", isGroupAdaptiveSupported = " + isGroupAdaptiveSupported + ", isGroupSelected = " + isGroupSelected + ", isGroupSupported = " + isGroupSupported + ", isTrackSelected = " + isTrackSelected + ", isTrackSupported = " + isTrackSupported);
                        continue;
                    }
                    // 未知
                    else {
                        //  LogUtil.log("VideoExo2Player => getTrackInfo[Unknow] => groupCount = " + groupCount + ", groupIndex = " + groupIndex + ", trackCount = " + trackCount + ", trackIndex = " + trackIndex + ", trackType = " + trackType + ", isGroupAdaptiveSupported = " + isGroupAdaptiveSupported + ", isGroupSelected = " + isGroupSelected + ", isGroupSupported = " + isGroupSupported + ", isTrackSelected = " + isTrackSelected + ", isTrackSupported = " + isTrackSupported);
                        continue;
                    }


                    trackInfo.setGroupCount(groupCount);
                    trackInfo.setGroupIndex(groupIndex);
                    trackInfo.setTrackCount(trackCount);
                    trackInfo.setTrackIndex(trackIndex);
                    trackInfo.setTrackType(trackType);

                    trackInfo.setGroupAdaptiveSupported(isGroupAdaptiveSupported);
                    trackInfo.setGroupSupported(isGroupSupported);
                    trackInfo.setGroupSelected(isGroupSelected);
                    trackInfo.setTrackSupported(isTrackSupported);

                    // 自适应码率
                    if (isGroupAdaptiveSupported && trackType == androidx.media3.common.C.TRACK_TYPE_VIDEO) {
                        int videoWidth = getPlayerApi().getVideoRender().getVideoWidth();
                        int videoHeight = getPlayerApi().getVideoRender().getVideoHeight();
                        int videoBitrate = getPlayerApi().getVideoRender().getVideoBitrate();
                        boolean selected = (videoWidth == format.width && videoHeight == format.height && videoBitrate == format.bitrate);
                        if (selected) {
                            trackInfo.setTrackSelected(true);
                        } else {
                            trackInfo.setTrackSelected(false);
                        }
                    } else {
                        trackInfo.setTrackSelected(isTrackSelected);
                    }


                    trackInfo.setId(format.id);
                    trackInfo.setLabel(format.label);

//                    object.put("labels", format.labels);


                    trackInfo.setLanguage(format.language);
                    trackInfo.setRoleFlags(format.roleFlags);
                    trackInfo.setSelectionFlags(format.selectionFlags);
                    trackInfo.setSampleMimeType(format.sampleMimeType);
//                    object.put("averageBitrate", format.averageBitrate);
//                    object.put("peakBitrate", format.peakBitrate);
//                    object.put("codecs", format.codecs);
//                    object.put("metadata", format.metadata);
////                    object.put("customData", format.customData);
//                    // Container specific.
//                    object.put("containerMimeType", format.containerMimeType);
//                    object.put("maxInputSize", format.maxInputSize);
////                    object.put("maxNumReorderSamples", format.maxNumReorderSamples);
//                    object.put("initializationData", format.initializationData);
//                    object.put("drmInitData", format.drmInitData);
//                    object.put("subsampleOffsetUs", format.subsampleOffsetUs);
////                    object.put("hasPrerollSamples", format.hasPrerollSamples);

                    //   LogUtil.log("VideoExo2Player => getTrackInfo => groupCount = " + groupCount + ", groupIndex = " + groupIndex + ", trackCount = " + trackCount + ", trackIndex = " + trackIndex + ", trackType = " + trackType + ", isGroupAdaptiveSupported = " + isGroupAdaptiveSupported + ", isGroupSelected = " + isGroupSelected + ", isGroupSupported = " + isGroupSupported + ", isTrackSelected = " + isTrackSelected + ", isTrackSupported = " + isTrackSupported + ", isTrackMixed = " + isTrackMixed + ", isTrackMixedSelected = " + isTrackMixedSelected + ", format = " + object);
                    //
                    list.add(trackInfo);
                }
            }

            //
            if (list.isEmpty())
                throw new Exception("error: list empty");

//            LogUtil.log("VideoMediaxPlayer => getTrackInfo => type = " + type + ", list = " + list);
            return list;
        } catch (Exception e) {
            LogUtil.log("VideoMediaxPlayer => getTrackInfo => Exception " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<HlsSpanInfo> getSegments() {
        try {
            if (null == mHlsManifest)
                throw new Exception("warning: mHlsManifest null");
            if (null == mSimpleCache)
                throw new Exception("warning: mSimpleCache null");

            //
            ArrayList<HlsSpanInfo> list = null;
            //
            HlsMediaPlaylist mediaPlaylist = mHlsManifest.mediaPlaylist;
            String url = mediaPlaylist.baseUri;
            int lastIndexOf = url.lastIndexOf(PlayerType.MarkType.SEPARATOR);
            String baseUrl = url.substring(0, lastIndexOf);
            //
            for (HlsMediaPlaylist.Segment segment : mediaPlaylist.segments) {

                String segmentUrl = baseUrl + PlayerType.MarkType.SEPARATOR + segment.url;
                NavigableSet<CacheSpan> cachedSpans = mSimpleCache.getCachedSpans(segmentUrl);
                for (CacheSpan span : cachedSpans) {
                    if (null == span)
                        continue;
                    if (!span.isCached)
                        continue;
                    HlsSpanInfo hlsSpanInfo = new HlsSpanInfo();
                    hlsSpanInfo.setPath(span.file.getAbsolutePath());
                    hlsSpanInfo.setUrl(segmentUrl);
                    hlsSpanInfo.setRelativeStartTimeUs(segment.relativeStartTimeUs);
                    hlsSpanInfo.setDurationUs(segment.durationUs);
                    //
                    if (null == list) {
                        list = new ArrayList<>(0);
                    }
                    list.add(hlsSpanInfo);
                }
            }
            if (null == list)
                throw new Exception("warning: list null");
            return list;
        } catch (Exception e) {
            LogUtil.log("VideoMediaxPlayer => getBufferedHlsSpanInfo => Exception " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean appendSubtitleOffsetMs(int offsetMs) {
        try {
            if (null == mExoPlayer)
                throw new Exception("error: mExoPlayer null");
            int rendererCount = mExoPlayer.getRendererCount();
            for (int i = 0; i < rendererCount; i++) {
                int rendererType = mExoPlayer.getRendererType(i);
                if (rendererType != C.TRACK_TYPE_TEXT)
                    continue;
                Renderer renderer = mExoPlayer.getRenderer(i);
                if (null == renderer)
                    continue;
                if (renderer instanceof OffsetMsTextRenderer) {
                    ((OffsetMsTextRenderer) renderer).appendOffsetMs(offsetMs);
                    onUpdateSubtitle(PlayerType.KernelType.MEDIA_V3, "");
                }
                break;
            }
            return true;
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => appendSubtitleOffsetMs => Exception " + e.getMessage());
            return false;
        }
    }
}
