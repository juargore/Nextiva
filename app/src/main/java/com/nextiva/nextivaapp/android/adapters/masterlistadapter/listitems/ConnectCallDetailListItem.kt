package com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems

import com.nextiva.nextivaapp.android.models.CallLogEntry
import com.nextiva.nextivaapp.android.models.Voicemail

class ConnectCallDetailListItem(): BaseListItem() {
    var callLogEntry: CallLogEntry? = null
    var voicemail: Voicemail? = null

    constructor(callLogEntry: CallLogEntry): this() {
        this.callLogEntry = callLogEntry
    }

    constructor(voicemail: Voicemail): this() {
        this.voicemail = voicemail
    }
}