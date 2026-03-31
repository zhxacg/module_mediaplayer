package lib.kalu.mediaplayer.core.player.video;

import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.util.LogUtil;

public interface VideoPlayerApiSubtitle extends VideoPlayerApiBase, VideoPlayerApiCall {

    default boolean subtitleOffsetMs(int offset) {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            boolean playing = kernel.isPlaying();
            if (!playing)
                throw new Exception("warning: playing false");
            boolean result = kernel.subtitleOffsetMs(offset);
            if (result) {
                onBuriedSubtitleOffsetMs(offset);
            }
            return result;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiSubtitle -> subtitleOffsetMs -> " + e.getMessage());
            }
            return false;
        }
    }

    default boolean addSubtitleTrack(String url) {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            boolean playing = kernel.isPlaying();
            if (!playing)
                throw new Exception("warning: playing false");
            return kernel.addSubtitleTrack(url);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiSubtitle -> addSubtitleTrack -> " + e.getMessage());
            }
            return false;
        }
    }
}
