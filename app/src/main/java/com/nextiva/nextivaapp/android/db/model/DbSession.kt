package com.nextiva.nextivaapp.android.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nextiva.nextivaapp.android.db.util.DbConstants

@Entity(indices = [Index(value = [DbConstants.SESSION_COLUMN_NAME_KEY], unique = true)],
        tableName = DbConstants.TABLE_NAME_SESSION)
data class DbSession(@PrimaryKey(autoGenerate = true)
                     @ColumnInfo(name = DbConstants.SESSION_COLUMN_NAME_ID) var id: Long?,
                     @ColumnInfo(name = DbConstants.SESSION_COLUMN_NAME_KEY) var key: String?,
                     @ColumnInfo(name = DbConstants.SESSION_COLUMN_NAME_VALUE) var value: String?) {
    @Ignore
    constructor() : this(null, null, null)
}
