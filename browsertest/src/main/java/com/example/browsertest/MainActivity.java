package com.example.browsertest;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    private TextView tv_browser;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };
    private int a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_browser = findViewById(R.id.tv_browser);
         a = 0;
        try {
            Log.d(TAG, "onClick: ");
//            Intent intent = new Intent();
//            intent.setAction("android.intent.action.VIEW");
//            Uri content_url = Uri.parse("www.baidu.com");//splitflowurl为分流地址
//            intent.setData(content_url);
//            intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
//            startActivity(intent);
//            Intent intent = new Intent();
//            intent.setClass(MainActivity.this,SecondActivity.class);
//            startActivity(intent);
        } catch (Exception e) {
            Log.d(TAG, "onClick: " + e.toString());
            e.printStackTrace();
        }
        tv_browser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (a) {
                    case  0:

                        Log.d(TAG, "onClick: " + a);
                        break;
                    case  1:

                        Log.d(TAG, "onClick: " + a);
                        break;
                    case  2:
                    case  3:
                        Log.d(TAG, "onClick: " + a);
                        break;
                    case  4:

                        Log.d(TAG, "onClick: " + a);
                        break;
                }
                a++;
//                System.exit(0);
//                Intent intent = new Intent();
//                intent.setClass(MainActivity.this,SecondActivity.class);
//                startActivity(intent);
//                try {
//                    Log.d(TAG, "onClick: ");
//                    Intent intent = new Intent();
//                    intent.setAction("android.intent.action.VIEW");
//                    Uri content_url = Uri.parse("www.baidu.com");//splitflowurl为分流地址
//                    intent.setData(content_url);
//                    intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
//                    startActivity(intent);
//                } catch (Exception e) {
//                    Log.d(TAG, "onClick: " + e.toString());
//                    e.printStackTrace();
//                }

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
//        handler.sendEmptyMessage(0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: ");
    }
}
