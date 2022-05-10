package com.android.iparking.pojo;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class ParkingPojo {

    @SerializedName("Id")
    @Expose
    private String id;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Address")
    @Expose
    private String address;
    @SerializedName("Booking Fare")
    @Expose
    private Double bookingFare;
    @SerializedName("Stay Fare")
    @Expose
    private Double stayFare;
    @SerializedName("Location")
    @Expose
    private LocationPojo location;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getBookingFare() {
        return bookingFare;
    }

    public void setBookingFare(Double bookingFare) {
        this.bookingFare = bookingFare;
    }

    public Double getStayFare() {
        return stayFare;
    }

    public void setStayFare(Double stayFare) {
        this.stayFare = stayFare;
    }

    public LocationPojo getLocation() {
        return location;
    }

    public void setLocation(LocationPojo location) {
        this.location = location;
    }
}
