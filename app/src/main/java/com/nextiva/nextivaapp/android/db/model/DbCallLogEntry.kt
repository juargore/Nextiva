package com.nextiva.nextivaapp.android.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.util.DbConstants
import com.nextiva.nextivaapp.android.models.CallLogEntry
import com.nextiva.nextivaapp.android.models.ConnectContactStripped

@Entity(
    indices = [Index(value = [DbConstants.CALL_LOGS_COLUMN_NAME_CALL_LOG_ID], unique = true)],
    tableName = DbConstants.TABLE_NAME_CALL_LOG_ENTRIES
)
data class DbCallLogEntry(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DbConstants.CALL_LOGS_COLUMN_NAME_ID) var id: Long?,
    @ColumnInfo(name = DbConstants.CALL_LOGS_COLUMN_NAME_CALL_LOG_ID) var callLogId: String?,
    @ColumnInfo(name = DbConstants.CALL_LOGS_COLUMN_NAME_DISPLAY_NAME) var displayName: String?,
    @ColumnInfo(name = DbConstants.CALL_LOGS_COLUMN_NAME_CALL_DATE_TIME) var callDateTime: Long?,
    @ColumnInfo(name = DbConstants.CALL_LOGS_COLUMN_NAME_COUNTRY_CODE) var countryCode: String?,
    @ColumnInfo(name = DbConstants.CALL_LOGS_COLUMN_NAME_PHONE_NUMBER) var phoneNumber: String?,
    @ColumnInfo(name = DbConstants.CALL_LOGS_COLUMN_NAME_FORMATTED_PHONE_NUMBER) var formattedPhoneNumber: String?,
    @ColumnInfo(name = DbConstants.CALL_LOGS_COLUMN_NAME_CALL_TYPE) var callType: String?,
    @ColumnInfo(name = DbConstants.CALL_LOGS_COLUMN_NAME_IS_READ) var isRead: Int?,
    @ColumnInfo(name = DbConstants.CALL_LOGS_COLUMN_NAME_CONTACT_ID) var contactId: String?,
    @ColumnInfo(name = DbConstants.CALL_LOGS_COLUMN_NAME_PAGE_NUMBER) var pageNumber: Int?,
    @ColumnInfo(name = DbConstants.CALL_LOGS_COLUMN_NAME_CALL_DURATION) var callDuration: Int?,
    @ColumnInfo(name = DbConstants.CALL_LOGS_COLUMN_NAME_CALL_START_TIME) var callStartTime: String?
) {

    @Ignore
    constructor() : this(null, null, null, null, null, null, null, null, null, null, null, null, null)

    fun toCallLogEntry(uiName: String?, connectContactStripped: ConnectContactStripped?): CallLogEntry {
        val callLogEntry = CallLogEntry(
            callLogId,
            displayName,
            callDateTime,
            countryCode,
            phoneNumber,
            callType,
            null,
            uiName,
            connectContactStripped?.presence?.state ?: Enums.Contacts.PresenceStates.NONE,
            connectContactStripped?.presence?.priority ?: -128,
            connectContactStripped?.presence?.status,
            connectContactStripped?.contactType ?: Enums.Contacts.ContactTypes.NONE,
            null,
            connectContactStripped?.presence?.type ?: Enums.Contacts.PresenceTypes.NONE,
            callDuration ?: 0,
            callStartTime ?: ""
        )

        callLogEntry.setIsRead(isRead == 1)

        return callLogEntry
    }
}