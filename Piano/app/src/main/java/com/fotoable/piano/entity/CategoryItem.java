package com.fotoable.piano.entity;

import java.util.ArrayList;

/**
 * Created by damon on 30/06/2017.
 */

public class CategoryItem {

    public String name;
    /**
     * 理论上size不会为0
     */
    public final ArrayList<SongData> songList = new ArrayList<>();

    @Override
    public String toString() {
        return "CategoryItem{" +
                "name='" + name + '\'' +
                ", songList=" + songList +
                '}';
    }
}
