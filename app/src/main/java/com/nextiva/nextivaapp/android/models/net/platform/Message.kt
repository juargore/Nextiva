package com.nextiva.nextivaapp.android.models.net.platform

import android.text.TextUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.model.DbSmsMessage
import com.nextiva.nextivaapp.android.managers.FormatterManager
import com.nextiva.nextivaapp.android.models.net.platform.messages.SmsTeamPayload
import com.nextiva.nextivaapp.android.util.extensions.nullIfEmpty
import org.threeten.bp.Instant

data class Message(

    var attachments: List<Attachment>? = null,
    var body: String? = null,
    var channel: String? = null,
    var messageId: String,
    var clientId: String,
    var preview: String? = null,
    var priority: String? = null,
    var recipients: List<Participant>? = null,
    var sender: Participant? = null,
    var sent: String? = null,
    var subject: String? = null,
    var threadIds: Any? = null,
    var groupValue: String? = null,
    var messageState: MessageState? = null,
    var isSender: Boolean = false,
    var sentStatus: Int,
    var teams: List<SmsTeamPayload>? = null,
    var groupId: String? = null
) {

    fun getConversationId(): String {
        val participantList: ArrayList<String> = ArrayList()
        var hasTeams = false

        sender?.let { sender ->
            sender.phoneNumber?.nullIfEmpty()?.let {
                if (sender.teamUuids.isNullOrEmpty()) {
                    participantList.add(it)
                }
            }
        }

        recipients?.forEach { recipient ->
            recipient.phoneNumber?.nullIfEmpty()?.let {
                if (recipient.teamUuids.isNullOrEmpty()) {
                    participantList.add(it)
                }
            }
        }

        teams?.forEach { team ->
            hasTeams = true
            team.phoneNumber?.nullIfEmpty()?.let { participantList.add(it) }
        }
        participantList.sort()

        return participantList.distinct().joinToString(separator = ",")
    }

    fun getDbSmsMessages(sentStatus: Int, ourNumber: String, conversationId: String? = null): DbSmsMessage? {
        try {
            return if (TextUtils.isEmpty(sent)) {
                DbSmsMessage(null,
                    messageId,
                    channel,
                    body,
                    preview,
                    null,
                    priority,
                    groupValue,
                    isSender,
                    sentStatus,
                    groupId,
                    conversationId ?: getConversationId()
                )

            } else {
                val formatManager = FormatterManager.getInstance()
                val date = if (sentStatus == Enums.SMSMessages.SentStatus.DRAFT) {
                    Instant.parse(sent)
                } else {
                    formatManager.getSentFormatterDateTimeManager(sent)
                }
                DbSmsMessage(null,
                    messageId,
                    channel,
                    body,
                    preview,
                    date,
                    priority,
                    groupValue,
                    isSender,
                    sentStatus,
                    groupId,
                    conversationId ?: getConversationId()
                )
            }

        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            return null
        }

    }

    fun getDbSmsMessage(smsId: Long, ourNumber: String, conversationId: String? = null): DbSmsMessage? {
        try {
            val formatManager = FormatterManager.getInstance()
            if (TextUtils.isEmpty(sent)) {
                return DbSmsMessage(smsId, messageId, channel, body, preview,
                        null,
                       priority, groupValue, isSender,  Enums.SMSMessages.SentStatus.SUCCESSFUL, groupId, conversationId ?: getConversationId())
            } else {
                return DbSmsMessage(smsId, messageId, channel, body, preview,
                        Instant.from(formatManager.getSentFormatterDateTimeManager(sent)),
                      priority, groupValue, isSender,  Enums.SMSMessages.SentStatus.SUCCESSFUL, groupId, conversationId ?:getConversationId())
            }

        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            return null
        }
    }
}