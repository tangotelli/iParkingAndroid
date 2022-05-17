package com.android.iparking.models;

import com.android.iparking.pojo.ParkingPojo;

import java.io.Serializable;

public class Parking implements Serializable {

    private String id;
    private String name;
    private String address;
    private Double bookingFare;
    private Double stayFare;
    private ParkingLocation parkingLocation;

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

    public ParkingLocation getParkingLocation() {
        return parkingLocation;
    }

    public void setParkingLocation(ParkingLocation parkingLocation) {
        this.parkingLocation = parkingLocation;
    }

    public static Parking fromPojo(ParkingPojo parkingPojo) {
        return new ParkingBuilder()
                .id(parkingPojo.getId())
                .name(parkingPojo.getName())
                .address(parkingPojo.getAddress())
                .bookingFare(parkingPojo.getBookingFare())
                .stayFare(parkingPojo.getStayFare())
                .location(ParkingLocation.fromPojo(parkingPojo.getLocation()))
                .build();
    }

    public static class ParkingBuilder implements ParkingBuilders.Id, ParkingBuilders.Name,
            ParkingBuilders.Address, ParkingBuilders.BookingFare, ParkingBuilders.StayFare,
            ParkingBuilders.Location, ParkingBuilders.Optional {

        private Parking parking;

        public ParkingBuilder() {
            this.parking = new Parking();
        }

        @Override
        public ParkingBuilders.Name id(String id) {
            this.parking.setId(id);
            return this;
        }

        @Override
        public ParkingBuilders.Address name(String name) {
            this.parking.setName(name);
            return this;
        }

        @Override
        public ParkingBuilders.BookingFare address(String address) {
            this.parking.setAddress(address);
            return this;
        }

        @Override
        public ParkingBuilders.StayFare bookingFare(Double bookingFare) {
            this.parking.setBookingFare(bookingFare);
            return this;
        }

        @Override
        public ParkingBuilders.Location stayFare(Double stayFare) {
            this.parking.setStayFare(stayFare);
            return this;
        }

        @Override
        public ParkingBuilders.Optional location(ParkingLocation location) {
            this.parking.setParkingLocation(location);
            return this;
        }

        @Override
        public Parking build() {
            return this.parking;
        }
    }
}
