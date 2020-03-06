package com.talktiva.pilot.request

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RequestSignUp : Serializable {

    @SerializedName("appartmentUnit")
    var appartmentUnit: String? = null

    @SerializedName("communityId")
    var communityId: Int? = null

    @SerializedName("deviceType")
    var deviceType: String? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("fullName")
    var fullName: String? = null

    @SerializedName("idToken")
    var idToken: String? = null

    @SerializedName("invitationCode")
    var invitationCode: String? = null

    @SerializedName("password")
    var password: String? = null

    @SerializedName("phone")
    var phone: String? = null

    @SerializedName("registrationId")
    var registrationId: String? = null

    @SerializedName("registrationType")
    var registrationType: String? = null

    @SerializedName("street")
    var street: String? = null

    @SerializedName("udid")
    var udid: String? = null
}
