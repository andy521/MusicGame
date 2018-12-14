package com.fotoable.piano.entity;

/**
 * Created by damon on 28/06/2017.
 * 音频数据
 */

public class SongData {

    /**
     * 歌曲的唯一标示 id
     * 不能为空
     */
    public int id;

    /**
     * 歌曲的名字
     * 不能为空
     */
    public String name;

    /**
     * 歌手名
     */
    public String singerName;

    /**
     * 歌曲的上传时间 单位:毫秒
     * 不能为空
     */
    public long uploadTime;
    /**
     * 歌曲的分类
     * 不能为空
     */
    public String category;

    /**
     * 购买需要花费的金币数
     * 不能为空
     */
    public int cost;

    /**
     * 是需要vip 才能玩
     */
    public boolean isVip;

    /**
     * 是否是 新歌推荐
     */
    public boolean isNew;

    /**
     * 是否播放中
     */
    public boolean isPlay;

    /**
     * 容易等级
     * 可以为空
     */
    public SongDataDifficulty easy;
    /**
     * 中等等级
     * 可以为空
     */
    public SongDataDifficulty middle;
    /**
     * 困难等级
     * 可以为空
     */
    public SongDataDifficulty hard;

    @Override
    public String toString() {
        return "SongData{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", singerName='" + singerName + '\'' +
                ", uploadTime=" + uploadTime +
                ", category='" + category + '\'' +
                ", cost=" + cost +
                ", isVip=" + isVip +
                ", isNew=" + isNew +
                ", isPlay=" + isPlay +
                ", easy=" + easy +
                ", middle=" + middle +
                ", hard=" + hard +
                '}';
    }
}
