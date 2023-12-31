package com.example.farm2door.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.farm2door.BottomNavFragment;
import com.example.farm2door.MyCart;
import com.example.farm2door.OnRecyclerItemClickListener;
import com.example.farm2door.ProductDetails;
import com.example.farm2door.R;
import com.example.farm2door.adapters.ProductAdapter;
import com.example.farm2door.models.Product;
import com.example.farm2door.viewmodel.ProductViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements OnRecyclerItemClickListener, BottomNavFragment {

    RecyclerView recyclerView;
    ProductAdapter productAdapter;
    List<Product> productList;
    ImageButton imgProfile;
    ProductViewModel productViewModel;

    public HomeFragment() {
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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rvProducts);
        imgProfile = view.findViewById(R.id.imgProfile);

        productList  = new ArrayList<>();
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        // fetch products from firebase
        productViewModel.fetchProducts();

        // observe for products loaded from db
        productViewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                productList.clear();
                productList.addAll(products);
                productAdapter.notifyDataSetChanged();
            }
        });

        // create a layout manager for the recyclerview
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);


        // initialize the adapter
        productAdapter = new ProductAdapter(getContext(), productList, this);
        // set recyclerview to read data from the adapter
        recyclerView.setAdapter(productAdapter);

        // open My Cart activity
        view.findViewById(R.id.imgCart).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), MyCart.class));
        });

        imgProfile.setOnClickListener(v -> {
            // start the account activity and set account tab as the active tab
            //  Intent intent = new Intent(getContext(), AccountActivity.class);
            // startActivity(intent);
        });

    }

    @Override
    public void onItemClick(int position) {
        // get the product at the clicked position
        Product product = productList.get(position);
        Intent intent = new Intent(getContext(), ProductDetails.class);
        intent.putExtra("product", (Serializable) product);
        startActivity(intent);
    }

    @Override
    public Fragment createFragment() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public int getTabIndex() {
        return 0;
    }
}