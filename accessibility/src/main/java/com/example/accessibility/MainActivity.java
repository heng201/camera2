package com.example.accessibility;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.input.InputManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.SequenceInputStream;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.example.accessibility.MyAccessibilityService.step0;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    public static boolean isPointChange = false;
    private static boolean isExec = false;
    public static boolean isStartRecord = false;

    private Button btn_test;
    private TextView tv_content;
    private Instrumentation mInst = new Instrumentation();
    public static int x = 1080;
    public static int y = 1920;
    private ServiceConnection conn;
    public static IFloatViewAidlInterface iBinder;
    private  Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(getApplicationContext(), "继续操作", Toast.LENGTH_SHORT).show();
        }
    };

    public void launch(String pak, String classname) {
        Intent intent1 = new Intent();
        intent1.setFlags(FLAG_ACTIVITY_NEW_TASK);
        intent1.setClassName(pak, classname);
        startActivity(intent1);
    }
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_test = findViewById(R.id.btn_test);
        tv_content = findViewById(R.id.tv_content);
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                tv_content.setText("被模拟点击了");
            }
        });
        findViewById(R.id.btn_douyin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                step0 = true;
                launch();
            }
        });
        findViewById(R.id.btn_weixin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                EditText contact = findViewById(R.id.et_contact);
                MyAccessibilityService.contact = contact.getText().toString();
                EditText content = findViewById(R.id.et_content);
                MyAccessibilityService.content = content.getText().toString();
                MyAccessibilityService.s0 = true;
                Intent intent1 = new Intent();
                intent1.setFlags(FLAG_ACTIVITY_NEW_TASK);
                intent1.setClassName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
                startActivity(intent1);


            }
        });
       findViewById(R.id.btn_instrumentation).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
//               IBinder imBinder = ServiceManager.getService("input");
//               InputManager im = (InputManager) getSystemService(Context.INPUT_SERVICE);
//               IInputManager im = IInputManager.Stub.asInterface(imBinder);
//
//              //inject key event
//               final KeyEvent keyEvent = new KeyEvent(downTime, eventTime, action,
//                       code, repeatCount, metaState, deviceId, scancode,
//                       flags | KeyEvent.FLAG_FROM_SYSTEM |KeyEvent.FLAG_KEEP_TOUCH_MODE | KeyEvent.FLAG_SOFT_KEYBOARD,
//                       source);
//               event.setSource(InputDevice.SOURCE_ANY)
//               im.injectInputEvent(keyEvent, InputManager.INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH);
//
//               //inject pointer event
//               motionEvent.setSource(InputDevice.SOURCE_TOUCHSCREEN);
//               im.injectInputEvent(motionEvent, InputManager.INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH);
//
//
//               InputManager im = (InputManager) getSystemService(Context.INPUT_SERVICE);
//               IBinder wmbinder = ServiceManager.getService("window");
//               IWindowManager wm = IWindowManager.Stub.asInterface(wmbinder); //pointer
//               wm.injectPointerEvent(myMotionEvent, false); //key
//               wm.injectKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_A), false);
//               wm.injectKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_A), false); //trackball
//               wm.injectTrackballEvent(myMotionEvent, false);

               new Thread(){
                   public void run(){
                       mInst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
                               SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN,
                               x, y, 0));
                       mInst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
                               SystemClock.uptimeMillis(), MotionEvent.ACTION_UP,
                               x, y, 0));


                   }
               }.start();
               new Thread(){
                   public void run(){
                       try {
                           sleep(2000);
                           Log.d(TAG, "run: ");
//                           mInst.sendCharacterSync(KeyEvent.KEYCODE_CAMERA);
                           //KEYCODE_CAMERA,KEYCODE_MENU,KEYCODE_POWER,KEYCODE_BACK
                           //                       mInst.sendCharacterSync(KeyEvent.KEYCODE_HOME);
//                       mInst.sendStringSync("test");
                       mInst.sendKeyDownUpSync(KeyEvent.KEYCODE_HOME);
                       /*sendCharacterSync(int keyCode)           //用于发送指定KeyCode的按键

                       sendKeyDownUpSync(int key)               //用于发送指定KeyCode的按键

                       sendPointerSync(MotionEvent event)    //用于模拟Touch

                       sendStringSync(String text)                  //用于发送字符串*/
                       } catch (InterruptedException e) {
                           e.printStackTrace();
                       }
                   }
               }.start();
               
           }
       });


        findViewById(R.id.btn_startrecord).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 开始录制");
                MyAccessibilityService.isStartRecord = true;
                MyAccessibilityService.eventBeens.clear();
                if (!isExec) {
                    execShellCmdReturn("getevent -l");
                }

//                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//                intent.setData(Uri.parse("package:" + getPackageName()));
//                startActivityForResult(intent, 100);


            }
        });
        findViewById(R.id.btn_stoprecord).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 结束录制");
                MyAccessibilityService.isStartRecord = false;
                isStartRecord = false;
            }
        });
        findViewById(R.id.btn_startplay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 开始播放");
                MyAccessibilityService.isStartPlay = true;
            }
        });
        findViewById(R.id.btn_stopplay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 结束播放");
                MyAccessibilityService.isStartPlay = false;
                MyAccessibilityService.begin1 = false;
            }
        });
       findViewById(R.id.btn_onclick).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               execShellCmd1("getevent -l");
               Log.d(TAG, "onClick: 接收点击成功");

           }
       });

        conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "onServiceConnected: ");
                iBinder =  IFloatViewAidlInterface.Stub.asInterface(service);
                try {
                    iBinder.displayView();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };


//        InputManager im = (InputManager) getSystemService(Context.INPUT_SERVICE);
//        IBinder wmbinder = (IBinder) getSystemService("window");
//        IWindowManager wm = IWindowManager.Stub.asInterface(wmbinder); //pointer
//        wm.injectPointerEvent(myMotionEvent, false); //key
//        wm.injectKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_A), false);
//        wm.injectKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_A), false); //trackball
//        wm.injectTrackballEvent(myMotionEvent, false);
//
//        IBinder imBinder = (IBinder) getSystemService("input");
////        IInputManager im = IInputManager.Stub.asInterface(imBinder);
//
////inject key event
//        final KeyEvent keyEvent = new KeyEvent(downTime, eventTime, action,
//                code, repeatCount, metaState, deviceId, scancode,
//                flags | KeyEvent.FLAG_FROM_SYSTEM |KeyEvent.FLAG_KEEP_TOUCH_MODE | KeyEvent.FLAG_SOFT_KEYBOARD,
//                source);
//        event.setSource(InputDevice.SOURCE_ANY)
//        im.injectInputEvent(keyEvent, InputManager.INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH);
//
////inject pointer event
//        motionEvent.setSource(InputDevice.SOURCE_TOUCHSCREEN);
//        im.injectInputEvent(motionEvent, InputManager.INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH);






        Intent intent = new Intent();
        intent.setClass(MainActivity.this,MyAccessibilityService.class);
        startService(intent);
        Log.d(TAG, "currentThread: " + Thread.currentThread().toString());
        Intent intent1 = new Intent();

        intent1.setClassName("com.ss.android.ugc.aweme", "com.ss.android.ugc.aweme.main.MainActivity");
//        execShellCmd1("getevent -l");
//        startActivity(intent1);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        Intent intent = new Intent();
        intent.setClass(MainActivity.this,FloatViewService.class);
        bindService(intent,conn, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    private void launch() {
        Intent intent1 = new Intent();
        intent1.setFlags(FLAG_ACTIVITY_NEW_TASK);
        intent1.setClassName("com.ss.android.ugc.aweme", "com.ss.android.ugc.aweme.main.MainActivity");
        startActivity(intent1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent: " + event.getX() + " " + event.getY());
        return super.onTouchEvent(event);
    }

    /**
     * 执行shell并输出返回的数据
     * @param cmd
     */
    public static int touchx = 0;
    public static int touchy = 0;
    private  void execShellCmdReturn(final String cmd) {
        isExec = true;
        try {
            Log.d(TAG, "execShellCmd1: " + cmd);
            // 申请获取root权限，这一步很重要，不然会没有作用
            Process process = Runtime.getRuntime().exec("su");
            // 获取输出流
            OutputStream outputStream = process.getOutputStream();
            InputStream inputStream = process.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            final BufferedReader bufrIn = new BufferedReader(inputStreamReader);
            new Thread(){
                public void run(){
                    String line = null;
                    Log.d(TAG, "run: ");
                    while (true) {
                        Log.d(TAG, "run: eeeeeeee");
                        boolean one = false;
                        boolean two = false;
                        boolean three = false;
                        boolean four = false;
                        try {
                            while ((line = bufrIn.readLine()) != null) {
                                Log.d(TAG, "execShellCmd1: line " + line);


                                if (line.contains("BTN_TOUCH")) {
                                    one = true;
                                    continue;
                                }
                                if (one) {
                                    one = false;
                                    two = false;
                                    three = false;
                                    four = false;
                                    if (line.contains("BTN_TOOL_FINGER")) {
                                        two = true;
                                        continue;
                                    }
                                }
                                if (two) {
                                    one = false;
                                    two = false;
                                    three = false;
                                    four = false;
                                    if (line.contains("ABS_MT_POSITION_X")) {
                                        three = true;
                                        String[] split = line.split(" ");
                                        touchx = getInt(split[split.length - 1]);
                                        Log.d(TAG, "run: split " + split.toString()+ " " + touchx);
                                        continue;
                                    }
                                }
                                if (three) {
                                    one = false;
                                    two = false;
                                    three = false;
                                    four = false;
                                    if (line.contains("ABS_MT_POSITION_Y")) {
                                        String[] split = line.split(" ");
                                        touchy = getInt(split[split.length - 1]);
                                        isPointChange = true;
                                        Log.d(TAG, "run: split " + split.toString() + " " + touchy);


                                        judgeEvent();
                                    }
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }.start();
            dataOutputStream.writeBytes(cmd);

            dataOutputStream.flush();
            dataOutputStream.close();
            outputStream.close();
        } catch (Throwable t) {
            Log.d(TAG, "execShellCmd1: Throwable");
            t.printStackTrace();
        }
    }

    private  void judgeEvent() {
        if (!MyAccessibilityService.isStartRecord || !isStartRecord) {
            return;
        }

        new Thread(){
            public void run(){
                for (int i = 0; i < 2; i++){
                    if (MyAccessibilityService.eventBeen == null) {
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (i == 1) {
                            MyAccessibilityService.eventBeens.add(new EventBeen(AccessibilityEvent.TYPE_VIEW_CLICKED, null,
                                    "asdasd", null,
                                    null,MainActivity.touchx, MainActivity.touchy));
                            handler.sendEmptyMessage(0);
                            break;
                        }
                    }  else {
                        MyAccessibilityService.eventBeens.add(MyAccessibilityService.eventBeen);
                        MyAccessibilityService.eventBeen = null;

                        handler.sendEmptyMessage(0);
                        break;
                    }
                }

            }
        }.start();

    }

    private static int getInt(String s) {
        char[] chars = s.toCharArray();
        int sum = 0;
        for (int i = chars.length - 1; i >= 0; i--){
            if (chars[i] <= '9' && chars[i] >= '0') {
                sum = (int) (sum + (chars[i] - '0') * Math.pow(16, chars.length - 1 -i));
            }
            if (chars[i] <= 'f' && chars[i] >= 'a') {
                sum = (int) (sum + (chars[i] - 'a' + 10) * Math.pow(16, chars.length - 1 -i));
            }
        }
        return sum;
    }

    /**
     * 执行shell命令
     *
     * @param cmd
     */
    private static void execShellCmd1(final String cmd) {

        new Thread(){
            public void run(){
                try {

                    Log.d(TAG, "execShellCmd1: ");
                    // 申请获取root权限，这一步很重要，不然会没有作用
                    Process process = Runtime.getRuntime().exec("su");
                    // 获取输出流
                    OutputStream outputStream = process.getOutputStream();

                    DataOutputStream dataOutputStream = new DataOutputStream(
                            outputStream);
                    dataOutputStream.writeBytes(cmd);
                    BufferedReader bufrIn = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
                    String line = null;
                    while ((line = bufrIn.readLine()) != null) {
                        Log.d(TAG, "execShellCmd1: line " + line);
                    }
                    dataOutputStream.flush();
                    dataOutputStream.close();
                    outputStream.close();
                } catch (Throwable t) {
                    Log.d(TAG, "execShellCmd1: Throwable");
                    t.printStackTrace();
                }
            }
        }.start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                PrintWriter pw = null;
                Process process = null;
                try {
                    process = Runtime.getRuntime().exec("su");
                    pw = new PrintWriter(process.getOutputStream());
                    InputStream inStream = process.getInputStream();
                    InputStream errStream = process.getErrorStream();
                    SequenceInputStream sequenceIs = new SequenceInputStream(inStream, errStream);
                    BufferedInputStream bufStream = new BufferedInputStream(sequenceIs);
                    Reader reader = new InputStreamReader(bufStream, "UTF-8");
                    BufferedReader br = new BufferedReader(reader);

                    boolean run = true;
                    int index = 0;
                    while (run){
                        pw.println(cmd);
                        pw.flush();
                        //Log.i(TAG, "run: 写命令");
                        index = 0;

                        char[] data = new char[19];
                        int pos = 0;
                        while ((pos = br.read(data)) != -1){
                            index++;
                            if(index == 4){
                                //Log.i(TAG, "跳过");
                                break;
                            }

                            String line = new String(data);
                            if(line.startsWith("0000")){
                                //Log.i(TAG, "run: 跳过命令分割行");
                                continue;
                            }
                            // 识别键值
                            if(data[7] == 'a' && data[8] == '3' && data[17] == '1'){
                                Log.i(TAG, "run: 下一");
                            }else if(data[7] == 'c' && data[8] == '9'&& data[17] == '1'){
                                Log.i(TAG, "run: 暂停");
                            }else if(data[7] == 'c' && data[8] == '8'&& data[17] == '1'){
                                Log.i(TAG, "run: 开始");
                            }else if(data[7] == 'a' && data[8] == '5'&& data[17] == '1'){
                                Log.i(TAG, "run: 上一");
                            }else {
                                Log.i(TAG, index + "-" + line);
                            }
                            //Log.i(TAG, index + "-" + line);
                        }
                        //Log.i(TAG, "run: 结束一次");
                    }
                    pw.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }finally{
                    if(process!=null){
                        process.destroy();
                    }
                }
            }
        }).start();
//        try {
//            Runtime rt = Runtime.getRuntime();
//            Process proc = rt.exec("dumpsys activity activities");
//            InputStream stderr = proc.getErrorStream();
//            InputStreamReader isr = new InputStreamReader(stderr);
//            BufferedReader br = new BufferedReader(isr);
//            String line = null;
//            System.out.println("<error></error>");
//            while ((line = br.readLine()) != null)
//                System.out.println(line);
//            System.out.println("");
//            int exitVal = proc.waitFor();
//            System.out.println("Process exitValue: " + exitVal);
//        } catch (Throwable t) {
//            t.printStackTrace();
//        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }
}
