package lib.kalu.mediaplayer.bean.cache;

import java.io.Serializable;

public final class Cache implements Serializable {

    private boolean enable;
    private boolean external;
    private int size;
    private String dir;


    public boolean isEnable() {
        return enable;
    }

    public boolean isExternal() {
        return external;
    }

    public int getSizeMB() {
        return size;
    }

    public String getDir() {
        return dir;
    }

    private Cache(Cache.Builder builder) {
        enable = builder.enable;
        external = builder.external;
        size = builder.size;
        dir = builder.dir;
    }

    public final static class Builder {
        private boolean enable;
        private boolean external;
        private int size = 500;
        private String dir = "video_cache";

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

        public Cache.Builder setDir(String v) {
            this.dir = v;
            return this;
        }


        public Cache build() {
            return new Cache(this);
        }
    }
}
