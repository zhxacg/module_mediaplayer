package lib.kalu.mediaplayer.core.render.surface;

import android.content.Context;
import android.view.KeyEvent;
import android.view.SurfaceControl;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.core.render.VideoRenderApi;
import lib.kalu.mediaplayer.util.LogUtil;

/**
 * 优点：可以在一个独立的线程中进行绘制，不会影响主线程；使用双缓冲机制，播放视频时画面更流畅
 * 缺点：Surface不在View hierachy中，它的显示也不受View的属性控制，所以不能进行平移，缩放等变换，
 * 也不能放在其它ViewGroup中。SurfaceView 不能嵌套使用。
 * <p>
 * SurfaceView双缓冲
 * 1.SurfaceView在更新视图时用到了两张Canvas，一张frontCanvas和一张backCanvas。
 * 2.每次实际显示的是frontCanvas，backCanvas存储的是上一次更改前的视图，当使用lockCanvas（）获取画布时，
 * 得到的实际上是backCanvas而不是正在显示的frontCanvas，之后你在获取到的backCanvas上绘制新视图，
 * 再unlockCanvasAndPost（canvas）此视图，那么上传的这张canvas将替换原来的frontCanvas作为新的frontCanvas，
 * 原来的frontCanvas将切换到后台作为backCanvas。
 */
public class VideoSurfaceView extends SurfaceView implements VideoRenderApi {

    @Nullable
    private VideoKernelApi mKernel;

    private int mVideoWidth = -1;
    private int mVideoHeight = -1;
    private int mVideoBitrate = -1;
    private int mVideoRotation = PlayerType.RotationType.DEFAULT;
    private int mVideoScaleType = PlayerType.ScaleType.DEFAULT;

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

    public VideoSurfaceView(Context context) {
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
        registListener();
    }

    @Override
    public void registListener() {
        try {
            SurfaceHolder surfaceHolder = getHolder();
            if (null == surfaceHolder) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoSurfaceView", "registListener -> surfaceHolder error: null");
                }
                return;
            }
            surfaceHolder.addCallback(mCallback);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoSurfaceView -> registListener -> " + e.getMessage());
            }
        }
    }

    @Override
    public void unRegistListener() {
        try {
            SurfaceHolder surfaceHolder = getHolder();
            if (null == surfaceHolder) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoSurfaceView", "unRegistListener -> surfaceHolder error: null");
                }
                return;
            }
            surfaceHolder.removeCallback(mCallback);
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoSurfaceView -> release -> unRegistListener -> succ");
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoSurfaceView -> release -> unRegistListener -> " + e.getMessage());
            }
        }
    }

    @Override
    public void setSurface(boolean release) {
        try {
            if (null == mKernel) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoSurfaceView", "setSurface -> mKernel warning: null");
                }
                return;
            }
            if (release) {
//                mKernel.setDisplay(null);
                mKernel.setSurface(null, 0, 0);
            } else {
                mKernel.setSurface(getHolder().getSurface(), 0, 0);
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoSurfaceView -> setSurface -> " + e.getMessage());
            }
        }
    }

    @Override
    public void reset() {
        if (LogUtil.DEBUG) {
            LogUtil.log("VideoSurfaceView -> reset ->");
        }
        setSurface(false);
    }

    @Override
    public void release() {
        if (LogUtil.DEBUG) {
            LogUtil.log("VideoSurfaceView -> release ->");
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
//        try {
//            float v = getRotation();
//            if (v == rotation)
//                throw new Exception("rotation warning: " + rotation);
//            super.setRotation(rotation);
//            requestLayout();
//        } catch (Exception e) {
//            LogUtil.log("VideoSurfaceView -> setRotation -> " + e.getMessage());
//        }
    }

    @Override
    public void setFixedSize(int width, int height) {
        getHolder().setFixedSize(width, width);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        try {
            int screenWidth = MeasureSpec.getSize(widthMeasureSpec);
            int screenHeight = MeasureSpec.getSize(heightMeasureSpec);
            int[] measureSpec = doMeasureSpec(screenWidth, screenHeight);
            if (null == measureSpec) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("VideoSurfaceView", "onMeasure -> warning: measureSpec null");
                }
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                return;
            }
            int width = measureSpec[0];
            int height = measureSpec[1];
//            LogUtil.log("VideoSurfaceView -> onMeasure -> width = " + width + ", height = " + height + ",screenWidth = " + screenWidth + ", screenHeight = " + screenHeight);
            int specW = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            int specH = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            super.onMeasure(specW, specH);
            // getHolder().setFixedSize(specW, specH);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoSurfaceView -> onMeasure -> Exception " + e.getMessage());
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private final SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {

        /**
         * 创建的时候调用该方法
         * @param holder                        holder
         */
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoSurfaceView -> surfaceCreated -> holder = " + holder);
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
                LogUtil.log("VideoSurfaceView -> surfaceChanged -> holder = " + holder + ", format = " + format + ", width = " + width + ", height = " + height);
            }
        }

        /**
         * 销毁的时候调用该方法
         * @param holder
         */
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (LogUtil.DEBUG) {
                LogUtil.log("VideoSurfaceView -> surfaceDestroyed -> holder = " + holder);
            }
            setSurface(true);
        }
    };
}
