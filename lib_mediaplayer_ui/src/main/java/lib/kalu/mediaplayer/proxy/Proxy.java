package lib.kalu.mediaplayer.proxy;

import java.io.Serializable;

public final class Proxy implements Serializable {

    private ProxyUrl proxyUrl;
    private ProxyTrack proxyTrack;

    private Proxy() {
    }

    public ProxyUrl getProxyUrl() {
        return proxyUrl;
    }

    public ProxyTrack getProxyTrack() {
        return proxyTrack;
    }

    private Proxy(Proxy.Builder builder) {
        proxyUrl = builder.proxyUrl;
        proxyTrack = builder.proxyTrack;
    }

    public final static class Builder {
        private ProxyUrl proxyUrl;
        private ProxyTrack proxyTrack;

        public Proxy.Builder setProxyUrl(ProxyUrl v) {
            this.proxyUrl = v;
            return this;
        }

        public Proxy.Builder setProxyTrack(ProxyTrack v) {
            this.proxyTrack = v;
            return this;
        }

        public Proxy build() {
            return new Proxy(this);
        }
    }
}