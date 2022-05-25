package com.android.iparking.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.android.iparking.dtos.BookingDTO;
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
    private ArrayAdapter<String> adapter;

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
        Call<VehicleDTO[][]> call_async = this.apiService.findAllVehiclesByUser(this.user.getEmail());
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
        this.adapter = this.createAdapter(vehicles);
        spinner.setAdapter(this.adapter);
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
                    switch (result.getResultCode()) {
                        case PayActivity.PAYMENT_COMPLETED:
                            Snackbar.make(
                                    findViewById(android.R.id.content),
                                    getString(R.string.pay_completed),
                                    Snackbar.LENGTH_LONG
                            ).show();
                            this.bookSpot();
                            break;
                        case PayActivity.PAYMENT_FAILED:
                            Snackbar.make(
                                    findViewById(android.R.id.content),
                                    getString(R.string.pay_failed),
                                    Snackbar.LENGTH_LONG
                            ).show();
                            break;
                        case RegisterVehicleActivity.VEHICLE_REGISTERED:
                            Snackbar.make(
                                    findViewById(android.R.id.content),
                                    getString(R.string.registered_vehicle),
                                    Snackbar.LENGTH_LONG
                            ).show();
                            this.adapter.clear();
                            ((Spinner) findViewById(R.id.spinnerVehicle)).setAdapter(null);
                            this.findUserVehicles();
                            break;
                        default:
                            Snackbar.make(
                                    findViewById(android.R.id.content),
                                    getString(R.string.canceled),
                                    Snackbar.LENGTH_LONG
                            ).show();
                    }
                });
    }

    private void bookSpot() {
        BookingFormDTO bookingFormDto = this.getBookingForm();
        Call<BookingDTO> call_async = this.apiService.bookSpot(bookingFormDto);
        call_async.enqueue(new Callback<BookingDTO>() {
            @Override
            public void onResponse(Call<BookingDTO> call, Response<BookingDTO> response) {
                if (response.isSuccessful()) {
                    Snackbar.make(
                            findViewById(android.R.id.content),
                            getString(R.string.booking_complete),
                            Snackbar.LENGTH_LONG
                    ).show();
                } else {
                    processUnsuccesfulResponse(response.code());
                }
            }

            @Override
            public void onFailure(Call<BookingDTO> call, Throwable t) {
                Snackbar.make(
                        findViewById(android.R.id.content),
                        //getString(R.string.connection_failure),
                        "ERROR " + t.getMessage(),
                        Snackbar.LENGTH_LONG
                ).show();
            }
        });
    }

    private void processUnsuccesfulResponse(int code) {
        switch (code) {
            case 403:
                Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.existing_booking),
                        Snackbar.LENGTH_LONG
                ).show();
                break;
            case 404:
                Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.wrong_data_provided),
                        Snackbar.LENGTH_LONG
                ).show();
                break;
            case 412:
                Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.no_empty_spots),
                        Snackbar.LENGTH_LONG
                ).show();
                break;
        }
    }

    private BookingFormDTO getBookingForm() {
        BookingFormDTO bookingFormDto = new BookingFormDTO();
        bookingFormDto.setEmail(this.user.getEmail());
        bookingFormDto.setParkingId(this.parking.getId());
        bookingFormDto.setVehicle(this.selectedVehicle);
        return bookingFormDto;
    }

    public void confirm(View view) {
        if (this.selectedVehicle != null) {
            this.activityResultLauncher
                    .launch(new Intent(BookSpotActivity.this, PayActivity.class));
        } else {
            Snackbar.make(
                    findViewById(android.R.id.content),
                    getString(R.string.choose_vehicle),
                    Snackbar.LENGTH_LONG
            ).show();
        }
    }

    public void registerVehicle(View view) {
        Intent intent = new Intent(BookSpotActivity.this, RegisterVehicleActivity.class);
        intent.putExtra("user", this.user);
        this.activityResultLauncher.launch(intent);
    }
}