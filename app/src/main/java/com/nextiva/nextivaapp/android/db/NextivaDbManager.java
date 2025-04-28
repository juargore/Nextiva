/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.db;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.paging.PagingSource;
import androidx.room.Transaction;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ContactListItem;
import com.nextiva.nextivaapp.android.constants.Constants;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.core.notifications.api.UserScheduleResponse;
import com.nextiva.nextivaapp.android.core.notifications.models.Schedule;
import com.nextiva.nextivaapp.android.db.dao.AttachmentsDao;
import com.nextiva.nextivaapp.android.db.dao.CallLogsDao;
import com.nextiva.nextivaapp.android.db.dao.CompleteContactDao;
import com.nextiva.nextivaapp.android.db.dao.ContactDao;
import com.nextiva.nextivaapp.android.db.dao.ContactRecentDao;
import com.nextiva.nextivaapp.android.db.dao.GroupDao;
import com.nextiva.nextivaapp.android.db.dao.LoggingDao;
import com.nextiva.nextivaapp.android.db.dao.MeetingDao;
import com.nextiva.nextivaapp.android.db.dao.MessageStateDao;
import com.nextiva.nextivaapp.android.db.dao.MessagesDao;
import com.nextiva.nextivaapp.android.db.dao.ParticipantsDao;
import com.nextiva.nextivaapp.android.db.dao.PresenceDao;
import com.nextiva.nextivaapp.android.db.dao.SchedulesDao;
import com.nextiva.nextivaapp.android.db.dao.SessionDao;
import com.nextiva.nextivaapp.android.db.dao.SmsMessagesDao;
import com.nextiva.nextivaapp.android.db.dao.VCardDao;
import com.nextiva.nextivaapp.android.db.dao.VoicemailDao;
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
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager;
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager;
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager;
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
import com.nextiva.nextivaapp.android.models.UserDetails;
import com.nextiva.nextivaapp.android.models.Voicemail;
import com.nextiva.nextivaapp.android.models.net.platform.Data;
import com.nextiva.nextivaapp.android.models.net.platform.presence.ConnectPresenceResponse;
import com.nextiva.nextivaapp.android.models.net.platform.voicemail.VoicemailDetails;
import com.nextiva.nextivaapp.android.models.net.platform.websocket.WebSocketConnectPresencePayload;
import com.nextiva.nextivaapp.android.net.buses.RxBus;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.ContactUpdatedResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.IncomingChatMessageResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.RosterResponseEvent;
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider;
import com.nextiva.nextivaapp.android.util.GsonUtil;
import com.nextiva.nextivaapp.android.util.GuidUtil;
import com.nextiva.nextivaapp.android.view.AvatarView;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import kotlinx.coroutines.flow.Flow;

/**
 * Created by joedephillipo on 3/1/18.
 */

// USE NextivaDbManagerKt!!  This class will be converted over.
@Singleton
public class NextivaDbManager implements DbManager {

    private static final long ROSTER_CACHE_EXPIRY_MILLIS = Constants.ONE_MINUTE_IN_MILLIS * 5L;
    private static final long ROSTER_PUSH_CACHE_EXPIRY_MILLIS = Constants.ONE_SECOND_IN_MILLIS * 3L;
    private static final long ENTERPRISE_CACHE_EXPIRY_MILLIS = Constants.ONE_DAY_IN_MILLIS;
    private static final long CONNECT_CACHE_EXPIRY_MILLIS = Constants.ONE_DAY_IN_MILLIS;
    private static final long VOICE_CONVERSATION_CACHE_EXPIRY_MILLIS = Constants.ONE_MINUTE_IN_MILLIS * 5L;
    private static final long SCHEDULES_CACHE_EXPIRY_MILLIS = Constants.ONE_MINUTE_IN_MILLIS * 5L;
    private static final long SMS_MESSAGES_CACHE_EXPIRY_MILLIS = Constants.ONE_SECOND_IN_MILLIS * 30L;
    private static final long DB_TIMEOUT_MILLIS = 50;
    private final SchedulerProvider mSchedulerProvider;
    private final AvatarManager mAvatarManager;
    private final SharedPreferencesManager mSharedPreferencesManager;
    private final CalendarManager mCalendarManager;
    private final LogManager mLogManager;
    private final ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
    private AppDatabase mAppDatabase;
    private VCardDao mVCardDao;
    private PresenceDao mPresenceDao;
    private ContactDao mContactDao;
    private ContactRecentDao mContactRecentDao;
    private MessagesDao mMessagesDao;
    private CallLogsDao mCallLogsDao;
    private CompleteContactDao mCompleteContactDao;
    private SessionDao mSessionDao;
    private GroupDao mGroupDao;
    private VoicemailDao mVoicemailDao;
    private SmsMessagesDao mSmsMessagesDao;
    private MessageStateDao mMessageStateDao;
    private AttachmentsDao mAttachmentsDao;
    private LoggingDao mLoggingDao;
    private MeetingDao mMeetingDao;
    private SchedulesDao mSchedulesDao;
    private ParticipantsDao mParticipantsDao;
    private RxBus mRxBus;

    @Inject
    public NextivaDbManager(Application application,
                            SchedulerProvider schedulerProvider,
                            SharedPreferencesManager sharedPreferencesManager,
                            AvatarManager avatarManager,
                            CalendarManager calendarManager,
                            LogManager logManager) {

        mAppDatabase = AppDatabase.Companion.getAppDatabase(application);
        mVCardDao = mAppDatabase.vCardDao();
        mContactDao = mAppDatabase.contactDao();
        mContactRecentDao = mAppDatabase.contactRecentDao();
        mMessagesDao = mAppDatabase.messagesDao();
        mPresenceDao = mAppDatabase.presenceDao();
        mCallLogsDao = mAppDatabase.callLogEntriesDao();
        mCompleteContactDao = mAppDatabase.completeContactDao();
        mSessionDao = mAppDatabase.sessionDao();
        mGroupDao = mAppDatabase.groupDao();
        mVoicemailDao = mAppDatabase.voicemailDao();
        mSmsMessagesDao = mAppDatabase.smsMessagesDao();
        mMessageStateDao = mAppDatabase.messageStateDao();
        mAttachmentsDao = mAppDatabase.attachmentsDao();
        mLoggingDao = mAppDatabase.loggingDao();
        mMeetingDao = mAppDatabase.meetingDao();
        mSchedulesDao = mAppDatabase.schedulesDao();
        mParticipantsDao = mAppDatabase.participantDao();

        mSchedulerProvider = schedulerProvider;
        mAvatarManager = avatarManager;
        mSharedPreferencesManager = sharedPreferencesManager;
        mCalendarManager = calendarManager;
        mLogManager = logManager;
    }

    // --------------------------------------------------------------------------------------------
    // DbManager VCard Methods
    // --------------------------------------------------------------------------------------------

    @Override
    public Maybe<DbVCard> getVCard(final String jid) {
        return mVCardDao.getVCardFromUserJid(jid)
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<List<DbVCard>> getVCardsForChat(final String jid) {
        return mVCardDao.getVCardsForConversations(jid)
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public DbVCard getVCardInThread(final String jid) {
        try {
            return mExecutorService.submit(() -> mVCardDao.getVCardFromUserJidInThread(jid)).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    @Override
    public DbSession getOwnVCardInThread() {
        try {
            return mExecutorService.submit(() -> mSessionDao.getSessionFromKeyInThread(Enums.Session.DatabaseKey.USER_AVATAR)).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    @Override
    public Single<DbVCard> getSingleVCard(final String jid) {
        return mVCardDao.getSingleVCardFromUserJid(jid)
                .onErrorReturn(throwable -> {
                    FirebaseCrashlytics.getInstance().recordException(throwable);
                    return new DbVCard();
                })
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public LiveData<DbVCard> getVCardFromUserJidLiveData(String jid) {
        return mVCardDao.getVCardFromUserJidLiveData(jid);
    }

    @Override
    public void updateAllVCards(
            @NonNull final CompositeDisposable compositeDisposable,
            final ArrayList<DbVCard> vCardData) {

        compositeDisposable.add(
                Completable.fromAction(() -> {
                            DbVCard userVCard;
                            Integer contactId;

                            String ownJid = "";

                            if (GsonUtil.getObject(UserDetails.class, getSessionSettingValue(Enums.Session.DatabaseKey.USER_DETAILS)) != null) {
                                UserDetails userDetails = GsonUtil.getObject(UserDetails.class, getSessionSettingValue(Enums.Session.DatabaseKey.USER_DETAILS));

                                if (!TextUtils.isEmpty(Objects.requireNonNull(userDetails).getImpId())) {
                                    ownJid = Objects.requireNonNull(userDetails).getImpId();
                                }
                            }

                            for (DbVCard vCard : vCardData) {

                                if (TextUtils.isEmpty(vCard.getJid())) {
                                    continue;
                                }

                                contactId = mContactDao.getRosterContactIdFromJid(vCard.getJid()).onErrorReturn(throwable -> {
                                    return -1;
                                }).blockingGet();
                                userVCard = mVCardDao.getVCardFromUserJidInThread(vCard.getJid());

                                if (!TextUtils.isEmpty(vCard.getJid()) &&
                                        !TextUtils.isEmpty(ownJid) &&
                                        TextUtils.equals(vCard.getJid().toLowerCase(), ownJid.toLowerCase())) {
                                    saveOwnVCard(compositeDisposable, vCard.getPhotoData());

                                } else {
                                    if (vCard.getPhotoData() != null && vCard.getPhotoData().length > 0) {
                                        if (userVCard == null && contactId != -1) {
                                            mVCardDao.insertVCard(new DbVCard(null, contactId, vCard.getPhotoData(), null, null, null, null));

                                        } else if (userVCard != null && !Arrays.equals(vCard.getPhotoData(), userVCard.getPhotoData())) {
                                            mVCardDao.updateVCardWithNewAvatar(vCard.getJid(), vCard.getPhotoData(), null);
                                        }
                                    } else if (userVCard == null && contactId != -1) {
                                        mVCardDao.insertVCard(new DbVCard(null, contactId, null, null, null, null, null));

                                    } else if (userVCard != null && !Arrays.equals(vCard.getPhotoData(), userVCard.getPhotoData())) {
                                        mVCardDao.updateVCardWithNewAvatar(vCard.getJid(), null, null);
                                    }
                                }
                            }
                        })
                        .subscribeOn(mSchedulerProvider.io())
                        .observeOn(mSchedulerProvider.ui())
                        .subscribe());
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // UserDetailDao Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void setSessionSetting(@Enums.Session.DatabaseKey.Key final String key, final String value) {
        Completable.fromAction(() -> mSessionDao.insertSession(new DbSession(null, key, value)))
                .subscribeOn(mSchedulerProvider.io())
                .subscribe();
    }

    @Nullable
    @Override
    public String getSessionSettingValue(@Enums.Session.DatabaseKey.Key final String key) {
        try {
            return mExecutorService.submit(() -> {
                DbSession session = mSessionDao.getSessionFromKeyInThread(key);
                return session != null ? session.getValue() : null;
            }).get();

        } catch (InterruptedException | ExecutionException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            return null;
        }
    }

    @Override
    public Maybe<DbSession> getOwnAvatar() {
        return mSessionDao.getSessionFromKey(Enums.Session.DatabaseKey.USER_AVATAR)
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public void saveOwnVCard(@NonNull CompositeDisposable compositeDisposable, final byte[] avatarByteArray) {
        compositeDisposable.add(
                Completable
                        .fromAction(() -> {
                            if (mAvatarManager.isByteArrayNotEmpty(avatarByteArray)) {
                                if (mSessionDao.getSessionFromKeyInThread(Enums.Session.DatabaseKey.USER_AVATAR) == null) {
                                    mSessionDao.insertSession(new DbSession(null, Enums.Session.DatabaseKey.USER_AVATAR, mAvatarManager.byteArrayToString(avatarByteArray)));

                                } else if (!TextUtils.equals(mAvatarManager.byteArrayToString(avatarByteArray),
                                                             mSessionDao.getSessionFromKeyInThread(Enums.Session.DatabaseKey.USER_AVATAR).getValue())) {
                                    mSessionDao.updateValue(Enums.Session.DatabaseKey.USER_AVATAR, mAvatarManager.byteArrayToString(avatarByteArray));
                                }
                            } else {
                                if (mSessionDao.getSessionFromKeyInThread(Enums.Session.DatabaseKey.USER_AVATAR) == null) {
                                    mSessionDao.insertSession(new DbSession(null, Enums.Session.DatabaseKey.USER_AVATAR, null));

                                } else if (mSessionDao.getSessionFromKeyInThread(Enums.Session.DatabaseKey.USER_AVATAR) != null) {
                                    mSessionDao.updateValue(Enums.Session.DatabaseKey.USER_AVATAR, null);
                                }
                            }
                        })
                        .subscribeOn(mSchedulerProvider.io())
                        .observeOn(mSchedulerProvider.ui())
                        .subscribe());
    }

    @Override
    public LiveData<DbSession> getOwnVCardLiveData() {
        return mSessionDao.getSessionLiveDataFromKey(Enums.Session.DatabaseKey.USER_AVATAR);
    }

    @Override
    public LiveData<DbSession> getOwnPresenceLiveData() {
        return mSessionDao.getSessionLiveDataFromKey(Enums.Session.DatabaseKey.USER_PRESENCE);
    }

    @Override
    public LiveData<DbSession> getOwnConnectPresenceLiveData() {
        return mSessionDao.getSessionLiveDataFromKey(Enums.Session.DatabaseKey.USER_PRESENCE_CONNECT);
    }

    @Deprecated
    @Override
    public LiveData<Integer> getNewVoicemailCountLiveData() {
        return mVoicemailDao.getUnreadVoicemailCountLiveData();
    }

    @Override
    public void updateCurrentUserStatus(String statusText) {
        DbPresence nextivaPresence = GsonUtil.getObject(DbPresence.class, getSessionSettingValue(Enums.Session.DatabaseKey.USER_PRESENCE));
        if (nextivaPresence != null) {
            nextivaPresence.setStatus(statusText);
        }
        setSessionSetting(Enums.Session.DatabaseKey.USER_PRESENCE, GsonUtil.getJSON(nextivaPresence));
    }
    // --------------------------------------------------------------------------------------------


    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // DbManager Contact Methods
    // --------------------------------------------------------------------------------------------

    @Override
    public Completable saveContacts(ArrayList<NextivaContact> contacts, String transactionId, boolean isConnect) {
        return Completable.fromAction(() ->
                                              mCompleteContactDao.insertConnectContacts(contacts, mAppDatabase, isConnect ? new int[] {Enums.Contacts.ContactTypes.CONNECT_PERSONAL,
                                                                                                Enums.Contacts.ContactTypes.CONNECT_SHARED,
                                                                                                Enums.Contacts.ContactTypes.CONNECT_USER,
                                                                                                Enums.Contacts.ContactTypes.CONNECT_CALL_FLOW,
                                                                                                Enums.Contacts.ContactTypes.CONNECT_CALL_CENTERS,
                                                                                                Enums.Contacts.ContactTypes.CONNECT_TEAM} :
                                                                                                new int[] {Enums.Contacts.ContactTypes.LOCAL},
                                                                                        transactionId))
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Completable saveRecentContacts(ArrayList<NextivaContact> contacts, String transactionId) {
        return Completable.fromAction(() ->
                        mContactRecentDao.insertContacts(contacts, mAppDatabase, transactionId))
                .subscribeOn(mSchedulerProvider.io());
    }

    @Override
    public Completable updateContact(NextivaContact contact) {
        return Completable.fromAction(() ->
                                              mCompleteContactDao.updateConnectContact(contact, mAppDatabase, UUID.randomUUID().toString()))
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Integer getLocalContactsCount() {
        try {
            return mExecutorService.submit(() -> mContactDao.getLocalContactsCount()).get();

        } catch (InterruptedException | ExecutionException e) {
            mLogManager.logToFile(Enums.Logging.STATE_ERROR, e.getClass().getSimpleName());
            return 0;
        }
    }

    @Override
    public void saveRosterContacts(
            @NonNull final CompositeDisposable compositeDisposable,
            @NonNull final UmsRepository umsRepository,
            final ArrayList<NextivaContact> contacts,
            final boolean isUpdateFromContactDetails) {

        setCacheExpiryMillis(SharedPreferencesManager.ROSTER_CONTACTS);

        compositeDisposable.add(
                Completable
                        .fromAction(() -> {
                            for (NextivaContact contact : contacts) {
                                if (mVCardDao.getVCardFromUserJidInThread(contact.getJid()) != null) {
                                    contact.setVCard(new DbVCard(
                                            contact.getJid(),
                                            mVCardDao.getVCardFromUserJidInThread(contact.getJid()).getPhotoData()));
                                }
                            }

                            mCompleteContactDao.insertContacts(contacts, mAppDatabase, Enums.Contacts.ContactTypes.PERSONAL, UUID.randomUUID().toString());
                        })
                        .subscribeOn(mSchedulerProvider.io())
                        .observeOn(mSchedulerProvider.ui())
                        .subscribe(() -> {
                            if (isUpdateFromContactDetails) {
                                getRxBus().publish(new ContactUpdatedResponseEvent(true));

                            } else {
                                int favoritesNumber = 0;

                                for (NextivaContact nextivaContact : contacts) {
                                    if (nextivaContact.isFavorite()) {
                                        favoritesNumber++;
                                    }
                                }

                                getRxBus().publish(new RosterResponseEvent(true, favoritesNumber));
                            }

                            compositeDisposable.add(
                                    umsRepository.getVCards(contacts, compositeDisposable)
                                            .subscribe());
                        }));
    }

    @Override
    public void updateEnterpriseContact(NextivaContact updatedContact) {
        Completable.fromAction(() -> {
                    List<NextivaContact> enterpriseContacts = mCompleteContactDao.getNextivaContactsListInThread(Enums.Contacts.CacheTypes.ENTERPRISE);

                    if (enterpriseContacts != null) {
                        for (NextivaContact nextivaContact : enterpriseContacts) {
                            if (TextUtils.equals(nextivaContact.getUserId(), updatedContact.getUserId())) {
                                nextivaContact.updateContactWith(updatedContact);
                            }
                        }

                        mCompleteContactDao.insertContacts(enterpriseContacts,
                                                           mAppDatabase,
                                                           Enums.Contacts.ContactTypes.ENTERPRISE,
                                                           UUID.randomUUID().toString());

                    }
                })
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .subscribe();
    }

    @Override
    public void saveEnterpriseContactsInThread(@NonNull final List<NextivaContact> nextivaContactsList, String transactionId) {
        setCacheExpiryMillis(SharedPreferencesManager.ENTERPRISE_CONTACTS);

        for (NextivaContact contact : nextivaContactsList) {
            if (!TextUtils.isEmpty(contact.getJid())) {
                DbVCard dbVCard = mVCardDao.getSingleVCardFromUserJid(contact.getJid())
                        .onErrorReturn(throwable -> new DbVCard())
                        .blockingGet();

                if (dbVCard != null && dbVCard.getPhotoData() != null) {
                    contact.setVCard(new DbVCard(contact.getJid(), dbVCard.getPhotoData()));
                }
            }
        }

        mCompleteContactDao.insertContacts(nextivaContactsList, mAppDatabase, Enums.Contacts.ContactTypes.ENTERPRISE, transactionId);
    }

    @Override
    public void saveLocalContactsInThread(ArrayList<NextivaContact> contacts) {
        mCompleteContactDao.insertContacts(contacts, mAppDatabase, Enums.Contacts.ContactTypes.LOCAL, UUID.randomUUID().toString());
    }

    @Override
    public List<String> getTeammateContactIds() {
        return mContactDao.getTeammateContactIds();
    }

    @Override
    public void addContact(final NextivaContact nextivaContact, @NonNull CompositeDisposable compositeDisposable) {
        compositeDisposable.add(
                Completable
                        .fromAction(() -> {
                            ArrayList<NextivaContact> nextivaContacts = new ArrayList<>();
                            nextivaContacts.add(nextivaContact);
                            mCompleteContactDao.insertContacts(nextivaContacts, mAppDatabase, Enums.Contacts.ContactTypes.NONE, UUID.randomUUID().toString());
                        }).subscribeOn(mSchedulerProvider.io())
                        .observeOn(mSchedulerProvider.ui())
                        .subscribe());
    }

    @Override
    public void deleteRosterContactByJid(String jid) {
        Completable
                .fromAction(() -> mContactDao.deleteRosterContactByJid(jid))
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .subscribe();
    }

    @Override
    public void deleteAllContacts(@NonNull CompositeDisposable compositeDisposable) {
        compositeDisposable.add(
                Completable
                        .fromAction(() -> mContactDao.deleteAllContacts())
                        .subscribeOn(mSchedulerProvider.io())
                        .observeOn(mSchedulerProvider.ui())
                        .subscribe());
    }

    @Override
    public void deleteContactByContactId(@NonNull CompositeDisposable compositeDisposable, final String contactTypeId) {
        compositeDisposable.add(
                Completable
                        .fromAction(() -> mContactDao.deleteContactByContactTypeId(contactTypeId))
                        .subscribeOn(mSchedulerProvider.io())
                        .observeOn(mSchedulerProvider.ui())
                        .subscribe());
    }

    @Override
    public void deleteContactsByContactType(@NonNull CompositeDisposable compositeDisposable, final int contactType) {
        compositeDisposable.add(
                Completable
                        .fromAction(() -> mContactDao.deleteContactsByContactType(contactType))
                        .subscribeOn(mSchedulerProvider.io())
                        .observeOn(mSchedulerProvider.ui())
                        .subscribe());
    }

    @Override
    public List<NextivaContact> getDbContactsInThread(@Enums.Contacts.CacheTypes.Type int cacheType) {
        return mCompleteContactDao.getNextivaContactsListInThread(cacheType);
    }

    @Override
    public Single<NextivaContact> getNextivaContactByUserId(String userId) {
        return mCompleteContactDao.getCompleteContactByUserId(userId)
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public ArrayList<NextivaContact> getRosterContactsInThread() {
        try {
            List<NextivaContact> nextivaContactList = mCompleteContactDao.getNextivaContactsListInThread(Enums.Contacts.CacheTypes.ALL_ROSTER);
            if (nextivaContactList != null) {
                return mExecutorService.submit(() -> new ArrayList<>(nextivaContactList)).get();
            } else {
                return new ArrayList<>();
            }

        } catch (InterruptedException | ExecutionException e) {
            mLogManager.logToFile(Enums.Logging.STATE_ERROR, e.getClass().getSimpleName());
            return new ArrayList<>();
        }
    }

    @Override
    public Single<List<NextivaContact>> getDirectoryContactsInJids(ArrayList<String> jidList) {
        return mCompleteContactDao.getCompleteDirectoryContactsByJids(jidList)
                .onErrorReturn(throwable -> {
                    FirebaseCrashlytics.getInstance().recordException(throwable);
                    return new ArrayList<>();
                })
                .map(nextivaContacts -> {
                    for (String jid : new ArrayList<>(jidList)) {
                        for (NextivaContact nextivaContact : nextivaContacts) {
                            if (jid != null && !TextUtils.isEmpty(nextivaContact.getJid()) && TextUtils.equals(nextivaContact.getJid().toLowerCase(), jid.toLowerCase())) {
                                jidList.remove(jid);
                            }
                        }
                    }

                    if (jidList.size() > 0) {
                        NextivaContact nextivaContact;

                        for (String jidNotFound : jidList) {
                            if (!doesRosterContactWithJidExist(jidNotFound)) {
                                nextivaContact = new NextivaContact(String.valueOf(GuidUtil.getRandomId()));
                                nextivaContact.setContactType(Enums.Contacts.ContactTypes.PERSONAL);
                                nextivaContact.setJid(jidNotFound);
                                nextivaContacts.add(nextivaContact);
                            }
                        }
                    }

                    return nextivaContacts;
                })
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<List<NextivaContact>> getContacts(@Enums.Contacts.CacheTypes.Type final int cacheType) {
        return mCompleteContactDao.getNextivaContactsList(cacheType)
                .onErrorReturn(throwable -> {
                    FirebaseCrashlytics.getInstance().recordException(throwable);
                    return new ArrayList<NextivaContact>() {
                    };
                })
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public LiveData<List<NextivaContact>> getContactsLiveData(final int cacheType) {
        return mCompleteContactDao.getNextivaContactsListLiveData(cacheType);
    }

    @Override
    public LiveData<NextivaContact> getContactLiveData(String contactId) {
        return mCompleteContactDao.getContactLiveData(contactId);
    }

    @Override
    public LiveData<List<NextivaContact>> getRecentContactsLiveData() {
        return mContactRecentDao.getContactLiveData();
    }

    @Override
    public PagingSource<Integer, NextivaContact> getRecentContactsPagingData(int [] types) {
        return mContactRecentDao.getContactPagingSource(types);
    }

    @Override
    public DataSource.Factory<Integer, ContactListItem> getContactsDataSourceFactory(int cacheType, String searchTerm, boolean isListItemLongClickable) {
        return mCompleteContactDao.getContactsDataSourceFactory(cacheType, searchTerm)
                .map(input -> new ContactListItem(
                        input,
                        searchTerm,
                        isListItemLongClickable));
    }

    @Override
    public LiveData<Integer> getConnectGroupCount(@Enums.Platform.ConnectContactGroups.GroupType String group) {
        switch (group) {
            case Enums.Platform.ConnectContactGroups.FAVORITES: {
                return mContactDao.getConnectFavoritesCount();
            }
            case Enums.Platform.ConnectContactGroups.TEAMMATES: {
                return mContactDao.getConnectTypeCount(new int[] {Enums.Contacts.ContactTypes.CONNECT_USER});
            }
            case Enums.Platform.ConnectContactGroups.BUSINESS: {
                return mContactDao.getConnectTypeCount(new int[] {Enums.Contacts.ContactTypes.CONNECT_SHARED,
                        Enums.Contacts.ContactTypes.CONNECT_PERSONAL});
            }
            case Enums.Platform.ConnectContactGroups.ALL_CONTACTS: {
                return mContactDao.getConnectTypeCount(new int[] {Enums.Contacts.ContactTypes.CONNECT_USER,
                        Enums.Contacts.ContactTypes.CONNECT_SHARED,
                        Enums.Contacts.ContactTypes.CONNECT_PERSONAL,
                        Enums.Contacts.ContactTypes.CONNECT_TEAM,
                        Enums.Contacts.ContactTypes.CONNECT_CALL_FLOW,
                        Enums.Contacts.ContactTypes.CONNECT_CALL_CENTERS});
            }
            default: {
                return null;
            }
        }
    }

    @Override
    public void markContactFavorite(String contactTypeId, boolean isFavorite) {
        Completable
                .fromAction(() -> mContactDao.setFavorite(contactTypeId, isFavorite))
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .subscribe();
    }

    @Override
    public PagingSource<Integer, ConnectContactDbReturnModel> getConnectContactsPagingSource(boolean favoritesExpanded, boolean teammatesExpanded, boolean businessExpanded, boolean allExpanded) {
        return mCompleteContactDao.getConnectContactPagingSource(favoritesExpanded, teammatesExpanded, businessExpanded, allExpanded);
    }

    @Override
    public List<NextivaContact> getConnectSmsContactList() {
        return mCompleteContactDao.getConnectSmsContactList();
    }

    @Override
    public PagingSource<Integer, NextivaContact> getContactTypePagingSource(int[] types, String searchTerm) {
        return mCompleteContactDao.getContactTypePagingSource(types, searchTerm);
    }

    @Override
    public Integer getContactTypeSearchCount(int[] types, String searchTerm) {
        return mCompleteContactDao.getContactTypeSearchCount(types, searchTerm);
    }

    @Override
    public Maybe<String> getUserNameFromJid(String jid) {
        return mContactDao.getUserNameFromJid(jid)
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public String getUINameFromJid(String jid) {
        try {
            String uiName = mExecutorService.submit(() -> mContactDao.getRosterContactUINameFromJid(jid)).get();

            if (TextUtils.isEmpty(uiName)) {
                uiName = mExecutorService.submit(() -> mContactDao.getUINameFromJid(jid)).get();
            }

            if (TextUtils.isEmpty(uiName)) {
                uiName = jid;
            }

            return uiName;

        } catch (InterruptedException | ExecutionException e) {
            mLogManager.logToFile(Enums.Logging.STATE_ERROR, e.getClass().getSimpleName());
            return null;
        }
    }

    @Override
    public boolean doesRosterContactWithJidExist(String jid) {
        try {
            int contactCount = mExecutorService.submit(() -> mContactDao.doesRosterContactWithJidExist(jid)).get();
            return contactCount > 0;

        } catch (InterruptedException | ExecutionException e) {
            mLogManager.logToFile(Enums.Logging.STATE_ERROR, e.getClass().getSimpleName());
            return false;
        }
    }

    @Override
    public Single<NextivaContact> getContactFromJid(String jid) {
        return mContactDao.getContact(jid)
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .onErrorReturnItem(new NextivaContact(""));
    }

    @Override
    public Boolean doesLocalContactWithUiNameExist(String uiName) {
        try {
            int contactCount = mExecutorService.submit(() -> mContactDao.doesLocalContactWithUiNameExist(uiName)).get();
            return contactCount > 0;

        } catch (InterruptedException | ExecutionException e) {
            mLogManager.logToFile(Enums.Logging.STATE_ERROR, e.getClass().getSimpleName());
            return false;
        }
    }

    @Override
    public Boolean doesLocalContactWithLookupKeyExist(String lookupKey) {
        try {
            int contactCount = mExecutorService.submit(() -> mCompleteContactDao.doesLocalContactWithLookupKeyExist(lookupKey)).get();
            return contactCount > 0;

        } catch (InterruptedException | ExecutionException e) {
            mLogManager.logToFile(Enums.Logging.STATE_ERROR, e.getClass().getSimpleName());
            return false;
        }
    }

    @Override
    public Single<List<String>> getBusinessContactLookupKeys() {
        return mCompleteContactDao.getBusinessContactLookupKeys()
                .subscribeOn(mSchedulerProvider.io());
    }


    @Override
    public Single<List<String>> getBusinessContactLookupKeysAndPrimaryWorkEmails() {
        return mCompleteContactDao.getBusinessContactLookupKeysAndPrimaryWorkEmails()
                .subscribeOn(mSchedulerProvider.io());
    }



    @Override
    public DbResponse<NextivaContact> getContactFromPhoneNumberInThread(String phoneNumber) {
        try {
            return mCompleteContactDao.getNextivaContactInThread(phoneNumber, mExecutorService);
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public Single<DbResponse<NextivaContact>> getContactFromPhoneNumber(String phoneNumber) {
        return mCompleteContactDao.getNextivaContact(phoneNumber, mExecutorService)
                .onErrorReturn(throwable -> {
                    FirebaseCrashlytics.getInstance().recordException(throwable);
                    return new DbResponse<>(new NextivaContact(""));
                })
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<DbResponse<NextivaContact>> getConnectContactFromPhoneNumber(String phoneNumber) {
        return mCompleteContactDao.getConnectContact(phoneNumber, mExecutorService)
                .onErrorReturn(throwable -> {
                    FirebaseCrashlytics.getInstance().recordException(throwable);
                    return new DbResponse<>(new NextivaContact(""));
                })
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui());
    }


    @Override
    public Single<List<DbResponse<NextivaContact>>> getConnectContactsFromPhoneNumbers(List<String> phoneNumbers) {
        List<Single<DbResponse<NextivaContact>>> singles = phoneNumbers.stream()
                .map(this::getConnectContact)
                .collect(Collectors.toList());

        return Single.zip(singles, responses -> Arrays.stream(responses)
                .map(response -> response != null ? (DbResponse<NextivaContact>) response : new DbResponse<>(new NextivaContact()))
                .collect(Collectors.toList()));
    }

    private Single<DbResponse<NextivaContact>> getConnectContact(String phoneNumber) {
        return mCompleteContactDao.getConnectContact(phoneNumber, mExecutorService)
                .onErrorReturn(throwable -> {
                    FirebaseCrashlytics.getInstance().recordException(throwable);
                    return new DbResponse<>(new NextivaContact(""));
                })
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public DbResponse<NextivaContact> getConnectContactFromPhoneNumberInThread(String phoneNumber) {
        return mCompleteContactDao.getConnectContact(phoneNumber, mExecutorService)
                .subscribeOn(mSchedulerProvider.io())
                .timeout(DB_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .onErrorReturn(throwable -> {
                    FirebaseCrashlytics.getInstance().recordException(throwable);
                    return new DbResponse<>(null);
                })
                .blockingGet();
    }

    @Override
    public DbResponse<NextivaContact> getConnectContactFromUuidInThread(String userUuid) {
        return new DbResponse<>(mCompleteContactDao.getConnectContactFromUuid(userUuid));
    }

    @Override
    public Single<NextivaContact> getContactFromUIName(String uiName) {
        return Single.fromCallable(() -> mCompleteContactDao.getCompleteContactFromUIName(uiName))
                .onErrorReturn(throwable -> {
                    FirebaseCrashlytics.getInstance().recordException(throwable);
                    return new NextivaContact("");
                })
                .subscribeOn(mSchedulerProvider.io());
    }

    @Override
    public String getUiNameFromPhoneNumber(String phoneNumber) {
        return mCompleteContactDao.getUiNameFromPhoneNumber(phoneNumber, mExecutorService);
    }

    @Override
    public String getConnectUiNameFromPhoneNumber(String phoneNumber) {
        return mCompleteContactDao.getConnectUiNameFromPhoneNumber(phoneNumber, mExecutorService);
    }

    @Override
    public Single<DatabaseResponse<NextivaContact>> getCompleteContactFromJid(String jid) {
        return mCompleteContactDao.getCompleteRosterContactByJid(jid)
                .onErrorResumeNext(mCompleteContactDao.getCompleteContactByJid(jid))
                .onErrorReturn(throwable -> {
                    FirebaseCrashlytics.getInstance().recordException(throwable);
                    return new NextivaContact("");
                })
                .map(nextivaContact -> new DatabaseResponse<>(true, nextivaContact))
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<DatabaseResponse<NextivaContact>> getCompleteRosterContactFromJid(String jid) {
        return mCompleteContactDao.getCompleteRosterContactByJid(jid)
                .onErrorReturn(throwable -> {
                    FirebaseCrashlytics.getInstance().recordException(throwable);
                    return new NextivaContact("");
                })
                .map(nextivaContact -> new DatabaseResponse<>(true, nextivaContact))
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<NextivaContact> getContactFromJidAndContactType(String jid, @Enums.Contacts.ContactTypes.Type int contactType) {
        return mContactDao.getContact(jid, contactType)
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<NextivaContact> getContactFromContactTypeId(String contactTypeId) {
        return mContactDao.getContactFromContactTypeId(contactTypeId)
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public LiveData<NextivaContact> getContactFromContactTypeIdLiveData(String contactTypeId) {
        return mContactDao.getContactFromContactTypeIdLiveData(contactTypeId);
    }

    @Override
    public List<String> getRosterContactIds() {
        try {
            return mExecutorService.submit(() -> mContactDao.getRosterContactIds()).get();

        } catch (ExecutionException | InterruptedException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public Completable upsertContacts(List<NextivaContact> contacts) {
        return Completable
                .fromAction(() -> mCompleteContactDao.insertContacts(contacts, mAppDatabase, Enums.Contacts.ContactTypes.NONE, null))
                .subscribeOn(mSchedulerProvider.io());
    }

    @Override
    public boolean clearAndResetAllTables() {
        if (mAppDatabase == null) {
            return false;
        }

        SimpleSQLiteQuery query = new SimpleSQLiteQuery("DELETE FROM sqlite_sequence");

        mAppDatabase.beginTransaction();

        try {
            mAppDatabase.clearAllTables();
            mAppDatabase.query(query, null);
            mAppDatabase.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            mAppDatabase.endTransaction();
        }
    }

    @Override
    public void deleteAllPresences() {
        Completable
                .fromAction(() -> mPresenceDao.deleteAllPresences())
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .subscribe();
    }

    @Override
    public LiveData<DbPresence> getPresenceLiveDataFromJid(String jid) {
        return mPresenceDao.getPresenceFromUserJidLiveData(jid);
    }

    @Override
    @Transaction
    public void updateConnectPresences(ArrayList<ConnectPresenceResponse> presences) {
        Completable.fromAction(() -> mPresenceDao.updatePresences(presences, mContactDao))
                .subscribeOn(mSchedulerProvider.io())
                .onErrorComplete()
                .subscribe();
    }

    @Override
    public DbPresence getPresenceInThread(String jid) {
        try {
            return mExecutorService.submit(() -> mPresenceDao.getPresenceByJidInThread(jid)).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    public void updatePresence(WebSocketConnectPresencePayload payload) {
        Completable.fromAction(() -> {
                    DbPresence dbPresence = mPresenceDao.getPresenceByContactTypeId(payload.getUserId());

                    if (dbPresence == null) {
                        int contactId = (int) mContactDao.getContactIdInThread(payload.getUserId());

                        if (contactId != 0) {
                            mPresenceDao.insertPresence(new DbPresence(contactId,
                                                                       payload.getUserId(),
                                                                       payload.getPresenceState(),
                                                                       payload.getStatusExpiresAt(),
                                                                       payload.getCustomMessage(),
                                                                       payload.getInCall()));
                        }

                    } else if (payload.getPresenceState() != dbPresence.getState() ||
                            payload.getInCall() != dbPresence.getInCall() ||
                            !TextUtils.equals(payload.getCustomMessage(), dbPresence.getStatus())) {
                        mPresenceDao.updatePresenceForUserId(payload.getPresenceState(),
                                                             payload.getCustomMessage(),
                                                             payload.getUserId(),
                                                             payload.getStatusExpiresAt(),
                                                             payload.getInCall());
                    }
                })
                .subscribeOn(mSchedulerProvider.io())
                .subscribe();
    }

    @Override
    public DbPresence getPresenceFromContactTypeIdInThread(String contactTypeId) {
        try {
            return mExecutorService.submit(() -> mPresenceDao.getPresenceByContactTypeId(contactTypeId)).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    @Override
    public LiveData<DbPresence> getPresenceLiveDataFromContactTypeId(String contactTypeId) {
        return mPresenceDao.getPresenceByContactTypeIdLiveData(contactTypeId);
    }

    @Override
    public void updatePresence(@NonNull final DbPresence presence, @NonNull final CompositeDisposable compositeDisposable) {
        @Enums.Contacts.SubscriptionStates.SubscriptionState
        int state = presence.getState() == Enums.Contacts.PresenceStates.NONE ?
                Enums.Contacts.SubscriptionStates.UNSUBSCRIBED : presence.getState() == Enums.Contacts.PresenceStates.PENDING ?
                Enums.Contacts.SubscriptionStates.PENDING :
                Enums.Contacts.SubscriptionStates.SUBSCRIBED;


        Completable.fromAction(() -> mContactDao.updateContactSubscriptionState(state,
                                                                                presence.getJid()))
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .subscribe();

        compositeDisposable.add(
                Completable.fromAction(() -> mPresenceDao.updatePresenceFromJidDisposable(presence.getState(),
                                                                                          presence.getType(),
                                                                                          presence.getPriority(),
                                                                                          presence.getStatus(),
                                                                                          presence.getJid()))
                        .subscribeOn(mSchedulerProvider.io())
                        .observeOn(mSchedulerProvider.ui())
                        .subscribe(() -> {
                            mLogManager.xmppLogToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
                        }));
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // DbManager Chat Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void saveChatMessage(@NonNull ChatMessage chatMessage) {
        Completable.fromAction(() -> {
                    DbMessage dbMessage = null;

                    if (!TextUtils.isEmpty(chatMessage.getMessageId())) {
                        dbMessage = mMessagesDao.getMessageWithMessageId(chatMessage.getMessageId());
                    }

                    if (dbMessage == null) {
                        dbMessage = new DbMessage();

                        dbMessage.setMessageId(chatMessage.getMessageId());
                        dbMessage.setBody(chatMessage.getBody());
                        dbMessage.setType(chatMessage.getType());
                        dbMessage.setFrom(!TextUtils.isEmpty(chatMessage.getFrom()) ? chatMessage.getFrom() : null);
                        dbMessage.setChatWith(!TextUtils.isEmpty(chatMessage.getChatWith()) ? chatMessage.getChatWith() : null);
                        dbMessage.setTimestamp(chatMessage.getTimestamp());
                        dbMessage.setRead(chatMessage.isRead());
                        dbMessage.setSender(chatMessage.isSender());
                        dbMessage.setThreadId(chatMessage.getThreadId());
                        dbMessage.setMembers(chatMessage.getMembersString());
                        dbMessage.setLanguage(chatMessage.getLanguage());
                        dbMessage.setSentStatus(chatMessage.getSentStatus());
                        dbMessage.setTo(!TextUtils.isEmpty(chatMessage.getTo()) ? chatMessage.getTo() : null);
                        mMessagesDao.insert(dbMessage);

                        try {
                            DbVCard dbVCard = mExecutorService.submit(() -> mVCardDao.getVCardFromUserJidInThread(chatMessage.getFrom())).get();
                            if (dbVCard != null) {
                                chatMessage.setAvatar(dbVCard.getPhotoData());
                            }
                        } catch (InterruptedException | ExecutionException e) {
                            mLogManager.logToFile(Enums.Logging.STATE_ERROR, e.getClass().getSimpleName());
                        }

                        getRxBus().publish(new IncomingChatMessageResponseEvent(true, chatMessage));
                    }
                }).subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .subscribe();
    }

    @Override
    public Completable saveChatMessages(@NonNull ArrayList<ChatConversation> chatConversations, String transactionId) {
        return Completable.fromAction(() -> {
                    ArrayList<DbMessage> dbMessages = new ArrayList<>();
                    DbMessage dbMessage;

                    for (ChatConversation chatConversation : chatConversations) {
                        for (ChatMessage chatMessage : chatConversation.getChatMessagesList()) {
                            dbMessage = new DbMessage();

                            dbMessage.setMessageId(chatMessage.getMessageId());
                            dbMessage.setBody(chatMessage.getBody());
                            dbMessage.setType(chatMessage.getType());
                            dbMessage.setFrom(!TextUtils.isEmpty(chatMessage.getFrom()) ? chatMessage.getFrom() : null);
                            dbMessage.setSender(chatMessage.isSender());
                            dbMessage.setChatWith(TextUtils.equals(chatMessage.getType(), Enums.Chats.ConversationTypes.GROUP_ALIAS) ? chatMessage.getThreadId() : (!TextUtils.isEmpty(chatMessage.getChatWith()) ? chatMessage.getChatWith() : null));
                            dbMessage.setTimestamp(chatMessage.getTimestamp());
                            dbMessage.setRead(chatMessage.isRead());
                            dbMessage.setThreadId(chatMessage.getThreadId());
                            dbMessage.setMembers(chatMessage.getMembersString());
                            dbMessage.setLanguage(chatMessage.getLanguage());
                            dbMessage.setSentStatus(chatMessage.getSentStatus());
                            dbMessage.setTo(!TextUtils.isEmpty(chatMessage.getTo()) ? chatMessage.getTo() : null);
                            dbMessage.setTransactionId(transactionId);

                            dbMessages.add(dbMessage);
                        }
                    }

                    mMessagesDao.refreshMessages(dbMessages, transactionId);
                }).subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public void markAllMessagesRead() {
        Completable.fromAction(() -> mMessagesDao.markAllMessagesRead())
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .subscribe();
    }

    @Override
    public void markMessagesFromSenderRead(String jid) {
        Completable.fromAction(() -> mMessagesDao.markMessagesFromSenderRead(jid))
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .subscribe();
    }

    @Override
    public String getMembersStringFromThreadId(String threadId) {
        try {
            return mExecutorService.submit(() -> mMessagesDao.getMemberFromThreadId(threadId)).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    @Override
    public DbMessage getMessageByMessageId(String messageId) {
        try {
            return mExecutorService.submit(() -> mMessagesDao.getMessageWithMessageId(messageId)).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    @Override
    public void markMessageReadWithMessageId(String messageId) {
        Completable.fromAction(() -> mMessagesDao.markMessagesReadByMessageId(messageId))
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .subscribe();
    }

    @Override
    public Single<List<ChatMessage>> getChatConversation(String chatWith) {
        return mMessagesDao.getChatConversation(chatWith)
                .onErrorReturn(throwable -> {
                    FirebaseCrashlytics.getInstance().recordException(throwable);
                    return new ArrayList<>();
                })
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public LiveData<Integer> getUnreadChatMessagesCount() {
        return mMessagesDao.getUnreadChatMessagesCount();
    }

    @Override
    public LiveData<List<ChatMessage>> getChatMessagesLiveData() {
        return mMessagesDao.getChatMessagesLiveData();
    }

    @Override
    public PagingSource<Integer, ChatMessage> getChatConversationPagingSource(String chatWith) {
        return mMessagesDao.getChatConversationPagingSource(chatWith);
    }

    @Override
    public List<String> getUnreadChatMessageIdsFromChatWith(String chatwith) {
        try {
            return mExecutorService.submit(() -> mMessagesDao.getUnreadChatMessageIdsFromChatWith(chatwith)).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    @Override
    public void updateTempMessageId(String tempMessageId, String messageId) {
        Completable.fromAction(() -> mMessagesDao.updateTempMessageId(tempMessageId, messageId, System.currentTimeMillis()))
                .subscribeOn(mSchedulerProvider.io())
                .subscribe();
    }

    @Override
    public void updateMessageSentStatus(String tempMessageId, @Enums.Chats.SentStatus.Status Integer sentStatus) {
        Completable.fromAction(() -> mMessagesDao.updateMessageSentStatus(tempMessageId, sentStatus))
                .subscribeOn(mSchedulerProvider.io())
                .subscribe();
    }

    @Override
    public void setPendingMessagesFailed() {
        Completable.fromAction(() -> mMessagesDao.setPendingMessagesFailed())
                .subscribeOn(mSchedulerProvider.io())
                .subscribe();
    }

    @Override
    public void deleteMessageFromMessageId(String messageId) {
        Completable.fromAction(() -> mMessagesDao.deleteMessageFromMessageId(messageId))
                .subscribeOn(mSchedulerProvider.io())
                .subscribe();
    }

    @Override
    public LiveData<Integer> getTotalUnreadMessageConversationsCount() {
        return mSmsMessagesDao.getTotalUnreadMessageConversationsCount();
    }

    @Override
    public LiveData<Integer> getTotalUnreadMessagesCount() {
        return mMessageStateDao.getTotalUnreadMessagesCount(Enums.SMSMessages.ReadStatus.UNREAD);
    }

    @Override
    public List<String> getGroupValueContainingNumber(String number) {
        try {
            return mExecutorService.submit(() -> mSmsMessagesDao.getGroupValueContainingNumber(number)).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    @Override
    public LiveData<Integer> getUnreadSmsMessagesCount() {
        return mSmsMessagesDao.getUnreadSmsConversationCount();
    }

    @Override
    public Integer getUnreadSmsMessagesCountByConversationIdInThread(String conversationId) {
        try {
            return mExecutorService.submit(() -> mSmsMessagesDao.getUnreadSmsMessagesInConversationCount(conversationId)).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    @Override
    public Integer getUnreadChatMessagesCountByChatWith(String chatWith) {
        try {
            return mExecutorService.submit(() -> mMessagesDao.unreadMessagesCountFromChatWith(chatWith)).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    @Override
    public void updateReadStatusForMessageId(String messageId) {
        Completable.fromAction(() -> {
            SmsMessage message = mExecutorService.submit(() -> mSmsMessagesDao.getSmsMessageSingleByMessageId(messageId)).get().blockingGet();
            if (message != null && message.getMessageState() != null && !message.getMessageState().isRead()) {
                mSmsMessagesDao.updateReadStatusForMessageId(messageId);
            }
        })
                .subscribeOn(mSchedulerProvider.io())
                .onErrorComplete()
                .subscribe();
    }

    @Override
    public void updateUnreadStatusForMessageId(String messageId) {
        Completable.fromAction(() -> {
                    SmsMessage message = mExecutorService.submit(() -> mSmsMessagesDao.getSmsMessageSingleByMessageId(messageId)).get().blockingGet();
                    if (message != null && message.getMessageState() != null && message.getMessageState().isRead()) {
                        mSmsMessagesDao.updateUnreadStatusForMessageId(messageId);
                    }
                })
                .subscribeOn(mSchedulerProvider.io())
                .onErrorComplete()
                .subscribe();
    }

    @Override
    public void updateReadStatusForConversationId(String conversationId) {
        Completable.fromAction(() -> mSmsMessagesDao.updateReadStatusForConversationId(conversationId))
                .subscribeOn(mSchedulerProvider.io())
                .subscribe();
    }

    @Override
    public void updateReadStatusForGroupId(String groupId) {
        Completable.fromAction(() -> mSmsMessagesDao.updateReadStatusForGroupId(groupId))
                .subscribeOn(mSchedulerProvider.io())
                .subscribe();
    }

    @Override
    public void updateUnreadStatusForGroupId(String groupId) {
        Completable.fromAction(() -> mSmsMessagesDao.updateUnreadStatusForGroupId(groupId))
                .subscribeOn(mSchedulerProvider.io())
                .subscribe();
    }

    @Override
    public void markMessagesUnread(List<DbMessageState> messageStates) {
        Completable.fromAction(() -> {
            for (DbMessageState messageState : messageStates) {
                if (!TextUtils.isEmpty(messageState.getMessageId())) {
                    mSmsMessagesDao.updateUnreadStatusForMessageId(messageState.getMessageId());
                }
            }
        }).subscribeOn(mSchedulerProvider.io())
                .subscribe();
    }

    @Override
    @Nullable
    public String getSuccessfullySentMessageId() {
        try {
            return mExecutorService.submit(() -> mSmsMessagesDao.getSuccessfulMessageId()).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    @Override
    @Nullable
    public DbAttachment getFirstAttachment() {
        try {
            return mExecutorService.submit(() -> mAttachmentsDao.getFirstAttachment()).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    @Override
    public Single<List<DbMessageState>> getMessageStateList(String conversationId) {
        return mMessageStateDao.getMessageStateList(conversationId)
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public List<DbMessageState> getMessageStateListInThread(String groupId) {
        try {
            return mExecutorService.submit(() -> mMessageStateDao.getMessageStateListInThread(groupId)).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    @Override
    public Completable saveContentDataFromLink(String link, String thumbnailLink, String contentType) {
        return Completable
                .fromAction(() -> {
                    String linkToDownload = link;
                    if (thumbnailLink != null) {
                        linkToDownload = thumbnailLink;
                    }
                    byte[] byteArray = getAttachmentDataByteArray(linkToDownload);

                    if (byteArray != null) {
                        if (!contentType.contentEquals(Enums.Attachment.AttachmentContentType.IMAGE_GIF) && !contentType.contains(Enums.Attachment.ContentMajorType.AUDIO)) {
                            byteArray = mAvatarManager.scaleImageByteArrayUnderTwoMB(byteArray);
                        }

                        if (byteArray != null) {
                            mAttachmentsDao.updateAttachment(byteArray, link);
                        }
                    }
                })
                .subscribeOn(mSchedulerProvider.io())
                .onErrorComplete();
    }

    @Override
    public Single<byte[]> saveContentDataFromLinkWithReturn(String link, String contentType) {
        return Single.fromCallable(() -> {
            byte[] byteArray = getAttachmentDataByteArray(link);

            if (byteArray != null) {
                if (!contentType.contentEquals(Enums.Attachment.AttachmentContentType.IMAGE_GIF) && !contentType.contains(Enums.Attachment.ContentMajorType.AUDIO)) {
                    byteArray = mAvatarManager.scaleImageByteArrayUnderTwoMB(byteArray);
                }

                if (byteArray != null) {
                    mAttachmentsDao.updateAttachment(byteArray, link);
                }
            }

            return byteArray;
        }).subscribeOn(mSchedulerProvider.io());
    }

    @Override
    public Completable saveContentData(byte[] contentData, String link) {
        return Completable
                .fromAction(() -> {
                    if (contentData != null) {
                        mAttachmentsDao.updateAttachment(contentData, link);
                    }
                })
                .subscribeOn(mSchedulerProvider.io());
    }

    @Override
    public Single<Long> saveFileDuration(String link, Long duration) {
        return Single.fromCallable(() -> {
            mAttachmentsDao.updateFileDuration(link, duration);
            return duration;
        }).subscribeOn(mSchedulerProvider.io());
    }

    @Override
    public Single<byte[]> getContentDataFromSmsId(String link) {
        return mAttachmentsDao.getContentData(link)
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public SmsConversationDetails getConversationDetailsFrom(SmsConversationDetails conversationDetails) {
        try {
            return mExecutorService.submit(() -> mSmsMessagesDao.getConversationDetailsFrom(conversationDetails)).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    @Override
    public String getGroupIdFrom(String conversationId) {
        try {
            return mExecutorService.submit(() -> mSmsMessagesDao.getGroupIdFrom(conversationId)).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    @Override
    public void deleteAllSmsMessages() {
        mSmsMessagesDao.deleteAllSmsMessages();
    }

    private byte[] getAttachmentDataByteArray(String urlString) {
        try {
            URL url = new URL(urlString);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            URLConnection conn = url.openConnection();

            try (InputStream inputStream = conn.getInputStream()) {
                int n = 0;
                byte[] buffer = new byte[1024];
                while (-1 != (n = inputStream.read(buffer))) {
                    output.write(buffer, 0, n);
                }
            }

            return output.toByteArray();

        } catch (Exception e) {
            mLogManager.logToFile(Enums.Logging.STATE_ERROR, e.getLocalizedMessage());
        }
        return null;
    }


    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // CallLog Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public PagingSource<Integer, CallsDbReturnModel> getCallLogAndVoicemailPagingSource() {
        List<String> callTypesList = Arrays.asList(Enums.Calls.CallTypes.MISSED, Enums.Calls.CallTypes.RECEIVED, Enums.Calls.CallTypes.PLACED);
        return mCallLogsDao.getCallLogAndVoicemailPagingSource(callTypesList);
    }

    @Override
    public PagingSource<Integer, CallsDbReturnModel> getCallLogPagingSource(List<String> callTypesList) {
        return mCallLogsDao.getCallLogEntriesPagingSource(callTypesList);
    }

    @Override
    public PagingSource<Integer, CallsDbReturnModel> getCallLogAndVoicemailPagingSource(String query) {
        List<String> callTypesList = Arrays.asList(Enums.Calls.CallTypes.MISSED, Enums.Calls.CallTypes.RECEIVED, Enums.Calls.CallTypes.PLACED);
        return mCallLogsDao.getCallLogAndVoicemailPagingSource(callTypesList, "%" + query + "%");
    }

    @Override
    public PagingSource<Integer, CallsDbReturnModel> getCallLogPagingSource(List<String> callTypesList, String query) {
        return mCallLogsDao.getCallLogEntriesPagingSource(callTypesList, "%" + query + "%");
    }

    @Override
    public void insertBWCallLogs(final ArrayList<CallLogEntry> callLogEntries) {
        Completable
                .fromAction(() -> mCallLogsDao.insertBWCallLogs(callLogEntries))
                .subscribeOn(mSchedulerProvider.io())
                .subscribe();
    }

    @Override
    public LiveData<List<CallLogEntry>> getCallLogEntriesLiveData(List<String> callTypesList) {
        return mCallLogsDao.getCallLogEntriesLiveData(callTypesList);
    }

    @Override
    public Completable insertCallLogs(ArrayList<DbCallLogEntry> callLogs) {
        return Completable
                .fromAction(() -> mCallLogsDao.replaceCallLogs(callLogs))
                .subscribeOn(mSchedulerProvider.io());
    }

    @Override
    public void insertCallLogsInThread(ArrayList<DbCallLogEntry> callLogEntries) {
        mCallLogsDao.replaceCallLogs(callLogEntries);
    }

    @Override
    public Completable deleteCallLogByCallLogId(final String callLogId) {
        return Completable
                .fromAction(() -> mCallLogsDao.deleteCallLogFromId(callLogId))
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public void deleteCallLogByCallLogIdInThread(String callLogId) {
        mCallLogsDao.deleteCallLogFromId(callLogId);
    }

    @Override
    public Completable deleteCallLogs() {
        return Completable
                .fromAction(() -> mCallLogsDao.deleteCallLogs())
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public void deleteCallLogsAndVoicemails() {
        mCallLogsDao.deleteCallLogs();
        deleteAllVoicemails();
    }

    @Override
    public void deleteVoiceConversationMessagesInThread() {
        setCacheExpiryMillis(SharedPreferencesManager.VOICE_CONVERSATION_MESSAGES);
        mCallLogsDao.deleteCallLogs();
    }

    @Override
    public Completable markAllCallLogEntriesRead() {
        return Completable
                .fromAction(() -> mCallLogsDao.markAllCallLogEntriesAsRead())
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Completable markCallLogEntryRead(final String callLogId) {
        return Completable
                .fromAction(() -> mCallLogsDao.markCallLogEntryAsRead(callLogId))
                .subscribeOn(mSchedulerProvider.io());
    }

    @Override
    public Completable markCallLogEntryUnread(final String callLogId) {
        return Completable
                .fromAction(() -> mCallLogsDao.markCallLogEntryAsUnread(callLogId))
                .subscribeOn(mSchedulerProvider.io());
    }

    @Override
    public Completable swapCallLogReadState(String callLogId, Boolean readStatus) {
        if (readStatus){
            return markCallLogEntryRead(callLogId);
        } else {
            return markCallLogEntryUnread(callLogId);
        }
    }

    @Override
    public LiveData<Integer> getUnreadCallLogEntriesCount() {
        return mCallLogsDao.getUnreadCallLogEntriesCount();
    }

    @Override
    public LiveData<Integer> getUnreadMissedCallLogEntriesCount() {
        return mCallLogsDao.getUnreadMissedCallLogEntriesCount();
    }

    @Override
    public Completable bulkUpdateCallLogsReadStatus(final int readStatus, final List<String> callLogIdList){
        return Completable.fromAction(() -> mCallLogsDao.updateCallLogEntriesReadStatus(readStatus, callLogIdList))
                .subscribeOn(mSchedulerProvider.io());
    }

    @Override
    public LiveData<Integer> getUnreadCallLogAndVoicemailCount() {
        return mCallLogsDao.getUnreadCallLogAndVoicemailCount();
    }

    @Override
    public Integer getLastCallLogsPageFetched() {
        try {
            return mExecutorService.submit(() -> Math.max(mCallLogsDao.getLastPageFetched(), mVoicemailDao.getLastPageFetched())).get();
        } catch (InterruptedException | ExecutionException e) {
            return 0;
        }
    }

    @Override
    public DbCallLogEntry getCallLogByLogId(String callLogId) {
        try {
            return mExecutorService.submit(() -> mCallLogsDao.getCallLogByLogId(callLogId)).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // DbManager setAvatar Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public Disposable setAvatar(@NonNull final AvatarView avatarView, final String jid) {
        UserDetails userDetails = GsonUtil.getObject(UserDetails.class, getSessionSettingValue(Enums.Session.DatabaseKey.USER_DETAILS));

        if (userDetails != null && !TextUtils.isEmpty(userDetails.getImpId()) && !TextUtils.equals(jid.toLowerCase(), userDetails.getImpId().toLowerCase())) {
            return getVCard(jid)
                    .subscribe(dbVCard -> {
                        if (dbVCard.getPhotoData() != null) {
                            AvatarInfo avatarInfo = new AvatarInfo.Builder()
                                    .setPhotoData(dbVCard.getPhotoData())
                                    .build();

                            avatarView.setAvatar(avatarInfo);
                        }
                    });

        } else {
            return getOwnAvatar()
                    .subscribe(dbSession -> {
                        if (!TextUtils.isEmpty(dbSession.getValue())) {
                            AvatarInfo avatarInfo = new AvatarInfo.Builder()
                                    .setPhotoData(mAvatarManager.stringToByteArray(dbSession.getValue()))
                                    .build();

                            avatarView.setAvatar(avatarInfo);
                        }
                    });
        }
    }

    @Override
    public Maybe<AvatarInfo> getAvatarInfo(final String jid) {
        UserDetails userDetails = GsonUtil.getObject(UserDetails.class, getSessionSettingValue(Enums.Session.DatabaseKey.USER_DETAILS));

        if (userDetails != null && !TextUtils.isEmpty(userDetails.getImpId())
                && !TextUtils.equals(jid.toLowerCase(), userDetails.getImpId().toLowerCase())) {
            return getVCard(jid)
                    .map(dbVCard -> {
                        AvatarInfo.Builder builder = new AvatarInfo.Builder();

                        if (dbVCard != null) {
                            builder.setPhotoData(dbVCard.getPhotoData());
                        }

                        return builder.build();
                    });

        } else {
            return getOwnAvatar()
                    .map(dbSession -> {
                        AvatarInfo.Builder builder = new AvatarInfo.Builder();

                        if (dbSession != null && !TextUtils.isEmpty(dbSession.getValue())) {
                            builder.setPhotoData(mAvatarManager.stringToByteArray(dbSession.getValue()));
                        }

                        return builder.build();
                    });
        }
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // DbManager Group Methods
    // --------------------------------------------------------------------------------------------

    @Override
    public void insertGroups(ArrayList<DbGroup> dbGroups) {
        String transactionId = UUID.randomUUID().toString();

        Completable.fromAction(() -> {
                    for (DbGroup group : dbGroups) {
                        group.setTransactionId(transactionId);
                        mGroupDao.insertGroup(group);
                    }

                    mGroupDao.clearOldGroupsByTransactionId(transactionId);

                }).subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .subscribe();
    }

    @Override
    public ArrayList<DbGroup> getGroups() {
        try {
            return mExecutorService.submit(() -> new ArrayList<>(mGroupDao.getGroups())).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // Voicemail Methods
    // --------------------------------------------------------------------------------------------

    @Override
    public Completable insertVoicemails(ArrayList<DbVoicemail> voicemails) {
        return Completable
                .fromAction(() -> mVoicemailDao.insertVoicemails(voicemails))
                .subscribeOn(mSchedulerProvider.io());
    }

    @Override
    public void insertVoicemailsInThread(ArrayList<DbVoicemail> voicemails) {
        mVoicemailDao.insertVoicemails(voicemails);
    }

    @Override
    public void insertVoicemailTranscriptions(ArrayList<VoicemailDetails> voicemailList) {
        Completable.fromAction(() -> {
                    mLogManager.logToFile(Enums.Logging.STATE_INFO, "Inserting voicemail transcriptions");
                    for (VoicemailDetails voicemail : voicemailList) {
                        if (!TextUtils.isEmpty(voicemail.getVoicemailId())) {
                            mVoicemailDao.insertVoicemailTranscriptions(voicemail.getTranscriptText(),
                                                                        voicemail.getTranscriptRating(),
                                                                        Objects.requireNonNull(voicemail.getVoicemailId()));
                        }
                    }

                }).subscribeOn(mSchedulerProvider.io())
                .doOnError(throwable -> mLogManager.logToFile(Enums.Logging.STATE_ERROR, "Error inserting voicemail transcriptions: " + throwable.getLocalizedMessage()))
                .subscribe();
    }

    @Override
    public PagingSource<Integer, CallsDbReturnModel> getVoicemailPagingSource() {
        return mVoicemailDao.getVoicemailPagingSource();
    }

    @Override
    public PagingSource<Integer, CallsDbReturnModel> getVoicemailPagingSource(String query) {
        return mVoicemailDao.getVoicemailPagingSource("%" + query + "%");
    }

    @Override
    public String getVoicemailRatingById(String voicemailId) {
        try {
            return mExecutorService.submit(() -> mVoicemailDao.getRatingByVoicemailId(voicemailId)).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    @Override
    public DbVoicemail getVoicemailById(String messageId) {
        try {
            return mExecutorService.submit(() -> mVoicemailDao.getVoicemailById(messageId)).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    @Override
    public void updateVoicemailRating(String rating, String messageId) {
        Completable.fromAction(() -> mVoicemailDao.updateRating(rating, messageId))
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .subscribe();
    }

    @Override
    public void updateVoicemailDuration(int duration, String messageId) {
        Completable.fromAction(() -> mVoicemailDao.updateDuration(duration, messageId))
                .subscribeOn(mSchedulerProvider.io())
                .subscribe();
    }

    @Override
    public LiveData<List<Voicemail>> getVoicemailsLiveData() {
        return mVoicemailDao.getVoicemailLiveData();
    }


    @Override
    public LiveData<Boolean> getVoicemailReadLiveData(String messageId) {
        return mVoicemailDao.getVoicemailReadLiveData(messageId);
    }

    @Override
    public void markVoicemailRead(String messageId) {
        mVoicemailDao.markVoicemailRead(messageId);
        //updateUnreadVoicemailCount();
    }

    @Override
    public void markVoicemailUnread(String messageId) {
        mVoicemailDao.markVoicemailUnread(messageId);
        //updateUnreadVoicemailCount();
    }

    @Override
    public Completable bulkUpdateVoicemailReadStatus(int readStatus, List<String> voicemailIdList) {
        return Completable.fromAction(() -> mVoicemailDao.updateVoicemailEntriesReadStatus(readStatus, voicemailIdList))
                .subscribeOn(mSchedulerProvider.io());
    }

    @Override
    public void patchConversationVoicemailRead(String messageId, Boolean isRead) {
        if (isRead) {
            mVoicemailDao.markConversationVoicemailRead(messageId);
        } else {
            mVoicemailDao.markConversationVoicemailUnread(messageId);
        }

        //updateUnreadVoicemailCount();
    }

    public void updateUnreadVoicemailCount(int count) {
        Completable.fromAction(() -> setSessionSetting(Enums.Session.DatabaseKey.NEW_VOICEMAIL_MESSAGES_COUNT,
                                                       String.valueOf(count)))
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .subscribe();
    }

    public void updateUnreadMissedCallCount(int count) {
        Completable.fromAction(() -> setSessionSetting(Enums.Session.DatabaseKey.NEW_VOICE_CALL_MESSAGES_COUNT,
                                                       String.valueOf(count)))
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .subscribe();
    }

    public void updateUnreadChatCount(int count) {
        Completable.fromAction(() -> setSessionSetting(Enums.Session.DatabaseKey.NEW_CHAT_MESSAGES_COUNT,
                                                       String.valueOf(count)))
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .subscribe();
    }

    public void updateUnreadSMSCount(int count) {
        Completable.fromAction(() -> setSessionSetting(Enums.Session.DatabaseKey.NEW_SMS_MESSAGES_COUNT,
                                                       String.valueOf(count)))
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .subscribe();
    }

    @Override
    public void markAllVoicemailsRead() {
        mVoicemailDao.markAllVoicemailsRead();
    }

    @Override
    public void deleteAllVoicemails() {
        mVoicemailDao.deleteVoicemails();
    }

    @Override
    public void deleteVoicemail(String messageId) {
        mVoicemailDao.deleteVoicemail(messageId);
    }

    @Override
    public void bulkDeleteVoicemails(ArrayList<String> voicemailSelectedList) {
        mVoicemailDao.deleteVoicemails(voicemailSelectedList);
    }

    @Override
    public void bulkDeleteCallLogs(ArrayList<String> callLogSelectedList){
        mCallLogsDao.deleteCallLogs(callLogSelectedList);
    }


    @Override
    public void updateVoicemailReadState(boolean isRead, String messageId) {
        mAppDatabase.runInTransaction(() -> {
            if (isRead) {
                markVoicemailRead(messageId);
            } else {
                markVoicemailUnread(messageId);
            }
        });
    }

    @Override
    public PagingSource<Integer, SmsMessage> getSmsConversationPagingSource(String groupId) {
        return mSmsMessagesDao.getSmsConversationPagingSource(groupId);
    }

    @Override
    public Completable saveSmsMessages(List<Data> data, String phoneNumber, int sentStatus, String userUuid, List<SmsTeam> allSavedTeams) {
        return Completable.fromAction(() -> mSmsMessagesDao.insertSmsMessages(mAppDatabase,
                                                                              new ArrayList<>(data),
                                                                              phoneNumber,
                                                                              userUuid,
                                                                              sentStatus,
                                                                              allSavedTeams,
                                                                              null)).subscribeOn(mSchedulerProvider.io());
    }

    @Override
    public Completable updateAllSmsSentStatus() {
        return Completable.fromAction(() -> {
                    mSmsMessagesDao.updateAllSentStatusFromPendingToFailed(Enums.SMSMessages.SentStatus.FAILED, Enums.SMSMessages.SentStatus.PENDING);
                }).subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public LiveData<List<SmsMessage>> getAllSmsMessages() {
        return mSmsMessagesDao.getAllMessages();
    }

    @Override
    public PagingSource<Integer, SmsMessage> getFilteredSmsMessagePagingSource(String filter) {
        return mSmsMessagesDao.getFilteredMessagesPagingSource(filter);
    }

    @Override
    public PagingSource<Integer, SmsMessage> getAllSmsMessagesPagingSource() {
        return mSmsMessagesDao.getAllMessagesPagingSource();
    }

    @Override
    public Completable saveSendMessage(Data data, String telephoneNumber, int status, String userUuid, List<SmsTeam> allSavedTeams, String groupId) {
        ArrayList<Data> dataList = new ArrayList<>();
        dataList.add(data);

        return Completable.fromAction(() -> mSmsMessagesDao.insertSmsMessages(mAppDatabase,
                                                                              dataList,
                                                                              telephoneNumber,
                                                                              userUuid,
                                                                              status,
                                                                              allSavedTeams,
                                                                              groupId))
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public void deleteSmsMessageByMessageId(String messageId) {
        Completable.fromAction(() -> {
                    mSmsMessagesDao.deleteSmsMessageByMessageId(messageId);
                }).subscribeOn(mSchedulerProvider.io())
                .subscribe();
    }

    @Override
    public void deleteDraftMessagesFromConversation(String groupId, Integer draftStatus) {
        Completable.fromAction(() -> {
                    mSmsMessagesDao.deleteDraftMessagesByConversation(groupId, draftStatus);
                }).subscribeOn(mSchedulerProvider.io())
                .subscribe();
    }

    @Override
    public void deleteMessagesFromConversationByGroupId(String groupId) {
        Completable.fromAction(() -> mSmsMessagesDao.deleteMessagesFromConversationByGroupId(groupId)).subscribeOn(mSchedulerProvider.io()).subscribe();
    }

    @Override
    public List<SmsMessage> getDraftMessagesFromConversationInThread(String groupId, Integer draftStatus) {
        try {
            return mExecutorService.submit(() -> mSmsMessagesDao.getAllDraftMessagesFromConversation(groupId, draftStatus)).get();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public SmsMessage getMostRecentMessageFromConversationWithoutDraft(String groupId, Integer draftStatus) {
        try {
            return mExecutorService.submit(() -> mSmsMessagesDao.getMostRecentMessageFromConversationWithoutDraft(groupId, draftStatus)).get();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void updateSentStatus(String messageId, int status) {
        Completable.fromAction(() -> {
                    mSmsMessagesDao.updateSentStatus(messageId, status);
                }).subscribeOn(mSchedulerProvider.io())
                .subscribe();
    }

    @Override
    public void updateMessageIdAndSentStatus(@NotNull String tempMessageId, String messageId, int sentStatus, String groupId) {
        Completable.fromAction(() -> {
                    mSmsMessagesDao.updateMessageIdAndSentStatus(tempMessageId, messageId, sentStatus, groupId);
                    mParticipantsDao.updateParticipantsForNewConversation(tempMessageId, groupId);
                }).subscribeOn(mSchedulerProvider.io())
                .subscribe();
    }

    @Override
    public Integer getCurrentConversationListCount() {
        try {
            return mExecutorService.submit(() -> mSmsMessagesDao.getCurrentConversationListCount()).get();
        } catch (InterruptedException | ExecutionException e) {
            return 0;
        }
    }

    @Override
    public Integer getCurrentConversationCount(String groupId) {
        try {
            return mExecutorService.submit(() -> mSmsMessagesDao.getCurrentConversationCount(groupId)).get();
        } catch (InterruptedException | ExecutionException e) {
            return 0;
        }
    }

    @Override
    public void deleteMessagesByGroupId(String groupId) {
        Completable.fromAction(() -> {
            List<SmsMessage> messages = mSmsMessagesDao.getAllMessagesForGroupId(groupId);
            for (SmsMessage message : messages) {
                mAttachmentsDao.deleteAttachments(message.getId());
            }
            mSmsMessagesDao.deleteMessagesByGroupId(groupId);
        })
        .subscribeOn(mSchedulerProvider.io())
        .subscribe();
    }

    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // region Logging Methods
    // --------------------------------------------------------------------------------------------

    @Override
    public void insertLog(
            @NonNull final CompositeDisposable compositeDisposable, @NonNull DbLogging log) {
        compositeDisposable.add(
                Completable.fromAction(() -> mLoggingDao.insertLog(log))
                        .subscribeOn(mSchedulerProvider.io())
                        .observeOn(mSchedulerProvider.ui())
                        .subscribe()
        );
    }

    @Override
    public void clearAllLogs(@NonNull final CompositeDisposable compositeDisposable) {
        compositeDisposable.add(
                Completable
                        .fromAction(() -> mLoggingDao.clearAllLogs())
                        .subscribeOn(mSchedulerProvider.io())
                        .observeOn(mSchedulerProvider.ui())
                        .subscribe()
        );
    }

    @Override
    public void clearPostedLogs(int count) {
        Completable
                .fromAction(() -> mLoggingDao.clearPostedLogs(count))
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .subscribe();
    }

    @Override
    public int getLogsCount() {
        return mLoggingDao.getLogsCount();
    }

    @Override
    public List<DbLogging> getLogs() {
        return mLoggingDao.getAllLogs();
    }

    @Override
    public List<DbLogging> getLogs(int count) {
        return mLoggingDao.getLimitedLogs(count);
    }


    // --------------------------------------------------------------------------------------------
    // endregion Logging Methods
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // Table Count Methods
    // --------------------------------------------------------------------------------------------

    @Override
    public LiveData<DbTableCountModel> getTableCountsLiveData() {
        return mCompleteContactDao.getTableCountsLiveData();
    }

    // --------------------------------------------------------------------------------------------
    // endregion Table Count Methods
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // DbManager Cache Expire Management Methods
    // --------------------------------------------------------------------------------------------
    private void setCacheExpiryMillis(@SharedPreferencesManager.SettingsKey String key) {
        mSharedPreferencesManager.setLong(getLastCacheTimestampKey(key), mCalendarManager.getNowMillis());
    }

    private String getLastCacheTimestampKey(@SharedPreferencesManager.SettingsKey String key) {
        return key + SharedPreferencesManager.LAST_CACHE_TIMESTAMP_SUFFIX;
    }

    @Override
    public void updateConnectContactsExpiry() {
        setCacheExpiryMillis(SharedPreferencesManager.CONNECT_CONTACTS);
    }

    @Override
    public void updateRosterContactsPushExpiry() {
        setCacheExpiryMillis(SharedPreferencesManager.ROSTER_CONTACT_PUSH);
    }

    public void updateConnectCallLogsExpiry(){
        setCacheExpiryMillis(SharedPreferencesManager.VOICE_CONVERSATION_MESSAGES);
    }

    @Override
    public void updateConnectSMSMessagesExpiry() {
        setCacheExpiryMillis(SharedPreferencesManager.SMS_CONVERSATION_MESSAGES);
    }

    @Override
    public boolean isCacheExpired(@SharedPreferencesManager.SettingsKey String key) {
        long lastCacheTimestamp = mSharedPreferencesManager.getLong(getLastCacheTimestampKey(key), 0);

        switch (key) {
            case SharedPreferencesManager.ROSTER_CONTACTS:
                if (lastCacheTimestamp < mCalendarManager.getNowMillis() - ROSTER_CACHE_EXPIRY_MILLIS) {
                    return true;
                }
                break;
            case SharedPreferencesManager.ENTERPRISE_CONTACTS:
                if (lastCacheTimestamp < mCalendarManager.getNowMillis() - ENTERPRISE_CACHE_EXPIRY_MILLIS) {
                    return true;
                }
                break;
            case SharedPreferencesManager.ROSTER_CONTACT_PUSH:
                if (lastCacheTimestamp < mCalendarManager.getNowMillis() - ROSTER_PUSH_CACHE_EXPIRY_MILLIS) {
                    return true;
                }
                break;
            case SharedPreferencesManager.CONNECT_ROOMS:
                if (lastCacheTimestamp < mCalendarManager.getNowMillis() - CONNECT_CACHE_EXPIRY_MILLIS) {
                    return true;
                }
                break;
            case SharedPreferencesManager.VOICE_CONVERSATION_MESSAGES:
                if (lastCacheTimestamp < mCalendarManager.getNowMillis() - VOICE_CONVERSATION_CACHE_EXPIRY_MILLIS) {
                    return true;
                }
                break;
            case SharedPreferencesManager.SCHEDULES:
                if (lastCacheTimestamp < mCalendarManager.getNowMillis() - SCHEDULES_CACHE_EXPIRY_MILLIS) {
                    return true;
                }
                break;
            case SharedPreferencesManager.SMS_CONVERSATION_MESSAGES:
                if (lastCacheTimestamp < mCalendarManager.getNowMillis() - SMS_MESSAGES_CACHE_EXPIRY_MILLIS) {
                    return true;
                }
                break;
        }

        return false;
    }

    @Override
    public void expireContactCache() {
        mSharedPreferencesManager.setLong(getLastCacheTimestampKey(SharedPreferencesManager.ROSTER_CONTACTS), 0);
        mSharedPreferencesManager.setLong(getLastCacheTimestampKey(SharedPreferencesManager.ENTERPRISE_CONTACTS), 0);
        mSharedPreferencesManager.setLong(getLastCacheTimestampKey(SharedPreferencesManager.VOICE_CONVERSATION_MESSAGES), 0);
        mSharedPreferencesManager.setLong(getLastCacheTimestampKey(SharedPreferencesManager.SCHEDULES), 0);
    }

    @Override
    public void expireVoiceConversationMessagesCache() {
        mSharedPreferencesManager.setLong(getLastCacheTimestampKey(SharedPreferencesManager.VOICE_CONVERSATION_MESSAGES), 0);
    }

    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // DbManager Meetings Methods
    // --------------------------------------------------------------------------------------------

    @Override
    public Completable saveMeetings(List<DbMeeting> meetingList, Long startDate) {
        return Completable.fromAction(() -> mMeetingDao.refreshMeetings(meetingList, startDate)).subscribeOn(mSchedulerProvider.io());

    }


    @Override
    public List<String> getMeetingsBetweenDates(Long startDate, Long endDate) {
        try {
            return mExecutorService.submit(() -> mMeetingDao.getMeetingsBetweenDates(startDate, endDate)).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // DbManager Schedules Methods
    // --------------------------------------------------------------------------------------------

    @Override
    public PagingSource<Integer, Schedule> getSchedulesPagingSource() {
        return mSchedulesDao.getSchedulesPagingSource();
    }

    @Override
    public void insertSchedules(boolean isRefresh, ArrayList<UserScheduleResponse> schedules, Integer pageNumber) {
        mSchedulesDao.insertSchedules(isRefresh, schedules, pageNumber);
    }

    public Single<String> insertSchedule(UserScheduleResponse schedule) {
        return Single.just(mSchedulesDao.insertSchedules(false, new ArrayList<UserScheduleResponse>() {{
            add(schedule);
        }}, 0));
    }

    public boolean isScheduleNameInUse(String scheduleName) {
        try {
            int scheduleCount = mExecutorService.submit(() -> mSchedulesDao.isScheduleNameInUse(scheduleName)).get();
            return scheduleCount > 0;
        } catch (Exception e) {
            mLogManager.logToFile(Enums.Logging.STATE_ERROR, e.getClass().getSimpleName());
            return false;
        }
    }

    @Override
    public LiveData<DbSession> getSessionLiveDataFromKey(String key) {
        return mSessionDao.getSessionLiveDataFromKey(key);
    }

    @Override
    public Flow<DbSession> getSessionFlowFromKey(String key) {
        return mSessionDao.getSessionFlowFromKey(key);
    }

    @Override
    public LiveData<List<DbSession>> getSessionLiveDataFromMultipleKeys(List<String> keys) {
        return mSessionDao.getSessionLiveDataFromMultipleKeys(keys);
    }

    @NonNull
    @Override
    public LiveData<Integer> getTotalUnreadNotificationsLiveDataFromMultipleKeys(@NonNull List<String> keys)
    {
        return mSessionDao.getTotalUnreadNotificationsLiveDataFromMultipleKeys(keys);
    }

    @Override
    public Integer getLastSchedulesPageFetched() {
        try {
            return mExecutorService.submit(() -> mSchedulesDao.getLastPageFetched()).get();
        } catch (InterruptedException | ExecutionException e) {
            return 0;
        }
    }

    @Override
    public Flow<Schedule> getDndScheduleFlow() {
        return mSchedulesDao.getDndScheduleFlow();
    }

    @Override
    public String getDndScheduleId() {
        return mSchedulesDao.getDndScheduleId();
    }

    @Override
    public void setDndSchedule(String scheduleId) {
        mAppDatabase.runInTransaction(() -> mSchedulesDao.setDndScheduleSelected(scheduleId));
    }

    @Override
    public void deleteDndSchedules() {
        mAppDatabase.runInTransaction(() -> mSchedulesDao.deleteDndSchedules());
    }

    @Override
    public void deleteScheduleByScheduleId(String scheduleId) {
        mAppDatabase.runInTransaction(() -> mSchedulesDao.deleteSchedule(scheduleId));
    }

    // --------------------------------------------------------------------------------------------

    private RxBus getRxBus() {
        if (mRxBus == null) {
            mRxBus = RxBus.INSTANCE;
        }

        return mRxBus;
    }

    @VisibleForTesting
    public void setRxBus(RxBus rxBus) {
        mRxBus = rxBus;
    }

    @Override
    public AppDatabase getDatabase() {
        return mAppDatabase;
    }

    @VisibleForTesting
    public void setDatabase(AppDatabase appDatabase) {
        mAppDatabase = appDatabase;
    }

    @VisibleForTesting
    public void setVCardDao(VCardDao vCardDao) {
        mVCardDao = vCardDao;
    }

    @VisibleForTesting
    public void setContactDao(ContactDao contactDao) {
        mContactDao = contactDao;
    }

    @VisibleForTesting
    public void setMessagesDao(MessagesDao messagesDao) {
        mMessagesDao = messagesDao;
    }

    @VisibleForTesting
    public void setCompleteContactDao(CompleteContactDao completeContactDao) {
        mCompleteContactDao = completeContactDao;
    }

    @VisibleForTesting
    public void closeDatabase() {
        if (mAppDatabase != null) {
            mAppDatabase.close();
        }
    }

    @VisibleForTesting
    public void setPresenceDao(PresenceDao presenceDao) {
        mPresenceDao = presenceDao;
    }

    @VisibleForTesting
    public void setCallLogsDao(CallLogsDao callLogsDao) {
        mCallLogsDao = callLogsDao;
    }

    @VisibleForTesting
    public void setSessionDao(SessionDao sessionDao) {
        mSessionDao = sessionDao;
    }
}
