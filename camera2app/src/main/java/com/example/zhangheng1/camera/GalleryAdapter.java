package com.example.zhangheng1.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by zhangheng1 on 2017/12/6.
 */

public class GalleryAdapter extends BaseAdapter {
    private ArrayList<String> pictures;
    private Context context;

    public GalleryAdapter(ArrayList<String> pictures, Context context) {
        this.pictures = pictures;
        this.context = context;
    }

    @Override
    public int getCount() {
        return pictures.size();
    }

    @Override
    public Object getItem(int i) {
        return pictures.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView;
        if (view == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(300, 500));//设置ImageView对象布局
            imageView.setAdjustViewBounds(false);//设置边界对齐
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);//设置刻度的类型
            //imageView.setPadding(8, 8, 8, 8);//设置间距
        } else {
            imageView = (ImageView) view;
        }

        Bitmap bitmap = BitmapFactory.decodeFile(pictures.get(i));
        //imageView.setImageResource(bitmap);//为ImageView设置图片资源
        imageView.setImageBitmap(bitmap);
        return imageView;

    }
}
