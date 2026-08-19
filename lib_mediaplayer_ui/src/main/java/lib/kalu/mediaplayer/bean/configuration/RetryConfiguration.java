package lib.kalu.mediaplayer.bean.configuration;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 重试策略
 */
public final class RetryConfiguration implements Serializable {
    // 重试url
    private String[] retryUrls;
    // 重试次数
    private int retryCount;
    private int retryIndex;

    public int getRetryIndex() {
        return retryIndex;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public String[] getRetryUrls() {
        return retryUrls;
    }

    public RetryConfiguration(Builder builder) {
        this.retryCount = builder.retryCount;
        this.retryUrls = builder.retryUrls;
        this.retryIndex = builder.retryIndex;
    }
    public Builder newBuilderCopy() {
        Builder builder = new Builder();
        builder.retryCount = retryCount;
        builder.retryUrls = retryUrls;
        builder.retryIndex = retryIndex;
        return builder;
    }

    public  static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "RetryConfiguration{" +
                "retryUrls=" + Arrays.toString(retryUrls) +
                ", retryCount=" + retryCount +
                ", retryIndex=" + retryIndex +
                '}';
    }

    public static class Builder implements Serializable {

        // 重试url
        private String[] retryUrls = null;
        // 重试次数 默认3次
        private int retryCount = 3;

        private int retryIndex = 0;

        public Builder setRetryIndex(int v) {
            this.retryIndex = v;
            return this;
        }

        public Builder setRetryCount(int v) {
            this.retryCount = v;
            return this;
        }

        public Builder setRetryUrls(String[] v) {
            this.retryUrls = v;
            return this;
        }

        private Builder() {
        }

        public RetryConfiguration build() {
            return new RetryConfiguration(this);
        }
    }
}