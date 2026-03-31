package lib.kalu.mediaplayer;


import lib.kalu.mediaplayer.bean.args.ConfigArgs;
import lib.kalu.mediaplayer.bean.cache.Cache;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.buried.PlayBuried;


public final class PlayerSDK {

    private ConfigArgs mConfig = null;
    private ConfigArgs.Builder mConfigBuilder;

    /***************/

    private static final class Holder {
        private static final PlayerSDK mInstance = new PlayerSDK();
    }

    public static PlayerSDK init() {
        return Holder.mInstance;
    }

    /***************/

    private PlayerSDK() {
        mConfigBuilder = new ConfigArgs.Builder();
    }

    public PlayerSDK setConnectTimeoutMs(int v) {
        this.mConfigBuilder.setConnectTimeoutMs(v);
        return this;
    }

    public PlayerSDK setBufferingTimeoutRetry(boolean v) {
        this.mConfigBuilder.setBufferingTimeoutRetry(v);
        return this;
    }

    public PlayerSDK setSeekType(@PlayerType.SeekType.Value int v) {
        mConfigBuilder.setSeekType(v);
        return this;
    }

    public PlayerSDK setLog(boolean v) {
        mConfigBuilder.setLog(v);
        return this;
    }

    public PlayerSDK setInitRelease(boolean v) {
        mConfigBuilder.setInitRelease(v);
        return this;
    }

    public PlayerSDK setSupportAutoRelease(boolean v) {
        mConfigBuilder.setSupportAutoRelease(v);
        return this;
    }

    public PlayerSDK setExternalAudioKernel(@PlayerType.KernelType.Value int v) {
        mConfigBuilder.setExternalAudioKernel(v);
        return this;
    }

    public PlayerSDK setKernelType(@PlayerType.KernelType.Value int v) {
        mConfigBuilder.setKernelType(v);
        return this;
    }

    public PlayerSDK setRenderType(@PlayerType.RenderType.Value int v) {
        mConfigBuilder.setRenderType(v);
        return this;
    }

    public PlayerSDK setDecoderType(@PlayerType.DecoderType.Value int v) {
        mConfigBuilder.setDecoderType(v);
        return this;
    }

    public PlayerSDK setScaleType(@PlayerType.ScaleType.Value int v) {
        mConfigBuilder.setScaleType(v);
        updatePlayerBuilder(false);
        return this;
    }

    public PlayerSDK setCache(Cache v) {
        mConfigBuilder.setCache(v);
        return this;
    }

    public PlayerSDK setPlayBuried(PlayBuried v) {
        mConfigBuilder.setPlayBuried(v);
        return this;
    }

    public void build() {
        mConfig = mConfigBuilder.build();
    }

    public ConfigArgs getPlayerBuilder() {
        updatePlayerBuilder(true);
        return mConfig;
    }

    private void updatePlayerBuilder(boolean check) {
        if (check) {
            if (null == mConfig) {
                mConfig = mConfigBuilder.build();
            }
        } else {
            mConfig = mConfigBuilder.build();
        }
    }

    /***************/
}
