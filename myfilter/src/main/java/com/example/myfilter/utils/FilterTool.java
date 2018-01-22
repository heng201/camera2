package com.example.myfilter.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

/**
 * Created by xloger on 9月8日.
 * Author:xloger
 * Email:phoenix@xloger.com
 */
public class FilterTool {
    static float colormatrix_lomo[] = {
    1.7f,  0.1f, 0.1f, 0, -73.1f,
    0,  1.7f, 0.1f, 0, -73.1f,
    0,  0.1f, 1.6f, 0, -73.1f,
    0,  0, 0, 1.0f, 0

    };

// 2、黑白
 static float colormatrix_heibai[] = {


    0.8f,  1.6f, 0.2f, 0, -163.9f,
    0.8f,  1.6f, 0.2f, 0, -163.9f,
    0.8f,  1.6f, 0.2f, 0, -163.9f,
    0,  0, 0, 1.0f, 0

    };
// 3、复古
 static float colormatrix_huajiu[] = {
            0.2f,0.5f, 0.1f, 0, 40.8f,
            0.2f, 0.5f, 0.1f, 0, 40.8f,
            0.2f,0.5f, 0.1f, 0, 40.8f,
            0, 0, 0, 1, 0 };

// 4、哥特
 static float colormatrix_gete[] = {
            1.9f,-0.3f, -0.2f, 0,-87.0f,
            -0.2f, 1.7f, -0.1f, 0, -87.0f,
            -0.1f,-0.6f, 2.0f, 0, -87.0f,
            0, 0, 0, 1.0f, 0 };

// 5、锐化
 static float colormatrix_ruise[] = {
            4.8f,-1.0f, -0.1f, 0,-388.4f,
            -0.5f,4.4f, -0.1f, 0,-388.4f,
            -0.5f,-1.0f, 5.2f, 0,-388.4f,
            0, 0, 0, 1.0f, 0 };


// 6、淡雅
 static float colormatrix_danya[] = {
            0.6f,0.3f, 0.1f, 0,73.3f,
            0.2f,0.7f, 0.1f, 0,73.3f,
            0.2f,0.3f, 0.4f, 0,73.3f,
            0, 0, 0, 1.0f, 0 };

// 7、酒红
 static float colormatrix_jiuhong[] = {
            1.2f,0.0f, 0.0f, 0.0f,0.0f,
            0.0f,0.9f, 0.0f, 0.0f,0.0f,
            0.0f,0.0f, 0.8f, 0.0f,0.0f,
            0, 0, 0, 1.0f, 0 };

// 8、清宁
 static float colormatrix_qingning[] = {
            0.9f, 0, 0, 0, 0,
            0, 1.1f,0, 0, 0,
            0, 0, 0.9f, 0, 0,
            0, 0, 0, 1.0f, 0 };

// 9、浪漫
 static float colormatrix_langman[] = {
            0.9f, 0, 0, 0, 63.0f,
            0, 0.9f,0, 0, 63.0f,
            0, 0, 0.9f, 0, 63.0f,
            0, 0, 0, 1.0f, 0 };

// 10、光晕
 static float colormatrix_guangyun[] = {
            0.9f, 0, 0,  0, 64.9f,
            0, 0.9f,0,  0, 64.9f,
            0, 0, 0.9f,  0, 64.9f,
            0, 0, 0, 1.0f, 0 };

// 11、蓝调
 static float colormatrix_landiao[] = {
            2.1f, -1.4f, 0.6f, 0.0f, -31.0f,
            -0.3f, 2.0f, -0.3f, 0.0f, -31.0f,
            -1.1f, -0.2f, 2.6f, 0.0f, -31.0f,
            0.0f, 0.0f, 0.0f, 1.0f, 0.0f
    };

// 12、梦幻
 static float colormatrix_menghuan[] = {
            0.8f, 0.3f, 0.1f, 0.0f, 46.5f,
            0.1f, 0.9f, 0.0f, 0.0f, 46.5f,
            0.1f, 0.3f, 0.7f, 0.0f, 46.5f,
            0.0f, 0.0f, 0.0f, 1.0f, 0.0f
    };

// 13、夜色
 static float colormatrix_yese[] = {
            1.0f, 0.0f, 0.0f, 0.0f, -66.6f,
            0.0f, 1.1f, 0.0f, 0.0f, -66.6f,
            0.0f, 0.0f, 1.0f, 0.0f, -66.6f,
            0.0f, 0.0f, 0.0f, 1.0f, 0.0f
    };
    public static Bitmap oldRemember(Bitmap bitmap){
        long start= System.currentTimeMillis();
        int width=bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap bit= Bitmap.createBitmap(width,height, Bitmap.Config.RGB_565);
        int pixColor=0;
        int pixR=0,pixG=0,pixB=0;
        int newR=0,newG=0,newB=0;
        int[] pixels=new int[width*height];
        bitmap.getPixels(pixels,0,width,0,0,width,height);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                pixColor=pixels[width*i+j];
                pixR= Color.red(pixColor);
                pixG= Color.green(pixColor);
                pixB= Color.blue(pixColor);
                newR = (int) (0.393 * pixR + 0.769 * pixG + 0.189 * pixB);
                newG = (int) (0.349 * pixR + 0.686 * pixG + 0.168 * pixB);
                newB = (int) (0.272 * pixR + 0.534 * pixG + 0.131 * pixB);
                int newColor = Color.argb(255, newR > 255 ? 255 : newR, newG > 255 ? 255 : newG, newB > 255 ? 255 : newB);
                pixels[width * i + j] = newColor;
            }
        }

        bit.setPixels(pixels,0,width,0,0,width,height);
        long end= System.currentTimeMillis();
        return bit;
    }


    public static Bitmap blurImageAmeliorate(Bitmap bmp, Context context)
    {
        float radius = 20;
        int width=bmp.getWidth();
        int height = bmp.getHeight();
        Bitmap overlay= Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(overlay);
        canvas.translate(0, 0);
        canvas.drawBitmap(bmp, 0, 0, null);

        RenderScript rs = RenderScript.create(context);

        Allocation overlayAlloc = Allocation.createFromBitmap(rs, overlay);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, overlayAlloc.getElement());
        blur.setInput(overlayAlloc);
        blur.setRadius(radius);
        blur.forEach(overlayAlloc);
        overlayAlloc.copyTo(overlay);
        rs.destroy();

        return overlay;
    }

    /**
     * 图片锐化（拉普拉斯变换）
     * @param bmp
     * @return
     */
    public static Bitmap sharpenImageAmeliorate(Bitmap bmp) {
        long start = System.currentTimeMillis();
        // 拉普拉斯矩阵
        int[] laplacian = new int[] { -1, -1, -1, -1, 9, -1, -1, -1, -1 };

        int width = bmp.getWidth();
        int height = bmp.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        int pixR = 0;
        int pixG = 0;
        int pixB = 0;

        int pixColor = 0;

        int newR = 0;
        int newG = 0;
        int newB = 0;

        int idx = 0;
        float alpha = 0.3F;
        int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 1, length = height - 1; i < length; i++)
        {
            for (int k = 1, len = width - 1; k < len; k++)
            {
                idx = 0;
                for (int m = -1; m <= 1; m++)
                {
                    for (int n = -1; n <= 1; n++)
                    {
                        pixColor = pixels[(i + n) * width + k + m];
                        pixR = Color.red(pixColor);
                        pixG = Color.green(pixColor);
                        pixB = Color.blue(pixColor);

                        newR = newR + (int) (pixR * laplacian[idx] * alpha);
                        newG = newG + (int) (pixG * laplacian[idx] * alpha);
                        newB = newB + (int) (pixB * laplacian[idx] * alpha);
                        idx++;
                    }
                }

                newR = Math.min(255, Math.max(0, newR));
                newG = Math.min(255, Math.max(0, newG));
                newB = Math.min(255, Math.max(0, newB));

                pixels[i * width + k] = Color.argb(255, newR, newG, newB);
                newR = 0;
                newG = 0;
                newB = 0;
            }
        }

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        long end = System.currentTimeMillis();
        return bitmap;
    }

    public static Bitmap fuDiao(Bitmap bmp){
        long start= System.currentTimeMillis();
        int width=bmp.getWidth();
        int height = bmp.getHeight();
        Bitmap bitmap= Bitmap.createBitmap(width,height, Bitmap.Config.RGB_565);
        int[] pixels = new int[width*height];
        int[] newPixels = new int[width*height];
        int pixColor=0;
        int color2;
        int pixR=0,pixG=0,pixB=0;
        int newR=0,newG=0,newB=0;
        //获取像素
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int i = 1; i < height-1; i++)
        {
            for (int k = 1; k < width-1; k++)
            {
                //获取前一个像素颜色
                pixColor = pixels[width * i + k];
                pixR = Color.red(pixColor);
                pixG = Color.green(pixColor);
                pixB = Color.blue(pixColor);
                //获取当前像素
                pixColor = pixels[(width * i + k) + 1];
                newR = Color.red(pixColor) - pixR +127;
                newG = Color.green(pixColor) - pixG +127;
                newB = Color.blue(pixColor) - pixB +127;
                newR = Math.min(255, Math.max(0, newR));
                newG = Math.min(255, Math.max(0, newG));
                newB = Math.min(255, Math.max(0, newB));
                pixels[width * i + k] = Color.argb(255, newR, newG, newB);
            }
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        long end= System.currentTimeMillis();
        return bitmap;
    }

    public static Bitmap diPian1(Bitmap bmp){
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int[] oldPixels = new int[width*height];
        int[] newPixels = new int[width*height];
        int color;
        int pixelsA,pixelsR,pixelsG,pixelsB;
        Bitmap bitmap= Bitmap.createBitmap(width,height, Bitmap.Config.RGB_565);
        bmp.getPixels(oldPixels, 0, width, 0, 0, width, height);

        for(int i = 1;i < height*width; i++){
            color = oldPixels[i];
            //获取RGB分量
            pixelsA = Color.alpha(color);
            pixelsR = Color.red(color);
            pixelsG = Color.green(color);
            pixelsB = Color.blue(color);

            //转换
            pixelsR = (255 - pixelsR);
            pixelsG = (255 - pixelsG);
            pixelsB = (255 - pixelsB);
            //均小于等于255大于等于0
            if(pixelsR > 255){
                pixelsR = 255;
            }
            else if(pixelsR < 0){
                pixelsR = 0;
            }
            if(pixelsG > 255){
                pixelsG = 255;
            }
            else if(pixelsG < 0){
                pixelsG = 0;
            }
            if(pixelsB > 255){
                pixelsB = 255;
            }
            else if(pixelsB < 0){
                pixelsB = 0;
            }
            //根据新的RGB生成新像素
            newPixels[i] = Color.argb(pixelsA, pixelsR, pixelsG, pixelsB);

        }
        //根据新像素生成新图片
        bitmap.setPixels(newPixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * 反向
     * @param bmp
     * @return
     */
    public static Bitmap fanXiang(Bitmap bmp){
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int[] oldPixels = new int[width*height];
        int[] newPixels = new int[width*height];
        int color;
        int pixelsA,pixelsR,pixelsG,pixelsB;
        Bitmap bitmap= Bitmap.createBitmap(width,height, Bitmap.Config.RGB_565);
        bmp.getPixels(oldPixels, 0, width, 0, 0, width, height);

        for(int i = 1;i < height*width; i++){
            color = oldPixels[i];
            //获取RGB分量
            pixelsA = Color.alpha(color);
            pixelsR = Color.red(color);
            pixelsG = Color.green(color);
            pixelsB = Color.blue(color);


            //均小于等于255大于等于0
            if(pixelsR >= 128 || pixelsG >= 128 || pixelsB >= 128){
                pixelsR = 0;
                pixelsG = 0;
                pixelsB = 0;
            }else{
                pixelsR = 255;
                pixelsG = 255;
                pixelsB = 255;
            }

            //根据新的RGB生成新像素
            newPixels[i] = Color.argb(pixelsA, pixelsR, pixelsG, pixelsB);

        }
        //根据新像素生成新图片
        bitmap.setPixels(newPixels, 0, width, 0, 0, width, height);
        return bitmap;
    }
    /**
     * 黑白
     * @param bmp
     * @return
     */

    public static Bitmap heibai1(Bitmap bmp){
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int[] oldPixels = new int[width*height];
        int color;
        int pixelsA,pixelsR,pixelsG,pixelsB;
        Bitmap bitmap= Bitmap.createBitmap(width,height, Bitmap.Config.RGB_565);
        bmp.getPixels(oldPixels, 0, width, 0, 0, width, height);

        for(int i = 1;i < height*width; i++){
            color = oldPixels[i];
            //获取RGB分量
            pixelsA = Color.alpha(color);
            pixelsR = Color.red(color);
            pixelsG = Color.green(color);
            pixelsB = Color.blue(color);
            pixelsR = (pixelsR + pixelsG + pixelsB) / 3;
            //根据新的RGB生成新像素
            oldPixels[i] = Color.argb(pixelsA, pixelsR, pixelsR, pixelsR);

        }
        //根据新像素生成新图片
        bitmap.setPixels(oldPixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public static Bitmap heibai(Bitmap bitmap){
        int width, height;
        width = bitmap.getWidth();
        height = bitmap.getHeight();

        Bitmap grayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(grayBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true); // 设置抗锯齿

        //一，数组矩阵的方法

    /*float[] array = {1, 0, 0, 0, 100,
                     0, 1, 0, 0, 100,
					 0, 0, 1, 0, 0,
					 0, 0, 0, 1, 0};
	ColorMatrix colorMatrix = new ColorMatrix(array);
	*/

        /*float[] array = {0.393f,0.769f,0.189f,0,0,
                         0.349f,0.686f,0.168f,0,0,
                         0.272f,0.534f,0.131f,0,0,
                         0,0,0,1,0};
        ColorMatrix colorMatrix1 = new ColorMatrix(array);*/
        //二，把饱和度设置为0 就可以得到灰色（黑白)的图片
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);

        //曝光效果
        ColorMatrix colorMatrix2 = new ColorMatrix(new float[]{
                -1,0,0,0,255,
                0,-1,0,0,255,
                0,0,-1,0,255,
                0,0,0,1,0,
        });
        //美白效果
        ColorMatrix colorMatrix3 = new ColorMatrix(new float[]{
                1.2f,0,0,0,0,
                0,1.2f,0,0,0,
                0,0,1.2f,0,0,
                0,0,0,1.2f,0,
        });
        //黑白图片
        ColorMatrix colorMatrix4 = new ColorMatrix(new float[]{
                0.213f, 0.715f,0.072f,0,0,
                0.213f, 0.715f,0.072f,0,0,
                0.213f, 0.715f,0.072f,0,0,
                0,      0,      0,    1f,0,
        });
        //原始效果
        ColorMatrix colorMatrix5 = new ColorMatrix(new float[]{
                1f,0,0,0,0,
                0,1f,0,0,0,
                0,0,1f,0,0,
                0,0,0,1f,0,
        });
        //复古风格
        ColorMatrix colorMatrix6 = new ColorMatrix(new float[]{
                1/2f,1/2f,1/2f,0,0,
                1/3f,1/3f,1/3f,0,0,
                1/4f,1/4f,1/4f,0,0,
                0,0,0,1f,0,
        });
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);

        paint.setColorFilter(filter);

        canvas.drawBitmap(bitmap, 0, 0, paint);

        return grayBitmap;
    }
    public static Bitmap dipian(Bitmap bitmap){
        int width, height;
        width = bitmap.getWidth();
        height = bitmap.getHeight();

        Bitmap grayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(grayBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true); // 设置抗锯齿

        //一，数组矩阵的方法

    /*float[] array = {1, 0, 0, 0, 100,
                     0, 1, 0, 0, 100,
					 0, 0, 1, 0, 0,
					 0, 0, 0, 1, 0};
	ColorMatrix colorMatrix = new ColorMatrix(array);
	*/

        float[] array = {0.393f,0.769f,0.189f,0,0,
                0.349f,0.686f,0.168f,0,0,
                0.272f,0.534f,0.131f,0,0,
                0,0,0,1,0};
        ColorMatrix colorMatrix1 = new ColorMatrix(array);
        //二，把饱和度设置为0 就可以得到灰色（黑白)的图片
        //ColorMatrix colorMatrix = new ColorMatrix();
        //colorMatrix.setSaturation(0);

        //曝光效果
        ColorMatrix colorMatrix2 = new ColorMatrix(new float[]{
                -1,0,0,0,255,
                0,-1,0,0,255,
                0,0,-1,0,255,
                0,0,0,1,0,
        });
        //美白效果
        ColorMatrix colorMatrix3 = new ColorMatrix(new float[]{
                1.2f,0,0,0,0,
                0,1.2f,0,0,0,
                0,0,1.2f,0,0,
                0,0,0,1.2f,0,
        });
        //黑白图片
        ColorMatrix colorMatrix4 = new ColorMatrix(new float[]{
                0.213f, 0.715f,0.072f,0,0,
                0.213f, 0.715f,0.072f,0,0,
                0.213f, 0.715f,0.072f,0,0,
                0,      0,      0,    1f,0,
        });
        //原始效果
        ColorMatrix colorMatrix5 = new ColorMatrix(new float[]{
                1f,0,0,0,0,
                0,1f,0,0,0,
                0,0,1f,0,0,
                0,0,0,1f,0,
        });
        //复古风格
        ColorMatrix colorMatrix6 = new ColorMatrix(new float[]{
                1/2f,1/2f,1/2f,0,0,
                1/3f,1/3f,1/3f,0,0,
                1/4f,1/4f,1/4f,0,0,
                0,0,0,1f,0,
        });
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix2);

        paint.setColorFilter(filter);

        canvas.drawBitmap(bitmap, 0, 0, paint);

        return grayBitmap;
    }
    public static Bitmap meibai(Bitmap bitmap){
        int width, height;
        width = bitmap.getWidth();
        height = bitmap.getHeight();

        Bitmap grayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(grayBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true); // 设置抗锯齿

        //一，数组矩阵的方法

    /*float[] array = {1, 0, 0, 0, 100,
                     0, 1, 0, 0, 100,
					 0, 0, 1, 0, 0,
					 0, 0, 0, 1, 0};
	ColorMatrix colorMatrix = new ColorMatrix(array);
	*/

        float[] array = {0.393f,0.769f,0.189f,0,0,
                0.349f,0.686f,0.168f,0,0,
                0.272f,0.534f,0.131f,0,0,
                0,0,0,1,0};
        ColorMatrix colorMatrix1 = new ColorMatrix(array);
        //二，把饱和度设置为0 就可以得到灰色（黑白)的图片
        //ColorMatrix colorMatrix = new ColorMatrix();
        //colorMatrix.setSaturation(0);

        //曝光效果
        ColorMatrix colorMatrix2 = new ColorMatrix(new float[]{
                -1,0,0,0,255,
                0,-1,0,0,255,
                0,0,-1,0,255,
                0,0,0,1,0,
        });
        //美白效果
        ColorMatrix colorMatrix3 = new ColorMatrix(new float[]{
                1.2f,0,0,0,0,
                0,1.2f,0,0,0,
                0,0,1.2f,0,0,
                0,0,0,1.2f,0,
        });
        //黑白图片
        ColorMatrix colorMatrix4 = new ColorMatrix(new float[]{
                0.213f, 0.715f,0.072f,0,0,
                0.213f, 0.715f,0.072f,0,0,
                0.213f, 0.715f,0.072f,0,0,
                0,      0,      0,    1f,0,
        });
        //原始效果
        ColorMatrix colorMatrix5 = new ColorMatrix(new float[]{
                1f,0,0,0,0,
                0,1f,0,0,0,
                0,0,1f,0,0,
                0,0,0,1f,0,
        });
        //复古风格
        ColorMatrix colorMatrix6 = new ColorMatrix(new float[]{
                1/2f,1/2f,1/2f,0,0,
                1/3f,1/3f,1/3f,0,0,
                1/4f,1/4f,1/4f,0,0,
                0,0,0,1f,0,
        });
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix3);

        paint.setColorFilter(filter);

        canvas.drawBitmap(bitmap, 0, 0, paint);


        return grayBitmap;
    }

    /**
     * 复古
     * @param bitmap
     * @return
     */
    public static Bitmap fugu(Bitmap bitmap){
        int width, height;
        width = bitmap.getWidth();
        height = bitmap.getHeight();

        Bitmap grayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(grayBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true); // 设置抗锯齿

        //一，数组矩阵的方法

    /*float[] array = {1, 0, 0, 0, 100,
                     0, 1, 0, 0, 100,
					 0, 0, 1, 0, 0,
					 0, 0, 0, 1, 0};
	ColorMatrix colorMatrix = new ColorMatrix(array);
	*/

        float[] array = {0.393f,0.769f,0.189f,0,0,
                0.349f,0.686f,0.168f,0,0,
                0.272f,0.534f,0.131f,0,0,
                0,0,0,1,0};
        ColorMatrix colorMatrix1 = new ColorMatrix(array);
        //二，把饱和度设置为0 就可以得到灰色（黑白)的图片
        //ColorMatrix colorMatrix = new ColorMatrix();
        //colorMatrix.setSaturation(0);

        //曝光效果
        ColorMatrix colorMatrix2 = new ColorMatrix(new float[]{
                -1,0,0,0,255,
                0,-1,0,0,255,
                0,0,-1,0,255,
                0,0,0,1,0,
        });
        //美白效果
        ColorMatrix colorMatrix3 = new ColorMatrix(new float[]{
                1.2f,0,0,0,0,
                0,1.2f,0,0,0,
                0,0,1.2f,0,0,
                0,0,0,1.2f,0,
        });
        //黑白图片
        ColorMatrix colorMatrix4 = new ColorMatrix(new float[]{
                0.213f, 0.715f,0.072f,0,0,
                0.213f, 0.715f,0.072f,0,0,
                0.213f, 0.715f,0.072f,0,0,
                0,      0,      0,    1f,0,
        });
        //原始效果
        ColorMatrix colorMatrix5 = new ColorMatrix(new float[]{
                1f,0,0,0,0,
                0,1f,0,0,0,
                0,0,1f,0,0,
                0,0,0,1f,0,
        });
        //复古风格
        ColorMatrix colorMatrix6 = new ColorMatrix(new float[]{
                1/2f,1/2f,1/2f,0,0,
                1/3f,1/3f,1/3f,0,0,
                1/4f,1/4f,1/4f,0,0,
                0,0,0,1f,0,
        });
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix6);

        paint.setColorFilter(filter);

        canvas.drawBitmap(bitmap, 0, 0, paint);

        return grayBitmap;
    }

    /**
     *反射
     * @param bitmap
     * @return
     */
    public static Bitmap fanshe(Bitmap bitmap){
        int width, height;
        width = bitmap.getWidth();
        height = bitmap.getHeight();

        Bitmap grayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(grayBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true); // 设置抗锯齿

        //一，数组矩阵的方法

    /*float[] array = {1, 0, 0, 0, 100,
                     0, 1, 0, 0, 100,
					 0, 0, 1, 0, 0,
					 0, 0, 0, 1, 0};
	ColorMatrix colorMatrix = new ColorMatrix(array);
	*/

        float[] array = {0.393f,0.769f,0.189f,0,0,
                0.349f,0.686f,0.168f,0,0,
                0.272f,0.534f,0.131f,0,0,
                0,0,0,1,0};
        ColorMatrix colorMatrix1 = new ColorMatrix(array);
        //二，把饱和度设置为0 就可以得到灰色（黑白)的图片
        //ColorMatrix colorMatrix = new ColorMatrix();
        //colorMatrix.setSaturation(0);

        //曝光效果
        ColorMatrix colorMatrix2 = new ColorMatrix(new float[]{
                -1,0,0,0,255,
                0,-1,0,0,255,
                0,0,-1,0,255,
                0,0,0,1,0,
        });
        //美白效果
        ColorMatrix colorMatrix3 = new ColorMatrix(new float[]{
                1.2f,0,0,0,0,
                0,1.2f,0,0,0,
                0,0,1.2f,0,0,
                0,0,0,1.2f,0,
        });
        //黑白图片
        ColorMatrix colorMatrix4 = new ColorMatrix(new float[]{
                0.213f, 0.715f,0.072f,0,0,
                0.213f, 0.715f,0.072f,0,0,
                0.213f, 0.715f,0.072f,0,0,
                0,      0,      0,    1f,0,
        });
        //原始效果
        ColorMatrix colorMatrix5 = new ColorMatrix(new float[]{
                1f,0,0,0,0,
                0,1f,0,0,0,
                0,0,1f,0,0,
                0,0,0,1f,0,
        });
        //复古风格
        ColorMatrix colorMatrix6 = new ColorMatrix(new float[]{
                1/2f,1/2f,1/2f,0,0,
                1/3f,1/3f,1/3f,0,0,
                1/4f,1/4f,1/4f,0,0,
                0,0,0,1f,0,
        });
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix6);

        paint.setColorFilter(filter);

        canvas.drawBitmap(bitmap, 0, 0, paint);

        return grayBitmap;
    }
    /**
     * 二值
     * @param bitmap
     * @return
     */
    public static Bitmap erzhi(Bitmap bitmap){
        int width, height;
        width = bitmap.getWidth();
        height = bitmap.getHeight();

        Bitmap grayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(grayBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true); // 设置抗锯齿

        //一，数组矩阵的方法

    /*float[] array = {1, 0, 0, 0, 100,
                     0, 1, 0, 0, 100,
					 0, 0, 1, 0, 0,
					 0, 0, 0, 1, 0};
	ColorMatrix colorMatrix = new ColorMatrix(array);
	*/

        float[] array = {0.393f,0.769f,0.189f,0,0,
                0.349f,0.686f,0.168f,0,0,
                0.272f,0.534f,0.131f,0,0,
                0,0,0,1,0};
        ColorMatrix colorMatrix1 = new ColorMatrix(array);
        //二，把饱和度设置为0 就可以得到灰色（黑白)的图片
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(100);

        //曝光效果
        ColorMatrix colorMatrix2 = new ColorMatrix(new float[]{
                -1,0,0,0,255,
                0,-1,0,0,255,
                0,0,-1,0,255,
                0,0,0,1,0,
        });
        //美白效果
        ColorMatrix colorMatrix3 = new ColorMatrix(new float[]{
                0.5f,0,0,0,0,
                0,0.5f,0,0,0,
                0,0,0.5f,0,0,
                0,0,0,1,0,
        });
        //黑白图片
        ColorMatrix colorMatrix4 = new ColorMatrix(new float[]{
                0.213f, 0.715f,0.072f,0,0,
                0.213f, 0.715f,0.072f,0,0,
                0.213f, 0.715f,0.072f,0,0,
                0,      0,      0,    1f,0,
        });
        //原始效果
        ColorMatrix colorMatrix5 = new ColorMatrix(new float[]{
                1f,0,0,0,0,
                0,1f,0,0,0,
                0,0,1f,0,0,
                0,0,0,1f,0,
        });
        //复古风格
        ColorMatrix colorMatrix6 = new ColorMatrix(new float[]{
                1/2f,1/2f,1/2f,0,0,
                1/3f,1/3f,1/3f,0,0,
                1/4f,1/4f,1/4f,0,0,
                0,0,0,1f,0,
        });
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix3);

        paint.setColorFilter(filter);

        canvas.drawBitmap(bitmap, 0, 0, paint);

        return grayBitmap;
    }

    /**
     * 浪漫
     * @param bitmap
     * @return
     */
    public static Bitmap langman(Bitmap bitmap){
        int width, height;
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        Bitmap grayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(grayBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true); // 设置抗锯齿
        ColorMatrix colorMatrix = new ColorMatrix(colormatrix_menghuan);
        //二，把饱和度设置为0 就可以得到灰色（黑白)的图片

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(filter);
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return grayBitmap;
    }
    /**
     * 酒红
     * @param bitmap
     * @return
     */
    public static Bitmap jiuhong(Bitmap bitmap){
        int width, height;
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        Bitmap grayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(grayBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true); // 设置抗锯齿
        ColorMatrix colorMatrix = new ColorMatrix(colormatrix_jiuhong);
        //二，把饱和度设置为0 就可以得到灰色（黑白)的图片

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(filter);
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return grayBitmap;
    }
    /**
     * 清宁
     * @param bitmap
     * @return
     */
    public static Bitmap qingning(Bitmap bitmap){
        int width, height;
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        Bitmap grayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(grayBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true); // 设置抗锯齿
        ColorMatrix colorMatrix = new ColorMatrix(colormatrix_qingning);
        //二，把饱和度设置为0 就可以得到灰色（黑白)的图片

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(filter);
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return grayBitmap;
    }

}
