package com.example.notification;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private static final String TAG = "no_MainActivity";
    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notification(null);
        notificationduo(null);
        notificationlayout(null);
        notificationMax(null);
        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, Main2Activity.class);
                PersonBeen personBeen = new PersonBeen("limei","女",20);
                intent.putExtra("parcelable",personBeen);
                startActivity(intent);
            }
        });
        HandlerThread handlerThread = new HandlerThread("test1");
        handlerThread.start();
        Handler handler1 = new Handler(handlerThread.getLooper());
        HandlerThread handlerThread1 = new HandlerThread("test2");
        handlerThread1.start();
        final Handler handler2 = new Handler(handlerThread1.getLooper());
        handler1.post(new Runnable() {
            @Override
            public void run() {

                Log.d(TAG, "run:0 ");
            }
        });
        handler1.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: 1");
                handler2.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "run: 2");
                    }
                });

            }
        });
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                while (true){
//                    Log.d("test", "run: ");
//                    try {
//                        Thread.sleep(200);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
//        handler.post(runnable);
//        new Thread(){
//            public void run(){
//                try {
//                    Thread.sleep(2000);
//                    Log.d("postDelayed", "run: postDelayedpostDelayedpostDelayed");
//                    handler.removeCallbacksAndMessages(null);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();


    }
    private int id = 1;

    public void notification(View view) {
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_location_default);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        //设置小图标
        mBuilder.setSmallIcon(R.drawable.ic_location_default);
        //设置大图标
        mBuilder.setLargeIcon(bitmap);
        //设置标题
        mBuilder.setContentTitle("这是标题");
        //设置通知正文
        mBuilder.setContentText("这是正文，当前ID是：" + id);
        //设置摘要
        mBuilder.setSubText("这是摘要");
        //设置是否点击消息后自动clean
        mBuilder.setAutoCancel(true);
        //显示指定文本
        mBuilder.setContentInfo("Info");
        //与setContentInfo类似，但如果设置了setContentInfo则无效果
        //用于当显示了多个相同ID的Notification时，显示消息总数
        mBuilder.setNumber(2);
        //通知在状态栏显示时的文本
        mBuilder.setTicker("在状态栏上显示的文本");
        //设置优先级
        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        //自定义消息时间，以毫秒为单位，当前设置为比系统时间少一小时
        mBuilder.setWhen(System.currentTimeMillis() - 3600000);
        //设置为一个正在进行的通知，此时用户无法清除通知
        mBuilder.setOngoing(true);
        //设置消息的提醒方式，震动提醒：DEFAULT_VIBRATE     声音提醒：NotificationCompat.DEFAULT_SOUND
        //三色灯提醒NotificationCompat.DEFAULT_LIGHTS     以上三种方式一起：DEFAULT_ALL
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        //设置震动方式，延迟零秒，震动一秒，延迟一秒、震动一秒
        mBuilder.setVibrate(new long[]{0, 1000, 1000, 1000});

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        mBuilder.setContentIntent(pIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(id++, mBuilder.build());
    }

    /**
     * 多文本
     * @param view
     */
    public void notificationduo(View view) {
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_location_default);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("这是标题");
        builder.setContentText("这是正文");
        builder.setSmallIcon(R.drawable.ic_location_default);
        builder.setLargeIcon(bitmap);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setAutoCancel(true);
         //设置为一个正在进行的通知，此时用户无法清除通知
        builder.setOngoing(true);
        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.bigText("这里可以写很长的一段话，不信你试试。这里可以写很长的一段话，不信你试试。这里可以写很长的一段话，不信你试试。");
        style.setBigContentTitle("点击后的标题");
        style.setSummaryText("这是摘要");
        builder.setStyle(style);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(id++, builder.build());
    }


    /**
     * 布局通知
     * @param view
     */
    public void notificationlayout(View view) {
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_location_default);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("这是标题");
        builder.setContentText("这是正文");
        builder.setSmallIcon(R.drawable.ic_location_default);
        builder.setLargeIcon(bitmap);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setAutoCancel(true);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        String[] events = {"第一", "第二", "第三", "第四", "第五", "第六", "第七"};
        inboxStyle.setBigContentTitle("展开后的标题");
        inboxStyle.setSummaryText("这是摘要");
        for (String event : events) {
            inboxStyle.addLine(event);
        }
        builder.setStyle(inboxStyle);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(id++, builder.build());
    }

    /**
     * 大图
     * @param view
     */
    public void notificationMax(View view) {
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_location_default);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("这是标题");
        builder.setContentText("这是正文");
        builder.setSmallIcon(R.drawable.ic_location_default);
        builder.setLargeIcon(bitmap);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setAutoCancel(true);

        NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
        style.setBigContentTitle("展开后的标题");
        style.setSummaryText("这是摘要");
        style.bigPicture(bitmap);
        builder.setStyle(style);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(id++, builder.build());
    }


    public void cleanNotification(View view) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
        mNotificationManager.cancel(1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanNotification(null);
    }
}
