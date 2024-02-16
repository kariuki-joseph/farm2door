package com.example.farm2door;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.farm2door.databinding.ActivityOrderSuccessBinding;

public class OrderSuccess extends AppCompatActivity {

    ActivityOrderSuccessBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOrderSuccessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // get passed order id
        String orderNumber = getIntent().getStringExtra("orderNumber");
        String farmerId = getIntent().getStringExtra("farmerId");

        // get order id passed as 
        binding.btnTrackMyOrder.setOnClickListener(v -> {
            Intent intent = new Intent(OrderSuccess.this, TrackOrder.class);
            intent.putExtra("orderNumber", orderNumber);
            intent.putExtra("farmerId", farmerId);
            startActivity(intent);
            finish();
        });
    }
}
