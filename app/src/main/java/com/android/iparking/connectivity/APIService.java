package com.android.iparking.connectivity;

import com.android.iparking.dtos.BookingDTO;
import com.android.iparking.dtos.OccupationDTO;
import com.android.iparking.dtos.OperationFormDTO;
import com.android.iparking.dtos.CardDTO;
import com.android.iparking.dtos.RegisterVehicleFormDTO;
import com.android.iparking.dtos.StayDTO;
import com.android.iparking.models.User;
import com.android.iparking.dtos.ParkingDTO;
import com.android.iparking.dtos.UserDTO;
import com.android.iparking.dtos.VehicleDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {

    @GET("/user/login")
    Call<UserDTO> login(@Query("email") String email, @Query("password") String password);

    @GET("/parking/all")
    Call<ParkingDTO[][]> findAll();

    @GET("/parking/occupation/{id}")
    Call<OccupationDTO> getLevelOfOccupation(@Path("id") String parkingId);

    @GET("/vehicle/get/{email}")
    Call<VehicleDTO[][]> findAllVehiclesByUser(@Path("email") String email);

    @POST("/user/signin")
    Call<UserDTO> signin(@Body User user);

    @POST("/payment/pay")
    Call<Void> pay(@Body CardDTO cardDTO);

    @POST("/booking/new")
    Call<BookingDTO> bookSpot(@Body OperationFormDTO operationFormDTO);

    @POST("/stay/new")
    Call<StayDTO> beginStay(@Body OperationFormDTO operationFormDTO);

    @POST("/vehicle/register")
    Call<VehicleDTO> registerVehicle(@Body RegisterVehicleFormDTO registerVehicleFormDTO);

    @PUT("/stay/end/{id}")
    Call<StayDTO> endStay(@Path("id") String id);

    @PUT("/stay/resume/{id}")
    Call<StayDTO> resumeStay(@Path("id") String id);

    @DELETE("/vehicle/delete/{id}")
    Call<Void> deleteVehicle(@Path("id") String id);
}
