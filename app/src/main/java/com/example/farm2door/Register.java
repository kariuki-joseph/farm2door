package com.example.farm2door;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.farm2door.databinding.ActivityRegisterBinding;

public class Register extends AppCompatActivity {
    ActivityRegisterBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        binding.btnRegister.setOnClickListener(v -> {
            startActivity(new android.content.Intent(Register.this, HomeActivity.class));
        });

        binding.tvLogin.setOnClickListener(v -> {
            startActivity(new android.content.Intent(Register.this, Login.class));
        });

    }
}