/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */
package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManagerKt
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.net.buses.RxBus.listen
import com.nextiva.nextivaapp.android.net.buses.RxEvents.CallUpdatedEvent
import com.nextiva.nextivaapp.android.receivers.IncomingCallNotificationBroadcastReceiver
import com.nextiva.nextivaapp.android.receivers.IncomingCallNotificationBroadcastReceiver.Companion.newIntent
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.sip.pjsip.PJSipManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Thaddeus Dannar on 6/14/18.
 */
@HiltViewModel
class IncomingCallViewModel @Inject constructor(
    nextivaApplication: Application,
    private val dbManager: DbManagerKt,
    private val sipManager: PJSipManager,
    private val schedulerProvider: SchedulerProvider,
    private val logManager: LogManager) : BaseViewModel(nextivaApplication) {

    private val callUpdatedMutableCallInfo = MutableLiveData<CallUpdatedEvent>()

    init {
        initRxEventListeners()
    }

    fun answerCall() {
        logManager.logToFile(Enums.Logging.STATE_INFO, "answerCall Button Pressed")
        application.sendBroadcast(newIntent(application, sipManager.incomingCall)
            .setAction(IncomingCallNotificationBroadcastReceiver.ANSWER_AUDIO_CALL))
    }

    fun declineCall() {
        logManager.logToFile(Enums.Logging.STATE_INFO, "declineCall Button Pressed")
        sipManager.incomingCall?.let { application.sendBroadcast(newIntent(application, it).setAction(IncomingCallNotificationBroadcastReceiver.DECLINE_CALL)) }
    }

    suspend fun getContactFromPhoneNumber(phoneNumber: String?): NextivaContact? {
        return phoneNumber?.let { dbManager.getContactFromPhoneNumberInThread(it).value }
    }

    fun doesSipHaveIncomingCall(): Boolean {
        return sipManager.incomingCall != null
    }

    // --------------------------------------------------------------------------------------------
    // RX Events
    // --------------------------------------------------------------------------------------------
    private fun initRxEventListeners() {
        mCompositeDisposable.addAll(
            listen(CallUpdatedEvent::class.java)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe { value: CallUpdatedEvent -> callUpdatedMutableCallInfo.setValue(value) })
    } // --------------------------------------------------------------------------------------------
}