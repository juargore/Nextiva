package com.nextiva.nextivaapp.android.features.rooms.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.nextiva.nextivaapp.android.features.rooms.RoomsEnums
import com.nextiva.nextivaapp.android.features.rooms.api.ConnectRoom
import com.nextiva.nextivaapp.android.features.rooms.api.ConnectRoomAdmin
import com.nextiva.nextivaapp.android.features.rooms.api.ConnectRoomMediaCallMetadata
import com.nextiva.nextivaapp.android.features.rooms.api.ConnectRoomMember
import com.nextiva.nextivaapp.android.features.rooms.db.DbConstants

@Entity(tableName = DbConstants.TABLE_NAME_ROOMS)
data class DbRoom(@PrimaryKey @ColumnInfo(name = DbConstants.ROOM_COLUMN_NAME_ROOM_ID) var roomId: String,
                  @ColumnInfo(name = DbConstants.ROOM_COLUMN_NAME_NAME) var name: String?,
                  @ColumnInfo(name = DbConstants.ROOM_COLUMN_NAME_DESCRIPTION) var description: String?,
                  @ColumnInfo(name = DbConstants.ROOM_COLUMN_NAME_ARCHIVED) var archived: String?,
                  @ColumnInfo(name = DbConstants.ROOM_COLUMN_NAME_ADMINS) var admins: List<ConnectRoomAdmin>?,
                  @ColumnInfo(name = DbConstants.ROOM_COLUMN_NAME_MEMBERS) var members: List<ConnectRoomMember>?,
                  @ColumnInfo(name = DbConstants.ROOM_COLUMN_NAME_RECENT_ACTIVITY_TYPE) var recentActivityType: String?,
                  @ColumnInfo(name = DbConstants.ROOM_COLUMN_NAME_RECENT_ACTIVITY_TIMESTAMP) var recentActivityTimestamp: String?,
                  @ColumnInfo(name = DbConstants.ROOM_COLUMN_NAME_CREATED_BY) var createdBy: String?,
                  @ColumnInfo(name = DbConstants.ROOM_COLUMN_NAME_CREATED_TIME) var createdTime: String?,
                  @ColumnInfo(name = DbConstants.ROOM_COLUMN_NAME_LAST_MODIFIED_TIME) var lastModifiedTime: String?,
                  @ColumnInfo(name = DbConstants.ROOM_COLUMN_NAME_LAST_MODIFIED_BY) var lastModifiedBy: String?,
                  @ColumnInfo(name = DbConstants.ROOM_COLUMN_NAME_TYPE) var type: String?,
                  @ColumnInfo(name = DbConstants.ROOM_COLUMN_NAME_REQUESTER_USER_ID) var requestorID: String?,
                  @ColumnInfo(name = DbConstants.ROOM_COLUMN_NAME_REQUESTER_MUTE_NOTIFICATIONS) var requestorMuteNotifications: Boolean?,
                  @ColumnInfo(name = DbConstants.ROOM_COLUMN_NAME_REQUESTER_FAVORITE) var requestorFavorite: Boolean?,
                  @ColumnInfo(name = DbConstants.ROOM_COLUMN_NAME_REQUESTER_HIDE_ROOM) var requestorHideRoom: Boolean?,
                  @ColumnInfo(name = DbConstants.ROOM_COLUMN_NAME_ARCHIVED_BY) var archivedBy: String?,
                  @ColumnInfo(name = DbConstants.ROOM_COLUMN_NAME_ARCHIVED_TIME) var archivedTime: String?,
                  @ColumnInfo(name = DbConstants.ROOM_COLUMN_NAME_UNARCHIVED_BY) var unarchivedBy: String?,
                  @ColumnInfo(name = DbConstants.ROOM_COLUMN_NAME_UNARCHIVED_TIME) var unarchivedTime: String?,
                  @ColumnInfo(name = DbConstants.ROOM_COLUMN_NAME_CORP_ID) var corpId: String?,
                  @ColumnInfo(name = DbConstants.ROOM_COLUMN_NAME_FAVORITE_INTERACTION_ERROR) var favoriteInteractionError: String?,
                  @ColumnInfo(name = DbConstants.ROOM_COLUMN_NAME_LOCKED) var locked: Boolean?,
                  @ColumnInfo(name = DbConstants.ROOM_COLUMN_NAME_MEDIA_CALL_META_DATA) var mediaCallMetaData: ConnectRoomMediaCallMetadata?,
                  @ColumnInfo(name = DbConstants.ROOM_COLUMN_NAME_UNREAD_MESSAGE_COUNT) var unreadMessageCount: Int?,
                  @ColumnInfo(name = DbConstants.ROOM_COLUMN_NAME_OWNER_ID) var ownerId: String?
                  ) {

    @Ignore
    constructor(connectRoom: ConnectRoom) : this(
            name = connectRoom.name,
            admins = connectRoom.admins,
            members = connectRoom.members,
            archived = connectRoom.archived.toString(),
            archivedBy = connectRoom.archivedBy,
            corpId = connectRoom.corpId,
            createdBy = connectRoom.createdBy,
            createdTime = connectRoom.createdTime,
            favoriteInteractionError = connectRoom.favoriteInteractionError,
            roomId = connectRoom.id!!,
            locked = connectRoom.locked,
            recentActivityType = connectRoom.recentActivity?.type,
            recentActivityTimestamp = connectRoom.recentActivity?.timestamp,
            lastModifiedTime = connectRoom.lastModifiedTime,
            lastModifiedBy = connectRoom.lastModifiedBy,
            requestorID = connectRoom.requestor?.userUuid,
            requestorMuteNotifications = connectRoom.requestor?.muteNotifications,
            requestorFavorite = connectRoom.requestor?.favorite,
            requestorHideRoom = connectRoom.requestor?.hideRoom,
            unarchivedBy = connectRoom.unarchivedBy,
            unarchivedTime = connectRoom.unarchivedTime,
            description = connectRoom.description,
            type = connectRoom.type,
            archivedTime = connectRoom.archivedTime,
            mediaCallMetaData = connectRoom.mediaCallMetaData,
            unreadMessageCount = connectRoom.requestor?.unreadMessageCount,
            ownerId = connectRoom.ownerId
    )

    fun typeEnum() : RoomsEnums.ConnectRoomsTypes {
        type?.let {
            try {
                return RoomsEnums.ConnectRoomsTypes.valueOf(it)
            } catch (e: Exception) {
                // The 'type' value is not defined in the Enum.  Return UNKNOWN.
            }
        }
        return RoomsEnums.ConnectRoomsTypes.UNKNOWN
    }

    fun isMember(memberId: String) : Boolean {
        return members?.firstOrNull { it.userUuid == memberId } != null
    }

    fun isChat() : Boolean {
        return typeEnum() == RoomsEnums.ConnectRoomsTypes.INDIVIDUAL_CONVERSATION ||
                typeEnum() == RoomsEnums.ConnectRoomsTypes.GROUP_CONVERSATION ||
                typeEnum() == RoomsEnums.ConnectRoomsTypes.MY_CONVERSATION
    }
}