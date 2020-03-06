package com.talktiva.pilot.model

import com.google.gson.annotations.SerializedName

import java.io.Serializable

class Links : Serializable {

    @SerializedName("next")
    var next: String? = null

    @SerializedName("prev")
    var prev: String? = null
}
