package com.fotoable.piano.entity;

import java.util.ArrayList;

/**
 * Created by damon on 28/06/2017.
 * 所有已播放的音乐的集合
 */

public class AllPlayedData {


    /**
     * 所有已播放的音乐的集合
     */
    public final ArrayList<PlayedSong> playedSongList =new ArrayList<>();

    @Override
    public String toString() {
        return "AllPlayedData{" +
                "playedSongList=" + playedSongList +
                '}';
    }
}
