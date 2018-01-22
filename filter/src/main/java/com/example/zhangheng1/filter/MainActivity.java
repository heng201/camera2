package com.example.zhangheng1.filter;


import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.example.zhangheng1.filter.view.CameraGLSurfaceView;
import com.seu.magicfilter.MagicEngine;
import com.seu.magicfilter.filter.helper.MagicFilterType;
import com.seu.magicfilter.widget.MagicCameraView;

public class MainActivity extends Activity {


    private CameraGLSurfaceView gl_surface;
    private SurfaceHolder surfaceHolder;
    private SurfaceTexture surfaceTexture;
    private SurfaceView sv_surface;
    private MagicEngine magicEngine;
    private Button btn_change;
    private Button btn_heibai;
    private Button btn_mohu;
    private Button btn_erzhi;
    private int i = 0;

    private final MagicFilterType[] types = new MagicFilterType[]{
            MagicFilterType.NONE,
            MagicFilterType.FAIRYTALE,
            MagicFilterType.SUNRISE,
            MagicFilterType.SUNSET,
            MagicFilterType.WHITECAT,
            MagicFilterType.BLACKCAT,
            MagicFilterType.SKINWHITEN,
            MagicFilterType.HEALTHY,
            MagicFilterType.SWEETS,
            MagicFilterType.ROMANCE,
            MagicFilterType.SAKURA,
            MagicFilterType.WARM,
            MagicFilterType.ANTIQUE,
            MagicFilterType.NOSTALGIA,
            MagicFilterType.CALM,
            MagicFilterType.LATTE,
            MagicFilterType.TENDER,
            MagicFilterType.COOL,
            MagicFilterType.EMERALD,
            MagicFilterType.EVERGREEN,
            MagicFilterType.CRAYON,
            MagicFilterType.SKETCH,
            MagicFilterType.AMARO,
            MagicFilterType.BRANNAN,
            MagicFilterType.BROOKLYN,
            MagicFilterType.EARLYBIRD,
            MagicFilterType.FREUD,
            MagicFilterType.HEFE,
            MagicFilterType.HUDSON,
            MagicFilterType.INKWELL,
            MagicFilterType.KEVIN,
            MagicFilterType.LOMO,
            MagicFilterType.N1977,
            MagicFilterType.NASHVILLE,
            MagicFilterType.PIXAR,
            MagicFilterType.RISE,
            MagicFilterType.SIERRA,
            MagicFilterType.SUTRO,
            MagicFilterType.TOASTER2,
            MagicFilterType.VALENCIA,
            MagicFilterType.WALDEN,
            MagicFilterType.XPROII
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MagicEngine.Builder builder = new MagicEngine.Builder();
        magicEngine = builder
                .build((MagicCameraView)findViewById(R.id.MagicCameraView));
        btn_change = findViewById(R.id.btn_change);
        btn_heibai = findViewById(R.id.btn_heibai);
        btn_mohu = findViewById(R.id.btn_mohu);
        btn_erzhi = findViewById(R.id.btn_erzhi);
        btn_erzhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                magicEngine.setFilter(MagicFilterType.SAKURA);
            }
        });

        btn_mohu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                magicEngine.setFilter(MagicFilterType.SKETCH);
            }
        });
        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                magicEngine.setFilter(types[i % types.length]);
                i++;
            }
        });

        btn_heibai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                magicEngine.setFilter(MagicFilterType.SIERRA);
            }
        });


    }



}
