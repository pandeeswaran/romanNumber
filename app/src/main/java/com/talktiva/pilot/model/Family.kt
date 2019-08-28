package com.talktiva.pilot.model

import com.google.gson.annotations.SerializedName

import java.io.Serializable
import java.util.*

class Family : Serializable {

    @SerializedName("memberId")
    var memberId: Int? = null

    @SerializedName("createdBy")
    var createdBy: Int? = null

    @SerializedName("createdOn")
    var createdOn: Date? = null

    @SerializedName("modifiedOn")
    var modifiedOn: Date? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("fullName")
    var fullName: String? = null

    @SerializedName("invitationSent")
    var invitationSent :Boolean = false

    @SerializedName("addressVerified")
    var addressVerified: Boolean = false

    @SerializedName("isRegistered")
    var isRegistered: Boolean = false

    fun getAllMembers(): List<Family> {
        val familys = mutableListOf<Family>()
        val family1 = Family()
        family1.fullName = "Fidel Martin"
        family1.addressVerified = true
        family1.isRegistered = true
        family1.invitationSent = true
        familys.add(family1)

        val family2 = Family()
        family2.fullName = "Jules Boutin"
        family2.addressVerified = false
        family2.isRegistered = true
        family2.invitationSent = true
        familys.add(family2)

        val family3 = Family()
        family3.fullName = "Henry Jurk"
        family3.addressVerified = false
        family3.isRegistered = false
        family3.invitationSent = true
        familys.add(family3)

        val family4 = Family()
        family4.fullName = "John Klok"
        family4.addressVerified = false
        family4.isRegistered = false
        family4.invitationSent = false
        familys.add(family4)

        return familys
    }
}
