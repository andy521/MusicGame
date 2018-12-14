package com.fotoable.piano.game.entity;

import com.fotoable.piano.game.utils.NodeCircleManager;
import com.fotoable.piano.game.utils.NodeLineManager;

/**
 * Created by damon on 14/06/2017.
 *
 * 音符下落的绘制的实体数据
 */

public class TrackNodeData {

    /**
     * 开始播放的时间(当当前已经播放的时间处于[startTime,endTime]之间时 处于屏幕上的绘制阶段)
     */
    public long startTime;
    /**
     * 游戏界面中 node出现的时间
     */
    public long startTime0=-1;

    /**
     * 距离上一个音节的距离间隔(长度为矩阵换算过的数据)
     * 当为平行音节时第二个往后的音节的deltaLast 值都为0;
     */
    public float deltaLast=0f;
    /**
     * 根据速度,算出来在屏幕上消失的时间
     */
//    public long endTime;

    /**
     * 对应 MidiEventBean.midiEventList 中的index的数据
     */
    public int index0;
    /**
     * 对应 MidiEventBean.midiEventList.mMidiNote 中的index的数据
     */
    public int index1;
    /**
     * 是否击中, 击中后执行消失动画
     */
    public boolean isHit;
    /**
     * true: 消失掉, false 不消失
     */
    public boolean isDismiss;

    /**
     * 当前位于平行音节的第几个
     */
    public int indexParallel=0;
    public int countParallel=1;

    /**
     * 是否有连接线
     */
    public boolean hasLine;

    /**
     * 线的类型, 不同类型颜色不一样
     */
    public @NodeCircleManager.NodeType int nodeType;


    /**
     * 圆圈的 矩阵 translate
     */
    public float translateCircleX;
    public float translateCircleY;


    public float scaleCircleX;
    public float scaleCircleY;

    public float alphaCircle=1.0f;
    public float alphaSolid=1.0f;

    /**
     * 测试自动点击功能中的相关变量
     */
    public volatile boolean isHandleHit = false;

    @Override
    public String toString() {
        return "TrackNodeData{" +
                "startTime=" + startTime +
                ", deltaLast=" + deltaLast +
                ", index0=" + index0 +
                ", index1=" + index1 +
                ", isHit=" + isHit +
                ", indexParallel=" + indexParallel +
                ", countParallel=" + countParallel +
                ", hasLine=" + hasLine +
                ", nodeType=" + nodeType +
                ", translateCircleX=" + translateCircleX +
                ", translateCircleY=" + translateCircleY +
                ", scaleCircleX=" + scaleCircleX +
                ", scaleCircleY=" + scaleCircleY +
                ", alphaCircle=" + alphaCircle +
                ", alphaSolid=" + alphaSolid +
                ", isHandleHit=" + isHandleHit +
                '}';
    }
}
