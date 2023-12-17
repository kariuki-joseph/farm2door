package com.example.farm2door;

import androidx.fragment.app.Fragment;

import com.example.farm2door.fragments.HomeFragment;

public class HomeActivity extends BaseActivity{

    @Override
    protected Fragment createFragment() {
        return new HomeFragment();
    }
}
