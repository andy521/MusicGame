package com.fotoable.piano.entity;

import java.util.HashMap;

/**
 * Created by damon on 28/06/2017.
 */

public class CategoryUIItem {


    public int songId;

    /**
     * 0:表示 显示需要购买的金币数 1:表示已购买但未弹奏  2: 星星数
     */
    public int flag;

    /**
     * 需要购买的金币数
     */
    public int cost;


    /**
     * 难易程度数据
     */
    public Difficulty difficultyData;


    public static class Difficulty {
        public int easyStar = 0;
        public int middleStar = 0;
        public int hardStar = 0;
    }

    @Override
    public String toString() {
        return "CategoryUIItem{" +
                "songId=" + songId +
                ", flag=" + flag +
                ", cost=" + cost +
                ", difficultyData=" + difficultyData +
                '}';
    }
}
