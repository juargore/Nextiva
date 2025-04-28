package com.nextiva.nextivaapp.android.sip

import android.os.Binder
import java.lang.ref.WeakReference

class SipOnIncomingCallServiceBinder(val service: WeakReference<SipOnIncomingCallService>) : Binder() {

    fun getInstance(): WeakReference<SipOnIncomingCallService> = service
}