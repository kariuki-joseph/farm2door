package com.example.farm2door;

import androidx.appcompat.app.AppCompatActivity;
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

import java.util.ArrayList;
import java.util.List;

public class MyCart extends AppCompatActivity implements CartAdapter.OnQuantityClickListener{

    private List<CartItem> cartItems;
    private double totalAmount;
    private CartAdapter adapter;
    ActivityMyCartBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMyCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // enable toolbar
        ToolBarHelper.setupToolBar(this, binding.toolbar.toolbarLayout, "My Cart", true);

        cartItems = createCartItems();
        adapter = new CartAdapter(this, cartItems, this);

        // create a layout manager for the recyclerview
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.recyclerview.setLayoutManager(linearLayoutManager);
        binding.recyclerview.setHasFixedSize(true);

        binding.recyclerview.setAdapter(adapter);


        binding.btnCheckout.setOnClickListener(v -> {
            Intent intent = new Intent(MyCart.this, AddLocation.class);
            startActivity(intent);
        });
    }

    private List<CartItem> createCartItems() {
        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(new CartItem("1", "Mangoes",  200, "kg", "https://cdn.pixabay.com/photo/2017/01/27/11/54/milk-bottle-2012800_640.png"));
        cartItems.add(new CartItem("2", "Oranges",  190, "kg", "https://cdn.pixabay.com/photo/2017/05/16/17/33/holstein-cattle-2318436_640.jpg"));
        return cartItems;
    }

    @Override
    public void onIncreaseClick(int position) {
        double price = cartItems.get(position).getProductPrice();
        int quantity = cartItems.get(position).getProductQuantity();
        // increase quantity
        cartItems.get(position).setProductQuantity(quantity + 1);
        // recalculate total price
        cartItems.get(position).setProductTotalPrice(++quantity * price);
        // refresh recyclerview to reflect changes
        adapter.notifyItemChanged(position);
    }

    @Override
    public void onDecreaseClick(int position) {
        double price = cartItems.get(position).getProductPrice();
        int quantity = cartItems.get(position).getProductQuantity();

        if (quantity == 1) {
            // remove item from cart
            cartItems.remove(position);
            adapter.notifyDataSetChanged();
            return;
        }

        // decrease quantity
        cartItems.get(position).setProductQuantity(quantity - 1);
        // recalculate total price
        cartItems.get(position).setProductTotalPrice(--quantity * price);
        // refresh recyclerview to reflect changes
        adapter.notifyItemChanged(position);
    }
}