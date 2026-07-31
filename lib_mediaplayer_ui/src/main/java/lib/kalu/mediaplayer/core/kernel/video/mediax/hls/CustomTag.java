package lib.kalu.mediaplayer.core.kernel.video.mediax.hls;

public enum CustomTag {

    MASTER_PLAY_URL_VIDEO("MASTER_PLAY_URL_VIDEO");
    private final String value;

    // 枚举构造方法（默认是 private 的）
    CustomTag(String value) {
        this.value = value;
    }

    // 获取内部绑定的字符串值
    public String getValue() {
        return value;
    }
    }