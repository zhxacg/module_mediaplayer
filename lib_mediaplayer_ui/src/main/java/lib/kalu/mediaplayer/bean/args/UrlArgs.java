package lib.kalu.mediaplayer.bean.args;

import java.io.Serializable;

public final class UrlArgs implements Serializable {
    private String mainUrl;
    private String[] extVideoUrl;
    private String[] extAudioUrl;
    private SubtitleArgs[] extSubtitleUrl;

    public UrlArgs(Builder builder) {
        this.mainUrl = builder.mainUrl;
        this.extVideoUrl = builder.extVideoUrl;
        this.extAudioUrl = builder.extAudioUrl;
        this.extSubtitleUrl = builder.extSubtitleUrl;
    }

    public SubtitleArgs[] getExtSubtitleUrl() {
        return extSubtitleUrl;
    }

    public String[] getExtAudioUrl() {
        return extAudioUrl;
    }

    public String[] getExtVideoUrl() {
        return extVideoUrl;
    }

    public String getMainUrl() {
        return mainUrl;
    }

    public static class Builder implements Serializable {

        private String mainUrl;
        private String[] extVideoUrl;
        private String[] extAudioUrl;
        private SubtitleArgs[] extSubtitleUrl;

        public UrlArgs.Builder setMainUrl(String v) {
            this.mainUrl = v;
            return this;
        }

        public UrlArgs.Builder setExtVideoUrl(String[] v) {
            this.extVideoUrl = v;
            return this;
        }

        public UrlArgs.Builder setExtAudioUrl(String[] v) {
            this.extAudioUrl = v;
            return this;
        }

        public UrlArgs.Builder setExtSubtitleUrl(SubtitleArgs[] v) {
            this.extSubtitleUrl = v;
            return this;
        }

        public Builder() {
        }

        public UrlArgs build() {
            return new UrlArgs(this);
        }
    }
}
