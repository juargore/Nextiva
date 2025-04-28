package com.nextiva.nextivaapp.android.models.net

import com.nextiva.nextivaapp.android.models.ChatMessage
import io.reactivex.Single

data class PendingChatMessage(val single: Single<String>, val message: ChatMessage)