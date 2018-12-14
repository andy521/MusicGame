//package com.fotoable.piano.game.shared;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//
//import com.fotoable.piano.MyApplication;
//
///**
// * Created by damon on 27/06/2017.
// */
//
//public class SharedHelper {
//
//
//
//
//
//    /**
//     * shared name
//     */
//    public static final String NAME_SHARED_GAME = "name_shared_game";
//    /**
//     * key : 经验积分
//     */
//    public static final String KEY_EXPERIENCE = "key_experience_";
//    /**
//     * key : 金币
//     */
//    public static final String KEY_GAME_GOLD = "key_game_gold_";
//
//    /**
//     * key : 星星
//     */
//    public static final String KEY_GAME_STAR = "key_game_star_";
//
//    /**
//     * @return 经验积分
//     */
//    public static int getExpriencePoints(int songId) {
//        SharedPreferences sharedPreferences = MyApplication.application.getSharedPreferences(NAME_SHARED_GAME, Context.MODE_PRIVATE);
//        return sharedPreferences.getInt(KEY_EXPERIENCE+songId, 0);
//    }
//
//    /**
//     * @param songId
//     * @param experiencePoint 需要累加的经验积分
//     */
//    public static void addExpriencePoints(int songId,int experiencePoint) {
//        SharedPreferences sharedPreferences = MyApplication.application.getSharedPreferences(NAME_SHARED_GAME, Context.MODE_PRIVATE);
//        int currentPoints = sharedPreferences.getInt(KEY_EXPERIENCE+songId, 0);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putInt(KEY_EXPERIENCE+songId, currentPoints + experiencePoint);
//        editor.apply();
//    }
//
//    /**
//     * @param experiencePoint 需要设置总经验分
//     */
//    public static void setExpriencePoints(int songId,int experiencePoint) {
//        SharedPreferences sharedPreferences = MyApplication.application.getSharedPreferences(NAME_SHARED_GAME, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putInt(KEY_EXPERIENCE+songId, experiencePoint);
//        editor.apply();
//    }
//
//    /**
//     * @return 获取积分
//     */
//    public static int getGameGold(int songId) {
//        SharedPreferences sharedPreferences = MyApplication.application.getSharedPreferences(NAME_SHARED_GAME, Context.MODE_PRIVATE);
//        return sharedPreferences.getInt(KEY_GAME_GOLD, 0);
//    }
//
//    /**
//     * @param goldCount 需要设置金币数
//     */
//    public static void setGameGold(int goldCount) {
//        SharedPreferences sharedPreferences = MyApplication.application.getSharedPreferences(NAME_SHARED_GAME, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putInt(KEY_GAME_GOLD, goldCount);
//        editor.apply();
//    }
//
//    /**
//     * @param goldCount 需要累加的金币数
//     * @return
//     */
//    public static void addGameGold(int goldCount) {
//        SharedPreferences sharedPreferences = MyApplication.application.getSharedPreferences(NAME_SHARED_GAME, Context.MODE_PRIVATE);
//        int currentPoints = sharedPreferences.getInt(KEY_GAME_GOLD, 0);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putInt(KEY_GAME_GOLD, currentPoints + goldCount);
//        editor.apply();
//    }
//
//}
