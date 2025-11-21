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

    public boolean containsMainUrl() {
        return null != mainUrl && !mainUrl.isEmpty();
    }

    public boolean containsExtUrl() {
        return (null != extVideoUrl && extVideoUrl.length > 0) || (null != extAudioUrl && extAudioUrl.length > 0) || (null != extSubtitleUrl && extSubtitleUrl.length > 0);
    }

    public int getUrlCount() {
        try {
            int result = 0;
            // mainUrl
            if (null != mainUrl && !mainUrl.isEmpty()) {
                result += 1;
            }
            // extVideoUrl
            if (null != extVideoUrl) {
                for (String url : extVideoUrl) {
                    if (null == url)
                        continue;
                    if (url.isEmpty())
                        continue;
                    result += 1;
                }
            }
            // extAudioUrl
            if (null != extAudioUrl) {
                for (String url : extAudioUrl) {
                    if (null == url)
                        continue;
                    if (url.isEmpty())
                        continue;
                    result += 1;
                }
            }
            // extVideoUrl
            if (null != extSubtitleUrl) {
                for (SubtitleArgs args : extSubtitleUrl) {
                    if (null == args)
                        continue;
                    String url = args.getUrl();
                    if (null == url)
                        continue;
                    if (url.isEmpty())
                        continue;
                    result += 1;
                }
            }
            return result;
        } catch (Exception e) {
            return 0;
        }
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
