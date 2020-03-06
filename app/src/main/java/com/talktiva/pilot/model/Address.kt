package com.talktiva.pilot.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

class Address : Serializable {

    @SerializedName("addressId")
    var addressId: Int? = null

    @SerializedName("street")
    var street: String? = null

    @SerializedName("city")
    var city: String? = null

    @SerializedName("state")
    var state: String? = null

    @SerializedName("zip")
    var zip: String? = null

    @SerializedName("createdOn")
    var createdOn: Date? = null

    @SerializedName("modifiedOn")
    var modifiedOn: Date? = null
}
