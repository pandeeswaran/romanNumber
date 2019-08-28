package com.talktiva.pilot.request

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RequestProfile : Serializable {

    @SerializedName("birthday")
    var birthday: String? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("fullName")
    var fullName: String? = null

    @SerializedName("phone")
    var phone: String? = null

    @SerializedName("residentSince")
    var residentSince: String? = null

    @SerializedName("showMyBirthday")
    var showMyBirthday: Boolean? = null
}