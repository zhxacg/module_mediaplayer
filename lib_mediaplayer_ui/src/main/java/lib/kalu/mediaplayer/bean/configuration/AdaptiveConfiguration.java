package lib.kalu.mediaplayer.bean.configuration;

import java.io.Serializable;
import lib.kalu.mediaplayer.PlayerConst;
import lib.kalu.mediaplayer.PlayerSDK;
import lib.kalu.mediaplayer.bean.type.PlayerType;

public final class AdaptiveConfiguration implements Serializable {

    private boolean enable;
    private int minDurationForQualityIncreaseMs;
    private int maxDurationForQualityDecreaseMs;
    private int minDurationToRetainAfterDiscardMs;
    private int maxWidthToDiscard;
    private int maxHeightToDiscard;
    private float bandwidthFraction;
    private float bufferedFractionToLiveEdgeForQualityIncrease;

    public boolean isEnable() {
        return enable;
    }

    public int getMinDurationForQualityIncreaseMs() {
        return minDurationForQualityIncreaseMs;
    }

    public int getMaxDurationForQualityDecreaseMs() {
        return maxDurationForQualityDecreaseMs;
    }

    public int getMinDurationToRetainAfterDiscardMs() {
        return minDurationToRetainAfterDiscardMs;
    }

    public int getMaxWidthToDiscard() {
        return maxWidthToDiscard;
    }

    public int getMaxHeightToDiscard() {
        return maxHeightToDiscard;
    }

    public float getBandwidthFraction() {
        return bandwidthFraction;
    }

    public float getBufferedFractionToLiveEdgeForQualityIncrease() {
        return bufferedFractionToLiveEdgeForQualityIncrease;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "AdaptiveConfiguration{" +
                "enable=" + enable +
                ", minDurationForQualityIncreaseMs=" + minDurationForQualityIncreaseMs +
                ", maxDurationForQualityDecreaseMs=" + maxDurationForQualityDecreaseMs +
                ", minDurationToRetainAfterDiscardMs=" + minDurationToRetainAfterDiscardMs +
                ", maxWidthToDiscard=" + maxWidthToDiscard +
                ", maxHeightToDiscard=" + maxHeightToDiscard +
                ", bandwidthFraction=" + bandwidthFraction +
                ", bufferedFractionToLiveEdgeForQualityIncrease=" + bufferedFractionToLiveEdgeForQualityIncrease +
                '}';
    }

    public AdaptiveConfiguration(AdaptiveConfiguration.Builder builder) {
        this.enable = builder.enable;
        this.minDurationForQualityIncreaseMs = builder.minDurationForQualityIncreaseMs;
        this.maxDurationForQualityDecreaseMs = builder.maxDurationForQualityDecreaseMs;
        this.minDurationToRetainAfterDiscardMs = builder.minDurationToRetainAfterDiscardMs;
        this.maxWidthToDiscard = builder.maxWidthToDiscard;
        this.maxHeightToDiscard = builder.maxHeightToDiscard;
        this.bandwidthFraction = builder.bandwidthFraction;
        this.bufferedFractionToLiveEdgeForQualityIncrease = builder.bufferedFractionToLiveEdgeForQualityIncrease;
    }

    public static class Builder implements Serializable {

        private boolean enable = PlayerConst.AdaptiveConfiguration.DEFAULT_ENABLE;
        private int minDurationForQualityIncreaseMs = PlayerConst.AdaptiveConfiguration.DEFAULT_MIN_DURATION_FOR_QUALITY_INCREASE_MS;
        private int maxDurationForQualityDecreaseMs = PlayerConst.AdaptiveConfiguration.DEFAULT_MAX_DURATION_FOR_QUALITY_DECREASE_MS;
        private int minDurationToRetainAfterDiscardMs = PlayerConst.AdaptiveConfiguration.DEFAULT_MIN_DURATION_TO_RETAIN_AFTER_DISCARD_MS;
        private int maxWidthToDiscard = PlayerConst.AdaptiveConfiguration.DEFAULT_MAX_WIDTH_TO_DISCARD;
        private int maxHeightToDiscard = PlayerConst.AdaptiveConfiguration.DEFAULT_MAX_HEIGHT_TO_DISCARD;
        private float bandwidthFraction = PlayerConst.AdaptiveConfiguration.DEFAULT_BANDWIDTH_FRACTION;
        private float bufferedFractionToLiveEdgeForQualityIncrease = PlayerConst.AdaptiveConfiguration.DEFAULT_BUFFERED_FRACTION_TO_LIVE_EDGE_FOR_QUALITY_INCREASE;

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
         * 快速设置为【低端 TV 盒子/电视】优化配置
         */
        private Builder applyBoxDefaults() {
            this.enable = true;
            this.minDurationForQualityIncreaseMs = PlayerConst.AdaptiveConfiguration.BOX_MIN_DURATION_FOR_QUALITY_INCREASE_MS;
            this.maxDurationForQualityDecreaseMs = PlayerConst.AdaptiveConfiguration.BOX_MAX_DURATION_FOR_QUALITY_DECREASE_MS;
            this.minDurationToRetainAfterDiscardMs = PlayerConst.AdaptiveConfiguration.BOX_MIN_DURATION_TO_RETAIN_AFTER_DISCARD_MS;
            this.maxWidthToDiscard = PlayerConst.AdaptiveConfiguration.BOX_MAX_WIDTH_TO_DISCARD;
            this.maxHeightToDiscard = PlayerConst.AdaptiveConfiguration.BOX_MAX_HEIGHT_TO_DISCARD;
            this.bandwidthFraction = PlayerConst.AdaptiveConfiguration.BOX_BANDWIDTH_FRACTION;
            this.bufferedFractionToLiveEdgeForQualityIncrease = PlayerConst.AdaptiveConfiguration.BOX_BUFFERED_FRACTION_TO_LIVE_EDGE_FOR_QUALITY_INCREASE;
            return this;
        }

        /**
         * 快速设置为【智能手机/平板】优化配置
         */
        private Builder applyPhoneDefaults() {
            this.enable = true;
            this.minDurationForQualityIncreaseMs = PlayerConst.AdaptiveConfiguration.PHONE_MIN_DURATION_FOR_QUALITY_INCREASE_MS;
            this.maxDurationForQualityDecreaseMs = PlayerConst.AdaptiveConfiguration.PHONE_MAX_DURATION_FOR_QUALITY_DECREASE_MS;
            this.minDurationToRetainAfterDiscardMs = PlayerConst.AdaptiveConfiguration.PHONE_MIN_DURATION_TO_RETAIN_AFTER_DISCARD_MS;
            this.maxWidthToDiscard = PlayerConst.AdaptiveConfiguration.PHONE_MAX_WIDTH_TO_DISCARD;
            this.maxHeightToDiscard = PlayerConst.AdaptiveConfiguration.PHONE_MAX_HEIGHT_TO_DISCARD;
            this.bandwidthFraction = PlayerConst.AdaptiveConfiguration.PHONE_BANDWIDTH_FRACTION;
            this.bufferedFractionToLiveEdgeForQualityIncrease = PlayerConst.AdaptiveConfiguration.PHONE_BUFFERED_FRACTION_TO_LIVE_EDGE_FOR_QUALITY_INCREASE;
            return this;
        }

        public Builder setEnable(boolean v) {
            this.enable = v;
            return this;
        }

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

        public AdaptiveConfiguration build() {
            return new AdaptiveConfiguration(this);
        }
    }
}