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


    /**
     * 最多可以同时触摸的手指的个数
     */
    public static final int TOUCH_POINTS = 5;


    ////未击中音节 动画时长
    public static final long ANIMATION_DURATION_TOTAL = 500L;

    //击中音节 动画时长
    public static final long ANIMATION_DURATION_TOTAL1 = 20000L;


    public static final float BALL_INIT_ALPHA = 0.5f;

    public static final float HIT_SMALL_SCALE_START = 0.5f;
    public static final float HIT_SMALL_ALPHA_START_OFFSET = 0f;
    public static final long ANIMATION_DURATION_HIT_SMALL = (long) (ANIMATION_DURATION_TOTAL / 5f * 2f);


    public static final float HIT_BIG_SCALE_START = 0.5f;
    public static final float HIT_BIG_ALPHA_START_OFFSET = 0f;
    public static final long ANIMATION_DURATION_HIT_BIG = (long) (ANIMATION_DURATION_TOTAL / 5 * 3f);


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


    private void handleTouchAnimation(TouchAnimationData item) {
        int deltaTime = (int) (item.currentTime - item.startTime);
//        if (item.touchData.isHitNode) {
//            int realDeltaTimeSmall = (int) (deltaTime - HIT_SMALL_ALPHA_START_OFFSET);
//            if (realDeltaTimeSmall > 0) {
//                float percentSmall = realDeltaTimeSmall / (float) (ANIMATION_DURATION_HIT_SMALL - realDeltaTimeSmall);
//                if (percentSmall > 1) {
//                    percentSmall = 1;
//                }
//
//                item.hitSmallAlpha = percentSmall;
//            } else {
//                item.hitSmallAlpha = 0f;
//            }
//            int realDeltaTimeBig = (int) (deltaTime - HIT_BIG_ALPHA_START_OFFSET);
//            if (realDeltaTimeBig > 0) {
//                float percentBig = realDeltaTimeBig / (float) (ANIMATION_DURATION_HIT_BIG - realDeltaTimeBig);
//                if (percentBig > 1) {
//                    percentBig = 1;
//                }
//                item.hitBigAlpha = percentBig;
//            } else {
//                item.hitBigAlpha = 0f;
//            }
//        }

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


    //////////////////////////////////////////////
    private TextureMode touchRound;
    private TextureMode touchRound1;
    private TextureMode touchRound2;
    private TextureMode touchRound3;
    private TextureMode touchRound4;

    private TextureMode touchOuterRingGray;
    private TextureMode touchOuterRingBoldGray;

    private final float radiux = GLConstants.getOneCircleNodeWidthPx() / 4 * 3;


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

        ///////////////////////////////////////////////
        touchRound1 = new TextureMode();
        touchRound1.pictureName = "touch_round1.png";
        GLUtils.loadTexture(touchRound1, true);

        touchRound2 = new TextureMode();
        touchRound2.pictureName = "touch_round2.png";
        GLUtils.loadTexture(touchRound2, true);

        touchRound3 = new TextureMode();
        touchRound3.pictureName = "touch_round3.png";
        GLUtils.loadTexture(touchRound3, true);

        touchRound4 = new TextureMode();
        touchRound4.pictureName = "touch_round4.png";
        GLUtils.loadTexture(touchRound4, true);

        touchOuterRingGray = new TextureMode();
        touchOuterRingGray.pictureName = "touch_circle0_gray.png";
        GLUtils.loadTexture(touchOuterRingGray, true);

        touchOuterRingBoldGray = new TextureMode();
        touchOuterRingBoldGray.pictureName = "touch_circle1_gray.png";
        GLUtils.loadTexture(touchOuterRingBoldGray, true);


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
        if (pointerCount <= 0) {
            return;
        }
        if (pointerCount > TOUCH_POINTS) {
            pointerCount = TOUCH_POINTS;
        }

//        for (int i = 0; i < pointerCount; i++) {
        int lastInex = pointerCount - 1;
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

        //生成向上浮动所需的数据
        generateBundleData(touchAnimationData, touchEvent.getX(lastInex), touchEvent.getY(lastInex));


        final Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                if (touchAnimationData.startTime == TouchAnimationData.DEFAULT_VALUE) {
                    long currentTime = System.currentTimeMillis();
                    touchAnimationData.startTime = currentTime;
                    touchAnimationData.currentTime = currentTime;
                    return;
                }
                if (timerList.size() > 4) {
                    timerList.get(0).task.cancel();
                    timerList.remove(0);
                }
                touchAnimationData.currentTime += 17;
                if (touchAnimationData.touchData.isDismiss) {
                    //击中音节逻辑
                    if ((touchAnimationData.currentTime - touchAnimationData.startTime) > ANIMATION_DURATION_TOTAL1) {
                        timerList.remove(touchAnimationData);
                        gameRender.mView.requestRender();
                        cancel();
                        return;
                    }
                    handleTouchAnimation0(touchAnimationData);
                    handleTouchAnimation(touchAnimationData);
                } else {
                    //未击中音节逻辑
                    if ((touchAnimationData.currentTime - touchAnimationData.startTime) > ANIMATION_DURATION_TOTAL) {
                        timerList.remove(touchAnimationData);
                        gameRender.mView.requestRender();
                        cancel();
                        return;
                    }
                    handleTouchAnimation(touchAnimationData);
                }

                gameRender.mView.requestRender();
            }
        };
        touchAnimationData.task = task;
        timerList.add(touchAnimationData);
        timer.schedule(task, 0, 17);


//        }


    }

    private void handleTouchAnimation0(TouchAnimationData item) {
//        if (!item.touchData.isHitNode) {
//            return;
//        }
//        int deltaTime = (int) (item.currentTime - item.startTime);
        //第一个球的轨迹
        for (int i = 0; i < item.riseDatas.size(); i++) {
            TouchAnimationData.RiseData riseData = item.riseDatas.get(i);
            int deltaTime = (int) (item.currentTime - item.startTime);
            if (i == 1) {
                deltaTime = (int) (item.currentTime - item.startTime - 400);
            }
            if (i == 2) {
                deltaTime = (int) (item.currentTime - item.startTime - 800);
            }

            if (deltaTime > 0 && riseData.alpha > 0) {
                riseData.isDraw = true;
                riseData.translateY -= GLConstants.UP_DELTA_Y;
                riseData.alpha = (BALL_INIT_ALPHA - deltaTime / 2000f);
//            Log.d(TAG, "handleTouchAnimation0, item.currentTime-->>" + item.currentTime + ", item.startTime-->>" + item.startTime + ", deltaTime-->>" + deltaTime+", riseData.alpha-->>"+riseData.alpha);
                if (riseData.alpha < 0) {
                    riseData.alpha = 0;
                }
                if (riseData.isFirstLeft) {
                    riseData.translateX += GLConstants.UP_DELTA_Y;
                    if (riseData.translateX < getRiseDataMax(riseData)) {
                        riseData.isFirstLeft = !riseData.isFirstLeft;
                        riseData.currentSegment++;
                    }
                } else {
                    riseData.translateX -= GLConstants.UP_DELTA_Y;
                    if (riseData.translateX > getRiseDataMax(riseData)) {
                        riseData.isFirstLeft = !riseData.isFirstLeft;
                        riseData.currentSegment++;
                    }
                }

            }

        }

    }

    private float getRiseDataMax(TouchAnimationData.RiseData riseData) {
        float result = riseData.x5;
        if (riseData.currentSegment == 0) {
            result = riseData.x0;
        } else if (riseData.currentSegment == 1) {
            result = riseData.x1;
        } else if (riseData.currentSegment == 2) {
            result = riseData.x2;
        } else if (riseData.currentSegment == 3) {
            result = riseData.x3;
        } else if (riseData.currentSegment == 4) {
            result = riseData.x4;
        } else if (riseData.currentSegment == 5) {
            result = riseData.x5;
        }

        return result;
    }


    private void generateBundleData(TouchAnimationData touchAnimationData, float touchX, float touchY) {
        if (touchAnimationData == null) {
            return;
        }

        for (int i = 0; i < touchAnimationData.riseDatas.size(); i++) {
            TouchAnimationData.RiseData riseData = touchAnimationData.riseDatas.get(i);
            if (touchAnimationData.touchData.targetItem != null) {
                riseData.translateX = touchAnimationData.touchData.targetItem.translateCircleX;
                riseData.translateY = touchAnimationData.touchData.targetItem.translateCircleY;
            }
            float random00 = (float) Math.random();
            float random01 = (float) Math.random();
            float random02 = (float) Math.random();
            float random03 = (float) Math.random();
            float random04 = (float) Math.random();
            float random05 = (float) Math.random();
            int leftOrRight0 = (int) (random00 * 10);
            if (leftOrRight0 > 4) {
                //turn right
                riseData.isFirstLeft = false;
                float[] xy0 = GLUtils.touchXY2Position(touchX + random00 * radiux, touchY);
                riseData.x0 = xy0[0];

                float[] xy1 = GLUtils.touchXY2Position(touchX - random01 * radiux, touchY);
                riseData.x1 = xy1[0];

                float[] xy2 = GLUtils.touchXY2Position(touchX + random02 * radiux, touchY);
                riseData.x2 = xy2[0];

                float[] xy3 = GLUtils.touchXY2Position(touchX - random03 * radiux, touchY);
                riseData.x3 = xy3[0];

                float[] xy4 = GLUtils.touchXY2Position(touchX + random04 * radiux, touchY);
                riseData.x4 = xy4[0];

                float[] xy5 = GLUtils.touchXY2Position(touchX - random05 * radiux, touchY);
                riseData.x5 = xy5[0];


            } else {
                //turn left
                riseData.isFirstLeft = true;
                float[] xy0 = GLUtils.touchXY2Position(touchX - random00 * radiux, touchY);
                riseData.x0 = xy0[0];

                float[] xy1 = GLUtils.touchXY2Position(touchX + random01 * radiux, touchY);
                riseData.x1 = xy1[0];

                float[] xy2 = GLUtils.touchXY2Position(touchX - random02 * radiux, touchY);
                riseData.x2 = xy2[0];

                float[] xy3 = GLUtils.touchXY2Position(touchX + random03 * radiux, touchY);
                riseData.x3 = xy3[0];

                float[] xy4 = GLUtils.touchXY2Position(touchX - random04 * radiux, touchY);
                riseData.x4 = xy4[0];

                float[] xy5 = GLUtils.touchXY2Position(touchX + random05 * radiux, touchY);
                riseData.x5 = xy5[0];

            }

        }

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
//        if (touchAnimationData.translateX != TouchAnimationData.DEFAULT_VALUE && touchAnimationData.translateY != TouchAnimationData.DEFAULT_VALUE) {
        drawAnimation1(touchAnimationData.translateX, touchAnimationData.translateY, touchAnimationData);
//        }
    }

    //private boolean testHist = false;
    private void drawAnimation1(float translateX, float translateY, final TouchAnimationData touchAnimationData) {
//        Log.d(TAG, "drawAnimation1" + ", touchAnimationData.touchData-->>" + touchAnimationData.touchData.toString()+", startTime-->>"+touchAnimationData.startTime);
        selectedNodeType(touchAnimationData.touchData.nodeType);
        if (touchAnimationData.touchData.isDismiss) {
            for (int i = 0; i < touchAnimationData.riseDatas.size(); i++) {
                TouchAnimationData.RiseData riseData = touchAnimationData.riseDatas.get(i);
                if (riseData.isDraw) {
//                    Log.d(TAG, "draw riseData-->>" + riseData.toString());
                    drawAnimation2(riseData.translateX, riseData.translateY, riseData.alpha, GLConstants.getInitCircleNodeScale() * 0.7f, touchRound.textureId[0]);
                }

            }
        }
        drawAnimation2(translateX, translateY, touchAnimationData.outerRingAlpha, touchAnimationData.outerRingScale, touchOuterRingGray.textureId[0]);
        drawAnimation2(translateX, translateY, touchAnimationData.outerRingBoldAlpha, touchAnimationData.outerRingBoldScale, touchOuterRingBoldGray.textureId[0]);
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
                touchRound = touchRound1;
                break;
            case NodeCircleManager.NodeType.NODE_TYPE_2:
                touchRound = touchRound2;
                break;
            case NodeCircleManager.NodeType.NODE_TYPE_3:
                touchRound = touchRound3;
                break;
            case NodeCircleManager.NodeType.NODE_TYPE_4:
                touchRound = touchRound4;
                break;

            default:
                //don't forget default
                break;
        }
    }

    public void cleanData() {
        timerList.clear();
    }

}
