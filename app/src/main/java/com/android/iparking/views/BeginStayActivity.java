package com.android.iparking.views;

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
import com.android.iparking.dtos.OperationFormDTO;
import com.android.iparking.dtos.StayDTO;
import com.android.iparking.dtos.VehicleDTO;
import com.android.iparking.models.Parking;
import com.android.iparking.models.Stay;
import com.android.iparking.models.User;
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

public class BeginStayActivity extends AppCompatActivity {

    private User user;
    private Parking parking;
    private APIService apiService;
    private String selectedVehicle;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ArrayAdapter<String> adapter;

    public static final int STAY_BEGUN = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin_stay);
        ((TextView) findViewById(R.id.tvStayDate)).setText(this.getCurrentDate());
        this.user = (User) getIntent().getSerializableExtra("user");
        this.parking = (Parking) getIntent().getSerializableExtra(("parking"));
        ((TextView) findViewById(R.id.tvStayParking)).setText(this.parking.getName());
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
                SnackbarGenerator.snackbar(findViewById(android.R.id.content),
                        getString(R.string.connection_failure));
            }
        });
    }

    private void setUpSpinner(VehicleDTO[][] body) {
        Spinner spinner = findViewById(R.id.spinnerStayVehicle);
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
        Call<StayDTO> call_async = this.apiService.beginStay(operationFormDto);
        call_async.enqueue(new Callback<StayDTO>() {
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
        switch (code) {
            case 403:
                SnackbarGenerator.snackbar(findViewById(android.R.id.content),
                        getString(R.string.existing_stay));
                break;
            case 404:
                SnackbarGenerator.snackbar(findViewById(android.R.id.content),
                        getString(R.string.wrong_data_provided));
                break;
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