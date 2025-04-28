/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers.apimanagers;

import static com.nextiva.nextivaapp.android.constants.Enums.Notification.TypeIDs.FAILED_CHAT_MESSAGE_NOTIFICATION_ID;
import static com.nextiva.nextivaapp.android.fcm.NextivaFirebaseMessagingService.REQUEST_CODE;

import android.app.Application;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nextiva.nextivaapp.android.BuildConfig;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.DbManager;
import com.nextiva.nextivaapp.android.db.model.DbGroup;
import com.nextiva.nextivaapp.android.db.model.DbPresence;
import com.nextiva.nextivaapp.android.db.model.DbVCard;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UmsRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository;
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager;
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager;
import com.nextiva.nextivaapp.android.managers.interfaces.ConfigManager;
import com.nextiva.nextivaapp.android.managers.interfaces.ConnectionStateManager;
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager;
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager;
import com.nextiva.nextivaapp.android.managers.interfaces.NotificationManager;
import com.nextiva.nextivaapp.android.managers.interfaces.PushNotificationManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager;
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager;
import com.nextiva.nextivaapp.android.models.ChatConversation;
import com.nextiva.nextivaapp.android.models.ChatMessage;
import com.nextiva.nextivaapp.android.models.NextivaContact;
import com.nextiva.nextivaapp.android.models.NextivaVCard;
import com.nextiva.nextivaapp.android.models.NextivaVCardPhoto;
import com.nextiva.nextivaapp.android.models.UserDetails;
import com.nextiva.nextivaapp.android.models.net.PendingChatMessage;
import com.nextiva.nextivaapp.android.models.net.broadsoft.BroadsoftVCard;
import com.nextiva.nextivaapp.android.models.net.broadsoft.BroadsoftVCardBody;
import com.nextiva.nextivaapp.android.models.net.broadsoft.contactstorage.BroadsoftAddressbookContact;
import com.nextiva.nextivaapp.android.models.net.broadsoft.contactstorage.BroadsoftAddressbookGroup;
import com.nextiva.nextivaapp.android.models.net.broadsoft.contactstorage.BroadsoftContactStorage;
import com.nextiva.nextivaapp.android.models.net.broadsoft.contactstorage.BroadsoftContactStorageSetBody;
import com.nextiva.nextivaapp.android.models.net.broadsoft.ondemandpresence.BroadsoftOnDemandPresence;
import com.nextiva.nextivaapp.android.models.net.broadsoft.ondemandpresence.BroadsoftOnDemandPresencePostBody;
import com.nextiva.nextivaapp.android.models.net.broadsoft.superpresence.BroadsoftSuperPresence;
import com.nextiva.nextivaapp.android.models.net.broadsoft.superpresence.BroadsoftSuperPresenceResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.superpresence.BroadsoftSuperPresenceShowPostBody;
import com.nextiva.nextivaapp.android.models.net.broadsoft.superpresence.BroadsoftSuperPresenceStatusPostBody;
import com.nextiva.nextivaapp.android.models.net.broadsoft.ums.BroadsoftChatMessageBody;
import com.nextiva.nextivaapp.android.models.net.broadsoft.ums.BroadsoftChatMessageDetails;
import com.nextiva.nextivaapp.android.models.net.broadsoft.ums.BroadsoftDeviceRegistrationBody;
import com.nextiva.nextivaapp.android.models.net.broadsoft.ums.BroadsoftDeviceRegistrationDetails;
import com.nextiva.nextivaapp.android.models.net.broadsoft.ums.BroadsoftMarkMessagesReadBody;
import com.nextiva.nextivaapp.android.models.net.broadsoft.ums.BroadsoftMessageId;
import com.nextiva.nextivaapp.android.models.net.broadsoft.ums.BroadsoftSetVCardBody;
import com.nextiva.nextivaapp.android.models.net.broadsoft.ums.BroadsoftUmsChatMessage;
import com.nextiva.nextivaapp.android.models.net.broadsoft.ums.BroadsoftUmsChatMessagesResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.ums.BroadsoftUmsJid;
import com.nextiva.nextivaapp.android.net.BroadsoftUmsApi;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.ChatConversationsResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.RegisterDeviceResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.UnregisterDeviceResponseEvent;
import com.nextiva.nextivaapp.android.net.interceptors.UmsHostInterceptor;
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider;
import com.nextiva.nextivaapp.android.util.ApplicationUtil;
import com.nextiva.nextivaapp.android.util.BroadsoftUtil;
import com.nextiva.nextivaapp.android.util.ChatUtil;
import com.nextiva.nextivaapp.android.util.GsonUtil;
import com.nextiva.nextivaapp.android.util.StringUtil;
import com.nextiva.nextivaapp.android.util.XmlUtil;
import com.nextiva.nextivaapp.android.xmpp.util.NextivaXMPPConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import retrofit2.HttpException;

/**
 * Created by adammacdonald on 3/7/18.
 */

public class BroadsoftUmsApiManager extends BaseApiManager implements
        UmsRepository {

    private final SessionManager mSessionManager;
    private final ConfigManager mConfigManager;
    private final DbManager mDbManager;
    private final SchedulerProvider mSchedulerProvider;
    private final AvatarManager mAvatarManager;
    private final UserRepository mUserRepository;
    private final NotificationManager mNotificationManager;
    private final ConnectionStateManager mConnectionStateManager;
    private final DialogManager mDialogManager;
    private final SharedPreferencesManager mSharedPreferencesManager;

    private final BroadsoftUmsApi mBroadsoftUmsApi;
    private final UmsHostInterceptor mUmsHostInterceptor;
    private final PushNotificationManager mPushNotificationManager;
    private final CalendarManager mCalendarManager;

    private boolean isRegisteringDevice = false;

    private ArrayList<PendingChatMessage> mPendingChatMessages = new ArrayList<>();

    @Inject
    public BroadsoftUmsApiManager(Application application,
                                  SessionManager sessionManager,
                                  ConfigManager configManager,
                                  DbManager dbManager,
                                  LogManager logManager,
                                  SchedulerProvider schedulerProvider,
                                  BroadsoftUmsApi broadsoftUmsApi,
                                  UmsHostInterceptor umsHostInterceptor,
                                  PushNotificationManager pushNotificationManager,
                                  AvatarManager avatarManager,
                                  UserRepository userRepository,
                                  CalendarManager calendarManager,
                                  NotificationManager notificationManager,
                                  ConnectionStateManager connectionStateManager,
                                  DialogManager dialogManager,
                                  SharedPreferencesManager sharedPreferencesManager) {

        super(application, logManager);

        mSessionManager = sessionManager;
        mConfigManager = configManager;
        mDbManager = dbManager;
        mSchedulerProvider = schedulerProvider;
        mBroadsoftUmsApi = broadsoftUmsApi;
        mUmsHostInterceptor = umsHostInterceptor;
        mPushNotificationManager = pushNotificationManager;
        mAvatarManager = avatarManager;
        mUserRepository = userRepository;
        mCalendarManager = calendarManager;
        mNotificationManager = notificationManager;
        mConnectionStateManager = connectionStateManager;
        mDialogManager = dialogManager;
        mSharedPreferencesManager = sharedPreferencesManager;

        setupUmsUdid();
        mDbManager.setPendingMessagesFailed();

        mDbManager.setPendingMessagesFailed();

        mDbManager.setPendingMessagesFailed();

        mDbManager.setPendingMessagesFailed();

        mDbManager.setPendingMessagesFailed();

        new Handler(Looper.getMainLooper()).post(() ->
                                                         mConnectionStateManager.getUMSConnectionStateLiveData().observeForever(connected -> {
                                                             if (mPendingChatMessages.size() > 0) {
                                                                 if (connected) {
                                                                     sendPendingChatMessage();

                                                                 } else {
                                                                     clearPendingMessagesWithError();
                                                                 }
                                                             }
                                                         })
        );
    }

    private void clearPendingMessagesWithError() {
        for (PendingChatMessage chatMessage : mPendingChatMessages) {
            mDbManager.updateMessageSentStatus(chatMessage.getMessage().getMessageId(), Enums.Chats.SentStatus.FAILED);
        }

        mPendingChatMessages = new ArrayList<>();

        if (ApplicationUtil.isAppInForeground(mApplication)) {
            mDialogManager.showDialog(mApplication,
                                      R.string.error_general_error_title,
                                      R.string.error_message_failed_to_send,
                                      R.string.general_ok,
                                      (dialog, which) -> {
                                      });

        } else {
            mNotificationManager.showNotification(mApplication,
                                                  Enums.Notification.ChannelIDs.FAILED_CHAT_MESSAGE,
                                                  FAILED_CHAT_MESSAGE_NOTIFICATION_ID,
                                                  REQUEST_CODE,
                                                  mApplication.getString(R.string.error_message_failed_to_send),
                                                  mApplication.getString(R.string.error_general_error_title),
                                                  null,
                                                  null,
                                                  null);
        }
    }

    private void sendPendingChatMessage() {
        if (mConnectionStateManager.isUMSConnected() && mPendingChatMessages.size() > 0) {
            Single<String> chatMessage = mPendingChatMessages.get(0).getSingle();
            mPendingChatMessages.remove(0);

            chatMessage.subscribe(new DisposableSingleObserver<String>() {
                @Override
                public void onSuccess(String s) {
                    if (mPendingChatMessages.size() > 0) {
                        sendPendingChatMessage();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    clearPendingMessagesWithError();
                }
            });
        } else if (mPendingChatMessages.size() > 0) {
            clearPendingMessagesWithError();
        }
    }

    private void reconnectToUmsIfNecessary() {
        setupUmsUdid();

        if (mConnectionStateManager.isInternetConnected() &&
                !isRegisteringDevice &&
                !mSessionManager.isNextivaConnectEnabled() &&
                (TextUtils.isEmpty(mSharedPreferencesManager.getString(SharedPreferencesManager.UMS_UDID, "")) || !mConnectionStateManager.isUMSConnected())) {

            registerDevice().subscribe(new DisposableSingleObserver<RegisterDeviceResponseEvent>() {
                @Override
                public void onSuccess(RegisterDeviceResponseEvent registerDeviceResponseEvent) {
                    mConnectionStateManager.postIsUMSConnected(true);
                    setDeviceFinishedRegistering();
                }

                @Override
                public void onError(Throwable e) {
                    mConnectionStateManager.postIsUMSConnected(false);
                    setDeviceFinishedRegistering();
                }
            });
        }
    }

    // --------------------------------------------------------------------------------------------
    // UmsRepository Methods
    // --------------------------------------------------------------------------------------------

    private void setupUmsUdid() {
        if (TextUtils.isEmpty(mSharedPreferencesManager.getString(SharedPreferencesManager.UMS_UDID, ""))) {
            mSharedPreferencesManager.setString(SharedPreferencesManager.UMS_UDID, UUID.randomUUID().toString().replace("-", ""));
        }
    }

    @Override
    public boolean isClientSetup() {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (!mUmsHostInterceptor.isHostSetup() && !TextUtils.isEmpty(mSessionManager.getUmsHost())) {
            mUmsHostInterceptor.setHost(mSessionManager.getUmsHost());
        }

        return mUmsHostInterceptor.isHostSetup();
    }

    @Override
    public void setDeviceFinishedRegistering() {
        isRegisteringDevice = false;
    }

    @Override
    public Single<RegisterDeviceResponseEvent> registerDevice() {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        isRegisteringDevice = true;

        if (!mConnectionStateManager.isInternetConnected() || !isClientSetup() || mConfigManager.getMobileConfig() != null
                && mConfigManager.getMobileConfig().getXmpp() != null && TextUtils.isEmpty(mConfigManager.getMobileConfig().getXmpp().getUsername()) || mSessionManager.getUserDetails() == null || mPushNotificationManager.getToken() != null && TextUtils.isEmpty(mPushNotificationManager.getToken())) {

            mLogManager.logToFile(Enums.Logging.STATE_ERROR, R.string.log_message_no_host_or_username);
            mSessionManager.setPushNotificationRegistrationId(null);
            isRegisteringDevice = false;
            return Single.just(new RegisterDeviceResponseEvent(false));

        } else {
            setupUmsUdid();

            BroadsoftDeviceRegistrationDetails registrationDetails = new BroadsoftDeviceRegistrationDetails(
                    mApplication.getPackageName(),
                    "Android",
                    String.valueOf(Build.VERSION.SDK_INT),
                    BuildConfig.VERSION_NAME,
                    mPushNotificationManager.getToken());

            BroadsoftDeviceRegistrationBody body = new BroadsoftDeviceRegistrationBody(registrationDetails);
            if (mConfigManager.getMobileConfig() != null &&
                    mConfigManager.getMobileConfig().getXmpp() != null) {
                return mBroadsoftUmsApi.putRegisterDevice(
                        getAppVersionHeader(),
                        mConfigManager.getMobileConfig().getXmpp().getXmppAuthorizationHeader(),
                        mConfigManager.getMobileConfig().getXmpp().getUsername(),
                        mSharedPreferencesManager.getString(SharedPreferencesManager.UMS_UDID, ""),
                        body)
                        .subscribeOn(mSchedulerProvider.io())
                        .map(response -> {
                            isRegisteringDevice = false;

                            if (response.isSuccessful()) {
                                mConnectionStateManager.postIsUMSConnected(true);
                                logServerSuccess(response);
                                return new RegisterDeviceResponseEvent(true);

                            } else {
                                mConnectionStateManager.postIsUMSConnected(false);

                                try {
                                    throw new HttpException(response);
                                } catch (HttpException exception) {
                                    logHttpException(exception);
                                    return new RegisterDeviceResponseEvent(false);
                                }
                            }
                        })
                        .onErrorReturn(throwable -> {
                            mConnectionStateManager.postIsUMSConnected(false);
                            logServerResponseError(throwable);
                            return new RegisterDeviceResponseEvent(false);
                        })
                        .observeOn(mSchedulerProvider.ui());
            } else {
                isRegisteringDevice = false;
                return Single.just(new RegisterDeviceResponseEvent(false));
            }
        }
    }

    @Override
    public Single<UnregisterDeviceResponseEvent> unregisterDevice() {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (TextUtils.isEmpty(mSharedPreferencesManager.getString(SharedPreferencesManager.UMS_UDID, ""))) {
            mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_no_ums_udid);
            setHost(null);
            mConnectionStateManager.postIsUMSConnected(false);

            return Single.just(new UnregisterDeviceResponseEvent(true));

        } else if (!isClientSetup() || (mConfigManager.getMobileConfig() != null && mConfigManager.getMobileConfig().getXmpp() != null && TextUtils.isEmpty(mConfigManager.getMobileConfig().getXmpp().getUsername()))) {
            mLogManager.logToFile(Enums.Logging.STATE_ERROR, R.string.log_message_no_host_or_username);

            setHost(null);
            mConnectionStateManager.postIsUMSConnected(false);

            return Single.just(new UnregisterDeviceResponseEvent(false));

        } else {
            String xmppAuthorizationHeader = "";
            String xmppUsername = "";
            if (mConfigManager.getMobileConfig() != null && mConfigManager.getMobileConfig().getXmpp() != null) {
                xmppAuthorizationHeader = mConfigManager.getMobileConfig().getXmpp().getXmppAuthorizationHeader();
                xmppUsername = mConfigManager.getMobileConfig().getXmpp().getUsername();
            }

            return mBroadsoftUmsApi.deleteUnregisterDevice(
                    getAppVersionHeader(),
                    xmppAuthorizationHeader,
                    xmppUsername,
                    mSharedPreferencesManager.getString(SharedPreferencesManager.UMS_UDID, ""))
                    .subscribeOn(mSchedulerProvider.io())
                    .map(response -> {
                        if (response.isSuccessful()) {
                            logServerSuccess(response);
                            mConnectionStateManager.postIsUMSConnected(false);
                            mUserRepository.unregisterForPushNotifications().subscribe();
                            setHost(null);

                            return new UnregisterDeviceResponseEvent(true);

                        } else {
                            mConnectionStateManager.postIsUMSConnected(false);

                            try {
                                throw new HttpException(response);
                            } catch (HttpException exception) {
                                logHttpException(exception);
                                return new UnregisterDeviceResponseEvent(false);
                            }
                        }
                    })
                    .onErrorReturn(throwable -> {
                        logServerResponseError(throwable);
                        setHost(null);
                        mConnectionStateManager.postIsUMSConnected(false);
                        return new UnregisterDeviceResponseEvent(false);
                    })
                    .observeOn(mSchedulerProvider.ui());
        }
    }

    @Override
    public Single<ChatConversationsResponseEvent> getChatConversations(final long timeStamp) {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        reconnectToUmsIfNecessary();

        if (!isClientSetup() || TextUtils.isEmpty(mSharedPreferencesManager.getString(SharedPreferencesManager.UMS_UDID, ""))) {
            mLogManager.logToFile(Enums.Logging.STATE_ERROR, R.string.log_message_no_host_or_username);
            return Single.just(new ChatConversationsResponseEvent(false, null));

        } else {
            final AtomicReference<Long> timestampReference = new AtomicReference<>(timeStamp);
            HashMap<String, ChatConversation> conversationsMap = new HashMap<>();

            String xmppAuthorizationHeader = "";
            if (mConfigManager.getMobileConfig() != null && mConfigManager.getMobileConfig().getXmpp() != null) {
                xmppAuthorizationHeader = mConfigManager.getMobileConfig().getXmpp().getXmppAuthorizationHeader();
            }

            return mBroadsoftUmsApi.getChatMessageHistory(
                    getAppVersionHeader(),
                    xmppAuthorizationHeader,
                    mSharedPreferencesManager.getString(SharedPreferencesManager.UMS_UDID, ""),
                    timestampReference.get())
                    .subscribeOn(mSchedulerProvider.io())
                    .doOnSuccess(response -> {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                logServerSuccess(response);

                                BroadsoftUmsChatMessagesResponse responseBody = response.body();
                                BroadsoftUtil.processChatConversations(responseBody, conversationsMap);

                                if (responseBody.getChatMessages() != null && responseBody.getChatMessages().length == 100) {
                                    long nextTimeStamp = timeStamp + 1;
                                    BroadsoftUmsChatMessage umsChatMessage = responseBody.getChatMessages()[responseBody.getChatMessages().length - 1];

                                    if (umsChatMessage != null && umsChatMessage.getTimestamp() != null) {
                                        nextTimeStamp = umsChatMessage.getTimestamp() + 1;
                                    }

                                    timestampReference.set(nextTimeStamp);

                                } else {
                                    timestampReference.set(null);
                                }

                            } else {
                                logServerParseFailure(response);
                                timestampReference.set(null);
                            }

                        } else {
                            throw new HttpException(response);
                        }
                    })
                    .repeatWhen(objectFlowable -> objectFlowable.takeWhile(o -> timestampReference.get() != null))
                    .map(response -> {
                        ArrayList<ChatConversation> chatConversationsList = new ArrayList<>();

                        for (String key : conversationsMap.keySet()) {
                            ChatConversation conversation = conversationsMap.get(key);
                            chatConversationsList.add(conversation);
                        }

                        ChatUtil.sortConversations(chatConversationsList);

                        return new ChatConversationsResponseEvent(true, chatConversationsList);
                    })
                    .firstOrError()
                    .onErrorReturn(throwable -> {
                        logServerResponseError(throwable);
                        return new ChatConversationsResponseEvent(false, null);
                    })
                    .observeOn(mSchedulerProvider.ui());
        }
    }

    @Override
    public Single<Boolean> markMessagesReadFromSender(@NonNull final CompositeDisposable compositeDisposable,
                                                      @NonNull String jid) {
        reconnectToUmsIfNecessary();

        if (mBroadsoftUmsApi == null) {
            return Single.just(false);
        }

        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        String xmppAuthHeader = "";

        if (mConfigManager.getMobileConfig() != null && mConfigManager.getMobileConfig().getXmpp() != null) {
            xmppAuthHeader = mConfigManager.getMobileConfig().getXmpp().getXmppAuthorizationHeader();
        }

        ArrayList<String> unreadMessageIds = new ArrayList<>(mDbManager.getUnreadChatMessageIdsFromChatWith(jid));
        ArrayList<BroadsoftMessageId> broadsoftMessageIds = new ArrayList<>();

        for (String messageId : unreadMessageIds) {
            broadsoftMessageIds.add(new BroadsoftMessageId(messageId));
        }

        if (broadsoftMessageIds.size() > 0) {
            BroadsoftMarkMessagesReadBody broadsoftMarkMessagesReadBody = new BroadsoftMarkMessagesReadBody(null, broadsoftMessageIds);

            return mBroadsoftUmsApi.markAllMessagesRead(
                    getAppVersionHeader(),
                    xmppAuthHeader,
                    mSharedPreferencesManager.getString(SharedPreferencesManager.UMS_UDID, ""),
                    broadsoftMarkMessagesReadBody)
                    .subscribeOn(mSchedulerProvider.io())
                    .map(response -> {
                        if (response.body() != null && TextUtils.equals(response.body().getStatus().getType(), "success")) {
                            mDbManager.markMessagesFromSenderRead(jid);
                            mNotificationManager.cancelChatNotificationsFromMessageIds(unreadMessageIds);
                            return true;
                        }

                        return false;

                    }).onErrorReturn(throwable -> {
                        logServerResponseError(throwable);
                        return false;

                    }).observeOn(mSchedulerProvider.ui());

        } else {
            return Single.just(true);
        }
    }

    @Override
    public Single<Boolean> markAllMessagesRead(@NonNull final CompositeDisposable compositeDisposable) {
        reconnectToUmsIfNecessary();

        if (mBroadsoftUmsApi == null) {
            return Single.just(false);
        }

        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        String xmppAuthHeader = "";

        if (mConfigManager.getMobileConfig() != null && mConfigManager.getMobileConfig().getXmpp() != null) {
            xmppAuthHeader = mConfigManager.getMobileConfig().getXmpp().getXmppAuthorizationHeader();
        }

        ArrayList<BroadsoftMessageId> messageIds = new ArrayList<>();
        messageIds.add(new BroadsoftMessageId("0"));
        BroadsoftMarkMessagesReadBody broadsoftMarkAllReadBody = new BroadsoftMarkMessagesReadBody("all", messageIds);

        return mBroadsoftUmsApi.markAllMessagesRead(
                getAppVersionHeader(),
                xmppAuthHeader,
                mSharedPreferencesManager.getString(SharedPreferencesManager.UMS_UDID, ""),
                broadsoftMarkAllReadBody)
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    if (response.body() != null && TextUtils.equals(response.body().getStatus().getType(), "success")) {
                        mDbManager.markAllMessagesRead();
                        mNotificationManager.cancelNotification(Enums.Notification.TypeIDs.NEW_CHAT_MESSAGE_NOTIFICATION_ID);
                        return true;
                    } else {
                        return false;
                    }

                }).onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return false;

                }).observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<Boolean> getVCards(
            @NonNull final ArrayList<NextivaContact> nextivaContacts,
            @NonNull final CompositeDisposable compositeDisposable) {

        reconnectToUmsIfNecessary();

        if (mBroadsoftUmsApi == null) {
            return Single.just(false);
        }

        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        final ArrayList<String> jidList = new ArrayList<>();

        for (NextivaContact nextivaContact : nextivaContacts) {
            if (!TextUtils.isEmpty(nextivaContact.getJid())) {
                jidList.add(nextivaContact.getJid());
            }
        }

        if (mSessionManager.getUserDetails() != null && !TextUtils.isEmpty(mSessionManager.getUserDetails().getImpId())) {
            jidList.add(mSessionManager.getUserDetails().getImpId());
        }

        BroadsoftVCardBody vCardBody = new BroadsoftVCardBody(jidList);

        String xmppAuthHeader = "";
        if (mConfigManager.getMobileConfig() != null && mConfigManager.getMobileConfig().getXmpp() != null) {
            xmppAuthHeader = mConfigManager.getMobileConfig().getXmpp().getXmppAuthorizationHeader();
        }


        return mBroadsoftUmsApi.getVCards(
                getAppVersionHeader(),
                xmppAuthHeader,
                vCardBody)
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    if (response.isSuccessful()) {
                        if (response.body() != null && response.body().getBroadsoftVCards() != null) {
                            logServerSuccess(response);

                            ArrayList<BroadsoftVCard> vCards = response.body().getBroadsoftVCards();
                            ArrayList<DbVCard> nextivaContactVCards = new ArrayList<>();

                            for (BroadsoftVCard vCard : vCards) {
                                NextivaVCard nextivaVCard = XmlUtil.toNextivaVCard(vCard);
                                NextivaVCardPhoto nextivaVCardPhoto = null;

                                if (nextivaVCard != null && nextivaVCard.getPhoto() != null) {
                                    nextivaVCardPhoto = nextivaVCard.getPhoto();
                                }

                                String binval = null;
                                if (nextivaVCardPhoto != null) {
                                    binval = nextivaVCardPhoto.getBinVal();
                                }

                                nextivaContactVCards.add(new DbVCard(vCard.getJid(), mAvatarManager.stringToByteArray(binval)));
                            }

                            mDbManager.updateAllVCards(compositeDisposable, nextivaContactVCards);

                        } else {
                            logServerParseFailure(response);
                        }

                        return true;

                    } else {
                        try {
                            throw new HttpException(response);
                        } catch (HttpException exception) {
                            logHttpException(exception);
                            return false;
                        }
                    }
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return false;
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<Boolean> setVCard(byte[] vcardData) {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        reconnectToUmsIfNecessary();

        if (mSessionManager.getUserDetails() == null || TextUtils.isEmpty(mSessionManager.getUserDetails().getImpId())) {
            return Single.never();
        }

        String xmppAuthHeader = "";
        if (mConfigManager.getMobileConfig() != null && mConfigManager.getMobileConfig().getXmpp() != null) {
            xmppAuthHeader = mConfigManager.getMobileConfig().getXmpp().getXmppAuthorizationHeader();
        }

        NextivaVCard nextivaVCard = new NextivaVCard(mSharedPreferencesManager.getString(SharedPreferencesManager.UMS_UDID, ""),
                                                     new NextivaVCardPhoto(NextivaXMPPConstants.VCARD_MIME_TYPE,
                                                                           mAvatarManager.isByteArrayNotEmpty(vcardData) && !TextUtils.isEmpty(mAvatarManager.byteArrayToString(vcardData))
                                                                                   ? Objects.requireNonNull(mAvatarManager.byteArrayToString(vcardData)).replaceAll("\n", "") : ""));

        return mBroadsoftUmsApi.setVCard(getAppVersionHeader(),
                                         xmppAuthHeader,
                                         mSessionManager.getUserDetails().getImpId(),
                                         new BroadsoftSetVCardBody(XmlUtil.deserializeNextivaVCard(nextivaVCard), mSharedPreferencesManager.getString(SharedPreferencesManager.UMS_UDID, "")))
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    if (response.isSuccessful()) {
                        return true;

                    } else {
                        try {
                            throw new HttpException(response);
                        } catch (HttpException exception) {
                            logHttpException(exception);
                            return false;
                        }
                    }
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return false;
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<Boolean> getVCard(
            @NonNull final String jid,
            @NonNull final CompositeDisposable compositeDisposable) {

        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        reconnectToUmsIfNecessary();

        ArrayList<String> jidList = new ArrayList<>();
        jidList.add(jid);

        BroadsoftVCardBody vCardBody = new BroadsoftVCardBody(jidList);
        String xmppAuthHeader = "";
        if (mConfigManager.getMobileConfig() != null && mConfigManager.getMobileConfig().getXmpp() != null) {
            xmppAuthHeader = mConfigManager.getMobileConfig().getXmpp().getXmppAuthorizationHeader();
        }

        return mBroadsoftUmsApi.getVCards(
                getAppVersionHeader(),
                xmppAuthHeader,
                vCardBody)
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    if (response.isSuccessful()) {
                        if (response.body() != null && response.body().getBroadsoftVCards() != null) {
                            logServerSuccess(response);

                            ArrayList<BroadsoftVCard> vCards = response.body().getBroadsoftVCards();
                            ArrayList<DbVCard> nextivaContactVCards = new ArrayList<>();

                            for (BroadsoftVCard vCard : vCards) {
                                NextivaVCard nextivaVCard = XmlUtil.toNextivaVCard(vCard);
                                NextivaVCardPhoto nextivaVCardPhoto = null;
                                if (nextivaVCard.getPhoto() != null) {
                                    nextivaVCardPhoto = nextivaVCard.getPhoto();
                                }
                                String binval = null;
                                if (nextivaVCardPhoto != null) {
                                    binval = nextivaVCardPhoto.getBinVal();
                                }

                                nextivaContactVCards.add(new DbVCard(vCard.getJid(), mAvatarManager.stringToByteArray(binval)));
                            }

                            mDbManager.updateAllVCards(compositeDisposable, nextivaContactVCards);

                        } else {
                            logServerParseFailure(response);
                        }

                        return true;

                    } else {
                        try {
                            throw new HttpException(response);
                        } catch (HttpException exception) {
                            logHttpException(exception);
                            return false;
                        }
                    }
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return false;
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<ArrayList<NextivaContact>> getContactStorage() {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        reconnectToUmsIfNecessary();

        String xmppAuthHeader = "";
        if (mConfigManager.getMobileConfig() != null && mConfigManager.getMobileConfig().getXmpp() != null) {
            xmppAuthHeader = mConfigManager.getMobileConfig().getXmpp().getXmppAuthorizationHeader();
        }

        return mBroadsoftUmsApi.getContactStorage(
                getAppVersionHeader(),
                xmppAuthHeader,
                "0")
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    ArrayList<NextivaContact> nextivaContacts = new ArrayList<>();

                    if (response.isSuccessful() && response.body() != null) {
                        BroadsoftContactStorage contactStorage = XmlUtil.toBroadsoftContactStorage(response.body().getContactStorage());

                        if (contactStorage != null && contactStorage.getAddressbook() != null && contactStorage.getAddressbook().getContacts() != null) {
                            if (contactStorage.getAddressbook().getGroups() != null) {
                                ArrayList<DbGroup> dbGroups = new ArrayList<>();

                                for (BroadsoftAddressbookGroup group : contactStorage.getAddressbook().getGroups()) {
                                    if (group.getPosition() != null) {
                                        dbGroups.add(new DbGroup(null, group.getId(), group.getDisplayName(), group.getPosition(), null));
                                    }
                                }

                                mDbManager.insertGroups(dbGroups);
                            }

                            for (BroadsoftAddressbookContact contact : contactStorage.getAddressbook().getContacts()) {
                                nextivaContacts.add(contact.toNextivaContact(contactStorage.getAddressbook().getFavorites(), contactStorage.getAddressbook().getGroups()));
                            }
                        }
                    }

                    return nextivaContacts;
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return new ArrayList<>();
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<Boolean> setContactStorageWithGroups(ArrayList<DbGroup> groups) {
        return setContactStorage(mDbManager.getRosterContactsInThread(), groups);
    }

    @Override
    public Single<Boolean> setContactStorageWithContacts(ArrayList<NextivaContact> contacts) {
        return setContactStorage(contacts, mDbManager.getGroups());
    }

    @Override
    public Single<Boolean> setContactStorage(ArrayList<NextivaContact> contacts, ArrayList<DbGroup> groups) {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        reconnectToUmsIfNecessary();

        if (mSessionManager.getUserDetails() == null || TextUtils.isEmpty(mSessionManager.getUserDetails().getImpId())) {
            return Single.just(false);
        }

        String xmppAuthHeader = "";
        if (mConfigManager.getMobileConfig() != null && mConfigManager.getMobileConfig().getXmpp() != null) {
            xmppAuthHeader = mConfigManager.getMobileConfig().getXmpp().getXmppAuthorizationHeader();
        }

        return mBroadsoftUmsApi.setContactStorage(
                getAppVersionHeader(),
                xmppAuthHeader,
                mSessionManager.getUserDetails().getImpId(),
                new BroadsoftContactStorageSetBody(XmlUtil.deserializeContactStorageTimestamp(mSharedPreferencesManager.getString(SharedPreferencesManager.UMS_UDID, ""), mCalendarManager),
                                                   XmlUtil.deserializeContactStorage(contacts, groups, mCalendarManager, mSharedPreferencesManager.getString(SharedPreferencesManager.UMS_UDID, ""))))
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    if (response.isSuccessful()) {
                        logServerSuccess(response);
                        return true;

                    } else {
                        try {
                            throw new HttpException(response);
                        } catch (HttpException exception) {
                            logHttpException(exception);
                            return false;
                        }
                    }
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return false;
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<ArrayList<DbPresence>> getOnDemandPresences(ArrayList<String> jids) {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        reconnectToUmsIfNecessary();

        String xmppAuthHeader = "";
        if (mConfigManager.getMobileConfig() != null && mConfigManager.getMobileConfig().getXmpp() != null) {
            xmppAuthHeader = mConfigManager.getMobileConfig().getXmpp().getXmppAuthorizationHeader();
        }

        return mBroadsoftUmsApi.getOnDemandPresences(
                getAppVersionHeader(),
                xmppAuthHeader,
                new BroadsoftOnDemandPresencePostBody(jids))
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    if (response.isSuccessful()) {
                        logServerSuccess(response);
                        ArrayList<DbPresence> dbPresences = new ArrayList<>();

                        if (response.body() != null && response.body().getPresences() != null) {
                            for (BroadsoftOnDemandPresence presence : response.body().getPresences()) {
                                dbPresences.add(presence.toDbPresence());
                            }
                        }

                        return dbPresences;

                    } else {
                        try {
                            throw new HttpException(response);
                        } catch (HttpException exception) {
                            logHttpException(exception);
                            return null;
                        }
                    }
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return new ArrayList<>();
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<Boolean> getSuperPresence(CompositeDisposable compositeDisposable) {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        reconnectToUmsIfNecessary();

        if (mSessionManager.getUserDetails() == null ||
                mSessionManager.getUserDetails() != null && TextUtils.isEmpty(mSessionManager.getUserDetails().getImpId()) ||
                TextUtils.isEmpty(mSharedPreferencesManager.getString(SharedPreferencesManager.UMS_UDID, ""))) {
            return Single.just(false);
        }

        String xmppAuthHeader = "";
        if (mConfigManager.getMobileConfig() != null && mConfigManager.getMobileConfig().getXmpp() != null) {
            xmppAuthHeader = mConfigManager.getMobileConfig().getXmpp().getXmppAuthorizationHeader();
        }

        return mBroadsoftUmsApi.getSuperPresence(
                getAppVersionHeader(),
                xmppAuthHeader,
                mSharedPreferencesManager.getString(SharedPreferencesManager.UMS_UDID, ""))
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    if (response.isSuccessful() &&
                            response.body() != null &&
                            response.body().getSuperPresence() != null) {
                        BroadsoftSuperPresenceResponse broadsoftSuperPresenceResponse = response.body();
                        compositeDisposable.add(
                                getOnDemandPresences(new ArrayList<String>() {{
                                    if (mSessionManager.getUserDetails() != null
                                            && mSessionManager.getUserDetails().getImpId() != null) {
                                        add(mSessionManager.getUserDetails().getImpId());
                                    }
                                }})
                                        .subscribe(presences -> {
                                            if (presences != null &&
                                                    presences.size() > 0 &&
                                                    presences.get(0) != null &&
                                                    mSessionManager.getUserDetails() != null &&
                                                    mSessionManager.getUserDetails().getImpId() != null) {
                                                DbPresence dbPresence = presences.get(0);
                                                UserDetails userDetails = mSessionManager.getUserDetails();
                                                BroadsoftSuperPresence broadsoftSuperPresence = broadsoftSuperPresenceResponse.getSuperPresence();

                                                if (dbPresence.getJid() != null &&
                                                        !TextUtils.isEmpty(dbPresence.getJid()) &&
                                                        userDetails != null &&
                                                        userDetails.getImpId() != null &&
                                                        !TextUtils.isEmpty(userDetails.getImpId()) &&
                                                        TextUtils.equals(dbPresence.getJid().toLowerCase(),
                                                                         userDetails.getImpId().toLowerCase())) {
                                                    if (broadsoftSuperPresence != null) {
                                                        if (broadsoftSuperPresence.isEmpty() &&
                                                                broadsoftSuperPresence.toDbPresence() != null) {
                                                            mSessionManager.setUserPresence(presences.get(0), true);

                                                        } else {
                                                            DbPresence superPresence = broadsoftSuperPresence.toDbPresence();
                                                            DbPresence presence = presences.get(0);

                                                            if (superPresence != null) {
                                                                presence.setPriority(superPresence.getPriority() != -128 ? superPresence.getPriority() : presence.getPriority());
                                                                presence.setState(superPresence.getState() != Enums.Contacts.PresenceStates.NONE ? superPresence.getState() : presence.getState());
                                                                presence.setStatus(!TextUtils.isEmpty(superPresence.getStatus()) ? superPresence.getStatus() : presence.getStatus());
                                                            }

                                                            mSessionManager.setUserPresence(presence, superPresence == null || superPresence.getState() == Enums.Contacts.PresenceStates.NONE);
                                                        }
                                                    }
                                                }

                                            }
                                        }));

                        return true;

                    } else {
                        try {
                            throw new HttpException(response);
                        } catch (HttpException exception) {
                            logHttpException(exception);
                            return false;
                        }
                    }
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return false;
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<Boolean> sendPresenceAvailability(DbPresence dbPresence, CompositeDisposable compositeDisposable) {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        reconnectToUmsIfNecessary();

        if (mSessionManager.getUserDetails() == null || TextUtils.isEmpty(mSessionManager.getUserDetails().getImpId()) || TextUtils.isEmpty(mSharedPreferencesManager.getString(SharedPreferencesManager.UMS_UDID, ""))) {
            return Single.just(false);
        }

        String xmppAuthHeader = "";
        if (mConfigManager.getMobileConfig() != null && mConfigManager.getMobileConfig().getXmpp() != null) {
            xmppAuthHeader = mConfigManager.getMobileConfig().getXmpp().getXmppAuthorizationHeader();
        }

        return mBroadsoftUmsApi.setPresenceAvailability(
                getAppVersionHeader(),
                xmppAuthHeader,
                mSharedPreferencesManager.getString(SharedPreferencesManager.UMS_UDID, ""),
                new BroadsoftSuperPresenceShowPostBody(dbPresence.getAvailability(), dbPresence.getPriority()))
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    if (response.isSuccessful()) {
                        getSuperPresence(compositeDisposable).subscribe();
                        return true;

                    } else {
                        try {
                            throw new HttpException(response);
                        } catch (HttpException exception) {
                            logHttpException(exception);
                            return false;
                        }
                    }
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return false;
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<Boolean> deletePresenceAvailability(CompositeDisposable compositeDisposable) {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        reconnectToUmsIfNecessary();

        if (mSessionManager.getUserDetails() == null || TextUtils.isEmpty(mSessionManager.getUserDetails().getImpId()) || TextUtils.isEmpty(mSharedPreferencesManager.getString(SharedPreferencesManager.UMS_UDID, ""))) {
            return Single.just(false);
        }

        String xmppAuthHeader = "";
        if (mConfigManager.getMobileConfig() != null && mConfigManager.getMobileConfig().getXmpp() != null) {
            xmppAuthHeader = mConfigManager.getMobileConfig().getXmpp().getXmppAuthorizationHeader();
        }

        return mBroadsoftUmsApi.deletePresenceAvailability(
                getAppVersionHeader(),
                xmppAuthHeader,
                mSharedPreferencesManager.getString(SharedPreferencesManager.UMS_UDID, ""))
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    if (response.isSuccessful()) {
                        getSuperPresence(compositeDisposable).subscribe();
                        return true;

                    } else {
                        try {
                            throw new HttpException(response);
                        } catch (HttpException exception) {
                            logHttpException(exception);
                            return false;
                        }
                    }
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return false;
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<Boolean> sendPresenceStatus(String status, CompositeDisposable compositeDisposable) {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        reconnectToUmsIfNecessary();

        if (mSessionManager.getUserDetails() == null || TextUtils.isEmpty(mSessionManager.getUserDetails().getImpId()) || TextUtils.isEmpty(mSharedPreferencesManager.getString(SharedPreferencesManager.UMS_UDID, ""))) {
            return Single.just(false);
        }

        String xmppAuthHeader = "";
        if (mConfigManager.getMobileConfig() != null && mConfigManager.getMobileConfig().getXmpp() != null) {
            xmppAuthHeader = mConfigManager.getMobileConfig().getXmpp().getXmppAuthorizationHeader();
        }

        return mBroadsoftUmsApi.setPresenceStatusText(
                getAppVersionHeader(),
                xmppAuthHeader,
                mSharedPreferencesManager.getString(SharedPreferencesManager.UMS_UDID, ""),
                new BroadsoftSuperPresenceStatusPostBody(status))
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    if (response.isSuccessful()) {
                        getSuperPresence(compositeDisposable).subscribe();
                        return true;

                    } else {
                        try {
                            throw new HttpException(response);
                        } catch (HttpException exception) {
                            logHttpException(exception);
                            return false;
                        }
                    }
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return false;
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<Boolean> deletePresenceStatus(CompositeDisposable compositeDisposable) {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);
        reconnectToUmsIfNecessary();

        if (mSessionManager.getUserDetails() == null || TextUtils.isEmpty(mSessionManager.getUserDetails().getImpId()) || TextUtils.isEmpty(mSharedPreferencesManager.getString(SharedPreferencesManager.UMS_UDID, ""))) {
            return Single.just(false);
        }

        String xmppAuthHeader = "";
        if (mConfigManager.getMobileConfig() != null && mConfigManager.getMobileConfig().getXmpp() != null) {
            xmppAuthHeader = mConfigManager.getMobileConfig().getXmpp().getXmppAuthorizationHeader();
        }

        return mBroadsoftUmsApi.deletePresenceStatusText(
                getAppVersionHeader(),
                xmppAuthHeader,
                mSharedPreferencesManager.getString(SharedPreferencesManager.UMS_UDID, ""))
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    if (response.isSuccessful()) {
                        getSuperPresence(compositeDisposable).subscribe();
                        return true;

                    } else {
                        try {
                            throw new HttpException(response);
                        } catch (HttpException exception) {
                            logHttpException(exception);
                            return false;
                        }
                    }
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return false;
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public void resendChatMessage(ChatMessage chatMessage) {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mSessionManager.getUserDetails() == null || TextUtils.isEmpty(mSessionManager.getUserDetails().getImpId()) || TextUtils.isEmpty(mSharedPreferencesManager.getString(SharedPreferencesManager.UMS_UDID, ""))) {
            mApplication.getString(R.string.log_message_failure_send_chat_info,
                                   chatMessage,
                                   "",
                                   TextUtils.isEmpty(chatMessage.getTo()) ? GsonUtil.getJSON(chatMessage.getMembersList()) : chatMessage.getTo(),
                                   String.valueOf(System.currentTimeMillis()),
                                   mApplication.getString(R.string.log_message_failure_send_chat_null_info));
            return;
        }

        String xmppAuthHeader = "";
        if (mConfigManager.getMobileConfig() != null && mConfigManager.getMobileConfig().getXmpp() != null) {
            xmppAuthHeader = mConfigManager.getMobileConfig().getXmpp().getXmppAuthorizationHeader();
        }

        ArrayList<BroadsoftUmsJid> memberList = null;

        if (chatMessage.getMembersList() != null) {
            memberList = new ArrayList<>();

            for (String memberJid : chatMessage.getMembersList()) {
                memberList.add(new BroadsoftUmsJid(memberJid));
            }
        }

        mDbManager.updateMessageSentStatus(chatMessage.getMessageId(), Enums.Chats.SentStatus.PENDING);

        Single<String> sendChatMessage = mBroadsoftUmsApi.sendChatMessage(
                getAppVersionHeader(),
                xmppAuthHeader,
                mSharedPreferencesManager.getString(SharedPreferencesManager.UMS_UDID, ""),
                new BroadsoftChatMessageBody(
                        new BroadsoftChatMessageDetails(!TextUtils.equals(chatMessage.getType(), Enums.Chats.ConversationTypes.GROUP_ALIAS) ? chatMessage.getTo() : null,
                                mSessionManager.getUserDetails().getImpId() + "/" + mSharedPreferencesManager.getString(SharedPreferencesManager.UMS_UDID, ""),
                                chatMessage.getType(),
                                "en",
                                chatMessage.getBody(),
                                chatMessage.getThreadId(),
                                memberList)))
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    if (response.isSuccessful()) {
                        logServerSuccess(response);

                        if (response.body() != null) {
                            mDbManager.updateTempMessageId(chatMessage.getMessageId(), response.body().getMsgId());
                        } else {
                            mDbManager.deleteMessageFromMessageId(chatMessage.getMessageId());
                        }

                        return response.body() != null ? response.body().getMsgId() : null;

                    } else {
                        mDbManager.updateMessageSentStatus(chatMessage.getMessageId(), Enums.Chats.SentStatus.FAILED);

                        try {
                            throw new HttpException(response);
                        } catch (HttpException exception) {
                            mLogManager.logToFile(Enums.Logging.STATE_ERROR,
                                                  mApplication.getString(R.string.log_message_failure_send_chat_info,
                                                                         chatMessage,
                                                                         mSessionManager.getUserDetails().getImpId(),
                                                                         TextUtils.isEmpty(chatMessage.getTo()) ? GsonUtil.getJSON(chatMessage.getMembersList()) : chatMessage.getTo(),
                                                                         String.valueOf(System.currentTimeMillis()),
                                                                         mApplication.getString(R.string.log_message_failure_send_chat_server_error)));

                            logHttpException(exception);
                            return "";
                        }
                    }
                })
                .onErrorReturn(throwable -> {
                    mDbManager.updateMessageSentStatus(chatMessage.getMessageId(), Enums.Chats.SentStatus.FAILED);

                    mLogManager.logToFile(Enums.Logging.STATE_ERROR,
                                          mApplication.getString(R.string.log_message_failure_send_chat_info,
                                                                 chatMessage,
                                                                 mSessionManager.getUserDetails().getImpId(),
                                                                 TextUtils.isEmpty(chatMessage.getTo()) ? GsonUtil.getJSON(chatMessage.getMembersList()) : chatMessage.getTo(),
                                                                 String.valueOf(System.currentTimeMillis()),
                                                                 mApplication.getString(R.string.log_message_failure_send_chat_server_error)));
                    logServerResponseError(throwable);
                    return "";
                })
                .observeOn(mSchedulerProvider.ui());

        if (mConnectionStateManager.isUMSConnected()) {
            sendChatMessage.subscribe();

        } else {
            reconnectToUmsIfNecessary();
            mPendingChatMessages.add(new PendingChatMessage(sendChatMessage, chatMessage));
        }
    }

    @Override
    public void sendChatMessage(String jid, String chatMessage, @Enums.Chats.ConversationTypes.Type String chatType, String threadId, ArrayList<String> jidList) {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mSessionManager.getUserDetails() == null || TextUtils.isEmpty(mSessionManager.getUserDetails().getImpId()) || TextUtils.isEmpty(mSharedPreferencesManager.getString(SharedPreferencesManager.UMS_UDID, ""))) {
            mLogManager.logToFile(Enums.Logging.STATE_ERROR,
                                  mApplication.getString(R.string.log_message_failure_send_chat_info,
                                                         chatMessage,
                                                         "",
                                                         TextUtils.isEmpty(jid) ? GsonUtil.getJSON(jidList) : jid,
                                                         String.valueOf(System.currentTimeMillis()),
                                                         mApplication.getString(R.string.log_message_failure_send_chat_null_info)));
            return;
        }

        String xmppAuthHeader = "";
        if (mConfigManager.getMobileConfig() != null && mConfigManager.getMobileConfig().getXmpp() != null) {
            xmppAuthHeader = mConfigManager.getMobileConfig().getXmpp().getXmppAuthorizationHeader();
        }

        ArrayList<BroadsoftUmsJid> memberList = null;

        if (jidList != null) {
            memberList = new ArrayList<>();

            for (String memberJid : jidList) {
                memberList.add(new BroadsoftUmsJid(memberJid));
            }
        }

        ChatMessage messageToSave = new ChatMessage();
        messageToSave.setFrom(mSessionManager.getUserDetails().getImpId());
        messageToSave.setTo(jid);
        messageToSave.setTimestamp(System.currentTimeMillis());
        messageToSave.setChatWith(TextUtils.equals(chatType, Enums.Chats.ConversationTypes.GROUP_ALIAS) ? threadId : jid);
        messageToSave.setIsSender(true);
        messageToSave.setType(chatType);
        messageToSave.setThreadId(threadId);
        messageToSave.setBody(chatMessage);
        messageToSave.setSentStatus(Enums.Chats.SentStatus.PENDING);
        messageToSave.setMembersString(jidList != null && jidList.size() > 0 ? GsonUtil.getJSON(jidList) : null);
        messageToSave.setIsRead(true);

        String tempMessageId = UUID.randomUUID().toString();
        messageToSave.setMessageId(tempMessageId);

        mDbManager.saveChatMessage(messageToSave);

        Single<String> sendChatMessage = mBroadsoftUmsApi.sendChatMessage(
                getAppVersionHeader(),
                xmppAuthHeader,
                mSharedPreferencesManager.getString(SharedPreferencesManager.UMS_UDID, ""),
                new BroadsoftChatMessageBody(
                        new BroadsoftChatMessageDetails(!TextUtils.equals(chatType, Enums.Chats.ConversationTypes.GROUP_ALIAS) ? jid : null,
                                                        mSessionManager.getUserDetails().getImpId() + "/" + mSharedPreferencesManager.getString(SharedPreferencesManager.UMS_UDID, ""),
                                                        chatType,
                                                        "en",
                                                        chatMessage,
                                                        threadId,
                                                        memberList)))
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    if (response.isSuccessful()) {
                        logServerSuccess(response);

                        if (response.body() != null) {
                            mDbManager.updateTempMessageId(tempMessageId, response.body().getMsgId());
                        } else {
                            mDbManager.deleteMessageFromMessageId(tempMessageId);
                        }

                        return response.body() != null ? response.body().getMsgId() : null;

                    } else {
                        mDbManager.updateMessageSentStatus(tempMessageId, Enums.Chats.SentStatus.FAILED);

                        try {
                            throw new HttpException(response);
                        } catch (HttpException exception) {
                            mLogManager.logToFile(Enums.Logging.STATE_ERROR,
                                                  mApplication.getString(R.string.log_message_failure_send_chat_info,
                                                                         chatMessage,
                                                                         mSessionManager.getUserDetails().getImpId(),
                                                                         TextUtils.isEmpty(jid) ? GsonUtil.getJSON(jidList) : jid,
                                                                         String.valueOf(System.currentTimeMillis()),
                                                                         mApplication.getString(R.string.log_message_failure_send_chat_server_error)));

                            logHttpException(exception);
                            return "";
                        }
                    }
                })
                .onErrorReturn(throwable -> {
                    mDbManager.updateMessageSentStatus(tempMessageId, Enums.Chats.SentStatus.FAILED);

                    mLogManager.logToFile(Enums.Logging.STATE_ERROR,
                                          mApplication.getString(R.string.log_message_failure_send_chat_info,
                                                                 chatMessage,
                                                                 mSessionManager.getUserDetails().getImpId(),
                                                                 TextUtils.isEmpty(jid) ? GsonUtil.getJSON(jidList) : jid,
                                                                 String.valueOf(System.currentTimeMillis()),
                                                                 mApplication.getString(R.string.log_message_failure_send_chat_server_error)));
                    logServerResponseError(throwable);
                    return "";
                })
                .observeOn(mSchedulerProvider.ui());

        if (mConnectionStateManager.isUMSConnected()) {
            sendChatMessage.subscribe();

        } else {
            reconnectToUmsIfNecessary();
            mPendingChatMessages.add(new PendingChatMessage(sendChatMessage, messageToSave));
        }
    }

    @Override
    public void sendChatMessage(String jid, String chatMessage, @Enums.Chats.ConversationTypes.Type String chatType) {
        sendChatMessage(jid, chatMessage, chatType, null, null);
    }

    private void logHttpException(HttpException exception) {
        mLogManager.logToFile(Enums.Logging.STATE_ERROR, exception.response().raw().request().method() + " " + StringUtil.redactApiUrl(exception.response().raw().request().url().toString()) + " " + StringUtil.redactApiUrl(exception.getMessage()));
        FirebaseCrashlytics.getInstance().recordException(exception);
    }

    private void setHost(String host) {
        mUmsHostInterceptor.setHost(host);
        mSessionManager.setUmsHost(host);
    }

    // --------------------------------------------------------------------------------------------
}
