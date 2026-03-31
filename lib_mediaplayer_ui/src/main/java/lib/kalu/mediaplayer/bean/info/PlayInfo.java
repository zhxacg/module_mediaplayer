package lib.kalu.mediaplayer.bean.info;

import lib.kalu.mediaplayer.bean.type.PlayerType;

public class PlayInfo {

    private boolean isLive = false;
    private long position;
    private long duration;
    private float speed;
    private @PlayerType.ScaleType int scale;

    public PlayInfo(boolean isLive, long position, long duration, float speed, int scale) {
        this.isLive = isLive;
        this.duration = duration;
        this.speed = speed;
        this.position = position;
        this.scale = scale;
    }

    @Override
    public String toString() {
        return "PlayInfo{" +
                "position=" + position +
                ", duration=" + duration +
                ", speed=" + speed +
                ", scale=" + scale +
                '}';
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
}
