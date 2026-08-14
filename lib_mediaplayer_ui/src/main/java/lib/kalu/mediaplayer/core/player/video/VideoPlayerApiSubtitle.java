package lib.kalu.mediaplayer.core.player.video;

import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.util.LogUtil;

public interface VideoPlayerApiSubtitle extends VideoPlayerApiBase, VideoPlayerApiCall {

    default boolean subtitleOffsetMs(int offset) {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoPlayerApiSubtitle", "subtitleOffsetMs -> warning: kernel null");
                }
                return false;
            }
            boolean playing = kernel.isPlaying();
            if (!playing) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoPlayerApiSubtitle", "subtitleOffsetMs -> warning: playing false");
                }
                return false;
            }
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
            if (null == kernel) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoPlayerApiSubtitle", "addSubtitleTrack -> warning: kernel null");
                }
                return false;
            }
            boolean playing = kernel.isPlaying();
            if (!playing) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoPlayerApiSubtitle", "addSubtitleTrack -> warning: playing false");
                }
                return false;
            }
            return kernel.addSubtitleTrack(url);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiSubtitle -> addSubtitleTrack -> " + e.getMessage());
            }
            return false;
        }
    }
}
