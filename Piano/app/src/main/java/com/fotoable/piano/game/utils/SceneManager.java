package com.fotoable.piano.game.utils;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.fotoable.piano.activity.GameActivity;
import com.fotoable.piano.game.GameRender;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by damon on 16/06/2017.
 */

public class SceneManager {
    private static final String TAG = "SceneManager";

    private GameRender gameRender;


    private float bgScaleX;
    private float bgScaleY;

    private volatile Timer timerTracker;

    private volatile TimerTask taskTracker;

    public static final int LOGO_ANIMATION_DURATION = 500;
    public static volatile int count = 0;

    public BloomManager bloomManager;
    public FootstepsManager footstepsManager;
    public ProgressBarManager progressBarManager;
    public PauseBtnManager pauseBtnManager;
    public IntegralManager integralManager;
//    public AvatarManager avatarManager;

    public SceneManager(GameRender gameRender) {
        this.gameRender = gameRender;
        footstepsManager = new FootstepsManager(gameRender);
        progressBarManager = new ProgressBarManager(gameRender);
        pauseBtnManager = new PauseBtnManager(gameRender);
        integralManager = new IntegralManager(gameRender);
//        avatarManager = new AvatarManager(gameRender);
    }


    public void init() {
        bloomManager = new BloomManager(gameRender);
        bgScaleY = 1.0f;
        bgScaleX = GLConstants.getScreenWidth() / GLConstants.getScreenHeight();


    }


    public synchronized void startLogoAnimation() {

        if (timerTracker != null) {
            timerTracker.cancel();
        }
        timerTracker = new Timer();

        if (taskTracker != null) {
            bloomManager.strength = 0;
            bloomManager.strengthTemp = 0;
            count = 0;
            footstepsManager.alphaTemp = GameResource.INIT_WHITE_ALPHA;
            GameResource.COLOR_FOOT_STEPS[3] = GameResource.INIT_WHITE_ALPHA;
            taskTracker.cancel();
        }
        taskTracker = new TimerTask() {
            @Override
            public void run() {

                if (bloomManager.strength < 0) {
                    Log.d(TAG, "game over, strength-->>" + bloomManager.strength);
//                    cancel();//测试
                    bloomManager.strength = 0;
                    bloomManager.strengthTemp = 0;
                    count = 0;
                    footstepsManager.alphaTemp = GameResource.INIT_WHITE_ALPHA;
                    GameResource.COLOR_FOOT_STEPS[3] = GameResource.INIT_WHITE_ALPHA;
//                    gameRender.mView.requestRender();

                    cancel();
                    return;
                }
                if (count == 0 || count > 4) {
                    bloomManager.strengthTemp += (float) GLConstants.ONE_FRAME_DURATION / (float) LOGO_ANIMATION_DURATION * BloomManager.MAX_STRENGTH * 2;
                    footstepsManager.alphaTemp += (float) GLConstants.ONE_FRAME_DURATION / (float) LOGO_ANIMATION_DURATION * FootstepsManager.MAX_ALPHA * 2;
                }


                if (bloomManager.strengthTemp >= BloomManager.MAX_STRENGTH) {
                    count++;
                    if (count <= 4) {
                        bloomManager.strength = BloomManager.MAX_STRENGTH;
                    } else {
                        bloomManager.strength = BloomManager.MAX_STRENGTH * 2 - bloomManager.strengthTemp;
                    }
                } else {
                    bloomManager.strength = bloomManager.strengthTemp;
                }

                if (count <= 4) {
                    GameResource.COLOR_FOOT_STEPS[3]=1.0f;
                } else {

                    if (footstepsManager.alphaTemp > 1.0f) {

                        GameResource.COLOR_FOOT_STEPS[3] = 2.0f - footstepsManager.alphaTemp;
                    } else {
                        GameResource.COLOR_FOOT_STEPS[3] = footstepsManager.alphaTemp;
                    }
                }
//                Log.d(TAG, "logo animation, strength-->>" + strength);

//                gameRender.mView.requestRender();

            }
        };
        timerTracker.schedule(taskTracker, 0, 8);
    }

    public void drawScene() {
        if(gameRender.bgTexture!=null&&gameRender.bgTexture.textureId!=null) {
            //背景纹理在极个别手机上可能加载不出来
            drawBg0(0, 0, 1.0f, bgScaleX, bgScaleY, gameRender.bgTexture.textureId[0]);
        }
        footstepsManager.drawFootsteps(0, FootstepsManager.LINE_TRANSLATE_Y, 1.0f, footstepsManager.lineScaleX, FootstepsManager.LINE_SCALE_Y);
        bloomManager.drawLogo(bloomManager.logoTranslateX, bloomManager.logoTranslateY, 1.0f, BloomManager.LOGO_SCALE_Y, BloomManager.LOGO_SCALE_Y, bloomManager.strength);
        progressBarManager.drawProgressBar();
        if(GameActivity.gameType!=null&&GameActivity.gameType==0) {
            pauseBtnManager.drawPauseBtn();
        }
        integralManager.drawIntegral(gameRender.totalScore.get(),gameRender.continuousData.currentContinuousHit.get());

    }


    private void drawBg0(float translateX, float translateY, float alpha, float scaleX, float scaleY, int textureId) {
        gameRender.setProjection();
//        Log.d(TAG, "drawBg0");

        GLES20.glUseProgram(gameRender.mProgram);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glBlendEquation(GLES20.GL_FUNC_ADD);

        GLES20.glUniform1f(gameRender.mOpacity, alpha);
        float[] matrix = gameRender.getMatrixFromIndex();
        gameRender.pushMatrix(matrix);
//        Matrix.translateM(mMatrixCurrent, 0, 0, 0, 0);
        Matrix.translateM(matrix, 0, translateX, translateY, 0);
//        Log.e(TAG,"pointerIndex-->>"+pointerIndex+", coor[0]-->>"+coor[0]+", -coor[1]-->>"+(-coor[1]));
        Matrix.scaleM(matrix, 0, scaleX, scaleY, 1f);
        GLES20.glUniformMatrix4fv(gameRender.mHMatrix, 1, false, gameRender.getFinalMatrix(matrix), 0);
        gameRender.popMatrix();


        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(gameRender.mHTexture, 0);


        //启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(gameRender.mHPosition);
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(gameRender.mHPosition, 2, GLES20.GL_FLOAT, false, 0, gameRender.mVerBuffer);

        //为纹理准备纹理坐标数据
        GLES20.glEnableVertexAttribArray(gameRender.mHCoord);
        GLES20.glVertexAttribPointer(gameRender.mHCoord, 2, GLES20.GL_FLOAT, false, 0, gameRender.mTexBuffer);

        //绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(gameRender.mHPosition);
        GLES20.glDisableVertexAttribArray(gameRender.mHCoord);
        GLES20.glDisable(GLES20.GL_BLEND);
    }
    public void cleanData(){
    }
}
