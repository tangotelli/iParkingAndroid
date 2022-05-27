package com.android.iparking.connectivity;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {

    private static final String API_BASE_URL = "https://192.168.1.37:8000";

    public static APIService setUpRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(MyHttpClient.getUnsafeOkHttpClient())
                .build();
        return retrofit.create(APIService.class);
    }
}
