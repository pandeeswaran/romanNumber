package com.talktiva.pilot.results

import com.google.gson.annotations.SerializedName
import com.talktiva.pilot.model.Event
import com.talktiva.pilot.model.Links
import java.io.Serializable

class ResultEvents : Serializable {

    @SerializedName("content")
    val events: List<Event>? = null

    @SerializedName("totalElements")
    val totalElements: Int? = null

    @SerializedName("totalPages")
    val totalPages: Int? = null

    @SerializedName("totalCount")
    val totalCount: Int? = null

    @SerializedName("size")
    val size: Int? = null

    @SerializedName("currentPage")
    val currentPage: Int? = null

    @SerializedName("links")
    val links: Links? = null
}
