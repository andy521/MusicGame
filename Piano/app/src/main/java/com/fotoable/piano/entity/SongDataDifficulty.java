package com.fotoable.piano.entity;

/**
 * Created by damon on 28/06/2017.
 * 不同难易程度的音频数据
 */

public class SongDataDifficulty {

    /**
     * 难易程度 0:easy 1:middle 2:hard
     * 范围:[0,2]
     */
    public int difficulty;

    /**
     * 该音频路径(当本地路径)
     * asset 开头
     * /xxx 开头 表示sd卡上
     * 先判断本地路径, 如果没有再去服务器下载
     * 可以为空
     */
    public String pathLocal;
    /**
     * 该音频路径(服务器路径)
     * 可以为空
     */
    public String pathServer;

    /**
     * 该难易程度的歌曲 弹完获得的最大的金币数
     * 该难易程度的歌曲弹完X次后就不再给奖励了
     */
    public int reward;

    /**
     * 该难易程度的歌曲 弹完获得的最大的经验值(根据你玩的好坏决定给你多少经验)
     * 当前逻辑: 只要玩, 每次弹奏都给经验
     */
    public int xp;

    @Override
    public String toString() {
        return "SongDataDifficulty{" +
                "difficulty=" + difficulty +
                ", pathLocal='" + pathLocal + '\'' +
                ", pathServer='" + pathServer + '\'' +
                ", reward=" + reward +
                ", xp=" + xp +
                '}';
    }
}
