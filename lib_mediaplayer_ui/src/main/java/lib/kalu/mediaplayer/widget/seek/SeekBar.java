package lib.kalu.mediaplayer.widget.seek;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.util.TimeUtil;

public final class SeekBar extends android.widget.SeekBar {

    private Paint mPaint = null;
    private int mTextColor = Color.WHITE;
    private int mTextSize = 10;
    private int mTextGravity = 2;
    private int mTextDurationPaddingLeft = 0;
    private int mTextDurationPaddingRight = 0;
    private int mTextProgressPaddingLeft = 0;
    private int mTextProgressPaddingRight = 0;

    public SeekBar(Context context) {
        super(context);
        init(null);
    }

    public SeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SeekBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        // 计算 paddingLeft paddingRight
        try {
            long duration = getMax();
            if (duration <= 0) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("SeekBar -> onMeasure -> warning: duration <= 0");
                }
                super.onMeasure(widthMeasureSpec, -2);
                return;
            }

            if (null == mPaint) {
                mPaint = new Paint();
            }
            mPaint.setAntiAlias(true);
            mPaint.setTextSize(mTextSize);
            mPaint.setColor(mTextColor);

            String text = TimeUtil.formatTimeMillis(duration);
            float textWidth = mPaint.measureText(text);
            if (LogUtil.DEBUG) {
                LogUtil.log("SeekBar -> onMeasure -> text = " + text + ", textWidth = " + textWidth);
            }
            int left = (int) (textWidth + mTextProgressPaddingLeft + mTextProgressPaddingRight);
            int right = (int) (textWidth + mTextDurationPaddingLeft + mTextDurationPaddingRight);
            int top = getPaddingTop();
            int bottom = getPaddingBottom();
            setPadding(left, top, right, bottom);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("SeekBar -> onMeasure -> Exception " + e.getMessage());
            }
        }
        super.onMeasure(widthMeasureSpec, -2);
    }

    private long mPosition = 0L;
    private long mDuration = 0L;

    public void setTextInfo(long position, long duration) {
        this.mPosition = position;
        this.mDuration = duration;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // 进度条
        try {

            int visibility = getVisibility();
            if (visibility != View.VISIBLE) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("SeekBar -> onDraw -> error: visibility != View.VISIBLE");
                }
                return;
            }

            long duration = getMax();
            if (duration <= 0) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("SeekBar -> onDraw -> warning: duration <= 0");
                }
                return;
            }
            super.onDraw(canvas);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("SeekBar -> onDraw -> Exception1 " + e.getMessage());
            }
        }

        // 文字
        try {

            if (LogUtil.DEBUG) {
                LogUtil.log("SeekBar -> onDraw -> mDuration = " + mDuration + ", mPosition = " + mPosition);
            }
//            if (mDuration <= 0)
//                throw new Exception("warning: mDuration <= 0");
//
//            if (mPosition <= 0)
//                throw new Exception("warning: mPosition <= 0");

            for (int i = 0; i < 2; i++) {

                if (null == mPaint) {
                    mPaint = new Paint();
                }
                mPaint.setAntiAlias(true);
                mPaint.setTextSize(mTextSize);
                mPaint.setColor(mTextColor);

                if (i == 0) {

                    String text = TimeUtil.formatTimeMillis(mPosition, mDuration);
                    if (LogUtil.DEBUG) {
                        LogUtil.log("SeekBar -> onDraw -> text = " + text);
                    }

                    Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
                    float textHeight = fontMetrics.bottom - fontMetrics.top;

                    int y;
                    if (mTextGravity == 1) {
                        y = (int) (textHeight * 0.8);
                    } else if (mTextGravity == 2) {
                        Rect rect = getProgressDrawable().getBounds();
                        int paddingTop = getPaddingTop();
                        int height = rect.height();
                        int top = rect.top;
                        y = paddingTop + top + height * 2;
                    } else {
                        int height = getHeight();
                        y = (int) (height - fontMetrics.descent);
                    }

                    int left = getPaddingLeft();
                    float textWidth = mPaint.measureText(text);
                    int x = (int) (left - textWidth - mTextProgressPaddingRight);

                    canvas.drawText(text, x, y, mPaint);
                } else {

                    String text = TimeUtil.formatTimeMillis(mDuration);
                    if(LogUtil.DEBUG) {
                        LogUtil.log("SeekBar -> onDraw -> text = " + text);
                    }

                    Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
                    float textHeight = fontMetrics.bottom - fontMetrics.top;

                    int width = getWidth();
                    int right = getPaddingRight();
                    int x = (width - right + mTextDurationPaddingLeft);

                    int y;
                    // top
                    if (mTextGravity == 1) {
                        y = (int) (textHeight * 0.8);
                    }
                    // center
                    else if (mTextGravity == 2) {
                        Rect rect = getProgressDrawable().getBounds();
                        int paddingTop = getPaddingTop();
                        int height = rect.height();
                        int top = rect.top;
                        y = paddingTop + top + height * 2;
                    }
                    // bottom
                    else {
                        int height = getHeight();
                        y = (int) (height - fontMetrics.descent);
                    }

                    canvas.drawText(text, x, y, mPaint);
                }

            }

        } catch (Exception e) {
            if(LogUtil.DEBUG) {
                LogUtil.log("SeekBar -> onDraw -> Exception2 " + e.getMessage());
            }
        }

//        // 快进快退 提示
//        try {
//            int visibility = getVisibility();
//            if (visibility != View.VISIBLE)
//                throw new Exception("error: visibility != View.VISIBLE");
//
////            if (mMode == 2)
////                throw new Exception("warning: mMode = 2");
//
//            long duration = getMax();
//            if (duration <= 0L)
//                throw new Exception("warning: duration <= 0");
//
//            long progress = getProgress();
//            if (progress < 0L) {
//                progress = 0L;
//            }
//
//            if (null == mPaint) {
//                mPaint = new Paint();
//            }
//            mPaint.setAntiAlias(true);
//            mPaint.setTextSize((float) (mTextSize * 1.1));
//            mPaint.setColor(mTextColor);
//
//            String text = TimeUtil.formatTimeMillis(progress, duration);
//            float textWidth = mPaint.measureText(text);
//
//            Rect rect = getProgressDrawable().getBounds();
//            int width = rect.width();
////            int paddingLeft = getPaddingLeft();
//
//            int range = (int) (width * progress / duration);
//            int x = (int) (range + mTextProgressPaddingLeft + mTextProgressPaddingRight + textWidth * 0.5);
//
//            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
//            float textHeight = fontMetrics.bottom - fontMetrics.top;
//
//            int top = getPaddingTop();
//            int y = (int) (top - textHeight * 0.5);
//
//            canvas.drawText(text, x, y, mPaint);
//        } catch (Exception e) {
//            LogUtil.log("SeekBar -> onDraw -> Exception4 " + e.getMessage());
//        }
    }

    private void init(@Nullable AttributeSet attrs) {
        TypedArray a = null;
        try {
            a = getContext().obtainStyledAttributes(attrs, R.styleable.SeekBar);
            //  mMode = a.getInteger(R.styleable.SeekBar_sb_mode, 2);
            mTextColor = a.getColor(R.styleable.SeekBar_sb_text_color, Color.WHITE);
            mTextGravity = a.getInteger(R.styleable.SeekBar_sb_text_gravity, 2);
            mTextSize = a.getDimensionPixelOffset(R.styleable.SeekBar_sb_text_size, 10);
            mTextDurationPaddingLeft = a.getDimensionPixelOffset(R.styleable.SeekBar_sb_text_duration_padding_left, 0);
            mTextDurationPaddingRight = a.getDimensionPixelOffset(R.styleable.SeekBar_sb_text_duration_padding_right, 0);
            mTextProgressPaddingLeft = a.getDimensionPixelOffset(R.styleable.SeekBar_sb_text_progress_padding_left, 0);
            mTextProgressPaddingRight = a.getDimensionPixelOffset(R.styleable.SeekBar_sb_text_progress_padding_right, 0);
//            mMinHeight = a.getDimensionPixelSize(R.styleable.ProgressBar_minHeight, mMinHeight);
//            mMaxHeight = a.getDimensionPixelSize(R.styleable.ProgressBar_maxHeight, mMaxHeight);
        } catch (Exception e) {
        }
        if (null != a) {
            a.recycle();
        }

        setMax(0);
        setProgress(0);
        setSecondaryProgress(0);
        setThumbOffset(0);
    }
}
