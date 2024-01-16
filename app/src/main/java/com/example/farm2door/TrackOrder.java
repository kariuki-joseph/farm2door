package com.example.farm2door;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.farm2door.databinding.ActivityTrackOrderBinding;
import com.example.farm2door.helpers.AuthHelper;
import com.example.farm2door.helpers.ToolBarHelper;
import com.example.farm2door.viewmodel.OrdersViewModel;
import com.example.farm2door.viewmodel.TrackOrderViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TrackOrder extends AppCompatActivity implements OnMapReadyCallback {
    ActivityTrackOrderBinding binding;
    TrackOrderViewModel trackOrderViewModel;
    GoogleMap map;;

    DatabaseReference farmerLocationRef;
    LatLng INITIAL_FARMER_POSITION = new LatLng(-0.391396,  36.945992);
    LatLng INITIAL_CUSTOMER_POSITION = new LatLng(-0.391396,  36.934992);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrackOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        trackOrderViewModel = new ViewModelProvider(this).get(TrackOrderViewModel.class);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // get order id passed via intent
        String orderId = getIntent().getStringExtra("orderId");

        // switch layouts on click
        binding.alreadyDelivered.setOnClickListener(v -> {
            switchLayouts();
        });

        // back button
        binding.btnBack.setOnClickListener(v -> {
            finish();
        });

        // observe order item
        trackOrderViewModel.getOrderItem().observe(this, orderItem -> {
            if (orderItem != null) {

                farmerLocationRef = FirebaseDatabase.getInstance().getReference().child("farmers").child(orderItem.getFarmerId()).child("location");
                farmerLocationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            double lat = snapshot.child("latitude").getValue(Double.class);
                            double lng = snapshot.child("longitude").getValue(Double.class);
                            LatLng farmerPosition = new LatLng(lat, lng);
                            // update the position of the farmer
                            map.addMarker(new MarkerOptions().position(farmerPosition).icon(BitmapDescriptorFactory.fromResource(R.drawable.farmer_marker)).draggable(false)).setTitle("Farmer");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                // set the coordinates of the map
                LatLng orderLocation = new LatLng(orderItem.getLatitude(), orderItem.getLongitude());
                // set the location of the customer making the order
                if(AuthHelper.getInstance(this).isUserFarmer()){
                    trackOrderViewModel.fetchCustomerInfo(orderItem.getCustomerId());
                }else{
                    // get farmer info
                    trackOrderViewModel.fetchFarmerInfo(orderItem.getFarmerId());
                }

                binding.orderNumber.setText("Order Number: "+orderItem.getOrderNumber());
                // load farmer information
                //
            }
        });

        // observe farmer
        trackOrderViewModel.getFarmer().observe(this, farmer -> {
            if (farmer != null) {
              binding.tvFarmerName.setText(farmer.getFullName());
              // set clicking the phone icon to call the farmer
                binding.btnCallFarmer.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(android.net.Uri.parse("tel:"+farmer.getPhoneNumber()));
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
                    intent.setData(android.net.Uri.parse("tel:"+customer.getPhoneNumber()));
                    startActivity(intent);
                });
            }
        });

        // get order
        if(!orderId.isEmpty()){
            trackOrderViewModel.getOrder(orderId);
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
        BitmapDescriptor farmerMarker = BitmapDescriptorFactory.fromResource(R.drawable.location_marker);
        // Default marker
        BitmapDescriptor defaultMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);

        // place marker on initial position
        map.addMarker(new MarkerOptions().position(INITIAL_FARMER_POSITION).icon(farmerMarker).draggable(false).title("Farmer"));
        map.addMarker(new MarkerOptions().position(INITIAL_CUSTOMER_POSITION).icon(defaultMarker).draggable(false).title("Me"));

        // move camera to initial position and set the zoom level
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(INITIAL_FARMER_POSITION, 12.0f));
    }
}