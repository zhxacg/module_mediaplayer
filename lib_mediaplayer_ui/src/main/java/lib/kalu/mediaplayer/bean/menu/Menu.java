package lib.kalu.mediaplayer.bean.menu;

import java.io.Serializable;
import java.util.List;

import lib.kalu.mediaplayer.bean.args.StartArgs;

public final class Menu implements Serializable {

    private List<? extends Item> data;

    public List<? extends Item> getData() {
        return data;
    }

    public List<String> getPlayUrls() {
        try {
            for (Item item : data) {
                if (item instanceof Episode) {
                    return ((Episode) item).getPlayUrls();
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public int getPlayPos() {
        try {
            for (Item item : data) {
                if (item instanceof Episode) {
                    return ((Episode) item).getPlayPos();
                }
            }
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }

    public int getPlayCount() {
        try {
            for (Item item : data) {
                if (item instanceof Episode) {
                    return ((Episode) item).getPlayCount();
                }
            }
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }

    private Menu(Menu.Builder builder) {
        data = builder.data;
    }

    public final static class Builder {

        private List<? extends Item> data;

        public Menu.Builder setData(List<? extends Item> v) {
            this.data = v;
            return this;
        }

        public Menu build() {
            return new Menu(this);
        }
    }

    public static class Item implements Serializable {

        private String name;


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static final class Default extends Item implements Serializable {
        private int[] data;

        public int[] getData() {
            return data;
        }

        public void setData(int[] data) {
            this.data = data;
        }
    }

    public static final class Episode extends Item implements Serializable {

        private int freeCount;
        private int freeRes;
        private int vipRes;
        private int playPos;
        private int playCount;
        private List<String> playUrls;

        public int getFreeCount() {
            return freeCount;
        }

        public void setFreeCount(int freeCount) {
            this.freeCount = freeCount;
        }

        public int getFreeRes() {
            return freeRes;
        }

        public void setFreeRes(int freeRes) {
            this.freeRes = freeRes;
        }

        public int getVipRes() {
            return vipRes;
        }

        public void setVipRes(int vipRes) {
            this.vipRes = vipRes;
        }

        public int getPlayPos() {
            return playPos;
        }

        public void setPlayPos(int playPos) {
            this.playPos = playPos;
        }

        public List<String> getPlayUrls() {
            return playUrls;
        }

        public void setPlayUrls(List<String> playUrls) {
            this.playUrls = playUrls;
        }

        public int getPlayCount() {
            return playCount;
        }

        public void setPlayCount(int playCount) {
            this.playCount = playCount;
        }
    }
}
