package lib.kalu.mediaplayer.bean.configuration;

import java.io.Serializable;

import lib.kalu.mediaplayer.PlayerConst;
import lib.kalu.mediaplayer.PlayerSDK;
import lib.kalu.mediaplayer.bean.type.PlayerType;

/**
 * 用于管理自适应码率切换（ABR）与直播低延时追帧/追时差（Live Offset Control）的动态联动
 */
public final class LiveConfiguration implements Serializable {

    private long minUpdateIntervalMs;
    private float proportionalControlFactorUs;
    private long maxLiveOffsetErrorUsForUnitSpeed;
    private long targetLiveOffsetIncrementOnRebufferUs;
    private float minPossibleLiveOffsetSmoothingFactor;

    private long targetOffsetMs;
    private long minOffsetMs;
    private long maxOffsetMs;
    private float minPlaybackSpeed;
    private float fallbackMinPlaybackSpeed;
    private float maxPlaybackSpeed;
    private float fallbackMaxPlaybackSpeed;

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

    @Override
    public String toString() {
        return "LiveConfiguration{" +
                "minUpdateIntervalMs=" + minUpdateIntervalMs +
                ", proportionalControlFactorUs=" + proportionalControlFactorUs +
                ", maxLiveOffsetErrorUsForUnitSpeed=" + maxLiveOffsetErrorUsForUnitSpeed +
                ", targetLiveOffsetIncrementOnRebufferUs=" + targetLiveOffsetIncrementOnRebufferUs +
                ", minPossibleLiveOffsetSmoothingFactor=" + minPossibleLiveOffsetSmoothingFactor +
                ", targetOffsetMs=" + targetOffsetMs +
                ", minOffsetMs=" + minOffsetMs +
                ", maxOffsetMs=" + maxOffsetMs +
                ", minPlaybackSpeed=" + minPlaybackSpeed +
                ", fallbackMinPlaybackSpeed=" + fallbackMinPlaybackSpeed +
                ", maxPlaybackSpeed=" + maxPlaybackSpeed +
                ", fallbackMaxPlaybackSpeed=" + fallbackMaxPlaybackSpeed +
                '}';
    }

    public LiveConfiguration(LiveConfiguration.Builder builder) {
        this.fallbackMinPlaybackSpeed = builder.fallbackMinPlaybackSpeed;
        this.fallbackMaxPlaybackSpeed = builder.fallbackMaxPlaybackSpeed;
        this.minUpdateIntervalMs = builder.minUpdateIntervalMs;
        this.proportionalControlFactorUs = builder.proportionalControlFactorUs;
        this.maxLiveOffsetErrorUsForUnitSpeed = builder.maxLiveOffsetErrorUsForUnitSpeed;
        this.targetLiveOffsetIncrementOnRebufferUs = builder.targetLiveOffsetIncrementOnRebufferUs;
        this.minPossibleLiveOffsetSmoothingFactor = builder.minPossibleLiveOffsetSmoothingFactor;
        this.targetOffsetMs = builder.targetOffsetMs;
        this.minOffsetMs = builder.minOffsetMs;
        this.maxOffsetMs = builder.maxOffsetMs;
        this.minPlaybackSpeed = builder.minPlaybackSpeed;
        this.maxPlaybackSpeed = builder.maxPlaybackSpeed;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder implements Serializable {

        // =================================================================
        // 所有字段默认值全部指向 Cons (PlayerConst.LiveConfiguration)
        // =================================================================
        private long minUpdateIntervalMs = PlayerConst.LiveConfiguration.DEFAULT_MIN_UPDATE_INTERVAL_MS;
        private float proportionalControlFactorUs = PlayerConst.LiveConfiguration.DEFAULT_PROPORTIONAL_CONTROL_FACTOR_US;
        private long maxLiveOffsetErrorUsForUnitSpeed = PlayerConst.LiveConfiguration.DEFAULT_MAX_LIVE_OFFSET_ERROR_US_FOR_UNIT_SPEED;
        private long targetLiveOffsetIncrementOnRebufferUs = PlayerConst.LiveConfiguration.DEFAULT_TARGET_LIVE_OFFSET_INCREMENT_ON_REBUFFER_US;
        private float minPossibleLiveOffsetSmoothingFactor = PlayerConst.LiveConfiguration.DEFAULT_MIN_POSSIBLE_LIVE_OFFSET_SMOOTHING_FACTOR;

        private long targetOffsetMs = PlayerConst.LiveConfiguration.DEFAULT_TARGET_OFFSET_MS;
        private long minOffsetMs = PlayerConst.LiveConfiguration.DEFAULT_MIN_OFFSET_MS;
        private long maxOffsetMs = PlayerConst.LiveConfiguration.DEFAULT_MAX_OFFSET_MS;
        private float minPlaybackSpeed = PlayerConst.LiveConfiguration.DEFAULT_MIN_PLAYBACK_SPEED;
        private float fallbackMinPlaybackSpeed = PlayerConst.LiveConfiguration.DEFAULT_MIN_PLAYBACK_SPEED;
        private float maxPlaybackSpeed = PlayerConst.LiveConfiguration.DEFAULT_MAX_PLAYBACK_SPEED;
        private float fallbackMaxPlaybackSpeed = PlayerConst.LiveConfiguration.DEFAULT_MAX_PLAYBACK_SPEED;

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
         * 应用【低端 TV 盒子/电视】参数（取自 Cons 中的 BOX 属性）
         */
        private Builder applyBoxDefaults() {
            this.targetLiveOffsetIncrementOnRebufferUs = PlayerConst.LiveConfiguration.BOX_TARGET_LIVE_OFFSET_INCREMENT_ON_REBUFFER_US;
            this.targetOffsetMs = PlayerConst.LiveConfiguration.BOX_TARGET_OFFSET_MS;
            this.minOffsetMs = PlayerConst.LiveConfiguration.BOX_MIN_OFFSET_MS;
            this.maxOffsetMs = PlayerConst.LiveConfiguration.BOX_MAX_OFFSET_MS;

            this.minPlaybackSpeed = PlayerConst.LiveConfiguration.BOX_MIN_PLAYBACK_SPEED;
            this.fallbackMinPlaybackSpeed = PlayerConst.LiveConfiguration.BOX_MIN_PLAYBACK_SPEED;

            this.maxPlaybackSpeed = PlayerConst.LiveConfiguration.BOX_MAX_PLAYBACK_SPEED;
            this.fallbackMaxPlaybackSpeed = PlayerConst.LiveConfiguration.BOX_MAX_PLAYBACK_SPEED;
            return this;
        }

        /**
         * 应用【智能手机/平板】参数（取自 Cons 中的 PHONE 属性）
         */
        private Builder applyPhoneDefaults() {
            this.targetLiveOffsetIncrementOnRebufferUs = PlayerConst.LiveConfiguration.PHONE_TARGET_LIVE_OFFSET_INCREMENT_ON_REBUFFER_US;
            this.targetOffsetMs = PlayerConst.LiveConfiguration.PHONE_TARGET_OFFSET_MS;
            this.minOffsetMs = PlayerConst.LiveConfiguration.PHONE_MIN_OFFSET_MS;
            this.maxOffsetMs = PlayerConst.LiveConfiguration.PHONE_MAX_OFFSET_MS;

            this.minPlaybackSpeed = PlayerConst.LiveConfiguration.PHONE_MIN_PLAYBACK_SPEED;
            this.fallbackMinPlaybackSpeed = PlayerConst.LiveConfiguration.PHONE_MIN_PLAYBACK_SPEED;

            this.maxPlaybackSpeed = PlayerConst.LiveConfiguration.PHONE_MAX_PLAYBACK_SPEED;
            this.fallbackMaxPlaybackSpeed = PlayerConst.LiveConfiguration.PHONE_MAX_PLAYBACK_SPEED;
            return this;
        }

        public Builder setTargetOffsetMs(long v) {
            this.targetOffsetMs = v;
            return this;
        }

        public Builder setMinOffsetMs(long v) {
            this.minOffsetMs = v;
            return this;
        }

        public Builder setMaxOffsetMs(long v) {
            this.maxOffsetMs = v;
            return this;
        }

        public Builder setMinPlaybackSpeed(float v) {
            this.minPlaybackSpeed = v;
            return this;
        }

        public Builder setMaxPlaybackSpeed(float v) {
            this.maxPlaybackSpeed = v;
            return this;
        }

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

        public LiveConfiguration build() {
            return new LiveConfiguration(this);
        }
    }
}