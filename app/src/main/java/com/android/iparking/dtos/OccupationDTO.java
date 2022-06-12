package com.android.iparking.dtos;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class OccupationDTO {

    @SerializedName("Occupation percentage")
    @Expose
    private Double occupationPercentage;

    public Double getOccupationPercentage() {
        return occupationPercentage;
    }

    public void setOccupationPercentage(Double occupationPercentage) {
        this.occupationPercentage = occupationPercentage;
    }

}
