package com.nextiva.nextivaapp.android.adapters.pagedlistadapters.datasources

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingState
import androidx.paging.rxjava2.RxPagingSource
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformContactsRepository
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.models.net.platform.contacts.ConnectContactsResponse
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import io.reactivex.Single
import retrofit2.Response

internal class ConnectContactSearchRxPagingSource constructor(val nextivaApplication: Application,
                                                              private val platformContactsRepository: PlatformContactsRepository,
                                                              val schedulerProvider: SchedulerProvider,
                                                              val query: String,
                                                              private val contactTypeFilter: String?,
                                                              private val totalCountLiveData: MutableLiveData<Int>) : RxPagingSource<Int, NextivaContact>() {

    private var currentPage = 1

    override fun getRefreshKey(state: PagingState<Int, NextivaContact>): Int? {
        return state.anchorPosition
    }

    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, NextivaContact>> {
        val position = if (params.key == 0) 1 else params.key ?: 1

        val typeQuery: ArrayList<String>? = when (contactTypeFilter) {
            nextivaApplication.getString(R.string.connect_contacts_type_search_filter_business) -> arrayListOf("business", "personal")
            nextivaApplication.getString(R.string.connect_contacts_type_search_filter_teammate) -> arrayListOf("corporate")
            else -> null
        }

        if (typeQuery != null) {
            return platformContactsRepository.searchContactsWithTypeFilter(query,
                    currentPage,
                    200,
                    typeQuery)
                    .subscribeOn(schedulerProvider.io())
                    .map { toLoadResult(it, position) }
                    .onErrorReturn { LoadResult.Error(it) }

        } else {
            return platformContactsRepository.searchContacts(query,
                    currentPage,
                    200)
                    .subscribeOn(schedulerProvider.io())
                    .map { toLoadResult(it, position) }
                    .onErrorReturn { LoadResult.Error(it) }
        }
    }

    private fun toLoadResult(data: Response<ConnectContactsResponse>?, position: Int): LoadResult<Int, NextivaContact> {
        val contactList: ArrayList<NextivaContact> = ArrayList()
        var totalCount = 0

        data?.body()?.let { responseBody ->
            responseBody.contactItems?.forEach { contactList.add(it.toNextivaContact()) }
            totalCount = responseBody.totalCount ?: 0
        }

        totalCountLiveData.postValue(totalCount)
        currentPage++

        return LoadResult.Page(
                data = contactList,
                prevKey = null,
                nextKey = if (totalCount < 1000 || totalCount == 0) null else position + 1
        )
    }
}