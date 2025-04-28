package com.nextiva.nextivaapp.android.features.rooms.api

import com.google.gson.annotations.SerializedName
import com.nextiva.nextivaapp.android.models.NextivaContact
import java.io.Serializable

data class ConnectRoomMember(
        @SerializedName("userUuid") var userUuid: String?,
        @SerializedName("muteNotifications") var muteNotifications: Boolean?,
        @SerializedName("favorite") var favorite: Boolean?,
        @SerializedName("hideRoom") var hideRoom: Boolean?,
        @SerializedName("firstName") var firstName: String?,
        @SerializedName("lastName") var lastName: String?,
        @SerializedName("email") var email: String?,
        @SerializedName("guest") var guest: Boolean?,
        @SerializedName("unreadMessageCount") var unreadMessageCount: Int?,
        @SerializedName("disabled") var disabled: Boolean?,
        @SerializedName("deactivated") var deactivated: Boolean?,
        @SerializedName("displayName") var displayName: String?,
        @SerializedName("photoUrl") var photoUrl: String?,
        @SerializedName("deleted") var deleted: Boolean?,
        @SerializedName("missingContactInfo") var missingContactInfo: Boolean?,
        @SerializedName("workPhone") var workPhone: String?,
        @SerializedName("workPhoneExt") var workPhoneExt: String?
): Serializable {

        constructor(nextivaContact: NextivaContact) : this(
                userUuid = nextivaContact.userId,
                muteNotifications = null,
                favorite = null,
                hideRoom = null,
                firstName = nextivaContact.firstName,
                lastName = nextivaContact.lastName,
                email = null,
                guest = null,
                unreadMessageCount = null,
                disabled = null,
                deactivated = null,
                displayName = nextivaContact.displayName,
                photoUrl = null,
                deleted = null,
                missingContactInfo = null,
                workPhone = null,
                workPhoneExt = null
        )

}