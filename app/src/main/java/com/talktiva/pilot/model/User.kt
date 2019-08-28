package com.talktiva.pilot.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

class User : Serializable {

    @SerializedName("address")
    var address: Address? = null

    @SerializedName("addressProofUploaded")
    var addressProofUploaded: Boolean? = null

    @SerializedName("addressVerified")
    var addressVerified: Boolean? = null

    @SerializedName("birthday")
    var birthday: String? = null

    @SerializedName("communityFlag")
    var communityFlag: Boolean? = null

    @SerializedName("createdOn")
    var createdOn: Date? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("emailVerified")
    var emailVerified: Boolean? = null

    @SerializedName("eventAttendedCount")
    var eventAttendedCount: Int? = null

    @SerializedName("eventHostedCount")
    var eventHostedCount: Int? = null

    @SerializedName("familyMemberStatus")
    var familyMemberStatus: String? = null

    @SerializedName("firstName")
    var firstName: String? = null

    @SerializedName("fullName")
    var fullName: String? = null

    @SerializedName("lastName")
    var lastName: String? = null

    @SerializedName("modifiedOn")
    var modifiedOn: Date? = null

    @SerializedName("phone")
    var phone: String? = null

    @SerializedName("postCount")
    var postCount: Int? = null

    @SerializedName("residentSince")
    var residentSince: String? = null

    @SerializedName("showMyBirthday")
    var showMyBirthday: Boolean? = null

    @SerializedName("canChangeName")
    var canChangeName: Boolean? = null

    @SerializedName("userId")
    var userId: Int? = null

    @SerializedName("userImage")
    var userImage: String? = null

    @SerializedName("familyMemberCount")
    var familyMemberCount: Int? = null
}
