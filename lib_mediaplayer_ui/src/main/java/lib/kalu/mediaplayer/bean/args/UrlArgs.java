package lib.kalu.mediaplayer.bean.args;

import java.io.Serializable;

public final class UrlArgs implements Serializable {
    private VideoArgs mainVideoArgs;
    private VideoArgs[] extVideoArgs;
    private AudioArgs[] extAudioArgs;
    private SubtitleArgs[] extSubtitleArgs;

    public UrlArgs(Builder builder) {
        this.mainVideoArgs = builder.mainVideoArgs;
        this.extVideoArgs = builder.extVideoArgs;
        this.extAudioArgs = builder.extAudioArgs;
        this.extSubtitleArgs = builder.extSubtitleArgs;
    }

    public boolean containsMainUrl() {
        return null != mainVideoArgs && mainVideoArgs.containsUrl();
    }

    public boolean containsExtUrl() {
        return (null != extVideoArgs && extVideoArgs.length > 0) || (null != extAudioArgs && extAudioArgs.length > 0) || (null != extSubtitleArgs && extSubtitleArgs.length > 0);
    }

    public int getUrlCount() {
        try {
            int result = 0;
            // mainUrl
            if (null != mainVideoArgs && mainVideoArgs.containsUrl()) {
                result += 1;
            }
            // extVideoUrl
            if (null != extVideoArgs) {
                for (VideoArgs videoArgs : extVideoArgs) {
                    if (null == videoArgs)
                        continue;
                    if (!videoArgs.containsUrl())
                        continue;
                    result += 1;
                }
            }
            // extAudioUrl
            if (null != extAudioArgs) {
                for (AudioArgs url : extAudioArgs) {
                    if (null == url)
                        continue;
                    if (!url.containsUrl())
                        continue;
                    result += 1;
                }
            }
            // extVideoUrl
            if (null != extSubtitleArgs) {
                for (SubtitleArgs args : extSubtitleArgs) {
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

    public SubtitleArgs[] getExtSubtitle() {
        return extSubtitleArgs;
    }

    public AudioArgs[] getExtAudio() {
        return extAudioArgs;
    }

    public VideoArgs[] getExtVideo() {
        return extVideoArgs;
    }

    public VideoArgs getMainVideo() {
        return mainVideoArgs;
    }

    public String getMainUrl() {
        if (null == mainVideoArgs) {
            return null;
        } else {
            return mainVideoArgs.getUrl();
        }
    }

    public static class Builder implements Serializable {

        private VideoArgs mainVideoArgs;
        private VideoArgs[] extVideoArgs;
        private AudioArgs[] extAudioArgs;
        private SubtitleArgs[] extSubtitleArgs;

        public UrlArgs.Builder setMainUrl(String v) {
            this.mainVideoArgs = new VideoArgs.Builder().setUrl(v).setLanguage("Default").build();
            return this;
        }

        public UrlArgs.Builder setExtVideo(VideoArgs[] v) {
            this.extVideoArgs = v;
            return this;
        }

        public UrlArgs.Builder setExtAudio(AudioArgs[] v) {
            this.extAudioArgs = v;
            return this;
        }

        public UrlArgs.Builder setExtSubtitle(SubtitleArgs[] v) {
            this.extSubtitleArgs = v;
            return this;
        }

        public Builder() {
        }

        public UrlArgs build() {
            return new UrlArgs(this);
        }
    }
}
