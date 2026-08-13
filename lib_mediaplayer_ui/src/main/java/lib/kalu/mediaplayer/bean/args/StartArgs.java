package lib.kalu.mediaplayer.bean.args;


import org.json.JSONObject;

import java.io.Serializable;

import lib.kalu.mediaplayer.PlayerSDK;
import lib.kalu.mediaplayer.bean.configuration.AdaptiveConfiguration;
import lib.kalu.mediaplayer.bean.configuration.BufferConfiguration;
import lib.kalu.mediaplayer.bean.configuration.LiveConfiguration;
import lib.kalu.mediaplayer.bean.configuration.RetryConfiguration;
import lib.kalu.mediaplayer.bean.configuration.StuckConfiguration;
import lib.kalu.mediaplayer.bean.configuration.TimeoutConfiguration;
import lib.kalu.mediaplayer.bean.menu.Menu;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.proxy.Proxy;
import lib.kalu.mediaplayer.proxy.ProxyUrl;

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


    // 视频url
    private UrlArgs urlArgs;


    public boolean containsMainUrl() {
        return null != urlArgs && urlArgs.containsUrl();
    }

    public UrlArgs getUrlArgs() {
        return urlArgs;
    }

    public String getUrl() {
        if (null == urlArgs) {
            return null;
        } else {
            return urlArgs.getDefaultUrl();
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
//        if (LogUtil.DEBUG) {
//            LogUtil.log(TAG, "getTrySeeDuration -> trySeeDuration = " + trySeeDuration);
//        }
        return trySeeDuration;
    }


    // 起播快进指定位置
    private long playWhenReadySeekToPosition;

    public long getPlayWhenReadySeekToPosition() {
//        if (LogUtil.DEBUG) {
//            LogUtil.log(TAG, "getPlayWhenReadySeekToPosition -> playWhenReadySeekToPosition = " + playWhenReadySeekToPosition);
//        }
        return playWhenReadySeekToPosition;
    }

    // 是否直播源
    private boolean liveStream;

    public boolean isLiveStream() {
//        if (LogUtil.DEBUG) {
//            LogUtil.log(TAG, "isLiveStream -> liveStream = " + liveStream + ", this = " + this);
//        }
        return liveStream;
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

    // 追播参数1
    private LiveConfiguration liveConfiguration;

    public LiveConfiguration getLiveConfiguration() {
        return liveConfiguration;
    }

    // 多路流
    private AdaptiveConfiguration adaptiveConfiguration;

    public AdaptiveConfiguration getAdaptiveConfiguration() {
        return adaptiveConfiguration;
    }

    // 卡顿检测
    private StuckConfiguration stuckConfiguration;

    public StuckConfiguration getStuckConfiguration() {
        return stuckConfiguration;
    }

    // 缓冲超时 默认不检测
    private BufferConfiguration bufferConfiguration;

    public BufferConfiguration getBufferConfiguration() {
        return bufferConfiguration;
    }

    // 超时
    private TimeoutConfiguration timeoutConfiguration;

    public TimeoutConfiguration getTimeoutConfiguration() {
        return timeoutConfiguration;
    }


    // 重试类型
    private int retryType;

    public int getRetryType() {
        return retryType;
    }

    // 重试策略
    private RetryConfiguration retryConfiguration;

    public RetryConfiguration getRetryConfiguration() {
        return retryConfiguration;
    }

    @Override
    public String toString() {
        return "StartArgs{" +
                "TAG='" + TAG + '\'' +
                ", decoderType=" + decoderType +
                ", seekType=" + seekType +
                ", renderType=" + renderType +
                ", scaleType=" + scaleType +
                ", kernelType=" + kernelType +
                ", urlArgs=" + urlArgs +
                ", title='" + title + '\'' +
                ", trySeeDuration=" + trySeeDuration +
                ", playWhenReadySeekToPosition=" + playWhenReadySeekToPosition +
                ", liveStream=" + liveStream +
                ", looping=" + looping +
                ", mute=" + mute +
                ", playWhenReady=" + playWhenReady +
                ", playWhenReadyDelayedTime=" + playWhenReadyDelayedTime +
                ", prepareAsync=" + prepareAsync +
                ", rotation=" + rotation +
                ", extraData=" + extraData +
                ", showSpeed=" + showSpeed +
                ", menu=" + menu +
                ", noProxy=" + noProxy +
                ", proxy=" + proxy +
                ", liveConfiguration=" + liveConfiguration +
                ", adaptiveConfiguration=" + adaptiveConfiguration +
                ", stuckConfiguration=" + stuckConfiguration +
                ", bufferConfiguration=" + bufferConfiguration +
                ", timeoutConfiguration=" + timeoutConfiguration +
                ", retryType=" + retryType +
                ", retryConfiguration=" + retryConfiguration +
                '}';
    }

    public StartArgs(Builder builder) {
        this.decoderType = builder.decoderType;
        this.seekType = builder.seekType;
        this.renderType = builder.renderType;
        this.scaleType = builder.scaleType;
        this.kernelType = builder.kernelType;
        this.urlArgs = builder.urlArgs;
        this.title = builder.title;
        this.trySeeDuration = builder.trySeeDuration;
        this.liveStream = builder.liveStream;
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
        this.stuckConfiguration = builder.stuckConfiguration;
        this.liveConfiguration = builder.liveConfiguration;
        this.adaptiveConfiguration = builder.adaptiveConfiguration;
        this.bufferConfiguration = builder.bufferConfiguration;
        this.timeoutConfiguration = builder.timeoutConfiguration;
        this.retryType = builder.retryType;
        this.retryConfiguration = builder.retryConfiguration;
    }

    public Builder newBuilder() {
        Builder builder = new Builder();
        builder.seekType = seekType;
        builder.decoderType = decoderType;
        builder.renderType = renderType;
        builder.scaleType = scaleType;
        builder.kernelType = kernelType;
        builder.urlArgs = urlArgs;
        builder.title = title;
        builder.trySeeDuration = trySeeDuration;
        builder.liveStream = liveStream;
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
        builder.liveConfiguration = liveConfiguration;
        builder.adaptiveConfiguration = adaptiveConfiguration;
        builder.stuckConfiguration = stuckConfiguration;
        builder.bufferConfiguration = bufferConfiguration;
        builder.timeoutConfiguration = timeoutConfiguration;
        builder.retryType = 0;
        builder.retryConfiguration = retryConfiguration;
        return builder;
    }

    public static class Builder implements Serializable {

        // 解码器类型
        private int decoderType = PlayerSDK.decoderType;
        // 播放器类型
        @PlayerType.KernelType.Value
        private int kernelType = PlayerSDK.kernelType;
        // 画面缩放类型
        @PlayerType.ScaleType.Value
        private int scaleType = PlayerSDK.scaleType;
        // 旋转角度
        @PlayerType.RotationType.Value
        private int rotation = PlayerSDK.rotation;

        @PlayerType.SeekType.Value
        private int seekType = PlayerSDK.seekType;

        @PlayerType.SeekType.Value
        public int getSeekType() {
            return seekType;
        }

        // 视频渲染类型
        @PlayerType.RenderType.Value
        private int renderType = PlayerSDK.renderType;

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
            return this;
        }

        // 起播快进
        private long playWhenReadySeekToPosition = 0;

        public Builder setPlayWhenReadySeekToPosition(long v) {
            this.playWhenReadySeekToPosition = v;
            return this;
        }

        // 是否直播源
        private boolean liveStream = false;

        public Builder setLiveStream(boolean liveStream) {
            this.liveStream = liveStream;
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
        private boolean noProxy = PlayerSDK.noProxy;

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

        // 直播配置
        private LiveConfiguration liveConfiguration = LiveConfiguration.newBuilder().applyDefaults().build();

        public Builder setLiveConfiguration(LiveConfiguration v) {
            this.liveConfiguration = v;
            return this;
        }

        // 多路流
        private AdaptiveConfiguration adaptiveConfiguration = AdaptiveConfiguration.newBuilder().applyDefaults().build();

        public Builder setAdaptiveConfiguration(AdaptiveConfiguration v) {
            this.adaptiveConfiguration = v;
            return this;
        }

        // 卡顿检测
        private StuckConfiguration stuckConfiguration = StuckConfiguration.newBuilder().applyDefaults().build();

        public Builder setStuckConfiguration(StuckConfiguration v) {
            this.stuckConfiguration = v;
            return this;
        }

        // 缓冲超时
        private BufferConfiguration bufferConfiguration = new BufferConfiguration.Builder().applyDefaults().build();

        public Builder setBufferingConfiguration(BufferConfiguration v) {
            this.bufferConfiguration = v;
            return this;
        }

        // 超时时间
        private TimeoutConfiguration timeoutConfiguration = TimeoutConfiguration.newBuilder().build();

        public Builder setTimeoutConfiguration(TimeoutConfiguration v) {
            this.timeoutConfiguration = v;
            return this;
        }

        private int retryType;

        public Builder setRetryType(int v) {
            this.retryType = v;
            return this;
        }

        // 重试策略
        private RetryConfiguration retryConfiguration = RetryConfiguration.newBuilder().build();

        public Builder setRetryConfiguration(RetryConfiguration v) {
            this.retryConfiguration = v;
            return this;
        }

        public Builder() {
        }

        public StartArgs build() {
            return new StartArgs(this);
        }
    }
}
