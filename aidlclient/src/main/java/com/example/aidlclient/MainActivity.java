package com.example.aidlclient;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;

import com.example.aidlservice.IMyAidlInterface;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends Activity {

    //创建aidl接口引用
    private IMyAidlInterface mIAppServiceInterface = null;
    //创建ServiceConnection接口的实现类对象,用于监听Service的链接和断开连接
    private MyServiceConnection conn = new MyServiceConnection();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //创建绑定的目标AppService的显示意图

        //绑定服务
        findViewById(R.id.id_btn_bind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent("com.example.aidlservice.MyService");
//                intent.setPackage("com.example.aidlservice");
//                final Intent intent = new Intent();
//
//                intent.setComponent(new ComponentName("com.example.aidlservice", "com.example.aidlservice.MyService"));

//                bindService(intent, conn, BIND_AUTO_CREATE);
                Intent intent = new Intent();
                intent.setPackage("com.example.aidlservice");
                intent.setAction("com.example.aidlservice.MyService");
//        startService(intent);
                bindService(intent, conn, Context.BIND_AUTO_CREATE);
            }
        });
        //解绑服务
        findViewById(R.id.id_btn_unbind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                unbindService(conn);
//                mIAppServiceInterface = null;
                //可用的

                WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    manager.startLocalOnlyHotspot(new WifiManager.LocalOnlyHotspotCallback() {

                        @Override
                        public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
                            super.onStarted(reservation);
                            Log.d("onStarted", "Wifi Hotspot is on now");
                        }

                        @Override
                        public void onStopped() {
                            super.onStopped();
                            Log.d("onStopped", "onStopped: ");
                        }

                        @Override
                        public void onFailed(int reason) {
                            super.onFailed(reason);
                            Log.d("onFailed", "onFailed: ");
                        }
                    }, new Handler());
                }
            }
        });
        //获取EditText中录入的文本
//        EditText editText = (EditText) findViewById(R.id.id_edt_input);
//        final String data = editText.getText().toString();
        //提交数据
        findViewById(R.id.id_btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mIAppServiceInterface) {
                    try {
                        //调用aidl接口新定义的方法,动态传递参数
                        mIAppServiceInterface.add(1,3);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }else{
                    Log.d("App2Activity2", "onClick: nullllllllllll");
                }
            }
        });
    }
//    private void bindService(){
//        Intent intent = new Intent();
//        intent.setPackage("com.example.aidlservice");
//        intent.setAction("com.example.aidlservice.action");
//        bindService(intent, connection, Context.BIND_AUTO_CREATE);
//
//    }


    /**
     * 创建ServiceConnection接口的实现类
     */
    private class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            /**
             * 当服务建立连接的时候,就可以在此方法中拿到aidl接口的实现类了,即参数service
             * 注意此时不能直接直接对service进行强制类型转换,因为虽然aidl接口的名字是相同,
             * 当时他们是存在2个不同的应用中的,我们可以通过Stub类的asInterface方法获取目标
             * 应用的aidl接口
             */
            mIAppServiceInterface = IMyAidlInterface.Stub.asInterface(service);
            Log.d("App2Activity2", "目标Service已连接");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("App2Activity2", "目标Service已断开连接");

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
        mIAppServiceInterface = null;
    }

}
