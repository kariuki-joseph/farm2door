package com.example.farm2door.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthHelper {
    private static final String USER_TYPE_KEY = "user_type";
    private static AuthHelper instance;
    private Context context;
    private boolean isUserFarmer;

    private AuthHelper(Context context) {
        this.context = context;
        // initialize shared preferences
        SharedPreferences preferences = context.getSharedPreferences(USER_TYPE_KEY, Context.MODE_PRIVATE);
        isUserFarmer = preferences.getBoolean(USER_TYPE_KEY, false);
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
        SharedPreferences preferences = context.getSharedPreferences(USER_TYPE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(USER_TYPE_KEY, isUserFarmer);
        editor.apply();
    }
}
