package lib.kalu.mediaplayer.bean.args;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import lib.kalu.mediaplayer.PlayerConst;
import lib.kalu.mediaplayer.bean.type.PlayerType;

public final class UrlArgs implements Serializable {

    @PlayerType.StreamType.Value
    private int streamType;
    private List<Item> defaultStreams;
    private List<Item> extraStreams;
    private List<Item> extraSubtitles;

    public int getStreamType() {
        return streamType;
    }

    @Override
    public String toString() {
        return "UrlArgs{" +
                "streamType=" + streamType +
                ", defaultStreams=" + defaultStreams +
                ", extraStreams=" + extraStreams +
                ", extraSubtitles=" + extraSubtitles +
                '}';
    }

    public UrlArgs(Builder builder) {
        this.defaultStreams = builder.defaultStreams;
        this.extraStreams = builder.extraStreams;
        this.extraSubtitles = builder.extraSubtitles;
        this.streamType = builder.streamType;
    }

    public boolean containsUrl() {
        try {
            for (Item item : defaultStreams) {
                if (item.parser == PlayerType.ParserType.VIDEO) {
                    return true;
                } else if (item.parser == PlayerType.ParserType.VIDEO_AUDIO) {
                    return true;
                } else if (item.parser == PlayerType.ParserType.VIDEO_AUDIO_SUBTITLE) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public String getDefaultUrl() {
        try {
            for (Item item : defaultStreams) {
                if (item.parser == PlayerType.ParserType.VIDEO) {
                    return item.url;
                } else if (item.parser == PlayerType.ParserType.DEFAULT) {
                    return item.url;
                }
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    public Item getMasterItem() {
        try {
            for (Item item : defaultStreams) {
                if (item.parser == PlayerType.ParserType.VIDEO) {
                    return item;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public List<Item> getExtraSubtitles() {
        return extraSubtitles;
    }

    public List<Item> getExtraStreams() {
        return extraStreams;
    }

    public List<Item> getDefaultStreams() {
        return defaultStreams;
    }

    public List<Item> getAllStreams() {
        LinkedList<Item> list = new LinkedList<>();
        if (null != defaultStreams && !defaultStreams.isEmpty()) {
            list.addAll(defaultStreams);
        }
        if (null != extraStreams && !extraStreams.isEmpty()) {
            list.addAll(extraStreams);
        }
        if (null != extraSubtitles && !extraSubtitles.isEmpty()) {
            list.addAll(extraSubtitles);
        }
        return list;
    }

    public static class Builder implements Serializable {

        private List<Item> defaultStreams = new LinkedList<>();
        private List<Item> extraStreams = new LinkedList<>();
        private List<Item> extraSubtitles = new LinkedList<>();

        @PlayerType.StreamType.Value
        private int streamType = PlayerConst.DEFAULT_STREAM_TYPE;

        public UrlArgs.Builder setStreamType(@PlayerType.StreamType.Value int v) {
            this.streamType = v;
            return this;
        }

        public UrlArgs.Builder setUrl(String v) {
            this.defaultStreams.clear();
            this.defaultStreams.add(new Item.Builder().setUrl(v)
                    .setParser(PlayerType.ParserType.DEFAULT)
                    .setLabel("DefaultVideo")
                    .setLanguage("DefaultVideo")
                    .setDefault(true)
                    .build());
            return this;
        }

        public UrlArgs.Builder setUrl(Item item) {
            this.defaultStreams.clear();

            if (item.isDefault()) {
                this.defaultStreams.add(item);
            } else {
                this.defaultStreams.add(new Item.Builder().setUrl(item.url)
                        .setParser(item.parser)
                        .setLabel(item.label)
                        .setLanguage(item.language)
                        .setDefault(true)
                        .build());
            }
            return this;
        }

//        public UrlArgs.Builder setUrl(String v) {
//            this.defaultStreams = new Item[]{new Item.Builder().setUrl(v)
//                    .setLabel("Default")
//                    .setLanguage("Default")
//                    .build()};
//            return this;
//        }

        public UrlArgs.Builder setDefaultStreams(List<Item> v) {
            this.defaultStreams = v;
            return this;
        }

        public UrlArgs.Builder setExtraStreams(List<Item> v) {
            this.extraStreams = v;
            return this;
        }

        public UrlArgs.Builder appendExtraStreams(List<Item> v) {
            this.extraStreams.addAll(v);
            return this;
        }

        public UrlArgs.Builder setExtraSubtitles(List<Item> v) {
            this.extraSubtitles = v;
            return this;
        }

        public UrlArgs.Builder appendExtraSubtitles(List<Item> v) {
            this.extraSubtitles.addAll(v);
            return this;
        }

        public Builder() {
        }

        public UrlArgs build() {
            return new UrlArgs(this);
        }
    }


    public final static class Item implements Serializable {

        private boolean def;
        private String url;
        private String language;
        private String label;
        @PlayerType.ParserType.Value
        private int parser;
        @PlayerType.ResolutionType.Value
        private String resolution;

        @Override
        public String toString() {
            return "Item{" +
                    "def=" + def +
                    ", url='" + url + '\'' +
                    ", language='" + language + '\'' +
                    ", label='" + label + '\'' +
                    ", parser=" + parser +
                    ", resolution='" + resolution + '\'' +
                    '}';
        }

        public boolean containsUrl() {
            return null != url && !url.isEmpty();
        }

        public Item(Item.Builder builder) {
            this.def = builder.def;
            this.url = builder.url;
            this.language = builder.language;
            this.label = builder.label;
            this.parser = builder.parser;
            this.resolution = builder.resolution;
        }

        public boolean isDefault() {
            return def;
        }

        @PlayerType.ParserType.Value
        public int getParser() {
            return parser;
        }

        @PlayerType.ResolutionType.Value
        public String getResolution() {
            return resolution;
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

            private boolean def;
            private String url;
            private String language;
            private String label;
            @PlayerType.ParserType.Value
            private int parser = PlayerConst.DEFAULT_TYPE_PRASE;
            @PlayerType.ResolutionType.Value
            private String resolution = PlayerConst.DEFAULT_TYPE_RESOLUTION;

            public Item.Builder setDefault(boolean v) {
                this.def = v;
                return this;
            }

            public Item.Builder setResolution(@PlayerType.ResolutionType.Value String v) {
                this.resolution = v;
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

    }
}
