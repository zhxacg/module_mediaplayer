package lib.kalu.mediaplayer.bean.info;

import java.io.Serializable;

public final class HlsSpanInfo implements Serializable {

    private int position;
    private int count;
    private String url;
    private String path;
    private long startTimeMs;
    private long endTimeMs;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getEndTimeMs() {
        return endTimeMs;
    }

    public void setEndTimeMs(long endTimeMs) {
        this.endTimeMs = endTimeMs;
    }

    public long getStartTimeMs() {
        return startTimeMs;
    }

    public void setStartTimeMs(long startTimeMs) {
        this.startTimeMs = startTimeMs;
    }

    @Override
    public String toString() {
        return "HlsSpanInfo{" +
                "endTimeMs=" + endTimeMs +
                ", url='" + url + '\'' +
                ", path='" + path + '\'' +
                ", startTimeMs=" + startTimeMs +
                '}';
    }
}
