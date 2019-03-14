package com.example.audioplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by zhangheng1 on 2018/11/2.
 */

public class TouchLayout extends RelativeLayout {
    private static final String TAG = "TouchLayout";
    public TouchLayout(Context context) {
        this(context , null);
    }

    public TouchLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TouchLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public TouchLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        LayoutInflater.from(context).inflate(R.layout.float_view, this, true);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d(TAG, "dispatchTouchEvent: x: " + ev.getX() + " y: " + ev.getY());

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d(TAG, "onInterceptTouchEvent: x: " + ev.getX() + " y: " + ev.getY());
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        Log.d(TAG, "dispatchGenericMotionEvent: x: " + event.getX() + " y: " + event.getY());
        return super.dispatchGenericMotionEvent(event);
    }

    @Override
    protected boolean dispatchGenericFocusedEvent(MotionEvent event) {
        Log.d(TAG, "dispatchGenericFocusedEvent: x: " + event.getX() + " y: " + event.getY());
        return super.dispatchGenericFocusedEvent(event);
    }

    @Override
    protected boolean dispatchGenericPointerEvent(MotionEvent event) {
        Log.d(TAG, "dispatchGenericPointerEvent: x: " + event.getX() + " y: " + event.getY());
        return super.dispatchGenericPointerEvent(event);
    }

    @Override
    protected boolean dispatchHoverEvent(MotionEvent event) {
        Log.d(TAG, "dispatchHoverEvent: x: " + event.getX() + " y: " + event.getY());
        return super.dispatchHoverEvent(event);
    }

    @Override
    public boolean dispatchCapturedPointerEvent(MotionEvent event) {
        Log.d(TAG, "dispatchCapturedPointerEvent: x: " + event.getX() + " y: " + event.getY());
        return super.dispatchCapturedPointerEvent(event);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent: x: " + event.getRawX() + " y: " + event.getRawY());
        Log.d(TAG, "onTouchEvent: x: " + event.getX() + " y: " + event.getY());
        return super.onTouchEvent(event);
    }
}
