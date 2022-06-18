package com.android.iparking.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.android.iparking.R;
import com.android.iparking.dtos.BookingDTO;
import com.android.iparking.dtos.OperationFormDTO;
import com.android.iparking.dtos.VehicleDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookSpotActivity extends OperationActivity {
    
    private ActivityResultLauncher<Intent> activityResultLauncher;

    public static final int SPOT_BOOKED = 51;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_spot);
        this.getCurrentDate(R.id.tvBookingDate);
       ((TextView) findViewById(R.id.tvBookingParking)).setText(this.parking.getName());
        this.findUserVehicles();
        this.registerActivityResultLauncher();
    }

    @Override
    protected void setUpSpinner(VehicleDTO[][] body) {
        this.setUpSpinner(body, R.id.spinnerBookingVehicle);
    }

    private void registerActivityResultLauncher() {
        this.activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    switch (result.getResultCode()) {
                        case PayActivity.PAYMENT_COMPLETED:
                            SnackbarGenerator.snackbar(findViewById(android.R.id.content),
                                    getString(R.string.pay_completed));
                            this.bookSpot();
                            break;
                        case PayActivity.PAYMENT_FAILED:
                            SnackbarGenerator.snackbar(findViewById(android.R.id.content),
                                    getString(R.string.pay_failed));
                            break;
                        case RegisterVehicleActivity.VEHICLE_REGISTERED:
                            SnackbarGenerator.snackbar(findViewById(android.R.id.content),
                                    getString(R.string.registered_vehicle));
                            this.adapter.clear();
                            ((Spinner) findViewById(R.id.spinnerBookingVehicle)).setAdapter(null);
                            this.findUserVehicles();
                            break;
                        default:
                            SnackbarGenerator.snackbar(findViewById(android.R.id.content),
                                    getString(R.string.canceled));
                    }
                });
    }

    private void bookSpot() {
        OperationFormDTO operationFormDto = this.getOperationForm();
        Call<BookingDTO> callAsync = this.apiService.bookSpot(operationFormDto);
        callAsync.enqueue(new Callback<BookingDTO>() {
            @Override
            public void onResponse(Call<BookingDTO> call, Response<BookingDTO> response) {
                if (response.isSuccessful()) {
                    setResult(SPOT_BOOKED, new Intent());
                    finish();
                } else {
                    processUnsuccesfulResponse(response.code());
                }
            }

            @Override
            public void onFailure(Call<BookingDTO> call, Throwable t) {
                SnackbarGenerator.snackbar(findViewById(android.R.id.content),
                        getString(R.string.connection_failure));
            }
        });
    }

    private void processUnsuccesfulResponse(int code) {
        switch (code) {
            case 403:
                SnackbarGenerator.snackbar(findViewById(android.R.id.content),
                        getString(R.string.existing_booking));
                break;
            case 404:
                SnackbarGenerator.snackbar(findViewById(android.R.id.content),
                        getString(R.string.wrong_data_provided));
                break;
            case 412:
                SnackbarGenerator.snackbar(findViewById(android.R.id.content),
                        getString(R.string.no_empty_spots));
                break;
            default:
                SnackbarGenerator.snackbar(findViewById(android.R.id.content),
                        getString(R.string.connection_failure));
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
            Intent intent = new Intent(BookSpotActivity.this, PayActivity.class);
            intent.putExtra("price", this.parking.getBookingFare());
            this.activityResultLauncher.launch(intent);
        } else {
            SnackbarGenerator.snackbar(findViewById(android.R.id.content),
                    getString(R.string.choose_vehicle));
        }
    }

    public void registerVehicle(View view) {
        Intent intent = new Intent(BookSpotActivity.this, RegisterVehicleActivity.class);
        intent.putExtra("user", this.user);
        this.activityResultLauncher.launch(intent);
    }
}