package lib.kalu.mediaplayer.bean.cache;

import java.io.Serializable;
import java.util.LinkedHashMap;

import lib.kalu.mediaplayer.bean.type.PlayerType;

public final class Cache implements Serializable {

    private boolean enable;
    private boolean external;
    private int size;
    private LinkedHashMap<Integer, String> dirs;

    public boolean isEnable() {
        return enable;
    }

    public boolean isExternal() {
        return external;
    }

    public int getSizeMB() {
        return size;
    }

    public String getDir(@PlayerType.KernelType.Value int type) {
        return dirs.get(type);
    }

    private Cache(Cache.Builder builder) {
        enable = builder.enable;
        external = builder.external;
        size = builder.size;
        dirs = builder.dirs;
    }

    public final static class Builder {
        private boolean enable;
        private boolean external;
        private int size = 500;
        private LinkedHashMap<Integer, String> dirs = new LinkedHashMap<Integer, String>() {{
            put(PlayerType.KernelType.ANDROID, "android_video_cache");
            put(PlayerType.KernelType.IJK, "ijk_video_cache");
            put(PlayerType.KernelType.FFPLAYER, "ff_video_cache");
            put(PlayerType.KernelType.VLC, "vlc_video_cache");
            put(PlayerType.KernelType.EXO_V2, "exo2_video_cache");
            put(PlayerType.KernelType.MEDIA_V3, "media3_video_cache");
        }};

        public Cache.Builder setEnable(boolean v) {
            this.enable = v;
            return this;
        }

        public Cache.Builder setExternal(boolean v) {
            this.external = v;
            return this;
        }

        public Cache.Builder setSizeMB(int v) {
            this.size = v;
            return this;
        }

        public Cache.Builder setDir(@PlayerType.KernelType.Value int type, String v) {
            dirs.remove(type);
            dirs.put(type, v);
            return this;
        }

        public Cache build() {
            return new Cache(this);
        }
    }
}
