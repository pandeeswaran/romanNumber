package com.talktiva.pilot.results

import com.google.gson.annotations.SerializedName
import com.talktiva.pilot.model.Links
import com.talktiva.pilot.model.Notification
import java.io.Serializable

class ResultNotification : Serializable {

    @SerializedName("content")
    var content: List<Notification>? = null

    @SerializedName("currentPage")
    var currentPage: Int? = null

    @SerializedName("links")
    var links: Links? = null

    @SerializedName("size")
    var size: Int? = null

    @SerializedName("totalCount")
    var totalCount: Int? = null

    @SerializedName("totalElements")
    var totalElements: Int? = null

    @SerializedName("totalPages")
    var totalPages: Int? = null
}