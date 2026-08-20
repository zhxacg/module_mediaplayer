package lib.kalu.mediaplayer.bean.configuration;

import java.io.Serializable;

import lib.kalu.mediaplayer.PlayerConst;
import lib.kalu.mediaplayer.PlayerSDK;
import lib.kalu.mediaplayer.bean.type.PlayerType;

/**
 * 缓冲区与加载控制策略配置类
 * <p>
 * 对应 ExoPlayer / Media3 中的 {@code DefaultLoadControl} 参数配置。
 * 决定播放器的起播缓冲门限、卡顿二次恢复门限、最大/最小内存驻留缓存量以及缓冲超时阈值。
 * </p>
 */
public final class BufferConfiguration implements Serializable {

    /**
     * 内存池中单个缓冲区 (Allocation) 的字节大小**。
     */
    private int individualAllocationSize;


    /**
     * 最小缓冲区大小（毫秒）
     * <p>当已缓存时长低于该值时，播放器将重新开启网络加载。</p>
     */
    private int minBufferMs;

    /**
     * 最大缓冲区大小（毫秒）
     * <p>当已缓存时长达到该值时，播放器将暂停主动加载，以控制内存占用。</p>
     */
    private int maxBufferMs;

    /**
     * 起播/恢复播放所需的最小缓冲时长（毫秒）
     * <p>首次播放或 Seek 结束时，必须储备至少此门限的数据才开始渲染播放。</p>
     */
    private int bufferForPlaybackMs;

    /**
     * 卡顿/二次缓冲（Rebuffer）后恢复播放所需的最小缓冲时长（毫秒）
     * <p>因网络波动导致 Buffer 干涸暂停后，必须重新储备至少此门限的数据才恢复播放。</p>
     */
    private int bufferForPlaybackAfterRebufferMs;

    public int getIndividualAllocationSize() {
        return individualAllocationSize;
    }

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


    @Override
    public String toString() {
        return "BufferConfiguration{" +
                "individualAllocationSize=" + individualAllocationSize +
                ", minBufferMs=" + minBufferMs +
                ", maxBufferMs=" + maxBufferMs +
                ", bufferForPlaybackMs=" + bufferForPlaybackMs +
                ", bufferForPlaybackAfterRebufferMs=" + bufferForPlaybackAfterRebufferMs +
                '}';
    }

    public BufferConfiguration(BufferConfiguration.Builder builder) {
        this.individualAllocationSize = builder.individualAllocationSize;
        this.minBufferMs = builder.minBufferMs;
        this.maxBufferMs = builder.maxBufferMs;
        this.bufferForPlaybackMs = builder.bufferForPlaybackMs;
        this.bufferForPlaybackAfterRebufferMs = builder.bufferForPlaybackAfterRebufferMs;
    }

    public static BufferConfiguration.Builder newBuilder() {
        return new Builder();
    }

    public BufferConfiguration.Builder newBuilderCopy() {
        BufferConfiguration.Builder builder = new BufferConfiguration.Builder();
        builder.individualAllocationSize = individualAllocationSize;
        builder.minBufferMs = minBufferMs;
        builder.maxBufferMs = maxBufferMs;
        builder.bufferForPlaybackMs = bufferForPlaybackMs;
        builder.bufferForPlaybackAfterRebufferMs = bufferForPlaybackAfterRebufferMs;
        return builder;
    }

    /**
     * {@link BufferConfiguration} 构建器
     */
    public static class Builder implements Serializable {

        // =================================================================
        // 默认值统一从 PlayerConst.BufferConfiguration 读取
        // =================================================================


        private int individualAllocationSize = PlayerConst.BufferConfiguration.DEFAULT_INDIVIDUAL_ALLOCATION_SIZE;


        /**
         * 最小缓冲区大小（毫秒）
         * <p>默认读取 {@link PlayerConst.BufferConfiguration#DEFAULT_MIN_BUFFER_MS}</p>
         */
        private int minBufferMs = PlayerConst.BufferConfiguration.DEFAULT_MIN_BUFFER_MS;

        /**
         * 最大缓冲区大小（毫秒）
         * <p>默认读取 {@link PlayerConst.BufferConfiguration#DEFAULT_MAX_BUFFER_MS}</p>
         */
        private int maxBufferMs = PlayerConst.BufferConfiguration.DEFAULT_MAX_BUFFER_MS;

        /**
         * 启播所需的最小缓冲时长（毫秒）
         * <p>默认读取 {@link PlayerConst.BufferConfiguration#DEFAULT_BUFFER_FOR_PLAYBACK_MS}</p>
         */
        private int bufferForPlaybackMs = PlayerConst.BufferConfiguration.DEFAULT_BUFFER_FOR_PLAYBACK_MS;

        /**
         * 卡顿后恢复播放所需的最小缓冲时长（毫秒）
         * <p>默认读取 {@link PlayerConst.BufferConfiguration#DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS}</p>
         */
        private int bufferForPlaybackAfterRebufferMs = PlayerConst.BufferConfiguration.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS;

        public Builder() {
        }

        public Builder applyDefaults() {
            if (PlayerSDK.decoderType == PlayerType.DeviceType.PHONE) {
                return applyPhoneDefaults();
            } else {
                applyBoxDefaults();
            }
            return this;
        }

        /**
         * 应用【低端 TV 盒子/电视】缓冲控制参数
         * <p>
         * <b>策略说明：</b><br>
         * 1. 降低最大缓存上限（30s），防止低端盒子内存不足引发 OOM。<br>
         * 2. 提高起播与卡顿恢复门限（2.5s / 4s），给解码器和渲染 Surface 预留充足时间，防止频繁黑屏卡顿。
         * </p>
         *
         * @return {@link Builder}
         */
        private Builder applyBoxDefaults() {
            this.individualAllocationSize = PlayerConst.BufferConfiguration.BOX_INDIVIDUAL_ALLOCATION_SIZE;
            this.minBufferMs = PlayerConst.BufferConfiguration.BOX_MIN_BUFFER_MS;
            this.maxBufferMs = PlayerConst.BufferConfiguration.BOX_MAX_BUFFER_MS;
            this.bufferForPlaybackMs = PlayerConst.BufferConfiguration.BOX_BUFFER_FOR_PLAYBACK_MS;
            this.bufferForPlaybackAfterRebufferMs = PlayerConst.BufferConfiguration.BOX_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS;
            return this;
        }

        /**
         * 应用【智能手机/平板】缓冲控制参数
         * <p>
         * <b>策略说明：</b><br>
         * 1. 允许更大的最大缓存（50s），提升 Seek 与快进流畅度。<br>
         * 2. 较紧凑的起播门限（1s / 2s），追求极致秒开体验。
         * </p>
         *
         * @return {@link Builder}
         */
        private Builder applyPhoneDefaults() {
            this.individualAllocationSize = PlayerConst.BufferConfiguration.PHONE_INDIVIDUAL_ALLOCATION_SIZE;
            this.minBufferMs = PlayerConst.BufferConfiguration.PHONE_MIN_BUFFER_MS;
            this.maxBufferMs = PlayerConst.BufferConfiguration.PHONE_MAX_BUFFER_MS;
            this.bufferForPlaybackMs = PlayerConst.BufferConfiguration.PHONE_BUFFER_FOR_PLAYBACK_MS;
            this.bufferForPlaybackAfterRebufferMs = PlayerConst.BufferConfiguration.PHONE_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS;
            return this;
        }

        /**
         * 设置最小缓冲区大小（ms）
         */
        public Builder setMinBufferMs(int v) {
            this.minBufferMs = v;
            return this;
        }

        /**
         * 设置最大缓冲区大小（ms）
         */
        public Builder setMaxBufferMs(int v) {
            this.maxBufferMs = v;
            return this;
        }

        /**
         * 设置启播所需的最小缓冲时长（ms）
         */
        public Builder setBufferForPlaybackMs(int v) {
            this.bufferForPlaybackMs = v;
            return this;
        }

        /**
         * 设置卡顿后恢复播放所需的最小缓冲时长（ms）
         */
        public Builder setBufferForPlaybackAfterRebufferMs(int v) {
            this.bufferForPlaybackAfterRebufferMs = v;
            return this;
        }

        /**
         * 构建 {@link BufferConfiguration} 对象
         */
        public BufferConfiguration build() {
            return new BufferConfiguration(this);
        }
    }
}