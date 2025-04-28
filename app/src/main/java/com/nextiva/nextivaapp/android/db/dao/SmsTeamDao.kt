package com.nextiva.nextivaapp.android.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nextiva.nextivaapp.android.db.model.SmsTeam
import com.nextiva.nextivaapp.android.db.model.SmsTeamRelation

@Dao
interface SmsTeamDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(team: SmsTeam): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(relation: SmsTeamRelation): Long

    @Query("SELECT * FROM sms_team")
    fun getAllTeams(): List<SmsTeam>

    @Query("SELECT id FROM sms_team where team_id = :teamId OR legacy_Id = :teamId")
    fun checkTeam(teamId: String): Long

    @Query("UPDATE sms_team " +
            "SET team_phone_number = :teamPhoneNumber, " +
            "team_name = :teamName " +
            "WHERE team_id = :teamId")
    fun updateParticipant(teamId: String, teamName: String, teamPhoneNumber: String?)
}