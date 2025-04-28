/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.features.rooms.db

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.nextiva.nextivaapp.android.features.rooms.RoomsEnums
import com.nextiva.nextivaapp.android.features.rooms.api.ChatMessage
import com.nextiva.nextivaapp.android.features.rooms.api.ConnectRoom
import com.nextiva.nextivaapp.android.features.rooms.model.DbChatMessage
import com.nextiva.nextivaapp.android.features.rooms.model.DbRoom
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import io.reactivex.Completable
import io.reactivex.Single

interface RoomsDbManager {
    fun saveRooms(rooms: ArrayList<ConnectRoom>): Completable
    fun saveRoomChatMessages(chatMessages: ArrayList<ChatMessage>): Completable
    fun updateRoomChatMessage(chatMessage: DbChatMessage): Completable
    fun getConnectRoomsGroupCount(group: RoomsEnums.ConnectRoomsGroups): LiveData<Int>
    fun markRoomFavorite(roomId: String, isFavorite: Boolean)
    fun getConnectRoomsPagingSource(favoritesExpanded: Boolean, roomsExpanded: Boolean): PagingSource<Int, DbRoom>
    fun getAllRooms(): LiveData<List<DbRoom>>
    fun getChatMessagePagingSource(roomId: String): PagingSource<Int, DbChatMessage>
    fun isCacheExpired(@SharedPreferencesManager.SettingsKey key: String): Boolean
    fun getAllChatMessages(roomId: String?): LiveData<List<DbChatMessage>>
    fun deleteChatMessage(roomId: String, messageId: String)
    fun undoDeleteChatMessage(message: DbChatMessage): Completable
    fun getRoom(roomId: String): LiveData<DbRoom?>
    fun getRoomCompletable(roomId: String): Single<DbRoom?>
    fun getMyRoomInThread(): DbRoom?
    fun getRoomInThread(contactUuids: List<String>): DbRoom?
    fun saveAudioDataFromLinkWithReturn(roomId: String?, messageId: String, attachmentId: String?, link: String, sessionId: String, corpAcctNumber: String): Single<ByteArray?>
    fun saveAudioFileDuration(roomId: String?, messageId: String, attachmentId: String, duration: Long): Single<Long?>
    fun deleteRoom(roomId: String)
    fun clearAndResetAllTables(): Boolean
    fun getUnreadMessageCountInThread(): Int
    fun getUnreadMessageCount(): Single<Int>
    fun modifyUnreadMessageCountByRoomId(count: Int, roomId: String)
    fun getUnreadMessageLiveData(types: List<String>): LiveData<Int>
    fun getUnreadMessageByRoomIdLiveData(roomId: String): LiveData<Int?>
}