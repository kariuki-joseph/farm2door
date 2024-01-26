package com.example.farm2door;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.farm2door.databinding.ActivityAddLocationBinding;
import com.example.farm2door.helpers.ToolBarHelper;
import com.example.farm2door.models.CartItem;
import com.example.farm2door.viewmodel.CartViewModel;
import com.example.farm2door.viewmodel.LoadingViewModel;
import com.example.farm2door.viewmodel.PlaceOrderViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.List;

public class AddLocation extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapClickListener {
    ActivityAddLocationBinding binding;
    GoogleMap map;
    Polyline polyline;
    Marker draggableMarker;
    LatLng INITIAL_POSITION = new LatLng(-0.391396,  36.933992);
    double orderLat, orderLng; // store the location of the customer making the order
    PlaceOrderViewModel placeOrderViewModel;
    CartViewModel cartViewModel;
    LoadingViewModel loadingViewModel;
    List<CartItem> cartItemList = new ArrayList<>();
    String orderNumber = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ToolBarHelper.setupToolBar(this, binding.toolbar.toolbarLayout, "Add Location", true);

        placeOrderViewModel = new ViewModelProvider(this).get(PlaceOrderViewModel.class);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        loadingViewModel = LoadingViewModel.getInstance();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // listen for cart items load success
        cartViewModel.getCartItems().observe(this, cartItems -> {
            if(cartItems == null){
                Toast.makeText(this, "Error loading cart items", Toast.LENGTH_SHORT).show();
                return;
            }

            cartItemList = new ArrayList<>(cartItems.values());
        });

        // observe when order number has been generated
        placeOrderViewModel.getOrderNumber().observe(this, orderNumber -> {
            if(orderNumber == null){
                Toast.makeText(this, "An error has occurred placing your order! Please try again", Toast.LENGTH_LONG).show();
                finish();
            }

            this.orderNumber = orderNumber;
            // clear cart items now since the order has been placed successfully
            cartViewModel.deleteCartItems();
        });


        // listen for loading status
        loadingViewModel.getIsLoading().observe(this, isLoading-> {
            binding.btnPlaceOrder.setEnabled(isLoading? false : true);
            binding.progressBarLayout.progressBar.setVisibility(isLoading? View.VISIBLE : View.GONE);
        });


        // order placement complete after cart item has been cleared
        cartViewModel.getIsCartItemsDeleted().observe(this, isDeleted -> {
            if(isDeleted){
                Intent intent = new Intent(AddLocation.this, OrderSuccess.class);
                intent.putExtra("orderNumber", orderNumber); // take the first item in the order
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(this, "Failed to delete all cart items", Toast.LENGTH_SHORT).show();
            }
        });

        // place order on button click
        binding.btnPlaceOrder.setOnClickListener(v -> {
            placeOrderViewModel.generateAndPlaceOrders(cartItemList);
        });

        // load cart items
        cartViewModel.fetchCartItems();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        // create custom marker for the draggable marker
        BitmapDescriptor customMarker = BitmapDescriptorFactory.fromResource(R.drawable.location_marker);

        // place marker on initial position
        draggableMarker = map.addMarker(new MarkerOptions().position(INITIAL_POSITION).icon(customMarker).draggable(true));
        // move camera to initial position and set the zoom level
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(INITIAL_POSITION, 15.0f));

        // set this as initial position of customer order
        placeOrderViewModel.setCustomerLocation(INITIAL_POSITION);

        // set the map to listen to marker drag
        map.setOnMarkerDragListener(this);
        // set the map to listen to map click
        map.setOnMapClickListener(this);
    }

    @Override
    public void onMarkerDrag(@NonNull Marker marker) {
        LatLng newPosition = marker.getPosition();
        orderLat = newPosition.latitude;
        orderLng = newPosition.longitude;
        placeOrderViewModel.setCustomerLocation(newPosition);
    }

    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {

    }

    @Override
    public void onMarkerDragStart(@NonNull Marker marker) {

    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        draggableMarker.setPosition(latLng);
        orderLat = latLng.latitude;
        orderLng = latLng.longitude;
        placeOrderViewModel.setCustomerLocation(latLng);
    }
}