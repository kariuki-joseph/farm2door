package com.example.farm2door.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.farm2door.models.User;

public class AuthHelper {
    private static final String AUTH_PREFS = "auth_prefs";
    private static AuthHelper instance;
    private Context context;
    private boolean isUserFarmer;
    SharedPreferences preferences;

    private AuthHelper(Context context) {
        this.context = context;
        // initialize shared preferences
        preferences = context.getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE);
        isUserFarmer = preferences.getBoolean(AUTH_PREFS, false);
    }

    public static synchronized AuthHelper getInstance() {
        if (instance == null) {
            throw new IllegalStateException("AuthHelper must be initialized before use");
        }
        return instance;
    }

    public static synchronized void initialize(Context context) {
        if (instance != null) {
            throw new IllegalStateException("AuthHelper is already initialized");
        }
        instance = new AuthHelper(context);
    }

    public boolean isUserFarmer() {
        return isUserFarmer;
    }

    public void setIsUserFarmer(boolean isFarmer) {
        this.isUserFarmer = isFarmer;
        saveUserType();
    }

    private void saveUserType() {
        SharedPreferences preferences = context.getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(AUTH_PREFS, isUserFarmer);
        editor.apply();
    }

    public void saveUser(User user){
        SharedPreferences prefs = context.getSharedPreferences("USER", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("id", user.getId());
        editor.putString("fullName", user.getFullName());
        editor.putString("phoneNumber", user.getPhoneNumber());
        editor.putString("email", user.getEmail());
        editor.putString("userType", user.getUserType());

        editor.apply();
        editor.commit();
    }

    public User getSavedUser(){
        SharedPreferences preferences = context.getSharedPreferences("USER", Context.MODE_PRIVATE);
        if(preferences.getAll().isEmpty()) return null;

        User user = new User();
        user.setId(preferences.getString("id", null));
        user.setFullName(preferences.getString("fullName", null));
        user.setEmail(preferences.getString("email", null));
        user.setPhoneNumber(preferences.getString("phoneNumber", null));
        user.setUserType(preferences.getString("userType", null));

        return user;
    }
}
