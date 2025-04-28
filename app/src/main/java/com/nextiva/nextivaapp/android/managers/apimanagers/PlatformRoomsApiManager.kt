package com.nextiva.nextivaapp.android.managers.apimanagers

import android.app.Application
import com.nextiva.nextivaapp.android.features.rooms.RoomsEnums
import com.nextiva.nextivaapp.android.features.rooms.api.AddMemberDetails
import com.nextiva.nextivaapp.android.features.rooms.api.AddMemberRequest
import com.nextiva.nextivaapp.android.features.rooms.api.ConnectRoom
import com.nextiva.nextivaapp.android.features.rooms.api.ConnectRoomFavoriteBody
import com.nextiva.nextivaapp.android.features.rooms.api.ConnectRoomFavoriteMemberBody
import com.nextiva.nextivaapp.android.features.rooms.api.ConnectRoomMember
import com.nextiva.nextivaapp.android.features.rooms.api.ConnectRoomsResponse
import com.nextiva.nextivaapp.android.features.rooms.api.ConnectRoomsReturn
import com.nextiva.nextivaapp.android.features.rooms.api.GetChatMessageResponse
import com.nextiva.nextivaapp.android.features.rooms.api.Page
import com.nextiva.nextivaapp.android.features.rooms.api.RoomAttachmentResponse
import com.nextiva.nextivaapp.android.features.rooms.api.SendChatMessageRequest
import com.nextiva.nextivaapp.android.features.rooms.api.SendChatMessageResponse
import com.nextiva.nextivaapp.android.features.rooms.api.SendMessageMemberDetails
import com.nextiva.nextivaapp.android.features.rooms.db.RoomsDbManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformRoomsRepository
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.managers.interfaces.NetManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

internal class PlatformRoomsApiManager @Inject constructor(
    var application: Application,
    var logManager: LogManager,
    var netManager: NetManager,
    var schedulerProvider: SchedulerProvider,
    var sessionManager: SessionManager,
    var dbManager: RoomsDbManager,
    var sharedPreferencesManager: SharedPreferencesManager
) : BaseApiManager(application, logManager), PlatformRoomsRepository {

    companion object {
        const val CHAT_MESSAGE_PAGE_SIZE = 25
        const val START_PAGE_NUMBER = 1
    }

    override fun fetchRooms(forceRefresh: Boolean, compositeDisposable: CompositeDisposable, onSaveFinishedCallback: () -> Unit) {
        compositeDisposable.add(
            getAllRooms(forceRefresh)
                .subscribe { roomsRepository ->
                    if (roomsRepository.totalCount != null) {
                        roomsRepository.roomsList?.let { rooms ->
                            dbManager.saveRooms(rooms)
                                .observeOn(schedulerProvider.ui())
                                .subscribe(object : DisposableCompletableObserver() {
                                    override fun onComplete() {
                                        onSaveFinishedCallback()
                                    }

                                    override fun onError(e: Throwable) {
                                        onSaveFinishedCallback()
                                    }
                                })
                        }
                    } else {
                        onSaveFinishedCallback()
                    }
                }
        )
    }

    private fun getAllRooms(forceRefresh: Boolean): Single<ConnectRoomsReturn> {
        if (forceRefresh || dbManager.isCacheExpired(SharedPreferencesManager.CONNECT_ROOMS)) {
            return netManager.getRoomsApi()?.getRooms(
                sessionId = sessionManager.sessionId,
                corpAccountNumber = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
                pageNumber = null,
                pageSize = null,
                adminUserUuid = null,
                memberUserUuid = sessionManager.userInfo?.comNextivaUseruuid,
                meetingPersonalizedId = null,
                mediaCallMeetingId = null,
                name = null,
                type = null
            )
                ?.subscribeOn(schedulerProvider.io())
                ?.map { response ->
                    var totalCount = 0
                    val roomList: ArrayList<ConnectRoom> = ArrayList()

                    if (response.isSuccessful) {
                        response.body()?.let { responseBody ->
                            responseBody.roomItems?.forEach {
                                if (it.archived != null && !it.archived!!) {
                                    roomList.add(it)
                                }
                            }
                            totalCount = roomList.size
                        }
                        ConnectRoomsReturn(totalCount, roomList)
                    } else {
                        logServerParseFailure(response)
                        ConnectRoomsReturn(null, null)
                    }
                }
                ?.onErrorReturn { throwable ->
                    logServerResponseError(throwable)
                    ConnectRoomsReturn(null, null)
                } ?: Single.just(ConnectRoomsReturn(null, null))
        }

        return Single.just(ConnectRoomsReturn(null, null))
    }

    override fun fetchMyRoom(forceRefresh: Boolean, compositeDisposable: CompositeDisposable, onSaveFinishedCallback: (ConnectRoom?) -> Unit) {
        compositeDisposable.add(
            getMyRoom(forceRefresh)
                .subscribe { roomsRepository ->
                    if (roomsRepository.totalCount != null) {
                        roomsRepository.roomsList?.let { rooms ->
                            dbManager.saveRooms(rooms)
                                .observeOn(schedulerProvider.ui())
                                .subscribe(object : DisposableCompletableObserver() {
                                    override fun onComplete() {
                                        onSaveFinishedCallback(rooms.firstOrNull())
                                    }

                                    override fun onError(e: Throwable) {
                                        onSaveFinishedCallback(rooms.firstOrNull())
                                    }
                                })
                        }
                    } else {
                        onSaveFinishedCallback(null)
                    }
                }
        )
    }

    override fun fetchContactRoom(contactId: String, compositeDisposable: CompositeDisposable, onSaveFinishedCallback: (ConnectRoom) -> Unit) {
        compositeDisposable.add(
            getContactRoom(contactId)
                .subscribe { room ->
                    room?.let { room ->
                        dbManager.saveRooms(arrayListOf(room))
                            .observeOn(schedulerProvider.ui())
                            .subscribe(object : DisposableCompletableObserver() {
                                override fun onComplete() {
                                    onSaveFinishedCallback(room)
                                }

                                override fun onError(e: Throwable) {
                                    onSaveFinishedCallback(room)
                                }
                            })
                    }
                }
        )
    }

    override fun addRoomMembers(roomId: String, members: List<NextivaContact>): Single<ConnectRoomsReturn> {
        val roomsApi = netManager.getRoomsApi() ?: return Single.just(ConnectRoomsReturn(null, null))

        val memberIds = members.map { it.userId }
        val memberDetails = members.map { contact ->
            AddMemberDetails(
                contact.userId,
                contact.firstName,
                contact.lastName,
                contact.emailAddresses?.firstOrNull()?.address
            )
        }
        val sender = sessionManager.userInfo?.comNextivaUseruuid?.let {
            AddMemberDetails(
                it,
                sessionManager.userInfo?.comNextivaFirstName,
                sessionManager.userInfo?.comNextivaLastName,
                sessionManager.userInfo?.comNextivaEmail
            )
        }
        val addMemberRequest = sender?.let { AddMemberRequest(memberIds, memberDetails, it) }

        return addMemberRequest?.let {
            roomsApi.addRoomMembers(
                sessionId = sessionManager.sessionId,
                corpAccountNumber = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
                body = it,
                roomId
            )
                .subscribeOn(schedulerProvider.io())
                .map { response ->
                    var totalCount = 0
                    val roomList: ArrayList<ConnectRoom> = ArrayList()
                    if (response.isSuccessful) {
                        response.body()?.let { responseBody ->
                            responseBody.room?.let {
                                dbManager.getMyRoomInThread()?.roomId?.let { myRoomId ->
                                    if (it.id == myRoomId) {
                                        it.type = RoomsEnums.ConnectRoomsTypes.CURRENT_USER_MY_ROOM.value
                                    }
                                }
                                roomList.add(it)
                            }
                            totalCount = roomList.size
                        }
                        ConnectRoomsReturn(totalCount, roomList)
                    } else {
                        logServerParseFailure(response)
                        ConnectRoomsReturn(null, null)
                    }
                }
                .onErrorReturn { throwable ->
                    logServerResponseError(throwable)
                    ConnectRoomsReturn(null, null)
                }
        } ?: Single.just(ConnectRoomsReturn(null, null))
    }

    override fun createRoom(members: List<NextivaContact>): Single<ConnectRoom?> {
        val roomsApi = netManager.getRoomsApi() ?: return Single.just(null)
        val roomMembers = members.map { contact -> ConnectRoomMember(contact) }
        val createRoomRequest = ConnectRoom(
            members = ArrayList(roomMembers),
            type = RoomsEnums.ConnectRoomsTypes.INDIVIDUAL_CONVERSATION.value
        )

        return roomsApi.createRoom(
            sessionId = sessionManager.sessionId,
            corpAccountNumber = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            body = createRoomRequest
        )
            .subscribeOn(schedulerProvider.io())
            .map { response ->
                var room: ConnectRoom? = null
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        responseBody.room?.let {
                            room = it
                        }
                    }
                } else {
                    logServerParseFailure(response)
                }
                room
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                null
            }
    }

    override fun getMyRoom(forceRefresh: Boolean): Single<ConnectRoomsReturn> {
        if (forceRefresh || dbManager.isCacheExpired(SharedPreferencesManager.CONNECT_ROOMS)) {
            return netManager.getRoomsApi()?.getMyRoom(
                sessionId = sessionManager.sessionId,
                corpAccountNumber = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
                memberUserUuid = sessionManager.userInfo?.comNextivaUseruuid
            )
                ?.subscribeOn(schedulerProvider.io())
                ?.map { response ->
                    var totalCount = 0
                    val roomList: ArrayList<ConnectRoom> = ArrayList()

                    if (response.isSuccessful) {
                        response.body()?.let { responseBody ->
                            responseBody.room?.let {
                                it.type = RoomsEnums.ConnectRoomsTypes.CURRENT_USER_MY_ROOM.value
                                roomList.add(it)
                            }
                            totalCount = roomList.size
                        }
                        ConnectRoomsReturn(totalCount, roomList)
                    } else {
                        logServerParseFailure(response)
                        ConnectRoomsReturn(null, null)
                    }
                }
                ?.onErrorReturn { throwable ->
                    logServerResponseError(throwable)
                    ConnectRoomsReturn(null, null)
                } ?: Single.just(ConnectRoomsReturn(null, null))
        }

        return Single.just(ConnectRoomsReturn(null, null))
    }

    override fun getContactRoom(contactId: String): Single<ConnectRoom?> {
        return netManager.getRoomsApi()?.getMyRoom(
            sessionId = sessionManager.sessionId,
            corpAccountNumber = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            memberUserUuid = contactId
        )
            ?.subscribeOn(schedulerProvider.io())
            ?.map { response ->
                var room: ConnectRoom? = null
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        responseBody.room?.let {
                            room = it
                        }
                    }
                } else {
                    logServerParseFailure(response)
                }
                room
            }
            ?.onErrorReturn { throwable ->
                logServerResponseError(throwable)
                null
            } ?: Single.just(null)
    }

    override suspend fun getRoom(roomId: String): ConnectRoom? {
        val response = netManager.getRoomsApi()?.getRoom(
            sessionId = sessionManager.sessionId,
            corpAccountNumber = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            roomId = roomId
        )

        return if (response?.isSuccessful == true) {
            response.body()?.room
        } else {
            if (response != null) {
                logServerParseFailure(response)
            }
            null
        }
    }

    override fun setRoomsFavorite(roomId: String, isFavorite: Boolean): Single<Boolean> {
        dbManager.markRoomFavorite(roomId, isFavorite)

        val callToMake: Single<Response<Void>> = netManager.getRoomsApi()?.setRoomFavorite(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            ConnectRoomFavoriteBody(ConnectRoomFavoriteMemberBody(isFavorite)),
            sessionManager.userInfo?.comNextivaUseruuid.toString(),
            roomId
        ) ?: return Single.just(false)

        return successOrFailureApiCall(callToMake)
    }

    override fun leaveRoom(roomId: String): Single<Boolean> {
        val callToMake: Single<Response<Void>> = netManager.getRoomsApi()?.leaveRoom(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            sessionManager.userInfo?.comNextivaUseruuid.toString(),
            roomId
        ) ?: return Single.just(false)

        return successOrFailureApiCall(callToMake)
    }

    private fun successOrFailureApiCall(callToMake: Single<Response<Void>>): Single<Boolean> {
        return callToMake
            .subscribeOn(schedulerProvider.io())
            .map { response ->
                if (response.isSuccessful) {
                    logServerSuccess(response)
                } else {
                    logServerParseFailure(response)
                }
                response.isSuccessful
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                false
            }
            .observeOn(schedulerProvider.ui())
    }

    override fun searchRooms(query: String, pageNumber: Int, pageSize: Int): Single<Response<ConnectRoomsResponse>?> {
        TODO("Not yet implemented")
    }

    override fun fetchChatMessages(roomId: String, pageNumber: Int, compositeDisposable: CompositeDisposable, onSaveFinishedCallback: (Page) -> Unit) {
        compositeDisposable.add(
            requestChatMessagePage(roomId, pageNumber)
                .subscribe { chatMessageReturn ->
                    chatMessageReturn?.messageItems?.let {
                        dbManager.saveRoomChatMessages(it)
                            .observeOn(schedulerProvider.ui())
                            .subscribe(object : DisposableCompletableObserver() {
                                override fun onComplete() {
                                    onSaveFinishedCallback(chatMessageReturn.pageInfo)
                                }

                                override fun onError(throwable: Throwable) {
                                    logServerResponseError(throwable)
                                }
                            })
                    }
                }
        )
    }

    private fun requestChatMessagePage(roomId: String, pageNumber: Int): Single<GetChatMessageResponse?> {
        return netManager.getRoomsApi()?.getRoomMessages(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            roomId, pageNumber.toString(), CHAT_MESSAGE_PAGE_SIZE.toString()
        )
            ?.subscribeOn(schedulerProvider.io())
            ?.map { response ->
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        logServerSuccess(response)
                        responseBody
                    }
                } else {
                    logServerParseFailure(response)
                    null
                }
            }
            ?.onErrorReturn { throwable ->
                logServerResponseError(throwable)
                null
            } ?: Single.just(null)
    }

    override fun sendChatMessage(roomId: String, text: String): Single<SendChatMessageResponse?> {
        val roomsApi = netManager.getRoomsApi() ?: return Single.just(null)
        return roomsApi.sendRoomMessages(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            SendChatMessageRequest(text, arrayListOf(memberDetails())),
            roomId
        )
            .subscribeOn(schedulerProvider.io())
            .map { response ->
                if (response.isSuccessful) {
                    logServerSuccess(response)
                } else {
                    logServerParseFailure(response)
                }
                response.body()
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                null
            }
            .observeOn(schedulerProvider.ui())
    }

    override fun sendChatAttachments(roomId: String, attachments: List<MultipartBody.Part>): Single<RoomAttachmentResponse?> {
        val roomsApi = netManager.getRoomsApi() ?: return Single.just(null)
        return roomsApi.sendRoomAttachments(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            attachments,
            roomId
        )
            .subscribeOn(schedulerProvider.io())
            .map { response ->
                if (response.isSuccessful) {
                    logServerSuccess(response)
                } else {
                    logServerParseFailure(response)
                }
                response.body()
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                null
            }
            .observeOn(schedulerProvider.ui())
    }

    override fun sendChatMessage(roomId: String, text: String, messageId: String): Single<SendChatMessageResponse?> {
        val roomsApi = netManager.getRoomsApi() ?: return Single.just(null)
        return roomsApi.sendRoomMessages(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            SendChatMessageRequest(text, arrayListOf(memberDetails())),
            roomId,
            messageId
        )
            .subscribeOn(schedulerProvider.io())
            .map { response ->
                if (response.isSuccessful) {
                    logServerSuccess(response)
                } else {
                    logServerParseFailure(response)
                }
                response.body()
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                null
            }
            .observeOn(schedulerProvider.ui())
    }

    override fun sendChatAttachments(roomId: String, attachments: List<MultipartBody.Part>, messageId: String): Single<RoomAttachmentResponse?> {
        val roomsApi = netManager.getRoomsApi() ?: return Single.just(null)
        return roomsApi.sendRoomAttachments(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            attachments,
            roomId,
            messageId
        )
            .subscribeOn(schedulerProvider.io())
            .map { response ->
                if (response.isSuccessful) {
                    logServerSuccess(response)
                } else {
                    logServerParseFailure(response)
                }
                response.body()
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                null
            }
            .observeOn(schedulerProvider.ui())
    }

    override fun deleteChatMessage(roomId: String, messageId: String): Single<Boolean> {
        val roomsApi = netManager.getRoomsApi() ?: return Single.just(false)
        return roomsApi.deleteRoomMessage(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            roomId, messageId
        )
            .subscribeOn(schedulerProvider.io())
            .map { response ->
                if (response.isSuccessful) {
                    logServerSuccess(response)
                } else {
                    logServerParseFailure(response)
                }
                response.isSuccessful
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                false
            }
            .observeOn(schedulerProvider.ui())
    }

    override fun updateChatMessage(roomId: String, messageId: String, text: String): Single<SendChatMessageResponse?> {
        val roomsApi = netManager.getRoomsApi() ?: return Single.just(null)
        return roomsApi.updateRoomMessage(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            SendChatMessageRequest(text, arrayListOf(memberDetails())),
            roomId, messageId
        )
            .subscribeOn(schedulerProvider.io())
            .map { response ->
                if (response.isSuccessful) {
                    logServerSuccess(response)
                } else {
                    logServerParseFailure(response)
                }
                response.body()
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                null
            }
            .observeOn(schedulerProvider.ui())
    }

    private fun memberDetails(): SendMessageMemberDetails {
        return SendMessageMemberDetails(
            id = sessionManager.userInfo?.comNextivaUseruuid.toString(),
            firstName = sessionManager.userInfo?.comNextivaFirstName.toString(),
            lastName = sessionManager.userInfo?.comNextivaLastName.toString(),
            email = sessionManager.userInfo?.comNextivaEmail.toString()
        )
    }
}
