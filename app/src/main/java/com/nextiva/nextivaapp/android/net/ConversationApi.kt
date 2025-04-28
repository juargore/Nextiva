package com.nextiva.nextivaapp.android.net

import com.nextiva.nextivaapp.android.models.net.platform.voice.VoiceMessageState
import com.nextiva.nextivaapp.android.models.net.platform.voice.VoiceMessagesResponse
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.BulkActionsConversationData
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ConversationApi {
    @GET("rest-api/conversation/v3/messages?collapseGroups=false&filter=(channel:VOICE%7Cchannel:VOICEMAIL)&sortOrder=NEWEST")
    fun getAllVoiceConversationMessages(
        @Header("x-api-key") sessionId: String?,
        @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
        @Query(value = "pageNumber") pageNumber: Int,
        @Query("pageSize") pageSize: Int): Single<Response<VoiceMessagesResponse>?>

    @PATCH("rest-api/conversation/v2/message-states")
    fun patchCallMessageState(@Header("x-api-key") sessionId: String?,
                              @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                              @Query("messageIds") messageIds: ArrayList<String>,
                              @Body messageState: VoiceMessageState): Single<Response<Void>>

    @DELETE("rest-api/conversation/v3/corp-accounts/{corpAccountNumber}/users/{userId}/messages/{messageId}")
    fun deleteMessage(@Header("x-api-key") sessionId: String?,
                         @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                         @Path("corpAccountNumber") corpAccountNumber2: String?,
                         @Path("userId") userId: String?,
                         @Path("messageId") messageId: String): Single<Response<Int>>

    @GET("rest-api/conversation/v3/corp-accounts/{corpAccountNumber}/users/{userId}/messages/count")
    fun getMessagesCount(@Header("x-api-key") sessionId: String?,
                         @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                         @Path("corpAccountNumber") corpAccountNumber2: String?,
                         @Path("userId") userId: String?,
                         @Query("channel") channel: String,
                         @Query("messageState.readStatus") readStatus: String): Single<Response<Int>>
    @DELETE("rest-api/conversation/v3/corp-accounts/{corpAccountNumber}/users/{userId}/messages/count/cache")
    fun deleteMessagesCountCache(@Header("x-api-key") sessionId: String?,
                         @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                         @Path("corpAccountNumber") corpAccountNumber2: String?,
                         @Path("userId") userId: String?,
                         @Query("channel") channel: String): Single<Response<Void>>

    @DELETE("rest-api/conversation/v2/corp-accounts/{corpAccountNumber}/users/{userId}")
    fun deleteSmsMessages(@Header("x-api-key") sessionId: String?,
                          @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                          @Path("corpAccountNumber") corpAccountNumber2: String?,
                          @Path("userId") userId: String?,
                          @Query("channel") channel: String): Single<Response<Void>>

    @POST("rest-api/conversation/v1/corp-accounts/{corpAccount}/users/{userId}/bulk-actions/deletes")
    fun bulkDeleteConversations(
        @Header("x-api-key") sessionId: String?,
        @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
        @Path("corpAccount") corpAccount: String?,
        @Path("userId") userId: String,
        @Body patchData: BulkActionsConversationData
    ): Single<Response<ResponseBody>>

    @POST("rest-api/conversation/v1/corp-accounts/{corpAcctNumber}/users/{userId}/bulk-actions/deletes")
    fun deleteMessage(
            @Header("x-api-key") sessionId: String?,
            @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
            @Path("corpAcctNumber") corpAcctNumber: String?,
            @Path("userId") userId: String,
            @Body request: BulkActionsConversationData
    ): Single<Response<ResponseBody>>

    @POST("rest-api/conversation/v1/corp-accounts/{corpAccount}/users/{userId}/bulk-actions/updates")
    fun bulkUpdateConversations(
        @Header("x-api-key") sessionId: String?,
        @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
        @Path("corpAccount") corpAccount: String?,
        @Path("userId") userId: String,
        @Body patchData: BulkActionsConversationData
    ): Single<Response<ResponseBody>>
}