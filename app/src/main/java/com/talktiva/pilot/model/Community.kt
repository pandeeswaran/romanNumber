package com.talktiva.pilot.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Community : Serializable {

    @SerializedName("city")
    var city: String? = null

    @SerializedName("communityId")
    var communityId: String? = null

    @SerializedName("communityName")
    var communityName: String? = null

    @SerializedName("communityType")
    var communityType: String? = null

    @SerializedName("county")
    var county: String? = null

    @SerializedName("createdOn")
    var createdOn: String? = null

    @SerializedName("homeType")
    var homeType: String? = null

    @SerializedName("locationId")
    var locationId: Int? = null

    @SerializedName("modifiedOn")
    var modifiedOn: String? = null

    @SerializedName("otherCommunityName")
    var otherCommunityName: String? = null

    @SerializedName("schoolDistrictRatingInState")
    var schoolDistrictRatingInState: String? = null

    @SerializedName("selfManaged")
    var selfManaged: Boolean? = null

    @SerializedName("state")
    var state: String? = null

    @SerializedName("street")
    var street: String? = null

    @SerializedName("totalHomes")
    var totalHomes: Int? = null

    @SerializedName("zip")
    var zip: String? = null
}
