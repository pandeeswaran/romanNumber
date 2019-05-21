package com.lbh.talktiva.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class User {

    @SerializedName("userId")
    private int userId;

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("email")
    private String email;

    @SerializedName("createdOn")
    private String createdOn;

    @SerializedName("modifiedOn")
    private String modifiedOn;

    @SerializedName("addresses")
    private List<Address> addressList;

    public int getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public String getModifiedOn() {
        return modifiedOn;
    }

    public List<Address> getAddressList() {
        return addressList;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public void setModifiedOn(String modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }
}
