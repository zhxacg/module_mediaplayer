package lib.kalu.mediaplayer.bean.configuration;

import java.io.Serializable;

import lib.kalu.mediaplayer.PlayerConst;
import lib.kalu.mediaplayer.PlayerSDK;
import lib.kalu.mediaplayer.bean.type.PlayerType;

/**
 * 卡顿/假死检测配置类
 * <p>
 * 用于对播放器运行状态进行健康度监测，避免播放器因为解码器挂起、网络无响应、硬件渲染假死或事件回调丢失而无限期阻塞。
 * 支持根据设备形态（TV 盒子 vs 智能手机）动态加载适配的超时检测阈值。
 * </p>
 */
public final class StuckConfiguration implements Serializable {

    /**
     * 缓冲状态卡死检测超时时间（单位：毫秒）
     * <p>
     * <b>判定场景：</b>播放器处于缓冲状态（如 {@code STATE_BUFFERING}），但长时间未收到任何网络数据或解码进度更新。<br>
     * <b>触发后果：</b>超时后通常判定为网络/数据源死锁，触发重新链接或切换备用线路。
     * </p>
     */
    private int bufferingDetectionTimeoutMs;

    /**
     * 播放状态假死检测超时时间（单位：毫秒）
     * <p>
     * <b>判定场景：</b>播放器处于播放状态（如 {@code STATE_READY} 或 {@code STATE_PLAYING}），但音视频渲染帧率长时间为 0（如画面冻结、有声无画或无声无画）。<br>
     * <b>触发后果：</b>超时后通常判定为硬件解码器卡死（Codec Hang）或渲染 Surface 异常，触发解码器重建或渲染器重置。
     * </p>
     */
    private int playingDetectionTimeoutMs;

    /**
     * 播放未结束卡死检测超时时间（单位：毫秒）
     * <p>
     * <b>判定场景：</b>播放进度已到达视频尾帧，但播放器长时间未正常抛出播放完成回调（如 {@code onPlaybackEnded}），导致停滞在尾帧状态。<br>
     * <b>触发后果：</b>超时后强制补发播放结束事件，避免上层 UI 无法正常切换下一集或自动退出。
     * </p>
     */
    private int playingNotEndingTimeoutMs;

    /**
     * 播放被抑制卡死检测超时时间（单位：毫秒）
     * <p>
     * <b>判定场景：</b>播放器被系统或外部暂停/抑制（如音频焦点丢失、后台播放限制），但长时间未能恢复正常播放或退出。<br>
     * <b>触发后果：</b>超时后进行主动回收或释放资源，防止播放服务在后台长时间占用系统资源。
     * </p>
     */
    private int suppressedDetectionTimeoutMs;

    /**
     * 获取缓冲卡死检测超时（ms）
     */
    public int getBufferingDetectionTimeoutMs() {
        return bufferingDetectionTimeoutMs;
    }

    /**
     * 获取播放假死检测超时（ms）
     */
    public int getPlayingDetectionTimeoutMs() {
        return playingDetectionTimeoutMs;
    }

    /**
     * 获取播放未结束卡死检测超时（ms）
     */
    public int getPlayingNotEndingTimeoutMs() {
        return playingNotEndingTimeoutMs;
    }

    /**
     * 获取播放被抑制超时（ms）
     */
    public int getSuppressedDetectionTimeoutMs() {
        return suppressedDetectionTimeoutMs;
    }

    public StuckConfiguration(StuckConfiguration.Builder builder) {
        this.bufferingDetectionTimeoutMs = builder.bufferingDetectionTimeoutMs;
        this.playingDetectionTimeoutMs = builder.playingDetectionTimeoutMs;
        this.playingNotEndingTimeoutMs = builder.playingNotEndingTimeoutMs;
        this.suppressedDetectionTimeoutMs = builder.suppressedDetectionTimeoutMs;
    }

    public static StuckConfiguration.Builder newBuilder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "StuckConfiguration{" +
                "bufferingDetectionTimeoutMs=" + bufferingDetectionTimeoutMs +
                ", playingDetectionTimeoutMs=" + playingDetectionTimeoutMs +
                ", playingNotEndingTimeoutMs=" + playingNotEndingTimeoutMs +
                ", suppressedDetectionTimeoutMs=" + suppressedDetectionTimeoutMs +
                '}';
    }

    /**
     * {@link StuckConfiguration} 构建器
     */
    public static class Builder implements Serializable {

        // =================================================================
        // 默认值统一从 PlayerConst.StuckConfiguration 读取
        // =================================================================

        /**
         * 缓冲状态卡死检测超时（毫秒）
         * <p>默认读取 {@link PlayerConst.StuckConfiguration#DEFAULT_BUFFERING_DETECTION_TIMEOUT_MS}</p>
         */
        private int bufferingDetectionTimeoutMs = PlayerConst.StuckConfiguration.DEFAULT_BUFFERING_DETECTION_TIMEOUT_MS;

        /**
         * 播放状态假死检测超时（毫秒）
         * <p>默认读取 {@link PlayerConst.StuckConfiguration#DEFAULT_PLAYING_DETECTION_TIMEOUT_MS}</p>
         */
        private int playingDetectionTimeoutMs = PlayerConst.StuckConfiguration.DEFAULT_PLAYING_DETECTION_TIMEOUT_MS;

        /**
         * 播放未结束卡死检测超时（毫秒）
         * <p>默认读取 {@link PlayerConst.StuckConfiguration#DEFAULT_PLAYING_NOT_ENDING_TIMEOUT_MS}</p>
         */
        private int playingNotEndingTimeoutMs = PlayerConst.StuckConfiguration.DEFAULT_PLAYING_NOT_ENDING_TIMEOUT_MS;

        /**
         * 播放被抑制卡死检测超时（毫秒）
         * <p>默认读取 {@link PlayerConst.StuckConfiguration#DEFAULT_SUPPRESSED_DETECTION_TIMEOUT_MS}</p>
         */
        private int suppressedDetectionTimeoutMs = PlayerConst.StuckConfiguration.DEFAULT_SUPPRESSED_DETECTION_TIMEOUT_MS;

        private Builder() {
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
         * 应用【低端 TV 盒子/电视】卡顿检测默认参数
         * <p>
         * <b>策略说明：</b>TV 盒子系统硬件性能较低且解码速度偏慢，适当延长超时门限（如缓冲 15s），
         * 避免因盒子自身渲染/解复用较慢而引发误判，导致播放器频繁重建。
         * </p>
         *
         * @return {@link Builder} 实例，支持链式调用
         */
        private Builder applyBoxDefaults() {
            this.bufferingDetectionTimeoutMs = PlayerConst.StuckConfiguration.BOX_BUFFERING_DETECTION_TIMEOUT_MS;
            this.playingDetectionTimeoutMs = PlayerConst.StuckConfiguration.BOX_PLAYING_DETECTION_TIMEOUT_MS;
            this.playingNotEndingTimeoutMs = PlayerConst.StuckConfiguration.BOX_PLAYING_NOT_ENDING_TIMEOUT_MS;
            this.suppressedDetectionTimeoutMs = PlayerConst.StuckConfiguration.BOX_SUPPRESSED_DETECTION_TIMEOUT_MS;
            return this;
        }

        /**
         * 应用【智能手机/平板】卡顿检测默认参数
         * <p>
         * <b>策略说明：</b>移动端性能强劲且网络并发高，用户对卡顿敏感度高。
         * 采用较紧凑的超时门限（如缓冲 8s），在出现卡死或无响应时能快速触发恢复机制。
         * </p>
         *
         * @return {@link Builder} 实例，支持链式调用
         */
        private Builder applyPhoneDefaults() {
            this.bufferingDetectionTimeoutMs = PlayerConst.StuckConfiguration.PHONE_BUFFERING_DETECTION_TIMEOUT_MS;
            this.playingDetectionTimeoutMs = PlayerConst.StuckConfiguration.PHONE_PLAYING_DETECTION_TIMEOUT_MS;
            this.playingNotEndingTimeoutMs = PlayerConst.StuckConfiguration.PHONE_PLAYING_NOT_ENDING_TIMEOUT_MS;
            this.suppressedDetectionTimeoutMs = PlayerConst.StuckConfiguration.PHONE_SUPPRESSED_DETECTION_TIMEOUT_MS;
            return this;
        }

        /**
         * 设置缓冲卡死检测超时时间
         *
         * @param v 超时时长（单位：毫秒）
         * @return {@link Builder}
         */
        public Builder setBufferingDetectionTimeoutMs(int v) {
            this.bufferingDetectionTimeoutMs = v;
            return this;
        }

        /**
         * 设置播放假死检测超时时间
         *
         * @param v 超时时长（单位：毫秒）
         * @return {@link Builder}
         */
        public Builder setPlayingDetectionTimeoutMs(int v) {
            this.playingDetectionTimeoutMs = v;
            return this;
        }

        /**
         * 设置播放未结束卡死检测超时时间
         *
         * @param v 超时时长（单位：毫秒）
         * @return {@link Builder}
         */
        public Builder setPlayingNotEndingTimeoutMs(int v) {
            this.playingNotEndingTimeoutMs = v;
            return this;
        }

        /**
         * 设置播放被抑制卡死检测超时时间
         *
         * @param v 超时时长（单位：毫秒）
         * @return {@link Builder}
         */
        public Builder setSuppressedDetectionTimeoutMs(int v) {
            this.suppressedDetectionTimeoutMs = v;
            return this;
        }

        /**
         * 构建 {@link StuckConfiguration} 对象
         */
        public StuckConfiguration build() {
            return new StuckConfiguration(this);
        }
    }
}