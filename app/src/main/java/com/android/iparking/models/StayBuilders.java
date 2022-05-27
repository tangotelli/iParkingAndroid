package com.android.iparking.models;

public interface StayBuilders {

    interface Id {
        StayBuilders.Parking id(String id);
    }

    interface Parking {
        StayBuilders.Vehicle parking(String parking);
    }

    interface Vehicle {
        StayBuilders.Beginning vehicle(String vehicle);
    }

    interface Beginning {
        StayBuilders.Optional beginning(String beginning);
    }

    interface Optional {
        StayBuilders.Optional end(String end);

        StayBuilders.Optional price(String price);

        Stay build();
    }

}
