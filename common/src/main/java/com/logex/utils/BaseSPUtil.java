package com.logex.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 * Created by liguangxi on 18-3-7.
 * SharedPreferences基类
 */
public class BaseSPUtil {
    private SharedPreferences sp;

    public BaseSPUtil(Context context, String spName) {
        sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
    }

    /**
     * 存数据的方法 这里使用apply方法，该方法没有返回值，提交为异步操作，而commit为同步操作有返回值
     */
    public void saveData(String key, Object value) {
        if (value instanceof String) {
            // string类型
            sp.edit().putString(key, (String) value).apply();
        }
        if (value instanceof Boolean) {
            sp.edit().putBoolean(key, (Boolean) value).apply();
        }
        if (value instanceof Integer) {
            sp.edit().putInt(key, (Integer) value).apply();
        }
        if (value instanceof Long) {
            sp.edit().putLong(key, (Long) value).apply();
        }
    }

    public boolean getBoolean(String key) {
        return sp.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return sp.getBoolean(key, defValue);
    }

    public String getString(String key) {
        return sp.getString(key, null);
    }

    public String getString(String key, String defValue) {
        return sp.getString(key, defValue);
    }

    public int getInt(String key) {
        return sp.getInt(key, 0);
    }

    public int getInt(String key, int defValue) {
        return sp.getInt(key, defValue);
    }

    public long getLong(String key) {
        return sp.getLong(key, 0);
    }

    /**
     * 是否存储了key
     *
     * @param key key
     * @return 是否存储了key
     */
    public boolean isHasKey(String key) {
        return sp.contains(key);
    }

    /**
     * 清空本地数据
     */
    public void clear() {
        sp.edit().clear().apply();
    }

    /**
     * 根据key清空值
     *
     * @param key 键值
     */
    public void clear(@NonNull String key) {
        LogUtil.i("根据key>>>" + key + "<<<清空数据......");
        sp.edit().remove(key).apply();
    }
}
