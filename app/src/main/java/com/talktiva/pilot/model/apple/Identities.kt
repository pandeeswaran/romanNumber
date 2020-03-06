package com.talktiva.pilot.model.apple


import com.google.gson.annotations.SerializedName

data class Identities(
    @SerializedName("apple.com")
    var appleCom: List<String?>? = listOf(),
    @SerializedName("email")
    var email: List<String?>? = listOf()
)