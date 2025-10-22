package lib.kalu.mediaplayer.core.kernel.video.exo2;

import android.content.Context;
import android.net.Uri;
import android.os.Looper;
import android.view.Surface;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerLibraryInfo;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.Tracks;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.analytics.DefaultAnalyticsCollector;
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider;
import com.google.android.exoplayer2.decoder.DecoderReuseEvaluation;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.LoadEventInfo;
import com.google.android.exoplayer2.source.MediaLoadData;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.hls.HlsManifest;
import com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistParserFactory;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.CueGroup;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionOverride;
import com.google.android.exoplayer2.trackselection.TrackSelectionParameters;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSink;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheKeyFactory;
import com.google.android.exoplayer2.upstream.cache.CacheSpan;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.video.VideoSize;
import com.google.common.collect.ImmutableList;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
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
import lib.kalu.mediaplayer.core.kernel.video.exo2.proxy.CustomDefaultHttpDataSource;
import lib.kalu.mediaplayer.core.kernel.video.exo2.proxy.CustomHlsPlaylistParserFactory;
import lib.kalu.mediaplayer.util.LogUtil;


public final class VideoExo2Player extends VideoBasePlayer {

    private boolean isVideoSizeChanged = false;
    private boolean mPlayWhenReadySeeking = false;
    private boolean mSeeking = false;
    private boolean isBuffering = false;
    private boolean isPrepared = false;


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
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => releaseDecoder => " + e.getMessage());
        }
    }


    @Override
    public void createDecoder(Context context, StartArgs args) {
        try {
            if (null != mExoPlayer)
                throw new Exception("warning: null != mExoPlayer");
            if (null == args)
                throw new Exception("error: args null");

            ExoPlayer.Builder builder = new ExoPlayer.Builder(context)
                    // 播放器调试和诊断相关的配置项
                    .setUsePlatformDiagnostics(false)
                    // 创建渲染器工厂
                    .setRenderersFactory(new DefaultRenderersFactory(context) {
                        @Override
                        protected void buildTextRenderers(Context context, TextOutput textOutput, Looper looper, int i, ArrayList<Renderer> arrayList) {
                            super.buildTextRenderers(context, textOutput, looper, i, arrayList);
//                            TextRenderer textRenderer = new TextRenderer(textOutput, looper);
//                            textRenderer.experimentalSetLegacyDecodingEnabled(true);
//                            arrayList.arrayListadd(textRenderer);
                        }
                    })
                    .setMediaSourceFactory(new DefaultMediaSourceFactory(context)
//                            .experimentalParseSubtitlesDuringExtraction(true)
                    )
                    // 监听
                    .setAnalyticsCollector(new DefaultAnalyticsCollector(Clock.DEFAULT))
                    // 配置带宽测量器
                    .setBandwidthMeter(new DefaultBandwidthMeter.Builder(context)
                            // 初始带宽估算为5Mbps（5,000,000 bps）
//                            .setInitialBitrateEstimate(5_000_000)
                            .build())
                    // 缓冲缓存
                    .setLoadControl(new DefaultLoadControl.Builder()
                            // minBufferMs 最小缓冲时长的参数，单位为毫秒
                            // maxBufferMs 限制最大缓冲时长的参数，单位是毫秒
                            // bufferForPlaybackMs 如果设置 bufferForPlaybackMs 为 5000，那么播放器会在开始播放前先缓冲 5 秒钟的媒体数据
                            // bufferForPlaybackAfterRebufferMs 用于指定在播放过程中出现重新缓冲（Rebuffer）后，为了保证后续播放流畅，需要再次缓冲的时长，单位同样是毫秒
                            .setBufferDurationsMs(60_000, 60_000, 5_000, 5_000)
                            .build())
                    // 自适应码率
                    .setTrackSelector(new DefaultTrackSelector(context, DefaultTrackSelector.Parameters.getDefaults(context)
                            .buildUpon()
                            // 主字幕轨道
                            .setPreferredTextRoleFlags(C.ROLE_FLAG_MAIN)
                            // 主音频轨道
                            .setPreferredAudioRoleFlags(C.ROLE_FLAG_MAIN)
                            // 主视频轨道
                            .setPreferredVideoRoleFlags(C.ROLE_FLAG_MAIN)
                            // 音频禁止混合 MIME 类型切换（如视频+音频单独切换）
                            .setAllowAudioMixedMimeTypeAdaptiveness(true)
                            // 视频禁止混合 MIME 类型切换（如视频+音频单独切换）
                            .setAllowVideoMixedMimeTypeAdaptiveness(true)
                            // 音频禁止非无缝切换
                            // .setAllowAudioNonSeamlessAdaptiveness(false)
                            // 视频禁止非无缝切换
                            .setAllowVideoNonSeamlessAdaptiveness(true)
                            // 音频混合声道数量的自适应性
                            .setAllowAudioMixedChannelCountAdaptiveness(true)
                            // 音频混合采样率自适应
                            .setAllowAudioMixedSampleRateAdaptiveness(true)
                            // 音频混合时解码器支持自适应
                            .setAllowAudioMixedDecoderSupportAdaptiveness(true)
                            // 音频混合时解码器支持自适应
                            .setAllowVideoMixedDecoderSupportAdaptiveness(true)
                            .build(),
                            // 10000, 25000, 25000, 0.7F
                            new AdaptiveTrackSelection.Factory(
                                    10_000,// 至少 10 秒后才允许升码率
                                    25_000, // 最多 2.5 秒后允许降码率
                                    25_000, //
                                    0.7F))); //

            int decoderType = args.getDecoderType();
            LogUtil.log("VideoExo2Player => createDecoder => decoderType = " + decoderType);
            // only_ffmpeg
            if (decoderType == PlayerType.DecoderType.ONLY_FFMPEG) {
                LogUtil.log("VideoExo2Player => createDecoder => only_ffmpeg");
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.renderers.VideoFFmpegAudioFFmpegRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_codec
            else if (decoderType == PlayerType.DecoderType.ONLY_CODEC) {
                LogUtil.log("VideoExo2Player => createDecoder => only_codec");
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.renderers.VideoCodecAudioCodecRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // ONLY_VIDEO_CODEC_AUDIO_FFMPEG
            else if (decoderType == PlayerType.DecoderType.ONLY_VIDEO_CODEC_AUDIO_FFMPEG) {
                LogUtil.log("VideoExo2Player => createDecoder => only_video_codec_audio_ffmpeg");
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.renderers.VideoCodecAudioFFmpegRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_video_ffmpeg_audio_codec
            else if (decoderType == PlayerType.DecoderType.ONLY_VIDEO_FFMPEG_AUDIO_CODEC) {
                LogUtil.log("VideoExo2Player => createDecoder => only_video_ffmpeg_audio_codec");
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.renderers.VideoFFmpegAudioCodecRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_audio_ffmpeg
            else if (decoderType == PlayerType.DecoderType.ONLY_AUDIO_FFMPEG) {
                LogUtil.log("VideoExo2Player => createDecoder => only_audio_ffmpeg");
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.renderers.OnlyAudioFFmpegRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_video_ffmpeg
            else if (decoderType == PlayerType.DecoderType.ONLY_VIDEO_FFMPEG) {
                LogUtil.log("VideoExo2Player => createDecoder => only_video_ffmpeg");
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.renderers.OnlyVideoFFmpegRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_audio_codec
            else if (decoderType == PlayerType.DecoderType.ONLY_AUDIO_CODEC) {
                LogUtil.log("VideoExo2Player => createDecoder => only_audio_codec");
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.renderers.OnlyAudioCodecRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_video_codec
            else if (decoderType == PlayerType.DecoderType.ONLY_VIDEO_CODEC) {
                LogUtil.log("VideoExo2Player => createDecoder => only_video_codec");
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.renderers.OnlyVideoCodecRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // all
            else {
                LogUtil.log("VideoExo2Player => createDecoder => all");
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.renderers.BaseRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((com.google.android.exoplayer2.RenderersFactory) newInstance);
            }

            mExoPlayer = builder.build();
            registListener();

            //
            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.INIT);
            long trySeeDuration = args.getTrySeeDuration();
            if (trySeeDuration > 0L) {
                onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.TRY_SEE_START);
            }

//            if (mSpeedPlaybackParameters != null) {
//                mExoPlayer.setPlaybackParameters(mSpeedPlaybackParameters);
//            }
//        mIsPreparing = true;

            //播放器日志
//        if (mExoPlayer.getTrackSelector() instanceof MappingTrackSelector) {
//            mExoPlayer.addAnalyticsListener(new EventLogger((MappingTrackSelector) mExoPlayer.getTrackSelector(), "ExoPlayer"));
//        }
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => createDecoder => " + e.getMessage());
        }
    }

    @Override
    public void startDecoder(Context context, StartArgs args) {
        try {
            if (null == mExoPlayer)
                throw new Exception("warning: mExoPlayer null");
            if (null == args)
                throw new Exception("error: args null");
            String url = args.getUrl();
            if (null == url)
                throw new Exception("error: url null");

            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.INIT_READY);

            MediaSource mediaSource = buildSource(context, args);
            mExoPlayer.setMediaSource(mediaSource);
            boolean prepareAsync = args.isPrepareAsync();
            if (prepareAsync) {
                mExoPlayer.prepare();
            } else {
                mExoPlayer.prepare();
            }
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => startDecoder => Exception " + e.getMessage());
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
            LogUtil.log("VideoExo2Player => initOptions => Exception step1 " + e.getMessage());
        }

        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer warning: null");
            if (null == args)
                throw new Exception("error: args null");
            int seekType = args.getSeekType();
            if (seekType == PlayerType.SeekType.EXO_CLOSEST_SYNC) {
                mExoPlayer.setSeekParameters(SeekParameters.CLOSEST_SYNC);
            } else if (seekType == PlayerType.SeekType.EXO_PREVIOUS_SYNC) {
                mExoPlayer.setSeekParameters(SeekParameters.PREVIOUS_SYNC);
            } else if (seekType == PlayerType.SeekType.EXO_NEXT_SYNC) {
                mExoPlayer.setSeekParameters(SeekParameters.NEXT_SYNC);
            } else if (seekType == PlayerType.SeekType.EXO_EXACT) {
                mExoPlayer.setSeekParameters(SeekParameters.EXACT);
            } else {
                mExoPlayer.setSeekParameters(SeekParameters.DEFAULT);
            }
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => initOptions => Exception step2 " + e.getMessage());
        }

        // log
        try {
            if (null == mExoPlayer)
                throw new Exception("error: mExoPlayer null");
            if (null == args)
                throw new Exception("error: args null");
            boolean log = args.isLog();
            lib.kalu.exoplayer2.util.ExoLogUtil.setDebug(log);
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => initOptions => Exception step3 " + e.getMessage());
        }
    }

    @Override
    public void setSurface(Surface surface, int w, int h) {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            mExoPlayer.clearVideoSurface();
            if (null == surface)
                throw new Exception("surface error: null");
            mExoPlayer.setVideoSurface(surface);
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => setSurface => " + e.getMessage());
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
            LogUtil.log("VideoExo2Player => isPlaying => " + e.getMessage());
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

            long position = getPosition();
            onEvent(PlayerType.KernelType.EXO_V2, seek < position ? PlayerType.EventType.SEEK_START_REWIND : PlayerType.EventType.SEEK_START_FORWARD);
            mSeeking = true;
            mExoPlayer.seekTo(seek);
            LogUtil.log("VideoExo2Player => seekTo =>");
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => seekTo => " + e.getMessage());
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
//            MPLogUtil.log("VideoExo2Player => getPosition => " + e.getMessage());
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
//            MPLogUtil.log("VideoExo2Player => getDuration => " + e.getMessage());
            return 0L;
        }
    }

    @Override
    public boolean isPrepared() {
        return isPrepared;
    }

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
            LogUtil.log("VideoExo2Player => setSpeed => " + e.getMessage());
            return false;
        }
    }

    /**
     * 设置播放速度
     */


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
            LogUtil.log("VideoExo2Player => setVolume => " + e.getMessage());
        }
    }

    @Override
    public void registListener() {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            mExoPlayer.addAnalyticsListener(mAnalyticsListener);
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => registListener => Exception " + e.getMessage());
        }
    }

    @Override
    public void unRegistListener() {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            mExoPlayer.removeAnalyticsListener(mAnalyticsListener);
            mExoPlayer.setPlaybackParameters(null);
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => unRegistListener => Exception " + e.getMessage());
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
                throw new Exception("mExoPlayer error: null");
            mExoPlayer.setVideoSurface(null);
            mExoPlayer.release();
            mExoPlayer = null;
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => release => " + e.getMessage());
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
        LogUtil.log("VideoExo2Player => start");
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            mExoPlayer.play();
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => start => " + e.getMessage());
        }
    }

    /**
     * isLopping
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
            LogUtil.log("VideoExo2Player => pause => " + e.getMessage());
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
            LogUtil.log("VideoExo2Player => stop => " + e.getMessage());
        }
    }

    /************************/

    private MediaSource buildSource(Context context, StartArgs args) throws Exception {

        try {
            String url = args.getUrl();

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

            // 2
            MediaItem.Builder builder = new MediaItem.Builder();
            builder.setUri(Uri.parse(url));


            CustomDefaultHttpDataSource.Factory dataSourceFactory = new CustomDefaultHttpDataSource.Factory()
                    .setUserAgent(ExoPlayerLibraryInfo.VERSION_SLASHY)
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

            // 开启缓存
            DataSource.Factory dataSource;
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
//                                LogUtil.log("VideoExo2Player => buildSource => buildCacheKey => subUrl = " + subUrl);
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
                    Class<?> cls = Class.forName("com.google.android.exoplayer2.ext.rtmp.RtmpDataSource");
                    DataSource.Factory factory = (DataSource.Factory) cls.newInstance();
                    LogUtil.log("VideoExo2Player => buildSource => rtmp");
                    ProgressiveMediaSource source = new ProgressiveMediaSource.Factory(factory).createMediaSource(new MediaItem.Builder()
                            .setUri(Uri.parse(vUrl))
                            .build());
                    mediaSources.add(source);
                }
                // rtsp
                else if (contentType == C.CONTENT_TYPE_RTSP) {
                    Class<?> cls = Class.forName("com.google.android.exoplayer2.source.rtsp.RtspMediaSource$Factory");
                    Constructor<?> constructor = cls.getDeclaredConstructor(DataSource.Factory.class);
                    constructor.setAccessible(true);
                    Object object = constructor.newInstance(dataSource);
                    LogUtil.log("VideoExo2Player => buildSource => rtsp");
                    MediaSource source = ((MediaSource.Factory) object).createMediaSource(new MediaItem.Builder()
                            .setUri(Uri.parse(vUrl))
                            .build());
                    mediaSources.add(source);
                }
                // dash
                else if (contentType == C.CONTENT_TYPE_DASH) {
                    Class<?> cls = Class.forName("com.google.android.exoplayer2.source.dash.DashMediaSource$Factory");
                    Constructor<?> constructor = cls.getDeclaredConstructor(DataSource.Factory.class);
                    constructor.setAccessible(true);
                    Object object = constructor.newInstance(dataSource);
                    LogUtil.log("VideoExo2Player => buildSource => dash");
                    MediaSource source = ((MediaSource.Factory) object).createMediaSource(new MediaItem.Builder()
                            .setUri(Uri.parse(vUrl))
                            .build());
                    mediaSources.add(source);
                }
                // hls
                else if (contentType == C.CONTENT_TYPE_HLS) {
                    Class<?> cls = Class.forName("com.google.android.exoplayer2.source.hls.HlsMediaSource$Factory");
                    Constructor<?> constructor = cls.getDeclaredConstructor(DataSource.Factory.class);
                    constructor.setAccessible(true);
                    Method method = cls.getMethod("setPlaylistParserFactory", HlsPlaylistParserFactory.class);
                    Object object = method.invoke(constructor.newInstance(dataSource), new CustomHlsPlaylistParserFactory());
                    LogUtil.log("VideoExo2Player => buildSource => hls");

                    MediaSource source = ((MediaSource.Factory) object)
                            .createMediaSource(new MediaItem.Builder()
                                    .setUri(Uri.parse(vUrl))
                                    .build());
                    mediaSources.add(source);
                }
                // SmoothStreaming
                else if (contentType == C.CONTENT_TYPE_SS) {
                    Class<?> cls = Class.forName("exoplayer.smoothstreaming.SsMediaSource$Factory");
                    Constructor<?> constructor = cls.getDeclaredConstructor(DataSource.Factory.class);
                    constructor.setAccessible(true);
                    Object object = constructor.newInstance(dataSource);
                    LogUtil.log("VideoExo2Player => buildSource => SmoothStreaming");
                    MediaSource source = ((MediaSource.Factory) object).createMediaSource(new MediaItem.Builder()
                            .setUri(Uri.parse(vUrl))
                            .build());
                    mediaSources.add(source);
                }
                // mp4
                else if (contentType == -200) {
                    LogUtil.log("VideoExo2Player => buildSource => mp4");
                    ProgressiveMediaSource source = new ProgressiveMediaSource.Factory(dataSource).createMediaSource(new MediaItem.Builder()
                            .setUri(Uri.parse(vUrl))
                            .build());
                    mediaSources.add(source);
                }
                // other
                else {
                    LogUtil.log("VideoExo2Player => buildSource => other");
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
        public void onTimelineChanged(EventTime eventTime, int i) {
            Object manifest = mExoPlayer.getCurrentManifest();
            LogUtil.log("VideoExo2Player => onTimelineChanged => manifest = " + manifest);
            if (manifest instanceof HlsManifest) {
                mHlsManifest = (HlsManifest) manifest;
            }
        }

        @Override
        public void onPlayerErrorChanged(EventTime eventTime, PlaybackException error) {
            LogUtil.log("VideoExo2Player => onPlayerErrorChanged => message = " + error.getMessage(), error);
        }

        @Override
        public void onPlaybackStateChanged(AnalyticsListener.EventTime eventTime, int state) {
            // 播放错误
            if (state == Player.STATE_IDLE) {
                LogUtil.log("VideoExo2Player => onPlaybackStateChanged -> state[Player.STATE_IDLE] = " + state);
            }
            // 播放完成
            else if (state == Player.STATE_ENDED) {
                LogUtil.log("VideoExo2Player => onPlaybackStateChanged -> state[Player.STATE_ENDED] = " + state);
                onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.COMPLETE);
            }
            // 播放开始
            else if (state == Player.STATE_READY) {
                LogUtil.log("VideoExo2Player => onPlaybackStateChanged -> state[Player.STATE_READY] = " + state);
                try {
                    if (!isPrepared)
                        throw new Exception("warning: isPrepared false");

                    // buffering
                    if (isBuffering) {
                        LogUtil.log("VideoExo2Player => onPlaybackStateChanged -> state[Player.STATE_READY] -> buffering");
                        isBuffering = false;
                        onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.BUFFERING_STOP);
                    }
                    // seeking
                    else if (mSeeking) {
                        LogUtil.log("VideoExo2Player => onPlaybackStateChanged -> state[Player.STATE_READY] -> seeking");
                        mSeeking = false;
                        onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.SEEK_FINISH);

                        // 起播快进
                        if (mPlayWhenReadySeeking) {
                            mPlayWhenReadySeeking = false;
                            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.START);
                            // 立即播放
                            boolean playWhenReady = isPlayWhenReady();
                            onEvent(PlayerType.KernelType.EXO_V2, playWhenReady ? PlayerType.EventType.START_PLAY_WHEN_READY_TRUE : PlayerType.EventType.START_PLAY_WHEN_READY_FALSE);
                            if (playWhenReady) {
                                boolean playing = isPlaying();
                                if (playing)
                                    throw new Exception("warning: isPlaying true");
                                start();
                            } else {
                                pause();
                                onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.PAUSE_PlAY_WHEN_READY);
                            }
                        }
                        // 正常快进&快退
                        else {

                        }
                    }
                    // start ready
                    else {
                        LogUtil.log("VideoExo2Player => onPlaybackStateChanged -> state[Player.STATE_READY] -> start ready ");

                        boolean playWhenReady = isPlayWhenReady();
                        LogUtil.log("VideoExo2Player => onPlaybackStateChanged -> state[Player.STATE_READY] -> playWhenReady = " + playWhenReady);
                        onEvent(PlayerType.KernelType.EXO_V2, playWhenReady ? PlayerType.EventType.START_PLAY_WHEN_READY_TRUE : PlayerType.EventType.START_PLAY_WHEN_READY_FALSE);
                        if (playWhenReady) {
                            boolean playing = isPlaying();
                            if (playing)
                                throw new Exception("warning: isPlaying true");
                            start();
                        } else {
                            pause();
                            onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.PAUSE);
                        }
                    }

                } catch (Exception e) {
                    LogUtil.log("VideoExo2Player => onPlaybackStateChanged -> state[Player.STATE_READY] -> Exception " + e.getMessage());
                }
            }
            // 播放缓冲
            else if (state == Player.STATE_BUFFERING) {
                LogUtil.log("VideoExo2Player => onPlaybackStateChanged -> state[Player.STATE_BUFFERING] = " + state);
                try {
                    if (!isPrepared)
                        throw new Exception("mPrepared warning: false");
                    isBuffering = true;
                    onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.BUFFERING_START);
                } catch (Exception e) {
                    LogUtil.log("VideoExo2Player => onPlaybackStateChanged -> state[Player.STATE_BUFFERING] -> Exception " + state);
                }
            }
            // ????
            else {
                LogUtil.log("VideoExo2Player => onPlaybackStateChanged -> state[????] = " + state);
            }
        }

        @Override
        public void onVideoInputFormatChanged(EventTime eventTime, Format format, DecoderReuseEvaluation decoderReuseEvaluation) {
            LogUtil.log("VideoExo2Player => onVideoInputFormatChanged[出画面] => width = " + format.width + ", height = " + format.height + ", bitrate = " + format.bitrate);

            // 视频信息
            try {
                StartArgs args = getStartArgs();
                if (null == args)
                    throw new Exception("error: args null");
                @PlayerType.ScaleType.Value
                int scaleType = args.getscaleType();
                int rotation = args.getRotation();
//                int rotation = (videoSize.unappliedRotationDegrees > 0 ? videoSize.unappliedRotationDegrees : PlayerType.RotationType.DEFAULT);
                onVideoFormatChanged(PlayerType.KernelType.EXO_V2, rotation, scaleType, format.width, format.height, format.bitrate);
            } catch (Exception e) {
                LogUtil.log("VideoExo2Player => onVideoInputFormatChanged => " + e.getMessage());
            }

            // 起播快进??
            try {
                if (isPrepared)
                    throw new Exception("warning: isPrepared true");
                isPrepared = true;
                onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.PREPARE);
                onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.VIDEO_RENDERING_START);
                long seek = getPlayWhenReadySeekToPosition();
                // 立即播放
                if (seek <= 0L) {
                    onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.START);
                }
                // 起播快进
                else {
                    onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.SEEK_START_FORWARD);
                    mPlayWhenReadySeeking = true;
                    seekTo(seek);
                }
            } catch (Exception e) {
                LogUtil.log("VideoExo2Player => onVideoInputFormatChanged => Exception " + e.getMessage());
            }
        }

        @Override
        public void onPlayerError(AnalyticsListener.EventTime eventTime, PlaybackException error) {
            LogUtil.log("VideoExo2Player => onPlayerError => error = " + error.getMessage());
            try {
                if (null == error)
                    throw new Exception("warning: null == error");
                if (!(error instanceof ExoPlaybackException))
                    throw new Exception("warning: error not instanceof ExoPlaybackException");
                stop();
                onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.STOP);
                onEvent(PlayerType.KernelType.EXO_V2, PlayerType.EventType.ERROR);
            } catch (Exception e) {
                LogUtil.log("VideoExo2Player => onPlayerError => " + e.getMessage());
            }
        }

        @Override
        public void onVideoSizeChanged(AnalyticsListener.EventTime eventTime, VideoSize videoSize) {
        }

        @Override
        public void onLoadStarted(AnalyticsListener.EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
            LogUtil.log("VideoExo2Player => onLoadStarted =>");
        }

        @Override
        public void onLoadCompleted(AnalyticsListener.EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
            LogUtil.log("VideoExo2Player => onLoadCompleted =>");
        }

        @Override
        public void onLoadCanceled(AnalyticsListener.EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
            LogUtil.log("VideoExo2Player => onLoadCanceled =>");
        }

        @Override
        public void onLoadError(AnalyticsListener.EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {
            LogUtil.log("VideoExo2Player => onLoadError =>");
        }

        @Override
        public void onCues(EventTime eventTime, CueGroup cueGroup) {
            LogUtil.log("VideoExo2Player => onCues => cueGroup = " + cueGroup);
        }

        @Override
        public void onCues(EventTime eventTime, List<Cue> cues) {
            LogUtil.log("VideoExo2Player => onCues => cuesLength = " + cues.size());

//            //
//            String language = null;
//            try {
//                Tracks tracks = mExoPlayer.getCurrentTracks();
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
                for (Cue cue : cues) {
                    if (null != cue.text && cue.text.length() > 0) {
                        onUpdateSubtitle(PlayerType.KernelType.EXO_V2, cue.text);
                    }
                }
            } catch (Exception e) {
                onUpdateSubtitle(PlayerType.KernelType.EXO_V2, null);
            }
        }
    };

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
//            LogUtil.log("VideoExo2Player => toggleTrackLanguageSubtitle => " + e.getMessage());
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
//            LogUtil.log("VideoExo2Player => toggleTrackLanguageAudio => " + e.getMessage());
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
//            LogUtil.log("VideoExo2Player => toggleTrackRoleFlagSubtitle => " + e.getMessage());
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
//            LogUtil.log("VideoExo2Player => toggleTrackRoleFlagAudio => " + e.getMessage());
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
//            LogUtil.log("VideoExo2Player => toggleTrackRoleFlagVideo => " + e.getMessage());
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
            LogUtil.log("VideoExo2Player => toggleTrack => " + e.getMessage());
            return false;
        }
    }

    public List<TrackInfo> getTrackInfo(int type) {

        try {
            if (null == mExoPlayer)
                throw new Exception("error: mExoPlayer null");

            //
            LinkedList<TrackInfo> list = new LinkedList<>();

            //
            Tracks tracks = mExoPlayer.getCurrentTracks();
            com.google.common.collect.ImmutableList<Tracks.Group> groups = tracks.getGroups();
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

            //   LogUtil.log("VideoExo2Player => getTrackInfo => type = " + type + ", list = " + list);
            return list;
        } catch (Exception e) {
            LogUtil.log("VideoExo2Player => getTrackInfo => Exception " + e.getMessage());
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
//            LogUtil.log("VideoExo2Player => getBufferedHlsSpanInfo => url = " + url);
            int lastIndexOf = url.lastIndexOf(PlayerType.MarkType.SEPARATOR);
            String baseUrl = url.substring(0, lastIndexOf);
//            LogUtil.log("VideoExo2Player => getBufferedHlsSpanInfo => baseUrl = " + baseUrl);
            //
            for (HlsMediaPlaylist.Segment segment : mediaPlaylist.segments) {


                String segmentUrl = baseUrl + PlayerType.MarkType.SEPARATOR + segment.url;
//                LogUtil.log("VideoExo2Player => getBufferedHlsSpanInfo => segment.title = " + segment.title+", segment.url = "+segment.url);
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
            LogUtil.log("VideoExo2Player => getSegments => Exception " + e.getMessage());
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
                if (renderer instanceof lib.kalu.exoplayer2.subtitle.OffsetMsTextRenderer) {
                    ((lib.kalu.exoplayer2.subtitle.OffsetMsTextRenderer) renderer).appendOffsetMs(offsetMs);
                    onUpdateSubtitle(PlayerType.KernelType.EXO_V2, "");
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