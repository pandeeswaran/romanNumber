package com.talktiva.pilot.request

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RequestFeedback : Serializable {

    @SerializedName("feedback")
    var feedback: String? = null
}