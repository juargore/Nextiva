package com.nextiva.nextivaapp.android.managers

import android.app.Application
import com.datadog.android.Datadog
import com.datadog.android.rum.GlobalRumMonitor
import com.datadog.android.rum.RumActionType
import com.datadog.android.rum.RumErrorSource
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.managers.interfaces.DatadogManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NextivaDatadogManager @Inject constructor(var application: Application, var sessionManager: SessionManager): DatadogManager {
    override fun setUserInfo() {
        if (!sessionManager.currentUser?.userUuid.isNullOrEmpty()) {
            dataDogUserInfo(
                name = sessionManager.currentUser?.profileDisplayName ?: "",
                email = sessionManager.currentUser?.email ?: "",
                userUuid = sessionManager.currentUser?.userUuid ?: "",
                corpAccountNumber = sessionManager.currentUser?.corpAccountNumber ?: ""
            )
        } else if (!sessionManager.userInfo?.comNextivaUseruuid.isNullOrEmpty()) {
            dataDogUserInfo(
                name = sessionManager.userInfo?.comNextivaFirstName ?: "",
                email = sessionManager.userInfo?.comNextivaEmail ?: "",
                userUuid = sessionManager.userInfo?.comNextivaUseruuid ?: "",
                corpAccountNumber = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString()
            )
        }
    }

    override fun performCustomAction(name: String, attributes: ArrayList<Pair<String, Object>>) {
        performCustomAction(name, attributes.toMap())
    }

    override fun performCustomAction(name: String, attributes: Map<String, Any?>) {
        GlobalRumMonitor.get().addAction(RumActionType.CUSTOM, name, attributes)
    }

    override fun monitorError(name: String, attributes: ArrayList<Pair<String, Object>>) {
        monitorError(name, attributes.toMap())
    }

    override fun monitorError(name: String, attributes: Map<String, Any?>) {
        GlobalRumMonitor.get().addError(name, RumErrorSource.SOURCE, null, attributes)
    }

    override fun getAttribute(name: String, value: Any?): Pair<String, Any?> {
        return Pair(name, value)
    }

    private fun dataDogUserInfo(name: String, email: String, corpAccountNumber: String, userUuid: String) {
        Datadog.setUserInfo(
            name = name,
            email = email,
            extraInfo = mapOf(
                Pair("corpAcctNumber", corpAccountNumber),
                Pair("useruuid", userUuid),
                Pair("environment", application.getString(R.string.app_environment)),
            )
        )
    }
}