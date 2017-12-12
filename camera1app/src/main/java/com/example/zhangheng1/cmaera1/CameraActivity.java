package com.example.zhangheng1.cmaera1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.RotateAnimation;
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
import java.util.Date;


public class CameraActivity extends Activity implements View.OnClickListener {

    private LinearLayout llTitle;
    private TextView tvLight;
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
            }
        }
    };


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

        surfaceHolder = surfaceViewCamera2Activity.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {//surface创建时
                Toast.makeText(CameraActivity.this, "surfaceCreated", Toast.LENGTH_SHORT).show();
                //初始化相机并开始预览
                intiCamera();
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
     * 初始化相机并开始预览
     */
    private void intiCamera() {

        //打开相机
        camera = Camera.open(0);
        //设置相机的参数
        setCamera();

        try {
            //开始预览
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * 设置相机的参数
     */
    private void setCamera() {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setFlashMode(lightOpenOrClose);
        parameters.setPictureFormat(ImageFormat.JPEG);
        //parameters.setPreviewSize(800, 1200);
        //持续对焦
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        //Toast.makeText(CameraActivity.this, parameters.getFocusMode(), Toast.LENGTH_SHORT).show();

        camera.setParameters(parameters);
        int orientation = CameraActivity.this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            camera.setDisplayOrientation(180);
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            camera.setDisplayOrientation(90);
        }
    }

    /**
     * 单拍的照片获取
     */
    class MyPictureCallback implements Camera.PictureCallback {

        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {

            camera.startPreview();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            if (bitmap != null) {

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssmm").format(new Date());
                String bitmapPath = "/sdcard/" + timeStamp + ".png";
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

        }
    }

    //拍照
    private void takepicture() {
        camera.takePicture(null, null, new MyPictureCallback());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
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
