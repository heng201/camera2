package com.example.audioplayer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.audioplayer.reflect.CompatUtils;
import com.example.audioplayer.reflect.SystemProperties;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;

public class MainActivity extends Activity {

    private static String TAG = "MainActivity";
    private AudioManager audioManager;
    private ServiceConnection conn;

    private IFloatViewAidlInterface iBinder;
    private Handler handler;


    @TargetApi(19)
    private void setTranslucentStatus() {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;    // 设置Activity高亮显示
        window.setAttributes(params);
    }

    /**
     * 执行系统命令, 返回执行结果
     *
     * @param cmd 需要执行的命令
     * @param dir 执行命令的子进程的工作目录, null 表示和当前主进程工作目录相同
     */
    public static String execCmd(String cmd, File dir) throws Exception {
        StringBuilder result = new StringBuilder();

        Process process = null;
        BufferedReader bufrIn = null;
        BufferedReader bufrError = null;

        try {
            // 执行命令, 返回一个子进程对象（命令在子进程中执行）
            process = Runtime.getRuntime().exec(cmd, null, dir);

            // 方法阻塞, 等待命令执行完成（成功会返回0）
            process.waitFor();

            // 获取命令执行结果, 有两个结果: 正常的输出 和 错误的输出（PS: 子进程的输出就是主进程的输入）
            bufrIn = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            bufrError = new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF-8"));

            // 读取输出
            String line = null;
            while ((line = bufrIn.readLine()) != null) {
                result.append(line).append('\n');
                Log.d(TAG, "execCmd: " + line);
            }
            while ((line = bufrError.readLine()) != null) {
                result.append(line).append('\n');
                Log.d(TAG, "execCmd: " + line);
            }

        } finally {
            closeStream(bufrIn);
            closeStream(bufrError);

            // 销毁子进程
            if (process != null) {
                process.destroy();
            }
        }

        // 返回执行结果
        return result.toString();
    }

    private static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception e) {
                // nothing
            }
        }
    }
    private static void execShellCmd2(String cmd) {

        try {
            Log.d(TAG, "execShellCmd1: " + cmd);
            // 申请获取root权限，这一步很重要，不然会没有作用
            Process process = Runtime.getRuntime().exec("su");
            // 获取输出流
            OutputStream outputStream = process.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(
                    outputStream);
            dataOutputStream.writeBytes(cmd);
            dataOutputStream.flush();
            dataOutputStream.close();
            outputStream.close();
        } catch (Throwable t) {
            Log.d(TAG, "execShellCmd1: Throwable");
            t.printStackTrace();
        }
    }
    private static void execShellCmd1(final String cmd) {
        try {
            Log.d(TAG, "execShellCmd1: " + cmd);
            // 申请获取root权限，这一步很重要，不然会没有作用
            Process process = Runtime.getRuntime().exec("su");
            // 获取输出流
            OutputStream outputStream = process.getOutputStream();
            InputStream inputStream = process.getInputStream();
            InputStream errorStream = process.getErrorStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            InputStreamReader errorStreamReader = new InputStreamReader(errorStream, "UTF-8");
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            final BufferedReader bufrIn = new BufferedReader(inputStreamReader);
            final BufferedReader error= new BufferedReader(errorStreamReader);
            new Thread(){
                public void run(){
                    String line = null;
                    try {
                        int read = error.read();
                        while ((line = error.readLine()) != null) {
                            Log.d(TAG, "execShellCmd1: line errorStreamReader" + line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }.start();
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
                                        Log.d(TAG, "run: split " + split.toString() + " " + touchy);
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
    public static int touchx = 0;
    public static int touchy = 0;
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

    private static Class sIntentExtClass;
    private static Field fMZ_ACTION_MTP_OPEN;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler(getMainLooper());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            setTranslucentStatus();
            //还有设置View的高度，因为每个型号的手机状态栏高度都不相同
        }
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
//        List<AudioPlaybackConfiguration> activePlaybackConfigurations = audioManager.getActivePlaybackConfigurations();
//        Log.d(TAG, "onCreate: " + activePlaybackConfigurations.toString());
//        List<AudioRecordingConfiguration> activeRecordingConfigurations = audioManager.getActiveRecordingConfigurations();
//        Log.d(TAG, "onCreate: " + activeRecordingConfigurations.toString());
        conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "onServiceConnected: ");
                iBinder = IFloatViewAidlInterface.Stub.asInterface(service);
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
        findViewById(R.id.btn_display).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: 1111111");
                if (iBinder != null) {
                    try {
                        iBinder.displayView();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    return;


                }
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 100);

            }
        });

        findViewById(R.id.btn_cmd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: btn_cmd");
//                if (sIntentExtClass == null) {
//                    sIntentExtClass = CompatUtils.getClass("android.content.IntentExt");
//                }
//                if (fMZ_ACTION_MTP_OPEN == null) {
//                    fMZ_ACTION_MTP_OPEN = CompatUtils.getField(sIntentExtClass, "MZ_ACTION_MTP_OPEN");
//                }
//                String action = (String) CompatUtils.getFieldValue(sIntentExtClass, "", fMZ_ACTION_MTP_OPEN);
//                Log.d(TAG, "onClick: " + action);
//                SystemProperties.set("persist.sys.mtp.open.config", "no");
//                Intent openMtpIntent = new Intent(action);
//                openMtpIntent.putExtra("sender", "com.meizu.voiceassistant");
//                openMtpIntent.putExtra("usb_mode", false ? "mtp" : "ptp");
//                Intent usbSettingIntent = new Intent();
//                usbSettingIntent.setComponent(new ComponentName("com.android.settings", "com.android.settings.UsbSettings"));
//                usbSettingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP
//                        | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//
//                KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//                if (!keyguardManager.isKeyguardLocked() || !keyguardManager.isKeyguardSecure()) {
//                    sendBroadcast(openMtpIntent);
//                    usbSettingIntent.putExtra("va_key_activity_finish", true);
//                    startActivity(usbSettingIntent);
//
//
//                }

//                if (KeyguardOpenAppHelper.isKeyguardHandle(mContext)) {
//                    ArrayList<PendingIntent> pendingIntentList = new ArrayList<>(2);
//                    pendingIntentList.add(PendingIntent.getBroadcast(mContext, 0, openMtpIntent, PendingIntent.FLAG_ONE_SHOT));
//                    pendingIntentList.add(PendingIntent.getActivity(mContext, 0,
//                            KeyguardOpenAppHelper.convertUsageIntent(mContext, usbSettingIntent), PendingIntent.FLAG_ONE_SHOT));
//                    KeyguardOpenAppHelper.speakAndSendCommand(getContext(), this, pendingIntentList);
//                    return true;
//                }
//                File file = null;
//                try{
//                    file = new File("sdcard/myfile.txt");
//                    if(file.createNewFile())
//                        System.out.println("文件创建成功！");
//                    else
//                        System.out.println("出错了，该文件已经存在。");
//                }
//                catch(IOException ioe) {
//                    ioe.printStackTrace();
//                }
//                try {
                    execShellCmd1("getevent -l");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        });
        findViewById(R.id.btn_dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showCustomPopupMenu();
                Log.d(TAG, "onClick: ");
                try {
                    iBinder.dismissView();
                } catch (RemoteException e) {

                }
//                new Thread(){
//                    public void run(){
//
//                    }
//                }.start();
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        WindowManager mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//                        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.float_view, null);
//                        ViewGroup.LayoutParams layoutParams =
//                                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//                        view.setLayoutParams(layoutParams);
//                        WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams();
//                        mLayoutParams.flags = mLayoutParams.flags
//                                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
//                                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//                        mLayoutParams.dimAmount = 0.2f;
//                        mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
//                        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//                        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
//                        mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
//                        mLayoutParams.format = PixelFormat.RGBA_8888;
//                        mLayoutParams.alpha = 1.0f;  //  设置整个窗口的透明度
//
//                        mLayoutParams.x = 500;
//                        mLayoutParams.y = 1000;
//                        mWindowManager.addView(view, mLayoutParams);
//                        try {
//                            iBinder.dismissView();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                });

            }
        });
//        Intent intent = new Intent();
//        intent.setClass(MainActivity.this,FloatViewService.class);
//        bindService(intent,conn, Context.BIND_AUTO_CREATE);
//        startService(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, FloatViewService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    private void showCustomPopupMenu() {
        WindowManager windowManager2 = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.float_view, null);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER | Gravity.CENTER;
        params.x = 500;
        params.y = 1000;
        windowManager2.addView(view, params);
    }
}
