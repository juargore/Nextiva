/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.features.rooms.db

object DbConstants {

    const val DATABASE_NAME_ROOMS = "rooms_database"

    const val TABLE_NAME_ROOMS = "rooms"
    const val TABLE_NAME_CHAT_MESSAGES = "chat_messages"

    // Room
    const val ROOM_COLUMN_NAME_ID = "id"
    const val ROOM_COLUMN_NAME_ROOM_ID = "roomId"
    const val ROOM_COLUMN_NAME_CORP_ID = "corp_id"
    const val ROOM_COLUMN_NAME_CREATED_TIME = "created_time"
    const val ROOM_COLUMN_NAME_RECENT_ACTIVITY_TYPE = "recent_activity_type"
    const val ROOM_COLUMN_NAME_RECENT_ACTIVITY_TIMESTAMP = "recent_activity_timestamp"
    const val ROOM_COLUMN_NAME_CREATED_BY = "created_by"
    const val ROOM_COLUMN_NAME_LAST_MODIFIED_TIME = "last_modified_time"
    const val ROOM_COLUMN_NAME_MEMBERS = "members"
    const val ROOM_COLUMN_NAME_LAST_MODIFIED_BY = "last_modified_by"
    const val ROOM_COLUMN_NAME_REQUESTER_USER_ID = "requesterID"
    const val ROOM_COLUMN_NAME_REQUESTER_MUTE_NOTIFICATIONS = "requesterMuteNotifications"
    const val ROOM_COLUMN_NAME_REQUESTER_FAVORITE = "requesterFavorite"
    const val ROOM_COLUMN_NAME_REQUESTER_HIDE_ROOM = "requesterHideRoom"
    const val ROOM_COLUMN_NAME_ARCHIVED_BY = "archived_by"
    const val ROOM_COLUMN_NAME_UNARCHIVED_BY = "unarchived_by"
    const val ROOM_COLUMN_NAME_UNARCHIVED_TIME = "unarchived_time"
    const val ROOM_COLUMN_NAME_FAVORITE_INTERACTION_ERROR = "favorite_interaction_error"
    const val ROOM_COLUMN_NAME_LOCKED = "locked"
    const val ROOM_COLUMN_NAME_NAME = "name"
    const val ROOM_COLUMN_NAME_ARCHIVED = "archived"
    const val ROOM_COLUMN_NAME_ADMINS = "admins"
    const val ROOM_COLUMN_NAME_DESCRIPTION = "description"
    const val ROOM_COLUMN_NAME_TYPE = "type"
    const val ROOM_COLUMN_NAME_ARCHIVED_TIME = "archived_time"
    const val ROOM_COLUMN_NAME_MEDIA_CALL_META_DATA = "mediaCallMetaData"
    const val ROOM_COLUMN_NAME_UNREAD_MESSAGE_COUNT = "unreadMessageCount"
    const val ROOM_COLUMN_NAME_OWNER_ID= "ownerId"


    // Chat Message
    const val CHAT_MESSAGE_COLUMN_NAME_ID = "id"
    const val CHAT_MESSAGE_COLUMN_NAME_TYPE = "type"
    const val CHAT_MESSAGE_COLUMN_NAME_ROOM_ID = "room_id"
    const val CHAT_MESSAGE_COLUMN_NAME_CORP_ID = "corp_id"
    const val CHAT_MESSAGE_COLUMN_NAME_SENDER_ID = "sender_id"
    const val CHAT_MESSAGE_COLUMN_NAME_MENTIONS = "mentions"
    const val CHAT_MESSAGE_COLUMN_NAME_TEXT = "text"
    const val CHAT_MESSAGE_COLUMN_NAME_EDITED = "edited"
    const val CHAT_MESSAGE_COLUMN_NAME_DELIVERED = "delivered"
    const val CHAT_MESSAGE_COLUMN_NAME_READ = "read"
    const val CHAT_MESSAGE_COLUMN_NAME_TIMESTAMP = "timestamp"
    const val CHAT_MESSAGE_COLUMN_NAME_REACTIONS = "reactions"
    const val CHAT_MESSAGE_COLUMN_NAME_PARTICIPANTS = "participants"
    const val CHAT_MESSAGE_COLUMN_NAME_NON_MEMBER_MENTIONS = "non_member_mentions"
    const val CHAT_MESSAGE_COLUMN_NAME_HAS_THREAD = "has_thread"
    const val CHAT_MESSAGE_COLUMN_NAME_PARENT_MESSAGE_ID = "parent_message_id"
    const val CHAT_MESSAGE_COLUMN_NAME_ATTACHMENTS = "attachments"
}
