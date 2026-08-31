package lib.kalu.mediaplayer.core.render.texture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.core.render.VideoRenderApi;
import lib.kalu.mediaplayer.util.LogUtil;

/**
 * desc: 重写TextureView，适配视频的宽高和旋转
 */
public class VideoTextureView extends TextureView implements VideoRenderApi {

    private static final String TAG = "VideoTextureView";

    @Nullable
    private VideoKernelApi mKernel;
    private Surface mSurface;
    private SurfaceTexture mSurfaceTexture;

    private int mVideoWidth = -1;
    private int mVideoHeight = -1;
    private int mVideoBitrate = -1;
    private int mVideoRotation = PlayerType.RotationType.DEFAULT;
    private int mVideoScaleType = PlayerType.ScaleType.DEFAULT;

    public VideoTextureView(Context context) {
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
        registListener();
    }

    @Override
    public void registListener() {
        try {
            setSurfaceTextureListener(mListener);
        } catch (Exception e) {
            LogUtil.log(TAG, "registListener -> " + e.getMessage());
        }
    }

    @Override
    public void unRegistListener() {
        try {
            setSurfaceTextureListener(null);
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
                if (mSurfaceTexture != null) {
                    if (mSurface != null) {
                        mSurface.release();
                    }
                    mSurface = new Surface(mSurfaceTexture);
                    mKernel.setSurface(mSurface, 0, 0);
                }
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
            LogUtil.log(TAG, "release -> " + e.getMessage());
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
        Bitmap bitmap = getBitmap();
        return saveBitmap(getContext(), bitmap);
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

    private final SurfaceTextureListener mListener = new SurfaceTextureListener() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int width, int height) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onSurfaceTextureAvailable -> " + VideoTextureView.this);
            }
            mSurfaceTexture = surfaceTexture;
            setSurface(false);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onSurfaceTextureDestroyed -> " + VideoTextureView.this);
            }
            if (mKernel != null) {
                mKernel.setSurface(null, 0, 0);
            }
            if (mSurface != null) {
                mSurface.release();
                mSurface = null;
            }
            mSurfaceTexture = null;
            return true;
        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onSurfaceTextureSizeChanged -> width = " + width + ", height = " + height);
            }
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
        }
    };
}
