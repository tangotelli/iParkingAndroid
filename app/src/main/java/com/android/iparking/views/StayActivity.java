package com.android.iparking.views;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.iparking.R;
import com.android.iparking.connectivity.APIService;
import com.android.iparking.connectivity.RetrofitFactory;
import com.android.iparking.dtos.StayDTO;
import com.android.iparking.models.Stay;
import com.android.iparking.models.User;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StayActivity extends AppCompatActivity {

    private Stay activeStay;
    private User user;
    private double stayFare;
    private APIService apiService;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stay);
        this.activeStay = (Stay) getIntent().getSerializableExtra("stay");
        this.user = (User) getIntent().getSerializableExtra("user");
        this.stayFare = getIntent().getDoubleExtra("stayFare", 0.0);
        this.showInformation();
        this.apiService = RetrofitFactory.setUpRetrofit();
        this.registerActivityResultLauncher();
    }

    private void showInformation() {
        ((TextView) findViewById(R.id.tvParking)).setText(this.activeStay.getParking());
        ((TextView) findViewById(R.id.tvStayFare))
                .setText(String.format(getString(R.string.fare_long), this.stayFare + ""));
        ((TextView) findViewById(R.id.tvStartDate)).setText(this.activeStay.getBeginning());
        ((TextView) findViewById(R.id.tvVehicle)).setText(this.activeStay.getVehicle());
    }

    private void registerActivityResultLauncher() {
        this.activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    switch (result.getResultCode()) {
                        case PayActivity.PAYMENT_COMPLETED:
                            this.stayEndedOperations();
                            break;
                        case PayActivity.PAYMENT_FAILED:
                            SnackbarGenerator.snackbar(findViewById(android.R.id.content),
                                    getString(R.string.pay_failed));
                            this.resumeStay();
                            break;
                        default:
                            SnackbarGenerator.snackbar(findViewById(android.R.id.content),
                                    getString(R.string.canceled));
                            this.resumeStay();
                    }
                });
    }

    private void stayEndedOperations() {
        SnackbarGenerator.snackbar(findViewById(android.R.id.content),
                getString(R.string.stay_ended));
        findViewById(R.id.buttonEndStay).setEnabled(false);
        findViewById(R.id.buttonShowMap).setEnabled(true);
    }

    private void resumeStay() {
        Call<StayDTO> callAsync = this.apiService.resumeStay(this.activeStay.getId());
        callAsync.enqueue(new Callback<StayDTO>() {
            @Override
            public void onResponse(Call<StayDTO> call, Response<StayDTO> response) {
                if (!response.isSuccessful()) {
                    processUnsuccesfulResponse(response.code());
                }
            }

            @Override
            public void onFailure(Call<StayDTO> call, Throwable t) {
                SnackbarGenerator.snackbar(findViewById(android.R.id.content),
                        getString(R.string.connection_failure));
            }
        });
    }

    public void endStay(View view) {
        Call<StayDTO> callAsync = this.apiService.endStay(this.activeStay.getId());
        callAsync.enqueue(new Callback<StayDTO>() {
            @Override
            public void onResponse(Call<StayDTO> call, Response<StayDTO> response) {
                if (response.isSuccessful()) {
                    processSuccessfulResponse(response.body());
                } else {
                    processUnsuccesfulResponse(response.code());
                }
            }

            @Override
            public void onFailure(Call<StayDTO> call, Throwable t) {
                SnackbarGenerator.snackbar(findViewById(android.R.id.content),
                        getString(R.string.connection_failure));
            }
        });
    }

    private void processSuccessfulResponse(StayDTO stayDTO) {
        if (!stayDTO.getPrice().equals("null")) {
            Stay finishedStay = Stay.finishedStayFromDTO(stayDTO);
            Intent intent = new Intent(StayActivity.this, PayActivity.class);
            intent.putExtra("price", Double.valueOf(finishedStay.getPrice()));
            this.activityResultLauncher.launch(intent);
        } else {
            this.stayEndedOperations();
        }
    }

    private void processUnsuccesfulResponse(int code) {
        if (code == 404) {
            SnackbarGenerator.snackbar(findViewById(android.R.id.content),
                    getString(R.string.no_stay_found));
        }
    }

    public void showMap(View view) {
        Intent intent = new Intent(StayActivity.this, MapActivity.class);
        intent.putExtra("user", this.user);
        startActivity(intent);
        finish();
    }
}