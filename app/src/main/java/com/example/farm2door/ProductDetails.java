package com.example.farm2door;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.farm2door.adapters.CustomerFeedbackAdapter;
import com.example.farm2door.adapters.ImagePagerAdapter;
import com.example.farm2door.databinding.ActivityProductDetailsBinding;
import com.example.farm2door.helpers.ToolBarHelper;
import com.example.farm2door.models.CustomerFeedback;
import com.example.farm2door.models.Product;
import com.example.farm2door.viewmodel.CartViewModel;
import com.example.farm2door.viewmodel.LoadingViewModel;
import com.example.farm2door.viewmodel.ProductDetailsViewModel;
import com.example.farm2door.viewmodel.ProductViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class ProductDetails extends AppCompatActivity implements OnRecyclerItemClickListener, OnMapReadyCallback {

    ActivityProductDetailsBinding binding;

    List<CustomerFeedback> customerFeedbacks;
    Product receivedProduct;
    List<String> imageUrls;
    LoadingViewModel loadingViewModel;
    CartViewModel cartViewModel;
    ProductDetailsViewModel productDetailsViewModel;
    GoogleMap map;
    Marker farmerMarker;
    final LatLng INITIAL_POSITION = new LatLng(-0.391396,  36.933992);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProductDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // enable toolbar
        ToolBarHelper.setupToolBar(this, binding.toolbar.toolbarLayout, "Product Details", true);

        // initialize ViewModels
        loadingViewModel = LoadingViewModel.getInstance();
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        productDetailsViewModel = new ViewModelProvider(this).get(ProductDetailsViewModel.class);

        // setup map and get notified when ready
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.farmerMap);
        mapFragment.getMapAsync(this);

        // load image adapter for our carousel(ViewPager)
        ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(this);
        binding.viewPager.setAdapter(imagePagerAdapter);

        customerFeedbacks = createCustomerFeedbacks();


        // Adapter for our recyclerview
        CustomerFeedbackAdapter feedbackAdapter = new CustomerFeedbackAdapter(this, customerFeedbacks,this);

        receivedProduct = (Product) getIntent().getSerializableExtra("product");

        // observe loaded product
        productDetailsViewModel.getProduct().observe(this, product -> {
            if(product == null){
                Toast.makeText(this, "Error loading product", Toast.LENGTH_SHORT).show();
                return;
            }

            this.receivedProduct = product;
            binding.productName.setText(product.getName());
            binding.productPrice.setText(String.format("Ksh. %s", product.getPrice()));
            binding.productDescription.setText(product.getDescription());
            imagePagerAdapter.setImageUrls(product.getImages());
            imagePagerAdapter.notifyDataSetChanged();

            // update the location of the farmer on the map
            updateMarkerPosition(new LatLng(product.getLatitude(), product.getLongitude()));
        });

        // observe cart item added to cart
        cartViewModel.getCartItemAddSuccess().observe(this, success -> {
            binding.btnAddToCart.setEnabled(true);

            if(success){
                Toast.makeText(this, "Add successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, AddLocation.class);
                Log.d("ProductDetails.java", "Starting new intent with product id "+receivedProduct.getProductId());
                intent.putExtra("productId", receivedProduct.getProductId());
                startActivity(intent);
            }else {
                Toast.makeText(this, "Unable to add item to cart", Toast.LENGTH_SHORT).show();
            }
        });

        // observe loading and disable Add to cart button
        loadingViewModel.getIsLoading().observe(this, isLoading -> {
            binding.btnAddToCart.setEnabled(isLoading ? false : true);
            binding.btnAddToCart.setText(isLoading ? "Adding..." : "Add to Cart");
        });

        // load product details
        productDetailsViewModel.fetchProduct(receivedProduct.getProductId());

        // handle previous and next button clicks
        binding.btnPrev.setOnClickListener(v -> {
            int currentItem = binding.viewPager.getCurrentItem();
            if (currentItem > 0) {
                binding.viewPager.setCurrentItem(currentItem - 1);
            }
        });

        binding.btnNext.setOnClickListener(v -> {
            int currentItem = binding.viewPager.getCurrentItem();
            if (currentItem < receivedProduct.getImages().size() - 1) {
                binding.viewPager.setCurrentItem(currentItem + 1);
            }
        });


        // add product to cart
        binding.btnAddToCart.setOnClickListener(v -> {
            cartViewModel.addItemToCart(receivedProduct);

            binding.btnAddToCart.setText("Adding...");
            binding.btnAddToCart.setEnabled(false);
        });
    }

    private List<CustomerFeedback> createCustomerFeedbacks() {
        List<CustomerFeedback> feedbacks = new ArrayList<>();

        return feedbacks;
    }


    @Override
    public void onItemClick(int position) {
        CustomerFeedback customerFeedback = customerFeedbacks.get(position);
        Toast.makeText(this, customerFeedback.getCustomerName(), Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
    }


    // update the position of the marker
    private void updateMarkerPosition(LatLng newPosition){
        // set initial marker position
        if(map != null){
            farmerMarker = map.addMarker(new MarkerOptions().position(newPosition).title("Farmer"));
            farmerMarker.showInfoWindow();
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(newPosition, 16));
        }else {
            Toast.makeText(this, "Map is null at the moment", Toast.LENGTH_SHORT).show();
        }
    }
}