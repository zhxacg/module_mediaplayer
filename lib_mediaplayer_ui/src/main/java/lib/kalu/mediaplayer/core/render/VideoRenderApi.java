package lib.kalu.mediaplayer.core.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.FileOutputStream;

import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;


public interface VideoRenderApi extends VideoRenderApiBase {

    /********/

    int[] mVideoWidth = new int[]{0};
    int[] mVideoHeight = new int[]{0};
    int[] mVideoBitrate = new int[]{0};

    default void setVideoSize(int videoWidth, int videoHeight) {
        try {
            if (mVideoWidth[0] == videoWidth && mVideoHeight[0] == videoWidth)
                throw new Exception("warning: mVideoWidth && mVideoHeight not change");
            this.mVideoWidth[0] = videoWidth;
            this.mVideoHeight[0] = videoHeight;
            ((View) this).requestLayout();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoRenderApi => setVideoSize => Exception " + e.getMessage());
            }
        }
    }

    default int getVideoWidth() {
        return this.mVideoWidth[0];
    }

    default int getVideoHeight() {
        return this.mVideoHeight[0];
    }

    default int getVideoBitrate() {
        return this.mVideoBitrate[0];
    }

    /********/

    int[] mVideoRotation = new int[]{PlayerType.RotationType.DEFAULT};

    default void setVideoRotation(@PlayerType.RotationType.Value int videoRotation) {
        try {
            if (mVideoRotation[0] == videoRotation)
                throw new Exception("warning: mVideoRotation not change");
            this.mVideoRotation[0] = videoRotation;
            ((View) this).requestLayout();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoRenderApi => setVideoRotation => Exception " + e.getMessage());
            }
        }
    }

    @PlayerType.RotationType.Value
    default int getVideoRotation() {
        return mVideoRotation[0];
    }

    /********/

    int[] mVideoScaleType = new int[]{PlayerType.ScaleType.DEFAULT};

    default void setVideoScaleType(@PlayerType.ScaleType.Value int scaleType) {
        try {
            if (mVideoScaleType[0] == scaleType)
                throw new Exception("warning: mVideoScaleType not change");
            this.mVideoScaleType[0] = scaleType;
            ((View) this).requestLayout();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoRenderApi => setVideoScaleType => Exception " + e.getMessage());
            }
        }
    }

    @PlayerType.ScaleType.Value
    default int getVideoScale() {
        return mVideoScaleType[0];
    }

    /********/

    default void setVideoFormat(int kernel, int rotation, int scaleType, int width, int height, int bitrate) {
        try {
            if (mVideoWidth[0] == width && mVideoHeight[0] == height && mVideoRotation[0] == rotation && mVideoScaleType[0] == scaleType && mVideoBitrate[0] == bitrate)
                throw new Exception("warning: not change");
            this.mVideoWidth[0] = width;
            this.mVideoHeight[0] = height;
            this.mVideoBitrate[0] = bitrate;
            this.mVideoRotation[0] = rotation;
            this.mVideoScaleType[0] = scaleType;
            ((View) this).requestLayout();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoRenderApi => setVideoFormat => Exception " + e.getMessage());
            }
        }
    }

    /********/

    default void init() {
        mVideoWidth[0] = -1;
        mVideoHeight[0] = -1;
        mVideoBitrate[0] = -1;
        mVideoRotation[0] = PlayerType.RotationType.DEFAULT;
        mVideoScaleType[0] = PlayerType.ScaleType.DEFAULT;
    }

    void registListener();

    void unRegistListener();

    void setSurface(boolean release);

    void reset();

    void release();

    void setLayoutParams(ViewGroup.LayoutParams params);

    void setScaleX(float v);

    String screenshot(String url, long position);

    void setFixedSize(int width, int height);

    /**
     * 注意：VideoView的宽高一定要定死，否者以下算法不成立
     * 借鉴于网络
     */
    default int[] doMeasureSpec(int screenWidth, int screenHeight) {

        int videoScaleType = getVideoScale();
        int videoRotation = getVideoRotation();
        int videoWidth = getVideoWidth();
        int videoHeight = getVideoHeight();

        // 软解码时处理旋转信息，交换宽高
        if (videoRotation == 90 || videoRotation == 270) {
//            widthMeasureSpec = widthMeasureSpec + heightMeasureSpec;
//            heightMeasureSpec = widthMeasureSpec - heightMeasureSpec;
//            widthMeasureSpec = widthMeasureSpec - heightMeasureSpec;
        }
        if (LogUtil.DEBUG) {
            LogUtil.log("VideoRenderApi => doMeasureSpec => videoWidth = " + videoWidth + ", videoHeight = " + videoHeight + ", screenWidth = " + screenWidth + ", screenHeight = " + screenHeight + ", videoScaleType = " + videoScaleType + ", videoRotation = " + videoRotation);
        }

        // SCREEN_SCALE_ORIGINAL
        if (videoScaleType == PlayerType.ScaleType.REAL) {
            try {
                if (videoWidth <= 0 || videoHeight <= 0)
                    throw new Exception("warning: videoWidth <= 0 || videoHeight <= 0");
                if (screenWidth <= 0 || screenHeight <= 0)
                    throw new Exception("warning: screenWidth <= 0 || screenHeight <= 0");
                float realW = videoWidth * 1F;
                float realH = videoHeight * 1F;
                return new int[]{(int) realW, (int) realH};
//                // 视频宽高 > 屏幕宽高
//                if (videoWidth > screenWidth && videoHeight > screenHeight) {
//                    // 屏幕比例
//                    float v1 = (float) screenWidth / screenHeight;
//                    // 视频比例
//                    float v2 = (float) videoWidth / videoWidth;
//                    // 屏幕比例 > 视频比例
//                    if (v1 > v2) {
//                        float realH = screenHeight * 1F;
//                        float realW = videoWidth * realH / videoHeight;
//                        return new int[]{(int) realW, (int) realH};
//                    }
//                    // 屏幕比例 < 视频比例
//                    else {
//                        float realW = screenWidth * 1F;
//                        float realH = realW * videoHeight / videoWidth;
//                        return new int[]{(int) realW, (int) realH};
//                    }
//                }
//                // 视频宽 > 屏幕宽
//                else if (videoWidth > screenWidth) {
//                    float realW = videoWidth * 1F;
//                    float realH = realW * videoHeight / videoWidth;
//                    return new int[]{(int) realW, (int) realH};
//                }
//                // 视频高 > 屏幕高
//                else if (videoHeight > screenHeight) {
//                    float realH = screenHeight * 1F;
//                    float realW = videoWidth * realH / videoHeight;
//                    return new int[]{(int) realW, (int) realH};
//                }
//                // 视频宽高 < 屏幕宽高
//                else {
//                    float realW = videoWidth * 1F;
//                    float realH = videoHeight * 1F;
//                    return new int[]{(int) realW, (int) realH};
//                }
            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoRenderApi => doMeasureSpec => REAL => Exception " + e.getMessage());
                }
                return null;
            }
        }
        // SCREEN_SCALE_16_9
        else if (videoScaleType == PlayerType.ScaleType._16_9) {
            try {
                if (videoWidth <= 0 || videoHeight <= 0)
                    throw new Exception("warning: videoWidth <= 0 || videoHeight <= 0");
                if (screenWidth <= 0 || screenHeight <= 0)
                    throw new Exception("warning: screenWidth <= 0 || screenHeight <= 0");
                // 屏幕比例
                float v1 = (float) screenWidth / screenHeight;
                // 视频比例
                float v2 = (float) videoWidth / videoHeight;
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoRenderApi => doMeasureSpec => 16_9 => videoWidth = " + videoWidth + ", videoHeight = " + videoHeight + ", screenWidth = " + screenWidth + ", screenHeight = " + screenHeight);
                    LogUtil.log("VideoRenderApi => doMeasureSpec => 16_9 => v1 = " + v1 + ", v2 = " + v2);
                }
                // 屏幕比例 >= 视频比例
                if (v1 >= v2) {
                    float realH = screenHeight * 1F;
                    float realW = realH * 16F / 9F;
                    return new int[]{(int) realW, (int) realH};
                }
                // 屏幕比例 < 视频比例
                else {
                    float realW = screenWidth * 1F;
                    float realH = realW * 9F / 16F;
                    return new int[]{(int) realW, (int) realH};
                }
            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoRenderApi => doMeasureSpec => 16_9 => Exception " + e.getMessage());
                }
                return null;
            }
        }
        // SCREEN_SCALE_16_10
        else if (videoScaleType == PlayerType.ScaleType._16_10) {
            try {
                if (videoWidth <= 0 || videoHeight <= 0)
                    throw new Exception("warning: videoWidth <= 0 || videoHeight <= 0");
                if (screenWidth <= 0 || screenHeight <= 0)
                    throw new Exception("warning: screenWidth <= 0 || screenHeight <= 0");
                // 屏幕比例
                float v1 = (float) screenWidth / screenHeight;
                // 视频比例
                float v2 = (float) videoWidth / videoHeight;
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoRenderApi => doMeasureSpec => 16_10 => videoWidth = " + videoWidth + ", videoHeight = " + videoHeight + ", screenWidth = " + screenWidth + ", screenHeight = " + screenHeight);
                    LogUtil.log("VideoRenderApi => doMeasureSpec => 16_10 => v1 = " + v1 + ", v2 = " + v2);
                }
                // 屏幕比例 >= 视频比例
                if (v1 >= v2) {
                    float realH = screenHeight * 1F;
                    float realW = realH * 16F / 10F;
                    return new int[]{(int) realW, (int) realH};
                }
                // 屏幕比例 < 视频比例
                else {
                    float realW = screenWidth * 1F;
                    float realH = realW * 10F / 16F;
                    return new int[]{(int) realW, (int) realH};
                }
            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoRenderApi => doMeasureSpec => SCREEN_SCALE_16_10 => Exception " + e.getMessage());
                }
                return null;
            }
        }
        // SCREEN_SCALE_5_4
        else if (videoScaleType == PlayerType.ScaleType._5_4) {
            try {
                if (videoWidth <= 0 || videoHeight <= 0)
                    throw new Exception("warning: videoWidth <= 0 || videoHeight <= 0");
                if (screenWidth <= 0 || screenHeight <= 0)
                    throw new Exception("warning: screenWidth <= 0 || screenHeight <= 0");
                // 屏幕比例
                float v1 = (float) screenWidth / screenHeight;
                // 视频比例
                float v2 = (float) videoWidth / videoHeight;
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoRenderApi => doMeasureSpec => 5_4 => videoWidth = " + videoWidth + ", videoHeight = " + videoHeight + ", screenWidth = " + screenWidth + ", screenHeight = " + screenHeight);
                    LogUtil.log("VideoRenderApi => doMeasureSpec => 5_4 => v1 = " + v1 + ", v2 = " + v2);
                }
                // 屏幕比例 >= 视频比例
                if (v1 >= v2) {
                    float realH = screenHeight * 1F;
                    float realW = realH * 5F / 4F;
                    return new int[]{(int) realW, (int) realH};
                }
                // 屏幕比例 < 视频比例
                else {
                    float realW = screenWidth * 1F;
                    float realH = realW * 4F / 5F;
                    return new int[]{(int) realW, (int) realH};
                }
            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoRenderApi => doMeasureSpec => 5_4 => Exception " + e.getMessage());
                }
                return null;
            }
        }
        // SCREEN_SCALE_4_3
        else if (videoScaleType == PlayerType.ScaleType._4_3) {
            try {
                if (videoWidth <= 0 || videoHeight <= 0)
                    throw new Exception("warning: videoWidth <= 0 || videoHeight <= 0");
                if (screenWidth <= 0 || screenHeight <= 0)
                    throw new Exception("warning: screenWidth <= 0 || screenHeight <= 0");
                // 屏幕比例
                float v1 = (float) screenWidth / screenHeight;
                // 视频比例
                float v2 = (float) videoWidth / videoHeight;
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoRenderApi => doMeasureSpec => 4_3 => videoWidth = " + videoWidth + ", videoHeight = " + videoHeight + ", screenWidth = " + screenWidth + ", screenHeight = " + screenHeight);
                    LogUtil.log("VideoRenderApi => doMeasureSpec => 4_3 => v1 = " + v1 + ", v2 = " + v2);
                }
                // 屏幕比例 >= 视频比例
                if (v1 >= v2) {
                    float realH = screenHeight * 1F;
                    float realW = realH * 4F / 3F;
                    return new int[]{(int) realW, (int) realH};
                }
                // 屏幕比例 < 视频比例
                else {
                    float realW = screenWidth * 1F;
                    float realH = realW * 3F / 4F;
                    return new int[]{(int) realW, (int) realH};
                }
            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoRenderApi => doMeasureSpec => 4_3 => Exception " + e.getMessage());
                }
                return null;
            }
        }
        // SCREEN_SCALE_1_1
        else if (videoScaleType == PlayerType.ScaleType._1_1) {
            try {
                if (videoWidth <= 0 || videoHeight <= 0)
                    throw new Exception("warning: videoWidth <= 0 || videoHeight <= 0");
                if (screenWidth <= 0 || screenHeight <= 0)
                    throw new Exception("warning: screenWidth <= 0 || screenHeight <= 0");
                // 屏幕宽 >= 屏幕高
                if (screenWidth >= screenHeight) {
                    float realH = screenHeight * 1F;
                    float realW = realH;
                    return new int[]{(int) realW, (int) realH};
                }
                // 屏幕宽 < 屏幕高
                else {
                    float realW = screenWidth * 1F;
                    float realH = realW;
                    return new int[]{(int) realW, (int) realH};
                }
            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoRenderApi => doMeasureSpec => 1_1 => Exception " + e.getMessage());
                }
                return null;
            }
        }
        // SCREEN_SCALE_FULL
        else if (videoScaleType == PlayerType.ScaleType.FULL) {
            try {
                if (videoWidth <= 0 || videoHeight <= 0)
                    throw new Exception("warning: videoWidth <= 0 || videoHeight <= 0");
                if (screenWidth <= 0 || screenHeight <= 0)
                    throw new Exception("warning: screenWidth <= 0 || screenHeight <= 0");
                float realW = screenWidth * 1F;
                float realH = screenHeight * 1F;
                return new int[]{(int) realW, (int) realH};
            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoRenderApi => doMeasureSpec => FULL => Exception " + e.getMessage());
                }
                return null;
            }
        }
        // SCREEN_SCALE_AUTO
        else {
            try {
                if (videoWidth <= 0 || videoHeight <= 0)
                    throw new Exception("warning: videoWidth <= 0 || videoHeight <= 0");
                if (screenWidth <= 0 || screenHeight <= 0)
                    throw new Exception("warning: screenWidth <= 0 || screenHeight <= 0");
                // 屏幕比例
                float v1 = (float) screenWidth / screenHeight;
                // 视频比例
                float v2 = (float) videoWidth / videoHeight;
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoRenderApi => doMeasureSpec => AUTO => videoWidth = " + videoWidth + ", videoHeight = " + videoHeight + ", screenWidth = " + screenWidth + ", screenHeight = " + screenHeight);
                    LogUtil.log("VideoRenderApi => doMeasureSpec => AUTO => v1 = " + v1 + ", v2 = " + v2);
                }
                // 视频宽高比>屏幕宽高比, 以屏幕宽度为基准缩放
                if (v2 > v1) {
                    int realW = screenWidth;
                    int realH = realW * videoHeight / videoWidth;
                    if (LogUtil.DEBUG) {
                        LogUtil.log("VideoRenderApi => doMeasureSpec => AUTO => realW 1 = " + realW + ", realH = " + realH);
                    }
                    return new int[]{realW, realH};
                }
                // 视频宽高比<屏幕宽高比, 以屏幕高度为基准缩放
                else if (v2 < v1) {
                    int realH = screenHeight;
                    int realW = videoWidth * realH / videoHeight;
                    if (LogUtil.DEBUG) {
                        LogUtil.log("VideoRenderApi => doMeasureSpec => AUTO => realW 2 = " + realW + ", realH = " + realH);
                    }
                    return new int[]{realW, realH};
                }
                // 正方形视频
                else {
                    int realH = screenHeight;
                    int realW = videoWidth * realH / videoHeight;
                    if (LogUtil.DEBUG) {
                        LogUtil.log("VideoRenderApi => doMeasureSpec => AUTO => realW 3 = " + realW + ", realH = " + realH);
                    }
                    return new int[]{realW, realH};
                }
            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoRenderApi => doMeasureSpec => AUTO => Exception " + e.getMessage());
                }
                return null;
            }
        }
    }

    default String saveBitmap(Context context, Bitmap bitmap) {
        try {
            // 1
            File dir = context.getFilesDir();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // 2
            File screenshotDir = new File(dir, "mp_screenshot");
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }
            // 3
            File[] files = screenshotDir.listFiles();
            for (File file : files) {
                if (null == file)
                    continue;
                if (file.exists()) {
                    file.delete();
                }
            }
            // 4
            String screenshotName = System.nanoTime() + ".jpg";
            File screenshotFile = new File(screenshotDir, screenshotName);
            if (screenshotFile.exists()) {
                screenshotFile.delete();
            }
            screenshotFile.createNewFile();
            // 5
            FileOutputStream fos = new FileOutputStream(screenshotFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            // 6
            bitmap.recycle();
            bitmap = null;
            // 5
            return screenshotFile.getAbsolutePath();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoRenderApi => saveBitmap => " + e.getMessage());
            }
            return null;
        }
    }

    /********/

    default void clearSurface(Surface surface) {
        try {
            if (null == surface)
                throw new Exception("surface error: null");
            Paint paint = new Paint();
            paint.setColor(0xff000000);
            Canvas canvas = surface.lockCanvas(null);
            canvas.drawColor(0x00000000, PorterDuff.Mode.CLEAR);
            canvas.drawRect(0, 0, 0 + canvas.getWidth(), 0 + canvas.getHeight(), paint);
            surface.unlockCanvasAndPost(canvas);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoRenderApi => clearSurface => " + e.getMessage());
            }
        }
    }

    default void clearSurfaceGLES(Surface surface) {
        try {
            if (null == surface)
                throw new Exception("surface error: null");
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR1)
                throw new Exception("sdkVersion warning: " + android.os.Build.VERSION.SDK_INT);
            EGLDisplay display = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
            int[] version = new int[2];
            EGL14.eglInitialize(display, version, 0, version, 1);
            int[] attribList = {
                    EGL14.EGL_RED_SIZE, 8,
                    EGL14.EGL_GREEN_SIZE, 8,
                    EGL14.EGL_BLUE_SIZE, 8,
                    EGL14.EGL_ALPHA_SIZE, 8,
                    EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                    EGL14.EGL_NONE, 0,
                    EGL14.EGL_NONE
            };
            EGLConfig[] configs = new EGLConfig[1];
            int[] numConfigs = new int[1];
            EGL14.eglChooseConfig(display, attribList, 0, configs, 0, configs.length, numConfigs, 0);

            EGLConfig config = configs[0];
            EGLContext context = EGL14.eglCreateContext(display, config, EGL14.EGL_NO_CONTEXT, new int[]{
                    EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                    EGL14.EGL_NONE
            }, 0);

            EGLSurface eglSurface = EGL14.eglCreateWindowSurface(display, config, surface,
                    new int[]{
                            EGL14.EGL_NONE
                    }, 0);

            EGL14.eglMakeCurrent(display, eglSurface, eglSurface, context);
            GLES20.glClearColor(0, 0, 0, 1);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            EGL14.eglSwapBuffers(display, eglSurface);
            EGL14.eglDestroySurface(display, eglSurface);
            EGL14.eglMakeCurrent(display, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
            EGL14.eglDestroyContext(display, context);
            EGL14.eglTerminate(display);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoRenderApi => clearSurfaceGLES => " + e.getMessage());
            }
        }
    }
}