package com.example.farm2door.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.farm2door.BottomNavFragment;
import com.example.farm2door.R;

public class InventoryFragment extends Fragment  implements BottomNavFragment {

    public InventoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inventory, container, false);
    }

    @Override
    public Fragment createFragment() {
        InventoryFragment fragment = new InventoryFragment();
        return fragment;
    }

    @Override
    public int getTabIndex() {
        return 1;
    }
}