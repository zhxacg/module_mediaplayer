package lib.kalu.mediaplayer.core.render.glsurface;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.core.render.VideoRenderApi;
import lib.kalu.mediaplayer.util.LogUtil;

public class VideoGLSurfaceView extends GLSurfaceView implements VideoRenderApi {

    private static final String TAG = "VideoGLSurfaceView";

    @Nullable
    private GLDrawer mDrawer;

    @Nullable
    private GLRender mRender;

    @Nullable
    private VideoKernelApi mKernel;

    @Nullable
    private Surface mVideoSurface;

    private boolean mSurfaceAttached = false;
    private boolean mListenerRegistered = false;
    private boolean mReleased = false;

    private int mVideoWidth = -1;
    private int mVideoHeight = -1;
    private int mVideoBitrate = -1;
    private int mVideoRotation = PlayerType.RotationType.DEFAULT;
    private int mVideoScaleType = PlayerType.ScaleType.DEFAULT;

    public VideoGLSurfaceView(Context context) {
        super(context);
        init();
    }

    @Override
    public void init() {

        VideoRenderApi.super.init();

        setFocusable(false);
        setFocusableInTouchMode(false);

        setEGLContextClientVersion(2);

        /*
         * 减少 pause/resume 导致 EGL Context 频繁重建。
         */
        setPreserveEGLContextOnPause(true);

        mDrawer = new GLDrawer();

        mRender = new GLRender();
        mRender.addDrawer(mDrawer);

        /*
         * GLSurfaceView生命周期内只能调用一次。
         */
        setRenderer(mRender);

        /*
         * 视频帧到来后通过 requestRender() 主动绘制。
         */
        setRenderMode(RENDERMODE_WHEN_DIRTY);

        mReleased = false;

        registListener();
    }

    // -------------------------------------------------------------------------
    // Listener
    // -------------------------------------------------------------------------

    private final GLDrawer.SurfaceListener mSurfaceListener =
            new GLDrawer.SurfaceListener() {

                @Override
                public void onSurfaceAvailable(@NonNull Surface surface) {

                    if (mReleased) {
                        return;
                    }

                    if (LogUtil.DEBUG) {
                        LogUtil.log(
                                TAG,
                                "onSurfaceAvailable -> surface="
                                        + surface
                                        + ", valid="
                                        + surface.isValid()
                        );
                    }

                    /*
                     * EGL Context 重建时可能产生新 Surface。
                     */
                    if (mVideoSurface != null
                            && mVideoSurface != surface
                            && mSurfaceAttached) {

                        detachSurface();
                    }

                    mVideoSurface = surface;
                    mSurfaceAttached = false;

                    attachSurfaceIfValid();
                }

                @Override
                public void onSurfaceDestroyed() {

                    if (LogUtil.DEBUG) {
                        LogUtil.log(
                                TAG,
                                "onSurfaceDestroyed"
                        );
                    }

                    detachSurface();

                    mVideoSurface = null;
                }

                @Override
                public void onFrameAvailable() {

                    if (mReleased) {
                        return;
                    }

                    try {

                        requestRender();

                    } catch (Exception e) {

                        if (LogUtil.DEBUG) {
                            LogUtil.log(
                                    TAG,
                                    "onFrameAvailable -> "
                                            + e.getMessage()
                            );
                        }
                    }
                }
            };

    @Override
    public void registListener() {

        if (mListenerRegistered) {
            return;
        }

        GLDrawer drawer = mDrawer;

        if (drawer == null) {
            return;
        }

        try {

            drawer.addSurfaceListener(
                    mSurfaceListener
            );

            mListenerRegistered = true;

            if (LogUtil.DEBUG) {
                LogUtil.log(
                        TAG,
                        "registListener -> succ"
                );
            }

        } catch (Exception e) {

            LogUtil.log(
                    TAG,
                    "registListener -> "
                            + e.getMessage()
            );
        }
    }

    @Override
    public void unRegistListener() {

        if (!mListenerRegistered) {
            return;
        }

        try {

            /*
             * 先让 MediaCodec 停止使用 Surface。
             */
            detachSurface();

            GLDrawer drawer = mDrawer;

            if (drawer != null) {

                drawer.removeSurfaceListener(
                        mSurfaceListener
                );
            }

        } catch (Exception e) {

            LogUtil.log(
                    TAG,
                    "unRegistListener -> "
                            + e.getMessage()
            );

        } finally {

            mListenerRegistered = false;
            mVideoSurface = null;
        }
    }

    // -------------------------------------------------------------------------
    // Kernel
    // -------------------------------------------------------------------------

    @Override
    public void setVideoKernel(@Nullable VideoKernelApi kernel) {

        if (mKernel == kernel) {
            return;
        }

        /*
         * 旧 Kernel 先解绑。
         */
        if (mKernel != null) {
            detachSurface();
        }

        mKernel = kernel;

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
    // Surface
    // -------------------------------------------------------------------------

    private void attachSurfaceIfValid() {

        if (mReleased) {
            return;
        }

        if (mSurfaceAttached) {
            return;
        }

        VideoKernelApi kernel = mKernel;
        Surface surface = mVideoSurface;

        if (kernel == null
                || surface == null
                || !surface.isValid()) {

            return;
        }

        try {

            kernel.setSurface(
                    surface,
                    0,
                    0
            );

            mSurfaceAttached = true;

            if (LogUtil.DEBUG) {
                LogUtil.log(
                        TAG,
                        "attachSurface -> succ"
                                + ", surface="
                                + surface
                );
            }

        } catch (Exception e) {

            LogUtil.log(
                    TAG,
                    "attachSurface -> "
                            + e.getMessage()
            );
        }
    }

    private void detachSurface() {

        if (!mSurfaceAttached) {
            return;
        }

        VideoKernelApi kernel = mKernel;

        try {

            if (kernel != null) {

                kernel.setSurface(
                        null,
                        0,
                        0
                );
            }

        } catch (Exception e) {

            LogUtil.log(
                    TAG,
                    "detachSurface -> "
                            + e.getMessage()
            );

        } finally {

            mSurfaceAttached = false;
        }
    }

    @Override
    public void setSurface(boolean release) {

        if (release) {
            detachSurface();
        } else {
            attachSurfaceIfValid();
        }
    }

    @Override
    public void reset() {

        if (LogUtil.DEBUG) {
            LogUtil.log(TAG, "reset");
        }

        /*
         * 不重建 Surface。
         */
        attachSurfaceIfValid();
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    public void resumeRender() {

        if (mReleased) {
            return;
        }

        try {

            onResume();

        } catch (Exception e) {

            LogUtil.log(
                    TAG,
                    "resumeRender -> "
                            + e.getMessage()
            );
        }
    }

    public void pauseRender() {

        if (mReleased) {
            return;
        }

        try {

            onPause();

        } catch (Exception e) {

            LogUtil.log(
                    TAG,
                    "pauseRender -> "
                            + e.getMessage()
            );
        }
    }

    @Override
    public void release() {

        if (mReleased) {
            return;
        }

        mReleased = true;

        if (LogUtil.DEBUG) {
            LogUtil.log(
                    TAG,
                    "release"
            );
        }

        /*
         * 内部会先 detachSurface。
         */
        unRegistListener();

        final GLDrawer drawer = mDrawer;

        if (drawer != null) {

            try {

                /*
                 * GLES资源必须GL线程释放。
                 */
                queueEvent(
                        drawer::release
                );

            } catch (Exception e) {

                LogUtil.log(
                        TAG,
                        "release -> queueEvent -> "
                                + e.getMessage()
                );
            }
        }

        try {
            onPause();
        } catch (Exception e) {

            LogUtil.log(
                    TAG,
                    "release -> onPause -> "
                            + e.getMessage()
            );
        }

        mSurfaceAttached = false;
        mVideoSurface = null;

        mKernel = null;
        mDrawer = null;
        mRender = null;
    }

    // -------------------------------------------------------------------------
    // Video
    // -------------------------------------------------------------------------

    @Override
    public void updateVideoWidth(int videoWidth) {

        mVideoWidth = videoWidth;

        updateDrawerVideoSize();
    }

    @Override
    public int getVideoWidth() {
        return mVideoWidth;
    }

    @Override
    public void updateVideoHeight(int videoHeight) {

        mVideoHeight = videoHeight;

        updateDrawerVideoSize();
    }

    @Override
    public int getVideoHeight() {
        return mVideoHeight;
    }

    private void updateDrawerVideoSize() {

        final GLDrawer drawer = mDrawer;

        if (drawer == null
                || mVideoWidth <= 0
                || mVideoHeight <= 0
                || mReleased) {
            return;
        }

        try {

            queueEvent(
                    () -> drawer.setVideoSize(
                            mVideoWidth,
                            mVideoHeight
                    )
            );

            requestRender();

        } catch (Exception e) {

            LogUtil.log(
                    TAG,
                    "updateDrawerVideoSize -> "
                            + e.getMessage()
            );
        }
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
    // Measure
    // -------------------------------------------------------------------------

    @Override
    protected void onMeasure(
            int widthMeasureSpec,
            int heightMeasureSpec
    ) {

        try {

            int screenWidth =
                    MeasureSpec.getSize(
                            widthMeasureSpec
                    );

            int screenHeight =
                    MeasureSpec.getSize(
                            heightMeasureSpec
                    );

            int[] measureSpec =
                    doMeasureSpec(
                            screenWidth,
                            screenHeight
                    );

            if (measureSpec == null
                    || measureSpec.length < 2
                    || measureSpec[0] <= 0
                    || measureSpec[1] <= 0) {

                super.onMeasure(
                        widthMeasureSpec,
                        heightMeasureSpec
                );

                return;
            }

            setMeasuredDimension(
                    measureSpec[0],
                    measureSpec[1]
            );

        } catch (Exception e) {

            LogUtil.log(
                    TAG,
                    "onMeasure -> "
                            + e.getMessage()
            );

            super.onMeasure(
                    widthMeasureSpec,
                    heightMeasureSpec
            );
        }
    }

    // -------------------------------------------------------------------------
    // Other
    // -------------------------------------------------------------------------

    @Override
    public void setRotation(float rotation) {

        try {

            if (getRotation() == rotation) {
                return;
            }

            super.setRotation(rotation);

            requestLayout();

        } catch (Exception e) {

            LogUtil.log(
                    TAG,
                    "setRotation -> "
                            + e.getMessage()
            );
        }
    }

    @Override
    public String screenshot(
            String url,
            long position
    ) {
        return null;
    }

    @Override
    public void setFixedSize(
            int width,
            int height
    ) {
    }
}