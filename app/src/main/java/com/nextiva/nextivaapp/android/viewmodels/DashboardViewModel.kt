package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(application: Application, val dbManager: DbManager, val sessionManager: SessionManager) : BaseViewModel(application) {

    private var callLogEntriesMarkedReadLiveData: MutableLiveData<Void> = MutableLiveData()

    fun getNewVoicemailCount(): Int {
        return sessionManager.newVoicemailMessagesCount
    }

    fun getUnreadChatMessageCount(): LiveData<Int> {
        return dbManager.unreadChatMessagesCount
    }

    fun getTotalUnreadMessagesConversationCount(): LiveData<Int> {
        return dbManager.totalUnreadMessageConversationsCount
    }

    fun getNewVoicemailCountLiveData(): LiveData<Int> {
        return dbManager.newVoicemailCountLiveData
    }

    fun getUnreadCallLogEntriesCount(): LiveData<Int> {
        return dbManager.unreadCallLogEntriesCount
    }

    fun getCallLogEntriesMarkedReadLiveData(): LiveData<Void> {
        return callLogEntriesMarkedReadLiveData
    }

    fun markAllCallLogEntriesRead() {
        mCompositeDisposable.add(
                dbManager.markAllCallLogEntriesRead()
                        .subscribe { callLogEntriesMarkedReadLiveData.value = null }
        )
    }


}