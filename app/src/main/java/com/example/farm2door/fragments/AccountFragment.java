package com.example.farm2door.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.farm2door.BottomNavFragment;
import com.example.farm2door.Login;
import com.example.farm2door.R;
import com.example.farm2door.helpers.AuthHelper;
import com.example.farm2door.models.User;

public class AccountFragment extends Fragment implements BottomNavFragment {
    private static final String SELECTED_TAB_INDEX = "SELECTED_TAB_INDEX";
    TextView tvFullName, tvEmail, tvPhone, tvLocation, tvRole;
    Button btnLogout;


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
        View v = inflater.inflate(R.layout.fragment_account, container, false);
        // Inflate the layout for this fragment
        tvFullName = v.findViewById(R.id.fullName);
        tvEmail = v.findViewById(R.id.email);
        tvPhone = v.findViewById(R.id.phone);
        tvRole = v.findViewById(R.id.role);
        btnLogout = v.findViewById(R.id.btnLogout);

        // load saved user
        User user = AuthHelper.getInstance(getContext()).getSavedUser();
        if (user != null) {
            tvFullName.setText(user.getFullName());
            tvEmail.setText(user.getEmail());
            tvPhone.setText(user.getPhoneNumber());
            tvRole.setText(user.getUserType());
        }

        // logout button click listener
        btnLogout.setOnClickListener(view -> {
            AuthHelper.getInstance(getContext()).logout();
            Intent intent = new Intent(getContext(), Login.class);
            startActivity(intent);
        });
        return v;
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
