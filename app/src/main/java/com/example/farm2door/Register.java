package com.example.farm2door;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.farm2door.databinding.ActivityRegisterBinding;
import com.example.farm2door.helpers.AuthHelper;
import com.example.farm2door.models.User;
import com.example.farm2door.repository.UserRepository;
import com.example.farm2door.viewmodel.LoadingViewModel;
import com.example.farm2door.viewmodel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {
    ActivityRegisterBinding binding;
    FirebaseUser firebaseUser;
    String fullName, email, phoneNumber, password, confirmPassword, userType;
    private UserViewModel userViewModel;
    private LoadingViewModel loadingViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // setup spinner
        String[] items = new String[]{"User", "Farmer"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        binding.spUserType.setAdapter(adapter);
        binding.spUserType.setSelection(0);


        // setup view model for use with this activity
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        loadingViewModel = LoadingViewModel.getInstance();


        // listen for user registration
        userViewModel.getRegisterSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Register.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // show loading indicator
        loadingViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                binding.btnRegister.setEnabled(false);
                binding.btnRegister.setText("Please wait...");
                binding.progressBar.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.btnRegister.setEnabled(true);
                binding.btnRegister.setText("Register");
                binding.progressBar.progressBar.setVisibility(View.GONE);
            }
        });

        // listen for registration errors
        userViewModel.getException().observe(this, e -> {
            if (e != null) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // set up user type on successful registration
        userViewModel.getUser().observe(this, user -> {
            if (user != null) {
                if (user.getUserType().equals("Farmer")) {
                    AuthHelper.getInstance(this).setIsUserFarmer(true);
                }else {
                    AuthHelper.getInstance(this).setIsUserFarmer(false);
                }
            }
        });


        binding.btnRegister.setOnClickListener(v -> {
            fullName = binding.edtFullName.getText().toString();
            email = binding.edtEmail.getText().toString();
            phoneNumber = binding.edtPhone.getText().toString();
            password = binding.edtPassword.getText().toString();
            confirmPassword = binding.edtCPassword.getText().toString();
            userType = binding.spUserType.getSelectedItem().toString();

            // validate empty fields
            if(!validateFields()) return;

            User user = new User();
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPhoneNumber(phoneNumber);
            user.setUserType(userType);
            user.setPassword(password);

            userViewModel.registerUser(user);
        });

        binding.tvLogin.setOnClickListener(v -> {
            startActivity(new android.content.Intent(Register.this, Login.class));
        });
    }

    private boolean validateFields(){
        if (fullName.isEmpty()) {
            binding.edtFullName.setError("Full name is required");
            binding.edtFullName.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            binding.edtEmail.setError("Email is required");
            binding.edtEmail.requestFocus();
            return false;
        }

        if (phoneNumber.isEmpty()) {
            binding.edtPhone.setError("Phone number is required");
            binding.edtPhone.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            binding.edtPassword.setError("Password is required");
            binding.edtPassword.requestFocus();
            return false;
        }
        if(confirmPassword.isEmpty()){
            binding.edtCPassword.setError("Confirm password is required");
            binding.edtCPassword.requestFocus();
            return false;
        }

        if(!password.equals(confirmPassword)){
            binding.edtCPassword.setError("Password does not match");
            binding.edtCPassword.requestFocus();
            return false;
        }

        return true;
    }
}