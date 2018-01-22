package com.xloger.demo.imagetransform.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.xloger.demo.imagetransform.R;
import com.xloger.demo.imagetransform.view.HandWriteView;

public class HandWriteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hand_write);
        Bitmap bitmap=null;
        String path=getIntent().getStringExtra("path");
        if (path != null) {
            bitmap = BitmapFactory.decodeFile(path);
        }else {
            bitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
        }
        final HandWriteView handWriteView = (HandWriteView) findViewById(R.id.hand_img);
        Button clearBtn = (Button) findViewById(R.id.hand_clear);

        handWriteView.setImg(bitmap);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handWriteView.clear();
            }
        });

    }
}
