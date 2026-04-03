package lib.kalu.mediaplayer.bean.info;

import lib.kalu.mediaplayer.bean.type.PlayerType;

public class PlayInfo {

    private int subtitleOffsetMs = 0;
    private boolean isTrysee = false;
    private boolean isLive = false;
    private boolean isPrepare = false;
    private long position;
    private long duration;
    private float speed;
    private @PlayerType.ScaleType int scale;

    private String stopReason = "";

    public PlayInfo(String stopReason, boolean isPrepare, int subtitleOffsetMs, boolean isTrysee, boolean isLive, long position, long duration, float speed, int scale) {
        this.stopReason = stopReason;
        this.isPrepare = isPrepare;
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
                "subtitleOffsetMs=" + subtitleOffsetMs +
                ", isTrysee=" + isTrysee +
                ", isLive=" + isLive +
                ", isPrepare=" + isPrepare +
                ", position=" + position +
                ", duration=" + duration +
                ", speed=" + speed +
                ", scale=" + scale +
                ", stopReason='" + stopReason + '\'' +
                '}';
    }

    public String getStopReason() {
        return stopReason;
    }

    public boolean isPrepare() {
        return isPrepare;
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
