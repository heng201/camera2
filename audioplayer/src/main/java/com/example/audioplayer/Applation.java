package com.example.audioplayer;

import android.app.Application;
import android.util.Log;

/**
 * Created by zhangheng1 on 2018/11/16.
 */

public class Applation extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Application", "onCreate: ");
    }
}
