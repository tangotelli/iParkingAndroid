package com.android.iparking.views;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.iparking.R;
import com.android.iparking.connectivity.APIService;
import com.android.iparking.connectivity.RetrofitFactory;
import com.android.iparking.dtos.UserDTO;
import com.android.iparking.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class AuthenticationActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    protected APIService apiService;
    protected User user;

    protected static final int LOCATION_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.apiService = RetrofitFactory.setUpRetrofit();
    }

    protected void authenticate(Call<UserDTO> callAsync) {
        callAsync.enqueue(new Callback<UserDTO>() {
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

    protected abstract void processSuccesfulResponse(UserDTO body);

    protected void processSuccesfulResponse(UserDTO body, Activity activity) {
        this.user = User.fromDTO(body);
        if (ContextCompat
                .checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat
                    .requestPermissions(activity,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION_CODE);
        } else {
            this.showMap();
        }
    }

    protected abstract void showMap();

    protected abstract void processUnsuccesfulResponse(int code);

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

    protected abstract void askForPermissions();

}
