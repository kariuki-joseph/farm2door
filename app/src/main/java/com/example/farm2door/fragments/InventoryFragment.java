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
import android.widget.Toast;

import com.example.farm2door.AddProduct;
import com.example.farm2door.BottomNavFragment;
import com.example.farm2door.InventoryActivity;
import com.example.farm2door.R;
import com.example.farm2door.adapters.InventoryAdapter;
import com.example.farm2door.models.InventoryItem;
import com.example.farm2door.viewmodel.InventoryViewModel;
import com.example.farm2door.viewmodel.LoadingViewModel;
import com.example.farm2door.viewmodel.ProductViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class InventoryFragment extends Fragment  implements BottomNavFragment, InventoryAdapter.OnInventoryItemClickListener {

    InventoryAdapter inventoryAdapter;
    List<InventoryItem> inventoryItems;
    RecyclerView recyclerView;
    ImageButton btnAddProduct;
    InventoryViewModel inventoryViewModel;
    LoadingViewModel loadingViewModel;
    FirebaseUser firebaseUser;
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

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // initialize viewModels
        inventoryViewModel = new ViewModelProvider(this).get(InventoryViewModel.class);
        loadingViewModel = LoadingViewModel.getInstance();


        inventoryItems = new ArrayList<>();
        // listen for inventory products
        inventoryViewModel.fetchInventoryItems(firebaseUser.getUid());

        // observe for loading state
        loadingViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if(isLoading){
                Toast.makeText(getContext(), "Loading", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();
            }
        });


        inventoryAdapter = new InventoryAdapter(getContext(), inventoryItems,this);

        recyclerView = view.findViewById(R.id.recyclerview);
        btnAddProduct = view.findViewById(R.id.btnAdd);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(inventoryAdapter);

        // observe for inventory items
        inventoryViewModel.getInventoryItems().observe(getViewLifecycleOwner(), items -> {
            inventoryItems.clear();
            inventoryItems.addAll(items);
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
    public void onEditClick(int position) {
        Toast.makeText(getContext(), ""+position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(int position) {
        inventoryItems.remove(position);
        inventoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPredictClick(int position) {
        Toast.makeText(getContext(), ""+position, Toast.LENGTH_SHORT).show();
    }
}