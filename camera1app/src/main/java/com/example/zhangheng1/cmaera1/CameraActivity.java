package com.example.zhangheng1.cmaera1;

import android.Manifest;
import android.app.Activity;

import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;

import android.os.Handler;
import android.os.Message;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class CameraActivity extends Activity implements View.OnClickListener {

    private LinearLayout llTitle;
    private TextView tvLight;
    private TextView tv_opencameraTime;
    private TextView tv_openTostartpreTime;
    private TextView tv_clickTodataReadyTime;
    private TextView tv_dateTostartTime;
    private TextView tv_stopPre;
    private Button btn_stopPre;
    private Button btn_closeCamera;
    private TextView tv_closeCamera;
    private TextView tv_setcamera;
    private TextView tv_cameraID;
    private TextView tv_shut;
    private Button btn_autotest;

    private long openCameraTime = 0;
    private long setTime = 0;
    private long startPreTime = 0;
    private long shutTime = 0;
    private long pictureTime = 0;
    private long rePreTime = 0;
    private long stopPreTime = 0;
    private long closeCameraTime = 0;


    private int CAMERAID = 0;
    private EditText et_cameraID;

    private static int TV_OPENCAMERATIME = 3;
    private static int TV_OPENTOSTARTPRETIME = 4;
    private static int TV_CLICKTODATAREADYTIME = 5;
    private static int TV_DATETOSTARTTIME = 6;
    private static int TV_STOPPRE = 7;
    private static int TV_SETCAMERA = 8;
    private static int TV_SHUT = 9;
    private SurfaceView surfaceViewCamera2Activity;
    private ImageButton ibTakephoto;
    private ImageView ivPhotes;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private String lightOpenOrClose = Camera.Parameters.FLASH_MODE_ON;//闪光灯是否打开

    private static int LIGHTOPEN = 0;
    private static int LIGHTCLOSE = 1;
    private static int TAKEPICTURES = 2;
    private int allreadyGetPictureNum = 0;//已经连拍的数量
    private MediaRecorder mediaRecorder;


    //@SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == LIGHTCLOSE) {
                tvLight.setText("闪光灯关闭");
            } else if (msg.what == LIGHTOPEN) {
                tvLight.setText("闪光灯打开");
            } else if (msg.what == TAKEPICTURES) {
                Log.d("TAKEPICTURES", "handleMessage: TAKEPICTURES");
                mediaRecorder.stop();

                //创建MediaMetadataRetriever对象
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                //设置资源位置
                String path = "/sdcard/testvideo.3gp";
                //绑定资源
                mmr.setDataSource(path);
                for (int i = 0; i < 1000; i = i + 100) {
                    Bitmap bitmapI = mmr.getFrameAtTime(i);
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssmm").format(new Date());
                    String bitmapPath = "/sdcard/" + timeStamp + ".png";
                    saveBitmap(bitmapPath, bitmapI);
                }

                //获取第一帧图像的bitmap对象
                Bitmap bitmap = mmr.getFrameAtTime();
                //加载到ImageView控件上
                ivPhotes.setImageBitmap(bitmap);
                //finish();
                //handler.removeMessages(TAKEPICTURES);
//                Log.d("连拍handler", "handleMessage: 连拍handler");
//                allreadyGetPictureNum = 0;
//                getPrePicture();
                //camera.setPreviewCallback(null);
            }else if(msg.what == TV_OPENCAMERATIME) {
                tv_opencameraTime.setText("打开相机," + msg.obj.toString() + " ms");
            }else if(msg.what == TV_OPENTOSTARTPRETIME) {
                tv_openTostartpreTime.setText("打开相机之后到开始预览 " + msg.obj.toString() + " ms");

            }else if(msg.what == TV_CLICKTODATAREADYTIME) {

                tv_clickTodataReadyTime.setText("点击拍照到数据完成 " + msg.obj.toString() + " ms");
            }else if(msg.what == TV_DATETOSTARTTIME) {

                tv_dateTostartTime.setText("数据准备好后到重新开始预览 " + msg.obj.toString() + " ms");
            }else if(msg.what == TV_STOPPRE) {

                tv_stopPre.setText("结束预览的时间 " + msg.obj.toString() + " ms" );
            }else if(msg.what == TV_SETCAMERA) {
                tv_setcamera.setText("设置相机参数的时间 " + msg.obj.toString() + " ms");
            }else if(msg.what == TV_SHUT) {
                tv_shut.setText("点拍照到快门响应时间" + msg.obj.toString() + " ms");
            }

        }
    };
    private long startTime;


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-12-07 15:40:19 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        llTitle = (LinearLayout) findViewById(R.id.ll_title);
        tvLight = (TextView) findViewById(R.id.tv_light);
        surfaceViewCamera2Activity = (SurfaceView) findViewById(R.id.surface_view_camera2_activity);
        ibTakephoto = (ImageButton) findViewById(R.id.ib_takephoto);
        ivPhotes = (ImageView) findViewById(R.id.iv_photes);
        tv_opencameraTime = findViewById(R.id.tv_opencameraTime);


        tv_openTostartpreTime = findViewById(R.id.tv_openTostartpreTime);
        tv_clickTodataReadyTime = findViewById(R.id.tv_clickTodataReadyTime);
        tv_dateTostartTime = findViewById(R.id.tv_dateTostartTime);
        tv_stopPre = findViewById(R.id.tv_stopPre);
        btn_stopPre = findViewById(R.id.btn_stopPre);
        btn_closeCamera = findViewById(R.id.btn_closeCamera);
        tv_closeCamera = findViewById(R.id.tv_closeCamera);
        tv_shut = findViewById(R.id.tv_shut);
        btn_autotest = findViewById(R.id.btn_autotest);
        btn_autotest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TestThreead().start();
                btn_autotest.setEnabled(false);
            }
        });
        btn_closeCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeCamera();
            }
        });
        tv_setcamera = findViewById(R.id.tv_setcamera);
        btn_stopPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPre();
            }
        });
        tv_cameraID = findViewById(R.id.tv_cameraID);
        tv_cameraID.setOnClickListener(this);
        et_cameraID = findViewById(R.id.et_cameraID);

        surfaceHolder = surfaceViewCamera2Activity.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {//surface创建时
                Toast.makeText(CameraActivity.this, "surfaceCreated", Toast.LENGTH_SHORT).show();
                //初始化相机并开始预览
                requestPermissions();
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {//surface销毁时
                if (camera != null) {
                    camera.setPreviewCallback(null);
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                }
                Toast.makeText(CameraActivity.this, "surfaceDestroyed", Toast.LENGTH_SHORT).show();


            }
        });
        //设置监听
        ibTakephoto.setOnClickListener(this);
        tvLight.setOnClickListener(this);
        ivPhotes.setOnClickListener(this);
        ibTakephoto.setOnLongClickListener(new MyOnLongClickListener());
    }

    /**
     * 关闭相机
     */
    private void closeCamera() {
        if(camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            long startTime=System.currentTimeMillis();
            camera.release();
            long endTime = System.currentTimeMillis(); //获取结束时间
            tv_closeCamera.setText("关闭相机的时间 " + (endTime - startTime) + "ms");
            System.out.println("从打开相机到开始预览 程序运行时间： "+(endTime-startTime)+"ms");
            //Toast.makeText(CameraActivity.this, ""+(endTime-startTime), Toast.LENGTH_SHORT).show();
            camera = null;

        }
    }

    /**
     * 当有多个权限需要申请的时候
     * 这里以打电话和SD卡读写权限为例
     */
    private void requestPermissions(){

        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.CAMERA);
        }

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.RECORD_AUDIO);
        }

        if (!permissionList.isEmpty()){  //申请的集合不为空时，表示有需要申请的权限
            ActivityCompat.requestPermissions(this,permissionList.toArray(new String[permissionList.size()]),1);
        }else { //所有的权限都已经授权过了

            //new TestThreead().start();
            initCamera();

        }
    }

    class TestThreead extends Thread{
        @Override
        public void run() {
            super.run();
            try {
                //lightOpenOrClose = Camera.Parameters.FLASH_MODE_ON;
                //initCamera();
                //sleep(5000);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CameraActivity.this, "开始", Toast.LENGTH_SHORT).show();
                    }
                });
                //热启动camera的时间,
                closeCameraTime = 0;
                openCameraTime = 0;
                setTime = 0;
                startPreTime = 0;
                stopPreTime = 0;
                for (int i = 0; i < 10; i++) {
                    sleep(3000);
                    switchCmaera();
                }
                long OpenCameraTime = openCameraTime / 10;
                long SetTime = setTime / 10;
                long StartPreTime = startPreTime / 10;
                long CloseCameraTime = closeCameraTime / 10;
                long StopPreTime = stopPreTime / 10;
                closeCameraTime = 0;
                openCameraTime = 0;
                setTime = 0;
                startPreTime = 0;
                stopPreTime = 0;
                //闪关灯拍照的时间
                sleep(1000);
                shutTime = 0;
                pictureTime = 0;
                rePreTime = 0;
                for (int i = 0; i < 10; i++) {
                    sleep(3000);
                    takepicture();
                }
                long ShutTime = shutTime / 10;
                long PictureTime = pictureTime / 10;
                long RePreTime = rePreTime / 10;
                shutTime = 0;
                pictureTime = 0;
                rePreTime = 0;
                //关闭闪关灯拍照的时间
                System.out.print("切换闪光灯模式");
                sleep(100);
                CAMERAID = 0;
                lightOpenOrClose = Camera.Parameters.FLASH_MODE_OFF;
                setCamera();
                sleep(1000);
                shutTime = 0;
                pictureTime = 0;
                rePreTime = 0;
                for (int i = 0; i < 10; i++) {
                    sleep(3000);
                    takepicture();
                }
                long ShutTimeOff = shutTime / 10;
                long PictureTimeOff = pictureTime / 10;
                long RePreTimeOff = rePreTime / 10;
                shutTime = 0;
                pictureTime = 0;
                rePreTime = 0;
                sleep(1000);
                //打开前置摄像头的时间
                CAMERAID = 1;
                switchCmaera();
                sleep(1000);
                closeCameraTime = 0;
                openCameraTime = 0;
                setTime = 0;
                startPreTime = 0;
                stopPreTime = 0;
                for (int i = 0; i < 10; i++) {
                    sleep(3000);
                    switchCmaera();
                }
                long OpenCameraTime1 = openCameraTime / 10;
                long SetTime1 = setTime / 10;
                long StartPreTime1 = startPreTime / 10;
                long CloseCameraTime1 = closeCameraTime / 10;
                long StopPreTime1 = stopPreTime / 10;
                //前置摄像头拍照的时间
                sleep(1000);
                shutTime = 0;
                pictureTime = 0;
                rePreTime = 0;
                for (int i = 0; i < 10; i++) {
                    sleep(3000);
                    takepicture();
                }
                long ShutTimeOff1 = shutTime / 10;
                long PictureTimeOff1 = pictureTime / 10;
                long RePreTimeOff1 = rePreTime / 10;
                sleep(1000);
                Message msg = new Message();
                msg.what = TV_OPENCAMERATIME;
                msg.obj = " OpenCameraTime: " + OpenCameraTime + " SetTime: " + SetTime + " StartPreTime: " +
                        StartPreTime + " ShutTime: " + ShutTime + " PictureTime: " + PictureTime + "RePreTime: " + RePreTime +
                        " StopPreTime: " + StopPreTime + " closeCameraTime: " + CloseCameraTime +
                        " ShutTimeOff: " + ShutTimeOff + " PictureTimeOff " + PictureTimeOff + " RePreTimeOff: " + RePreTimeOff +
                        " OpenCameraTime1: " + OpenCameraTime1 + " SetTime1: " + SetTime1 + " StartPreTime1: " + StartPreTime1 +
                        " ShutTimeOff1: " + ShutTimeOff1 + " PictureTimeOff1: " + PictureTimeOff1 + " RePreTimeOff1: " + RePreTimeOff1+
                        " StopPreTime1: " + StopPreTime1 + " CloseCameraTime1: " + CloseCameraTime1;

                handler.sendMessage(msg);
                Log.d("最终结果", msg.obj.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CameraActivity.this, "结束", Toast.LENGTH_SHORT).show();
                        btn_autotest.setEnabled(true);
                    }
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
    /**
     * 权限申请返回结果
     * @param requestCode 请求码
     * @param permissions 权限数组
     * @param grantResults  申请结果数组，里面都是int类型的数
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0){ //安全写法，如果小于0，肯定会出错了
                    for (int i = 0; i < grantResults.length; i++) {

                        int grantResult = grantResults[i];
                        if (grantResult == PackageManager.PERMISSION_DENIED){ //这个是权限拒绝
                            String s = permissions[i];
                            Toast.makeText(this,s+"权限被拒绝了",Toast.LENGTH_SHORT).show();
                        }else{ //授权成功了
                            //do Something
                            initCamera();
                            //new TestThreead().start();
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 初始化相机并开始预览
     */
    private void initCamera() {

        //打开相机
        openCamera();

        //设置相机的参数
        setCamera();
        //camera = Camera.open(0);
        //long startTime=System.currentTimeMillis();   //获取开始时间

        //打开相机
//        long endTime=System.currentTimeMillis(); //获取结束时间
//        Message msg1 = new Message();
//        msg1.what = TV_SETCAMERA;
//        msg1.obj = endTime - startTime + " ";
//        handler.sendMessage(msg1);

        try {
            long startTime=System.currentTimeMillis();
            //开始预览
            //camera.setPreviewDisplay(surfaceHolder);
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
            long endTime = System.currentTimeMillis(); //获取结束时间
            startPreTime = startPreTime + endTime - startTime;
            Log.d(TAG, "startPreTime: " + (endTime - startTime));
            Message msg2 = new Message();
            msg2.what = TV_OPENTOSTARTPRETIME;
            msg2.obj = endTime - startTime + " ";
            handler.sendMessage(msg2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //camera.startPreview();
        //开始预览
        //camera.setPreviewDisplay(surfaceHolder);

        //startPre();


    }

    private static String TAG = "CameraTime";
    private void openCamera() {

        try{
            long startTime=System.currentTimeMillis();   //获取开始时间
            //打开相机
            camera = Camera.open(CAMERAID);
            long endTime=System.currentTimeMillis(); //获取结束时间
            openCameraTime = openCameraTime + endTime - startTime;
            Log.d(TAG, "openCameraTime: " + (endTime - startTime));
            Message msg1 = new Message();
            msg1.what = TV_OPENCAMERATIME;
            msg1.obj = endTime - startTime + " ";
            handler.sendMessage(msg1);
        }catch (Exception e){
            Log.d("打开相机失败", "openCamera: 打开相机失败");
            Toast.makeText(CameraActivity.this, "打开相机失败", Toast.LENGTH_SHORT).show();
        }




    }

    private void startPre(){
       // try {
            long startTime;
            long endTime;//开始预览
            startTime=System.currentTimeMillis();
            //camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
            endTime = System.currentTimeMillis(); //获取结束时间
            Message msg2 = new Message();
            msg2.what = TV_OPENTOSTARTPRETIME;
            msg2.obj = endTime - startTime + " ";
            handler.sendMessage(msg2);
            System.out.println("从打开相机到开始预览 程序运行时间： "+(endTime-startTime)+"ms");
            //Toast.makeText(CameraActivity.this, ""+(endTime-startTime), Toast.LENGTH_SHORT).show();


//        } catch (IOException e) {
//            e.printStackTrace();
//        }
         }
    private void stopPre(){
        //结束预览
        if(camera != null) {
            long startTime=System.currentTimeMillis();   //获取开始时间
            camera.stopPreview();
            long endTime=System.currentTimeMillis(); //获取结束时间
            stopPreTime = stopPreTime + endTime - startTime;
            Log.d(TAG, "stopPreTime: " + (endTime - startTime));
            Message msg1 = new Message();
            msg1.what = TV_STOPPRE;
            msg1.obj = endTime - startTime + " ";
            handler.sendMessage(msg1);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPre();
    }

    /**
     * 设置相机的参数
     */
    private void setCamera() {
        Camera.Size previewSize =getLargePreviewSize(camera);


            long startTime=System.currentTimeMillis();   //获取开始时间
            Camera.Parameters parameters = camera.getParameters();

            if(CAMERAID == 0) {
            parameters.setFlashMode(lightOpenOrClose);

            parameters.setPictureFormat(ImageFormat.JPEG);
            //parameters.setPreviewSize(previewSize.width, previewSize.height);
            //持续对焦
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);}
            //Toast.makeText(CameraActivity.this, parameters.getFocusMode(), Toast.LENGTH_SHORT).show();
            camera.setParameters(parameters);
            //打开相机
            long endTime=System.currentTimeMillis(); //获取结束时间
            setTime = setTime + endTime - startTime;
            Log.d(TAG, "setTime: "+ (endTime - startTime));
            Message msg1 = new Message();
            msg1.what = TV_SETCAMERA;
            msg1.obj = endTime - startTime + " ";
            handler.sendMessage(msg1);

        int orientation = CameraActivity.this.getResources().getConfiguration().orientation;
        Log.d(TAG, "setCamera: " + orientation);
        System.out.print("setCamera: " + orientation);
        setCameraDisplayOrientation(this,CAMERAID,camera);
//        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            camera.setDisplayOrientation(180);
//        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
//            camera.setDisplayOrientation(90);
//        }
    }
    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    public static Camera.Size getLargePreviewSize(Camera camera){
        if(camera != null){
            List<Camera.Size> sizes = camera.getParameters().getSupportedPreviewSizes();
            Camera.Size temp = sizes.get(0);
            for(int i = 1;i < sizes.size();i ++){
                if(temp.width < sizes.get(i).width)
                    temp = sizes.get(i);
            }
            return temp;
        }
        return null;
    }
    /**
     * 单拍的照片获取
     */
    class MyPictureCallback implements Camera.PictureCallback {

        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            long endTime=System.currentTimeMillis(); //获取结束时间
            pictureTime = pictureTime + (endTime-startTime);
            Log.d(TAG, "pictureTime: " + (endTime - startTime));
            System.out.println("点击拍照到回调完成 程序运行时间： "+(endTime-startTime)+"ms");
            //Toast.makeText(CameraActivity.this, ""+(endTime-startTime), Toast.LENGTH_SHORT).show();
            Message msg3 = new Message();
            msg3.what = TV_CLICKTODATAREADYTIME;
            msg3.obj = endTime - startTime + " ";
            handler.sendMessage(msg3);

            long startTime=System.currentTimeMillis(); //获取结束时间
            camera.startPreview();
            long endTime1=System.currentTimeMillis(); //获取结束时间

            rePreTime = rePreTime + endTime1 - startTime;
            Log.d(TAG, "rePreTime: " + (endTime - startTime));
            Message msg1 = new Message();
            msg1.what = TV_DATETOSTARTTIME;
            msg1.obj = endTime1 - startTime + " ";
            handler.sendMessage(msg1);
            System.out.println("重新开始预览 程序运行时间： "+(endTime1-startTime)+"ms");
           // Toast.makeText(CameraActivity.this, ""+(endTime1-startTime), Toast.LENGTH_SHORT).show();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            if (bitmap != null) {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssmm").format(new Date());
                String bitmapPath = "/sdcard/Camera1/" + timeStamp + ".png";
                //判断父目录是否存在

                Log.e("path", bitmapPath);
                bitmap = rotateMyBitmap(bitmap);
                saveBitmap(bitmapPath, bitmap);
                ivPhotes.setImageBitmap(bitmap);


            }
        }

    }

    /**
     * 旋转图片
     *
     * @param bmp
     */
    public Bitmap rotateMyBitmap(Bitmap bmp) {
        //*****旋转一下
        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        Bitmap bitmap = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);

        Bitmap nbmp2 = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);


        return nbmp2;

    }

    ;

    /**
     * 保存bitmap
     *
     * @param bitNamePath
     * @param mBitmap
     */
    public void saveBitmap(String bitNamePath, Bitmap mBitmap) {
        File f = new File(bitNamePath);
        if (!f.getParentFile().exists()) {
            //父目录不存在 创建父目录
            Log.d(TAG,"creating parent directory...");
            if (!f.getParentFile().mkdirs()) {
                Log.e(TAG,"created parent directory failed.");
                //return FLAG_FAILED;
            }
        }
        try {
            f.createNewFile();
        } catch (IOException e) {
            Toast.makeText(CameraActivity.this, "保存图片是出错", Toast.LENGTH_SHORT).show();
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 连拍
     */
    class MyOnLongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View view) {
            Log.d("onLongClick", "onLongClick: 开始");
            takePictures();
            return true;
        }
    }

    private void takePictures() {
        try {
            // 创建保存录制视频的视频文件
            File videoFile = new File("/sdcard/testvideo.3gp");
            // 创建MediaPlayer对象
            mediaRecorder = new MediaRecorder();
            mediaRecorder.reset();
            // 这两项需要放在setOutputFormat之前
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

            // Set output file format
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

            // 这两项需要放在setOutputFormat之后
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);

            mediaRecorder.setVideoSize(640, 480);
            mediaRecorder.setVideoFrameRate(30);
            //mediaRecorder.setVideoEncodingBitRate(3 * 1024 * 1024);
            //mediaRecorder.setOrientationHint(90);
            //设置记录会话的最大持续时间（毫秒）
            mediaRecorder.setMaxDuration(30 * 1000);
            //mediaRecorder.setOrientationHint(90);
            mediaRecorder.setOutputFile(videoFile.getAbsolutePath());
            // 指定使用SurfaceView来预览视频
            mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());  //①
            mediaRecorder.prepare();
            // 开始录制
            mediaRecorder.start();
            handler.sendEmptyMessageDelayed(TAKEPICTURES, 5000);
            System.out.println("---recording---");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //handler.sendEmptyMessage(TAKEPICTURES);
        //camera.setPreviewCallback(null);
    }

    /**
     * 获取预览的图片
     */
    private void getPrePicture() {
        camera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] bytes, Camera camera) {
                if (allreadyGetPictureNum >= 10) {
                    camera.setPreviewCallback(null);
                    return;
                }
                Log.d("allreadyGetPictureNum", "onPreviewFrame: " + allreadyGetPictureNum);
                allreadyGetPictureNum++;
                //Toast.makeText(CameraActivity.this, "连拍", Toast.LENGTH_SHORT).show();
                Log.e("连拍", "获取");
                Camera.Size size = camera.getParameters().getPreviewSize();
                try {
                    YuvImage image = new YuvImage(bytes, ImageFormat.NV21, size.width, size.height, null);
                    if (image != null) {
                        //Log.e("连拍","获取连拍失败");
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, stream);

                        Bitmap bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());

                        //**********************
                        //因为图片会放生旋转，因此要对图片进行旋转到和手机在一个方向上
                        bitmap = rotateMyBitmap(bitmap);
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssmm").format(new Date());
                        String bitmapPath = "/sdcard/" + timeStamp + ".png";
                        Log.e("path", bitmapPath);

                        saveBitmap(bitmapPath, bitmap);
                        //Thread.sleep(150);
                        ivPhotes.setImageBitmap(bitmap);

                        stream.close();
                    } else {
                        Log.e("连拍", "获取连拍失败");
                    }
                } catch (Exception ex) {
                    Log.e("Sys", "Error:" + ex.getMessage());
                }
                /*Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (bitmap != null) {

                    Toast.makeText(CameraActivity.this, "连拍成功", Toast.LENGTH_SHORT).show();
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String bitmapPath = "/sdcard/" + timeStamp + ".png";
                    Log.e("path",bitmapPath);

                    saveBitmap(bitmapPath,bitmap);

                    //ivPhotes.setImageBitmap(bitmap);


                }else{
                    Log.e("连拍","获取连拍失败");
                }*/
            }
        });
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-12-07 15:40:19 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == ibTakephoto) {
            // Handle clicks for ibTakephoto
            //拍照
            takepicture();

        } else if (v == ivPhotes) {

        } else if (v == tvLight) {
            //设置闪光灯
            if (lightOpenOrClose.equals(Camera.Parameters.FLASH_MODE_ON)) {//闪光灯关闭
                lightOpenOrClose = Camera.Parameters.FLASH_MODE_OFF;
                //更新相机的参数
                setCamera();
                handler.sendEmptyMessage(LIGHTCLOSE);


            } else {//闪光灯打开
                lightOpenOrClose = Camera.Parameters.FLASH_MODE_ON;
                //更新相机的参数
                setCamera();
                handler.sendEmptyMessage(LIGHTOPEN);
            }

        }else if(v == tv_cameraID) {
            String s = et_cameraID.getText().toString();
            try{
                CAMERAID = Integer.parseInt(s);
            }catch (Exception e){
                Toast.makeText(CameraActivity.this, "请输入0 1 2", Toast.LENGTH_SHORT).show();
            }

            if(camera != null) {
                switchCmaera();

                //tv_cameraID.setText("摄像头");

            }
        }
    }

    private void switchCmaera() {
        //更新相机的参数
        camera.setPreviewCallback(null);

        long startTime1=System.currentTimeMillis();   //获取开始时间
        camera.stopPreview();
        long endTime1=System.currentTimeMillis(); //获取结束时间
        stopPreTime = stopPreTime + endTime1 - startTime1;
        Log.d(TAG, "stopPreTime: " + (endTime1 - startTime1));


        long startTime=System.currentTimeMillis(); //获取结束时间
        camera.release();
        long endTime=System.currentTimeMillis(); //获取结束时间
        //System.out.println("点击拍照到快门完成 程序运行时间： "+(endTime-startTime)+"ms");
        closeCameraTime = closeCameraTime + (endTime-startTime) ;
        Log.d(TAG, "closeCameraTime: " + (endTime - startTime));

        camera = null;
        initCamera();
    }

    //拍照
    private void takepicture() {
        startTime=System.currentTimeMillis();   //获取开始时间

        //其中第一个参数是按下快门的回调，第二个参数是原始图片的回调，第三个参数是经压缩处理后比较小的jpeg图片的回调。
        camera.takePicture(new MyShutterCallback(), null, new MyPictureCallback());
    }

    class MyShutterCallback implements Camera.ShutterCallback{

        @Override
        public void onShutter() {
            long endTime=System.currentTimeMillis(); //获取结束时间
            System.out.println("点击拍照到快门完成 程序运行时间： "+(endTime-startTime)+"ms");
            shutTime = shutTime + (endTime-startTime);
            Log.d(TAG, "shutTime: " + (endTime - startTime));
            //Toast.makeText(CameraActivity.this, ""+(endTime-startTime), Toast.LENGTH_SHORT).show();
            Message msg3 = new Message();
            msg3.what = TV_SHUT;
            msg3.obj = endTime - startTime + " ";
            handler.sendMessage(msg3);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //初始化
        findViews();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        surfaceViewCamera2Activity = null;
        surfaceHolder = null;
        if (mediaRecorder != null) {
            // 停止录制
            mediaRecorder.stop();//先停止
            mediaRecorder.reset(); // 在重置mediarecorder
            // 释放资源
            mediaRecorder.release();//释放mediarecorder
            mediaRecorder = null;

        }
        handler.removeCallbacksAndMessages(null);
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }


    }
}
