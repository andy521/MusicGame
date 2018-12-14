package com.fotoable.piano.game.utils;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.support.annotation.IntDef;
import android.util.Log;

import com.fotoable.piano.game.GameRender;
import com.fotoable.piano.game.entity.TextureMode;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.FloatBuffer;

/**
 * Created by damon on 21/06/2017.
 */

public class NodeCircleManager {


    private static final String TAG = "NodeCircleManager";

    @IntDef({NodeType.NODE_TYPE_1, NodeType.NODE_TYPE_2, NodeType.NODE_TYPE_3, NodeType.NODE_TYPE_4})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NodeType {
        //表示只有一个音节 白色
        int NODE_TYPE_1 = 1;
        //有两个平行音节 黄色
        int NODE_TYPE_2 = 2;
        //三个蓝色
        int NODE_TYPE_3 = 3;
        //四个粉色
        int NODE_TYPE_4 = 4;
    }

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


    public TextureMode nodeCircle1;
    public TextureMode nodeCircle2;
    public TextureMode nodeCircle3;
    public TextureMode nodeCircle4;

    public FloatBuffer verBuffer1;
    public FloatBuffer verBuffer2;
    public FloatBuffer verBuffer3;
    public FloatBuffer verBuffer4;

    public FloatBuffer texBuffer1;
    public FloatBuffer texBuffer2;
    public FloatBuffer texBuffer3;
    public FloatBuffer texBuffer4;

    public TextureMode currentNodeCircle;
    public FloatBuffer currentVerBuffer;
    public FloatBuffer currentTexBuffer;


    public int mProgramNode;
    public int positionNode;
    public int coorNode;
    public int matrixNode;
    public int texNode;
    public int alphaNode;

    public GameRender gameRender;


    public NodeCircleManager(GameRender gameRender) {
        this.gameRender = gameRender;
    }

    public void init() {
        initTexture();
        initProgramLine();
        initBuffer();
    }

    private void initTexture() {
        nodeCircle1 = new TextureMode();
        nodeCircle1.pictureName = "node_circle1.png";
        GLUtils.loadTexture(nodeCircle1, true);

        nodeCircle2 = new TextureMode();
        nodeCircle2.pictureName = "node_circle2.png";
        GLUtils.loadTexture(nodeCircle2, true);

        nodeCircle3 = new TextureMode();
        nodeCircle3.pictureName = "node_circle3.png";
        GLUtils.loadTexture(nodeCircle3, true);

        nodeCircle4 = new TextureMode();
        nodeCircle4.pictureName = "node_circle4.png";
        GLUtils.loadTexture(nodeCircle4, true);

    }

    private void initCurrent(@NodeType int nodeType) {

        switch (nodeType) {
            case NodeType.NODE_TYPE_1:
                currentNodeCircle = nodeCircle1;
                currentVerBuffer = verBuffer1;
                currentTexBuffer = texBuffer1;
                break;
            case NodeType.NODE_TYPE_2:
                currentNodeCircle = nodeCircle2;
                currentVerBuffer = verBuffer2;
                currentTexBuffer = texBuffer2;
                break;
            case NodeType.NODE_TYPE_3:
                currentNodeCircle = nodeCircle3;
                currentVerBuffer = verBuffer3;
                currentTexBuffer = texBuffer3;
                break;
            case NodeType.NODE_TYPE_4:
                currentNodeCircle = nodeCircle4;
                currentVerBuffer = verBuffer4;
                currentTexBuffer = texBuffer4;
                break;

        }
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
        verBuffer1 = GLUtils.CreateVertexArray(GLUtils.pos);
        verBuffer2 = GLUtils.CreateVertexArray(GLUtils.pos);
        verBuffer3 = GLUtils.CreateVertexArray(GLUtils.pos);
        verBuffer4 = GLUtils.CreateVertexArray(GLUtils.pos);

        texBuffer1 = GLUtils.CreateVertexArray(GLUtils.coord);
        texBuffer2 = GLUtils.CreateVertexArray(GLUtils.coord);
        texBuffer3 = GLUtils.CreateVertexArray(GLUtils.coord);
        texBuffer4 = GLUtils.CreateVertexArray(GLUtils.coord);
    }

    public void drawNodeCircle(float translateX, float translateY, float scale, @NodeType int nodeType) {

//        Log.d(TAG, "drawNodeCircle");
        initCurrent(nodeType);
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
        Matrix.scaleM(matrix, 0, scale, scale, 1f);
        GLES20.glUniformMatrix4fv(matrixNode, 1, false, gameRender.getFinalMatrix(matrix), 0);
        gameRender.popMatrix();



        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, currentNodeCircle.textureId[0]);
        GLES20.glUniform1i(texNode, 0);

        //启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(positionNode);
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(positionNode, 2, GLES20.GL_FLOAT, false, 2 * 4, currentVerBuffer);

        //为纹理准备纹理坐标数据
        GLES20.glEnableVertexAttribArray(coorNode);
        GLES20.glVertexAttribPointer(coorNode, 2, GLES20.GL_FLOAT, false, 0, currentTexBuffer);

        //绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(positionNode);
        GLES20.glDisableVertexAttribArray(coorNode);
        GLES20.glDisable(GLES20.GL_BLEND);
    }

}
