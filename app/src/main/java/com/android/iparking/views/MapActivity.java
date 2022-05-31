package com.android.iparking.views;

import static com.android.iparking.BuildConfig.MAPS_API_KEY;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.android.iparking.R;
import com.android.iparking.connectivity.APIService;
import com.android.iparking.connectivity.RetrofitFactory;
import com.android.iparking.models.Parking;
import com.android.iparking.models.Stay;
import com.android.iparking.models.User;
import com.android.iparking.dtos.ParkingDTO;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private Location deviceLocation;
    private User user;
    private Parking selectedParking;
    private APIService apiService;
    private List<Parking> parkings;
    private List<Marker> markers;
    private BottomSheetBehavior bottomSheetBehavior;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private Polyline route;

    private static final float STREETS_ZOOM_LEVEL = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        this.setUpBottomSheet();
        this.getLocationUpdates();
        this.user = (User) getIntent().getSerializableExtra("user");
        this.apiService = RetrofitFactory.setUpRetrofit();
        this.markers = new ArrayList<>();
        this.registerActivityResultLauncher();
    }

    private void setUpBottomSheet() {
        LinearLayout bottomSheet = (LinearLayout) findViewById(R.id.bottomSheet);
        this.bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        this.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.map = googleMap;
        this.setUpMapListeners();
        if (ContextCompat
                .checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            this.map.setMyLocationEnabled(true);
        }
        if (deviceLocation != null) {
            zoomToCurrentLocation();
        }
    }

    private void setUpMapListeners() {
        this.map.setOnMyLocationButtonClickListener(() -> {
            if (deviceLocation != null) {
                if (route != null) {
                    route.remove();
                }
                this.zoomToCurrentLocation();
                this.findClosestParkings();
                return true;
            } else {
                return false;
            }
        });
        this.map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                if (marker.getTag() != null) {
                    if (route != null) {
                        route.remove();
                    }
                    showParkingDetail((Parking) marker.getTag());
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void showParkingDetail(Parking parking) {
        this.selectedParking = parking;
        ((TextView) findViewById(R.id.bottomSheetName)).setText(parking.getName());
        ((TextView) findViewById(R.id.bottomSheetAddress)).setText(parking.getAddress());
        ((TextView) findViewById(R.id.bottomSheetBookingFare))
                .setText(String.format(getString(R.string.booking_fare), "" + parking.getBookingFare()));
        ((TextView) findViewById(R.id.bottomSheetStayFare))
                .setText(String.format(getString(R.string.stay_fare), "" + parking.getStayFare()));
        this.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
    }

    private void findClosestParkings() {
        Call<ParkingDTO[][]> callAsync = apiService.findAll();
        callAsync.enqueue(new Callback<ParkingDTO[][]>() {
            @Override
            public void onResponse(Call<ParkingDTO[][]> call, Response<ParkingDTO[][]> response) {
                if (response.isSuccessful()) {
                    showClosestParkings(response.body());
                }
            }

            @Override
            public void onFailure(Call<ParkingDTO[][]> call, Throwable t) {
                SnackbarGenerator.snackbar(findViewById(android.R.id.content),
                        getString(R.string.connection_failure));
            }
        });
    }

    private void showClosestParkings(ParkingDTO[][] body) {
        this.parkings = Arrays.stream(body)
                .flatMap(Stream::of)
                .map(Parking::fromDTO)
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
        LatLng current = new LatLng(this.deviceLocation.getLatitude(),
                this.deviceLocation.getLongitude());
        this.map.moveCamera(CameraUpdateFactory.newLatLng(current));
        this.map.moveCamera(CameraUpdateFactory.zoomTo(STREETS_ZOOM_LEVEL));
    }

    @SuppressLint("MissingPermission")
    private void getLocationUpdates() {
        FusedLocationProviderClient fusedLocationClient = LocationServices
                .getFusedLocationProviderClient(this);
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(60000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationCallback locationCallback = new LocationCallback() {
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
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback, Looper.getMainLooper());
        }
    }

    public void bookSpot(View view) {
        this.createIntent(BookSpotActivity.class);
    }

    public void beginStay(View view) {
        this.createIntent(BeginStayActivity.class);
    }

    private void createIntent(Class<?> cls) {
        Intent intent = new Intent(MapActivity.this, cls);
        intent.putExtra("user", this.user);
        intent.putExtra("parking", this.selectedParking);
        this.activityResultLauncher.launch(intent);
    }

    private void registerActivityResultLauncher() {
        this.activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == BeginStayActivity.STAY_BEGUN) {
                        assert result.getData() != null;
                        this.showStay((Stay) result.getData().getSerializableExtra("stay"));
                    }
                });
    }

    private void showStay(Stay stay) {
        Intent intent = new Intent(MapActivity.this, StayActivity.class);
        intent.putExtra("user", this.user);
        intent.putExtra("stayFare", this.selectedParking.getStayFare());
        intent.putExtra("stay", stay);
        startActivity(intent);
        finish();
    }

    public void showProfile(View view) {
        this.createIntent(UserActivity.class);
    }

    public void showRoute(View view) {
        GoogleDirection.withServerKey(MAPS_API_KEY)
                .from(new LatLng(this.deviceLocation.getLatitude(),
                        this.deviceLocation.getLongitude()))
                .to(this.selectedParking.getParkingLocation().toLatLng())
                .transportMode(TransportMode.DRIVING)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction) {
                        if (direction.isOK()) {
                            drawRoute(direction);
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        SnackbarGenerator.snackbar(findViewById(android.R.id.content), t);
                    }
                });
    }

    private void drawRoute(Direction direction) {
        ArrayList<LatLng> directionPositionList =
                direction.getRouteList().get(0).getLegList().get(0)
                        .getDirectionPoint();
        this.route = this.map.addPolyline(DirectionConverter
                .createPolyline(this, directionPositionList, 5, Color.RED));
    }
}