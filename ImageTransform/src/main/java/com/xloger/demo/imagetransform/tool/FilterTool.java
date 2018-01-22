package com.xloger.demo.imagetransform.tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;

/**
 * Created by xloger on 9月8日.
 * Author:xloger
 * Email:phoenix@xloger.com
 */
public class FilterTool {
    public static Bitmap oldRemember(Bitmap bitmap){
        long start= System.currentTimeMillis();
        int width=bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap bit=Bitmap.createBitmap(width,height, Bitmap.Config.RGB_565);
        int pixColor=0;
        int pixR=0,pixG=0,pixB=0;
        int newR=0,newG=0,newB=0;
        int[] pixels=new int[width*height];
        bitmap.getPixels(pixels,0,width,0,0,width,height);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                pixColor=pixels[width*i+j];
                pixR= Color.red(pixColor);
                pixG=Color.green(pixColor);
                pixB=Color.blue(pixColor);
                newR = (int) (0.393 * pixR + 0.769 * pixG + 0.189 * pixB);
                newG = (int) (0.349 * pixR + 0.686 * pixG + 0.168 * pixB);
                newB = (int) (0.272 * pixR + 0.534 * pixG + 0.131 * pixB);
                int newColor = Color.argb(255, newR > 255 ? 255 : newR, newG > 255 ? 255 : newG, newB > 255 ? 255 : newB);
                pixels[width * i + j] = newColor;
            }
        }

        bit.setPixels(pixels,0,width,0,0,width,height);
        long end=System.currentTimeMillis();
        return bit;
    }


    public static Bitmap blurImageAmeliorate(Bitmap bmp, Context context)
    {
        float radius = 20;
        int width=bmp.getWidth();
        int height = bmp.getHeight();
        Bitmap overlay=Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);

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
        Bitmap bitmap=Bitmap.createBitmap(width,height, Bitmap.Config.RGB_565);
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

        long end=System.currentTimeMillis();
        return bitmap;
    }

    public static Bitmap diPian(Bitmap bmp){
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int[] oldPixels = new int[width*height];
        int[] newPixels = new int[width*height];
        int color;
        int pixelsA,pixelsR,pixelsG,pixelsB;
        Bitmap bitmap=Bitmap.createBitmap(width,height, Bitmap.Config.RGB_565);
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

}
