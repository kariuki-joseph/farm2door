package com.example.farm2door;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.example.farm2door.fragments.AccountFragment;

public class AccountActivity extends BaseActivity{
        @Override
        protected Fragment createFragment() {
            return new AccountFragment();
        }

    @Override
    protected int getActiveTabIndex() {
        return 2;
    }
}
