package com.lbh.talktiva.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class CreateEvent implements Serializable {

    @SerializedName("eventId")
    private int eventId;

    @SerializedName("createdBy")
    private User createdBy;

    @SerializedName("createdOn")
    private Date createdOn;

    @SerializedName("modifiedOn")
    private Date modifiedOn;

    @SerializedName("eventDate")
    private Long eventDate;

    @SerializedName("status")
    private String status;

    @SerializedName("canInviteGuests")
    private Boolean canInviteGuests;

    @SerializedName("title")
    private String title;

    @SerializedName("location")
    private String location;

    @SerializedName("invitations")
    private List<Invitations> invitations;

    @SerializedName("private")
    private Boolean isPrivate;

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public void setEventDate(Long eventDate) {
        this.eventDate = eventDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCanInviteGuests(Boolean canInviteGuests) {
        this.canInviteGuests = canInviteGuests;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setInvitations(List<Invitations> invitations) {
        this.invitations = invitations;
    }

    public void setIsPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }
}
