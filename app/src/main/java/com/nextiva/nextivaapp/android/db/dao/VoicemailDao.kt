package com.nextiva.nextivaapp.android.db.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import com.nextiva.nextivaapp.android.db.model.DbVoicemail
import com.nextiva.nextivaapp.android.models.CallsDbReturnModel
import com.nextiva.nextivaapp.android.models.Voicemail
import java.util.UUID

@Dao
interface VoicemailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVoicemail(voicemail: DbVoicemail)

    @Transaction
    fun insertVoicemails(voicemails: ArrayList<DbVoicemail>) {
        val transactionId = UUID.randomUUID().toString()

        voicemails.forEach { voicemail ->
            voicemail.messageId?.let { messageId ->
                val dbVoicemail = getVoicemailById(messageId)

                if (dbVoicemail != null) {

                    updateVoicemail(
                        transactionId, voicemail.name,
                        voicemail.read == true, messageId, voicemail.transcription.toString()
                    )

                } else {
                    insertVoicemail(voicemail)
                }
            }
        }
    }

    @Transaction
    @Query("DELETE FROM voicemails WHERE transaction_id != :transactionId OR transaction_id IS NULL")
    fun clearOldVoicemailsByTransactionId(transactionId: String)

    @Transaction
    @Query(
        """SELECT voicemails.*, 
            vcards.photo_data, 
            number_results.ui_name, 
            IFNULL(presences.presence_state, -1) AS presence_state, 
            status_text, 
            priority, 
            contact_type, 
            presences.jid, 
            IFNULL(presences.presence_type, -1) AS presence_type 
            FROM voicemails 
            LEFT JOIN (SELECT contacts.id as contact_id, 
                MIN(contacts.ui_name) AS ui_name, 
                phones.stripped_number FROM contacts 
            INNER JOIN phones 
                ON contacts.id = phones.contact_id 
                GROUP BY phones.stripped_number 
                ORDER BY contact_type ASC) AS number_results 
                ON CASE WHEN length(number_results.stripped_number) < 9
					THEN number_results.stripped_number = voicemails.address  
					ELSE number_results.stripped_number = voicemails.address 
                        OR number_results.stripped_number = (1 || voicemails.address)
			    END
            LEFT JOIN (SELECT contacts.id as contact_id, phones.stripped_number, 
                contacts.contact_type AS contact_type 
                FROM contacts 
                INNER JOIN phones 
                    ON contacts.id = phones.contact_id 
                    WHERE contacts.contact_type IN (2, 3) 
                    GROUP BY phones.stripped_number 
                    ORDER BY contact_type ASC) AS results 
                ON CASE WHEN length(results.stripped_number) < 9
					THEN results.stripped_number = voicemails.address 
					ELSE results.stripped_number = voicemails.address 
                        OR results.stripped_number = (1 || voicemails.address)
				END
            LEFT JOIN vcards ON vcards.contact_id = results.contact_id 
            LEFT JOIN presences ON presences.contact_id = results.contact_id ORDER BY time DESC"""
    )
    fun getVoicemailLiveData(): LiveData<List<Voicemail>>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("""SELECT voicemails.*, null as call_with_contact_id   
            FROM voicemails 
            ORDER BY time DESC""")
    fun getVoicemailPagingSource(): PagingSource<Int, CallsDbReturnModel>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("""SELECT *, null as call_with_contact_id   
            FROM voicemails  
            WHERE name LIKE :query OR formatted_phone_number LIKE replace(replace(replace(replace(replace(:query, '+', ''), '-', ''), '(', ''), ')', ''), ' ', '')
            ORDER BY time DESC""")
    fun getVoicemailPagingSource(query: String): PagingSource<Int, CallsDbReturnModel>

    @Query("SELECT read FROM voicemails WHERE message_id = :messageId")
    fun getVoicemailReadLiveData(messageId: String?): LiveData<Boolean>

    @Transaction
    @Query("UPDATE voicemails SET read = 1 WHERE read = 0 AND message_id = :messageId")
    fun markVoicemailRead(messageId: String?)

    @Transaction
    @Query("UPDATE voicemails SET read = 0 WHERE read = 1 AND message_id = :messageId")
    fun markVoicemailUnread(messageId: String?)

    @Transaction
    @Query("UPDATE voicemails SET read = :isRead WHERE message_id IN (:voicemailIdList)")
    fun updateVoicemailEntriesReadStatus(isRead: Int, voicemailIdList: List<String>)

    @Transaction
    @Query("UPDATE voicemails SET read = 1 WHERE read = 0 AND actual_voicemail_id = :messageId")
    fun markConversationVoicemailRead(messageId: String?)

    @Transaction
    @Query("UPDATE voicemails SET read = 0 WHERE read = 1 AND actual_voicemail_id = :messageId")
    fun markConversationVoicemailUnread(messageId: String?)

    @Transaction
    @Query("UPDATE voicemails SET read = 1 WHERE read = 0")
    fun markAllVoicemailsRead()

    @Query("DELETE FROM voicemails WHERE message_id = :messageId")
    fun deleteVoicemail(messageId: String?)

    @Query("DELETE FROM voicemails WHERE message_id IN (:messageIds)")
    fun deleteVoicemails(messageIds: ArrayList<String>)

    @Deprecated(message = "Use sessionManager.getUnreadVoicemailCount() instead")
    @Query("SELECT COUNT(*) FROM voicemails WHERE read = 0")
    fun getUnreadVoicemailCount(): Int

    @Deprecated(message = "Use sessionManager.getUnreadVoicemailCountLiveData() instead")
    @Query("SELECT COUNT(*) FROM voicemails WHERE read = 0")
    fun getUnreadVoicemailCountLiveData(): LiveData<Int>

    @Query("UPDATE voicemails SET transcription = :transcription, rating = :rating, actual_voicemail_id = :messageId WHERE message_id LIKE '%' || :messageId || '%'")
    fun insertVoicemailTranscriptions(transcription: String?, rating: String?, messageId: String)

//    @Query("UPDATE voicemails SET transcription = :transcription WHERE message_id LIKE '%' || :messageId || '%'")
//    fun insertVoicemailTranscriptions(transcription: String?, messageId: String)

    @Query("UPDATE voicemails SET rating = :rating WHERE message_ID = :messageId")
    fun updateRating(rating: String, messageId: String)

    @Query("UPDATE voicemails SET duration = :duration WHERE actual_voicemail_id = :messageId")
    fun updateDuration(duration: Int, messageId: String)

    @Query("SELECT rating FROM voicemails WHERE message_id LIKE '%' || :messageId || '%'")
    fun getRatingByVoicemailId(messageId: String): String?

    @Query("SELECT * FROM voicemails WHERE message_id = :messageId")
    fun getVoicemailById(messageId: String): DbVoicemail?

    @Query("UPDATE voicemails SET read = :read, name = :name, transaction_id = :transactionId, transcription = :transcription WHERE message_id = :messageId")
    fun updateVoicemail(
        transactionId: String,
        name: String?,
        read: Boolean,
        messageId: String,
        transcription: String,
    )

    @Query("SELECT IFNULL(MAX(vm_page_number), 0) FROM voicemails")
    fun getLastPageFetched(): Int

    @Query("DELETE FROM voicemails")
    fun deleteVoicemails()
}