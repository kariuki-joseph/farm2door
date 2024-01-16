package com.example.farm2door.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.farm2door.BottomNavFragment;
import com.example.farm2door.R;
import com.example.farm2door.TrackOrder;
import com.example.farm2door.adapters.OrderItemAdapter;
import com.example.farm2door.helpers.AuthHelper;
import com.example.farm2door.models.OrderItem;
import com.example.farm2door.viewmodel.OrdersViewModel;
import com.google.firestore.v1.StructuredQuery;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment implements OrderItemAdapter.OrderItemListener, BottomNavFragment {
    OrderItemAdapter orderItemAdapter;
    RecyclerView recyclerView;

    OrdersViewModel ordersViewModel;
    public OrdersFragment() {
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
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerview);
        ordersViewModel = new ViewModelProvider(this).get(OrdersViewModel.class);

        // create a layout manager for the recyclerview
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        // create an adapter for the recyclerview
        orderItemAdapter = new OrderItemAdapter(getContext(), this);
        recyclerView.setAdapter(orderItemAdapter);

        // observe order items
        ordersViewModel.getOrderItems().observe(getViewLifecycleOwner(), orderItems -> {
            if(orderItems == null){
                return;
            }
            orderItemAdapter.setOrderItems(orderItems);
            orderItemAdapter.notifyDataSetChanged();
        });

        // get order items from database
        ordersViewModel.fetchOrderItems();
    }

    @Override
    public void onDeleteClick(OrderItem orderItem) {
        ordersViewModel.deleteOrderItem(orderItem);
    }

    @Override
    public void onDynamicButtonClick(OrderItem orderItem) {
        Intent intent = new Intent(getContext(), TrackOrder.class);
        intent.putExtra("orderId", orderItem.getId());
        startActivity(intent);
    }

    @Override
    public Fragment createFragment() {
        OrdersFragment fragment = new OrdersFragment();
        return fragment;
    }

    @Override
    public int getTabIndex() {
        return AuthHelper.getInstance(getContext()).isUserFarmer() ? 2 : 1;
    }
}