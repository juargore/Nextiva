package com.nextiva.nextivaapp.android.features.rooms.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ConnectRoom(@SerializedName("id") var id: String?,
                       @SerializedName("name") var name: String?,
                       @SerializedName("description") var description: String?,
                       @SerializedName("archived") var archived: Boolean?,
                       @SerializedName("admins") var admins: ArrayList<ConnectRoomAdmin>?,
                       @SerializedName("members") var members: ArrayList<ConnectRoomMember>?,
                       @SerializedName("recentActivity") var recentActivity: ConnectRoomRecentActivity?,
                       @SerializedName("createdTime") var createdTime: String?,
                       @SerializedName("createdBy") var createdBy: String?,
                       @SerializedName("lastModifiedTime") var lastModifiedTime: String?,
                       @SerializedName("lastModifiedBy") var lastModifiedBy: String?,
                       @SerializedName("type") var type: String?,
                       @SerializedName("requestor") var requestor: ConnectRoomMember?,
                       @SerializedName("archivedTime") var archivedTime: String?,
                       @SerializedName("archivedBy") var archivedBy: String?,
                       @SerializedName("unarchivedTime") var unarchivedTime: String?,
                       @SerializedName("unarchivedBy") var unarchivedBy: String?,
                       @SerializedName("corpId") var corpId: String?,
                       @SerializedName("favoriteInteractionError") var favoriteInteractionError: String?,
                       @SerializedName("locked") var locked: Boolean?,
                       @SerializedName("mediaCallMetaData") var mediaCallMetaData: ConnectRoomMediaCallMetadata?,
                       @SerializedName("ownerId") var ownerId: String?
                       ) : Serializable {

    constructor(members: ArrayList<ConnectRoomMember>, type: String) : this(
        id = null,
        name = null,
        description = null,
        archived = null,
        admins = null,
        members = members,
        recentActivity = null,
        createdTime = null,
        createdBy = null,
        lastModifiedTime = null,
        lastModifiedBy = null,
        type = type,
        requestor = null,
        archivedTime = null,
        archivedBy = null,
        unarchivedTime = null,
        unarchivedBy = null,
        corpId = null,
        favoriteInteractionError = null,
        locked = null,
        mediaCallMetaData = null,
        ownerId = null
    )

}
