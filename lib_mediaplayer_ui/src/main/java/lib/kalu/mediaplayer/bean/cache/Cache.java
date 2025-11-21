package lib.kalu.mediaplayer.bean.cache;

import java.io.Serializable;
import java.util.LinkedHashMap;

import lib.kalu.mediaplayer.bean.type.PlayerType;

public final class Cache implements Serializable {

    private boolean enable;
    private boolean external;
    private int sizeMB;
    private LinkedHashMap<Integer, String> dirs;

    public boolean isEnable() {
        return enable;
    }

    public boolean isExternal() {
        return external;
    }

    public int getSizeMB() {
        return sizeMB;
    }

    public String getDir(@PlayerType.KernelType.Value int type) {
        return dirs.get(type);
    }

    private Cache(Cache.Builder builder) {
        enable = builder.enable;
        external = builder.external;
        sizeMB = builder.sizeMB;
        dirs = builder.dirs;
    }

    public final static class Builder {
        // 缓存开关
        private boolean enable;
        // 缓存内部开关
        private boolean external;
        // 缓存大小
        private int sizeMB = 1024;
        private LinkedHashMap<Integer, String> dirs = new LinkedHashMap<Integer, String>() {{
            put(PlayerType.KernelType.ANDROID, "android_cache");
            put(PlayerType.KernelType.IJK, "ijk_cache");
            put(PlayerType.KernelType.FFPLAYER, "ff_cache");
            put(PlayerType.KernelType.VLC, "vlc_cache");
            put(PlayerType.KernelType.EXO_V2, "exo2_cache");
            put(PlayerType.KernelType.MEDIA_V3, "media3_cache");
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
            this.sizeMB = v;
            return this;
        }

//        public Cache.Builder setDir(@PlayerType.KernelType.Value int type, String v) {
//            dirs.remove(type);
//            dirs.put(type, v);
//            return this;
//        }

        public Cache build() {
            return new Cache(this);
        }
    }
}
