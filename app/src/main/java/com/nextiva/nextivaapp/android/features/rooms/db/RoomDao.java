/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.features.rooms.db;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.paging.PagingSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Transaction;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.nextiva.nextivaapp.android.features.rooms.api.ChatMessageAttachment;
import com.nextiva.nextivaapp.android.features.rooms.model.DbChatMessage;
import com.nextiva.nextivaapp.android.features.rooms.model.DbRoom;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public abstract class RoomDao {

    @Query("SELECT * FROM rooms ORDER BY recent_activity_timestamp DESC")
    public abstract Flowable<List<DbRoom>> getAll();

    @Query("SELECT roomId FROM rooms WHERE roomId = :roomId LIMIT 1")
    public abstract String getRoomById(String roomId);

    @Query("SELECT * FROM rooms WHERE roomId = :roomId LIMIT 1")
    public abstract LiveData<DbRoom> getRoomLiveData(String roomId);

    @Query("SELECT * FROM rooms WHERE roomId = :roomId LIMIT 1")
    public abstract DbRoom getRoom(String roomId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insertConnectRoom(DbRoom room);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insertRoomChatMessage(DbChatMessage chatMessage);

    public PagingSource<Integer, DbRoom> getAllRoomsPagingSource(boolean favoritesExpanded, boolean roomsExpanded) {
        String pagingSourceQuery = "";

        if (roomsExpanded && favoritesExpanded) {
            //satisfies requirement of duplicating favorite items in both lists
            pagingSourceQuery = "SELECT * FROM " +
                    "(SELECT * FROM rooms WHERE requesterFavorite == 1 AND type = 'CURRENT_USER_MY_ROOM'" + // my room favorite as 1st spot
                    " UNION ALL " +
                    "SELECT * FROM rooms WHERE type NOT LIKE 'CURRENT_USER_MY_ROOM' AND requesterFavorite == 1"+ // all other favorites
                    " UNION ALL "+
                    "SELECT * FROM rooms WHERE type = 'CURRENT_USER_MY_ROOM'" + // my room as 1st spot
                    " UNION ALL " +
                    "SELECT * FROM rooms WHERE type NOT LIKE 'CURRENT_USER_MY_ROOM')"; // all other rooms
        } else if (roomsExpanded) {
            pagingSourceQuery = "SELECT * FROM rooms ORDER BY CASE WHEN type = 'CURRENT_USER_MY_ROOM' THEN 1 ELSE 2 END";
        } else if (favoritesExpanded) {
            pagingSourceQuery = "SELECT * FROM rooms WHERE requesterFavorite == 1";
        } else {
            pagingSourceQuery = "SELECT * FROM rooms WHERE type = -1";
        }

        return getConnectRoomsPagingSourceRawQuery(new SimpleSQLiteQuery(pagingSourceQuery, null));
    }

    @Query("SELECT * FROM rooms ORDER BY recent_activity_timestamp DESC")
    public abstract LiveData<List<DbRoom>> getAllRoomsLiveData();

    @Query("SELECT * FROM rooms ORDER BY recent_activity_timestamp DESC")
    public abstract List<DbRoom> getAllRooms();

    @Query("SELECT * FROM rooms WHERE type = 'CURRENT_USER_MY_ROOM'")
    public abstract DbRoom getMyRoom();

    @Transaction
    @Query("SELECT * FROM chat_messages WHERE room_id = :roomId ORDER BY datetime(timestamp) DESC")
    public abstract PagingSource<Integer, DbChatMessage> getAllChatMessagesPagingSource(String roomId);

    @Query("SELECT * FROM chat_messages WHERE room_id = :roomId ORDER BY datetime(timestamp) DESC")
    public abstract LiveData<List<DbChatMessage>> getAllChatMessages(String roomId);

    @Query("SELECT * FROM chat_messages WHERE room_id = :roomId AND id = :dbChatMessageId")
    public abstract DbChatMessage getChatMessage(String roomId, String dbChatMessageId);

    @Transaction
    public void updateAttachmentAttribute(String roomId, String dbChatMessageId, String chatMessageAttachmentId, UpdateAction action) {
        List<ChatMessageAttachment> attachments = getChatMessage(roomId, dbChatMessageId).getAttachments();
        if (attachments != null) {
            for (ChatMessageAttachment attachment : attachments) {
                if (attachment.getId().equals(chatMessageAttachmentId)) {
                    action.updateAttribute(attachment);
                    return;
                }
            }
        }
    }

    @Transaction
    @RawQuery(observedEntities = {DbRoom.class})
    abstract PagingSource<Integer, DbRoom> getConnectRoomsPagingSourceRawQuery(SupportSQLiteQuery query);

    @Query("SELECT COUNT(*) " +
            "FROM rooms ")
    public abstract LiveData<Integer> getConnectRoomsCount();

    @Query("SELECT COUNT(*) " +
            "FROM rooms " +
            "WHERE requesterFavorite == 1 " +
            " COLLATE NOCASE")
    public abstract LiveData<Integer> getConnectRoomsFavoritesCount();

    @Query("UPDATE rooms " +
            "SET requesterFavorite = :isFavorite " +
            "WHERE roomId = :roomId")
    public abstract void setFavorite(String roomId, boolean isFavorite);

    @Query("DELETE FROM chat_messages WHERE room_id = :roomId AND id = :messageId")
    public abstract void deleteChatMessage(String roomId, String messageId);

    @Query("DELETE FROM rooms WHERE roomId = :roomId")
    public abstract void deleteRoom(String roomId);

    @Query("SELECT SUM(unreadMessageCount) FROM rooms")
    public abstract int getUnreadMessageCount();

    @Query("UPDATE rooms SET unreadMessageCount = :count WHERE roomId = :roomId")
    public abstract void setUnreadMessageCountByRoomId(int count, String roomId);

    @Query("SELECT unreadMessageCount FROM rooms WHERE roomId = :roomId")
    public abstract int getUnreadMessageCountByRoomId(String roomId);

    @NonNull
    @Query("SELECT SUM(unreadMessageCount) FROM rooms WHERE type IN (:types)")
    public abstract LiveData<Integer> getTotalUnreadMessagesLiveData(List<String> types);

    @NonNull
    @Query("SELECT unreadMessageCount FROM rooms WHERE roomId = :roomId")
    public abstract LiveData<Integer> getUnreadMessagesByRoomIdLiveData(String roomId);

    public interface UpdateAction {
        void updateAttribute(ChatMessageAttachment attachment);
    }
}
