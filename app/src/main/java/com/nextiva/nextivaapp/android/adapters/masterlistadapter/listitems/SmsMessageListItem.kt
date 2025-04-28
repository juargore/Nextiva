package com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems

import com.nextiva.nextivaapp.android.constants.Enums.Chats.MessageBubbleTypes
import com.nextiva.nextivaapp.android.db.model.DbPresence
import com.nextiva.nextivaapp.android.models.SmsMessage

class SmsMessageListItem(data: SmsMessage,
                          @MessageBubbleTypes.Type var bubbleType: Int,
                          var humanReadableDatetime: String?,
                          var showTimeSeparator: Boolean = false,
                          var showHumanReadableTime: Boolean = true,
                          var humanReadableDate: String?,
                          var humanReadableTime: String?,
                          var presence: DbPresence? = null) : SimpleBaseListItem<SmsMessage?>(data) {

    constructor(data: SmsMessage,
                humanReadableDatetime: String?,
                showTimeSeparator: Boolean = false,
                showHumanReadableTime: Boolean = true,
                humanReadableDate: String?,
                humanReadableTime: String?) : this(data, MessageBubbleTypes.START, humanReadableDatetime, showTimeSeparator, showHumanReadableTime, humanReadableDate, humanReadableTime)
}