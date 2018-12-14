package com.fotoable.piano.game.shared;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.fotoable.piano.MyApplication;
import com.fotoable.piano.entity.UserData;
import com.google.gson.Gson;

import java.util.UUID;

/**
 * Created by damon on 28/06/2017.
 * 用户数据
 */

public class SharedUser {

    /**
     * 默认用户的id
     */
    private static String defaultUserId;
    private static final String TAG = "SharedUser";
    public static final String NAME_DEFAULT_USER = "name_default_user";
    public static final String KEY_DEFAULT_USER = "key_default_user";

    /**
     * VIP
     */
    private static final String USER_IS_VIP = "user_is_vip";
    private static final String KEY_VIP = "key_vip";

    /**
     * 游戏界面是否显示了引导文字
     */
    private static final String KEY_IS_SHOW_GUIDE0 = "key_is_show_guide0";


    /**
     * @return 不会为null
     */
    public static UserData getDefaultUser() {
        SharedPreferences sharedPreferences = MyApplication.application.getSharedPreferences(NAME_DEFAULT_USER, Context.MODE_PRIVATE);
        String userString = sharedPreferences.getString(KEY_DEFAULT_USER, null);
        UserData userData;
        if (TextUtils.isEmpty(userString)) {
            synchronized (KEY_DEFAULT_USER) {
                userData = generateDefaultUser();
                updateDefaultUser(userData);
            }
        } else {
            Gson gson = new Gson();
            try {
                userData = gson.fromJson(userString, UserData.class);
            } catch (Exception e) {
                e.printStackTrace();
                synchronized (KEY_DEFAULT_USER) {
                    userData = generateDefaultUser();
                    updateDefaultUser(userData);
                }
            }
        }
        return userData;
    }

    public static String getDefaultUserId() {
        if (defaultUserId == null) {
            synchronized (NAME_DEFAULT_USER) {
                defaultUserId = getDefaultUser().id;
            }
        }
        return defaultUserId;
    }

    public static void updateDefaultUser(UserData userData) {
        if (userData == null) {
            Log.e(TAG, "updateDefaultUser, userData == null");
            return;
        }
        if (TextUtils.isEmpty(userData.id)) {
            Log.e(TAG, "updateDefaultUser, userData.id == kong");
            return;
        }
        if (!userData.id.equals(getDefaultUserId())) {
            Log.e(TAG, "updateDefaultUser, userData.id -->>" + userData.id + ", -->>" + getDefaultUserId());
            return;
        }
        SharedPreferences sharedPreferences = MyApplication.application.getSharedPreferences(NAME_DEFAULT_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonString = gson.toJson(userData);
        editor.putString(KEY_DEFAULT_USER, jsonString);
        editor.apply();
    }

    /**
     * 生成一个默认用户
     */
    private static UserData generateDefaultUser() {
        UserData userData = new UserData();
        userData.id = UUID.randomUUID().toString();
        userData.level = 0;
        userData.nickName = "Piano";
        userData.xpLevel = 0;
        userData.xpTotal = 0;
        userData.coin = 300;
        defaultUserId = userData.id;
        return userData;
    }

    /**
     * 保存用户是否购买VIP
     * @param isVip
     */
    public static void updateVipData(boolean isVip){
        SharedPreferences sharedPreferences = MyApplication.application.getSharedPreferences(USER_IS_VIP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_VIP, isVip);
        editor.apply();
    }

    /**
     * 获取VIP日期
     * @return
     */
    public static boolean getVipData(){
        SharedPreferences sharedPreferences = MyApplication.application.getSharedPreferences(USER_IS_VIP, Context.MODE_PRIVATE);
        boolean isVip = sharedPreferences.getBoolean(KEY_VIP,false);
        Log.e(TAG, "updateVipData,   isVip=======> " + isVip);
        return isVip;

    }

    public static boolean getIsShowGuide0() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.application);
        return preferences.getBoolean(KEY_IS_SHOW_GUIDE0, false);
    }

    public static void setIsShowGuide0(boolean isShowGuide0) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.application);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_IS_SHOW_GUIDE0, isShowGuide0).apply();
    }
}
