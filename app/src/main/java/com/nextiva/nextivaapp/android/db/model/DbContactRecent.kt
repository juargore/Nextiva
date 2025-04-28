package com.nextiva.nextivaapp.android.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.nextiva.nextivaapp.android.db.util.DbConstants
import com.nextiva.nextivaapp.android.models.NextivaContact

@Entity(tableName = DbConstants.TABLE_NAME_CONTACTS_RECENT)
data class DbContactRecent(@PrimaryKey(autoGenerate = true) var id: Long?,
                           @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_CONTACT_TYPE_ID) var contactTypeId: String?,
                           @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_TRANSACTION_ID) var transactionId: String?
) {

    @Ignore
    constructor() : this(null, null, null)

    @Ignore
    constructor(nextivaContact: NextivaContact, transactionId: String?) : this(
            null,
            nextivaContact.userId,
            transactionId)
}