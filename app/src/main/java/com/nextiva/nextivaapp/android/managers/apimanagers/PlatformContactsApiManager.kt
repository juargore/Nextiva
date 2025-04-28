package com.nextiva.nextivaapp.android.managers.apimanagers

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.core.content.ContextCompat
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformContactsRepository
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.managers.interfaces.NetManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.models.BulkContacts
import com.nextiva.nextivaapp.android.models.BulkContactsResult
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.models.net.platform.contacts.ConnectContact
import com.nextiva.nextivaapp.android.models.net.platform.contacts.ConnectContactBatchImportJobBody
import com.nextiva.nextivaapp.android.models.net.platform.contacts.ConnectContactBatchImportJobResponse
import com.nextiva.nextivaapp.android.models.net.platform.contacts.ConnectContactPatchBody
import com.nextiva.nextivaapp.android.models.net.platform.contacts.ConnectContactsResponse
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableCompletableObserver
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.util.Locale
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject


internal class PlatformContactsApiManager @Inject constructor(
    var application: Application,
    var logManager: LogManager,
    var netManager: NetManager,
    var schedulerProvider: SchedulerProvider,
    var sessionManager: SessionManager,
    var dbManager: DbManager,
    var sharedPreferencesManager: SharedPreferencesManager
) : BaseApiManager(application, logManager), PlatformContactsRepository {

    private val pageSize = 1000
    private val compositeDisposable = CompositeDisposable()

    internal var lastContactsRefreshTimestamp: Long
        get() = sharedPreferencesManager.getLong(SharedPreferencesManager.LAST_CONTACTS_REFRESHED_TIMESTAMP, 0L)
        set(timestamp) {
            sharedPreferencesManager.setLong(SharedPreferencesManager.LAST_CONTACTS_REFRESHED_TIMESTAMP, timestamp)
        }

    override fun clearCompositeDisposable() {
        compositeDisposable.clear()
    }

    override fun fetchContacts(
        forceRefresh: Boolean,
        onSaveFinishedCallback: () -> Unit,
        onErrorOccurredCallback: (() -> Unit)?
    ) {
        compositeDisposable.clear()

        getContacts(
            forceRefresh,
            "0",
            if (forceRefresh) 0L else lastContactsRefreshTimestamp,
            onSaveFinishedCallback,
            onErrorOccurredCallback
        )
    }

    private fun getContactApi(
        isAllContacts: Boolean,
        iterativeParam: AtomicReference<String>,
        lastModifiedAt: String
    ): Single<Response<ConnectContactsResponse>> {
        return if (isAllContacts) {
            netManager.getContactsApi()!!.getContacts(
                sessionManager.sessionId,
                sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
                pageSize.toString(), iterativeParam.get().toString()
            )
        } else {
            netManager.getContactsApi()!!.getContactsSince(
                sessionManager.sessionId,
                sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
                pageSize.toString(),
                lastModifiedAt,
                iterativeParam.get()
            )
        }
    }

    private fun getContacts(
        forceRefresh: Boolean,
        initialValue: String,
        bufferTimestamp: Long,
        onSaveFinishedCallback: () -> Unit,
        onErrorOccurredCallback: (() -> Unit)?
    ) {

        val isAllContacts = bufferTimestamp == 0L || forceRefresh
        val iteration: AtomicReference<String> = AtomicReference(initialValue)
        val stop = AtomicBoolean(false)
        val tempList = ArrayList<ConnectContact>()
        var retry = 3 // retries before dropping in case a request fails
        val timestamp = System.currentTimeMillis()

        io.reactivex.Observable.just(0)
            .flatMap {
                getContactApi(
                    isAllContacts,
                    iteration,
                    bufferTimestamp.toString()
                ).toObservable()
            }
            .flatMap { response ->
                if (!response.isSuccessful) {
                    stop.set(--retry <= 0)
                    if (stop.get()) {
                        return@flatMap io.reactivex.Observable.error(Throwable())
                    }
                }
                io.reactivex.Observable.just(response)
            }
            .subscribeOn(schedulerProvider.io())
            .repeatUntil { stop.get() }
            .subscribe(object : io.reactivex.Observer<Response<ConnectContactsResponse>> {
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {
                    logServerResponseError(e)
                    onErrorOccurredCallback?.invoke()
                }

                override fun onComplete() {
                    val nextivaContacts = ArrayList<NextivaContact>().apply {
                        addAll(tempList.map { it.toNextivaContact() })
                    }
                    if (nextivaContacts.isEmpty()) {
                        lastContactsRefreshTimestamp = timestamp
                        onSaveFinishedCallback.invoke()
                        return
                    }
                    if (isAllContacts && forceRefresh && nextivaContacts.isNotEmpty()) {
                        dbManager.deleteAllContacts(compositeDisposable)
                    }

                    val disposable = object : DisposableCompletableObserver() {
                        override fun onComplete() {
                            lastContactsRefreshTimestamp = timestamp
                            onSaveFinishedCallback.invoke()
                        }

                        override fun onError(e: Throwable) {
                            logServerResponseError(e)
                            onErrorOccurredCallback?.invoke()
                        }
                    }

                    if (nextivaContacts.isNotEmpty()) {
                        val xbertDrawable = ContextCompat.getDrawable(application, R.drawable.xbert_avatar)
                        val bitmap = xbertDrawable?.let { drawableToBitmap(it) }

                        for (contact in nextivaContacts) {
                            if (contact.aliases != null
                                && contact.aliases?.isNotBlank() == true
                                && contact.aliases?.lowercase(Locale.ROOT)
                                    ?.contains(Constants.Contacts.Aliases.XBERT_ALIASES) == true
                                && application.resources != null
                            ) {
                                val byteArrayOutput = ByteArrayOutputStream()
                                bitmap?.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutput)

                                contact.photoData = byteArrayOutput.toByteArray()
                                contact.avatarInfo.photoData = byteArrayOutput.toByteArray()
                            }
                        }
                    }

                    if (isAllContacts) { // Save All Contacts
                        dbManager.saveContacts(
                            nextivaContacts,
                            UUID.randomUUID().toString(),
                            true
                        )
                            .subscribeOn(schedulerProvider.io())
                            .observeOn(schedulerProvider.ui())
                            .subscribe(disposable)
                    } else { // Save contacts since specific date/time
                        dbManager.upsertContacts(nextivaContacts)
                            .subscribeOn(schedulerProvider.io())
                            .observeOn(schedulerProvider.ui())
                            .subscribe(disposable)
                    }
                }

                override fun onNext(response: Response<ConnectContactsResponse>) {
                    if (response.isSuccessful) {
                        logServerSuccess(response)

                        response.body()?.contactItems?.let { contacts ->
                            tempList.addAll(contacts)
                        }
                        response.body()?.nextPage?.let { nextPage ->
                            iteration.set(Uri.parse(nextPage).getQueryParameter("cursor"))
                        }

                        stop.set(response.body()?.nextPage.isNullOrEmpty())

                    } else {
                        logServerParseFailure(response)
                    }
                }
            })
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap? {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    override fun setContactFavorite(contactId: String, contactType: String, isFavorite: Boolean): Single<Boolean> {
        val callToMake: Single<Response<Void>> = if (isFavorite) {
            netManager.getContactsApi()!!.setContactFavorite(
                sessionManager.sessionId,
                sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
                contactId
            )
        } else {
            netManager.getContactsApi()!!.setContactNotFavorite(
                sessionManager.sessionId,
                sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
                contactId
            )
        }

        return callToMake
            .subscribeOn(schedulerProvider.io())
            .map { response ->
                if (response.isSuccessful) {
                    dbManager.markContactFavorite(contactId, isFavorite)
                    logServerSuccess(response)
                    true
                } else {
                    logServerParseFailure(response)
                    false
                }
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                false
            }
            .observeOn(schedulerProvider.ui())
    }

    override fun searchContactsWithTypeFilter(
        query: String,
        pageNumber: Int,
        pageSize: Int,
        contactTypes: ArrayList<String>
    ): Single<Response<ConnectContactsResponse>?> {
        if (query.isBlank()) {
            return Single.just(Response.success(ConnectContactsResponse(null, null, null, null, null, null)))
        }

        return netManager.getContactsApi()!!.searchContactsWithTypeFilter(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            contactTypes,
            query,
            pageNumber.toString(),
            pageSize.toString()
        )
            .subscribeOn(schedulerProvider.io())
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                null
            }
            .observeOn(schedulerProvider.ui())
    }

    override fun searchContacts(query: String, pageNumber: Int, pageSize: Int): Single<Response<ConnectContactsResponse>?> {
        if (query.isBlank()) {
            return Single.just(Response.success(ConnectContactsResponse(null, null, null, null, null, null)))
        }

        return netManager.getContactsApi()!!.searchContacts(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            query,
            pageNumber.toString(),
            pageSize.toString()
        )
            .subscribeOn(schedulerProvider.io())
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                null
            }
            .observeOn(schedulerProvider.ui())
    }

    override fun fetchRecentContacts(): Single<Boolean> {
        return netManager.getContactsApi()!!.getRecentContacts(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            50
        )
            .subscribeOn(schedulerProvider.io())
            .map { response ->
                if (response.isSuccessful) {
                    response.body()?.contactItems?.map { it.toNextivaContact() }?.let { contactItems ->
                        dbManager.saveRecentContacts(ArrayList(contactItems), UUID.randomUUID().toString())
                            .subscribeOn(schedulerProvider.io())
                            .observeOn(schedulerProvider.ui())
                            .subscribe(object : DisposableCompletableObserver() {
                                override fun onComplete() {}
                                override fun onError(e: Throwable) {}
                            })
                    }
                    logServerSuccess(response)
                    true
                } else {
                    logServerParseFailure(response)
                    false
                }
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                false
            }
            .observeOn(schedulerProvider.ui())
    }

    override fun postJobCount(contactCount: Int): Single<Response<ConnectContactBatchImportJobResponse>> {
        return netManager.getContactsApi()!!.postJobCount(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            ConnectContactBatchImportJobBody(contactCount)
        )
            .subscribeOn(schedulerProvider.io())
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                null
            }
            .observeOn(schedulerProvider.ui())
    }

    override fun postJobContacts(jobId: String, contacts: List<NextivaContact>): Single<Response<ResponseBody>> {
        val connectContacts: ArrayList<ConnectContact> = ArrayList()
        contacts.forEach { connectContacts.add(ConnectContact(it)) }

        return netManager.getContactsApi()!!.postJobContacts(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            jobId,
            connectContacts
        )
            .subscribeOn(schedulerProvider.io())
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                null
            }
            .observeOn(schedulerProvider.ui())
    }

    override suspend fun bulkExternalContactImportRequest(bulkContacts: BulkContacts): BulkContactsResult? {
        try {
            netManager.getContactsApi()?.let { contactsApi ->
                val response = contactsApi.bulkExternalContactImportRequestV2(
                    sessionManager.sessionId,
                    sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
                    bulkContacts = bulkContacts
                )
                if (response.isSuccessful) {
                    return response.body()
                } else {
                    val errorBody = response.errorBody()?.string().orEmpty()
                    mLogManager.logToFile(Enums.Logging.STATE_ERROR, "Error on API response ContactImportRequest: $errorBody")
                    return null
                }
            } ?: run {
                return null
            }
        } catch (e: Exception) {
            mLogManager.logToFile(Enums.Logging.STATE_ERROR, "Error ContactImportRequest bulkExternalContactImportRequest(): $e")
            logServerResponseError(e)
            return null
        }
    }

    override suspend fun bulkExternalContactImportRequestState(jobId: String): BulkContactsResult? {
        try {
            netManager.getContactsApi()?.let { contactsApi ->
                val response = contactsApi.bulkExternalContactImportRequestState(
                    sessionManager.sessionId,
                    sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
                    jobId = jobId
                )
                if (response.isSuccessful) {
                    return response.body()
                }
            }
        } catch (e: Exception) {
            logServerResponseError(e)
        }
        return null
    }

    override fun createContact(contact: NextivaContact): Single<Response<ConnectContact>?> {
        return netManager.getContactsApi()!!.createContact(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            ConnectContact(contact)
        )
            .subscribeOn(schedulerProvider.io())
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                null
            }
            .observeOn(schedulerProvider.ui())
    }

    override fun patchContact(contact: NextivaContact): Single<Response<ConnectContact>?> {
        return netManager.getContactsApi()!!.editContact(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            contact.userId,
            ConnectContactPatchBody(contact)
        )
            .subscribeOn(schedulerProvider.io())
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                null
            }
            .observeOn(schedulerProvider.ui())
    }

    override fun deleteContact(contact: NextivaContact): Single<Boolean> {
        return netManager.getContactsApi()!!.deleteContact(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            contact.userId
        )
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .map { response ->
                if (response.isSuccessful) {
                    logServerSuccess(response)
                    true
                } else {
                    logServerParseFailure(response)
                    false
                }
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                false
            }
            .observeOn(schedulerProvider.ui())
    }

    override fun checkApiHealth(): Single<Boolean> {
        return netManager.getContactsApi()!!.getContacts(
            sessionManager.sessionId,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
            "1", "1"
        )
            .subscribeOn(schedulerProvider.io())
            .map { response ->
                if (response.isSuccessful) {
                    logServerSuccess(response)
                    true
                } else {
                    logServerParseFailure(response)
                    false
                }
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                false
            }
    }

    override fun resetLastRefreshTimestamp() {
        lastContactsRefreshTimestamp = 0L
    }
}