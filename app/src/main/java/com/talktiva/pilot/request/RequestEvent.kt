package com.talktiva.pilot.request

import com.google.gson.annotations.SerializedName
import com.talktiva.pilot.model.Invitation

import java.io.Serializable

class RequestEvent : Serializable {

    @SerializedName("eventId")
    var eventId: Int? = null

    @SerializedName("canInviteGuests")
    var canInviteGuests: Boolean? = null

    @SerializedName("eventDate")
    var eventDate: Long? = null

    @SerializedName("invitations")
    var invitations: List<Invitation>? = null

    @SerializedName("location")
    var location: String? = null

    @SerializedName("private")
    var isPrivate: Boolean? = null

    @SerializedName("title")
    var title: String? = null
}
