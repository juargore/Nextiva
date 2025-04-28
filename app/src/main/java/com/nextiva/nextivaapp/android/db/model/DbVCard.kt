package com.nextiva.nextivaapp.android.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nextiva.nextivaapp.android.db.util.DbConstants
import java.io.Serializable

@Entity(tableName = DbConstants.TABLE_NAME_VCARDS,
        indices = [Index(DbConstants.VCARDS_COLUMN_NAME_CONTACT_ID)],
        foreignKeys = [ForeignKey(entity = DbContact::class,
                parentColumns = [DbConstants.CONTACTS_COLUMN_NAME_ID],
                childColumns = [DbConstants.VCARDS_COLUMN_NAME_CONTACT_ID],
                onDelete = ForeignKey.CASCADE)])

data class DbVCard(@PrimaryKey(autoGenerate = true)
                   @ColumnInfo(name = DbConstants.VCARDS_COLUMN_NAME_ID) var id: Long?,
                   @ColumnInfo(name = DbConstants.VCARDS_COLUMN_NAME_CONTACT_ID) var contactId: Int?,
                   @ColumnInfo(name = DbConstants.VCARDS_COLUMN_NAME_PHOTO_DATA, typeAffinity = ColumnInfo.BLOB) var photoData: ByteArray?,
                   @ColumnInfo(name = DbConstants.VCARDS_COLUMN_NAME_CALL_ID) var callId: String?,
                   @ColumnInfo(name = DbConstants.VCARDS_COLUMN_NAME_SIP_URI) var sipUri: String?,
                   @ColumnInfo(name = DbConstants.VCARDS_COLUMN_NAME_TRANSACTION_ID) var transactionId: String?,
                   @Ignore var jid: String?) : Serializable {

    constructor() : this(null, null, null, null, null, null, null)

    constructor(jid: String?, photoData: ByteArray?) : this(null, null, photoData, null, null, null, jid)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DbVCard

        if (id != other.id) return false
        if (contactId != other.contactId) return false
        if (photoData != null) {
            if (other.photoData == null) return false
            other.photoData?.let { otherPhotoData ->
                photoData?.let { ourPhotoData ->
                    if (!ourPhotoData.contentEquals(otherPhotoData)) {
                        return false
                    }
                }
            }
        } else if (other.photoData != null) return false
        if (callId != other.callId) return false
        if (sipUri != other.sipUri) return false
        if (transactionId != other.transactionId) return false
        if (jid != other.jid) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (contactId ?: 0)
        result = 31 * result + (photoData?.contentHashCode() ?: 0)
        result = 31 * result + (callId?.hashCode() ?: 0)
        result = 31 * result + (sipUri?.hashCode() ?: 0)
        result = 31 * result + (transactionId?.hashCode() ?: 0)
        result = 31 * result + (jid?.hashCode() ?: 0)
        return result
    }
}