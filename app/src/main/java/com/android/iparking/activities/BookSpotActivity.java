package com.android.iparking.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.iparking.R;
import com.android.iparking.connectivity.APIService;
import com.android.iparking.connectivity.RetrofitFactory;
import com.android.iparking.dtos.BookingFormDTO;
import com.android.iparking.models.Parking;
import com.android.iparking.models.User;
import com.android.iparking.dtos.VehicleDTO;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookSpotActivity extends AppCompatActivity {
    
    private User user;
    private Parking parking;
    private APIService apiService;
    private String selectedVehicle;
    private ActivityResultLauncher<Intent> activityResultLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_spot);
        ((TextView) findViewById(R.id.tvDate)).setText(this.getCurrentDate());
        this.user = (User) getIntent().getSerializableExtra("user");
        this.parking = (Parking) getIntent().getSerializableExtra(("parking"));
        ((TextView) findViewById(R.id.tvParking)).setText(this.parking.getName());
        this.apiService = RetrofitFactory.setUpRetrofit();
        this.findUserVehicles();
        this.registerActivityResultLauncher();
    }

    private String getCurrentDate() {
        DateFormat dateFormat =
                new SimpleDateFormat("dd/MM/yyyy - hh:mm", new Locale("es", "ES"));
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    private void findUserVehicles() {
        Call<VehicleDTO[][]> call_async = apiService.findAllVehiclesByUser(this.user.getEmail());
        call_async.enqueue(new Callback<VehicleDTO[][]>() {
            @Override
            public void onResponse(Call<VehicleDTO[][]> call, Response<VehicleDTO[][]> response) {
                if (response.isSuccessful()) {
                    setUpSpinner(response.body());
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

    private void setUpSpinner(VehicleDTO[][] body) {
        Spinner spinner = (Spinner) findViewById(R.id.spinnerVehicle);
        List<String> vehicles = Arrays.stream(body)
                .flatMap(Stream::of)
                .map(VehicleDTO::getNickname)
                .collect(Collectors.toList());
        vehicles.add(0, getString(R.string.choose_vehicle));
        ArrayAdapter<String> adapter = this.createAdapter(vehicles);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedVehicle = (String) parent.getItemAtPosition(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //empty for framework
            }
        });
    }

    @NonNull
    private ArrayAdapter<String> createAdapter(List<String> vehicles) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, vehicles) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                return spinnerHintColor(position, view);
            }
        };
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        return adapter;
    }

    private View spinnerHintColor(int position, View view) {
        if (position == 0) {
            ((TextView) view).setTextColor(Color.GRAY);
        } else {
            ((TextView) view).setTextColor(Color.BLACK);
        }
        return view;
    }

    private void registerActivityResultLauncher() {
        this.activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == PayActivity.PAYMENT_COMPLETED) {
                        //TO-DO make booking via controller
                        /*BookingFormDTO bookingForm = new BookingFormDTO();
                        bookingForm.setEmail(this.user.getEmail());
                        bookingForm.setParkingId(this.parking.getId());
                        bookingForm.setVehicle(this.selectedVehicle);*/
                        Snackbar.make(
                                findViewById(android.R.id.content),
                                "Pago completado",
                                Snackbar.LENGTH_LONG
                        ).show();
                    } else if (result.getResultCode() == PayActivity.PAYMENT_FAILED) {
                        Snackbar.make(
                                findViewById(android.R.id.content),
                                "Pago fallido",
                                Snackbar.LENGTH_LONG
                        ).show();
                    } else {
                        Snackbar.make(
                                findViewById(android.R.id.content),
                                "Pago cancelado",
                                Snackbar.LENGTH_LONG
                        ).show();
                    }
                });
    }

    public void confirm(View view) {
        if (this.selectedVehicle != null) {
            this.activityResultLauncher
                    .launch(new Intent(BookSpotActivity.this, PayActivity.class));
        } else {
            Snackbar.make(
                    findViewById(android.R.id.content),
                    "Seleccione un vehículo",
                    Snackbar.LENGTH_LONG
            ).show();
        }
    }
}