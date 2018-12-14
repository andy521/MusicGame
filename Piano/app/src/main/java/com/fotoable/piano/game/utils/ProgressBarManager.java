package com.fotoable.piano.game.utils;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.fotoable.piano.game.GameRender;
import com.fotoable.piano.game.entity.TextureMode;

import java.nio.FloatBuffer;

/**
 * Created by damon on 21/06/2017.
 */

public class ProgressBarManager {


    private static final String TAG = "ProgressBarManager";


    /**
     * 纹理 顶点着色器
     */
    private final String vertexShaderCode =

            "attribute vec4 a_position;" +
                    "attribute vec2 a_tex_coor;" +
                    "varying vec2 v_tex_coor;" +
                    "uniform mat4 u_matrix;" +
                    "void main(){" +
                    "    v_tex_coor = a_tex_coor;" +
                    "    gl_Position = u_matrix*a_position;" +
                    "}";

    /**
     * 纹理 片段着色器
     */
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec2 v_tex_coor;" +
                    "uniform sampler2D u_tex_id;" +
                    "uniform float alpha;" +
                    "void main() {" +
                    "    vec4 color=texture2D( u_tex_id, v_tex_coor);" +
                    "   color.a *= alpha;" +
                    "    gl_FragColor = color;" +
                    "}";

    //顶点坐标
    public TextureMode texture;
    public FloatBuffer verBuffer;
    public FloatBuffer texBuffer;



    public int mProgramNode;
    public int positionNode;
    public int coorNode;
    public int matrixNode;
    public int texNode;
    public int alphaNode;

    public GameRender gameRender;


    public float translateY;
    public float translateX=-GLConstants.getScreenRatioWh() * 2;
    public float scaleX=0;
    public static final float scaleY=0.02f;
    public ProgressBarManager(GameRender gameRender) {
        this.gameRender = gameRender;
    }

    public void init() {
        initTexture();
        initProgramLine();
        initBuffer();
        initData();
    }
private void initData(){
    scaleX = GLConstants.getScreenWidth() / GLConstants.getScreenHeight();
    translateY = (GLConstants.getScreenHeight()/2-scaleY*GLConstants.getScreenHeight())/(GLConstants.getScreenHeight()/2);
    translateX=-GLConstants.getScreenRatioWh() * 2;
}
    private void initTexture() {
        texture = new TextureMode();
        texture.pictureName = "play_progress_bar.png";
        GLUtils.loadTexture(texture, true);

    }


    private void initProgramLine() {
        int vertexShader = GLUtils.LoadShader(vertexShaderCode, GLES20.GL_VERTEX_SHADER);
        int fragmentShader = GLUtils.LoadShader(fragmentShaderCode, GLES20.GL_FRAGMENT_SHADER);
        //创建一个空的OpenGLES程序
        mProgramNode = GLES20.glCreateProgram();
        //将顶点着色器加入到程序
        GLES20.glAttachShader(mProgramNode, vertexShader);
        //将片元着色器加入到程序中
        GLES20.glAttachShader(mProgramNode, fragmentShader);
        //连接到着色器程序
        GLES20.glLinkProgram(mProgramNode);

        positionNode = GLES20.glGetAttribLocation(mProgramNode, "a_position");
        coorNode = GLES20.glGetAttribLocation(mProgramNode, "a_tex_coor");
        matrixNode = GLES20.glGetUniformLocation(mProgramNode, "u_matrix");
        texNode = GLES20.glGetUniformLocation(mProgramNode, "u_tex_id");
        alphaNode = GLES20.glGetUniformLocation(mProgramNode, "alpha");
    }

    protected void initBuffer() {
        verBuffer = GLUtils.CreateVertexArray(GLUtils.pos);
        texBuffer = GLUtils.CreateVertexArray(GLUtils.coord);
    }

    public void drawProgressBar() {

//        Log.d(TAG, "drawProgressBar");
        GLES20.glUseProgram(mProgramNode);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glBlendEquation(GLES20.GL_FUNC_ADD);

        GLES20.glUniform1f(alphaNode, 1.0f);
        float[] matrix = gameRender.getMatrixFromIndex();
        gameRender.pushMatrix(matrix);
//        Matrix.translateM(mMatrixCurrent, 0, 0, 0, 0);
        Matrix.translateM(matrix, 0, translateX, translateY, 0);
//        Log.e(TAG,"pointerIndex-->>"+pointerIndex+", coor[0]-->>"+coor[0]+", -coor[1]-->>"+(-coor[1]));
        Matrix.scaleM(matrix, 0, scaleX, scaleY, 1f);
        GLES20.glUniformMatrix4fv(matrixNode, 1, false, gameRender.getFinalMatrix(matrix), 0);
        gameRender.popMatrix();



        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.textureId[0]);
        GLES20.glUniform1i(texNode, 0);

        //启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(positionNode);
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(positionNode, 2, GLES20.GL_FLOAT, false, 2 * 4, verBuffer);

        //为纹理准备纹理坐标数据
        GLES20.glEnableVertexAttribArray(coorNode);
        GLES20.glVertexAttribPointer(coorNode, 2, GLES20.GL_FLOAT, false, 0, texBuffer);

        //绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(positionNode);
        GLES20.glDisableVertexAttribArray(coorNode);
        GLES20.glDisable(GLES20.GL_BLEND);
    }
    public void cleanData(){
        initData();
    }
}
