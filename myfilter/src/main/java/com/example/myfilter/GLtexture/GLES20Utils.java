package com.example.myfilter.GLtexture;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.util.Log;

import java.lang.reflect.Constructor;

public class GLES20Utils {

	private static int sNextId = 9999;
	
	public synchronized static void glGenTextures(int n, int[] textures, int offset) {
		synchronized(GLES20Utils.class) {
			while (n-- > 0) {
				textures[offset + n] = sNextId++;
			}
			sNextId = sNextId > 99999999 ? 9999 : sNextId;
			Log.e("@@@", "createTextureID  2               texture:" + textures[0]);
		}
	}
	
	public synchronized static void glGenBuffers(int n, int[] buffers, int offset) {
		synchronized(GLES20Utils.class) {
			while (n-- > 0) {
				buffers[offset + n] = sNextId++;
			}
			sNextId = sNextId > 99999999 ? 9999 : sNextId;
		}
	}
	
	private static Constructor<SurfaceTexture> sSurfaceTextureCtor;
	static {
		try {
			sSurfaceTextureCtor = SurfaceTexture.class.getDeclaredConstructor(boolean.class);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	public static Constructor<SurfaceTexture> getSurfaceTextureCtor() {
		return sSurfaceTextureCtor;
	}
	
	public static void clear(float r, float g, float b, float a) {
		GLES20.glClearColor(r, g, b, a);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
	}

	public static void checkGlError(String op) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			throw new RuntimeException(op + ": glError " + error);
		}
	}
	
	public static boolean isIdentityMatrix(float[] m) {
		return (m[0] == 1 && m[1] == 0 && m[2] == 0 && m[3] == 0 &&
				m[4] == 0 && m[5] == 1 && m[6] == 0 && m[7] == 0 &&
				m[8] == 0 && m[9] == 0 && m[10] == 1 && m[11] == 0 && 
				m[12] == 0 && m[13] == 0 && m[14] == 0 && m[15] == 1);
	}
}
