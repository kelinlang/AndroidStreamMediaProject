package com.lib.commonlib.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Author: Robert
 * Date:  2016-08-30
 * Copyright (c) 2016,dudu Co.,Ltd. All rights reserved.
 * Desc: SharedPreferences工具类
 */
public class SharedPreferencesUtil {

    public static final String MAX_SPEED = "maxSpeed";
    public static final String ROBBERY_MODE_TRIGGER = "robbery_trigger";

    /**
     * 获取默认的 SharedPreferences.
     */
    public static SharedPreferences getDefaultSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * 获取指定名称的 SharedPreferences.
     */
    public static SharedPreferences getSharedPreferences(Context context, String name) {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    /**
     * 往指定的SharedPreferences中保存一个键值对.
     */
    public static void putStringValue(Context context, String name, String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences(context, name).edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 往默认的SharedPreferences中保存一个键值对.
     */
    public static void putStringValue(Context context, String key, String value) {
        SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 从默认的 SharedPreferences 中查询指定key对应的value，未查到的话使用默认值defValue.
     */
    public static String getStringValue(Context context, String key, String defValue) {
        return getDefaultSharedPreferences(context).getString(key, defValue);
    }

    /**
     * 向默认的 SharedPreferences 保存一个键值对..
     */
    public static void putLongValue(Context context, String key, long value) {
        SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * 从默认的 SharedPreferences 中查询指定key对应的value，未查到的话使用默认值defValue..
     */
    public static long getLongValue(Context context, String key, long defValue) {
        return getDefaultSharedPreferences(context).getLong(key, defValue);
    }

    /**
     * 向默认的 SharedPreferences 保存一个键值对.
     */
    public static void putIntValue(Context context, String key, int value) {
        SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * 从默认的 SharedPreferences 中查询指定key对应的value，未查到的话使用默认值defValue.
     */
    public static int getIntValue(Context context, String key, int defValue) {
        return getDefaultSharedPreferences(context).getInt(key, defValue);
    }

    public static void putFloatValue(Context context, String key, float value) {
        SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public static float getFloatValue(Context context, String key, float defValue) {
        return getDefaultSharedPreferences(context).getFloat(key, defValue);
    }

    /**
     * 从 name 指定的 SharedPreferences 中查询指定key对应的value，未查到的话使用默认值defValue.
     */
    public static String getStringValue(Context context, String name, String key, String defValue) {
        return getSharedPreferences(context, name).getString(key, defValue);
    }

    /**
     * 向 name 指定的 SharedPreferences 中保存键值对.
     */
    public static void putBooleanValue(Context context, String name, String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences(context, name).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 向默认的 SharedPreferences 中保存键值对.
     */
    public static void putBooleanValue(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 从默认的 SharedPreferences 中查询key对应的value,未找到的话使用默认值defValue.
     */
    public static boolean getBooleanValue(Context context, String key, boolean defValue) {
        return getDefaultSharedPreferences(context).getBoolean(key, defValue);
    }

    /**
     * 从name 指定的 SharedPreferences 中查询key对应的value,未找到的话使用默认值defValue.
     */
    public static boolean getBooleanValue(Context context, String name, String key, boolean defValue) {
        return getSharedPreferences(context, name).getBoolean(key, defValue);
    }

}
