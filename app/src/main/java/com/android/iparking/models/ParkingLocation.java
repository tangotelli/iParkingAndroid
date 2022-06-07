package com.android.iparking.models;

import com.android.iparking.dtos.LocationDTO;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class ParkingLocation implements Serializable {

    private Double latitude;
    private Double longitude;

    public ParkingLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public static ParkingLocation fromDTO(LocationDTO locationDTO) {
        return new ParkingLocation(locationDTO.getLatitude(), locationDTO.getLongitude());
    }

    public LatLng toLatLng() {
        return new LatLng(this.latitude, this.longitude);
    }
}
