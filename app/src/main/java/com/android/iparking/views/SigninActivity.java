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

public class SigninActivity extends AuthenticationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
    }

    public void signin(View view) {
        if (this.isFormFilled()) {
            this.signin();
        } else {
            SnackbarGenerator.snackbar(findViewById(android.R.id.content),
                    getString(R.string.missing_fields));
        }
    }

    private boolean isFormFilled() {
        return (((EditText) findViewById(R.id.etEmail)).getText().toString().trim().length() != 0)
                && (((EditText) findViewById(R.id.etName))
                .getText().toString().trim().length() != 0)
                && (((EditText) findViewById(R.id.etPassword))
                .getText().toString().trim().length() != 0)
                && (((EditText) findViewById(R.id.etRepeatPassword))
                .getText().toString().trim().length() != 0);
    }

    private void signin() {
        String email = ((EditText) findViewById(R.id.etEmail)).getText().toString();
        String name = ((EditText) findViewById(R.id.etName)).getText().toString();
        String password = ((EditText) findViewById(R.id.etPassword)).getText().toString();
        String repeatedPassword = ((EditText) findViewById(R.id.etRepeatPassword))
                .getText().toString();
        if (password.equals(repeatedPassword)) {
            User newUser = new User.UserBuilder()
                    .id("")
                    .email(email)
                    .name(name)
                    .password(password)
                    .build();
            this.signin(newUser);
        } else {
            SnackbarGenerator.snackbar(findViewById(android.R.id.content),
                    getString(R.string.passwords_do_not_match));
        }
    }

    private void signin(User user) {
        Call<UserDTO> callAsync = apiService.signin(user);
        this.authenticate(callAsync);
    }

    @Override
    protected void processSuccesfulResponse(UserDTO body) {
        this.processSuccesfulResponse(body, SigninActivity.this);
    }

    @Override
    protected void processUnsuccesfulResponse(int code) {
        if (code == 400) {
            SnackbarGenerator.snackbar(findViewById(android.R.id.content),
                    getString(R.string.signin_failure));
        }
    }

    @Override
    protected void showMap() {
        finish();
        this.createIntent(MapActivity.class);
    }

    @Override
    protected void askForPermissions() {
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