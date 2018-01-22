package com.example.myfilter.GLtexture;

import android.graphics.Bitmap;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;

public class GLTexture {

	public static final int TEXTURE_2D = GLES20.GL_TEXTURE_2D;
	public static final int TEXTURE_2D_OES = GLES11Ext.GL_TEXTURE_EXTERNAL_OES;

	protected int mTextureID;
	protected int mTextureTarget;
	protected int mTextureWidth;
	protected int mTextureHeight;

	public GLTexture(int textureID, int textureTarget) {
		mTextureID = textureID;
		mTextureTarget = textureTarget;
		mTextureWidth = 0;
		mTextureHeight = 0;
	}

	public static GLTexture createTexture(int width, int height) {
		GLTexture texture = null;

		if (width > 0 && height > 0) {
			int textureID = createTextureID();

			texture = new GLTexture(textureID, GLES20.GL_TEXTURE_2D);
			if (texture != null) {
				texture.setSize(width, height);
			}
		}
		return texture;
	}

	public static GLTexture createTextureFromBitmap(Bitmap bitmap) {
		GLTexture texture = null;

		if (bitmap != null) {
			int textureID = createTextureID();

			texture = new GLTexture(textureID, GLES20.GL_TEXTURE_2D);
			if (texture != null) {
				texture.setBitmap(bitmap);
			}
		}
		return texture;
	}

	public static GLTexture createTextureFromBitmap(Bitmap bitmap, int w, int h) {
		GLTexture texture = null;

		if (bitmap != null) {
			int textureID = createTextureID();

			texture = new GLTexture(textureID, GLES20.GL_TEXTURE_2D);
			if (texture != null) {
				texture.setBitmap(bitmap, w, h);
			}
		}
		return texture;
	}

	public void setSize(int width, int height) {
		if (width > 0 && height > 0) {
			mTextureWidth = width;
			mTextureHeight = height;

			GLES20.glBindTexture(mTextureTarget, mTextureID);
			GLES20Utils.checkGlError("glBindTexture");

			GLES20.glTexParameteri(mTextureTarget, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameteri(mTextureTarget, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameteri(mTextureTarget, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameteri(mTextureTarget, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
			GLES20Utils.checkGlError("glTexParameteri");

			GLES20.glTexImage2D(mTextureTarget, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
			GLES20Utils.checkGlError("glTexImage2D");

			GLES20.glBindTexture(mTextureTarget, 0);
			GLES20Utils.checkGlError("glBindTexture");
		}
	}

	public void setBitmap(Bitmap bitmap) {
		if (bitmap != null) {
			mTextureWidth = bitmap.getWidth();
			mTextureHeight = bitmap.getHeight();

			GLES20.glBindTexture(mTextureTarget, mTextureID);
			GLES20Utils.checkGlError("glBindTexture");

			GLUtils.texImage2D(mTextureTarget, 0, bitmap, 0);
			GLES20Utils.checkGlError("texImage2D");

			GLES20.glTexParameteri(mTextureTarget, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameteri(mTextureTarget, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameteri(mTextureTarget, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameteri(mTextureTarget, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
			GLES20Utils.checkGlError("glTexParameteri");

			GLES20.glBindTexture(mTextureTarget, 0);
		}
	}

	public void setBitmap(Bitmap bitmap, int w, int h) {
		if (bitmap != null) {
			mTextureWidth = w;
			mTextureHeight = h;

			GLES20.glBindTexture(mTextureTarget, mTextureID);
			GLES20Utils.checkGlError("glBindTexture");

			ByteBuffer buffer = ByteBuffer.allocate(mTextureWidth * mTextureHeight * 4);
			bitmap.copyPixelsToBuffer(buffer);
			buffer.position(0);

			GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mTextureWidth, mTextureHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer);
			GLES20Utils.checkGlError("glTexImage2D");

			GLES20.glTexParameteri(mTextureTarget, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameteri(mTextureTarget, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameteri(mTextureTarget, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameteri(mTextureTarget, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
			GLES20Utils.checkGlError("glTexParameteri");

			GLES20.glBindTexture(mTextureTarget, 0);
		}
	}

	public int getTextureID() {
		return mTextureID;
	}

	public int getTextureTarget() {
		return mTextureTarget;
	}

	public int getTextureWidth() {
		return mTextureWidth;
	}

	public int getTextureHeight() {
		return mTextureHeight;
	}

	public boolean isRecycled() {
		return (mTextureID == 0);
	}

	public void save(Bitmap bitmap) {
		if (bitmap == null) {
		}
		int[] frame = new int[1];
		GLES20.glGenFramebuffers(1, frame, 0);
		GLES20Utils.checkGlError("glGenFramebuffers");
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frame[0]);
		GLES20Utils.checkGlError("glBindFramebuffer");
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, mTextureID, 0);
		GLES20Utils.checkGlError("glFramebufferTexture2D");

		Log.e("@@@", "glReadPixels  1................  mTextureWidth:" + mTextureWidth + "  mTextureHeight:" + mTextureHeight);
		ByteBuffer buffer = ByteBuffer.allocate(mTextureWidth * mTextureHeight * 4);
		GLES20.glReadPixels(0, 0, mTextureWidth, mTextureHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer);
		GLES20Utils.checkGlError("glReadPixels");
		Log.e("@@@", "glReadPixels  2.........");
		bitmap.copyPixelsFromBuffer(buffer);

		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
		GLES20Utils.checkGlError("glBindFramebuffer");
		GLES20.glDeleteFramebuffers(1, frame, 0);
		GLES20Utils.checkGlError("glDeleteFramebuffers");
	}

	public void recycle() {
		if (mTextureID != 0) {
			Log.e("camPreviewRenderView", "recycle mTextureID  " + mTextureID);
			deleteTextureID(mTextureID);
			mTextureID = 0;
		}
	}

	public static int createTextureID() {
		int[] textures = new int[1];
		GLES20.glGenTextures(textures.length, textures, 0);
		GLES20Utils.checkGlError("createTextureId");
		Log.e("@@@", "createTextureID    1             texture:" + textures[0]);
		return textures[0];
	}

	public static void deleteTextureID(int texture) {
		Log.e("@@@", "deleteTextureID                 texture:" + texture);
		int[] textures = new int[1];
		textures[0] = texture;
		GLES20.glDeleteTextures(textures.length, textures, 0);
		GLES20Utils.checkGlError("glDeleteTextures");
	}

	public Bitmap getBitmap()
	{
		return null;
	}
}
