package lib.kalu.mediaplayer.core.kernel.video.exo2;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLivePlaybackSpeedControl;
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
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Tracks;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.analytics.DefaultAnalyticsCollector;
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider;
import com.google.android.exoplayer2.decoder.DecoderReuseEvaluation;
import com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.LoadEventInfo;
import com.google.android.exoplayer2.source.MediaLoadData;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsManifest;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
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
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lib.kalu.exoplayer2.subtitle.OffsetMsTextRenderer;
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
import lib.kalu.mediaplayer.core.kernel.video.exo2.proxy.CustomDefaultHlsExtractorFactory;
import lib.kalu.mediaplayer.core.kernel.video.exo2.proxy.CustomDefaultHttpDataSource;
import lib.kalu.mediaplayer.core.kernel.video.exo2.proxy.CustomHlsPlaylistParserFactory;
import lib.kalu.mediaplayer.proxy.ProxyUrl;
import lib.kalu.mediaplayer.util.LogUtil;

public final class VideoExo2Player extends VideoBasePlayer {

    private String TAG = "VideoExo2Player2";

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
    public void releaseDecoder(boolean isFromUser) {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            if (isFromUser) {
                setEvent(null);
            }
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
            if (null != mExoPlayer)
                throw new Exception("warning: null != mExoPlayer");
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "checkDecoder ->");
            }

            if (null == startArgs)
                throw new Exception("error: startArgs null");

            StartArgs.TimeoutConfiguration timeoutConfiguration = startArgs.getTimeoutConfiguration();
            int connectTimeoutMs = timeoutConfiguration.getConnectTimeoutMs();
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "checkDecoder -> connectTimeoutMs = " + connectTimeoutMs);
            }

            ExoPlayer.Builder builder = new ExoPlayer.Builder(context)
                    // 启用懒加载准备
                    .setUseLazyPreparation(true)
                    // 播放器调试和诊断相关的配置项
                    .setUsePlatformDiagnostics(false)
                    // 创建渲染器工厂
                    .setRenderersFactory(new DefaultRenderersFactory(context) {
                        @Override
                        protected void buildTextRenderers(Context context, TextOutput textOutput, Looper looper, int i, ArrayList<Renderer> arrayList) {
//                            TextRenderer textRenderer = new TextRenderer(textOutput, looper);
//                            textRenderer.experimentalSetLegacyDecodingEnabled(true);
//                            arrayList.add(textRenderer);
                        }
                    })
                    // 创建媒体源工厂，开启字幕预解析（核心配置）
                    .setMediaSourceFactory(new DefaultMediaSourceFactory(context)
                            // 实验性配置：在数据提取阶段解析字幕  true = 预解析字幕，false = 播放时解析（默认）
                            .experimentalUseProgressiveMediaSourceForSubtitles(true)
                    )
                    // 监听
                    .setAnalyticsCollector(new DefaultAnalyticsCollector(Clock.DEFAULT))
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
                            .setAllowAudioMixedMimeTypeAdaptiveness(false)
                            // 视频禁止混合 MIME 类型切换（如视频+音频单独切换）
                            .setAllowVideoMixedMimeTypeAdaptiveness(true)
                            // 音频禁止非无缝切换
//                            .setAllowAudioNonSeamlessAdaptiveness(false)
                            // 视频禁止非无缝切换
                            .setAllowVideoNonSeamlessAdaptiveness(false)
                            // 音频混合声道数量的自适应性
                            .setAllowAudioMixedChannelCountAdaptiveness(true)
                            // 音频混合采样率自适应
                            .setAllowAudioMixedSampleRateAdaptiveness(true)
                            // 音频混合时解码器支持自适应
                            .setAllowAudioMixedDecoderSupportAdaptiveness(true)
                            // 音频混合时解码器支持自适应
                            .setAllowVideoMixedDecoderSupportAdaptiveness(true)
                            .build(),
                            new AdaptiveTrackSelection.Factory(
                                    10000,// 至少 10 秒后才允许升码率
                                    25000, // 最多 2.5 秒后允许降码率
                                    25000, //
                                    0.7F)))
                    // 配置带宽测量器
                    .setBandwidthMeter(new DefaultBandwidthMeter.Builder(context)
                            // 初始带宽估算为100Mbps
                            .setInitialBitrateEstimate(100_000_000)
                            .build())
                    // 增大内存缓存（默认 2MB，按需调整）
                    .setLoadControl(new DefaultLoadControl.Builder()
                            /**
                             * private int minBufferMs = 50000;
                             *         private int maxBufferMs = 50000;
                             *         private int bufferForPlaybackMs = 1000;
                             *         private int bufferForPlaybackAfterRebufferMs = 2000;
                             */
                            .setBufferDurationsMs(
                                    // minBufferMs：播放器至少要缓冲 1 秒的数据后，才会停止主动加载更多数据；如果缓冲低于这个值，会重新开始加载。
                                    50_000,
                                    // maxBufferMs：播放器最多缓冲 5 秒的数据，达到这个值后会停止加载，避免占用过多内存。
                                    50_000,
                                    // bufferForPlaybackMs：播放器需要至少缓冲 1 秒的数据，才会开始播放（或从暂停恢复播放）。
                                    1000,
                                    // bufferForPlaybackAfterRebufferMs：播放器在缓冲不足导致暂停后，需要重新缓冲 1 秒的数据，才会恢复播放。
                                    2000
                            )
                            // 内存分配器 默认 64 * 1024 = 65536
                            .setAllocator(new DefaultAllocator(true, 64 * 1024))
                            .build()
                    )
                    // 直播场景
                    .setLivePlaybackSpeedControl(new DefaultLivePlaybackSpeedControl.Builder()
                            // 最小直播偏移的平滑因子（用于稳定计算「实时直播位置」）
                            .setMinPossibleLiveOffsetSmoothingFactor(0.999F)
                            // 速度调整的最小间隔（多久能调整一次速度）,弱网 / 低延迟场景可缩短至 500ms（调整更频繁）；追求性能可延长至 2000ms
                            .setMinUpdateIntervalMs(500)
                            //「保持 1 倍速」的最大偏移误差（超出这个范围才调整速度）,无需修改（默认值已足够平滑，改大易导致偏移计算波动）
                            .setMaxLiveOffsetErrorMsForUnitSpeed(200)
                            // 极端场景下的最小速度（如缓存彻底耗尽时的保底速度）,建议 ≥0.8f（过低会导致播放卡顿感明显）
                            .setFallbackMinPlaybackSpeed(0.8f)
                            // 极端场景下的最大速度（如缓存严重过剩时的保底速度）,建议 ≤1.2f（过高会让用户感知到快放）
                            .setFallbackMaxPlaybackSpeed(1.2f)
                            // 速度调整的「比例控制因子」（偏移越大，速度调整幅度越大）,弱网可调大至 0.005f（更快调整速度）；低延迟可调小至 0.001f（调整更平缓）
                            .setProportionalControlFactor(0.005f)
                            // 发生缓冲时，目标直播偏移的增量（缓冲后临时增大目标偏移，避免再次缓冲）,弱网可增大至 2000ms（缓冲后更保守）；低延迟可减小至 500ms（不牺牲太多实时性）
                            .setTargetLiveOffsetIncrementOnRebufferMs(1000)
                            .build());


            int decoderType = startArgs.getDecoderType();
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "checkDecoder -> decoderType = " + decoderType);
            }
            // only_ffmpeg
            if (decoderType == PlayerType.DecoderType.ONLY_FFMPEG) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "checkDecoder -> only_ffmpeg");
                }
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.renderers.VideoFFmpegAudioFFmpegRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_codec
            else if (decoderType == PlayerType.DecoderType.ONLY_CODEC) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "checkDecoder -> only_codec");
                }
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.renderers.VideoCodecAudioCodecRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // video_codec_audio_ffmpeg
            else if (decoderType == PlayerType.DecoderType.ONLY_VIDEO_CODEC_AUDIO_FFMPEG) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "checkDecoder -> only_video_codec_audio_ffmpeg");
                }
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.renderers.VideoCodecAudioFFmpegRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_video_ffmpeg_audio_codec
            else if (decoderType == PlayerType.DecoderType.ONLY_VIDEO_FFMPEG_AUDIO_CODEC) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "checkDecoder -> only_video_ffmpeg_audio_codec");
                }
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.renderers.VideoFFmpegAudioCodecRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_audio_ffmpeg
            else if (decoderType == PlayerType.DecoderType.ONLY_AUDIO_FFMPEG) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "checkDecoder -> only_audio_ffmpeg");
                }
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.renderers.OnlyAudioFFmpegRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_video_ffmpeg
            else if (decoderType == PlayerType.DecoderType.ONLY_VIDEO_FFMPEG) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "checkDecoder -> only_video_ffmpeg");
                }
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.renderers.OnlyVideoFFmpegRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_audio_codec
            else if (decoderType == PlayerType.DecoderType.ONLY_AUDIO_CODEC) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "checkDecoder -> only_audio_codec");
                }
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.renderers.OnlyAudioCodecRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // only_video_codec
            else if (decoderType == PlayerType.DecoderType.ONLY_VIDEO_CODEC) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "checkDecoder -> only_video_codec");
                }
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.renderers.OnlyVideoCodecRenderersFactory");
                Object newInstance = clazz.getDeclaredConstructor(Context.class).newInstance(context);
                builder.setRenderersFactory((RenderersFactory) newInstance);
            }
            // all
            else {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "checkDecoder -> only_video_codec");
                }
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.renderers.BaseRenderersFactory");
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
            onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.INIT_READY);
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
            CustomDefaultHttpDataSource.Factory httpFactory = new CustomDefaultHttpDataSource.Factory(proxyUrl, noProxy)
                    .setUserAgent(ExoPlayerLibraryInfo.VERSION_SLASHY)
                    .setConnectTimeoutMs(connectTimoutMs)
                    .setReadTimeoutMs(connectTimoutMs)
                    .setDefaultRequestProperties(new HashMap<>())
                    .setAllowCrossProtocolRedirects(true)
                    .setKeepPostFor302Redirects(true);
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
                            LogUtil.log(TAG, "startDecoder -> 外挂字幕轨道: subtitle = " + item);
                        }
                        MediaSource mediaSource = buildMediaSource(context, httpFactory, startArgs, PlayerType.UrlType.SUBTITLE, item);
                        if (LogUtil.DEBUG) {
                            LogUtil.log(TAG, "startDecoder -> 外挂字幕轨道: mediaSource = " + mediaSource);
                        }
                        if (null == mediaSource)
                            continue;
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
            stop();
            onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.ERROR_DECODE);
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "startDecoder -> Exception " + e.getMessage());
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
                LogUtil.log(TAG, "initOptions -> Exception step1 " + e.getMessage());
            }
        }

        try {
            if (null == mExoPlayer)
                throw new Exception("error: mExoPlayer null");
            int seekParameters = args.getSeekType();
            if (seekParameters == PlayerType.SeekType.EXO_CLOSEST_SYNC) {
                mExoPlayer.setSeekParameters(SeekParameters.CLOSEST_SYNC);
            } else if (seekParameters == PlayerType.SeekType.EXO_PREVIOUS_SYNC) {
                mExoPlayer.setSeekParameters(SeekParameters.PREVIOUS_SYNC);
            } else if (seekParameters == PlayerType.SeekType.EXO_NEXT_SYNC) {
                mExoPlayer.setSeekParameters(SeekParameters.NEXT_SYNC);
            } else if (seekParameters == PlayerType.SeekType.EXO_EXACT) {
                mExoPlayer.setSeekParameters(SeekParameters.EXACT);
            } else {
                mExoPlayer.setSeekParameters(SeekParameters.DEFAULT);
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "initOptions -> Exception step2 " + e.getMessage());
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
                LogUtil.log(TAG, "initOptions -> Exception step3 " + e.getMessage());
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
                LogUtil.log(TAG, "isPlaying -> " + e.getMessage());
            }
            return false;
        }
    }

    @Override
    public void seekToDefaultPosition() {
        try {
            if (null == mExoPlayer)
                throw new Exception("error: mMediaPlayer null");
            mSeeking = true;
            mExoPlayer.seekToDefaultPosition();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "seekToDefaultPosition -> " + e.getMessage());
            }
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
                LogUtil.log(TAG, "seekTo ->");
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
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            boolean live = super.isLiveStream();
            if (live) {
                return true;
            } else {
                // Media3 中 Timeline 和 Window 的使用方式
                Timeline timeline = mExoPlayer.getCurrentTimeline();
                if (timeline.isEmpty())
                    throw new Exception("error: timeline is empty");
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
            if (!isPrepared)
                throw new Exception("mPrepared warning: false");
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");

            boolean live = isLiveStream();
            // Media3 中判断是否为直播
            if (live) {

                // Media3 中 Timeline 和 Window 的使用方式
                Timeline timeline = mExoPlayer.getCurrentTimeline();
                if (timeline.isEmpty())
                    throw new Exception("error: timeline is empty");

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
            if (!isPrepared)
                throw new Exception("mPrepared warning: false");
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");

            // Media3 中判断是否为直播
            boolean live = isLiveStream();
            if (live) {

                // Media3 中 Timeline 和 Window 的使用方式
                Timeline timeline = mExoPlayer.getCurrentTimeline();
                if (timeline.isEmpty())
                    throw new Exception("error: timeline is empty");

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
                if (duration <= 0)
                    throw new Exception("duration warning: " + duration);
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
            if (null == mExoPlayer)
                throw new Exception("mMediaPlayer error: null");
            PlaybackParameters playbackParameters = mExoPlayer.getPlaybackParameters();
            if (null != playbackParameters) {
                playbackParameters = playbackParameters.withSpeed(speed);
            } else {
                playbackParameters = new PlaybackParameters(speed);
            }
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
            if (null == mExoPlayer)
                throw new Exception("mMediaPlayer error: null");
            return mExoPlayer.getPlaybackParameters().speed;
        } catch (Exception e) {
            return 1.0f;
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
                LogUtil.log(TAG, "setVolume -> " + e.getMessage());
            }
        }
    }

    @Override
    public float getVolume() {
        try {
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
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
            if (null == mExoPlayer)
                throw new Exception("error: mExoPlayer null");
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
            if (null == mExoPlayer)
                throw new Exception("error: mExoPlayer null");
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
            if (null == mSimpleCache)
                throw new Exception("warning: mSimpleCache null");
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
            if (!containsMainUrl)
                throw new Exception("error: containsMainUrl false");

            String url = args.getUrl();
            if (url.startsWith(PlayerType.SchemeType.FILE))
                throw new Exception("error: url is file");

            ConfigArgs configArgs = PlayerSDK.getInstance().getConfigArgs();
            if (null == configArgs)
                throw new Exception("error: configArgs null");

            Cache cache = configArgs.getCache();
            if (null == cache)
                throw new Exception("error: cache null");

            boolean cacheEnable = cache.isEnable();
            if (!cacheEnable)
                throw new Exception("error: cacheEnable false");

            int sizeMB = cache.getSizeMB();
            if (sizeMB <= 0)
                throw new Exception("error: sizeMB <= 0, sizeMB = " + sizeMB);

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
                    new StandaloneDatabaseProvider(context)
            );
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
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
            mExoPlayer.play();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "start -> " + e.getMessage());
            }
        }
    }

    /**
     * 暂停
     */
    @Override
    public void pause() {

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "pause");
        }

        try {
            if (!isPrepared)
                throw new Exception("mPrepared warning: false");
            if (null == mExoPlayer)
                throw new Exception("mMediaPlayer error: null");
            mExoPlayer.pause();
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
            if (null == mExoPlayer)
                throw new Exception("mExoPlayer error: null");
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
            if (null == mExoPlayer)
                throw new Exception("error: mExoPlayer null");
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

    private final com.google.android.exoplayer2.upstream.cache.Cache.Listener mCacheListener = new com.google.android.exoplayer2.upstream.cache.Cache.Listener() {

        @Override
        public void onSpanAdded(com.google.android.exoplayer2.upstream.cache.Cache cache, CacheSpan cacheSpan) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "Cache.Listener -> onSpanAdded -> span = " + cacheSpan);
            }
        }

        @Override
        public void onSpanRemoved(com.google.android.exoplayer2.upstream.cache.Cache cache, CacheSpan cacheSpan) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "Cache.Listener -> onSpanRemoved -> span = " + cacheSpan);
            }
        }

        @Override
        public void onSpanTouched(com.google.android.exoplayer2.upstream.cache.Cache cache, CacheSpan cacheSpan, CacheSpan cacheSpan1) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "Cache.Listener -> onSpanTouched -> span = " + cacheSpan);
            }
        }
    };

    private final AnalyticsListener mAnalyticsListener = new AnalyticsListener() {

        /**
         * 初始化当前的 本地所有缓存
         * @param eventTime
         * @param i
         */
        @Override
        public void onTimelineChanged(AnalyticsListener.EventTime eventTime, int i) {
            try {
                if (null == mSimpleCache)
                    throw new Exception("warning: mSimpleCache null");

                // Media3 中 Timeline 和 Window 的使用方式
                Timeline timeline = mExoPlayer.getCurrentTimeline();
                if (timeline.isEmpty())
                    throw new Exception("error: timeline is empty");

                int windowIndex = mExoPlayer.getCurrentWindowIndex();
                Timeline.Window window = new Timeline.Window();
                timeline.getWindow(windowIndex, window, Player.REPEAT_MODE_OFF);

                // Media3 中判断是否为直播
                boolean isLive = window.isLive();
                if (isLive)
                    throw new Exception("error: isLive true");

                Object currentManifest = mExoPlayer.getCurrentManifest();
                if (null == currentManifest)
                    throw new Exception("warning: currentManifest null");
                if (!(currentManifest instanceof HlsManifest))
                    throw new Exception("warning: currentManifest not instanceof HlsManifest");
                HlsMediaPlaylist hlsMediaPlaylist = ((HlsManifest) currentManifest).mediaPlaylist;
                if (null == hlsMediaPlaylist)
                    throw new Exception("warning: hlsMediaPlaylist null");
                List<HlsMediaPlaylist.Segment> segments = hlsMediaPlaylist.segments;
                if (null == segments)
                    throw new Exception("warning: segments null");
                String url = hlsMediaPlaylist.baseUri;
                String baseUrl = formatBaseUrl(url);
                for (HlsMediaPlaylist.Segment segment : segments) {
                    if (null == segment)
                        continue;

                    String segmentUrl = baseUrl + PlayerType.MarkType.SEPARATOR + segment.url;
                    String cacheKey = formatCacheKey(segmentUrl);

                    NavigableSet<CacheSpan> cachedSpans = mSimpleCache.getCachedSpans(cacheKey);
                    if (cachedSpans.isEmpty())
                        continue;

                    for (CacheSpan span : cachedSpans) {
                        if (null == span)
                            continue;
                        if (!span.isCached)
                            continue;
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
                    LogUtil.log(TAG, "onTimelineChanged -> load segments completed, mHlsSpanInfos.size = " + mHlsSpanInfos.size());
                }
            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "onTimelineChanged -> Exception: " + e.getMessage());
                }
            }
        }

        @Override
        public void onPlayerErrorChanged(EventTime eventTime, @Nullable PlaybackException e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onPlayerErrorChanged -> message = " + e.getMessage(), e);
            }
        }

        @Override
        public void onPlayWhenReadyChanged(AnalyticsListener.EventTime eventTime, boolean playWhenReady, int reason) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onPlayWhenReadyChanged -> playWhenReady = " + playWhenReady + ", reason = " + reason);
            }
        }

        @Override
        public void onPlayerError(AnalyticsListener.EventTime eventTime, PlaybackException error) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onPlayerError -> " + error.getMessage());
            }

            try {
                if (null == error)
                    throw new Exception("PlaybackException error: null");
                if (!(error instanceof ExoPlaybackException))
                    throw new Exception("PlaybackException error: not instanceof ExoPlaybackException");
                stop();
                onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.STOP);
                onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.ERROR_PLAY);
            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "onPlayerError -> error = " + error.getMessage());
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
        public void onLoadError(EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData, IOException e, boolean b) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onLoadError -> loadEventInfo = " + loadEventInfo.dataSpec.uri);
                LogUtil.log(TAG, "onLoadError -> message = " + e.getMessage());
            }
//            stop();
//            onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.STOP);
//            onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.ERROR_LOAD);
        }

        @Override
        public void onLoadCompleted(EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
            if (LogUtil.DEBUG) {
//                long position = loadEventInfo.dataSpec.position;
                LogUtil.log(TAG, "onLoadCompleted -> mediaLoadData.dataType = " + mediaLoadData.dataType + ", loadEventInfo.dataSpec.position = " + loadEventInfo.dataSpec.position + ", loadEventInfo.dataSpec.uri = " + loadEventInfo.dataSpec.uri);
            }
            loadHlsSpanInfo(loadEventInfo, mediaLoadData);
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
                    if (!isPrepared)
                        throw new Exception("warning: isPrepared false");

                    // buffering
                    if (isBuffering) {
                        if (LogUtil.DEBUG) {
                            LogUtil.log(TAG, "onPlaybackStateChanged -> state[Player.STATE_READY] -> buffering");
                        }
                        isBuffering = false;
                        onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.BUFFERING_STOP);
                    }
                    // seeking
                    else if (mSeeking) {
                        if (LogUtil.DEBUG) {
                            LogUtil.log(TAG, "onPlaybackStateChanged -> state[Player.STATE_READY] -> seeking");
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
                            LogUtil.log(TAG, "onPlaybackStateChanged -> state[Player.STATE_READY] -> start ready");
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
                    if (!isPrepared)
                        throw new Exception("mPrepared warning: false");
                    isBuffering = true;
                    onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.BUFFERING_START);
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
                if (null == args)
                    throw new Exception("error: args null");
                @PlayerType.ScaleType.Value
                int scaleType = args.getscaleType();
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
                if (isPrepared)
                    throw new Exception("warning: isPrepared true");
                isPrepared = true;
                onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.PREPARE);
                onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.VIDEO_RENDERING_START);
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
                    onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.SEEK_START_FORWARD);
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
                LogUtil.log(TAG, "toggleTrack -> " + e.getMessage());
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
            Tracks tracks = mExoPlayer.getCurrentTracks();
            ImmutableList<Tracks.Group> groups = tracks.getGroups();
            int groupCount = groups.size();
            for (int groupIndex = 0; groupIndex < groupCount; groupIndex++) {
                Tracks.Group group = groups.get(groupIndex);
                if (null == group)
                    continue;

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
                    if (!isTrackSupported)
                        continue;

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
            if (list.isEmpty())
                throw new Exception("error: list empty");

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
            if (null == mHlsSpanInfos)
                throw new Exception("warning: mHlsSpanInfo null");
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
                LogUtil.log(TAG, "subtitleOffsetMs -> Exception " + e.getMessage());
            }
            return false;
        }
    }

    /************************/

    private MediaSource buildMediaSource(Context context,
                                         HttpDataSource.Factory httpFactory,
                                         StartArgs args,
                                         @PlayerType.UrlType.Value
                                         int urlType,
                                         UrlArgs.Item urlItem) {

        try {

            if (null == urlItem)
                throw new Exception("error: urlItem null");


            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "buildMediaSource -> urlItem = " + urlItem);
            }

            String url = urlItem.getUrl();
            int metaType = urlItem.getMetaType();
            int hashCode = url.hashCode();

            // 轨道音频 hls
            if (metaType == PlayerType.MetaType.VIDEO_HLS && urlType == PlayerType.UrlType.AUDIO) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaSource -> track audio, type = hls, url = " + url);
                }

                HlsMediaSource.Factory factory = buildHlsMediaSourceFactory(context, httpFactory, args, PlayerType.UrlType.AUDIO, urlItem);
                return ((MediaSource.Factory) factory).createMediaSource(new MediaItem.Builder()
                        .setUri(Uri.parse(url))
                        .setMediaId("audio:" + hashCode)
                        .build());
            }
            // 轨道音频
            else if (urlType == PlayerType.UrlType.AUDIO) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaSource -> track audio, type = def, url = " + url);
                }

                DataSource.Factory factory = buildDefaultDataSource(context, httpFactory);
                return new DefaultMediaSourceFactory(factory)
                        .createMediaSource(new MediaItem.Builder()
                                .setUri(Uri.parse(url))
                                .setMediaId("audio:" + hashCode)
                                .build());
            }
            // 轨道字幕
            else if (urlType == PlayerType.UrlType.SUBTITLE) {

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

                String mimeType;
                if (url.endsWith(PlayerType.SchemeType._SSA)) {
                    mimeType = PlayerType.TrackType.TEXT_SSA;
                } else if (url.endsWith(PlayerType.SchemeType._ASS)) {
                    mimeType = PlayerType.TrackType.TEXT_ASS;
                } else {
                    mimeType = PlayerType.TrackType.TEXT_VTT;
                }

                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaSource -> track subtitle, type = def, mimeType = " + mimeType + ", url = " + url);
                }

                Object factory = buildDefaultDataSource(context, httpFactory);
                MediaItem.SubtitleConfiguration subtitleConfig = new MediaItem.SubtitleConfiguration.Builder(Uri.parse(url))
                        // 主轨道
                        .setSelectionFlags(selectionFlags)
                        // 描述轨道的「角色 / 用途」ROLE_FLAG_*		MAIN（主轨道）、SUBTITLE（字幕）、COMMENTARY（解说）
                        .setRoleFlags(roleFlags)
                        .setMimeType(mimeType) // 也可以用 MimeTypes.APPLICATION_SUBRIP
                        .setLanguage(language)
                        .setLabel(label)
                        .setId("subtitle:" + hashCode)
                        .build();

//                      .setSubtitleMediaSourceFactory(
//                            SingleSampleMediaSource.Factory(defaultDataSourceFactory) // 字幕用非缓存数据源
//                    )

                if (factory instanceof CacheDataSource.Factory) {
                    return new SingleSampleMediaSource.Factory((CacheDataSource.Factory) factory)
                            .createMediaSource(subtitleConfig, C.TIME_UNSET);
                } else {
                    return new SingleSampleMediaSource.Factory((DataSource.Factory) factory)
                            .createMediaSource(subtitleConfig, C.TIME_UNSET);
                }
            }
            // 轨道视频 rtmp
            else if (metaType == PlayerType.MetaType.VIDEO_RTMP && urlType == PlayerType.UrlType.VIDEO) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaSource -> track video, type = rtmp, url = " + url);
                }

                Class<?> cls = Class.forName("ext.rtmp.RtmpDataSource");
                DataSource.Factory factory = (DataSource.Factory) cls.newInstance();
                return new ProgressiveMediaSource.Factory(factory).createMediaSource(new MediaItem.Builder()
                        .setUri(Uri.parse(url))
                        .setMediaId("video:" + hashCode)
                        .build());
            }
            // 轨道视频 rtsp
            else if (metaType == PlayerType.MetaType.VIDEO_RTSP && urlType == PlayerType.UrlType.VIDEO) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaSource -> track video, type = rtsp, url = " + url);
                }

                Class<?> cls = Class.forName("rtsp.RtspMediaSource$Factory");
                Constructor<?> constructor = cls.getDeclaredConstructor(DataSource.Factory.class);
                constructor.setAccessible(true);

                DataSource.Factory obj = buildDefaultDataSource(context, httpFactory);
                DataSource.Factory factory = (DataSource.Factory) constructor.newInstance(obj);
                return ((MediaSource.Factory) factory).createMediaSource(new MediaItem.Builder()
                        .setUri(Uri.parse(url))
                        .setMediaId("video:" + hashCode)
                        .build());
            }
            // 轨道视频 dash
            else if (metaType == PlayerType.MetaType.VIDEO_DASH && urlType == PlayerType.UrlType.VIDEO) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaSource -> track video, type = dash, url = " + url);
                }

                DataSource.Factory obj = buildDefaultDataSource(context, httpFactory);
                DashMediaSource.Factory factory = new DashMediaSource.Factory(obj);

                return ((MediaSource.Factory) factory).createMediaSource(new MediaItem.Builder()
                        .setUri(Uri.parse(url))
                        .setMediaId("video:" + hashCode)
                        .build());
            }
            // 轨道视频 hls
            else if (metaType == PlayerType.MetaType.VIDEO_HLS && urlType == PlayerType.UrlType.VIDEO) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaSource -> track video, type = hls, url = " + url);
                }

                HlsMediaSource.Factory factory = buildHlsMediaSourceFactory(context, httpFactory, args, PlayerType.UrlType.VIDEO, urlItem);
                return ((MediaSource.Factory) factory).createMediaSource(new MediaItem.Builder()
                        .setUri(Uri.parse(url))
                        .setMediaId("video:" + hashCode)
                        .build());
            }
            // 轨道视频 SmoothStreaming
            else if (metaType == PlayerType.MetaType.VIDEO_SS && urlType == PlayerType.UrlType.VIDEO) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaSource -> track video, type = SmoothStreaming, url = " + url);
                }

                DataSource.Factory obj = buildDefaultDataSource(context, httpFactory);
                SsMediaSource.Factory factory = new SsMediaSource.Factory(obj);
                return ((MediaSource.Factory) factory).createMediaSource(new MediaItem.Builder()
                        .setUri(Uri.parse(url))
                        .setMediaId("video:" + hashCode)
                        .build());
            }
            // 轨道视频 mp4
            else if (metaType == PlayerType.MetaType.VIDEO_MP4 && urlType == PlayerType.UrlType.VIDEO) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaSource -> track video, type = mp4, url = " + url);
                }

                DataSource.Factory factory = buildDefaultDataSource(context, httpFactory);
                return new ProgressiveMediaSource.Factory(factory).createMediaSource(new MediaItem.Builder()
                        .setUri(Uri.parse(url))
                        .setMediaId("video:" + hashCode)
                        .build());
            }
            // 轨道视频 def
            else {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaSource -> track video, type = def, url = " + url);
                }

                DataSource.Factory factory = buildDefaultDataSource(context, httpFactory);
                return new DefaultMediaSourceFactory(factory)
                        .createMediaSource(new MediaItem.Builder()
                                .setUri(Uri.parse(url))
                                .setMediaId("video:" + hashCode)
                                .build());
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
            String baseUrl = new StringBuilder().append(uri.getScheme())
                    .append("://")
                    .append(uri.getHost())
                    .append(path)
                    .toString();
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

    private String formatCacheKey(String url) {
        try {
            if (null == url)
                throw new Exception("error: url null");
            if (url.isEmpty())
                throw new Exception("error: url isEmpty");
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
            if (null == uri)
                throw new Exception("error: uri null");
            String newKey = new StringBuilder().append(uri.getScheme())
                    .append("://")
                    .append(uri.getHost())
                    .append(uri.getPath())
                    .toString();
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
            if (!matcher.find())
                throw new Exception("error: not find1");
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
            if (null == mSimpleCache)
                throw new Exception("warning: mSimpleCache null");
            if (null == loadEventInfo)
                throw new Exception("warning: loadEventInfo null");
            if (null == mediaLoadData)
                throw new Exception("warning: mediaLoadData null");

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
                if (null == uri)
                    throw new Exception("warning: uri null");

                String segmentUrl = uri.toString();
                if (segmentUrl.isEmpty())
                    throw new Exception("warning: segmentUrl isEmpty");

                int segmentPosition = formatSegmentPosition(segmentUrl);
                if (segmentPosition < 0)
                    throw new Exception("warning: segmentPosition < 0");

                if (null == mHlsSpanInfos) {
                    mHlsSpanInfos = new HlsSpanList();
                }
                HlsSpanInfo spanInfos = mHlsSpanInfos.get(segmentPosition);
                if (null != spanInfos)
                    throw new Exception("warning: spanInfos already contains, segmentPosition = " + segmentPosition);

                String cacheKey = formatCacheKey(segmentUrl);
                if (cacheKey.isEmpty())
                    throw new Exception("warning: cacheKey isEmpty");

                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "loadHlsSpanInfo -> .m3u8 缓存文件, cacheKey = " + cacheKey + ", segmentUrl = " + segmentUrl);
                }

                NavigableSet<CacheSpan> cachedSpans = mSimpleCache.getCachedSpans(cacheKey);
                if (cachedSpans.isEmpty())
                    throw new Exception("warning: cachedSpans isEmpty");

                for (CacheSpan span : cachedSpans) {
                    if (null == span)
                        continue;
                    if (!span.isCached)
                        continue;
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

    private DataSource.Factory buildDefaultDataSource(Context context,
                                                      HttpDataSource.Factory httpFactory) {

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "buildDefaultDataSource -> mSimpleCache = " + mSimpleCache);
        }

        try {

            if (null == mSimpleCache)
                throw new Exception("error: mSimpleCache null");

            return new CacheDataSource.Factory()
                    .setFlags(
                            // 当发生错误时忽略缓存（比如错误时不读取缓存，直接请求源数据）。
                            CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR
                                    // 对于未设置长度的请求忽略缓存（比如请求体长度未知时不使用缓存）。
                                    | CacheDataSource.FLAG_IGNORE_CACHE_FOR_UNSET_LENGTH_REQUESTS
                    )
                    .setCache(mSimpleCache)
                    // 网络请求工厂
                    .setUpstreamDataSourceFactory(httpFactory)
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


    private HlsMediaSource.Factory buildHlsMediaSourceFactory(Context context,
                                                              HttpDataSource.Factory httpFactory,
                                                              StartArgs args,
                                                              @PlayerType.UrlType.Value
                                                              int urlType,
                                                              UrlArgs.Item item) {

        DataSource.Factory factory = buildDefaultDataSource(context, httpFactory);

        HlsMediaSource.Factory hlsMediaSource = new HlsMediaSource.Factory(factory)
                // 播放器可以跳过「预加载切片」的步骤，仅解析 M3U8 元数据就完成准备，从而加快播放启动速度，但可能牺牲首帧加载的稳定性。
                .setAllowChunklessPreparation(true);

        //
        hlsMediaSource.setLoadErrorHandlingPolicy(new DefaultLoadErrorHandlingPolicy() {

            @Override
            public FallbackSelection getFallbackSelectionFor(FallbackOptions fallbackOptions, LoadErrorInfo loadErrorInfo) {
                FallbackSelection fallbackSelectionFor = super.getFallbackSelectionFor(fallbackOptions, loadErrorInfo);
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildHlsMediaSourceFactory -> getFallbackSelectionFor -> fallbackSelectionFor = " + fallbackSelectionFor);
                }
                return fallbackSelectionFor;
            }

            @Override
            public void onLoadTaskConcluded(long l) {

                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildHlsMediaSourceFactory -> onLoadTaskConcluded -> l = " + l);
                }

                super.onLoadTaskConcluded(l);
            }

            @Override
            public int getMinimumLoadableRetryCount(int i) {
                int minimumLoadableRetryCount = super.getMinimumLoadableRetryCount(i);
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildHlsMediaSourceFactory -> getRetryDelayMsFor -> minimumLoadableRetryCount = " + minimumLoadableRetryCount);
                }
                return minimumLoadableRetryCount;
            }

            @Override
            public long getRetryDelayMsFor(LoadErrorInfo loadErrorInfo) {
                long retryDelayMsFor = super.getRetryDelayMsFor(loadErrorInfo);
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildHlsMediaSourceFactory -> getRetryDelayMsFor -> retryDelayMsFor = " + retryDelayMsFor);
                }
                return retryDelayMsFor;
            }
        });

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
