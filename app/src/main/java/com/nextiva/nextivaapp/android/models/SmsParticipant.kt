package com.nextiva.nextivaapp.android.models

import androidx.room.ColumnInfo
import androidx.room.Ignore
import androidx.room.Relation
import com.nextiva.nextivaapp.android.db.model.DbContact
import com.nextiva.nextivaapp.android.db.model.DbPresence
import com.nextiva.nextivaapp.android.db.model.SmsTeam
import com.nextiva.nextivaapp.android.db.util.DbConstants
import com.nextiva.nextivaapp.android.util.extensions.nullIfEmpty
import java.io.Serializable

data class SmsParticipant(@ColumnInfo(name = DbConstants.PARTICIPANT_COLUMN_NAME_NAME) var name: String?,
                          @ColumnInfo(name = DbConstants.PARTICIPANT_COLUMN_NAME_EMAIl) var emailId: String?,
                          @ColumnInfo(name = DbConstants.PARTICIPANT_COLUMN_NAME_PHONE_NUMBER) var phoneNumber: String?,
                          @ColumnInfo(name = DbConstants.PARTICIPANT_COLUMN_NAME_USER_UUID) var userUUID: String?,
                          @ColumnInfo(name = DbConstants.PARTICIPANT_COLUMN_NAME_TEAM_UUID) private var teamUUID: String?,
                          @Relation(parentColumn = DbConstants.PARTICIPANT_COLUMN_NAME_USER_UUID,
                                  entityColumn = DbConstants.CONTACTS_COLUMN_NAME_CONTACT_TYPE_ID,
                                  entity = DbContact::class)
                          private var contacts: List<ConnectContactStripped>?) : Serializable {

    @Ignore
    constructor(phoneNumber: String): this(null, null, phoneNumber, null, null, null)

    @Ignore
    constructor(phoneNumber: String, userUuid: String?): this(null, null, phoneNumber, userUuid, null, null)

    @Ignore
    var contact: NextivaContact? = null
    get() { return field ?: contacts?.firstOrNull()?.toNextivaContact() }

    @Ignore
    var representingTeam: SmsTeam? = null

    val uiName: String?
    get() {
        return contacts?.firstOrNull()?.uiName ?: contacts?.firstOrNull()?.displayName ?: name
    }

    val teamUuids: List<String>?
    get() {
        return teamUUID?.nullIfEmpty()?.split(",")
    }

    val presence: DbPresence?
    get() {
        return contacts?.firstOrNull()?.presence
    }

    val photoData: ByteArray?
        get() {
            return contacts?.firstOrNull()?.photoData
        }

    val uiFirstName: String?
    get() {
        return contacts?.firstOrNull()?.firstName
    }

    val uiLastName: String?
    get(){
        return contacts?.firstOrNull()?.lastName
    }
}