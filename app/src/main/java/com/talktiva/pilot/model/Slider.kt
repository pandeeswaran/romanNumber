package com.talktiva.pilot.model

import com.google.gson.annotations.SerializedName

import java.io.Serializable

class Slider(@field:SerializedName("text")
             val text: Int?, @field:SerializedName("image_url")
             val imageUrl: String) : Serializable
