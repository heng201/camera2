package com.example.zhangheng1.camera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Range;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhangheng1.camera.adapter.EffectAdapter;
import com.example.zhangheng1.camera.adapter.AwbAdapter;
import com.example.zhangheng1.camera.adapter.SenseAdapter;
import com.example.zhangheng1.camera.listener.EffectItemClickListener;
import com.example.zhangheng1.camera.listener.AwbItemClickListener;
import com.example.zhangheng1.camera.listener.SenseItemClickListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import wseemann.media.FFmpegMediaMetadataRetriever;

import static android.support.v4.math.MathUtils.clamp;

public class CameraActivity extends Activity implements View.OnClickListener {


    private static int CONTROL_AF_MODE = CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE;
    private static int CONTROL_AE_MODE = CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH;
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
    private TextView tv_focus;
    private TextView tv_iso;
    private TextView tv_scale;
    private TextView tv_ae;
    private TextView tv_time;
    private TextView tv_awb;
    private TextView tv_effect;
    private TextView tv_messagetoast;
    private TextView tv_scene;

    private SeekBar sb_focus;
    private SeekBar sb_iso;
    private SeekBar sb_scale;
    private SeekBar sb_ae;
    private SeekBar sb_time;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Button btnTakephoto;
    private Button btn_startrecord;
    private ImageView ivPhotes;

    private Handler handler1, handler2, mainHandler;
    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder preCameraCaptureRequest;
    private ImageReader imageReader;
    private ImageReader imageReaderTakePictures;
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
     * camera数据属性
     */
    private CameraCharacteristics characteristics;
    private StreamConfigurationMap map;
    /**
     * 对焦框
     */
    private FocusFrameView focusFrameView;
    /**
     * 最小的焦距值
     */
    private Float minimumLens;
    /**
     * 传感器的信息ISO
     */
    private Range<Integer> integerRange;

    /**
     * 传感器的最大值
     */
    private Integer maxISO;
    /**
     * 传感器的最小值
     */
    private Integer minISO;
    /**
     * 要设置的iso的值
     */
    private int isoValue;
    /**
     * 固定焦距值
     */
    private float focusDistance;
    /**
     * 是否显示seekbar
     */
    private boolean isFoucs = false;
    private boolean isISO = false;
    private boolean isSacle = false;
    private boolean isAe = false;
    private boolean isTime = false;
    /**
     * 消失对焦框
     */
    private static int REFRESHFOCUSVIEW = 0;
    private static int LIGHTOPEN = 1;
    private static int LIGHTCLOSE = 2;
    private static int LIGHTAUTO = 3;
    private static int CAPTURES = 4;
    private static int TOASTGONE = 5;
    private static int LIGHTOPENORCLOSE = CaptureRequest.FLASH_MODE_OFF;
    private Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == REFRESHFOCUSVIEW) {
                focusFrameView.setVisibility(View.GONE);
                //messageHandler.removeMessages(REFRESHFOCUSVIEW);
            } else if (msg.what == LIGHTCLOSE) {
                tvLight.setText("闪光灯关闭");
            } else if (msg.what == LIGHTOPEN) {
                tvLight.setText("闪光灯打开");
            } else if (msg.what == LIGHTAUTO) {
                tvLight.setText("闪光灯自动");
            } else if (msg.what == CAPTURES) {
                mState = STATE_CAPTURE;
            }else if(msg.what == TOASTGONE) {
                tv_messagetoast.setVisibility(View.GONE);
            }


        }
    };
    private static final int STATE_PREVIEW = 0;
    private static final int STATE_CAPTURE = 1;
    private static final int STATE_REPRE = 2;
    private int mState = STATE_PREVIEW;


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
        sb_focus = findViewById(R.id.sb_focus);
        sb_iso = findViewById(R.id.sb_iso);
        sb_scale = findViewById(R.id.sb_scale);
        sb_ae = findViewById(R.id.sb_ae);
        sb_time = findViewById(R.id.sb_time);
        tv_focus = findViewById(R.id.tv_focus);
        tv_iso = findViewById(R.id.tv_iso);
        tv_scale = findViewById(R.id.tv_scale);
        tv_ae = findViewById(R.id.tv_ae);
        tv_time = findViewById(R.id.tv_time);
        tv_awb = findViewById(R.id.tv_awb);
        tv_effect = findViewById(R.id.tv_effect);
        tv_messagetoast = findViewById(R.id.tv_messagetoast);
        tv_scene = findViewById(R.id.tv_scene);

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
        //btnTakephoto.setOnLongClickListener(new MyOnLongClickListener());
        btnTakephoto.setOnTouchListener(OnTouchListener);
        ivPhotes.setOnClickListener(this);
        tvLight.setOnClickListener(this);
        tv_focus.setOnClickListener(this);
        tv_iso.setOnClickListener(this);
        tv_scale.setOnClickListener(this);
        tv_ae.setOnClickListener(this);
        tv_time.setOnClickListener(this);
        tv_awb.setOnClickListener(this);
        tv_effect.setOnClickListener(this);
        tv_scene.setOnClickListener(this);

        MyOnSeekBarChangeListener myOnSeekBarChangeListener = new MyOnSeekBarChangeListener();
        sb_focus.setOnSeekBarChangeListener(myOnSeekBarChangeListener);
        sb_iso.setOnSeekBarChangeListener(myOnSeekBarChangeListener);
        sb_scale.setOnSeekBarChangeListener(myOnSeekBarChangeListener);
        sb_ae.setOnSeekBarChangeListener(myOnSeekBarChangeListener);
        sb_time.setOnSeekBarChangeListener(myOnSeekBarChangeListener);
        sb_focus.setVisibility(View.INVISIBLE);
        sb_iso.setVisibility(View.INVISIBLE);
        sb_scale.setVisibility(View.INVISIBLE);
        sb_ae.setVisibility(View.INVISIBLE);
        sb_time.setVisibility(View.INVISIBLE);
    }

    private View.OnTouchListener OnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    messageHandler.sendEmptyMessageDelayed(CAPTURES, 300);
                    break;
                case MotionEvent.ACTION_MOVE:

                    break;
                case MotionEvent.ACTION_UP:
                    mState = STATE_PREVIEW;
                    messageHandler.removeMessages(CAPTURES);
                    break;
            }
            return false;
        }
    };

    /**
     * seekbar手动seekbar的监听
     */
    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            tv_messagetoast.setVisibility(View.VISIBLE);
            if (previewRequestBuilder == null) {
                return;
            }

            switch (seekBar.getId()) {
                case R.id.sb_focus:
                    float minimumLens = characteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);
                    float num = (((float) i) * minimumLens / 100);
                    previewRequestBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, num);
                    int showNum = (int) num;
                    tv_messagetoast.setText("聚焦：" + showNum);
                    break;
                case R.id.sb_ae:
                    //Switch switchAE = (Switch) getView().findViewById(R.id.switch_ae);
                    //if (switchAE.isChecked()) {
                    //曝光增益
                    Range<Integer> range1 = characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE);
                    int maxmax = range1.getUpper();
                    int minmin = range1.getLower();
                    int all = (-minmin) + maxmax;
                    int time = 100 / all;
                    int ae = ((i / time) - maxmax) > maxmax ? maxmax : ((i / time) - maxmax) < minmin ? minmin : ((i / time) - maxmax);
                    previewRequestBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, ae);
                    tv_messagetoast.setText("曝光增益：" + ae);
                    //valueAE = ae;
//                    } else {
//                        //曝光时间
//                        Range<Long> range = mCameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE);
//                        long max = range.getUpper();
//                        long min = range.getLower();
//                        long ae = ((i * (max - min)) / 100 + min);
//                        mPreviewBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, ae);
//                        mSeekBarTextView.setText("曝光时间：" + ae);
//                        valueAETime = ae;
//                    }
                    break;
                case R.id.sb_iso:
                    Range<Integer> range = characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE);
                    int max1 = range.getUpper();//10000
                    int min1 = range.getLower();//100
                    int iso = ((i * (max1 - min1)) / 100 + min1);
                    previewRequestBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, iso);
                    valueISO = iso;
                    tv_messagetoast.setText("灵敏度：" + iso);
                    break;

                case R.id.sb_scale:
                    Rect rect = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
                    int radio = characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM).intValue() / 2;
                    int realRadio = characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM).intValue();
                    int centerX = rect.centerX();
                    int centerY = rect.centerY();
                    int minMidth = (rect.right - ((i * centerX) / 100 / radio) - 1) - ((i * centerX / radio) / 100 + 8);
                    int minHeight = (rect.bottom - ((i * centerY) / 100 / radio) - 1) - ((i * centerY / radio) / 100 + 16);
                    if (minMidth < rect.right / realRadio || minHeight < rect.bottom / realRadio) {
                        Log.i("sb_zoom", "sb_zoomsb_zoomsb_zoom");
                        return;
                    }
//                    Rect newRect = new Rect(20, 20, rect.right - ((i * centerX) / 100 / radio) - 1, rect.bottom - ((i * centerY) / 100 / radio) - 1);
//                    Log.i("sb_zoom", "left--->" + "20" + ",,,top--->" + "20" + ",,,right--->" + (rect.right - ((i * centerX) / 100 / radio) - 1) + ",,,bottom--->" + (rect.bottom - ((i * centerY) / 100 / radio) - 1));
                    Rect newRect = new Rect((i * centerX / radio) / 100 + 40, (i * centerY / radio) / 100 + 40, rect.right - ((i * centerX) / 100 / radio) - 1, rect.bottom - ((i * centerY) / 100 / radio) - 1);
                    Log.i("sb_zoom", "left--->" + ((i * centerX / radio) / 100 + 8) + ",,,top--->" + ((i * centerY / radio) / 100 + 16) + ",,,right--->" + (rect.right - ((i * centerX) / 100 / radio) - 1) + ",,,bottom--->" + (rect.bottom - ((i * centerY) / 100 / radio) - 1));
                    previewRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, newRect);
                    tv_messagetoast.setText("放大：" + i + "%");
                    break;
//                case R.id.sb_zoom_2:
//                    Rect rect2 = mCameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
//                    int radio2 = mCameraCharacteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM).intValue() / 3;
//                    int realRadio2 = mCameraCharacteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM).intValue();
//                    int centerX2 = rect2.centerX();
//                    int centerY2 = rect2.centerY();
//                    int minMidth2 = (rect2.right - ((i * centerX2) / 100 / radio2) - 1) - 20;
//                    int minHeight2 = (rect2.bottom - ((i * centerY2) / 100 / radio2) - 1) - 20;
//                    if (minMidth2 < rect2.right / realRadio2 || minHeight2 < rect2.bottom / realRadio2) {
//                        Log.i("sb_zoom", "sb_zoomsb_zoomsb_zoom");
//                        return;
//                    }
//                    Rect newRect2 = new Rect(20, 20, rect2.right - ((i * centerX2) / 100 / radio2) - 1, rect2.bottom - ((i * centerY2) / 100 / radio2) - 1);
//                    Log.i("sb_zoom", "left--->" + "20" + ",,,top--->" + "20" + ",,,right--->" + (rect2.right - ((i * centerX2) / 100 / radio2) - 1) + ",,,bottom--->" + (rect2.bottom - ((i * centerY2) / 100 / radio2) - 1));
//                    mPreviewBuilder.set(CaptureRequest.SCALER_CROP_REGION, newRect2);
//                    mSeekBarTextView.setText("放大：" + i + "%");
//                    break;
                case R.id.sb_time:
                    //曝光时间
                    Range<Long> range2 = characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE);
                    long max = range2.getUpper();
                    long min = range2.getLower();
                    long ae2 = ((i * (max - min)) / 100 + min);
                    previewRequestBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, ae2);
                    tv_messagetoast.setText("曝光时间：" + ae2);
                    valueAETime = ae2;
                    break;


            }

            updatePreview1();
        }


        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.d(TAG, "onStartTrackingTouch: seek按下");

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

            messageHandler.sendEmptyMessageDelayed(TOASTGONE,100);

        }
    }

    /**
     * 更新预览
     */
    private void updatePreview1() {
        try {
            previewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            //3A
            mCameraCaptureSession.setRepeatingRequest(previewRequestBuilder.build(), myCaptureCallback, handler1);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("updatePreview", "ExceptionExceptionException");
        }
    }


    /**
     * 重新开始固定焦距和ISO的预览
     */
    private void rePreFocusAndISODisplay(float focusDistance, int isoValue) {
        try {
            // 创建预览需要的CaptureRequest.Builder
//            previewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
//            // 将SurfaceView的surface作为CaptureRequest.Builder的目标
//            previewRequestBuilder.addTarget(surfaceHolder.getSurface());

            //previewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_OFF);
            previewRequestBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, focusDistance);
//            previewRequestBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, valueAETime);
//            previewRequestBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, valueAE);
//            previewRequestBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, valueISO);

            previewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            //3A
            previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_OFF);
            previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON);
            //previewRequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CameraMetadata.CONTROL_AWB_MODE_AUTO);
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

    /**
     * surfaceview 的点击监听，点击对焦
     */
    class MyOnTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            // 先取相对于view上面的坐标
            //double x = motionEvent.getX(), y = motionEvent.getY(), tmp;
            //Log.d(TAG, "onTouch: x" + x + " y : " + y);

            //重绘对焦框
            refreshFocusView((int) motionEvent.getX(), (int) motionEvent.getY());
            final Rect sensorArraySize = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
            //TODO: here I just flip x,y, but this needs to correspond with the sensor orientation (via SENSOR_ORIENTATION)
            //得到触摸点对应的图像点
            final int y = (int) ((motionEvent.getX() / (float) view.getWidth()) * (float) sensorArraySize.height());
            final int x = (int) ((motionEvent.getY() / (float) view.getHeight()) * (float) sensorArraySize.width());
            final int halfTouchWidth = 300; //(int)motionEvent.getTouchMajor(); //TODO: this doesn't represent actual touch size in pixel. Values range in [3, 10]...
            final int halfTouchHeight = 300; //(int)motionEvent.getTouchMinor();
            Log.d(TAG, "onTouch: x" + x + " y : " + y);
            final MeteringRectangle focusAreaTouch = new MeteringRectangle(Math.max(x - halfTouchWidth, 0),
                    Math.max(y - halfTouchHeight, 0),
                    halfTouchWidth * 2,
                    halfTouchHeight * 2,
                    MeteringRectangle.METERING_WEIGHT_MAX - 1);
            //closePreviewSession();
            /**
             * 更新预览界面
             */
            rePreview(focusAreaTouch);
            //takepreCameraDisplay();
            return false;
        }
    }

    private void rePreview(final MeteringRectangle focusAreaTouch) {
//        try {
//            // 创建预览需要的CaptureRequest.Builder
//            previewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
//            // 将SurfaceView的surface作为CaptureRequest.Builder的目标
//            previewRequestBuilder.addTarget(surfaceHolder.getSurface());
//            // 创建CameraCaptureSession，该对象负责管理处理预览请求和拍照请求
        previewRequestBuilder.set(CaptureRequest.CONTROL_AF_REGIONS, new MeteringRectangle[]{focusAreaTouch});
        previewRequestBuilder.set(CaptureRequest.CONTROL_AE_REGIONS, new MeteringRectangle[]{focusAreaTouch});
        previewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
        previewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
        updatePreview1();
//            mCameraDevice.createCaptureSession(Arrays.asList(surfaceHolder.getSurface(), imageReader.getSurface()), new CameraCaptureSession.StateCallback() // ③
//            {
//                @Override
//                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
//                    if (null == mCameraDevice) {
//                        //Toast.makeText(MainActivity.this, "mCameraDevice is null", Toast.LENGTH_SHORT).show();
//                        Log.d(TAG, "onConfigured: ");
//                        return;
//                    }
//                    // 当摄像头已经准备好时，开始显示预览
//                    mCameraCaptureSession = cameraCaptureSession;
//                    try {
//
//                        // 显示预览
//                        previewRequestBuilder.set(CaptureRequest.CONTROL_AF_REGIONS, new MeteringRectangle[]{focusAreaTouch});
//
////                        CaptureRequest.Key<Integer> key = CaptureRequest.CONTROL_MODE;
////                        Integer Value =  CaptureRequest.CONTROL_AF_MODE_AUTO;
////                        previewRequestBuilder.set(key,Value);
//                        previewRequestBuilder.set(CaptureRequest.CONTROL_AE_REGIONS, new MeteringRectangle[]{focusAreaTouch});
//                        //previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,);
//                        previewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
//                        previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
//                        previewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
//                        //previewRequestBuilder.setTag("FOCUS_TAG"); //we'll capture this later for resuming the preview
//                        CaptureRequest previewRequest = previewRequestBuilder.build();
//                        mCameraCaptureSession.setRepeatingRequest(previewRequest, null, handler1);
//                        //previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
//                    } catch (CameraAccessException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//                @Override
//                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
//                    //Toast.makeText(CameraActivity.this, "配置失败", Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, "onConfigureFailed: 配置失败");
//                }
//            }, handler1);

//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 重绘focusview
     */
    private void refreshFocusView(int x, int y) {
        focusFrameView.setmCenterX(x);
        focusFrameView.setmCenterY(y);
        focusFrameView.invalidate();
        //focusFrameView.refreshDrawableState();
        focusFrameView.setVisibility(View.VISIBLE);
        messageHandler.removeMessages(REFRESHFOCUSVIEW);
        messageHandler.sendEmptyMessageDelayed(REFRESHFOCUSVIEW, 2000);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-12-06 10:38:41 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        sb_focus.setVisibility(View.INVISIBLE);
        sb_iso.setVisibility(View.INVISIBLE);
        sb_scale.setVisibility(View.INVISIBLE);
        sb_ae.setVisibility(View.INVISIBLE);
        sb_time.setVisibility(View.INVISIBLE);
        switch (v.getId()) {
            case R.id.btn_takephoto:
                //拍照
                takePicture();
                break;
            case R.id.iv_photes:
                //进入照片预览
                showPictures();
                break;
            case R.id.tv_light:
                //闪光灯开启或关闭
                //开启或关闭闪光灯
                Toast.makeText(CameraActivity.this, "闪光灯", Toast.LENGTH_SHORT).show();
                if (LIGHTOPENORCLOSE == CaptureRequest.FLASH_MODE_OFF) {
                    LIGHTOPENORCLOSE = CaptureRequest.FLASH_MODE_TORCH;
                    messageHandler.sendEmptyMessage(LIGHTOPEN);
                } else if (LIGHTOPENORCLOSE == CaptureRequest.FLASH_MODE_TORCH) {
                    LIGHTOPENORCLOSE = CaptureRequest.FLASH_MODE_SINGLE;
                    messageHandler.sendEmptyMessage(LIGHTAUTO);
                } else if (LIGHTOPENORCLOSE == CaptureRequest.FLASH_MODE_SINGLE) {
                    LIGHTOPENORCLOSE = CaptureRequest.FLASH_MODE_OFF;
                    messageHandler.sendEmptyMessage(LIGHTCLOSE);
                }
                break;
            case R.id.btn_startrecord:
                if (isStartRecord) {
                    Log.d(TAG, "onClick: 停止录像");
                    Toast.makeText(CameraActivity.this, "停止录像,视频地址" + mFileName, Toast.LENGTH_SHORT).show();
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
            case R.id.tv_focus:
                if (isFoucs) {
                    sb_focus.setVisibility(View.INVISIBLE);

                    previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_AUTO);


                    isFoucs = false;
                } else {
                    previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_OFF);
                    sb_focus.setVisibility(View.VISIBLE);
                    isFoucs = true;
                }
                break;
            case R.id.tv_iso:
                if (isISO) {
                    previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON);
                    sb_iso.setVisibility(View.INVISIBLE);
                    isISO = false;
                } else {
                    previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_OFF);
                    sb_iso.setVisibility(View.VISIBLE);
                    isISO = true;
                }
                break;
            case R.id.tv_scale:
                if (isSacle) {
                    sb_scale.setVisibility(View.INVISIBLE);
                    isSacle = false;
                } else {
                    sb_scale.setVisibility(View.VISIBLE);
                    isSacle = true;
                }
                break;
            case R.id.tv_ae:
                if (isAe) {

                    previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_OFF);
                    sb_ae.setVisibility(View.INVISIBLE);
                    isAe = false;
                } else {
                    previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON);
                    sb_ae.setVisibility(View.VISIBLE);
                    isAe = true;
                }
                break;

            case R.id.tv_time:
                if (isTime) {
                    previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON);
                    sb_time.setVisibility(View.INVISIBLE);
                    isTime = false;
                } else {
                    previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_OFF);
                    sb_time.setVisibility(View.VISIBLE);
                    isTime = true;
                }
                break;
            case R.id.tv_awb:
                ListView lv3 = new ListView(this);
                lv3.setBackgroundColor(Color.parseColor("#808080"));
                SimpleAdapter listItemAdapter3 = AwbAdapter.getAdapter(this);
                lv3.setAdapter(listItemAdapter3);
                PopupWindow window3 = createPopupWindow(this, lv3);
                lv3.setOnItemClickListener(new AwbItemClickListener(previewRequestBuilder, mCameraCaptureSession, handler1, window3, myCaptureCallback));
                int xoff3 = window3.getWidth() / 2 - tv_awb.getWidth() / 2;
                window3.update();
                window3.showAsDropDown(tv_awb, -xoff3, 0);
                break;
            case R.id.tv_effect:

                ListView lv = new ListView(this);
                lv.setBackgroundColor(Color.parseColor("#808080"));
                SimpleAdapter listItemAdapter = EffectAdapter.getAdapter(this);
                lv.setAdapter(listItemAdapter);
                PopupWindow window = createPopupWindow(this, lv);
                lv.setOnItemClickListener(new EffectItemClickListener(previewRequestBuilder, mCameraCaptureSession, handler1, window, myCaptureCallback));
                int xoff = window.getWidth() / 2 - tv_effect.getWidth() / 2;
                window.update();
                window.showAsDropDown(tv_effect, -xoff, 0);
                break;
            case R.id.tv_scene:
                ListView lv1 = new ListView(this);
                lv1.setBackgroundColor(Color.parseColor("#808080"));
                SimpleAdapter listItemAdapter1 = SenseAdapter.getAdapter(this);
                lv1.setAdapter(listItemAdapter1);
                PopupWindow window1 = createPopupWindow(this, lv1);
                lv1.setOnItemClickListener(new SenseItemClickListener(previewRequestBuilder, mCameraCaptureSession, handler1, window1, myCaptureCallback));
                int xoff1 = window1.getWidth() / 2 - tv_scene.getWidth() / 2;
                window1.update();
                window1.showAsDropDown(tv_scene, -xoff1, 0);
                    break;
        }
        updatePreview1();
    }

    /**
     * 创建一个包含自定义view的PopupWindow
     *
     * @param cx
     * @return
     */
    private PopupWindow createPopupWindow(Context cx, ListView lv) {
        PopupWindow window = new PopupWindow(cx);
        window.setContentView(lv);
        Resources res = cx.getResources();
        window.setWidth(res.getDimensionPixelOffset(R.dimen.popupwindow_width));
        window.setHeight(res.getDimensionPixelOffset(R.dimen.popupwindow_height) * (lv.getAdapter().getCount()));
        window.setFocusable(true); //设置PopupWindow可获得焦点
        window.setTouchable(true); //设置PopupWindow可触摸
        window.setOutsideTouchable(true); //设置非PopupWindow区域可触摸
        return window;
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
        HandlerThread handlerThread1 = new HandlerThread("Captures");
        handlerThread1.start();
        handler2 = new Handler(handlerThread1.getLooper());
        mainHandler = new Handler(getMainLooper());
        mCameraID = "" + CameraCharacteristics.LENS_FACING_FRONT;//后摄像头
        //获取摄像头管理
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            //打开摄像头
            Log.d("MyCamera", "打开摄像头成功");
            cameraManager.openCamera(mCameraID, stateCallback, mainHandler);
            characteristics = cameraManager.getCameraCharacteristics(mCameraID);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.d("MyCamera", "打开摄像头失败");
        }

        //获取最小的焦距值
        minimumLens = characteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);
        //传感器的信息

        integerRange = characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE);
        Integer integer1 = characteristics.get(CameraCharacteristics.SENSOR_MAX_ANALOG_SENSITIVITY);

        Log.d(TAG, "initCamera: SENSOR_MAX_ANALOG_SENSITIVITY " + integer1);
        if (integerRange != null) {
            //获取最大最小值
            maxISO = integerRange.getUpper();
            minISO = integerRange.getLower();
            Log.d(TAG, "initCamera: minimumLens :" + minimumLens + " maxISO: " + maxISO + " minISO: " + minISO + " range: " + integerRange.toString());
        }


        Log.d(TAG, "initCamera: " + minimumLens + " ca ");

        map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        Integer integer = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
        Toast.makeText(CameraActivity.this, "" + integer.toString(), Toast.LENGTH_SHORT).show();
        Log.d(TAG, "initCamera: " + integer.toString());
        Size largest = Collections.max(
                Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                new CompareSizesByArea());
        //设置最大的图像尺寸
        imageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(), ImageFormat.JPEG, 35);
        imageReader.setOnImageAvailableListener(new MyOnImageAvailableListener(), mainHandler);
        /*imageReaderTakePictures = ImageReader.newInstance(1080, 1920, ImageFormat.JPEG, 50);
        imageReaderTakePictures.setOnImageAvailableListener(new MyPicturesOnImageAvailableListener(), mainHandler);*/
    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }

    /**
     * 当有多个权限需要申请的时候
     * 这里以打电话和SD卡读写权限为例
     */
    private void requestPermissions() {

        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAMERA);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.RECORD_AUDIO);
        }

        if (!permissionList.isEmpty()) {  //申请的集合不为空时，表示有需要申请的权限
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), 1);
        } else { //所有的权限都已经授权过了

            initCamera();
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
            if (!mFile.getParentFile().exists()) {
                //父目录不存在 创建父目录
                Log.d(TAG, "creating parent directory...");
                if (!mFile.getParentFile().mkdirs()) {
                    Log.e(TAG, "created parent directory failed.");
                    //return FLAG_FAILED;
                }
            }
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
    class MyPicturesOnImageAvailableListener implements ImageReader.OnImageAvailableListener {

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
            if (num > 100) {
                Toast.makeText(CameraActivity.this, "照片够多了", Toast.LENGTH_SHORT).show();
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
            image.close();

            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            num++;
            if (num > 100) {
                Toast.makeText(CameraActivity.this, "照片够多了", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d(TAG, "onImageAvailable: saveimage " + num);
            if (bitmap != null) {

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssmm").format(new Date());
                String bitmapPath = "/sdcard/Camera2/" + timeStamp + num + ".jpg";

                new ImageSaver(bytes, bitmapPath).start();
                picturesPath.add(bitmapPath);
                ivPhotes.setImageBitmap(bitmap);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        ivPhotes.setImageBitmap(bitmap);
//                    }
//                });

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
            takePictureRequestBuilder.addTarget(surfaceHolder.getSurface());
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
            for (int i = 0; i < 10; i++) {
                list.add(mCaptureRequest);
            }

            mCameraCaptureSession.captureBurst(list, null, handler1);

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
        for (int i = 0; i < 1000; i = i + 40, pictureNum++) {
            Bitmap bitmap = mmr.getFrameAtTime(i * 1000, FFmpegMediaMetadataRetriever.OPTION_CLOSEST);
            if (bitmap != null) {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssmm").format(new Date());
                String bitmapPath = "/sdcard/" + timeStamp + "_" + pictureNum + ".png";
                saveBitmap(bitmapPath, bitmap);
                Log.e("saveImages: for", "saveImages: for 成功保存" + pictureNum + timeStamp);
                //System.out.print("saveImages: for 成功保存");
            }

        }
    }

    /**
     * 将previewBuilder中修改的参数设置到captureBuilder中
     */
    private void previewBuilder2CaptureBuilder() {
        //HDR等等
        takePictureRequestBuilder.set(CaptureRequest.CONTROL_MODE, previewRequestBuilder.get(CaptureRequest.CONTROL_MODE));
        //AWB
        takePictureRequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, previewRequestBuilder.get(CaptureRequest.CONTROL_AWB_MODE));
        //AE
//        if (mPreviewBuilder.get(CaptureRequest.CONTROL_AE_MODE) == CameraMetadata.CONTROL_AE_MODE_OFF) {
        //曝光时间
        takePictureRequestBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, previewRequestBuilder.get(CaptureRequest.SENSOR_EXPOSURE_TIME));
//        } else if (mPreviewBuilder.get(CaptureRequest.CONTROL_AE_MODE) == CameraMetadata.CONTROL_AE_MODE_ON) {
        //曝光增益
        takePictureRequestBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, previewRequestBuilder.get(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION));
//        }
        //AF
//        if (mPreviewBuilder.get(CaptureRequest.CONTROL_AF_MODE) == CameraMetadata.CONTROL_AF_MODE_OFF) {
        //手动聚焦的值
        takePictureRequestBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, previewRequestBuilder.get(CaptureRequest.LENS_FOCUS_DISTANCE));
//        }
        //effects
        takePictureRequestBuilder.set(CaptureRequest.CONTROL_EFFECT_MODE, previewRequestBuilder.get(CaptureRequest.CONTROL_EFFECT_MODE));
        //ISO
        takePictureRequestBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, previewRequestBuilder.get(CaptureRequest.SENSOR_SENSITIVITY));
        //AF REGIONS
        takePictureRequestBuilder.set(CaptureRequest.CONTROL_AF_REGIONS, previewRequestBuilder.get(CaptureRequest.CONTROL_AF_REGIONS));
//        mCaptureBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
        //AE REGIONS
        takePictureRequestBuilder.set(CaptureRequest.CONTROL_AE_REGIONS, previewRequestBuilder.get(CaptureRequest.CONTROL_AE_REGIONS));
//        mCaptureBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_START);
        //SCENSE
        takePictureRequestBuilder.set(CaptureRequest.CONTROL_SCENE_MODE, previewRequestBuilder.get(CaptureRequest.CONTROL_SCENE_MODE));
        //zoom
        takePictureRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, previewRequestBuilder.get(CaptureRequest.SCALER_CROP_REGION));
    }

    /**
     * 拍照
     */
    private void takePicture() {
        try {


            takePictureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            // 将imageReader的surface作为CaptureRequest.Builder的目标
            takePictureRequestBuilder.addTarget(imageReader.getSurface());
            takePictureRequestBuilder.addTarget(surfaceHolder.getSurface());
            //takePictureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            //设置连续帧
            Range<Integer> fps[] = characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
            takePictureRequestBuilder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, fps[fps.length - 1]);//设置每秒30帧
            //得到方向
            takePictureRequestBuilder.set(CaptureRequest.FLASH_MODE, LIGHTOPENORCLOSE);
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            // 根据设备方向计算设置照片的方向
            takePictureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            previewBuilder2CaptureBuilder();
            Log.d(TAG, "takePicture: " + rotation);
            //拍照
            CaptureRequest mCaptureRequest = takePictureRequestBuilder.build();
            mCameraCaptureSession.stopRepeating();
            mCameraCaptureSession.capture(mCaptureRequest, myCaptureCallback, handler1);
            mState = STATE_REPRE;
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

    //----------------------------seekbar的值-------------------------
    //初始化的话是实用中间值
    private float valueAF;
    private int valueAE;
    private long valueAETime;
    private int valueISO;
    //----------------------------seekbar的值-------------------------

    /**
     * 初始化seekbar上的一些参数
     */
    private void initSeekBarValue() {
        valueAF = 5.0f;
        valueAETime = (214735991 - 13231) / 2;
        valueISO = (10000 - 100) / 2;
        valueAE = 0;
    }

    /**
     * 初始化预览的builder，这样做是为了一来就调iso的时候ae不为最低
     */
    private void initPreviewBuilder() {
        previewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_OFF);
        previewRequestBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, valueAF);
        previewRequestBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, valueAETime);
        previewRequestBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, valueAE);
        previewRequestBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, valueISO);
    }

    /**
     * 开始预览
     */
    private void takepreCameraDisplay() {
        try {
            // 创建预览需要的CaptureRequest.Builder
            previewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            // 将SurfaceView的surface作为CaptureRequest.Builder的目标
            previewRequestBuilder.addTarget(surfaceHolder.getSurface());
            //初始化参数
            initPreviewBuilder();
            //3A--->auto
            previewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            //3A
            previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON);
            previewRequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CameraMetadata.CONTROL_AWB_MODE_AUTO);
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
                        // 显示预览
                        CaptureRequest previewRequest = previewRequestBuilder.build();
                        mCameraCaptureSession.setRepeatingRequest(previewRequest, myCaptureCallback, handler1);
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

    private int pictureNum = 0;
    private CameraCaptureSession.CaptureCallback myCaptureCallback = new CameraCaptureSession.CaptureCallback() {

        @Override
        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
            Log.d(TAG, "onCaptureStarted: ");
        }

        private void process(CaptureResult result) {
            switch (mState) {
                case STATE_PREVIEW: {
                    // We have nothing to do when the camera preview is working normally.
                    break;
                }
                case STATE_CAPTURE:
//                    try {
//                        mCameraCaptureSession.stopRepeating();
//                    } catch (CameraAccessException e) {
//                        e.printStackTrace();
//                    }
                    if (pictureNum > 30) {
                        mState = STATE_PREVIEW;
                        pictureNum = 0;
                        return;
                    }
                    pictureNum++;
                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected void onPreExecute() {

                        }

                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                takePictureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_ZERO_SHUTTER_LAG);
                                // 将imageReader的surface作为CaptureRequest.Builder的目标
                                takePictureRequestBuilder.addTarget(imageReader.getSurface());
                                takePictureRequestBuilder.addTarget(surfaceHolder.getSurface());
                                // 自动对焦
                                takePictureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                // 自动曝光
                                takePictureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                                // 获取手机方向
                                int rotation = getWindowManager().getDefaultDisplay().getRotation();
                                // 根据设备方向计算设置照片的方向
                                takePictureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
                                mCameraCaptureSession.capture(takePictureRequestBuilder.build(), null, handler2);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {

                        }
                    }.execute();
                    break;
                case STATE_REPRE:
                    updatePreview1();
                    mState = STATE_PREVIEW;
                    break;
            }
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session,
                                        @NonNull CaptureRequest request,
                                        @NonNull CaptureResult partialResult) {
            Log.d(TAG, "onCaptureProgressed: ");
            //process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            Log.d(TAG, "onCaptureCompleted: ");
            mCameraCaptureSession = session;
            process(result);
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
            mCameraCaptureSession = session;
            Log.d(TAG, "onCaptureFailed: ");
        }

        @Override
        public void onCaptureSequenceCompleted(@NonNull CameraCaptureSession session, int sequenceId, long frameNumber) {
            super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
        }

        @Override
        public void onCaptureSequenceAborted(@NonNull CameraCaptureSession session, int sequenceId) {
            super.onCaptureSequenceAborted(session, sequenceId);
            Log.d(TAG, "onCaptureSequenceAborted: ");
        }

        @Override
        public void onCaptureBufferLost(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull Surface target, long frameNumber) {
            super.onCaptureBufferLost(session, request, target, frameNumber);
            Log.d(TAG, "onCaptureBufferLost: ");
        }
    };

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
        focusFrameView = new FocusFrameView(CameraActivity.this, 540, 960, 200, 200, Color.BLUE);

        initSeekBarValue();
        //在一个activity上面添加额外的content
        addContentView(focusFrameView, new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT));
        focusFrameView.setVisibility(View.GONE);

        // 设置横屏显示
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // 设置全屏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        // 选择支持半透明模式,在有surfaceview的activity中使用。
//        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        findViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messageHandler != null) {
            messageHandler.removeCallbacksAndMessages(null);
        }
        if (handler1 != null) {
            handler1.removeCallbacksAndMessages(null);
        }
        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
        }
        if (handler2 != null) {
            handler2.removeCallbacksAndMessages(null);
        }

    }
}
