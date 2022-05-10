package com.android.iparking.connectivity;

import com.android.iparking.pojo.UserPojo;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;

public interface APIService {

    @FormUrlEncoded
    @GET("/user/login")
    Call<UserPojo> updateUser(@Field("email") String email, @Field("password") String password);

}
