package com.talktiva.pilot.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Event implements Serializable {

    @SerializedName("eventId")
    private Integer eventId;

    @SerializedName("createdBy")
    private Integer createdBy;

    @SerializedName("createdOn")
    private Date createdOn;

    @SerializedName("modifiedOn")
    private Date modifiedOn;

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

    @SerializedName("creatorFirstName")
    private String creatorFirstName;

    @SerializedName("creatorLasttName")
    private String creatorLasttName;

    @SerializedName("invitations")
    private List<Invitation> invitations;

    @SerializedName("likeCount")
    private Integer likeCount;

    @SerializedName("hasLiked")
    private Boolean hasLiked;

    @SerializedName("private")
    private Boolean isPrivate;

    public Integer getEventId() {
        return eventId;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public Date getModifiedOn() {
        return modifiedOn;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public String getStatus() {
        return status;
    }

    public Boolean canInviteGuests() {
        return canInviteGuests;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getCreatorFirstName() {
        return creatorFirstName;
    }

    public String getCreatorLasttName() {
        return creatorLasttName;
    }

    public List<Invitation> getInvitations() {
        return invitations;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public Boolean isHasLiked() {
        return hasLiked;
    }

    public Boolean isPrivate() {
        return isPrivate;
    }
}
