package com.talktiva.pilot.results

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ResultMessage : Serializable {

    @SerializedName("responseMessage")
    var responseMessage: String? = null
}
