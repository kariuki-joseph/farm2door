package com.example.farm2door;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.farm2door.databinding.ActivityLoginBinding;
import com.example.farm2door.helpers.AuthHelper;
import com.example.farm2door.viewmodel.LoadingViewModel;
import com.example.farm2door.viewmodel.AuthViewModel;

public class Login extends AppCompatActivity {

    LoadingViewModel loadingViewModel;
    AuthViewModel authViewModel;
    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        loadingViewModel = LoadingViewModel.getInstance();
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // listen for loading state
        loadingViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                binding.progressBarLayout.progressBar.setVisibility(View.VISIBLE);
                binding.btnLogin.setText("Please wait...");
                binding.btnLogin.setEnabled(false);
            } else {
                binding.progressBarLayout.progressBar.setVisibility(View.GONE);
                binding.btnLogin.setText("Login");
                binding.btnLogin.setEnabled(true);
            }
        });

        // listen for login state
        authViewModel.getLoginSuccess().observe(this, loginSuccess -> {
            if (loginSuccess) {
               // wait for user data to be fetched from FireStore
            }
        });

        // set up user type on successful login
        authViewModel.getUser().observe(this, user -> {
            if (user != null) {
                // save user to local storage if "Remember me" is checked
                if(binding.cbRememberMe.isChecked()){
                    authViewModel.saveUserToLocalStorage(user);
                }

                if (user.getUserType().equals("Farmer")) {
                    AuthHelper.getInstance().setIsUserFarmer(true);
                }else {
                    AuthHelper.getInstance().setIsUserFarmer(false);
                }
            }

            // login must be successful at this point
            startActivity(new Intent(Login.this, HomeActivity.class));
            finish();
        });

        // listen for login error
        authViewModel.getException().observe(this, error -> {
            Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
        });


        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.edtEmail.getText().toString();
            String password = binding.edtPassword.getText().toString();

            if (email.isEmpty()) {
                binding.edtEmail.setError("Email is required");
                binding.edtEmail.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                binding.edtPassword.setError("Password is required");
                binding.edtPassword.requestFocus();
                return;
            }

            // login user from view model
            authViewModel.loginUser(email, password);
        });

        binding.tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(Login.this, Register.class));
        });

        binding.tvForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(Login.this, ForgotPassword.class));
        });

        // attempt to login user from local storage
        authViewModel.getUserFromLocalStorage(this);
    }
}