package com.android.iparking.connectivity;

import static com.android.iparking.BuildConfig.IPARKING_BASE_URL;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {

    private static final String API_BASE_URL = IPARKING_BASE_URL;

    private RetrofitFactory() {
        //empty for framework
    }

    public static APIService setUpRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://192.168.1.36:8000")
                .addConverterFactory(GsonConverterFactory.create())
                .client(MyHttpClient.getUnsafeOkHttpClient())
                .build();
        return retrofit.create(APIService.class);
    }
}
