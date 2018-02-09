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

public class EffectItemClickListener implements AdapterView.OnItemClickListener {
    private  CaptureRequest.Builder previewRequestBuilder;
    private  CameraCaptureSession mCameraCaptureSession;
    private  Handler handler;
    private  PopupWindow window;
    private  CameraCaptureSession.CaptureCallback myCaptureCallback;

    public EffectItemClickListener(CaptureRequest.Builder previewRequestBuilder, CameraCaptureSession mCameraCaptureSession, Handler handler, PopupWindow window, CameraCaptureSession.CaptureCallback myCaptureCallback) {
        this.previewRequestBuilder = previewRequestBuilder;
        this.mCameraCaptureSession = mCameraCaptureSession;
        this.handler = handler;
        this.window = window;
        this.myCaptureCallback = myCaptureCallback;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        previewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        switch (i) {
            case 0:
                previewRequestBuilder.set(CaptureRequest.CONTROL_EFFECT_MODE, CameraMetadata.CONTROL_EFFECT_MODE_AQUA);
                //mAnimationTextView.start("AQUA", DisplayFragment.WINDOW_TEXT_DISAPPEAR);
                break;
            case 1:
                previewRequestBuilder.set(CaptureRequest.CONTROL_EFFECT_MODE, CameraMetadata.CONTROL_EFFECT_MODE_BLACKBOARD);
                //mAnimationTextView.start("BLACKBOARD", DisplayFragment.WINDOW_TEXT_DISAPPEAR);
                break;
            case 2:
                previewRequestBuilder.set(CaptureRequest.CONTROL_EFFECT_MODE, CameraMetadata.CONTROL_EFFECT_MODE_MONO);
//                mAnimationTextView.start("MONO", DisplayFragment.WINDOW_TEXT_DISAPPEAR);
                break;
            case 3:
                previewRequestBuilder.set(CaptureRequest.CONTROL_EFFECT_MODE, CameraMetadata.CONTROL_EFFECT_MODE_NEGATIVE);
//                mAnimationTextView.start("NEGATIVE", DisplayFragment.WINDOW_TEXT_DISAPPEAR);
                break;
            case 4:
                previewRequestBuilder.set(CaptureRequest.CONTROL_EFFECT_MODE, CameraMetadata.CONTROL_EFFECT_MODE_POSTERIZE);
//                mAnimationTextView.start("POSTERIZE", DisplayFragment.WINDOW_TEXT_DISAPPEAR);
                break;
            case 5:
                previewRequestBuilder.set(CaptureRequest.CONTROL_EFFECT_MODE, CameraMetadata.CONTROL_EFFECT_MODE_SEPIA);
//                mAnimationTextView.start("SEPIA", DisplayFragment.WINDOW_TEXT_DISAPPEAR);
                break;
            case 6:
                previewRequestBuilder.set(CaptureRequest.CONTROL_EFFECT_MODE, CameraMetadata.CONTROL_EFFECT_MODE_SOLARIZE);
//                mAnimationTextView.start("SOLARIZE", DisplayFragment.WINDOW_TEXT_DISAPPEAR);
                break;
            case 7:
                previewRequestBuilder.set(CaptureRequest.CONTROL_EFFECT_MODE, CameraMetadata.CONTROL_EFFECT_MODE_WHITEBOARD);
//                mAnimationTextView.start("WHITEBOARD", DisplayFragment.WINDOW_TEXT_DISAPPEAR);
                break;
            case 8:
                previewRequestBuilder.set(CaptureRequest.CONTROL_EFFECT_MODE, CameraMetadata.CONTROL_EFFECT_MODE_OFF);
//                mAnimationTextView.start("OFF", DisplayFragment.WINDOW_TEXT_DISAPPEAR);
                break;
        }
        updatePreview();
        window.dismiss();

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
