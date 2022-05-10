package com.android.iparking.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;

import com.android.iparking.R;
import com.android.iparking.connectivity.APIService;
import com.android.iparking.connectivity.RetrofitFactory;
import com.android.iparking.models.Parking;
import com.android.iparking.models.User;
import com.android.iparking.pojo.ParkingPojo;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private Location deviceLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private User user;
    private APIService apiService;
    private List<Parking> parkings;
    private List<Marker> markers;

    private static final float STREETS_ZOOM_LEVEL = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        this.getLocationUpdates();
        this.user = (User) getIntent().getSerializableExtra("user");
        this.apiService = RetrofitFactory.setUpRetrofit();
        this.markers = new ArrayList<>();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.map = googleMap;
        this.map.setOnMyLocationButtonClickListener(() -> {
            if (deviceLocation != null) {
                this.zoomToCurrentLocation();
                this.findClosestParkings();
                return true;
            } else {
                return false;
            }
        });
        if (ContextCompat
                .checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            this.map.setMyLocationEnabled(true);
        }
        if (deviceLocation != null) {
            zoomToCurrentLocation();
        }
    }

    private void findClosestParkings() {
        Call<ParkingPojo[][]> call_async = apiService.findAll();
        call_async.enqueue(new Callback<ParkingPojo[][]>() {
            @Override
            public void onResponse(Call<ParkingPojo[][]> call, Response<ParkingPojo[][]> response) {
                if (response.isSuccessful()) {
                    showClosestParkings(response.body());
                }
            }

            @Override
            public void onFailure(Call<ParkingPojo[][]> call, Throwable t) {
                Snackbar.make(
                        findViewById(android.R.id.content),
                        //getString(R.string.connection_failure),
                        "ERROR " + t.getMessage(),
                        Snackbar.LENGTH_LONG
                ).show();
            }
        });
    }

    private void showClosestParkings(ParkingPojo[][] body) {
        this.parkings = Arrays.stream(body)
                .flatMap(Stream::of)
                .map(Parking::fromPojo)
                .collect(Collectors.toList());
        if (!(this.parkings.isEmpty())) {
            for (Parking parking : parkings) {
                Marker marker = map.addMarker(new MarkerOptions()
                        .position(parking.getParkingLocation().toLatLng())
                        .title(parking.getName()));
                marker.setTag(parking);
                this.markers.add(marker);
            }
        }
    }

    private void zoomToCurrentLocation() {
        LatLng current = new LatLng(deviceLocation.getLatitude(), deviceLocation.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLng(current));
        map.moveCamera(CameraUpdateFactory.zoomTo(STREETS_ZOOM_LEVEL));
    }

    @SuppressLint("MissingPermission")
    private void getLocationUpdates() {
        this.fusedLocationClient = LocationServices
                .getFusedLocationProviderClient(this);
        this.locationRequest = LocationRequest.create();
        locationRequest.setInterval(60000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        this.locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        deviceLocation = location;
                    }
                }
            }
        };
        if (ContextCompat
                .checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            this.fusedLocationClient.requestLocationUpdates(this.locationRequest,
                    this.locationCallback, Looper.getMainLooper());
        }
    }
}