package com.android.iparking.views;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.iparking.R;
import com.android.iparking.connectivity.APIService;
import com.android.iparking.connectivity.RetrofitFactory;
import com.android.iparking.dtos.VehicleDTO;
import com.android.iparking.models.User;
import com.android.iparking.models.Vehicle;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity {

    private User user;
    private APIService apiService;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        this.user = (User) getIntent().getSerializableExtra("user");
        this.apiService = RetrofitFactory.setUpRetrofit();
        this.showInformation();
        this.registerActivityResultLauncher();
    }

    private void showInformation() {
        ((TextView) findViewById(R.id.labelEmail)).setText(this.user.getEmail());
        ((TextView) findViewById(R.id.labelName)).setText(this.user.getName());
        this.findUserVehicles();
    }

    private void findUserVehicles() {
        Call<VehicleDTO[][]> call_async = this.apiService.findAllVehiclesByUser(this.user.getEmail());
        call_async.enqueue(new Callback<VehicleDTO[][]>() {
            @Override
            public void onResponse(Call<VehicleDTO[][]> call, Response<VehicleDTO[][]> response) {
                if (response.isSuccessful()) {
                    showUserVehicles(response.body());
                }
            }

            @Override
            public void onFailure(Call<VehicleDTO[][]> call, Throwable t) {
                Snackbar.make(
                        findViewById(android.R.id.content),
                        //getString(R.string.connection_failure),
                        "ERROR " + t.getMessage(),
                        Snackbar.LENGTH_LONG
                ).show();
            }
        });
    }

    private void showUserVehicles(VehicleDTO[][] body) {
        List<Vehicle> vehicles = Arrays.stream(body)
                .flatMap(Stream::of)
                .map(Vehicle::fromDTO)
                .collect(Collectors.toList());
        ((ListView) findViewById(R.id.listVehicles)).setAdapter(
                new VehicleListAdapter(vehicles, this.getResources(), this));
    }

    private void registerActivityResultLauncher() {
        this.activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RegisterVehicleActivity.VEHICLE_REGISTERED) {
                        Snackbar.make(
                                findViewById(android.R.id.content),
                                getString(R.string.registered_vehicle),
                                Snackbar.LENGTH_LONG
                        ).show();
                        this.findUserVehicles();
                    } else {
                        Snackbar.make(
                                findViewById(android.R.id.content),
                                getString(R.string.canceled),
                                Snackbar.LENGTH_LONG
                        ).show();
                    }
                });
    }

    public void registerVehicle(View view) {
        Intent intent = new Intent(UserActivity.this, RegisterVehicleActivity.class);
        intent.putExtra("user", this.user);
        this.activityResultLauncher.launch(intent);
    }
}