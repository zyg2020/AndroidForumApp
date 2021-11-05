package com.yangezhu.forumproject.utilities;

import android.content.Context;
import android.content.SharedPreferences;

// Usage: SharedPreferencesManager.getInstance(context).getSomeKey();
public class SharedPreferencesManager {

    private static final String APP_PREFS = "AppPrefsFile";
    private static final String KEY_FOR_USERNAME = "KEY_FOR_USERNAME";
    private static final String KEY_FOR_USER_ID = "KEY_FOR_USER_ID";

    private SharedPreferences sharedPrefs;
    private static SharedPreferencesManager instance;

    private SharedPreferencesManager(Context context) {
        sharedPrefs = context.getApplicationContext().getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPreferencesManager getInstance(Context context){
        if(instance == null)
            instance = new SharedPreferencesManager(context);

        return instance;
    }

    public void setUsername(String username) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(KEY_FOR_USERNAME, username);
        editor.apply();
    }

    public String getUsername() {
        String username = sharedPrefs.getString(KEY_FOR_USERNAME, "");
        return username;
    }

    public void setUserId(String userId) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(KEY_FOR_USER_ID, userId);
        editor.apply();
    }

    public String getUserId() {
        String username = sharedPrefs.getString(KEY_FOR_USER_ID, "");
        return username;
    }
}
