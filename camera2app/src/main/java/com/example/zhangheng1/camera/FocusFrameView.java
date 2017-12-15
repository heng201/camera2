package com.example.zhangheng1.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by zhangheng1 on 2017/12/15.
 */

/**
 * 对焦框
 */
public class FocusFrameView extends View
{
    private int mcolorfill;
    private int mCenterX;
    private  int mCenterY;
    private int mwidth;
    private  int mheight;

    public int getMcolorfill() {
        return mcolorfill;
    }

    public void setMcolorfill(int mcolorfill) {
        this.mcolorfill = mcolorfill;
    }

    public int getmCenterX() {
        return mCenterX;
    }

    public void setmCenterX(int mCenterX) {
        this.mCenterX = mCenterX;
    }

    public int getmCenterY() {
        return mCenterY;
    }

    public void setmCenterY(int mCenterY) {
        this.mCenterY = mCenterY;
    }

    public int getMwidth() {
        return mwidth;
    }

    public void setMwidth(int mwidth) {
        this.mwidth = mwidth;
    }

    public int getMheight() {
        return mheight;
    }

    public void setMheight(int mheight) {
        this.mheight = mheight;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        //refreshDrawableState();
    }

    public FocusFrameView(Context context, int left, int top, int width, int height, int colorfill) {
        super(context);
        // TODO Auto-generated constructor stub
        this.mCenterX = left;
        this.mCenterY = top;
        this.mwidth = width;
        this.mheight = height;
        this.mcolorfill = colorfill;

    }
    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        Paint mpaint = new Paint();
        mpaint.setColor(mcolorfill);
        mpaint.setStyle(Paint.Style.FILL);
        mpaint.setStrokeWidth(2.0f);
        canvas.drawLine(mCenterX -mwidth / 2 , mCenterY -mheight/ 2 , mCenterX + mwidth/2, mCenterY -mheight/ 2, mpaint);
        canvas.drawLine(mCenterX +mwidth/2, mCenterY -mheight/ 2, mCenterX +mwidth/2, mCenterY +mheight/2, mpaint);
        canvas.drawLine(mCenterX +mwidth/2, mCenterY +mheight/2, mCenterX - mwidth/2, mCenterY +mheight/2, mpaint);
        canvas.drawLine(mCenterX - mwidth/2, mCenterY +mheight/2, mCenterX -mwidth/2, mCenterY -mheight/2, mpaint);
        super.onDraw(canvas);
    }

}