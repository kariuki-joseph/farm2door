package com.example.farm2door;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.farm2door.databinding.ActivityBaseBinding;
import com.example.farm2door.fragments.AccountFragment;
import com.example.farm2door.fragments.HomeFragment;
import com.example.farm2door.fragments.InventoryFragment;
import com.example.farm2door.fragments.OrdersFragment;
import com.example.farm2door.helpers.AuthHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseActivity extends AppCompatActivity {
    protected abstract Fragment createFragment();
    protected abstract int getActiveTabIndex();
    ActivityBaseBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupBottomNavigationBar();

        setSelectTab(0);

    }

    protected void setSelectTab(int index) {
        binding.bottomNavigationView.getMenu().getItem(index).setChecked(true);
    }

    protected void setupBottomNavigationBar() {
        Map<Integer, BottomNavFragment> bottomNavFragments = new HashMap<>();
        bottomNavFragments.put(R.id.home_tab, new HomeFragment());
        bottomNavFragments.put(R.id.orders_tab, new OrdersFragment());
        bottomNavFragments.put(R.id.account_tab, new AccountFragment());
        bottomNavFragments.put(R.id.inventory_tab, new InventoryFragment());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, createFragment())
                .commit();

        // hide Inventory tab if user is not admin
        if (!AuthHelper.isUserAdmin()) {
            binding.bottomNavigationView.getMenu().removeItem(R.id.inventory_tab);
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            BottomNavFragment selectedFragment = bottomNavFragments.get(itemId);
            if (selectedFragment != null) {

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment.createFragment())
                        .commit();
            }

            // update selected tab item
            setSelectTab(selectedFragment.getTabIndex());

            return false;
        });
    }
}