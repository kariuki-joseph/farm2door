package com.example.farm2door;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.farm2door.databinding.ActivityLoginBinding;

public class Login extends AppCompatActivity {

    ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());


    }
}