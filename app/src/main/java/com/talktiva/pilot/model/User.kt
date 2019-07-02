package com.talktiva.pilot.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

class User : Serializable {

    @SerializedName("userId")
    var userId: Int? = null

    @SerializedName("fullName")
    var fullName: String? = null

    @SerializedName("firstName")
    var firstName: String? = null

    @SerializedName("lastName")
    var lastName: String? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("createdOn")
    var createdOn: Date? = null

    @SerializedName("modifiedOn")
    var modifiedOn: Date? = null

    @SerializedName("address")
    var address: Address? = null

    @SerializedName("addressProofUploaded")
    var addressProofUploaded: Boolean? = null

    @SerializedName("addressVerified")
    var addressVerified: Boolean? = null

    @SerializedName("emailVerified")
    var emailVerified: Boolean? = null
}
