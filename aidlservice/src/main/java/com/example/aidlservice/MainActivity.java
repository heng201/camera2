

package com.example.aidlservice;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;



public class MainActivity extends Activity {

    private IMyAidlInterface aidl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindService();
    }
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onBindingDied(ComponentName name) {
            Log.d("aidl", "onBindingDied: ");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("aidl", "onServiceConnected: ");
            //绑定服务成功回调
            aidl = IMyAidlInterface.Stub.asInterface(service);
            try {
                int add = aidl.add(0, 0);
                Log.d("aidl", "onServiceConnected: " + add);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("aidl", "onServiceDisconnected: ");
            //服务断开时回调
            aidl = null;
        }
    };
    private void bindService(){

        Intent intent = new Intent();
        intent.setPackage("com.example.aidlservice");
        intent.setAction("com.example.aidlservice.MyService");
//        startService(intent);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        return super.onTouchEvent(event);
        return false;
    }

}
