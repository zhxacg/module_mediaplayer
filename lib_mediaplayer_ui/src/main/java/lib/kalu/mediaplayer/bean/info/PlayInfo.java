package lib.kalu.mediaplayer.bean.info;

import lib.kalu.mediaplayer.bean.type.PlayerType;

public class PlayInfo {

    private long position;
    private long duration;
    private float speed;
    private @PlayerType.ScaleType int scale;

    public PlayInfo(long duration, float speed, long position, int scale) {
        this.duration = duration;
        this.speed = speed;
        this.position = position;
        this.scale = scale;
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
