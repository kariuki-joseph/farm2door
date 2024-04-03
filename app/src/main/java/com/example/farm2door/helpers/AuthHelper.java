package com.example.farm2door.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.farm2door.models.User;

public class AuthHelper {
    private static final String AUTH_PREFS = "auth_prefs";
    private static AuthHelper instance;
    private boolean isUserFarmer;
    private SharedPreferences preferences;
    private static Context appContext;

    private AuthHelper(Context context) {
        appContext = context.getApplicationContext(); // Get application context
        preferences = appContext.getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE);
        isUserFarmer = preferences.getBoolean(AUTH_PREFS, false);
    }

    public static synchronized AuthHelper getInstance(Context context) {
        if (instance == null) {
            instance = new AuthHelper(context);
        }
        return instance;
    }

    public boolean isUserFarmer() {
        return isUserFarmer;
    }

    public void setIsUserFarmer(boolean isFarmer) {
        this.isUserFarmer = isFarmer;
        saveUserType();
    }

    private void saveUserType() {
        SharedPreferences preferences = appContext.getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(AUTH_PREFS, isUserFarmer);
        editor.apply();
    }

    public void saveUser(User user, boolean rememberMe) {
        SharedPreferences prefs = appContext.getSharedPreferences("USER", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("id", user.getId());
        editor.putString("fullName", user.getFullName());
        editor.putString("phoneNumber", user.getPhoneNumber());
        editor.putString("email", user.getEmail());
        editor.putString("userType", user.getUserType());
        editor.putBoolean("rememberMe", rememberMe);

        editor.apply();
    }

    public User getSavedUser(){
        SharedPreferences preferences = appContext.getSharedPreferences("USER", Context.MODE_PRIVATE);
        if(preferences.getAll().isEmpty()) return null;

        User user = new User();
        user.setId(preferences.getString("id", null));
        user.setFullName(preferences.getString("fullName", null));
        user.setEmail(preferences.getString("email", null));
        user.setPhoneNumber(preferences.getString("phoneNumber", null));
        user.setUserType(preferences.getString("userType", null));
        user.setRememberMe(preferences.getBoolean("rememberMe", false));

        return user;
    }

    // logout user
    public void logout(){
        SharedPreferences preferences = appContext.getSharedPreferences("USER", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}