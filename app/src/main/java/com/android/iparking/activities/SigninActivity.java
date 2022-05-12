package com.android.iparking.activities;

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
import com.android.iparking.pojo.UserPojo;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SigninActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    private User user;
    private APIService apiService;

    private static final int LOCATION_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        this.apiService = RetrofitFactory.setUpRetrofit();
    }

    public void signin(View view) {
        String email = ((EditText) findViewById(R.id.etEmail)).getText().toString();
        String name = ((EditText) findViewById(R.id.etName)).getText().toString();
        String password = ((EditText) findViewById(R.id.etPassword)).getText().toString();
        String repeatedPassword = ((EditText) findViewById(R.id.etRepeatPassword)).getText().toString();
        if (password.equals(repeatedPassword)) {
            User user = new User.UserBuilder()
                    .id("")
                    .email(email)
                    .name(name)
                    .password(password)
                    .build();
            this.signin(user);
        } else {
            Snackbar.make(
                    findViewById(android.R.id.content),
                    getString(R.string.passwords_do_not_match),
                    Snackbar.LENGTH_LONG
            ).show();
        }
    }

    private void signin(User user) {
        Call<UserPojo> call_async = apiService.signin(user);
        call_async.enqueue(new Callback<UserPojo>() {
            @Override
            public void onResponse(Call<UserPojo> call, Response<UserPojo> response) {
                if (response.isSuccessful()) {
                    completeSignin(response.body());
                } else {
                    incompleteSignin(response.code());
                }
            }

            @Override
            public void onFailure(Call<UserPojo> call, Throwable t) {
                Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.connection_failure),
                        Snackbar.LENGTH_LONG
                ).show();
            }
        });
    }

    private void completeSignin(UserPojo userPojo) {
        this.user = User.fromPojo(userPojo);
        if (ContextCompat
                .checkSelfPermission(SigninActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat
                    .requestPermissions(SigninActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION_CODE);
        } else {
            this.showMap();
        }
    }

    private void incompleteSignin(int code) {
        //TO-DO Revisar el status code para ver cuál pudo ser el error
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(R.string.signin_failure),
                Snackbar.LENGTH_LONG
        ).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.showMap();
            } else {
                Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.permission_denied),
                        Snackbar.LENGTH_LONG
                ).show();
                this.askForPermissions();
            }
        }
    }

    private void showMap() {
        finish();
        this.createIntent(MapActivity.class);
    }

    private void askForPermissions() {
        finish();
        this.createIntent(PermissionsActivity.class);
    }

    private void createIntent(Class<?> cls) {
        Intent intent = new Intent(SigninActivity.this, cls);
        intent.putExtra("user", this.user);
        startActivity(intent);
    }

    public void login(View view) {
        finish();
    }
}