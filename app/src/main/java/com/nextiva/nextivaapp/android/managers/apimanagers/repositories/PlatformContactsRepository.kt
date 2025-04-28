package com.nextiva.nextivaapp.android.managers.apimanagers.repositories

import com.nextiva.nextivaapp.android.models.BulkContacts
import com.nextiva.nextivaapp.android.models.BulkContactsResult
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.models.net.platform.contacts.ConnectContact
import com.nextiva.nextivaapp.android.models.net.platform.contacts.ConnectContactBatchImportJobResponse
import com.nextiva.nextivaapp.android.models.net.platform.contacts.ConnectContactsResponse
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response

interface PlatformContactsRepository {

    fun clearCompositeDisposable()
    fun fetchContacts(forceRefresh:Boolean, onSaveFinishedCallback: () -> Unit, onErrorOccurredCallback: (() -> Unit)? = null)

    fun setContactFavorite(contactId: String, contactType: String, isFavorite: Boolean): Single<Boolean>

    fun searchContacts(query: String, pageNumber: Int, pageSize: Int): Single<Response<ConnectContactsResponse>?>

    fun fetchRecentContacts(): Single<Boolean>

    fun searchContactsWithTypeFilter(query: String, pageNumber: Int, pageSize: Int, contactTypes: ArrayList<String>): Single<Response<ConnectContactsResponse>?>

    fun postJobCount(contactCount: Int): Single<Response<ConnectContactBatchImportJobResponse>>

    fun postJobContacts(jobId: String, contacts: List<NextivaContact>): Single<Response<ResponseBody>>

    suspend fun bulkExternalContactImportRequest(bulkContacts: BulkContacts): BulkContactsResult?

    suspend fun bulkExternalContactImportRequestState(jobId: String): BulkContactsResult?

    fun createContact(contact: NextivaContact): Single<Response<ConnectContact>?>

    fun patchContact(contact: NextivaContact): Single<Response<ConnectContact>?>

    fun deleteContact(contact: NextivaContact): Single<Boolean>

    fun checkApiHealth(): Single<Boolean>

    fun resetLastRefreshTimestamp()
}