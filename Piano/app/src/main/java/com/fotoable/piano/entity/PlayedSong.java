package com.fotoable.piano.entity;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by damon on 28/06/2017.
 * 本首歌的数据记录
 */

public class PlayedSong {

    /**
     * 歌曲的唯一id
     */
    public int id;

    /**
     * 是否已花金币购买(这个和VIP是不同的)
     * 已花金币购买过的,下次再弹奏时就不需要花钱购买了
     * 漏洞: app卸载, 再重新安装,先前的金币历史操作将置空.因为咱没有金币操作远程存储逻辑
     */
    public boolean payCoin;

    /**
     * 歌曲的分类
     * 不能为空
     */
    public HashSet<Integer> categorySet;

    public final ArrayList<PlayedData> easyDatas = new ArrayList<>();

    public final ArrayList<PlayedData> middleDatas = new ArrayList<>();

    public final ArrayList<PlayedData> hardDatas = new ArrayList<>();


    @Override
    public String toString() {
        return "PlayedSong{" +
                "id=" + id +
                ", payCoin=" + payCoin +
                ", categorySet='" + categorySet + '\'' +
                ", easyDatas=" + easyDatas +
                ", middleDatas=" + middleDatas +
                ", hardDatas=" + hardDatas +
                '}';
    }
}
