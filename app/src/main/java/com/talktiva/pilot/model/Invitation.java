package com.talktiva.pilot.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class Invitation implements Serializable {

    @SerializedName("invitationId")
    private int invitationId;

    @SerializedName("inviteeId")
    private int inviteeId;

    @SerializedName("status")
    private String status;

    @SerializedName("createdOn")
    private Date createdOn;

    @SerializedName("statusChangedDate")
    private Date statusChangedDate;

    @SerializedName("guestUserEmail")
    private String guestUserEmail;

    @SerializedName("guestUserPhone")
    private String guestUserPhone;

    @SerializedName("inviteeFirstName")
    private String inviteeFirstName;

    @SerializedName("inviteeLasttName")
    private String inviteeLasttName;

    public int getInvitationId() {
        return invitationId;
    }

    public void setInvitationId(int invitationId) {
        this.invitationId = invitationId;
    }

    public int getInviteeId() {
        return inviteeId;
    }

    public void setInviteeId(int inviteeId) {
        this.inviteeId = inviteeId;
    }

    public String getStatus() {
        return status;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public Date getStatusChangedDate() {
        return statusChangedDate;
    }

    public String getGuestUserEmail() {
        return guestUserEmail;
    }

    public void setGuestUserEmail(String guestUserEmail) {
        this.guestUserEmail = guestUserEmail;
    }

    public String getGuestUserPhone() {
        return guestUserPhone;
    }

    public void setGuestUserPhone(String guestUserPhone) {
        this.guestUserPhone = guestUserPhone;
    }

    public String getInviteeFirstName() {
        return inviteeFirstName;
    }

    public String getInviteeLasttName() {
        return inviteeLasttName;
    }
}
