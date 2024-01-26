package com.example.farm2door;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.farm2door.databinding.ActivitySplashScreenBinding;
import com.example.farm2door.helpers.AuthHelper;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int SPLASH_TIMER = 3000;


        // initialize Auth Helper
        AuthHelper.initialize(this);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashScreen.this, Login.class);
            startActivity(intent);
            finish();
        }, SPLASH_TIMER);

    }
}