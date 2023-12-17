package com.example.farm2door;

import androidx.fragment.app.Fragment;

import com.example.farm2door.fragments.OrdersFragment;

public class OrdersActivity extends BaseActivity{

        @Override
        protected Fragment createFragment() {
            return new OrdersFragment();
        }
}
