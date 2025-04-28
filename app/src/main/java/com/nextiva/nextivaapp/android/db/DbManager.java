/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.db;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.paging.PagingSource;

import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ContactListItem;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.core.notifications.api.UserScheduleResponse;
import com.nextiva.nextivaapp.android.core.notifications.models.Schedule;
import com.nextiva.nextivaapp.android.db.model.DbAttachment;
import com.nextiva.nextivaapp.android.db.model.DbCallLogEntry;
import com.nextiva.nextivaapp.android.db.model.DbGroup;
import com.nextiva.nextivaapp.android.db.model.DbLogging;
import com.nextiva.nextivaapp.android.db.model.DbMeeting;
import com.nextiva.nextivaapp.android.db.model.DbMessage;
import com.nextiva.nextivaapp.android.db.model.DbMessageState;
import com.nextiva.nextivaapp.android.db.model.DbPresence;
import com.nextiva.nextivaapp.android.db.model.DbSession;
import com.nextiva.nextivaapp.android.db.model.DbVCard;
import com.nextiva.nextivaapp.android.db.model.DbVoicemail;
import com.nextiva.nextivaapp.android.db.model.SmsTeam;
import com.nextiva.nextivaapp.android.db.response.DatabaseResponse;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UmsRepository;
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager;
import com.nextiva.nextivaapp.android.models.AvatarInfo;
import com.nextiva.nextivaapp.android.models.CallLogEntry;
import com.nextiva.nextivaapp.android.models.CallsDbReturnModel;
import com.nextiva.nextivaapp.android.models.ChatConversation;
import com.nextiva.nextivaapp.android.models.ChatMessage;
import com.nextiva.nextivaapp.android.models.ConnectContactDbReturnModel;
import com.nextiva.nextivaapp.android.models.DbResponse;
import com.nextiva.nextivaapp.android.models.DbTableCountModel;
import com.nextiva.nextivaapp.android.models.NextivaContact;
import com.nextiva.nextivaapp.android.models.SmsConversationDetails;
import com.nextiva.nextivaapp.android.models.SmsMessage;
import com.nextiva.nextivaapp.android.models.Voicemail;
import com.nextiva.nextivaapp.android.models.net.platform.Data;
import com.nextiva.nextivaapp.android.models.net.platform.presence.ConnectPresenceResponse;
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.VoicemailDetails;
import com.nextiva.nextivaapp.android.models.net.platform.websocket.WebSocketConnectPresencePayload;
import com.nextiva.nextivaapp.android.view.AvatarView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import kotlinx.coroutines.flow.Flow;

/**
 * Created by joedephillipo on 3/4/18.
 */

public interface DbManager {

    // --------------------------------------------------------------------------------------------
    // VCard Methods
    // --------------------------------------------------------------------------------------------
    void updateAllVCards(@NonNull CompositeDisposable compositeDisposable, final ArrayList<DbVCard> vCardData);

    Maybe<DbVCard> getVCard(final String jid);

    Single<DbVCard> getSingleVCard(final String jid);

    Single<List<DbVCard>> getVCardsForChat(final String jid);

    DbVCard getVCardInThread(final String jid);

    DbSession getOwnVCardInThread();

    Disposable setAvatar(@NonNull AvatarView avatarView, String jid);

    Maybe<AvatarInfo> getAvatarInfo(String jid);

    LiveData<DbVCard> getVCardFromUserJidLiveData(String jid);
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // Session Methods
    // --------------------------------------------------------------------------------------------
    void setSessionSetting(@Enums.Session.DatabaseKey.Key final String key, final String value);

    String getSessionSettingValue(@Enums.Session.DatabaseKey.Key final String key);

    Maybe<DbSession> getOwnAvatar();

    void saveOwnVCard(@NonNull CompositeDisposable compositeDisposable, final byte[] avatarByteArray);

    LiveData<DbSession> getOwnVCardLiveData();

    LiveData<Integer> getNewVoicemailCountLiveData();

    LiveData<DbSession> getOwnPresenceLiveData();

    LiveData<DbSession> getOwnConnectPresenceLiveData();

    void updateCurrentUserStatus(String statusText);
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // Contact Methods
    // --------------------------------------------------------------------------------------------
    Completable updateContact(NextivaContact contact);

    Integer getLocalContactsCount();

    Completable saveContacts(ArrayList<NextivaContact> contacts, String transactionId, boolean isConnect);

    Completable saveRecentContacts(ArrayList<NextivaContact> contacts, String transactionId);

    void saveRosterContacts(
            @NonNull CompositeDisposable compositeDisposable,
            @NonNull UmsRepository umsRepository,
            final ArrayList<NextivaContact> contacts,
            boolean isFromRefresh);

    void saveEnterpriseContactsInThread(@NonNull final List<NextivaContact> nextivaContactsList, String transactionId);

    void updateEnterpriseContact(NextivaContact nextivaContact);

    void saveLocalContactsInThread(final ArrayList<NextivaContact> contacts);

    List<String> getTeammateContactIds();

    Maybe<String> getUserNameFromJid(final String jid);

    String getUINameFromJid(String jid);

    Single<List<NextivaContact>> getDirectoryContactsInJids(ArrayList<String> jidList);

    ArrayList<NextivaContact> getRosterContactsInThread();

    Single<NextivaContact> getContactFromJid(String jid);

    Boolean doesLocalContactWithUiNameExist(String uiName);

    Boolean doesLocalContactWithLookupKeyExist(String lookupKey);

    DbResponse<NextivaContact> getContactFromPhoneNumberInThread(String phoneNumber);

    Single<DbResponse<NextivaContact>> getContactFromPhoneNumber(String phoneNumber);

    Single<DbResponse<NextivaContact>> getConnectContactFromPhoneNumber(String phoneNumber);

    Single<List<DbResponse<NextivaContact>>> getConnectContactsFromPhoneNumbers(List<String> phoneNumbers);

    DbResponse<NextivaContact> getConnectContactFromPhoneNumberInThread(String phoneNumber);

    DbResponse<NextivaContact> getConnectContactFromUuidInThread(String userUuid);

    Single<NextivaContact> getContactFromUIName(String uiName);

    String getUiNameFromPhoneNumber(String phoneNumber);

    String getConnectUiNameFromPhoneNumber(String phoneNumber);

    Single<NextivaContact> getContactFromContactTypeId(String contactTypeId);

    LiveData<NextivaContact> getContactFromContactTypeIdLiveData(String contactTypeId);

    Single<NextivaContact> getContactFromJidAndContactType(String jid, @Enums.Contacts.ContactTypes.Type int contactType);

    Single<List<NextivaContact>> getContacts(@Enums.Contacts.CacheTypes.Type int cacheType);

    List<String> getRosterContactIds();

    LiveData<List<NextivaContact>> getContactsLiveData(@Enums.Contacts.CacheTypes.Type int cacheType);

    LiveData<List<NextivaContact>> getRecentContactsLiveData();

    PagingSource<Integer, NextivaContact> getRecentContactsPagingData(int [] types);

    Single<List<String>> getBusinessContactLookupKeys();

    Single<List<String>> getBusinessContactLookupKeysAndPrimaryWorkEmails();

    LiveData<NextivaContact> getContactLiveData(String contactId);

    DataSource.Factory<Integer, ContactListItem> getContactsDataSourceFactory(@Enums.Contacts.CacheTypes.Type int cacheType, String searchTerm, boolean isListItemLongClickable);

    void markContactFavorite(String contactTypeId, boolean isFavorite);

    PagingSource<Integer, ConnectContactDbReturnModel> getConnectContactsPagingSource(boolean favoritesExpanded, boolean teammatesExpanded, boolean businessExpanded, boolean allExpanded);

    List<NextivaContact> getConnectSmsContactList();

    PagingSource<Integer, NextivaContact> getContactTypePagingSource(int[] types, String searchTerm);

    Integer getContactTypeSearchCount(int[] types, String searchTerm);

    LiveData<Integer> getConnectGroupCount(@Enums.Platform.ConnectContactGroups.GroupType String group);

    List<NextivaContact> getDbContactsInThread(@Enums.Contacts.CacheTypes.Type int cacheType);

    Single<NextivaContact> getNextivaContactByUserId(String userId);

    boolean doesRosterContactWithJidExist(String jid);

    Single<DatabaseResponse<NextivaContact>> getCompleteRosterContactFromJid(String jid);

    Single<DatabaseResponse<NextivaContact>> getCompleteContactFromJid(String jid);

    void addContact(final NextivaContact nextivaContact, CompositeDisposable compositeDisposable);

    Completable upsertContacts(List<NextivaContact> contacts);

    boolean clearAndResetAllTables();

    void deleteRosterContactByJid(String jid);

    void deleteAllContacts(@NonNull CompositeDisposable compositeDisposable);

    void deleteContactByContactId(@NonNull CompositeDisposable compositeDisposable, final String contactId);

    void deleteContactsByContactType(@NonNull CompositeDisposable compositeDisposable, final int contactId);

    void deleteAllPresences();

    void updatePresence(WebSocketConnectPresencePayload payload);

    void updatePresence(@NonNull final DbPresence presence, @NonNull final CompositeDisposable compositeDisposable);

    LiveData<DbPresence> getPresenceLiveDataFromJid(String jid);

    void updateConnectPresences(ArrayList<ConnectPresenceResponse> presences);

    DbPresence getPresenceInThread(String jid);

    DbPresence getPresenceFromContactTypeIdInThread(String contactTypeId);

    LiveData<DbPresence> getPresenceLiveDataFromContactTypeId(String contactTypeId);
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // Messages Methods
    // --------------------------------------------------------------------------------------------
    void saveChatMessage(ChatMessage chatMessage);

    Completable saveChatMessages(@NonNull ArrayList<ChatConversation> chatConversations, String transactionId);

    void markMessagesFromSenderRead(String jid);

    void markMessageReadWithMessageId(String messageId);

    void markAllMessagesRead();

    String getMembersStringFromThreadId(String threadId);

    DbMessage getMessageByMessageId(String messageId);

    LiveData<List<ChatMessage>> getChatMessagesLiveData();

    Single<List<ChatMessage>> getChatConversation(String chatWith);

    LiveData<Integer> getUnreadChatMessagesCount();

    PagingSource<Integer, ChatMessage> getChatConversationPagingSource(String chatWith);

    List<String> getUnreadChatMessageIdsFromChatWith(String chatwith);

    void updateTempMessageId(String tempMessageId, String messageId);

    void updateMessageSentStatus(String tempMessageId, @Enums.Chats.SentStatus.Status Integer sentStatus);

    void setPendingMessagesFailed();

    void deleteMessageFromMessageId(String messageId);

    LiveData<Integer> getTotalUnreadMessageConversationsCount();

    LiveData<Integer> getTotalUnreadMessagesCount();

    List<String> getGroupValueContainingNumber(String number);

    LiveData<Integer> getUnreadSmsMessagesCount();

    Integer getUnreadSmsMessagesCountByConversationIdInThread(String conversationId);

    Integer getUnreadChatMessagesCountByChatWith(String chatWith);


    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // Call Log Methods
    // --------------------------------------------------------------------------------------------
    void insertBWCallLogs(final ArrayList<CallLogEntry> callLogEntries);

    Completable insertCallLogs(ArrayList<DbCallLogEntry> callLogEntries);

    void insertCallLogsInThread(ArrayList<DbCallLogEntry> callLogEntries);

    LiveData<List<CallLogEntry>> getCallLogEntriesLiveData(List<String> callTypesList);

    PagingSource<Integer, CallsDbReturnModel> getCallLogAndVoicemailPagingSource();

    PagingSource<Integer, CallsDbReturnModel> getCallLogPagingSource(List<String> callTypesList);
    PagingSource<Integer, CallsDbReturnModel> getCallLogAndVoicemailPagingSource(String query);

    PagingSource<Integer, CallsDbReturnModel> getCallLogPagingSource(List<String> callTypesList, String query);

    Completable deleteCallLogByCallLogId(final String callLogId);

    void deleteCallLogByCallLogIdInThread(String callLogId);

    Completable deleteCallLogs();

    void deleteCallLogsAndVoicemails();

    void deleteVoiceConversationMessagesInThread();

    Completable markAllCallLogEntriesRead();

    Completable markCallLogEntryRead(final String callLogId);

    Completable bulkUpdateCallLogsReadStatus(final int readStatus, final List<String> callLogIdList);

    Completable markCallLogEntryUnread(final String callLogId);

    Completable swapCallLogReadState(String callLogId, Boolean readStatus);

    LiveData<Integer> getUnreadCallLogEntriesCount();

    LiveData<Integer> getUnreadMissedCallLogEntriesCount();

    LiveData<Integer> getUnreadCallLogAndVoicemailCount();

    Integer getLastCallLogsPageFetched();

    DbCallLogEntry getCallLogByLogId(String callLogId);
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // DbManager Group Methods
    // --------------------------------------------------------------------------------------------

    void insertGroups(ArrayList<DbGroup> dbGroups);

    ArrayList<DbGroup> getGroups();

    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // DbManager Voicemail Methods
    // --------------------------------------------------------------------------------------------

    Completable insertVoicemails(ArrayList<DbVoicemail> voicemails);

    void insertVoicemailsInThread(ArrayList<DbVoicemail> voicemails);

    PagingSource<Integer, CallsDbReturnModel> getVoicemailPagingSource();

    PagingSource<Integer, CallsDbReturnModel> getVoicemailPagingSource(String query);

    void insertVoicemailTranscriptions(ArrayList<VoicemailDetails> voicemailList);

    String getVoicemailRatingById(String voicemailId);

    DbVoicemail getVoicemailById(String messageId);

    void updateVoicemailRating(String rating, String messageId);

    void updateVoicemailDuration(int duration, String messageId);

    LiveData<List<Voicemail>> getVoicemailsLiveData();

    LiveData<Boolean> getVoicemailReadLiveData(String messageId);

    void markVoicemailRead(String messageId);

    void markVoicemailUnread(String messageId);

    Completable bulkUpdateVoicemailReadStatus(final int readStatus, final List<String> voicemailIdList);

    void patchConversationVoicemailRead(String messageId, Boolean isRead);

    void markAllVoicemailsRead();



    void deleteVoicemail(String messageId);

    void bulkDeleteVoicemails(ArrayList<String> voicemailSelectedList);

    void bulkDeleteCallLogs(ArrayList<String> callLogSelectedList);

    void deleteAllVoicemails();

    void updateVoicemailReadState(boolean isRead, String messageId);

    void updateUnreadVoicemailCount(int count);

    void updateUnreadMissedCallCount(int count);

    void updateUnreadChatCount(int count);

    void updateUnreadSMSCount(int count);

    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // DbManager SmsMessage Methods
    // --------------------------------------------------------------------------------------------

    Completable saveSmsMessages(List<Data> data, String phoneNumber, int successful, String userUuid, List<SmsTeam> allSavedTeams);

    Completable updateAllSmsSentStatus();

    LiveData<List<SmsMessage>> getAllSmsMessages();

    PagingSource<Integer, SmsMessage> getAllSmsMessagesPagingSource();

    PagingSource<Integer, SmsMessage> getFilteredSmsMessagePagingSource(String filter);

    PagingSource<Integer, SmsMessage> getSmsConversationPagingSource(String groupId);

    Completable saveSendMessage(Data data, String telephoneNumber, int pending, String userUuid, List<SmsTeam> allSavedTeams, @Nullable String groupId);

    void deleteSmsMessageByMessageId(String messageId);

    void deleteDraftMessagesFromConversation(String groupId, Integer draftStatus);

    void deleteMessagesFromConversationByGroupId(String groupId);

    List<SmsMessage> getDraftMessagesFromConversationInThread(String groupId, Integer draftStatus);

    SmsMessage getMostRecentMessageFromConversationWithoutDraft(String groupId, Integer draftStatus);

    void updateSentStatus(String messageId, int status);

    void updateMessageIdAndSentStatus(@NotNull String tempMessageId, @Nullable String messageId, int sentStatus, String groupId);

    void updateReadStatusForMessageId(String messageId);

    void updateUnreadStatusForMessageId(String messageId);

    void updateReadStatusForConversationId(String conversationId);

    void updateReadStatusForGroupId(String groupId);

    void updateUnreadStatusForGroupId(String groupId);

    void markMessagesUnread(List<DbMessageState> messageStates);

    String getSuccessfullySentMessageId();

    DbAttachment getFirstAttachment();

    Single<List<DbMessageState>> getMessageStateList(String conversationId);

    List<DbMessageState> getMessageStateListInThread(String groupId);

    Completable saveContentDataFromLink(String link, String thumbnailLink, String contentType);

    Single<byte[]> saveContentDataFromLinkWithReturn(String link, String contentType);

    Completable saveContentData(byte[] contentData, String link);

    Single<byte[]> getContentDataFromSmsId(String link);

    Single<Long> saveFileDuration(String link, Long duration);

    SmsConversationDetails getConversationDetailsFrom(SmsConversationDetails conversationDetails);

    String getGroupIdFrom(String conversationId);

    void deleteAllSmsMessages();

    Integer getCurrentConversationListCount();

    Integer getCurrentConversationCount(String groupId);

    void deleteMessagesByGroupId(String groupId);

    // --------------------------------------------------------------------------------------------


    // --------------------------------------------------------------------------------------------
    // region Logging Methods
    // --------------------------------------------------------------------------------------------

    void insertLog(@NonNull final CompositeDisposable compositeDisposable, @NonNull DbLogging log);

    void clearAllLogs(@NonNull final CompositeDisposable compositeDisposable);

    void clearPostedLogs(int count);

    List<DbLogging> getLogs();

    List<DbLogging> getLogs(int count);

    int getLogsCount();

    // --------------------------------------------------------------------------------------------
    // endregion Logging Methods
    // --------------------------------------------------------------------------------------------

    LiveData<DbTableCountModel> getTableCountsLiveData();

    // --------------------------------------------------------------------------------------------
    // DbManager Cache Expire Management Methods
    // --------------------------------------------------------------------------------------------

    void expireContactCache();

    void expireVoiceConversationMessagesCache();

    void updateRosterContactsPushExpiry();

    void updateConnectContactsExpiry();

    void updateConnectCallLogsExpiry();

    void updateConnectSMSMessagesExpiry();

    boolean isCacheExpired(@SharedPreferencesManager.SettingsKey String key);

    // --------------------------------------------------------------------------------------------

    AppDatabase getDatabase();

    // --------------------------------------------------------------------------------------------
    // DbManager Meetings Methods
    // --------------------------------------------------------------------------------------------

    Completable saveMeetings(List<DbMeeting> meetingList, Long startDate);

    //List<String> getUnreadChatMessageIdsFromChatWith(String chatwith);
    List<String> getMeetingsBetweenDates(Long startDate, Long endDate);
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // DbManager Schedules Methods
    // --------------------------------------------------------------------------------------------

    PagingSource<Integer, Schedule> getSchedulesPagingSource();

    void insertSchedules(boolean isRefresh, ArrayList<UserScheduleResponse> schedules, Integer pageNumber);

    Single<String> insertSchedule(UserScheduleResponse schedule);

    Integer getLastSchedulesPageFetched();

    Flow<Schedule> getDndScheduleFlow();

    String getDndScheduleId();

    void setDndSchedule(String scheduleId);

    void deleteDndSchedules();

    void deleteScheduleByScheduleId(String scheduleId);

    boolean isScheduleNameInUse(String scheduleName);

    LiveData<DbSession> getSessionLiveDataFromKey(String key);

    Flow<DbSession> getSessionFlowFromKey(String key);

    LiveData<List<DbSession>> getSessionLiveDataFromMultipleKeys(List<String> keys);

    LiveData<Integer> getTotalUnreadNotificationsLiveDataFromMultipleKeys(List<String> keys);
    // --------------------------------------------------------------------------------------------
}
