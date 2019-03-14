package com.example.accessibility;

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
    private int x = 0;
    private int y = 0;
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
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent: x: " + event.getRawX() + " y: " + event.getRawY());
        x = (int) event.getRawX();
        y = (int) event.getRawX();
        return super.onTouchEvent(event);
    }
    public int getTouchX(){
        return x;
    }
    public int getTouchY(){
        return y;
    }
}
