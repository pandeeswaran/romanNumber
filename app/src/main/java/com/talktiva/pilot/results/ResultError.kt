package com.talktiva.pilot.results

import com.google.gson.annotations.SerializedName
import com.talktiva.pilot.model.Errors
import java.io.Serializable

class ResultError : Serializable {

    @SerializedName("error")
    var error: String? = null

    @SerializedName("error_description")
    var errorDescription: String? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("status")
    var status: Int? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("developerMessage")
    var developerMessage: String? = null

    @SerializedName("code")
    var code: String? = null

    @SerializedName("field")
    var field: String? = null

    @SerializedName("errors")
    var errors: List<Errors>? = null
}
