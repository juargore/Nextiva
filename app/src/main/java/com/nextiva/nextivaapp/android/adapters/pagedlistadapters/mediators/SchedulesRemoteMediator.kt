package com.nextiva.nextivaapp.android.adapters.pagedlistadapters.mediators

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.rxjava2.RxRemoteMediator
import com.nextiva.nextivaapp.android.core.notifications.api.SchedulesRepository
import com.nextiva.nextivaapp.android.core.notifications.models.Schedule
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import io.reactivex.Single
import javax.inject.Inject

// https://developer.android.com/topic/libraries/architecture/paging/v3-network-db#kotlin
@OptIn(ExperimentalPagingApi::class)
class SchedulesRemoteMediator @Inject constructor(
    private val schedulesRepository: SchedulesRepository,
    private val dbManager: DbManager
) : RxRemoteMediator<Int, Schedule>() {

    override fun initializeSingle(): Single<InitializeAction> {
        return if (dbManager.isCacheExpired(SharedPreferencesManager.SCHEDULES)) {
            Single.just(InitializeAction.LAUNCH_INITIAL_REFRESH)
        } else {
            Single.just(InitializeAction.SKIP_INITIAL_REFRESH)
        }
    }

    override fun loadSingle(loadType: LoadType, state: PagingState<Int, Schedule>): Single<MediatorResult> {
        var pageToFetch = dbManager.lastSchedulesPageFetched + 1

        when (loadType) {
            LoadType.REFRESH -> {
                pageToFetch = 1
            }
            LoadType.PREPEND -> {
                return Single.just(MediatorResult.Success(true))
            }
            LoadType.APPEND -> {
                if (state.lastItemOrNull() == null) {
                    if (pageToFetch > 1) {
                        return Single.just(MediatorResult.Success(true))
                    }
                }
            }
        }

        return schedulesRepository.getUserHours(pageToFetch)
            .map { schedulesResponse ->
                if (schedulesResponse.schedules.isNotEmpty()) {
                    dbManager.database.runInTransaction {
                        dbManager.insertSchedules(loadType == LoadType.REFRESH, schedulesResponse.schedules, pageToFetch)
                    }
                }

                return@map MediatorResult.Success(schedulesResponse.hasNext) as MediatorResult
            }
            .onErrorReturn { error -> MediatorResult.Error(error) }
    }
}