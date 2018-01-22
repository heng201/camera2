package com.example.myfilter.GLtexture;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * Created by shaoanbao on 17-2-19.
 */

public class NativeEGLBitmap implements GLResource {

    private int mGenerationID;
    private long mNativeEGLBitmap;
    private int mWidth;
    private int mHeight;
    private boolean mUploadWithGPU;
    private int mFormat;
    private static boolean mIsLoad = false;

    public NativeEGLBitmap(Bitmap bitmap) {
        this(bitmap, true);
    }

    public NativeEGLBitmap(Bitmap bitmap, boolean uploadWithGPU) {
        this(bitmap.getWidth(), bitmap.getHeight(), bitmapFormat2PixelFormat(bitmap.getConfig()), uploadWithGPU);
        this.setBitmap(bitmap);
    }

    public NativeEGLBitmap(int width, int height) {
        this(width, height, 1, true);
    }

    public NativeEGLBitmap(int width, int height, int format) {
        this(width, height, format, true);
    }

    public NativeEGLBitmap(int width, int height, int format, boolean uploadWithGPU) {
        this.mFormat = 0;
        this.mWidth = width;
        this.mHeight = height;
        this.mFormat = format;
        byte c = '0';
        this.mNativeEGLBitmap = nativeAlloc(width, height, format, 1, true, c);
        //this.resize(format, width, height);
        this.mGenerationID = 0;
        this.mUploadWithGPU = uploadWithGPU;
    }

    private NativeEGLBitmap() {
        this.mFormat = 0;
    }

    public Bitmap getBitmap() {
        if (this.mWidth != 0 && this.mHeight != 0 && this.mNativeEGLBitmap != 0L) {
            Bitmap bitmap = Bitmap.createBitmap(this.mWidth, this.mHeight, pixelFormat2BitmapFormat(this.mFormat));
            this.fillBitmap(bitmap);
            return bitmap;
        } else {
            return null;
        }
    }

    public void fillBitmap(Bitmap bitmap) {
        if (bitmap != null && bitmapFormat2PixelFormat(bitmap.getConfig()) == this.mFormat) {
            if (bitmap.getWidth() == this.mWidth && bitmap.getHeight() == this.mHeight) {
                if (!bitmap.isMutable()) {
                    throw new IllegalArgumentException("Bitmap is not mutable.");
                } else {
                    nativeGetPixels(this.mNativeEGLBitmap, bitmap);
                }
            } else {
                throw new IllegalArgumentException("Input bitmap size is not matching EGLBitmap.");
            }
        }
    }

    public void setBitmap(Bitmap bitmap) {
        this.resize(bitmapFormat2PixelFormat(bitmap.getConfig()), bitmap.getWidth(), bitmap.getHeight());
        nativeSetPixels(this.mNativeEGLBitmap, bitmap);
        this.mGenerationID = bitmap.getGenerationId();
    }

    public boolean bindTexture(int texId) {
        nativeBindTexture(this.mNativeEGLBitmap, texId);
        return true;
    }

    public long getNativeEGLBitmap() {
        return this.mNativeEGLBitmap;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public int getFormat() {
        return this.mFormat;
    }

    public int getGenerationId() {
        return this.mGenerationID;
    }

    public boolean isValid() {
        return this.mNativeEGLBitmap != 0L;
    }

    public boolean isEmpty() {
        return this.mNativeEGLBitmap == 0L;
    }

    public boolean theSame(NativeEGLBitmap that) {
        return that != null && !that.isEmpty() ? this == that : false;
    }

    public void freeGLResource() {
        synchronized (this) {
            if (this.mNativeEGLBitmap != 0L) {
                nativeRelease(this.mNativeEGLBitmap);
                this.mNativeEGLBitmap = 0L;
                this.mGenerationID = -1;
            }

        }
    }

    protected void finalize() throws Throwable {
        try {
            this.freeGLResource();
        } finally {
            super.finalize();
        }

    }


    private void resize(int format, int w, int h) {
        if (!isFormatValid(format)) {
            throw new IllegalArgumentException("Bitmap pixel format is : " + format);
        } else {
            if (this.mWidth != w || this.mHeight != h || this.mFormat != format) {
                byte c = '0';
                if (w <= 0 || h <= 0 || !nativeResize(this.mNativeEGLBitmap, w, h, format, 1, true, c)) {
                    throw new IllegalArgumentException("Resize fail");
                }
                this.mWidth = w;
                this.mHeight = h;
                this.mFormat = format;
            }

        }
    }

    public int getSize() {
        return this.mFormat == 1 ? this.mWidth * this.mHeight * 4 : (this.mFormat == 4 ? this.mWidth * this.mHeight * 2 : 0);
    }

    public static int bitmapFormat2PixelFormat(Bitmap.Config format) {
        return format == Bitmap.Config.ARGB_8888 ? 1 : (format == Bitmap.Config.RGB_565 ? 4 : 0);
    }

    public static Bitmap.Config pixelFormat2BitmapFormat(int format) {
        return format == 1 ? Bitmap.Config.ARGB_8888 : (format == 4 ? Bitmap.Config.RGB_565 : null);
    }

    public static boolean isFormatValid(int format) {
        return format == 1 || format == 4;
    }

    static {
        try {
            Log.e("@@@","load eglbitmap");
            System.loadLibrary("eglbitmap");
        } catch (UnsatisfiedLinkError e) {
        }
    }

/*
    private  native long native_alloc(int width, int height, int format, int usage, boolean erase, byte c);

    private  native boolean native_resize(long handle, int width, int height, int format, int usage, boolean erase, byte c);

    private  native void nativeRelease(long handle);

    private  native void nativeSetPixels(long handle, Object var2);

    private  native void nativeGetPixels(long handle, Object var2);

    private  native boolean nativeBindTexture(long handle, int texId);*/

    private  native long nativeAlloc(int width, int height, int format, int usage, boolean erase, byte c);

    private  native boolean nativeResize(long handle, int width, int height, int format, int usage, boolean erase, byte c);

    private  native void nativeRelease(long handle);

    private  native void nativeSetPixels(long handle, Object var2);

    private  native void nativeGetPixels(long handle, Object var2);

    private  native boolean nativeBindTexture(long handle, int texId);
}


