package lib.kalu.mediaplayer.proxy;

import java.io.Serializable;

public final class Proxy implements Serializable {

    private ProxyUrl proxyUrl;
    private ProxyBuried proxyBuried;

    public ProxyUrl getProxyUrl() {
        return proxyUrl;
    }

    public ProxyBuried getProxyBuried() {
        return proxyBuried;
    }

    private Proxy(Proxy.Builder builder) {
        proxyUrl = builder.proxyUrl;
        proxyBuried = builder.proxyBuried;
    }

    public final static class Builder {
        private ProxyUrl proxyUrl;
        private ProxyBuried proxyBuried;

        public Proxy.Builder setProxyUrl(ProxyUrl v) {
            this.proxyUrl = v;
            return this;
        }

        public Proxy.Builder setProxyBuried(ProxyBuried v) {
            this.proxyBuried = v;
            return this;
        }

        public Proxy build() {
            return new Proxy(this);
        }
    }
}
