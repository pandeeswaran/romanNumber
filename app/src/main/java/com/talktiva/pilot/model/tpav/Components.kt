package com.talktiva.pilot.model.tpav

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Components : Serializable {

    @SerializedName("street_name")
    var streetName: String? = null

    @SerializedName("street_suffix")
    var streetSuffix: String? = null

    @SerializedName("city_name")
    var cityName: String? = null

    @SerializedName("default_city_name")
    var defaultCityName: String? = null

    @SerializedName("state_abbreviation")
    var stateAbbreviation: String? = null

    @SerializedName("zipcode")
    var zipcode: String? = null

    @SerializedName("plus4_code")
    var plus4Code: String? = null

    @SerializedName("delivery_point")
    var deliveryPoint: String? = null

    @SerializedName("delivery_point_check_digit")
    var deliveryPointCheckDigit: String? = null
}

