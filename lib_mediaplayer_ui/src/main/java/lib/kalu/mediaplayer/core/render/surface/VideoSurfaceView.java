package lib.kalu.mediaplayer.core.render.surface;

import android.content.Context;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.core.render.VideoRenderApi;
import lib.kalu.mediaplayer.util.LogUtil;

/**
 * SurfaceView 视频渲染
 * <p>
 * 特点：
 * 1. 视频使用独立 Surface，不经过 TextureView / HWUI 纹理合成。
 * 2. 适合 Android TV、低端盒子、老 Mali / Allwinner / Rockchip 设备。
 * 3. Surface 生命周期完全交给 SurfaceHolder.Callback 管理。
 * <p>
 * 注意：
 * 不建议同时使用：
 * setZOrderOnTop(true)
 * setZOrderMediaOverlay(true)
 * <p>
 * 对部分 Android 6/7 老设备可能增加 Surface / HWC / BufferQueue 兼容风险。
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

    private boolean mCallbackRegistered = false;
    private boolean mSurfaceAttached = false;

    public VideoSurfaceView(Context context) {
        super(context);
        init();
    }

    @Override
    public void init() {
        VideoRenderApi.super.init();

        setFocusable(false);
        setFocusableInTouchMode(false);

        // SurfaceView 自己不需要执行普通 View 绘制
        setWillNotDraw(true);

        /*
         * 不要默认设置：
         *
         * setZOrderOnTop(true);
         * setZOrderMediaOverlay(true);
         *
         * 普通视频播放使用 SurfaceView 默认层级即可。
         *
         * 对 Allwinner / Mali / Android 7 这类老平台，
         * 尽量减少额外的 Surface Z-Order 操作。
         */

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
    // Kernel
    // -------------------------------------------------------------------------

    @Override
    public void setVideoKernel(@Nullable VideoKernelApi kernel) {

        // kernel 发生切换时，先解除旧 kernel 的 Surface
        if (mKernel != null && mKernel != kernel) {
            detachSurface();
        }

        mKernel = kernel;

        /*
         * kernel 设置时 Surface 可能已经创建完成。
         *
         * 例如：
         * SurfaceView 已 attach
         *      ↓
         * surfaceCreated()
         *      ↓
         * 此时 kernel 还没有赋值
         *      ↓
         * 后面才 setVideoKernel()
         *
         * 所以这里需要尝试补一次 attach。
         */
        if (kernel != null) {
            attachSurfaceIfValid();
        }
    }

    @Nullable
    @Override
    public VideoKernelApi getVideoKernel() {
        return mKernel;
    }

    // -------------------------------------------------------------------------
    // Surface callback
    // -------------------------------------------------------------------------

    @Override
    public void registListener() {

        if (mCallbackRegistered) {
            return;
        }

        try {

            getHolder().addCallback(mCallback);

            mCallbackRegistered = true;

            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "registListener -> succ");
            }

        } catch (Exception e) {

            LogUtil.log( TAG, "registListener -> " + e.getMessage() );
        }
    }

    @Override
    public void unRegistListener() {

        if (!mCallbackRegistered) {
            return;
        }

        try {

            getHolder().removeCallback(mCallback);

            mCallbackRegistered = false;

            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "unRegistListener -> succ");
            }

        } catch (Exception e) {

            LogUtil.log( TAG, "unRegistListener -> " + e.getMessage() );
        }
    }

    /**
     * 检查当前 Surface 是否有效，并绑定到播放器。
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

            SurfaceHolder holder = getHolder();

            if (holder == null) {

                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "attachSurface -> holder is null");
                }

                return;
            }

            Surface surface = holder.getSurface();

            if (surface == null) {

                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "attachSurface -> surface is null");
                }

                return;
            }

            if (!surface.isValid()) {

                if (LogUtil.DEBUG) {
                    LogUtil.log( TAG, "attachSurface -> surface invalid" );
                }

                return;
            }

            /*
             * 已经 attach 时，没有必要反复执行 setSurface。
             *
             * 某些老 MediaCodec / OMX 实现频繁重复绑定同一个
             * Surface 反而容易出现兼容性问题。
             */
            if (mSurfaceAttached) {

                if (LogUtil.DEBUG) {
                    LogUtil.log( TAG, "attachSurface -> already attached" );
                }

                return;
            }

            kernel.setSurface( surface, 0, 0 );

            mSurfaceAttached = true;

            if (LogUtil.DEBUG) {

                LogUtil.log(
                        TAG,
                        "attachSurface -> succ"
                                + ", surface = " + surface
                                + ", valid = " + surface.isValid()
                                + ", frame = " + holder.getSurfaceFrame()
                );
            }

        } catch (Exception e) {

            LogUtil.log( TAG, "attachSurface -> " + e.getMessage() );
        }
    }

    /**
     * 从播放器解除 Surface。
     */
    private void detachSurface() {

        VideoKernelApi kernel = mKernel;

        if (kernel == null) {

            mSurfaceAttached = false;

            return;
        }

        try {

            /*
             * 即使 mSurfaceAttached == false，
             * release 时调用一次 null 也没有问题。
             *
             * 保证 decoder 不再持有旧 Surface。
             */
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

    private final SurfaceHolder.Callback mCallback =
            new SurfaceHolder.Callback() {

                @Override
                public void surfaceCreated( @NonNull SurfaceHolder holder ) {

                    if (LogUtil.DEBUG) {

                        Surface surface = holder.getSurface();

                        LogUtil.log( TAG, "surfaceCreated" + ", surface = " + surface + ", valid = " + (surface != null && surface.isValid()) );
                    }

                    /*
                     * Surface 真正创建完成后再交给 MediaCodec。
                     */
                    mSurfaceAttached = false;

                    attachSurfaceIfValid();
                }

                @Override
                public void surfaceChanged( @NonNull SurfaceHolder holder, int format, int width, int height ) {

                    if (LogUtil.DEBUG) {

                        LogUtil.log( TAG, "surfaceChanged" + ", format = " + format + ", size = " + width + "x" + height );
                    }

                    /*
                     * 这里不要重新调用 setSurface。
                     *
                     * surfaceChanged 很可能多次触发。
                     * 对 Allwinner / Rockchip 老 OMX，
                     * 尽量避免频繁重新绑定 Surface。
                     */
                }

                @Override
                public void surfaceDestroyed( @NonNull SurfaceHolder holder ) {

                    if (LogUtil.DEBUG) {

                        LogUtil.log( TAG, "surfaceDestroyed" + ", surface = " + holder.getSurface() );
                    }

                    /*
                     * Surface 即将销毁：
                     *
                     * 必须先通知 MediaCodec 不再使用这个 Surface。
                     */
                    detachSurface();
                }
            };

    // -------------------------------------------------------------------------
    // VideoRenderApi
    // -------------------------------------------------------------------------

    @Override
    public void setSurface(boolean release) {

        if (release) {
            detachSurface();
        } else {

            /*
             * 外部即使调用 setSurface(false)，
             * 也必须验证 Surface 是否有效。
             */
            attachSurfaceIfValid();
        }
    }

    @Override
    public void reset() {

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "reset");
        }

        /*
         * 不再无条件：
         *
         * getHolder().getSurface()
         *          ↓
         * kernel.setSurface(...)
         *
         * 只在 Surface 有效时重新绑定。
         */
        mSurfaceAttached = false;

        attachSurfaceIfValid();
    }

    @Override
    public void release() {

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "release");
        }

        /*
         * 顺序非常重要：
         *
         * 1. MediaCodec 先停止使用 Surface
         * 2. 再移除 Surface callback
         * 3. 最后清除 kernel 引用
         */

        detachSurface();

        unRegistListener();

        mKernel = null;
    }

    // -------------------------------------------------------------------------
    // Surface size
    // -------------------------------------------------------------------------

    @Override
    public void setFixedSize(int width, int height) {

        /*
         * 不允许非法尺寸传给 SurfaceHolder。
         */
        if (width <= 0 || height <= 0) {

            if (LogUtil.DEBUG) {
                LogUtil.log( TAG, "setFixedSize -> invalid size = " + width + "x" + height );
            }

            return;
        }

        try {

            if (LogUtil.DEBUG) {
                LogUtil.log( TAG, "setFixedSize -> " + width + "x" + height );
            }

            getHolder().setFixedSize( width, height );

        } catch (Exception e) {

            LogUtil.log( TAG, "setFixedSize -> " + e.getMessage() );
        }
    }

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
    // Others
    // -------------------------------------------------------------------------

    @Override
    public String screenshot( String url, long position ) {

        /*
         * SurfaceView 截图：
         * API 24+ 可以使用 PixelCopy。
         */

        return null;
    }

    @Override
    public void setRotation(float rotation) {

        /*
         * 不建议对老设备的 SurfaceView
         * 直接使用 View rotation。
         *
         * 视频旋转优先交给 decoder / matrix / layout 处理。
         */
    }
}
