package com.nextiva.nextivaapp.android.net

import com.nextiva.nextivaapp.android.models.net.platform.BulkUpdateUserMessageState
import com.nextiva.nextivaapp.android.models.net.platform.GenerateGroupIdPostBody
import com.nextiva.nextivaapp.android.models.net.platform.GroupIdResponse
import com.nextiva.nextivaapp.android.models.net.platform.MessageStatePutBody
import com.nextiva.nextivaapp.android.models.net.platform.PhoneNumberInformation
import com.nextiva.nextivaapp.android.models.net.platform.Product
import com.nextiva.nextivaapp.android.models.net.platform.SendMessagePostBody
import com.nextiva.nextivaapp.android.models.net.platform.SendMessageResponse
import com.nextiva.nextivaapp.android.models.net.platform.SmsMessages
import com.nextiva.nextivaapp.android.models.net.platform.UserMessageState
import com.nextiva.nextivaapp.android.models.net.platform.UserMessageStateResponse
import com.nextiva.nextivaapp.android.models.net.platform.messages.SmsMessageBulkActionList
import com.nextiva.nextivaapp.android.models.net.platform.teams.TeamsResponse
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface SmsMessagesApi {
    @GET("rest-api/iam/v1/licenses/products/me")
    fun getProducts(@Header("x-api-key") sessionId: String?,
                    @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?): Single<Response<List<Product>>>

    @GET("rest-api/phone-number/v2/phone-numbers/{phoneNumber}")
    fun getPhoneNumberInformation(@Header("x-api-key") sessionId: String?,
                                  @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                                  @Path("phoneNumber") phoneNumber: String): Single<Response<PhoneNumberInformation>>

    @GET("rest-api/phone-number/v2/phone-numbers/bulk")
    fun getTeamPhoneNumberInformation(@Header("x-api-key") sessionId: String?,
                                    @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                                    @Query("includingTeam") includingTeam: Boolean,
                                    @Query("phoneNumbers") phoneNumbers: ArrayList<String>): Single<Response<List<PhoneNumberInformation>>>

    @GET("rest-api/team/v1/corp-accounts/{corpAccount}/teams")
    fun getTeams(@Header("x-api-key") sessionId: String?,
                 @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                 @Path("corpAccount") corpAccount: String,
                 @Query("withVoice") withVoice: Boolean,
                 @Query("withMembers") withMembers: Boolean,
                 @Query("userId") userId: String,
                 ): Single<Response<TeamsResponse>>

    @GET("rest-api/conversation/v3/messages?collapseGroups=false&filter=channel:SMS&groupBy=groupId&sortOrder=NEWEST")
    fun getSmsConversations(
        @Header("x-api-key") sessionId: String?,
        @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
        @Query("maxGroupedResults") maxGroupedResults: Int,
        @Query("pageSize") pageSize: Int,
        @Query("pageNumber") pageNumber: Int): Single<Response<SmsMessages?>?>

    @GET("rest-api/conversation/v3/messages?&sortOrder=NEWEST&fullDetails=true")
    fun getSmsConversation(
            @Header("x-api-key") sessionId: String?,
            @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
            @Query("filter") filter: String,
            @Query("pageSize") pageSize: Int,
            @Query("pageNumber") pageNumber: Int): Single<Response<SmsMessages?>?>

    @POST("rest-api/sms/v1/messages/send")
    fun sendSmsMessages(
            @Header("x-api-key") sessionId: String?,
            @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
            @Body sendMessagePostBody: SendMessagePostBody): Single<Response<SendMessageResponse?>?>

    @PUT("rest-api/conversation/v2/message-states/{messageId}")
    fun updateReadStatus(
            @Header("x-api-key") sessionId: String?,
            @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
            @Body messageStatePutBody: MessageStatePutBody,
            @Path("messageId") messageId: String): Single<Response<ResponseBody>>

    @GET("/rest-api/conversation/v2/message-states")
    fun getMessageStateFromMessageId(@Header("x-api-key") sessionId: String?,
                                     @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                                     @Query(value = "messageIds") messageIds: Array<String>): Single<Response<ResponseBody>>

    @GET("rest-api/conversation/v2/messages?collapseGroups=true&filter=channel:SMS&groupBy=participants.phoneNumber&sortOrder=NEWEST")
    fun getMessageFromMessageIdV2(@Header("x-api-key") sessionId: String?,
                                     @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                                     @Query("messageId") messageId: String?): Single<Response<ResponseBody>>

    @POST("rest-api/sms/v1/messages/mms/send")
    fun sendMms(@Body partFile: MultipartBody,
                @Header("x-api-key") sessionId: String?,
                @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?): Single<Response<SendMessageResponse>>

    @GET("rest-api/conversation/v2/messages?collapseGroups=true&filter=channel:SMS&groupBy=participants.phoneNumber&sortOrder=NEWEST")
    fun getMessageFromMessageId(
            @Header("x-api-key") sessionId: String?,
            @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
            @Query("filter") messageId: String?): Single<Response<SmsMessages?>?>

    @PATCH("rest-api/sms/v1/messages/{messageId}/userMessageState")
    fun updateUserMessageState(
        @Header("x-api-key") sessionId: String?,
        @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
        @Body userMessageState: UserMessageState,
        @Path("messageId") messageId: String): Single<Response<UserMessageStateResponse?>?>

    @PATCH("rest-api/sms/v1/messages/userMessageState/")
    fun updateUserMessageState(
        @Header("x-api-key") sessionId: String?,
        @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
        @Body bulkUpdateUserMessageState: BulkUpdateUserMessageState): Single<Response<UserMessageStateResponse?>?>

    @GET("rest-api/conversation/v1/corp-accounts/{corpAcctNumber}/users/{userId}/bulk-actions?status=COMPLETE")
    fun checkForDeletedItems(
        @Header("x-api-key") sessionId: String?,
        @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
        @Path("corpAcctNumber") corpAcctNumber: String,
        @Path("userId") userId: String,
        @Query("createdAfter", encoded = true) createdAfter: String,
        @Query("pageNumber") page: Int): Single<Response<SmsMessageBulkActionList?>?>

    @POST("rest-api/sms/v1/messages/group-id")
    suspend fun generateGroupId(
        @Header("x-api-key") sessionId: String?,
        @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
        @Body body: GenerateGroupIdPostBody): Response<GroupIdResponse>

}
