package com.android.iparking.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.iparking.R;
import com.android.iparking.connectivity.APIService;
import com.android.iparking.connectivity.RetrofitFactory;
import com.android.iparking.dtos.CardDTO;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayActivity extends AppCompatActivity {

    private APIService apiService;

    public static final int PAYMENT_COMPLETED = 47;
    public static final int PAYMENT_FAILED = 48;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        this.apiService = RetrofitFactory.setUpRetrofit();
        ((TextView) findViewById(R.id.tvPrice))
                .setText(String.format(getString(R.string.price),
                        getIntent().getDoubleExtra("price", 0.0) + ""));
    }

    public void pay(View view) {
        if (this.isFormFilled()) {
            CardDTO cardDTO = this.getCardData();
            this.pay(cardDTO);
        } else {
            Snackbar.make(
                    findViewById(android.R.id.content),
                    getString(R.string.missing_fields),
                    Snackbar.LENGTH_LONG
            ).show();
        }
    }

    private void pay(CardDTO cardDTO) {
        Call<Void> call_async = this.apiService.pay(cardDTO);
        call_async.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    setResult(PAYMENT_COMPLETED);
                } else {
                    setResult(PAYMENT_FAILED);
                }
                finish();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Snackbar.make(
                        findViewById(android.R.id.content),
                        //getString(R.string.connection_failure),
                        "ERROR " + t.getMessage(),
                        Snackbar.LENGTH_LONG
                ).show();
            }
        });
    }

    private boolean isFormFilled() {
        return (((EditText) findViewById(R.id.etCard)).getText().toString().trim().length() != 0)
                && (((EditText) findViewById(R.id.etExpiringMonth))
                    .getText().toString().trim().length() != 0)
                && (((EditText) findViewById(R.id.etExpiringYear))
                    .getText().toString().trim().length() != 0)
                && (((EditText) findViewById(R.id.etCVC)).getText().toString().trim().length() != 0);
    }

    private CardDTO getCardData() {
        CardDTO cardDTO = new CardDTO();
        cardDTO.setCardNumber(((EditText) findViewById(R.id.etCard)).getText().toString());
        cardDTO.setExpirationDate(String.format("%s/%s",
                ((EditText) findViewById(R.id.etExpiringMonth)).getText().toString(),
                ((EditText) findViewById(R.id.etExpiringYear)).getText().toString()));
        cardDTO.setCvc(((EditText) findViewById(R.id.etCVC)).getText().toString());
        return cardDTO;
    }
}