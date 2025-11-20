package lib.kalu.mediaplayer.widget.progress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.util.LogUtil;

public class MultiSegmentProgressBar extends View {

    private Bitmap mThumbIcon = null;
    private float mThumbRadius = 0f;
    private int mThumbColor = Color.BLUE;
    private int mThumbColorGradient = 0;

    private float mCorner = 0f;
    private long mProgress = 0; // 单位毫秒
    private long mMax = 100L;// 单位毫秒
    private List<long[]> mSegments;

    private int mBackgroundColor = Color.BLACK;
    private int mProgressColor = Color.RED;
    private int mProgressColorGradient = 0;
    private int mBufferColor = Color.GRAY;

    private int mWidth;
    private int mHeight;
    private int mRealHeight;


    private RectF mRectBackground;
    private RectF mRectBuffer;
    private RectF mRectProgress;

    private Paint mPaint;
//    private Paint mProgressPaint;
//    private Paint mBufferPaint;
//    private Paint mThumbPaint;

    public MultiSegmentProgressBar(Context context) {
        super(context);
        init(context, null);
    }

    public MultiSegmentProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MultiSegmentProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {

        TypedArray typed = null;
        try {
            typed = context.obtainStyledAttributes(attrs, R.styleable.MultiSegmentProgressBar);
            mBackgroundColor = typed.getColor(R.styleable.MultiSegmentProgressBar_ms_background_color, mBackgroundColor);
            mProgressColorGradient = typed.getResourceId(R.styleable.MultiSegmentProgressBar_ms_progress_color_gradient, mProgressColorGradient);
            mBufferColor = typed.getColor(R.styleable.MultiSegmentProgressBar_ms_buffer_color, mBufferColor);
            mProgressColor = typed.getColor(R.styleable.MultiSegmentProgressBar_ms_progress_color, mProgressColor);
            mCorner = typed.getDimension(R.styleable.MultiSegmentProgressBar_ms_corner, mCorner);
            mThumbRadius = typed.getDimension(R.styleable.MultiSegmentProgressBar_ms_thumb_radius, mThumbRadius);
            mThumbColor = typed.getColor(R.styleable.MultiSegmentProgressBar_ms_thumb_color, mThumbColor);
            mThumbColorGradient = typed.getResourceId(R.styleable.MultiSegmentProgressBar_ms_thumb_color_gradient, mThumbColorGradient);
            //
            int resourceId = typed.getResourceId(R.styleable.MultiSegmentProgressBar_ms_thumb_icon, -1);
            if (resourceId != -1) {

                float reqWidth = typed.getDimension(R.styleable.MultiSegmentProgressBar_ms_thumb_icon_width, 0);
                float reqHeight = typed.getDimension(R.styleable.MultiSegmentProgressBar_ms_thumb_icon_height, 0);

                if (reqWidth > 0 && reqHeight > 0) {

                    Drawable drawable = getResources().getDrawable(resourceId);

                    // 创建与 Drawable 尺寸相同的 Bitmap
                    Bitmap bitmap = Bitmap.createBitmap(
                            (int) reqWidth,
                            (int) reqHeight,
                            Bitmap.Config.ARGB_8888
                    );

                    // 创建 Canvas 并绘制 Drawable
                    Canvas canvas = new Canvas(bitmap);
                    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                    drawable.draw(canvas);


                    mThumbIcon = bitmap;
                }
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log("MultiSegmentProgressBar -> init -> Exception " + e.getMessage(), e);
            }
        }
        if (null != typed) {
            typed.recycle();
        }

        // 缓存段列表
        mSegments = new ArrayList<>();

        // 初始化画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mThumbRadius <= 0) {
            mRealHeight = MeasureSpec.getSize(heightMeasureSpec);
            int measureSpec = MeasureSpec.makeMeasureSpec(mRealHeight * 2, MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, measureSpec);
        } else {
            int thumbHeight = (int) (mThumbRadius * 2);
            mRealHeight = MeasureSpec.getSize(heightMeasureSpec);
            if (mRealHeight < thumbHeight) {
                int measureSpec = MeasureSpec.makeMeasureSpec(thumbHeight, MeasureSpec.EXACTLY);
                super.onMeasure(widthMeasureSpec, measureSpec);
            } else {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (LogUtil.DEBUG) {
            LogUtil.log("MultiSegmentProgressBar -> onDraw -> mWidth = " + mWidth + ", mHeight = " + mHeight + ", mRealHeight = " + mRealHeight + ", mProgress = " + mProgress + ", mMax = " + mMax + ", mThumbIcon = " + mThumbIcon);
        }
        // 绘制背景
        drawBackground(canvas);

        // 绘制缓存段
        drawBuffer(canvas);

        // 绘制播放进度
        drawProgress(canvas);

        // 绘制手柄
        drawThumb(canvas);
    }

    private void drawBackground(Canvas canvas) {
        try {
            if (mWidth <= 0)
                return;
            if (mHeight <= 0)
                return;
            if (null == mRectBackground) {
                float left = 0f;
                float top = mHeight * 0.5F - mRealHeight * 0.5F;
                float right = mWidth;
                float bottom = mHeight * 0.5F + mRealHeight * 0.5F;
                mRectBackground = new RectF(left, top, right, bottom);
            }
            mPaint.setShader(null);
            mPaint.setColor(mBackgroundColor);
            canvas.drawRoundRect(mRectBackground, mCorner, mCorner, mPaint);
        } catch (Exception e) {
        }
    }

    private void drawBuffer(Canvas canvas) {
        try {

            if (LogUtil.DEBUG) {
                LogUtil.log("MultiSegmentProgressBar -> drawBuffer -> mWidth = " + mWidth + ", mHeight = " + mHeight);
            }

            if (mWidth <= 0)
                return;
            if (mHeight <= 0)
                return;

            mPaint.setShader(null);
            mPaint.setColor(mBufferColor);


            boolean isDrawLeft = false;
            boolean isDrawRight = false;

            if (LogUtil.DEBUG) {
                LogUtil.log("MultiSegmentProgressBar -> drawBuffer -> mSegments = " + mSegments);
            }

            // 绘制缓存段
            for (long[] segment : mSegments) {
                if (null == segment)
                    continue;
                if (segment.length != 2)
                    continue;
                long start = segment[0];
                long end = segment[1];

                float left = start * mWidth / mMax;
                float top = mHeight * 0.5F - mRealHeight * 0.5F;
                float right = end * mWidth / mMax;
                float bottom = mHeight * 0.5F + mRealHeight * 0.5F;

                if (LogUtil.DEBUG) {
                    LogUtil.log("MultiSegmentProgressBar -> drawBuffer -> left = " + left + ", top = " + top + ", right = " + right + ", bottom = " + bottom);
                }

                // 左侧边缘半圆
                if (left <= mCorner) {

                    if (!isDrawLeft) {
                        // 绘制上半圆（180度到360度）
                        RectF oval = new RectF(0, top, mCorner, bottom);
                        canvas.drawArc(oval, -90, -180, false, mPaint);
                    }

                    isDrawLeft = true;

                    //
                    left += (mCorner - left);
                    if (right < left) {
                        right = left;
                    }

                }
                // 右侧边缘半圆
                else if (right >= mWidth - mCorner) {


                    if (!isDrawRight) {
                        // 绘制上半圆（180度到360度）
                        RectF oval = new RectF(mWidth - mCorner, top, mWidth, bottom);
                        canvas.drawArc(oval, -90, 180, false, mPaint);
                    }

                    isDrawRight = true;

                    //
                    right -= (right - (mWidth - mCorner));
                    if (left > right) {
                        left = right;
                    }
                }


                if (null == mRectBuffer) {
                    mRectBuffer = new RectF();
                } else {
                    mRectBuffer.setEmpty();
                }
                mRectBuffer.set(left, top, right, bottom);

                canvas.drawRect(mRectBuffer, mPaint);
//                canvas.drawRoundRect(mRectBuffer, mCorner, mCorner, mPaint);
            }
        } catch (Exception e) {
        }
    }

    private void drawProgress(Canvas canvas) {
        try {
            if (mWidth <= 0)
                return;
            if (mHeight <= 0)
                return;

            if (null == mRectProgress) {
                mRectProgress = new RectF();
            } else {
                mRectProgress.setEmpty();
            }
            float left = 0f;
            float top = mHeight * 0.5F - mRealHeight * 0.5F;
            float right = mProgress * mWidth / mMax;
            float bottom = mHeight * 0.5F + mRealHeight * 0.5F;
            mRectProgress.set(left, top, right, bottom);

            mPaint.setShader(null);
            if (mProgressColorGradient != 0) {
                int[] ints = getResources().getIntArray(mProgressColorGradient);
                LinearGradient linearGradient = new LinearGradient(
                        left, top,                 // 起点坐标 (x1, y1)
                        right, bottom,             // 终点坐标 (x2, y2)
                        ints,
                        null,                // 颜色分布位置（null表示均匀分布）
                        Shader.TileMode.CLAMP // 边缘处理模式
                );
                mPaint.setShader(linearGradient);
            } else {
                mPaint.setColor(mProgressColor);
            }
            canvas.drawRoundRect(mRectProgress, mCorner, mCorner, mPaint);
        } catch (Exception e) {
        }
    }

    private void drawThumb(Canvas canvas) {
        try {
            if (mWidth <= 0)
                return;
            if (mHeight <= 0)
                return;

            float radius;
            if (mThumbRadius <= 0f) {
                radius = mHeight * 0.5f;
            } else {
                radius = mThumbRadius;
            }

            float cx = mProgress * mWidth / mMax;
            if (cx < mThumbRadius) {
                cx = mThumbRadius;
            } else if (cx + mThumbRadius > mWidth) {
                cx = mWidth - mThumbRadius;
            }
            float cy = radius;

            mPaint.setShader(null);
            if (mThumbColorGradient != 0) {
                int[] ints = getResources().getIntArray(mThumbColorGradient);
                RadialGradient radialGradient = new RadialGradient(
                        cx, cy,            // 圆心坐标 (centerX, centerY)
                        mThumbRadius,                 // 半径
                        ints,
                        new float[]{0f, 1f}, // 颜色位置（0f为圆心，1f为边缘）
                        Shader.TileMode.CLAMP
                );
                mPaint.setShader(radialGradient);
            } else {
                mPaint.setColor(mThumbColor);
            }
            canvas.drawCircle(cx, cy, radius, mPaint);

            //
            if (null != mThumbIcon) {
                int width = mThumbIcon.getWidth();
                int height = mThumbIcon.getHeight();
                float left = cx - width * 0.5F;
                float top = cy - height * 0.5f;
                canvas.drawBitmap(mThumbIcon, left, top, mPaint);
            }
        } catch (Exception e) {
        }
    }

    public final void setProgress(int progress, int max) {
        this.mProgress = progress;
        this.mMax = max;
        invalidate();
    }

    public final void setMax(int max) {
        this.mMax = max;
        invalidate();
    }

    public final void setBuffer(int buffer) {
        this.mSegments.clear();
        addBuffer(0, buffer);
        invalidate();
    }

    public final void addBufferSegment(long start, long end) {
        addBuffer(start, end);
        invalidate();
    }

    public final void addBufferSegments(long[] segments) {

        if (LogUtil.DEBUG) {
            LogUtil.log("MultiSegmentProgressBar -> addBufferSegments -> segments = " + segments);
        }

        if (null == segments)
            return;
        int length = segments.length;
        if (LogUtil.DEBUG) {
            LogUtil.log("MultiSegmentProgressBar -> addBufferSegments -> segments.length = " + length);
        }
        if (length % 2 != 0)
            return;
        for (int i = 0; i < length; i += 2) {
            long start = segments[i];
            long end = segments[i + 1];
            addBuffer(start, end);
        }
        invalidate();
    }

    public final void addBufferSegments(List<int[]> segments) {
        if (null == segments)
            return;
        for (int[] segment : segments) {
            if (null == segment)
                continue;
            if (segment.length != 2)
                continue;
            int start = segment[0];
            int end = segment[1];
            addBuffer(start, end);
        }
        invalidate();
    }

    public final void clearSegments() {
        mSegments.clear();
        invalidate();
    }

    private void addBuffer(long start, long end) {

        if (LogUtil.DEBUG) {
            LogUtil.log("MultiSegmentProgressBar -> addBuffer -> start = " + start + ", end = " + end + ", mMax = " + mMax);
        }

        try {
            if (start >= end)
                return;
            if (start >= mMax)
                return;
            if (end > mMax) {
                end = mMax;
            }
            // 添加新的缓存段
            if (mSegments.isEmpty()) {
                mSegments.add(new long[]{start, end});
            } else {
                int size = mSegments.size();
                int index = size - 1;
                long[] segment = mSegments.get(index);
                long beforeEnd = segment[1];
                if (start > beforeEnd) {
                    mSegments.add(new long[]{start, end});
                } else if (end > beforeEnd) {
                    segment[1] = end;
                }
            }

        } catch (Exception e) {
        }
    }
}