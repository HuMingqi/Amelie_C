package com.diandian.coolco.emilie.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Preference {

    public static String getPrefString(Context context, String key) {
        return getPrefString(context, key, "");
    }

    public static String getPrefString(Context context, String key, String defaultValue) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(key, defaultValue);
    }

    public static Boolean getPrefBoolean(Context context, String key) {
        return getPrefBoolean(context, key, false);
    }

    public static Boolean getPrefBoolean(Context context, String key, Boolean defaultValue) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(key, defaultValue);
    }

    public static int getPrefInt(Context context, String key) {
        return getPrefInt(context, key, 0);
    }

    public static int getPrefInt(Context context, String key, int defaultValue) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getInt(key, defaultValue);
    }

    public static long getPrefLong(Context context, String key) {
        return getPrefLong(context, key, 0);
    }

    public static long getPrefLong(Context context, String key, long defaultValue) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getLong(key, defaultValue);
    }

    public static void setPrefString(Context context, String key, String value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = pref.edit();
        editor.putString(key, value).commit();
    }

    public static void setPrefBoolean(Context context, String key, boolean b) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = pref.edit();
        editor.putBoolean(key, b).commit();
    }

    public static void setPrefInt(Context context, String key, int n) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = pref.edit();
        editor.putInt(key, n).commit();
    }

    public static void setPrefLong(Context context, String key, long n) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = pref.edit();
        editor.putLong(key, n).commit();
    }

}
