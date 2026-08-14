package lib.kalu.mediaplayer.core.kernel.video;

import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;
import lib.kalu.mediaplayer.util.LogUtil;

public abstract class VideoBasePlayer implements VideoKernelApi {

    private VideoKernelApiEvent eventApi;
    private VideoPlayerApi playerApi;

    @Override
    public void setPlayerApi(VideoPlayerApi playerApi) {
        this.playerApi = playerApi;
    }

    @Override
    public VideoPlayerApi getPlayerApi() {
        return this.playerApi;
    }

    @Override
    public void setKernelApi(VideoKernelApiEvent eventApi) {
        this.eventApi = eventApi;
    }

    @Override
    public VideoKernelApiEvent getKernelApi() {
        return this.eventApi;
    }

    @Override
    public void onUpdateProgress(long trySeeDuration, long position, long duration) {
        try {
            if (null == eventApi) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoBasePlayer -> onUpdateProgress -> eventApi warning: null");
                }
                return;
            }
            eventApi.onUpdateProgress(trySeeDuration, position, duration);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoBasePlayer -> onUpdateProgress -> " + e.getMessage());
            }
        }
    }

    @Override
    public void onUpdateSubtitle(int kernel, CharSequence result) {
        try {
            if (null == eventApi) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoBasePlayer -> onUpdateSubtitle -> eventApi error: null");
                }
                return;
            }
            eventApi.onUpdateSubtitle(kernel, result);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoBasePlayer -> onUpdateSubtitle -> " + e.getMessage());
            }
        }
    }

    @Override
    public void onUpdateBandwidth(int kernel, long totalLoadTimeMs,long estimateKBs, long realAvgKBs) {
        try {
            if (null == eventApi) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoBasePlayer -> onUpdateBandwidth -> eventApi error: null");
                }
                return;
            }
            eventApi.onUpdateBandwidth(kernel, totalLoadTimeMs, estimateKBs, realAvgKBs);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoBasePlayer -> onUpdateBandwidth -> " + e.getMessage());
            }
        }
    }

    @Override
    public void onEvent(int kernel, int event) {
        try {
            if (null == eventApi) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoBasePlayer -> onEvent -> eventApi error: null");
                }
                return;
            }
            eventApi.onEvent(kernel, event);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoBasePlayer -> onEvent -> " + e.getMessage());
            }
        }
    }

    @Override
    public void onVideoFormatChanged(int kernel, int rotation, int scaleType, int width, int height, int bitrate) {
        try {
            if (null == eventApi) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoBasePlayer -> onVideoFormatChanged -> eventApi error: null");
                }
                return;
            }
            eventApi.onVideoFormatChanged(kernel, rotation, scaleType, width, height, bitrate);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoBasePlayer -> onVideoFormatChanged -> " + e.getMessage());
            }
        }
    }

    public final void setEvent(VideoKernelApiEvent eventApi) {
        this.eventApi = eventApi;
    }
}
