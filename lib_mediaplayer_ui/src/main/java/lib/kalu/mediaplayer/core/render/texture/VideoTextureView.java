package lib.kalu.mediaplayer.core.render.texture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.core.render.VideoRenderApi;
import lib.kalu.mediaplayer.util.LogUtil;


/**
 * <pre>
 *     desc  : 重写TextureView，适配视频的宽高和旋转
 *     revise: 1.继承View，具有view的特性，比如移动，旋转，缩放，动画等变化。支持截图
 *             8.必须在硬件加速的窗口中使用，占用内存比SurfaceView高，在5.0以前在主线程渲染，5.0以后有单独的渲染线程。
 * </pre>
 */
public class VideoTextureView extends TextureView implements VideoRenderApi {

    @Nullable
    private VideoKernelApi mKernel;
    private Surface mSurface;
    private SurfaceTexture mSurfaceTexture;

    public VideoTextureView(Context context) {
        super(context);
        init();
    }

    @Override
    public void init() {
        VideoRenderApi.super.init();
        //        setDrawingCacheEnabled(true);
        setFocusable(false);
        setFocusableInTouchMode(false);
        registListener();
    }

    @Override
    public void registListener() {
        try {
            setSurfaceTextureListener(mListener);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoTextureView => registListener => Exception " + e.getMessage());
            }
        }
    }

    @Override
    public void unRegistListener() {
        try {
            setSurfaceTextureListener(null);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoTextureView => unRegistListener => Exception " + e.getMessage());
            }
        }
    }

    @Override
    public void setSurface(boolean release) {
        if (release) {
            if (mSurface != null) {
                mSurface.release();
                mSurface = null;
            }
            mSurface = new Surface(mSurfaceTexture);
        }
        if (mKernel != null) {
            int w = getWidth();
            int h = getHeight();
            mKernel.setSurface(mSurface, w, h);
        }
    }

    @Override
    public void reset() {
        if (LogUtil.DEBUG) {
            LogUtil.log("VideoTextureView => reset =>");
        }
        setSurface(false);
    }

    /**
     * 释放资源
     */
    @Override
    public void release() {
        unRegistListener();
        try {
            if (mSurfaceTexture != null) {
                mSurfaceTexture.release();
                mSurfaceTexture = null;
            }
            if (mSurface != null) {
                mSurface.release();
                mSurface = null;
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoTextureView => release => Exception " + e.getMessage());
            }
        }
    }

    @Override
    public void setVideoKernel(VideoKernelApi player) {
        this.mKernel = player;
    }

    @Override
    public VideoKernelApi getVideoKernel() {
        return this.mKernel;
    }

    @Override
    public String screenshot(String url, long position) {
        Context context = getContext();
        Bitmap bitmap = getBitmap();
//        //设置缓存
//        setDrawingCacheEnabled(true);
//        buildDrawingCache();
//        //从缓存中获取当前屏幕的图片
//        Context context = getContext();
//        Bitmap bitmap = getDrawingCache();
        return saveBitmap(context, bitmap);
    }

    @Override
    public void setFixedSize(int width, int height) {

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
//        if (rotation != getRotation()) {
//            super.setRotation(rotation);
//            requestLayout();
//        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            int screenWidth = MeasureSpec.getSize(widthMeasureSpec);
            int screenHeight = MeasureSpec.getSize(heightMeasureSpec);
            int[] measureSpec = doMeasureSpec(screenWidth, screenHeight);
            if (null == measureSpec)
                throw new Exception("warning: measureSpec null");
            int width = measureSpec[0];
            int height = measureSpec[1];
            int specW = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            int specH = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            super.onMeasure(specW, specH);
//            getHolder().setFixedSize(measureSpec, measureSpec[1]);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoTextureView => onMeasure => Exception " + e.getMessage());
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private final SurfaceTextureListener mListener = new SurfaceTextureListener() {
        /**
         * SurfaceTexture准备就绪
         * @param surfaceTexture            surface
         * @param width                     WIDTH
         * @param height                    HEIGHT
         */
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoTextureView => onSurfaceTextureAvailable => " + this);
            }
//                VideoRenderTextureView.this.mSurfaceTexture = surfaceTexture;
//                setSurfaceTexture(VideoRenderTextureView.this.mSurfaceTexture);
//                setSurface(true);
            mSurfaceTexture = surfaceTexture;
            setSurface(true);
        }

        /**
         * SurfaceTexture即将被销毁
         * @param surface                   surface
         */
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoTextureView => onSurfaceTextureDestroyed => " + this);
            }
            return false;
        }

        /**
         * SurfaceTexture缓冲大小变化
         * @param surface                   surface
         * @param width                     WIDTH
         * @param height                    HEIGHT
         */
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoTextureView => onSurfaceTextureSizeChanged => " + this);
            }
        }

        /**
         * SurfaceTexture通过updateImage更新
         * @param surface                   surface
         */
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoTextureView => onSurfaceTextureUpdated => " + this);
            }
        }
    };
}