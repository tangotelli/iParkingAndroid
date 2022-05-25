package com.android.iparking.dtos;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class OperationFormDTO {

    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("vehicle")
    @Expose
    private String vehicle;
    @SerializedName("parkingId")
    @Expose
    private String parkingId;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getParkingId() {
        return parkingId;
    }

    public void setParkingId(String parkingId) {
        this.parkingId = parkingId;
    }

}
