package com.nextiva.nextivaapp.android.managers.apimanagers.repositories

import android.content.Context
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.model.DbCallLogEntry
import com.nextiva.nextivaapp.android.db.model.DbVoicemail
import com.nextiva.nextivaapp.android.models.net.platform.voice.VoiceMessagesResponse
import com.nextiva.nextivaapp.android.models.net.platform.voice.VoiceMessagesReturn
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.BulkActionsConversationData
import io.reactivex.Single

interface ConversationRepository {
    fun fetchVoiceConversationMessages(): Single<VoiceMessagesReturn>

    fun fetchVoiceConversationMessages(pageToFetch: Int): Single<VoiceMessagesReturn>

    fun fetchVoiceConversationMessageForMediator(pageToFetch: Int): Single<VoiceMessagesResponse?>

    fun deleteVoicemail(voicemailId: String): Single<Boolean>

    fun markVoicemailUnread(voicemailId: String): Single<Boolean>

    fun markVoicemailRead(voicemailId: String): Single<Boolean>

    fun markCallUnread(messageId: String): Single<Boolean>

    fun markCallRead(messageId: String): Single<Boolean>

    fun getPageSize(): Int

    fun getChannelMessagesCount(@Enums.Messages.Channels.Channel channel: String, @Enums.Messages.ReadStatus.Status readStatus: String): Single<Int>

    fun deleteMessagesCountCache(channel: String): Single<Void>

    fun deleteSmsMessages()

    fun bulkDeleteConversations(bulkDeleteData: BulkActionsConversationData, deleteAudioFiles: (context: Context, messageIds: ArrayList<String>) -> Unit) : Single<Boolean>

    fun deleteMessage(bulkDeleteData: BulkActionsConversationData) : Single<Boolean>

    fun bulkUpdateConversations(bulkDeleteData: BulkActionsConversationData) : Single<Boolean>

    fun performDataDogCustomAction(callLogsList: ArrayList<DbCallLogEntry>, voicemailsList: ArrayList<DbVoicemail>)
}