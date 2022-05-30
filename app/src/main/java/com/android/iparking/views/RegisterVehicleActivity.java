package com.android.iparking.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.android.iparking.R;
import com.android.iparking.connectivity.APIService;
import com.android.iparking.connectivity.RetrofitFactory;
import com.android.iparking.dtos.RegisterVehicleFormDTO;
import com.android.iparking.dtos.VehicleDTO;
import com.android.iparking.models.User;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterVehicleActivity extends AppCompatActivity {

    private APIService apiService;
    private User user;

    public static final int VEHICLE_REGISTERED = 49;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_vehicle);
        this.apiService = RetrofitFactory.setUpRetrofit();
        this.user = (User) getIntent().getSerializableExtra("user");
    }

    public void register(View view) {
        if (this.isFormFilled()) {
            RegisterVehicleFormDTO registerVehicleFormDTO = this.getVehicleData();
            this.register(registerVehicleFormDTO);
        }
    }

    private boolean isFormFilled() {
        return (((EditText) findViewById(R.id.etNickname)).getText().toString().trim().length() != 0)
                && (((EditText) findViewById(R.id.etLicensePlate))
                .getText().toString().trim().length() != 0);
    }

    private RegisterVehicleFormDTO getVehicleData() {
        RegisterVehicleFormDTO registerVehicleFormDTO = new RegisterVehicleFormDTO();
        registerVehicleFormDTO.setEmail(this.user.getEmail());
        registerVehicleFormDTO.setLicensePlate(((EditText) findViewById(R.id.etLicensePlate))
                .getText().toString());
        registerVehicleFormDTO.setNickname(((EditText) findViewById(R.id.etNickname))
                .getText().toString());
        return registerVehicleFormDTO;
    }

    private void register(RegisterVehicleFormDTO registerVehicleFormDTO) {
        Call<VehicleDTO> call_async = this.apiService.registerVehicle(registerVehicleFormDTO);
        call_async.enqueue(new Callback<VehicleDTO>() {
            @Override
            public void onResponse(Call<VehicleDTO> call, Response<VehicleDTO> response) {
                if (response.isSuccessful()) {
                    setResult(VEHICLE_REGISTERED);
                    finish();
                } else {
                    processUnsuccesfulResponse(response.code());
                }
            }

            @Override
            public void onFailure(Call<VehicleDTO> call, Throwable t) {
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
        if (code == 400) {
            Snackbar.make(
                    findViewById(android.R.id.content),
                    getString(R.string.register_failed),
                    Snackbar.LENGTH_LONG
            ).show();
        }
    }
}