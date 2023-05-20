package com.slaythetrash.Models;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private static final String PREF_NAME = "MyAppPrefs";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_HIGH_SCORE = "HighScore";
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    public PreferenceManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setUserId(String userId) {
        editor.putString(KEY_USER_ID, userId);
        editor.apply();
    }


    public String getUserId() {
        return pref.getString(KEY_USER_ID, null);
    }
    public int getHighScore() {
        return pref.getInt(KEY_HIGH_SCORE, 0);
    }

    public void setHighScore(int highScore) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(KEY_HIGH_SCORE, highScore);
        editor.apply();
    }
}

