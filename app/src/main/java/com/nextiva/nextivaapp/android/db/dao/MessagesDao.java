/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.paging.PagingSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.model.DbMessage;
import com.nextiva.nextivaapp.android.models.ChatMessage;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * Created by joedephillipo on 2/25/18.
 */

@Dao
public abstract class MessagesDao {

    @Query("SELECT * FROM messages")
    public abstract Flowable<List<DbMessage>> getAll();

    @Insert
    public abstract long insert(DbMessage message);

    @Query("DELETE FROM messages WHERE (transaction_id != :transactionId OR transaction_id IS NULL) AND sent_status = " + Enums.Chats.SentStatus.SUCCESSFUL)
    public abstract void deleteMessagesByTransactionId(String transactionId);

    @Transaction
    public void refreshMessages(List<DbMessage> messages, String transactionId) {
        for (DbMessage message : messages) {
            if (updateMessage(message.getFrom(), message.getTo(), message.getBody(), message.getMessageId(), message.getTimestamp(), message.isRead(), message.isSender(),
                    message.getType(), message.getGuestFirst(), message.getGuestLast(), message.getLanguage(), message.getParticipant(), message.getThreadId(),
                    message.getMembers(), message.getChatWith(), message.getSentStatus(), message.getTransactionId()) == 0) {
                insert(message);
            }
        }

        deleteMessagesByTransactionId(transactionId);
    }

    @Query("UPDATE messages " +
            "SET message_from = :messageFrom, message_to = :messageTo, body = :body, timestamp = :timestamp, is_read = :isRead, " +
            "is_sender = :isSender, type = :type, guest_first = :guestFirst, guest_last = :guestLast, language = :language, participant = :participant," +
            "thread_id = :threadId, members = :members, chat_with = :chatWith, sent_status = :sentStatus, transaction_id = :transactionId WHERE msg_id = :messageId")
    abstract int updateMessage(String messageFrom, String messageTo, String body, String messageId, Long timestamp, Boolean isRead, Boolean isSender, @Enums.Chats.ConversationTypes.Type String type,
                               String guestFirst, String guestLast, String language, String participant, String threadId, String members, String chatWith, @Enums.Chats.SentStatus.Status Integer sentStatus,
                               String transactionId);

    @Query("UPDATE messages SET is_read = 1 WHERE is_read = 0")
    public abstract void markAllMessagesRead();

    @Query("UPDATE messages SET is_read = 1 WHERE chat_with COLLATE NOCASE = :jid AND is_read = 0")
    public abstract void markMessagesFromSenderRead(String jid);

    @Query("UPDATE messages SET is_read = 1 WHERE msg_id = :messageId AND is_read = 0")
    public abstract void markMessagesReadByMessageId(String messageId);

    @Query("SELECT * FROM messages WHERE msg_id = :messageId")
    public abstract DbMessage getMessageWithMessageId(String messageId);

    @Query("SELECT * " +
            "FROM messages " +
            "WHERE messages.chat_with COLLATE NOCASE = :chatWith " +
            "ORDER BY timestamp DESC")
    public abstract Single<List<ChatMessage>> getChatConversation(String chatWith);

    @Query("SELECT *, vcards.photo_data " +
            "FROM messages " +
            "LEFT JOIN (SELECT contacts.id as contact_id, contacts.ui_name AS ui_name, jid FROM contacts WHERE contact_type = 2) AS results ON results.jid COLLATE NOCASE = messages.message_from " +
            "LEFT JOIN vcards ON vcards.contact_id = results.contact_id " +
            "WHERE messages.chat_with COLLATE NOCASE = :chatWith " +
            "COLLATE NOCASE " +
            "ORDER BY timestamp DESC")
    public abstract PagingSource<Integer, ChatMessage> getChatConversationPagingSource(String chatWith);

    @Query("SELECT msg_id FROM messages WHERE chat_with = :chatwith AND is_read == 0")
    public abstract List<String> getUnreadChatMessageIdsFromChatWith(String chatwith);

    @Transaction
    @Query("SELECT DISTINCT *, MAX(timestamp), vcards.photo_data, results.ui_name, IFNULL (presences.presence_state, -1) AS presence_state, priority " +
            "FROM messages " +
            "LEFT JOIN (SELECT contacts.id as contact_id, contacts.ui_name AS ui_name, jid FROM contacts WHERE contact_type = 2) AS results ON results.jid COLLATE NOCASE = messages.chat_with " +
            "LEFT JOIN vcards ON vcards.contact_id = results.contact_id " +
            "LEFT JOIN presences ON presences.contact_id = results.contact_id " +
            "GROUP BY chat_with COLLATE NOCASE ")
    public abstract LiveData<List<ChatMessage>> getChatMessagesLiveData();

    @Deprecated
    @Transaction
    @Query("SELECT COUNT(DISTINCT chat_with) as count FROM messages WHERE is_read = 0")
    public abstract LiveData<Integer> getUnreadChatMessagesCount();

    @Query("SELECT members FROM messages WHERE thread_id = :threadId LIMIT 1")
    public abstract String getMemberFromThreadId(String threadId);

    @Query("UPDATE messages SET msg_id = :messageId, timestamp = :timestamp, sent_status = " + Enums.Chats.SentStatus.SUCCESSFUL + " WHERE msg_id = :tempMessageId")
    public abstract void updateTempMessageId(String tempMessageId, String messageId, Long timestamp);

    @Query("UPDATE messages SET sent_status = :sentStatus WHERE msg_id = :tempMessageId")
    public abstract void updateMessageSentStatus(String tempMessageId, Integer sentStatus);

    @Query("UPDATE messages SET sent_status = " + Enums.Chats.SentStatus.FAILED + " WHERE sent_status = " + Enums.Chats.SentStatus.PENDING)
    public abstract void setPendingMessagesFailed();

    @Query("DELETE FROM messages WHERE msg_id == :messageId")
    public abstract void deleteMessageFromMessageId(String messageId);

    @Query("SELECT count(*) FROM messages WHERE chat_with = :chatwith AND is_read == 0")
    public abstract int unreadMessagesCountFromChatWith(String chatwith);
}
