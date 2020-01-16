package com.talktiva.pilot.model


import com.google.gson.annotations.SerializedName

data class Apple(
    @SerializedName("aud")
    val aud: String,
    @SerializedName("auth_time")
    val authTime: Int,
    @SerializedName("email")
    val email: String,
    @SerializedName("email_verified")
    val emailVerified: Boolean,
    @SerializedName("exp")
    val exp: Int,
    @SerializedName("firebase")
    val firebase: Firebase,
    @SerializedName("iat")
    val iat: Int,
    @SerializedName("iss")
    val iss: String,
    @SerializedName("sub")
    val sub: String,
    @SerializedName("user_id")
    val userId: String
) {
    data class Firebase(
        @SerializedName("identities")
        val identities: Identities,
        @SerializedName("sign_in_provider")
        val signInProvider: String
    ) {
        data class Identities(
            @SerializedName("apple.com")
            val appleCom: List<String>,
            @SerializedName("email")
            val email: List<String>
        )
    }
}