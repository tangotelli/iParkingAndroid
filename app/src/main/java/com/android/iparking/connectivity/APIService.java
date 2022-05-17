package com.android.iparking.connectivity;

import com.android.iparking.models.User;
import com.android.iparking.dtos.ParkingDTO;
import com.android.iparking.dtos.UserDTO;
import com.android.iparking.dtos.VehicleDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {

    @GET("/user/login")
    Call<UserDTO> login(@Query("email") String email, @Query("password") String password);

    @GET("/parking/all")
    Call<ParkingDTO[][]> findAll();

    @POST("/user/signin")
    Call<UserDTO> signin(@Body User user);

    @GET("/vehicle/get/{email}")
    Call<VehicleDTO[][]> findAllVehiclesByUser(@Path("email") String email);
}
