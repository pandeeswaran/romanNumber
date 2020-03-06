package com.talktiva.pilot.results

import com.google.gson.annotations.SerializedName
import com.talktiva.pilot.model.Event
import com.talktiva.pilot.model.Links
import java.io.Serializable

class ResultEvents : Serializable {

    @SerializedName("content")
    var events: List<Event>? = null

    @SerializedName("totalElements")
    var totalElements: Int? = null

    @SerializedName("totalPages")
    var totalPages: Int? = null

    @SerializedName("totalCount")
    var totalCount: Int? = null

    @SerializedName("size")
    var size: Int? = null

    @SerializedName("currentPage")
    var currentPage: Int? = null

    @SerializedName("links")
    var links: Links? = null
}
