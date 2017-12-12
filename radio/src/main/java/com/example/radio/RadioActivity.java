package com.example.radio;

import android.app.Activity;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class RadioActivity extends Activity implements View.OnClickListener {

    private SurfaceView svRadio;
    private Button vbStartradio;
    private ImageView iv_photes;
    private SurfaceHolder surfaceHolder;
    private MediaRecorder mediaRecorder;
    private String mFileName;
    private Camera camera;
    private static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    /**
     * 是否开始录像
     */
    private Boolean start;
    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-12-11 09:31:51 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        //mFileName = getExternalCacheDir().getAbsolutePath();

        svRadio = (SurfaceView)findViewById(R.id.sv_radio);
        vbStartradio = (Button)findViewById(R.id.btn_startradio );
        iv_photes = findViewById(R.id.iv_photes);

        start = true;//初始化是否开始录像的变量
        surfaceHolder = svRadio.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {//surfaceview创建的时候
                //开始预览
                startPreRadio();

            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {//surfaceview销毁的时候
                stopRecording();
                if (camera != null) {
                    camera.setPreviewCallback(null);
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                }
            }
        });

        vbStartradio.setOnClickListener( this );
    }

    //开始预览
    private void startPreRadio() {

        camera = Camera.open(0);
        setCamera();
        /*try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }*/


    }

    /**
     * 设置相机的参数
     */
    private void setCamera() {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setFlashMode( Camera.Parameters.FLASH_MODE_ON);
        parameters.setPictureFormat(ImageFormat.JPEG);
        //parameters.setPreviewSize(800, 1200);
        //持续对焦
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        //Toast.makeText(CameraActivity.this, parameters.getFocusMode(), Toast.LENGTH_SHORT).show();

        camera.setParameters(parameters);
        int orientation = RadioActivity.this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            camera.setDisplayOrientation(180);
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            camera.setDisplayOrientation(90);
        }
    }
    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-12-11 09:31:51 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if ( v == vbStartradio ) {
            Log.d("vbStartradio: 按键按下", "onClick: 按键按下");
            // Handle clicks for vbStartradio
           if(start){
               //开始录像
               Log.e("onClick: 开始录像", "onClick: 开始录像");
               Toast.makeText(RadioActivity.this, "开始录像", Toast.LENGTH_SHORT).show();
               String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssmm").format(new Date());
               mFileName = "/sdcard/" + timeStamp + ".mp4";
               startRecording();
               start = false;
           }else{
               //停止录像
               Log.e("onClick: 停止录像", "onClick: 停止录像");
               System.out.print("onClick: 停止录像");
               Toast.makeText(RadioActivity.this, "停止录像", Toast.LENGTH_SHORT).show();
               stopRecording();
               start = false;
            }
        }
    }


    /**
     * 开始录像
     */
    private void startRecording() {
        camera.unlock();

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setCamera(camera);

        //设置的顺序尽量按照下面的顺序否则会出错
        //视频源
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        //mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        //音频源
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        //输出格式
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        //音频编码
        mediaRecorder.setAudioEncoder( MediaRecorder.AudioEncoder.AMR_NB);
        //视频编码
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);

        //视频输出文件
        mediaRecorder.setOutputFile(mFileName);
        //设置视频大小
        mediaRecorder.setVideoSize(1920, 1080);
        //设置视频码率
        mediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
        //设置视频的帧，
        mediaRecorder.setVideoFrameRate(24);
        //旋转图像
        mediaRecorder.setOrientationHint(90);


        mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            Log.e("RadioActivity", "prepare() failed");
        }

        mediaRecorder.start();
    }

    /**
     * 暂停录像
     */
    private void stopRecording() {
        if(mediaRecorder == null)
            return;
        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();

        camera.lock();
        mediaRecorder = null;
        //保存连拍图片
        saveImages();
    }

    /**
     * 保存连拍图片
     */
    private void saveImages() {
        //https://github.com/wseemann/FFmpegMediaMetadataRetriever

        final FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
        mmr.setDataSource(mFileName);
        //mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
        //mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
        /*//创建MediaMetadataRetriever对象
        final MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        //设置资源位置
        //绑定资源
        mmr.setDataSource(mFileName);*/
        //获取第一帧图像的bitmap对象
        Bitmap bitmap1 = mmr.getFrameAtTime(0);
        //加载到ImageView控件上
        iv_photes.setImageBitmap(bitmap1);

        //连拍视频的张数
        int picturenum = 0;
        //得到视频时长毫秒
        //String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        for (int i = 0; i < 3000; i = i + 50,picturenum++) {
            Log.e("saveImages: for", "saveImages: for 进入");
            //System.out.print("saveImages: for 进入");
            final int finalI = i;
            final int pictureNum = picturenum;
            cachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    //参数为微妙
                    Bitmap bitmap = mmr.getFrameAtTime(finalI*1000,FFmpegMediaMetadataRetriever.OPTION_CLOSEST);
                    if (bitmap != null) {
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssmm").format(new Date());
                        String bitmapPath = "/sdcard/" + timeStamp   + "_" + pictureNum + ".png";


                        saveBitmap(bitmapPath, bitmap);
                        Log.e("saveImages: for", "saveImages: for 成功保存" + finalI + timeStamp);
                        //System.out.print("saveImages: for 成功保存");
                    }
                }
            });

        }

    }






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
            Log.e("保存图片是出错", "saveBitmap:保存图片是出错 ");
            //Toast.makeText(RadioActivity.this, "保存图片是出错", Toast.LENGTH_SHORT).show();
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            Log.e("保存图片是出错", "saveBitmap:保存图片是出错 ");
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            Log.e("保存图片是出错", "saveBitmap:保存图片是出错 ");
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            Log.e("保存图片是出错", "saveBitmap:保存图片是出错 ");
            e.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);
        //初始化
        findViews();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopRecording();
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRecording();
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }
}
