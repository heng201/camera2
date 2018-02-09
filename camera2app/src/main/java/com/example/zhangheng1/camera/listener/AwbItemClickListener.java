package com.example.zhangheng1.camera.listener;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.PopupWindow;

/**
 * Created by zhangheng1 on 2018/2/8.
 */

public class AwbItemClickListener implements AdapterView.OnItemClickListener {

    private  CaptureRequest.Builder previewRequestBuilder;
    private  CameraCaptureSession mCameraCaptureSession;
    private  Handler handler;
    private  PopupWindow popupWindow;
    private  CameraCaptureSession.CaptureCallback myCaptureCallback;

    public AwbItemClickListener(CaptureRequest.Builder previewRequestBuilder, CameraCaptureSession mCameraCaptureSession, Handler handler, PopupWindow popupWindow, CameraCaptureSession.CaptureCallback myCaptureCallback) {
        this.previewRequestBuilder = previewRequestBuilder;
        this.mCameraCaptureSession = mCameraCaptureSession;
        this.handler = handler;
        this.popupWindow = popupWindow;
        this.myCaptureCallback = myCaptureCallback;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //previewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_USE_SCENE_MODE);
        switch (i) {
            case 0:
                //mTextView.setText("自动");
                previewRequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CameraMetadata.CONTROL_AWB_MODE_AUTO);
                break;
            case 1:
//                mTextView.setText("多云");
                previewRequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CameraMetadata.CONTROL_AWB_MODE_CLOUDY_DAYLIGHT);
                break;
            case 2:
//                mTextView.setText("白天");
                previewRequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CameraMetadata.CONTROL_AWB_MODE_DAYLIGHT);
                break;
            case 3:
//                mTextView.setText("日光灯");
                previewRequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CameraMetadata.CONTROL_AWB_MODE_FLUORESCENT);
                break;
            case 4:
//                mTextView.setText("白炽灯");
                previewRequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CameraMetadata.CONTROL_AWB_MODE_INCANDESCENT);
                break;
            case 5:
//                mTextView.setText("阴影");
                previewRequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CameraMetadata.CONTROL_AWB_MODE_SHADE);
                break;
            case 6:
                //mTextView.setText("黄昏");
                previewRequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CameraMetadata.CONTROL_AWB_MODE_TWILIGHT);
                break;
            case 7:
                //mTextView.setText("暖光");
                previewRequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CameraMetadata.CONTROL_AWB_MODE_WARM_FLUORESCENT);
                break;
        }
    updatePreview();
        popupWindow.dismiss();

}

    /**
     * 更新预览
     */
    private void updatePreview() {
        try {
            mCameraCaptureSession.setRepeatingRequest(previewRequestBuilder.build(), myCaptureCallback, handler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("updatePreview", "ExceptionExceptionException");
        }
    }
}
