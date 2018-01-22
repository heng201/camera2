package com.example.zhangheng1.filter.view;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.util.AttributeSet;
import android.util.Log;


import com.example.zhangheng1.filter.utils.CameraEngine;

import java.io.IOException;

public class CameraGLSurfaceView extends GLSurfaceView implements Renderer, SurfaceTexture.OnFrameAvailableListener {
    private static final String TAG = "CameraGLSurfaceView";
    Context mContext;
    SurfaceTexture mSurface;
    int mTextureID = -1;
    DirectDrawer mDirectDrawer;
    private Camera mCamera;
    public CameraGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        mContext = context;
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // TODO Auto-generated method stub
        Log.i(TAG, "onSurfaceCreated...");
        mTextureID = createTextureID();
        mSurface = new SurfaceTexture(mTextureID);
        mSurface.setOnFrameAvailableListener(this);
        mDirectDrawer = new DirectDrawer(mTextureID);
        if(CameraEngine.openCamera()) {
            mCamera = CameraEngine.getCamera();
        }


    }
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // TODO Auto-generated method stub
        Log.i(TAG, "onSurfaceChanged...");
        GLES20.glViewport(0, 0, width, height);
        try {
            mCamera.setPreviewTexture(mSurface);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();



    }
    @Override
    public void onDrawFrame(GL10 gl) {
        // TODO Auto-generated method stub
        Log.i(TAG, "onDrawFrame...");
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        mSurface.updateTexImage();
        float[] mtx = new float[16];
        mSurface.getTransformMatrix(mtx);
        mDirectDrawer.draw(mtx);
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mCamera.stopPreview();
    }
    private int createTextureID()
    {
        int[] texture = new int[1];

        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        return texture[0];
    }
    public SurfaceTexture _getSurfaceTexture(){
        return mSurface;
    }
    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        // TODO Auto-generated method stub
        Log.i(TAG, "onFrameAvailable...");
        this.requestRender();
    }

}