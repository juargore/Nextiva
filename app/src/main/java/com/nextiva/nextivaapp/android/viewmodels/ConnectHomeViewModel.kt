package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectHomeHeaderListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectHomeListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ConnectHomeViewModel @Inject constructor(application: Application, var nextivaApplication: Application, var dbManager: DbManager,
                                               var sessionManager: SessionManager) : BaseViewModel(application) {
    private var baseListItemsLiveData: MutableLiveData<ArrayList<BaseListItem>> = MutableLiveData()

    private val unreadCallsLiveData = dbManager.unreadCallLogEntriesCount
    private val unreadTextsLiveData = dbManager.unreadSmsMessagesCount
    private val unreadVoicemailsLiveData = dbManager.newVoicemailCountLiveData
    private val unreadCallsVoicemailsLiveData = MediatorLiveData<Int>()

    private val callChannelListItem: BaseListItem = ConnectHomeListItem(R.string.fa_phone_alt, Enums.Platform.ConnectHomeChannels.CALLS, unreadCallsVoicemailsLiveData.value)
    private val textsChannelListItem: BaseListItem = ConnectHomeListItem(R.string.fa_comment_dots, Enums.Platform.ConnectHomeChannels.MESSAGES, unreadTextsLiveData.value, true)

    init {
        unreadCallsVoicemailsLiveData.addSource(unreadCallsLiveData) {
            val count = it ?: 0
            unreadCallsVoicemailsLiveData.value = count.plus(unreadVoicemailsLiveData.value?.toInt() ?: 0)
        }
        unreadCallsVoicemailsLiveData.addSource(unreadVoicemailsLiveData) {
            val count = (it ?: 0)
            unreadCallsVoicemailsLiveData.value = count.plus(unreadCallsLiveData.value ?: 0)
        }

        unreadCallsVoicemailsLiveData.observeForever { count -> (callChannelListItem as? ConnectHomeListItem)?.updateCount?.let { it(count) }}

        if (sessionManager.isSmsEnabled) {
            unreadTextsLiveData.observeForever { count -> (textsChannelListItem as? ConnectHomeListItem)?.updateCount?.let { it(count) } }
        }
    }

    fun getListItems() {
        val listItems: ArrayList<BaseListItem> = ArrayList()

        listItems.add(ConnectHomeHeaderListItem(nextivaApplication.getString(R.string.connect_home_communications_title)))
        listItems.add(callChannelListItem)

        if (sessionManager.isSmsEnabled) {
            (callChannelListItem as? ConnectHomeListItem)?.showDivider = false
            listItems.add(textsChannelListItem)
        } else {
            (callChannelListItem as? ConnectHomeListItem)?.showDivider = true
        }

        baseListItemsLiveData.value = listItems
    }

    fun getBaseListItemsLiveData(): LiveData<ArrayList<BaseListItem>> {
        return baseListItemsLiveData
    }
}