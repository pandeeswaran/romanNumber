package com.lbh.talktiva.model;

import com.google.gson.annotations.SerializedName;

public class Invitations {

    @SerializedName("invitationId")
    private int invitationId;

    @SerializedName("invitee")
    private User invitee;

    @SerializedName("status")
    private String status;

    @SerializedName("statusChangedDate")
    private String statusChangedDate;

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

    public String getStatusChangedDate() {
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

    public void setStatusChangedDate(String statusChangedDate) {
        this.statusChangedDate = statusChangedDate;
    }

    public void setGuestUserEmail(String guestUserEmail) {
        this.guestUserEmail = guestUserEmail;
    }

    public void setGuestUserPhone(String guestUserPhone) {
        this.guestUserPhone = guestUserPhone;
    }
}
