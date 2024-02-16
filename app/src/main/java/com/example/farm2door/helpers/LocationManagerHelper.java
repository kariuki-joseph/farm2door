package com.example.farm2door.helpers;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.farm2door.interfaces.MyLocationListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class LocationManagerHelper {
    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;
    private final LocationCallback locationCallback;
    private final LocationManager locationManager;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    public LocationManagerHelper(Context context, MyLocationListener listener) {
        this.context = context;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        Log.d("LocationChanged", "Location Changed!"+location.toString());
                        listener.onLocationChanged(location);
                    }
                }
            }
        };
    }

    // request location updates
    public void requestLocationUpdates() {
        if (!checkLocationPermission()) {
            Log.d("LocationPermission", "Location permission not granted. Requesting permission.");
            requestLocationPermission();
            return;
        }
        if (!isLocationEnabled()) {
            showLocationSettingsDialog();
            return;
        }

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000); // 1 second interval

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    // get single location update
    public void requestSingleLocationUpdate() {
        if (!checkLocationPermission()) {
            Log.d("LocationPermission", "Location permission not granted. Requesting permission.");
            requestLocationPermission();
            return;
        }
        if (!isLocationEnabled()) {
            showLocationSettingsDialog();
            return;
        }

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setNumUpdates(1); // Request a single location update

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    public void removeLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(
                (Activity) context,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE
        );
    }

    // check if location is turned on
    public boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void showLocationSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Location Services Disabled");
        builder.setMessage("Please enable location services to continue");
        builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }
}
