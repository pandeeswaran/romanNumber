package com.talktiva.pilot.model.apple


import com.google.gson.annotations.SerializedName

data class AppleData(
    @SerializedName("aud")
    var aud: String? = "",
    @SerializedName("auth_time")
    var authTime: Int? = 0,
    @SerializedName("email")
    var email: String? = "",
    @SerializedName("email_verified")
    var emailVerified: Boolean? = false,
    @SerializedName("exp")
    var exp: Int? = 0,
    @SerializedName("firebase")
    var firebase: Firebase? = Firebase(),
    @SerializedName("iat")
    var iat: Int? = 0,
    @SerializedName("iss")
    var iss: String? = "",
    @SerializedName("sub")
    var sub: String? = "",
    @SerializedName("user_id")
    var userId: String? = ""
)