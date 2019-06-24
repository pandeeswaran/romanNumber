package com.talktiva.pilot.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

class Address : Serializable {

    @SerializedName("addressId")
    val addressId: Int? = null

    @SerializedName("street")
    val street: String? = null

    @SerializedName("city")
    val city: String? = null

    @SerializedName("state")
    val state: String? = null

    @SerializedName("zip")
    val zip: String? = null

    @SerializedName("createdOn")
    val createdOn: Date? = null

    @SerializedName("modifiedOn")
    val modifiedOn: Date? = null
}
