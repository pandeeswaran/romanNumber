package com.talktiva.pilot.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

class Notification : Serializable {

    @SerializedName("action")
    var action: String? = null

    @SerializedName("createdOn")
    var createdOn: Date? = null

    @SerializedName("description")
    var description: String? = null

    @SerializedName("entityId")
    var entityId: Int? = null

    @SerializedName("modifiedOn")
    var modifiedOn: Date? = null

    @SerializedName("notificationId")
    var notificationId: Int? = null

    @SerializedName("read")
    var read: Boolean = false

    @SerializedName("recipientId")
    var recipientId: Int? = null

    @SerializedName("type")
    var type: String? = null


}