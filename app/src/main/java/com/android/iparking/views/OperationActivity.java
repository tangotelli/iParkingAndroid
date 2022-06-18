package com.android.iparking.views;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.iparking.R;
import com.android.iparking.connectivity.APIService;
import com.android.iparking.connectivity.RetrofitFactory;
import com.android.iparking.dtos.VehicleDTO;
import com.android.iparking.models.Parking;
import com.android.iparking.models.User;

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

public abstract class OperationActivity extends AppCompatActivity {

    protected User user;
    protected Parking parking;
    protected APIService apiService;
    protected String selectedVehicle;
    protected ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.user = (User) getIntent().getSerializableExtra("user");
        this.parking = (Parking) getIntent().getSerializableExtra(("parking"));
        this.apiService = RetrofitFactory.setUpRetrofit();
    }

    protected void getCurrentDate(int id) {
        DateFormat dateFormat =
                new SimpleDateFormat("dd/MM/yyyy - hh:mm", new Locale("es", "ES"));
        ((TextView) findViewById(id))
                .setText(dateFormat.format(Calendar.getInstance().getTime()));
    }

    protected void findUserVehicles() {
        Call<VehicleDTO[][]> callAsync = this.apiService.findAllVehiclesByUser(this.user.getEmail());
        callAsync.enqueue(new Callback<VehicleDTO[][]>() {
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

    protected abstract void setUpSpinner(VehicleDTO[][] body);

    protected void setUpSpinner(VehicleDTO[][] body, int id) {
        Spinner spinner = findViewById(id);
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
    protected ArrayAdapter<String> createAdapter(List<String> vehicles) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
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
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        return arrayAdapter;
    }

    protected View spinnerHintColor(int position, View view) {
        if (position == 0) {
            ((TextView) view).setTextColor(Color.GRAY);
        } else {
            ((TextView) view).setTextColor(Color.BLACK);
        }
        return view;
    }

}
