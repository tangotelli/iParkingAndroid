package com.android.iparking.models;

public interface VehicleBuilders {

    interface Id {
        VehicleBuilders.Nickname id(String id);
    }

    interface Nickname {
        VehicleBuilders.LicensePlate nickname(String nickname);
    }

    interface LicensePlate {
        VehicleBuilders.UserEmail licensePlate(String licensePlate);
    }

    interface UserEmail {
        VehicleBuilders.Optional userEmail(String userEmail);
    }

    interface Optional {
        Vehicle build();
    }
}
