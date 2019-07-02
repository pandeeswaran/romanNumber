package com.talktiva.pilot.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

class Event : Serializable {

    @SerializedName("eventId")
    var eventId: Int? = null

    @SerializedName("createdBy")
    var createdBy: Int? = null

    @SerializedName("createdOn")
    var createdOn: Date? = null

    @SerializedName("modifiedOn")
    var modifiedOn: Date? = null

    @SerializedName("eventDate")
    var eventDate: Date? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("canInviteGuests")
    var canInviteGuests: Boolean? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("location")
    var location: String? = null

    @SerializedName("creatorFirstName")
    var creatorFirstName: String? = null

    @SerializedName("creatorLasttName")
    var creatorLasttName: String? = null

    @SerializedName("invitations")
    var invitations: List<Invitation>? = null

    @SerializedName("likeCount")
    var likeCount: Int? = null

    @SerializedName("hasLiked")
    var isHasLiked: Boolean? = null

    @SerializedName("private")
    var isPrivate: Boolean? = null
}
