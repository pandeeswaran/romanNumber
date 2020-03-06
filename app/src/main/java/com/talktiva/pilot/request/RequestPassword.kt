package com.talktiva.pilot.request

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RequestPassword : Serializable {

    @SerializedName("currentPasssword")
    var currentPasssword: String? = null

    @SerializedName("newPassword")
    var newPassword: String? = null

    @SerializedName("confirmPassword")
    var confirmPassword: String? = null
}