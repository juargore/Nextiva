package com.nextiva.nextivaapp.android.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nextiva.nextivaapp.android.db.model.DbParticipant


@Dao
interface ParticipantsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(participant: DbParticipant): Long

    @Query("SELECT * FROM participant")
    fun getAllParticipants(): List<DbParticipant>

    @Query("SELECT * FROM participant WHERE team_uuid = :teamId AND user_uuid = :uuid LIMIT 1")
    fun getParticipantByUuidAndTeamId(uuid: String, teamId: String): DbParticipant?

    @Query("SELECT * FROM participant " +
            "WHERE phone_number = :phoneNumber " +
            "AND group_id = :conversationId")
    fun getParticipantByPhone(phoneNumber: String, conversationId: String): DbParticipant?

    @Query("SELECT * FROM participant " +
            "WHERE user_uuid = :userUuid " +
            "AND group_id = :conversationId")
    fun getParticipantByUUID(userUuid: String, conversationId: String): DbParticipant?

    @Query("UPDATE participant SET name = null, user_uuid = null WHERE user_uuid = :userUuid AND phone_number = :strippedNumber")
    fun cleanParticipantReferenceByUUIDAndPhoneNumber(userUuid: String, strippedNumber: String)

    @Query("UPDATE participant SET user_uuid = :userUuid, name = :name WHERE phone_number = :strippedNumber")
    fun setParticipantReferenceByUUID(userUuid: String?, name: String?, strippedNumber: String)

    @Query("UPDATE participant " +
            "SET team_uuid = :teamUuid " +
            "WHERE user_uuid = :participantUuid " +
            "AND group_id = :conversationId")
    fun updateParticipantTeams(teamUuid: String?, participantUuid: String, conversationId: String)

    @Query("UPDATE participant " +
            "SET name = :name " +
            "WHERE user_uuid = :participantUuid")
    fun updateParticipantName(name: String?, participantUuid: String)

    @Query("UPDATE participant " +
            "SET user_uuid = :userUuid " +
            "WHERE phone_number = :phoneNumber")
    fun updateParticipant(userUuid: String?, phoneNumber: String)

    @Query("UPDATE participant " +
            "SET group_id = :groupId " +
            "WHERE group_id = :teamMessageId")
    fun updateParticipantsForNewConversation(teamMessageId: String, groupId: String)
}
