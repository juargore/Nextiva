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

@Entity(tableName = DbConstants.TABLE_NAME_DATES,
        indices = [Index(DbConstants.DATE_COLUMN_NAME_CONTACT_ID)],
        foreignKeys = [ForeignKey(entity = DbContact::class,
                parentColumns = [DbConstants.CONTACTS_COLUMN_NAME_ID],
                childColumns = [DbConstants.DATE_COLUMN_NAME_CONTACT_ID],
                onDelete = ForeignKey.CASCADE)])
data class DbDate(@PrimaryKey(autoGenerate = true)
                  @ColumnInfo(name = DbConstants.DATE_COLUMN_NAME_ID) var id: Long?,
                  @ColumnInfo(name = DbConstants.DATE_COLUMN_NAME_CONTACT_ID) var contactId: Int?,
                  @ColumnInfo(name = DbConstants.DATE_COLUMN_NAME_DATE) var date: String?,
                  @ColumnInfo(name = DbConstants.DATE_COLUMN_NAME_TYPE) @Enums.Contacts.DateType.Type var type: Int?,
                  @ColumnInfo(name = DbConstants.DATE_COLUMN_NAME_TRANSACTION_ID) var transactionId: String?) : Serializable {

    @Ignore
    constructor() : this(null, null, null, Enums.Contacts.DateType.OTHER, null)

    @Ignore
    constructor(date: DbDate?) : this(null, date?.contactId, date?.date, date?.type
            ?: Enums.Contacts.DateType.OTHER, date?.transactionId)

    @Ignore
    constructor(type: Int, date: String?) : this(null, null, date, type, null)
}