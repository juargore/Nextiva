package com.nextiva.nextivaapp.android.mocks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PagingSource
import androidx.paging.testing.asPagingSourceFactory
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ContactListItem
import com.nextiva.nextivaapp.android.core.notifications.api.UserScheduleResponse
import com.nextiva.nextivaapp.android.core.notifications.models.Schedule
import com.nextiva.nextivaapp.android.db.AppDatabase
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.model.DbAttachment
import com.nextiva.nextivaapp.android.db.model.DbCallLogEntry
import com.nextiva.nextivaapp.android.db.model.DbGroup
import com.nextiva.nextivaapp.android.db.model.DbLogging
import com.nextiva.nextivaapp.android.db.model.DbMeeting
import com.nextiva.nextivaapp.android.db.model.DbMessage
import com.nextiva.nextivaapp.android.db.model.DbMessageState
import com.nextiva.nextivaapp.android.db.model.DbPresence
import com.nextiva.nextivaapp.android.db.model.DbSession
import com.nextiva.nextivaapp.android.db.model.DbVCard
import com.nextiva.nextivaapp.android.db.model.DbVoicemail
import com.nextiva.nextivaapp.android.db.model.SmsTeam
import com.nextiva.nextivaapp.android.db.response.DatabaseResponse
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UmsRepository
import com.nextiva.nextivaapp.android.mocks.values.CallsMock
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.models.CallLogEntry
import com.nextiva.nextivaapp.android.models.CallsDbReturnModel
import com.nextiva.nextivaapp.android.models.ChatConversation
import com.nextiva.nextivaapp.android.models.ChatMessage
import com.nextiva.nextivaapp.android.models.ConnectContactDbReturnModel
import com.nextiva.nextivaapp.android.models.DbResponse
import com.nextiva.nextivaapp.android.models.DbTableCountModel
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.models.SmsConversationDetails
import com.nextiva.nextivaapp.android.models.SmsMessage
import com.nextiva.nextivaapp.android.models.Voicemail
import com.nextiva.nextivaapp.android.models.net.platform.Data
import com.nextiva.nextivaapp.android.models.net.platform.presence.ConnectPresenceResponse
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.VoicemailDetails
import com.nextiva.nextivaapp.android.models.net.platform.websocket.WebSocketConnectPresencePayload
import com.nextiva.nextivaapp.android.util.extensions.orFalse
import com.nextiva.nextivaapp.android.view.AvatarView
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.flow.Flow
import org.mockito.kotlin.mock
import javax.inject.Inject


open class FakeDbManager @Inject constructor() : DbManager {
    override fun deleteContactsByContactType(
        compositeDisposable: CompositeDisposable,
        contactId: Int
    ) {
    }

    override fun saveContacts(
        contacts: java.util.ArrayList<NextivaContact>?,
        transactionId: String?,
        isConnect: Boolean
    ): Completable {
        return Completable.never()
    }

    override fun saveRecentContacts(contacts: ArrayList<NextivaContact>, transactionId: String): Completable {
        return Completable.never()
    }

    override fun markContactFavorite(contactTypeId: String?, isFavorite: Boolean) {

    }

    override fun getConnectContactsPagingSource(
        favoritesExpanded: Boolean,
        teammatesExpanded: Boolean,
        businessExpanded: Boolean,
        allExpanded: Boolean
    ): PagingSource<Int, ConnectContactDbReturnModel> {
        return mock()
    }

    override fun getConnectSmsContactList(): List<NextivaContact> {
        return mock()
    }

    override fun getConnectGroupCount(group: String?): LiveData<Int> {
        return MutableLiveData()
    }

    override fun updateConnectContactsExpiry() {

    }

    override fun updateConnectCallLogsExpiry() {

    }

    override fun updateConnectSMSMessagesExpiry() {

    }

    override fun getUnreadChatMessageIdsFromChatWith(chatwith: String?): MutableList<String> {
        return ArrayList()
    }

    override fun getOwnVCardInThread(): DbSession {
        return DbSession()
    }

    override fun getVoicemailRatingById(voicemailId: String?): String {
        return ""
    }

    override fun getVoicemailById(messageId: String?): DbVoicemail {
        return DbVoicemail(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null)
    }

    override fun updateVoicemailRating(rating: String?, messageId: String?) {
    }

    override fun updateVoicemailDuration(duration: Int, messageId: String?) {

    }

    override fun updateRosterContactsPushExpiry() {
    }

    override fun getSuccessfullySentMessageId(): String {
        return ""
    }

    override fun getPresenceInThread(jid: String?): DbPresence {
        return DbPresence()
    }

    override fun saveContentDataFromLink(link: String, thumbnailLink: String, contentType: String): Completable? {
        return Completable.never()
    }

    override fun saveContentData(contentData: ByteArray?, link: String?): Completable {
        return Completable.never()
    }

    override fun getContentDataFromSmsId(link: String?): Single<ByteArray> {
        return Single.just(ByteArray(1))
    }

    override fun saveFileDuration(link: String?, duration: Long?): Single<Long> {
        return Single.just(duration ?: 0)
    }

    override fun getConversationDetailsFrom(conversationDetails: SmsConversationDetails?): SmsConversationDetails {
        return SmsConversationDetails()
    }

    override fun insertLog(compositeDisposable: CompositeDisposable, log: DbLogging) {

    }

    override fun clearAllLogs(compositeDisposable: CompositeDisposable) {

    }

    override fun clearPostedLogs(count: Int) {

    }

    override fun getLogs(): MutableList<DbLogging>? {
        return null
    }

    override fun getLogs(count: Int): MutableList<DbLogging> {
       return mutableListOf()
    }

    override fun getLogsCount(): Int {
        return 0
    }

    override fun updateEnterpriseContact(nextivaContact: NextivaContact?) {
    }

    override fun markMessageReadWithMessageId(messageId: String?) {
    }

    override fun saveOwnVCard(
        compositeDisposable: CompositeDisposable,
        avatarByteArray: ByteArray?
    ) {
    }

    override fun updateAllVCards(
        compositeDisposable: CompositeDisposable,
        vCardData: ArrayList<DbVCard>?
    ) {
    }

    override fun getNextivaContactByUserId(userId: String?): Single<NextivaContact> {
        return Single.never()
    }

    override fun getMessageByMessageId(messageId: String?): DbMessage {
        return DbMessage()
    }

    override fun getUINameFromJid(jid: String?): String {
        return ""
    }

    override fun getUiNameFromPhoneNumber(phoneNumber: String?): String {
        return ""
    }

    override fun getConnectUiNameFromPhoneNumber(phoneNumber: String?): String? {
        return ""
    }

    override fun getMembersStringFromThreadId(threadId: String?): String {
        return ""
    }

    override fun getNewVoicemailCountLiveData(): LiveData<Int> {
        return MutableLiveData()
    }

    override fun doesLocalContactWithUiNameExist(uiName: String?): Boolean {
        return false
    }

    override fun doesLocalContactWithLookupKeyExist(lookupKey: String?): Boolean {
        return false
    }

    override fun getContactFromPhoneNumberInThread(phoneNumber: String?): DbResponse<NextivaContact> {
        return DbResponse(NextivaContact(""))
    }

    override fun getConnectContactFromPhoneNumberInThread(phoneNumber: String?): DbResponse<NextivaContact?>? {
        return DbResponse(NextivaContact(""))
    }

    override fun getVCardInThread(jid: String?): DbVCard {
        return DbVCard()
    }

    override fun getChatConversationPagingSource(chatWith: String?): PagingSource<Int, ChatMessage> {
        return mock()
    }

    override fun getDirectoryContactsInJids(jidList: ArrayList<String>?): Single<List<NextivaContact>> {
        return Single.never()
    }

    override fun deleteRosterContactByJid(jid: String?) {
    }

    override fun deleteAllContacts(compositeDisposable: CompositeDisposable) {
        TODO("Not yet implemented")
    }

    override fun getVCardFromUserJidLiveData(jid: String?): LiveData<DbVCard> {
        return MutableLiveData()
    }

    override fun getOwnVCardLiveData(): LiveData<DbSession> {
        return MutableLiveData()
    }

    override fun getOwnPresenceLiveData(): LiveData<DbSession> {
        return MutableLiveData()
    }

    override fun getPresenceLiveDataFromJid(jid: String?): LiveData<DbPresence> {
        return MutableLiveData()
    }

    override fun getVCard(jid: String?): Maybe<DbVCard> {
        return Maybe.empty()
    }

    override fun getSingleVCard(jid: String?): Single<DbVCard> {
        return Single.never()
    }

    override fun setSessionSetting(key: String?, value: String?) {
    }

    override fun getSessionSettingValue(key: String?): String? {
        return null
    }

    override fun updateCurrentUserStatus(statusText: String?) {
    }

    override fun getOwnAvatar(): Maybe<DbSession> {
        return Maybe.empty()
    }

    override fun setAvatar(avatarView: AvatarView, jid: String?): Disposable {
        return mock()
    }

    override fun getAvatarInfo(jid: String?): Maybe<AvatarInfo> {
        return mock()
    }

    override fun doesRosterContactWithJidExist(jid: String?): Boolean {
        return false
    }

    override fun getCompleteContactFromJid(jid: String?): Single<DatabaseResponse<NextivaContact>> {
        return Single.never()
    }

    override fun saveRosterContacts(
        compositeDisposable: CompositeDisposable,
        umsRepository: UmsRepository,
        contacts: ArrayList<NextivaContact>?,
        isFromRefresh: Boolean
    ) {

    }

    override fun saveEnterpriseContactsInThread(
        nextivaContactsList: MutableList<NextivaContact>,
        transactionId: String
    ) {

    }

    override fun getContactsDataSourceFactory(
        cacheType: Int,
        searchTerm: String?,
        isLongClickable: Boolean
    ): DataSource.Factory<Int, ContactListItem> {
        return mock()
    }

    override fun saveLocalContactsInThread(contacts: ArrayList<NextivaContact>?) {

    }

    override fun getContactFromPhoneNumber(phoneNumber: String?): Single<DbResponse<NextivaContact>> {
        return Single.never()
    }

    override fun getContactFromUIName(uiName: String?): Single<NextivaContact> {
        return Single.never()
    }

    override fun getUserNameFromJid(jid: String?): Maybe<String> {
        return Maybe.empty()
    }

    override fun getContactFromJid(jid: String?): Single<NextivaContact> {
        return Single.never()
    }

    override fun getContactFromContactTypeId(contactTypeId: String?): Single<NextivaContact> {
        return Single.never()
    }

    override fun getContactFromJidAndContactType(
        jid: String?,
        contactType: Int
    ): Single<NextivaContact> {
        return Single.never()
    }

    override fun getContacts(cacheType: Int): Single<List<NextivaContact>> {
        return Single.never()
    }

    override fun getContactsLiveData(cacheType: Int): LiveData<List<NextivaContact>> {
        return MutableLiveData()
    }

    override fun getRecentContactsLiveData(): LiveData<List<NextivaContact>> {
        return MutableLiveData()
    }

    override fun getRecentContactsPagingData(types: IntArray?): PagingSource<Int, NextivaContact> {
        return mock()
    }

    override fun getDbContactsInThread(cacheType: Int): MutableList<NextivaContact> {
        return ArrayList()
    }

    override fun getCompleteRosterContactFromJid(jid: String?): Single<DatabaseResponse<NextivaContact>> {
        return Single.never()
    }

    override fun addContact(
        nextivaContact: NextivaContact?,
        compositeDisposable: CompositeDisposable?
    ) {
    }

    override fun clearAndResetAllTables(): Boolean {
        return true
    }

    override fun deleteContactByContactId(
        compositeDisposable: CompositeDisposable,
        contactId: String?) {
    }

    override fun deleteAllPresences() {
    }

    override fun insertGroups(dbGroups: ArrayList<DbGroup>?) {
    }

    override fun getGroups(): ArrayList<DbGroup> {
        return ArrayList()
    }

    override fun updatePresence(
        nextivaPresence: DbPresence,
        compositeDisposable: CompositeDisposable
    ) {
    }

    override fun getRosterContactIds(): MutableList<String> {
        return mock()
    }

    override fun getVCardsForChat(jid: String?): Single<MutableList<DbVCard>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveChatMessage(chatMessage: ChatMessage?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveChatMessages(
        chatConversations: ArrayList<ChatConversation>,
        transactionId: String?
    ): Completable {
        return Completable.never()
    }

    override fun getUnreadChatMessagesCount(): LiveData<Int> {
        return mock()
    }

    override fun getChatMessagesLiveData(): LiveData<MutableList<ChatMessage>> {
        return MutableLiveData()
    }

    override fun getChatConversation(chatWith: String?): Single<MutableList<ChatMessage>> {
        return Single.never()
    }

    override fun markMessagesFromSenderRead(jid: String?) {
    }

    override fun markAllMessagesRead() {
    }

    override fun expireContactCache() {
    }

    override fun isCacheExpired(key: String?): Boolean {
        return false
    }

    override fun insertCallLogs(callLogEntries: java.util.ArrayList<DbCallLogEntry>?): Completable {
        return Completable.never()
    }

    override fun getCallLogEntriesLiveData(callTypesList: MutableList<String>?): LiveData<MutableList<CallLogEntry>> {
        return MutableLiveData()
    }

    override fun getCallLogAndVoicemailPagingSource(): PagingSource<Int, CallsDbReturnModel> {
        TODO("Not yet implemented")
    }

    override fun deleteCallLogByCallLogId(callLogId: String?): Completable {
        return Completable.never()
    }

    override fun deleteCallLogs(): Completable {
        return Completable.never()
    }

    override fun markAllCallLogEntriesRead(): Completable {
        return Completable.never()
    }

    override fun markCallLogEntryRead(callLogId: String?): Completable {
        return Completable.never()
    }

    override fun bulkUpdateCallLogsReadStatus(readStatus: Int, callLogIdList: MutableList<String>?): Completable {
        return Completable.never()
    }

    override fun markCallLogEntryUnread(callLogId: String?): Completable {
        return Completable.never()
    }

    override fun swapCallLogReadState(callLogId: String?, readStatus: Boolean?): Completable {
        return Completable.never()
    }

    override fun getUnreadCallLogEntriesCount(): LiveData<Int> {
        return MutableLiveData()
    }

    override fun getUnreadMissedCallLogEntriesCount(): LiveData<Int> {
        return MutableLiveData()
    }

    override fun getRosterContactsInThread(): java.util.ArrayList<NextivaContact> {
        return ArrayList()
    }

    override fun updateTempMessageId(tempMessageId: String?, messageId: String?) {
    }

    override fun deleteMessageFromMessageId(messageId: String?) {
    }

    override fun getTotalUnreadMessageConversationsCount(): LiveData<Int> {
        return MutableLiveData()
    }

    override fun getTotalUnreadMessagesCount(): LiveData<Int> {
        return MutableLiveData()
    }

    override fun getUnreadSmsMessagesCount(): LiveData<Int> {
        return MutableLiveData()
    }

    override fun getUnreadSmsMessagesCountByConversationIdInThread(groupValue: String?): Int {
        TODO("Not yet implemented")
    }

    override fun getUnreadChatMessagesCountByChatWith(chatWith: String?): Int {
        TODO("Not yet implemented")
    }

    override fun insertVoicemails(voicemails: java.util.ArrayList<DbVoicemail>?): Completable {
        return Completable.never()
    }

    override fun getVoicemailsLiveData(): LiveData<MutableList<Voicemail>> {
        return MutableLiveData()
    }

    override fun markVoicemailRead(messageId: String?) {

    }

    override fun markAllVoicemailsRead() {

    }

    override fun deleteAllVoicemails() { }
    
    override fun deleteVoicemail(messageId: String?) {

    }

    override fun bulkDeleteVoicemails(voicemailSelectedList: java.util.ArrayList<String>?) {
    }

    override fun bulkDeleteCallLogs(callLogSelectedList: java.util.ArrayList<String>?) {

    }

    override fun saveSmsMessages(
        data: MutableList<Data>,
        phoneNumber: String,
        successful: Int,
        userUuid: String,
        allSavedTeams: List<SmsTeam>
    ): Completable? {
        return Completable.never()
    }

    override fun updateAllSmsSentStatus(): Completable {
        TODO("Not yet implemented")
    }

    override fun getAllSmsMessages(): LiveData<MutableList<SmsMessage>> {
        return MutableLiveData()
    }

    override fun getSmsConversationPagingSource(groupId: String): PagingSource<Int, SmsMessage> {
        return mock()
    }

    override fun saveSendMessage(data: Data?, telephoneNumber: String?, pending: Int, userUuid: String, allSavedTeams: List<SmsTeam>, groupId: String?): Completable {
        return Completable.never()
    }

    override fun deleteSmsMessageByMessageId(messageId: String?) {

    }

    override fun deleteDraftMessagesFromConversation(groupId: String?, draftStatus: Int) {

    }

    override fun deleteMessagesFromConversationByGroupId(groupId: String) {

    }

    override fun getDraftMessagesFromConversationInThread(groupId: String?, draftStatus: Int): List<SmsMessage> {
        return mock()
    }

    override fun getMostRecentMessageFromConversationWithoutDraft(groupId: String?, draftStatus: Int): SmsMessage {
        return mock()
    }

    override fun getGroupValueContainingNumber(number: String?): MutableList<String> {
        return mock()
    }

    override fun updateReadStatusForMessageId(messageId: String?) {

    }

    override fun updateMessageIdAndSentStatus(
        tempMessageId: String,
        messageId: String?,
        sentStatus: Int,
        groupId: String?) {

    }

    override fun deleteMessagesByGroupId(groupId: String?) {

    }

    override fun updateSentStatus(messageId: String, status: Int) {

    }

    override fun getMessageStateList(groupValue: String?): Single<MutableList<DbMessageState>> {
        return Single.never()
    }

    override fun markVoicemailUnread(messageId: String?) {

    }

    override fun bulkUpdateVoicemailReadStatus(readStatus: Int, voicemailIdList: MutableList<String>?): Completable {
        return Completable.never()
    }

    override fun getVoicemailReadLiveData(messageId: String?): LiveData<Boolean> {
        return MutableLiveData()
    }

    override fun updateMessageSentStatus(tempMessageId: String?, sentStatus: Int?) {

    }

    override fun setPendingMessagesFailed() {

    }

    override fun getTableCountsLiveData(): LiveData<DbTableCountModel> {
        return MutableLiveData()
    }

    override fun getLocalContactsCount(): Int {
        return 0
    }

    override fun getConnectContactFromPhoneNumber(phoneNumber: String?): Single<DbResponse<NextivaContact>> {
        return Single.never()
    }

    override fun getConnectContactsFromPhoneNumbers(phoneNumbers: MutableList<String>?): Single<MutableList<DbResponse<NextivaContact>>> {
        return Single.never()
    }

    override fun getCallLogAndVoicemailPagingSource(query: String): PagingSource<Int, CallsDbReturnModel> {
        return CallsMock().allData.filter {
            it.callLogEntry?.displayName?.contains(query).orFalse() ||
                    it.voicemail?.name?.contains(query).orFalse()
        }.asPagingSourceFactory()()
    }

    override fun getCallLogPagingSource(callTypesList: MutableList<String>?): PagingSource<Int, CallsDbReturnModel> {
        return CallsMock().allData  as PagingSource<Int, CallsDbReturnModel>
    }

    override fun getCallLogPagingSource(callTypesList: MutableList<String>?, query: String): PagingSource<Int, CallsDbReturnModel> {
        return CallsMock().callsData.filter {
            it.callLogEntry?.displayName?.contains(query).orFalse()
        }.asPagingSourceFactory()()
    }

    override fun getVoicemailPagingSource(query: String): PagingSource<Int, CallsDbReturnModel>? {
        return CallsMock().voicemailData.filter {
                it.voicemail?.name?.contains(query).orFalse()
        }.asPagingSourceFactory()()
    }

    override fun getUnreadCallLogAndVoicemailCount(): LiveData<Int> {
        return MutableLiveData()
    }

    override fun updateContact(contact: NextivaContact?): Completable {
        return Completable.never()
    }

    override fun getTeammateContactIds(): MutableList<String> {
        return ArrayList()
    }

    override fun updateConnectPresences(presences: ArrayList<ConnectPresenceResponse>?) {

    }

    override fun getPresenceFromContactTypeIdInThread(contactTypeId: String?): DbPresence {
        return DbPresence()
    }

    override fun getPresenceLiveDataFromContactTypeId(contactTypeId: String?): LiveData<DbPresence> {
        return MutableLiveData()
    }

    override fun getOwnConnectPresenceLiveData(): LiveData<DbSession> {
        return MutableLiveData()
    }

    override fun getContactLiveData(contactId: String?): LiveData<NextivaContact> {
        return MutableLiveData()
    }

    override fun getContactTypePagingSource(
        types: IntArray?,
        searchTerm: String?
    ): PagingSource<Int, NextivaContact> {
        return mock()
    }

    override fun updatePresence(payload: WebSocketConnectPresencePayload?) {

    }

    override fun getContactTypeSearchCount(types: IntArray?, searchTerm: String?): Int {
        return 0
    }

    override fun getAllSmsMessagesPagingSource(): PagingSource<Int, SmsMessage> {
        return mock()
    }

    override fun getFilteredSmsMessagePagingSource(filter: String?): PagingSource<Int, SmsMessage> {
        return mock()
    }

    override fun getContactFromContactTypeIdLiveData(contactTypeId: String?): LiveData<NextivaContact> {
        return MutableLiveData()
    }

    override fun saveContentDataFromLinkWithReturn(
        link: String?,
        contentType: String?
    ): Single<ByteArray> {
        return Single.just(ByteArray(1))
    }

    override fun getMessageStateListInThread(groupId: String?): MutableList<DbMessageState> {
        return mutableListOf()
    }

    override fun getBusinessContactLookupKeys(): Single<MutableList<String>> {
        return Single.just(ArrayList())
    }

    override fun getBusinessContactLookupKeysAndPrimaryWorkEmails(): Single<MutableList<String>> {
        TODO("Not yet implemented")
    }

    override fun insertBWCallLogs(callLogEntries: java.util.ArrayList<CallLogEntry>?) {

    }

    override fun insertVoicemailTranscriptions(voicemailList: java.util.ArrayList<VoicemailDetails>?) {

    }

    override fun getFirstAttachment(): DbAttachment {
        return DbAttachment(null, null, null, null, null, null, null, null, null)
    }

    override fun getConnectContactFromUuidInThread(userUuid: String?): DbResponse<NextivaContact> {
        return mock()
    }

    override fun insertCallLogsInThread(callLogEntries: ArrayList<DbCallLogEntry>?) {
    }

    override fun deleteVoiceConversationMessagesInThread() {
    }

    override fun getLastCallLogsPageFetched(): Int {
        return 0
    }

    override fun getCallLogByLogId(callLogId: String?): DbCallLogEntry? {
        return null
    }

    override fun insertVoicemailsInThread(voicemails: ArrayList<DbVoicemail>?) {
    }

    override fun getVoicemailPagingSource(): PagingSource<Int, CallsDbReturnModel> {
        TODO("Not yet implemented")
    }

    override fun getDatabase(): AppDatabase {
        return mock()
    }

    override fun deleteCallLogByCallLogIdInThread(callLogId: String?) {
    }

    override fun saveMeetings(meetingList: MutableList<DbMeeting>?, startDate: Long?): Completable {
        return Completable.never()
    }

    override fun getMeetingsBetweenDates(startDate: Long?, endDate: Long?): MutableList<String> {
        return ArrayList()
    }

    override fun updateVoicemailReadState(isRead: Boolean, messageId: String?) {
    }

    override fun updateUnreadVoicemailCount(count: Int) {
    }

    override fun updateUnreadMissedCallCount(count: Int) {
    }

    override fun updateUnreadChatCount(count: Int) {
    }

    override fun updateUnreadSMSCount(count: Int) {
    }

    override fun patchConversationVoicemailRead(messageId: String?, isRead: Boolean?) {
    }

    override fun getSchedulesPagingSource(): PagingSource<Int, Schedule> {
        return mock()
    }

    override fun insertSchedules(
        isRefresh: Boolean,
        schedules: java.util.ArrayList<UserScheduleResponse>?,
        pageNumber: Int?
    ) {}

    override fun getLastSchedulesPageFetched(): Int {
        return 0
    }

    override fun getDndScheduleFlow(): Flow<Schedule> {
        return mock()
    }

    override fun getDndScheduleId(): String {
        return ""
    }

    override fun setDndSchedule(scheduleId: String?) {
    }

    override fun deleteDndSchedules() {
    }

    override fun deleteScheduleByScheduleId(scheduleId: String?) {
    }

    override fun isScheduleNameInUse(scheduleName: String?): Boolean {
        return false
    }

    override fun getSessionLiveDataFromMultipleKeys(keys: MutableList<String>?): LiveData<MutableList<DbSession>>
    {
        return mock()
    }

    override fun getTotalUnreadNotificationsLiveDataFromMultipleKeys(keys: MutableList<String>?): LiveData<Int> {
        return mock()
    }

    override fun insertSchedule(schedule: UserScheduleResponse?): Single<String> {
        return Single.never()
    }

    override fun upsertContacts(contacts: MutableList<NextivaContact>?) : Completable{
        return Completable.complete()
    }


    override fun updateReadStatusForConversationId(groupId: String?) {
    }

    override fun updateReadStatusForGroupId(groupId: String) {
    }

    override fun updateUnreadStatusForMessageId(messageId: String) {
    }

    override fun updateUnreadStatusForGroupId(groupId: String) {
    }

    override fun getSessionLiveDataFromKey(key: String?): LiveData<DbSession> {
        return mock()
    }

    override fun deleteAllSmsMessages() {}

    override fun getCurrentConversationListCount(): Int {
        return 0
    }

    override fun getCurrentConversationCount(groupId: String?): Int {
        return 0
    }

    override fun markMessagesUnread(messageStates: MutableList<DbMessageState>?) {
    }

    override fun getGroupIdFrom(conversationId: String?): String {
        return ""
    }

    override fun deleteCallLogsAndVoicemails() {
    }

    override fun expireVoiceConversationMessagesCache() {
    }

    override fun getSessionFlowFromKey(key: String?): Flow<DbSession> {
        return mock()
    }
}