package com.example.farm2door;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.farm2door.databinding.ActivityForgotPasswordBinding;
import com.example.farm2door.helpers.ToolBarHelper;

public class ForgotPassword extends AppCompatActivity {

    ActivityForgotPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ToolBarHelper.setupToolBar(this, binding.toolbarLayout.toolbar, "Forgot Password", true);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            ForgotPassword.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}