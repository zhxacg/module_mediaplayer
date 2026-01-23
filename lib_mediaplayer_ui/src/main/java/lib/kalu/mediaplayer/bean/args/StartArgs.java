package lib.kalu.mediaplayer.bean.args;


import org.json.JSONObject;

import java.io.Serializable;

import lib.kalu.mediaplayer.PlayerSDK;
import lib.kalu.mediaplayer.bean.menu.Menu;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.proxy.Proxy;
import lib.kalu.mediaplayer.proxy.ProxyUrl;
import lib.kalu.mediaplayer.util.LogUtil;

public class StartArgs implements Serializable {

    private String TAG = "StartArgs22";

    @PlayerType.DecoderType.Value
    private int decoderType;

    public int getDecoderType() {
        return decoderType;
    }

    @PlayerType.SeekType.Value
    private int seekType;

    public int getSeekType() {
        return seekType;
    }

    // 视频渲染类型
    @PlayerType.RenderType.Value
    private int renderType;

    public int getRenderType() {
        return renderType;
    }

    // 画面缩放类型
    @PlayerType.ScaleType.Value
    private int scaleType;

    public int getscaleType() {
        return scaleType;
    }

    // 视频解码类型
    @PlayerType.KernelType.Value
    private int kernelType;

    public int getKernelType() {
        return kernelType;
    }

    // 超时时间
    private int connectTimeoutMs;

    public int getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    // 日志
    private boolean log;

    public boolean isLog() {
        return log;
    }

    // 缓冲超时, 是否重新播放
    private boolean bufferingTimeoutRetry;

    public boolean isBufferingTimeoutRetry() {
        return bufferingTimeoutRetry;
    }

    // 开始播放前，是否销毁已存在的播放器相关实例
    private boolean initRelease;

    public boolean isInitRelease() {
        return initRelease;
    }

    // 视频url
    private UrlArgs urlArgs;

    public boolean containsMainUrl() {
        return null != urlArgs && urlArgs.containsMainUrl();
    }

    public boolean containsExtUrl() {
        return null != urlArgs && urlArgs.containsExtUrl();
    }

    public UrlArgs getUrlArgs() {
        return urlArgs;
    }

    public String getUrl() {
        if (null == urlArgs) {
            return null;
        } else {
            return urlArgs.getMainUrl();
        }
    }

    // 视频title
    private String title;

    public String getTitle() {
        return title;
    }


    // 试看时长
    private long trySeeDuration;

    public long getTrySeeDuration() {
        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "getTrySeeDuration -> trySeeDuration = " + trySeeDuration);
        }
        return trySeeDuration;
    }


    // 起播快进指定位置
    private long playWhenReadySeekToPosition;

    public long getPlayWhenReadySeekToPosition() {
        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "getPlayWhenReadySeekToPosition -> playWhenReadySeekToPosition = " + playWhenReadySeekToPosition);
        }
        return playWhenReadySeekToPosition;
    }

    // 是否直播源
    private boolean live;

    public boolean isLive() {
        return live;
    }

    // 循环播放
    private boolean looping;

    public boolean isLooping() {
        return looping;
    }

    // 静音
    private boolean mute;

    public boolean isMute() {
        return mute;
    }

    // 默认自动开播
    private boolean playWhenReady;

    public boolean isPlayWhenReady() {
        return playWhenReady;
    }

    // 延迟播放
    private long playWhenReadyDelayedTime;

    public long getPlayWhenReadyDelayedTime() {
        return playWhenReadyDelayedTime;
    }

    // 默认异步初始化
    private boolean prepareAsync;

    public boolean isPrepareAsync() {
        return prepareAsync;
    }

    // 旋转角度
    @PlayerType.RotationType.Value
    private int rotation;

    public int getRotation() {
        return rotation;
    }


    // 透传数据
    private JSONObject extraData;

    public JSONObject getExtraData() {
        return extraData;
    }

    // 显示网速
    private boolean showSpeed;

    public boolean isShowSpeed() {
        return showSpeed;
    }

    // 菜单数据
    private Menu menu;

    public Menu getMenu() {
        return menu;
    }

    // 禁止抓包
    private boolean noProxy;

    public boolean isNoProxy() {
        return noProxy;
    }

    // 轨道代理
    private Proxy proxy;

    public Proxy getProxy() {
        return proxy;
    }

    public ProxyUrl getProxyUrl() {
        if (null == proxy) {
            return null;
        } else {
            return proxy.getProxyUrl();
        }
    }

    // 缓冲区参数
    private BufferDurationsMs bufferDurationsMs;

    public int getBufferDurationsMsMinBufferMs() {
        return bufferDurationsMs.minBufferMs;
    }

    public int getBufferDurationsMsMaxBufferMs() {
        return bufferDurationsMs.maxBufferMs;
    }

    public int getBufferDurationsMsBufferForPlaybackMs() {
        return bufferDurationsMs.bufferForPlaybackMs;
    }

    public int getBufferDurationsMsBufferForPlaybackAfterRebufferMs() {
        return bufferDurationsMs.bufferForPlaybackAfterRebufferMs;
    }

    // 追播参数
    private LiveConfiguration liveConfiguration;

    public float getLiveConfigurationMaxPlaybackSpeed() {
        return liveConfiguration.maxPlaybackSpeed;
    }

    public float getLiveConfigurationMinPlaybackSpeed() {
        return liveConfiguration.minPlaybackSpeed;
    }

    public long getLiveConfigurationTargetOffsetMs() {
        return liveConfiguration.targetOffsetMs;
    }

    public long getLiveConfigurationMinOffsetMs() {
        return liveConfiguration.minOffsetMs;
    }

    public long getLiveConfigurationMaxOffsetMs() {
        return liveConfiguration.maxOffsetMs;
    }

    @Override
    public String toString() {
        return "StartArgs{" +
                "seekType=" + seekType +
                ", renderType=" + renderType +
                ", scaleType=" + scaleType +
                ", decoderType=" + decoderType +
                ", kernelType=" + kernelType +
                ", connectTimeoutMs=" + connectTimeoutMs +
                ", log=" + log +
                ", bufferingTimeoutRetry=" + bufferingTimeoutRetry +
                ", initRelease=" + initRelease +
                ", urlArgs='" + urlArgs + '\'' +
                ", title='" + title + '\'' +
                ", trySeeDuration=" + trySeeDuration +
                ", live=" + live +
                ", looping=" + looping +
                ", mute=" + mute +
                ", playWhenReady=" + playWhenReady +
                ", playWhenReadyDelayedTime=" + playWhenReadyDelayedTime +
                ", playWhenReadySeekToPosition=" + playWhenReadySeekToPosition +
                ", prepareAsync=" + prepareAsync +
                ", rotation=" + rotation +
                ", extraData=" + extraData +
                ", showSpeed=" + showSpeed +
                ", menu=" + menu +
                ", noProxy=" + noProxy +
                ", proxy=" + proxy +
                ", bufferDurationsMs=" + bufferDurationsMs +
                ", liveConfiguration=" + liveConfiguration +
                '}';
    }

    public StartArgs(Builder builder) {
        this.decoderType = builder.decoderType;
        this.seekType = builder.seekType;
        this.renderType = builder.renderType;
        this.scaleType = builder.scaleType;
        this.kernelType = builder.kernelType;
        this.connectTimeoutMs = builder.connectTimeoutMs;
        this.log = builder.log;
        this.bufferingTimeoutRetry = builder.bufferingTimeoutRetry;
        this.initRelease = builder.initRelease;
        this.urlArgs = builder.urlArgs;
        this.title = builder.title;
        this.trySeeDuration = builder.trySeeDuration;
        this.live = builder.live;
        this.looping = builder.looping;
        this.mute = builder.mute;
        this.playWhenReady = builder.playWhenReady;
        this.playWhenReadyDelayedTime = builder.playWhenReadyDelayedTime;
        this.playWhenReadySeekToPosition = builder.playWhenReadySeekToPosition;
        this.prepareAsync = builder.prepareAsync;
        this.rotation = builder.rotation;
        this.extraData = builder.extraData;
        this.showSpeed = builder.showSpeed;
        this.menu = builder.menu;
        this.noProxy = builder.noProxy;
        this.proxy = builder.proxy;
        this.bufferDurationsMs = builder.bufferDurationsMs;
        this.liveConfiguration = builder.liveConfiguration;
    }

    public Builder newBuilder() {
        Builder builder = new Builder();
        builder.seekType = seekType;
        builder.decoderType = decoderType;
        builder.renderType = renderType;
        builder.scaleType = scaleType;
        builder.kernelType = kernelType;
        builder.connectTimeoutMs = connectTimeoutMs;
        builder.log = log;
        builder.bufferingTimeoutRetry = bufferingTimeoutRetry;
        builder.initRelease = initRelease;
        builder.urlArgs = urlArgs;
        builder.title = title;
        builder.trySeeDuration = trySeeDuration;
        builder.live = live;
        builder.looping = looping;
        builder.mute = mute;
        builder.playWhenReady = playWhenReady;
        builder.playWhenReadyDelayedTime = playWhenReadyDelayedTime;
        builder.playWhenReadySeekToPosition = playWhenReadySeekToPosition;
        builder.prepareAsync = prepareAsync;
        builder.rotation = rotation;
        builder.extraData = extraData;
        builder.showSpeed = showSpeed;
        builder.menu = menu;
        builder.noProxy = noProxy;
        builder.proxy = proxy;
        builder.bufferDurationsMs = bufferDurationsMs;
        builder.liveConfiguration = liveConfiguration;
        return builder;
    }

    public static class Builder implements Serializable {

        private String TAG = "StartArgs22.Builder";

        private final PlayerArgs playerArgs = PlayerSDK.init().getPlayerBuilder();

        // 解码器类型
        private int decoderType = playerArgs.getDecoderType();
        // 播放器类型
        @PlayerType.KernelType.Value
        private int kernelType = playerArgs.getKernelType();
        // 画面缩放类型
        @PlayerType.ScaleType.Value
        private int scaleType = playerArgs.getScaleType();
        // 旋转角度
        @PlayerType.RotationType.Value
        private int rotation = playerArgs.getRotation();
        // 超时时间
        private int connectTimeoutMs = playerArgs.getConnectTimeoutMs();
        // 日志
        private boolean log = playerArgs.isLog();
        // 缓冲超时, 是否重新播放
        private boolean bufferingTimeoutRetry = playerArgs.getBufferingTimeoutRetry();
        // 开始播放前，是否销毁已存在的播放器相关实例
        private boolean initRelease = playerArgs.isInitRelease();

        @PlayerType.SeekType.Value
        private int seekType = playerArgs.getSeekType();

        @PlayerType.SeekType.Value
        public int getSeekType() {
            return seekType;
        }

        // 视频渲染类型
        @PlayerType.RenderType.Value
        private int renderType = playerArgs.getRenderType();

        public Builder setRenderType(@PlayerType.RenderType.Value int v) {
            this.renderType = v;
            return this;
        }

        // 视频url
        private UrlArgs urlArgs;

        public Builder setUrl(UrlArgs v) {
            this.urlArgs = v;
            return this;
        }

        public Builder setUrl(String v) {
            this.urlArgs = new UrlArgs.Builder().setUrl(v).build();
            return this;
        }

        // 视频title
        private String title;

        public Builder setTitle(String v) {
            this.title = v;
            return this;
        }

        // 字幕url
        private String subtitleUrl;

        public Builder setSubtitleUrl(String subtitleUrl) {
            this.subtitleUrl = subtitleUrl;
            return this;
        }

        // 试看时长
        private long trySeeDuration = 0L;

        public Builder setTrySeeDuration(long v) {
            this.trySeeDuration = v;
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "setTrySeeDuration -> trySeeDuration = " + trySeeDuration);
            }
            return this;
        }

        // 起播快进
        private long playWhenReadySeekToPosition = 0;

        public Builder setPlayWhenReadySeekToPosition(long v) {
            this.playWhenReadySeekToPosition = v;
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "setPlayWhenReadySeekToPosition -> playWhenReadySeekToPosition = " + playWhenReadySeekToPosition);
            }
            return this;
        }

        // 是否直播源
        private boolean live = false;

        public Builder setLive(boolean live) {
            this.live = live;
            return this;
        }

        // 循环播放
        private boolean looping = false;

        public Builder setLooping(boolean looping) {
            this.looping = looping;
            return this;
        }

        // 静音
        private boolean mute = false;

        public Builder setMute(boolean mute) {
            this.mute = mute;
            return this;
        }

        // 默认自动开播
        private boolean playWhenReady = true;

        public Builder setPlayWhenReady(boolean playWhenReady) {
            this.playWhenReady = playWhenReady;
            return this;
        }

        // 延迟播放
        private long playWhenReadyDelayedTime = 0L;

        public Builder setPlayWhenReadyDelayedTime(long v) {
            this.playWhenReadyDelayedTime = v;
            return this;
        }

        // 默认异步初始化
        private boolean prepareAsync = true;

        public Builder setPrepareAsync(boolean prepareAsync) {
            this.prepareAsync = prepareAsync;
            return this;
        }

        // 透传数据
        private JSONObject extraData;

        public Builder setExtraData(JSONObject v) {
            this.extraData = v;
            return this;
        }

        // 显示网速
        private boolean showSpeed;

        public Builder setShowSpeed(boolean v) {
            this.showSpeed = v;
            return this;
        }


        // 菜单数据
        private Menu menu;

        public Builder setMenu(Menu v) {
            this.menu = v;
            return this;
        }

        // 禁止抓包
        private boolean noProxy = playerArgs.isNoProxy();

        public Builder setNoProxy(Boolean v) {
            this.noProxy = v;
            return this;
        }

        // 轨道代理
        private Proxy proxy = null;

        public Builder setProxy(Proxy v) {
            this.proxy = v;
            return this;
        }

        // 缓冲区参数
        private BufferDurationsMs bufferDurationsMs = new BufferDurationsMs.Builder().build();

        public Builder setBufferDurationsMs(BufferDurationsMs v) {
            this.bufferDurationsMs = v;
            return this;
        }

        // 追播参数
        private LiveConfiguration liveConfiguration = new LiveConfiguration.Builder().build();

        public Builder setLiveConfiguration(LiveConfiguration v) {
            this.liveConfiguration = v;
            return this;
        }

        public Builder() {
        }

        public StartArgs build() {
            return new StartArgs(this);
        }
    }

    public static final class BufferDurationsMs implements Serializable {

        private int minBufferMs;
        private int maxBufferMs;
        private int bufferForPlaybackMs;
        private int bufferForPlaybackAfterRebufferMs;

        public BufferDurationsMs(BufferDurationsMs.Builder builder) {
            this.minBufferMs = builder.minBufferMs;
            this.maxBufferMs = builder.maxBufferMs;
            this.bufferForPlaybackMs = builder.bufferForPlaybackMs;
            this.bufferForPlaybackAfterRebufferMs = builder.bufferForPlaybackAfterRebufferMs;
        }

        public BufferDurationsMs.Builder newBuilder() {
            BufferDurationsMs.Builder builder = new BufferDurationsMs.Builder();
            builder.minBufferMs = minBufferMs;
            builder.maxBufferMs = maxBufferMs;
            builder.bufferForPlaybackMs = bufferForPlaybackMs;
            builder.bufferForPlaybackAfterRebufferMs = bufferForPlaybackAfterRebufferMs;
            return builder;
        }

        @Override
        public String toString() {
            return "BufferDurationsMs{" +
                    "minBufferMs=" + minBufferMs +
                    ", maxBufferMs=" + maxBufferMs +
                    ", bufferForPlaybackMs=" + bufferForPlaybackMs +
                    ", bufferForPlaybackAfterRebufferMs=" + bufferForPlaybackAfterRebufferMs +
                    '}';
        }

        public static class Builder implements Serializable {

            // 播放器至少要缓冲 50 秒的数据后，才会停止主动加载更多数据；如果缓冲低于这个值，会重新开始加载。
            private int minBufferMs = 50_000;
            // 播放器最多缓冲 50 秒的数据，达到这个值后会停止加载，避免占用过多内存。
            private int maxBufferMs = 50_000;
            // 启播, 播放器需要至少缓冲 1 秒的数据，才会开始播放（或从暂停恢复播放）。
            private int bufferForPlaybackMs = 1000;
            // 播放过程中缓冲，播放器在缓冲不足导致暂停后，需要重新缓冲 2 秒的数据，才会恢复播放。
            private int bufferForPlaybackAfterRebufferMs = 2000;

            public BufferDurationsMs.Builder setMinBufferMs(int v) {
                this.minBufferMs = v;
                return this;
            }

            public BufferDurationsMs.Builder setMaxBufferMs(int v) {
                this.maxBufferMs = v;
                return this;
            }

            public BufferDurationsMs.Builder setBufferForPlaybackMs(int v) {
                this.bufferForPlaybackMs = v;
                return this;
            }

            public BufferDurationsMs.Builder setBufferForPlaybackAfterRebufferMs(int v) {
                this.bufferForPlaybackAfterRebufferMs = v;
                return this;
            }

            public Builder() {
            }

            public BufferDurationsMs build() {
                return new BufferDurationsMs(this);
            }
        }
    }

    public static final class LiveConfiguration implements Serializable {

        private long targetOffsetMs;
        private long minOffsetMs;
        private long maxOffsetMs;
        private float minPlaybackSpeed;
        private float maxPlaybackSpeed;

        public LiveConfiguration(LiveConfiguration.Builder builder) {
            this.targetOffsetMs = builder.targetOffsetMs;
            this.minOffsetMs = builder.minOffsetMs;
            this.maxOffsetMs = builder.maxOffsetMs;
            this.minPlaybackSpeed = builder.minPlaybackSpeed;
            this.maxPlaybackSpeed = builder.maxPlaybackSpeed;
        }

        public LiveConfiguration.Builder newBuilder() {
            LiveConfiguration.Builder builder = new LiveConfiguration.Builder();
            builder.targetOffsetMs = targetOffsetMs;
            builder.minOffsetMs = minOffsetMs;
            builder.maxOffsetMs = maxOffsetMs;
            builder.minPlaybackSpeed = minPlaybackSpeed;
            builder.maxPlaybackSpeed = maxPlaybackSpeed;
            return builder;
        }

        @Override
        public String toString() {
            return "LiveConfiguration{" +
                    "targetOffsetMs=" + targetOffsetMs +
                    ", minOffsetMs=" + minOffsetMs +
                    ", maxOffsetMs=" + maxOffsetMs +
                    ", minPlaybackSpeed=" + minPlaybackSpeed +
                    ", maxPlaybackSpeed=" + maxPlaybackSpeed +
                    '}';
        }

        public static class Builder implements Serializable {

            // 目标直播延迟（离直播边缘的距离）	3000 - 5000ms	值越大越稳定（不易触发 BehindLiveWindow），值越小越实时
            private long targetOffsetMs = 5000;
            // 最小允许的直播延迟	2000ms	防止播放器离直播边缘太近导致频繁卡顿
            private long minOffsetMs = 2000;
            // 最大允许的直播延迟	10000ms	超过这个值就会自动加速追赶
            private long maxOffsetMs = 10_000;
            // 播放器为了等待缓冲的最小倍速	0.8f - 1.0f	网络差时，降速播放避免卡顿
            private float minPlaybackSpeed = 0.8f;
            // 播放器追赶直播时允许的最大倍速	1.2f - 1.5f	当播放器落后于直播点时，自动加速播放追赶
            private float maxPlaybackSpeed = 1.2f;

            public LiveConfiguration.Builder setTargetOffsetMs(long v) {
                this.targetOffsetMs = v;
                return this;
            }

            public LiveConfiguration.Builder setMinOffsetMs(long v) {
                this.minOffsetMs = v;
                return this;
            }

            public LiveConfiguration.Builder setMaxOffsetMs(long v) {
                this.maxOffsetMs = v;
                return this;
            }

            public LiveConfiguration.Builder setMinPlaybackSpeed(float v) {
                this.minPlaybackSpeed = v;
                return this;
            }

            public LiveConfiguration.Builder setMaxPlaybackSpeed(long v) {
                this.maxPlaybackSpeed = v;
                return this;
            }

            public Builder() {
            }

            public LiveConfiguration build() {
                return new LiveConfiguration(this);
            }
        }
    }

    public static final class AdaptiveTrackSelection implements Serializable {

        private final int minDurationForQualityIncreaseMs;
        private final int maxDurationForQualityDecreaseMs;
        private final int minDurationToRetainAfterDiscardMs;
        private final int maxWidthToDiscard;
        private final int maxHeightToDiscard;
        private final float bandwidthFraction;
        private final float bufferedFractionToLiveEdgeForQualityIncrease;

        public AdaptiveTrackSelection(AdaptiveTrackSelection.Builder builder) {
            this.minDurationForQualityIncreaseMs = builder.minDurationForQualityIncreaseMs;
            this.maxDurationForQualityDecreaseMs = builder.maxDurationForQualityDecreaseMs;
            this.minDurationToRetainAfterDiscardMs = builder.minDurationToRetainAfterDiscardMs;
            this.maxWidthToDiscard = builder.maxWidthToDiscard;
            this.maxHeightToDiscard = builder.maxHeightToDiscard;
            this.bandwidthFraction = builder.bandwidthFraction;
            this.bufferedFractionToLiveEdgeForQualityIncrease = builder.bufferedFractionToLiveEdgeForQualityIncrease;
        }

        public AdaptiveTrackSelection.Builder newBuilder() {
            AdaptiveTrackSelection.Builder builder = new AdaptiveTrackSelection.Builder();
            builder.minDurationForQualityIncreaseMs = minDurationForQualityIncreaseMs;
            builder.maxDurationForQualityDecreaseMs = maxDurationForQualityDecreaseMs;
            builder.minDurationToRetainAfterDiscardMs = minDurationToRetainAfterDiscardMs;
            builder.maxWidthToDiscard = maxWidthToDiscard;
            builder.maxHeightToDiscard = maxHeightToDiscard;
            builder.bandwidthFraction = bandwidthFraction;
            builder.bufferedFractionToLiveEdgeForQualityIncrease = bufferedFractionToLiveEdgeForQualityIncrease;
            return builder;
        }


        public static class Builder implements Serializable {

            private int minDurationForQualityIncreaseMs;
            private int maxDurationForQualityDecreaseMs;
            private int minDurationToRetainAfterDiscardMs;
            private int maxWidthToDiscard;
            private int maxHeightToDiscard;
            private float bandwidthFraction;
            private float bufferedFractionToLiveEdgeForQualityIncrease;

            public Builder() {
            }

            public AdaptiveTrackSelection build() {
                return new AdaptiveTrackSelection(this);
            }
        }
    }
}
