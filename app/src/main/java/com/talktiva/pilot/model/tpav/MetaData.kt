package com.talktiva.pilot.model.tpav

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class MetaData : Serializable {
    @SerializedName("record_type")
    var recordType: String? = null

    @SerializedName("zip_type")
    var zipType: String? = null

    @SerializedName("county_fips")
    var countyFips: String? = null

    @SerializedName("county_name")
    var countyName: String? = null

    @SerializedName("carrier_route")
    var carrierRoute: String? = null

    @SerializedName("building_default_indicator")
    var buildingDefaultIndicator: String? = null

    @SerializedName("rdi")
    var rdi: String? = null

    @SerializedName("elot_sequence")
    var elotSequence: String? = null

    @SerializedName("elot_sort")
    var elotSort: String? = null

    @SerializedName("latitude")
    var latitude: Float? = null

    @SerializedName("longitude")
    var longitude: Float? = null

    @SerializedName("precision")
    var precision: String? = null

    @SerializedName("time_zone")
    var time_zone: String? = null

    @SerializedName("utc_offset")
    var utc_offset: Int? = null

    @SerializedName("dst")
    var dst: Boolean? = null
}