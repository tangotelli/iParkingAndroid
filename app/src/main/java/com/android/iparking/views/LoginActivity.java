package com.android.iparking.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.android.iparking.R;
import com.android.iparking.connectivity.APIService;
import com.android.iparking.connectivity.RetrofitFactory;
import com.android.iparking.models.User;
import com.android.iparking.dtos.UserDTO;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    private APIService apiService;
    private User user;

    private static final int LOCATION_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.apiService = RetrofitFactory.setUpRetrofit();
    }

    public void login(View view) {
        if (this.isFormFilled()) {
            String email = ((EditText) findViewById(R.id.etEmail)).getText().toString();
            String password = ((EditText) findViewById(R.id.etPassword)).getText().toString();
            this.login(email, password);
        } else {
            SnackbarGenerator.snackbar(findViewById(android.R.id.content),
                    getString(R.string.missing_fields));
        }
    }

    private boolean isFormFilled() {
        return (((EditText) findViewById(R.id.etEmail)).getText().toString().trim().length() != 0)
                && (((EditText) findViewById(R.id.etPassword))
                .getText().toString().trim().length() != 0);
    }

    private void login(String email, String password) {
        Call<UserDTO> call_async = apiService.login(email, password);
        call_async.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.isSuccessful()) {
                    processSuccesfulResponse(response.body());
                } else {
                    processUnsuccesfulResponse(response.code());
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                SnackbarGenerator.snackbar(findViewById(android.R.id.content),
                        getString(R.string.connection_failure));
            }
        });
    }

    private void processSuccesfulResponse(UserDTO userDTO) {
        this.user = User.fromDTO(userDTO);
        if (ContextCompat
                .checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat
                    .requestPermissions(LoginActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION_CODE);
        } else {
            this.showMap();
        }
    }

    private void processUnsuccesfulResponse(int code) {
        if (code == 401) {
            SnackbarGenerator.snackbar(findViewById(android.R.id.content),
                    getString(R.string.auth_failure));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.showMap();
            } else {
                SnackbarGenerator.snackbar(findViewById(android.R.id.content),
                        getString(R.string.permission_denied));
                this.askForPermissions();
            }
        }
    }

    private void showMap() {
        this.createIntent(MapActivity.class);
    }

    private void askForPermissions() {
        this.createIntent(PermissionsActivity.class);
    }

    private void createIntent(Class<?> cls) {
        Intent intent = new Intent(LoginActivity.this, cls);
        intent.putExtra("user", this.user);
        startActivity(intent);
    }

    public void signin(View view) {
        startActivity(new Intent(LoginActivity.this, SigninActivity.class));
    }
}