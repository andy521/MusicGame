package com.fotoable.piano.game.shared;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.fotoable.piano.MyApplication;
import com.fotoable.piano.game.entity.LevelItem;
import com.fotoable.piano.game.utils.GLUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by damon on 30/06/2017.
 */

public class SharedLevel {

    private static final String TAG = "SharedLevel";
    public static final String NAME_LEVEL = "name_level";
    public static final String KEY_LEVEL = "key_level";


    /**
     * @return 不会为null
     */
    public static ArrayList<LevelItem> getLevel() {
        SharedPreferences sharedPreferences = MyApplication.application.getSharedPreferences(NAME_LEVEL, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(KEY_LEVEL, null);
        if (TextUtils.isEmpty(json)) {
            synchronized (KEY_LEVEL) {
                ArrayList<LevelItem> items = generateDefaultLevel();
                updateLevel(items);
                return items;
            }
        }
        Gson gson = new Gson();
        ArrayList<LevelItem> result;
        try {
            result = gson.fromJson(json, new TypeToken<ArrayList<LevelItem>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
            synchronized (KEY_LEVEL) {
                ArrayList<LevelItem> items = generateDefaultLevel();
                updateLevel(items);
                return items;
            }
        }
        return result;
    }


    public static void updateLevel(ArrayList<LevelItem> items) {
        if (items == null || items.size() == 0) {
            Log.e(TAG, "updateLevel, items == null||items.size()==0");
            return;
        }
        SharedPreferences sharedPreferences = MyApplication.application.getSharedPreferences(NAME_LEVEL, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonString = gson.toJson(items);
        editor.putString(KEY_LEVEL, jsonString);
        editor.apply();
    }

    /**
     * @return 不会为null
     */
    private static ArrayList<LevelItem> generateDefaultLevel() {

        String defaultJson = GLUtils.getFromAssets("level.json");
        Gson gson = new Gson();
        ArrayList<LevelItem> result = gson.fromJson(defaultJson, new TypeToken<ArrayList<LevelItem>>() {
        }.getType());
        Log.d(TAG, "test generateDefaultLevel-->>" + result);
        return result;
    }

}
