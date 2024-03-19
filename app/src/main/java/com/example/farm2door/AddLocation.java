package com.example.farm2door;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.farm2door.databinding.ActivityAddLocationBinding;
import com.example.farm2door.helpers.LocationManagerHelper;
import com.example.farm2door.helpers.ToolBarHelper;
import com.example.farm2door.interfaces.MyLocationListener;
import com.example.farm2door.models.CartItem;
import com.example.farm2door.models.Product;
import com.example.farm2door.viewmodel.CartViewModel;
import com.example.farm2door.viewmodel.LoadingViewModel;
import com.example.farm2door.viewmodel.PlaceOrderViewModel;
import com.example.farm2door.viewmodel.ProductDetailsViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddLocation extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapClickListener, GoogleMap.OnMyLocationButtonClickListener {
    private String TAG = "AddLocation";
    ActivityAddLocationBinding binding;
    GoogleMap map;
    Polyline polyline;
    Marker draggableMarker;
    double orderLat = -0.391396, orderLng =36.933992; // store the location of the customer making the order
    LatLng INITIAL_POSITION = new LatLng(orderLat, orderLng);
    LatLng farmerPosition;
    PlaceOrderViewModel placeOrderViewModel;
    CartViewModel cartViewModel;
    LoadingViewModel loadingViewModel;
    ProductDetailsViewModel productDetailsViewModel;
    List<CartItem> cartItemList = new ArrayList<>();
    String orderNumber = "", receivedProductId = "";
    Marker farmerMarker;
    float distance = 0.0f; // initial distance between farmer and customer
    final int DELIVERY_FEE = 50; // delivery fee per kilometer
    private Product product;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ToolBarHelper.setupToolBar(this, binding.toolbar.toolbarLayout, "Choose Delivery Location", true);

        receivedProductId  = getIntent().getStringExtra("productId");

        placeOrderViewModel = new ViewModelProvider(this).get(PlaceOrderViewModel.class);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        productDetailsViewModel = new ViewModelProvider(this).get(ProductDetailsViewModel.class);

        loadingViewModel = LoadingViewModel.getInstance();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // listen for received product finish loading
        // observe loaded product
        productDetailsViewModel.getProduct().observe(this, product -> {
            if(product == null){
                Toast.makeText(this, "Error loading product", Toast.LENGTH_SHORT).show();
                return;
            }

            this.product = product;

            // update the location of the farmer on the map
            if(farmerMarker != null){
                farmerPosition = new LatLng(product.getLatitude(), product.getLongitude());
                farmerMarker.setPosition(farmerPosition);
            }
        });

        // listen for cart items load success
        cartViewModel.getCartItems().observe(this, cartItems -> {
            if (cartItems == null) {
                Toast.makeText(this, "Error loading cart items", Toast.LENGTH_SHORT).show();
                return;
            }

            cartItemList = new ArrayList<>(cartItems.values());
        });

        // observe when order number has been generated
        placeOrderViewModel.getOrderNumber().observe(this, orderNumber -> {
            if (orderNumber == null) {
                Toast.makeText(this, "An error has occurred placing your order! Please try again", Toast.LENGTH_LONG).show();
                finish();
            }

            this.orderNumber = orderNumber;
            // clear cart items now since the order has been placed successfully
            cartViewModel.deleteCartItems();
        });

        // listen for cart item add success
        cartViewModel.getCartItemAddSuccess().observe(this, success -> {
            if(success){
                Toast.makeText(this, "Cart item added successfully", Toast.LENGTH_SHORT).show();
                finish();
            }else {
                Toast.makeText(this, "Failed to set delivery location. Please try again", Toast.LENGTH_SHORT).show();
            }
        });


        // listen for loading status
        loadingViewModel.getIsLoading().observe(this, isLoading -> {
            binding.btnChooseLocation.setEnabled(isLoading ? false : true);
            binding.progressBarLayout.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });


        // order placement complete after cart item has been cleared
        cartViewModel.getIsCartItemsDeleted().observe(this, isDeleted -> {
            if (isDeleted) {
                Intent intent = new Intent(AddLocation.this, OrderSuccess.class);
                intent.putExtra("orderNumber", orderNumber); // take the first item in the order
                intent.putExtra("farmerId", cartItemList.get(0).getFarmerId());
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Failed to delete all cart items", Toast.LENGTH_SHORT).show();
            }
        });

        // once delivery fees has been updated to database, the location has been selected successfully
        cartViewModel.getIsDeliveryFeesUpdated().observe(this, success -> {
            if(success){
                Toast.makeText(this, "Location added successfully", Toast.LENGTH_SHORT).show();
                finish();
            }else {
                Toast.makeText(this, "Failed to add location. Please try again", Toast.LENGTH_SHORT).show();
            }
        });

        // place order on button click
        binding.btnChooseLocation.setOnClickListener(v -> {
            if(distance == 0) {
                Toast.makeText(this, "No delivery location selected", Toast.LENGTH_SHORT).show();
                return;
            }

            cartViewModel.updateDeliveryFees(receivedProductId, DELIVERY_FEE*distance);
        });

        // load cart items
        cartViewModel.fetchCartItems();
        // load product details
        productDetailsViewModel.fetchProduct(receivedProductId);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        LocationManagerHelper locationManagerHelper = new LocationManagerHelper(AddLocation.this, location -> {

        });

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationManagerHelper.requestLocationPermission();
            return;
        }
        // check if location has been started and prompt user to turn it on
        if(!locationManagerHelper.isLocationEnabled()){
            locationManagerHelper.showLocationSettingsDialog();
            return;
        }

        map.setMyLocationEnabled(true);
        // farmer marker position
        farmerMarker = map.addMarker(new MarkerOptions().position(INITIAL_POSITION).title("Farmer"));
        farmerMarker.showInfoWindow();

        // create custom marker for the draggable marker
        BitmapDescriptor customMarker = BitmapDescriptorFactory.fromResource(R.drawable.location_marker);

        // set the anchor position of the custom marker
        float anchorX = 0.25f; // left side of the image horizontally
        float anchorY = 1.0f; // bottom of the image vertically
        // place marker on initial position
        draggableMarker = map.addMarker(new MarkerOptions().position(INITIAL_POSITION).icon(customMarker).anchor(anchorX, anchorY).draggable(true));
        // move camera to initial position and set the zoom level
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(INITIAL_POSITION, 15.0f));

        // set this as initial position of customer order
        placeOrderViewModel.setCustomerLocation(INITIAL_POSITION);

        // set the map to listen to marker drag
        map.setOnMarkerDragListener(this);
        // set the map to listen to map click
        map.setOnMapClickListener(this);
        // set on click listener for current location
        map.setOnMyLocationButtonClickListener(this);
    }

    @Override
    public void onMarkerDrag(@NonNull Marker marker) {
        LatLng newPosition = marker.getPosition();
        orderLat = newPosition.latitude;
        orderLng = newPosition.longitude;
        placeOrderViewModel.setCustomerLocation(newPosition);
        // update the distance between farmer and customer
        distance = calculateDistance(farmerPosition, newPosition);
        updateDistanceAndPrice();
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
        animateToView();
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        // update the distance between farmer and customer
        distance = calculateDistance(farmerPosition, latLng);
        updateDistanceAndPrice();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        LocationManagerHelper locationHelper = new LocationManagerHelper(AddLocation.this, currentLocation -> {
            orderLat = currentLocation.getLatitude();
            orderLng = currentLocation.getLongitude();

            LatLng position = new LatLng(orderLat, orderLng);
            draggableMarker.setPosition(position);
            animateToView();
            map.animateCamera(CameraUpdateFactory.newLatLng(position));
            // update the distance between farmer and customer
            distance = calculateDistance(farmerPosition, position);
            updateDistanceAndPrice();
        });

        locationHelper.requestSingleLocationUpdate();
        return true;
    }
    // animate camera to the midpoint of the two points
    private void animateToView(){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(farmerPosition);
        builder.include(new LatLng(orderLat, orderLng));
        LatLngBounds bounds = builder.build();

        int padding = 100; // adjust as needed
        if(map == null) return;
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
    }

    // calculate the distance between the farmer and customer
    private float calculateDistance(LatLng origin, LatLng dest){
        float[] results = new float[1];
        Location.distanceBetween(origin.latitude, origin.longitude, dest.latitude, dest.longitude, results);
        float distanceInKm = results[0] / 1000;
        float roundedDistance = Math.round(distanceInKm * 100.0) / 100.0f;
        return roundedDistance;
    }

    // update distance and price to the UI
    private void updateDistanceAndPrice(){
        binding.deliveryFees.setText(DELIVERY_FEE+"/km x "+distance+"km = Ksh. "+(DELIVERY_FEE*distance));
    }
}
