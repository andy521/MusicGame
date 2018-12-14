package com.fotoable.piano.entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by damon on 28/06/2017.
 */

public class CategoryUI {


    /**
     * 不会为null
     */
    public CategoryItem categoryItem ;
    public LinkedHashMap<Integer, PlayedSong> mapPlayedSong = new LinkedHashMap<>();

    public ArrayList<CategoryUIItem> listData = new ArrayList<>();

    @Override
    public String toString() {
        return "CategoryUI{" +
                "categoryItem=" + categoryItem +
                ", mapPlayedSong=" + mapPlayedSong +
                ", listData=" + listData +
                '}';
    }
}
