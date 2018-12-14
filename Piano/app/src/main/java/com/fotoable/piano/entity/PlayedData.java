package com.fotoable.piano.entity;

/**
 * Created by damon on 28/06/2017.
 * 不同难易程度的音频的 数据记录
 */

public class PlayedData {

    /**
     * 本次游戏的时间(也就是本次游戏结束的时间)
     */
    public long time;

    /**
     * 本次游戏获取星星数
     * 范围[0,3]
     */
    public int star;

    /**
     * 本次游戏获取的金币数
     */
    public int coin;

    /**
     * 本次游戏获取的经验
     */
    public int xp;

    /**
     * 本次游戏 点击屏幕的总数
     */
    public int touchCount;

    /**
     * 本次游戏 击中音节的总数
     */
    public int hitCount;

    /**
     * 本次游戏 总得分
     */
    public int score;


    //本次弹奏每个音节的数据: 比如1.是否击中 2.并行音节是否一次全都击中
    //暂不记录原因: 1.暂时没需求 2.复杂
//    public xxxxxxx


    @Override
    public String toString() {
        return "PlayedData{" +
                "time=" + time +
                ", star=" + star +
                ", coin=" + coin +
                ", xp=" + xp +
                ", touchCount=" + touchCount +
                ", hitCount=" + hitCount +
                ", score=" + score +
                '}';
    }
}
