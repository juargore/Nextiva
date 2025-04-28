package com.nextiva.nextivaapp.android.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.util.DbConstants
import java.io.Serializable

@Entity(tableName = DbConstants.TABLE_NAME_EMAILS,
        indices = [Index(DbConstants.EMAILS_COLUMN_NAME_CONTACT_ID)],
        foreignKeys = [ForeignKey(entity = DbContact::class,
                parentColumns = [DbConstants.CONTACTS_COLUMN_NAME_ID],
                childColumns = [DbConstants.EMAILS_COLUMN_NAME_CONTACT_ID],
                onDelete = ForeignKey.CASCADE)])

data class EmailAddress(@PrimaryKey(autoGenerate = true)
                        @ColumnInfo(name = DbConstants.EMAILS_COLUMN_NAME_ID) var id: Long?,
                        @ColumnInfo(name = DbConstants.EMAILS_COLUMN_NAME_CONTACT_ID) var contactId: Int?,
                        @ColumnInfo(name = DbConstants.EMAILS_COLUMN_NAME_ADDRESS) var address: String?,
                        @ColumnInfo(name = DbConstants.EMAILS_COLUMN_NAME_TYPE) @Enums.Contacts.EmailTypes.Type var type: Int,
                        @ColumnInfo(name = DbConstants.EMAILS_COLUMN_NAME_LABEL) var label: String?,
                        @ColumnInfo(name = DbConstants.EMAILS_COLUMN_NAME_TRANSACTION_ID) var transactionId: String?) : Serializable {
    @Ignore
    constructor() : this(null, null, null, Enums.Contacts.EmailTypes.OTHER_EMAIL, null, null)

    @Ignore
    constructor(emailAddress: EmailAddress?) : this(null, emailAddress?.contactId, emailAddress?.address, emailAddress?.type
            ?: Enums.Contacts.EmailTypes.OTHER_EMAIL, emailAddress?.label, emailAddress?.transactionId)

    @Ignore
    constructor(type: Int, address: String?, label: String?) : this(null, null, address, type, label, null)
}