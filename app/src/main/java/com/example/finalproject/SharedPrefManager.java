package com.example.finalproject;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "mypref";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EVENT_CREATOR_ID = "event_creator_id";

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

    // Add this new method to save the event creator ID
    public void saveEventCreatorId(String eventCreatorId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EVENT_CREATOR_ID, eventCreatorId);
        editor.apply();
    }

    // Optional: add a method to retrieve the event creator ID
    public String getEventCreatorId() {
        return sharedPreferences.getString(KEY_EVENT_CREATOR_ID, null);
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }
}
