package com.nextiva.nextivaapp.android.models



data class MeetingData(
        var id: Long? = null,
        var title: String? = null,
        var startTime: String? = null,
        var callId: String? = null,
        var callStatus: String? = null,
        var callType: String? = null,
        var corpAcctNum: String? = null
)
