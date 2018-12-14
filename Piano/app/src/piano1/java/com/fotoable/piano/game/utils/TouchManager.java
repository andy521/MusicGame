package com.fotoable.piano.game.utils;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.view.MotionEvent;
import android.view.View;

import com.fotoable.piano.activity.GameActivity;
import com.fotoable.piano.game.GameRender;
import com.fotoable.piano.game.entity.TextureMode;
import com.fotoable.piano.game.entity.TouchAnimationData;

import java.nio.FloatBuffer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by damon on 13/06/2017.
 */

public class TouchManager {
    private static final String TAG = "TouchManager";

    /**
     * 纹理 顶点着色器
     */
    private final String vertexShaderCode =

            "attribute vec4 vPosition;" +
                    "attribute vec2 vCoord;" +
                    "varying vec2 aCoord;" +
                    "uniform mat4 vMatrix;" +
                    "void main(){" +
                    "    aCoord = vCoord;" +
                    "    gl_Position = vMatrix*vPosition;" +
                    "}";

    /**
     * 纹理 片段着色器
     */
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec2 aCoord;" +
                    "uniform sampler2D vTexture;" +
                    "uniform float Opacity;" +
                    "void main() {" +
                    "    vec4 color=texture2D( vTexture, aCoord);" +
                    "   color *= Opacity;" +
//                    "   color.a *= Opacity;" +
                    "    gl_FragColor = color;" +
                    "}";

    public int mProgram;
    //矩阵引用 uniform mat4 vMatrix
    public int mHMatrix;
    /**
     * 坐标引用 attribute vec2 a_v2TexCoord
     */
    public int mHCoord;
    /**
     * attribute vec4 a_v4Position
     */
    public int mHPosition;
    /**
     * uniform sampler2D u_s2dTexture
     */
    public int mHTexture;
    /**
     * uniform vec1 vAlpha
     */
    public int mOpacity;

    public FloatBuffer mVerBuffer;
    public FloatBuffer mTexBuffer;
//    public FloatBuffer mVerBuffer2;
//    public FloatBuffer mTexBuffer2;
//    public FloatBuffer mVerBuffer3;
//    public FloatBuffer mTexBuffer3;
//    public FloatBuffer mVerBuffer4;
//    public FloatBuffer mTexBuffer4;


    /**
     * 最多可以同时触摸的手指的个数
     */
    public static final int TOUCH_POINTS = 5;


    public static final long ANIMATION_DURATION_TOTAL = 500L;


    public static final float HIT_SMALL_SCALE_START = 0.5f;
    public static final float HIT_SMALL_ALPHA_START_OFFSET = 0f;
    public static final long ANIMATION_DURATION_HIT_SMALL = (long) (ANIMATION_DURATION_TOTAL / 5f * 2f);


    public static final float HIT_BIG_SCALE_START = 0.5f;
    public static final float HIT_BIG_ALPHA_START_OFFSET = 0f;
    public static final long ANIMATION_DURATION_HIT_BIG = (long) (ANIMATION_DURATION_TOTAL/5*3f);


    public static final long ANIMATION_DURATION_OUT_RING_SCALE = (long) (ANIMATION_DURATION_TOTAL * 0.4f);
    public static final float OUTER_RING_SCALE_START = 0.08f;
    public static final float OUTER_RING_SCALE_END = 0.25f;
    public static final float OUTER_RING_ALPHA_START_OFFSET = (long) (ANIMATION_DURATION_TOTAL * 0.15f);


    public static final long ANIMATION_DURATION_OUT_RING_BOLD_SCALE = (long) (ANIMATION_DURATION_TOTAL * 0.6f);
    public static final float OUTER_RING_SCALE_BOLD_START = 0.08f;
    public static final float OUTER_RING_BOLD_SCALE_END = 0.28f;
    public static final float OUTER_RING_BOLD_ALPHA_START_OFFSET = (long) (ANIMATION_DURATION_TOTAL * 0.2f);


    public static final long ANIMATION_DURATION_INNER_SCALE = (long) (ANIMATION_DURATION_TOTAL * 0.4f);
    public static final float INNER_SCALE_START = 0.08f;
    public static final float INNER_SCALE_END = 0.14f;

    public static final int ANIMATION_PERIOD = 60;

    private void handleTouchAnimation(long currentTime, TouchAnimationData item) {
        int deltaTime = (int) (currentTime - item.startTime);
//        if(true){
        if (item.touchData.isDismiss) {
//        if (testHist) {
            int realDeltaTimeSmall = (int) (deltaTime - HIT_SMALL_ALPHA_START_OFFSET);
            if (realDeltaTimeSmall > 0) {
                float percentSmall = realDeltaTimeSmall / (float) (ANIMATION_DURATION_HIT_SMALL-realDeltaTimeSmall);
//                float percentSmall = realDeltaTimeSmall / (float) ANIMATION_DURATION_HIT_SMALL/2;
//                if(realDeltaTimeSmall>=ANIMATION_DURATION_HIT_SMALL/2){
//                    percentSmall=2-realDeltaTimeSmall;
//                }
//                percentSmall+=0.2f;
                if (percentSmall > 1) {
                    percentSmall = 1;
                }

                item.hitSmallAlpha = percentSmall;
            } else {
                item.hitSmallAlpha = 0f;
            }
            int realDeltaTimeBig = (int) (deltaTime - HIT_BIG_ALPHA_START_OFFSET);
            if (realDeltaTimeBig > 0) {
                float percentBig = realDeltaTimeBig / (float) (ANIMATION_DURATION_HIT_BIG-realDeltaTimeBig);
//                float percentBig = realDeltaTimeBig / (float) ANIMATION_DURATION_HIT_BIG/2;
//
//                if(realDeltaTimeBig>=ANIMATION_DURATION_HIT_BIG/2){
//                    percentBig=2-realDeltaTimeBig+0.4f;
//                }
//                percentBig+=0.2f;
                if (percentBig > 1) {
                    percentBig = 1;
                }
                item.hitBigAlpha = percentBig;
            } else {
                item.hitBigAlpha = 0f;
            }
        }

        float ringScalePercent = (deltaTime / (float) ANIMATION_DURATION_OUT_RING_SCALE);
        if (ringScalePercent > 1) {
            ringScalePercent = 1;
        }
        item.outerRingScale = OUTER_RING_SCALE_START + ringScalePercent * (OUTER_RING_SCALE_END - OUTER_RING_SCALE_START);

        int realDeltaTimeRingAlpha = (int) (deltaTime - OUTER_RING_ALPHA_START_OFFSET);
        if (realDeltaTimeRingAlpha > 0) {
            float ringAlphaPercent = (realDeltaTimeRingAlpha / (float) (ANIMATION_DURATION_TOTAL - OUTER_RING_ALPHA_START_OFFSET));
            if (ringAlphaPercent > 1) {
                ringAlphaPercent = 1;
            }
            item.outerRingAlpha = 1 - ringAlphaPercent;
        } else {
            item.outerRingAlpha = 1f;
        }

        float ringBoldScalePercent = (deltaTime / (float) ANIMATION_DURATION_OUT_RING_BOLD_SCALE);
        if (ringBoldScalePercent > 1) {
            ringBoldScalePercent = 1;
        }
        item.outerRingBoldScale = OUTER_RING_SCALE_BOLD_START + ringBoldScalePercent * (OUTER_RING_BOLD_SCALE_END - OUTER_RING_SCALE_BOLD_START);

        int realDeltaTimeRingBoldAlpha = (int) (deltaTime - OUTER_RING_BOLD_ALPHA_START_OFFSET);
        if (realDeltaTimeRingBoldAlpha > 0) {
            float ringBoldAlphaPercent = (realDeltaTimeRingBoldAlpha / (float) (ANIMATION_DURATION_TOTAL - OUTER_RING_BOLD_ALPHA_START_OFFSET));
            if (ringBoldAlphaPercent > 1) {
                ringBoldAlphaPercent = 1;
            }
            item.outerRingBoldAlpha = 1 - ringBoldAlphaPercent;
        } else {
            item.outerRingBoldAlpha = 1f;
        }
//        Log.d(TAG,"item.outerRingBoldAlpha-->>"+item.outerRingBoldAlpha);


        float innerScalePercent = (deltaTime / (float) ANIMATION_DURATION_INNER_SCALE);
        float innerScale;
        if (innerScalePercent > 1) {
            innerScale = INNER_SCALE_END;
            item.innerRingBoldAlpha = 0;
        } else {
            innerScale = INNER_SCALE_START + innerScalePercent * (INNER_SCALE_END - INNER_SCALE_START);
            item.innerRingBoldAlpha = 1f;
        }
        item.innerRingBoldScale = innerScale;

//        Log.d(TAG,"handleTouchAnimation, testHist-->>"+testHist+", "+item);


    }

    private GameRender gameRender;

    /**
     * 触摸时, 最外层圆环
     */
    private TextureMode touchOuterRing;
    private TextureMode touchOuterRingBold;
    private TextureMode touchHitSmall;
    private TextureMode touchHitBig;
    private TextureMode touchInner;

    private TextureMode touchOuterRingWhite;
    private TextureMode touchOuterRingBoldWhite;
    private TextureMode touchHitSmallWhite;
    private TextureMode touchHitBigWhite;
    private TextureMode touchInnerWhite;

    private TextureMode touchOuterRingYellow;
    private TextureMode touchOuterRingBoldYellow;
    private TextureMode touchHitSmallYellow;
    private TextureMode touchHitBigYellow;
    private TextureMode touchInnerYellow;

    private TextureMode touchOuterRingPink;
    private TextureMode touchOuterRingBoldPink;
    private TextureMode touchHitSmallPink;
    private TextureMode touchHitBigPink;
    private TextureMode touchInnerPink;

    private TextureMode touchOuterRingBlue;
    private TextureMode touchOuterRingBoldBlue;
    private TextureMode touchHitSmallBlue;
    private TextureMode touchHitBigBlue;
    private TextureMode touchInnerBlue;


    private final CopyOnWriteArrayList<TouchAnimationData> timerList = new CopyOnWriteArrayList<>();

    public TouchManager(GameRender gameRender) {
        this.gameRender = gameRender;


    }

    public void init() {
        initTexture();
        intBuffer();
        initProgram();
    }

    private void intBuffer() {
        mVerBuffer = GLUtils.CreateVertexArray(GLUtils.pos);
        mTexBuffer = GLUtils.CreateVertexArray(GLUtils.coord);
    }

    private void initProgram() {
        int vertexShader = GLUtils.LoadShader(vertexShaderCode, GLES20.GL_VERTEX_SHADER);
        int fragmentShader = GLUtils.LoadShader(fragmentShaderCode, GLES20.GL_FRAGMENT_SHADER);
        //创建一个空的OpenGLES程序
        mProgram = GLES20.glCreateProgram();
        //将顶点着色器加入到程序
        GLES20.glAttachShader(mProgram, vertexShader);
        //将片元着色器加入到程序中
        GLES20.glAttachShader(mProgram, fragmentShader);
        //连接到着色器程序
        GLES20.glLinkProgram(mProgram);

        mHPosition = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mHCoord = GLES20.glGetAttribLocation(mProgram, "vCoord");
        mHMatrix = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        mHTexture = GLES20.glGetUniformLocation(mProgram, "vTexture");
        mOpacity = GLES20.glGetUniformLocation(mProgram, "Opacity");

    }

    private void initTexture() {
        touchOuterRingWhite = new TextureMode();
        touchOuterRingWhite.pictureName = "touch_circle0_white.png";
        GLUtils.loadTexture(touchOuterRingWhite, true);

        touchOuterRingBoldWhite = new TextureMode();
        touchOuterRingBoldWhite.pictureName = "touch_circle1_white.png";
        GLUtils.loadTexture(touchOuterRingBoldWhite, true);

        touchHitSmallWhite = new TextureMode();
        touchHitSmallWhite.pictureName = "touch_hit_small_white.png";
        GLUtils.loadTexture(touchHitSmallWhite, true);

        touchHitBigWhite = new TextureMode();
        touchHitBigWhite.pictureName = "touch_hit_big_white.png";
        GLUtils.loadTexture(touchHitBigWhite, true);

        touchInnerWhite = new TextureMode();
        touchInnerWhite.pictureName = "touch_inner_white.png";
        GLUtils.loadTexture(touchInnerWhite, true);

        //yellow
        touchOuterRingYellow = new TextureMode();
        touchOuterRingYellow.pictureName = "touch_circle0_yellow.png";
        GLUtils.loadTexture(touchOuterRingYellow, true);

        touchOuterRingBoldYellow = new TextureMode();
        touchOuterRingBoldYellow.pictureName = "touch_circle1_yellow.png";
        GLUtils.loadTexture(touchOuterRingBoldYellow, true);

        touchHitSmallYellow = new TextureMode();
        touchHitSmallYellow.pictureName = "touch_hit_small_yellow.png";
        GLUtils.loadTexture(touchHitSmallYellow, true);

        touchHitBigYellow = new TextureMode();
        touchHitBigYellow.pictureName = "touch_hit_big_yellow.png";
        GLUtils.loadTexture(touchHitBigYellow, true);

        touchInnerYellow = new TextureMode();
        touchInnerYellow.pictureName = "touch_inner_yellow.png";
        GLUtils.loadTexture(touchInnerYellow, true);

        //pink
        touchOuterRingPink = new TextureMode();
        touchOuterRingPink.pictureName = "touch_circle0_pink.png";
        GLUtils.loadTexture(touchOuterRingPink, true);

        touchOuterRingBoldPink = new TextureMode();
        touchOuterRingBoldPink.pictureName = "touch_circle1_pink.png";
        GLUtils.loadTexture(touchOuterRingBoldPink, true);

        touchHitSmallPink = new TextureMode();
        touchHitSmallPink.pictureName = "touch_hit_small_pink.png";
        GLUtils.loadTexture(touchHitSmallPink, true);

        touchHitBigPink = new TextureMode();
        touchHitBigPink.pictureName = "touch_hit_big_pink.png";
        GLUtils.loadTexture(touchHitBigPink, true);

        touchInnerPink = new TextureMode();
        touchInnerPink.pictureName = "touch_inner_pink.png";
        GLUtils.loadTexture(touchInnerPink, true);

        //blue
        touchOuterRingBlue = new TextureMode();
        touchOuterRingBlue.pictureName = "touch_circle0_blue.png";
        GLUtils.loadTexture(touchOuterRingBlue, true);

        touchOuterRingBoldBlue = new TextureMode();
        touchOuterRingBoldBlue.pictureName = "touch_circle1_blue.png";
        GLUtils.loadTexture(touchOuterRingBoldBlue, true);

        touchHitSmallBlue = new TextureMode();
        touchHitSmallBlue.pictureName = "touch_hit_small_blue.png";
        GLUtils.loadTexture(touchHitSmallBlue, true);

        touchHitBigBlue = new TextureMode();
        touchHitBigBlue.pictureName = "touch_hit_big_blue.png";
        GLUtils.loadTexture(touchHitBigBlue, true);

        touchInnerBlue = new TextureMode();
        touchInnerBlue.pictureName = "touch_inner_blue.png";
        GLUtils.loadTexture(touchInnerBlue, true);


    }

    public void handleTouch(final MotionEvent touchEvent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                handleTouch0(touchEvent);
            }
        }).start();
    }

    private void handleTouch0(final MotionEvent touchEvent) {

        if (touchEvent == null) {
            return;
        }

        int pointerCount = touchEvent.getPointerCount();
//        Log.d(TAG, "drawTouch-->>" + pointerCount);
        if(pointerCount<=0){
            return;
        }
        if (pointerCount > TOUCH_POINTS) {
            pointerCount = TOUCH_POINTS;
        }

//        for (int i = 0; i < pointerCount; i++) {
        int lastInex = pointerCount-1;
            final TouchAnimationData touchAnimationData = new TouchAnimationData();
            float[] xy = GLUtils.touchXY2Position(touchEvent.getX(lastInex), touchEvent.getY(lastInex));
            touchAnimationData.translateX = xy[0];
            touchAnimationData.translateY = xy[1];
            touchAnimationData.touchData = gameRender.isHit(touchEvent.getX(lastInex), touchEvent.getY(lastInex));

            if (touchAnimationData.touchData.isHitPauseBtn) {
                //触摸到了暂停按钮
                gameRender.mView.post(new Runnable() {
                    @Override
                    public void run() {
                        Context context = gameRender.mView.getContext();
                        if (context != null) {
                            ((GameActivity) context).pause_layout.setVisibility(View.VISIBLE);
                        }

                    }
                });
                return;
            } else {
                //触摸总数加1
                gameRender.touchCount.incrementAndGet();
            }
            if (touchAnimationData.touchData.isHitNode) {
                //击中总数加1
                gameRender.hitNodeCount.incrementAndGet();
                int hitIndex = touchAnimationData.touchData.hitIndex;
                if (gameRender.continuousData.currentContinuousHit.get() <= 0) {
                    //开始连续
                    gameRender.continuousData.currentContinuousHit.set(1);
                    gameRender.continuousData.lastHitIndex = hitIndex;
                    gameRender.totalScore.incrementAndGet();
                } else {
                    //只要在琴键区击中就算连续
                    int continuous = gameRender.continuousData.currentContinuousHit.incrementAndGet();
                    gameRender.continuousData.lastHitIndex = hitIndex;
                    gameRender.totalScore.addAndGet(IntegralManager.getScoreFromContinuous(continuous));
                }

            }
//            else{
//                //无效触摸,连续得分中断, 介于游戏的可玩性, 只要不漏掉就不算连续中断
//                gameRender.continuousData.lastHitIndex = -1;
//                gameRender.continuousData.currentContinuousHit.set(0);
//            }
//            Log.d(TAG, "touchCount-->>" + gameRender.touchCount.get() + ", hitNodeCount-->>" + gameRender.hitNodeCount.get() + ", totalScore-->>" + gameRender.totalScore.get() + ", ContinuousHit-->>" + gameRender.continuousData.currentContinuousHit.get());
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    long currentTime = System.currentTimeMillis();

                    if (touchAnimationData.startTime == TouchAnimationData.DEFAULT_VALUE) {
                        touchAnimationData.startTime = currentTime;
                        return;
                    }
                    if ((currentTime - touchAnimationData.startTime) > ANIMATION_DURATION_TOTAL) {
                        timerList.remove(touchAnimationData);
                        gameRender.mView.requestRender();
//                        Log.e(TAG, "touch animation finish");
                        cancel();
                        return;
                    }
                    handleTouchAnimation(currentTime, touchAnimationData);

//                if (!GameActivity.isAnimation) {
//                    cancel();//测试
//                }
//                Log.e(TAG, "touch touchAnimationData.outerRingBoldScale-->>"+touchAnimationData.outerRingBoldScale);
                    gameRender.mView.requestRender();
                }
            };
            timerList.add(touchAnimationData);
            timer.schedule(task, 0, ANIMATION_PERIOD);
//        }


    }


    public void drawAnimation() {
        if (timerList.size() == 0) {
            return;
        }
        for (int i = timerList.size() - 1; i >= 0; i--) {
            try {
                drawAnimation0(timerList.get(i));

            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
                //damontodo 并发可能存在读取指针越界问题.
            }
        }
    }

    private void drawAnimation0(final TouchAnimationData touchAnimationData) {
        gameRender.setProjection();
        if (touchAnimationData.translateX != TouchAnimationData.DEFAULT_VALUE && touchAnimationData.translateY != TouchAnimationData.DEFAULT_VALUE) {
            drawAnimation1(touchAnimationData.translateX, touchAnimationData.translateY, touchAnimationData);
        }
    }

    //private boolean testHist = false;
    private void drawAnimation1(float translateX, float translateY, final TouchAnimationData touchAnimationData) {
//        Log.d(TAG, "drawAnimation1" + ", touchAnimationData.touchData-->>" + touchAnimationData.touchData.toString()+", startTime-->>"+touchAnimationData.startTime);
        selectedNodeType(touchAnimationData.touchData.nodeType);
//        if (true) {
        if (touchAnimationData.touchData.isDismiss&&touchAnimationData.touchData.targetItem!=null) {
            //击中效果(音节位置)
            drawAnimation2(touchAnimationData.touchData.targetItem.translateCircleX, touchAnimationData.touchData.targetItem.translateCircleY, touchAnimationData.innerRingBoldAlpha, touchAnimationData.innerRingBoldScale, touchInner.textureId[0]);
            drawAnimation2(touchAnimationData.touchData.targetItem.translateCircleX, touchAnimationData.touchData.targetItem.translateCircleY, touchAnimationData.hitSmallAlpha, touchAnimationData.hitSmallScale, touchHitSmall.textureId[0]);
            drawAnimation2(touchAnimationData.touchData.targetItem.translateCircleX, touchAnimationData.touchData.targetItem.translateCircleY, touchAnimationData.hitBigAlpha, touchAnimationData.hitBigAlScale, touchHitBig.textureId[0]);
            //触摸效果(音节位置)
//            drawAnimation2(touchAnimationData.touchData.targetItem.translateCircleX, touchAnimationData.touchData.targetItem.translateCircleY, touchAnimationData.outerRingAlpha, touchAnimationData.outerRingScale, touchOuterRing.textureId[0]);
//            drawAnimation2(touchAnimationData.touchData.targetItem.translateCircleX, touchAnimationData.touchData.targetItem.translateCircleY, touchAnimationData.outerRingBoldAlpha, touchAnimationData.outerRingBoldScale, touchOuterRingBold.textureId[0]);
        }
        //触摸效果(触摸位置)
        drawAnimation2(translateX, translateY, touchAnimationData.outerRingAlpha, touchAnimationData.outerRingScale, touchOuterRing.textureId[0]);
        drawAnimation2(translateX, translateY, touchAnimationData.outerRingBoldAlpha, touchAnimationData.outerRingBoldScale, touchOuterRingBold.textureId[0]);
    }

    private void drawAnimation2(float translateX, float translateY, float alpha, float scale, int textureId) {

        GLES20.glUseProgram(mProgram);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
//        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
//        GLES20.glBlendEquation(GLES20.GL_FUNC_ADD);
        GLES20.glUniform1f(mOpacity, alpha);


        float[] matrix = gameRender.getMatrixFromIndex();
        gameRender.pushMatrix(matrix);
//        Matrix.translateM(mMatrixCurrent, 0, 0, 0, 0);
        Matrix.translateM(matrix, 0, translateX, translateY, 0);
//        Log.e(TAG,"pointerIndex-->>"+pointerIndex+", coor[0]-->>"+coor[0]+", -coor[1]-->>"+(-coor[1]));
        Matrix.scaleM(matrix, 0, scale, scale, 1f);
        GLES20.glUniformMatrix4fv(mHMatrix, 1, false, gameRender.getFinalMatrix(matrix), 0);
        gameRender.popMatrix();


        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(mHTexture, 0);

        //启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(mHPosition);
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(mHPosition, 2, GLES20.GL_FLOAT, false, 0, mVerBuffer);

        //为纹理准备纹理坐标数据
        GLES20.glEnableVertexAttribArray(mHCoord);
        GLES20.glVertexAttribPointer(mHCoord, 2, GLES20.GL_FLOAT, false, 0, mTexBuffer);

        //绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(mHPosition);
        GLES20.glDisableVertexAttribArray(mHCoord);
        GLES20.glDisable(GLES20.GL_BLEND);
    }

    private void selectedNodeType(@NodeCircleManager.NodeType int nodeType) {
        switch (nodeType) {
            case NodeCircleManager.NodeType.NODE_TYPE_1:
                touchOuterRing = touchOuterRingWhite;
                touchOuterRingBold = touchOuterRingBoldWhite;
                touchHitSmall = touchHitSmallWhite;
                touchHitBig = touchHitBigWhite;
                touchInner = touchInnerWhite;
                break;
            case NodeCircleManager.NodeType.NODE_TYPE_2:
                touchOuterRing = touchOuterRingYellow;
                touchOuterRingBold = touchOuterRingBoldYellow;
                touchHitSmall = touchHitSmallYellow;
                touchHitBig = touchHitBigYellow;
                touchInner = touchInnerYellow;
                break;
            case NodeCircleManager.NodeType.NODE_TYPE_3:
                touchOuterRing = touchOuterRingBlue;
                touchOuterRingBold = touchOuterRingBoldBlue;
                touchHitSmall = touchHitSmallBlue;
                touchHitBig = touchHitBigBlue;
                touchInner = touchInnerBlue;
                break;
            case NodeCircleManager.NodeType.NODE_TYPE_4:
                touchOuterRing = touchOuterRingPink;
                touchOuterRingBold = touchOuterRingBoldPink;
                touchHitSmall = touchHitSmallPink;
                touchHitBig = touchHitBigPink;
                touchInner = touchInnerPink;
                break;

            default:
                //don't forget default
                touchOuterRing = touchOuterRingWhite;
                touchOuterRingBold = touchOuterRingBoldWhite;
                touchHitSmall = touchHitSmallWhite;
                touchHitBig = touchHitBigWhite;
                touchInner = touchInnerWhite;
                break;
        }
    }

    public void cleanData() {
        timerList.clear();
    }

}
