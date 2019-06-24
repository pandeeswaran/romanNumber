package com.talktiva.pilot.model

import com.google.gson.annotations.SerializedName

import java.io.Serializable
import java.util.Date

class User : Serializable {

    @SerializedName("userId")
    val userId: Int? = null

    @SerializedName("firstName")
    val firstName: String? = null

    @SerializedName("lastName")
    val lastName: String? = null

    @SerializedName("email")
    val email: String? = null

    @SerializedName("username")
    val username: String? = null

    @SerializedName("createdOn")
    val createdOn: Date? = null

    @SerializedName("modifiedOn")
    val modifiedOn: Date? = null

    @SerializedName("address")
    val address: Address? = null
}
