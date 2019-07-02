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
    var status: String? = null

    @SerializedName("createdOn")
    var createdOn: Date? = null

    @SerializedName("statusChangedDate")
    var statusChangedDate: Date? = null

    @SerializedName("guestUserEmail")
    var guestUserEmail: String? = null

    @SerializedName("guestUserPhone")
    var guestUserPhone: String? = null

    @SerializedName("inviteeFirstName")
    var inviteeFirstName: String? = null

    @SerializedName("inviteeLasttName")
    var inviteeLasttName: String? = null

    @SerializedName("inviteeAddress")
    var inviteeAddress: String? = null
}
