package com.android.iparking.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.android.iparking.R;
import com.android.iparking.dtos.OperationFormDTO;
import com.android.iparking.dtos.StayDTO;
import com.android.iparking.dtos.VehicleDTO;
import com.android.iparking.models.Stay;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BeginStayActivity extends OperationActivity {

    private ActivityResultLauncher<Intent> activityResultLauncher;

    public static final int STAY_BEGUN = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin_stay);
        this.getCurrentDate(R.id.tvStayDate);
        ((TextView) findViewById(R.id.tvStayParking)).setText(this.parking.getName());
        this.findUserVehicles();
        this.registerActivityResultLauncher();
    }

    @Override
    protected void setUpSpinner(VehicleDTO[][] body) {
        this.setUpSpinner(body, R.id.spinnerStayVehicle);
    }

    private void registerActivityResultLauncher() {
        this.activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RegisterVehicleActivity.VEHICLE_REGISTERED) {
                        SnackbarGenerator.snackbar(findViewById(android.R.id.content),
                                getString(R.string.registered_vehicle));
                        this.adapter.clear();
                        ((Spinner) findViewById(R.id.spinnerBookingVehicle)).setAdapter(null);
                        this.findUserVehicles();
                    } else {
                        SnackbarGenerator.snackbar(findViewById(android.R.id.content),
                                getString(R.string.canceled));
                    }
                });
    }

    private void beginStay() {
        OperationFormDTO operationFormDto = this.getOperationForm();
        Call<StayDTO> callAsync = this.apiService.beginStay(operationFormDto);
        callAsync.enqueue(new Callback<StayDTO>() {
            @Override
            public void onResponse(Call<StayDTO> call, Response<StayDTO> response) {
                if (response.isSuccessful()) {
                    processSuccesfulResponse(response.body());
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

    private void processSuccesfulResponse(StayDTO stayDTO) {
        SnackbarGenerator.snackbar(findViewById(android.R.id.content),
                getString(R.string.stay_began));
        Intent intent = new Intent().putExtra("stay", Stay.activeStayFromDTO(stayDTO));
        setResult(STAY_BEGUN, intent);
        finish();
    }

    private void processUnsuccesfulResponse(int code) {
        if (code == 403) {
            SnackbarGenerator.snackbar(findViewById(android.R.id.content),
                    getString(R.string.existing_stay));
        } else if (code == 404) {
            SnackbarGenerator.snackbar(findViewById(android.R.id.content),
                    getString(R.string.wrong_data_provided));
        }
    }

    private OperationFormDTO getOperationForm() {
        OperationFormDTO operationFormDto = new OperationFormDTO();
        operationFormDto.setEmail(this.user.getEmail());
        operationFormDto.setParkingId(this.parking.getId());
        operationFormDto.setVehicle(this.selectedVehicle);
        return operationFormDto;
    }

    public void confirm(View view) {
        if (this.selectedVehicle != null) {
            this.beginStay();
        } else {
            SnackbarGenerator.snackbar(findViewById(android.R.id.content),
                    getString(R.string.choose_vehicle));
        }
    }

    public void registerVehicle(View view) {
        Intent intent = new Intent(BeginStayActivity.this, RegisterVehicleActivity.class);
        intent.putExtra("user", this.user);
        this.activityResultLauncher.launch(intent);
    }
}