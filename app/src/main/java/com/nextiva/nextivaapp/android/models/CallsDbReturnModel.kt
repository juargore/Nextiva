package com.nextiva.nextivaapp.android.models

import androidx.room.ColumnInfo
import androidx.room.Relation
import com.nextiva.nextivaapp.android.db.model.DbCallLogEntry
import com.nextiva.nextivaapp.android.db.model.DbContact
import com.nextiva.nextivaapp.android.db.model.DbVoicemail
import com.nextiva.nextivaapp.android.db.util.DbConstants
import com.nextiva.nextivaapp.android.util.CallUtil

data class CallsDbReturnModel(@ColumnInfo(name = DbConstants.CALL_LOGS_COLUMN_NAME_CALL_LOG_ID) var callLogId: String?,
                              @ColumnInfo(name = DbConstants.CALL_LOGS_COLUMN_NAME_DISPLAY_NAME) private var displayName: String?,
                              @ColumnInfo(name = DbConstants.CALL_LOGS_COLUMN_NAME_CALL_DATE_TIME) private var callDateTime: Long?,
                              @ColumnInfo(name = DbConstants.CALL_LOGS_COLUMN_NAME_CALL_START_TIME) private var callStartTime: String?,
                              @ColumnInfo(name = DbConstants.CALL_LOGS_COLUMN_NAME_COUNTRY_CODE) private var countryCode: String?,
                              @ColumnInfo(name = DbConstants.CALL_LOGS_COLUMN_NAME_PHONE_NUMBER) private var phoneNumber: String?,
                              @ColumnInfo(name = DbConstants.CALL_LOGS_COLUMN_NAME_CALL_TYPE) private var callType: String?,
                              @ColumnInfo(name = DbConstants.CALL_LOGS_COLUMN_NAME_IS_READ) private var isRead: Int?,
                              @ColumnInfo(name = DbConstants.CALL_LOGS_COLUMN_NAME_CONTACT_ID) private var callWithContactId: String?,
                              @Relation(parentColumn = DbConstants.CALL_LOGS_COLUMN_NAME_CONTACT_ID,
                                  entityColumn = DbConstants.CONTACTS_COLUMN_NAME_CONTACT_TYPE_ID,
                                  entity = DbContact::class)
                              private var callWithContacts: List<ConnectContactStripped>?,
                              @ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_DURATION) private var duration: Int?,
                              @ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_ADDRESS) private var address: String?,
                              @ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_NAME) private var name: String?,
                              @ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_USER_ID) private var userId: String?,
                              @ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_MESSAGE_ID) var messageId: String?,
                              @ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_READ) private var read: Boolean?,
                              @ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_TRANSCRIPTION) private var transcription: String?,
                              @ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_RATING) private var rating: String?,
                              @ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_FORMATTED_PHONE_NUMBER) private var formattedPhoneNumber: String?,
                              @ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_ACTUAL_ID) private var actualVoicemailId: String?,
                              @ColumnInfo(name = DbConstants.VOICEMAIL_COLUMN_NAME_CALLER_ID) private var callerId: String?,
                              @Relation(parentColumn = DbConstants.VOICEMAIL_COLUMN_NAME_USER_ID,
                                  entityColumn = DbConstants.CONTACTS_COLUMN_NAME_CONTACT_TYPE_ID,
                                  entity = DbContact::class)
                              private var voicemailFromContacts: List<ConnectContactStripped>?,
                              private var time: Long?) {

    val callLogEntry: CallLogEntry?
        get() {
            if (callLogId.isNullOrEmpty()) {
                return null
            }

            return DbCallLogEntry(
                null,
                callLogId,
                displayName,
                callDateTime,
                countryCode,
                phoneNumber,
                CallUtil.phoneNumberFormatNumberDefaultCountry(phoneNumber),
                callType,
                isRead,
                null,
                null,
                duration,
                callStartTime ?: ""
            ).toCallLogEntry(null, callWithContacts?.firstOrNull())
        }

    val voicemail: Voicemail?
        get() {
            if (messageId.isNullOrEmpty()) {
                return null
            }

            return DbVoicemail(null,
                    duration,
                    address,
                    name,
                    userId,
                    time,
                    messageId,
                    read,
                    transcription,
                    rating,
                    CallUtil.phoneNumberFormatNumberDefaultCountry(address?.let { CallUtil.cleanPhoneNumberAndRemoveUSCountryCode(it) }),
                    actualVoicemailId,
                    null,
                null,
                    callerId).toVoicemail(null, voicemailFromContacts?.firstOrNull())
        }
}