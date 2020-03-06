package com.talktiva.pilot.model.tpav

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class AddressObject : Serializable {

    @SerializedName("input_index")
    var inputIndex: Int? = null

    @SerializedName("candidate_index")
    var candidateIndex: Int? = null

    @SerializedName("delivery_line_1")
    var deliveryLine1: String? = null

    @SerializedName("last_line")
    var lastLine: String? = null

    @SerializedName("delivery_point_barcode")
    var deliveryPointBarcode: String? = null

    @SerializedName("components")
    var components: Components? = null

    @SerializedName("metadata")
    var metadata: MetaData? = null

    @SerializedName("analysis")
    var analysis: Analysis? = null

}