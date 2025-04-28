package com.nextiva.nextivaapp.android.mocks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import com.nextiva.nextivaapp.android.features.rooms.RoomsEnums
import com.nextiva.nextivaapp.android.features.rooms.api.ChatMessage
import com.nextiva.nextivaapp.android.features.rooms.api.ConnectRoom
import com.nextiva.nextivaapp.android.features.rooms.db.RoomsDbManager
import com.nextiva.nextivaapp.android.features.rooms.model.DbChatMessage
import com.nextiva.nextivaapp.android.features.rooms.model.DbRoom
import io.reactivex.Completable
import io.reactivex.Single
import org.mockito.kotlin.mock
import javax.inject.Inject

open class FakeRoomsDbManager @Inject constructor() : RoomsDbManager {
    override fun saveRooms(contacts: ArrayList<ConnectRoom>): Completable {
        return Completable.never()
    }

    override fun saveRoomChatMessages(chatMessages: ArrayList<ChatMessage>): Completable {
        return Completable.never()
    }

    override fun updateRoomChatMessage(chatMessage: DbChatMessage): Completable {
        return Completable.never()
    }

    override fun getConnectRoomsGroupCount(group: RoomsEnums.ConnectRoomsGroups): LiveData<Int> {
        return MutableLiveData()
    }

    override fun markRoomFavorite(roomId: String, isFavorite: Boolean) {
    }

    override fun getConnectRoomsPagingSource(
        favoritesExpanded: Boolean,
        roomsExpanded: Boolean
    ): PagingSource<Int, DbRoom> {
        return mock()
    }

    override fun getAllRooms(): LiveData<List<DbRoom>> {
        return MutableLiveData()
    }

    override fun getChatMessagePagingSource(roomId: String): PagingSource<Int, DbChatMessage> {
        return mock()
    }

    override fun isCacheExpired(key: String): Boolean {
        return false
    }

    override fun getAllChatMessages(roomId: String?): LiveData<List<DbChatMessage>> {
        return MutableLiveData()
    }

    override fun deleteChatMessage(roomId: String, message: String) {
    }

    override fun undoDeleteChatMessage(message: DbChatMessage): Completable {
        return Completable.never()
    }

    override fun getRoom(roomId: String): LiveData<DbRoom?> {
        return MutableLiveData()
    }

    override fun getRoomCompletable(roomId: String): Single<DbRoom?> {
        return Single.just(null)
    }

    override fun getMyRoomInThread(): DbRoom? {
        return null
    }

    override fun getRoomInThread(contactUuids: List<String>): DbRoom? {
        return null
    }

    override fun saveAudioDataFromLinkWithReturn(
        roomId: String?,
        messageId: String,
        attachmentId: String?,
        link: String,
        sessionId: String,
        corpAcctNumber: String
    ): Single<ByteArray?> {
        return Single.just(null)}

    override fun saveAudioFileDuration(roomId: String?, messageId: String, attachmentId: String, duration: Long): Single<Long?> {
        return Single.just(null)
    }

    override fun deleteRoom(roomId: String) {
    }

    override fun clearAndResetAllTables(): Boolean {
        return false
    }

    override fun getUnreadMessageCountInThread(): Int {
        return 0
    }

    override fun getUnreadMessageCount(): Single<Int> {
        return Single.just(null)
    }

    override fun modifyUnreadMessageCountByRoomId(count: Int, roomId: String) {
    }

    override fun getUnreadMessageLiveData(types: List<String>): LiveData<Int> {
        return MutableLiveData()
    }

    override fun getUnreadMessageByRoomIdLiveData(roomId: String): LiveData<Int?> {
        return MutableLiveData()
    }
}
