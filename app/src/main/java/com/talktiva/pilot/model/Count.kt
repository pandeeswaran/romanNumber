package com.talktiva.pilot.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Count : Serializable {

    @SerializedName("eventCount")
    var eventCount: Int? = null

    @SerializedName("userCount")
    var userCount: Int? = null

    @SerializedName("emailVerified")
    var emailVerified: Boolean? = null

    @SerializedName("addressVerified")
    var addressVerified: Boolean? = null

    @SerializedName("addressProofUploaded")
    var addressProofUploaded: Boolean? = null
}
