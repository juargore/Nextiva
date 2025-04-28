package com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems

import com.nextiva.nextivaapp.android.db.model.DbPresence
import com.nextiva.nextivaapp.android.models.SmsMessage
import com.nextiva.nextivaapp.android.models.SmsParticipant

class MessageListItem(
        var smsMessage: SmsMessage,
        var photoData: ByteArray?,
        var unReadCount: Int,
        var presence: DbPresence? = null,
        var unreadMessagesIds: List<String>? = null,
        var draftMessage: SmsMessage? = null,
        var isChecked: Boolean?,
        var isSwipeActionEnabled: Boolean = true,
        var participants: List<SmsParticipant>? = null
) : SimpleBaseListItem<SmsMessage>(smsMessage)
