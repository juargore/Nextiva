package com.nextiva.nextivaapp.android.sip

import android.os.Binder
import java.lang.ref.WeakReference

class SipOnCallServiceBinder(val service: WeakReference<SipOnCallService>) : Binder() {
    fun getInstance(): WeakReference<SipOnCallService> = service
}