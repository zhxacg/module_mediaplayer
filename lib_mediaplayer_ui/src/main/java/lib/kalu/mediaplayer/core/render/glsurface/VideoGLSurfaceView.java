package lib.kalu.mediaplayer.core.render.glsurface;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.core.render.VideoRenderApi;
import lib.kalu.mediaplayer.util.LogUtil;

public class VideoGLSurfaceView extends GLSurfaceView implements VideoRenderApi {

    @Nullable
    private GLDrawer mDrawer;
    @Nullable
    private GLRender mRender;
    @Nullable
    private VideoKernelApi mKernel;


    public VideoGLSurfaceView(Context context) {
        super(context);
        init();
    }

    @Override
    public void init() {
        VideoRenderApi.super.init();
        setFocusable(false);
        setFocusableInTouchMode(false);
        setWillNotDraw(true); //禁止onDraw
        setZOrderOnTop(true); //画布透明处理
        setZOrderMediaOverlay(true); //画面置顶
//        getHolder().setFormat(PixelFormat.TRANSLUCENT);

        // 设置OpenGl ES的版本为2.0
        setEGLContextClientVersion(2);
        //初始化绘制器
        mDrawer = new GLDrawer();
//        mDrawer.setVideoSize(1080, 1920);
        //初始化渲染器
        mRender = new GLRender();
        mRender.addDrawer(mDrawer);
        setRenderer(mRender);
        // 设置渲染的模式
        setRenderMode(RENDERMODE_WHEN_DIRTY);


        registListener();
    }

    @Override
    public void registListener() {
        try {
            SurfaceHolder surfaceHolder = getHolder();
            if (null == surfaceHolder)
                throw new Exception("surfaceHolder error: null");
            surfaceHolder.addCallback(mCallback);
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoGLSurfaceView -> unRegistListener ->");
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoGLSurfaceView -> unRegistListener -> Exception " + e.getMessage());
            }
        }
    }

    @Override
    public void unRegistListener() {
        try {
            SurfaceHolder surfaceHolder = getHolder();
            if (null == surfaceHolder)
                throw new Exception("surfaceHolder error: null");
            surfaceHolder.removeCallback(mCallback);
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoGLSurfaceView -> unRegistListener ->");
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoGLSurfaceView -> unRegistListener -> Exception " + e.getMessage());
            }
        }
    }

    @Override
    public void setSurface(boolean release) {
        try {
            if (null == mKernel)
                throw new Exception("mKernel warning: null");
            if (release) {
//                mKernel.setDisplay(null);
                mKernel.setSurface(null, 0, 0);
            } else {
                mKernel.setSurface(getHolder().getSurface(), 0, 0);
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoGLSurfaceView -> setSurface -> " + e.getMessage());
            }
        }
    }

    @Override
    public void reset() {
        if (LogUtil.DEBUG) {
            LogUtil.log("VideoGLSurfaceView -> reset ->");
        }
        setSurface(false);
    }

    @Override
    public void release() {
        unRegistListener();

        try {
            if (null == mRender)
                throw new Exception("warning: mRender null");
            mRender = null;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoGLSurfaceView -> release -> Exception2 " + e.getMessage());
            }
        }

        try {
            if (null == mDrawer)
                throw new Exception("warning: mDrawer null");
            mDrawer.release();
            mDrawer = null;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoGLSurfaceView -> release -> Exception2 " + e.getMessage());
            }
        }

        try {
            SurfaceHolder surfaceHolder = getHolder();
            if (null == surfaceHolder)
                throw new Exception("surfaceHolder error: null");
            Surface surface = surfaceHolder.getSurface();
            if (null == surface)
                throw new Exception("surface error: null");
//            clearSurface(surface);
            surface.release();
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoGLSurfaceView -> release ->");
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoGLSurfaceView -> release -> Exception3 " + e.getMessage());
            }
        }
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
        return null;
//        try {
//            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
//            mediaMetadataRetriever.setDataSource(url);
//            Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(position);
//            mediaMetadataRetriever.release();
//            return saveBitmap(getContext().getApplicationContext(), bitmap);
//        } catch (Exception e) {
//            return null;
//        }

//        Canvas canvasH = getHolder().lockCanvas(null);//获取画布
//
//        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        Paint paint = new Paint();
//        paint.setColor(Color.BLUE);
//        canvas.drawRect(new RectF(0, 0, getWidth(), getHeight()), paint);
//
//        getHolder().unlockCanvasAndPost(canvasH);//解锁画布，提交画好的图像

//        return saveBitmap(getContext().getApplicationContext(), bitmap);

//        Context context = getContext();
//        Bitmap bitmap = getDrawingCache();
//        return saveBitmap(context, bitmap);
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

    /************/

    /**
     * 记得一定要重新写这个方法，如果角度发生了变化，就重新绘制布局
     * 设置视频旋转角度
     *
     * @param rotation 角度
     */
    @Override
    public void setRotation(float rotation) {
        try {
            float v = getRotation();
            if (v == rotation)
                throw new Exception("rotation warning: " + rotation);
            super.setRotation(rotation);
            requestLayout();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoGLSurfaceView -> setRotation -> " + e.getMessage());
            }
        }
    }

    /***************/

//    private void drawBitmap() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Canvas canvas = null;
//                try {
//                    SurfaceHolder holder = getHolder();
//                    if (null == holder)
//                        throw new Exception("holder warning: null");
//                    canvas = holder.lockCanvas();
//                    Bitmap bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
//                    bitmap.eraseColor(Color.RED);//填充颜色
//                    canvas.drawBitmap(bitmap, 0, 0, null);
//                } catch (Exception e) {
//                    MPLogUtil.log("VideoGLSurfaceView -> drawBitmap -> " + e.getMessage());
//                }
//                try {
//                    SurfaceHolder holder = getHolder();
//                    if (null == holder)
//                        throw new Exception("holder warning: null");
//                    if (null == canvas)
//                        throw new Exception("canvas warning: null");
//                    //手动try catch一下这个方法，让程序在4.3的手机上不至于崩溃，部分Android13也会崩溃
//                    getHolder().unlockCanvasAndPost(canvas);
//                } catch (Exception e) {
//                    MPLogUtil.log("VideoGLSurfaceView -> drawBitmap -> " + e.getMessage());
//                }
//            }
//        }).start();
//    }

    /***************/

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
//            getHolder().setFixedSize(measureSpec[0], measureSpec[1]);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoGLSurfaceView -> onMeasure -> Exception " + e.getMessage());
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Nullable
    private final SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {

        /**
         * 创建的时候调用该方法
         * @param holder                        holder
         */
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoGLSurfaceView -> surfaceCreated ->");
            }
            setSurface(false);
        }

        /**
         * 视图改变的时候调用方法
         * @param holder
         * @param format
         * @param width
         * @param height
         */
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoGLSurfaceView -> surfaceChanged -> width = " + width + ", height = " + height + ",surfaceChanged -> " + this);
            }
        }

        /**
         * 销毁的时候调用该方法
         * @param holder
         */
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoGLSurfaceView -> surfaceDestroyed -> " + this);
            }
            setSurface(true);
        }
    };
}
