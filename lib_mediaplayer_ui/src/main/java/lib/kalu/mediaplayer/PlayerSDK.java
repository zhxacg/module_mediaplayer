package lib.kalu.mediaplayer;


import lib.kalu.mediaplayer.bean.cache.Cache;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.buried.PlayBuried;


public final class PlayerSDK {

    public static boolean log = false;
    public static boolean initRelease = true;
    public static boolean supportAutoRelease = true;
    public static int connectTimeoutMs = 10_000;  // 连接超时 10s
    public static boolean bufferingTimeoutRetry = false; // 缓冲失败重试
    @PlayerType.KernelType.Value
    public static int externalAudioKernel = PlayerType.KernelType.DEFAULT; // 音频播放器内核
    @PlayerType.KernelType.Value
    public static int kernelType = PlayerType.KernelType.DEFAULT; // 视频播放器内核
    @PlayerType.RenderType.Value
    public static int renderType = PlayerType.RenderType.DEFAULT; // 视频渲染类型
    @PlayerType.DecoderType.Value
    public static int decoderType = PlayerType.DecoderType.DEFAULT; // 解码器类型
    @PlayerType.ScaleType
    public static int scaleType = PlayerType.ScaleType.DEFAULT; // 视频缩放比例
    // 旋转角度
    @PlayerType.RotationType.Value
    public static int rotation = PlayerType.RotationType.DEFAULT;
    // 快进参数
    @PlayerType.SeekType.Value
    public static int seekType = PlayerType.SeekType.DEFAULT;
    // 缓存
    public static Cache cache = new Cache.Builder()
            .build();
    // 代理
    public static boolean noProxy = false;
    // 代理
    public static PlayBuried playBuried = null;

    public static Builder newBuilder() {
        return new Builder();
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

        // 快进参数
        @PlayerType.SeekType.Value
        private int seekType = PlayerType.SeekType.DEFAULT;

        // 缓存
        private Cache cache = new Cache.Builder()
                .build();

        // 代理
        private boolean noProxy = false;

        // 代理
        private PlayBuried playBuried = null;

        private Builder() {
        }

        public Builder setRotation(int v) {
            this.rotation = v;
            return this;
        }


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

        public Builder setCache(Cache v) {
            this.cache = v;
            return this;
        }

        public Builder setNoProxy(boolean v) {
            this.noProxy = v;
            return this;
        }

        public Builder setPlayBuried(PlayBuried v) {
            this.playBuried = v;
            return this;
        }

        public void init() {
            PlayerSDK.log = log;
            PlayerSDK.initRelease = initRelease;
            PlayerSDK.supportAutoRelease = supportAutoRelease;
            PlayerSDK.connectTimeoutMs = connectTimeoutMs;
            PlayerSDK.bufferingTimeoutRetry = bufferingTimeoutRetry;
            PlayerSDK.externalAudioKernel = externalAudioKernel;
            PlayerSDK.kernelType = kernelType;
            PlayerSDK.renderType = renderType;
            PlayerSDK.decoderType = decoderType;
            PlayerSDK.scaleType = scaleType;
            PlayerSDK.rotation = rotation;
            PlayerSDK.seekType = seekType;
            PlayerSDK.cache = cache;
            PlayerSDK.noProxy = noProxy;
            PlayerSDK.playBuried = playBuried;
        }
    }

    /***************/
}
