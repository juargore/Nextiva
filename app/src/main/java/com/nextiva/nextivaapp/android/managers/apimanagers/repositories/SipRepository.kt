package com.nextiva.nextivaapp.android.managers.apimanagers.repositories

import com.nextiva.nextivaapp.android.models.net.sip.SipCallDetails
import io.reactivex.Single

interface SipRepository {
    fun getActiveCalls(): Single<ArrayList<SipCallDetails>?>

    fun mergeCalls(): Single<Boolean>
}