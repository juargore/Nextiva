package com.nextiva.nextivaapp.android.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import com.nextiva.nextivaapp.android.db.util.DbConstants
import java.io.Serializable

@Entity(tableName = DbConstants.TABLE_NAME_GROUP_RELATIONS,
        primaryKeys = [DbConstants.GROUPS_RELATION_COLUMN_NAME_CONTACT_ID, DbConstants.GROUPS_RELATION_COLUMN_NAME_GROUP_ID])
data class DbGroupRelation(@ColumnInfo(name = DbConstants.GROUPS_RELATION_COLUMN_NAME_CONTACT_ID) var contactId: Int,
                           @ColumnInfo(name = DbConstants.GROUPS_RELATION_COLUMN_NAME_GROUP_ID) var groupId: String,
                           @ColumnInfo(name = DbConstants.GROUPS_COLUMN_NAME_TRANSACTION_ID) var transactionId: String?) : Serializable {
    @Ignore
    constructor() : this(0, "", null)
}