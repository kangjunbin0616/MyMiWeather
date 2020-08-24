package com.example.kangjunbin.mymiweatherdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by kangjunbin on 2020/8/17.
 */

public class SunView extends View {

    //定义各个画笔
    private Paint pathPaint;
    private Paint sunPaint;
    private Paint motionPaint;
    private Paint timePaint;
    private Paint recPaint;
    private Path  pathPath;
    private Path  motionPath;
    private DashPathEffect pathEffect;
    private Boolean isDraw=false;
    private int viewWidth;
    private int viewHeigth;
    int controlX, controlY;
    float startX, startY;
    float endX, endY;
    float t;
    private double rX;
    private double rY;
    private int[] mSunrise = new int[2];
    private int[] mSunset = new int[2];
    private float svSunSize;
    private float svTextSize;
    private float textOffset;
    private float svPadding;
    private float svTrackWidth;


    public SunView(Context context) {
        super(context);
        init(null);
    }

    public SunView(Context context,AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SunView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs){

        //初始化属性
        final Context context = getContext();
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SunView);
        //赋值
        svSunSize = array.getDimension(R.styleable.SunView_svSunRadius, 10);
        svTextSize = array.getDimension(R.styleable.SunView_svTextSize, 18);
        textOffset = array.getDimension(R.styleable.SunView_svTextOffset, 10);
        svPadding = array.getDimension(R.styleable.SunView_svPadding, 10);
        svTrackWidth = array.getDimension(R.styleable.SunView_svTrackWidth, 3);
        array.recycle();

        //实线虚线画笔
        motionPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        motionPaint.setColor(getResources().getColor(R.color.colorPrimary));
        motionPaint.setStrokeCap(Paint.Cap.ROUND);
        motionPaint.setStrokeWidth(svTrackWidth);
        motionPaint.setStyle(Paint.Style.STROKE);
        //时间画笔
        timePaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        timePaint.setStyle(Paint.Style.FILL);
        timePaint.setColor(getResources().getColor(R.color.colorPrimary));
        timePaint.setTextSize(28);
        //矩形遮盖画笔
        recPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        recPaint.setColor(getResources().getColor(R.color.colorPrimary));
        recPaint.setStyle(Paint.Style.FILL);
        //太阳画笔
        sunPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        sunPaint.setColor(getResources().getColor(R.color.sunColor));
        sunPaint.setStyle(Paint.Style.FILL);
        sunPaint.setStrokeWidth(30);
        //实线路径
        pathPath=new Path();
        //虚线路径
        motionPath=new Path();
        //虚线化
        pathEffect=new DashPathEffect(new float[]{6,12},0);

    }
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        canvas.save();
        if(!isDraw){
            viewWidth=getWidth();
            viewHeigth=getHeight();
            controlX = viewWidth/2;
            controlY = 0-viewHeigth/2;
            startX = svPadding;
            startY = viewHeigth-svPadding;
            endX = viewWidth-svPadding;
            endY = viewHeigth-svPadding;
            rX = startX * Math.pow(1 - t, 2) + 2 * controlX * t * (1 - t) + endX * Math.pow(t, 2);
            rY = startY * Math.pow(1 - t, 2) + 2 * controlY * t * (1 - t) + endY * Math.pow(t, 2);
            Log.d("rx1", rX+"");
            Log.d("rx1", rY+"");
            // 运动轨迹
            motionPath.moveTo(startX, startY);
            motionPath.quadTo(controlX, controlY, endX, endY);
         //   isDraw = true;
        }
        // 按遮挡关系画
        // 画已经运动过去的轨迹
        motionPaint.setStyle(Paint.Style.STROKE);
        motionPaint.setPathEffect(null);
        canvas.drawPath(motionPath, motionPaint);
        // 画一个矩形遮住未运动到的渐变和轨迹
        //canvas.drawRect((float) rX, 0, viewWidth,viewHeigth, recPaint);
        // 画一条虚线表示未运动到的轨迹
        motionPaint.setPathEffect(pathEffect);
        canvas.drawPath(motionPath, motionPaint);

        if (mSunrise.length != 0||mSunset.length != 0){
            sunPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("日出 "+(mSunrise[0]<10? "0"+mSunrise[0]: mSunrise[0])
                    +":"+(mSunrise[1]<10? "0"+mSunrise[1]: mSunrise[1]), startX+textOffset, startY, timePaint);
            sunPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText("日落 "+(mSunset[0]<10? "0"+mSunset[0]: mSunset[0])
                    +":"+(mSunset[1]<10? "0"+mSunset[1]: mSunset[1]), endX-textOffset-150, endY, timePaint);
        }

        // 画太阳
        Log.d("rx", rX+"");
        Log.d("rx", rY+"");
        canvas.drawCircle((float) rX, (float)rY, 20*6/5, sunPaint);
        canvas.drawCircle((float) rX, (float)rY, 20, sunPaint);
        // 画端点
        motionPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(startX, startY, svTrackWidth*2, motionPaint);
        canvas.drawCircle(endX, endY, svTrackWidth*2, motionPaint);
        canvas.restore();
    }
    /**
     * 设置当前进度，并更新太阳中心点的位置
     * @param t
     */
//    private void setProgress(float t){
//        Log.d("rxt", startX+"");
//        rX = startX * Math.pow(1 - t, 2) + 2 * controlX * t * (1 - t) + endX * Math.pow(t, 2);
//        rY = startY * Math.pow(1 - t, 2) + 2 * controlY * t * (1 - t) + endY * Math.pow(t, 2);
//        Log.d("rx1", rX+"");
//        Log.d("rx1", rY+"");
//        invalidate();
//    }

    /**
     * 设置当前时间(请先设置日出日落时间)
     */
    public void setCurrentTime(int hour, int minute){
        if (mSunrise.length != 0||mSunset.length != 0){
            float p0 = mSunrise[0]*60+mSunrise[1];// 起始分钟数
            float p1 = hour*60+minute-p0;// 当前时间总分钟数
            float p2 = mSunset[0]*60+mSunset[1]-p0;// 日落到日出总分钟数
            float progress = p1/p2;
            t=progress;
//            setProgress(progress);
            invalidate();

        }
    }

    /**
     * 设置日出时间
     */
    public void setSunrise(int hour, int minute){
        mSunrise[0] = hour;
        mSunrise[1] = minute;
    }

    /**
     * 设置日落时间
     */
    public void setSunset(int hour, int minute){
        mSunset[0] = hour;
        mSunset[1] = minute;
    }
}
