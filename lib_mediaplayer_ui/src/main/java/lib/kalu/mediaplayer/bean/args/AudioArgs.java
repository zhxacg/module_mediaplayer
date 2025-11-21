package lib.kalu.mediaplayer.bean.args;

import java.io.Serializable;

public final class AudioArgs implements Serializable {
    private String url;
    private String language;

    public boolean containsUrl() {
        return null != url && !url.isEmpty();
    }

    public AudioArgs(Builder builder) {
        this.url = builder.url;
        this.language = builder.language;
    }

    public String getLanguage() {
        return language;
    }

    public String getUrl() {
        return url;
    }

    public static class Builder implements Serializable {

        private String url;
        private String language;

        public AudioArgs.Builder setUrl(String v) {
            this.url = v;
            return this;
        }

        public AudioArgs.Builder setLanguage(String v) {
            this.language = v;
            return this;
        }

        public Builder() {
        }

        public AudioArgs build() {
            return new AudioArgs(this);
        }
    }

    @Override
    public String toString() {
        return "AudioArgs{" +
                "language='" + language + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
