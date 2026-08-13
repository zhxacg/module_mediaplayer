package lib.kalu.mediaplayer.bean.configuration;

import java.io.Serializable;

import lib.kalu.mediaplayer.PlayerSDK;

/**
 * 超时 默认10s
 */
public final class TimeoutConfiguration implements Serializable {

    private int connectTimeoutMs;

    public int getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    public TimeoutConfiguration(Builder builder) {
        this.connectTimeoutMs = builder.connectTimeoutMs;
    }

    public Builder newBuilderFromThis() {
        Builder builder = new Builder();
        builder.connectTimeoutMs = connectTimeoutMs;
        return builder;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "TimeoutConfiguration{" +
                "connectTimeoutMs=" + connectTimeoutMs +
                '}';
    }

    public static class Builder implements Serializable {

        private int connectTimeoutMs = PlayerSDK.connectTimeoutMs;

        public Builder setConnectTimeoutMs(int v) {
            this.connectTimeoutMs = v;
            return this;
        }

        private Builder() {
        }

        public TimeoutConfiguration build() {
            return new TimeoutConfiguration(this);
        }
    }
}
