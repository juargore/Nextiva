package com.nextiva.nextivaapp.android.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.util.DbConstants
import com.nextiva.nextivaapp.android.models.ConnectContactStripped
import com.nextiva.nextivaapp.android.models.Voicemail

@Entity(tableName = DbConstants.TABLE_NAME_VOICEMAILS,
    indices = [Index(value = [DbConstants.VOICEMAIL_COLUMN_NAME_ACTUAL_ID], unique = true)])
data class DbVoicemail(@PrimaryKey(autoGenerate = true)
                       @ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_ID) var id: Long?,
                       @ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_DURATION) var duration: Int?,
                       @ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_ADDRESS) var address: String?,
                       @ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_NAME) var name: String?,
                       @ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_USER_ID) var userId: String?,
                       @ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_TIME) var time: Long?,
                       @ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_MESSAGE_ID) var messageId: String?,
                       @ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_READ) var read: Boolean?,
                       @ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_TRANSCRIPTION) var transcription: String?,
                       @ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_RATING) var rating: String?,
                       @ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_FORMATTED_PHONE_NUMBER) var formatted_phone_number: String?,
                       @ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_ACTUAL_ID) var actualVoicemailId: String?,
                       @ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_TRANSACTION_ID) var transactionId: String?,
                       @ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_PAGE_NUMBER) var pageNumber: Int?,
                       @ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_CALLER_ID) var callerId: String?) {

    fun toVoicemail(uiName: String?, connectContactStripped: ConnectContactStripped?): Voicemail {
        return Voicemail(id.toString(),
                duration,
                address,
                name,
                userId,
                read,
                time,
                messageId,
                transcription,
                rating,
                actualVoicemailId,
                formatted_phone_number,
                null,
                uiName,
                connectContactStripped?.presence?.state ?: Enums.Contacts.PresenceStates.NONE,
                connectContactStripped?.presence?.priority ?: -128,
                connectContactStripped?.presence?.status,
                connectContactStripped?.contactType ?: Enums.Contacts.ContactTypes.NONE,
                null,
                callerId,
                connectContactStripped?.presence?.type ?: Enums.Contacts.PresenceTypes.NONE)
    }
}