package com.nextiva.nextivaapp.android.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.nextiva.nextivaapp.android.db.util.DbConstants

@Entity(tableName = DbConstants.TABLE_NAME_LOGGING)
data class DbLogging(@PrimaryKey(autoGenerate = true)
                        @ColumnInfo(name = DbConstants.LOGGING_COLUMN_ID) var id: Long?,
                        @ColumnInfo(name = DbConstants.LOGGING_COLUMN_LEVEL) var level: String?,
                        @ColumnInfo(name = DbConstants.LOGGING_COLUMN_MESSAGE) var message: String?,
                        @ColumnInfo(name = DbConstants.LOGGING_COLUMN_TIME) var time: Long?,
                     @ColumnInfo(name = DbConstants.LOGGING_COLUMN_LOCATION) var location: String?,
                     @ColumnInfo(name = DbConstants.LOGGING_COLUMN_INTERNET_STATUS) var internetStatus: String?){
    @Ignore
    constructor() : this(null, null, null, null, null, null)
}