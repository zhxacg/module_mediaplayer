package lib.kalu.mediaplayer.core.render.surface;

import android.content.Context;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.core.render.VideoRenderApi;
import lib.kalu.mediaplayer.util.LogUtil;

/**
 * 优点：独立线程绘制，不影响主线程；双缓冲机制，视频播放更流畅。
 * 缺点：不在 View hierarchy 中，不受 View 属性控制（平移、缩放、旋转受限），不能嵌套。
 */
public class VideoSurfaceView extends SurfaceView implements VideoRenderApi {

    private static final String TAG = "VideoSurfaceView";

    @Nullable
    private VideoKernelApi mKernel;

    private int mVideoWidth = -1;
    private int mVideoHeight = -1;
    private int mVideoBitrate = -1;
    private int mVideoRotation = PlayerType.RotationType.DEFAULT;
    private int mVideoScaleType = PlayerType.ScaleType.DEFAULT;

    public VideoSurfaceView(Context context) {
        super(context);
        init();
    }

    @Override
    public void updateVideoWidth(int videoWidth) {
        this.mVideoWidth = videoWidth;
    }

    @Override
    public int getVideoWidth() {
        return mVideoWidth;
    }

    @Override
    public void updateVideoHeight(int videoHeight) {
        this.mVideoHeight = videoHeight;
    }

    @Override
    public int getVideoHeight() {
        return mVideoHeight;
    }

    @Override
    public void updateVideoBitrate(int videoBitrate) {
        this.mVideoBitrate = videoBitrate;
    }

    @Override
    public int getVideoBitrate() {
        return mVideoBitrate;
    }

    @Override
    public void updateVideoRotation(int videoRotation) {
        this.mVideoRotation = videoRotation;
    }

    @Override
    public int getVideoRotation() {
        return mVideoRotation;
    }

    @Override
    public void updateVideoScaleType(int scaleType) {
        this.mVideoScaleType = scaleType;
    }

    @Override
    public int getVideoScale() {
        return mVideoScaleType;
    }

    @Override
    public void init() {
        VideoRenderApi.super.init();
        setFocusable(false);
        setFocusableInTouchMode(false);
        setWillNotDraw(true);
        setZOrderOnTop(true);
        setZOrderMediaOverlay(true);
        registListener();
    }

    @Override
    public void registListener() {
        try {
            getHolder().addCallback(mCallback);
        } catch (Exception e) {
            LogUtil.log(TAG, "registListener -> " + e.getMessage());
        }
    }

    @Override
    public void unRegistListener() {
        try {
            getHolder().removeCallback(mCallback);
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "unRegistListener -> succ");
            }
        } catch (Exception e) {
            LogUtil.log(TAG, "unRegistListener -> " + e.getMessage());
        }
    }

    @Override
    public void setSurface(boolean release) {
        if (mKernel == null) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "setSurface -> mKernel is null, skip");
            }
            return;
        }
        try {
            if (release) {
                mKernel.setSurface(null, 0, 0);
            } else {
                mKernel.setSurface(getHolder().getSurface(), 0, 0);
            }
        } catch (Exception e) {
            LogUtil.log(TAG, "setSurface -> " + e.getMessage());
        }
    }

    @Override
    public void reset() {
        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "reset");
        }
        setSurface(false);
    }

    @Override
    public void release() {
        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "release");
        }
        unRegistListener();
        setSurface(true);
    }

    @Override
    public void setVideoKernel(VideoKernelApi kernel) {
        this.mKernel = kernel;
    }

    @Override
    public VideoKernelApi getVideoKernel() {
        return this.mKernel;
    }

    @Override
    public String screenshot(String url, long position) {
        // SurfaceView 截图需使用 PixelCopy (API 24+)
        return null;
    }

    @Override
    public boolean hasFocus() {
        return false;
    }

    @Override
    public boolean hasFocusable() {
        return false;
    }

    @Override
    public boolean hasExplicitFocusable() {
        return false;
    }

    @Override
    public boolean hasWindowFocus() {
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return false;
    }

    @Override
    public void setRotation(float rotation) {
        // SurfaceView 不支持 View 级别的旋转
    }

    @Override
    public void setFixedSize(int width, int height) {
        getHolder().setFixedSize(width, height);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            int screenWidth = MeasureSpec.getSize(widthMeasureSpec);
            int screenHeight = MeasureSpec.getSize(heightMeasureSpec);
            int[] measureSpec = doMeasureSpec(screenWidth, screenHeight);
            
            if (measureSpec == null) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                return;
            }
            
            int width = measureSpec[0];
            int height = measureSpec[1];
            setMeasuredDimension(width, height);
        } catch (Exception e) {
            LogUtil.log(TAG, "onMeasure -> " + e.getMessage());
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private final SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "surfaceCreated -> holder = " + holder);
            }
            setSurface(false);
        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "surfaceChanged -> format = " + format + ", size = " + width + "x" + height);
            }
        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "surfaceDestroyed");
            }
            setSurface(true);
        }
    };
}
