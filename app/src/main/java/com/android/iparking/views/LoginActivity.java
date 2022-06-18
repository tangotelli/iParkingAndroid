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

public class LoginActivity extends AuthenticationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
        Call<UserDTO> callAsync = apiService.login(email, password);
        this.authenticate(callAsync);
    }

    @Override
    protected void processSuccesfulResponse(UserDTO body) {
        this.processSuccesfulResponse(body, LoginActivity.this);
    }

    @Override
    protected void processUnsuccesfulResponse(int code) {
        if (code == 401) {
            SnackbarGenerator.snackbar(findViewById(android.R.id.content),
                    getString(R.string.auth_failure));
        }
    }

    @Override
    protected void showMap() {
        this.createIntent(MapActivity.class);
    }

    @Override
    protected void askForPermissions() {
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