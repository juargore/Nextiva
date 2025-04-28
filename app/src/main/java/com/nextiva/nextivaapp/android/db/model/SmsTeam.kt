package com.nextiva.nextivaapp.android.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.db.util.DbConstants
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.models.net.platform.teams.TeamMemberResponse
import java.io.Serializable

@Entity(tableName = DbConstants.TABLE_NAME_SMS_TEAM)
data class SmsTeam(@PrimaryKey(autoGenerate = true)
                   @ColumnInfo(name = DbConstants.SMS_TEAM_COLUMN_NAME_ID) var id: Long?,
                   @ColumnInfo(name = DbConstants.SMS_TEAM_COLUMN_NAME_TEAM_ID) var teamId: String?,
                   @ColumnInfo(name = DbConstants.SMS_TEAM_COLUMN_NAME_TEAM_NAME) var teamName: String?,
                   @ColumnInfo(name = DbConstants.SMS_TEAM_COLUMN_NAME_TEAM_PHONE_NUMBER) var teamPhoneNumber: String?,
                   @ColumnInfo(name = DbConstants.SMS_TEAM_COLUMN_NAME_TEAM_LEGACY_ID) var legacyId: String?,
                   @Ignore var members: ArrayList<TeamMemberResponse>? = null,
                   @Ignore var smsEnabled: Boolean? = null) : Serializable {
    constructor(id: Long?, teamId: String?, teamName: String?, teamPhoneNumber: String?, legacyId: String?): this(id, teamId, teamName, teamPhoneNumber, legacyId,null, false)

    val uiName: String?
    get() {
        return teamName ?: teamPhoneNumber
    }

    val avatarInfo: AvatarInfo
    get() {
        return AvatarInfo.Builder()
            .setDisplayName(teamName ?: teamPhoneNumber)
            .setIconResId(R.drawable.avatar_team)
            .isConnect(true)
            .setAlwaysShowIcon(true)
            .build()
    }
}