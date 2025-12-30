package lib.kalu.mediaplayer.core.kernel.video;

import lib.kalu.mediaplayer.bean.type.PlayerType;


public interface VideoKernelApiEvent {

    default void onUpdateProgress() {
    }

    default void onUpdateSubtitle(int kernel, CharSequence result) {
    }

    default void onUpdateNetSpeed(int kernel) {
    }

    default void onEvent(@PlayerType.KernelType.Value int kernel, @PlayerType.EventType.Value int event) {
    }

    default void onVideoFormatChanged(@PlayerType.KernelType.Value int kernel,
                                      @PlayerType.RotationType.Value int rotation,
                                      @PlayerType.ScaleType.Value int scaleType,
                                      int width, int height, int bitrate) {
    }
}
