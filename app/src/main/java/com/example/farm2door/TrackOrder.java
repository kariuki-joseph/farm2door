package com.example.farm2door;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.farm2door.databinding.ActivityTrackOrderBinding;
import com.example.farm2door.helpers.AuthHelper;
import com.example.farm2door.viewmodel.LoadingViewModel;
import com.example.farm2door.viewmodel.TrackOrderViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class TrackOrder extends AppCompatActivity implements OnMapReadyCallback {
    ActivityTrackOrderBinding binding;
    TrackOrderViewModel trackOrderViewModel;
    LoadingViewModel loadingViewModel;
    GoogleMap map;
    ;
    Marker farmerMarker, customerMarker;
    DatabaseReference farmerLocationRef;
    LatLng farmerPosition = new LatLng(-0.391396, 36.945992);
    LatLng customerLocation = new LatLng(-0.391396, 36.934992);
    LocationManager locationManager;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrackOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        trackOrderViewModel = new ViewModelProvider(this).get(TrackOrderViewModel.class);
        loadingViewModel = LoadingViewModel.getInstance();
        // get the location of the farmer
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // get order id passed via intent
        String orderNumber = getIntent().getStringExtra("orderNumber");
        String farmerId = getIntent().getStringExtra("farmerId");

        // check if the user is a farmer
        if (AuthHelper.getInstance(this).isUserFarmer()) {
            requestLocationPermissions();
        }

        // get the location of the farmer
        farmerLocationRef = FirebaseDatabase.getInstance().getReference().child("farmers").child(farmerId).child("location");
        farmerLocationRef.child("latitude").setValue(farmerPosition.latitude);
        farmerLocationRef.child("longitude").setValue(farmerPosition.longitude);
        farmerLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double lat = snapshot.child("latitude").getValue(Double.class);
                    double lng = snapshot.child("longitude").getValue(Double.class);
                    farmerPosition = new LatLng(lat, lng);
                    // update the position of the farmer
                    updateMarker(farmerMarker, farmerPosition, true);
                    Toast.makeText(TrackOrder.this, "Location of the farmer changed! : " + lat + "," + lng, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // observe order delivered
        trackOrderViewModel.isOrderDelivered().observe(this, isDelivered -> {
            if (isDelivered) {
                switchLayouts();
            }
        });

        // switch layouts on click
        binding.alreadyDelivered.setOnClickListener(v -> {
            trackOrderViewModel.setOrderDelivered(orderNumber);
        });

        // back button
        binding.btnBack.setOnClickListener(v -> {
            finish();
        });

        // hide order details when order is loading
        loadingViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                binding.progressBarLayout.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.progressBarLayout.progressBar.setVisibility(View.GONE);
                binding.orderDetailsLayout.setVisibility(View.VISIBLE);
            }
        });

        // observe order item
        trackOrderViewModel.getOrderItem().observe(this, orderItem -> {
            if (orderItem == null) {
                Toast.makeText(this, "Value of order item was null", Toast.LENGTH_LONG).show();
                return;
            }
            // set the coordinates of the map
            LatLng orderLocation = new LatLng(orderItem.getLatitude(), orderItem.getLongitude());
            // set the location of the customer making the order
            if (AuthHelper.getInstance(this).isUserFarmer()) {
                trackOrderViewModel.fetchCustomerInfo(orderItem.getCustomerId());
            } else {
                // get farmer info
                trackOrderViewModel.fetchFarmerInfo(orderItem.getFarmerId());
            }

            binding.orderNumber.setText("Order Number: " + orderItem.getOrderNumber());

            // set the coordinates of the farmer on the map
            updateMarker(customerMarker, orderLocation, AuthHelper.getInstance(this).isUserFarmer()? false: true);
        });

        // observe farmer
        trackOrderViewModel.getFarmer().observe(this, farmer -> {
            if (farmer != null) {
                binding.tvFarmerName.setText(farmer.getFullName());
                // set clicking the phone icon to call the farmer
                binding.btnCallFarmer.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(android.net.Uri.parse("tel:" + farmer.getPhoneNumber()));
                    startActivity(intent);
                });
            }
        });

        // observe farmer live location

        // observe customer
        trackOrderViewModel.getCustomer().observe(this, customer -> {
            if (customer != null) {
                binding.tvFarmerName.setText(customer.getFullName());
                // set clicking the phone icon to call the customer
                binding.btnCallFarmer.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(android.net.Uri.parse("tel:" + customer.getPhoneNumber()));
                    startActivity(intent);
                });
            }
        });

        // get order
        if (!orderNumber.isEmpty()) {
            trackOrderViewModel.getOrder(orderNumber);
        }

    }


    private void switchLayouts() {
        binding.orderDetailsLayout.setVisibility(View.GONE);
        binding.orderDeliveredLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        // create a custom marker for the farmer
        BitmapDescriptor farmerIcon = BitmapDescriptorFactory.fromResource(R.drawable.location_marker);
        BitmapDescriptor defaultIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);

        customerMarker = map.addMarker(new MarkerOptions().position(farmerPosition).icon(farmerIcon).draggable(false).title("Me"));
        farmerMarker = map.addMarker(new MarkerOptions().position(customerLocation).icon(defaultIcon).draggable(false).title("Farmer"));

        customerMarker.showInfoWindow();
        farmerMarker.showInfoWindow();
        // move camera to initial position and set the zoom level
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(AuthHelper.getInstance(this).isUserFarmer() ? farmerPosition : customerLocation, 15.0f));
    }

    // update the position of a marker in google maps
    private void updateMarker(Marker marker, LatLng newLocation, boolean focus) {
        if (marker == null) {
            return;
        }

        marker.setPosition(newLocation);
        marker.showInfoWindow();
        if(focus) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 15.0f));
        }
    }

    // get live location of the farmer
    private void requestLocationPermissions() {
        // check if location permissions have been granted
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // permission granted. Start tracking location
            getInitialLocation();
            startLocationUpdates();
        }
    }

    // start location updates
    private void startLocationUpdates() {
        Toast.makeText(this, "Starting location updates", Toast.LENGTH_LONG).show();
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS is not enabled", Toast.LENGTH_SHORT).show();
            showEnableLocationDialog();
            return;
        }
        // request location updates within a minimum time interval of 2 seconds and a minimum distance of 10 meters
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this, "Location permissions not granted!", Toast.LENGTH_SHORT).show();
            return;
        }

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                2,
                locationListener
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start location updates
                startLocationUpdates();
            } else {
                // Permission denied, handle accordingly (e.g., show a message to the user)
                Log.e("Location", "Permission denied");
            }
        }
    }

    private void showEnableLocationDialog() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
        finish();
    }

    private void getInitialLocation() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showEnableLocationDialog();
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(lastKnownLocation == null){
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        double latitude = lastKnownLocation.getLatitude();
        double longitude = lastKnownLocation.getLongitude();
        if(farmerLocationRef != null ){
            farmerLocationRef.child("latitude").setValue(latitude);
            farmerLocationRef.child("longitude").setValue(longitude);
        }

        Toast.makeText(this, "Initial location: " + latitude + ", " + longitude, Toast.LENGTH_SHORT).show();
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            // update to real time database
            farmerLocationRef.child("latitude").setValue(latitude);
            farmerLocationRef.child("longitude").setValue(longitude);
            Toast.makeText(TrackOrder.this, "Location Changed: " + latitude + ", " + longitude, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFlushComplete(int requestCode) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
        }
    };
}
