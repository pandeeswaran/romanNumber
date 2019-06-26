package com.talktiva.pilot.request

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RequestCommunity : Serializable {
    @SerializedName("appartmentUnit")
    val appartmentUnit: String? = null

    @SerializedName("street")
    val street: String? = null

    @SerializedName("zip")
    val zip: String? = null
}
