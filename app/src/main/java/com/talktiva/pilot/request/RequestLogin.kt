package com.talktiva.pilot.request

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RequestLogin : Serializable {

    @SerializedName("grant_type")
    var grantType: String? = null

    @SerializedName("username")
    var username: String? = null

    @SerializedName("password")
    var password: String? = null
}
