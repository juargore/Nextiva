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
import com.nextiva.nextivaapp.android.models.SmsMessage
import com.nextiva.nextivaapp.android.models.net.platform.MessageState
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.extensions.orZero
import io.reactivex.Single
import io.reactivex.observers.DisposableCompletableObserver
import javax.inject.Inject
import kotlin.math.ceil

@OptIn(ExperimentalPagingApi::class)
class SmsConversationRemoteMediator @Inject constructor(
    private val smsManagementRepository: SmsManagementRepository,
    private val dbManager: DbManager,
    private val sessionManager: SessionManager,
    groupId: String?,
    conversationId: String?
): RxRemoteMediator<Int, SmsMessage>() {

    companion object {
        const val PAGE_SIZE = 50
    }

    private var groupId: String? = groupId
    private var conversationId: String? = conversationId

    private var totalCount: Int = 0
    private var lastPageFetched: Int = 0

    private var ourUuid: String? = null
    private var ourNumber: String? = null
    private var allSavedTeams: List<SmsTeam>? = null

    init {
        ourUuid = sessionManager.currentUser.userUuid
        ourNumber = getUserTelephoneNumber()
        allSavedTeams = sessionManager.allTeams
    }

    override fun initializeSingle(): Single<InitializeAction> {
        return Single.just(InitializeAction.LAUNCH_INITIAL_REFRESH)
    }


    override fun loadSingle(loadType: LoadType, state: PagingState<Int, SmsMessage>): Single<MediatorResult> {
        var pageToFetch = lastPageFetched + 1

        when (loadType) {
            LoadType.REFRESH -> {
                pageToFetch = 1
            }
            LoadType.PREPEND -> {
                return Single.just(MediatorResult.Success(true))
            }
            LoadType.APPEND -> {
                val lastPage = ceil(totalCount.toDouble() / PAGE_SIZE).toInt()
                if (totalCount != 0 && (pageToFetch > lastPage)) {
                    return Single.just(MediatorResult.Success(true))
                }
            }
        }

        return smsManagementRepository.getSmsConversationForMediator(pageToFetch, groupId)
            .map { smsMessages ->
                totalCount = smsMessages.totalCount ?: 0

                // verify that the API response contains elements. If not, avoid overwriting the current data in the internal db
                if (smsMessages.data != null && (smsMessages.data?.size.orZero() >= 1)) {
                    dbManager.saveSmsMessages(
                        smsMessages.data,
                        ourNumber,
                        Enums.SMSMessages.SentStatus.SUCCESSFUL,
                        ourUuid,
                        allSavedTeams
                    )
                    .subscribe(object : DisposableCompletableObserver() {
                        override fun onComplete() {
                            lastPageFetched = pageToFetch
                            val messageStateList: ArrayList<MessageState?> = ArrayList()
                            smsMessages.data?.forEach { data ->
                                data.messages?.forEach { message ->
                                    messageStateList.add(message.messageState)
                                }
                            }
                            conversationId?.let { conversationId ->
                                smsManagementRepository.updateMessagesAsRead(
                                    messageStateList.filter { messageState -> messageState?.isRead() != true },
                                    conversationId
                                )
                            }
                        }

                        override fun onError(e: Throwable) { }
                    })
                }
                return@map MediatorResult.Success(smsMessages.next == null) as MediatorResult
            }
            .onErrorReturn { error -> MediatorResult.Error(error) }
    }

    private fun getUserTelephoneNumber(): String {
        sessionManager.userDetails?.telephoneNumber?.let { userPhoneNumber ->
            return CallUtil.getCountryCode() + userPhoneNumber
        }?: kotlin.run {
            return ""
        }
    }
}
