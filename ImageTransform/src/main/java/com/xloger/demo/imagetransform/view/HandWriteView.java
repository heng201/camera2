package com.xloger.demo.imagetransform.view;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.xloger.demo.imagetransform.R;

/**
 * Created by xloger on 9月8日.
 * Author:xloger
 * Email:phoenix@xloger.com
 */
public class HandWriteView extends View {
    private Paint paint=null;
    private Bitmap originalBitmap=null;
    private Bitmap new1Bitmap=null;
    private Bitmap new2Bitmap=null;
    private float clickX=0,clickY=0;
    private float startX=0,startY=0;
    private boolean isMove=true;
    private boolean isClear=false;
    private int color= Color.BLUE;
    private float storkeWidth=5.0f;

    public HandWriteView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        originalBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//        new1Bitmap = Bitmap.createBitmap(originalBitmap);
    }

    public void setImg(Bitmap bitmap){
        originalBitmap=bitmap;
//        new1Bitmap = Bitmap.createBitmap(originalBitmap);
        new1Bitmap=originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        setMinimumHeight(originalBitmap.getHeight());
        setMinimumWidth(originalBitmap.getWidth());
        invalidate();
    }

    public void clear(){
        isClear=true;
        new2Bitmap=originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (new1Bitmap != null) {
            canvas.drawBitmap(HandWriting(new1Bitmap),0,0,null);
        }
    }

    public Bitmap HandWriting(Bitmap bitmap){
        Canvas canvas=null;

        if (isClear){
            canvas=new Canvas(new2Bitmap);
        }else {
            canvas=new Canvas(bitmap);
        }

        paint=new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStrokeWidth(storkeWidth);
        if (isMove){
            canvas.drawLine(startX,startY,clickX,clickY,paint);
        }

        startX=clickX;
        startY=clickY;

        if (isClear){
            return new2Bitmap;
        }else {
            return bitmap;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        clickX=event.getX();
        clickY=event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.e("xloger","down");
                isMove=false;
                getParent().requestDisallowInterceptTouchEvent(true);
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                Log.e("xloger","move");
                getParent().requestDisallowInterceptTouchEvent(true);
                isMove=true;
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }


}
