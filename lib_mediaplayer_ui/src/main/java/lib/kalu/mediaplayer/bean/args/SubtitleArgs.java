package lib.kalu.mediaplayer.bean.args;

import java.io.Serializable;

import lib.kalu.mediaplayer.bean.type.PlayerType;

public final class SubtitleArgs implements Serializable {
    private String url;
    private String language;
    @PlayerType.TrackType.Value
    private String mimeType;

    public SubtitleArgs(Builder builder) {
        this.url = builder.url;
        this.language = builder.language;
        this.mimeType = builder.mimeType;
    }

    public String getLanguage() {
        return language;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getUrl() {
        return url;
    }

    public static class Builder implements Serializable {

        private String url;
        private String language;
        @PlayerType.TrackType.Value
        private String mimeType;

        public SubtitleArgs.Builder setUrl(String v) {
            this.url = v;
            return this;
        }

        public SubtitleArgs.Builder setLanguage(String v) {
            this.language = v;
            return this;
        }

        public SubtitleArgs.Builder setMimeType(@PlayerType.TrackType.Value String v) {
            this.mimeType = v;
            return this;
        }

        public Builder() {
        }

        public SubtitleArgs build() {
            return new SubtitleArgs(this);
        }
    }
}
