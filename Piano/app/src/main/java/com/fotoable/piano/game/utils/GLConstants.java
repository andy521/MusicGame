package com.fotoable.piano.game.utils;

import android.content.Context;
import android.graphics.RectF;
import android.util.Log;

import com.fotoable.piano.MyApplication;

/**
 * Created by damon on 15/06/2017.
 */

public class GLConstants {
    private static final String TAG = "GLConstants";
    /**
     * 钢琴键的总键数,则横屏将被分成88份,而每个音节绘制的中心点为每份的中间
     */
    public static final int NOTE_COLUMNS = 88;
    /**
     * 绘制每一帧的时间间隔
     */
    public static final int ONE_FRAME_DURATION = 5;
    /**
     * 两个平行音节间距 单位: px
     */
    private static int GAP_TWO_NODE_PX = -1;

    /**
     * 五线谱区域
     */
    private static RectF FOOTSTEPS_AREA = null;
    /**
     * 一个音节下落 需要的时间 单位:毫秒
     */
    private static int ONE_NODE_DROP_TIME = -1;
    /**
     * 一个音节下落整个屏幕需要的时间, 单位:毫秒
     */
    private static int ONE_NODE_DROP_TIME_ALL = -1;
    /**
     * 一个音节下落到五线谱底部的位置时, 单位:毫秒
     */
    private static int ONE_NODE_DROP_TIME_FOOTSTEPS = -1;

    private static int INTEGRAL_TEXT_SIZE = -1;

    /**
     * 两个平行音节间距的缩放比
     */
    private static float GAP_TWO_NODE_SCALE_X = -1;
    /**
     * 两个平行音节间距的画矩形线的偏移(因为画矩形线的中心点不是音节的中心点)
     */
    private static float GAP_TWO_NODE_OFFSET_X = -1;
    /**
     * 游戏界面右上角积分的 坐标 单位:像素
     */
    private static float[] POSITION_FRACTION_TEXT = null;


    /**
     * 每个音节 外圆所占的宽度 单位:像素
     */
    private static int ONE_CIRCLE_NODE_WIDTH_PX = -1;
//    /**
//     * 每个音节 外圆所占的宽度
//     */
//    public static final int ONE_CIRCLE_NODE_WIDTH  =8;
//    /**
//     * 每个音节实心圆
//     */
//    public static final int ONE_SOLID_NODE_WIDTH  =6;


    /**
     * 每秒多少像素,注意不同手机上值是不一样的
     */
    private static float SPEED = -1;

    /**
     * 屏幕左侧的开始绘制的偏移量, 单位:像素
     */
    private static int OFFSET_LEFT_PX = -1;
    /**
     * 屏幕右侧的开始绘制的偏移量, 单位:像素
     */
    private static int OFFSET_RIGHT_PX = -1;
//    /**
//     * 屏幕左侧的开始绘制的偏移量, 单位:音节的份数
//     */
//    private static int OFFSET_LEFT = -1;
//    /**
//     * 屏幕右侧的开始绘制的偏移量, 单位:音节的份数
//     */
//    private static int OFFSET_RIGHT = -1;


    /**
     * 音节 实心圆 的初始缩放
     */
    private static float INIT_CIRCLE_NODE_SCALE = -1;

//    /**
//     * 每个音节占的宽度 单位:像素
//     */
//    private static int NODE_PIXEL = -1;

    /**
     * 屏幕宽度(横屏)
     */
    private static float SCREEN_WIDTH = -1;
    /**
     * 屏幕的高度(横屏)
     */
    private static float SCREEN_HEIGHT = -1;
    /**
     * 屏幕的宽高比
     */
    private static float SCREEN_RATIO_WH = -1;
    /**
     * 每次刷新下落的移动的距离
     */
    private static float DROP_DELTA_Y = -1;

    /**
     * 点击后向上移动时每次变化的距离
     */
    public final static float UP_DELTA_Y = -0.008f;

    /**
     * 音节最底部消失的位置
     */
    private static float DISMISS_BOTTOM_Y = -1;

    /**
     * footsteps 的高度
     */
    private static float FOOTSTEPS_HEIGHT = -1;


    /**
     * 音节出现的位置
     */
    private static float APPEAR_TOP_Y = -1;

    /**
     * 音节暂停的位置 像素
     */
    private static float NODE_STOP_POSITION= -1;
    private static float NODE_STOP_POSITION1= -1;




    public static float getInitCircleNodeScale() {
        if (INIT_CIRCLE_NODE_SCALE == -1) {
            synchronized (GLConstants.class) {
                if (INIT_CIRCLE_NODE_SCALE == -1) {
                    initConstants();
                }
            }
        }
        return INIT_CIRCLE_NODE_SCALE;
    }

    public static float getScreenWidth() {
        if (SCREEN_WIDTH == -1) {
            synchronized (GLConstants.class) {
                if (SCREEN_WIDTH == -1) {
                    initConstants();
                }
            }
        }
        return SCREEN_WIDTH;
    }

    public static float getScreenHeight() {
        if (SCREEN_HEIGHT == -1) {
            synchronized (GLConstants.class) {
                if (SCREEN_HEIGHT == -1) {
                    initConstants();
                }
            }
        }
        return SCREEN_HEIGHT;
    }

    public static float getScreenRatioWh() {
        if (SCREEN_RATIO_WH == -1) {
            synchronized (GLConstants.class) {
                if (SCREEN_RATIO_WH == -1) {
                    initConstants();
                }
            }
        }
        return SCREEN_RATIO_WH;
    }

    public static float getSPEED() {
        if (SPEED == -1) {
            synchronized (GLConstants.class) {
                if (SPEED == -1) {
                    initConstants();
                }
            }
        }
        return SPEED;
    }


    public static int getOneCircleNodeWidthPx() {
        if (ONE_CIRCLE_NODE_WIDTH_PX == -1) {
            synchronized (GLConstants.class) {
                if (ONE_CIRCLE_NODE_WIDTH_PX == -1) {
                    initConstants();
                }
            }
        }
        return ONE_CIRCLE_NODE_WIDTH_PX;
    }


    public static float getGapTwoNodeScaleX() {
        if (GAP_TWO_NODE_SCALE_X == -1) {
            synchronized (GLConstants.class) {
                if (GAP_TWO_NODE_SCALE_X == -1) {
                    initConstants();
                }
            }
        }
        return GAP_TWO_NODE_SCALE_X;
    }

    public static float getGapTwoNodeOffsetX() {
        if (GAP_TWO_NODE_OFFSET_X == -1) {
            synchronized (GLConstants.class) {
                if (GAP_TWO_NODE_OFFSET_X == -1) {
                    initConstants();
                }
            }
        }
        return GAP_TWO_NODE_OFFSET_X;
    }

    public static float getGapTwoNodePx() {
        if (GAP_TWO_NODE_PX == -1) {
            synchronized (GLConstants.class) {
                if (GAP_TWO_NODE_PX == -1) {
                    initConstants();
                }
            }
        }
        return GAP_TWO_NODE_PX;
    }

    public static RectF getFootstepsArea() {
        if (FOOTSTEPS_AREA == null) {
            synchronized (GLConstants.class) {
                if (FOOTSTEPS_AREA == null) {
                    initConstants();
                }
            }
        }
        return FOOTSTEPS_AREA;
    }

    public static int getOneNodeDropTime() {
        if (ONE_NODE_DROP_TIME == -1) {
            synchronized (GLConstants.class) {
                if (ONE_NODE_DROP_TIME == -1) {
                    initConstants();
                }
            }
        }
        return ONE_NODE_DROP_TIME;
    }

    public static int getOneNodeDropTimeAll() {
        if (ONE_NODE_DROP_TIME_ALL == -1) {
            synchronized (GLConstants.class) {
                if (ONE_NODE_DROP_TIME_ALL == -1) {
                    initConstants();
                }
            }
        }
        return ONE_NODE_DROP_TIME_ALL;
    }

    public static int getOneNodeDropTimeFootsteps() {
        if (ONE_NODE_DROP_TIME_FOOTSTEPS == -1) {
            synchronized (GLConstants.class) {
                if (ONE_NODE_DROP_TIME_FOOTSTEPS == -1) {
                    initConstants();
                }
            }
        }
        return ONE_NODE_DROP_TIME_FOOTSTEPS;
    }

    public static int getIntegralTextSize() {
        if (INTEGRAL_TEXT_SIZE == -1) {
            synchronized (GLConstants.class) {
                if (INTEGRAL_TEXT_SIZE == -1) {
                    initConstants();
                }
            }
        }
        return INTEGRAL_TEXT_SIZE;
    }

    public static float[] getPositionFractionText() {
        if (POSITION_FRACTION_TEXT == null) {
            synchronized (GLConstants.class) {
                if (POSITION_FRACTION_TEXT == null) {
                    initConstants();
                }
            }
        }
        return POSITION_FRACTION_TEXT;
    }

    public static float getDropDeltaY() {
        if (DROP_DELTA_Y == -1) {
            synchronized (GLConstants.class) {
                if (DROP_DELTA_Y == -1) {
                    initConstants();
                }
            }
        }
        return DROP_DELTA_Y;
    }

    public static float getDismissBottomY() {
        if (DISMISS_BOTTOM_Y == -1) {
            synchronized (GLConstants.class) {
                if (DISMISS_BOTTOM_Y == -1) {
                    initConstants();
                }
            }
        }
        return DISMISS_BOTTOM_Y;
    }

    public static float getAppearTopY() {
        if (APPEAR_TOP_Y == -1) {
            synchronized (GLConstants.class) {
                if (APPEAR_TOP_Y == -1) {
                    initConstants();
                }
            }
        }
        return APPEAR_TOP_Y;
    }

    public static float getFootstepsHeight() {
        if (FOOTSTEPS_HEIGHT == -1) {
            synchronized (GLConstants.class) {
                if (FOOTSTEPS_HEIGHT == -1) {
                    initConstants();
                }
            }
        }
        return FOOTSTEPS_HEIGHT;
    }

    public static float getNodeStopPosition() {
        if (NODE_STOP_POSITION == -1) {
            synchronized (GLConstants.class) {
                if (NODE_STOP_POSITION == -1) {
                    initConstants();
                }
            }
        }
        return NODE_STOP_POSITION;
    }

    public static float getNodeStopPosition1() {
        if (NODE_STOP_POSITION1 == -1) {
            synchronized (GLConstants.class) {
                if (NODE_STOP_POSITION1 == -1) {
                    initConstants();
                }
            }
        }
        return NODE_STOP_POSITION1;
    }

    public static int getOffsetLeftPx() {
        return OFFSET_LEFT_PX;
    }

    public static int getOffsetRightPx() {
        return OFFSET_RIGHT_PX;
    }


    public static void updateSpeed(int minDeltaTime) {

        if (minDeltaTime < 150) {
            //防止速度过快
            minDeltaTime = 150;
        } else if (minDeltaTime > 400) {
            //防止速度过慢
            minDeltaTime = 400;
        }
        ONE_NODE_DROP_TIME = (int) (minDeltaTime * 0.62f);
        SPEED = (float) ONE_CIRCLE_NODE_WIDTH_PX / (float) ONE_NODE_DROP_TIME;
        //下落一个音节的距离需要的时间
        ONE_NODE_DROP_TIME_ALL = (int) ((SCREEN_HEIGHT + ONE_CIRCLE_NODE_WIDTH_PX) / SPEED);

        ONE_NODE_DROP_TIME_FOOTSTEPS = 1000;
//        ONE_NODE_DROP_TIME_FOOTSTEPS = (int) ((FOOTSTEPS_AREA.bottom - ONE_CIRCLE_NODE_WIDTH_PX*1.5f) / SPEED);
        DROP_DELTA_Y = generateDropDeltaY();
        DISMISS_BOTTOM_Y = generateDismissBottomY();
        APPEAR_TOP_Y = GLUtils.touchXY2Position(0, -GLConstants.getOneCircleNodeWidthPx() / 2)[1];


        NODE_STOP_POSITION = FOOTSTEPS_AREA.bottom-GLConstants.getOneCircleNodeWidthPx()/2f;
        NODE_STOP_POSITION1 = GLUtils.touchXY2Position(0,NODE_STOP_POSITION)[1]+generateDropDeltaY();//+generateDropDeltaY(),多算一点距离,是为了让音节在结束前能拦截到停止


        Log.d(TAG, "node speed-->>" + SPEED + ", minDeltaTime-->>" + minDeltaTime + ", ONE_CIRCLE_NODE_WIDTH_PX-->>" + ONE_CIRCLE_NODE_WIDTH_PX + ", ONE_NODE_DROP_TIME_ALL-->>" + ONE_NODE_DROP_TIME_ALL + ", DROP_DELTA_Y-->>" + DROP_DELTA_Y+", APPEAR_TOP_Y-->>"+APPEAR_TOP_Y+", DISMISS_BOTTOM_Y-->>"+DISMISS_BOTTOM_Y+", ONE_NODE_DROP_TIME_FOOTSTEPS-->>"+ONE_NODE_DROP_TIME_FOOTSTEPS+", NODE_STOP_POSITION1-->>"+NODE_STOP_POSITION1);
    }


    /**
     * 根据上一个音节 计算出下一个点出现的时机
     *
     * @param deltaTime deltaTime
     * @return
     */
    public static float getNodeAppearY(long deltaTime) {
        int length = (int) (deltaTime * SPEED);
        float[] result = GLUtils.touchXY2Position(0, length);
        return result[1];
    }

    public static void initConstants() {
        int screenHeight = MyApplication.application.getResources().getDisplayMetrics().heightPixels;
        int screenWidth = MyApplication.application.getResources().getDisplayMetrics().widthPixels;

        SPEED = 0.5f;
        OFFSET_LEFT_PX = dip2px(MyApplication.application, 100);
        OFFSET_RIGHT_PX = dip2px(MyApplication.application, 100);
        GAP_TWO_NODE_PX = dip2px(MyApplication.application, 90);
        ONE_CIRCLE_NODE_WIDTH_PX = dip2px(MyApplication.application, 45);


//        ONE_SOLID_NODE_WIDTH_PX = 60;

        SCREEN_WIDTH = screenWidth > screenHeight ? screenWidth : screenHeight;
        SCREEN_HEIGHT = screenWidth > screenHeight ? screenHeight : screenWidth;

        SCREEN_RATIO_WH = SCREEN_WIDTH / SCREEN_HEIGHT;

//        NODE_PIXEL = (int) (SCREEN_WIDTH / (OFFSET_LEFT + OFFSET_RIGHT + NOTE_COLUMNS));
        INIT_CIRCLE_NODE_SCALE = ONE_CIRCLE_NODE_WIDTH_PX / SCREEN_HEIGHT;
        GAP_TWO_NODE_SCALE_X = GAP_TWO_NODE_PX / SCREEN_HEIGHT;

        float widthP = SCREEN_RATIO_WH * 2;
        GAP_TWO_NODE_OFFSET_X = (float) GAP_TWO_NODE_PX / GLConstants.getScreenWidth() * widthP / 2;

        //下落一个音节的距离需要的时间
        ONE_NODE_DROP_TIME = (int) (ONE_CIRCLE_NODE_WIDTH_PX / SPEED);
        ONE_NODE_DROP_TIME_ALL = (int) ((SCREEN_HEIGHT + ONE_CIRCLE_NODE_WIDTH_PX) / SPEED);

        //计算五线谱区域
        FOOTSTEPS_AREA = generateFootstepsArea();

        //算法:ONE_CIRCLE_NODE_WIDTH_PX/2+FOOTSTEPS_AREA.bottom-ONE_CIRCLE_NODE_WIDTH_PX/2
        // 屏幕上方加上ONE_CIRCLE_NODE_WIDTH_PX/2为开始下落的地方
        // FOOTSTEPS_AREA.bottom 表示五线谱的bottom的位置
        //最后再减去ONE_CIRCLE_NODE_WIDTH_PX/2是因为绘制音节的位置在五线谱图bottom向上半个音节的位置


        INTEGRAL_TEXT_SIZE = dip2px(MyApplication.application, 20);
        POSITION_FRACTION_TEXT = new float[2];
        POSITION_FRACTION_TEXT[0] = dip2px(MyApplication.application, 11);
        POSITION_FRACTION_TEXT[1] = getScreenHeight() / 2 - dip2px(MyApplication.application, 30);
        Log.d(TAG, "SCREEN_RATIO_WH-->>" + SCREEN_RATIO_WH + ", SCREEN_WIDTH-->>" + SCREEN_WIDTH + ", height-->>" + SCREEN_HEIGHT + ", GAP_TWO_NODE_PX-->>" + GAP_TWO_NODE_PX + ", FOOTSTEPS_AREA-->>" + FOOTSTEPS_AREA.toString());


    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    /**
     * 判断触摸的范围是否在五线谱区域
     */
    public static RectF generateFootstepsArea() {

        RectF rectF = new RectF();
        float translateY = Math.abs(FootstepsManager.LINE_TRANSLATE_Y / 2f * GLConstants.getScreenHeight());

        FOOTSTEPS_HEIGHT = FootstepsManager.LINE_SCALE_Y * GLConstants.getScreenHeight();

        //点击区域的向上和向下的扩展范围(因为五线谱区域太小很容易点不着)
        float offsetTop = FOOTSTEPS_HEIGHT * 2;
        float offsetBottom = ONE_CIRCLE_NODE_WIDTH_PX;
        rectF.top = translateY + (GLConstants.getScreenHeight() / 2f - FOOTSTEPS_HEIGHT / 2f) - offsetTop;
        rectF.bottom = translateY + (GLConstants.getScreenHeight() / 2f + FOOTSTEPS_HEIGHT / 2f) + offsetBottom;
        rectF.left = 0;
        rectF.right = GLConstants.getScreenWidth();
//        Log.d(TAG,"generateFootstepsArea,translateY-->>"+translateY+", footstepsHeight-->>"+footstepsHeight+", rectF-->>"+rectF.toString());

        return rectF;

    }

    private static float generateDropDeltaY() {
        float y = (1000f / 60f) / (float) GLConstants.getOneNodeDropTimeAll() * (GLConstants.getScreenHeight() + GLConstants.getOneCircleNodeWidthPx()) - GLConstants.getOneCircleNodeWidthPx() / 2;
        float[] xy = GLUtils.touchXY2Position(0, y);
        float y2 = (1000f / 60f * 2f) / (float) GLConstants.getOneNodeDropTimeAll() * (GLConstants.getScreenHeight() + GLConstants.getOneCircleNodeWidthPx()) - GLConstants.getOneCircleNodeWidthPx() / 2;
        float[] xy2 = GLUtils.touchXY2Position(0, y2);

        Log.d(TAG, "generateDropDeltaY -->>" + xy[1] + ", y2-->>" + xy2[1]);


        return xy2[1] - xy[1];
    }

    private static float generateDismissBottomY() {

        float y3 = (GLConstants.getScreenHeight() + GLConstants.getOneCircleNodeWidthPx()) - GLConstants.getOneCircleNodeWidthPx() / 2;
        float[] xy3 = GLUtils.touchXY2Position(0, y3);
        Log.d(TAG, "generateDropDeltaY  y3-->>" + xy3[1]);


        return xy3[1];
    }

}


