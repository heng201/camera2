package com.example.myfilter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myfilter.GLtexture.GLBitmapTexture;
import com.example.myfilter.GLtexture.GLES20Utils;
import com.example.myfilter.GLtexture.GLTexture;
import com.example.myfilter.filter.DipianDrawer;
import com.example.myfilter.filter.ErzhiDrawer;
import com.example.myfilter.filter.FansheDrawer;
import com.example.myfilter.filter.FugueDrawer;
import com.example.myfilter.filter.JiaopianDrawer;
import com.example.myfilter.filter.JiuhongDrawer;
import com.example.myfilter.filter.LangmanDrawer;
import com.example.myfilter.filter.QingningDrawer;
import com.example.myfilter.filter.FendaiDrawer;
import com.example.myfilter.filter.TestSaveDrawer;
import com.example.myfilter.filter.YueguangDrawer;
import com.example.myfilter.utils.CameraEngine;
import com.example.myfilter.utils.DirectDrawer;
import com.example.myfilter.utils.FilterTool;
import com.example.myfilter.utils.HeibaiDraw;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLSurfaceView.RENDERMODE_WHEN_DIRTY;

public class MainActivity extends Activity implements View.OnClickListener {

    private static String TAG = "MainActivity";
    private GLSurfaceView glsv_glsurfaceview;
    private SurfaceTexture surfaceTexture;
    private Camera mCamera;
    private int texttureID;
    private DirectDrawer directDrawer;
    private Button btn_takephoto;
    private Button btn_heibai;
    private Button btn_erzhi;
    private Button btn_fanshe;
    private Button btn_fugu;
    private Button btn_dipian;
    private Button btn_langman;
    private Button btn_jiuhong;
    private Button btn_qingning;
    private Button btn_fendai;
    private Button btn_jiaopian;
    private JiaopianDrawer jiaopianDrawer;
    private Button btn_yueguang;
    private YueguangDrawer yueguangDrawer;
    private HeibaiDraw heibaiDraw;
    private FansheDrawer fansheDrawer;
    private ErzhiDrawer erzhiDrawer;
    private FugueDrawer fugueDrawer;
    private DipianDrawer dipianDrawer;
    private LangmanDrawer langmanDrawer;
    private JiuhongDrawer jiuhongDrawer;
    private QingningDrawer qingningDrawer;
    private FendaiDrawer fendaiDrawer;
    private TestSaveDrawer testSaveDrawer;
    private int lvjingID = 0;
    private Bitmap testBitmap;
    /**
     * 从照片到texture
     */
    private GLTexture textureFromBitmap;
    private int textureID;
    /**
     * 拍照的照片
     *
     */
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化
        glsv_glsurfaceview = findViewById(R.id.glsv_glsurfaceview);
        glsv_glsurfaceview.setEGLContextClientVersion(2);

        glsv_glsurfaceview.setRenderer(new MyRender());
        glsv_glsurfaceview.setRenderMode(RENDERMODE_WHEN_DIRTY);
        btn_takephoto = findViewById(R.id.btn_takephoto);
        btn_heibai = findViewById(R.id.btn_heibai);
        btn_fanshe = findViewById(R.id.btn_fanshe);
        btn_fanshe.setOnClickListener(this);
        btn_erzhi = findViewById(R.id.btn_erzhi);
        btn_erzhi.setOnClickListener(this);
        btn_fugu = findViewById(R.id.btn_fugu);
        btn_fugu.setOnClickListener(this);
        btn_dipian = findViewById(R.id.btn_dipian);
        btn_dipian.setOnClickListener(this);
        btn_langman = findViewById(R.id.btn_langman);
        btn_langman.setOnClickListener(this);
        btn_jiuhong = findViewById(R.id.btn_jiuhong);
        btn_jiuhong.setOnClickListener(this);
        btn_qingning = findViewById(R.id.btn_qingning);
        btn_qingning.setOnClickListener(this);
        btn_fendai = findViewById(R.id.btn_fendai);
        btn_fendai.setOnClickListener(this);
        btn_jiaopian = findViewById(R.id.btn_jiaopian);
        btn_jiaopian.setOnClickListener(this);
        btn_yueguang = findViewById(R.id.btn_yueguang);
        btn_yueguang.setOnClickListener(this);



        btn_heibai.setOnClickListener(this);
        btn_takephoto.setOnClickListener(this);
        testBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.test);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_takephoto:
                mCamera.takePicture(null,null,new MyPictureCallback());
                break;
            case R.id.btn_heibai:
                lvjingID = 1;
                break;
            case R.id.btn_fanshe:
                lvjingID = 2;
                break;
            case R.id.btn_erzhi:
                lvjingID = 3;
                break;
            case R.id.btn_fugu:
                lvjingID = 4;
                break;
            case R.id.btn_dipian:
                lvjingID = 5;
                break;
            case R.id.btn_langman:
                lvjingID = 6;
                break;
            case R.id.btn_jiuhong:
                lvjingID = 7;
                break;
            case R.id.btn_qingning:
                lvjingID = 8;
                break;
            case R.id.btn_fendai:
                lvjingID = 9;
                break;
            case R.id.btn_jiaopian:
                lvjingID = 10;
                break;
            case R.id.btn_yueguang:
                lvjingID = 11;
                break;



        }
    }

    /**
     * 单拍的照片获取
     */
    class MyPictureCallback implements Camera.PictureCallback {

        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            camera.startPreview();
            long startTime=System.currentTimeMillis();   //获取开始时间
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            if (bitmap != null) {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssmm").format(new Date());
                String bitmapPath = "/sdcard/" + timeStamp + ".png";
                Log.e("path", bitmapPath);
                /*textureFromBitmap = GLTexture.createTextureFromBitmap(bitmap);
                textureID = textureFromBitmap.getTextureID();
                TestSaveDrawer testSaveDrawer = new TestSaveDrawer(MainActivity.this,textureID);
                testSaveDrawer.draw(bitmap);
                textureFromBitmap.save(bitmap);*/
                switch (lvjingID) {
                    case  1:
                        bitmap = FilterTool.heibai(bitmap);
                        break;
                    case 2:
                        //bitmap = FilterTool.fanshe(bitmap);
                        break;
                    case 3:
                        bitmap = FilterTool.fanXiang(bitmap);
                        break;
                    case 4:
                        bitmap = FilterTool.fugu(bitmap);
                        break;
                    case 5:
                        bitmap = FilterTool.dipian(bitmap);
                        break;
                    case 6:
                        bitmap = FilterTool.langman(bitmap);
                        Toast.makeText(MainActivity.this, "jignur1", Toast.LENGTH_SHORT).show();
                        break;
                    case 7:
                        bitmap = FilterTool.jiuhong(bitmap);
                        Toast.makeText(MainActivity.this, "jignur2", Toast.LENGTH_SHORT).show();
                        break;
                    case 8:
                        bitmap = FilterTool.qingning(bitmap);
                        Toast.makeText(MainActivity.this, "jignur3", Toast.LENGTH_SHORT).show();
                        break;


                }
                //bitmap = FilterTool.heibai(bitmap);
                new saveBitmap(bitmapPath, bitmap).start();
                //long startTime=System.currentTimeMillis();   //获取开始时间
                long endTime=System.currentTimeMillis(); //获取结束时间
                System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
                Toast.makeText(MainActivity.this, ""+(endTime-startTime), Toast.LENGTH_SHORT).show();


            }
        }
    }



    /**
     * 保存图片的线程
     */
    private class saveBitmap extends Thread {
        /**
         * The JPEG image
         */
        private final String bitNamePath;
        /**
         * The file we save the image into.
         */
        private final Bitmap mBitmap;

        saveBitmap(String bitNamePath, Bitmap mBitmap) {
            this.bitNamePath = bitNamePath;
            this.mBitmap = mBitmap;
        }

        @Override
        public void run() {
            File f = new File(bitNamePath);
            try {
                f.createNewFile();
            } catch (IOException e) {
                Log.d(TAG, "saveBitmap: 保存图片是出错");
            }
            FileOutputStream fOut = null;
            try {
                fOut = new FileOutputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            try {
                fOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    /**
     * 保存图片的线程
     */
    private class ImageSaver extends Thread {
        /**
         * The JPEG image
         */
        private final byte[] bytes;
        /**
         * The file we save the image into.
         */
        private final String filepath;

        ImageSaver(byte[] bytes, String filepath) {
            this.bytes = bytes;
            this.filepath = filepath;
        }

        @Override
        public void run() {
            File mFile = new File(filepath);
            try {
                mFile.createNewFile();
            } catch (IOException e) {
                Log.d(TAG, "saveBitmap: 保存图片是出错");
            }
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(mFile);
                output.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != output) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

        public static int createOESTextureObject() {
        int[] tex = new int[1];
        //生成一个纹理
        GLES20.glGenTextures(1, tex, 0);
        //将此纹理绑定到外部纹理上
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex[0]);
        //设置纹理过滤参数
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        //解除纹理绑定
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        return tex[0];
    }
    class MyRender implements GLSurfaceView.Renderer{

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            texttureID = createOESTextureObject();
            surfaceTexture = new SurfaceTexture(texttureID);

            surfaceTexture.setOnFrameAvailableListener(new MyOnFrameAvailableListener());
            directDrawer = new DirectDrawer(texttureID);
            heibaiDraw = new HeibaiDraw(texttureID);
            fansheDrawer = new FansheDrawer(texttureID);
            erzhiDrawer = new ErzhiDrawer(texttureID);
            fugueDrawer = new FugueDrawer(texttureID);
            dipianDrawer = new DipianDrawer(texttureID);
            langmanDrawer = new LangmanDrawer(texttureID);
            jiuhongDrawer = new JiuhongDrawer(texttureID);
            qingningDrawer = new QingningDrawer(texttureID);
            fendaiDrawer =  new FendaiDrawer(MainActivity.this,texttureID);
            jiaopianDrawer = new JiaopianDrawer(MainActivity.this,texttureID);
            yueguangDrawer = new YueguangDrawer(MainActivity.this,texttureID);
            testSaveDrawer = new TestSaveDrawer(MainActivity.this,texttureID);

            if(CameraEngine.openCamera()) {
                mCamera = CameraEngine.getCamera();
            }

        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int i, int i1) {
            //GLES20.glViewport(0, 0, i, i1);
            try {
                mCamera.setPreviewTexture(surfaceTexture);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            float[] mtx = new float[16];
            surfaceTexture.updateTexImage();
            if(lvjingID == 0) {
                directDrawer.draw(mtx);
            }else if(lvjingID == 1) {
                heibaiDraw.draw(mtx);
            }else if(lvjingID == 2) {
                fansheDrawer.draw(mtx);
            }else if(lvjingID == 3) {
                erzhiDrawer.draw(mtx);
            }else if(lvjingID == 4) {
                fugueDrawer.draw(mtx);
            }else if(lvjingID == 5) {
                dipianDrawer.draw(mtx);
            }else if(lvjingID == 6) {
                langmanDrawer.draw(mtx);
            }else if(lvjingID == 7) {
                jiuhongDrawer.draw(mtx);
            }else if(lvjingID == 8) {
                qingningDrawer.draw(mtx);
            }else if(lvjingID == 9) {
                fendaiDrawer.draw(mtx);
            }else if(lvjingID == 10) {
                jiaopianDrawer.draw(mtx);
            }else if(lvjingID == 11) {
                //yueguangDrawer.draw(mtx);
                testSaveDrawer.draw(testBitmap);
                long startTime=System.currentTimeMillis();   //获取开始时间
                if(bool && lvjingID == 11) {
                    testSaveDrawer.sendImage(800,1000);
                    bool = false;
                    long endTime=System.currentTimeMillis(); //获取结束时间
                    System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
                    //Toast.makeText(MainActivity.this, ""+(endTime-startTime), Toast.LENGTH_SHORT).show();
                }
            }

        }
    }


    private Boolean bool = true;
    class MyOnFrameAvailableListener implements SurfaceTexture.OnFrameAvailableListener {

        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {

            glsv_glsurfaceview.requestRender();


        }
    }
}
