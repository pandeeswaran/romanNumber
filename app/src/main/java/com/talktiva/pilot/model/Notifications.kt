package com.talktiva.pilot.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Notifications : Serializable {

    @SerializedName("acceptOrDecline")
    var acceptOrDecline: Boolean? = null

    @SerializedName("cancelled")
    var cancelled: Boolean? = null

    @SerializedName("comment")
    var comment: Boolean? = null

    @SerializedName("directChat")
    var directChat: Boolean? =  null

    @SerializedName("directChatSound")
    var directChatSound: String? = null

    @SerializedName("groupChat")
    var groupChat: Boolean? = null

    @SerializedName("groupChatSound")
    var groupChatSound: Boolean? = null

    @SerializedName("invited")
    var invited: Boolean? = null

    @SerializedName("like")
    var like: Boolean? = null

    @SerializedName("managementChat")
    var managementChat: Boolean? = null

    @SerializedName("newPeopleJoinedCommunity")
    var newPeopleJoinedCommunity: Boolean? = null

    @SerializedName("notice")
    var notice: Boolean? = null

    @SerializedName("notificationSettingId")
    var notificationSettingId: Int? = null

    @SerializedName("pauseAll")
    var pauseAll: Boolean? = null

    @SerializedName("reportAbuse")
    var reportAbuse: Boolean? = null

    @SerializedName("shareOrLike")
    var shareOrLike: Boolean? = null
}