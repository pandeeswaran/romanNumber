package com.talktiva.pilot.model

import com.google.gson.annotations.SerializedName

import java.io.Serializable

class Slider(@field:SerializedName("text")
             var text: Int?, @field:SerializedName("image_url")
             var imageUrl: String) : Serializable
