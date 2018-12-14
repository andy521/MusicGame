package com.fotoable.piano.game.utils;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.fotoable.piano.MyApplication;
import com.fotoable.piano.game.GameRender;
import com.fotoable.piano.game.text.GLText;

import java.util.Arrays;

/**
 * Created by damon on 21/06/2017.
 * 右上角游戏分数字体
 */

public class IntegralManager {


    private static final String TAG = "IntegralManager";
    public GameRender gameRender;
    private GLText glText;

    public IntegralManager(GameRender gameRender) {
        this.gameRender = gameRender;
    }

    public void init() {
        glText = new GLText(MyApplication.application.getAssets());
        glText.load(GLConstants.getIntegralTextSize(), 2, 2);
    }

    public void drawIntegral(int fraction, int continuousCount) {
        setProjection();

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glBlendEquation(GLES20.GL_FUNC_ADD);

//        glText.drawTexture(100,  100, gameRender.mMVPMatrix);
        glText.begin(GameResource.COLOR_TEXT_INTEGRAL[0], GameResource.COLOR_TEXT_INTEGRAL[1], GameResource.COLOR_TEXT_INTEGRAL[2], GameResource.COLOR_TEXT_INTEGRAL[3], gameRender.mMVPMatrix);

        StringBuilder temp = new StringBuilder();
        temp.append(fraction);
        String typeS = continuous2String(continuousCount);
        if (typeS != null && !typeS.equals("1X")) {
            temp.append("(");
            temp.append(typeS);
            temp.append(")");
        }
        glText.draw(temp.toString(), GLConstants.getScreenWidth()/2-GLConstants.getPositionFractionText()[0]*(temp.length()+1), GLConstants.getPositionFractionText()[1]);
        glText.end();
    }

    public static String continuous2String(int continuous) {

        String result ;
        if (continuous < 5) {
            result = "1X";
        } else if (continuous < 10) {
            result = "2X";
        } else if (continuous < 20) {
            result = "3X";
        } else {
            result = "4X";
        }

        return result;
    }

    public static int getScoreFromContinuous(int continuous) {
        int result ;
        if (continuous < 5) {
            result = 1;
        } else if (continuous < 10) {
            result = 2;
        } else if (continuous < 20) {
            result = 3;
        } else {
            result = 4;
        }
        return result;
    }

    public void setProjection() {

        Matrix.frustumM(gameRender.mProjectMatrix, 0, -GLConstants.getScreenRatioWh(), GLConstants.getScreenRatioWh(), -1, 1, 1, 10);
        int useForOrtho = (int) Math.min(GLConstants.getScreenHeight(), GLConstants.getScreenWidth());
        //damontodo 这个ortho 不能去掉, 待研究
        Matrix.orthoM(gameRender.mViewMatrix, 0,
                -useForOrtho / 2,
                useForOrtho / 2,
                -useForOrtho / 2,
                useForOrtho / 2, 0.1f, 100f);
//        Matrix.frustumM(gameRender.mProjectMatrix, 0, -GLConstants.getScreenRatioWh(), GLConstants.getScreenRatioWh(), -1, 1, 3f, 7);
//        //设置相机位置 相机z轴 就要结合调用 Matrix.frustumM 时的 near 和 far 参数了，near <= z <= far
//        Matrix.setLookAtM(gameRender.mViewMatrix, 0, 0, 0, 1f, 0f, 0f, 0f, 0f, 1.0f, 0f);
        Matrix.multiplyMM(gameRender.mMVPMatrix, 0, gameRender.mProjectMatrix, 0, gameRender.mViewMatrix, 0);



//        GLES20.glViewport(0, 0, (int) GLConstants.getScreenWidth(), (int) GLConstants.getScreenHeight());
//        float ratio = GLConstants.getScreenWidth() / GLConstants.getScreenHeight();
//        //投影矩阵
//        Matrix.frustumM(gameRender.mProjectMatrix, 0, -ratio, ratio, -1, 1, 1f, 10);
//        //设置相机位置 相机z轴 就要结合调用 Matrix.frustumM 时的 near 和 far 参数了，near <= z <= far
//        Matrix.setLookAtM(gameRender.mViewMatrix, 0, 0, 0, 1f, 0f, 0f, 0f, 0f, 1.0f, 0f);
////        Matrix.frustumM(mProjectMatrix, 0, -ratioWH, ratioWH, -1, 1, 3f, 7);
//        //计算变换矩阵
//        Matrix.multiplyMM(gameRender.mMVPMatrix, 0, gameRender.mProjectMatrix, 0, gameRender.mViewMatrix, 0);
//        Log.d(TAG,"mMVPMatrix-->>"+ Arrays.toString(gameRender.mMVPMatrix));
//        Log.d(TAG,"mViewMatrix-->>"+ Arrays.toString(gameRender.mViewMatrix));
    }
}
