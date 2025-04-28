package com.nextiva.nextivaapp.android.db

import com.nextiva.nextivaapp.android.db.model.SmsTeam
import com.nextiva.nextivaapp.android.models.DbResponse
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.models.SmsMessage
import com.nextiva.nextivaapp.android.models.net.platform.Data

interface DbManagerKt {
    suspend fun getContactFromPhoneNumberInThread(phoneNumber: String): DbResponse<NextivaContact>

    suspend fun getConnectContactFromUuidInThread(uuid: String): NextivaContact?

    suspend fun saveSmsMessages(data: List<Data>?, phoneNumber: String, sentStatus: Int, userUuid: String?, allSavedTeams: List<SmsTeam>)

    suspend fun getSmsMessageByMessageId(messageId: String): SmsMessage?

    fun expireVoiceConversationMessagesCache()
}