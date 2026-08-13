package lib.kalu.mediaplayer;


import lib.kalu.mediaplayer.bean.cache.Cache;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.buried.PlayBuried;
import lib.kalu.mediaplayer.util.LogUtil;


public final class PlayerSDK {

    public static boolean logEnable = PlayerConst.DEFAULT_LOG_ENABLE;
    public static boolean initRelease = true;
    public static boolean supportAutoRelease = true;
    public static int connectTimeoutMs = PlayerConst.DEFAULT_CONNECT_TIMEOUT;
    public static boolean bufferingTimeoutRetry = false; // 缓冲失败重试
    @PlayerType.DeviceType.Value
    public static int deviceType = PlayerConst.DEFAULT_TYPE_DEVICE;
    @PlayerType.KernelType.Value
    public static int externalAudioKernel = PlayerConst.DEFAULT_TYPE_KERNEL_EXTERNAL_AUDIO;
    @PlayerType.KernelType.Value
    public static int kernelType = PlayerConst.DEFAULT_TYPE_KERNEL;
    @PlayerType.RenderType.Value
    public static int renderType = PlayerConst.DEFAULT_TYPE_RENDER;
    @PlayerType.DecoderType.Value
    public static int decoderType = PlayerConst.DEFAULT_TYPE_DECODER;
    @PlayerType.ScaleType
    public static int scaleType = PlayerConst.DEFAULT_TYPE_SCALE;

    @PlayerType.RotationType.Value
    public static int rotation = PlayerConst.DEFAULT_TYPE_ROTATION;

    @PlayerType.SeekType.Value
    public static int seekType = PlayerConst.DEFAULT_TYPE_SEEK;
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

        private boolean logEnable = PlayerConst.DEFAULT_LOG_ENABLE;
        private boolean initRelease = true;
        private boolean supportAutoRelease = true;
        private int connectTimeoutMs = PlayerConst.DEFAULT_CONNECT_TIMEOUT;
        private boolean bufferingTimeoutRetry = false;
        @PlayerType.KernelType.Value
        private int externalAudioKernel = PlayerConst.DEFAULT_TYPE_KERNEL_EXTERNAL_AUDIO;
        @PlayerType.DeviceType.Value
        private int deviceType = PlayerConst.DEFAULT_TYPE_DEVICE;
        @PlayerType.KernelType.Value
        private int kernelType = PlayerConst.DEFAULT_TYPE_KERNEL;
        @PlayerType.RenderType.Value
        private int renderType = PlayerConst.DEFAULT_TYPE_RENDER;
        @PlayerType.DecoderType.Value
        private int decoderType = PlayerConst.DEFAULT_TYPE_DECODER;
        @PlayerType.ScaleType
        private int scaleType = PlayerConst.DEFAULT_TYPE_SCALE;

        @PlayerType.RotationType.Value
        private int rotation = PlayerConst.DEFAULT_TYPE_ROTATION;

        @PlayerType.SeekType.Value
        private int seekType = PlayerConst.DEFAULT_TYPE_SEEK;

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

        public Builder setLogEnable(boolean v) {
            logEnable = v;
            return this;
        }

        public Builder setDeviceType(@PlayerType.DeviceType.Value int v) {
            deviceType = v;
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
            PlayerSDK.logEnable = logEnable;
            PlayerSDK.initRelease = initRelease;
            PlayerSDK.supportAutoRelease = supportAutoRelease;
            PlayerSDK.connectTimeoutMs = connectTimeoutMs;
            PlayerSDK.bufferingTimeoutRetry = bufferingTimeoutRetry;
            PlayerSDK.externalAudioKernel = externalAudioKernel;
            PlayerSDK.decoderType = decoderType;
            PlayerSDK.kernelType = kernelType;
            PlayerSDK.renderType = renderType;
            PlayerSDK.decoderType = decoderType;
            PlayerSDK.scaleType = scaleType;
            PlayerSDK.rotation = rotation;
            PlayerSDK.seekType = seekType;
            PlayerSDK.cache = cache;
            PlayerSDK.noProxy = noProxy;
            PlayerSDK.playBuried = playBuried;
            //
            LogUtil.setEnable(PlayerSDK.logEnable);
        }
    }

    /***************/
}
