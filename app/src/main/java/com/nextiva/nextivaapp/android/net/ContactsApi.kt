package com.nextiva.nextivaapp.android.net

import com.nextiva.nextivaapp.android.models.BulkContacts
import com.nextiva.nextivaapp.android.models.BulkContactsResult
import com.nextiva.nextivaapp.android.models.net.platform.contacts.ConnectContact
import com.nextiva.nextivaapp.android.models.net.platform.contacts.ConnectContactBatchImportJobBody
import com.nextiva.nextivaapp.android.models.net.platform.contacts.ConnectContactBatchImportJobResponse
import com.nextiva.nextivaapp.android.models.net.platform.contacts.ConnectContactPatchBody
import com.nextiva.nextivaapp.android.models.net.platform.contacts.ConnectContactsResponse
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ContactsApi {
    @GET("rest-api/contacts/v1/contacts")
    fun getContacts(@Header("x-api-key") sessionId: String?,
                    @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                    @Query("pageSize") pageSize: String,
                    @Query("cursor") cursor: String): Single<Response<ConnectContactsResponse>>

    @GET("rest-api/contacts/v1/contacts/recentContacts")
    fun getRecentContacts(
        @Header("x-api-key") sessionId: String?,
        @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
        @Query("pageSize") pageSize: Int
    ): Single<Response<ConnectContactsResponse>>

    @GET("rest-api/contacts/v1/contacts")
    fun getContactsSince(@Header("x-api-key") sessionId: String?,
                    @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                    @Query("pageSize") pageSize: String,
                    @Query("lastModifiedAt") lastModifiedAt: String,
                    @Query("cursor") cursor: String): Single<Response<ConnectContactsResponse>>

    @PUT("rest-api/contacts/v1/favoritecontacts/{id}")
    fun setContactFavorite(@Header("x-api-key") sessionId: String?,
                           @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                           @Path("id", encoded = true) contactId: String): Single<Response<Void>>

    @DELETE("rest-api/contacts/v1/favoritecontacts/{id}")
    fun setContactNotFavorite(@Header("x-api-key") sessionId: String?,
                           @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                           @Path("id", encoded = true) contactId: String): Single<Response<Void>>

    @GET("rest-api/contacts/v1/search/contacts")
    fun searchContacts(@Header("x-api-key") sessionId: String?,
                       @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                       @Query("query") query: String,
                       @Query("pageNumber") pageNumber: String,
                       @Query("pageSize") pageSize: String): Single<Response<ConnectContactsResponse>>

    @GET("rest-api/contacts/v1/search/contacts")
    fun searchContactsWithTypeFilter(@Header("x-api-key") sessionId: String?,
                                     @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                                     @Query("type") contactType: ArrayList<String>,
                                     @Query("query") query: String,
                                     @Query("pageNumber") pageNumber: String,
                                     @Query("pageSize") pageSize: String): Single<Response<ConnectContactsResponse>>

    @POST("rest-api/contacts/v1/contacts/data-integration-jobs")
    fun postJobCount(@Header("x-api-key") sessionId: String?,
                      @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                      @Body recordCount: ConnectContactBatchImportJobBody): Single<Response<ConnectContactBatchImportJobResponse>>

    @POST("rest-api/contacts/v1/contacts/data-integration-jobs/{jobId}/payload")
    fun postJobContacts(@Header("x-api-key") sessionId: String?,
                     @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                        @Path("jobId") jobId: String,
                     @Body contacts: ArrayList<ConnectContact>): Single<Response<ResponseBody>>

    @Deprecated(
        message = "This endpoint is deprecated and may be removed in future versions.",
        replaceWith = ReplaceWith("bulkExternalContactImportRequestV2()")
    )
    @POST("rest-api/contacts/v1/contacts/bulk-requests/bulk-import-external-contact-requests")
    suspend fun bulkExternalContactImportRequest(
        @Header("x-api-key") sessionId: String?,
        @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
        @Body bulkContacts: BulkContacts
    ): Response<BulkContactsResult>

    @POST("rest-api/contacts/v2/contacts/bulk-requests/bulk-import-external-contact-requests")
    suspend fun bulkExternalContactImportRequestV2(
        @Header("x-api-key") sessionId: String?,
        @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
        @Body bulkContacts: BulkContacts
    ): Response<BulkContactsResult>

    @PUT("rest-api/contacts/v1/contacts/bulk-requests/bulk-import-external-contact-requests/{id}/active/false")
    suspend fun cancelBulkImportRequest(
        @Header("x-api-key") sessionId: String?,
        @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
        @Path("id", encoded = true) jobId: String
    ): Response<Void>

    @GET("rest-api/contacts/v1/contacts/bulk-requests/bulk-import-external-contact-requests/{id}")
    suspend fun bulkExternalContactImportRequestState(
        @Header("x-api-key") sessionId: String?,
        @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
        @Path("id", encoded = true) jobId: String
    ): Response<BulkContactsResult>

    @POST("rest-api/contacts/v1/contacts")
    fun createContact(@Header("x-api-key") sessionId: String?,
                    @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                      @Body connectContact: ConnectContact): Single<Response<ConnectContact>>

    @PATCH("rest-api/contacts/v1/contacts/{id}")
    fun editContact(@Header("x-api-key") sessionId: String?,
                    @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                    @Path("id", encoded = true) contactId: String,
                    @Body connectContact: ConnectContactPatchBody
    ): Single<Response<ConnectContact>>

    @DELETE("rest-api/contacts/v1/contacts")
    fun deleteContact(@Header("x-api-key") sessionId: String?,
                      @Header("nextiva-context-corpAcctNumber") corpAccountNumber: String?,
                      @Query("ids", encoded = true) contactId: String): Single<Response<Void>>
}