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
import androidx.media3.common.MimeTypes;
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
import androidx.media3.datasource.ResolvingDataSource;
import androidx.media3.datasource.cache.CacheDataSink;
import androidx.media3.datasource.cache.CacheDataSource;
import androidx.media3.datasource.cache.CacheSpan;
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor;
import androidx.media3.datasource.cache.SimpleCache;
import androidx.media3.exoplayer.DecoderReuseEvaluation;
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
import androidx.media3.exoplayer.trackselection.TrackSelector;
import androidx.media3.exoplayer.upstream.BandwidthMeter;
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
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lib.kalu.mediaplayer.PlayerSDK;
import lib.kalu.mediaplayer.bean.args.StartArgs;
import lib.kalu.mediaplayer.bean.args.UrlArgs;
import lib.kalu.mediaplayer.bean.cache.Cache;
import lib.kalu.mediaplayer.bean.configuration.AdaptiveConfiguration;
import lib.kalu.mediaplayer.bean.configuration.BufferConfiguration;
import lib.kalu.mediaplayer.bean.configuration.LiveConfiguration;
import lib.kalu.mediaplayer.bean.configuration.StuckConfiguration;
import lib.kalu.mediaplayer.bean.configuration.TimeoutConfiguration;
import lib.kalu.mediaplayer.bean.info.HlsSpanInfo;
import lib.kalu.mediaplayer.bean.info.TrackInfo;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.collect.HlsSpanList;
import lib.kalu.mediaplayer.core.kernel.video.VideoBasePlayer;
import lib.kalu.mediaplayer.core.kernel.video.mediax.hls.CusDefaultHlsExtractorFactory;
import lib.kalu.mediaplayer.core.kernel.video.mediax.hls.CusDefaultHttpDataSource;
import lib.kalu.mediaplayer.core.kernel.video.mediax.hls.CusHlsLoadErrorHandlingPolicy;
import lib.kalu.mediaplayer.core.kernel.video.mediax.hls.CusHlsPlaylistParserFactory;
import lib.kalu.mediaplayer.proxy.ProxyUrl;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.util.M3u8GeneratorUtil;
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
//        if (LogUtil.DEBUG) {
//            LogUtil.log(TAG, "setStartArgs -> mStartArgs = " + mStartArgs);
//        }
    }

    @Override
    public StartArgs getStartArgs() {
//        if (LogUtil.DEBUG) {
//            LogUtil.log(TAG, "getStartArgs -> mStartArgs = " + mStartArgs);
//        }
        return mStartArgs;
    }

    @Override
    public ExoPlayer getPlayer() {
        return mExoPlayer;
    }

    @Override
    public void releaseDecoder() {
        try {
            if (null == mExoPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "releaseDecoder -> mExoPlayer error: null");
                }
                return;
            }
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
            if (null != mExoPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "checkDecoder -> warning: null != mExoPlayer");
                }
                return;
            }
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "checkDecoder ->");
            }

            if (null == startArgs) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "checkDecoder -> error: startArgs null");
                }
                return;
            }

            LiveConfiguration liveConfiguration = startArgs.getLiveConfiguration();
            AdaptiveConfiguration adaptiveConfiguration = startArgs.getAdaptiveConfiguration();
            StuckConfiguration stuckConfiguration = startArgs.getStuckConfiguration();
            BufferConfiguration bufferConfiguration = startArgs.getBufferConfiguration();

            DefaultBandwidthMeter defaultBandwidthMeter = VideoMediaxConfig.createDefaultBandwidthMeter(context);
            defaultBandwidthMeter.addEventListener(new Handler(android.os.Looper.getMainLooper()), new BandwidthMeter.EventListener() {
                /**
                 * 带宽采样回调：当网络数据传输完成一个采样周期并更新带宽估算值时调用。
                 *
                 * @param elapsedMs 本次采样统计的实际网络传输耗时（单位：毫秒）。
                 * @param bytesTransferred 本次采样期间实际传输（下载）的数据量大小（单位：字节 Byte）。
                 * @param bitrateEstimate 播放器当前根据历史采样滑动窗口计算出的【估算可用带宽】（单位：比特每秒 bps，即 bits/s）。
                 *                        注意：转换成常见的 kbps 需要除以 1000（或 1024），转成 Mbps 除以 1,000,000。
                 */
                @Override
                public void onBandwidthSample(int elapsedMs, long bytesTransferred, long bitrateEstimate) {

                    // 1. 获取当前估算网速 (转换为 kbps)
                    long bitrateKbps = bitrateEstimate / 1000;

                    // 2. 计算本次单次请求的即时瞬时速率 (bps)
                    long instantBitrateBps = elapsedMs > 0 ? (bytesTransferred * 8 * 1000L) / elapsedMs : 0;

                }
            });

            ExoPlayer.Builder builder = new ExoPlayer.Builder(context)
                    // 核心：配置缓冲卡死超时（解决你最初的 StuckPlayerException）
                    .setStuckBufferingDetectionTimeoutMs(stuckConfiguration.getBufferingDetectionTimeoutMs())
                    // 配置播放状态卡死超时（画面/声音静止检测）
                    .setStuckPlayingDetectionTimeoutMs(stuckConfiguration.getPlayingDetectionTimeoutMs())
                    // 配置播放未结束卡死超时（播放完成异常检测）
                    .setStuckPlayingNotEndingTimeoutMs(stuckConfiguration.getPlayingNotEndingTimeoutMs())
                    // 配置抑制状态卡死超时（后台播放/焦点丢失检测）
                    .setStuckSuppressedDetectionTimeoutMs(stuckConfiguration.getSuppressedDetectionTimeoutMs())
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
                    .setTrackSelector(VideoMediaxConfig.createTrackSelector(context, adaptiveConfiguration))
                    // 配置带宽测量器
                    .setBandwidthMeter(defaultBandwidthMeter)
                    // 增大内存缓存（默认 2MB，按需调整）
                    .setLoadControl(VideoMediaxConfig.createLoadControl(bufferConfiguration))
                    // 直播场景
                    .setLivePlaybackSpeedControl(VideoMediaxConfig.createLivePlaybackSpeedControl(liveConfiguration));


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
                    LogUtil.log(TAG, "checkDecoder -> default all");
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
                LogUtil.log(TAG, "checkDecoder -> " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void startDecoder(Context context, StartArgs startArgs) {
        try {
            if (null == mExoPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "startDecoder -> mExoPlayer error: null");
                }
                return;
            }
            onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.READY);
            // 缓存
            boolean initSimpleCache = initSimpleCache(context, startArgs);
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "startDecoder -> initSimpleCache = " + initSimpleCache);
            }
            TimeoutConfiguration timeoutConfiguration = startArgs.getTimeoutConfiguration();
            int connectTimoutMs = timeoutConfiguration.getConnectTimeoutMs();
            ProxyUrl proxyUrl = startArgs.getProxyUrl();
            boolean noProxy = startArgs.isNoProxy();
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "startDecoder -> connectTimoutMs = " + connectTimoutMs + ", noProxy = " + noProxy + ", proxyUrl = " + proxyUrl);
            }
            // HttpClient
            CusDefaultHttpDataSource.Factory baseHttpDataSourceFactory = new CusDefaultHttpDataSource.Factory(proxyUrl, noProxy).setUserAgent(Util.getUserAgent(context, context.getApplicationInfo().packageName)).setConnectTimeoutMs(connectTimoutMs).setReadTimeoutMs(connectTimoutMs).setDefaultRequestProperties(new HashMap<>()).setAllowCrossProtocolRedirects(true).setKeepPostFor302Redirects(true);
            // 2. 用 ResolvingDataSource 包装它
            ResolvingDataSource.Factory httpFactory = new ResolvingDataSource.Factory(
                    baseHttpDataSourceFactory,
                    new ResolvingDataSource.Resolver() {

                        final HashMap mapQueryParameter = new HashMap<String, String>();

                        @Override
                        public DataSpec resolveDataSpec(DataSpec dataSpec) {

                            // 发起请求之前的Url
                            String dataUrl = dataSpec.uri.toString();
                            if (LogUtil.DEBUG) {
                                LogUtil.log(TAG, "startDecoder -> resolveDataSpec, dataSpec.uri = " + dataUrl);
                            }

                            if (null != proxyUrl) {

                                // 回调：每次发起请求
                                proxyUrl.formatOpen(dataUrl);

                                // ts
                                if (dataUrl.contains("childSegmentUrl=1")) {
                                    Uri parse = Uri.parse(dataUrl);
                                    String playlistUrl = parse.getQueryParameter("playlistUrl");
                                    Set<String> queryParameterNames = parse.getQueryParameterNames();
                                    Uri.Builder builder = parse.buildUpon().clearQuery();
                                    if (null != queryParameterNames && !queryParameterNames.isEmpty()) {
                                        for (String key : queryParameterNames) {
                                            if ("childSegmentUrl".equals(key))
                                                continue;
                                            if ("playlistUrl".equals(key))
                                                continue;
                                            String value = parse.getQueryParameter(key);
                                            builder.appendQueryParameter(key, value);
                                        }
                                    }
                                    String segmentUrl = builder.toString();
                                    String newUrl = proxyUrl.formatSegmentUrl(playlistUrl, segmentUrl);
                                    return dataSpec.buildUpon()
                                            .setUri(newUrl)
                                            .build();
                                }
                                // vtt
                                else if (dataUrl.contains(PlayerType.SchemeType._VTT) || dataUrl.contains(PlayerType.SchemeType._VTT_)) {
                                    String newUrl = proxyUrl.formatSubtitleUrl(dataUrl);
                                    return dataSpec.buildUpon()
                                            .setUri(newUrl)
                                            .build();
                                }
                                // ssa
                                else if (dataUrl.contains(PlayerType.SchemeType._SSA) || dataUrl.contains(PlayerType.SchemeType._SSA_)) {
                                    String newUrl = proxyUrl.formatSubtitleUrl(dataUrl);
                                    return dataSpec.buildUpon()
                                            .setUri(newUrl)
                                            .build();
                                }
                                // m3u8 child
                                else if (dataUrl.contains("childPlaylistUrl=1")) {

                                    Uri parse = Uri.parse(dataUrl);
                                    String playlistUrl = parse.getQueryParameter("playlistUrl");

                                    Set<String> queryParameterNames = parse.getQueryParameterNames();
                                    Uri.Builder builder = parse.buildUpon().clearQuery();
                                    if (null != queryParameterNames && !queryParameterNames.isEmpty()) {
                                        for (String key : queryParameterNames) {
                                            if ("childPlaylistUrl".equals(key))
                                                continue;
                                            if ("playlistUrl".equals(key))
                                                continue;
                                            String value = parse.getQueryParameter(key);
                                            builder.appendQueryParameter(key, value);
                                        }
                                    }
                                    String segmentUrl = builder.toString();
                                    String newUrl = proxyUrl.formatChildM3u8Url(playlistUrl, segmentUrl);
                                    return dataSpec.buildUpon()
                                            .setUri(newUrl)
                                            .build();
                                }
                                // m3u8
                                else if (dataUrl.contains(PlayerType.SchemeType._M3U8) || dataUrl.contains(PlayerType.SchemeType._M3U8_)) {
                                    String newUrl = proxyUrl.formatM3u8Url(dataUrl);
                                    return dataSpec.buildUpon()
                                            .setUri(newUrl)
                                            .build();
                                }
                            }

                            return dataSpec;
                        }
                    }
            );

            UrlArgs urlArgs = startArgs.getUrlArgs();
            int streamType = urlArgs.getStreamType();
            // 有 外挂轨道
            if (streamType == PlayerType.StreamType.MERGE_ALL) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "startDecoder -> 外挂轨道 有 MERGE_ALL");
                }

                ArrayList<MediaSource> listMediaSource = new ArrayList<MediaSource>();

                List<UrlArgs.Item> allStreams = urlArgs.getAllStreams();
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "startDecoder -> allStreams.size = " + allStreams.size());
                }

                for (UrlArgs.Item item : allStreams) {

                    if (item.getParser() == PlayerType.ParserType.SUBTITLE) {
                        MediaSource mainMediaSource = buildMediaSource(context, httpFactory, startArgs, PlayerType.UrlType.SUBTITLE, item);
                        if (null != mainMediaSource) {
                            listMediaSource.add(mainMediaSource);
                        }
                    } else if (item.getParser() == PlayerType.ParserType.AUDIO) {
                        MediaSource mainMediaSource = buildMediaSource(context, httpFactory, startArgs, PlayerType.UrlType.AUDIO, item);
                        if (null != mainMediaSource) {
                            listMediaSource.add(mainMediaSource);
                        }
                    } else {
                        MediaSource mainMediaSource = buildMediaSource(context, httpFactory, startArgs, PlayerType.UrlType.VIDEO, item);
                        if (null != mainMediaSource) {
                            listMediaSource.add(mainMediaSource);
                        }
                    }
                }


                int size = listMediaSource.size();
                if (size == 0) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "startDecoder -> error: listMediaSource isEmpty");
                    }

                    onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.ERROR_DECODE);
                    stop();
                    return;
                }

                MediaSource[] mediaSources = new MediaSource[listMediaSource.size()];
                for (int i = 0; i < size; i++) {
                    mediaSources[i] = listMediaSource.get(i);
                }

                MergingMediaSource mergingMediaSource = new MergingMediaSource(mediaSources);
                mExoPlayer.setMediaSource(mergingMediaSource);
            }
            // 有 外挂轨道 自己拼装playlist, 所有的m3u8必须是单层的
            else if (streamType == PlayerType.StreamType.FORMAT_MULTI_VARIANT_PLAYLIST) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "startDecoder -> 外挂轨道 有 FORMAT_MULTI_VARIANT_PLAYLIST");
                }

                String m3u8Data = M3u8GeneratorUtil.formatMasterM3u8DataUri(urlArgs);
                if (LogUtil.DEBUG) {
                    String m3u8Path = M3u8GeneratorUtil.saveCacheM3u8Path(context, urlArgs);
                    LogUtil.log(TAG, "startDecoder -> m3u8Path = " + m3u8Path);
                }
                UrlArgs.Item build = UrlArgs.Item.newBuilder().setUrl(m3u8Data).build();
                MediaSource multivariantMediaSource = buildMediaSource(context, httpFactory, startArgs, PlayerType.UrlType.DATA_HLS_MULTIVARIANT_PLAYLIST, build);
                if (null == multivariantMediaSource) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "startDecoder -> error: multivariantMediaSource null");
                    }

                    onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.ERROR_DECODE);
                    stop();
                    return;
                }
                mExoPlayer.setMediaSource(multivariantMediaSource);
            }
            // 无 外挂轨道
            else {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "startDecoder -> 外挂轨道 无");
                }

                UrlArgs.Item defaultItem = urlArgs.getDefaultStreamItem();
                if (null == defaultItem) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "startDecoder -> error: defaultItem null");
                    }

                    onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.ERROR_DECODE);
                    stop();
                    return;
                }

                MediaSource onlyMainMediaSource = buildMediaSource(context, httpFactory, startArgs, PlayerType.UrlType.VIDEO, defaultItem);
                if (null == onlyMainMediaSource) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "startDecoder -> error: onlyMainMediaSource null");
                    }

                    onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.ERROR_DECODE);
                    stop();
                    return;
                }

                mExoPlayer.setMediaSource(onlyMainMediaSource);
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
            if (null == mExoPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "initOptions -> mExoPlayer warning: null");
                }
                return;
            }
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
            if (null == mExoPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "initOptions -> error: mExoPlayer null");
                }
                return;
            }
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
            if (null == mExoPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "setSurface -> error: mExoPlayer null");
                }
                return;
            }
            mExoPlayer.clearVideoSurface();
            if (null == surface) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "setSurface -> error: surface null");
                }
                return;
            }
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
            if (!isPrepared) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "isPlaying -> mPrepared warning: false");
                }
                return false;
            }
            if (null == mExoPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "isPlaying -> mExoPlayer error: null");
                }
                return false;
            }
            int state = mExoPlayer.getPlaybackState();
            if (state == Player.STATE_BUFFERING || state == Player.STATE_READY) {
                return mExoPlayer.getPlayWhenReady();
            } else if (state == Player.STATE_IDLE || state == Player.STATE_ENDED) {
                return false;
            } else {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "isPlaying -> not find");
                }
                return false;
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

            if (null == mExoPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "seekToDefaultPosition -> error: mExoPlayer null");
                }
                return;
            }

            boolean live = isLiveStream();
            if (!live) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "seekToDefaultPosition -> warning: live false");
                }
                return;
            }

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

            if (seek < 0L) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "seekTo -> error: seek < 0");
                }
                return;
            }
            if (null == mExoPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "seekTo -> error: mExoPlayer null");
                }
                return;
            }
            StartArgs args = getStartArgs();
            if (null == args) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "seekTo -> error: args null");
                }
                return;
            }

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
            if (null == mExoPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "isLiveStream -> mExoPlayer error: null");
                }
                return false;
            }
            boolean live = super.isLiveStream();
            if (live) {
                return true;
            } else {
                return mExoPlayer.isCurrentMediaItemLive();
//                // Media3 中 Timeline 和 Window 的使用方式
//                Timeline timeline = mExoPlayer.getCurrentTimeline();
//                if (timeline.isEmpty()) throw new Exception("error: timeline is empty");
//                int windowIndex = mExoPlayer.getCurrentWindowIndex();
//                Timeline.Window window = new Timeline.Window();
//                timeline.getWindow(windowIndex, window, C.TIME_UNSET);
//                // Media3 中判断是否为直播
//                return window.isLive();
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
            if (!isPrepared) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "getPosition -> mPrepared warning: false");
                }
                return 0L;
            }
            if (null == mExoPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "getPosition -> mExoPlayer error: null");
                }
                return 0L;
            }

            boolean live = isLiveStream();
            // Media3 中判断是否为直播
            if (live) {

                // Media3 中 Timeline 和 Window 的使用方式
                Timeline timeline = mExoPlayer.getCurrentTimeline();
                if (timeline.isEmpty()) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "getPosition -> error: timeline is empty");
                    }
                    return 0L;
                }

                int windowIndex = mExoPlayer.getCurrentWindowIndex();
                Timeline.Window window = new Timeline.Window();
                timeline.getWindow(windowIndex, window, C.TIME_UNSET);

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
                if (currentPosition < 0) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "getPosition -> currentPosition warning: " + currentPosition);
                    }
                    return 0L;
                }
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
            if (!isPrepared) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "getDuration -> mPrepared warning: false");
                }
                return 0L;
            }
            if (null == mExoPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "getDuration -> mExoPlayer error: null");
                }
                return 0L;
            }

            // Media3 中判断是否为直播
            boolean live = isLiveStream();
            if (live) {

                // Media3 中 Timeline 和 Window 的使用方式
                Timeline timeline = mExoPlayer.getCurrentTimeline();
                if (timeline.isEmpty()) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "getDuration -> error: timeline is empty");
                    }
                    return 0L;
                }

                int windowIndex = mExoPlayer.getCurrentWindowIndex();
                Timeline.Window window = new Timeline.Window();
                timeline.getWindow(windowIndex, window, C.TIME_UNSET);

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
                if (duration <= 0) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "getDuration -> duration warning: " + duration);
                    }
                    return 0L;
                }
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
            if (null == mExoPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "setSpeed -> mMediaPlayer error: null");
                }
                return;
            }
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
            if (null == mExoPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "getSpeed -> mMediaPlayer error: null");
                }
                return 1.0f;
            }
            return mExoPlayer.getPlaybackParameters().speed;
        } catch (Exception e) {
            return 1.0f;
        }
    }

    @Override
    public void setVolume(float v1, float v2) {
        try {
            if (null == mExoPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "setVolume -> mExoPlayer error: null");
                }
                return;
            }
            float volume = Math.max(v1, v2);
            if (volume < 0) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "setVolume -> error: volume < 0");
                }
                return;
            }
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
            if (null == mExoPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "getVolume -> mExoPlayer error: null");
                }
                return 0f;
            }
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
            if (null == mExoPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "registListener -> error: mExoPlayer null");
                }
                return;
            }
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
            if (null == mExoPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "unRegistListener -> error: mExoPlayer null");
                }
                return;
            }
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
            if (null == mSimpleCache) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "unInitSimpleCache -> warning: mSimpleCache null");
                }
                return false;
            }
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

            boolean enableCache = args.isEnableCache();
            if (!enableCache) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "initSimpleCache -> error: enableCache not open");
                }
                return false;
            }


            boolean liveStream = args.isLiveStream();
            if (liveStream) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "initSimpleCache -> error: liveStream true");
                }
                return false;
            }

            boolean containsVideoUrl = args.containsVideoUrl();
            if (!containsVideoUrl) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "initSimpleCache -> error: containsVideoUrl false");
                }
                return false;
            }

            String url = args.getUrl();
            if (url.startsWith(PlayerType.SchemeType.FILE)) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "initSimpleCache -> error: url is file");
                }
                return false;
            }

            Cache cache = PlayerSDK.cache;
            if (null == cache) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "initSimpleCache -> error: cache null");
                }
                return false;
            }

            boolean cacheEnable = cache.isEnable();
            if (!cacheEnable) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "initSimpleCache -> error: cacheEnable false");
                }
                return false;
            }

            int sizeMB = cache.getSizeMB();
            if (sizeMB <= 0) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "initSimpleCache -> error: sizeMB <= 0, sizeMB = " + sizeMB);
                }
                return false;
            }

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
            if (null == mExoPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "start -> mExoPlayer error: null");
                }
                return;
            }
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
            if (!isPrepared) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "resume -> mPrepared warning: false");
                }
                return;
            }
            if (null == mExoPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "resume -> mMediaPlayer error: null");
                }
                return;
            }

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
            if (!isPrepared) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "pause -> mPrepared warning: false");
                }
                return;
            }
            if (null == mExoPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "pause -> mMediaPlayer error: null");
                }
                return;
            }
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
            if (null == mExoPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "stop -> mExoPlayer error: null");
                }
            } else {
                mExoPlayer.pause();
                mExoPlayer.stop();
            }
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
            if (null == mExoPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "release -> error: mExoPlayer null");
                }
            } else {
                mExoPlayer.setVideoSurface(null);
                mExoPlayer.release();
                mExoPlayer = null;
            }
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
         * @param bitrateEstimate  估算带宽（bps，比特每秒）
         */
        @Override
        public void onBandwidthEstimate(EventTime eventTime,
                                        int totalLoadTimeMs,
                                        long totalBytesLoaded,
                                        long bitrateEstimate) {

            if (totalLoadTimeMs <= 0) {
                return;
            }

            // ========== 单位换算修正 ==========
            // bitrateEstimate 单位: bps (bit/s)
            // 1 KB/s = 1024 Byte/s = 1024 *8 bit/s = 8192 bit/s
            // 估算带宽 → KB/s (千字节每秒)
            long estimateKBs = bitrateEstimate / 8 / 1024L;

            // 自己计算实际平均速度：totalBytesLoaded 是总字节，totalLoadTimeMs总耗时ms
            // bytes *1000 → 转成每秒字节，再 /1024 → KB/s
            long realAvgKBs = (totalBytesLoaded * 1000L) / totalLoadTimeMs / 1024L;

            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onBandwidthEstimate -> totalLoadTimeMs = " + totalLoadTimeMs
                        + ", estimateKBs(估算KB/s) = " + estimateKBs
                        + ", realAvgKBs(实际平均KB/s) = " + realAvgKBs);
            }

            onUpdateBandwidth(PlayerType.KernelType.MEDIA_V3, totalLoadTimeMs, estimateKBs, realAvgKBs);

            // 检测 网络卡顿
            if (mExoPlayer != null) {
                // 1. 获取码率（多重尝试）
                Format format = mExoPlayer.getVideoFormat();
                long videoBitrate = Format.NO_VALUE;
                if (format != null) {
                    if (format.bitrate != Format.NO_VALUE) {
                        videoBitrate = format.bitrate;
                    } else if (format.averageBitrate != Format.NO_VALUE) {
                        videoBitrate = format.averageBitrate;
                    } else if (format.peakBitrate != Format.NO_VALUE) {
                        videoBitrate = format.peakBitrate;
                    }
                }

                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "onBandwidthEstimate -> videoBitrate = " + videoBitrate + ", bitrateEstimate = " + bitrateEstimate);
                }

                // 2. 如果能取到码率：走码率比对逻辑
                if (videoBitrate > 0) {
                    if (bitrateEstimate > 0 && bitrateEstimate < videoBitrate * 0.75f) {
                        onUpdateStuckNet(PlayerType.KernelType.MEDIA_V3, videoBitrate, bitrateEstimate);
                    }
                } else {
                    // 3. 兜底方案：无法获取码率时，结合当前可用缓冲时长判定
                    long bufferedDurationMs = mExoPlayer.getTotalBufferedDuration(); // 当前已缓存时长
                    boolean isLoading = mExoPlayer.isLoading(); // 是否正在下载数据

                    // 若正在下载，但可用缓冲已不足 1.5 秒，且估算带宽偏低（例如低于 800 kbps），判定为网络紧张
                    if (isLoading && bufferedDurationMs < 1500 && bitrateEstimate < 800_000) {
                        onUpdateStuckNet(PlayerType.KernelType.MEDIA_V3, -1, bitrateEstimate);
                    }
                }
            }

//            if (null != mExoPlayer) {
//                Format format = mExoPlayer.getVideoFormat();
//                if (LogUtil.DEBUG) {
//                    LogUtil.log(TAG, "onBandwidthEstimate -> format = " + format);
//                }
//                if (format != null && format.bitrate != Format.NO_VALUE) {
//                    long videoBitrate = format.bitrate;
//                    if (LogUtil.DEBUG) {
//                        LogUtil.log(TAG, "onBandwidthEstimate -> videoBitrate = " + videoBitrate + ", bitrateEstimate = " + bitrateEstimate);
//                    }
//                    // 规则：估算下行带宽低于当前码率的 75%，判定为网络卡顿/弱网
//                    if (bitrateEstimate > 0 && bitrateEstimate < videoBitrate * 0.75f) {
//                        onUpdateStuckNet(PlayerType.KernelType.MEDIA_V3, videoBitrate, bitrateEstimate);
//                    }
//                }
//            }
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

        @Override
        public void onLoadCanceled(EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {

            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onLoadCanceled -> loadEventInfo.loadDurationMs = " + loadEventInfo.loadDurationMs + ", loadEventInfo.elapsedRealtimeMs = " + loadEventInfo.elapsedRealtimeMs);
            }
        }

        @Override
        public void onLoadStarted(EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData, int i) {

            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onLoadStarted -> loadEventInfo.loadDurationMs = " + loadEventInfo.loadDurationMs + ", loadEventInfo.elapsedRealtimeMs = " + loadEventInfo.elapsedRealtimeMs + ", i = " + i);
            }
        }

        @Override
        public void onLoadCompleted(EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {

            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onLoadCompleted -> loadEventInfo.loadDurationMs = " + loadEventInfo.loadDurationMs + ", loadEventInfo.elapsedRealtimeMs = " + loadEventInfo.elapsedRealtimeMs);
            }

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
                if (live) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "onLoadCompleted -> warning: current is live");
                    }
                    return;
                }
                loadHlsSpanInfo(loadEventInfo, mediaLoadData);
            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "onLoadCompleted -> Exception: " + e.getMessage());
                }
            }
        }

        @Override
        public void onIsLoadingChanged(EventTime eventTime, boolean b) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onIsLoadingChanged -> b = " + b);
            }
        }

        /**
         * 主要用途：用于精细化排查音视频播放卡顿、埋点统计各轨道渲染器的首帧耗时与缓冲状态。
         * 渲染器就绪状态发生变化时的回调
         * （常用于 ExoPlayer / Media3 的 AnalyticsListener 监听器中）
         *
         * @param eventTime 包含当前事件发生时的时间戳、媒体周期等播放上下文信息的事件对象
         * @param rendererIndex 触发该事件的渲染器索引（Renderer Index，例如视频渲染器或音频渲染器）
         *                      对应播放器内部的具体渲染组件（例如 0 通常为视频渲染器，1 通常为音频渲染器，具体视轨道配置而定）。
         * @param rendererTrackGroupIndex 渲染器所选轨道组（TrackGroup）在当前时间线/清单中的索引
         * @param isReady 渲染器是否已处于就绪状态（true 表示已就绪/可继续渲染数据，false 表示尚未就绪/正在缓冲）
         *                当网络波动或解码缓冲不足时会变为 false；缓冲完成可继续渲染画面/声音时恢复为 true。
         */
        @Override
        public void onRendererReadyChanged(
                AnalyticsListener.EventTime eventTime,
                int rendererIndex,
                int rendererTrackGroupIndex,
                boolean isReady) {

            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onRendererReadyChanged -> rendererIndex = " + rendererIndex + ", isReady = " + isReady);
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
                if (!live) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "onTimelineChanged1 -> warning: current not live");
                    }
                    return;
                }

//                StartArgs startArgs = getStartArgs();
//                BufferConfiguration bufferConfiguration = startArgs.getBufferConfiguration();
////                long minLivePlaybackTimelineOffsetMs = bufferConfiguration.getMinLivePlaybackTimelineOffsetMs();
//                long currentPlaybackPositionMs = eventTime.currentPlaybackPositionMs;
//                if (LogUtil.DEBUG) {
//                    LogUtil.log(TAG, "onTimelineChanged1 -> minLivePlaybackTimelineOffsetMs = " + minLivePlaybackTimelineOffsetMs + ", currentPlaybackPositionMs = " + currentPlaybackPositionMs);
//                }
//
//                if (currentPlaybackPositionMs < minLivePlaybackTimelineOffsetMs) {
//                    if (LogUtil.DEBUG) {
//                        LogUtil.log(TAG, "onTimelineChanged1 -> seekToDefaultPosition");
//                    }
////                    onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.RETRY_BUFFERING_TIMEOUT);
//                }

            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "onTimelineChanged1 -> Exception: " + e.getMessage());
                }
            }

            // 缓存
            try {

                boolean live = isLiveStream();
                if (live) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "onTimelineChanged2 -> warning: current is live");
                    }
                    return;
                }

                if (null == mSimpleCache) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "onTimelineChanged2 -> warning: mSimpleCache null");
                    }
                    return;
                }

                Object currentManifest = mExoPlayer.getCurrentManifest();
                if (null == currentManifest) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "onTimelineChanged2 -> warning: currentManifest null");
                    }
                    return;
                }
                if (!(currentManifest instanceof HlsManifest)) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "onTimelineChanged2 -> warning: currentManifest not instanceof HlsManifest");
                    }
                    return;
                }
                HlsMediaPlaylist hlsMediaPlaylist = ((HlsManifest) currentManifest).mediaPlaylist;
                if (null == hlsMediaPlaylist) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "onTimelineChanged2 -> warning: hlsMediaPlaylist null");
                    }
                    return;
                }
                List<HlsMediaPlaylist.Segment> segments = hlsMediaPlaylist.segments;
                if (null == segments) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "onTimelineChanged2 -> warning: segments null");
                    }
                    return;
                }
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
                LogUtil.log(TAG, "onPlayerErrorChanged -> errorCode = " + errorCode + ", errorMessage = " + errorMessage, e.getCause());
            }
        }

        @Override
        public void onPlayerError(AnalyticsListener.EventTime eventTime, PlaybackException error) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onPlayerError -> errorCode = " + error.errorCode + ", errMessage" + error.getMessage(), error);
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
                    if (!isPrepared) {
                        if (LogUtil.DEBUG) {
                            LogUtil.log(TAG, "onPlaybackStateChanged -> state[Player.STATE_READY] -> warning: isPrepared false");
                        }
                        return;
                    }

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
                            if (playing) {
                                if (LogUtil.DEBUG) {
                                    LogUtil.log(TAG, "onPlaybackStateChanged -> state[Player.STATE_READY] -> warning: isPlaying true");
                                }
                                return;
                            }
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
                    if (!isPrepared) {
                        if (LogUtil.DEBUG) {
                            LogUtil.log(TAG, "onPlaybackStateChanged -> state[Player.STATE_BUFFERING] -> mPrepared warning: false");
                        }
                        return;
                    }
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
                if (null == args) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "onVideoInputFormatChanged -> error: args null");
                    }
                    return;
                }
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
                if (isPrepared) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "onVideoInputFormatChanged -> warning: isPrepared true");
                    }
                    return;
                }
                isPrepared = true;
                onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.MEDIA_INFO_PREPARE);
                onEvent(PlayerType.KernelType.MEDIA_V3, PlayerType.EventType.MEDIA_INFO_VIDEO_RENDERING_START);
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
                if (null == cues) {
                    return;
                }
                if (cues.size() == 0) {
                    return;
                }

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

            for (Tracks.Group group : tracks.getGroups()) {
                if (group.getType() == C.TRACK_TYPE_VIDEO) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "onTracksChanged -> group.id = " + group.getMediaTrackGroup().id);
                    }
                }
            }
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
            if (null == trackInfo) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "toggleTrack -> error: trackArgs null");
                }
                return false;
            }
            int groupIndex = trackInfo.getGroupIndex();
            if (groupIndex == -1) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "toggleTrack -> error: groupIndex == -1");
                }
                return false;
            }
            int trackIndex = trackInfo.getTrackIndex();
            if (trackIndex == -1) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "toggleTrack -> error: trackIndex == -1");
                }
                return false;
            }
            if (null == mExoPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "toggleTrack -> error: mExoPlayer null");
                }
                return false;
            }
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
            if (null == mExoPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "getTrackInfo -> error: mExoPlayer null");
                }
                return null;
            }

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
            if (list.isEmpty()) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "getTrackInfo -> error: list empty");
                }
                return null;
            }

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
            if (null == mHlsSpanInfos) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "getSegments -> warning: mHlsSpanInfo null");
                }
                return null;
            }
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
            if (null == mExoPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "subtitleOffsetMs -> error: mExoPlayer null");
                }
                return false;
            }
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

    private MediaSource buildMediaSource(Context context, ResolvingDataSource.Factory httpFactory, StartArgs startArgs, @PlayerType.UrlType.Value int urlType, UrlArgs.Item urlItem) {

        try {

            if (null == urlItem) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaSource -> error: urlItem null");
                }
                return null;
            }


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

                DataSource.Factory factory = buildDefaultDataSource(context, startArgs, httpFactory);
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
                    Object factory = buildDefaultDataSource(context, startArgs, httpFactory);
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

                DataSource.Factory obj = buildDefaultDataSource(context, startArgs, httpFactory);
                DataSource.Factory factory = (DataSource.Factory) constructor.newInstance(obj);
                MediaItem mediaItem = buildMediaItem(PlayerType.UrlType.VIDEO, startArgs, urlItem);
                return ((MediaSource.Factory) factory).createMediaSource(mediaItem);
            }
            // 轨道视频 dash
            else if (metaType == PlayerType.MetaType.VIDEO_DASH && urlType == PlayerType.UrlType.VIDEO) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaSource -> track video, type = dash, url = " + url);
                }

                DataSource.Factory obj = buildDefaultDataSource(context, startArgs, httpFactory);
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

                DataSource.Factory obj = buildDefaultDataSource(context, startArgs, httpFactory);
                SsMediaSource.Factory factory = new SsMediaSource.Factory(obj);
                MediaItem mediaItem = buildMediaItem(PlayerType.UrlType.VIDEO, startArgs, urlItem);
                return ((MediaSource.Factory) factory).createMediaSource(mediaItem);
            }
            // 轨道视频 mp4
            else if (metaType == PlayerType.MetaType.VIDEO_MP4 && urlType == PlayerType.UrlType.VIDEO) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaSource -> track video, type = mp4, url = " + url);
                }

                DataSource.Factory factory = buildDefaultDataSource(context, startArgs, httpFactory);
                MediaItem mediaItem = buildMediaItem(PlayerType.UrlType.VIDEO, startArgs, urlItem);
                return new ProgressiveMediaSource.Factory(factory).createMediaSource(mediaItem);
            }
            // 轨道视频 hls (本地拼接的索引文件)
            else if (urlType == PlayerType.UrlType.DATA_HLS_MULTIVARIANT_PLAYLIST) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaSource -> track video, type = data_hls_multivariant_playlist, url = " + url);
                }

                HlsMediaSource.Factory factory = buildHlsMediaSourceFactory(context, httpFactory, startArgs, PlayerType.UrlType.VIDEO, urlItem);
                MediaItem mediaItem = buildMediaItem(PlayerType.UrlType.DATA_HLS_MULTIVARIANT_PLAYLIST, startArgs, urlItem);
                return ((MediaSource.Factory) factory).createMediaSource(mediaItem);
            }
            // 轨道视频 def
            else {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaSource -> track video, type = def, url = " + url);
                }

                DataSource.Factory factory = buildDefaultDataSource(context, startArgs, httpFactory);
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

            if (null == urlItem) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaItem -> error: urlItem null");
                }
                return null;
            }
            String url = urlItem.getUrl();
            if (null == url || url.isEmpty()) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaItem -> error: url null or empty");
                }
                return null;
            }

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
                return new MediaItem.Builder()
                        .setUri(Uri.parse(url))
                        .setMediaId("audio:" + url.hashCode())
                        .setLiveConfiguration(liveConfiguration)
                        .build();
            } else if (urlType == PlayerType.UrlType.VIDEO) {
                return new MediaItem.Builder()
                        .setUri(Uri.parse(url))
                        .setMediaId("video:" + url.hashCode())
                        .setLiveConfiguration(liveConfiguration)
                        .build();
            } else if (urlType == PlayerType.UrlType.DATA_HLS_MULTIVARIANT_PLAYLIST) {
                return new MediaItem.Builder()
                        .setUri(Uri.parse(url))
                        .setMimeType(MimeTypes.APPLICATION_M3U8)
                        .setMediaId("video:" + url.hashCode())
                        .setLiveConfiguration(liveConfiguration)
                        .build();
            } else {
                return new MediaItem.Builder()
                        .setUri(Uri.parse(url))
                        .setLiveConfiguration(liveConfiguration)
                        .build();
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

            if (null == urlItem) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaItemSubtitleConfiguration -> error: urlItem null");
                }
                return null;
            }
            String url = urlItem.getUrl();
            if (null == url) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaItemSubtitleConfiguration -> error: url null");
                }
                return null;
            }
            if (url.isEmpty()) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaItemSubtitleConfiguration -> error: url isEmpty");
                }
                return null;
            }

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
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildMediaItemSubtitleConfiguration -> error: not support " + url);
                }
                return null;
            }

            String language = urlItem.getLanguage();
            String label = urlItem.getLabel();
            if (null == label) {
                label = language;
            }
            int selectionFlags;
            if (urlItem.isDefault()) {
                selectionFlags = C.SELECTION_FLAG_AUTOSELECT;
            } else {
                selectionFlags = 0;
            }
            int roleFlags;
            if (urlItem.isDefault()) {
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
            if (null == url) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "formatCacheKey -> error: url null");
                }
                return "";
            }
            if (url.isEmpty()) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "formatCacheKey -> error: url isEmpty");
                }
                return "";
            }
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
            if (null == uri) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "formatCacheKey -> error: uri null");
                }
                return "";
            }
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
            if (!matcher.find()) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "formatSegmentPosition -> error: not find1");
                }
                return -1;
            }
            String segmentPosition = matcher.group(1);
            if (null == segmentPosition || segmentPosition.isEmpty()) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "formatSegmentPosition -> error: not find2");
                }
                return -1;
            }
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
            if (null == mSimpleCache) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "loadHlsSpanInfo -> warning: mSimpleCache null");
                }
                return false;
            }
            if (null == loadEventInfo) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "loadHlsSpanInfo -> warning: loadEventInfo null");
                }
                return false;
            }
            if (null == mediaLoadData) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "loadHlsSpanInfo -> warning: mediaLoadData null");
                }
                return false;
            }

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
                if (null == uri) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "loadHlsSpanInfo -> warning: uri null");
                    }
                    return false;
                }

                String segmentUrl = uri.toString();
                if (segmentUrl.isEmpty()) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "loadHlsSpanInfo -> warning: segmentUrl isEmpty");
                    }
                    return false;
                }

                int segmentPosition = formatSegmentPosition(segmentUrl);
                if (segmentPosition < 0) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "loadHlsSpanInfo -> warning: segmentPosition < 0");
                    }
                    return false;
                }

                if (null == mHlsSpanInfos) {
                    mHlsSpanInfos = new HlsSpanList();
                }
                HlsSpanInfo spanInfos = mHlsSpanInfos.get(segmentPosition);
                if (null != spanInfos) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "loadHlsSpanInfo -> warning: spanInfos already contains, segmentPosition = " + segmentPosition);
                    }
                    return false;
                }

                String cacheKey = formatCacheKey(segmentUrl);
                if (cacheKey.isEmpty()) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "loadHlsSpanInfo -> warning: cacheKey isEmpty");
                    }
                    return false;
                }

                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "loadHlsSpanInfo -> .m3u8 缓存文件, cacheKey = " + cacheKey + ", segmentUrl = " + segmentUrl);
                }

                NavigableSet<CacheSpan> cachedSpans = mSimpleCache.getCachedSpans(cacheKey);
                if (cachedSpans.isEmpty()) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "loadHlsSpanInfo -> warning: cachedSpans isEmpty");
                    }
                    return false;
                }

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
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "loadHlsSpanInfo -> warning: not support mediaLoadData.dataType = " + mediaLoadData.dataType);
                }
                return false;
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
                                                      StartArgs startArgs,
                                                      ResolvingDataSource.Factory httpFactory) {

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "buildDefaultDataSource -> mSimpleCache = " + mSimpleCache);
        }

        try {

            boolean enableCache = startArgs.isEnableCache();
            if (!enableCache) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildDefaultDataSource -> error: enableCache not open");
                }
                return new DefaultDataSource.Factory(context, httpFactory);
            }

            boolean liveStream = startArgs.isLiveStream();
            if (liveStream) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildDefaultDataSource -> error: liveStream true");
                }
                return new DefaultDataSource.Factory(context, httpFactory);
            }

            if (null == mSimpleCache) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "buildDefaultDataSource -> error: mSimpleCache null");
                }
                return new DefaultDataSource.Factory(context, httpFactory);
            }

            return new CacheDataSource.Factory()
                    .setCache(mSimpleCache)
                    .setUpstreamDataSourceFactory(httpFactory)
                    .setCacheReadDataSourceFactory(new FileDataSource.Factory())
                    .setCacheWriteDataSinkFactory(new CacheDataSink.Factory()
                            .setCache(mSimpleCache)
                            .setFragmentSize(CacheDataSink.DEFAULT_FRAGMENT_SIZE))
                    .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "buildDefaultDataSource -> Exception: " + e.getMessage());
            }
            return new DefaultDataSource.Factory(context, httpFactory);
        }
    }

    private HlsMediaSource.Factory buildHlsMediaSourceFactory(Context context, ResolvingDataSource.Factory httpFactory, StartArgs args, @PlayerType.UrlType.Value int urlType, UrlArgs.Item item) {

        DataSource.Factory factory = buildDefaultDataSource(context, args, httpFactory);

        HlsMediaSource.Factory hlsMediaSource = new HlsMediaSource.Factory(factory)
                // 播放器可以跳过「预加载切片」的步骤，仅解析 M3U8 元数据就完成准备，从而加快播放启动速度，但可能牺牲首帧加载的稳定性。
                .setAllowChunklessPreparation(true);


        int retryCount = args.getRetryConfiguration().getRetryCount();
        //
        hlsMediaSource.setLoadErrorHandlingPolicy(new CusHlsLoadErrorHandlingPolicy(retryCount));
        // setPlaylistParserFactory
        hlsMediaSource.setPlaylistParserFactory(new CusHlsPlaylistParserFactory(args.getProxyUrl()));

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
        hlsMediaSource.setExtractorFactory(new CusDefaultHlsExtractorFactory(payloadReaderFactoryFlags, exposeCea608WhenMissingDeclarations));

        return hlsMediaSource;
    }
}