package com.talktiva.pilot.model

import com.google.gson.annotations.SerializedName

import java.io.Serializable
import java.util.Date

class Invitation : Serializable {

    @SerializedName("invitationId")
    var invitationId: Int? = null

    @SerializedName("inviteeId")
    var inviteeId: Int? = null

    @SerializedName("status")
    val status: String? = null

    @SerializedName("createdOn")
    val createdOn: Date? = null

    @SerializedName("statusChangedDate")
    val statusChangedDate: Date? = null

    @SerializedName("guestUserEmail")
    var guestUserEmail: String? = null

    @SerializedName("guestUserPhone")
    var guestUserPhone: String? = null

    @SerializedName("inviteeFirstName")
    val inviteeFirstName: String? = null

    @SerializedName("inviteeLasttName")
    val inviteeLasttName: String? = null

    @SerializedName("inviteeAddress")
    val inviteeAddress: String? = null
}
