package com.fotoable.piano.game.shared;

import android.content.Context;
import android.content.SharedPreferences;

import com.fotoable.piano.MyApplication;

/**
 * Created by damon on 29/06/2017.
 */

public class SharedOther {

    private static final String TAG = "SharedOther";
    public static final String NAME_UPDATE_JSON = "name_update_json";
    public static final String KEY_UPDATE_JSON_TIME = "key_update_json_time";

    /**
     * 间隔多久才请求服务器更新一次 song json
     */
    public static final long INTERVAL_UPDATE_JSON =  60   * 1000L;
//    public static final long INTERVAL_UPDATE_JSON = 12 * 60 * 60 * 1000L;

    /**
     * @return true: 需要更新son json的配置文件 false: 不需要
     */
    public static boolean  checkNeedUpdate() {
        long lastUpdateTime = getLastUpdateSongJsonTime();
        return System.currentTimeMillis() - lastUpdateTime > INTERVAL_UPDATE_JSON;
    }

    public static long getLastUpdateSongJsonTime() {

        SharedPreferences sharedPreferences = MyApplication.application.getSharedPreferences(NAME_UPDATE_JSON, Context.MODE_PRIVATE);
        return  sharedPreferences.getLong(KEY_UPDATE_JSON_TIME, 0);
    }

    public static void updateDownloadSongJsonTime(long time) {
        SharedPreferences sharedPreferences = MyApplication.application.getSharedPreferences(NAME_UPDATE_JSON, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(KEY_UPDATE_JSON_TIME, time);
        editor.apply();
    }
}
