package com.decor.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Session Manager class to handle user login and related session data
 */
public class SessionManager {
    // SharedPreferences name
    private static final String PREF_NAME = "DecorAppSession";

    // SharedPreferences keys
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_BUSINESS_NAME = "business_name";

    // SharedPreferences instance
    private SharedPreferences pref;
    private Editor editor;
    private Context context;

    // Constructor
    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String userId, String businessName) {
        // Store login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Store user data
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_BUSINESS_NAME, businessName);

        // Commit changes
        editor.commit();
    }

    /**
     * Check login method will check user login status
     * If false it will redirect user to login page
     * */
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    /**
     * Get stored session data
     * */
    public String getUserId() {
        return pref.getString(KEY_USER_ID, null);
    }

    public String getBusinessName() {
        return pref.getString(KEY_BUSINESS_NAME, null);
    }

    /**
     * Clear session details
     * */
    public void logoutUser() {
        // Clear all data from SharedPreferences
        editor.clear();
        editor.commit();
    }
}