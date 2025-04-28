package com.nextiva.nextivaapp.android.net

import com.nextiva.nextivaapp.android.models.net.platform.voicemail.BulkActionsConversationData
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.VoicemailDetails
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.VoicemailPatchData
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.VoicemailRatingBody
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.VoicemailsResponseBody
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface VoicemailApi {
    @GET("rest-api/voice/v2/users/voicemails?includeTranscriptions=true")
    fun getAllVoicemails(
        @Header("x-api-key") sessionId: String?,
        @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?
    ): Single<Response<VoicemailsResponseBody?>?>


    @Headers("Content-Type: application/json-patch+json")
    @PATCH("rest-api/voice/v2/users/voicemails/{voicemailId}")
    fun updateVoicemailRating(
        @Header("x-api-key") sessionId: String?,
        @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
        @Header("Accept") accept: String?,
        @Path("voicemailId") voicemailId: String,
        @Body jsonObject: ArrayList<VoicemailRatingBody>
    ): Single<Response<Void>>

    @GET("rest-api/voice/v2/users/voicemails/{voicemailId}")
    fun getVoicemailDetails(
                            @Header("x-api-key") sessionId: String?,
                            @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                            @Path("voicemailId") voicemailId: String): Single<Response<VoicemailDetails>>

    @DELETE("rest-api/voice/v2/users/voicemails/{voicemailId}")
    fun deleteVoicemail(
        @Header("x-api-key") sessionId: String?,
        @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
        @Path("voicemailId") voicemailId: String): Single<Response<ResponseBody>>

    @Headers("Content-Type: application/json-patch+json")
    @PATCH("rest-api/voice/v2/users/voicemails/{voicemailId}")
    fun patchVoicemail(
        @Header("x-api-key") sessionId: String?,
        @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
        @Path("voicemailId") voicemailId: String,
        @Body patchBody: ArrayList<VoicemailPatchData>): Single<Response<ResponseBody>>

    @POST("rest-api/conversation/v1/corp-accounts/{corpAccount}/users/{userId}/bulk-actions/deletes")
    fun deleteVoicemails(
        @Header("x-api-key") sessionId: String?,
        @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
        @Path("corpAccount") corpAccount: String?,
        @Path("userId") userId: String,
        @Body patchData: BulkActionsConversationData
    ): Single<Response<ResponseBody>>

}