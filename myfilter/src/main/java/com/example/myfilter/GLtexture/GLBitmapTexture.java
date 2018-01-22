package com.example.myfilter.GLtexture;

import android.graphics.Bitmap;
import android.opengl.GLES20;


public class GLBitmapTexture extends GLTexture {

private NativeEGLBitmap mEGLBitmap;
	
	public GLBitmapTexture(int textureID, int textureTarget) {
		super(textureID, textureTarget);
	}
	
	public static GLBitmapTexture createGLBitmapTexture(int width, int height) {
		GLBitmapTexture texture = new GLBitmapTexture(createTextureID(), TEXTURE_2D);
		
		if (texture != null) {
			texture.setSize(width, height);
		}
		return texture;
	}
	
	public static GLBitmapTexture createGLBitmapTextureFromBitmap(Bitmap bitmap) {
		GLBitmapTexture texture = new GLBitmapTexture(createTextureID(), TEXTURE_2D);
		
		if (bitmap != null) {
			texture.setBitmap(bitmap);
		}
		return texture;
	}
	
	@Override
	public void setSize(int width, int height) {
		if (width > 0 && height > 0 && mTextureWidth != width && mTextureHeight != height) {
			mTextureWidth = width;
			mTextureHeight = height;
			if (mEGLBitmap != null) {
				mEGLBitmap.freeGLResource();
				mEGLBitmap = null;
			}
			mEGLBitmap = new NativeEGLBitmap(width, height);
			if (mEGLBitmap != null) {
				mEGLBitmap.bindTexture(mTextureID);
				
				GLES20.glBindTexture(mTextureTarget, mTextureID);
				GLES20Utils.checkGlError("glBindTexture");
				
				GLES20.glTexParameteri(mTextureTarget, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
				GLES20.glTexParameteri(mTextureTarget, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
				GLES20.glTexParameteri(mTextureTarget, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
				GLES20.glTexParameteri(mTextureTarget, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
				GLES20Utils.checkGlError("glTexParameteri");
		
				GLES20.glBindTexture(mTextureTarget, 0);
				GLES20Utils.checkGlError("glBindTexture");
			}
		}
	}
	
	@Override
	public void setBitmap(Bitmap bitmap) {
		if (bitmap != null) {
			if (mEGLBitmap == null) {
				mEGLBitmap = new NativeEGLBitmap(bitmap);
			} else {
				mEGLBitmap.setBitmap(bitmap);
			}
		}
	}

	public Bitmap getBitmap() {
		return mEGLBitmap != null ? mEGLBitmap.getBitmap() : null;
	}

	@Override
	public void recycle() {
		if (mEGLBitmap != null) {
			mEGLBitmap.freeGLResource();
			mEGLBitmap = null;
		}
		super.recycle();
	}
}
