package com.example.zhangheng1.camera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.MeteringRectangle;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import wseemann.media.FFmpegMediaMetadataRetriever;

import static android.support.v4.math.MathUtils.clamp;

public class CameraActivity extends Activity implements View.OnClickListener {

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    ///为了使照片竖直显示
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private static String TAG = "CameraActivity";
    private LinearLayout llTitle;
    private TextView tvLight;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Button btnTakephoto;
    private Button btn_startrecord;
    private ImageView ivPhotes;
    private Handler handler1, handler2, mainHandler;
    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder preCameraCaptureRequest;
    private ImageReader imageReader;
    /**
     * 图片的地址
     */
    private ArrayList<String> picturesPath;
    private CameraManager cameraManager;
    /**
     * 摄像头
     */
    private String mCameraID;

    private CameraCaptureSession mCameraCaptureSession;

    /**
     * 预览的build
     */
    private CaptureRequest.Builder previewRequestBuilder;
    /**
     * 拍照的build
     */
    private CaptureRequest.Builder takePictureRequestBuilder;
    /**
     * 录像的操作类
     */
    private MediaRecorder mediaRecorder;
    /**
     * 视频输出地址
     */
    private String mFileName;
    /**
     * 是否已经开始录像
     */
    private boolean isStartRecord = false;
    private CaptureRequest.Builder mRecordBuilder;


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-12-06 10:38:41 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        llTitle = (LinearLayout) findViewById(R.id.ll_title);
        tvLight = (TextView) findViewById(R.id.tv_light);
        btnTakephoto = (Button) findViewById(R.id.btn_takephoto);
        ivPhotes = (ImageView) findViewById(R.id.iv_photes);
        btn_startrecord = findViewById(R.id.btn_startrecord);


        picturesPath = new ArrayList<>();
        //surfaceview的初始化
        surfaceView = (SurfaceView) findViewById(R.id.sv_precamera);
        surfaceView.setOnTouchListener(new MyOnTouchListener());
        surfaceHolder = surfaceView.getHolder();
        //保持常亮
        surfaceView.setKeepScreenOn(true);
        //surfaceview的回调
        surfaceHolder.addCallback(new SurfaceHolderCallback());
        //imageReader = ImageReader.newInstance(100, 200, ImageFormat.YUV_420_888, 30);
        //图片准备好
        //imageReader.setOnImageAvailableListener(new MyOnImageAvailableListener(), handler1);
        //设置监听
        btnTakephoto.setOnClickListener(this);
        btn_startrecord.setOnClickListener(this);
        btnTakephoto.setOnLongClickListener(new MyOnLongClickListener());
        ivPhotes.setOnClickListener(this);
        tvLight.setOnClickListener(this);
    }

    /**
     * surfaceview 的点击监听
     */
    class MyOnTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            // 先取相对于view上面的坐标
            double x = motionEvent.getX(), y = motionEvent.getY(), tmp;

            // 取出来的图像如果有旋转角度的话，则需要将宽高交换下
            int realPreviewWidth = 1080, realPreviewHeight = 1920;

            // 计算摄像头取出的图像相对于view放大了多少，以及有多少偏移
            double imgScale = 1.0, verticalOffset = 0, horizontalOffset = 0;
            if (realPreviewHeight * view.getWidth() > realPreviewWidth * view.getHeight()) {
                imgScale = view.getWidth() * 1.0 / realPreviewWidth;
                verticalOffset = (realPreviewHeight - view.getHeight() / imgScale) / 2;
            } else {
                imgScale = view.getHeight() * 1.0 / realPreviewHeight;
                horizontalOffset = (realPreviewWidth - view.getWidth() / imgScale) / 2;
            }

           // 将点击的坐标转换为图像上的坐标
            x = x / imgScale + horizontalOffset;
            y = y / imgScale + verticalOffset;

            tmp = x; x = y; y = 1920 - tmp;

            // 计算取到的图像相对于裁剪区域的缩放系数，以及位移
            Rect cropRegion = previewRequestBuilder.get(CaptureRequest.SCALER_CROP_REGION);
            if (null == cropRegion) {
                Log.e(TAG, "can't get crop region");
                //cropRegion = mActiveArraySize;
                Log.d(TAG, "onTouch: null == cropRegion");
            }

            int cropWidth = cropRegion.width(), cropHeight = cropRegion.height();
            if (1920 * cropWidth > 1080 * cropHeight) {
                imgScale = cropHeight * 1.0 / 1920;
                verticalOffset = 0;
                horizontalOffset = (cropWidth - imgScale * 1080) / 2;
            } else {
                imgScale = cropWidth * 1.0 / 1080;
                horizontalOffset = 0;
                verticalOffset = (cropHeight - imgScale * 1920) / 2;
            }

            // 将点击区域相对于图像的坐标，转化为相对于成像区域的坐标
            x = x * imgScale + horizontalOffset + cropRegion.left;
            y = y * imgScale + verticalOffset + cropRegion.top;

            double tapAreaRatio = 0.1;
            final Rect rect = new Rect();
            rect.left = clamp((int) (x - tapAreaRatio / 2 * cropRegion.width()), 0, cropRegion.width());
            rect.right = clamp((int) (x + tapAreaRatio / 2 * cropRegion.width()), 0, cropRegion.width());
            rect.top = clamp((int) (y - tapAreaRatio / 2 * cropRegion.height()), 0, cropRegion.height());
            rect.bottom = clamp((int) (y + tapAreaRatio / 2 * cropRegion.height()), 0, cropRegion.height());

            closePreviewSession();

            try {
                // 创建预览需要的CaptureRequest.Builder
                previewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                // 将SurfaceView的surface作为CaptureRequest.Builder的目标
                previewRequestBuilder.addTarget(surfaceHolder.getSurface());
                // 创建CameraCaptureSession，该对象负责管理处理预览请求和拍照请求
                mCameraDevice.createCaptureSession(Arrays.asList(surfaceHolder.getSurface(), imageReader.getSurface()), new CameraCaptureSession.StateCallback() // ③
                {
                    @Override
                    public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                        if (null == mCameraDevice) {
                            //Toast.makeText(MainActivity.this, "mCameraDevice is null", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onConfigured: ");
                            return;
                        }
                        // 当摄像头已经准备好时，开始显示预览
                        mCameraCaptureSession = cameraCaptureSession;

                        previewRequestBuilder.set(CaptureRequest.CONTROL_AF_REGIONS, new MeteringRectangle[] {new MeteringRectangle(rect, 1000)});
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AE_REGIONS, new MeteringRectangle[] {new MeteringRectangle(rect, 1000)});
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_START);
                            CaptureRequest captureRequest = previewRequestBuilder.build();
                        try {
                            mCameraCaptureSession.setRepeatingRequest(captureRequest, null , handler1);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                        //Toast.makeText(CameraActivity.this, "配置失败", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onConfigureFailed: 配置失败");
                    }
                }, handler1);

            } catch (CameraAccessException e) {
                e.printStackTrace();
            }




            return true;
        }
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-12-06 10:38:41 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_takephoto:


                //imageReader.close();
                //imageReader = ImageReader.newInstance(1080, 1920, ImageFormat.JPEG, 1);

                //imageReader.setOnImageAvailableListener(new MyOnImageAvailableListener(), mainHandler);
                //拍照
                takePicture();

                break;
            case R.id.iv_photes:
                //进入照片预览
                showPictures();
                break;
            case R.id.tv_light:

                break;
            case R.id.btn_startrecord:
                if (isStartRecord) {
                    Log.d(TAG, "onClick: 停止录像");
                    Toast.makeText(CameraActivity.this, "停止录像,视频地址"+mFileName , Toast.LENGTH_SHORT).show();
                    //停止录像
                    stopRecord();
                    /**
                     * 保存连拍图片25张
                     */
                    //savaImages();
                    isStartRecord = false;
                } else {
                    Log.d(TAG, "onClick: 开始录像");
                    Toast.makeText(CameraActivity.this, "开始录像", Toast.LENGTH_SHORT).show();
                    //开始录像
                    startRecord();
                    isStartRecord = true;
                }

                break;
        }
    }

    /**
     * 停止录像
     */
    private void stopRecord() {


        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        takepreCameraDisplay();
    }

    /**
     * 关闭预览
     */
    private void closePreviewSession() {
        if (mCameraCaptureSession != null) {
            try {
                mCameraCaptureSession.stopRepeating();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            mCameraCaptureSession.close();
            mCameraCaptureSession = null;
        }
    }

    /**
     * 设置mediarecord
     *
     * @throws IOException
     */
    private void setUpMediaRecorder() throws IOException {
        final Activity activity = this;
        if (null == activity) {
            return;
        }
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssmm").format(new Date());
        mFileName = "/sdcard/" + timeStamp + ".mp4";
        mediaRecorder.setOutputFile(mFileName);
        mediaRecorder.setVideoEncodingBitRate(10000000);
        mediaRecorder.setVideoFrameRate(25);
        mediaRecorder.setVideoSize(1920, 1080);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
        mediaRecorder.setOrientationHint(ORIENTATIONS.get(rotation));
        mediaRecorder.prepare();
    }

    /**
     * 开始录像
     */
    private void startRecordingVideo() {

        try {
            mRecordBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        List<Surface> surfaces = new ArrayList<>();

        // Set up Surface for the camera preview
        surfaces.add(surfaceHolder.getSurface());
        mRecordBuilder.addTarget(surfaceHolder.getSurface());
        // Set up Surface for the MediaRecorder
        Surface recorderSurface = mediaRecorder.getSurface();
        if (recorderSurface == null) {
            Log.d(TAG, "startRecordingVideo: null");
        }
        surfaces.add(recorderSurface);
        mRecordBuilder.addTarget(recorderSurface);

        // Start a capture session
        // Once the session starts, we can update the UI and start recording
        try {
            mCameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    mCameraCaptureSession = cameraCaptureSession;

                    updatePreview();
                    mediaRecorder.start();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {


                }
            }, handler1);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }


    }

    /**
     * Update the camera preview. {@link #startPreview()} needs to be called in advance.
     */
    private void updatePreview() {
        if (null == mCameraDevice) {
            return;
        }
        try {
            setUpCaptureRequestBuilder(mRecordBuilder);
            HandlerThread thread = new HandlerThread("CameraPreview");
            thread.start();
            mCameraCaptureSession.setRepeatingRequest(mRecordBuilder.build(), null, handler1);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void setUpCaptureRequestBuilder(CaptureRequest.Builder builder) {
        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
    }

    /**
     * 开始录像
     */
    private void startRecord() {

        //关闭预览
        closePreviewSession();
        try {
            //设置参数
            setUpMediaRecorder();
            startRecordingVideo();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * SurfaceHolder的回调
     * 当surfaceview准备好的时候初始化相机
     */
    class SurfaceHolderCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            Log.d(TAG, "surfaceCreated: ");

            requestPermissions();
            //初始化相机
            //initCamera();
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
        }
    }

    private int num = 0;

    /**
     * 初始化相机
     */
    @SuppressLint("MissingPermission")
    private void initCamera() {
        HandlerThread handlerThread = new HandlerThread("Camera2");
        handlerThread.start();
        handler1 = new Handler(handlerThread.getLooper());
        mainHandler = new Handler(getMainLooper());
        mCameraID = "" + CameraCharacteristics.LENS_FACING_FRONT;//后摄像头


        imageReader = ImageReader.newInstance(1080, 1920, ImageFormat.JPEG, 50);

        imageReader.setOnImageAvailableListener(new MyOnImageAvailableListener(), mainHandler);

        //获取摄像头管理
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {

            //打开摄像头
            Log.d("MyCamera", "打开摄像头成功");


            cameraManager.openCamera(mCameraID, stateCallback, mainHandler);
            CameraCharacteristics characteristics
                    = cameraManager.getCameraCharacteristics(mCameraID);
            Integer integer = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
            Log.d(TAG, "initCamera: " + integer.toString());

        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.d("MyCamera", "打开摄像头失败");
        }
    }

    /**
     * 当有多个权限需要申请的时候
     * 这里以打电话和SD卡读写权限为例
     */
    private void requestPermissions(){

        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
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

            initCamera();
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
                        }
                    }
                }
                break;
            default:
                break;
        }
    }




    /**
     * 保存图片的线程
     */
    private class ImageSaver extends Thread {
        /**
         * The JPEG image
         */
        private final byte[] bytes;
        /**
         * The file we save the image into.
         */
        private final String filepath;

        ImageSaver(byte[] bytes, String filepath) {
            this.bytes = bytes;
            this.filepath = filepath;
        }

        @Override
        public void run() {
            File mFile = new File(filepath);
            try {
                mFile.createNewFile();
            } catch (IOException e) {
                Log.d(TAG, "saveBitmap: 保存图片是出错");
            }
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(mFile);
                output.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != output) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
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
            Log.d(TAG, "saveBitmap: 保存图片是出错");
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
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
     * 图片数据准备好的监听，获取图片
     */
    class MyOnImageAvailableListener implements ImageReader.OnImageAvailableListener {

        @Override
        public void onImageAvailable(ImageReader imageReader) {

            //mCameraDevice.close();
            //mSurfaceView.setVisibility(View.GONE);
            // 拿到拍照照片数据
            Image image = imageReader.acquireNextImage();
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);//由缓冲区存入字节数组
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            num++;
            if(num > 100){
                Toast.makeText( CameraActivity.this, "照片够多了", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d(TAG, "onImageAvailable: saveimage " + num);
            if (bitmap != null) {

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssmm").format(new Date());
                String bitmapPath = "/sdcard/" + timeStamp + num + ".png";

                new ImageSaver(bytes, bitmapPath).start();
                picturesPath.add(bitmapPath);
                ivPhotes.setImageBitmap(bitmap);
                Log.d(TAG, "onImageAvailable: saveimage in " + bitmapPath + num);
            } else {
                Log.d(TAG, "onImageAvailable: null");
            }
        }
    }

    /**
     * 长按连拍
     */
    class MyOnLongClickListener implements View.OnLongClickListener {

        @Override
        public boolean onLongClick(View view) {
            //设置最大张数限制

            //imageReader.close();
            //imageReader = ImageReader.newInstance(1080, 1920, ImageFormat.JPEG, 30);

            //imageReader.setOnImageAvailableListener(new MyOnImageAvailableListener(), mainHandler);
            //连拍
            takePictures();
            return true;
        }
    }

    /**
     * 连拍
     */
    private void takePictures() {
        try {
            takePictureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            // 将imageReader的surface作为CaptureRequest.Builder的目标
            takePictureRequestBuilder.addTarget(imageReader.getSurface());
            // 自动对焦
            takePictureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            // 自动曝光
            takePictureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            // 获取手机方向
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            // 根据设备方向计算设置照片的方向
            takePictureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            //拍照
            CaptureRequest mCaptureRequest = takePictureRequestBuilder.build();

            List<CaptureRequest> list = new LinkedList<>();
            for (int i = 0; i < 30; i++) {
                list.add(mCaptureRequest);
            }

            mCameraCaptureSession.captureBurst(list,null,handler1);

            //mCameraCaptureSession.capture(mCaptureRequest, null, handler1);
            // mCameraCaptureSession.setRepeatingRequest(mCaptureRequest, null, handler1);
            //mCameraCaptureSession.setRepeatingBurst(list,null,handler1);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }
    /**
     * 保存连拍图片
     */
    private void savaImages() {
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
        ivPhotes.setImageBitmap(bitmap1);

        //连拍视频的张数
        int pictureNum = 0;
        for(int i = 0; i < 1000; i = i + 40,pictureNum++){
            Bitmap bitmap = mmr.getFrameAtTime(i*1000,FFmpegMediaMetadataRetriever.OPTION_CLOSEST);
            if (bitmap != null) {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssmm").format(new Date());
                String bitmapPath = "/sdcard/" + timeStamp   + "_" + pictureNum + ".png";
                saveBitmap(bitmapPath, bitmap);
                Log.e("saveImages: for", "saveImages: for 成功保存" + pictureNum + timeStamp);
                //System.out.print("saveImages: for 成功保存");
            }

        }
    }


    /**
     * 拍照
     */
    private void takePicture() {
        try {
            takePictureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            // 将imageReader的surface作为CaptureRequest.Builder的目标
            takePictureRequestBuilder.addTarget(imageReader.getSurface());
            // 自动对焦
            takePictureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            //takePictureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,1);
            // 自动曝光
            takePictureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            // 获取手机方向
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            // 根据设备方向计算设置照片的方向
            takePictureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            //拍照
            CaptureRequest mCaptureRequest = takePictureRequestBuilder.build();

            /*List<CaptureRequest> list = new LinkedList<>();
            for (int i = 0; i < 30; i++) {
                list.add(mCaptureRequest);
            }*/

            //mCameraCaptureSession.captureBurst(list,null,handler1);

            mCameraCaptureSession.capture(mCaptureRequest, null, handler1);
            // mCameraCaptureSession.setRepeatingRequest(mCaptureRequest, null, handler1);
            //mCameraCaptureSession.setRepeatingBurst(list,null,handler1);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }


    //展示图片，进入到图片列表
    private void showPictures() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra("pictures", this.picturesPath);
        intent.setClass(CameraActivity.this, GalleryActivity.class);
        startActivity(intent);
    }


    /**
     * 摄像头创建监听
     */
    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {//打开摄像头

            //Toast.makeText(CameraActivity.this, "打开摄像头成功", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onOpened: 打开摄像头成功");
            //当打开摄像头成功后开始预览
            mCameraDevice = camera;
            takepreCameraDisplay();


        }

        @Override
        public void onDisconnected(CameraDevice camera) {//关闭摄像头
            //Toast.makeText(CameraActivity.this, "关闭摄像头成功", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onError(CameraDevice camera, int error) {//发生错误
            //Toast.makeText(CameraActivity.this, "摄像头开启失败", Toast.LENGTH_SHORT).show();
        }
    };


    /**
     * 开始预览
     */
    private void takepreCameraDisplay() {
        try {
            // 创建预览需要的CaptureRequest.Builder
            previewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            // 将SurfaceView的surface作为CaptureRequest.Builder的目标
            previewRequestBuilder.addTarget(surfaceHolder.getSurface());
            // 创建CameraCaptureSession，该对象负责管理处理预览请求和拍照请求
            mCameraDevice.createCaptureSession(Arrays.asList(surfaceHolder.getSurface(), imageReader.getSurface()), new CameraCaptureSession.StateCallback() // ③
            {
                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    if (null == mCameraDevice) {
                        //Toast.makeText(MainActivity.this, "mCameraDevice is null", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onConfigured: ");
                        return;
                    }
                    // 当摄像头已经准备好时，开始显示预览
                    mCameraCaptureSession = cameraCaptureSession;
                    try {
                        // 自动对焦
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        // 打开闪光灯
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH);
                        // 显示预览
                        CaptureRequest previewRequest = previewRequestBuilder.build();
                        mCameraCaptureSession.setRepeatingRequest(previewRequest, null, handler1);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                    //Toast.makeText(CameraActivity.this, "配置失败", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onConfigureFailed: 配置失败");
                }
            }, handler1);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != mCameraDevice) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去掉标题栏 ,必须放在setContentView之前
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_camera);
        // 设置横屏显示
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // 设置全屏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        // 选择支持半透明模式,在有surfaceview的activity中使用。
//        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        findViews();



    }

}
