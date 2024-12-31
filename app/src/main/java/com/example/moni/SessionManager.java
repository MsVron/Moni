package com.example.moni;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

public class SessionManager {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_DEFAULT_CURRENCY = "defaultCurrency";
    private static final String PREF_SEEN_OFFERS = "seenOffers";
    private static final String KEY_IS_ADMIN = "isAdmin";  // Added this line

    public SessionManager(Context context) {
        pref = context.getApplicationContext().getSharedPreferences("MoniSession", Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn, String userEmail, String userName, int userId) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.putString(KEY_USER_EMAIL, userEmail);
        editor.putString(KEY_USER_NAME, userName);
        editor.putInt(KEY_USER_ID, userId);
        editor.putBoolean(KEY_IS_ADMIN, false);  // Regular user login
        editor.commit();
    }

    public void setAdminLogin(boolean isAdmin) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putBoolean(KEY_IS_ADMIN, isAdmin);
        editor.putString(KEY_USER_NAME, "Admin");
        editor.commit();
    }

    public boolean isAdmin() {
        return pref.getBoolean(KEY_IS_ADMIN, false);
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
        return pref.getString(KEY_DEFAULT_CURRENCY, "USD");
    }

    public void markOfferAsSeen(int offerId) {
        Set<String> seenOffers = pref.getStringSet(PREF_SEEN_OFFERS, new HashSet<>());
        Set<String> newSet = new HashSet<>(seenOffers);
        newSet.add(String.valueOf(offerId));
        editor.putStringSet(PREF_SEEN_OFFERS, newSet);
        editor.commit();
    }

    public boolean hasSeenOffer(int offerId) {
        Set<String> seenOffers = pref.getStringSet(PREF_SEEN_OFFERS, new HashSet<>());
        return seenOffers.contains(String.valueOf(offerId));
    }

    public void logout() {
        // Preserve the default currency and seen offers during logout
        String defaultCurrency = getDefaultCurrency();
        Set<String> seenOffers = pref.getStringSet(PREF_SEEN_OFFERS, new HashSet<>());
        editor.clear();
        editor.putString(KEY_DEFAULT_CURRENCY, defaultCurrency);
        editor.putStringSet(PREF_SEEN_OFFERS, seenOffers);
        editor.commit();
    }

    public void clearSeenOffers() {
        editor.remove(PREF_SEEN_OFFERS);
        editor.commit();
    }

    public void updateUserName(String newName) {
        editor.putString(KEY_USER_NAME, newName);
        editor.commit();
    }

    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, "");
    }

}