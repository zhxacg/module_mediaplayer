package lib.kalu.mediaplayer.bean.configuration;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.proxy.Proxy;

/**
 * 重试策略
 */
public final class RetryConfiguration implements Serializable {

    @PlayerType.RetryType
    private int retryType;
    private List<RetryUrl> retryUrls;
    // 重试次数
    private int retryMax;
    private int retryIndex;

    @PlayerType.RetryType.Value
    public int getRetryType() {
        return retryType;
    }

    public int getRetryIndex() {
        return retryIndex;
    }

    public int getRetryMax() {
        return retryMax;
    }

    public List<RetryUrl> getRetryUrls() {
        return retryUrls;
    }

    public RetryConfiguration(Builder builder) {
        this.retryType = builder.retryType;
        this.retryMax = builder.retryMax;
        this.retryUrls = builder.retryUrls;
        this.retryIndex = builder.retryIndex;
    }

    public Builder newBuilderSelf() {
        Builder builder = new Builder();
        builder.retryType = retryType;
        builder.retryMax = retryMax;
        builder.retryUrls = retryUrls;
        builder.retryIndex = retryIndex;
        return builder;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "RetryConfiguration{" +
                "retryType=" + retryType +
                ", retryUrls=" + retryUrls +
                ", retryMax=" + retryMax +
                ", retryIndex=" + retryIndex +
                '}';
    }

    public static class Builder implements Serializable {


        @PlayerType.RetryType
        private int retryType = PlayerType.RetryType.SELF;
        private List<RetryUrl> retryUrls = Collections.emptyList();

        // 重试次数 默认3次
        private int retryMax = 3;

        private int retryIndex = 0;

        public Builder setRetryIndex(int v) {
            this.retryIndex = v;
            return this;
        }

        public Builder setRetryMax(int v) {
            this.retryMax = v;
            return this;
        }

        public Builder setRetryUrls(List<RetryUrl> v) {
            if (null != v && !v.isEmpty()) {
                this.retryType = PlayerType.RetryType.OTHER;
                this.retryUrls = v;
            }
            return this;
        }

        private Builder() {
        }

        public RetryConfiguration build() {
            return new RetryConfiguration(this);
        }
    }


    public final static class RetryUrl implements Serializable {

        private String url;
        private Proxy proxy;

        public String getUrl() {
            return url;
        }

        public Proxy getProxy() {
            return proxy;
        }

        public RetryUrl(Builder builder) {
            this.url = builder.url;
            this.proxy = builder.proxy;
        }

        public Builder newBuilderSelf() {
            Builder builder = new Builder();
            builder.url = url;
            builder.proxy = proxy;
            return builder;
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        @Override
        public String toString() {
            return "RetryUrl{" +
                    "url='" + url + '\'' +
                    ", proxy=" + proxy +
                    '}';
        }

        public static class Builder implements Serializable {

            private String url;
            private Proxy proxy;

            public Builder setUrl(String v) {
                this.url = v;
                return this;
            }

            public Builder setProxy(Proxy v) {
                this.proxy = v;
                return this;
            }

            private Builder() {
            }

            public RetryUrl build() {
                return new RetryUrl(this);
            }
        }
    }
}