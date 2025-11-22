package lib.kalu.mediaplayer.core.kernel.video.mediax;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.Surface;

import androidx.annotation.Nullable;
import androidx.media3.common.C;
import androidx.media3.common.Format;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaLibraryInfo;
import androidx.media3.common.MediaMetadata;
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
import androidx.media3.exoplayer.hls.HlsExtractorFactory;
import androidx.media3.exoplayer.hls.HlsManifest;
import androidx.media3.exoplayer.hls.playlist.HlsMediaPlaylist;
import androidx.media3.exoplayer.hls.playlist.HlsPlaylistParserFactory;
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
import androidx.media3.extractor.ts.DefaultTsPayloadReaderFactory;

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
import lib.kalu.mediaplayer.bean.args.PlayerArgs;
import lib.kalu.mediaplayer.bean.args.StartArgs;
import lib.kalu.mediaplayer.bean.args.UrlArgs;
import lib.kalu.mediaplayer.bean.cache.Cache;
import lib.kalu.mediaplayer.bean.info.HlsSpanInfo;
import lib.kalu.mediaplayer.bean.info.TrackInfo;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.VideoBasePlayer;
import lib.kalu.mediaplayer.core.kernel.video.mediax.hls.CustomDefaultHlsExtractorFactory;
import lib.kalu.mediaplayer.core.kernel.video.mediax.hls.CustomDefaultHttpDataSource;
import lib.kalu.mediaplayer.core.kernel.video.mediax.hls.CustomHlsPlaylistParserFactory;
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
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => releaseDecoder => completed");
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => releaseDecoder => " + e.getMessage());
            }
        }
    }

    @Override
    public void createDecoder(Context context, StartArgs args) {
        try {
            if (null != mExoPlayer)
                throw new Exception("warning: null != mExoPlayer");
            if (null == args)
                throw new Exception("error: args null");
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => createDecoder =>");
            }
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
                            .setBufferDurationsMs(10_0000, 10_0000, 1000, 5000)
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
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => createDecoder => decoderType = " + decoderType);
            }
            // only_ffmpeg
            if (decoderType == PlayerType.DecoderType.ONLY_FFMPEG) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => createDecoder => only_ffmpeg");
                }
                Class<?> clazz = Class.forName("lib.kalu.mediax.renderers.VideoFFmpegAudioFFmpegRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_codec
            else if (decoderType == PlayerType.DecoderType.ONLY_CODEC) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => createDecoder => only_codec");
                }
                Class<?> clazz = Class.forName("lib.kalu.mediax.renderers.VideoCodecAudioCodecRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // video_codec_audio_ffmpeg
            else if (decoderType == PlayerType.DecoderType.ONLY_VIDEO_CODEC_AUDIO_FFMPEG) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => createDecoder => only_video_codec_audio_ffmpeg");
                }
                Class<?> clazz = Class.forName("lib.kalu.mediax.renderers.VideoCodecAudioFFmpegRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_video_ffmpeg_audio_codec
            else if (decoderType == PlayerType.DecoderType.ONLY_VIDEO_FFMPEG_AUDIO_CODEC) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => createDecoder => only_video_ffmpeg_audio_codec");
                }
                Class<?> clazz = Class.forName("lib.kalu.mediax.renderers.VideoFFmpegAudioCodecRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_audio_ffmpeg
            else if (decoderType == PlayerType.DecoderType.ONLY_AUDIO_FFMPEG) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => createDecoder => only_audio_ffmpeg");
                }
                Class<?> clazz = Class.forName("lib.kalu.mediax.renderers.OnlyAudioFFmpegRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_video_ffmpeg
            else if (decoderType == PlayerType.DecoderType.ONLY_VIDEO_FFMPEG) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => createDecoder => only_video_ffmpeg");
                }
                Class<?> clazz = Class.forName("lib.kalu.mediax.renderers.OnlyVideoFFmpegRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_audio_codec
            else if (decoderType == PlayerType.DecoderType.ONLY_AUDIO_CODEC) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => createDecoder => only_audio_codec");
                }
                Class<?> clazz = Class.forName("lib.kalu.mediax.renderers.OnlyAudioCodecRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_video_codec
            else if (decoderType == PlayerType.DecoderType.ONLY_VIDEO_CODEC) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => createDecoder => only_video_codec");
                }
                Class<?> clazz = Class.forName("lib.kalu.mediax.renderers.OnlyVideoCodecRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // all
            else {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => createDecoder => only_video_codec");
                }
                Class<?> clazz = Class.forName("lib.kalu.mediax.renderers.BaseRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }

            mExoPlayer = builder.build();
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => createDecoder => mExoPlayer = " + mExoPlayer);
            }
            registListener();

            onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.INIT);
            long trySeeDuration = args.getTrySeeDuration();
            if (trySeeDuration > 0L) {
                onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.TRY_SEE_START);
            }

            //播放器日志
//        if (mExoPlayer.getTrackSelector() instanceof MappingTrackSelector) {
//            mExoPlayer.addAnalyticsListener(new EventLogger((MappingTrackSelector) mExoPlayer.getTrackSelector(), "ExoPlayer"));
//        }
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => createDecoder => completed");
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => createDecoder => " + e.getMessage());
            }
        }
    }

    @Override
    public void startDecoder(Context context, StartArgs args) {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            if (null == args)
                throw new Exception("error: args null");
            boolean containsMainUrl = args.containsMainUrl();
            if (!containsMainUrl)
                throw new Exception("error: containsMainUrl false");
            onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.INIT_READY);
            boolean initUseCache = initUseCache(context, args);
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => startDecoder => initUseCache = " + initUseCache);
            }
            MediaSource mediaSource = formatMediaSource(context, args);
            mExoPlayer.setMediaSource(mediaSource);
            boolean prepareAsync = args.isPrepareAsync();
            if (prepareAsync) {
                mExoPlayer.prepare();
            } else {
                mExoPlayer.prepare();
            }
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => startDecoder => completed");
            }
        } catch (Exception e) {
            onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.ERROR_BUILD_SOURCE);
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => startDecoder => Exception " + e.getMessage());
            }
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
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => initOptions => Exception step1 " + e.getMessage());
            }
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
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => initOptions => Exception step2 " + e.getMessage());
            }
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
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => initOptions => Exception step3 " + e.getMessage());
            }
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
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => setSurface => completed");
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => setSurface => " + e.getMessage());
            }
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
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => isPlaying => " + e.getMessage());
            }
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
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => seekTo =>");
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => seekTo => " + e.getMessage());
            }
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
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => getPosition => " + e.getMessage());
            }
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
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => getDuration => " + e.getMessage());
            }
            return 0L;
        }
    }

    @Override
    public boolean isPrepared() {
        return isPrepared;
    }

    @Override
    public boolean isUseCache() {
        return null != mSimpleCache;
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
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => setSpeed => " + e.getMessage());
            }
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
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => setVolume => " + e.getMessage());
            }
        }
    }

    @Override
    public void registListener() {
        try {
            if (null == mExoPlayer)
                throw new Exception("error: mExoPlayer null");
            mExoPlayer.addAnalyticsListener(mAnalyticsListener);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => registListener => Exception " + e.getMessage());
            }
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
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => unRegistListener => Exception " + e.getMessage());
            }
        }
    }

    @Override
    public void release() {
        try {
            if (null != mSimpleCache) {
                mSimpleCache.release();
                mSimpleCache = null;
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
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => release => " + e.getMessage());
            }
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
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => start => " + e.getMessage());
            }
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
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => pause => " + e.getMessage());
            }
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
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => stop => " + e.getMessage());
            }
        }
    }

    /************************/

    private boolean initUseCache(Context context, StartArgs args) {
        try {

            boolean containsMainUrl = args.containsMainUrl();
            if (!containsMainUrl)
                throw new Exception("error: containsMainUrl false");

            String url = args.getUrl();
            if (url.startsWith(PlayerType.SchemeType.FILE))
                throw new Exception("error: url is file");

            PlayerArgs playerBuilder = PlayerSDK.init().getPlayerBuilder();
            if (null == playerBuilder)
                throw new Exception("error: playerBuilder null");

            Cache cache = playerBuilder.getCache();
            if (null == cache)
                throw new Exception("error: cache null");

            boolean cacheEnable = cache.isEnable();
            if (!cacheEnable)
                throw new Exception("error: cacheEnable false");

            int sizeMB = cache.getSizeMB();
            if (sizeMB <= 0)
                throw new Exception("error: sizeMB <= 0, sizeMB = " + sizeMB);

//            if (null == mSimpleCache) {

//            StringBuilder builder = new StringBuilder();
//            builder.append(cache.getDir(PlayerType.KernelType.MEDIA_V3));
//            if (urlType == PlayerType.UrlType.VIDEO) {
//                builder.append("video");
//            } else if (urlType == PlayerType.UrlType.AUDIO) {
//                builder.append("audio");
//            } else if (urlType == PlayerType.UrlType.SUBTITLE) {
//                builder.append("subtitle");
//            } else {
//                builder.append("other");
//            }

            String dirName = cache.getDir(PlayerType.KernelType.MEDIA_V3);
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => initUseCache => dirName = " + dirName + ", url = " + url);
            }

            boolean external = cache.isExternal();
            File dirFile;
            if (external) {
                dirFile = new File(context.getExternalCacheDir(), dirName);
            } else {
                dirFile = new File(context.getCacheDir(), dirName);
            }

            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }

            if (null != mSimpleCache) {
                mSimpleCache.release();
                mSimpleCache = null;
            }

            mSimpleCache = new SimpleCache(dirFile,
                    //
                    new LeastRecentlyUsedCacheEvictor(sizeMB),
                    //
                    new StandaloneDatabaseProvider(context)
            );
            mSimpleCache.addListener("", new androidx.media3.datasource.cache.Cache.Listener() {
                @Override
                public void onSpanAdded(androidx.media3.datasource.cache.Cache cache, CacheSpan cacheSpan) {
                }

                @Override
                public void onSpanRemoved(androidx.media3.datasource.cache.Cache cache, CacheSpan cacheSpan) {
                }

                @Override
                public void onSpanTouched(androidx.media3.datasource.cache.Cache cache, CacheSpan cacheSpan, CacheSpan cacheSpan1) {
                }
            });
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => initUseCache => useCache completed");
            }
            return true;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => initUseCache => Exception: " + e.getMessage());
            }
            return false;
        }
    }

    private MediaSource formatMediaSource(Context context, StartArgs args) throws Exception {

        try {

            boolean containsMainUrl = args.containsMainUrl();
            if (!containsMainUrl)
                throw new Exception("error: containsMainUrl false");

            boolean containsExtUrl = args.containsExtUrl();
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => formatMediaSource => containsExtUrl = " + containsExtUrl);
            }

            UrlArgs urlArgs = args.getUrlArgs();
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => formatMediaSource => urlArgs = " + urlArgs);
            }

            UrlArgs.Item mainVideo = urlArgs.getMainVideo();
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => formatMediaSource => mainVideo = " + mainVideo);
            }

            // 有 外挂轨道
            if (containsExtUrl) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => formatMediaSource => 外挂轨道 有");
                }

                int urlCount = urlArgs.getUrlCount();
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => formatMediaSource => urlCount = " + urlCount);
                }

                int index = -1;
                MediaSource[] mediaSources = new MediaSource[urlCount];

                // mainUrl
                mediaSources[++index] = buildVideoMediaSource(context, args, mainVideo);

                // extVideo
                UrlArgs.Item[] extVideo = urlArgs.getExtVideo();
                if (null != extVideo) {
                    for (UrlArgs.Item videoArgs : extVideo) {
                        if (LogUtil.DEBUG) {
                            LogUtil.log("VideoMediaxPlayer => formatMediaSource => 外挂视频轨道: videoArgs = " + videoArgs);
                        }
                        MediaSource mediaSource = buildVideoMediaSource(context, args, videoArgs);
                        if (LogUtil.DEBUG) {
                            LogUtil.log("VideoMediaxPlayer => formatMediaSource => 外挂视频轨道: mediaSource = " + mediaSource);
                        }
                        mediaSources[++index] = mediaSource;
                    }
                }

                // extAudioUrl
                UrlArgs.Item[] extAudio = urlArgs.getExtAudio();
                if (null != extAudio) {
                    for (UrlArgs.Item audioArgs : extAudio) {
                        if (LogUtil.DEBUG) {
                            LogUtil.log("VideoMediaxPlayer => formatMediaSource => 外挂音频轨道: audioArgs = " + audioArgs);
                        }
                        MediaSource mediaSource = buildAudioMediaSource(context, args, audioArgs);
                        if (LogUtil.DEBUG) {
                            LogUtil.log("VideoMediaxPlayer => formatMediaSource => 外挂音频轨道: mediaSource = " + mediaSource);
                        }
                        if (null == mediaSource)
                            continue;
                        mediaSources[++index] = mediaSource;
                    }
                }

                // extSubtitleUrl
                UrlArgs.Item[] extSubtitle = urlArgs.getExtSubtitle();
                if (null != extSubtitle) {
                    for (UrlArgs.Item item : extSubtitle) {
                        if (LogUtil.DEBUG) {
                            LogUtil.log("VideoMediaxPlayer => formatMediaSource => 外挂字幕轨道: subtitle = " + item);
                        }
                        MediaSource mediaSource = buildSubtitleMediaSource(context, args, item);
                        if (LogUtil.DEBUG) {
                            LogUtil.log("VideoMediaxPlayer => formatMediaSource => 外挂字幕轨道: mediaSource = " + mediaSource);
                        }
                        if (null == mediaSource)
                            continue;
                        mediaSources[++index] = mediaSource;
                    }
                }

                return new MergingMediaSource(mediaSources);
            }
            // 无 外挂轨道
            else {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => formatMediaSource => 外挂轨道 无");
                }
                return buildVideoMediaSource(context, args, mainVideo);
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => formatMediaSource => Exception: " + e.getMessage());
            }
            throw e;
        }
    }

    @PlayerType.MetaType.Value
    private int formatMetaType(String url) {
        try {
            // rtmp
            if (url.startsWith(PlayerType.SchemeType.RTMP)) {
                return PlayerType.MetaType.VIDEO_RTMP;
            }
            // rtsp
            else if (url.startsWith(PlayerType.SchemeType.RTSP)) {
                return PlayerType.MetaType.VIDEO_RTSP;
            }
            // mp4
            else if (url.endsWith(PlayerType.SchemeType._MP4)) {
                return PlayerType.MetaType.VIDEO_MP4;
            }
            // dash
            else if (url.endsWith(PlayerType.SchemeType._MPD)) {
                return PlayerType.MetaType.VIDEO_DASH;
            }
            // hls
            else if (url.endsWith(PlayerType.SchemeType._M3U)) {
                return PlayerType.MetaType.VIDEO_HLS;
            }
            // hls
            else if (url.endsWith(PlayerType.SchemeType._M3U8)) {
                return PlayerType.MetaType.VIDEO_HLS;
            }
            // SmoothStreaming
            else if (url.matches(PlayerType.SchemeType._MATCHES)) {
                return PlayerType.MetaType.VIDEO_SS;
            }
            // other
            else {
                return PlayerType.MetaType.VIDEO_OTHER;
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => formatMetaType => Exception: " + e.getMessage());
            }
            return PlayerType.MetaType.VIDEO_OTHER;
        }
    }

    private MediaSource buildVideoMediaSource(Context context, StartArgs args,
                                              UrlArgs.Item item) {

        try {

            if (null == item)
                throw new Exception("erro: item null");

            String url = item.getUrl();
            String language = item.getLanguage();
            int metaType = formatMetaType(url);
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => buildVideoMediaSource => metaType = " + metaType + ", language = " + language + ", url = " + url);
            }

            int hashCode = url.hashCode();
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => buildVideoMediaSource => hashCode = " + hashCode + ", dataUrl = " + url);
            }

            // rtmp
            if (metaType == PlayerType.MetaType.VIDEO_RTMP) {
                Object factory = buildVideoMediaFactory(context, args, metaType, item);
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => buildVideoMediaSource => rtmp, dataUrl = " + url);
                }
                return new ProgressiveMediaSource.Factory(((DataSource.Factory) factory)).createMediaSource(new MediaItem.Builder()
                        .setUri(Uri.parse(url))
                        .setMediaId("video:" + hashCode)
                        .build());
            }
            // rtsp
            else if (metaType == PlayerType.MetaType.VIDEO_RTSP) {
                Object factory = buildVideoMediaFactory(context, args, metaType, item);
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => buildVideoMediaSource => rtsp, dataUrl = " + url);
                }
                return ((MediaSource.Factory) factory).createMediaSource(new MediaItem.Builder()
                        .setUri(Uri.parse(url))
                        .setMediaId("video:" + hashCode)
                        .build());
            }
            // mp4
            else if (metaType == PlayerType.MetaType.VIDEO_MP4) {
                Object factory = buildVideoMediaFactory(context, args, metaType, item);
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => buildVideoMediaSource => mp4, dataUrl = " + url);
                }

                return new ProgressiveMediaSource.Factory(((DataSource.Factory) factory)).createMediaSource(new MediaItem.Builder()
                        .setUri(Uri.parse(url))
                        .setMediaId("video:" + hashCode)
                        .build());
            }
            // dash
            else if (metaType == PlayerType.MetaType.VIDEO_DASH) {
                Object factory = buildVideoMediaFactory(context, args, metaType, item);
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => buildVideoMediaSource => dash, dataUrl = " + url);
                }
                return ((MediaSource.Factory) factory).createMediaSource(new MediaItem.Builder()
                        .setUri(Uri.parse(url))
                        .setMediaId("video:" + hashCode)
                        .build());
            }
            // hls
            else if (metaType == PlayerType.MetaType.VIDEO_HLS) {
                Object factory = buildVideoMediaFactory(context, args, metaType, item);
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => buildVideoMediaSource => hls, dataUrl = " + url);
                }
                return ((MediaSource.Factory) factory).createMediaSource(new MediaItem.Builder()
                        .setUri(Uri.parse(url))
                        .setMediaId("video:" + hashCode)
                        .build());
            }
            // SmoothStreaming
            else if (metaType == PlayerType.MetaType.VIDEO_SS) {
                Object factory = buildVideoMediaFactory(context, args, metaType, item);
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => buildVideoMediaSource => SmoothStreaming, dataUrl = " + url);
                }
                return ((MediaSource.Factory) factory).createMediaSource(new MediaItem.Builder()
                        .setUri(Uri.parse(url))
                        .setMediaId("video:" + hashCode)
                        .build());
            }
            // other
            else {
                Object factory = buildVideoMediaFactory(context, args, metaType, item);
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => buildVideoMediaSource => other, dataUrl = " + url);
                }
                return ((DefaultMediaSourceFactory) factory).createMediaSource(new MediaItem.Builder()
                        .setUri(Uri.parse(url))
                        .setMediaId("video:" + hashCode)
                        .build());
            }

        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => buildVideoMediaSource => Exception: " + e.getMessage());
            }
            return null;
        }
    }

    private Object buildVideoMediaFactory(Context context, StartArgs args,
                                          @PlayerType.MetaType.Value int metaType,
                                          UrlArgs.Item item) {

        try {

            String url = item.getUrl();

            // rtmp
            if (metaType == PlayerType.MetaType.VIDEO_RTMP) {
                Class<?> cls = Class.forName("ext.rtmp.RtmpDataSource");
                DataSource.Factory factory = (DataSource.Factory) cls.newInstance();
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => buildVideoMediaFactory => rtmp, dataUrl = " + url);
                }
                return factory;
            }
            // rtsp
            else if (metaType == PlayerType.MetaType.VIDEO_RTSP) {
                Class<?> cls = Class.forName("androidx.media3.exoplayer.rtsp.RtspMediaSource$Factory");
                Constructor<?> constructor = cls.getDeclaredConstructor(DataSource.Factory.class);
                constructor.setAccessible(true);
                DataSource.Factory factory = buildDateFactory(context, args, PlayerType.UrlType.VIDEO, url);
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => buildVideoMediaFactory => rtsp, dataUrl = " + url);
                }
                return constructor.newInstance(factory);
            }
            // mp4
            else if (metaType == PlayerType.MetaType.VIDEO_MP4) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => buildVideoMediaFactory => mp4, dataUrl = " + url);
                }
                return buildDateFactory(context, args, PlayerType.UrlType.VIDEO, url);
            }
            // dash
            else if (metaType == PlayerType.MetaType.VIDEO_DASH) {
                Class<?> cls = Class.forName("androidx.media3.exoplayer.dash.DashMediaSource$Factory");
                Constructor<?> constructor = cls.getDeclaredConstructor(DataSource.Factory.class);
                constructor.setAccessible(true);
                DataSource.Factory factory = buildDateFactory(context, args, PlayerType.UrlType.VIDEO, url);
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => buildVideoMediaFactory => dash, dataUrl = " + url);
                }
                return constructor.newInstance(factory);
            }
            // hls
            else if (metaType == PlayerType.MetaType.VIDEO_HLS) {
                Class<?> cls = Class.forName("androidx.media3.exoplayer.hls.HlsMediaSource$Factory");
                Constructor<?> constructor = cls.getDeclaredConstructor(DataSource.Factory.class);
                constructor.setAccessible(true);
                DataSource.Factory factory = buildDateFactory(context, args, PlayerType.UrlType.VIDEO, url);
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => buildVideoMediaFactory => hls, dataUrl = " + url);
                }
                Object object = constructor.newInstance(factory);

                // setPlaylistParserFactory
                Method method_setPlaylistParserFactory = cls.getMethod("setPlaylistParserFactory", HlsPlaylistParserFactory.class);
                method_setPlaylistParserFactory.invoke(object, new CustomHlsPlaylistParserFactory());

                // setExtractorFactory
                boolean onlyParserVideo = item.isOnlyParserVideo();
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => buildVideoMediaFactory => hls, onlyParserVideo = " + onlyParserVideo);
                }
                if (onlyParserVideo) {
                    Method method_setExtractorFactory = cls.getMethod("setExtractorFactory", HlsExtractorFactory.class);
                    method_setExtractorFactory.invoke(object, new CustomDefaultHlsExtractorFactory(DefaultTsPayloadReaderFactory.FLAG_IGNORE_AAC_STREAM, false));
                }

                return object;
            }
            // SmoothStreaming
            else if (metaType == PlayerType.MetaType.VIDEO_SS) {
                Class<?> cls = Class.forName("androidx.media3.exoplayer.smoothstreaming.SsMediaSource$Factory");
                Constructor<?> constructor = cls.getDeclaredConstructor(DataSource.Factory.class);
                constructor.setAccessible(true);
                DataSource.Factory factory = buildDateFactory(context, args, PlayerType.UrlType.VIDEO, url);
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => buildVideoMediaFactory => SmoothStreaming, dataUrl = " + url);
                }
                return constructor.newInstance(factory);
            }
            // other
            else {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => buildVideoMediaFactory => other, dataUrl = " + url);
                }
                return buildDateFactory(context, args, PlayerType.UrlType.VIDEO, url);
            }

        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => buildVideoMediaFactory => Exception: " + e.getMessage());
            }
            return null;
        }
    }

    private MediaSource buildAudioMediaSource(Context context, StartArgs args,
                                              UrlArgs.Item item) {

        try {

            if (null == item)
                throw new Exception("erro: item null");

            String url = item.getUrl();
            int metaType = formatMetaType(url);
            if (LogUtil.DEBUG) {
                String language = item.getLanguage();
                LogUtil.log("VideoMediaxPlayer => buildAudioMediaSource => metaType = " + metaType + ", language = " + language + ", url = " + url);
            }

            int hashCode = url.hashCode();
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => buildAudioMediaSource => hashCode = " + hashCode + ", dataUrl = " + url);
            }

            // hls
            if (metaType == PlayerType.MetaType.VIDEO_HLS) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => buildAudioMediaSource => hls, dataUrl = " + url);
                }
                Object factory = buildAudioMediaFactory(context, args, item);
                return ((MediaSource.Factory) factory).createMediaSource(new MediaItem.Builder()
                        .setUri(Uri.parse(url))
                        .setMediaId("audio:" + hashCode)
                        .setMediaMetadata(new MediaMetadata.Builder()
                                .setExtras(Bundle.EMPTY)
                                .setTitle("歌曲标题")
                                .setArtist("艺术家名称")
                                .setAlbumTitle("专辑名称")
                                .setArtworkUri(Uri.parse("https://example.com/cover.jpg")) // 封面图片
                                .setReleaseYear(2024)
                                .build())
                        .build());
            }
            // other
            else {
                Object factory = buildAudioMediaFactory(context, args, item);
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => buildAudioMediaSource => factory = " + factory);
                }
                if (null == factory)
                    throw new Exception("error: factory null");

                if (factory instanceof CacheDataSource.Factory) {
                    return new DefaultMediaSourceFactory((CacheDataSource.Factory) factory)
                            .createMediaSource(new MediaItem.Builder()
                                    .setUri(Uri.parse(url))
                                    .setMediaId("audio:" + hashCode)
                                    .build());
                } else {
                    return new DefaultMediaSourceFactory((DataSource.Factory) factory)
                            .createMediaSource(new MediaItem.Builder()
                                    .setUri(Uri.parse(url))
                                    .setMediaId("audio:" + hashCode)
                                    .build());
                }
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => buildAudioMediaSource => Exception: " + e.getMessage());
            }
            return null;
        }
    }

    private Object buildAudioMediaFactory(Context context, StartArgs args,
                                          UrlArgs.Item item) {

        try {

            String url = item.getUrl();
            int metaType = formatMetaType(url);
            if (LogUtil.DEBUG) {
                String language = item.getLanguage();
                LogUtil.log("VideoMediaxPlayer => buildAudioMediaFactory => metaType = " + metaType + ", language = " + language + ", url = " + url);
            }

            // hls
            if (metaType != PlayerType.MetaType.VIDEO_HLS) {
                return buildDateFactory(context, args, PlayerType.UrlType.AUDIO, url);
            } else {
                Class<?> cls = Class.forName("androidx.media3.exoplayer.hls.HlsMediaSource$Factory");
                Constructor<?> constructor = cls.getDeclaredConstructor(DataSource.Factory.class);
                constructor.setAccessible(true);
                DataSource.Factory factory = buildDateFactory(context, args, PlayerType.UrlType.AUDIO, url);
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => buildAudioMediaFactory => hls, dataUrl = " + url);
                }
                Object object = constructor.newInstance(factory);

                // setPlaylistParserFactory
                Method method_setPlaylistParserFactory = cls.getMethod("setPlaylistParserFactory", HlsPlaylistParserFactory.class);
                method_setPlaylistParserFactory.invoke(object, new CustomHlsPlaylistParserFactory());

                // setExtractorFactory
                Method method_setExtractorFactory = cls.getMethod("setExtractorFactory", HlsExtractorFactory.class);
                method_setExtractorFactory.invoke(object, new CustomDefaultHlsExtractorFactory(DefaultTsPayloadReaderFactory.FLAG_IGNORE_H264_STREAM, false));

                return object;
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => buildAudioMediaFactory => Exception: " + e.getMessage());
            }
            return null;
        }
    }

    private MediaSource buildSubtitleMediaSource(Context context, StartArgs args,
                                                 UrlArgs.Item subtitleArgs) {

        try {

            if (null == subtitleArgs)
                throw new Exception("error: subtitleArgs null");
            String sutitleUrl = subtitleArgs.getUrl();
            if (null == sutitleUrl)
                throw new Exception("error: sutitleUrl null");
            if (sutitleUrl.isEmpty())
                throw new Exception("error: sutitleUrl isEmpty");
            String language = subtitleArgs.getLanguage();
            if (null == language)
                throw new Exception("error: language null");
            if (language.isEmpty())
                throw new Exception("error: language isEmpty");
            String label = subtitleArgs.getLabel();
            if (null == label) {
                label = language;
            }

            String mimeType = null;
            if (sutitleUrl.endsWith(PlayerType.SchemeType._VTT)) {
                mimeType = PlayerType.TrackType.TEXT_VTT;
            } else if (sutitleUrl.endsWith(PlayerType.SchemeType._SSA)) {
                mimeType = PlayerType.TrackType.TEXT_SSA;
            } else if (sutitleUrl.endsWith(PlayerType.SchemeType._ASS)) {
                mimeType = PlayerType.TrackType.TEXT_ASS;
            }

            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => buildSubtitleMediaSource => mimeType = " + mimeType + ", sutitleUrl = " + sutitleUrl);
            }
            if (null == mimeType)
                throw new Exception("error: mimeType null");

            Object factory = buildDateFactory(context, args, PlayerType.UrlType.SUBTITLE, sutitleUrl);
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => buildSubtitleMediaSource => factory = " + factory);
            }

            int hashCode = sutitleUrl.hashCode();
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => buildSubtitleMediaSource => hashCode = " + hashCode + ", sutitleUrl = " + sutitleUrl);
            }
            MediaItem.SubtitleConfiguration subtitleConfig = new MediaItem.SubtitleConfiguration.Builder(Uri.parse(sutitleUrl))
                    .setSelectionFlags(C.SELECTION_FLAG_AUTOSELECT)
                    .setMimeType(mimeType) // 也可以用 MimeTypes.APPLICATION_SUBRIP
                    .setLanguage(language)
                    .setLabel(label)
                    .setRoleFlags(hashCode)
                    .setId("subtitle:" + hashCode)
                    .setSelectionFlags(hashCode)
                    .build();

//                      .setSubtitleMediaSourceFactory(
//                            SingleSampleMediaSource.Factory(defaultDataSourceFactory) // 字幕用非缓存数据源
//                    )
            //

            if (factory instanceof CacheDataSource.Factory) {
                return new SingleSampleMediaSource.Factory((CacheDataSource.Factory) factory)
                        .createMediaSource(subtitleConfig, C.TIME_UNSET);
            } else {
                return new SingleSampleMediaSource.Factory((DataSource.Factory) factory)
                        .createMediaSource(subtitleConfig, C.TIME_UNSET);
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => buildSubtitleMediaSource => Exception: " + e.getMessage());
            }
            return null;
        }
    }

    private DataSource.Factory buildDateFactory(Context context, StartArgs args,
                                                @PlayerType.UrlType.Value int urlType,
                                                String dataUrl) {

        try {
            return new CacheDataSource.Factory()
                    .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
//                        .setFlags(
//                                CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR | // 错误时跳过缓存
//                                        CacheDataSource.FLAG_IGNORE_CACHE_FOR_UNSET_LENGTH_REQUESTS // 允许缓存未知长度的资源（如直播流）
//                        )
                    .setCache(mSimpleCache)
                    // 网络请求工厂
                    .setUpstreamDataSourceFactory(buildHttpFactory(args))
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

                            if (LogUtil.DEBUG) {
                                LogUtil.log("VideoMediaxPlayer => buildDateFactory => subUrl = " + subUrl);
                            }

                            if (subUrl.endsWith(PlayerType.SchemeType._M3U8)) {
                                return subUrl;
                            } else if (subUrl.endsWith(PlayerType.SchemeType._TS)) {
                                return subUrl;
                            } else {
                                return dataUrl;
                            }
                        }
                    });
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => buildDateFactory => Exception: " + e.getMessage());
            }
            return new DefaultDataSource.Factory(context, buildHttpFactory(args));
        }
    }

    private DataSource.Factory buildHttpFactory(StartArgs args) {
        try {
            return new CustomDefaultHttpDataSource.Factory()
                    .setUserAgent(MediaLibraryInfo.VERSION_SLASHY)
                    .setConnectTimeoutMs((int) args.getConnectTimout())
                    .setReadTimeoutMs((int) args.getConnectTimout())
                    .setAllowCrossProtocolRedirects(true)
                    .setKeepPostFor302Redirects(true);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => buildHttpFactory => Exception: " + e.getMessage());
            }
            return null;
        }
    }

    private final AnalyticsListener mAnalyticsListener = new AnalyticsListener() {

        @Override
        public void onTimelineChanged(AnalyticsListener.EventTime eventTime, int i) {
            Object manifest = mExoPlayer.getCurrentManifest();
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => onTimelineChanged => manifest = " + manifest);
            }
            if (manifest instanceof HlsManifest) {
                mHlsManifest = (HlsManifest) manifest;
            }
        }

        @Override
        public void onPlayerErrorChanged(EventTime eventTime, @Nullable PlaybackException e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => onPlayerErrorChanged => message = " + e.getMessage(), e);
            }
        }

        @Override
        public void onPlayWhenReadyChanged(AnalyticsListener.EventTime eventTime, boolean playWhenReady, int reason) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => onPlayWhenReadyChanged => playWhenReady = " + playWhenReady + ", reason = " + reason);
            }
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
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => onPlayerError => error = " + error.getMessage());
                }
            }
        }

        public void onEvents(Player player, AnalyticsListener.Events events) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => onEvents => isPlaying = " + player.isPlaying());
            }
        }

        @Override
        public void onVideoSizeChanged(AnalyticsListener.EventTime eventTime, VideoSize videoSize) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => onVideoSizeChanged => width = " + videoSize.width + ", height = " + videoSize.height);
            }
        }

        @Override
        public void onIsPlayingChanged(AnalyticsListener.EventTime eventTime, boolean isPlaying) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => onIsPlayingChanged => isPlaying = " + isPlaying);
            }
        }

        @Override
        public void onLoadError(EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData, IOException e, boolean b) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => onLoadError =>");
            }
            stop();
            onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.STOP);
            onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.ERROR);
        }

        @Override
        public void onLoadCompleted(EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => onLoadCompleted => mediaLoadData.trackFormat = " + mediaLoadData.trackFormat);
            }
        }

        @Override
        public void onPlaybackStateChanged(AnalyticsListener.EventTime eventTime, int state) {

            // 播放错误
            if (state == Player.STATE_IDLE) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => onPlaybackStateChanged -> state[Player.STATE_IDLE] = " + state);
                }
            }
            // 播放完成
            else if (state == Player.STATE_ENDED) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => onPlaybackStateChanged -> state[Player.STATE_ENDED] = " + state);
                }
                onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.COMPLETE);
            }
            // 播放开始
            else if (state == Player.STATE_READY) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => onPlaybackStateChanged -> state[Player.STATE_READY] = " + state);
                }
                try {
                    if (!isPrepared)
                        throw new Exception("warning: isPrepared false");

                    // buffering
                    if (isBuffering) {
                        if (LogUtil.DEBUG) {
                            LogUtil.log("VideoMediaxPlayer => onPlaybackStateChanged -> state[Player.STATE_READY] -> buffering");
                        }
                        isBuffering = false;
                        onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.BUFFERING_STOP);
                    }
                    // seeking
                    else if (mSeeking) {
                        if (LogUtil.DEBUG) {
                            LogUtil.log("VideoMediaxPlayer => onPlaybackStateChanged -> state[Player.STATE_READY] -> seeking");
                        }
                        mSeeking = false;
                        onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.SEEK_FINISH);

                        // 起播快进
                        if (mPlayWhenReadySeeking) {
                            mPlayWhenReadySeeking = false;
                            // 立即播放
                            boolean playWhenReady = isPlayWhenReady();
                            onEvent(PlayerType.KernelType.MEDIA_V3, playWhenReady ? PlayerType.EventType.START_PLAY_WHEN_READY_TRUE : PlayerType.EventType.START_PLAY_WHEN_READY_FALSE);
                            if (playWhenReady) {
                                boolean playing = isPlaying();
                                if (playing)
                                    throw new Exception("warning: isPlaying true");
                                onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.START);
                                onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.VIDEO_RENDERING_START);
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
                        if (LogUtil.DEBUG) {
                            LogUtil.log("VideoMediaxPlayer => onPlaybackStateChanged -> state[Player.STATE_READY] -> start ready");
                        }
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
                    if (LogUtil.DEBUG) {
                        LogUtil.log("VideoMediaxPlayer => onPlaybackStateChanged -> state[Player.STATE_READY] -> Exception " + e.getMessage());
                    }
                }
            }
            // 播放缓冲
            else if (state == Player.STATE_BUFFERING) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => onPlaybackStateChanged -> state[Player.STATE_BUFFERING] = " + state);
                }
                try {
                    if (!isPrepared)
                        throw new Exception("mPrepared warning: false");
                    isBuffering = true;
                    onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.BUFFERING_START);
                } catch (Exception e) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log("VideoMediaxPlayer => onPlaybackStateChanged -> state[Player.STATE_BUFFERING] -> Exception " + state);
                    }
                }
            }
            // ????
            else {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => onPlaybackStateChanged -> state[????] = " + state);
                }
            }
        }

        @Override
        public void onVideoInputFormatChanged(AnalyticsListener.EventTime eventTime, Format format, @Nullable DecoderReuseEvaluation decoderReuseEvaluation) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => onVideoInputFormatChanged[出画面] => width = " + format.width + ", height = " + format.height);
            }
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
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => onVideoInputFormatChanged => " + e.getMessage());
                }
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
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoMediaxPlayer => onVideoInputFormatChanged => Exception " + e.getMessage());
                }
            }
        }

        @Override
        public void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime, Object output, long renderTimeMs) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => onRenderedFirstFrame =>");
            }
        }

        @Override
        public void onAudioInputFormatChanged(AnalyticsListener.EventTime eventTime, Format format, @Nullable DecoderReuseEvaluation decoderReuseEvaluation) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => onAudioInputFormatChanged =>");
            }
        }

        @Override
        public void onSeekStarted(EventTime eventTime) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => onSeekStarted =>");
            }
        }

        @Override
        public void onSeekBackIncrementChanged(EventTime eventTime, long l) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => onSeekBackIncrementChanged =>");
            }
        }

        @Override
        public void onSeekForwardIncrementChanged(EventTime eventTime, long l) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => onSeekForwardIncrementChanged =>");
            }
        }

        @Override
        public void onCues(EventTime eventTime, CueGroup cueGroup) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => onCues => cueGroup = " + cueGroup);
            }
        }

        @Override
        public void onCues(EventTime eventTime, List<Cue> cues) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => onCues => cues = " + cues);
            }

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
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => onTrackSelectionParametersChanged => trackSelectionParameters = " + trackSelectionParameters);
            }
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
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => onTracksChanged => tracks = " + tracks);
            }

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
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => onSurfaceSizeChanged => i = " + i + ", i1 = " + i1);
            }
        }
    };

    /*********/

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
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => toggleTrack => " + e.getMessage());
            }
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

                if (LogUtil.DEBUG) {
                    TrackGroup trackGroup = group.getMediaTrackGroup();
                    LogUtil.log("VideoMediaxPlayer => getTrackInfo => trackGroup.id = " + trackGroup.id + ", trackGroup.length = " + trackGroup.length);
                }

                int trackType = group.getType();
                int trackCount = group.length;
                // 是否支持自适应播放
                boolean isGroupAdaptiveSupported = group.isAdaptiveSupported();
                boolean isGroupSelected = group.isSelected();
                boolean isGroupSupported = group.isSupported();
                for (int trackIndex = 0; trackIndex < trackCount; trackIndex++) {

                    // 轨道是否支持
                    boolean isTrackSupported = group.isTrackSupported(trackIndex);
                    if (!isTrackSupported)
                        continue;

                    // 轨道是否被选中
                    boolean isTrackSelected = group.isTrackSelected(trackIndex);

                    //
                    Format format = group.getTrackFormat(trackIndex);
                    if (LogUtil.DEBUG) {
                        LogUtil.log("VideoMediaxPlayer => getTrackInfo => format = " + format + ", format.metadata = " + format.metadata);
                    }

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

//                        if(null == format.language && null == format.label)
//                            continue;

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

//                        if(null == format.language && null == format.label)
//                            continue;
                    }
                    // 媒体信息
                    else if (type == -1 && trackType == C.TRACK_TYPE_METADATA) {
                        // LogUtil.log("VideoMediaxPlayer => getTrackInfo[C.TRACK_TYPE_METADATA] => groupCount = " + groupCount + ", groupIndex = " + groupIndex + ", trackCount = " + trackCount + ", trackIndex = " + trackIndex + ", trackType = " + trackType + ", isGroupAdaptiveSupported = " + isGroupAdaptiveSupported + ", isGroupSelected = " + isGroupSelected + ", isGroupSupported = " + isGroupSupported + ", isTrackSelected = " + isTrackSelected + ", isTrackSupported = " + isTrackSupported);
                        continue;
                    }
                    // 未知
                    else {
                        //  LogUtil.log("VideoMediaxPlayer => getTrackInfo[Unknow] => groupCount = " + groupCount + ", groupIndex = " + groupIndex + ", trackCount = " + trackCount + ", trackIndex = " + trackIndex + ", trackType = " + trackType + ", isGroupAdaptiveSupported = " + isGroupAdaptiveSupported + ", isGroupSelected = " + isGroupSelected + ", isGroupSupported = " + isGroupSupported + ", isTrackSelected = " + isTrackSelected + ", isTrackSupported = " + isTrackSupported);
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

                    //   LogUtil.log("VideoMediaxPlayer => getTrackInfo => groupCount = " + groupCount + ", groupIndex = " + groupIndex + ", trackCount = " + trackCount + ", trackIndex = " + trackIndex + ", trackType = " + trackType + ", isGroupAdaptiveSupported = " + isGroupAdaptiveSupported + ", isGroupSelected = " + isGroupSelected + ", isGroupSupported = " + isGroupSupported + ", isTrackSelected = " + isTrackSelected + ", isTrackSupported = " + isTrackSupported + ", isTrackMixed = " + isTrackMixed + ", isTrackMixedSelected = " + isTrackMixedSelected + ", format = " + object);
                    //
                    list.add(trackInfo);
                }
            }

            //
            if (list.isEmpty())
                throw new Exception("error: list empty");

            return list;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => getTrackInfo => Exception " + e.getMessage());
            }
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
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => getBufferedHlsSpanInfo => Exception " + e.getMessage());
            }
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
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoMediaxPlayer => appendSubtitleOffsetMs => Exception " + e.getMessage());
            }
            return false;
        }
    }
}
