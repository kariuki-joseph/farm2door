package com.example.farm2door;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.farm2door.databinding.ActivityAddLocationBinding;
import com.example.farm2door.helpers.ToolBarHelper;
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

public class AddLocation extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {
    ActivityAddLocationBinding binding;
    GoogleMap map;
    Polyline polyline;
    Marker draggableMarker;

    LatLng INITIAL_POSITION = new LatLng(-0.391396,  36.933992);
    double orderLat, orderLng; // store the location of the customer making the order
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ToolBarHelper.setupToolBar(this, binding.toolbar.toolbarLayout, "Add Location", true);

        binding.btnAddLocation.setOnClickListener(v -> {
            Intent intent = new Intent(AddLocation.this, OrderSuccess.class);
            startActivity(intent);
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        // create custom marker for the draggable marker
        BitmapDescriptor customMarker = BitmapDescriptorFactory.fromResource(R.drawable.location_marker);

        // place marker on initial position
        draggableMarker = map.addMarker(new MarkerOptions().position(INITIAL_POSITION).icon(customMarker).draggable(true));
        // move camera to initial position and set the zoom level
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(INITIAL_POSITION, 12.0f));

        // set the map to listen to marker drag
        map.setOnMarkerDragListener(this);
    }

    @Override
    public void onMarkerDrag(@NonNull Marker marker) {
        LatLng newPosition = marker.getPosition();
        orderLat = newPosition.latitude;
        orderLng = newPosition.longitude;
        binding.tvCoords.setText(String.valueOf(orderLat)+","+String.valueOf(orderLng));
    }

    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {

    }

    @Override
    public void onMarkerDragStart(@NonNull Marker marker) {

    }
}