package com.example.zhangheng1.mycamera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.params.MeteringRectangle;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutput;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static int LIGHTOPENORCLOSE = CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH;

    ///为了使照片竖直显示
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private TextView tv_light;
    private ImageButton ib_takephoto;
    private ImageView iv_photes;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private CameraManager mCameraManager;//摄像头管理器
    private Handler childHandler, mainHandler, childHandler1;
    private String mCameraID;//摄像头Id 0 为后  1 为前
    private ImageReader mImageReader;
    private CameraCaptureSession mCameraCaptureSession;
    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder previewRequestBuilder;
    private CaptureRequest.Builder picturesRequestBuilder;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private static int LIGHTOPEN = 0;
    private static int LIGHTCLOSE = 1;
    private static int PICTURES = 2;
    private ArrayList<String> picturesPath = new ArrayList<>();
    private String bitmapPath = null;
    private int name = 0;

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == LIGHTCLOSE) {
                tv_light.setText("闪光灯关闭");
            } else if (msg.what == LIGHTOPEN) {
                tv_light.setText("闪光灯打开");
            } else if (msg.what == PICTURES) {
                Log.e("连拍", "连拍");
                takepictures();
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initVIew();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        myHandler.removeCallbacksAndMessages(null);

        if (null != mCameraDevice) {
            mCameraDevice.close();
            MainActivity.this.mCameraDevice = null;
        }
        ;
    }

    /**
     * 初始化
     */
    private void initVIew() {

        //mSurfaceView

        tv_light = (TextView) findViewById(R.id.tv_light);
        ib_takephoto = findViewById(R.id.ib_takephoto);
        iv_photes = findViewById(R.id.iv_photes);
        ib_takephoto.setOnClickListener(this);
        ib_takephoto.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(MainActivity.this, "连拍", Toast.LENGTH_SHORT).show();
                takePictures();
                return true;
            }
        });
        iv_photes.setOnClickListener(this);
        tv_light.setOnClickListener(this);
        initsurfaceView();

    }

    private void initsurfaceView() {
        Toast.makeText(MainActivity.this, "initsurfaceView", Toast.LENGTH_SHORT).show();
        mSurfaceView = (SurfaceView) findViewById(R.id.surface_view_camera2_activity);

        mSurfaceView.setOnClickListener(this);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setKeepScreenOn(true);
        // mSurfaceView添加回调
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) { //SurfaceView创建
                Toast.makeText(MainActivity.this, "surfaceCreated", Toast.LENGTH_SHORT).show();
                // 初始化Camera
                initCamera2();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }


            @Override
            public void surfaceDestroyed(SurfaceHolder holder) { //SurfaceView销毁
                Toast.makeText(MainActivity.this, "surfaceDestroyed", Toast.LENGTH_SHORT).show();
                // 释放Camera资源
                /*if (null != mCameraDevice) {
                    mCameraDevice.close();
                    MainActivity.this.mCameraDevice = null;
                }*/
            }
        });
    }


    /**
     * 连拍
     */
    private void takePictures() {


        //mImageReader = ImageReader.newInstance(1080, 1920, ImageFormat.YUV_420_888,30);

        for (int i = 0; i < 10; i++) {
            myHandler.sendEmptyMessageDelayed(PICTURES, 100);
        }

    }

    private void takepictures() {
        try {

            // picturesRequestBuilder，该对象负责管理处理预览请求和拍照请求
            picturesRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            // 将imageReader的surface作为CaptureRequest.Builder的目标
            picturesRequestBuilder.addTarget(mImageReader.getSurface());
            // 自动对焦
            picturesRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            // 自动曝光
            picturesRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, LIGHTOPENORCLOSE);
            // 获取手机方向
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            // 根据设备方向计算设置照片的方向
            picturesRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            //拍照
            CaptureRequest mCaptureRequest = picturesRequestBuilder.build();
            mCameraCaptureSession.capture(mCaptureRequest, null, childHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.surface_view_camera2_activity:
                //takePicture();
                Toast.makeText(MainActivity.this, "surface click", Toast.LENGTH_SHORT).show();
                break;
            //拍照
            case R.id.ib_takephoto:
                Toast.makeText(MainActivity.this, "拍照", Toast.LENGTH_SHORT).show();
                takePicture();

                break;
            //展示图片
            case R.id.iv_photes:
                Toast.makeText(MainActivity.this, "展示图片", Toast.LENGTH_SHORT).show();
                showpictures();
                break;
            case R.id.tv_light:
                //开启或关闭闪光灯
                Toast.makeText(MainActivity.this, "闪光灯", Toast.LENGTH_SHORT).show();
                if (LIGHTOPENORCLOSE == CaptureRequest.CONTROL_AE_MODE_OFF) {
                    LIGHTOPENORCLOSE = CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH;
                    myHandler.sendEmptyMessage(LIGHTOPEN);
                } else {
                    LIGHTOPENORCLOSE = CaptureRequest.CONTROL_AE_MODE_OFF;
                    myHandler.sendEmptyMessage(LIGHTCLOSE);

                }

                break;

        }

    }

    //展示图片，进入到图片列表
    private void showpictures() {
        Intent intent = new Intent();
        IntentFilter intentFilter = new IntentFilter();

        intent.putStringArrayListExtra("pictures", this.picturesPath);

        intent.setClass(MainActivity.this, GalleryActivity.class);
        startActivity(intent);
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
            Toast.makeText(MainActivity.this, "保存图片是出错", Toast.LENGTH_SHORT).show();
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
     * 初始化Camera2
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initCamera2() {
        Log.d("MyCamera", "打开摄像头");
        HandlerThread handlerThread = new HandlerThread("Camera2");
        handlerThread.start();
        childHandler = new Handler(handlerThread.getLooper());
        childHandler1 = new Handler(handlerThread.getLooper());
        mainHandler = new Handler(getMainLooper());
        mCameraID = "" + CameraCharacteristics.LENS_FACING_FRONT;//后摄像头
        mImageReader = ImageReader.newInstance(1080, 1920, ImageFormat.JPEG, 30);
        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() { //可以在这里处理拍照得到的临时照片 例如，写入本地
            @Override
            public void onImageAvailable(ImageReader reader) {
                //mCameraDevice.close();
                //mSurfaceView.setVisibility(View.GONE);

                // 拿到拍照照片数据
                Image image = reader.acquireNextImage();
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);//由缓冲区存入字节数组
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (bitmap != null) {
                    bitmapPath = "/sdcard/" + name + ".png";
                    name++;
                    saveBitmap(bitmapPath, bitmap);
                    picturesPath.add(bitmapPath);
                    iv_photes.setImageBitmap(bitmap);

                }
            }
        }, mainHandler);
        //获取摄像头管理
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 25);
                //Log.d("MyCamera","打开摄像头失败");
                //return;
            }
            //打开摄像头
            Log.d("MyCamera", "打开摄像头成功");
            mCameraManager.openCamera(mCameraID, stateCallback, mainHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.d("MyCamera", "打开摄像头失败");
        }
    }


    /**
     * 摄像头创建监听
     */
    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {//打开摄像头
            mCameraDevice = camera;
            //开启预览
            takePreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {//关闭摄像头
            Toast.makeText(MainActivity.this, "关闭摄像头", Toast.LENGTH_SHORT).show();
                /*if (null != mCameraDevice) {
                    mCameraDevice.close();
                    MainActivity.this.mCameraDevice = null;
                }*/
        }

        @Override
        public void onError(CameraDevice camera, int error) {//发生错误
            Toast.makeText(MainActivity.this, "摄像头开启失败", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 开始预览
     */

    private void takePreview() {
            /*double tapAreaRatio = 0.1;
            Rect rect = new Rect();
            rect.left = clamp((int) (x - tapAreaRatio / 2 * cropRegion.width()), 0, cropRegion.width());
            rect.right = clamp((int) (x + tapAreaRatio / 2 * cropRegion.width()), 0, cropRegion.width());
            rect.top = clamp((int) (y - tapAreaRatio / 2 * cropRegion.height()), 0, cropRegion.height());
            rect.bottom = clamp((int) (y + tapAreaRatio / 2 * cropRegion.height()), 0, cropRegion.height());


            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_REGIONS, new MeteringRectangle[] {new MeteringRectangle(rect, 1000)});
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_REGIONS, new MeteringRectangle[] {new MeteringRectangle(rect, 1000)});
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_START);

            CaptureRequest mPreviewRequest = mPreviewRequestBuilder.build();
            try {
                mCameraCaptureSession.setRepeatingRequest(mPreviewRequest, mAfCaptureCallback, mBackgroundHandler);
            } catch (CameraAccessException e) {
                Log.e("MainActivity", "setRepeatingRequest failed, " + e.getMessage());
            }
*/

            /*if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState
                    || CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO);
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);

                mPreviewRequest = mPreviewRequestBuilder.build();
                try {
                    mCaptureSession.setRepeatingRequest(mPreviewRequest, null, mBackgroundHandler);
                } catch (CameraAccessException e) {
                    Log.e("Main", "setRepeatingRequest failed, errMsg: " + e.getMessage());
                }
            }

*/
        try {
            // 创建预览需要的CaptureRequest.Builder
            previewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            // 将SurfaceView的surface作为CaptureRequest.Builder的目标
            previewRequestBuilder.addTarget(mSurfaceHolder.getSurface());
            // 创建CameraCaptureSession，该对象负责管理处理预览请求和拍照请求


            mCameraDevice.createCaptureSession(Arrays.asList(mSurfaceHolder.getSurface(), mImageReader.getSurface()), new CameraCaptureSession.StateCallback() // ③
            {
                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    if (null == mCameraDevice) {
                        Toast.makeText(MainActivity.this, "mCameraDevice is null", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // 当摄像头已经准备好时，开始显示预览
                    mCameraCaptureSession = cameraCaptureSession;
                    try {
                        // 自动对焦
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        // 打开闪光灯
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, LIGHTOPENORCLOSE);
                        // 显示预览
                        CaptureRequest previewRequest = previewRequestBuilder.build();
                        mCameraCaptureSession.setRepeatingRequest(previewRequest, null, childHandler1);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(MainActivity.this, "配置失败", Toast.LENGTH_SHORT).show();
                }
            }, childHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    /**
     * 拍照
     */
    private void takePicture() {
        if (mCameraDevice == null) return;
        // 创建拍照需要的CaptureRequest.Builder
        final CaptureRequest.Builder captureRequestBuilder;
        try {

            captureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            // 将imageReader的surface作为CaptureRequest.Builder的目标
            captureRequestBuilder.addTarget(mImageReader.getSurface());
            // 自动对焦
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            // 自动曝光
            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, LIGHTOPENORCLOSE);
            // 获取手机方向
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            // 根据设备方向计算设置照片的方向
            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            //拍照
            CaptureRequest mCaptureRequest = captureRequestBuilder.build();
            mCameraCaptureSession.capture(mCaptureRequest, null, childHandler);

            //initsurfaceView();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}
