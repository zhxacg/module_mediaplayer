package lib.kalu.mediaplayer.bean.configuration;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import lib.kalu.mediaplayer.proxy.Proxy;

/**
 * 重试策略
 */
public final class RetryConfiguration implements Serializable {

    private boolean retryEnable;
    private int retryCount;
    private List<RetryUrl> retryUrls;

    public boolean isRetryEnable() {
        return retryEnable;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public List<RetryUrl> getRetryUrls() {
        return retryUrls;
    }

    public RetryConfiguration(Builder builder) {
        this.retryEnable = builder.retryEnable;
        this.retryCount = builder.retryCount;
        this.retryUrls = builder.retryUrls;
    }

    public Builder newBuilderSelf() {
        Builder builder = new Builder();
        builder.retryEnable = retryEnable;
        builder.retryCount = retryCount;
        builder.retryUrls = retryUrls;
        return builder;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "RetryConfiguration{" +
                "retryEnable=" + retryEnable +
                ", retryCount=" + retryCount +
                ", retryUrls=" + retryUrls +
                '}';
    }

    public static class Builder implements Serializable {

        private boolean retryEnable = true;
        private int retryCount;
        private List<RetryUrl> retryUrls = Collections.emptyList();

        public Builder setRetryEnable(boolean v) {
            this.retryEnable = v;
            return this;
        }

        public Builder setRetryCount(int v) {
            this.retryCount = v;
            return this;
        }

        public Builder setRetryUrls(List<RetryUrl> v) {
            if (null != v && !v.isEmpty()) {
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