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

    void updateVideoWidth(int videoWidth);

    int getVideoWidth();

    void updateVideoHeight(int videoHeight);

    int getVideoHeight();

    void updateVideoBitrate(int videoBitrate);

    int getVideoBitrate();

    default void setVideoSize(int videoWidth, int videoHeight) {
        try {
            if (getVideoWidth() == videoWidth && getVideoHeight() == videoHeight) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoRenderApi", "setVideoSize -> warning: mVideoWidth && mVideoHeight not change");
                }
                return;
            }
            updateVideoWidth(videoWidth);
            updateVideoHeight(videoHeight);
            ((View) this).requestLayout();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoRenderApi -> setVideoSize -> Exception " + e.getMessage());
            }
        }
    }

    /********/

    void updateVideoRotation(@PlayerType.RotationType.Value int videoRotation);

    @PlayerType.RotationType.Value
    int getVideoRotation();

    default void setVideoRotation(@PlayerType.RotationType.Value int videoRotation) {
        try {
            if (getVideoRotation() == videoRotation) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoRenderApi", "setVideoRotation -> warning: mVideoRotation not change");
                }
                return;
            }
            updateVideoRotation(videoRotation);
            ((View) this).requestLayout();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoRenderApi -> setVideoRotation -> Exception " + e.getMessage());
            }
        }
    }

    /********/

    void updateVideoScaleType(@PlayerType.ScaleType.Value int scaleType);

    @PlayerType.ScaleType.Value
    int getVideoScale();

    default void setVideoScaleType(@PlayerType.ScaleType.Value int scaleType) {
        try {
            if (getVideoScale() == scaleType) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoRenderApi", "setVideoScaleType -> warning: mVideoScaleType not change");
                }
                return;
            }
            updateVideoScaleType(scaleType);
            ((View) this).requestLayout();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoRenderApi -> setVideoScaleType -> Exception " + e.getMessage());
            }
        }
    }

    /********/

    default void setVideoFormat(int kernel, int rotation, int scaleType, int width, int height, int bitrate) {
        try {
            if (getVideoWidth() == width && getVideoHeight() == height && getVideoRotation() == rotation && getVideoScale() == scaleType && getVideoBitrate() == bitrate) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoRenderApi", "setVideoFormat -> warning: not change");
                }
                return;
            }
            updateVideoWidth(width);
            updateVideoHeight(height);
            updateVideoBitrate(bitrate);
            updateVideoRotation(rotation);
            updateVideoScaleType(scaleType);
            ((View) this).requestLayout();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoRenderApi -> setVideoFormat -> Exception " + e.getMessage());
            }
        }
    }

    /********/

    default void init() {
        updateVideoWidth(-1);
        updateVideoHeight(-1);
        updateVideoBitrate(-1);
        updateVideoRotation(PlayerType.RotationType.DEFAULT);
        updateVideoScaleType(PlayerType.ScaleType.DEFAULT);
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
            LogUtil.log("VideoRenderApi -> doMeasureSpec -> videoWidth = " + videoWidth + ", videoHeight = " + videoHeight + ", screenWidth = " + screenWidth + ", screenHeight = " + screenHeight + ", videoScaleType = " + videoScaleType + ", videoRotation = " + videoRotation);
        }


        try {

            if (LogUtil.DEBUG) {
                LogUtil.log("VideoRenderApi -> doMeasureSpec -> videoWidth = " + videoWidth + ", videoHeight = " + videoHeight + ", screenWidth = " + screenWidth + ", screenHeight = " + screenHeight);
            }

            if (videoWidth <= 0 || videoHeight <= 0) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoRenderApi", "doMeasureSpec -> warning: videoWidth <= 0 || videoHeight <= 0");
                }
                return null;
            }
            if (screenWidth <= 0 || screenHeight <= 0) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoRenderApi", "doMeasureSpec -> warning: screenWidth <= 0 || screenHeight <= 0");
                }
                return null;
            }

            // 1. 定义目标比例
            float targetRate;

            // 画面拉甚至全屏, 可能变形
            if (videoScaleType == PlayerType.ScaleType.FULL) {
                targetRate = screenWidth * 1f / screenHeight;
            }
            // 画面拉伸16：9, 可能变形
            else if (videoScaleType == PlayerType.ScaleType._16_9) {
                targetRate = 16f / 9f;
            }
            // 画面拉伸16：10, 可能变形
            else if (videoScaleType == PlayerType.ScaleType._16_10) {
                targetRate = 16f / 10f;
            }
            // 画面拉伸5：4, 可能变形
            else if (videoScaleType == PlayerType.ScaleType._5_4) {
                targetRate = 5f / 4f;
            }
            // 画面拉伸4：3, 可能变形
            else if (videoScaleType == PlayerType.ScaleType._4_3) {
                targetRate = 4f / 3f;
            }
            // 画面拉伸1：1, 可能变形
            else if (videoScaleType == PlayerType.ScaleType._1_1) {
                targetRate = 1f;
            }
            // 自动
            else {
                targetRate = videoWidth * 1f / videoHeight;
            }

            // 2. 获取屏幕实际比例
            float screenRate = (float) screenWidth / screenHeight;
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoRenderApi -> doMeasureSpec -> targetRate = " + targetRate + ", videoWidth = " + videoWidth + ", videoHeight = " + videoHeight);
                LogUtil.log("VideoRenderApi -> doMeasureSpec -> screenRate = " + screenRate + ", screenWidth = " + screenWidth + ", screenHeight = " + screenHeight);
            }

            int finalWidth, finalHeight;

            // 3. 计算等比缩放后的尺寸 (FitCenter 逻辑)
            if (screenRate > targetRate) {
                // 屏幕太宽了，以高度为准，宽度缩窄
                finalHeight = screenHeight;
                finalWidth = (int) (finalHeight * targetRate);
            } else {
                // 屏幕太窄了（或者刚好），以宽度为准，高度增加/减少
                finalWidth = screenWidth;
                finalHeight = (int) (finalWidth / targetRate);
            }

            if (LogUtil.DEBUG) {
                LogUtil.log("VideoRenderApi -> doMeasureSpec -> finalWidth = " + finalWidth + ", finalHeight = " + finalHeight);
            }

            // 这里返回计算后的尺寸对象或数组
            return new int[]{finalWidth, finalHeight};

        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoRenderApi -> doMeasureSpec -> Exception " + e.getMessage());
            }
            return null;
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
                LogUtil.log("VideoRenderApi -> saveBitmap -> " + e.getMessage());
            }
            return null;
        }
    }

    /********/

    default void clearSurface(Surface surface) {
        try {
            if (null == surface) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoRenderApi", "clearSurface -> surface error: null");
                }
                return;
            }
            Paint paint = new Paint();
            paint.setColor(0xff000000);
            Canvas canvas = surface.lockCanvas(null);
            canvas.drawColor(0x00000000, PorterDuff.Mode.CLEAR);
            canvas.drawRect(0, 0, 0 + canvas.getWidth(), 0 + canvas.getHeight(), paint);
            surface.unlockCanvasAndPost(canvas);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoRenderApi -> clearSurface -> " + e.getMessage());
            }
        }
    }

    default void clearSurfaceGLES(Surface surface) {
        try {
            if (null == surface) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoRenderApi", "clearSurfaceGLES -> surface error: null");
                }
                return;
            }
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoRenderApi", "clearSurfaceGLES -> sdkVersion warning: " + android.os.Build.VERSION.SDK_INT);
                }
                return;
            }
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
                LogUtil.log("VideoRenderApi -> clearSurfaceGLES -> " + e.getMessage());
            }
        }
    }
}
