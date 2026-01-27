package lib.kalu.mediaplayer.bean.args;


import androidx.media3.common.util.Util;

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

    public BufferDurationsMs getBufferDurationsMs() {
        return bufferDurationsMs;
    }

    // 追播参数1
    private LiveConfiguration liveConfiguration;

    public LiveConfiguration getLiveConfiguration() {
        return liveConfiguration;
    }

    // 追播参数2
    private LivePlaybackSpeedControl livePlaybackSpeedControl;

    public LivePlaybackSpeedControl getLivePlaybackSpeedControl() {
        return livePlaybackSpeedControl;
    }

    // 多路流
    private AdaptiveTrackSelection adaptiveTrackSelection;

    public AdaptiveTrackSelection getAdaptiveTrackSelection() {
        return adaptiveTrackSelection;
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
                ", livePlaybackSpeedControl=" + livePlaybackSpeedControl +
                ", adaptiveTrackSelection=" + adaptiveTrackSelection +
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
        this.livePlaybackSpeedControl = builder.livePlaybackSpeedControl;
        this.adaptiveTrackSelection = builder.adaptiveTrackSelection;
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
        builder.livePlaybackSpeedControl = livePlaybackSpeedControl;
        builder.adaptiveTrackSelection = adaptiveTrackSelection;
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

        // 追播参数2
        private LivePlaybackSpeedControl livePlaybackSpeedControl = new LivePlaybackSpeedControl.Builder().build();

        public Builder setLivePlaybackSpeedControl(LivePlaybackSpeedControl v) {
            this.livePlaybackSpeedControl = v;
            return this;
        }

        // 多路流
        private AdaptiveTrackSelection adaptiveTrackSelection = new AdaptiveTrackSelection.Builder().build();

        public Builder setAdaptiveTrackSelection(AdaptiveTrackSelection v) {
            this.adaptiveTrackSelection = v;
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

        public int getMinBufferMs() {
            return minBufferMs;
        }

        public int getMaxBufferMs() {
            return maxBufferMs;
        }

        public int getBufferForPlaybackMs() {
            return bufferForPlaybackMs;
        }

        public int getBufferForPlaybackAfterRebufferMs() {
            return bufferForPlaybackAfterRebufferMs;
        }

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

        public long getTargetOffsetMs() {
            return targetOffsetMs;
        }

        public long getMinOffsetMs() {
            return minOffsetMs;
        }

        public long getMaxOffsetMs() {
            return maxOffsetMs;
        }

        public float getMinPlaybackSpeed() {
            return minPlaybackSpeed;
        }

        public float getMaxPlaybackSpeed() {
            return maxPlaybackSpeed;
        }

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


    public static final class LivePlaybackSpeedControl implements Serializable {

        private float fallbackMinPlaybackSpeed;
        private float fallbackMaxPlaybackSpeed;
        private long minUpdateIntervalMs;
        private float proportionalControlFactorUs;
        private long maxLiveOffsetErrorUsForUnitSpeed;
        private long targetLiveOffsetIncrementOnRebufferUs;
        private float minPossibleLiveOffsetSmoothingFactor;

        public float getFallbackMinPlaybackSpeed() {
            return fallbackMinPlaybackSpeed;
        }

        public float getFallbackMaxPlaybackSpeed() {
            return fallbackMaxPlaybackSpeed;
        }

        public long getMinUpdateIntervalMs() {
            return minUpdateIntervalMs;
        }

        public float getProportionalControlFactorUs() {
            return proportionalControlFactorUs;
        }

        public long getMaxLiveOffsetErrorUsForUnitSpeed() {
            return maxLiveOffsetErrorUsForUnitSpeed;
        }

        public long getTargetLiveOffsetIncrementOnRebufferUs() {
            return targetLiveOffsetIncrementOnRebufferUs;
        }

        public float getMinPossibleLiveOffsetSmoothingFactor() {
            return minPossibleLiveOffsetSmoothingFactor;
        }

        public LivePlaybackSpeedControl(LivePlaybackSpeedControl.Builder builder) {
            this.fallbackMinPlaybackSpeed = builder.fallbackMinPlaybackSpeed;
            this.fallbackMaxPlaybackSpeed = builder.fallbackMaxPlaybackSpeed;
            this.minUpdateIntervalMs = builder.minUpdateIntervalMs;
            this.proportionalControlFactorUs = builder.proportionalControlFactorUs;
            this.maxLiveOffsetErrorUsForUnitSpeed = builder.maxLiveOffsetErrorUsForUnitSpeed;
            this.targetLiveOffsetIncrementOnRebufferUs = builder.targetLiveOffsetIncrementOnRebufferUs;
            this.minPossibleLiveOffsetSmoothingFactor = builder.minPossibleLiveOffsetSmoothingFactor;
        }

        public static class Builder implements Serializable {


            // 极端场景下的最小速度（如缓存彻底耗尽时的保底速度）,建议 ≥0.8f（过低会导致播放卡顿感明显）
            private float fallbackMinPlaybackSpeed = 0.97F;
            // 极端场景下的最大速度（如缓存严重过剩时的保底速度）,建议 ≤1.2f（过高会让用户感知到快放）
            private float fallbackMaxPlaybackSpeed = 1.03F;
            // 速度调整的最小间隔（多久能调整一次速度）,弱网 / 低延迟场景可缩短至 500ms（调整更频繁）；追求性能可延长至 2000ms
            private long minUpdateIntervalMs = 1000L;
            // 速度调整的「比例控制因子」（偏移越大，速度调整幅度越大）,弱网可调大至 0.005f（更快调整速度）；低延迟可调小至 0.001f（调整更平缓）
            private float proportionalControlFactorUs = 1.0E-7F;
            //「保持 1 倍速」的最大偏移误差（超出这个范围才调整速度）,无需修改（默认值已足够平滑，改大易导致偏移计算波动）
            private long maxLiveOffsetErrorUsForUnitSpeed = Util.msToUs(20L);
            // 发生缓冲时，目标直播偏移的增量（缓冲后临时增大目标偏移，避免再次缓冲）,弱网可增大至 2000ms（缓冲后更保守）；低延迟可减小至 500ms（不牺牲太多实时性）
            private long targetLiveOffsetIncrementOnRebufferUs = Util.msToUs(500L);
            // 最小直播偏移的平滑因子（用于稳定计算「实时直播位置」）
            private float minPossibleLiveOffsetSmoothingFactor = 0.999F;

            public Builder setFallbackMinPlaybackSpeed(float v) {
                this.fallbackMinPlaybackSpeed = v;
                return this;
            }

            public Builder setFallbackMaxPlaybackSpeed(float v) {
                this.fallbackMaxPlaybackSpeed = v;
                return this;
            }

            public Builder setMinUpdateIntervalMs(long v) {
                this.minUpdateIntervalMs = v;
                return this;
            }

            public Builder setProportionalControlFactorUs(float v) {
                this.proportionalControlFactorUs = v;
                return this;
            }

            public Builder setMaxLiveOffsetErrorUsForUnitSpeed(long v) {
                this.maxLiveOffsetErrorUsForUnitSpeed = v;
                return this;
            }

            public Builder setTargetLiveOffsetIncrementOnRebufferUs(long v) {
                this.targetLiveOffsetIncrementOnRebufferUs = v;
                return this;
            }

            public Builder setMinPossibleLiveOffsetSmoothingFactor(float v) {
                this.minPossibleLiveOffsetSmoothingFactor = v;
                return this;
            }

            public Builder() {
            }

            public LivePlaybackSpeedControl build() {
                return new LivePlaybackSpeedControl(this);
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

        public static class Builder implements Serializable {

            private int minDurationForQualityIncreaseMs;
            private int maxDurationForQualityDecreaseMs;
            private int minDurationToRetainAfterDiscardMs;
            private int maxWidthToDiscard;
            private int maxHeightToDiscard;
            private float bandwidthFraction;
            private float bufferedFractionToLiveEdgeForQualityIncrease;

            public Builder setMinDurationForQualityIncreaseMs(int v) {
                this.minDurationForQualityIncreaseMs = v;
                return this;
            }

            public Builder setMaxDurationForQualityDecreaseMs(int v) {
                this.maxDurationForQualityDecreaseMs = v;
                return this;
            }

            public Builder setMinDurationToRetainAfterDiscardMs(int v) {
                this.minDurationToRetainAfterDiscardMs = v;
                return this;
            }

            public Builder setMaxWidthToDiscard(int v) {
                this.maxWidthToDiscard = v;
                return this;
            }

            public Builder setMaxHeightToDiscard(int v) {
                this.maxHeightToDiscard = v;
                return this;
            }

            public Builder setBandwidthFraction(float v) {
                this.bandwidthFraction = v;
                return this;
            }

            public Builder setBufferedFractionToLiveEdgeForQualityIncrease(float v) {
                this.bufferedFractionToLiveEdgeForQualityIncrease = v;
                return this;
            }

            public Builder() {
            }

            public AdaptiveTrackSelection build() {
                return new AdaptiveTrackSelection(this);
            }
        }
    }
}
