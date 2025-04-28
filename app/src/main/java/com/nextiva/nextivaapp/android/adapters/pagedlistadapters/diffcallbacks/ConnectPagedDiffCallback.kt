package com.nextiva.nextivaapp.android.adapters.pagedlistadapters.diffcallbacks

import android.text.TextUtils
import androidx.recyclerview.widget.DiffUtil
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ChatHeaderListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ChatMessageListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectCallHistoryListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactHeaderListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.MessageHeaderListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.MessageListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.SmsMessageListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.VoicemailListItem
import com.nextiva.nextivaapp.android.db.model.DbPresence
import com.nextiva.nextivaapp.android.models.SmsMessage
import java.util.Arrays

class ConnectPagedDiffCallback : DiffUtil.ItemCallback<BaseListItem>() {

    override fun areItemsTheSame(oldItem: BaseListItem, newItem: BaseListItem): Boolean {
        when {
            oldItem is ConnectContactHeaderListItem && newItem is ConnectContactHeaderListItem -> {
                return TextUtils.equals(oldItem.itemType, newItem.itemType)
            }
            oldItem is ConnectContactListItem && newItem is ConnectContactListItem -> {
                    return TextUtils.equals(oldItem.strippedContact?.contactTypeId
                            ?: oldItem.nextivaContact?.userId,
                            newItem.strippedContact?.contactTypeId
                                    ?: newItem.nextivaContact?.userId) &&
                            oldItem.groupValue == newItem.groupValue &&
                            oldItem.isBlocked == newItem.isBlocked
            }
            oldItem is ConnectCallHistoryListItem && newItem is ConnectCallHistoryListItem -> {
                return TextUtils.equals(oldItem.callEntry.callLogId, newItem.callEntry.callLogId) &&
                        (oldItem.isBlocked == newItem.isBlocked)
            }
            oldItem is VoicemailListItem && newItem is VoicemailListItem -> {
                return TextUtils.equals(oldItem.voicemail.messageId, newItem.voicemail.messageId) &&
                        (oldItem.isBlocked == newItem.isBlocked)
            }
            oldItem is MessageListItem && newItem is MessageListItem -> {
                return TextUtils.equals(oldItem.data.groupId, newItem.data.groupId) &&
                        oldItem.unReadCount == newItem.unReadCount
            }
            oldItem is MessageHeaderListItem && newItem is MessageHeaderListItem -> {
                return TextUtils.equals(oldItem.data, newItem.data) &&
                        TextUtils.equals(oldItem.connectData, newItem.connectData)
            }
            oldItem is SmsMessageListItem && newItem is SmsMessageListItem -> {
                return TextUtils.equals(oldItem.data.messageId, newItem.data.messageId)
            }
            oldItem is ChatHeaderListItem && newItem is ChatHeaderListItem -> {
                return TextUtils.equals(oldItem.data, newItem.data)
            }
            oldItem is ChatMessageListItem && newItem is ChatMessageListItem -> {
                return try {
                    val oldTimestamp = oldItem.data.timestamp
                    val newTimestamp = newItem.data.timestamp
                    oldTimestamp == newTimestamp && TextUtils.equals(oldItem.data.body, newItem.data.body)

                } catch (e: NumberFormatException) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    false
                }
            }
        }

        return false
    }

    override fun areContentsTheSame(oldItem: BaseListItem, newItem: BaseListItem): Boolean {
        when {
            oldItem is ConnectContactHeaderListItem && newItem is ConnectContactHeaderListItem -> {
                return TextUtils.equals(oldItem.itemType, newItem.itemType) &&
                        oldItem.isExpanded == newItem.isExpanded
            }
            oldItem is ConnectContactListItem && newItem is ConnectContactListItem -> {
                return (oldItem.strippedContact?.favorite ?: oldItem.nextivaContact?.isFavorite) ==
                        (newItem.strippedContact?.favorite ?: newItem.nextivaContact?.isFavorite) &&
                        oldItem.searchTerm == newItem.searchTerm &&
                        TextUtils.equals(oldItem.strippedContact?.contactTypeId
                                ?: oldItem.nextivaContact?.userId,
                                newItem.strippedContact?.contactTypeId
                                        ?: newItem.nextivaContact?.userId) &&
                        TextUtils.equals(oldItem.strippedContact?.uiName ?: oldItem.nextivaContact?.uiName,
                                newItem.strippedContact?.uiName ?: newItem.nextivaContact?.uiName) &&
                        (oldItem.strippedContact?.presence?.state ?: oldItem.nextivaContact?.presence?.state) ==
                        (newItem.strippedContact?.presence?.state ?: newItem.nextivaContact?.presence?.state) &&
                        (oldItem.isBlocked == newItem.isBlocked)
            }
            oldItem is ConnectCallHistoryListItem && newItem is ConnectCallHistoryListItem -> {
                return oldItem.callEntry.isRead == newItem.callEntry.isRead &&
                        TextUtils.equals(oldItem.callEntry.humanReadableName, newItem.callEntry.humanReadableName) &&
                        TextUtils.equals(oldItem.searchTerm, newItem.searchTerm) && oldItem.isChecked == newItem.isChecked &&
                        oldItem.callEntry.presenceState == newItem.callEntry.presenceState &&
                        oldItem.isBlocked == newItem.isBlocked
            }
            oldItem is VoicemailListItem && newItem is VoicemailListItem -> {
                return TextUtils.equals(oldItem.voicemail.messageId, newItem.voicemail.messageId) &&
                        TextUtils.equals(oldItem.voicemail.transcription, newItem.voicemail.transcription) &&
                        TextUtils.equals(oldItem.voicemail.rating, newItem.voicemail.rating) &&
                        oldItem.voicemail.duration == newItem.voicemail.duration &&
                        oldItem.voicemail.isRead == newItem.voicemail.isRead &&
                        oldItem.isChecked == newItem.isChecked &&
                        oldItem.voicemail.presenceState == newItem.voicemail.presenceState &&
                        Arrays.equals(oldItem.voicemail.avatar, newItem.voicemail.avatar) &&
                        oldItem.isBlocked == newItem.isBlocked
            }
            oldItem is MessageListItem && newItem is MessageListItem -> {
                val oldMessage = oldItem.data
                val newMessage = newItem.data

                return  TextUtils.equals(oldMessage.messageState?.readStatus, newMessage.messageState?.readStatus) &&
                        TextUtils.equals(oldMessage.body, newMessage.body) &&
                        equalParticipantNames(oldItem.data, newItem.data) &&
                        oldItem.presence?.state != newItem.presence?.state &&
                        TextUtils.equals(oldItem.data.messageId, newItem.data.messageId)
            }
            oldItem is MessageHeaderListItem && newItem is MessageHeaderListItem -> {
                return TextUtils.equals(oldItem.data, newItem.data) &&
                        TextUtils.equals(oldItem.connectData, newItem.connectData)
            }
            oldItem is SmsMessageListItem && newItem is SmsMessageListItem -> {
                val oldItemAttachmentCount = oldItem.data.attachments?.count() ?: 0
                val newItemAttachmentCount = newItem.data.attachments?.count() ?: 0
                var attachmentsSame = (oldItemAttachmentCount == newItemAttachmentCount)
                oldItem.data.attachments?.forEachIndexed { index, oldAttachment ->
                    val newAttachment = newItem.data.attachments?.get(index)
                    if (!TextUtils.equals(oldAttachment.link, newAttachment?.link) ||
                            oldAttachment.fileDuration != newAttachment?.fileDuration ||
                            !Arrays.equals(oldAttachment.contentData, newAttachment?.contentData)) {
                        attachmentsSame = false
                    }
                }
                return oldItem.data.sentStatus == newItem.data.sentStatus &&
                        TextUtils.equals(oldItem.humanReadableDatetime, newItem.humanReadableDatetime) &&
                        (oldItem.showTimeSeparator == newItem.showTimeSeparator) &&
                        TextUtils.equals(oldItem.data.body, newItem.data.body) &&
                        Arrays.equals(oldItem.data.photoData, newItem.data.photoData) &&
                        TextUtils.equals(oldItem.humanReadableDate, newItem.humanReadableDate) &&
                        TextUtils.equals(oldItem.humanReadableTime, newItem.humanReadableTime) &&
                        equalPresence(oldItem.presence, newItem.presence) &&
                        attachmentsSame
            }
            oldItem is ChatHeaderListItem && newItem is ChatHeaderListItem -> {
                return TextUtils.equals(oldItem.data, newItem.data)
            }
            oldItem is ChatMessageListItem && newItem is ChatMessageListItem -> {
                return try {
                    val oldChatMessage = oldItem.data
                    val newChatMessage = newItem.data

                    TextUtils.equals(oldChatMessage.body, newChatMessage.body) &&
                            TextUtils.equals(oldChatMessage.uiName, newChatMessage.uiName) &&
                            oldChatMessage.timestamp == newChatMessage.timestamp &&
                            oldChatMessage.sentStatus == newChatMessage.sentStatus &&
                            oldChatMessage.presenceState == newChatMessage.presenceState &&
                            oldChatMessage.avatar.contentEquals(newChatMessage.avatar)

                } catch (e: java.lang.NumberFormatException) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    false
                }
            }
        }

        return false
    }

    private fun equalPresence(itemOne: DbPresence?, itemTwo: DbPresence?): Boolean {
        var samePresence = true
        if (itemOne == null && itemTwo != null) {
            samePresence = false
        }
        if (itemOne != null && itemTwo == null) {
            samePresence = false
        }
        itemOne?.let { oldPresence ->
            itemTwo?.let { newPresence ->
                samePresence = oldPresence.state == newPresence.state &&
                        TextUtils.equals(oldPresence.humanReadablePresenceText, newPresence.humanReadablePresenceText)
            }
        }

        return samePresence
    }

    private fun equalParticipantNames(itemOne: SmsMessage, itemTwo: SmsMessage): Boolean {
        val itemOneParticipants = itemOne.getParticipantsList("") ?: emptyList()
        val itemTwoParticipants = itemTwo.getParticipantsList("") ?: emptyList()

        return if (itemOneParticipants.size == itemTwoParticipants.size) {
            itemOneParticipants.zip(itemTwoParticipants) { participantOne, participantTwo ->
                participantOne.uiName == participantTwo.uiName
            }.all { it }
        } else {
            false
        }
    }
}