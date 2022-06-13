package com.android.iparking.dtos;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class StayDTO {

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
    @SerializedName("Beginning")
    @Expose
    private String beginning;
    @SerializedName("End")
    @Expose
    private String end;
    @SerializedName("Price")
    @Expose
    private String price;
    @SerializedName("Fare")
    @Expose
    private String fare;

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

    public String getBeginning() {
        return beginning;
    }

    public void setBeginning(String beginning) {
        this.beginning = beginning;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getFare() {
        return fare;
    }

    public void setFare(String fare) {
        this.fare = fare;
    }
}
