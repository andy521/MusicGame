package com.fotoable.piano.game.entity;

import com.fotoable.piano.game.utils.NodeCircleManager;

/**
 * Created by damon on 22/06/2017.
 */

public class TouchData {


    public boolean isHitNode;
    public boolean isHitPauseBtn;
    public @NodeCircleManager.NodeType int nodeType;

    /**
     * 触摸时, 经逻辑处理后, 需要消失的那个音节
     */
    public TrackNodeData targetItem;

    /**
     * 击中音节的index
     */
    public int hitIndex;
    /**
     * true:音节消失, 注意: 消失并不意味着被击中了. 当前逻辑: 只要触摸屏幕, 则最下方的音节就消失
     * 可能为null
     */
    public boolean isDismiss;

    public TouchData(boolean isHitNode, boolean isHitPauseBtn, int nodeType, int hitIndex, boolean isDismiss,TrackNodeData targetItem) {
        this.isHitNode = isHitNode;
        this.isHitPauseBtn = isHitPauseBtn;
        this.nodeType = nodeType;
        this.hitIndex = hitIndex;
        this.isDismiss = isDismiss;
        this.targetItem = targetItem;
    }

    @Override
    public String toString() {
        return "TouchData{" +
                "isHitNode=" + isHitNode +
                ", isHitPauseBtn=" + isHitPauseBtn +
                ", nodeType=" + nodeType +
                '}';
    }
}
