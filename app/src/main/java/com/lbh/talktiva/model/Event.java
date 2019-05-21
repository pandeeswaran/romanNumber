package com.lbh.talktiva.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Event {

    @SerializedName("eventId")
    private int eventId;

    @SerializedName("createdBy")
    private User createdBy;

    @SerializedName("createdOn")
    private String createdOn;

    @SerializedName("modifiedOn")
    private String modifiedOn;

    @SerializedName("eventDate")
    private Date eventDate;

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

    public int getEventId() {
        return eventId;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public String getModifiedOn() {
        return modifiedOn;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public String getStatus() {
        return status;
    }

    public Boolean getCanInviteGuests() {
        return canInviteGuests;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public List<Invitations> getInvitations() {
        return invitations;
    }

    public Boolean getIsPrivate() {
        return isPrivate;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public void setModifiedOn(String modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public void setEventDate(Date eventDate) {
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
