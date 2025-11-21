package lib.kalu.mediaplayer.bean.args;

import java.io.Serializable;

import lib.kalu.mediaplayer.bean.type.PlayerType;

public final class SubtitleArgs implements Serializable {
    private String url;
    private String language;

    public SubtitleArgs(Builder builder) {
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

        public SubtitleArgs.Builder setUrl(String v) {
            this.url = v;
            return this;
        }

        public SubtitleArgs.Builder setLanguage(String v) {
            this.language = v;
            return this;
        }

        public Builder() {
        }

        public SubtitleArgs build() {
            return new SubtitleArgs(this);
        }
    }

    @Override
    public String toString() {
        return "SubtitleArgs{" +
                "language='" + language + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
