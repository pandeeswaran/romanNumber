package com.talktiva.pilot.results

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ResultError : Serializable {

    @SerializedName("error")
    val error: String? = null

    @SerializedName("error_description")
    val errorDescription: String? = null

    @SerializedName("title")
    val title: String? = null

    @SerializedName("status")
    val status: Int? = null

    @SerializedName("message")
    val message: String? = null

    @SerializedName("developerMessage")
    val developerMessage: String? = null
}
