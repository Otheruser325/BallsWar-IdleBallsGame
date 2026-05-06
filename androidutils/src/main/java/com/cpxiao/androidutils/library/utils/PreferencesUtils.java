package com.cpxiao.androidutils.library.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/** Utility helpers for the app's persisted primitive game state. */
public final class PreferencesUtils {
    private PreferencesUtils() {
    }

    private static SharedPreferences preferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        return preferences(context).getBoolean(key, defaultValue);
    }

    public static void putBoolean(Context context, String key, boolean value) {
        preferences(context).edit().putBoolean(key, value).apply();
    }

    public static int getInt(Context context, String key, int defaultValue) {
        return preferences(context).getInt(key, defaultValue);
    }

    public static void putInt(Context context, String key, int value) {
        preferences(context).edit().putInt(key, value).apply();
    }

    public static long getLong(Context context, String key, long defaultValue) {
        return preferences(context).getLong(key, defaultValue);
    }

    public static void putLong(Context context, String key, long value) {
        preferences(context).edit().putLong(key, value).apply();
    }

    public static float getFloat(Context context, String key, float defaultValue) {
        return preferences(context).getFloat(key, defaultValue);
    }

    public static void putFloat(Context context, String key, float value) {
        preferences(context).edit().putFloat(key, value).apply();
    }

    public static String getString(Context context, String key, String defaultValue) {
        return preferences(context).getString(key, defaultValue);
    }

    public static void putString(Context context, String key, String value) {
        preferences(context).edit().putString(key, value).apply();
    }
}
