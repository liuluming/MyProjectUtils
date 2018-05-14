package com.my51c.see51.app.view;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class RoundView extends View {

    private final int STROKE_WIDTH = 6;
    private final int RADIUS = 20;
    private final int ANIMATION_DURATION = 300;
    private final float SCALE_FACTOR = 0.3f;

    private Paint mPaint;

    public RoundView(Context context) {
        super(context);
        init();
    }

    public RoundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(STROKE_WIDTH);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2,
                RADIUS, mPaint);
    }

    public ObjectAnimator getScalingAnimator() {
        PropertyValuesHolder imgViewScaleY = PropertyValuesHolder.ofFloat(View
                .SCALE_Y, SCALE_FACTOR);
        PropertyValuesHolder imgViewScaleX = PropertyValuesHolder.ofFloat(View
                .SCALE_X, SCALE_FACTOR);

        ObjectAnimator imgViewScaleAnimator = ObjectAnimator
                .ofPropertyValuesHolder(this, imgViewScaleX, imgViewScaleY);
        imgViewScaleAnimator.setRepeatCount(1);
        imgViewScaleAnimator.setRepeatMode(ValueAnimator.REVERSE);
        imgViewScaleAnimator.setDuration(ANIMATION_DURATION);

        return imgViewScaleAnimator;
    }
}
