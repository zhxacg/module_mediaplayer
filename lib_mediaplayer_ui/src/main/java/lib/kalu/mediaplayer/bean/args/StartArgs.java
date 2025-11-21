package lib.kalu.mediaplayer.bean.args;


import org.json.JSONObject;

import java.io.Serializable;

import lib.kalu.mediaplayer.PlayerSDK;
import lib.kalu.mediaplayer.bean.menu.Menu;
import lib.kalu.mediaplayer.bean.type.PlayerType;

public class StartArgs implements Serializable {

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
    private long connectTimout;

    public long getConnectTimout() {
        return connectTimout;
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

    // View生命周期, 自动暂停&续播&销毁...
    private boolean supportAutoRelease;

    public boolean isSupportAutoRelease() {
        return supportAutoRelease;
    }

    // 视频url
    private UrlArgs url;

    public boolean containsUrl() {
        if (null == url) {
            return false;
        } else {
            String mainUrl = url.getMainUrl();
            if (null == mainUrl) {
                return false;
            } else if (mainUrl.isEmpty()) {
                return false;
            } else {
                return true;
            }
        }
    }

    public UrlArgs getUrl() {
        return url;
    }

    // 视频title
    private String title;

    public String getTitle() {
        return title;
    }


    // 试看时长
    private long trySeeDuration = 0L;

    public long getTrySeeDuration() {
        return trySeeDuration;
    }


    // 起播快进指定位置
    private long playWhenReadySeekToPosition;

    public long getPlayWhenReadySeekToPosition() {
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

    @Override
    public String toString() {
        return "StartArgs{" +
                ", seekType=" + seekType +
                ", renderType=" + renderType +
                ", scaleType=" + scaleType +
                ", decoderType=" + decoderType +
                ", kernelType=" + kernelType +
                ", connectTimout=" + connectTimout +
                ", log=" + log +
                ", bufferingTimeoutRetry=" + bufferingTimeoutRetry +
                ", initRelease=" + initRelease +
                ", supportAutoRelease=" + supportAutoRelease +
                ", url='" + url + '\'' +
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
                '}';
    }

    public StartArgs(Builder builder) {
        this.decoderType = builder.decoderType;
        this.seekType = builder.seekType;
        this.renderType = builder.renderType;
        this.scaleType = builder.scaleType;
        this.kernelType = builder.kernelType;
        this.connectTimout = builder.connectTimout;
        this.log = builder.log;
        this.bufferingTimeoutRetry = builder.bufferingTimeoutRetry;
        this.initRelease = builder.initRelease;
        this.supportAutoRelease = builder.supportAutoRelease;
        this.url = builder.url;
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
    }

    public Builder newBuilder() {
        Builder builder = new Builder();
        builder.seekType = seekType;
        builder.decoderType = decoderType;
        builder.renderType = renderType;
        builder.scaleType = scaleType;
        builder.kernelType = kernelType;
        builder.connectTimout = connectTimout;
        builder.log = log;
        builder.bufferingTimeoutRetry = bufferingTimeoutRetry;
        builder.initRelease = initRelease;
        builder.supportAutoRelease = supportAutoRelease;
        builder.url = url;
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
        return builder;
    }

    public static class Builder implements Serializable {

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
        private long connectTimout = playerArgs.getConnectTimeout();
        // 日志
        private boolean log = playerArgs.isLog();
        // 缓冲超时, 是否重新播放
        private boolean bufferingTimeoutRetry = playerArgs.getBufferingTimeoutRetry();
        // 开始播放前，是否销毁已存在的播放器相关实例
        private boolean initRelease = playerArgs.isInitRelease();
        // 自动暂停&续播&销毁...
        private boolean supportAutoRelease = playerArgs.isSupportAutoRelease();

        public Builder setSupportAutoRelease(boolean v) {
            this.supportAutoRelease = v;
            return this;
        }

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
        private UrlArgs url;

        public Builder setUrl(UrlArgs v) {
            this.url = v;
            return this;
        }

        public Builder setUrl(String v) {
            this.url = new UrlArgs.Builder().setMainUrl(v).build();
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

        public Builder() {
        }

        public StartArgs build() {
            return new StartArgs(this);
        }
    }
}
