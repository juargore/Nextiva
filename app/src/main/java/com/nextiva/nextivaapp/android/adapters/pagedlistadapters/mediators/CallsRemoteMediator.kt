package com.nextiva.nextivaapp.android.adapters.pagedlistadapters.mediators

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.rxjava2.RxRemoteMediator
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.model.DbCallLogEntry
import com.nextiva.nextivaapp.android.db.model.DbVoicemail
import com.nextiva.nextivaapp.android.managers.FormatterManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ConversationRepository
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.models.CallsDbReturnModel
import io.reactivex.Single
import javax.inject.Inject

// https://developer.android.com/topic/libraries/architecture/paging/v3-network-db#kotlin
@OptIn(ExperimentalPagingApi::class)
class CallsRemoteMediator @Inject constructor(
    private val dbManager: DbManager,
    private val conversationRepository: ConversationRepository
) : RxRemoteMediator<Int, CallsDbReturnModel>() {

    private val formatterManager = FormatterManager.getInstance()
    private var totalCount: Int = 0
    private var isEditModeEnabled = false

    override fun initializeSingle(): Single<InitializeAction> {
        return if (dbManager.isCacheExpired(SharedPreferencesManager.VOICE_CONVERSATION_MESSAGES)) {
            Single.just(InitializeAction.LAUNCH_INITIAL_REFRESH)
        } else {
            Single.just(InitializeAction.SKIP_INITIAL_REFRESH)
        }
    }

    fun updateEditModeState(isEditModeEnabled: Boolean){
        this.isEditModeEnabled = isEditModeEnabled
    }

    override fun loadSingle(loadType: LoadType, state: PagingState<Int, CallsDbReturnModel>): Single<MediatorResult> {
        if (isEditModeEnabled){
            return Single.just(MediatorResult.Success(true))
        }

        var pageToFetch = dbManager.lastCallLogsPageFetched + 1

        when (loadType) {
            LoadType.REFRESH -> {
                pageToFetch = 1
            }
            LoadType.PREPEND -> {
                return Single.just(MediatorResult.Success(true))
            }
            LoadType.APPEND -> {
                if (state.lastItemOrNull() == null) {
                    if (totalCount != 0 && (pageToFetch * conversationRepository.getPageSize()) > totalCount) {
                        return Single.just(MediatorResult.Success(true))
                    }
                }
            }
        }

        return conversationRepository.fetchVoiceConversationMessageForMediator(pageToFetch)
            .map { voiceMessagesResponse ->
                val callLogsList: ArrayList<DbCallLogEntry> = ArrayList()
                val voicemailsList: ArrayList<DbVoicemail> = ArrayList()
                totalCount = voiceMessagesResponse.totalCount ?: 0

                voiceMessagesResponse.data?.forEach { data ->
                    data.voiceMessageItems?.let { voiceMessages ->
                        val splitList = voiceMessages.partition { it.channel == Enums.Messages.Channels.VOICEMAIL }
                        splitList.first.map { it.toDbVoicemail(formatterManager, pageToFetch) }.let { voicemailsList.addAll(it) }
                        splitList.second.map { it.toDbCallLogEntry(formatterManager, pageToFetch) }.let { callLogsList.addAll(it) }
                    }
                }

                conversationRepository.performDataDogCustomAction(callLogsList,voicemailsList)

                dbManager.database.runInTransaction {
                    if (loadType == LoadType.REFRESH && pageToFetch == 1) {
                        dbManager.deleteCallLogsAndVoicemails()
                    }
                    dbManager.insertCallLogsInThread(callLogsList)
                    dbManager.insertVoicemailsInThread(voicemailsList)
                }
                dbManager.updateConnectCallLogsExpiry()

                return@map MediatorResult.Success(voiceMessagesResponse.nextPage == null) as MediatorResult
            }
            .onErrorReturn { error -> MediatorResult.Error(error) }
    }
}