package com.example.farm2door;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.farm2door.databinding.ActivityAddLocationBinding;
import com.example.farm2door.helpers.ToolBarHelper;

public class AddLocation extends AppCompatActivity {

    ActivityAddLocationBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ToolBarHelper.setupToolBar(this, binding.toolbar.toolbarLayout, "Add Location", true);

        binding.btnAddLocation.setOnClickListener(v -> {
            Intent intent = new Intent(AddLocation.this, OrderSuccess.class);
            startActivity(intent);
        });
    }
}