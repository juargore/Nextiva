/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.features.rooms.db

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.features.rooms.RoomsEnums
import com.nextiva.nextivaapp.android.features.rooms.RoomsEnums.ConnectRoomsGroups
import com.nextiva.nextivaapp.android.features.rooms.api.ChatMessage
import com.nextiva.nextivaapp.android.features.rooms.api.ConnectRoom
import com.nextiva.nextivaapp.android.features.rooms.model.DbChatMessage
import com.nextiva.nextivaapp.android.features.rooms.model.DbRoom
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager
import com.nextiva.nextivaapp.android.managers.interfaces.ChatManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager.SettingsKey
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import io.reactivex.Completable
import io.reactivex.Single
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import javax.inject.Inject

class NextivaRoomsDbManager @Inject constructor(var application: Application,
                                                var schedulerProvider: SchedulerProvider,
                                                var sharedPreferencesManager: SharedPreferencesManager,
                                                var calendarManager: CalendarManager,
                                                var mChatManager: ChatManager) : RoomsDbManager {

    private var mRoomsDatabase: RoomsDatabase = RoomsDatabase.getRoomsDatabase(application)
    private var mRoomDao: RoomDao = mRoomsDatabase.roomDao()
    private var executorService = Executors.newSingleThreadExecutor()

    override fun saveRooms(rooms: ArrayList<ConnectRoom>): Completable {
        return Completable.fromAction {
            for (room in rooms) {
                mRoomDao.insertConnectRoom(DbRoom(room))
            }
        }.subscribeOn(schedulerProvider.io())
    }

    override fun saveRoomChatMessages(chatMessages: ArrayList<ChatMessage>): Completable {
        return Completable.fromAction {
            for (chatMessage in chatMessages) {
                mRoomDao.insertRoomChatMessage(DbChatMessage(chatMessage))
            }
        }.subscribeOn(schedulerProvider.io())
    }

    override fun updateRoomChatMessage(chatMessage: DbChatMessage): Completable {
        return Completable.fromAction {
            mRoomDao.insertRoomChatMessage(chatMessage)
        }.subscribeOn(schedulerProvider.io())
    }

    override fun getConnectRoomsGroupCount(group: ConnectRoomsGroups): LiveData<Int> {
        if (group == ConnectRoomsGroups.FAVORITES) {
            return mRoomDao.connectRoomsFavoritesCount;
        }

        return mRoomDao.connectRoomsCount
    }

    override fun markRoomFavorite(roomId: String, isFavorite: Boolean) {
        Completable
            .fromAction { mRoomDao.setFavorite(roomId, isFavorite) }
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribe()
    }

    override fun getConnectRoomsPagingSource(
        favoritesExpanded: Boolean,
        roomsExpanded: Boolean
    ): PagingSource<Int, DbRoom> {
        return mRoomDao.getAllRoomsPagingSource(favoritesExpanded, roomsExpanded)
    }

    override fun getAllRooms(): LiveData<List<DbRoom>> {
        return mRoomDao.getAllRoomsLiveData()
    }

    override fun getChatMessagePagingSource(roomId: String): PagingSource<Int, DbChatMessage> {
        return mRoomDao.getAllChatMessagesPagingSource(roomId)
    }

    override fun getAllChatMessages(roomId: String?): LiveData<List<DbChatMessage>> {
        return mRoomDao.getAllChatMessages(roomId)
    }

    override fun deleteChatMessage(roomId: String, messageId: String) {
        Completable
            .fromAction { mRoomDao.deleteChatMessage(roomId, messageId) }
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribe()
    }

    override fun undoDeleteChatMessage(message: DbChatMessage): Completable {
        return Completable.fromAction {
            mRoomDao.insertRoomChatMessage(message)
        }.subscribeOn(schedulerProvider.io())
    }

    override fun getRoom(roomId: String): LiveData<DbRoom?> {
        return mRoomDao.getRoomLiveData(roomId)
    }

    override fun getRoomCompletable(roomId: String): Single<DbRoom?> {
        return Single.fromCallable {
            mRoomDao.getRoom(roomId)
        }.subscribeOn(schedulerProvider.io())
    }

    override fun getMyRoomInThread(): DbRoom? {
        return executorService.submit(Callable { mRoomDao.myRoom }).get()
    }

    override fun getRoomInThread(contactUuids: List<String>): DbRoom? {
        return executorService.submit(Callable {
            mRoomDao.allRooms?.filter { dbRoom ->
                var matches = dbRoom.typeEnum() == RoomsEnums.ConnectRoomsTypes.INDIVIDUAL_CONVERSATION &&
                        (dbRoom.members?.count() ?: 0) == contactUuids.count()
                contactUuids.forEach { contactUuid ->
                    if (dbRoom.members?.firstOrNull { it.userUuid == contactUuid } == null) {
                        matches = false
                    }
                }
                matches
            }
        }).get()?.firstOrNull()
    }

    override fun saveAudioDataFromLinkWithReturn(roomId: String?, messageId: String, attachmentId: String?, link: String, sessionId: String, corpAcctNumber: String): Single<ByteArray?> {
        return Single.fromCallable {
            val byteArray = mChatManager.getAttachmentDataByteArray(link, sessionId, corpAcctNumber)
            if (byteArray != null) {
                mRoomDao.updateAttachmentAttribute(roomId, messageId, attachmentId) { attachment ->
                    attachment?.contentData = byteArray
                }
            }
            byteArray
        }.subscribeOn(schedulerProvider.io())
    }

    override fun saveAudioFileDuration(roomId: String?, messageId: String, attachmentId: String, duration: Long): Single<Long?> {
        return Single.fromCallable {
            mRoomDao.updateAttachmentAttribute(roomId, messageId, attachmentId) { attachment ->
                attachment?.duration = duration
            }
            duration
        }.subscribeOn(schedulerProvider.io())
    }

    override fun deleteRoom(roomId: String) {
        Completable
            .fromAction { mRoomDao.deleteRoom(roomId) }
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribe()
    }

    private fun getLastCacheTimestampKey(@SettingsKey key: String): String? {
        return key + SharedPreferencesManager.LAST_CACHE_TIMESTAMP_SUFFIX
    }

    override fun isCacheExpired(@SettingsKey key: String): Boolean {
        val lastCacheTimestamp: Long = sharedPreferencesManager.getLong(getLastCacheTimestampKey(key), 0)
        when (key) {
            SharedPreferencesManager.CONNECT_ROOMS -> if (lastCacheTimestamp < calendarManager.nowMillis - Constants.ONE_DAY_IN_MILLIS) {
                return true
            }
        }
        return false
    }

    override fun clearAndResetAllTables(): Boolean {
        if (mRoomsDatabase == null) {
            return false
        }

        mRoomsDatabase.beginTransaction()
        return try {
            mRoomsDatabase.clearAllTables()
            mRoomsDatabase.setTransactionSuccessful()
            true
        } catch (e: Exception) {
            false
        } finally {
            mRoomsDatabase.endTransaction()
        }
    }

    override fun getUnreadMessageCountInThread(): Int {
        return try {
            executorService.submit(Callable { mRoomDao.unreadMessageCount }).get()
        } catch (e: ExecutionException) {
            0
        } catch (e: InterruptedException) {
            0
        }
    }

    override fun getUnreadMessageCount(): Single<Int> {
        return Single.fromCallable { mRoomDao.unreadMessageCount }.subscribeOn(schedulerProvider.io())
    }

    override fun modifyUnreadMessageCountByRoomId(count: Int, roomId: String) {
        Single.just(count)
            .map {
                if(it != 0) { count + mRoomDao.getUnreadMessageCountByRoomId(roomId) } else { 0 }
            }.flatMap { unreadCount ->
                Single.fromCallable { mRoomDao.setUnreadMessageCountByRoomId(unreadCount, roomId) }.subscribeOn(schedulerProvider.io())
            }.ignoreElement()
            .subscribeOn(schedulerProvider.io())
            .subscribe()
    }

    override fun getUnreadMessageLiveData(types: List<String>): LiveData<Int> {
        return mRoomDao.getTotalUnreadMessagesLiveData(types)
    }

    override fun getUnreadMessageByRoomIdLiveData(roomId: String): LiveData<Int?> {
        return mRoomDao.getUnreadMessagesByRoomIdLiveData(roomId)
    }
}