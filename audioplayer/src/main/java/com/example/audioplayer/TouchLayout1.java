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

public class TouchLayout1 extends RelativeLayout {
    private static final String TAG = "TouchLayout";
    public TouchLayout1(Context context) {
        this(context , null);
    }

    public TouchLayout1(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TouchLayout1(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public TouchLayout1(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d(TAG, "dispatchTouchEvent: x: " + ev.getX() + " y: " + ev.getY());

        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent: x: " + event.getX() + " y: " + event.getY());
        return super.onTouchEvent(event);
    }
}
