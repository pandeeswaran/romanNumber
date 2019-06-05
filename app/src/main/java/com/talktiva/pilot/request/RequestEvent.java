package com.talktiva.pilot.request;

import android.print.PrinterId;

import com.google.gson.annotations.SerializedName;
import com.talktiva.pilot.model.Invitation;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class RequestEvent implements Serializable {

    @SerializedName("eventId")
    private int eventId;

    @SerializedName("canInviteGuests")
    private Boolean canInviteGuests;

    @SerializedName("eventDate")
    private long eventDate;

    @SerializedName("invitations")
    private List<Invitation> invitations;

    @SerializedName("location")
    private String location;

    @SerializedName("private")
    private Boolean isPrivate;

    @SerializedName("title")
    private String title;

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public void setCanInviteGuests(Boolean canInviteGuests) {
        this.canInviteGuests = canInviteGuests;
    }

    public void setEventDate(long eventDate) {
        this.eventDate = eventDate;
    }

    public void setInvitations(List<Invitation> invitations) {
        this.invitations = invitations;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setIsPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
