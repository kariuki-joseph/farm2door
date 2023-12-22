package com.example.farm2door.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment implements OrderItemAdapter.OrderItemListener, BottomNavFragment {

    private List<OrderItem> orderItems;
    OrderItemAdapter orderItemAdapter;
    RecyclerView recyclerView;
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
        orderItems = createOrderItems();

        // create a layout manager for the recyclerview
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        // create an adapter for the recyclerview
        orderItemAdapter = new OrderItemAdapter(getContext(), orderItems, this);
        recyclerView.setAdapter(orderItemAdapter);
    }

    private List<OrderItem> createOrderItems() {
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(new OrderItem("FD001-01", "Yoghurt", 200, "packet", 2, "20/12/2023", "https://cdn.pixabay.com/photo/2016/10/31/18/25/yogurt-1786329_640.jpg"));
        orderItems.add(new OrderItem("FD001-02", "Milk", 100, "litre", 1, "20/12/2023", "https://cdn.pixabay.com/photo/2016/10/31/18/25/yogurt-1786329_640.jpg"));
        orderItems.add(new OrderItem("FD001-03", "Cheese", 300, "packet", 1, "19/12/2023", "https://cdn.pixabay.com/photo/2016/10/31/18/25/yogurt-1786329_640.jpg"));
        orderItems.add(new OrderItem("FD001-04", "Maize", 2000, "bag", 1, "30/11/2023", "https://cdn.pixabay.com/photo/2016/10/31/18/25/yogurt-1786329_640.jpg"));
        orderItems.add(new OrderItem("FD001-05", "Carrot", 700, "crate", 2, "10/12/2023", "https://cdn.pixabay.com/photo/2016/10/31/18/25/yogurt-1786329_640.jpg"));

        return orderItems;
    }

    @Override
    public void onDeleteClick(int position) {
        orderItems.remove(position);
        orderItemAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDynamicButtonClick(int position) {
        Intent intent = new Intent(getContext(), TrackOrder.class);
        intent.putExtra("orderNumber", orderItems.get(position).getOrderNumber());
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