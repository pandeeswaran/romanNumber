package com.talktiva.pilot.request

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RequestCommunity : Serializable {

    @SerializedName("appartmentUnit")
    var appartmentUnit: String? = null

    @SerializedName("street")
    var street: String? = null

    @SerializedName("zip")
    var zip: String? = null
}
