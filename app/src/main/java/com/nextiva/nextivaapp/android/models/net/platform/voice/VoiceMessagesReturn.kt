package com.nextiva.nextivaapp.android.models.net.platform.voice

data class VoiceMessagesReturn(val totalCount: Int?, val pageFetched: Int?, val messagesList: ArrayList<VoiceMessage>?) {
    constructor(): this(null, null, null)
    constructor(totalCount: Int?, pageFetched: Int?): this(totalCount, pageFetched, null)
}