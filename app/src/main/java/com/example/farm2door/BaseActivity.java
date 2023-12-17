package com.example.farm2door;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.farm2door.databinding.ActivityBaseBinding;
import com.example.farm2door.fragments.AccountFragment;
import com.example.farm2door.fragments.HomeFragment;
import com.example.farm2door.fragments.OrdersFragment;
import com.google.android.material.navigation.NavigationBarView;

public abstract class BaseActivity extends AppCompatActivity {
    protected abstract Fragment createFragment();
    ActivityBaseBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, createFragment())
                .commit();

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.home_tab) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.orders_tab) {
                selectedFragment = new OrdersFragment();
            } else if (itemId == R.id.account_tab) {
                selectedFragment = new AccountFragment();
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();

            return false;
        });

    }
}