package com.example.managedprofile;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    private TextView tv_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Test s = new Test1();
        s.test();
        tv_test = findViewById(R.id.tv_test);
        tv_test.setText("asasasaaasdasdadasdsadsad");
        tv_test.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= 16) {
                    tv_test.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }else {
                    tv_test.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                int width = tv_test.getWidth();
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        tv_test.getLayoutParams());
                Log.d(TAG, "onCreate: " + layoutParams.width + layoutParams.height);
                Log.d(TAG, "onCreate: " + tv_test.getMeasuredWidth() + tv_test.getMeasuredHeight());
                Bitmap numberBitmap = getNumberBitmap(MainActivity.this, tv_test.getMeasuredWidth(), tv_test.getMeasuredHeight());
                tv_test.setBackground(new BitmapDrawable(numberBitmap));
                Log.d(TAG, "onCreate: " + layoutParams.width + layoutParams.height);
            }
        });
        tv_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_test.setText("adadadas2132131231321dasfsdasdfsdfsdfs");
            }
        });


    }


    public static Bitmap getNumberBitmap(Context context, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        RectF rect = new RectF(0, 0, width, height);
        Paint paint = new Paint();
        // draw background
        paint.setColor(context.getResources().getColor(R.color.mz_controlbar_other_bg));
        canvas.drawRoundRect(rect, height / 2, height / 2, paint);
        return bitmap;
    }
}
