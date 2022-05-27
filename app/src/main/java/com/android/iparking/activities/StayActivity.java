package com.android.iparking.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.iparking.R;
import com.android.iparking.models.Stay;

public class StayActivity extends AppCompatActivity {

    private Stay stay;
    private double stayFare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stay);
        this.stay = (Stay) getIntent().getSerializableExtra("stay");
        this.stayFare = getIntent().getDoubleExtra("stayFare", 0.0);
        this.showInformation();
    }

    private void showInformation() {
        ((TextView) findViewById(R.id.tvParking)).setText(this.stay.getParking());
        ((TextView) findViewById(R.id.tvStayFare))
                .setText(String.format(getString(R.string.fare_long), this.stayFare + ""));
        ((TextView) findViewById(R.id.tvStartDate)).setText(this.stay.getBeginning());
        ((TextView) findViewById(R.id.tvVehicle)).setText(this.stay.getVehicle());
    }

    public void endStay(View view) {
    }
}