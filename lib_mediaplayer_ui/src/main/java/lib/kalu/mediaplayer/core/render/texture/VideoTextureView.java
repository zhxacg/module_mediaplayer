package lib.kalu.mediaplayer.core.render.texture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.core.render.VideoRenderApi;
import lib.kalu.mediaplayer.util.LogUtil;

/**
 * TextureView 视频渲染
 *
 * 注意：
 *
 * 1. SurfaceTexture 默认由 TextureView 管理生命周期。
 * 2. onSurfaceTextureDestroyed() 返回 true，
 *    表示 SurfaceTexture 由 TextureView / Framework 负责释放。
 * 3. 不要手动 release TextureView 提供的 SurfaceTexture。
 * 4. Surface 是我们自己 new 出来的，所以 Surface 需要自己 release。
 * 5. 同一个 SurfaceTexture 不要反复创建 Surface。
 */
public class VideoTextureView extends TextureView implements VideoRenderApi {

    private static final String TAG = "VideoTextureView";

    @Nullable
    private VideoKernelApi mKernel;

    /**
     * 我们自己创建，生命周期由本类管理。
     */
    @Nullable
    private Surface mSurface;

    /**
     * TextureView 提供。
     *
     * 注意：
     * 不主动调用 release()。
     */
    @Nullable
    private SurfaceTexture mSurfaceTexture;

    private boolean mListenerRegistered = false;
    private boolean mSurfaceAttached = false;

    private int mVideoWidth = -1;
    private int mVideoHeight = -1;
    private int mVideoBitrate = -1;

    private int mVideoRotation = PlayerType.RotationType.DEFAULT;
    private int mVideoScaleType = PlayerType.ScaleType.DEFAULT;

    public VideoTextureView(Context context) {
        super(context);
        init();
    }

    // -------------------------------------------------------------------------
    // init
    // -------------------------------------------------------------------------

    @Override
    public void init() {

        VideoRenderApi.super.init();

        setFocusable(false);
        setFocusableInTouchMode(false);

        registListener();
    }

    // -------------------------------------------------------------------------
    // Video info
    // -------------------------------------------------------------------------

    @Override
    public void updateVideoWidth(int videoWidth) {
        mVideoWidth = videoWidth;
    }

    @Override
    public int getVideoWidth() {
        return mVideoWidth;
    }

    @Override
    public void updateVideoHeight(int videoHeight) {
        mVideoHeight = videoHeight;
    }

    @Override
    public int getVideoHeight() {
        return mVideoHeight;
    }

    @Override
    public void updateVideoBitrate(int videoBitrate) {
        mVideoBitrate = videoBitrate;
    }

    @Override
    public int getVideoBitrate() {
        return mVideoBitrate;
    }

    @Override
    public void updateVideoRotation(int videoRotation) {
        mVideoRotation = videoRotation;
    }

    @Override
    public int getVideoRotation() {
        return mVideoRotation;
    }

    @Override
    public void updateVideoScaleType(int scaleType) {
        mVideoScaleType = scaleType;
    }

    @Override
    public int getVideoScale() {
        return mVideoScaleType;
    }

    // -------------------------------------------------------------------------
    // Listener
    // -------------------------------------------------------------------------

    @Override
    public void registListener() {

        if (mListenerRegistered) {
            return;
        }

        try {

            setSurfaceTextureListener(mListener);

            mListenerRegistered = true;

            /*
             * 很重要：
             *
             * 如果 setSurfaceTextureListener() 的时候 TextureView
             * 已经处于 available 状态，不一定再次收到
             * onSurfaceTextureAvailable()。
             *
             * 所以这里主动同步一次。
             */
            if (isAvailable()) {

                SurfaceTexture surfaceTexture = getSurfaceTexture();

                if (surfaceTexture != null) {
                    mSurfaceTexture = surfaceTexture;
                    attachSurfaceIfValid();
                }
            }

            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "registListener -> succ");
            }

        } catch (Exception e) {

            LogUtil.log( TAG, "registListener -> " + e.getMessage() );
        }
    }

    @Override
    public void unRegistListener() {

        if (!mListenerRegistered) {
            return;
        }

        try {

            setSurfaceTextureListener(null);

            mListenerRegistered = false;

            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "unRegistListener -> succ");
            }

        } catch (Exception e) {

            LogUtil.log( TAG, "unRegistListener -> " + e.getMessage() );
        }
    }

    // -------------------------------------------------------------------------
    // Kernel
    // -------------------------------------------------------------------------

    @Override
    public void setVideoKernel(@Nullable VideoKernelApi kernel) {

        /*
         * Kernel 被替换：
         * 先从旧 Kernel 上解绑 Surface。
         */
        if (mKernel != null && mKernel != kernel) {
            detachSurface();
        }

        mKernel = kernel;

        /*
         * TextureView 可能已经 Available，
         * 但 Kernel 是后设置进来的。
         */
        if (kernel != null) {

            syncSurfaceTexture();

            attachSurfaceIfValid();
        }
    }

    @Nullable
    @Override
    public VideoKernelApi getVideoKernel() {
        return mKernel;
    }

    // -------------------------------------------------------------------------
    // Surface
    // -------------------------------------------------------------------------

    /**
     * 从 TextureView 同步当前 SurfaceTexture。
     */
    private void syncSurfaceTexture() {

        if (!isAvailable()) {
            return;
        }

        try {

            SurfaceTexture current = getSurfaceTexture();

            if (current == null) {
                return;
            }

            /*
             * SurfaceTexture 发生变化，
             * 旧 Surface 已经不能继续复用。
             */
            if (mSurfaceTexture != current) {

                detachSurface();

                releaseSurface();

                mSurfaceTexture = current;

                mSurfaceAttached = false;
            }

        } catch (Exception e) {

            LogUtil.log( TAG, "syncSurfaceTexture -> " + e.getMessage() );
        }
    }

    /**
     * Surface 有效时绑定播放器。
     */
    private void attachSurfaceIfValid() {

        VideoKernelApi kernel = mKernel;

        if (kernel == null) {

            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "attachSurface -> kernel is null");
            }

            return;
        }

        try {

            /*
             * 如果当前成员没有 SurfaceTexture，
             * 尝试直接从 TextureView 获取。
             */
            if (mSurfaceTexture == null) {

                if (!isAvailable()) {

                    if (LogUtil.DEBUG) {
                        LogUtil.log( TAG, "attachSurface -> TextureView unavailable" );
                    }

                    return;
                }

                mSurfaceTexture = getSurfaceTexture();
            }

            SurfaceTexture surfaceTexture = mSurfaceTexture;

            if (surfaceTexture == null) {

                if (LogUtil.DEBUG) {
                    LogUtil.log( TAG, "attachSurface -> SurfaceTexture is null" );
                }

                return;
            }

            /*
             * 同一个 SurfaceTexture：
             *
             * 只创建一次 Surface。
             *
             * 原代码这里每调用一次都会：
             *
             * mSurface.release()
             * new Surface(mSurfaceTexture)
             *
             * 对老设备非常不友好。
             */
            if (mSurface == null) {

                mSurface = new Surface(surfaceTexture);

                if (LogUtil.DEBUG) {
                    LogUtil.log( TAG, "attachSurface -> create new Surface = " + mSurface );
                }
            }

            if (!mSurface.isValid()) {

                LogUtil.log( TAG, "attachSurface -> Surface invalid" );

                releaseSurface();

                return;
            }

            /*
             * 已经绑定就不要反复 setSurface。
             */
            if (mSurfaceAttached) {

                if (LogUtil.DEBUG) {
                    LogUtil.log( TAG, "attachSurface -> already attached" );
                }

                return;
            }

            kernel.setSurface( mSurface, 0, 0 );

            mSurfaceAttached = true;

            if (LogUtil.DEBUG) {

                LogUtil.log( TAG, "attachSurface -> succ" + ", surface = " + mSurface + ", valid = " + mSurface.isValid() );
            }

        } catch (Exception e) {

            LogUtil.log( TAG, "attachSurface -> " + e.getMessage() );
        }
    }

    /**
     * 通知播放器停止使用当前 Surface。
     */
    private void detachSurface() {

        VideoKernelApi kernel = mKernel;

        if (kernel == null) {
            mSurfaceAttached = false;
            return;
        }

        try {

            kernel.setSurface( null, 0, 0 );

            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "detachSurface -> succ");
            }

        } catch (Exception e) {

            LogUtil.log( TAG, "detachSurface -> " + e.getMessage() );

        } finally {

            mSurfaceAttached = false;
        }
    }

    /**
     * 释放我们自己 new Surface(...) 创建的 Surface。
     *
     * 注意：
     * 这里只释放 Surface，
     * 不释放 SurfaceTexture。
     */
    private void releaseSurface() {

        Surface surface = mSurface;

        mSurface = null;
        mSurfaceAttached = false;

        if (surface == null) {
            return;
        }

        try {

            surface.release();

            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "releaseSurface -> succ");
            }

        } catch (Exception e) {

            LogUtil.log( TAG, "releaseSurface -> " + e.getMessage() );
        }
    }

    @Override
    public void setSurface(boolean release) {

        if (release) {

            detachSurface();

        } else {

            syncSurfaceTexture();

            attachSurfaceIfValid();
        }
    }

    // -------------------------------------------------------------------------
    // reset
    // -------------------------------------------------------------------------

    @Override
    public void reset() {

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "reset");
        }

        /*
         * 不要再 release Surface + new Surface。
         *
         * SurfaceTexture 没变化时直接复用。
         */
        syncSurfaceTexture();

        if (mSurfaceAttached) {

            if (LogUtil.DEBUG) {
                LogUtil.log( TAG, "reset -> already attached" );
            }

            return;
        }

        attachSurfaceIfValid();
    }

    // -------------------------------------------------------------------------
    // release
    // -------------------------------------------------------------------------

    @Override
    public void release() {

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "release");
        }

        /*
         * 顺序：
         *
         * 1. 让 MediaCodec / Player 停止使用 Surface
         * 2. release 我们创建的 Surface
         * 3. 移除 TextureView listener
         * 4. 清引用
         *
         * 不主动 release SurfaceTexture。
         */

        detachSurface();

        releaseSurface();

        unRegistListener();

        /*
         * SurfaceTexture 属于 TextureView。
         *
         * 这里只清引用。
         */
        mSurfaceTexture = null;

        mKernel = null;
    }

    // -------------------------------------------------------------------------
    // TextureView callback
    // -------------------------------------------------------------------------

    private final SurfaceTextureListener mListener =
            new SurfaceTextureListener() {

                @Override
                public void onSurfaceTextureAvailable( @NonNull SurfaceTexture surfaceTexture, int width, int height ) {

                    if (LogUtil.DEBUG) {

                        LogUtil.log( TAG, "onSurfaceTextureAvailable" + ", texture = " + surfaceTexture + ", size = " + width + "x" + height );
                    }

                    /*
                     * 理论上 available 时旧 Surface 应该已经释放。
                     *
                     * 这里再做一次保护。
                     */
                    if (mSurfaceTexture != surfaceTexture) {

                        detachSurface();

                        releaseSurface();

                        mSurfaceTexture = surfaceTexture;
                    }

                    mSurfaceAttached = false;

                    attachSurfaceIfValid();
                }

                @Override
                public void onSurfaceTextureSizeChanged( @NonNull SurfaceTexture surfaceTexture, int width, int height ) {

                    if (LogUtil.DEBUG) {

                        LogUtil.log( TAG, "onSurfaceTextureSizeChanged" + ", width = " + width + ", height = " + height );
                    }

                    /*
                     * 不重新创建 Surface。
                     * 不重新 setSurface。
                     *
                     * TextureView 尺寸改变并不意味着
                     * SurfaceTexture 实例发生变化。
                     */
                }

                @Override
                public boolean onSurfaceTextureDestroyed( @NonNull SurfaceTexture surfaceTexture ) {

                    if (LogUtil.DEBUG) {

                        LogUtil.log( TAG, "onSurfaceTextureDestroyed" + ", texture = " + surfaceTexture );
                    }

                    /*
                     * 非常重要：
                     *
                     * 1. MediaCodec 先解绑。
                     * 2. 再 release Surface。
                     */
                    detachSurface();

                    releaseSurface();

                    if (mSurfaceTexture == surfaceTexture) {
                        mSurfaceTexture = null;
                    }

                    /*
                     * true：
                     *
                     * 告诉 TextureView：
                     * SurfaceTexture 可以由 Framework 释放。
                     *
                     * 因此我们绝对不能再执行：
                     *
                     * surfaceTexture.release();
                     */
                    return true;
                }

                @Override
                public void onSurfaceTextureUpdated( @NonNull SurfaceTexture surfaceTexture ) {
                }
            };

    // -------------------------------------------------------------------------
    // Measure
    // -------------------------------------------------------------------------

    @Override
    protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec ) {

        try {

            int screenWidth =
                    MeasureSpec.getSize(widthMeasureSpec);

            int screenHeight =
                    MeasureSpec.getSize(heightMeasureSpec);

            int[] measureSpec =
                    doMeasureSpec( screenWidth, screenHeight );

            if (measureSpec == null || measureSpec.length < 2) {

                super.onMeasure( widthMeasureSpec, heightMeasureSpec );

                return;
            }

            int width = measureSpec[0];
            int height = measureSpec[1];

            if (width <= 0 || height <= 0) {

                super.onMeasure( widthMeasureSpec, heightMeasureSpec );

                return;
            }

            setMeasuredDimension( width, height );

        } catch (Exception e) {

            LogUtil.log( TAG, "onMeasure -> " + e.getMessage() );

            super.onMeasure( widthMeasureSpec, heightMeasureSpec );
        }
    }

    // -------------------------------------------------------------------------
    // Screenshot
    // -------------------------------------------------------------------------

    @Override
    public String screenshot( String url, long position ) {

        Bitmap bitmap = null;

        try {

            if (isAvailable()) {
                bitmap = getBitmap();
            }

            if (bitmap == null) {
                return null;
            }

            return saveBitmap( getContext(), bitmap );

        } catch (Exception e) {

            LogUtil.log( TAG, "screenshot -> " + e.getMessage() );

            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }

            return null;
        }
    }

    // -------------------------------------------------------------------------
    // Others
    // -------------------------------------------------------------------------

    @Override
    public void setFixedSize(int width, int height) {
        /*
         * TextureView 没有 SurfaceHolder.setFixedSize()。
         */
    }

    @Override
    public void setRotation(float rotation) {
        /*
         * 如果以后需要 TextureView 旋转，
         * 建议通过 Matrix 实现。
         */
    }
}