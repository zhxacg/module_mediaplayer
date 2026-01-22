package lib.kalu.mediaplayer.bean.args;

import java.io.Serializable;

import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public final class UrlArgs implements Serializable {
    private Item mainVideoArgs;
    private Item[] extVideoArgs;
    private Item[] extAudioArgs;
    private Item[] extSubtitleArgs;

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
                for (Item videoArgs : extVideoArgs) {
                    if (null == videoArgs)
                        continue;
                    if (!videoArgs.containsUrl())
                        continue;
                    result += 1;
                }
            }
            // extAudioUrl
            if (null != extAudioArgs) {
                for (Item url : extAudioArgs) {
                    if (null == url)
                        continue;
                    if (!url.containsUrl())
                        continue;
                    result += 1;
                }
            }
            // extVideoUrl
            if (null != extSubtitleArgs) {
                for (Item args : extSubtitleArgs) {
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

    public Item[] getExtSubtitle() {
        return extSubtitleArgs;
    }

    public Item[] getExtAudio() {
        return extAudioArgs;
    }

    public Item[] getExtVideo() {
        return extVideoArgs;
    }

    public Item getMainVideo() {
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

        private Item mainVideoArgs;
        private Item[] extVideoArgs;
        private Item[] extAudioArgs;
        private Item[] extSubtitleArgs;

        public UrlArgs.Builder setUrl(String v) {
            this.mainVideoArgs = new Item.Builder().setUrl(v).setLanguage("Default").build();
            return this;
        }

        public UrlArgs.Builder setUrl(UrlArgs.Item v) {
            this.mainVideoArgs = v;
            return this;
        }

        public UrlArgs.Builder setExtVideo(Item[] v) {
            this.extVideoArgs = v;
            return this;
        }

        public UrlArgs.Builder setExtAudio(Item[] v) {
            this.extAudioArgs = v;
            return this;
        }

        public UrlArgs.Builder setExtSubtitle(Item[] v) {
            this.extSubtitleArgs = v;
            return this;
        }

        public Builder() {
        }

        public UrlArgs build() {
            return new UrlArgs(this);
        }
    }


    public final static class Item implements Serializable {

        private boolean main;
        private String url;
        private String language;
        private String label;
        @PlayerType.ParserType.Value
        private int parser;

        public boolean containsUrl() {
            return null != url && !url.isEmpty();
        }

        public Item(Item.Builder builder) {
            this.main = builder.main;
            this.url = builder.url;
            this.language = builder.language;
            this.label = builder.label;
            this.parser = builder.parser;
        }

        public boolean isMain() {
            return main;
        }

        @PlayerType.ParserType.Value
        public int getParser() {
            return parser;
        }

        public String getLabel() {
            return label;
        }

        public String getLanguage() {
            return language;
        }

        public String getUrl() {
            return url;
        }

        @PlayerType.MetaType.Value
        public int getMetaType() {
            try {
                // rtmp
                if (url.startsWith(PlayerType.SchemeType.RTMP)) {
                    return PlayerType.MetaType.VIDEO_RTMP;
                }
                // rtsp
                else if (url.startsWith(PlayerType.SchemeType.RTSP)) {
                    return PlayerType.MetaType.VIDEO_RTSP;
                }
                // mp41
                else if (url.endsWith(PlayerType.SchemeType._MP4)) {
                    return PlayerType.MetaType.VIDEO_MP4;
                }
                // mp42
                else if (url.contains(PlayerType.SchemeType._MP4_)) {
                    return PlayerType.MetaType.VIDEO_MP4;
                }
                // dash1
                else if (url.endsWith(PlayerType.SchemeType._MPD)) {
                    return PlayerType.MetaType.VIDEO_DASH;
                }
                // dash2
                else if (url.contains(PlayerType.SchemeType._MPD_)) {
                    return PlayerType.MetaType.VIDEO_DASH;
                }
                // hls1
                else if (url.endsWith(PlayerType.SchemeType._M3U)) {
                    return PlayerType.MetaType.VIDEO_HLS;
                }
                // hls2
                else if (url.contains(PlayerType.SchemeType._M3U_)) {
                    return PlayerType.MetaType.VIDEO_HLS;
                }
                // hls3
                else if (url.endsWith(PlayerType.SchemeType._M3U8)) {
                    return PlayerType.MetaType.VIDEO_HLS;
                }
                // hls4
                else if (url.contains(PlayerType.SchemeType._M3U8_)) {
                    return PlayerType.MetaType.VIDEO_HLS;
                }
                // SmoothStreaming
                else if (url.matches(PlayerType.SchemeType._MATCHES)) {
                    return PlayerType.MetaType.VIDEO_SS;
                }
                // other
                else {
                    return PlayerType.MetaType.VIDEO_OTHER;
                }
            } catch (Exception e) {
                return PlayerType.MetaType.VIDEO_OTHER;
            }
        }

        public static class Builder implements Serializable {

            private boolean main;
            private String url;
            private String language;
            private String label;
            @PlayerType.ParserType.Value
            private int parser = PlayerType.ParserType.DEFAULT;

            public Item.Builder setMain(boolean v) {
                this.main = v;
                return this;
            }

            public Item.Builder setParser(@PlayerType.ParserType.Value int v) {
                this.parser = v;
                return this;
            }

            public Item.Builder setUrl(String v) {
                this.url = v;
                return this;
            }

            public Item.Builder setLanguage(String v) {
                this.language = v;
                return this;
            }

            public Item.Builder setLabel(String v) {
                this.label = v;
                return this;
            }

            public Builder() {
            }

            public Item build() {
                return new Item(this);
            }
        }

        @Override
        public String toString() {
            return "Item{" +
                    "label='" + label + '\'' +
                    ", main='" + main + '\'' +
                    ", url='" + url + '\'' +
                    ", language='" + language + '\'' +
                    ", parser=" + parser +
                    '}';
        }
    }
}
