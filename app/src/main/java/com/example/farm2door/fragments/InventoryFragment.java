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
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.farm2door.AddProduct;
import com.example.farm2door.BottomNavFragment;
import com.example.farm2door.InventoryActivity;
import com.example.farm2door.R;
import com.example.farm2door.adapters.InventoryAdapter;
import com.example.farm2door.models.InventoryItem;

import java.util.ArrayList;
import java.util.List;

public class InventoryFragment extends Fragment  implements BottomNavFragment, InventoryAdapter.OnInventoryItemClickListener {

    InventoryAdapter inventoryAdapter;
    List<InventoryItem> inventoryItems;
    RecyclerView recyclerView;
    ImageButton btnAddProduct;
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

        inventoryItems = createInventoryItems();
        inventoryAdapter = new InventoryAdapter(getContext(), inventoryItems,this);

        recyclerView = view.findViewById(R.id.recyclerview);
        btnAddProduct = view.findViewById(R.id.btnAdd);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(inventoryAdapter);


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

    private List<InventoryItem> createInventoryItems() {
        List<InventoryItem> inventoryItems = new ArrayList<>();
        inventoryItems.add(new InventoryItem("Coffee", 200, 200, "packet", "https://cdn.pixabay.com/photo/2010/12/13/10/24/cheese-2785_640.jpg"));
        inventoryItems.add(new InventoryItem("Milk", 200, 100, "litre", "https://cdn.pixabay.com/photo/2010/12/13/10/24/cheese-2785_640.jpg"));
        inventoryItems.add(new InventoryItem("Tea", 200, 200, "sachet", "https://cdn.pixabay.com/photo/2010/12/13/10/24/cheese-2785_640.jpg"));
        inventoryItems.add(new InventoryItem("Maize", 200, 2000, "Bag", "https://cdn.pixabay.com/photo/2010/12/13/10/24/cheese-2785_640.jpg"));
        inventoryItems.add(new InventoryItem("Carrot", 200, 200, "crate", "https://cdn.pixabay.com/photo/2010/12/13/10/24/cheese-2785_640.jpg"));
        inventoryItems.add(new InventoryItem("Onions", 200, 250, "kg", "https://cdn.pixabay.com/photo/2010/12/13/10/24/cheese-2785_640.jpg"));
        inventoryItems.add(new InventoryItem("Bananas", 200, 100, "banana", "https://cdn.pixabay.com/photo/2010/12/13/10/24/cheese-2785_640.jpg"));
        return inventoryItems;
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