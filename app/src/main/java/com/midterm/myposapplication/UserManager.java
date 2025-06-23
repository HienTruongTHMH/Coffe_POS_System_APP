package com.midterm.myposapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class UserManager {
    private static final String TAG = "UserManager";
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROLE = "role";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    
    private static UserManager instance;
    private SharedPreferences preferences;
    private User currentUser;
    
    private UserManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        loadUserFromPreferences();
    }
    
    public static synchronized UserManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserManager(context.getApplicationContext());
        }
        return instance;
    }
    
    public void loginUser(User user) {
        this.currentUser = user;
        saveUserToPreferences(user);
        Log.d(TAG, "User logged in: " + user.getUsername() + " - Role: " + user.getRole());
    }
    
    public void logoutUser() {
        this.currentUser = null;
        clearUserPreferences();
        Log.d(TAG, "User logged out");
    }
    
    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false) && currentUser != null;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }
    
    public boolean isEmployee() {
        return currentUser != null && currentUser.isEmployee();
    }
    
    private void saveUserToPreferences(User user) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_USER_ID, user.getUserId());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_ROLE, user.getRole().getValue());
        editor.putString(KEY_FULL_NAME, user.getFullName());
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }
    
    private void loadUserFromPreferences() {
        if (preferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
            currentUser = new User();
            currentUser.setUserId(preferences.getString(KEY_USER_ID, ""));
            currentUser.setUsername(preferences.getString(KEY_USERNAME, ""));
            currentUser.setRole(UserRole.fromString(preferences.getString(KEY_ROLE, "")));
            currentUser.setFullName(preferences.getString(KEY_FULL_NAME, ""));
        }
    }
    
    private void clearUserPreferences() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}
