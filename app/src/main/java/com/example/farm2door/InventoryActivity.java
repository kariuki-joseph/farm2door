package com.example.farm2door;

import androidx.fragment.app.Fragment;

import com.example.farm2door.fragments.InventoryFragment;

public class InventoryActivity extends BaseActivity{
    @Override
    protected Fragment createFragment() {
        return new InventoryFragment();
    }

    @Override
    protected int getActiveTabIndex() {
        return 1;
    }
}
