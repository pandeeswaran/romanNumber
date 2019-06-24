package com.talktiva.pilot.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

class Event : Serializable {

    @SerializedName("eventId")
    val eventId: Int? = null

    @SerializedName("createdBy")
    val createdBy: Int? = null

    @SerializedName("createdOn")
    val createdOn: Date? = null

    @SerializedName("modifiedOn")
    val modifiedOn: Date? = null

    @SerializedName("eventDate")
    val eventDate: Date? = null

    @SerializedName("status")
    val status: String? = null

    @SerializedName("canInviteGuests")
    val canInviteGuests: Boolean? = null

    @SerializedName("title")
    val title: String? = null

    @SerializedName("location")
    val location: String? = null

    @SerializedName("creatorFirstName")
    val creatorFirstName: String? = null

    @SerializedName("creatorLasttName")
    val creatorLasttName: String? = null

    @SerializedName("invitations")
    val invitations: List<Invitation>? = null

    @SerializedName("likeCount")
    val likeCount: Int? = null

    @SerializedName("hasLiked")
    val isHasLiked: Boolean? = null

    @SerializedName("private")
    val isPrivate: Boolean? = null
}
