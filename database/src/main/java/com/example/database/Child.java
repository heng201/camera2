package com.example.database;


import android.util.Log;

/**
 * Created by zhangheng1 on 2018/10/18.
 */

public class Child extends Parent{
    public void sysChild(){
        sys();

        Log.d("Child", "sysChild: 00000000000" + get());
    }
}
