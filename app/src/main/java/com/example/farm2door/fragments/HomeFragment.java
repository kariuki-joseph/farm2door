package com.example.farm2door.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.farm2door.AccountActivity;
import com.example.farm2door.BottomNavFragment;
import com.example.farm2door.MyCart;
import com.example.farm2door.OnRecyclerItemClickListener;
import com.example.farm2door.ProductDetails;
import com.example.farm2door.R;
import com.example.farm2door.adapters.ProductAdapter;
import com.example.farm2door.models.Product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements OnRecyclerItemClickListener, BottomNavFragment {

    RecyclerView recyclerView;
    ProductAdapter productAdapter;
    List<Product> productList;
    ImageButton imgProfile;

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
        productList  = createProductList();

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

    private List<Product> createProductList() {
        List<Product> productList = new ArrayList<>();
        productList.add(new Product("Apple", 300, "kg", "https://www.shutterstock.com/image-photo/various-dairy-products-600nw-627224804.jpg"));
        productList.add(new Product("Banana", 200, "kg", "https://media.gettyimages.com/id/1297005217/photo/farmer-pouring-raw-milk-into-container.jpg?s=612x612&w=gi&k=20&c=y-l6-RUdnfJ4y-O5vJfR5SP0nj_qKSIFxUurRbrykuk="));
        productList.add(new Product("Orange", 100, "kg", "https://cdn.pixabay.com/photo/2016/12/06/18/27/cereal-1887237_640.jpg"));
        productList.add(new Product("Mango", 400, "kg", "https://cdn.pixabay.com/photo/2010/12/13/10/24/cheese-2785_640.jpg"));
        productList.add(new Product("Pineapple", 500, "kg", "https://cdn.pixabay.com/photo/2018/02/26/16/30/eggs-3183410_640.jpg"));
        productList.add(new Product("Pawpaw", 300, "kg", "https://cdn.pixabay.com/photo/2016/10/31/18/25/yogurt-1786329_640.jpg"));
        productList.add(new Product("Coffee", 290, "kg", "https://cdn.pixabay.com/photo/2018/02/25/07/15/food-3179853_640.jpg"));
        productList.add(new Product("Tea", 100, "kg", "https://cdn.pixabay.com/photo/2017/01/27/11/54/milk-bottle-2012800_640.png"));
        productList.add(new Product("Milk", 80, "kg", "https://cdn.pixabay.com/photo/2016/08/27/04/03/coconut-milk-1623611_640.jpg"));
        productList.add(new Product("Rice", 50, "kg", "https://cdn.pixabay.com/photo/2017/05/16/17/33/holstein-cattle-2318436_640.jpg"));
        return productList;
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