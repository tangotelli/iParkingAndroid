package com.android.iparking.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.iparking.R;

public class PayActivity extends AppCompatActivity {

    public static final int PAYMENT_COMPLETED = 47;
    public static final int PAYMENT_FAILED = 48;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
    }

    public void pay(View view) {
        //TO-DO send banking data to controller and retrieve ok or ko
        //setResult(PAYMENT_FAILED);
        finish();
    }
}