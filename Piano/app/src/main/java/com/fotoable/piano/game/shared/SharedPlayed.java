package com.fotoable.piano.game.shared;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.fotoable.piano.activity.GameActivity;
import com.fotoable.piano.MyApplication;
import com.fotoable.piano.entity.AllPlayedData;
import com.fotoable.piano.entity.AllSongData;
import com.fotoable.piano.entity.CategoryItem;
import com.fotoable.piano.entity.PlayedData;
import com.fotoable.piano.entity.PlayedSong;
import com.fotoable.piano.entity.SongData;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by damon on 28/06/2017.
 * 已弹奏的歌曲数据
 */

public class SharedPlayed {

    private static final String TAG = "SharedPlayed";
    public static final String NAME_PLAYED = "name_played";
    public static final String KEY_PLAYED_ = "key_played_";

    public static boolean dirtyAllPlayed = true;
    public static AllPlayedData allPlayedData;


    public static boolean dirtyAllSong = true;
    public static AllSongData allSongData;

    /**
     * 获取所有已弹奏的音乐
     *
     * @return 不会为null
     */
    public static AllPlayedData getAllPlayedData() {

        if (allPlayedData != null && !dirtyAllPlayed) {
//            Log.d(TAG,"no data change allPlayedData");
            return allPlayedData;
        }

        SharedPreferences sharedPreferences = MyApplication.application.getSharedPreferences(NAME_PLAYED, Context.MODE_PRIVATE);
        String playedString = sharedPreferences.getString(KEY_PLAYED_ + SharedUser.getDefaultUserId(), null);
        AllPlayedData allPlayedData;
        if (TextUtils.isEmpty(playedString)) {
            allPlayedData = new AllPlayedData();
        } else {
            try {
                Gson gson = new Gson();
                allPlayedData = gson.fromJson(playedString, AllPlayedData.class);
            } catch (Exception e) {
                e.printStackTrace();
                allPlayedData = new AllPlayedData();
            }
        }
        dirtyAllPlayed = false;
        SharedPlayed.allPlayedData = allPlayedData;
        return allPlayedData;
    }

    /**
     * 获取指定category的歌曲集合
     *
     * @param categoryId 分类id
     * @return 不会为 null
     */
    public static LinkedHashMap<Integer, PlayedSong> getPlayedFromCategory(int categoryId) {
        LinkedHashMap<Integer, PlayedSong> map = new LinkedHashMap<>();

        AllPlayedData allPlayedData = getAllPlayedData();
        for (int i = 0; i < allPlayedData.playedSongList.size(); i++) {
            PlayedSong temp = allPlayedData.playedSongList.get(i);
            if (temp.categorySet.contains(categoryId)) {
                map.put(temp.id, temp);
            }
        }
        return map;

    }

    /**
     * @param songId
     * @return null 表示没有找到歌曲
     */
    public static PlayedSong getPlayedFromSongId(int songId) {

        if (songId < 0) {
            Log.e(TAG, "getPlayedFromSongId songId <0 -->>" + songId);
            return null;
        }
        AllPlayedData allPlayedData = getAllPlayedData();
        PlayedSong playedSong = null;
        for (int i = 0; i < allPlayedData.playedSongList.size(); i++) {
            PlayedSong temp = allPlayedData.playedSongList.get(i);
            if (temp.id == songId) {
                playedSong = temp;
                break;
            }
        }
        return playedSong;
    }

    /**
     * 购买时 调用此方法
     *
     * @param songId songId
     */
    public static void addPaidSong(int songId) {
        AllSongData allSongData = SharedSongs.getAllSongs();
        HashSet<Integer> categorySet = new HashSet<>();
        for (Map.Entry<Integer, CategoryItem> entry : allSongData.category.entrySet()) {
            ArrayList<SongData> list = entry.getValue().songList;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).id == songId) {
                    categorySet.add(entry.getKey());
                }

            }
        }
        if (categorySet.size() == 0) {
            Log.e(TAG, "addPaidSong, songId illegal-->>" + songId);
            return;
        }


        SharedPreferences sharedPreferences = MyApplication.application.getSharedPreferences(NAME_PLAYED, Context.MODE_PRIVATE);
        String playedString = sharedPreferences.getString(KEY_PLAYED_ + SharedUser.getDefaultUserId(), null);
        AllPlayedData allPlayedData;
        if (TextUtils.isEmpty(playedString)) {
            allPlayedData = new AllPlayedData();
        } else {
            try {
                Gson gson = new Gson();
                allPlayedData = gson.fromJson(playedString, AllPlayedData.class);
            } catch (Exception e) {
                e.printStackTrace();
                allPlayedData = new AllPlayedData();
            }
        }
        PlayedSong playedSong = null;
        int index = -1;//该首歌存放的位置
        if (allPlayedData.playedSongList.size() == 0) {
            //playedSongList 空时
            playedSong = new PlayedSong();
            playedSong.id = songId;
            playedSong.payCoin = true;
            playedSong.categorySet = new HashSet<>();

        } else {
            for (int i = 0; i < allPlayedData.playedSongList.size(); i++) {
                PlayedSong temp = allPlayedData.playedSongList.get(i);
                if (temp.id == songId) {
                    playedSong = temp;
                    index = i;
                    break;
                }
            }
        }
        if (playedSong == null) {
            playedSong = new PlayedSong();
            playedSong.id = songId;
            playedSong.payCoin = true;
            playedSong.categorySet = new HashSet<>();
        }
        playedSong.categorySet.addAll(categorySet);
        if (index == -1) {
            allPlayedData.playedSongList.add(playedSong);
        } else {
            //已经购买过了,还要购买? 请检查代码
            Log.e(TAG, "addPaidSong 已经购买过了,还要购买? 请检查代码");
        }
        Gson gson = new Gson();
        String json = gson.toJson(allPlayedData);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PLAYED_ + SharedUser.getDefaultUserId(), json);
        editor.apply();

        dirtyAllPlayed = true;
        getAllPlayedData();
    }

    /**
     * 玩游戏结束时, 调用此方法
     *
     * @param songId     song id
     * @param difficulty 难易程度 0:容易 1:中等 2:困难
     * @return
     */
    public static void addPlayedData(int songId, int difficulty, PlayedData playedData) {

        if (playedData == null) {
            Log.e(TAG, "addPlayedData error playedData ==null -->>");
            return;
        }
        if (!checkDifficulty(difficulty)) {
            Log.e(TAG, "addPlayedData error difficulty -->>" + difficulty);
            return;
        }

        AllSongData allSongData = SharedSongs.getAllSongs();
        HashSet<Integer> categorySet = new HashSet<>();
        for (Map.Entry<Integer, CategoryItem> entry : allSongData.category.entrySet()) {
            ArrayList<SongData> list = entry.getValue().songList;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).id == songId) {
                    categorySet.add(entry.getKey());
                }

            }
        }
        if (categorySet.size() == 0) {
            Log.e(TAG, "addPaidSong, songId illegal-->>" + songId);
            return;
        }

        SharedPreferences sharedPreferences = MyApplication.application.getSharedPreferences(NAME_PLAYED, Context.MODE_PRIVATE);
        String playedString = sharedPreferences.getString(KEY_PLAYED_ + SharedUser.getDefaultUserId(), null);
        AllPlayedData allPlayedData;
        if (TextUtils.isEmpty(playedString)) {
            allPlayedData = new AllPlayedData();
        } else {
            try {
                Gson gson = new Gson();
                allPlayedData = gson.fromJson(playedString, AllPlayedData.class);
            } catch (Exception e) {
                e.printStackTrace();
                allPlayedData = new AllPlayedData();
            }
        }
        PlayedSong playedSong = null;
        int index = -1;//该首歌存放的位置
        if (allPlayedData.playedSongList.size() == 0) {
            //playedSongList 空时
            playedSong = createPlayedSong(songId, categorySet, difficulty, playedData);
        } else {
            for (int i = 0; i < allPlayedData.playedSongList.size(); i++) {
                PlayedSong temp = allPlayedData.playedSongList.get(i);
                if (temp.id == songId) {
                    playedSong = temp;
                    index = i;
                    addPlayedData(playedSong, difficulty, playedData);
                    break;
                }
            }
        }
        if (playedSong == null) {
            playedSong = createPlayedSong(songId, categorySet, difficulty, playedData);
        }
        if (index == -1) {
            allPlayedData.playedSongList.add(playedSong);
        } else {
            allPlayedData.playedSongList.set(index, playedSong);
        }
        Gson gson = new Gson();
        String json = gson.toJson(allPlayedData);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PLAYED_ + SharedUser.getDefaultUserId(), json);
        editor.apply();
        dirtyAllPlayed = true;
        getAllPlayedData();

    }

    private static PlayedSong createPlayedSong(int songId, HashSet<Integer> categorySet, int difficulty, PlayedData playedData) {

        PlayedSong playedSong = new PlayedSong();
        playedSong.id = songId;
        playedSong.payCoin = true;
        playedSong.categorySet = new HashSet<>();
        playedSong.categorySet.addAll(categorySet);
        if (difficulty == GameActivity.GameDifficulty.easy) {
            playedSong.easyDatas.add(playedData);
        } else if (difficulty == GameActivity.GameDifficulty.middle) {
            playedSong.middleDatas.add(playedData);
        } else if (difficulty == GameActivity.GameDifficulty.hard) {
            playedSong.hardDatas.add(playedData);
        }
        return playedSong;
    }

    private static PlayedSong addPlayedData(PlayedSong playedSong, int difficulty, PlayedData playedData) {

        playedSong.payCoin = true;
        if (difficulty == GameActivity.GameDifficulty.easy) {
            playedSong.easyDatas.add(playedData);
        } else if (difficulty == GameActivity.GameDifficulty.middle) {
            playedSong.middleDatas.add(playedData);
        } else if (difficulty == GameActivity.GameDifficulty.hard) {
            playedSong.hardDatas.add(playedData);
        }
        return playedSong;
    }

    public static boolean checkDifficulty(int difficulty) {
        return difficulty == GameActivity.GameDifficulty.easy || difficulty == GameActivity.GameDifficulty.middle || difficulty == GameActivity.GameDifficulty.hard;
    }

}
