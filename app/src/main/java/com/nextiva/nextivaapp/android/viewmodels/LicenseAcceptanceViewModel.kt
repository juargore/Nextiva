package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LicenseAcceptanceViewModel @Inject constructor(
    application: Application,
    private val sessionManager: SessionManager
) : BaseViewModel(application) {
    open fun setLicenseApproved() {
        sessionManager.isLicenseApproved = true
    }
}