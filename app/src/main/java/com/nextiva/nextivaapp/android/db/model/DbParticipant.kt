package com.nextiva.nextivaapp.android.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nextiva.nextivaapp.android.db.util.DbConstants

@Entity(tableName = DbConstants.TABLE_NAME_PARTICIPANT)
data class DbParticipant(@PrimaryKey(autoGenerate = true)
                         @ColumnInfo(name = DbConstants.PARTICIPANT_COLUMN_NAME_ID) var id: Long?,
                         @ColumnInfo(name = DbConstants.PARTICIPANT_COLUMN_NAME_NAME) var name: String?,
                         @ColumnInfo(name = DbConstants.PARTICIPANT_COLUMN_NAME_EMAIl) var emailId: String?,
                         @ColumnInfo(name = DbConstants.PARTICIPANT_COLUMN_NAME_PHONE_NUMBER) var phoneNumber: String?,
                         @ColumnInfo(name = DbConstants.PARTICIPANT_COLUMN_NAME_TEAM_UUID) var teamUUID: String?,
                         @ColumnInfo(name = DbConstants.PARTICIPANT_COLUMN_NAME_USER_UUID) var userUUID: String?,
                         @ColumnInfo(name = DbConstants.PARTICIPANT_COLUMN_NAME_GROUP_ID) var groupId: String?)