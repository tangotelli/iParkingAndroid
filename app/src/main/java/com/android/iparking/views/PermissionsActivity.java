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

import com.android.iparking.R;
import com.android.iparking.models.User;
import com.google.android.material.snackbar.Snackbar;

public class PermissionsActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    private User user;

    private static final int LOCATION_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);
        this.user = (User) getIntent().getSerializableExtra("user");
    }

    public void askForPermissions(View view) {
        if (ContextCompat
                .checkSelfPermission(PermissionsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat
                    .requestPermissions(PermissionsActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION_CODE);
        } else {
            this.showMap();
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
            }
        }
    }

    private void showMap() {
        Intent intent = new Intent(PermissionsActivity.this, MapActivity.class);
        intent.putExtra("user", this.user);
        startActivity(intent);
        finish();
    }
}