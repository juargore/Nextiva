package com.nextiva.nextivaapp.android.features.rooms.api

import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface RoomsApi {
    @GET("/rest-api/rooms/v1/rooms_orchestration/rooms")
    fun getRooms(@Header("x-api-key") sessionId: String?,
                       @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                       @Query("name") name: String?,
                       @Query("type") type: String?,
                       @Query("adminUserUuid") adminUserUuid: String?,
                       @Query("memberUserUuid") memberUserUuid: String?,
                       @Query("meetingPersonalizedId") meetingPersonalizedId: String?,
                       @Query("mediaCallMeetingId") mediaCallMeetingId: String?,
                       @Query(value = "pageNumber") pageNumber: String?,
                       @Query("pageSize") pageSize: String?): Single<Response<ConnectRoomsResponse>>

    @GET("/rest-api/rooms/v1/rooms_orchestration/rooms/{roomId}")
    suspend fun getRoom(@Header("x-api-key") sessionId: String?,
                @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                @Path("roomId") roomId: String): Response<ConnectRoomResponse?>

    @POST("/rest-api/rooms/v1/rooms_orchestration/rooms")
    fun createRoom(@Header("x-api-key") sessionId: String?,
                   @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                   @Body body: ConnectRoom): Single<Response<ConnectMyRoomResponse>>

    @GET("/rest-api/rooms/v1/rooms_orchestration/my-rooms/{memberUserUuid}")
    fun getMyRoom(@Header("x-api-key") sessionId: String?,
                 @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                 @Path("memberUserUuid") memberUserUuid: String?): Single<Response<ConnectMyRoomResponse>>

    @POST("/rest-api/rooms/v1/rooms_orchestration/rooms/{roomId}/members")
    fun addRoomMembers(@Header("x-api-key") sessionId: String?,
                       @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                       @Body body: AddMemberRequest,
                       @Path("roomId") roomId: String): Single<Response<ConnectMyRoomResponse>>

    @PATCH("/rest-api/rooms/v1/rooms_orchestration/rooms/{roomId}/members/{userId}")
    fun setRoomFavorite(@Header("x-api-key") sessionId: String?,
                        @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                        @Body isFavorite: ConnectRoomFavoriteBody,
                        @Path("userId") userId: String,
                        @Path("roomId") roomId: String): Single<Response<Void>>

    @DELETE("/rest-api/rooms/v1/rooms_orchestration/rooms/{roomId}/members/{userId}")
    fun leaveRoom(@Header("x-api-key") sessionId: String?,
                        @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                        @Path("userId") userId: String,
                        @Path("roomId") roomId: String): Single<Response<Void>>

    @GET("rest-api/chat/v1/rooms/{roomId}/messages")
    fun getRoomMessages(@Header("x-api-key") sessionId: String?,
                        @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                        @Path("roomId") roomId: String,
                        @Query(value = "pageNumber") pageNumber: String,
                        @Query("pageSize") pageSize: String): Single<Response<GetChatMessageResponse>>

    @POST("rest-api/chat/v1/rooms/{roomId}/messages")
    fun sendRoomMessages(@Header("x-api-key") sessionId: String?,
                         @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                         @Body body: SendChatMessageRequest,
                         @Path("roomId") roomId: String): Single<Response<SendChatMessageResponse>>

    @Multipart
    @POST("rest-api/chat/v1/rooms/{roomId}/attachments")
    fun sendRoomAttachments(@Header("x-api-key") sessionId: String?,
                            @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                            @Part files: List<MultipartBody.Part>,
                            @Path("roomId") roomId: String): Single<Response<RoomAttachmentResponse>>

    @POST("rest-api/chat/v1/rooms/{roomId}/messages/{messageId}/attachments-message")
    fun sendRoomMessages(@Header("x-api-key") sessionId: String?,
                         @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                         @Body body: SendChatMessageRequest,
                         @Path("roomId") roomId: String,
                         @Path("messageId") messageId: String): Single<Response<SendChatMessageResponse>>

    @Multipart
    @POST("rest-api/chat/v1/rooms/{roomId}/messages/{messageId}/attachments")
    fun sendRoomAttachments(@Header("x-api-key") sessionId: String?,
                            @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                            @Part files: List<MultipartBody.Part>,
                            @Path("roomId") roomId: String,
                            @Path("messageId") messageId: String): Single<Response<RoomAttachmentResponse>>

    @DELETE("rest-api/chat/v1/rooms/{roomId}/messages/{messageId}")
    fun deleteRoomMessage(@Header("x-api-key") sessionId: String?,
                          @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                          @Path("roomId") roomId: String,
                          @Path("messageId") messageId: String): Single<Response<Void>>

    @PATCH("rest-api/chat/v1/rooms/{roomId}/messages/{messageId}")
    fun updateRoomMessage(@Header("x-api-key") sessionId: String?,
                         @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                         @Body body: SendChatMessageRequest,
                         @Path("roomId") roomId: String,
                         @Path("messageId") messageId: String): Single<Response<SendChatMessageResponse>>
}