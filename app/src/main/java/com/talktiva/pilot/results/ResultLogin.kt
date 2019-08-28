package com.talktiva.pilot.results

import com.google.gson.annotations.SerializedName

class ResultLogin {

    @SerializedName("access_token")
    var accessToken: String? = null

    @SerializedName("token_type")
    var tokenType: String? = null

    @SerializedName("refresh_token")
    var refreshToken: String? = null

    @SerializedName("expires_in")
    var expiresIn: Long? = null

    @SerializedName("scope")
    var scope: String? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("userId")
    var userId: Int? = null

    @SerializedName("fullName")
    var fullName: String? = null

    @SerializedName("firstName")
    var firstName: String? = null

    @SerializedName("lastName")
    var lastName: String? = null

    @SerializedName("temporaryPassword")
    var temporaryPassword: Boolean? = null

    @SerializedName("jti")
    var jti: String? = null
}
