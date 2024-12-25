package com.example.moni;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_DEFAULT_CURRENCY = "defaultCurrency";

    public SessionManager(Context context) {
        // Using application context to avoid memory leaks
        pref = context.getApplicationContext().getSharedPreferences("MoniSession", Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn, String userEmail, String userName, int userId) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.putString(KEY_USER_EMAIL, userEmail);
        editor.putString(KEY_USER_NAME, userName);
        editor.putInt(KEY_USER_ID, userId);
        editor.commit(); // Using commit() for immediate effect
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserName() {
        return pref.getString(KEY_USER_NAME, "");
    }

    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    public void setDefaultCurrency(String currency) {
        editor.putString(KEY_DEFAULT_CURRENCY, currency);
        editor.commit();
    }

    public String getDefaultCurrency() {
        // Default to "USD" if no value is set
        return pref.getString(KEY_DEFAULT_CURRENCY, "USD");
    }

    public void logout() {
        // Preserve the default currency setting during logout
        String defaultCurrency = getDefaultCurrency();
        editor.clear();
        editor.putString(KEY_DEFAULT_CURRENCY, defaultCurrency);
        editor.commit();
    }
}
