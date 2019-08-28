package com.talktiva.pilot.request

import com.google.gson.annotations.SerializedName

import java.io.Serializable

class RequestShare : Serializable {

    @SerializedName("inviteeIds")
    var inviteeIds: String? = null

}
