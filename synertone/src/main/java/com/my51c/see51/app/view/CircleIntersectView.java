package com.my51c.see51.app.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;

import com.yzx.tools.DensityUtil;

/**
 * Created by snt1231 on 2018/1/16.
 */

public class CircleIntersectView extends View {
    private int circleR=50;
    private int strokeWidth=25;
    private int centreDis=52;
    private Paint mPaint;
    private PathMeasure mPathMeasure;
    private Path sourcePath;
    private Path dstPath;
    private int startD;

    public CircleIntersectView(Context context) {
        this(context,null);
    }

    public CircleIntersectView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircleIntersectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        mPathMeasure=new PathMeasure();
        initData(context);
    }

    private void initData(Context context) {
        circleR= DensityUtil.dip2px(context,16);
        strokeWidth=DensityUtil.dip2px(context,8);
        centreDis=DensityUtil.dip2px(context,16);
        startD=DensityUtil.dip2px(context,23);
         mPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
         mPaint.setStyle(Paint.Style.STROKE);
         mPaint.setStrokeWidth(strokeWidth);
         mPaint.setColor(Color.parseColor("#00BCD4"));
         sourcePath=new Path();
         dstPath=new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int calW=centreDis+2*circleR+2*strokeWidth;
        int calH=2*circleR+2*strokeWidth;
        int measuredWidth = resolveSize(calW, widthMeasureSpec);
        int measuredHeight = resolveSize(calH, heightMeasureSpec);
        setMeasuredDimension(measuredWidth,measuredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        sourcePath.reset();
        sourcePath.addCircle(circleR+strokeWidth,circleR+strokeWidth,circleR,Path.Direction.CW);
        mPathMeasure.setPath(sourcePath,true);
        float length = mPathMeasure.getLength();
        dstPath.reset();
        mPathMeasure.getSegment(startD,length,dstPath,true);
        canvas.save();
        canvas.rotate(30,circleR+strokeWidth,circleR+strokeWidth);
        canvas.drawPath(dstPath,mPaint);
        canvas.restore();

        canvas.save();
        canvas.translate(centreDis,0);
        canvas.rotate(-150,circleR+strokeWidth,circleR+strokeWidth);
        canvas.drawPath(dstPath,mPaint);
        canvas.restore();

    }
}
