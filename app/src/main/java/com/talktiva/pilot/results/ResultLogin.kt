package com.talktiva.pilot.results

import com.google.gson.annotations.SerializedName

class ResultLogin {

    @SerializedName("access_token")
    val accessToken: String? = null

    @SerializedName("token_type")
    val tokenType: String? = null

    @SerializedName("refresh_token")
    val refreshToken: String? = null

    @SerializedName("expires_in")
    val expiresIn: Long? = null

    @SerializedName("scope")
    val scope: String? = null

    @SerializedName("email")
    val email: String? = null

    @SerializedName("userId")
    val userId: Int? = null

    @SerializedName("fullName")
    val fullName: String? = null

    @SerializedName("firstName")
    val firstName: String? = null

    @SerializedName("lastName")
    val lastName: String? = null

    @SerializedName("jti")
    val jti: String? = null
}
