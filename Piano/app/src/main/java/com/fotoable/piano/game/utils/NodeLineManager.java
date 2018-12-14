package com.fotoable.piano.game.utils;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.fotoable.piano.game.GameRender;

import java.nio.FloatBuffer;

/**
 * Created by damon on 21/06/2017.
 */

public class NodeLineManager {


    private static final String TAG = "NodeLineManager";


    /**
     * 自定义形状 顶点着色器
     */
    private final String vertexShaderCodeShape =

            "attribute vec4 vPosition;" +
                    "uniform mat4 vMatrix;" +
                    "void main(){" +
                    "    gl_Position = vMatrix*vPosition;" +
                    "}";

    /**
     * 自定义形状 片段着色器
     */
    private final String fragmentShaderCodeShape =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    public int mProgramNodeLine;
    public int positionNodeLine;
    public int matrixNodeLine;
    public int colorNodeLine;
    public FloatBuffer mVerBufferNodeLine;

    public GameRender gameRender;


    public float[] color;


    private final float scaleY = 0.01f;

    public NodeLineManager(GameRender gameRender) {
        this.gameRender = gameRender;
    }

    public void init() {
        initProgramLine();
        initBuffer();
    }

    private void initColor(@NodeCircleManager.NodeType int nodeType) {

        switch (nodeType) {
            case NodeCircleManager.NodeType.NODE_TYPE_1:
                color = GameResource.NODE_LINE_COLOR1;
                break;
            case NodeCircleManager.NodeType.NODE_TYPE_2:
                color = GameResource.NODE_LINE_COLOR2;
                break;
            case NodeCircleManager.NodeType.NODE_TYPE_3:
                color = GameResource.NODE_LINE_COLOR3;
                break;
            case NodeCircleManager.NodeType.NODE_TYPE_4:
                color = GameResource.NODE_LINE_COLOR4;
                break;

        }
    }

    private void initProgramLine() {
        int vertexShader = GLUtils.LoadShader(vertexShaderCodeShape, GLES20.GL_VERTEX_SHADER);
        int fragmentShader = GLUtils.LoadShader(fragmentShaderCodeShape, GLES20.GL_FRAGMENT_SHADER);
        //创建一个空的OpenGLES程序
        mProgramNodeLine = GLES20.glCreateProgram();
        //将顶点着色器加入到程序
        GLES20.glAttachShader(mProgramNodeLine, vertexShader);
        //将片元着色器加入到程序中
        GLES20.glAttachShader(mProgramNodeLine, fragmentShader);
        //连接到着色器程序
        GLES20.glLinkProgram(mProgramNodeLine);

        positionNodeLine = GLES20.glGetAttribLocation(mProgramNodeLine, "vPosition");
        matrixNodeLine = GLES20.glGetUniformLocation(mProgramNodeLine, "vMatrix");
        colorNodeLine = GLES20.glGetUniformLocation(mProgramNodeLine, "vColor");
//        opacityShape = GLES20.glGetUniformLocation(mProgramLine, "Opacity");
    }

    protected void initBuffer() {
        mVerBufferNodeLine = GLUtils.CreateVertexArray(GLUtils.pos);
    }

    public void drawNodeLine(float translateX, float translateY, @NodeCircleManager.NodeType int nodeType) {

//        Log.d(TAG, "drawNodeLine");
        initColor(nodeType);
        GLES20.glUseProgram(mProgramNodeLine);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glBlendEquation(GLES20.GL_FUNC_ADD);

        float[] matrix = gameRender.getMatrixFromIndex();
        gameRender.pushMatrix(matrix);
//        Matrix.translateM(mMatrixCurrent, 0, 0, 0, 0);
        Matrix.translateM(matrix, 0, translateX + GLConstants.getGapTwoNodeOffsetX(), translateY, 0);
//        Log.e(TAG,"pointerIndex-->>"+pointerIndex+", coor[0]-->>"+coor[0]+", -coor[1]-->>"+(-coor[1]));
        Matrix.scaleM(matrix, 0, GLConstants.getGapTwoNodeScaleX(), scaleY, 1f);
        GLES20.glUniformMatrix4fv(matrixNodeLine, 1, false, gameRender.getFinalMatrix(matrix), 0);
        gameRender.popMatrix();


        //启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(positionNodeLine);
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(positionNodeLine, 2, GLES20.GL_FLOAT, false, 2 * 4, mVerBufferNodeLine);

        //设置颜色
        GLES20.glUniform4fv(colorNodeLine, 1, color, 0);


        //绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
//        GLES20.glDrawElements(GLES20.GL_LINES, posLine.length / 2,
//                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(positionNodeLine);
        GLES20.glDisable(colorNodeLine);
        GLES20.glDisable(GLES20.GL_BLEND);
    }

}
