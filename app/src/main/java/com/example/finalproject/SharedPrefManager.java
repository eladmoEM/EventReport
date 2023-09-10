package com.example.finalproject;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * The SharedPrefManager class is responsible for managing shared preferences in the application.
 * It provides methods to store and retrieve user-specific data.
 */
public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "mypref";
    private static final String KEY_USER_ID = "user_id";

    private static SharedPrefManager mInstance;
    private static Context mCtx;
    private SharedPreferences sharedPreferences;

    private SharedPrefManager(Context context) {
        mCtx = context;
        sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    public void saveLoggedInUserId(String userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_ID, userId);
        editor.apply();
    }

    public String getLoggedInUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }



}
