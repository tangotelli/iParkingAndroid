package com.android.iparking.models;

import com.android.iparking.dtos.VehicleDTO;

public class Vehicle {

    private String id;
    private String nickname;
    private String licensePlate;
    private String userEmail;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public static Vehicle fromDTO(VehicleDTO vehicleDTO) {
        return new VehicleBuilder()
                .id(vehicleDTO.getId())
                .nickname(vehicleDTO.getNickname())
                .licensePlate(vehicleDTO.getLicensePlate())
                .userEmail(vehicleDTO.getUser())
                .build();
    }

    public static class VehicleBuilder implements VehicleBuilders.Id, VehicleBuilders.Nickname,
            VehicleBuilders.LicensePlate, VehicleBuilders.UserEmail, VehicleBuilders.Optional {

        private Vehicle vehicle;

        public VehicleBuilder() {
            this.vehicle = new Vehicle();
        }

        @Override
        public VehicleBuilders.Nickname id(String id) {
            this.vehicle.setId(id);
            return this;
        }

        @Override
        public VehicleBuilders.LicensePlate nickname(String nickname) {
            this.vehicle.setNickname(nickname);
            return this;
        }

        @Override
        public VehicleBuilders.UserEmail licensePlate(String licensePlate) {
            this.vehicle.setLicensePlate(licensePlate);
            return this;
        }

        @Override
        public VehicleBuilders.Optional userEmail(String userEmail) {
            this.vehicle.setUserEmail(userEmail);
            return this;
        }

        @Override
        public Vehicle build() {
            return this.vehicle;
        }
    }
}
