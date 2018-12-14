package com.fotoable.piano.game.shared;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.fotoable.piano.MyApplication;
import com.fotoable.piano.entity.AllSongData;
import com.fotoable.piano.entity.CategoryItem;
import com.fotoable.piano.entity.CategoryUI;
import com.fotoable.piano.entity.CategoryUIItem;
import com.fotoable.piano.entity.PlayedData;
import com.fotoable.piano.entity.PlayedSong;
import com.fotoable.piano.entity.SongData;
import com.fotoable.piano.game.utils.GLUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Created by damon on 28/06/2017.
 * 歌曲数据
 */

public class SharedSongs {
    /*
    1.用金币购买过的
    2.分类
     */

    private static final String TAG = "SharedSongs";
    public static final String NAME_SONGS = "name_songs";
    public static final String KEY_SONGS = "key_songs";

    /**
     * @return 不会为null
     */
    public static AllSongData getAllSongs() {

        if (SharedPlayed.allSongData != null && !SharedPlayed.dirtyAllSong) {
//            Log.d(TAG,"no data change AllSongData");
            return SharedPlayed.allSongData;
        }
        SharedPreferences sharedPreferences = MyApplication.application.getSharedPreferences(NAME_SONGS, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(KEY_SONGS, null);
        if (TextUtils.isEmpty(json)) {
            synchronized (KEY_SONGS) {
                AllSongData allSongData = generateDefaultSongs();
                updateAllSongs(allSongData);
                SharedPlayed.dirtyAllSong = false;
                SharedPlayed.allSongData = allSongData;
                return allSongData;
            }
        }
        Gson gson = new Gson();
        AllSongData allSongData;
        try {
            allSongData = gson.fromJson(json, AllSongData.class);
        } catch (Exception e) {
            e.printStackTrace();
            synchronized (KEY_SONGS) {
                allSongData = generateDefaultSongs();
                updateAllSongs(allSongData);
                SharedPlayed.dirtyAllSong = false;
                SharedPlayed.allSongData = allSongData;
                return allSongData;
            }
        }
        SharedPlayed.dirtyAllSong = false;
        SharedPlayed.allSongData = allSongData;
        return allSongData;
    }

    /**
     * @param songId
     * @return null 表示没有找到歌曲
     */
    public static SongData getSongFromSongId(int songId) {

        if (songId < 0) {
            Log.e(TAG, "getSongFromSongId songId <0 -->>" + songId);
            return null;
        }
        AllSongData allSongData = getAllSongs();

        SongData songData = null;
        for (Map.Entry<Integer, CategoryItem> entry : allSongData.category.entrySet()) {
            CategoryItem tempC = entry.getValue();
            for (int i = 0; i < tempC.songList.size(); i++) {
                SongData temp = tempC.songList.get(i);
                if (temp.id == songId) {
                    songData = temp;
                    break;
                }
            }

        }
        return songData;
    }

    /**
     * @param categoryId 分类id
     * @return null: 表示该分类不存在
     */
    public static CategoryUI getCategoryUI(int categoryId) {

        CategoryUI categoryUI = new CategoryUI();
        CategoryItem categoryItem = getSongsFromCategory(categoryId);
        LinkedHashMap<Integer, PlayedSong> mapPlayedSong = SharedPlayed.getPlayedFromCategory(categoryId);
        ArrayList<CategoryUIItem> result = new ArrayList<>();

        categoryUI.mapPlayedSong = mapPlayedSong;
        categoryUI.categoryItem = categoryItem;
        categoryUI.listData = result;

        if (categoryItem == null) {
            Log.e(TAG, "this category not exist -->>" + categoryId);
            return null;
        }
        if (categoryItem.songList.size() == 0) {
            Log.e(TAG, "this category not exist2 -->>" + categoryId);
            return null;
        }

        for (int i = 0; i < categoryItem.songList.size(); i++) {

            CategoryUIItem categoryUIItem = new CategoryUIItem();
            SongData songData = categoryItem.songList.get(i);
            categoryUIItem.songId = songData.id;

            PlayedSong playedSong = mapPlayedSong.get(songData.id);
            if (playedSong == null) {
                //未购买
                categoryUIItem.cost = songData.cost;
                categoryUIItem.flag = 0;
            } else {
                if (playedSong.easyDatas.size() != 0 || playedSong.middleDatas.size() != 0 || playedSong.hardDatas.size() != 0) {
                    //已购买且 弹过
                    categoryUIItem.flag = 2;
                    CategoryUIItem.Difficulty difficulty = new CategoryUIItem.Difficulty();
                    if (playedSong.easyDatas.size() != 0) {
//                        difficulty.easyStar = playedSong.easyDatas.get(playedSong.easyDatas.size() - 1).star;
                        difficulty.easyStar = starNum(playedSong.easyDatas);
                    }
                    if (playedSong.middleDatas.size() != 0) {
//                        difficulty.middleStar = playedSong.middleDatas.get(playedSong.middleDatas.size() - 1).star;
                        difficulty.middleStar = starNum(playedSong.middleDatas);
                    }
                    if (playedSong.hardDatas.size() != 0) {
//                        difficulty.hardStar = playedSong.hardDatas.get(playedSong.hardDatas.size() - 1).star;
                        difficulty.hardStar = starNum(playedSong.hardDatas);
                    }
                    categoryUIItem.difficultyData = difficulty;
                } else {
                    //已购买 未弹过
                    categoryUIItem.flag = 1;

                }
            }

            result.add(categoryUIItem);
        }


        return categoryUI;
    }


    public static int starNum(ArrayList<PlayedData> listDatas) {
        int maxStar = 0;
        if (listDatas != null && !listDatas.isEmpty()) {
            for (PlayedData playedData : listDatas) {
                int thisStar = playedData.star;
                if (thisStar >= maxStar) {
                    maxStar = thisStar;
                    if(maxStar == 3){
                        break;
                    }
                }
            }
            return maxStar;
        }
        return 0;
    }


    /**
     * 获取指定category的歌曲集合
     *
     * @return null: 表示不存在该分类
     */
    public static CategoryItem getSongsFromCategory(int categoryId) {

        AllSongData allSongData = getAllSongs();
        allSongData.category.get(categoryId);
        return allSongData.category.get(categoryId);

    }

    public static void updateAllSongs(AllSongData allSongData) {
        if (allSongData == null) {
            Log.e(TAG, "updateAllSongs, userData == null");
            return;
        }
        if (allSongData.category == null || allSongData.category.size() == 0) {
            Log.e(TAG, "updateAllSongs, songList == null || songList.size()==0");
            return;
        }
        SharedPreferences sharedPreferences = MyApplication.application.getSharedPreferences(NAME_SONGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonString = gson.toJson(allSongData);
        editor.putString(KEY_SONGS, jsonString);
        editor.apply();


    }

    /**
     * @return 不会为null
     */
    public static AllSongData generateDefaultSongs() {

        String defaultJson = GLUtils.getFromAssets("allSongs.json");
        Gson gson = new Gson();
        AllSongData result = gson.fromJson(defaultJson, AllSongData.class);
        Log.d(TAG, "test defaultJson-->>" + result);
        return result;
    }

}
