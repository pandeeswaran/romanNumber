package com.talktiva.pilot.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class Address implements Serializable {

    @SerializedName("addressId")
    private Integer addressId;

    @SerializedName("street")
    private String street;

    @SerializedName("city")
    private String city;

    @SerializedName("state")
    private String state;

    @SerializedName("zip")
    private String zip;

    @SerializedName("createdOn")
    private Date createdOn;

    @SerializedName("modifiedOn")
    private Date modifiedOn;

    public int getAddressId() {
        return addressId;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZip() {
        return zip;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public Date getModifiedOn() {
        return modifiedOn;
    }
}
