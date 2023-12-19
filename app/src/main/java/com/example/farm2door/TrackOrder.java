package com.example.farm2door;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.farm2door.databinding.ActivityTrackOrderBinding;
import com.example.farm2door.helpers.ToolBarHelper;

public class TrackOrder extends AppCompatActivity {
    ActivityTrackOrderBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrackOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // switch layouts on click
        binding.alreadyDelivered.setOnClickListener(v -> {
            switchLayouts();
        });

        // back button
        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
    }


    private void switchLayouts() {
        binding.orderDetailsLayout.setVisibility(View.GONE);
        binding.orderDeliveredLayout.setVisibility(View.VISIBLE);
    }
}