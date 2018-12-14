package com.fotoable.piano.game.entity;

import com.fotoable.piano.game.utils.TouchManager;

import java.util.ArrayList;
import java.util.TimerTask;

/**
 * Created by damon on 13/06/2017.
 */

public class TouchAnimationData {
    public TouchAnimationData() {
        riseDatas.add(new RiseData());
        riseDatas.add(new RiseData());
        riseDatas.add(new RiseData());
    }

    public TimerTask task;

    public static final int DEFAULT_VALUE = -1;
    /**
     * 矩阵 translate
     * -1表示不可用
     */
    public float translateX = DEFAULT_VALUE;
    /**
     * 矩阵 translate
     */
    public float translateY = DEFAULT_VALUE;

//    /**
//     * 剩余时间的百分比: [1,0] 1开始,0表示结束了
//     */
////    public float percent=1.0f;

    public long startTime = DEFAULT_VALUE;

    public long currentTime = DEFAULT_VALUE;

    /**
     * 最外圆环的缩放
     * [0,x0] 速度快
     */
    public float outerRingScale = TouchManager.OUTER_RING_SCALE_START;
    /**
     * 最外圆环的alpha
     * 当放大到x0时,alpha开始递减
     */
    public float outerRingAlpha = 1.0f;

    /**
     * 最外宽圆的缩放
     * [0,x0]速度慢
     */
    public float outerRingBoldScale = TouchManager.OUTER_RING_SCALE_BOLD_START;
    /**
     * 最外宽圆的alpha
     * 当放大到x0时,alpha开始递减
     */
    public float outerRingBoldAlpha = 1.0f;

    /**
     * 内实心圆的缩放
     * [0,x1]
     */
    public float innerRingBoldScale = 0f;
    /**
     * 内实心圆的alpha
     * 当放大到x1时, alpha直接为0
     */
    public float innerRingBoldAlpha = 1.0f;


    /**
     * 击中时,细长条
     * scale 无变化
     */
    public float hitSmallScale = TouchManager.HIT_SMALL_SCALE_START;
    /**
     * 击中时,细长条
     * [0,1,0] 快
     */
    public float hitSmallAlpha = 0f;

    /**
     * 击中时,宽长条
     * scale 无变化
     */
    public float hitBigAlScale = TouchManager.HIT_BIG_SCALE_START;
    /**
     * 击中时,细长条
     * [0,1,0] 慢
     */
    public float hitBigAlpha = 0f;

    public TouchData touchData;


    ///////////////////// 向上浮动效果的数据 //////////////////////

    public final ArrayList<RiseData> riseDatas = new ArrayList<>(3);


//    public float left0X0  = 0f;
//    public float left0X1  = 0f;
//    public float left0X2  = 0f;
//    public float right0X0  = 0f;
//    public float right0X1  = 0f;
//    public float right0X2  = 0f;
//    public float alpha0 =1.0f;
//
//    public float left1X0  = 0f;
//    public float left1X1  = 0f;
//    public float left1X2  = 0f;
//    public float right1X0  = 0f;
//    public float right1X1  = 0f;
//    public float right1X2  = 0f;
//    public float alpha1 =1.0f;
//
//    public float left2X0  = 0f;
//    public float left2X1  = 0f;
//    public float left2X2  = 0f;
//    public float right2X0  = 0f;
//    public float right2X1  = 0f;
//    public float right2X2  = 0f;
//    public float alpha2 =1.0f;


  public  static class RiseData {
      /**
       * 第一步是先迈左脚还是右脚
       */
      public boolean isFirstLeft = false;
      public int currentSegment = 0;


      public boolean isDraw = false;
      public float translateX=0f;
      public float translateY=0f;
      //上升距离被分成六份
        public float x0 = 0f;
        public float x1 = 0f;
        public float x2 = 0f;
        public float x3 = 0f;
        public float x4 = 0f;
        public float x5 = 0f;
        public float alpha = 1.0f;

      @Override
      public String toString() {
          return "RiseData{" +
                  "isFirstLeft=" + isFirstLeft +
                  ", currentSegment=" + currentSegment +
                  ", isDraw=" + isDraw +
                  ", translateX=" + translateX +
                  ", translateY=" + translateY +
                  ", x0=" + x0 +
                  ", x1=" + x1 +
                  ", x2=" + x2 +
                  ", x3=" + x3 +
                  ", x4=" + x4 +
                  ", x5=" + x5 +
                  ", alpha=" + alpha +
                  '}';
      }
  }


    @Override
    public String toString() {
        return "TouchAnimationData{" +
                "translateX=" + translateX +
                ", translateY=" + translateY +
                ", startTime=" + startTime +
                ", outerRingScale=" + outerRingScale +
                ", outerRingAlpha=" + outerRingAlpha +
                ", outerRingBoldScale=" + outerRingBoldScale +
                ", outerRingBoldAlpha=" + outerRingBoldAlpha +
                ", innerRingBoldScale=" + innerRingBoldScale +
                ", innerRingBoldAlpha=" + innerRingBoldAlpha +
                ", hitSmallScale=" + hitSmallScale +
                ", hitSmallAlpha=" + hitSmallAlpha +
                ", hitBigAlScale=" + hitBigAlScale +
                ", hitBigAlpha=" + hitBigAlpha +
                ", touchData=" + touchData +
                '}';
    }
}
