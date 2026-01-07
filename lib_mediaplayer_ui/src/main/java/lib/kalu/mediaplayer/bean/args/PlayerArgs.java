package lib.kalu.mediaplayer.bean.args;


import lib.kalu.mediaplayer.bean.cache.Cache;
import lib.kalu.mediaplayer.bean.type.PlayerType;

/**
 * @description: 播放器全局配置
 * @date: 2021-05-12 14:43
 */

public final class PlayerArgs {

    private boolean log;// 日志log
    private boolean initRelease;
    private boolean supportAutoRelease; // 自动销毁
    private int connectTimeoutMs;
    private boolean bufferingTimeoutRetry;
    @PlayerType.KernelType.Value
    private int externalAudioKernel; // 音频播放器内核
    @PlayerType.KernelType.Value
    private int kernelType; // 视频播放器内核
    @PlayerType.RenderType.Value
    private int renderType; // 视频渲染类型
    @PlayerType.DecoderType.Value
    private int decoderType; // 解码器类型
    @PlayerType.ScaleType
    private int scaleType; // 视频缩放比例

    // 旋转角度
    @PlayerType.RotationType.Value
    private int rotation;

    public int getRotation() {
        return rotation;
    }

    private Cache cache;

    public Cache getCache() {
        return cache;
    }

    public int getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    public boolean getBufferingTimeoutRetry() {
        return bufferingTimeoutRetry;
    }

    public boolean isLog() {
        return log;
    }

    public boolean isInitRelease() {
        return initRelease;
    }

    public boolean isSupportAutoRelease() {
        return supportAutoRelease;
    }

    public int getExtrAudioKernel() {
        return externalAudioKernel;
    }

    @PlayerType.KernelType.Value
    public int getKernelType() {
        return kernelType;
    }

    @PlayerType.RenderType.Value
    public int getRenderType() {
        return renderType;
    }

    @PlayerType.DecoderType.Value
    public int getDecoderType() {
        return decoderType;
    }

    @PlayerType.ScaleType.Value
    public int getScaleType() {
        return scaleType;
    }

    @PlayerType.SeekType.Value
    private int seekType;

    public int getSeekType() {
        return seekType;
    }

    /****************/

    private PlayerArgs(Builder builder) {
        log = builder.log;
        initRelease = builder.initRelease;
        supportAutoRelease = builder.supportAutoRelease;
        connectTimeoutMs = builder.connectTimeoutMs;
        bufferingTimeoutRetry = builder.bufferingTimeoutRetry;
        externalAudioKernel = builder.externalAudioKernel;
        kernelType = builder.kernelType;
        renderType = builder.renderType;
        decoderType = builder.decoderType;
        scaleType = builder.scaleType;
        seekType = builder.seekType;
        rotation = builder.rotation;
        cache = builder.cache;
    }

    public Builder newBuilder() {
        Builder builder = new Builder();
        builder.setLog(this.log);
        builder.setInitRelease(this.initRelease);
        builder.setSupportAutoRelease(this.supportAutoRelease);
        builder.setConnectTimeoutMs(this.connectTimeoutMs);
        builder.setBufferingTimeoutRetry(this.bufferingTimeoutRetry);
        builder.setExternalAudioKernel(this.externalAudioKernel);
        builder.setKernelType(this.kernelType);
        builder.setRenderType(this.renderType);
        builder.setDecoderType(this.decoderType);
        builder.setScaleType(this.scaleType);
        builder.setSeekType(this.seekType);
        builder.setRotation(this.rotation);
        builder.setCache(this.cache);
        return builder;
    }


    public final static class Builder {

        private boolean log = false;
        private boolean initRelease = true;
        private boolean supportAutoRelease = true;
        private int connectTimeoutMs = 10_000;  // 连接超时 10s
        private boolean bufferingTimeoutRetry = false; // 缓冲失败重试
        @PlayerType.KernelType.Value
        private int externalAudioKernel = PlayerType.KernelType.DEFAULT; // 音频播放器内核
        @PlayerType.KernelType.Value
        private int kernelType = PlayerType.KernelType.DEFAULT; // 视频播放器内核
        @PlayerType.RenderType.Value
        private int renderType = PlayerType.RenderType.DEFAULT; // 视频渲染类型
        @PlayerType.DecoderType.Value
        private int decoderType = PlayerType.DecoderType.DEFAULT; // 解码器类型
        @PlayerType.ScaleType
        private int scaleType = PlayerType.ScaleType.DEFAULT; // 视频缩放比例

        // 旋转角度
        @PlayerType.RotationType.Value
        private int rotation = PlayerType.RotationType.DEFAULT;

        public Builder setRotation(int v) {
            this.rotation = v;
            return this;
        }


        // 快进参数
        @PlayerType.SeekType.Value
        private int seekType = PlayerType.SeekType.DEFAULT;

        public Builder setSeekType(@PlayerType.SeekType.Value int v) {
            seekType = v;
            return this;
        }

        public Builder setConnectTimeoutMs(int v) {
            connectTimeoutMs = v;
            return this;
        }


        public Builder setBufferingTimeoutRetry(boolean v) {
            bufferingTimeoutRetry = v;
            return this;
        }

        public Builder setDecoderType(@PlayerType.DecoderType int v) {
            decoderType = v;
            return this;
        }

        public Builder setLog(boolean v) {
            log = v;
            return this;
        }

        public Builder setInitRelease(boolean v) {
            initRelease = v;
            return this;
        }

        public Builder setSupportAutoRelease(boolean v) {
            supportAutoRelease = v;
            return this;
        }

        public Builder setExternalAudioKernel(@PlayerType.KernelType.Value int v) {
            externalAudioKernel = v;
            return this;
        }

        public Builder setKernelType(@PlayerType.KernelType.Value int v) {
            kernelType = v;
            return this;
        }

        public Builder setRenderType(@PlayerType.RenderType.Value int v) {
            renderType = v;
            return this;
        }

        public Builder setScaleType(@PlayerType.ScaleType.Value int v) {
            scaleType = v;
            return this;
        }

        // 缓存
        private Cache cache = new Cache.Builder()
                .build();

        public Builder setCache(Cache v) {
            this.cache = v;
            return this;
        }

        public PlayerArgs build() {
            return new PlayerArgs(this);
        }
    }
}
