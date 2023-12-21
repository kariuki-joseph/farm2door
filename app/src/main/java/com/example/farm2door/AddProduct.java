package com.example.farm2door;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.farm2door.databinding.ActivityAddProductBinding;
import com.example.farm2door.helpers.ToolBarHelper;

public class AddProduct extends AppCompatActivity {

    ActivityAddProductBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        ToolBarHelper.setupToolBar(this, binding.toolbar.toolbarLayout, "Add Product", true);
    }
}