package com.fotoable.piano.entity;

import java.util.LinkedHashMap;

/**
 * Created by damon on 28/06/2017.
 */

public class AllSongData {

    /**
     * 当前音乐数据的版本
     */
    public int versionCode;

    /**
     * 该json上传时间
     */
    public long uploadTime;


    /**
     * 理论上任何时候都不会为null 或size==0
     */
    public LinkedHashMap<Integer, CategoryItem> category;

    @Override
    public String toString() {

        return "AllSongData{" +
                "versionCode=" + versionCode +
                ", uploadTime=" + uploadTime +
                '}';
    }
}
