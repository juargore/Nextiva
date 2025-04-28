package com.nextiva.nextivaapp.android.mocks

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.nextiva.nextivaapp.android.adapters.pagedlistadapters.mediators.CallsRemoteMediator
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ConversationRepository
import com.nextiva.nextivaapp.android.models.CallsDbReturnModel
import io.reactivex.Single

@OptIn(ExperimentalPagingApi::class)
class FakeCallsRemoteMediator(dbManager: DbManager, conversationRepository: ConversationRepository) {
    private val callsRemoteMediator = CallsRemoteMediator(dbManager, conversationRepository)

    fun initializeSingle(): Single<RemoteMediator.InitializeAction> {
        return Single.just(RemoteMediator.InitializeAction.SKIP_INITIAL_REFRESH)
    }

    fun loadSingle(
        loadType: LoadType,
        state: PagingState<Int, CallsDbReturnModel>
    ): Single<RemoteMediator.MediatorResult> {
        return Single.just(RemoteMediator.MediatorResult.Success(true))
    }
}