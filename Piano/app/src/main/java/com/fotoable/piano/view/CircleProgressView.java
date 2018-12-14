package com.fotoable.piano.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.fotoable.piano.R;

/**
 * Created by fotoable on 2017/1/4.
 */

public class CircleProgressView extends View {

    private Paint mPaintBg = null;
    private Paint mPaintFg = null;
    private RectF mRectF = null;

    private float angle = 0;
    private float angleStep = 10;
    private int progressBgColor;
    private int progressFgColor;
    private float mCircleLineStrokeWidth;
    private volatile boolean mIsDireactToProgress = false;
    private float mTargetProgress;
    private float mTargetAngle;
    private int[] mColors;

    public CircleProgressView(Context context) {
        super(context);
    }

    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        obtainAttributes(context,attrs);
        init();
    }

    public CircleProgressView(Context context, AttributeSet attrs,
                               int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void obtainAttributes(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs,  R.styleable.CircleProgressView);
        mCircleLineStrokeWidth = ta.getDimension( R.styleable.CircleProgressView_line_stroke_width, getResources().getDimension(
                R.dimen.bg_circle_margin));
        progressFgColor = ta.getColor( R.styleable.CircleProgressView_progress_color, getResources().getColor(R.color.circle_progress_color));
        progressBgColor = ta.getColor( R.styleable.CircleProgressView_progress_color_bg, getResources().getColor(R.color.circle_progress_bg_color));
        ta.recycle();
    }

    private void init() {
        mPaintBg = new Paint();
        mPaintBg.setAntiAlias(true);
        mPaintBg.setStyle(Paint.Style.STROKE);
        mPaintBg.setStrokeWidth(mCircleLineStrokeWidth);

        mPaintFg = new Paint();
        mPaintFg.setAntiAlias(true);
        mPaintFg.setStyle(Paint.Style.STROKE);
        mPaintFg.setStrokeWidth(mCircleLineStrokeWidth);

//        progressBgColor = getResources().getColor(R.color.white);
//        progressFgColor = getResources().getColor(R.color.text_modena);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mRectF == null) {
            mRectF = new RectF();
            float margin = getResources().getDimension(
                    R.dimen.bg_circle_margin);
            mRectF.left = margin;
            mRectF.top = margin;
            mRectF.right = getWidth() - margin;
            mRectF.bottom = getHeight() - margin;
        }

        if (mIsDireactToProgress) {
            angle = mTargetProgress / 100 * 360;
            drawCircle(canvas);
        } else {
            angle += angleStep;
            angle = angle > mTargetAngle ? mTargetAngle : angle;
            drawCircle(canvas);
            if (angle < mTargetAngle) {
                //Log.d("View", "angle : " + angle + " targetAngle="
                //+ mTargetAngle + " mTargetProgress=" + mTargetProgress);
                // drawS(canvas);
                postDelayed(mDrawRunnable, 20);
            } else if (angle == mTargetAngle) {
                // drawS(canvas);
            }
        }
    }

    Runnable mDrawRunnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };

    private void drawCircle(Canvas canvas) {
        mPaintBg.setColor(progressBgColor);
        canvas.drawArc(mRectF, 0, 360, false, mPaintBg);

        if (mColors != null && mColors.length >2){
            int count = mColors.length;
            int[] colors = new int[count];
            System.arraycopy(mColors, 0, colors, 0, count);
            LinearGradient shader = new LinearGradient(mRectF.left, mRectF.top, mRectF.right, mRectF.bottom, colors, null,
                    Shader.TileMode.CLAMP);
            mPaintFg.setShader(shader);
        }else {
            mPaintFg.setColor(progressFgColor);
        }

        if (angle < 90) {
            canvas.drawArc(mRectF, 270, angle, false, mPaintFg);
        } else {
            canvas.drawArc(mRectF, 270, 91, false, mPaintFg);
            canvas.drawArc(mRectF, 0, angle - 89, false, mPaintFg);
        }
    }

    public void setProgress(int progress) {
        mIsDireactToProgress = false;
        mTargetProgress = progress >= 100 ? 100 : progress <= 0 ? 0 : progress;
        mTargetAngle = mTargetProgress * 360 / 100;
        Log.d("View", "mTargetProgress" + mTargetProgress);
        angle = 0;
        post(mDrawRunnable);
    }

    public void setCircleColor(int bgColor, int fgColor) {
        this.progressBgColor = bgColor;
        this.progressFgColor = fgColor;
        post(mDrawRunnable);
    }

    public void setArrilColors(int[] colors){
        mColors = colors;
    }

}