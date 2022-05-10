package com.android.iparking.connectivity;

import com.android.iparking.pojo.ParkingPojo;
import com.android.iparking.pojo.UserPojo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIService {

    @GET("/user/login")
    Call<UserPojo> login(@Query("email") String email, @Query("password") String password);

    @GET("/parking/all")
    Call<ParkingPojo[][]> findAll();
}
