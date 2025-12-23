package lib.kalu.mediaplayer.core.kernel.video;

public interface VideoKernelApiSubtitle {
    default boolean subtitleOffsetMs(int offset) {
        return false;
    }

    default boolean addSubtitleTrack(String url) {
        return false;
    }
}
