package com.fotoable.piano.game.utils;

import android.graphics.RectF;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.fotoable.piano.game.GameRender;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by damon on 21/06/2017.
 * 绘制五线谱
 */

public class FootstepsManager {

    private static final String TAG = "FootstepsManager";

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
                    //damontodo 当使用颜色时,再定义一个透明度,然后通过这个vColor.a *= Opacity;方式改变透明度 是不行的
//                    "uniform float Opacity;" +
                    "void main() {" +
//                    "   vColor.a *= Opacity;" +
                    "  gl_FragColor = vColor;" +
                    "}";


    public int mProgramLine;
    public int positionShape;
    public int matrixShape;
    public int colorShape;


    public GameRender gameRender;

    /**
     * 音符的4条线
     */
    public float posLine[] = {
            -1f, 1f,
            1f, 1f,

            -1f, 1f / 2f,
            1f, 1f / 2f,

            -1f, 0f,
            1f, 0f,

            -1f, -1f / 2f,
            1f, -1f / 2f,

            -1f, -1f,
            1f, -1f
    };
    /**
     * 顶点坐标Buffer
     */
    public FloatBuffer lineBuffer;



    public static final float LINE_SCALE_Y = 0.08f;
    public float lineScaleX;
    public static final float LINE_TRANSLATE_Y = -0.5f;

    public volatile float alphaTemp = GameResource.INIT_WHITE_ALPHA;
    public static final float MAX_ALPHA = 1f - GameResource.INIT_WHITE_ALPHA;

    private short[] pathDrawOrder = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    private ShortBuffer drawListBuffer;

    public FootstepsManager(GameRender gameRender) {
        this.gameRender = gameRender;
    }

    public void init() {
        lineScaleX = GLConstants.getScreenWidth() / GLConstants.getScreenHeight();
        initProgramLine();
        initBuffer();
    }


    private void initProgramLine() {
        int vertexShader = GLUtils.LoadShader(vertexShaderCodeShape,GLES20.GL_VERTEX_SHADER);
        int fragmentShader = GLUtils.LoadShader(fragmentShaderCodeShape,GLES20.GL_FRAGMENT_SHADER);
        //创建一个空的OpenGLES程序
        mProgramLine = GLES20.glCreateProgram();
        //将顶点着色器加入到程序
        GLES20.glAttachShader(mProgramLine, vertexShader);
        //将片元着色器加入到程序中
        GLES20.glAttachShader(mProgramLine, fragmentShader);
        //连接到着色器程序
        GLES20.glLinkProgram(mProgramLine);

        positionShape = GLES20.glGetAttribLocation(mProgramLine, "vPosition");
        matrixShape = GLES20.glGetUniformLocation(mProgramLine, "vMatrix");
        colorShape = GLES20.glGetUniformLocation(mProgramLine, "vColor");
    }

    protected void initBuffer() {
        ByteBuffer a = ByteBuffer.allocateDirect(posLine.length * 4);
        a.order(ByteOrder.nativeOrder());
        lineBuffer = a.asFloatBuffer();
        lineBuffer.put(posLine);
        lineBuffer.position(0);

        ByteBuffer dlb = ByteBuffer.allocateDirect(pathDrawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(pathDrawOrder);
        drawListBuffer.position(0);
    }

    public void drawFootsteps(float translateX, float translateY, float alpha, float scaleX, float scaleY) {

//        Log.d(TAG, "drawLine");

        GLES20.glUseProgram(mProgramLine);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glBlendEquation(GLES20.GL_FUNC_ADD);

//        GLES20.glUniform1f(gameRender.opacityShape, alpha);
        float[] matrix = gameRender.getMatrixFromIndex();
        gameRender.pushMatrix(matrix);
//        Matrix.translateM(mMatrixCurrent, 0, 0, 0, 0);
        Matrix.translateM(matrix, 0, translateX, translateY, 0);
//        Log.e(TAG,"pointerIndex-->>"+pointerIndex+", coor[0]-->>"+coor[0]+", -coor[1]-->>"+(-coor[1]));
        Matrix.scaleM(matrix, 0, scaleX, scaleY, 1f);
        GLES20.glUniformMatrix4fv(matrixShape, 1, false, gameRender.getFinalMatrix(matrix), 0);
        gameRender.popMatrix();

        //启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(positionShape);
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(positionShape, 2, GLES20.GL_FLOAT, false, 2 * 4, lineBuffer);


        //设置颜色
        GLES20.glUniform4fv(colorShape, 1, GameResource.COLOR_FOOT_STEPS, 0);

        //绘制三角形
//        GLES20.glDrawArrays(GLES20.GL_LINES, 0, posLine.length / 2);
        GLES20.glDrawElements(GLES20.GL_LINES, posLine.length / 2,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(positionShape);
        //damontodo 如果调用GLES20.glDisable(gameRender.colorShape); 则会报: <process_gl_state_enables:520>: GL_INVALID_ENUM
//        GLES20.glDisable(gameRender.colorShape);
        GLES20.glDisable(GLES20.GL_BLEND);
    }

    /**
     * 判断触摸的范围是否在五线谱区域
     * @param touchX x
     * @param touchY y
     */
    public boolean  checkInFootstepsArea(float touchX,float touchY){
        RectF temp = GLConstants.getFootstepsArea();

        boolean result =touchY>=temp.top&&touchY<=temp.bottom;
//        Log.e(TAG,"checkInFootstepsArea, touchX-->>"+touchX+", touchY-->>"+touchY+", area-->>"+temp+", result-->>"+result);
        return result;
    }

    /**
     * 判断触摸的点是否在可点击区域的最下方
     * @param touchX x
     * @param touchY y
     * @return
     */
    public boolean  checkInFootstepsArea2(float touchX,float touchY){
        RectF temp = GLConstants.getFootstepsArea();

        boolean result =touchY>=GLConstants.getNodeStopPosition();
//        Log.e(TAG,"checkInFootstepsArea, touchX-->>"+touchX+", touchY-->>"+touchY+", area-->>"+temp+", result-->>"+result);
        return result;
    }
    /**
     * 判断触摸的点是否在五线谱的正中间
     * @param touchX x
     * @param touchY y
     * @return
     */
    public boolean  checkInFootstepsArea3(float touchX,float touchY){
        RectF temp = GLConstants.getFootstepsArea();

        boolean result =touchY>=(temp.top+GLConstants.getFootstepsHeight()*2.48f);
//        boolean result =touchY>=(temp.top+GLConstants.getFootstepsHeight()*3f+GLConstants.getOneCircleNodeWidthPx()/2f);
//        Log.e(TAG,"checkInFootstepsArea, touchX-->>"+touchX+", touchY-->>"+touchY+", area-->>"+temp+", result-->>"+result);
        return result;
    }
}
