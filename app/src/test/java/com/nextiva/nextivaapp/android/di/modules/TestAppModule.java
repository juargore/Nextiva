package com.nextiva.nextivaapp.android.di.modules;

import com.nextiva.nextivaapp.android.db.DbManager;
import com.nextiva.nextivaapp.android.db.DbManagerKt;
import com.nextiva.nextivaapp.android.features.rooms.db.RoomsDbManager;
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
import com.nextiva.nextivaapp.android.mocks.FakeCallsRemoteMediator;
import com.nextiva.nextivaapp.android.mocks.FakeDbManager;
import com.nextiva.nextivaapp.android.mocks.FakePJSipManager;
import com.nextiva.nextivaapp.android.mocks.FakeRoomsDbManager;
import com.nextiva.nextivaapp.android.mocks.managers.FakeIntentManager;
import com.nextiva.nextivaapp.android.mocks.managers.FakeMediaPlayerManager;
import com.nextiva.nextivaapp.android.rx.TestSchedulerProvider;
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider;
import com.nextiva.nextivaapp.android.xmpp.managers.interfaces.XMPPConnectionActionManager;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.testing.TestInstallIn;

@Module
@TestInstallIn(
        components = SingletonComponent.class,
        replaces = AppModule.class
)
public class TestAppModule {

    @Provides
    @Singleton
    public SessionManager providesSessionManager() {
        return Mockito.mock(SessionManager.class);
    }

    @Provides
    @Singleton
    public SettingsManager providesSettingsManager() {
        return Mockito.mock(SettingsManager.class);
    }

    @Provides
    @Singleton
    public KeyStoreManager providesKeyStoreManager() {
        return Mockito.mock(KeyStoreManager.class);
    }

    @Provides
    @Singleton
    public SharedPreferencesManager providesSharedPreferencesManager() {
        return Mockito.mock(SharedPreferencesManager.class);
    }

    @Provides
    @Singleton
    public XMPPConnectionActionManager providesXMPPConnectionManager() {
        return Mockito.mock(XMPPConnectionActionManager.class);
    }

    @Provides
    @Singleton
    public DbManager providesDbManager() {
        return Mockito.spy(FakeDbManager.class);
    }

    @Provides
    public FakeCallsRemoteMediator providesFakeCallsRemoteMediator(DbManager dbManager, ConversationRepository conversationRepository) {
        return new FakeCallsRemoteMediator(dbManager, conversationRepository);
    }

    @Provides
    @Singleton
    public RoomsDbManager providesRoomsDbManager() {
        return Mockito.spy(FakeRoomsDbManager.class);
    }

    @Provides
    @Singleton
    public CalendarManager providesCalendarManager() {
        return Mockito.mock(CalendarManager.class);
    }

    @Provides
    @Singleton
    public ConfigManager providesConfigManager() {
        return Mockito.mock(ConfigManager.class);
    }

    @Provides
    @Singleton
    public LocalContactsManager providesLocalContactsManager() {
        return Mockito.mock(LocalContactsManager.class);
    }

    @Provides
    @Singleton
    public AnalyticsManager providesAnalyticsManager() {
        return Mockito.spy(AnalyticsManager.class);
    }

    @Provides
    @Singleton
    public DialogManager providesDialogManager() {
        return Mockito.mock(DialogManager.class);
    }

    @Provides
    @Singleton
    public CallManager providesCallManager() {
        return Mockito.mock(CallManager.class);
    }

    @Provides
    @Singleton
    public BlockingNumberManager providesBlockingNumberManager() {
        return Mockito.mock(BlockingNumberManager.class);
    }

    @Provides
    @Singleton
    public PollingManager providesPollingManager() {
        return Mockito.mock(PollingManager.class);
    }

    @Provides
    @Singleton
    public SchedulerProvider providesSchedulerProvider() {
        return new TestSchedulerProvider();
    }

    @Provides
    @Singleton
    public ContactManager providesContactManager() {
        return Mockito.mock(ContactManager.class);
    }

    @Provides
    @Singleton
    public IntentManager providesIntentManager() {
        return Mockito.spy(new FakeIntentManager());
    }

    @Provides
    @Singleton
    public ChatManager providesChatManager() {
        return Mockito.mock(ChatManager.class);
    }

    @Provides
    @Singleton
    public PushNotificationManager providesPushNotificationManager() {
        return Mockito.mock(PushNotificationManager.class);
    }

    @Provides
    @Singleton
    public PjSipManagerI providesNextivaSipManager() {
        return Mockito.spy(new FakePJSipManager());
    }

    @Provides
    @Singleton
    public NotificationManager providesNotificationManager() {
        return Mockito.mock(NotificationManager.class);
    }

    @Provides
    @Singleton
    public LogManager providesLogManager() {
        return Mockito.mock(LogManager.class);
    }

    @Provides
    @Singleton
    public ConnectionStateManager providesConnectionStateManager() {
        return Mockito.mock(ConnectionStateManager.class);
    }

    @Provides
    @Singleton
    public AvatarManager providesAvatarManager() {
        return Mockito.mock(AvatarManager.class);
    }

    @Provides
    @Singleton
    public AudioManager providesAudioManager() {
        return Mockito.mock(AudioManager.class);
    }

    @Provides
    @Singleton
    public PermissionManager providesPermissionManager() {
        return Mockito.mock(PermissionManager.class);
    }

    @Provides
    @Singleton
    public CallSettingsManager providesCallSettingsManager() {
        return Mockito.mock(CallSettingsManager.class);
    }

    @Provides
    @Singleton
    public AppUpdateManager providesAppUpdateManager() {
        return Mockito.mock(AppUpdateManager.class);
    }

    @Provides
    @Singleton
    public NetManager providesNetManager() {
        return Mockito.mock(NetManager.class);
    }

    @Provides
    @Singleton
    public WebSocketManager providesWebSocketManager() {
        return Mockito.mock(WebSocketManager.class);
    }

    @Provides
    public FakeMediaPlayerManager providesFakeMediaPlayerManager() {
        return new FakeMediaPlayerManager();
    }

    @Provides
    @Singleton
    public DatadogManager providesDatadogManager() {
        return Mockito.mock(DatadogManager.class);
    }

    @Provides
    @Singleton
    public PendoManager providesPendoManager() {
        return Mockito.mock(PendoManager.class);
    }

    @Provides
    @Singleton
    public DbManagerKt providesDbManagerKt() {
        return Mockito.mock(DbManagerKt.class);
    }

}