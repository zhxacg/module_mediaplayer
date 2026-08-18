package lib.kalu.mediaplayer.core.kernel.video;

import lib.kalu.mediaplayer.bean.type.PlayerType;


public interface VideoKernelApiEvent {

    default void onUpdateProgress(long trySeeDuration, long position, long duration) {
    }

    default void onUpdateSubtitle(int kernel, CharSequence result) {
    }

    default void onUpdateBandwidth(int kernel, long totalLoadTimeMs, long estimateKBs, long realAvgKBs) {
    }

    default void onUpdateStuckNet(int kernel, long videoBitrate, long netBitrate) {
    }

    default void onUpdateStuckPlay(int kernel, long position, long duration) {
    }

    default void onEvent(@PlayerType.KernelType.Value int kernel, @PlayerType.EventType.Value int event) {
    }

    default void onVideoFormatChanged(@PlayerType.KernelType.Value int kernel,
                                      @PlayerType.RotationType.Value int rotation,
                                      @PlayerType.ScaleType.Value int scaleType,
                                      int width, int height, int bitrate) {
    }
}
