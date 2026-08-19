package lib.kalu.mediaplayer.core.player.video;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import lib.kalu.mediaplayer.bean.args.StartArgs;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.core.render.VideoRenderApi;
import lib.kalu.mediaplayer.core.render.VideoRenderFactoryManager;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public interface VideoPlayerApiRender extends VideoPlayerApiBase, VideoPlayerApiListener {

    default String screenshot() {
        try {
            VideoRenderApi render = getVideoRender();
            return render.screenshot(getPlayerLayout().getUrl(), getPlayerLayout().getPosition());
        } catch (Exception e) {
            return null;
        }
    }

    default void setVideoScaleType(@PlayerType.ScaleType.Value int scaleType) {
        try {
            VideoRenderApi render = getVideoRender();
            if (null == render) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoPlayerApiRender", "setVideoScaleType -> render error: null");
                }
                return;
            }
            render.setVideoScaleType(scaleType);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiRender -> setVideoScaleType -> " + e.getMessage());
            }
        }
    }

    @PlayerType.ScaleType.Value
    default int getVideoScale() {
        try {
            VideoRenderApi render = getVideoRender();
            if (null == render) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoPlayerApiRender", "getVideoScale -> render error: null");
                }
                return PlayerType.ScaleType.DEFAULT;
            }
            return render.getVideoScale();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiRender", "getVideoScaleType -> " + e.getMessage());
            }
            return PlayerType.ScaleType.DEFAULT;
        }
    }

    default void setVideoFormat(int kernel, int rotation, int scaleType, int width, int height, int bitrate) {
        try {
            VideoRenderApi render = getVideoRender();
            render.setVideoFormat(kernel, rotation, scaleType, width, height, bitrate);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiRender -> setVideoFormat -> " + e.getMessage());
            }
        }
    }

    default void setVideoSize(int width, int height) {
        try {
            VideoRenderApi render = getVideoRender();
            render.setVideoSize(width, height);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiRender -> setVideoSize -> " + e.getMessage());
            }
        }
    }

    default void setVideoRotation(@PlayerType.RotationType.Value int rotation) {
        try {
            if (rotation == -1)
                return;
            VideoRenderApi render = getVideoRender();
            render.setVideoRotation(rotation);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiRender -> setVideoRotation -> " + e.getMessage());
            }
        }
    }

    /**
     * 设置镜像旋转，暂不支持SurfaceView
     *
     * @param enable
     */
    default void setMirrorRotation(boolean enable) {
        try {
            VideoRenderApi render = getVideoRender();
            render.setScaleX(enable ? -1 : 1);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiRender -> setMirrorRotation -> " + e.getMessage());
            }
        }
    }

    default VideoRenderApi searchVideoRender() {
        try {
            ViewGroup group = getBaseVideoViewGroup();
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                View view = group.getChildAt(i);
                if (null == view)
                    continue;
                if (!(view instanceof VideoRenderApi))
                    continue;
                return (VideoRenderApi) view;
            }
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiRender", "searchVideoRender -> not find");
            }
            return null;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiRender -> searchVideoRender -> " + e.getMessage());
            }
            return null;
        }
    }

    VideoRenderApi getVideoRender();

    void setVideoRender(VideoRenderApi render);

    default void releaseRender() {
        try {
            ViewGroup renderGroup = getBaseVideoViewGroup();
            if (null == renderGroup) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoPlayerApiRender", "releaseRender -> warning: null renderGroup");
                }
                return;
            }
            int childCount = renderGroup.getChildCount();
            if (childCount == 0) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoPlayerApiRender", "releaseRender -> warning: childCount == 0");
                }
                return;
            }
            for (int i = 0; i < childCount; i++) {
                View childAt = renderGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                if (!(childAt instanceof VideoRenderApi))
                    continue;
                ((VideoRenderApi) childAt).release();
            }
            renderGroup.removeAllViews();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiRender -> releaseRender -> " + e.getMessage());
            }
        }
    }

    default void initRender(StartArgs args) {
        try {
            ViewGroup renderGroup = getBaseVideoViewGroup();
            if (null == renderGroup) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoPlayerApiRender", "initRender -> renderGroup error: null");
                }
                return;
            }
            int childCount = renderGroup.getChildCount();
            if (childCount > 0) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoPlayerApiRender", "initRender -> error: renderGroup childCount > 0");
                }
                return;
            }
            Context context = getBaseContext();
            int renderType = args.getRenderType();
            VideoRenderApi videoRender = VideoRenderFactoryManager.createRender(context, renderType);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            videoRender.setLayoutParams(layoutParams);
            renderGroup.addView((View) videoRender, 0);
            setVideoRender(videoRender);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiRender -> initRender -> " + e.getMessage());
            }
        }
    }

    default void attachRenderKernel() {
        try {
            VideoRenderApi videoRender = getVideoRender();
            if (null == videoRender) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoPlayerApiRender", "attachRenderKernel -> error: null == videoRender");
                }
                return;
            }
            VideoKernelApi videoKernel = getVideoKernel();
            if (null == videoKernel) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoPlayerApiRender", "attachRenderKernel -> error: null == videoKernel");
                }
                return;
            }
            videoRender.setVideoKernel(videoKernel);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiRender -> attachRenderKernel -> " + e.getMessage());
            }
        }
    }

    default void initRenderView() {
        try {
            StartArgs startArgs = getStartArgs();
            if (null == startArgs) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoPlayerApiRender", "initRenderView -> error: startArgs null");
                }
                return;
            }

            @PlayerType.DecoderType.Value
            int decoderType = startArgs.getDecoderType();
            @PlayerType.KernelType.Value
            int kernelType = startArgs.getKernelType();
            @PlayerType.RenderType.Value
            int renderType = startArgs.getRenderType();

            if (decoderType == PlayerType.DecoderType.ONLY_CODEC && kernelType == PlayerType.KernelType.IJK && renderType == PlayerType.RenderType.SURFACE_VIEW) {
                releaseRender();
                StartArgs newArgs = startArgs.newBuilderCopy().setRenderType(PlayerType.RenderType.SURFACE_VIEW).build();
                initRender(newArgs);
                attachRenderKernel();
            } else if (decoderType == PlayerType.DecoderType.ONLY_CODEC && kernelType == PlayerType.KernelType.IJK) {
                VideoRenderApi videoRender = getVideoRender();
                videoRender.reset();
            } else if (kernelType == PlayerType.KernelType.IJK) {
                VideoRenderApi videoRender = getVideoRender();
                videoRender.reset();
            } else {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoPlayerApiRender", "initRenderView -> warning: kernel not ijk");
                }
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoPlayerApiRender -> resetRenderView -> " + e.getMessage());
            }
        }
    }

    void checkVideoVisibility();
}
