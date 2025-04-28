package com.nextiva.nextivaapp.android.core.common.api

import android.app.Application
import com.nextiva.nextivaapp.android.managers.apimanagers.BaseApiManager
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.managers.interfaces.NetManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import io.reactivex.Single
import javax.inject.Inject

internal class ContactManagementPolicyApiManager @Inject constructor(
    var application: Application,
    var logManager: LogManager,
    var netManager: NetManager,
    var sessionManager: SessionManager,
    var schedulerProvider: SchedulerProvider
) : BaseApiManager(application, logManager), ContactManagementPolicyRepository {

    override fun getContactManagementPrivilege(): Single<Boolean> {
        return netManager.getContactManagementPolicyApi().getContactManagementPrivilege(
            sessionId = sessionManager.sessionId,
            corpAccountNumber = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString()
        )
            .subscribeOn(schedulerProvider.io())
            .map { response ->
                if (response.isSuccessful) {
                    return@map response.body()?.firstOrNull()?.grant ?: false
                } else {
                    logServerParseFailure(response)
                }
                false
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                false
            }
    }
}
