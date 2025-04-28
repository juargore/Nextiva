package com.nextiva.nextivaapp.android.db.dao

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import androidx.room.Update
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.AppDatabase
import com.nextiva.nextivaapp.android.db.model.DbMessageState
import com.nextiva.nextivaapp.android.db.model.DbParticipant
import com.nextiva.nextivaapp.android.db.model.DbRecipient
import com.nextiva.nextivaapp.android.db.model.DbSender
import com.nextiva.nextivaapp.android.db.model.DbSmsMessage
import com.nextiva.nextivaapp.android.db.model.SmsTeam
import com.nextiva.nextivaapp.android.db.model.SmsTeamRelation
import com.nextiva.nextivaapp.android.models.SmsConversationDetails
import com.nextiva.nextivaapp.android.models.SmsMessage
import com.nextiva.nextivaapp.android.models.net.platform.Data
import com.nextiva.nextivaapp.android.models.net.platform.Participant
import com.nextiva.nextivaapp.android.util.extensions.nullIfEmpty
import io.reactivex.Single
import java.util.Arrays

@Dao
abstract class SmsMessagesDao {

    @Transaction
    open fun insertSmsMessages(appDatabase: AppDatabase,
                               messageData: ArrayList<Data>,
                               phoneNumber: String,
                               uuid: String?,
                               sentStatus: Int,
                               allSavedTeams: List<SmsTeam>?,
                               groupId: String?
    ) {
        var messageId: Long
        var smsId: Long = -1

        val messageStates: ArrayList<DbMessageState> = ArrayList()
        messageData.forEach { data ->
            val groupValue = data.groupValue

            data.messages?.let { messages ->
                messages.forEach { message ->
                    message.getDbSmsMessages(sentStatus, phoneNumber, groupId)?.messageId?.let { sentMessageId ->
                        messageId = checkSmsMessage(sentMessageId)

                        groupValue?.nullIfEmpty()?.let {
                            message.groupValue = getSortedGroupValue(it)
                        }

                        message.isSender = phoneNumber.equals(message.sender?.phoneNumber, ignoreCase = true) || uuid.equals(message.sender?.userUuid) || message.isSender
                        message.sentStatus = sentStatus

                        if (messageId == 0L) {
                            smsId = if (!TextUtils.isEmpty(message.clientId)) {
                                val pendingMessageId: Long = checkForMessageByClientId(message.clientId, Enums.SMSMessages.SentStatus.PENDING, Enums.SMSMessages.SentStatus.FAILED)

                                if (pendingMessageId == 0L) {
                                    message.getDbSmsMessages(sentStatus, phoneNumber, groupId)?.let { insert(it) }

                                } else {
                                    message.getDbSmsMessage(pendingMessageId, phoneNumber, groupId)?.let { updateMessageObject(it) }
                                    pendingMessageId
                                }
                            } else {
                                message.getDbSmsMessages(sentStatus, phoneNumber, groupId)?.let { insert(it) }

                            } ?: 0L

                        } else {
                            message.getDbSmsMessage(messageId, phoneNumber, groupId)?.let { updateMessageObject(it) }
                            smsId = messageId
                        }

                        message.messageState?.getDbMessageState(smsId)?.let {
                            appDatabase.messageStateDao().getSavedMessageState(smsId)?.let { messageState ->
                                it.readStatus = messageState.readStatus
                            }

                            messageStates.add(it)
                        }

                        message.attachments?.let { attachmentList ->
                            attachmentList.forEach { attachment ->
                                attachment.link?.let { link ->
                                    attachment.filename?.let { fileName ->
                                        val dbAttachment = appDatabase.attachmentsDao().getAttachment(smsId, fileName)

                                        if (dbAttachment == null) {
                                            attachment.getDbAttachment(smsId)?.let {
                                                appDatabase.attachmentsDao().insert(it)
                                            }

                                        }
                                    }
                                }
                            }
                        }

                        var mGroupId = groupId
                        if (mGroupId == null) { mGroupId = message.groupId }

                        mGroupId?.let { groupId ->
                            message.sender?.let { participant ->
                                var id: Long? = addParticipantToDb(appDatabase, groupId, participant)
                                id?.let { appDatabase.sendersDao().insert(DbSender(smsId, it)) }

                                message.recipients?.forEach { recipient ->
                                    id = addParticipantToDb(appDatabase, groupId, recipient)
                                    id?.let { appDatabase.recipientsDao().insert(DbRecipient(smsId, it)) }
                                }
                            }
                        }

                        message.teams?.forEach { team ->
                            team.id?.let { teamId ->
                                val teamToUse = allSavedTeams?.firstOrNull { it.teamId == teamId || it.legacyId == teamId } ?: SmsTeam(null, team.id, team.name, team.phoneNumber, team.legacyId)
                                var teamDbId = appDatabase.smsTeamDao().checkTeam(teamId)

                                if (teamDbId == 0L) {
                                    teamDbId = appDatabase.smsTeamDao().insert(SmsTeam(null, teamToUse.teamId, teamToUse.teamName, teamToUse.teamPhoneNumber, teamToUse.legacyId))
                                }

                                appDatabase.smsTeamDao().insert(SmsTeamRelation(smsId, teamDbId))
                            }
                        }
                    }
                }
            }
        }

        appDatabase.participantDao().getAllParticipants().forEach { participant ->
            if (participant.userUUID.isNullOrEmpty()) {
                participant.phoneNumber?.let { participantPhoneNumber ->
                    appDatabase.completeContactDao().getContactTypeIdFromPhoneNumberInThread(participantPhoneNumber)?.let { contactTypeId ->
                        appDatabase.participantDao().updateParticipant(contactTypeId, participantPhoneNumber)
                    }
                }
            }
        }

        appDatabase.messageStateDao().insertAll(messageStates)
    }

    private fun addParticipantToDb(appDatabase: AppDatabase, conversationId: String, participant: Participant): Long? {
        var dbParticipant: DbParticipant? = null
        var participantId: Long? = null

        if (participant.userUuid?.isNotEmpty() == true) {
            participant.userUuid?.let { userUuid ->
                dbParticipant = appDatabase.participantDao().getParticipantByUUID(userUuid, conversationId)
            }
        }

        if (dbParticipant?.id == 0L || dbParticipant?.id == null) {
            if (participant.phoneNumber?.isNotEmpty() == true) {
                participant.phoneNumber?.let { senderPhoneNumber ->
                    dbParticipant = appDatabase.participantDao().getParticipantByPhone(senderPhoneNumber, conversationId)
                }
            }
        }

        if (dbParticipant?.id == 0L || dbParticipant?.id == null) {
            participantId = appDatabase.participantDao().insert(participant.getDbParticipant(conversationId))

        } else {
            dbParticipant?.userUUID?.let { uuid ->
                appDatabase.participantDao().updateParticipantTeams(participant.getDbParticipant(conversationId).teamUUID, uuid, conversationId)
                if (dbParticipant?.name != participant.name) {
                    appDatabase.participantDao().updateParticipantName(participant.name, uuid)
                }
            }
        }

        return dbParticipant?.id ?: participantId
    }

    open fun getSortedGroupValue(groupValue: String): String? {
        val sortingGroupValueList = ArrayList(Arrays.asList(*groupValue.trim { it <= ' ' }.replace("\\s".toRegex(), "").split(",".toRegex()).filter {
            e->e.trim().isNotEmpty()
        }.toTypedArray()))
        sortingGroupValueList.sortWith(Comparator { s, t1 -> java.lang.Long.compare(s.trim { it <= ' ' }.toLong(), t1.trim { it <= ' ' }.toLong()) })
        return TextUtils.join(",", sortingGroupValueList).trim { it <= ' ' }
    }

    open fun getConversationDetailsFrom(conversationDetails: SmsConversationDetails): SmsConversationDetails {
        val teamIds = ArrayList(conversationDetails.getAllTeams().map { it.teamId ?: "" })

        if (teamIds.isNotEmpty()) {
            conversationDetails.groupValue = null
        }

        conversationDetails.groupId?.let { groupId ->
            getLatestMessageFromGroupId(groupId)?.let { message ->
                conversationDetails.updateFromSmsMessage(message)
            }
        }

        return conversationDetails
    }

    open fun getGroupIdFrom(conversationId: String): String? {
        return getLatestMessageFromConversationId(conversationId)?.groupId
    }

    @Query("SELECT sms_messages.* FROM sms_messages WHERE conversation_id = :conversationId LIMIT 1")
    abstract fun getLatestMessageFromConversationId(conversationId: String): SmsMessage?

    @Query("SELECT sms_messages.* FROM sms_messages WHERE group_id = :groupId LIMIT 1")
    abstract fun getLatestMessageFromGroupId(groupId: String): SmsMessage?

    @Transaction
    @Query("""SELECT *
                    FROM sms_messages 
                    WHERE group_id = :groupId
                    AND sent_status != :draftStatus
                    ORDER BY datetime(sent) DESC
                    LIMIT 1""")
    abstract fun getMostRecentMessageFromConversationWithoutDraft(groupId: String, draftStatus: Int): SmsMessage?

    @Query("SELECT sms_messages.* FROM sms_messages WHERE group_id = :groupId AND sent_status == :draftStatus")
    abstract fun getAllDraftMessagesFromConversation(groupId: String, draftStatus: Int): List<SmsMessage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(senders: DbSmsMessage): Long

    @Query("SELECT id FROM sms_messages WHERE message_id= :message_id")
    abstract fun checkSmsMessage(message_id: String): Long

    @Query("SELECT id FROM sms_messages WHERE message_id= :client_id AND (sent_status= :pendingStatus OR sent_status= :failedStatus)")
    abstract fun checkForMessageByClientId(client_id: String, pendingStatus: Int, failedStatus: Int): Long

    @Transaction
    @Query("DELETE FROM sms_messages WHERE message_id = :message_id ")
    abstract fun deleteSmsMessageByMessageId(message_id: String)

    @Transaction
    @Query("DELETE FROM sms_messages WHERE group_id = :groupId AND sent_status = :draftStatus")
    abstract fun deleteDraftMessagesByConversation(groupId: String, draftStatus: Int)

    @Transaction
    @Query("DELETE FROM sms_messages WHERE group_id = :groupId")
    abstract fun deleteMessagesFromConversationByGroupId(groupId: String)

    @Query("SELECT (SELECT COUNT(DISTINCT chat_with) as count FROM messages WHERE is_read = 0) + (SELECT COUNT(cnt) FROM (SELECT count(*) as cnt FROM message_state INNER JOIN sms_messages ON sms_messages.id = message_state.sms_id WHERE (read_status = 'UNREAD' AND is_sender= 0) GROUP BY group_id) t)")
    abstract fun getTotalUnreadMessageConversationsCount(): LiveData<Int>

    @Query("SELECT (SELECT COUNT(*) as count FROM messages WHERE is_read = 0) + (SELECT count(*) as cnt FROM message_state INNER JOIN sms_messages ON sms_messages.id = message_state.sms_id WHERE (read_status = 'UNREAD' AND is_sender= 0))")
    abstract fun getTotalUnreadMessagesCount(): LiveData<Int>

    @Query("SELECT COUNT(cnt) FROM (SELECT count(*) as cnt FROM message_state INNER JOIN sms_messages ON sms_messages.id = message_state.sms_id WHERE(read_status = 'UNREAD' AND is_sender= 0) GROUP BY group_id) t")
    abstract fun getUnreadSmsConversationCount(): LiveData<Int>

    @Query("SELECT count (*) as cnt FROM message_state INNER JOIN sms_messages ON sms_messages.id = message_state.sms_id WHERE (read_status = 'UNREAD' AND is_sender = 0 AND conversation_id = :conversationId)")
    abstract fun getUnreadSmsMessagesInConversationCount(conversationId: String): Int

    @Transaction
    @Query("""SELECT *, MAX(datetime(sent))  
                    FROM sms_messages 
                    GROUP BY conversation_id   
                    ORDER BY datetime(sent) DESC""")
    abstract fun getAllMessages(): LiveData<List<SmsMessage>>

    @Transaction
    @Query("SELECT * FROM sms_messages WHERE group_id = :groupId")
    abstract fun getAllMessagesForGroupId(groupId: String): List<SmsMessage>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("""SELECT *, MAX(datetime(sent)) 
            FROM sms_messages 
            GROUP BY group_id  
            ORDER BY datetime(sent) DESC""")
    abstract fun getAllMessagesPagingSource(): PagingSource<Int, SmsMessage>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """SELECT * , MAX(datetime(sent)) from sms_messages 
                   JOIN sms_team_relation ON sms_messages.id = sms_team_relation.relation_message_id
                   JOIN sms_team ON sms_team_relation.relation_team_id = sms_team.id 
                   where team_id = :filter
                   GROUP BY group_id 
                   ORDER BY datetime(sent) DESC""")
    abstract fun getFilteredMessagesPagingSource(filter: String): PagingSource<Int, SmsMessage>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("""SELECT sms_messages.*  
                    FROM sms_messages 
                    WHERE group_id = :groupId
                    ORDER BY datetime(sent) DESC""")
    abstract fun getSmsConversationPagingSource(groupId: String): PagingSource<Int, SmsMessage>

    @Query("UPDATE sms_messages SET sent_status = :status WHERE message_id= :messageId")
    abstract fun updateSentStatus(messageId: String, status: Int)

    @Query("UPDATE sms_messages SET message_id = :messageId, sent_status= :sentStatus, group_id = :groupId WHERE message_id = :tempMessageId")
    abstract fun updateMessageIdAndSentStatus(tempMessageId: String, messageId: String, sentStatus: Int, groupId: String?)

    @Query("update message_state set read_status = 'READ' where sms_id IN (select sms_messages.id from sms_messages where message_id = :messageId)")
    abstract fun updateReadStatusForMessageId(messageId: String)

    @Query("update message_state set read_status = 'UNREAD' where sms_id IN (select sms_messages.id from sms_messages where message_id = :messageId)")
    abstract fun updateUnreadStatusForMessageId(messageId: String)

    @Query("update message_state set read_status = 'READ' where sms_id IN (select sms_messages.id from sms_messages where conversation_id = :conversationId)")
    abstract fun updateReadStatusForConversationId(conversationId: String)

    @Query("update message_state set read_status = 'READ' where sms_id IN (select sms_messages.id from sms_messages where group_id = :groupId)")
    abstract fun updateReadStatusForGroupId(groupId: String)

    @Query("update message_state set read_status = 'UNREAD' where sms_id IN (select sms_messages.id from sms_messages where group_id = :groupId)")
    abstract fun updateUnreadStatusForGroupId(groupId: String)

    @Query("SELECT group_value FROM sms_messages WHERE group_value LIKE '%' || :number || '%'")
    abstract fun getGroupValueContainingNumber(number: String): List<String>

    @Update
    abstract fun updateMessageObject(DbSmsMessages: DbSmsMessage)

    @Query("UPDATE sms_messages set sent_status = :failedStatus  WHERE sent_status = :pendingStatus")
    abstract fun updateAllSentStatusFromPendingToFailed(failedStatus: Int, pendingStatus: Int)

    @Query("SELECT message_id FROM sms_messages WHERE sent_status = ${Enums.SMSMessages.SentStatus.SUCCESSFUL} LIMIT 1")
    abstract fun getSuccessfulMessageId(): String?

    @Query("SELECT * FROM sms_messages WHERE message_id = :messageId")
    abstract fun getSmsMessageSingleByMessageId(messageId: String): Single<SmsMessage>

    @Query("SELECT * FROM sms_messages WHERE message_id = :messageId")
    abstract fun getSmsMessageByMessageId(messageId: String): SmsMessage?

    @Query("DELETE FROM sms_messages")
    abstract fun deleteAllSmsMessages()

    @Query("SELECT COUNT(DISTINCT group_id) FROM sms_messages")
    abstract fun getCurrentConversationListCount(): Int

    @Query("SELECT COUNT(*) FROM sms_messages WHERE group_id = :groupId ")
    abstract fun getCurrentConversationCount(groupId: String): Int

    @Transaction
    @Query("DELETE FROM sms_messages WHERE group_id = :groupId")
    abstract fun deleteMessagesByGroupId(groupId: String)
}
