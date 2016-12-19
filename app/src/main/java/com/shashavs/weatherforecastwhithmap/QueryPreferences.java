package com.shashavs.weatherforecastwhithmap;

import android.content.Context;
import android.preference.PreferenceManager;

public class QueryPreferences {

    public static void setString(Context context, String key, String query) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(key, query)
                .apply();
    }

    public static String getSrting(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(key, "__");
    }
}
