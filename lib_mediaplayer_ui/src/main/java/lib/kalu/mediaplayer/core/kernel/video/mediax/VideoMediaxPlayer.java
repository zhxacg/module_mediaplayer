package lib.kalu.mediaplayer.core.kernel.video.mediax;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.C;
import androidx.media3.common.Format;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.PlaybackParameters;
import androidx.media3.common.Player;
import androidx.media3.common.Timeline;
import androidx.media3.common.TrackGroup;
import androidx.media3.common.TrackSelectionOverride;
import androidx.media3.common.TrackSelectionParameters;
import androidx.media3.common.Tracks;
import androidx.media3.common.VideoSize;
import androidx.media3.common.text.Cue;
import androidx.media3.common.text.CueGroup;
import androidx.media3.common.util.Clock;
import androidx.media3.common.util.Util;
import androidx.media3.database.StandaloneDatabaseProvider;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DataSpec;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.FileDataSource;
import androidx.media3.datasource.HttpDataSource;
import androidx.media3.datasource.cache.CacheDataSink;
import androidx.media3.datasource.cache.CacheDataSource;
import androidx.media3.datasource.cache.CacheKeyFactory;
import androidx.media3.datasource.cache.CacheSpan;
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor;
import androidx.media3.datasource.cache.SimpleCache;
import androidx.media3.exoplayer.DecoderReuseEvaluation;
import androidx.media3.exoplayer.DefaultLivePlaybackSpeedControl;
import androidx.media3.exoplayer.DefaultLoadControl;
import androidx.media3.exoplayer.DefaultRenderersFactory;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.Renderer;
import androidx.media3.exoplayer.RenderersFactory;
import androidx.media3.exoplayer.analytics.AnalyticsListener;
import androidx.media3.exoplayer.analytics.DefaultAnalyticsCollector;
import androidx.media3.exoplayer.dash.DashMediaSource;
import androidx.media3.exoplayer.hls.HlsManifest;
import androidx.media3.exoplayer.hls.HlsMediaSource;
import androidx.media3.exoplayer.hls.playlist.HlsMediaPlaylist;
import androidx.media3.exoplayer.smoothstreaming.SsMediaSource;
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
import androidx.media3.exoplayer.upstream.DefaultAllocator;
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter;
import androidx.media3.extractor.ts.DefaultTsPayloadReaderFactory;

import com.google.common.collect.ImmutableList;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lib.kalu.mediaplayer.PlayerSDK;
import lib.kalu.mediaplayer.bean.args.ConfigArgs;
import lib.kalu.mediaplayer.bean.args.StartArgs;
import lib.kalu.mediaplayer.bean.args.UrlArgs;
import lib.kalu.mediaplayer.bean.cache.Cache;
import lib.kalu.mediaplayer.bean.info.HlsSpanInfo;
import lib.kalu.mediaplayer.bean.info.TrackInfo;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.collect.HlsSpanList;
import lib.kalu.mediaplayer.core.kernel.video.VideoBasePlayer;
import lib.kalu.mediaplayer.core.kernel.video.mediax.hls.CustomDefaultHlsExtractorFactory;
import lib.kalu.mediaplayer.core.kernel.video.mediax.hls.CustomDefaultHttpDataSource;
import lib.kalu.mediaplayer.core.kernel.video.mediax.hls.CustomHlsLoadErrorHandlingPolicy;
import lib.kalu.mediaplayer.core.kernel.video.mediax.hls.CustomHlsPlaylistParserFactory;
import lib.kalu.mediaplayer.proxy.ProxyUrl;
import lib.kalu.mediaplayer.util.DisplayRefreshRateUtils;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediax.subtitle.OffsetMsTextRenderer;

public final class VideoMediaxPlayer extends VideoBasePlayer {

    private String TAG = "VideoMediaxPlayer";

    private boolean isPrepared = false;
    private boolean isBuffering = false;
    private boolean mPlayWhenReadySeeking = false;
    private boolean mSeeking = false;

    private SimpleCache mSimpleCache;
    private ExoPlayer mExoPlayer;
    private Handler mHandler;

    // 播放资源信息
    private StartArgs mStartArgs;

    // 缓存
    private HlsSpanList mHlsSpanInfos;

    @Override
    public void setStartArgs(StartArgs args) {
        this.mStartArgs = args;
    }

    @Override
    public StartArgs getStartArgs() {
        return mStartArgs;
    }

    @Override
    public ExoPlayer getPlayer() {
        return mExoPlayer;
    }

    @Override
    public void releaseDecoder() {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            setEvent(null);
            unRegistListener();
            release();
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "releaseDecoder -> completed");
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "releaseDecoder -> " + e.getMessage());
            }
        }
    }

    @Override
    public void checkDecoder(Context context, StartArgs startArgs) {
        try {
            if (null != mExoPlayer) throw new Exception("warning: null != mExoPlayer");
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "checkDecoder ->");
            }

            if (null == startArgs) throw new Exception("error: startArgs null");

            StartArgs.TimeoutConfiguration timeoutConfiguration = startArgs.getTimeoutConfiguration();
            int connectTimeoutMs = timeoutConfiguration.getConnectTimeoutMs();
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "checkDecoder -> connectTimeoutMs = " + connectTimeoutMs);
            }

            ExoPlayer.Builder builder = new ExoPlayer.Builder(context)
                    // 核心：配置缓冲卡死超时（解决你最初的 StuckPlayerException）
                    .setStuckBufferingDetectionTimeoutMs(startArgs.getStuckDetectorMs().getBufferingDetectionTimeoutMs())
                    // 配置播放状态卡死超时（画面/声音静止检测）
                    .setStuckPlayingDetectionTimeoutMs(startArgs.getStuckDetectorMs().getPlayingDetectionTimeoutMs())
                    // 配置播放未结束卡死超时（播放完成异常检测）
                    .setStuckPlayingNotEndingTimeoutMs(startArgs.getStuckDetectorMs().getPlayingNotEndingTimeoutMs())
                    // 配置抑制状态卡死超时（后台播放/焦点丢失检测）
                    .setStuckSuppressedDetectionTimeoutMs(startArgs.getStuckDetectorMs().getSuppressedDetectionTimeoutMs())
                    // 启用懒加载准备
                    .setUseLazyPreparation(true)
                    // 播放器调试和诊断相关的配置项
                    .setUsePlatformDiagnostics(false)
                    // 创建渲染器工厂 开启帧率同步
                    .setRenderersFactory(new DefaultRenderersFactory(context) {
                        @Override
                        protected void buildTextRenderers(Context context, TextOutput textOutput, Looper looper, int i, ArrayList<Renderer> arrayList) {
//                            super.buildTextRenderers(context, textOutput, looper, i, arrayList);
//                            ((TextRenderer) Iterables.getLast(arrayList)).experimentalSetLegacyDecodingEnabled(true);
                            TextRenderer textRenderer = new TextRenderer(textOutput, looper);
                            textRenderer.experimentalSetLegacyDecodingEnabled(true);
                            arrayList.add(textRenderer);
                        }
                    }.setEnableDecoderFallback(true).setAllowedVideoJoiningTimeMs(1000).forceEnableMediaCodecAsynchronousQueueing())
                    // 创建媒体源工厂，开启字幕预解析（核心配置）
                    .setMediaSourceFactory(new DefaultMediaSourceFactory(context)
                            // 实验性配置：在数据提取阶段解析字幕  true = 预解析字幕，false = 播放时解析（默认）
                            .experimentalParseSubtitlesDuringExtraction(true))
                    // 监听
                    .setAnalyticsCollector(new DefaultAnalyticsCollector(Clock.DEFAULT))
                    // 自适应码率
                    .setTrackSelector(new DefaultTrackSelector(context, DefaultTrackSelector.Parameters.getDefaults(context).buildUpon()
                            // 限制最大帧率为设备当前刷新率
                            .setMaxVideoFrameRate((int) DisplayRefreshRateUtils.getCurrentRefreshRate(context))
                            // 关闭非整数倍帧率适配（减少跳帧）
                            .setForceHighestSupportedBitrate(false)
                            // 主字幕轨道
                            .setPreferredTextRoleFlags(C.ROLE_FLAG_MAIN)
                            // 主音频轨道
                            .setPreferredAudioRoleFlags(C.ROLE_FLAG_MAIN)
                            // 主视频轨道
                            .setPreferredVideoRoleFlags(C.ROLE_FLAG_MAIN)
                            // 视频禁止混合 MIME 类型切换（如视频+音频单独切换）
                            .setAllowVideoMixedMimeTypeAdaptiveness(false)
                            // 音频混合时解码器支持自适应
                            .setAllowVideoMixedDecoderSupportAdaptiveness(false)
                            // 视频禁止非无缝切换
                            .setAllowVideoNonSeamlessAdaptiveness(false)
                            // 音频禁止混合 MIME 类型切换（如视频+音频单独切换）
                            .setAllowAudioMixedMimeTypeAdaptiveness(false)
                            // 音频禁止非无缝切换
                            .setAllowAudioNonSeamlessAdaptiveness(false)
                            // 音频混合声道数量的自适应性
                            .setAllowAudioMixedChannelCountAdaptiveness(false)
                            // 音频混合采样率自适应
                            .setAllowAudioMixedSampleRateAdaptiveness(false)
                            // 音频混合时解码器支持自适应
                            .setAllowAudioMixedDecoderSupportAdaptiveness(false).build(), new AdaptiveTrackSelection.Factory(10000,// 至少 10 秒后才允许升码率
                            25000, // 最多 2.5 秒后允许降码率
                            25000, //
                            0.7F)))
                    // 配置带宽测量器
                    .setBandwidthMeter(new DefaultBandwidthMeter.Builder(context)
                            // 初始带宽估算为100Mbps
                            .setInitialBitrateEstimate(100_000_000).build())
                    // 增大内存缓存（默认 2MB，按需调整）
                    .setLoadControl(new DefaultLoadControl.Builder()
                            /**
                             * private int minBufferMs = 50000;
                             *         private int maxBufferMs = 50000;
                             *         private int bufferForPlaybackMs = 1000;
                             *         private int bufferForPlaybackAfterRebufferMs = 2000;
                             */.setBufferDurationsMs(
                                    // minBufferMs：播放器至少要缓冲 1 秒的数据后，才会停止主动加载更多数据；如果缓冲低于这个值，会重新开始加载。
                                    startArgs.getBufferDurationsMs().getMinBufferMs(),
                                    // maxBufferMs：播放器最多缓冲 5 秒的数据，达到这个值后会停止加载，避免占用过多内存。
                                    startArgs.getBufferDurationsMs().getMaxBufferMs(),
                                    // bufferForPlaybackMs：播放器需要至少缓冲 1 秒的数据，才会开始播放（或从暂停恢复播放）。
                                    startArgs.getBufferDurationsMs().getBufferForPlaybackMs(),
                                    // bufferForPlaybackAfterRebufferMs：播放器在缓冲不足导致暂停后，需要重新缓冲 1 秒的数据，才会恢复播放。
                                    startArgs.getBufferDurationsMs().getBufferForPlaybackAfterRebufferMs())
                            // 内存分配器 默认 64 * 1024 = 65536
                            .setAllocator(new DefaultAllocator(true, 64 * 1024)).build())
                    // 直播场景
                    .setLivePlaybackSpeedControl(new DefaultLivePlaybackSpeedControl.Builder()
                            // 兜底最小播放速度：当无法计算动态速度时，使用的保底最小速度（最终 minPlaybackSpeed 会等于该值）
                            .setFallbackMinPlaybackSpeed(startArgs.getLivePlaybackSpeedControl().getFallbackMinPlaybackSpeed())
                            // 兜底最大播放速度：同上，保底最大速度（最终 maxPlaybackSpeed 会等于该值）
                            .setFallbackMaxPlaybackSpeed(startArgs.getLivePlaybackSpeedControl().getFallbackMaxPlaybackSpeed())
                            // 速度更新最小间隔：两次速度调整的最小时间差（避免频繁变速）
                            .setMinUpdateIntervalMs(startArgs.getLivePlaybackSpeedControl().getMinUpdateIntervalMs())
                            // 比例控制因子：速度调整的 “灵敏度”—— 延迟差值越大，速度调整幅度越大（核心算法参数）
                            .setProportionalControlFactor(startArgs.getLivePlaybackSpeedControl().getProportionalControlFactorUs())
                            // 匀速阈值：直播延迟误差小于该值时，使用 1.0f 匀速播放（不调整速度）
                            .setMaxLiveOffsetErrorMsForUnitSpeed(startArgs.getLivePlaybackSpeedControl().getMaxLiveOffsetErrorUsForUnitSpeed())
                            // 缓冲保护阈值：当直播延迟低于「目标延迟 - 该值」时，触发减速，避免缓冲不足导致卡顿
                            .setTargetLiveOffsetIncrementOnRebufferMs(startArgs.getLivePlaybackSpeedControl().getTargetLiveOffsetIncrementOnRebufferUs())
                            // 最小延迟平滑因子：对 “最小可播放延迟” 进行平滑处理的系数（避免延迟波动导致速度频繁变化）
                            .setMinPossibleLiveOffsetSmoothingFactor(startArgs.getLivePlaybackSpeedControl().getMinPossibleLiveOffsetSmoothingFactor()).build());


            int decoderType = startArgs.getDecoderType();
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "checkDecoder -> decoderType = " + decoderType);
            }
            // only_ffmpeg
            if (decoderType == PlayerType.DecoderType.ONLY_FFMPEG) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "checkDecoder -> only_ffmpeg");
                }
                Class<?> clazz = Class.forName("lib.kalu.mediax.renderers.VideoFFmpegAudioFFmpegRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_codec
            else if (decoderType == PlayerType.DecoderType.ONLY_CODEC) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "checkDecoder -> only_codec");
                }
                Class<?> clazz = Class.forName("lib.kalu.mediax.renderers.VideoCodecAudioCodecRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // video_codec_audio_ffmpeg
            else if (decoderType == PlayerType.DecoderType.ONLY_VIDEO_CODEC_AUDIO_FFMPEG) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "checkDecoder -> only_video_codec_audio_ffmpeg");
                }
                Class<?> clazz = Class.forName("lib.kalu.mediax.renderers.VideoCodecAudioFFmpegRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_video_ffmpeg_audio_codec
            else if (decoderType == PlayerType.DecoderType.ONLY_VIDEO_FFMPEG_AUDIO_CODEC) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "checkDecoder -> only_video_ffmpeg_audio_codec");
                }
                Class<?> clazz = Class.forName("lib.kalu.mediax.renderers.VideoFFmpegAudioCodecRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_audio_ffmpeg
            else if (decoderType == PlayerType.DecoderType.ONLY_AUDIO_FFMPEG) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "checkDecoder -> only_audio_ffmpeg");
                }
                Class<?> clazz = Class.forName("lib.kalu.mediax.renderers.OnlyAudioFFmpegRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_video_ffmpeg
            else if (decoderType == PlayerType.DecoderType.ONLY_VIDEO_FFMPEG) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "checkDecoder -> only_video_ffmpeg");
                }
                Class<?> clazz = Class.forName("lib.kalu.mediax.renderers.OnlyVideoFFmpegRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_audio_codec
            else if (decoderType == PlayerType.DecoderType.ONLY_AUDIO_CODEC) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "checkDecoder -> only_audio_codec");
                }
                Class<?> clazz = Class.forName("lib.kalu.mediax.renderers.OnlyAudioCodecRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_video_codec
            else if (decoderType == PlayerType.DecoderType.ONLY_VIDEO_CODEC) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "checkDecoder -> only_video_codec");
                }
                Class<?> clazz = Class.forName("lib.kalu.mediax.renderers.OnlyVideoCodecRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // all
            else {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "checkDecoder -> only_video_codec");
                }
                Class<?> clazz = Class.forName("lib.kalu.mediax.renderers.BaseRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }

            mExoPlayer = builder.build();

            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "checkDecoder -> mExoPlayer = " + mExoPlayer);
            }
            registListener();

            //播放器日志
//        if (mExoPlayer.getTrackSelector() instanceof MappingTrackSelector) {
//            mExoPlayer.addAnalyticsListener(new EventLogger((MappingTrackSelector) mExoPlayer.getTrackSelector(), "ExoPlayer"));
//        }
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "checkDecoder -> completed");
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "checkDecoder -> " + e.getMessage());
            }
        }
    }

    @Override
    public void startDecoder(Context context, StartArgs startArgs) {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.READY);
            // 缓存
            boolean initSimpleCache = initSimpleCache(context, startArgs);
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "startDecoder -> initSimpleCache = " + initSimpleCache);
            }
            StartArgs.TimeoutConfiguration timeoutConfiguration = startArgs.getTimeoutConfiguration();
            int connectTimoutMs = timeoutConfiguration.getConnectTimeoutMs();
            ProxyUrl proxyUrl = startArgs.getProxyUrl();
            boolean noProxy = startArgs.isNoProxy();
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "startDecoder -> connectTimoutMs = " + connectTimoutMs + ", noProxy = " + noProxy + ", proxyUrl = " + proxyUrl);
            }
            // HttpClient
            CustomDefaultHttpDataSource.Factory httpFactory = new CustomDefaultHttpDataSource.Factory(proxyUrl, noProxy).setUserAgent(Util.getUserAgent(context, context.getApplicationInfo().packageName)).setConnectTimeoutMs(connectTimoutMs).setReadTimeoutMs(connectTimoutMs).setDefaultRequestProperties(new HashMap<>()).setAllowCrossProtocolRedirects(true).setKeepPostFor302Redirects(true);
            boolean containsExtUrl = startArgs.containsExtUrl();
            UrlArgs urlArgs = startArgs.getUrlArgs();
            UrlArgs.Item mainVideo = urlArgs.getMainVideo();
            // 有 外挂轨道
            if (containsExtUrl) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "startDecoder -> 外挂轨道 有");
                }

                int urlCount = urlArgs.getUrlCount();
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "startDecoder -> urlCount = " + urlCount);
                }

                int index = -1;
                MediaSource[] mediaSources = new MediaSource[urlCount];

                // mainUrl
                mediaSources[++index] = buildMediaSource(context, httpFactory, startArgs, PlayerType.UrlType.VIDEO, mainVideo);

                // extVideo
                UrlArgs.Item[] extVideo = urlArgs.getExtVideo();
                if (null != extVideo) {
                    for (UrlArgs.Item videoArgs : extVideo) {
                        if (LogUtil.DEBUG) {
                            LogUtil.log(TAG, "startDecoder -> 外挂视频轨道: videoArgs = " + videoArgs);
                        }
                        MediaSource mediaSource = buildMediaSource(context, httpFactory, startArgs, PlayerType.UrlType.VIDEO, videoArgs);
                        if (LogUtil.DEBUG) {
                            LogUtil.log(TAG, "startDecoder -> 外挂视频轨道: mediaSource = " + mediaSource);
                        }
                        mediaSources[++index] = mediaSource;
                    }
                }

                // extAudioUrl
                UrlArgs.Item[] extAudio = urlArgs.getExtAudio();
                if (null != extAudio) {
                    for (UrlArgs.Item audioArgs : extAudio) {
                        if (LogUtil.DEBUG) {
                            LogUtil.log(TAG, "startDecoder -> 外挂音频轨道: audioArgs = " + audioArgs);
                        }
                        MediaSource mediaSource = buildMediaSource(context, httpFactory, startArgs, PlayerType.UrlType.AUDIO, audioArgs);
                        if (LogUtil.DEBUG) {
                            LogUtil.log(TAG, "startDecoder -> 外挂音频轨道: mediaSource = " + mediaSource);
                        }
                        if (null == mediaSource) continue;
                        mediaSources[++index] = mediaSource;
                    }
                }

                // extSubtitleUrl
                UrlArgs.Item[] extSubtitle = urlArgs.getExtSubtitle();
                if (null != extSubtitle) {
                    for (UrlArgs.Item item : extSubtitle) {
                        if (LogUtil.DEBUG) {
                            LogUtil.log(TAG, "startDecoder -> 外挂字幕轨道: subtitle = " + item);
                        }
                        MediaSource mediaSource = buildMediaSource(context, httpFactory, startArgs, PlayerType.UrlType.SUBTITLE, item);
                        if (LogUtil.DEBUG) {
                            LogUtil.log(TAG, "startDecoder -> 外挂字幕轨道: mediaSource = " + mediaSource);
                        }
                        if (null == mediaSource) continue;
                        mediaSources[++index] = mediaSource;
                    }
                }

                MergingMediaSource mergingMediaSource = new MergingMediaSource(mediaSources);
                mExoPlayer.setMediaSource(mergingMediaSource);
            }
            // 无 外挂轨道
            else {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "startDecoder -> 外挂轨道 无");
                }
                MediaSource mediaSource = buildMediaSource(context, httpFactory, startArgs, PlayerType.UrlType.VIDEO, mainVideo);
                mExoPlayer.setMediaSource(mediaSource);
            }

            boolean prepareAsync = startArgs.isPrepareAsync();
            if (prepareAsync) {
                mExoPlayer.prepare();
            } else {
                mExoPlayer.prepare();
            }
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "startDecoder -> completed");
            }
        } catch (Exception e) {
            onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.ERROR_DECODE);
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "startDecoder -> Exception " + e.getMessage());
            }
        }
    }

    @Override
    public void initOptions(Context context, StartArgs args) {

        try {
            if (null == mExoPlayer) throw new Exception("mExoPlayer warning: null");
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
                LogUtil.log(TAG, "initOptions -> Exception step1 " + e.getMessage());
            }
        }

        try {
            if (null == mExoPlayer) throw new Exception("error: mExoPlayer null");
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
                LogUtil.log(TAG, "initOptions -> Exception step2 " + e.getMessage());
            }
        }
    }

    @Override
    public void setSurface(Surface surface, int w, int h) {
        try {
            if (null == mExoPlayer) throw new Exception("error: mExoPlayer null");
            mExoPlayer.clearVideoSurface();
            if (null == surface) throw new Exception("error: surface null");
            mExoPlayer.setVideoSurface(surface);
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "setSurface -> completed");
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "setSurface -> " + e.getMessage());
            }
        }
    }

    /**
     * 是否正在播放
     */
    @Override
    public boolean isPlaying() {
        try {
            if (!isPrepared) throw new Exception("mPrepared warning: false");
            if (null == mExoPlayer) throw new Exception("mExoPlayer error: null");
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
                LogUtil.log(TAG, "isPlaying -> " + e.getMessage());
            }
            return false;
        }
    }

    @Override
    public void seekToDefaultPosition() {
        try {

            if (null == mExoPlayer) throw new Exception("error: mMediaPlayer null");

            boolean live = isLiveStream();
            if (!live)
                throw new Exception("warning: live false");

            mSeeking = true;
            mExoPlayer.seekToDefaultPosition();
            if (mExoPlayer.getPlaybackState() != Player.STATE_READY) {
                mExoPlayer.prepare();
            }
            mExoPlayer.play();
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "seekToDefaultPosition -> completed");
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "seekToDefaultPosition -> " + e.getMessage());
            }
        }
    }

    @Override
    public void seekTo(long seek) {
        try {

            if (seek < 0L) throw new Exception("error: seek<0");
            if (null == mExoPlayer) throw new Exception("error: mMediaPlayer null");
            StartArgs args = getStartArgs();
            if (null == args) throw new Exception("error: args null");

            long duration = getDuration();
            if (duration > 0L && seek > duration) {
                seek = duration;
            }

            mSeeking = true;
            long position = getPosition();
            if (seek < position) {
                onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.MEDIA_INFO_UPDATE_SEEK_START_REWIND);
            } else {
                onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.MEDIA_INFO_UPDATE_SEEK_START_FORWARD);
            }
            mExoPlayer.seekTo(seek);
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "seekTo -> completed");
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "seekTo -> " + e.getMessage());
            }
        }
    }

    @Override
    public boolean isLiveStream() {
        try {
            if (null == mExoPlayer) throw new Exception("mExoPlayer error: null");
            boolean live = super.isLiveStream();
            if (live) {
                return true;
            } else {
                // Media3 中 Timeline 和 Window 的使用方式
                Timeline timeline = mExoPlayer.getCurrentTimeline();
                if (timeline.isEmpty()) throw new Exception("error: timeline is empty");
                int windowIndex = mExoPlayer.getCurrentWindowIndex();
                Timeline.Window window = new Timeline.Window();
                timeline.getWindow(windowIndex, window, Player.REPEAT_MODE_OFF);
                // Media3 中判断是否为直播
                return window.isLive();
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取当前播放的位置
     */
    @Override
    public long getPosition() {
        try {
            if (!isPrepared) throw new Exception("mPrepared warning: false");
            if (null == mExoPlayer) throw new Exception("mExoPlayer error: null");

            boolean live = isLiveStream();
            // Media3 中判断是否为直播
            if (live) {

                // Media3 中 Timeline 和 Window 的使用方式
                Timeline timeline = mExoPlayer.getCurrentTimeline();
                if (timeline.isEmpty()) throw new Exception("error: timeline is empty");

                int windowIndex = mExoPlayer.getCurrentWindowIndex();
                Timeline.Window window = new Timeline.Window();
                timeline.getWindow(windowIndex, window, Player.REPEAT_MODE_OFF);

                // ========== Media3 通用适配方案 ==========
                // 1. 直播可回溯的最早位置（替代 getEarliestAvailablePositionMs()）
                //    window.startPositionMs 是 Media3 中表示窗口起始位置的标准字段
                long windowStartTimeMs = window.windowStartTimeMs;

                // 2. 直播最新可播放位置（替代 getLatestAvailablePositionMs()）
                //    方案：直播窗口的 "结束位置" = 起始位置 + 窗口时长（Media3 通用逻辑）
                long windowDurationMs = window.getDurationMs();
                long latestAvailablePositionMs = windowStartTimeMs + windowDurationMs;

                // 3. 计算直播偏移（当前位置到最新位置的差距）
                long currentPositionMs = mExoPlayer.getCurrentPosition();
                long liveOffsetMs = latestAvailablePositionMs - currentPositionMs;
                return liveOffsetMs;
            } else {
                long currentPosition = mExoPlayer.getCurrentPosition();
                if (currentPosition < 0)
                    throw new Exception("currentPosition warning: " + currentPosition);
                return currentPosition;
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "getPosition -> " + e.getMessage());
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
            if (!isPrepared) throw new Exception("mPrepared warning: false");
            if (null == mExoPlayer) throw new Exception("mExoPlayer error: null");

            // Media3 中判断是否为直播
            boolean live = isLiveStream();
            if (live) {

                // Media3 中 Timeline 和 Window 的使用方式
                Timeline timeline = mExoPlayer.getCurrentTimeline();
                if (timeline.isEmpty()) throw new Exception("error: timeline is empty");

                int windowIndex = mExoPlayer.getCurrentWindowIndex();
                Timeline.Window window = new Timeline.Window();
                timeline.getWindow(windowIndex, window, Player.REPEAT_MODE_OFF);

                // ========== Media3 通用适配方案 ==========
                // 1. 直播可回溯的最早位置（替代 getEarliestAvailablePositionMs()）
                //    window.startPositionMs 是 Media3 中表示窗口起始位置的标准字段
                long windowStartTimeMs = window.windowStartTimeMs;

                // 2. 直播最新可播放位置（替代 getLatestAvailablePositionMs()）
                //    方案：直播窗口的 "结束位置" = 起始位置 + 窗口时长（Media3 通用逻辑）
                long windowDurationMs = window.getDurationMs();
                long latestAvailablePositionMs = windowStartTimeMs + windowDurationMs;
                return latestAvailablePositionMs;
            } else {
                long duration = mExoPlayer.getDuration();
                if (duration <= 0) throw new Exception("duration warning: " + duration);
                return duration;
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "getDuration -> " + e.getMessage());
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

    @Override
    public void setSpeed(float speed) {
        try {
            if (null == mExoPlayer) throw new Exception("mMediaPlayer error: null");
            PlaybackParameters playbackParameters = mExoPlayer.getPlaybackParameters();
            if (null != playbackParameters) {
                playbackParameters = playbackParameters.withSpeed(speed);
            } else {
                playbackParameters = new PlaybackParameters(speed);
            }
            onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.MEDIA_INFO_UPDATE_PLAYBACLK_SPEED);
            mExoPlayer.setPlaybackParameters(playbackParameters);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "setSpeed -> " + e.getMessage());
            }
        }
    }

    @Override
    public float getSpeed() {
        try {
            if (null == mExoPlayer) throw new Exception("mMediaPlayer error: null");
            return mExoPlayer.getPlaybackParameters().speed;
        } catch (Exception e) {
            return 1.0f;
        }
    }

    @Override
    public void setVolume(float v1, float v2) {
        try {
            if (null == mExoPlayer) throw new Exception("mExoPlayer error: null");
            float volume = Math.max(v1, v2);
            if (volume < 0) throw new Exception("error: volume < 0");
            mExoPlayer.setVolume(volume);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "setVolume -> " + e.getMessage());
            }
        }
    }

    @Override
    public float getVolume() {
        try {
            if (null == mExoPlayer) throw new Exception("mExoPlayer error: null");
            return mExoPlayer.getVolume();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "getVolume -> " + e.getMessage());
            }
            return 0f;
        }
    }

    @Override
    public void registListener() {
        try {
            if (null == mExoPlayer) throw new Exception("error: mExoPlayer null");
//            mExoPlayer.setVideoFrameMetadataListener(new VideoFrameMetadataListener() {
//                @Override
//                public void onVideoFrameAboutToBeRendered(long presentationTimeUs, long releaseTimeNs, Format format, @Nullable MediaFormat mediaFormat) {
//
//                    /**
//                     * PTS（Presentation Time Stamp，显示时间戳
//                     * DTS（Decoding Time Stamp，解码时间戳
//                     * 这两个时间戳是控制媒体帧解码和显示时序的核心参数。
//                     */
//
//                    if (null != mediaFormat) {
//                        // presentationTimeUs = PTS（微秒级）
//                        long ptsMs = presentationTimeUs / 1000; // 转换为毫秒
////                        // DTS：ExoPlayer 中视频帧的 DTS 通常和 PTS 相同（I/P 帧），B 帧会提前
////                        // 可通过 mediaFormat 获取更底层的 DTS
//
//                        boolean containsKey = mediaFormat.containsKey("dts_us");
//                        long dtsUs = mediaFormat.getLong("dts_us", -1);
//                        long dtsMs = dtsUs / 1000;
//
//                        if (LogUtil.DEBUG) {
//                            LogUtil.log(TAG, "onVideoFrameAboutToBeRendered -> ptsMs = " + ptsMs + ", dtsMs = " + dtsMs + ", containsKey = " + containsKey);
//                        }
//                    }
//                }
//            });
            mExoPlayer.addAnalyticsListener(mAnalyticsListener);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "registListener -> Exception " + e.getMessage());
            }
        }
    }

    @Override
    public void unRegistListener() {
        try {
            if (null == mExoPlayer) throw new Exception("error: mExoPlayer null");
            mExoPlayer.removeAnalyticsListener(mAnalyticsListener);
            mExoPlayer.setPlaybackParameters(null);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "unRegistListener -> Exception " + e.getMessage());
            }
        }
    }

    private boolean releaseHlsManifest() {
        try {
//            if (null != mHlsSegmentInfos) {
//                mHlsSegmentInfos.clear();
//                mHlsSegmentInfos = null;
//            }

            if (null != mHlsSpanInfos) {
                mHlsSpanInfos.clear();
                mHlsSpanInfos = null;
            }

            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "releaseHlsManifest -> completed");
            }
            return true;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "releaseHlsManifest -> Exception: " + e.getMessage());
            }
            return false;
        }
    }

    private boolean unInitSimpleCache() {
        try {
            if (null == mSimpleCache) throw new Exception("warning: mSimpleCache null");
            mSimpleCache.removeListener("mCacheListener", mCacheListener);
            mSimpleCache.release();
            mSimpleCache = null;
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "unInitSimpleCache -> completed");
            }
            return true;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "unInitSimpleCache -> Exception: " + e.getMessage());
            }
            return false;
        }
    }

    private boolean initSimpleCache(Context context, StartArgs args) {

        //
        boolean unInitSimpleCache = unInitSimpleCache();
        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "initSimpleCache -> unInitSimpleCache " + unInitSimpleCache);
        }

        try {
            boolean containsMainUrl = args.containsMainUrl();
            if (!containsMainUrl) throw new Exception("error: containsMainUrl false");

            String url = args.getUrl();
            if (url.startsWith(PlayerType.SchemeType.FILE))
                throw new Exception("error: url is file");

            ConfigArgs configArgs = PlayerSDK.getInstance().getConfigArgs();
            if (null == configArgs) throw new Exception("error: configArgs null");

            Cache cache = configArgs.getCache();
            if (null == cache) throw new Exception("error: cache null");

            boolean cacheEnable = cache.isEnable();
            if (!cacheEnable) throw new Exception("error: cacheEnable false");

            int sizeMB = cache.getSizeMB();
            if (sizeMB <= 0) throw new Exception("error: sizeMB <= 0, sizeMB = " + sizeMB);

            String dirName = cache.getDir(PlayerType.KernelType.MEDIA_V3);
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "initSimpleCache -> dirName = " + dirName + ", url = " + url);
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
            mSimpleCache = new SimpleCache(dirFile,
                    //
                    new LeastRecentlyUsedCacheEvictor(sizeMB),
                    //
                    new StandaloneDatabaseProvider(context));
            mSimpleCache.addListener("mCacheListener", mCacheListener);
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "initSimpleCache -> useCache completed");
            }
            return true;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "initSimpleCache -> Exception: " + e.getMessage());
            }
            return false;
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

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "start");
        }

        try {
            if (null == mExoPlayer) throw new Exception("mExoPlayer error: null");
            mExoPlayer.play();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "start -> " + e.getMessage());
            }
        }
    }

    @Override
    public void resume() {

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "resume ->");
        }

        try {
            if (!isPrepared)
                throw new Exception("mPrepared warning: false");
            if (null == mExoPlayer)
                throw new Exception("mMediaPlayer error: null");

            start();

            boolean live = isLiveStream();
            if (live) {
                seekToDefaultPosition();
            }

            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "resume -> completed");
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "resume -> " + e.getMessage());
            }
        }
    }

    /**
     * 暂停
     */
    @Override
    public void pause() {

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "pause ->");
        }

        try {
            if (!isPrepared) throw new Exception("mPrepared warning: false");
            if (null == mExoPlayer) throw new Exception("mMediaPlayer error: null");
            mExoPlayer.pause();

            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "pause -> completed");
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "pause -> " + e.getMessage());
            }
        }
    }

    /**
     * 停止
     */
    @Override
    public void stop() {

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "stop");
        }

        try {
            if (null == mExoPlayer) throw new Exception("mExoPlayer error: null");
            mExoPlayer.pause();
            mExoPlayer.stop();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "stop -> " + e.getMessage());
            }
        }

        stopHandler();

        boolean unInitSimpleCache = unInitSimpleCache();
        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "stop -> SimpleCache unInitSimpleCache " + unInitSimpleCache);
        }
    }

    @Override
    public void release() {

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "release");
        }

        try {
            if (null == mExoPlayer) throw new Exception("error: mExoPlayer null");
            mExoPlayer.setVideoSurface(null);
            mExoPlayer.release();
            mExoPlayer = null;
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "release -> completed");
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "release -> " + e.getMessage());
            }
        }

        if (null != mStartArgs) {
            mStartArgs = null;
        }

        stopHandler();

        boolean unInitSimpleCache = unInitSimpleCache();
        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "release -> SimpleCache unInitSimpleCache " + unInitSimpleCache);
        }

        boolean releaseHlsManifest = releaseHlsManifest();
        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "release -> HlsManifest release " + releaseHlsManifest);
        }
    }

    @Override
    public void initHandler() {
        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "initHandler");
        }

        if (null == mHandler) {
            mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    formatMessage(msg);
                }
            };
        }
    }

    @Override
    public Handler getHandler() {
        return mHandler;
    }

    @Override
    public void stopHandler() {

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "stopHandler");
        }

        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    private final androidx.media3.datasource.cache.Cache.Listener mCacheListener = new androidx.media3.datasource.cache.Cache.Listener() {
        @Override
        public void onSpanAdded(androidx.media3.datasource.cache.Cache cache, CacheSpan cacheSpan) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "Cache.Listener -> onSpanAdded -> span = " + cacheSpan);
            }
        }

        @Override
        public void onSpanRemoved(androidx.media3.datasource.cache.Cache cache, CacheSpan cacheSpan) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "Cache.Listener -> onSpanRemoved -> span = " + cacheSpan);
            }
        }

        @Override
        public void onSpanTouched(androidx.media3.datasource.cache.Cache cache, CacheSpan cacheSpan, CacheSpan cacheSpan1) {
        }
    };

    private final AnalyticsListener mAnalyticsListener = new AnalyticsListener() {

        /**
         * 网速
         * @param eventTime        事件时间上下文
         * @param totalLoadTimeMs  累计加载耗时（ms）
         * @param totalBytesLoaded 累计加载字节数
         * @param bitrateEstimate  估算带宽（bps）
         */
        @Override
        public void onBandwidthEstimate(EventTime eventTime,
                                        int totalLoadTimeMs,
                                        long totalBytesLoaded,
                                        long bitrateEstimate) {

//            // 1. 当前估算带宽
//            long kbps = bitrateEstimate / 1000;
//            Log.d("BW", "估算带宽: " + kbps + " Kbps");
//
//            // 2. 自己计算实际平均速度（双重验证）
//            if (totalLoadTimeMs > 0) {
//                long actualBps = (totalBytesLoaded * 8 * 1000L) / totalLoadTimeMs;
//                Log.d("BW", "实际平均带宽: " + actualBps / 1000 + " Kbps");
//            }
//
//            // 3. 判断网络状况
//            if (bitrateEstimate < 500_000) {        // < 500 Kbps
//                Log.w("BW", "网络较差，可能卡顿");
//            } else if (bitrateEstimate < 2_000_000) { // < 2 Mbps
//                Log.d("BW", "网络一般");
//            } else {
//                Log.d("BW", "网络良好");
//            }
//
//            // 4. 流量统计（注意是累计值，需要做差值）
//            Log.d("BW", "累计加载: " + totalBytesLoaded / 1024 + " KB"
//                    + "，耗时: " + totalLoadTimeMs + " ms");
        }

        /**
         * 丢帧回调，当视频帧因渲染超时被丢弃时触发
         *
         * @param eventTime    事件时间上下文
         * @param droppedFrames 本次回调周期内丢弃的帧数
         * @param elapsedMs    本次回调周期的时长（ms）
         */
        @Override
        public void onDroppedVideoFrames(EventTime eventTime,
                                         int droppedFrames,
                                         long elapsedMs) {

//            // 计算丢帧率：丢帧数 / 周期时长(s) = 每秒丢帧数
//            float dropRate = droppedFrames / (elapsedMs / 1000f);
//            Log.w("DROP", "丢帧数: " + droppedFrames
//                    + "，周期: " + elapsedMs + "ms"
//                    + "，丢帧率: " + dropRate + " fps");
//
//            // 判断卡顿严重程度
//            if (droppedFrames >= 30) {
//                Log.e("DROP", "严重卡顿：单次丢帧超过 30 帧");
//            } else if (droppedFrames >= 10) {
//                Log.w("DROP", "明显卡顿：单次丢帧超过 10 帧");
//            }
        }

        @Override
        public void onVideoFrameProcessingOffset(EventTime eventTime, long l, int i) {

            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onVideoFrameProcessingOffset -> l = " + l + ", i = " + i);
            }
        }

        /**
         * 初始化当前的 本地所有缓存
         * @param eventTime
         * @param i
         */
        @Override
        public void onTimelineChanged(AnalyticsListener.EventTime eventTime, int i) {

            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onTimelineChanged -> i = " + i + ", eventTime.currentPlaybackPositionMs = " + eventTime.currentPlaybackPositionMs);
            }

            // 追播
            try {
                boolean live = isLiveStream();
                if (!live)
                    throw new Exception("warning: current not live");

                StartArgs startArgs = getStartArgs();
                StartArgs.BufferingConfiguration bufferingConfiguration = startArgs.getBufferingConfiguration();
                long minLivePlaybackTimelineOffsetMs = bufferingConfiguration.getMinLivePlaybackTimelineOffsetMs();
                long currentPlaybackPositionMs = eventTime.currentPlaybackPositionMs;
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "onTimelineChanged1 -> minLivePlaybackTimelineOffsetMs = " + minLivePlaybackTimelineOffsetMs + ", currentPlaybackPositionMs = " + currentPlaybackPositionMs);
                }

                if (currentPlaybackPositionMs < minLivePlaybackTimelineOffsetMs) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "onTimelineChanged1 -> seekToDefaultPosition");
                    }
//                    onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.RETRY_BUFFERING_TIMEOUT);
                }

            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "onTimelineChanged1 -> Exception: " + e.getMessage());
                }
            }

            // 缓存
            try {
                if (null == mSimpleCache)
                    throw new Exception("warning: mSimpleCache null");

                boolean live = isLiveStream();
                if (live) throw new Exception("warning: current is live");

                Object currentManifest = mExoPlayer.getCurrentManifest();
                if (null == currentManifest) throw new Exception("warning: currentManifest null");
                if (!(currentManifest instanceof HlsManifest))
                    throw new Exception("warning: currentManifest not instanceof HlsManifest");
                HlsMediaPlaylist hlsMediaPlaylist = ((HlsManifest) currentManifest).mediaPlaylist;
                if (null == hlsMediaPlaylist) throw new Exception("warning: hlsMediaPlaylist null");
                List<HlsMediaPlaylist.Segment> segments = hlsMediaPlaylist.segments;
                if (null == segments) throw new Exception("warning: segments null");
                String url = hlsMediaPlaylist.baseUri;
                String baseUrl = formatBaseUrl(url);
                for (HlsMediaPlaylist.Segment segment : segments) {
                    if (null == segment) continue;

                    String segmentUrl = baseUrl + PlayerType.MarkType.SEPARATOR + segment.url;
                    String cacheKey = formatCacheKey(segmentUrl);

                    NavigableSet<CacheSpan> cachedSpans = mSimpleCache.getCachedSpans(cacheKey);
                    if (cachedSpans.isEmpty()) continue;

                    for (CacheSpan span : cachedSpans) {
                        if (null == span) continue;
                        if (!span.isCached) continue;
                        HlsSpanInfo hlsSpanInfo = new HlsSpanInfo();
                        String segmentPath = span.file.getAbsolutePath();
                        hlsSpanInfo.setPath(segmentPath);
                        hlsSpanInfo.setUrl(segmentUrl);
                        long startTimeMs = segment.relativeStartTimeUs / 1000;
                        hlsSpanInfo.setStartTimeMs(startTimeMs);
                        long endTimeMs = startTimeMs + segment.durationUs / 1000;
                        hlsSpanInfo.setEndTimeMs(endTimeMs);

                        if (null == mHlsSpanInfos) {
                            mHlsSpanInfos = new HlsSpanList();
                        }
                        mHlsSpanInfos.add(hlsSpanInfo, false);
                    }

                }
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "onTimelineChanged2 -> load segments completed, mHlsSpanInfos.size = " + mHlsSpanInfos.size());
                }
            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "onTimelineChanged2 -> Exception: " + e.getMessage());
                }
            }
        }

        @Override
        public void onPlayWhenReadyChanged(AnalyticsListener.EventTime eventTime, boolean playWhenReady, int reason) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onPlayWhenReadyChanged -> playWhenReady = " + playWhenReady + ", reason = " + reason);
            }
        }

        @Override
        public void onLoadError(EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData, IOException e, boolean b) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onLoadError -> message = " + e.getMessage() + ", loadUrl = " + loadEventInfo.dataSpec.uri);
            }
        }

        @Override
        public void onPlayerErrorChanged(EventTime eventTime, @Nullable PlaybackException e) {
            if (LogUtil.DEBUG) {
                int errorCode = null == e ? -9 : e.errorCode;
                String errorMessage = null == e ? "null" : e.getMessage();
                LogUtil.log(TAG, "onPlayerErrorChanged -> errorCode = " + errorCode + ", errorMessage = " + errorMessage);
            }
        }

        @Override
        public void onPlayerError(AnalyticsListener.EventTime eventTime, PlaybackException error) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onPlayerError -> errorCode = " + error.errorCode + ", errMessage" + error.getMessage());
            }

            try {
                if (error.errorCode == PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW) {
                    seekToDefaultPosition();
                }
                // timeout
                else if (error.errorCode == PlaybackException.ERROR_CODE_TIMEOUT) {
                    // seekToDefaultPosition();
                    onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.STOP);
                    onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.ERROR_PLAY);
                    stop();
                }
                // errorCode=-9 的常见原因分析 媒体资源 URL 无效 / 过期：URL 拼写错误、资源被删除、CDN 节点失效；
                else if (error.errorCode == -9) {
                    onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.STOP);
                    onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.ERROR_PLAY);
                    stop();
                } else {
//                    if (!(error instanceof ExoPlaybackException))
//                        throw new Exception("PlaybackException error: not instanceof ExoPlaybackException");
                    onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.STOP);
                    onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.ERROR_PLAY);
                    stop();
                }
            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "onPlayerError -> Exception: " + e.getMessage());
                }
            }
        }

        public void onEvents(Player player, AnalyticsListener.Events events) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onEvents -> isPlaying = " + player.isPlaying());
            }
        }

        @Override
        public void onVideoSizeChanged(AnalyticsListener.EventTime eventTime, VideoSize videoSize) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onVideoSizeChanged -> width = " + videoSize.width + ", height = " + videoSize.height);
            }
        }

        @Override
        public void onIsPlayingChanged(AnalyticsListener.EventTime eventTime, boolean isPlaying) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onIsPlayingChanged -> isPlaying = " + isPlaying);
            }
        }

        @Override
        public void onLoadCompleted(EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onLoadCompleted -> loadEventInfo.dataSpec.uri = " + loadEventInfo.dataSpec.uri + ", eventTime.currentPlaybackPositionMs = " + eventTime.currentPlaybackPositionMs);

                int dataType = mediaLoadData.dataType;
                if (dataType == C.DATA_TYPE_MANIFEST) {
                    LogUtil.log(TAG, "onLoadCompleted -> current dataType DATA_TYPE_MANIFEST");
                } else if (dataType == C.DATA_TYPE_MEDIA) {
                    LogUtil.log(TAG, "onLoadCompleted -> current dataType DATA_TYPE_MEDIA");
                }

                int trackType = mediaLoadData.trackType;
                if (trackType == C.TRACK_TYPE_DEFAULT) {
                    LogUtil.log(TAG, "onLoadCompleted -> current trackType TRACK_TYPE_DEFAULT");
                } else if (trackType == C.TRACK_TYPE_VIDEO) {
                    LogUtil.log(TAG, "onLoadCompleted -> current trackType TRACK_TYPE_VIDEO");
                } else if (trackType == C.TRACK_TYPE_AUDIO) {
                    LogUtil.log(TAG, "onLoadCompleted -> current trackType TRACK_TYPE_AUDIO");
                } else if (trackType == C.TRACK_TYPE_TEXT) {
                    LogUtil.log(TAG, "onLoadCompleted -> current trackType TRACK_TYPE_TEXT");
                }

                if (trackType == C.TRACK_TYPE_DEFAULT || trackType == C.TRACK_TYPE_VIDEO) {
                    long mediaStartTimeMs = mediaLoadData.mediaStartTimeMs;
                    long mediaEndTimeMs = mediaLoadData.mediaEndTimeMs;
                    long mediaDuration = mediaEndTimeMs - mediaStartTimeMs;
                    LogUtil.log(TAG, "onLoadCompleted -> mediaStartTimeMs = " + mediaStartTimeMs + ", mediaEndTimeMs = " + mediaEndTimeMs + ", mediaDuration = " + mediaDuration);
                }
            }


            try {
                boolean live = isLiveStream();
                if (live) throw new Exception("warning: current is live");
                loadHlsSpanInfo(loadEventInfo, mediaLoadData);
            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "onLoadCompleted -> Exception: " + e.getMessage());
                }
            }
        }

        @Override
        public void onPlaybackStateChanged(AnalyticsListener.EventTime eventTime, int state) {

            // 播放错误
            if (state == Player.STATE_IDLE) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "onPlaybackStateChanged -> state[Player.STATE_IDLE] = " + state);
                }
            }
            // 播放完成
            else if (state == Player.STATE_ENDED) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "onPlaybackStateChanged -> state[Player.STATE_ENDED] = " + state);
                }
                stop();
                onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.STOP);
                onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.END);
            }
            // 播放开始
            else if (state == Player.STATE_READY) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "onPlaybackStateChanged -> state[Player.STATE_READY] = " + state);
                }
                try {
                    if (!isPrepared) throw new Exception("warning: isPrepared false");

                    // buffering
                    if (isBuffering) {
                        if (LogUtil.DEBUG) {
                            LogUtil.log(TAG, "onPlaybackStateChanged -> state[Player.STATE_READY] -> buffering");
                        }
                        isBuffering = false;
                        onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.MEDIA_INFO_BUFFERING_STOP);
                    }
                    // seeking
                    else if (mSeeking) {
                        if (LogUtil.DEBUG) {
                            LogUtil.log(TAG, "onPlaybackStateChanged -> state[Player.STATE_READY] -> seeking");
                        }
                        mSeeking = false;
                        onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.MEDIA_INFO_UPDATE_SEEK_FINISH);

                        // 起播快进
                        if (mPlayWhenReadySeeking) {
                            mPlayWhenReadySeeking = false;
                            // 立即播放
                            boolean playWhenReady = isPlayWhenReady();
                            if (playWhenReady) {
                                onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.START);
                                start();
                            } else {
                                onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.MEDIA_INFO_PLAY_WHEN_READY_PAUSE);
                                pause();
                            }
                        }
                        // 正常快进&快退
                        else {

                        }
                    }
                    // start ready
                    else {
                        if (LogUtil.DEBUG) {
                            LogUtil.log(TAG, "onPlaybackStateChanged -> state[Player.STATE_READY] -> start ready");
                        }
                        boolean playWhenReady = isPlayWhenReady();
                        if (playWhenReady) {
                            boolean playing = isPlaying();
                            if (playing) throw new Exception("warning: isPlaying true");
                            start();
                        } else {
                            onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.MEDIA_INFO_PLAY_WHEN_READY_PAUSE);
                            pause();
                        }
                    }

                } catch (Exception e) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "onPlaybackStateChanged -> state[Player.STATE_READY] -> Exception " + e.getMessage());
                    }
                }
            }
            // 播放缓冲
            else if (state == Player.STATE_BUFFERING) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "onPlaybackStateChanged -> state[Player.STATE_BUFFERING] = " + state);
                }
                try {
                    if (!isPrepared) throw new Exception("mPrepared warning: false");
                    isBuffering = true;
                    onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.MEDIA_INFO_BUFFERING_START);
                } catch (Exception e) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "onPlaybackStateChanged -> state[Player.STATE_BUFFERING] -> Exception " + state);
                    }
                }
            }
            // ????
            else {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "onPlaybackStateChanged -> state[????] = " + state);
                }
            }
        }

        @Override
        public void onVideoInputFormatChanged(AnalyticsListener.EventTime eventTime, Format format, @Nullable DecoderReuseEvaluation decoderReuseEvaluation) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onVideoInputFormatChanged[出画面] -> width = " + format.width + ", height = " + format.height + ", isPrepared = " + isPrepared);
            }
            // 视频信息
            try {
                StartArgs args = getStartArgs();
                if (null == args) throw new Exception("error: args null");
                @PlayerType.ScaleType.Value int scaleType = args.getscaleType();
                int rotation = args.getRotation();
//                int rotation = (videoSize.unappliedRotationDegrees > 0 ? videoSize.unappliedRotationDegrees : PlayerType.RotationType.DEFAULT);
                onVideoFormatChanged(PlayerType.KernelType.MEDIA_V3, rotation, scaleType, format.width, format.height, format.bitrate);
            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "onVideoInputFormatChanged -> " + e.getMessage());
                }
            }

            // 起播快进??
            try {
                if (isPrepared) throw new Exception("warning: isPrepared true");
                isPrepared = true;
                onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.MEDIA_INFO_PREPARE);
                long playWhenReadySeekToPosition = getPlayWhenReadySeekToPosition();
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "onVideoInputFormatChanged -> playWhenReadySeekToPosition = " + playWhenReadySeekToPosition);
                }
                // 立即播放
                if (playWhenReadySeekToPosition <= 0L) {
                    onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.START);
                }
                // 起播快进
                else {
                    onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.MEDIA_INFO_PLAY_WHEN_READY_SEEK);
                    mPlayWhenReadySeeking = true;
                    seekTo(playWhenReadySeekToPosition);
                }
            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "onVideoInputFormatChanged -> Exception " + e.getMessage());
                }
            }
        }

        @Override
        public void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime, Object output, long renderTimeMs) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onRenderedFirstFrame ->");
            }
        }

        @Override
        public void onAudioInputFormatChanged(AnalyticsListener.EventTime eventTime, Format format, @Nullable DecoderReuseEvaluation decoderReuseEvaluation) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onAudioInputFormatChanged ->");
            }
        }

        @Override
        public void onSeekStarted(EventTime eventTime) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onSeekStarted ->");
            }
        }

        @Override
        public void onSeekBackIncrementChanged(EventTime eventTime, long l) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onSeekBackIncrementChanged ->");
            }
        }

        @Override
        public void onSeekForwardIncrementChanged(EventTime eventTime, long l) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onSeekForwardIncrementChanged ->");
            }
        }

        @Override
        public void onCues(EventTime eventTime, CueGroup cueGroup) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onCues -> cueGroup = " + cueGroup);
            }
        }

        @Override
        public void onCues(EventTime eventTime, List<Cue> cues) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onCues -> cues = " + cues);
            }

            try {
                if (null == cues) throw new Exception();
                if (cues.size() == 0) throw new Exception();

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
                LogUtil.log(TAG, "onTrackSelectionParametersChanged -> trackSelectionParameters = " + trackSelectionParameters);
            }
//
//            int rendererCount = mExoPlayer.getRendererCount();
//            for(int i=0;i<rendererCount;i++){
//                int rendererType = mExoPlayer.getRendererType(i);
//                 LogUtil.log(TAG, "onTrackSelectionParametersChanged -> i = "+i+", rendererType = "+rendererType);
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
                LogUtil.log(TAG, "onTracksChanged -> tracks = " + tracks);
            }

//            if (videoIndex != -100) {
//                videoIndex = -100;
//                DefaultTrackSelector trackSelector = (DefaultTrackSelector) mExoPlayer.getTrackSelector();
//                DefaultTrackSelector.Parameters.Builder parameters = trackSelector.buildUponParameters();
//                // 找到视频渲染器的索引
//                for (int i = 0; i < trackSelector.getCurrentMappedTrackInfo().getRendererCount(); i++) {
//                    if (trackSelector.getCurrentMappedTrackInfo().getRendererType(i) == C.TRACK_TYPE_VIDEO) {
//                        videoIndex = i;
//                         LogUtil.log(TAG, "onTracksChanged -> i = " + i);
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
                LogUtil.log(TAG, "onSurfaceSizeChanged -> i = " + i + ", i1 = " + i1);
            }
        }
    };

    /*********/

    @Override
    public boolean toggleTrack(TrackInfo trackInfo) {
        try {
            if (null == trackInfo) throw new Exception("error: trackArgs null");
            int groupIndex = trackInfo.getGroupIndex();
            if (groupIndex == -1) throw new Exception("error: groupIndex == -1");
            int trackIndex = trackInfo.getTrackIndex();
            if (trackIndex == -1) throw new Exception("error: trackIndex == -1");
            if (null == mExoPlayer) throw new Exception("error: mExoPlayer null");
            Tracks tracks = mExoPlayer.getCurrentTracks();
            ImmutableList<Tracks.Group> tracksGroups = tracks.getGroups();
            TrackGroup trackGroup = tracksGroups.get(groupIndex).getMediaTrackGroup();

            TrackSelector trackSelector = mExoPlayer.getTrackSelector();
            TrackSelectionParameters selectionParameters = trackSelector.getParameters().buildUpon().setOverrideForType(new TrackSelectionOverride(trackGroup, trackIndex)).build();
            trackSelector.setParameters(selectionParameters);
            return true;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "toggleTrack -> " + e.getMessage());
            }
            return false;
        }
    }

    @Override
    public List<TrackInfo> getTrackInfo(int type) {


        try {
            if (null == mExoPlayer) throw new Exception("error: mExoPlayer null");

            //
            LinkedList<TrackInfo> list = new LinkedList<>();

            //
            androidx.media3.common.Tracks tracks = mExoPlayer.getCurrentTracks();
            ImmutableList<androidx.media3.common.Tracks.Group> groups = tracks.getGroups();
            int groupCount = groups.size();
            for (int groupIndex = 0; groupIndex < groupCount; groupIndex++) {
                Tracks.Group group = groups.get(groupIndex);
                if (null == group) continue;

                if (LogUtil.DEBUG) {
                    TrackGroup trackGroup = group.getMediaTrackGroup();
                    LogUtil.log(TAG, "getTrackInfo -> trackGroup.id = " + trackGroup.id + ", trackGroup.length = " + trackGroup.length);
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
                    if (!isTrackSupported) continue;

                    // 轨道是否被选中
                    boolean isTrackSelected = group.isTrackSelected(trackIndex);

                    //
                    Format format = group.getTrackFormat(trackIndex);
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "getTrackInfo -> format = " + format + ", format.metadata = " + format.metadata);
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
                        //  LogUtil.log(TAG, "getTrackInfo[C.TRACK_TYPE_METADATA] -> groupCount = " + groupCount + ", groupIndex = " + groupIndex + ", trackCount = " + trackCount + ", trackIndex = " + trackIndex + ", trackType = " + trackType + ", isGroupAdaptiveSupported = " + isGroupAdaptiveSupported + ", isGroupSelected = " + isGroupSelected + ", isGroupSupported = " + isGroupSupported + ", isTrackSelected = " + isTrackSelected + ", isTrackSupported = " + isTrackSupported);
                        continue;
                    }
                    // 未知
                    else {
                        //   LogUtil.log(TAG, "getTrackInfo[Unknow] -> groupCount = " + groupCount + ", groupIndex = " + groupIndex + ", trackCount = " + trackCount + ", trackIndex = " + trackIndex + ", trackType = " + trackType + ", isGroupAdaptiveSupported = " + isGroupAdaptiveSupported + ", isGroupSelected = " + isGroupSelected + ", isGroupSupported = " + isGroupSupported + ", isTrackSelected = " + isTrackSelected + ", isTrackSupported = " + isTrackSupported);
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

                    //    LogUtil.log(TAG, "getTrackInfo -> groupCount = " + groupCount + ", groupIndex = " + groupIndex + ", trackCount = " + trackCount + ", trackIndex = " + trackIndex + ", trackType = " + trackType + ", isGroupAdaptiveSupported = " + isGroupAdaptiveSupported + ", isGroupSelected = " + isGroupSelected + ", isGroupSupported = " + isGroupSupported + ", isTrackSelected = " + isTrackSelected + ", isTrackSupported = " + isTrackSupported + ", isTrackMixed = " + isTrackMixed + ", isTrackMixedSelected = " + isTrackMixedSelected + ", format = " + object);
                    //
                    list.add(trackInfo);
                }
            }

            //
            if (list.isEmpty()) throw new Exception("error: list empty");

            return list;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "getTrackInfo -> Exception " + e.getMessage());
            }
            return null;
        }
    }

    @Override
    public HlsSpanList getSegments() {
        try {
            if (null == mHlsSpanInfos) throw new Exception("warning: mHlsSpanInfo null");
            return mHlsSpanInfos;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "getSegments -> Exception " + e.getMessage());
            }
            return null;
        }
    }

    @Override
    public boolean subtitleOffsetMs(int offsetMs) {
        try {
            if (null == mExoPlayer) throw new Exception("error: mExoPlayer null");
            int rendererCount = mExoPlayer.getRendererCount();
            for (int i = 0; i < rendererCount; i++) {
                int rendererType = mExoPlayer.getRendererType(i);
                if (rendererType != C.TRACK_TYPE_TEXT) continue;
                Renderer renderer = mExoPlayer.getRenderer(i);
                if (null == renderer) continue;
                if (renderer instanceof OffsetMsTextRenderer) {
                    ((OffsetMsTextRenderer) renderer).appendOffsetMs(offsetMs);
                    onUpdateSubtitle(PlayerType.KernelType.MEDIA_V3, "");
                }
                break;
            }
            return true;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "subtitleOffsetMs -> Exception " + e.getMessage());
            }
            return false;
        }
    }

    /************************/

    private MediaSource buildMediaSource(Context context, HttpDataSource.Factory httpFactory, StartArgs startArgs, @PlayerType.UrlType.Value int urlType, UrlArgs.Item urlItem) {

        try {

            if (null == urlItem) throw new Exception("error: urlItem null");


            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "buildMediaSource -> urlItem = " + urlItem);
            }

            String url = urlItem.getUrl();
            int metaType = urlItem.getMetaType();

            // 轨道音频 hls
            if (metaType == PlayerType.MetaType.VIDEO_HLS && urlType == PlayerType.UrlType.AUDIO) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaSource -> track audio, type = hls, url = " + url);
                }

                HlsMediaSource.Factory factory = buildHlsMediaSourceFactory(context, httpFactory, startArgs, PlayerType.UrlType.AUDIO, urlItem);
                MediaItem mediaItem = buildMediaItem(PlayerType.UrlType.AUDIO, startArgs, urlItem);
                return ((MediaSource.Factory) factory).createMediaSource(mediaItem);
            }
            // 轨道音频
            else if (urlType == PlayerType.UrlType.AUDIO) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaSource -> track audio, type = def, url = " + url);
                }

                DataSource.Factory factory = buildDefaultDataSource(context, httpFactory);
                MediaItem mediaItem = buildMediaItem(PlayerType.UrlType.AUDIO, startArgs, urlItem);
                return new DefaultMediaSourceFactory(factory).createMediaSource(mediaItem);
            }
            // 轨道字幕
            else if (urlType == PlayerType.UrlType.SUBTITLE) {

                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaSource -> track subtitle, type = def, url = " + url);
                }

                MediaItem.SubtitleConfiguration subtitleConfiguration = buildMediaItemSubtitleConfiguration(urlItem);
                if (null == subtitleConfiguration) {
                    return null;
                } else {
                    Object factory = buildDefaultDataSource(context, httpFactory);
                    if (factory instanceof CacheDataSource.Factory) {
                        return new SingleSampleMediaSource.Factory((CacheDataSource.Factory) factory).createMediaSource(subtitleConfiguration, C.TIME_UNSET);
                    } else {
                        return new SingleSampleMediaSource.Factory((DataSource.Factory) factory).createMediaSource(subtitleConfiguration, C.TIME_UNSET);
                    }
                }
            }
            // 轨道视频 rtmp
            else if (metaType == PlayerType.MetaType.VIDEO_RTMP && urlType == PlayerType.UrlType.VIDEO) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaSource -> track video, type = rtmp, url = " + url);
                }

                Class<?> cls = Class.forName("ext.rtmp.RtmpDataSource");
                DataSource.Factory factory = (DataSource.Factory) cls.newInstance();
                MediaItem mediaItem = buildMediaItem(PlayerType.UrlType.VIDEO, startArgs, urlItem);
                return new ProgressiveMediaSource.Factory(factory).createMediaSource(mediaItem);
            }
            // 轨道视频 rtsp
            else if (metaType == PlayerType.MetaType.VIDEO_RTSP && urlType == PlayerType.UrlType.VIDEO) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaSource -> track video, type = rtsp, url = " + url);
                }

                Class<?> cls = Class.forName("androidx.media3.exoplayer.rtsp.RtspMediaSource$Factory");
                Constructor<?> constructor = cls.getDeclaredConstructor(DataSource.Factory.class);
                constructor.setAccessible(true);

                DataSource.Factory obj = buildDefaultDataSource(context, httpFactory);
                DataSource.Factory factory = (DataSource.Factory) constructor.newInstance(obj);
                MediaItem mediaItem = buildMediaItem(PlayerType.UrlType.VIDEO, startArgs, urlItem);
                return ((MediaSource.Factory) factory).createMediaSource(mediaItem);
            }
            // 轨道视频 dash
            else if (metaType == PlayerType.MetaType.VIDEO_DASH && urlType == PlayerType.UrlType.VIDEO) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaSource -> track video, type = dash, url = " + url);
                }

                DataSource.Factory obj = buildDefaultDataSource(context, httpFactory);
                DashMediaSource.Factory factory = new DashMediaSource.Factory(obj);
                MediaItem mediaItem = buildMediaItem(PlayerType.UrlType.VIDEO, startArgs, urlItem);
                return ((MediaSource.Factory) factory).createMediaSource(mediaItem);
            }
            // 轨道视频 hls
            else if (metaType == PlayerType.MetaType.VIDEO_HLS && urlType == PlayerType.UrlType.VIDEO) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaSource -> track video, type = hls, url = " + url);
                }

                HlsMediaSource.Factory factory = buildHlsMediaSourceFactory(context, httpFactory, startArgs, PlayerType.UrlType.VIDEO, urlItem);
                MediaItem mediaItem = buildMediaItem(PlayerType.UrlType.VIDEO, startArgs, urlItem);
                return ((MediaSource.Factory) factory).createMediaSource(mediaItem);
            }
            // 轨道视频 SmoothStreaming
            else if (metaType == PlayerType.MetaType.VIDEO_SS && urlType == PlayerType.UrlType.VIDEO) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaSource -> track video, type = SmoothStreaming, url = " + url);
                }

                DataSource.Factory obj = buildDefaultDataSource(context, httpFactory);
                SsMediaSource.Factory factory = new SsMediaSource.Factory(obj);
                MediaItem mediaItem = buildMediaItem(PlayerType.UrlType.VIDEO, startArgs, urlItem);
                return ((MediaSource.Factory) factory).createMediaSource(mediaItem);
            }
            // 轨道视频 mp4
            else if (metaType == PlayerType.MetaType.VIDEO_MP4 && urlType == PlayerType.UrlType.VIDEO) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaSource -> track video, type = mp4, url = " + url);
                }

                DataSource.Factory factory = buildDefaultDataSource(context, httpFactory);
                MediaItem mediaItem = buildMediaItem(PlayerType.UrlType.VIDEO, startArgs, urlItem);
                return new ProgressiveMediaSource.Factory(factory).createMediaSource(mediaItem);
            }
            // 轨道视频 def
            else {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaSource -> track video, type = def, url = " + url);
                }

                DataSource.Factory factory = buildDefaultDataSource(context, httpFactory);
                MediaItem mediaItem = buildMediaItem(PlayerType.UrlType.VIDEO, startArgs, urlItem);
                return new DefaultMediaSourceFactory(factory).createMediaSource(mediaItem);
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "buildVideoMediaSource -> Exception: " + e.getMessage());
            }
            return null;
        }
    }

    private String formatBaseUrl(String url) {
        try {
            Uri uri = Uri.parse(url);
            String path = uri.getPath();
            int lastIndexOf = path.lastIndexOf(PlayerType.MarkType.SEPARATOR);
            if (lastIndexOf > 0) {
                path = path.substring(0, lastIndexOf);
            }
            String baseUrl = new StringBuilder().append(uri.getScheme()).append("://").append(uri.getHost()).append(path).toString();
//            if (LogUtil.DEBUG) {
//                 LogUtil.log(TAG, "formatBaseUrl -> url =  " + url + ", baseUrl = " + baseUrl);
//            }
            return baseUrl;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "formatBaseUrl -> Exception: " + e.getMessage());
            }
            return "";
        }
    }

    private MediaItem buildMediaItem(@PlayerType.UrlType.Value int urlType, StartArgs startArgs, UrlArgs.Item urlItem) {

        try {

            if (null == urlItem) throw new Exception("error: urlItem null");
            String url = urlItem.getUrl();
            if (null == url) throw new Exception("error: url null");
            if (url.isEmpty()) throw new Exception("error: url isEmpty");

            MediaItem.LiveConfiguration liveConfiguration = new MediaItem.LiveConfiguration.Builder()
                    // 播放器追赶直播时允许的最大倍速	1.2f - 1.5f	当播放器落后于直播点时，自动加速播放追赶
                    .setMaxPlaybackSpeed(startArgs.getLiveConfiguration().getMaxPlaybackSpeed())
                    // 播放器为了等待缓冲的最小倍速	0.8f - 1.0f	网络差时，降速播放避免卡顿
                    .setMinPlaybackSpeed(startArgs.getLiveConfiguration().getMinPlaybackSpeed())
                    // 目标直播延迟（离直播边缘的距离）	3000 - 5000ms	值越大越稳定（不易触发 BehindLiveWindow），值越小越实时
                    .setTargetOffsetMs(startArgs.getLiveConfiguration().getTargetOffsetMs())
                    // 最小允许的直播延迟	2000ms	防止播放器离直播边缘太近导致频繁卡顿
                    .setMinOffsetMs(startArgs.getLiveConfiguration().getMinOffsetMs())
                    // 最大允许的直播延迟	10000ms	超过这个值就会自动加速追赶
                    .setMaxOffsetMs(startArgs.getLiveConfiguration().getMaxOffsetMs()).build();

            if (urlType == PlayerType.UrlType.AUDIO) {
                return new MediaItem.Builder().setUri(Uri.parse(url)).setMediaId("audio:" + url.hashCode()).setLiveConfiguration(liveConfiguration).build();
            } else if (urlType == PlayerType.UrlType.VIDEO) {
                return new MediaItem.Builder().setUri(Uri.parse(url)).setMediaId("video:" + url.hashCode()).setLiveConfiguration(liveConfiguration).build();
            } else {
                return new MediaItem.Builder().setUri(Uri.parse(url)).setLiveConfiguration(liveConfiguration).build();
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "buildMediaItem -> Exception: " + e.getMessage());
            }
            return null;
        }
    }

    private MediaItem.SubtitleConfiguration buildMediaItemSubtitleConfiguration(UrlArgs.Item urlItem) {

        try {

            if (null == urlItem) throw new Exception("error: urlItem null");
            String url = urlItem.getUrl();
            if (null == url) throw new Exception("error: url null");
            if (url.isEmpty()) throw new Exception("error: url isEmpty");

            String mimeType = null;
            // ssa字幕
            if (Pattern.matches(".*\\" + PlayerType.SchemeType._SSA + "$", url)) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaItemSubtitleConfiguration -> ssa 1");
                }
                mimeType = PlayerType.TrackType.TEXT_SSA;
            } else if (Pattern.matches(".*\\" + PlayerType.SchemeType._SSA_ + "\\?.*", url)) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaItemSubtitleConfiguration -> ssa 2");
                }
                mimeType = PlayerType.TrackType.TEXT_SSA;
            }
            // ass字幕
            else if (Pattern.matches(".*\\" + PlayerType.SchemeType._ASS + "$", url)) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaItemSubtitleConfiguration -> ass 1");
                }
                mimeType = PlayerType.TrackType.TEXT_ASS;
            } else if (Pattern.matches(".*\\" + PlayerType.SchemeType._ASS_ + "\\?.*", url)) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaItemSubtitleConfiguration -> ass 2");
                }
                mimeType = PlayerType.TrackType.TEXT_SSA;
            }
            // srt字幕
            else if (Pattern.matches(".*\\" + PlayerType.SchemeType._SRT + "$", url)) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaItemSubtitleConfiguration -> srt 1");
                }
                mimeType = PlayerType.TrackType.TEXT_SRT;
            } else if (Pattern.matches(".*\\" + PlayerType.SchemeType._SRT_ + "\\?.*", url)) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaItemSubtitleConfiguration -> srt 2");
                }
                mimeType = PlayerType.TrackType.TEXT_SSA;
            }
            // vtt字幕
            else if (Pattern.matches(".*\\" + PlayerType.SchemeType._VTT + "$", url)) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaItemSubtitleConfiguration -> vtt 1");
                }
                mimeType = PlayerType.TrackType.TEXT_VTT;
            } else if (Pattern.matches(".*\\" + PlayerType.SchemeType._VTT_ + "\\?.*", url)) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaItemSubtitleConfiguration -> vtt 2");
                }
                mimeType = PlayerType.TrackType.TEXT_VTT;
            }
            // 不支持
            else {
                throw new Exception("error: not support " + url);
            }

            String language = urlItem.getLanguage();
            String label = urlItem.getLabel();
            if (null == label) {
                label = language;
            }
            int selectionFlags;
            if (urlItem.isMain()) {
                selectionFlags = C.SELECTION_FLAG_AUTOSELECT;
            } else {
                selectionFlags = 0;
            }
            int roleFlags;
            if (urlItem.isMain()) {
                roleFlags = C.ROLE_FLAG_MAIN;
            } else {
                roleFlags = C.ROLE_FLAG_SUBTITLE;
            }

            return new MediaItem.SubtitleConfiguration.Builder(Uri.parse(url))
                    // 主轨道
                    .setSelectionFlags(selectionFlags)
                    // 描述轨道的「角色 / 用途」ROLE_FLAG_*		MAIN（主轨道）、SUBTITLE（字幕）、COMMENTARY（解说）
                    .setRoleFlags(roleFlags).setMimeType(mimeType) // 也可以用 MimeTypes.APPLICATION_SUBRIP
                    .setLanguage(language).setLabel(label).setId("subtitle:" + url.hashCode()).build();

//                      .setSubtitleMediaSourceFactory(
//                            SingleSampleMediaSource.Factory(defaultDataSourceFactory) // 字幕用非缓存数据源
//                    )

        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "buildMediaItemSubtitleConfiguration -> Exception: " + e.getMessage());
            }
            return null;
        }
    }

    private String formatCacheKey(String url) {
        try {
            if (null == url) throw new Exception("error: url null");
            if (url.isEmpty()) throw new Exception("error: url isEmpty");
            return formatCacheKey(Uri.parse(url));
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "formatCacheKey -> Exception: " + e.getMessage());
            }
            return "";
        }
    }

    private String formatCacheKey(Uri uri) {
        try {
            if (null == uri) throw new Exception("error: uri null");
            String newKey = new StringBuilder().append(uri.getScheme()).append("://").append(uri.getHost()).append(uri.getPath()).toString();
//            if (LogUtil.DEBUG) {
//                 LogUtil.log(TAG, "formatCacheKey -> url =  " + url + ", newKey = " + newKey);
//            }
            return newKey;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "formatCacheKey -> Exception: " + e.getMessage());
            }
            return "";
        }
    }

    private int formatSegmentPosition(String segmentUrl) {
        try {
            Pattern pattern = Pattern.compile(".*?(\\d+)\\.ts");
            Matcher matcher = pattern.matcher(segmentUrl);
            if (!matcher.find()) throw new Exception("error: not find1");
            String segmentPosition = matcher.group(1);
            if (null == segmentPosition || segmentPosition.isEmpty())
                throw new Exception("error: not find2");
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "formatSegmentPosition -> segmentPosition = " + segmentPosition + ", segmentUrl = " + segmentUrl);
            }
            return Integer.parseInt(segmentPosition);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "formatSegmentPosition -> Exception: " + e.getMessage());
            }
            return -1;
        }
    }

    private boolean loadHlsSpanInfo(LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
        try {
            if (null == mSimpleCache) throw new Exception("warning: mSimpleCache null");
            if (null == loadEventInfo) throw new Exception("warning: loadEventInfo null");
            if (null == mediaLoadData) throw new Exception("warning: mediaLoadData null");

            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "loadHlsSpanInfo -> mediaLoadData.dataType = " + mediaLoadData.dataType + ", mediaLoadData.trackType = " + mediaLoadData.trackType + ", mediaLoadData.mediaStartTimeMs = " + mediaLoadData.mediaStartTimeMs + ", mediaLoadData.mediaEndTimeMs = " + mediaLoadData.mediaEndTimeMs);
            }

            if (mediaLoadData.dataType == C.DATA_TYPE_MANIFEST) {
                if (LogUtil.DEBUG) {
                    DataSpec dataSpec = loadEventInfo.dataSpec;
                    LogUtil.log(TAG, "loadHlsSpanInfo -> .m3u8 索引文件, dataSpec.uri = " + dataSpec.uri);
                }
            } else if (mediaLoadData.dataType == C.DATA_TYPE_MEDIA) {
                DataSpec dataSpec = loadEventInfo.dataSpec;

                Uri uri = dataSpec.uri;
                if (null == uri) throw new Exception("warning: uri null");

                String segmentUrl = uri.toString();
                if (segmentUrl.isEmpty()) throw new Exception("warning: segmentUrl isEmpty");

                int segmentPosition = formatSegmentPosition(segmentUrl);
                if (segmentPosition < 0) throw new Exception("warning: segmentPosition < 0");

                if (null == mHlsSpanInfos) {
                    mHlsSpanInfos = new HlsSpanList();
                }
                HlsSpanInfo spanInfos = mHlsSpanInfos.get(segmentPosition);
                if (null != spanInfos)
                    throw new Exception("warning: spanInfos already contains, segmentPosition = " + segmentPosition);

                String cacheKey = formatCacheKey(segmentUrl);
                if (cacheKey.isEmpty()) throw new Exception("warning: cacheKey isEmpty");

                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "loadHlsSpanInfo -> .m3u8 缓存文件, cacheKey = " + cacheKey + ", segmentUrl = " + segmentUrl);
                }

                NavigableSet<CacheSpan> cachedSpans = mSimpleCache.getCachedSpans(cacheKey);
                if (cachedSpans.isEmpty()) throw new Exception("warning: cachedSpans isEmpty");

                for (CacheSpan span : cachedSpans) {
                    if (null == span) continue;
                    if (!span.isCached) continue;
                    HlsSpanInfo hlsSpanInfo = new HlsSpanInfo();
                    hlsSpanInfo.setPath(span.file.getAbsolutePath());
                    hlsSpanInfo.setUrl(segmentUrl);
                    hlsSpanInfo.setStartTimeMs(mediaLoadData.mediaStartTimeMs);
                    hlsSpanInfo.setEndTimeMs(mediaLoadData.mediaEndTimeMs);

                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "loadHlsSpanInfo -> add span completed, hlsSpanInfo = " + hlsSpanInfo);
                    }

                    mHlsSpanInfos.add(hlsSpanInfo, true);
                }
            } else {
                throw new Exception("warning: not support mediaLoadData.dataType = " + mediaLoadData.dataType);
            }
            return true;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "loadHlsSpanInfo -> Exception " + e.getMessage());
            }
            return false;
        }
    }

    private DataSource.Factory buildDefaultDataSource(Context context, HttpDataSource.Factory httpFactory) {

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "buildDefaultDataSource -> mSimpleCache = " + mSimpleCache);
        }

        try {

            if (null == mSimpleCache) throw new Exception("error: mSimpleCache null");

            return new CacheDataSource.Factory().setFlags(
                            // 当发生错误时忽略缓存（比如错误时不读取缓存，直接请求源数据）。
                            CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR
                                    // 对于未设置长度的请求忽略缓存（比如请求体长度未知时不使用缓存）。
                                    | CacheDataSource.FLAG_IGNORE_CACHE_FOR_UNSET_LENGTH_REQUESTS).setCache(mSimpleCache)
                    // 网络请求工厂
                    .setUpstreamDataSourceFactory(httpFactory)
                    // 缓存读取工厂
                    .setCacheReadDataSourceFactory(new FileDataSource.Factory())
                    // 写入数据到缓存
                    .setCacheWriteDataSinkFactory(new CacheDataSink.Factory().setFragmentSize(CacheDataSink.DEFAULT_FRAGMENT_SIZE).setCache(mSimpleCache))
                    // 自定义缓存键
                    .setCacheKeyFactory(new CacheKeyFactory() {
                        @Override
                        public String buildCacheKey(DataSpec dataSpec) {
                            return formatCacheKey(dataSpec.uri);
                        }
                    });
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "buildDefaultDataSource -> Exception: " + e.getMessage());
            }
            return new DefaultDataSource.Factory(context, httpFactory);
        }
    }


    private HlsMediaSource.Factory buildHlsMediaSourceFactory(Context context, HttpDataSource.Factory httpFactory, StartArgs args, @PlayerType.UrlType.Value int urlType, UrlArgs.Item item) {

        DataSource.Factory factory = buildDefaultDataSource(context, httpFactory);

        HlsMediaSource.Factory hlsMediaSource = new HlsMediaSource.Factory(factory)
                // 播放器可以跳过「预加载切片」的步骤，仅解析 M3U8 元数据就完成准备，从而加快播放启动速度，但可能牺牲首帧加载的稳定性。
                .setAllowChunklessPreparation(true);

        //
        hlsMediaSource.setLoadErrorHandlingPolicy(new CustomHlsLoadErrorHandlingPolicy(args.getRetryCount()));
        // setPlaylistParserFactory
        hlsMediaSource.setPlaylistParserFactory(new CustomHlsPlaylistParserFactory(args.getProxyUrl()));

        // setExtractorFactory
        int parser = item.getParser();
        int payloadReaderFactoryFlags;
        if (parser == PlayerType.ParserType.VIDEO) {
            payloadReaderFactoryFlags = DefaultTsPayloadReaderFactory.FLAG_IGNORE_AAC_STREAM;
        } else {
            payloadReaderFactoryFlags = 0;
        }
        boolean exposeCea608WhenMissingDeclarations;
        if (parser == PlayerType.ParserType.VIDEO) {
            exposeCea608WhenMissingDeclarations = false;
        } else if (parser == PlayerType.ParserType.AUDIO) {
            exposeCea608WhenMissingDeclarations = false;
        } else if (parser == PlayerType.ParserType.VIDEO_AUDIO) {
            exposeCea608WhenMissingDeclarations = false;
        } else {
            exposeCea608WhenMissingDeclarations = true;
        }
        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "buildHlsMediaSourceFactory -> hls, parser = " + parser + ", payloadReaderFactoryFlags = " + payloadReaderFactoryFlags + ", exposeCea608WhenMissingDeclarations = " + exposeCea608WhenMissingDeclarations);
        }
        hlsMediaSource.setExtractorFactory(new CustomDefaultHlsExtractorFactory(payloadReaderFactoryFlags, exposeCea608WhenMissingDeclarations));

        return hlsMediaSource;
    }
}