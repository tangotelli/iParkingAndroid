package com.android.iparking.models;

import com.android.iparking.dtos.StayDTO;

import java.io.Serializable;

public class Stay implements Serializable {

    private String id;
    private String parking;
    private String vehicle;
    private String beginning;
    private String end;
    private Double price;
    private Double fare;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParking() {
        return parking;
    }

    public void setParking(String parking) {
        this.parking = parking;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getFare() {
        return fare;
    }

    public void setFare(Double fare) {
        this.fare = fare;
    }

    public static Stay activeStayFromDTO(StayDTO stayDTO) {
        return new StayBuilder()
                .id(stayDTO.getId())
                .parking(stayDTO.getParking())
                .vehicle(stayDTO.getVehicle())
                .beginning(stayDTO.getBeginning())
                .fare(stayDTO.getFare())
                .build();
    }

    public static Stay finishedStayFromDTO(StayDTO stayDTO) {
        return new StayBuilder()
                .id(stayDTO.getId())
                .parking(stayDTO.getParking())
                .vehicle(stayDTO.getVehicle())
                .beginning(stayDTO.getBeginning())
                .end(stayDTO.getEnd())
                .price(stayDTO.getPrice())
                .build();
    }

    public static class StayBuilder implements StayBuilders.Id, StayBuilders.Parking,
            StayBuilders.Vehicle, StayBuilders.Beginning, StayBuilders.Optional {

        private final Stay stay;

        public StayBuilder() {
            this.stay = new Stay();
        }

        @Override
        public StayBuilders.Parking id(String id) {
            this.stay.setId(id);
            return this;
        }

        @Override
        public StayBuilders.Vehicle parking(String parking) {
            this.stay.setParking(parking);
            return this;
        }

        @Override
        public StayBuilders.Beginning vehicle(String vehicle) {
            this.stay.setVehicle(vehicle);
            return this;
        }

        @Override
        public StayBuilders.Optional beginning(String beginning) {
            this.stay.setBeginning(beginning);
            return this;
        }

        @Override
        public StayBuilders.Optional end(String end) {
            this.stay.setEnd(end);
            return this;
        }

        @Override
        public StayBuilders.Optional price(String price) {
            this.stay.setPrice(Double.valueOf(price));
            return this;
        }

        @Override
        public StayBuilders.Optional fare(String fare) {
            this.stay.setFare(Double.valueOf(fare));
            return this;
        }

        @Override
        public Stay build() {
            return this.stay;
        }
    }
}
