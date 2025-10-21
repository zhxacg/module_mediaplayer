package lib.kalu.mediaplayer.core.player.video;

import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.util.LogUtil;

public interface VideoPlayerApiSubtitle extends VideoPlayerApiBase {

    default boolean appendSubtitleOffsetMs(int offset) {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("warning: kernel null");
            boolean playing = kernel.isPlaying();
            if (!playing)
                throw new Exception("warning: playing false");
            return kernel.appendSubtitleOffsetMs(offset);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiSubtitle => appendSubtitleOffsetMs => " + e.getMessage());
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
            LogUtil.log("VideoPlayerApiSubtitle => addSubtitleTrack => " + e.getMessage());
            return false;
        }
    }
}
