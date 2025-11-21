package lib.kalu.mediaplayer.bean.args;

import java.io.Serializable;

public final class VideoArgs implements Serializable {
    private String url;
    private String language;

    public VideoArgs(Builder builder) {
        this.url = builder.url;
        this.language = builder.language;
    }

    public boolean containsUrl() {
        return null != url && !url.isEmpty();
    }

    public String getLanguage() {
        return language;
    }

    public String getUrl() {
        return url;
    }

    public static class Builder implements Serializable {

        private String url;
        private String language = "default";

        public VideoArgs.Builder setUrl(String v) {
            this.url = v;
            return this;
        }

        public VideoArgs.Builder setLanguage(String v) {
            this.language = v;
            return this;
        }

        public Builder() {
        }

        public VideoArgs build() {
            return new VideoArgs(this);
        }
    }

    @Override
    public String toString() {
        return "VideoArgs{" +
                "language='" + language + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
