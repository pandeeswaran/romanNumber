package com.talktiva.pilot.model.apple


import com.google.gson.annotations.SerializedName

data class Firebase(
    @SerializedName("identities")
    var identities: Identities? = Identities(),
    @SerializedName("sign_in_provider")
    var signInProvider: String? = ""
)