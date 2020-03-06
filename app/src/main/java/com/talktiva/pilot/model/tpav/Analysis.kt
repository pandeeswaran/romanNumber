package com.talktiva.pilot.model.tpav

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Analysis : Serializable {

    @SerializedName("dpv_match_code")
    var dpvMatchCode: String? = null

    @SerializedName("dpv_footnotes")
    var dpvFootnotes: String? = null

    @SerializedName("footnotes")
    var footnotes: String? = null

}