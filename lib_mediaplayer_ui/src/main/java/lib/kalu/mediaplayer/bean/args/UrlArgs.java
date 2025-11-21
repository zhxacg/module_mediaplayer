package lib.kalu.mediaplayer.bean.args;

import java.io.Serializable;

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
        private String url;
        private String language;
        private String label;

        private boolean onlyParserVideo;
        private boolean onlyParserAudio;
        private boolean onlyParserSubtitle;

        public boolean containsUrl() {
            return null != url && !url.isEmpty();
        }

        public Item(Item.Builder builder) {
            this.url = builder.url;
            this.language = builder.language;
            this.label = builder.label;
            this.onlyParserVideo = builder.onlyParserVideo;
            this.onlyParserAudio = builder.onlyParserAudio;
            this.onlyParserSubtitle = builder.onlyParserSubtitle;
        }

        public boolean isOnlyParserAudio() {
            return onlyParserAudio;
        }

        public boolean isOnlyParserSubtitle() {
            return onlyParserSubtitle;
        }

        public boolean isOnlyParserVideo() {
            return onlyParserVideo;
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

        public static class Builder implements Serializable {

            private String url;
            private String language;
            private String label;

            private boolean onlyParserVideo;
            private boolean onlyParserAudio;
            private boolean onlyParserSubtitle;

            public Item.Builder setOnlyParserVideo(boolean v) {
                this.onlyParserVideo = v;
                return this;
            }

            public Item.Builder setOnlyParserAudio(boolean v) {
                this.onlyParserAudio = v;
                return this;
            }

            public Item.Builder setOnlyParserSubtitle(boolean v) {
                this.onlyParserSubtitle = v;
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
                    "language='" + language + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }
    }
}
