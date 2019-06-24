package com.talktiva.pilot.results

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ResultLoginError : Serializable {

    @SerializedName("error")
    val error: String? = null

    @SerializedName("error_description")
    val errorDescription: String? = null
}
