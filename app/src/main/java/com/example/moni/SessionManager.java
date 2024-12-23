package com.example.moni;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        // Using application context to avoid memory leaks
        pref = context.getApplicationContext().getSharedPreferences("MoniSession", Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn, String userEmail, String userName) {
        editor.putBoolean("isLoggedIn", isLoggedIn);
        editor.putString("userName", userName);
        // Make sure to commit changes immediately
        editor.commit();  // Using commit() instead of apply() for immediate effect
    }

    public boolean isLoggedIn() {
        return pref.getBoolean("isLoggedIn", false);
    }

    public String getUserName() {
        return pref.getString("userName", "");
    }

    public void logout() {
        editor.clear();
        editor.commit();
    }
}