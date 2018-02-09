package com.example.myfilter.utils;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by zhangheng1 on 2017/12/25.
 */




/**
 * Created by zhangheng1 on 2017/12/20.
 */

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
    public class HeibaiDraw  {
        private  String vertexShaderCode =
                "attribute vec4 vPosition;" +
                        "attribute vec2 inputTextureCoordinate;" +
                        "varying vec2 textureCoordinate;" +
                        "void main()" +
                        "{" +
                        "gl_Position = vPosition;" +
                        "textureCoordinate = inputTextureCoordinate;" +
                        "}";

        private  String fragmentShaderCode =
                "#extension GL_OES_EGL_image_external : require\n" +
                        "precision mediump float;" +
                        "uniform sampler2D lut_tab;\n" +
                        "varying vec2 textureCoordinate;\n" +
                        //"uniform samplerExternalOES s_texture;\n" +
                        "void main() {" +
                        "vec4 temColor = texture2D( lut_tab, textureCoordinate );\n"+
                        "float max = (temColor.r + temColor.g + temColor.b)/3.0;\n"+
                        "  gl_FragColor = temColor;\n" +//vec4(max,max,max,temColor.w)
                        "}";

        private FloatBuffer vertexBuffer, textureVerticesBuffer;
        private ShortBuffer drawListBuffer;
        private final int mProgram;
        private int mPositionHandle;
        private int mTextureCoordHandle;

        private short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

        // number of coordinates per vertex in this array
        private static final int COORDS_PER_VERTEX = 2;

        private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

        static float squareCoords[] = {


// /
//                1.0f,  1.0f,
//                -1.0f,  1.0f,
//                -1.0f, -1.0f,
//                1.0f, -1.0f,
                -1.0f,  1.0f,
                -1.0f, -1.0f,
                1.0f, -1.0f,
                1.0f,  1.0f,

        };


        static float textureVertices[] = {


                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,

//                0.0f, 1.0f,
//                1.0f, 1.0f,
//                1.0f, 0.0f,
//                0.0f, 0.0f,


        };

        private int texture;
        private int texture1;
        private Context context;
        private ByteBuffer byteBuffer;

        public void setByteBuffer(ByteBuffer byteBuffer) {
            this.byteBuffer = byteBuffer;
        }

        public void setTexture(int texture) {
            this.texture1 = texture;
        }

        public HeibaiDraw(Context context,int texture)
        {
            this.context = context;
            this.texture = texture;
            // initialize vertex byte buffer for shape coordinates
            ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
            bb.order(ByteOrder.nativeOrder());
            vertexBuffer = bb.asFloatBuffer();
            vertexBuffer.put(squareCoords);
            vertexBuffer.position(0);

            // initialize byte buffer for the draw list
            ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
            dlb.order(ByteOrder.nativeOrder());
            drawListBuffer = dlb.asShortBuffer();
            drawListBuffer.put(drawOrder);
            drawListBuffer.position(0);

            ByteBuffer bb2 = ByteBuffer.allocateDirect(textureVertices.length * 4);
            bb2.order(ByteOrder.nativeOrder());
            textureVerticesBuffer = bb2.asFloatBuffer();
            textureVerticesBuffer.put(textureVertices);
            textureVerticesBuffer.position(0);

            int vertexShader    = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
            int fragmentShader  = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

            mProgram = GLES20.glCreateProgram();             // create empty OpenGL ES Program
            GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
            GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
            GLES20.glLinkProgram(mProgram);                  // creates OpenGL ES program executables
        }

        private int mGLUniformTexture;
        private int inputTextureHandles = -1;
        public void draw(float[] mtx)
        {
            GLES20.glUseProgram(mProgram);

//            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture);

            mGLUniformTexture = GLES20.glGetUniformLocation(mProgram, "lut_tab");
            //inputTextureHandles = OpenGlUtils.loadTexture(context, "filter/test.jpg");
            //inputTextureHandles = OpenGlUtils.loadTexturefromBuffer(byteBuffer,800,1000,inputTextureHandles);

            Log.d("draw: ", "draw: "+ inputTextureHandles);
            //filtertable_rgb_second_sunny，filtertable_rgb_second_ink，filtertable_rgb_mono，lut3d_table_moonlight

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 );
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture1);
            Log.d("draw: ", "draw: "+ texture1);
            GLES20.glUniform1i(mGLUniformTexture,0);
            // get handle to vertex shader's vPosition member
            mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
            // Enable a handle to the triangle vertices
            GLES20.glEnableVertexAttribArray(mPositionHandle);
            // Prepare the <insert shape here> coordinate data
            GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
            mTextureCoordHandle = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate");
            GLES20.glEnableVertexAttribArray(mTextureCoordHandle);
//        textureVerticesBuffer.clear();
//        textureVerticesBuffer.put( transformTextureCoordinates( textureVertices, mtx ));
//        textureVerticesBuffer.position(0);
            GLES20.glVertexAttribPointer(mTextureCoordHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, textureVerticesBuffer);


            GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

            // Disable vertex array
            GLES20.glDisableVertexAttribArray(mPositionHandle);
            GLES20.glDisableVertexAttribArray(mTextureCoordHandle);
        }

        private  int loadShader(int type, String shaderCode){

            // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
            // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
            int shader = GLES20.glCreateShader(type);

            // add the source code to the shader and compile it
            GLES20.glShaderSource(shader, shaderCode);
            GLES20.glCompileShader(shader);

            return shader;
        }
        private float[] transformTextureCoordinates( float[] coords, float[] matrix)
        {
            float[] result = new float[ coords.length ];
            float[] vt = new float[4];

            for ( int i = 0 ; i < coords.length ; i += 2 ) {
                float[] v = { coords[i], coords[i+1], 0 , 1  };
                Matrix.multiplyMV(vt, 0, matrix, 0, v, 0);
                result[i] = vt[0];
                result[i+1] = vt[1];
            }
            return result;
        }
    }
