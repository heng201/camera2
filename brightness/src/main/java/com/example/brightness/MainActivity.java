package com.example.brightness;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "MainActivity";
    private EditText etNowBright;
    private Button btnGetBright;
    private EditText etSetBright;
    private Button btnSetBright;
    private Button btnAutoBright;
    private Button btnStopautoBright;
    private EditText editText;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-08-06 09:42:58 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        etNowBright = (EditText)findViewById( R.id.et_nowBright );
        btnGetBright = (Button)findViewById( R.id.btn_getBright );
        etSetBright = (EditText)findViewById( R.id.et_setBright );
        btnSetBright = (Button)findViewById( R.id.btn_setBright );
        btnAutoBright = (Button)findViewById( R.id.btn_autoBright );
        btnStopautoBright = (Button)findViewById( R.id.btn_stopautoBright );
        editText = (EditText)findViewById( R.id.editText );

        btnGetBright.setOnClickListener( this );
        btnSetBright.setOnClickListener( this );
        btnAutoBright.setOnClickListener( this );
        btnStopautoBright.setOnClickListener( this );
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        requestPermissions();
        requestWriteSettings();
    }
    /**
     * 当有多个权限需要申请的时候
     * 这里以打电话和SD卡读写权限为例
     */
    private void requestPermissions() {

        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_SETTINGS);
        }

        if (!permissionList.isEmpty()) {  //申请的集合不为空时，表示有需要申请的权限
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), 1);
        } else { //所有的权限都已经授权过了

            findViews();
        }
    }

    /**
     * 权限申请返回结果
     *
     * @param requestCode  请求码
     * @param permissions  权限数组
     * @param grantResults 申请结果数组，里面都是int类型的数
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) { //安全写法，如果小于0，肯定会出错了
                    for (int i = 0; i < grantResults.length; i++) {

                        int grantResult = grantResults[i];
                        if (grantResult == PackageManager.PERMISSION_DENIED) { //这个是权限拒绝
                            String s = permissions[i];
                            Toast.makeText(this, s + "权限被拒绝了", Toast.LENGTH_SHORT).show();
                        } else { //授权成功了
                            //do Something
                            findViews();
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    private static final int REQUEST_CODE_WRITE_SETTINGS = 1;
    private void requestWriteSettings() {
        if (!Settings.System.canWrite(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS );
        }else{
            findViews();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_WRITE_SETTINGS) {
            if (Settings.System.canWrite(this)) {
                Log.d(TAG, "onActivityResult write settings granted" );
                findViews();
            }
        }
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2018-08-06 09:34:28 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if ( v == btnGetBright ) {
            // Handle clicks for btnGetBright
            int nowscreenBrightness = getScreenBrightness(this);
            etNowBright.setText(String.valueOf(nowscreenBrightness));
        } else if ( v == btnSetBright ) {
            // Handle clicks for btnSetBright
            if(etSetBright.getText() == null) {
                Toast.makeText(MainActivity.this, "值不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            int setscreenBrightness = Integer.parseInt(etSetBright.getText().toString());
            if(setscreenBrightness < 0 || setscreenBrightness > 255) {
                Toast.makeText(MainActivity.this, "在[0-255]之间", Toast.LENGTH_SHORT).show();
                return;
            }
            setBrightness(this,setscreenBrightness);
            ContentResolver resolver = this.getContentResolver();
            saveBrightness(resolver,setscreenBrightness);
        } else if ( v == btnAutoBright ) {
            // Handle clicks for btnAutoBright
            startAutoBrightness(this);
        } else if ( v == btnStopautoBright ) {
            // Handle clicks for btnStopautoBright
            stopAutoBrightness(this);
        }
    }

    //判断是否开启了自动亮度调节
    public static boolean isAutoBrightness(ContentResolver aContentResolver) {
        boolean automicBrightness = false;
        try {
            automicBrightness = Settings.System.getInt(aContentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return automicBrightness;
    }


    //**获取屏幕的亮度 **

    public static int getScreenBrightness(Activity activity) {
        int nowBrightnessValue = 0;
        ContentResolver resolver = activity.getContentResolver();
        try {
            nowBrightnessValue = android.provider.Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nowBrightnessValue;
    }

    //        **设置亮度 **
    public static void setBrightness(Activity activity, int brightness) {
        // Settings.System.putInt(activity.getContentResolver(),
        // Settings.System.SCREEN_BRIGHTNESS_MODE,
        // Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
        Log.d("lxy", "set  lp.screenBrightness == " + lp.screenBrightness);
        activity.getWindow().setAttributes(lp);
    }

    //停止自动亮度调节
    public static void stopAutoBrightness(Activity activity) {
        Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    //开启亮度自动调节
    public static void startAutoBrightness(Activity activity) {
        Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }

    //保存亮度设置状态
    public static void saveBrightness(ContentResolver resolver, int brightness) {
        Uri uri = android.provider.Settings.System.getUriFor("screen_brightness");
        android.provider.Settings.System.putInt(resolver, "screen_brightness", brightness);
        // resolver.registerContentObserver(uri, true, myContentObserver);
        resolver.notifyChange(uri, null);
    }
    /**
     * 设置当前activity的屏幕亮度
     *
     * @param paramFloat 0-1.0f
     * @param activity   需要调整亮度的activity
     */
    public static void setActivityBrightness(float paramFloat, Activity activity) {
        Window localWindow = activity.getWindow();
        WindowManager.LayoutParams params = localWindow.getAttributes();
        params.screenBrightness = paramFloat;
        localWindow.setAttributes(params);
    }

    /**
     * 获取当前activity的屏幕亮度
     *
     * @param activity 当前的activity对象
     * @return 亮度值范围为0-0.1f，如果为-1.0，则亮度与全局同步。
     */
    public static float getActivityBrightness(Activity activity) {
        Window localWindow = activity.getWindow();
        WindowManager.LayoutParams params = localWindow.getAttributes();
        return params.screenBrightness;
    }

}
