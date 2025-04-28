package com.nextiva.nextivaapp.android.managers.apimanagers.repositories

import com.nextiva.nextivaapp.android.db.model.SmsTeam
import com.nextiva.nextivaapp.android.models.net.platform.BulkUpdateUserMessageState
import com.nextiva.nextivaapp.android.models.net.platform.GenerateGroupIdPostBody
import com.nextiva.nextivaapp.android.models.net.platform.MessageState
import com.nextiva.nextivaapp.android.models.net.platform.MessageStatePutBody
import com.nextiva.nextivaapp.android.models.net.platform.SendMessagePostBody
import com.nextiva.nextivaapp.android.models.net.platform.SendMessageResponse
import com.nextiva.nextivaapp.android.models.net.platform.SmsMessages
import com.nextiva.nextivaapp.android.models.net.platform.UserMessageState
import com.nextiva.nextivaapp.android.models.net.platform.messages.SmsMessageBulkAction
import com.nextiva.nextivaapp.android.models.net.platform.messages.SmsMessageBulkActionList
import com.nextiva.nextivaapp.android.net.buses.RxEvents
import io.reactivex.Single
import okhttp3.MultipartBody
import org.threeten.bp.Instant

interface SmsManagementRepository {
    fun getSmsConversations(): Single<Boolean>

    fun getSmsConversations(pageToFetch: Int): Single<Boolean>

    fun getSmsConversationsForMediator(pageNumber : Int): Single<SmsMessages?>

    fun getSmsConversationForMediator(pageNumber: Int, groupId: String?): Single<SmsMessages?>

    fun getSmsConversation(groupId: String, conversationId: String, shouldMarkMessagesRead: Boolean = false): Single<Int>

    fun getSmsConversation(currentItemCount: Int, groupId: String, conversationId: String, shouldMarkMessagesRead: Boolean = false): Single<Int>

    fun getSmsMessageWithMessageId(messageId: String): Single<SmsMessages>

    fun sendSmsMessage(smsPostBody: SendMessagePostBody): Single<SendMessageResponse>

    suspend fun generateGroupId(body: GenerateGroupIdPostBody): String?

    fun updateMessageReadStatus(messageStatePostBody: MessageStatePutBody, messageId: String): Single<Boolean>

    fun updateMessagesAsRead(messageStateList: List<MessageState?>, conversationId: String)

    fun updateUserMessageReadStatus(userMessageState: UserMessageState, messageId: String): Single<Boolean>

    fun bulkUpdateMessageReadStatus(bulkUpdateUserMessageState: BulkUpdateUserMessageState): Single<Boolean>

    fun checkForDeletedItems(createdAfter: Instant, page: Int): Single<SmsMessageBulkActionList?>

    fun onMessageConversationBulk(bulkAction: SmsMessageBulkAction)

    fun sendMmsMessage(mmsPostBody: MultipartBody.Part, destination: String, message: String, source: String?, clientId: String, teams: List<SmsTeam>?): Single<SendMessageResponse>

    fun getUsersTeams(): Single<RxEvents.BaseResponseEvent>

    fun checkApiHealthByMessageId(messageId: String): Single<Boolean>

    fun checkV2ApiHealthByMessageId(messageId: String): Single<Boolean>

    fun testAttachmentApi(): Single<Boolean>

    fun getPageSize(): Int

    fun setIsFetchingMessages(isFetching: Boolean)

    fun getNextConversationListPage(): Int

    fun getNextConversationPage(groupId: String): Int

}
