package com.ytwd.midiengine.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SavePreferences保存类
 *
 * @author jiatao
 * @date 2016-5-17
 */
public class SavePreferencesUtils {
    private Context context;
    /**
     * 保存在手机里面的文件名
     */
    private final static String FILE_NAME = "save_pref_piano";//TODO

    private static SavePreferencesUtils spu;

    public SharedPreferences sp;

    public SharedPreferences.Editor editor;

    private SavePreferencesUtils(Context context) {
        this.context = context;
        sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public static SavePreferencesUtils getInstance(Context context) {
        if (spu == null) {
            synchronized (SavePreferencesUtils.class) {
                if (spu == null) {
                    spu = new SavePreferencesUtils(context);
                }
            }
        }
        return spu;
    }

    /**
     * 保存参数
     * @param key  键值
     * @param object 保存的对象
     */
    public void put(String key, Object object) {
        if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof String) {
            editor.putString(key, (String) object);
        }
        editor.commit();
    }

    /**
     * 移除SavePreferences中某个key值已经对应的值
     *
     * @param key 键值
     */
    public void remove(String key) {
        editor.remove(key).commit();
    }

    /**
     * 删除SavePreferences中所有的数据
     */
    public void removeAll() {
        editor.clear().commit();
    }
}
