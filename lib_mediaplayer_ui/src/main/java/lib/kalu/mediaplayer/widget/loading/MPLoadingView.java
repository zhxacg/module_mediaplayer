package lib.kalu.mediaplayer.widget.loading;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.util.LogUtil;

@SuppressLint("AppCompatCustomView")
public class MPLoadingView extends View {

    private int mDelayMillis = 200;
    private int mLoop = 0;

    private int mCount = 8;
    private float mRadius = 0f;
    private float mRate = 0f;
    private Paint mPaint = new Paint();
    private Rect mRect = new Rect();

    @ColorInt
    private int mColorBackground = Color.TRANSPARENT;
    @ColorInt
    private int mColorRound = Color.GRAY;

    public MPLoadingView(Context context) {
        super(context);
        init(null);
    }

    public MPLoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MPLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MPLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        TypedArray typedArray = null;
        try {
            typedArray = getContext().getApplicationContext().obtainStyledAttributes(attrs, R.styleable.MPLoadingView);
            mCount = typedArray.getInt(R.styleable.MPLoadingView_lv_count, 8);
            mDelayMillis = typedArray.getInt(R.styleable.MPLoadingView_lv_delay_millis, 120);
            mRate = typedArray.getFloat(R.styleable.MPLoadingView_lv_rate, 0.9f);
            mRadius = typedArray.getDimension(R.styleable.MPLoadingView_lv_radius, 0f);
            mColorBackground = typedArray.getColor(R.styleable.MPLoadingView_lv_color_background, Color.TRANSPARENT);
            mColorRound = typedArray.getColor(R.styleable.MPLoadingView_lv_color_round, Color.GRAY);
        } catch (Exception e) {
        }
        if (null != typedArray) {
            typedArray.recycle();
        }
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        if (LogUtil.DEBUG) {
            LogUtil.log("MPLoadingView -> onVisibilityChanged -> visibility = " + visibility);
        }
        super.onVisibilityChanged(changedView, visibility);
        loopingMsg();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        if (LogUtil.DEBUG) {
            LogUtil.log("MPLoadingView -> onWindowVisibilityChanged -> visibility = " + visibility);
        }
        super.onWindowVisibilityChanged(visibility);
        loopingMsg();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearMsg();
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        clearMsg();
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);

        try {
            int visibility = getVisibility();
            if (visibility != View.VISIBLE)
                throw new Exception("visibility warning: " + visibility);
            // 循环次数
            if (mLoop + 1 >= mCount) {
                mLoop = 0;
            }
            // 画笔
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mPaint.setAntiAlias(true);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeWidth(0f);
            mPaint.setFakeBoldText(true);

            float cx = getWidth() * 0.5f;
            float cy = getHeight() * 0.2f;
            float radius;
            if (mRadius == 0f) {
                radius = Math.max(cx, cy) / 5;
            } else {
                radius = mRadius;
            }
            float angle = 360 / mCount;

            mPaint.setColor(mColorBackground);
            canvas.drawCircle(cx, cy, Math.min(cx, cy), mPaint);

            // 椭圆
            int length = mLoop + mCount;
            for (int i = mLoop; i < length; i++) {
                if (i == mLoop) {
                    if (mLoop != 0) {
                        canvas.save();
                        canvas.rotate(angle * (i % mCount), cx, cx);
                    }
                    mPaint.setColor(mColorRound);
                } else {
                    try {
                        float r = ((mColorRound >> 16) & 0xff) / 255.0f;
                        float g = ((mColorRound >> 8) & 0xff) / 255.0f;
                        float b = ((mColorRound) & 0xff) / 255.0f;
                        int a = (255 / 11) * (i - mLoop);
                        int color = ((int) (a * 255.0f + 0.5f) << 24) |
                                ((int) (r * 255.0f + 0.5f) << 16) |
                                ((int) (g * 255.0f + 0.5f) << 8) |
                                (int) (b * 255.0f + 0.5f);
                        mPaint.setColor(color);
                    } catch (Exception e) {
                    }
                }
                radius = radius * mRate;
                canvas.drawCircle(cx, cy, radius, mPaint);
                canvas.save();
                canvas.rotate(angle, cx, cx);
            }

            // delay
            mLoop = mLoop + 1;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("MPLoadingView -> onDraw -> " + e.getMessage());
            }
            try {
                mPaint.reset();
                mPaint.setColor(Color.TRANSPARENT);
                int width = getWidth();
                int height = getHeight();
                mRect.set(0, 0, width, height);
                canvas.drawRect(mRect, mPaint);
//            // 方法一：
//            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.SRC_OVER);
//            //  方法二：
//            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//            //  方法三：
//            Paint paint = new Paint();
//            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//            canvas.drawPaint(paint);
//            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
            } catch (Exception e1) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("MPLoadingView -> onDraw -> " + e.getMessage());
                }
            }
        }
    }

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            try {
                int visibility = getVisibility();
                if (visibility != View.VISIBLE)
                    throw new Exception();
                if (msg.what != 9001)
                    throw new Exception("msg.what error: " + msg.what);
                invalidate();
                // looping
                loopingMsg();
            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("MPLoadingView -> handleMessage -> " + e.getMessage());
                }
            }
        }
    };

    private void clearMsg() {
        try {
            if (null == mHandler)
                throw new Exception("error: null == mHandler");
            mHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("MPLoadingView -> clearMsg -> " + e.getMessage());
            }
        }
    }

    private void loopingMsg() {
        clearMsg();
        try {
            if (null == mHandler)
                throw new Exception("error: null == mHandler");
            mHandler.sendEmptyMessageDelayed(9001, mDelayMillis);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("MPLoadingView -> loopingMsg -> " + e.getMessage());
            }
        }
    }

    /*************/


    public void setDelayMillis(int delayMillis) {
        this.mDelayMillis = delayMillis;
    }


    public void setCount(int count) {
        this.mCount = count;
    }


    public void setRate(float rate) {
        this.mRate = rate;
    }

    public void setRadius(@DimenRes int resId) {
        try {
            float dimension = getResources().getDimension(resId);
            this.mRadius = dimension;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("MPLoadingView -> setRadius -> " + e.getMessage());
            }
        }
    }
}
