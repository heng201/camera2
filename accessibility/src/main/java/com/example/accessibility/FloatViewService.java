package com.example.accessibility;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;



/**
 * Created by zhangheng1 on 2018/11/2.
 */

public class FloatViewService extends Service {

    private static String TAG = "FloatViewService";
    private WindowManager mWindowManager;
    private TouchLayout view;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");



    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
         FloatViewAidlInterface iFloatViewAidlInterface = new FloatViewAidlInterface(this);

        return iFloatViewAidlInterface;
    }
    class FloatViewAidlInterface extends IFloatViewAidlInterface.Stub {
        private Context mContext;
        public FloatViewAidlInterface(Context context) {
            mContext = context;
        }

        @Override
        public void displayView() throws RemoteException {
            Log.d(TAG, "displayView: ");
            mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            view = new TouchLayout(mContext);
            ViewGroup.LayoutParams layoutParams =
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(layoutParams);
            WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams();
            mLayoutParams.flags = mLayoutParams.flags
                    | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            mLayoutParams.dimAmount = 0.2f;
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY ;
            mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
            mLayoutParams.format = PixelFormat.RGBA_8888;
            mLayoutParams.alpha = 1.0f;  //  设置整个窗口的透明度

            mLayoutParams.x = 0;
            mLayoutParams.y = 0;
            mWindowManager.addView(view, mLayoutParams);
//            mWinManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
//            View view = LayoutInflater.from(mContext).inflate(R.layout.float_view, null);
//            ViewGroup.LayoutParams layoutParams1 =
//                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            view.setLayoutParams(layoutParams1);
//            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
//
//            params.type = WindowManager.LayoutParams.TYPE_TOAST;
//            params.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//            params.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
//            params.flags |= WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
//            params.gravity = Gravity.START | Gravity.BOTTOM;
//            params.format = PixelFormat.RGBA_8888;
//
//            params.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
//            params.setTitle("voice_floatview");
//
//            params.width = WindowManager.LayoutParams.MATCH_PARENT;
//            params.height = 100;
//
//            mWinManager.addView(view, params);

        }

        @Override
        public void dismissView() throws RemoteException {

            mWindowManager.removeView(view);
        }

        @Override
        public int getX() throws RemoteException {
            return view.getTouchX();
        }

        @Override
        public int getY() throws RemoteException {
            return view.getTouchY();
        }
    }

}
