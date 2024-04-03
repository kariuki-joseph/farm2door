package com.example.farm2door.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.farm2door.BottomNavFragment;
import com.example.farm2door.R;
import com.example.farm2door.helpers.AuthHelper;

public class AccountFragment extends Fragment implements BottomNavFragment {
    private static final String SELECTED_TAB_INDEX = "SELECTED_TAB_INDEX";

    public AccountFragment() {
        // Required empty public constructor
    }

    public static AccountFragment newInstance(int selectedIndex) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putInt(AccountFragment.SELECTED_TAB_INDEX, selectedIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public Fragment createFragment() {
        AccountFragment fragment = new AccountFragment();
        return fragment;
    }

    @Override
    public int getTabIndex() {
        return AuthHelper.getInstance(getContext()).isUserFarmer() ? 3 : 2;
    }

}
