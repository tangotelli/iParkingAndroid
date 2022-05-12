package com.android.iparking.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.android.iparking.models.Parking;
import com.android.iparking.models.User;
import com.android.iparking.models.Vehicle;
import com.android.iparking.pojo.VehiclePojo;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookSpotActivity extends AppCompatActivity {
    
    private User user;
    private APIService apiService;
    private String selectedVehicle;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_spot);
        this.user = (User) getIntent().getSerializableExtra("user");
        this.apiService = RetrofitFactory.setUpRetrofit();
        this.findUserVehicles();
    }

    private void findUserVehicles() {
        Call<VehiclePojo[][]> call_async = apiService.findAllVehiclesByUser(this.user.getEmail());
        call_async.enqueue(new Callback<VehiclePojo[][]>() {
            @Override
            public void onResponse(Call<VehiclePojo[][]> call, Response<VehiclePojo[][]> response) {
                if (response.isSuccessful()) {
                    setUpSpinner(response.body());
                }
            }

            @Override
            public void onFailure(Call<VehiclePojo[][]> call, Throwable t) {
                Snackbar.make(
                        findViewById(android.R.id.content),
                        //getString(R.string.connection_failure),
                        "ERROR " + t.getMessage(),
                        Snackbar.LENGTH_LONG
                ).show();
            }
        });
    }

    private void setUpSpinner(VehiclePojo[][] body) {
        Spinner spinner = (Spinner) findViewById(R.id.spinnerVehicle);
        List<String> vehicles = Arrays.stream(body)
                .flatMap(Stream::of)
                .map(VehiclePojo::getNickname)
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
}