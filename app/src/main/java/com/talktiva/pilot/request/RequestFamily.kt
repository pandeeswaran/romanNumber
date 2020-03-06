package com.talktiva.pilot.request

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RequestFamily : Serializable {

    @SerializedName("memberId")
    var memberId: Int? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("fullName")
    var fullName: String? = null
}