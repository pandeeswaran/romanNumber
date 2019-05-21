package com.lbh.talktiva.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Invitations {

    @SerializedName("invitationId")
    private int invitationId;

    @SerializedName("invitee")
    private User invitee;

    @SerializedName("status")
    private String status;

    @SerializedName("createdOn")
    private String createdOn;

    @SerializedName("statusChangedDate")
    private Date statusChangedDate;

    @SerializedName("guestUserEmail")
    private String guestUserEmail;

    @SerializedName("guestUserPhone")
    private String guestUserPhone;

    public int getInvitationId() {
        return invitationId;
    }

    public User getInvitee() {
        return invitee;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public Date getStatusChangedDate() {
        return statusChangedDate;
    }

    public String getGuestUserEmail() {
        return guestUserEmail;
    }

    public String getGuestUserPhone() {
        return guestUserPhone;
    }

    public void setInvitationId(int invitationId) {
        this.invitationId = invitationId;
    }

    public void setInvitee(User invitee) {
        this.invitee = invitee;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public void setStatusChangedDate(Date statusChangedDate) {
        this.statusChangedDate = statusChangedDate;
    }

    public void setGuestUserEmail(String guestUserEmail) {
        this.guestUserEmail = guestUserEmail;
    }

    public void setGuestUserPhone(String guestUserPhone) {
        this.guestUserPhone = guestUserPhone;
    }
}
