package com.example.webbrowser20;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref
{
    private static SharedPreferences mSharedPref;

    public static final String BOOKMARK_LIST = "bookmark list";
    public static final String HISTORY_LIST = "history list";
    public static final String CURRENT_PAGE = "current page";
    public static final String HOMEPAGE = "homepage";

    private SharedPref() {

    }

    public static void init(Context context) {
        if(mSharedPref == null)
            mSharedPref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
    }

    public static String read(String key, String defValue) {
        return mSharedPref.getString(key, defValue);
    }

    public static void write(String key, String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }
}
