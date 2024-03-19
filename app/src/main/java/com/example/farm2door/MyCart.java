package com.example.farm2door;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.farm2door.adapters.CartAdapter;
import com.example.farm2door.databinding.ActivityMyCartBinding;
import com.example.farm2door.helpers.ToolBarHelper;
import com.example.farm2door.models.CartItem;
import com.example.farm2door.viewmodel.CartViewModel;
import com.example.farm2door.viewmodel.LoadingViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MyCart extends AppCompatActivity implements CartAdapter.OnQuantityClickListener{
    private double totalAmount;
    private CartAdapter adapter;
    ActivityMyCartBinding binding;
    LoadingViewModel loadingViewModel;
    CartViewModel cartViewModel;
    List<String> farmerDeliveryCost;
    ArrayAdapter<String> deliveriesAdapter;
    String orderId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMyCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // enable toolbar
        ToolBarHelper.setupToolBar(this, binding.toolbar.toolbarLayout, "My Cart", true);

        loadingViewModel = LoadingViewModel.getInstance();
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        adapter = new CartAdapter(this, this);
        farmerDeliveryCost = new ArrayList<>();
        deliveriesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, farmerDeliveryCost);
        binding.farmerDeliveryFees.setAdapter(deliveriesAdapter);

        // create a layout manager for the recyclerview
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.recyclerview.setLayoutManager(linearLayoutManager);
        binding.recyclerview.setHasFixedSize(true);

        binding.recyclerview.setAdapter(adapter);

        // observe cart items
        cartViewModel.getCartItems().observe(this, cartItems -> {
            if(cartItems == null){
                Toast.makeText(this, "Error loading cart items", Toast.LENGTH_SHORT).show();
                return;
            }

            // set cart items
            ArrayList cartItemArrayList = new ArrayList<>(cartItems.values());

            adapter.setCartItems(cartItemArrayList);
            adapter.notifyDataSetChanged();
        });

        // observe cart total amount
        cartViewModel.getTotalAmount().observe(this, totalAmount -> {
            binding.tvTotalPrice.setText("Ksh. "+totalAmount);
        });

        // observe delivery fees and costs per farmer
        cartViewModel.getCostsPerFarmer().observe(this, costsPerFarmer -> {
            if(costsPerFarmer == null){
                Toast.makeText(this, "Error loading delivery fees", Toast.LENGTH_SHORT).show();
                return;
            }

            farmerDeliveryCost.clear();
            for(String farmerId: costsPerFarmer.keySet()){
                farmerDeliveryCost.add(costsPerFarmer.get(farmerId).get("farmerName")+"         Ksh. "+costsPerFarmer.get(farmerId).get("deliveryFees"));
            }
            deliveriesAdapter.notifyDataSetChanged();
        });

        // load cart items from database
        cartViewModel.fetchCartItems();

        binding.btnCheckout.setOnClickListener(v -> {
            Toast.makeText(this, "Make Payments", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MyCart.this, MakePayments.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onIncreaseClick(CartItem cartItem) {
        cartViewModel.increaseQuantity(cartItem);
    }

    @Override
    public void onDecreaseClick(CartItem cartItem) {
        cartViewModel.decreaseQuantity(cartItem);
    }
}