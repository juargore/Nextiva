package com.nextiva.nextivaapp.android.managers.apimanagers.repositories

import com.nextiva.nextivaapp.android.features.rooms.api.ConnectRoom
import com.nextiva.nextivaapp.android.features.rooms.api.ConnectRoomsResponse
import com.nextiva.nextivaapp.android.features.rooms.api.ConnectRoomsReturn
import com.nextiva.nextivaapp.android.features.rooms.api.Page
import com.nextiva.nextivaapp.android.features.rooms.api.RoomAttachmentResponse
import com.nextiva.nextivaapp.android.features.rooms.api.SendChatMessageResponse
import com.nextiva.nextivaapp.android.models.NextivaContact
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import okhttp3.MultipartBody
import retrofit2.Response

interface PlatformRoomsRepository {
    fun fetchRooms(forceRefresh: Boolean, compositeDisposable: CompositeDisposable, onSaveFinishedCallback: () -> Unit)

    fun fetchMyRoom(forceRefresh: Boolean, compositeDisposable: CompositeDisposable, onSaveFinishedCallback: (ConnectRoom?) -> Unit)

    fun addRoomMembers(roomId: String, members: List<NextivaContact>): Single<ConnectRoomsReturn>

    fun getMyRoom(forceRefresh: Boolean): Single<ConnectRoomsReturn>

    fun getContactRoom(contactId: String): Single<ConnectRoom?>

    suspend fun getRoom(roomId: String): ConnectRoom?

    fun fetchContactRoom(contactId: String, compositeDisposable: CompositeDisposable, onSaveFinishedCallback: (ConnectRoom) -> Unit)

    fun setRoomsFavorite(contactId: String, isFavorite: Boolean): Single<Boolean>

    fun leaveRoom(roomId: String): Single<Boolean>

    fun searchRooms(query: String, pageNumber: Int, pageSize: Int): Single<Response<ConnectRoomsResponse>?>

    fun fetchChatMessages(roomId: String, pageNumber: Int, compositeDisposable: CompositeDisposable, onSaveFinishedCallback: (Page) -> Unit)

    fun sendChatMessage(roomId: String, text: String): Single<SendChatMessageResponse?>

    fun sendChatAttachments(roomId: String, attachments: List<MultipartBody.Part>): Single<RoomAttachmentResponse?>

    fun sendChatMessage(roomId: String, text: String, messageId: String): Single<SendChatMessageResponse?>

    fun sendChatAttachments(roomId: String, attachments: List<MultipartBody.Part>, messageId: String): Single<RoomAttachmentResponse?>

    fun deleteChatMessage(roomId: String, messageId: String): Single<Boolean>

    fun updateChatMessage(roomId: String, messageId: String, text: String): Single<SendChatMessageResponse?>

    fun createRoom(members: List<NextivaContact>): Single<ConnectRoom?>
}