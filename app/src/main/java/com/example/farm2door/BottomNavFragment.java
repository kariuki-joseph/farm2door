package com.example.farm2door;

import androidx.fragment.app.Fragment;

public interface BottomNavFragment {
    Fragment createFragment();
    int getTabIndex();
}