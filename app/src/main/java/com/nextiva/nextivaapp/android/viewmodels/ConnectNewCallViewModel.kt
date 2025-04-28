package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformContactsRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PresenceRepository
import com.nextiva.nextivaapp.android.managers.interfaces.LocalContactsManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.UserDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ConnectNewCallViewModel @Inject constructor(application: Application, var sessionManager: SessionManager,
                                                  var localContactsManager: LocalContactsManager, var presenceRepository: PresenceRepository,
                                                  var platformContactsRepository: PlatformContactsRepository) : BaseViewModel(application) {

    fun fetchContacts(forceRefresh: Boolean, onSaveFinishedCallback: () -> Unit) {
        platformContactsRepository.fetchContacts( forceRefresh, {
            presenceRepository.getPresences()
            onSaveFinishedCallback()
        }, {})
    }

    fun getLocalContacts() {
        localContactsManager.localContacts.subscribe()
    }

    fun getUserDetails(): UserDetails? {
        return sessionManager.userDetails
    }
}