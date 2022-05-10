package com.android.iparking.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;

import com.android.iparking.R;
import com.android.iparking.connectivity.APIService;
import com.android.iparking.connectivity.MyHttpClient;
import com.android.iparking.models.User;
import com.android.iparking.pojo.UserPojo;
import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private static final String API_BASE_URL = "https://192.168.1.141:8000";

    private APIService apiService;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.setUpRetrofit();
    }

    private void setUpRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(MyHttpClient.getUnsafeOkHttpClient())
                .build();
        apiService = retrofit.create(APIService.class);
    }

    public void login(View view) {
        String email = ((EditText) findViewById(R.id.etEmail)).getText().toString();
        String password = ((EditText) findViewById(R.id.etPassword)).getText().toString();
        this.login(email, password);
    }

    private void login(String email, String password) {
        Call<UserPojo> call_async = apiService.login(email, password);
        call_async.enqueue(new Callback<UserPojo>() {
            @Override
            public void onResponse(Call<UserPojo> call, Response<UserPojo> response) {
                if (response.isSuccessful()) {
                    completeLogin(response.body());
                } else {
                    incompleteLogin(response.code());
                }
            }

            @Override
            public void onFailure(Call<UserPojo> call, Throwable t) {
                //incompleteLogin();
                Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.connection_failure),
                        Snackbar.LENGTH_LONG
                ).show();
            }
        });
    }

    private void completeLogin(UserPojo userPojo) {
        this.user = User.fromPojo(userPojo);
        //lanzar actividad del mapa
        Snackbar.make(
                findViewById(android.R.id.content),
                "Todo bien",
                Snackbar.LENGTH_LONG
        ).show();
    }

    private void incompleteLogin(int code) {
        //TO-DO Revisar el status code para ver cuál pudo ser el error
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(R.string.auth_failure),
                Snackbar.LENGTH_LONG
        ).show();
    }

    public void signin(View view) {

    }
}