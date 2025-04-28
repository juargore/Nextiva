package com.nextiva.nextivaapp.android.models

import androidx.room.ColumnInfo
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.util.DbConstants
import org.threeten.bp.Instant
import java.io.Serializable

data class Voicemail(
        @field:ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_ID) var voicemailId: String?,
        @field:ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_DURATION) var duration: Int?,
        @field:ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_ADDRESS) var address: String?,
        @field:ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_NAME) var name: String?,
        @field:ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_USER_ID) var userId: String?,
        @field:ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_READ) var isRead: Boolean? = false,
        @field:ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_TIME) var time: Long?,
        @field:ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_MESSAGE_ID) var messageId: String?,
        @field:ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_TRANSCRIPTION) var transcription: String?,
        @field:ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_RATING) var rating: String?,
        @field:ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_ACTUAL_ID) var actualVoiceMailId: String?,
        @field:ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_FORMATTED_PHONE_NUMBER) var formattedPhoneNumber: String?,
        @field:ColumnInfo(name = DbConstants.VCARDS_COLUMN_NAME_PHOTO_DATA) var avatar: ByteArray?,
        @field:ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_UI_NAME) var uiName: String?,
        @field:ColumnInfo(name = DbConstants.PRESENCES_COLUMN_NAME_PRESENCE_STATE) @get:Enums.Contacts.PresenceStates.PresenceState
        @param:Enums.Contacts.PresenceStates.PresenceState var presenceState: Int,
        @field:ColumnInfo(name = DbConstants.PRESENCES_COLUMN_NAME_PRIORITY) var presencePriority: Int,
        @field:ColumnInfo(name = DbConstants.PRESENCES_COLUMN_NAME_STATUS_TEXT) var statusText: String?,
        @field:ColumnInfo(name = DbConstants.PRESENCES_COLUMN_NAME_PRESENCE_TYPE) @get:Enums.Contacts.PresenceTypes.Type
        @param:Enums.Contacts.PresenceTypes.Type var presenceType: Int,
        @field:ColumnInfo(name = DbConstants.PRESENCES_COLUMN_NAME_JID) var jid: String?,
        @field:ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_CALLER_ID) var callerId: String?,
        @field:ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_CONTACT_TYPE) var contactType: Int) : Serializable {

    val humanReadableName: String?
        get() {
            return when {
                !uiName.isNullOrEmpty() -> uiName
                !name.isNullOrEmpty() -> name
                !formattedPhoneNumber.isNullOrEmpty() -> formattedPhoneNumber
                else -> null
            }
        }

    val voicemailInstant: Instant?
        get() {
            time?.let { return Instant.ofEpochMilli(it) }
            return null
    }

    fun getCreateContactFirstName(): String? {
        return uiName?.let {
            it.split(Regex("\\s+"), 2).firstOrNull()

        } ?: name?.let {
            it.split(Regex("\\s+"), 2).firstOrNull()
        }
    }

    fun getCreateContactLastName(): String? {
        return uiName?.let {
            it.split(Regex("\\s+"), 2).getOrNull(1)

        } ?: name?.let {
            it.split(Regex("\\s+"), 2).getOrNull(1)
        }
    }
}