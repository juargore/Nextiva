package com.nextiva.nextivaapp.android.adapters.pagedlistadapters.mediators

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.rxjava2.RxRemoteMediator
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.model.SmsTeam
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.SmsManagementRepository
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.models.SmsMessage
import com.nextiva.nextivaapp.android.util.CallUtil
import io.reactivex.Single
import io.reactivex.observers.DisposableCompletableObserver
import kotlin.math.ceil

@OptIn(ExperimentalPagingApi::class)
class SmsMessageRemoteMediator (
    private val smsManagementRepository: SmsManagementRepository,
    private val dbManager: DbManager,
    sessionManager: SessionManager
): RxRemoteMediator<Int, SmsMessage>() {

    companion object {
        const val PAGE_SIZE = 60
    }

    private var totalCount: Int = 0
    private var lastPageFetched: Int = 0
    private var isEditModeEnabled = false

    private var ourUuid: String? = null
    private var ourNumber: String? = null
    private var allSavedTeams: List<SmsTeam>? = null

    init {
        ourUuid = sessionManager.currentUser?.userUuid
        ourNumber = CallUtil.getCountryCode() + sessionManager.userDetails?.telephoneNumber
        allSavedTeams = sessionManager.allTeams
    }

    fun reset() {
        lastPageFetched = 0
    }

    override fun initializeSingle(): Single<InitializeAction> {
        return if (dbManager.isCacheExpired(SharedPreferencesManager.SMS_CONVERSATION_MESSAGES)) {
            Single.just(InitializeAction.LAUNCH_INITIAL_REFRESH)
        } else {
            Single.just(InitializeAction.SKIP_INITIAL_REFRESH)
        }
    }

    fun updateEditModeState(isEditModeEnabled: Boolean){
        this.isEditModeEnabled = isEditModeEnabled
    }

    override fun loadSingle(loadType: LoadType, state: PagingState<Int, SmsMessage>): Single<MediatorResult> {
        if (isEditModeEnabled){
            return Single.just(MediatorResult.Success(true))
        }

        var pageToFetch = lastPageFetched + 1
        val lastPage = ceil(totalCount.toDouble() / PAGE_SIZE).toInt()

        when (loadType) {
            LoadType.REFRESH -> {
                if (totalCount != 0 && (pageToFetch > lastPage)) {
                    return Single.just(MediatorResult.Success(true))
                }
            }

            LoadType.PREPEND -> {
                return Single.just(MediatorResult.Success(true))
            }

            LoadType.APPEND -> {
                if (totalCount != 0 && (pageToFetch > lastPage)) {
                    return Single.just(MediatorResult.Success(true))
                }
            }
        }

        return smsManagementRepository.getSmsConversationsForMediator(pageToFetch)
            .map { smsMessageResponse ->
                totalCount = smsMessageResponse.totalCount ?: 0
                if (smsMessageResponse.data != null) {
                    dbManager.saveSmsMessages(smsMessageResponse.data,
                        ourNumber,
                        Enums.SMSMessages.SentStatus.SUCCESSFUL,
                        ourUuid,
                        allSavedTeams)
                        .subscribe(object : DisposableCompletableObserver() {
                            override fun onComplete() {
                                lastPageFetched = pageToFetch
                                dbManager.updateConnectSMSMessagesExpiry()
                            }

                            override fun onError(e: Throwable) {
                            }
                        })
                }
                return@map MediatorResult.Success(smsMessageResponse.next == null) as MediatorResult
            }
            .onErrorReturn { error -> MediatorResult.Error(error) }
    }

}
