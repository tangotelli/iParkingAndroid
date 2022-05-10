package com.android.iparking.models;

public interface ParkingBuilders {

    interface Id {
        ParkingBuilders.Name id(String id);
    }

    interface Name {
        ParkingBuilders.Address name(String name);
    }

    interface Address {
        ParkingBuilders.BookingFare address(String address);
    }

    interface BookingFare {
        ParkingBuilders.StayFare bookingFare(Double bookingFare);
    }

    interface StayFare {
        ParkingBuilders.Location stayFare(Double stayFare);
    }

    interface Location {
        ParkingBuilders.Optional location(ParkingLocation location);
    }

    interface Optional {
        Parking build();
    }
}
