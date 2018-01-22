package com.xloger.demo.imagetransform;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.xloger.demo.imagetransform.activity.HandWriteActivity;
import com.xloger.demo.imagetransform.tool.FilterTool;
import com.xloger.demo.imagetransform.tool.ImgTool;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int Android43=43;
    private static final int Android44=44;
    private ImageView img;
    private Bitmap bitmap;
    private String path=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
        findViewById(R.id.get_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);//ACTION_OPEN_DOCUMENT
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/jpeg");
                if(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.KITKAT){
                    startActivityForResult(intent, Android44);
                }else{
                    startActivityForResult(intent, Android43);
                }
            }
        });
        img = (ImageView) findViewById(R.id.img);
        findViewById(R.id.clear_filter).setOnClickListener(this);
        findViewById(R.id.hand_write).setOnClickListener(this);
        findViewById(R.id.oldRemember).setOnClickListener(this);
        findViewById(R.id.blur).setOnClickListener(this);
        findViewById(R.id.sharpen).setOnClickListener(this);
        findViewById(R.id.fuDiao).setOnClickListener(this);
        findViewById(R.id.fuPian).setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        Log.e("xlogerImg",data.getData().toString());
//        Toast.makeText(this,"data:"+data.getData(),Toast.LENGTH_SHORT).show();
        if (requestCode==Android43){
            Log.e("版本","4.3");
            path=ImgTool.selectImage(this,data.getData());
        }else if (requestCode==Android44){
            Log.e("版本","4.4");
            path=ImgTool.getPath(this,data.getData());
        }
//        Toast.makeText(this,"path:"+path,Toast.LENGTH_SHORT).show();
        Log.e("xlogerImg","path:"+path);
        if (path != null) {
            bitmap = BitmapFactory.decodeFile(path);
            img.setImageBitmap(bitmap);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.clear_filter:
                img.setImageBitmap(bitmap);
                break;
            case R.id.hand_write:
                Intent intent=new Intent(MainActivity.this, HandWriteActivity.class);
                intent.putExtra("path",path);
                startActivity(intent);
                break;
            case R.id.oldRemember:
                img.setImageBitmap(FilterTool.oldRemember(bitmap));
                break;
            case R.id.blur:
                img.setImageBitmap(FilterTool.blurImageAmeliorate(bitmap,MainActivity.this));
                break;
            case R.id.sharpen:
                img.setImageBitmap(FilterTool.sharpenImageAmeliorate(bitmap));
                break;
            case R.id.fuDiao:
                img.setImageBitmap(FilterTool.fuDiao(bitmap));
                break;
            case R.id.fuPian:
                img.setImageBitmap(FilterTool.diPian(bitmap));
        }
    }


}
