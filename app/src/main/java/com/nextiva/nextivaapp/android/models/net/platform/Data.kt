package com.nextiva.nextivaapp.android.models.net.platform

import com.google.gson.annotations.SerializedName
import com.nextiva.nextivaapp.android.util.extensions.nullIfEmpty

data class Data(
    @SerializedName("groupValue") private var _groupValue: String? = null,
    var messages: List<Message>? = null,
    var unreadMessageCount: Int? = 0) {

    val groupValue: String?
    get() {
        return when {
            _groupValue.isNullOrEmpty() -> {
                var groupValue = ""

                messages?.firstOrNull()?.let { message ->
                    message.sender?.phoneNumber?.let { phoneNumber ->
                        if (phoneNumber.isNotEmpty()) {
                            groupValue += "$phoneNumber${if (message.recipients?.filter { it.phoneNumber?.nullIfEmpty() != null }?.size ?: 0 > 0) "," else ""}"
                        }
                    }

                    val recipientSize = message.recipients?.size ?: 0

                    message.recipients?.forEach { recipient ->
                        recipient.phoneNumber?.let { phoneNumber ->
                            if (phoneNumber.isNotEmpty()) {
                                groupValue += "$phoneNumber${if (message.recipients?.indexOf(recipient) == recipientSize - 1) "" else ","}"
                            }
                        }
                    }
                }

                groupValue
            }
            else -> _groupValue
        }
    }
}