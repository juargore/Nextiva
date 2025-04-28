package com.nextiva.nextivaapp.android.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.nextiva.nextivaapp.android.db.util.DbConstants
import java.io.Serializable

@Entity(tableName = DbConstants.TABLE_NAME_GROUPS)
data class DbGroup(@PrimaryKey(autoGenerate = true)
                   @ColumnInfo(name = DbConstants.GROUPS_COLUMN_NAME_ID) var id: Long?,
                   @ColumnInfo(name = DbConstants.GROUPS_COLUMN_NAME_GROUP_ID) var groupId: String?,
                   @ColumnInfo(name = DbConstants.GROUPS_COLUMN_NAME_NAME) var name: String?,
                   @ColumnInfo(name = DbConstants.GROUPS_COLUMN_NAME_ORDER) var order: Int?,
                   @ColumnInfo(name = DbConstants.GROUPS_COLUMN_NAME_TRANSACTION_ID) var transactionId: String?) : Serializable {

    @Ignore
    constructor() : this(null, null, null, 0, null)

    @Ignore
    constructor(group: DbGroup) : this(
            null,
            group.groupId,
            group.name,
            group.order,
            group.transactionId)

    @Ignore
    constructor(group: DbGroup, transactionId: String?) : this(
            null,
            group.groupId,
            group.name,
            group.order,
            transactionId)
}