package com.talktiva.pilot.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Errors : Serializable {
    @SerializedName("code")
    var code: String? = null

    @SerializedName("field")
    var field: String? = null

    @SerializedName("message")
    var message: String? = null
}
