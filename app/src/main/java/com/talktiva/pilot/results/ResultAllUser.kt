package com.talktiva.pilot.results

import com.google.gson.annotations.SerializedName
import com.talktiva.pilot.model.Links
import com.talktiva.pilot.model.User
import java.io.Serializable

class ResultAllUser : Serializable {

    @SerializedName("content")
    var users: List<User>? = null

    @SerializedName("totalElements")
    var totalElements: Int? = null

    @SerializedName("totalPages")
    var totalPages: Int? = null

    @SerializedName("totalCount")
    var totalCount: Int? = null

    @SerializedName("size")
    var size: Int? = null

    @SerializedName("userCount")
    var userCount: Int? = null

    @SerializedName("currentPage")
    var currentPage: Int? = null

    @SerializedName("links")
    var links: Links? = null
}
