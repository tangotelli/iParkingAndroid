package com.android.iparking.dtos;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class BookingDTO {

    @SerializedName("Id")
    @Expose
    private String id;
    @SerializedName("Parking Id")
    @Expose
    private String parkingId;
    @SerializedName("Parking")
    @Expose
    private String parking;
    @SerializedName("Spot")
    @Expose
    private String spot;
    @SerializedName("Vehicle")
    @Expose
    private String vehicle;
    @SerializedName("User")
    @Expose
    private String user;
    @SerializedName("Price")
    @Expose
    private Double price;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParkingId() {
        return parkingId;
    }

    public void setParkingId(String parkingId) {
        this.parkingId = parkingId;
    }

    public String getParking() {
        return parking;
    }

    public void setParking(String parking) {
        this.parking = parking;
    }

    public String getSpot() {
        return spot;
    }

    public void setSpot(String spot) {
        this.spot = spot;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

}
