package lib.kalu.mediaplayer.bean.info;

import lib.kalu.mediaplayer.bean.type.PlayerType;

public class PlayInfo {

    private int subtitleOffsetMs = 0;
    private boolean isTrysee = false;
    private boolean isLive = false;
    private long position;
    private long duration;
    private float speed;
    private @PlayerType.ScaleType int scale;

    public PlayInfo(int subtitleOffsetMs, boolean isTrysee, boolean isLive, long position, long duration, float speed, int scale) {
        this.subtitleOffsetMs = subtitleOffsetMs;
        this.isTrysee = isTrysee;
        this.isLive = isLive;
        this.duration = duration;
        this.speed = speed;
        this.position = position;
        this.scale = scale;
    }

    @Override
    public String toString() {
        return "PlayInfo{" +
                "isTrysee=" + isTrysee +
                ", isLive=" + isLive +
                ", position=" + position +
                ", duration=" + duration +
                ", speed=" + speed +
                ", scale=" + scale +
                '}';
    }

    public boolean isTrysee() {
        return isTrysee;
    }

    public boolean isLive() {
        return isLive;
    }

    public long getPosition() {
        return position;
    }


    public long getDuration() {
        return duration;
    }


    public float getSpeed() {
        return speed;
    }

    public int getScale() {
        return scale;
    }

    public int getSubtitleOffsetMs() {
        return subtitleOffsetMs;
    }
}
