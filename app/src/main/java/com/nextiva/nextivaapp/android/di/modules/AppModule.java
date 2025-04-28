/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.di.modules;

import android.annotation.SuppressLint;
import android.app.Application;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

import javax.inject.Singleton;

import com.nextiva.nextivaapp.android.adapters.pagedlistadapters.mediators.CallsRemoteMediator;
import com.nextiva.nextivaapp.android.db.DbManager;
import com.nextiva.nextivaapp.android.db.DbManagerKt;
import com.nextiva.nextivaapp.android.db.NextivaDbManager;
import com.nextiva.nextivaapp.android.db.NextivaDbManagerKt;
import com.nextiva.nextivaapp.android.features.messaging.helpers.SmsTitleHelper;
import com.nextiva.nextivaapp.android.features.rooms.db.NextivaRoomsDbManager;
import com.nextiva.nextivaapp.android.features.rooms.db.RoomsDbManager;
import com.nextiva.nextivaapp.android.managers.AudioDeviceManager;
import com.nextiva.nextivaapp.android.managers.NetworkManager;
import com.nextiva.nextivaapp.android.managers.NextivaAnalyticsManager;
import com.nextiva.nextivaapp.android.managers.NextivaAppUpdateManager;
import com.nextiva.nextivaapp.android.managers.NextivaAudioManager;
import com.nextiva.nextivaapp.android.managers.NextivaBlockingNumberManager;
import com.nextiva.nextivaapp.android.managers.NextivaCalendarManager;
import com.nextiva.nextivaapp.android.managers.NextivaCallManager;
import com.nextiva.nextivaapp.android.managers.NextivaCallSettingsManager;
import com.nextiva.nextivaapp.android.managers.NextivaChatManager;
import com.nextiva.nextivaapp.android.managers.NextivaConfigManager;
import com.nextiva.nextivaapp.android.managers.NextivaConnectionStateManager;
import com.nextiva.nextivaapp.android.managers.NextivaContactManager;
import com.nextiva.nextivaapp.android.managers.NextivaDatadogManager;
import com.nextiva.nextivaapp.android.managers.NextivaDialogManager;
import com.nextiva.nextivaapp.android.managers.NextivaIntentManager;
import com.nextiva.nextivaapp.android.managers.NextivaKeyStoreManager;
import com.nextiva.nextivaapp.android.managers.NextivaLocalContactsManager;
import com.nextiva.nextivaapp.android.managers.NextivaLogManager;
import com.nextiva.nextivaapp.android.managers.NextivaMediaPlayerManager;
import com.nextiva.nextivaapp.android.managers.NextivaNetManager;
import com.nextiva.nextivaapp.android.managers.NextivaNotificationManager;
import com.nextiva.nextivaapp.android.managers.NextivaPermissionManager;
import com.nextiva.nextivaapp.android.managers.NextivaPollingManager;
import com.nextiva.nextivaapp.android.managers.NextivaPushNotificationManager;
import com.nextiva.nextivaapp.android.managers.NextivaSessionManager;
import com.nextiva.nextivaapp.android.managers.NextivaSettingsManager;
import com.nextiva.nextivaapp.android.managers.NextivaSharedPreferencesManager;
import com.nextiva.nextivaapp.android.managers.NextivaWebSocketManager;
import com.nextiva.nextivaapp.android.managers.apimanagers.NextivaPendoManager;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ConversationRepository;
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager;
import com.nextiva.nextivaapp.android.managers.interfaces.AppUpdateManager;
import com.nextiva.nextivaapp.android.managers.interfaces.AudioManager;
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager;
import com.nextiva.nextivaapp.android.managers.interfaces.BlockingNumberManager;
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager;
import com.nextiva.nextivaapp.android.managers.interfaces.CallManager;
import com.nextiva.nextivaapp.android.managers.interfaces.CallSettingsManager;
import com.nextiva.nextivaapp.android.managers.interfaces.ChatManager;
import com.nextiva.nextivaapp.android.managers.interfaces.ConfigManager;
import com.nextiva.nextivaapp.android.managers.interfaces.ConnectionStateManager;
import com.nextiva.nextivaapp.android.managers.interfaces.ContactManager;
import com.nextiva.nextivaapp.android.managers.interfaces.DatadogManager;
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager;
import com.nextiva.nextivaapp.android.managers.interfaces.IntentManager;
import com.nextiva.nextivaapp.android.managers.interfaces.KeyStoreManager;
import com.nextiva.nextivaapp.android.managers.interfaces.LocalContactsManager;
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager;
import com.nextiva.nextivaapp.android.managers.interfaces.NetManager;
import com.nextiva.nextivaapp.android.managers.interfaces.NextivaMediaPlayer;
import com.nextiva.nextivaapp.android.managers.interfaces.NotificationManager;
import com.nextiva.nextivaapp.android.managers.interfaces.PendoManager;
import com.nextiva.nextivaapp.android.managers.interfaces.PermissionManager;
import com.nextiva.nextivaapp.android.managers.interfaces.PjSipManagerI;
import com.nextiva.nextivaapp.android.managers.interfaces.PollingManager;
import com.nextiva.nextivaapp.android.managers.interfaces.PushNotificationManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager;
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager;
import com.nextiva.nextivaapp.android.managers.interfaces.WebSocketManager;
import com.nextiva.nextivaapp.android.net.PendoApi;
import com.nextiva.nextivaapp.android.rx.NextivaSchedulerProvider;
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider;
import com.nextiva.nextivaapp.android.sip.pjsip.PJSipManager;
import com.nextiva.nextivaapp.android.util.NextivaAvatarManager;
import com.nextiva.nextivaapp.android.xmpp.managers.NextivaXMPPConnectionActionManager;
import com.nextiva.nextivaapp.android.xmpp.managers.interfaces.XMPPConnectionActionManager;

/**
 * Created by adammacdonald on 2/2/18.
 */
@Module
@InstallIn(SingletonComponent.class)
public class AppModule {
    public AppModule() {
    }

    @Provides
    @Singleton
    SessionManager providesSessionManager(NextivaSessionManager sessionManager) {
        return sessionManager;
    }

    @Provides
    @Singleton
    SettingsManager providesSettingsManager(NextivaSettingsManager settingsManager) {
        return settingsManager;
    }

    @Provides
    @Singleton
    KeyStoreManager providesKeyStoreManager(NextivaKeyStoreManager keyStoreManager) {
        return keyStoreManager;
    }

    @Provides
    @Singleton
    ConnectionStateManager providesConnectionStateManager(NextivaConnectionStateManager connectionStateManager) {
        return connectionStateManager;
    }

    @Provides
    @Singleton
    SharedPreferencesManager providesSharedPreferencesManager(NextivaSharedPreferencesManager sharedPreferencesManager) {
        return sharedPreferencesManager;
    }

    @Provides
    @Singleton
    DialogManager providesDialogManager(NextivaDialogManager dialogManager) {
        return dialogManager;
    }

    @Provides
    @Singleton
    CallManager providesCallManager(NextivaCallManager callManager) {
        return callManager;
    }

    @Provides
    @Singleton
    XMPPConnectionActionManager providesXMPPConnectionManager(NextivaXMPPConnectionActionManager xmppConnectionActionManager) {
        return xmppConnectionActionManager;
    }

    @Provides
    @Singleton
    DbManager providesDbManager(NextivaDbManager dbManager) {
        return dbManager;
    }

    @Provides
    @Singleton
    DbManagerKt providesDbManagerKt(NextivaDbManagerKt dbManager) {
        return dbManager;
    }

    @SuppressLint("UnsafeOptInUsageError")
    @Provides
    CallsRemoteMediator providesCallsRemoteMediator(
            DbManager dbManager,
            ConversationRepository conversationRepository) {
        return new CallsRemoteMediator(dbManager, conversationRepository);
    }

    @Provides
    @Singleton
    RoomsDbManager providesRoomsDbManager(NextivaRoomsDbManager roomsDbManager) {
        return roomsDbManager;
    }

    @Provides
    @Singleton
    CalendarManager providesCalendarManager(NextivaCalendarManager calendarManager) {
        return calendarManager;
    }

    @Provides
    @Singleton
    ConfigManager providesConfigManager(NextivaConfigManager configManager) {
        return configManager;
    }

    @Provides
    @Singleton
    LocalContactsManager providesLocalContactsManager(NextivaLocalContactsManager localContactsManager) {
        return localContactsManager;
    }

    @Provides
    @Singleton
    ChatManager providesChatManager(NextivaChatManager chatManager) {
        return chatManager;
    }

    @Provides
    @Singleton
    AnalyticsManager providesAnalyticsManager(NextivaAnalyticsManager analyticsManager) {
        return analyticsManager;
    }

    @Provides
    @Singleton
    PollingManager providesPollingManager(NextivaPollingManager pollingManager) {
        return pollingManager;
    }

    @Provides
    @Singleton
    SchedulerProvider providesSchedulerProvider(NextivaSchedulerProvider schedulerProvider) {
        return schedulerProvider;
    }

    @Provides
    @Singleton
    ContactManager providesContactManager(NextivaContactManager contactManager) {
        return contactManager;
    }

    @Provides
    @Singleton
    IntentManager providesIntentManager(NextivaIntentManager intentManager) {
        return intentManager;
    }

    @Provides
    @Singleton
    PushNotificationManager providesPushNotificationManager(NextivaPushNotificationManager nextivaPushNotificationManager) {
        return nextivaPushNotificationManager;
    }

    @Provides
    @Singleton
    NotificationManager providesNotificationManager(NextivaNotificationManager notificationManager) {
        return notificationManager;
    }

    @Provides
    @Singleton
    LogManager providesLogManager(NextivaLogManager logManager) {
        return logManager;
    }

    @Provides
    @Singleton
    AvatarManager providesAvatarManager(NextivaAvatarManager avatarManager) {
        return avatarManager;
    }

    @Provides
    @Singleton
    AudioManager providesAudioManager(NextivaAudioManager audioManager) {
        return audioManager;
    }

    @Provides
    @Singleton
    PermissionManager providesPermissionManager(NextivaPermissionManager nextivaPermissionManager) {
        return nextivaPermissionManager;
    }

    @Provides
    @Singleton
    CallSettingsManager providesCallSettingsManager(NextivaCallSettingsManager callSettingsManager) {
        return callSettingsManager;
    }

    @Provides
    @Singleton
    AppUpdateManager providesAppUpdateManager(NextivaAppUpdateManager appUpdateManager) {
        return appUpdateManager;
    }

    @Provides
    @Singleton
    PjSipManagerI providesPJSipManager(PJSipManager manager) {
        return manager;
    }

    @Provides
    AudioDeviceManager provideAudioDeviceManager(Application application) {
        return new AudioDeviceManager(application);
    }

    @Provides
    NetworkManager provideNetworkManager(Application application) {
        return new NetworkManager(application);
    }

    @Provides
    @Singleton
    NetManager providesNetManager(NextivaNetManager netManager) {
        return netManager;
    }

    @Provides
    @Singleton
    NextivaMediaPlayer providesMediaPlayerManager(NextivaMediaPlayerManager mediaPlayerManager) {
        return mediaPlayerManager;
    }

    @Provides
    @Singleton
    WebSocketManager providesWebSocketManager(NextivaWebSocketManager webSocketManager) {
        return webSocketManager;
    }

    @Provides
    @Singleton
    BlockingNumberManager providesBlockingNumberManager(NextivaBlockingNumberManager blockingNumberManager) {
        return blockingNumberManager;
    }

    @Provides
    @Singleton
    SmsTitleHelper providesSmsTitleHelper(DbManager dbManager, SessionManager sessionManager) {
        return new SmsTitleHelper(dbManager, sessionManager);
    }

    @Provides
    @Singleton
    DatadogManager providesDatadogManager(NextivaDatadogManager datadogManager) {
        return datadogManager;
    }

    @Provides
    @Singleton
    PendoManager providesPendoManager(PendoApi api, SharedPreferencesManager preferencesManager) {
        return new NextivaPendoManager(api, preferencesManager);
    }
}