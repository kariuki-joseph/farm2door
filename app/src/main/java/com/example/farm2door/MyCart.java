package com.example.farm2door;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.farm2door.adapters.CartAdapter;
import com.example.farm2door.databinding.ActivityMyCartBinding;
import com.example.farm2door.databinding.ActivityProductDetailsBinding;
import com.example.farm2door.helpers.ToolBarHelper;
import com.example.farm2door.models.CartItem;
import com.example.farm2door.viewmodel.CartViewModel;
import com.example.farm2door.viewmodel.LoadingViewModel;

import java.util.ArrayList;
import java.util.List;

public class MyCart extends AppCompatActivity implements CartAdapter.OnQuantityClickListener{
    private double totalAmount;
    private CartAdapter adapter;
    ActivityMyCartBinding binding;
    LoadingViewModel loadingViewModel;
    CartViewModel cartViewModel;
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

            // set order
            adapter.setCartItems(new ArrayList<>(cartItems.values()));
            adapter.notifyDataSetChanged();
        });

        // observe cart total amount
        cartViewModel.getTotalAmount().observe(this, totalAmount -> {
            binding.tvTotalPrice.setText("Ksh. "+totalAmount);
        });

        // load cart items from database
        cartViewModel.fetchCartItems();

        binding.btnCheckout.setOnClickListener(v -> {
            Intent intent = new Intent(MyCart.this, AddLocation.class);
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