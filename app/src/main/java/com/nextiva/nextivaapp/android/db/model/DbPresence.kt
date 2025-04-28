package com.nextiva.nextivaapp.android.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.util.DbConstants
import java.io.Serializable

@Entity(
    tableName = DbConstants.TABLE_NAME_PRESENCES,
    indices = [Index(DbConstants.PRESENCES_COLUMN_NAME_CONTACT_ID)],
    foreignKeys = [ForeignKey(
        entity = DbContact::class,
        parentColumns = [DbConstants.CONTACTS_COLUMN_NAME_ID],
        childColumns = [DbConstants.PRESENCES_COLUMN_NAME_CONTACT_ID],
        onDelete = ForeignKey.CASCADE
    )]
)

data class DbPresence(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DbConstants.PRESENCES_COLUMN_NAME_ID) var id: Long?,
    @ColumnInfo(name = DbConstants.PRESENCES_COLUMN_NAME_CONTACT_ID) var contactId: Int?,
    @ColumnInfo(name = DbConstants.PRESENCES_COLUMN_NAME_USER_ID) var userId: String?,
    @ColumnInfo(name = DbConstants.PRESENCES_COLUMN_NAME_JID) var jid: String?,
    @ColumnInfo(name = DbConstants.PRESENCES_COLUMN_NAME_PRESENCE_STATE) var state: Int,
    @ColumnInfo(name = DbConstants.PRESENCES_COLUMN_NAME_PRESENCE_TYPE) var type: Int?,
    @ColumnInfo(name = DbConstants.PRESENCES_COLUMN_NAME_PRIORITY) var priority: Int?,
    @ColumnInfo(name = DbConstants.PRESENCES_COLUMN_NAME_STATUS_TEXT) var status: String?,
    @ColumnInfo(name = DbConstants.PRESENCES_COLUMN_NAME_STATUS_EXPIRY_TIME) var statusExpiryTime: String?,
    @ColumnInfo(name = DbConstants.PRESENCES_COLUMN_NAME_TRANSACTION_ID) var transactionId: String?,
    @ColumnInfo(name = DbConstants.PRESENCES_COLUMN_NAME_IN_CALL) var inCall: Boolean?
) : Serializable {
    @Ignore
    constructor() : this(
        null,
        null,
        null,
        null,
        Enums.Contacts.PresenceStates.NONE,
        Enums.Contacts.PresenceTypes.NONE,
        Constants.PRESENCE_OFFLINE_PRIORITY,
        null,
        null,
        null,
        null
    )

    @Ignore
    constructor(contactId: Int?, nextivaPresence: DbPresence, transactionId: String?) : this(
        null,
        contactId,
        null,
        nextivaPresence.jid,
        nextivaPresence.state,
        nextivaPresence.type,
        nextivaPresence.priority,
        nextivaPresence.status,
        nextivaPresence.statusExpiryTime,
        transactionId,
        nextivaPresence.inCall
    )



    @Ignore
    constructor(nextivaPresence: DbPresence) : this(
        null,
        null,
        null,
        nextivaPresence.jid,
        nextivaPresence.state,
        nextivaPresence.type,
        nextivaPresence.priority,
        nextivaPresence.status,
        nextivaPresence.statusExpiryTime,
        null,
        nextivaPresence.inCall
    )

    @Ignore
    constructor(jid: String?, state: Int, priority: Int, status: String?, type: Int) : this(
        null,
        null,
        null,
        jid,
        state,
        type,
        priority,
        status,
        null,
        null,
        null
    )

    @Ignore
    constructor(contactId: Int?, userId: String?, state: Int, status: String?) : this(
        null,
        contactId,
        userId,
        null,
        state,
        null,
        null,
        status,
        null,
        null,
        null
    )

    @Ignore
    constructor(state: Int, status: String?) : this(
        null,
        null,
        null,
        null,
        state,
        null,
        null,
        status,
        null,
        null,
        null
    )

    @Ignore
    constructor(contactId: Int?, userId: String?, state: Int, status: String?, statusExpiryTime: String?, inCall: Boolean?) : this(
        null,
        contactId,
        userId,
        null,
        state,
        null,
        null,
        status,
        statusExpiryTime,
        null,
        inCall
    )

    val availability: String?
        get() {
            return when (state) {
                Enums.Contacts.PresenceStates.AVAILABLE -> Enums.Contacts.BroadsoftPresenceState.AVAILABLE
                Enums.Contacts.PresenceStates.AWAY -> Enums.Contacts.BroadsoftPresenceState.AWAY
                Enums.Contacts.PresenceStates.BUSY -> Enums.Contacts.BroadsoftPresenceState.BUSY
                else -> Enums.Contacts.BroadsoftPresenceState.OFFLINE
            }
        }

    val humanReadablePresenceText: String
        get() {
            val readStatus = status ?: ""
            val presenceText = readStatus.ifEmpty {
                when (state) {
                    Enums.Contacts.PresenceStates.AVAILABLE -> Enums.Contacts.PresenceStateText.ONLINE
                    Enums.Contacts.PresenceStates.CONNECT_ONLINE,
                    Enums.Contacts.PresenceStates.CONNECT_ACTIVE -> Enums.Contacts.PresenceStateText.AVAILABLE
                    Enums.Contacts.PresenceStates.AWAY,
                    Enums.Contacts.PresenceStates.CONNECT_AWAY,
                    Enums.Contacts.PresenceStates.CONNECT_BE_RIGHT_BACK -> Enums.Contacts.PresenceStateText.AWAY
                    Enums.Contacts.PresenceStates.CONNECT_DND -> Enums.Contacts.PresenceStateText.DND
                    Enums.Contacts.PresenceStates.BUSY,
                    Enums.Contacts.PresenceStates.CONNECT_BUSY,
                    Enums.Contacts.PresenceStates.CONNECT_OUT_OF_OFFICE -> if (priority == Constants.PRESENCE_ON_CALL_PRIORITY) Enums.Contacts.PresenceStateText.CALL else Enums.Contacts.PresenceStateText.BUSY
                    Enums.Contacts.PresenceStates.PENDING -> Enums.Contacts.PresenceStateText.PENDING
                    else -> Enums.Contacts.PresenceStateText.OFFLINE
                }
            }
            var prefix = ""
            var postfix = " "
            if (inCall == true) {
                if (readStatus.isEmpty()) {
                    postfix = " - ${Enums.Contacts.PresenceStateText.ON_A_CALL} "
                } else {
                    prefix = "${Enums.Contacts.PresenceStateText.ON_A_CALL} - "
                }
            }
            return prefix + presenceText + postfix
        }

    fun setConnectState(newState: Int) {
       state = when (newState) {
            Enums.Contacts.ConnectPresenceStates.ONLINE -> Enums.Contacts.PresenceStates.CONNECT_ONLINE
            Enums.Contacts.ConnectPresenceStates.ACTIVE -> Enums.Contacts.PresenceStates.CONNECT_ACTIVE
            Enums.Contacts.ConnectPresenceStates.DND -> Enums.Contacts.PresenceStates.CONNECT_DND
            Enums.Contacts.ConnectPresenceStates.AWAY -> Enums.Contacts.PresenceStates.CONNECT_AWAY
            Enums.Contacts.ConnectPresenceStates.BUSY -> Enums.Contacts.PresenceStates.CONNECT_BUSY
            Enums.Contacts.ConnectPresenceStates.BE_RIGHT_BACK -> Enums.Contacts.PresenceStates.CONNECT_BE_RIGHT_BACK
            Enums.Contacts.ConnectPresenceStates.OUT_OF_OFFICE -> Enums.Contacts.PresenceStates.CONNECT_OUT_OF_OFFICE
            else -> Enums.Contacts.PresenceStates.CONNECT_OFFLINE
        }
    }
}