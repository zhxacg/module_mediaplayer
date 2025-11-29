package lib.kalu.mediaplayer.widget.gradient;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.util.LogUtil;

/**
 * 渐变色 直线动画loading
 */
public final class GradientView extends View {

    private float mProgress = 0f;
    private int mBackgroundColorStart = 0xaaf85a55;
    private int mBackgroundColorEnd = 0xaaf85a55;
    private int mBackgroundColorCenter = 0xfff85a55;
    private int mLightColor = 0xaaffffff;
    private int mAnimDuration = 2000;
    private Paint mPaint = null;
    private ValueAnimator mValueAnimator = null;
    private ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener = null;
    private AccelerateDecelerateInterpolator mAccelerateDecelerateInterpolator = null;

    public GradientView(Context context) {
        super(context);
        init(null);
    }

    public GradientView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public GradientView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public GradientView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        try {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int w = (int) (width * 0.7F);
            int h = MeasureSpec.getSize(heightMeasureSpec);
            int specW = MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY);
            int specH = MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY);
            super.onMeasure(specW, specH);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == View.VISIBLE) {
            start();
        } else {
            close();
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility != View.VISIBLE) {
            close();
        }
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        close();
    }

//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        close();
//    }

    @Override
    protected void onDraw(Canvas canvas) {

        try {
            int width = getWidth();
            int height = getHeight();
            int startH = (int) (height * 0.4);
            int endH = (int) (height * 0.6);
            int startW = (int) (width * 0.05);
            int endW = (int) (width * 0.95);

            Path path = new Path();
            path.moveTo(0, startH);
            path.lineTo(startW, 0);
            path.lineTo(endW, 0);
            path.lineTo(width, startH);
            path.lineTo(width, endH);
            path.lineTo(endW, height);
            path.lineTo(startW, height);
            path.lineTo(0, endH);
            path.lineTo(0, startH);
            path.close();

            if (null == mPaint) {
                mPaint = new Paint();
                mPaint.setAntiAlias(true);
                mPaint.setStrokeJoin(Paint.Join.ROUND);
                mPaint.setStrokeCap(Paint.Cap.ROUND);
                mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
//                mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            }

            // 背景色
            @SuppressLint("DrawAllocation")
            LinearGradient backGradient = new LinearGradient(0, 0, width, height,
                    new int[]{mBackgroundColorStart, mBackgroundColorCenter, mBackgroundColorCenter, mBackgroundColorCenter, mBackgroundColorEnd},
                    new float[]{0f, 0.2f, 0.5f, 0.8f, 1f},
                    Shader.TileMode.CLAMP);
            mPaint.setShader(backGradient);
            canvas.drawPath(path, mPaint);

            // 光栅
            float center = mProgress;
            float start = center - 0.15f;
            if (start < 0) {
                start = 0;
            }
            float end = center + 0.15f;
            if (end > 1f) {
                end = 1f;
            }
            @SuppressLint("DrawAllocation")
            LinearGradient shadowGradient = new LinearGradient(0, 0, width, height,
                    new int[]{0x00000000, 0x00000000, mLightColor, 0x00000000, 0x00000000},
                    new float[]{0f, start, center, end, 1f},
//                    new float[]{0f, 0.35f, 0.5f, 0.65f, 1f},
                    Shader.TileMode.CLAMP);
            mPaint.setShader(shadowGradient);

//            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG));
            canvas.drawPath(path, mPaint);

        } catch (Exception e) {
        }
    }

    private void start() {
        try {
            if (null == mValueAnimator) {
                mValueAnimator = ValueAnimator.ofFloat(0f, 1f);
            }
            mValueAnimator.setDuration(mAnimDuration);
            // 循环次数，INFINITE表示无限循坏
            mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
            // 重复类型，有REVERSE和RESTART两种类型，与setRepeatCount配合使用，reverse                                                                       表示倒叙回放，restart表示重新开始
            mValueAnimator.setRepeatMode(ValueAnimator.REVERSE);

            if (null == mAccelerateDecelerateInterpolator) {
                mAccelerateDecelerateInterpolator = new AccelerateDecelerateInterpolator();
            }
            mValueAnimator.setInterpolator(mAccelerateDecelerateInterpolator);
            if (null == mAnimatorUpdateListener) {
                mAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mProgress = (float) animation.getAnimatedValue();
                        invalidate();

                        //                        int value = (int) animation.getAnimatedValue();
//                        for (int i = 0; i < 3; i++) {
//                            int color = i == 0 ? colorStart : i == 1 ? colorCenter : colorEnd;
//                            String A, R, G, B;
//                            StringBuffer sb = new StringBuffer();
//                            A = Integer.toHexString(value);
//                            R = Integer.toHexString(Color.red(color));
//                            G = Integer.toHexString(Color.green(color));
//                            B = Integer.toHexString(Color.blue(color));
//                            //判断获取到的R,G,B值的长度 如果长度等于1 给R,G,B值的前边添0
//                            R = R.length() == 1 ? "0" + R : R;
//                            G = G.length() == 1 ? "0" + G : G;
//                            B = B.length() == 1 ? "0" + B : B;
//                            sb.append("0x");
//                            sb.append(A);
//                            sb.append(R);
//                            sb.append(G);
//                            sb.append(B);
//                            String string = sb.toString();
//                            if (i == 0) {
//                                colorStart = Color.parseColor(string);
//                            } else if (i == 1) {
//                                colorCenter = Color.parseColor(string);
//                            } else {
//                                colorEnd = Color.parseColor(string);
//                            }
//                        }

//                        int value = (int) animation.getAnimatedValue();
//                        int nextInt = new Random().nextInt(255);
//                        int alpha = Math.abs(nextInt + value - 255);
//                        colorStart = (colorStart & 0x00FFFFFF) | (alpha << 24);
//                        colorEnd = (colorEnd & 0x00FFFFFF) | (alpha << 24);
//                        colorCenter = (colorCenter & 0x00FFFFFF) | (alpha << 24);

//                        int value = (int) animation.getAnimatedValue();
//                        colorStart = Color.rgb(255, value, 255 - value);
//                        colorEnd = Color.rgb(value, 0, 255 - value);
                    }
                };
            }
            mValueAnimator.addUpdateListener(mAnimatorUpdateListener);
            mValueAnimator.start();
        } catch (Exception e) {
            if(LogUtil.DEBUG) {
                LogUtil.log("GradientView -> start -> Exception " + e.getMessage());
            }
        }
    }

    private void close() {
        try {
            if (null == mValueAnimator) throw new Exception("Warning: mValueAnimator null");
            mValueAnimator.end();
            mValueAnimator.cancel();
            mValueAnimator = null;
            mAnimatorUpdateListener = null;
            mAccelerateDecelerateInterpolator = null;
        } catch (Exception e) {
            if(LogUtil.DEBUG) {
                LogUtil.log("GradientView -> close -> Exception " + e.getMessage());
            }
        }
    }

    private void init(@Nullable AttributeSet attrs) {
        TypedArray typedArray = null;
        try {
            typedArray = getContext().getApplicationContext().obtainStyledAttributes(attrs, R.styleable.GradientView);
            mBackgroundColorStart = typedArray.getColor(R.styleable.GradientView_gv_background_color_start, 0xaaf85a55);
            mBackgroundColorEnd = typedArray.getColor(R.styleable.GradientView_gv_background_color_end, 0xaaf85a55);
            mBackgroundColorCenter = typedArray.getColor(R.styleable.GradientView_gv_background_color_center, 0xfff85a55);
            mLightColor = typedArray.getColor(R.styleable.GradientView_gv_light_color, 0xaaffffff);
            mAnimDuration = typedArray.getColor(R.styleable.GradientView_gv_anim_duration, 2000);
        } catch (Exception e) {
        }
        if (null != typedArray) {
            typedArray.recycle();
        }
    }
}