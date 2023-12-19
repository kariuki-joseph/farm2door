package com.example.farm2door;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.farm2door.databinding.ActivityOrderSuccessBinding;
import com.example.farm2door.helpers.ToolBarHelper;

public class OrderSuccess extends AppCompatActivity {

    ActivityOrderSuccessBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOrderSuccessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnTrackMyOrder.setOnClickListener(v -> {
            Intent intent = new Intent(OrderSuccess.this, TrackOrder.class);
            startActivity(intent);
            finish();
        });
    }
}