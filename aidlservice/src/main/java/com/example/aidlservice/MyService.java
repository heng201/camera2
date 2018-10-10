package com.example.aidlservice;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

public class MyService extends Service {
    private String data = "默认数据";
    private boolean isRunning = true;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("AppService", "onBind");

        //通过向导生成了aidl文件后,需要Rebuild Project后,在onBind方法中就可以直接创建IAppServiceInterface.Stub对象了.
        return new IMyAidlInterface.Stub() {
            @Override
            public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
            }

            @Override
            public int add(int a, int b) throws RemoteException {
                MyService.this.data = String.valueOf(a + b);
                return -1;
            }
        };
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("AppService", "onCreate");
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    SystemClock.sleep(1000);
                    Log.i("AppService", "接收到的数据是:" + data);
                }
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != intent) {
            Log.i("AppService", "onStartCommand接收到的数据是:" + intent.getStringExtra("data"));
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("AppService", "onDestroy");
        isRunning = false;
    }
}
