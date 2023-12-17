package com.example.farm2door;

import androidx.fragment.app.Fragment;

import com.example.farm2door.fragments.AccountFragment;

public class AccountActivity extends BaseActivity{
        @Override
        protected Fragment createFragment() {
            return new AccountFragment();
        }
}
