package com.nextiva.nextivaapp.android.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nextiva.nextivaapp.android.db.model.DbMessageState
import io.reactivex.Single

@Dao
interface MessageStateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(messageState: DbMessageState): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(messageState: List<DbMessageState>)

    @Query("SELECT * FROM message_state INNER JOIN sms_messages ON sms_messages.id = message_state.sms_id WHERE conversation_id = :conversationId")
    fun getMessageStateList(conversationId: String): Single<List<DbMessageState>>

    @Query("SELECT * FROM message_state WHERE sms_id = :id LIMIT 1")
    fun getSavedMessageState(id: Long): DbMessageState?

    @Query("SELECT * FROM message_state INNER JOIN sms_messages ON sms_messages.id = message_state.sms_id WHERE group_id = :groupId")
    fun getMessageStateListInThread(groupId: String): List<DbMessageState>

    @Query("SELECT count(*) as count FROM message_state WHERE read_status = :unreadStatus")
    fun getTotalUnreadMessagesCount(unreadStatus: String): LiveData<Int>
}