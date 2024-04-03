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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farm2door.AddProduct;
import com.example.farm2door.BottomNavFragment;
import com.example.farm2door.InventoryActivity;
import com.example.farm2door.PredictActivity;
import com.example.farm2door.R;
import com.example.farm2door.adapters.InventoryAdapter;
import com.example.farm2door.models.InventoryItem;
import com.example.farm2door.repository.AuthRepository;
import com.example.farm2door.viewmodel.InventoryViewModel;
import com.example.farm2door.viewmodel.LoadingViewModel;
import com.example.farm2door.viewmodel.ProductViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class InventoryFragment extends Fragment  implements BottomNavFragment, InventoryAdapter.OnInventoryItemClickListener {

    InventoryAdapter inventoryAdapter;
    RecyclerView recyclerView;
    ImageButton btnAddProduct;
    InventoryViewModel inventoryViewModel;
    LoadingViewModel loadingViewModel;
    FirebaseUser firebaseUser;
    ProgressBar progressBar;
    TextView tvInStock, tvOutOfStock;
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerview);
        btnAddProduct = view.findViewById(R.id.btnAdd);
        progressBar = view.findViewById(R.id.progressBarLayout).findViewById(R.id.progressBar);
        tvInStock = view.findViewById(R.id.tvInStock);
        tvOutOfStock = view.findViewById(R.id.tvOutOfStock);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // initialize viewModels
        inventoryViewModel = new ViewModelProvider(this).get(InventoryViewModel.class);
        loadingViewModel = LoadingViewModel.getInstance();

        // listen for inventory products
        inventoryViewModel.fetchInventoryItems(AuthRepository.getLoggedInUserId(getContext()));

        // observe for loading state
        loadingViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
           progressBar.setVisibility(isLoading? View.VISIBLE: View.GONE);
        });

        inventoryAdapter = new InventoryAdapter(getContext(),this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(inventoryAdapter);

        // observe for inventory items
        inventoryViewModel.getInventoryItems().observe(getViewLifecycleOwner(), items -> {

            // set in stock and out of stock count
            int inStockCount = 0;
            int outOfStockCount = 0;
            for (InventoryItem item: items) {
                if(item.getRemainingQuantity() > 0){
                    inStockCount++;
                }else{
                    outOfStockCount++;
                }
            }

            tvInStock.setText(String.valueOf(inStockCount));
            tvOutOfStock.setText(String.valueOf(outOfStockCount));

            inventoryAdapter.setInventoryItems(items);
            inventoryAdapter.notifyDataSetChanged();
        });

        // open add product activity
        btnAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddProduct.class);
            startActivity(intent);
        });
    }

    @Override
    public int getTabIndex() {
        return 1;
    }

    // On inventory item click listener methods
    @Override
    public void onEditClick(InventoryItem inventoryItem) {

    }

    @Override
    public void onDeleteClick(InventoryItem inventoryItem) {
        inventoryViewModel.deleteInventoryItem(inventoryItem, AuthRepository.getLoggedInUserId(getContext()));
    }

    @Override
    public void onPredictClick(InventoryItem inventoryItem) {
        Intent intent = new Intent(getContext(), PredictActivity.class);
        startActivity(intent);
    }
}