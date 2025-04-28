package com.nextiva.nextivaapp.android.viewmodels;

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nextiva.androidNextivaAuth.data.datasource.network.dto.UserInfo;
import com.nextiva.nextivaapp.android.BuildConfig;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.core.common.api.ContactManagementPolicyRepository;
import com.nextiva.nextivaapp.android.db.DbManager;
import com.nextiva.nextivaapp.android.features.rooms.db.RoomsDbManager;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.AuthenticationRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.CallManagementRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ConversationRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.MobileConfigRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PresenceRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ProductsRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.SmsManagementRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository;
import com.nextiva.nextivaapp.android.managers.interfaces.ConfigManager;
import com.nextiva.nextivaapp.android.managers.interfaces.NetManager;
import com.nextiva.nextivaapp.android.managers.interfaces.NotificationManager;
import com.nextiva.nextivaapp.android.managers.interfaces.PushNotificationManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager;
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager;
import com.nextiva.nextivaapp.android.models.CurrentUser;
import com.nextiva.nextivaapp.android.models.IdentityVoice;
import com.nextiva.nextivaapp.android.models.SingleEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.BaseResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.CallHistoryResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.FeatureAccessCodesResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.MobileConfigResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.ServiceSettingsMapResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.UserDetailsResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.VoicemailMessageSummaryResponseEvent;
import com.nextiva.nextivaapp.android.net.exceptions.NoAccessDeviceFoundException;
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider;
import com.nextiva.nextivaapp.android.util.GsonUtil;
import com.nextiva.nextivaapp.android.util.LogUtil;
import com.nextiva.nextivaapp.android.xmpp.managers.interfaces.XMPPConnectionActionManager;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import me.leolin.shortcutbadger.ShortcutBadgeException;
import me.leolin.shortcutbadger.ShortcutBadger;

@HiltViewModel
public class LoginViewModel extends BaseViewModel {

    private final DbManager mDbManager;
    private final RoomsDbManager mRoomsDbManager;
    private final SettingsManager mSettingsManager;
    private final SessionManager mSessionManager;
    private final PushNotificationManager mPushNotificationManager;
    private final MobileConfigRepository mMobileConfigRepository;
    private final UserRepository mUserRepository;
    private final AuthenticationRepository mAuthenticationServiceRepository;
    private final CallManagementRepository mCallManagementRepository;
    private final SchedulerProvider mSchedulerProvider;
    private final XMPPConnectionActionManager mXMPPConnectionActionManager;
    private final SharedPreferencesManager mSharedPreferencesManager;
    private final PlatformRepository mPlatformRepository;
    private final SmsManagementRepository mSmsManagementRepository;
    private final ProductsRepository mProductsRepository;
    private final ConversationRepository mConversationRepository;
    private final PresenceRepository mPresenceRepository;
    private final ContactManagementPolicyRepository mContactManagementPolicyRepository;
    private final ConfigManager mConfigManager;
    private final NotificationManager mNotificationManager;
    private final NetManager mNetManager;

    private final MutableLiveData<SingleEvent<Boolean>> mLoginFailedMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<SingleEvent<Boolean>> mNoAccessDeviceFoundMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mFinalizeLoginProcessLiveData = new MutableLiveData<>();

    private String mSessionId = "";
    private String mSessionTenant = "";
    private UserInfo mUserInfo;
    private IdentityVoice mIdentityVoice;

    private final MutableLiveData<String> _userInfoResponse = new MutableLiveData<>();
    public LiveData<String> userInfoResponse = _userInfoResponse;


    private final Function<BaseResponseEvent, SingleSource<MobileConfigResponseEvent>> mMobileConfigFunction =
            new Function<BaseResponseEvent, SingleSource<MobileConfigResponseEvent>>() {
                @Override
                public SingleSource<MobileConfigResponseEvent> apply(BaseResponseEvent responseEvent) {
                    if (!responseEvent.isSuccessful()) {
                        FirebaseCrashlytics.getInstance().recordException(new Exception("Failed to Login, Mobile Config Exception: " + responseEvent));
                        Toast.makeText(getApplication(), getApplication().getString(R.string.login_error_toast_message_mobile_config), Toast.LENGTH_LONG).show();

                    } else {
                        if (responseEvent instanceof RxEvents.AuthenticationResponseEvent) {
                            if (((RxEvents.AuthenticationResponseEvent) responseEvent).getHasMobileDevice()) {
                                return mMobileConfigRepository.getMobileConfig();

                            } else {
                                FirebaseCrashlytics.getInstance().recordException(new Exception("Failed to Login: NoAccessDeviceFoundException"));
                                return Single.error(new NoAccessDeviceFoundException());
                            }
                        }

                        FirebaseCrashlytics.getInstance().recordException(new Exception("Failed to Login: " + responseEvent));
                    }
                    return Single.error(new Exception());
                }
            };

    private final Function<BaseResponseEvent, SingleSource<UserDetailsResponseEvent>> mUserDetailsFunction =
            new Function<BaseResponseEvent, SingleSource<UserDetailsResponseEvent>>() {
                @Override
                public SingleSource<UserDetailsResponseEvent> apply(BaseResponseEvent responseEvent) {
                    if (!responseEvent.isSuccessful()) {
                        FirebaseCrashlytics.getInstance().recordException(new Exception("Failed to Login, User Details Exception: " + responseEvent));

                        return Single.error(new Exception());
                    } else {
                        return mUserRepository.getUserDetails();
                    }
                }
            };

    private final Function<BaseResponseEvent, SingleSource<FeatureAccessCodesResponseEvent>> mFeatureAccessCodesFunction =
            new Function<BaseResponseEvent, SingleSource<FeatureAccessCodesResponseEvent>>() {
                @Override
                public SingleSource<FeatureAccessCodesResponseEvent> apply(BaseResponseEvent responseEvent) {
                    if (!responseEvent.isSuccessful()) {
                        FirebaseCrashlytics.getInstance().recordException(new Exception("Failed to Login, Feature Access Codes Exception: " + responseEvent));
                        return Single.error(new Exception());
                    } else {
                        return mUserRepository.getFeatureAccessCodes();
                    }
                }
            };

    private final Function<BaseResponseEvent, SingleSource<ServiceSettingsMapResponseEvent>> mServiceSettingsFunction =
            new Function<BaseResponseEvent, SingleSource<ServiceSettingsMapResponseEvent>>() {
                @Override
                public SingleSource<ServiceSettingsMapResponseEvent> apply(BaseResponseEvent responseEvent) {
                    if (mSessionManager.isSmsEnabled() && !mSessionManager.isNextivaConnectEnabled()) {
                        mSmsManagementRepository.getSmsConversations().subscribe();
                    }

                    mXMPPConnectionActionManager.startConnection();

                    return mUserRepository.getServiceSettingsFiltered(
                            new String[] {Enums.Service.TYPE_REMOTE_OFFICE, Enums.Service.TYPE_BROADWORKS_ANYWHERE});
                }
            };

    private final Function<BaseResponseEvent, SingleSource<VoicemailMessageSummaryResponseEvent>> mVoicemailMessageSummaryFunction =
            new Function<BaseResponseEvent, SingleSource<VoicemailMessageSummaryResponseEvent>>() {
                @Override
                public SingleSource<VoicemailMessageSummaryResponseEvent> apply(BaseResponseEvent responseEvent) {
                    if (!responseEvent.isSuccessful()) {
                        FirebaseCrashlytics.getInstance().recordException(new Exception("Failed to Login, Voicemail Message Summary Exception: " + responseEvent));
                        return Single.error(new Exception());
                    } else {
                        return mUserRepository.getVoicemailMessageSummary();
                    }
                }
            };

    private final Function<BaseResponseEvent, SingleSource<CallHistoryResponseEvent>> mAllCallLogEntriesFunction =
            new Function<BaseResponseEvent, SingleSource<CallHistoryResponseEvent>>() {
                @Override
                public SingleSource<CallHistoryResponseEvent> apply(BaseResponseEvent responseEvent) {
                    if (!responseEvent.isSuccessful()) {
                        return Single.error(new Exception());
                    } else {
                        return mCallManagementRepository.getAllCallLogEntries();
                    }
                }
            };

    private final Function<BaseResponseEvent, SingleSource<RxEvents.ResetConferenceCallResponseEvent>> mActiveConferenceCallFunction =
            new Function<BaseResponseEvent, SingleSource<RxEvents.ResetConferenceCallResponseEvent>>() {
                @Override
                public SingleSource<RxEvents.ResetConferenceCallResponseEvent> apply(BaseResponseEvent responseEvent) {
                    if (!responseEvent.isSuccessful()) {
                        return Single.error(new Exception());
                    } else {
                        return mCallManagementRepository.getResetConferenceCall();
                    }
                }
            };

    private final Function<BaseResponseEvent, SingleSource<RxEvents.PhoneInformationResponseEvent>> mProductsFunction =
            new Function<BaseResponseEvent, SingleSource<RxEvents.PhoneInformationResponseEvent>>() {
                @Override
                public SingleSource<RxEvents.PhoneInformationResponseEvent> apply(BaseResponseEvent responseEvent) {
                    if (!responseEvent.isSuccessful()) {
                        FirebaseCrashlytics.getInstance().recordException(new Exception("Failed to Login, Products Exception: " + responseEvent));
                        Toast.makeText(getApplication(), getApplication().getString(R.string.login_error_toast_message_products), Toast.LENGTH_LONG).show();
                        return Single.error(new Exception());
                    } else {
                        return mProductsRepository.refreshLicenses();
                    }
                }
            };

    private final Function<BaseResponseEvent, SingleSource<RxEvents.FeatureFlagsResponseEvent>> mFeatureFlagFunction =
            new Function<BaseResponseEvent, SingleSource<RxEvents.FeatureFlagsResponseEvent>>() {
                @Override
                public SingleSource<RxEvents.FeatureFlagsResponseEvent> apply(@io.reactivex.annotations.NonNull BaseResponseEvent baseResponseEvent) throws Exception {
                    return mPlatformRepository.getFeatureFlags();
                }
            };

    private final Function<BaseResponseEvent, SingleSource<RxEvents.AccountInformationResponseEvent>> mAccountInformationFunction =
            new Function<BaseResponseEvent, SingleSource<RxEvents.AccountInformationResponseEvent>>() {
                @Override
                public SingleSource<RxEvents.AccountInformationResponseEvent> apply(@io.reactivex.annotations.NonNull BaseResponseEvent baseResponseEvent) throws Exception {
                    if (!baseResponseEvent.isSuccessful()) {
                        FirebaseCrashlytics.getInstance().recordException(new Exception("Failed to Login, Account Information Exception: " + baseResponseEvent));
                        Toast.makeText(getApplication(), getApplication().getString(R.string.login_error_toast_message_account_information), Toast.LENGTH_LONG).show();
                        return Single.error(new Exception());
                    } else {
                        return mPlatformRepository.getAccountInformation();
                    }
                }
            };

    private final Function<BaseResponseEvent, SingleSource<RxEvents.PresenceSentResponseEvent>> mPresencePingFunction =
            new Function<BaseResponseEvent, SingleSource<RxEvents.PresenceSentResponseEvent>>() {
                @Override
                public SingleSource<RxEvents.PresenceSentResponseEvent> apply(BaseResponseEvent baseResponseEvent) {
                    if (!baseResponseEvent.isSuccessful()) {
                        FirebaseCrashlytics.getInstance().recordException(new Exception("Failed to Login, Presence Sent Exception: " + baseResponseEvent));
                        Toast.makeText(getApplication(), getApplication().getString(R.string.login_error_toast_message_account_information), Toast.LENGTH_LONG).show();
                        return Single.error(new Exception());
                    } else {
                        if (mSessionManager.isNextivaConnectEnabled()) {
                            mPresenceRepository.sendPresencePing();
                        }
                    }

                    return Single.just(new RxEvents.PresenceSentResponseEvent());
                }
            };

    private final Function<BaseResponseEvent, SingleSource<RxEvents.ContactManagementPolicyResponseEvent>> mContactManagementPolicyFunction =
            new Function<BaseResponseEvent, SingleSource<RxEvents.ContactManagementPolicyResponseEvent>>() {
                @Override
                public SingleSource<RxEvents.ContactManagementPolicyResponseEvent> apply(BaseResponseEvent baseResponseEvent) {
                    if (!baseResponseEvent.isSuccessful()) {
                        FirebaseCrashlytics.getInstance().recordException(new Exception("Failed to Login, Contact Management Policy Exception: " + baseResponseEvent));
                        Toast.makeText(getApplication(), getApplication().getString(R.string.login_error_toast_message_account_information), Toast.LENGTH_LONG).show();
                        return Single.error(new Exception());
                    } else {
                        if (mSessionManager.isNextivaConnectEnabled()) {
                            mContactManagementPolicyRepository.getContactManagementPrivilege()
                                    .subscribe(new DisposableSingleObserver<Boolean>() {
                                        @Override
                                        public void onSuccess(@io.reactivex.annotations.NonNull Boolean granted) {
                                            mSessionManager.setContactManagementPrivilege(granted);
                                        }

                                        @Override
                                        public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                        }
                                    });
                        }
                    }

                    return Single.just(new RxEvents.ContactManagementPolicyResponseEvent());
                }
            };

    private final Function<BaseResponseEvent, SingleSource<BaseResponseEvent>> mUsersTeamsFunction =
            new Function<BaseResponseEvent, SingleSource<BaseResponseEvent>>() {
                @Override
                public SingleSource<BaseResponseEvent> apply(BaseResponseEvent baseResponseEvent) {
                    return mSmsManagementRepository.getUsersTeams();
                }
            };

    @Inject
    public LoginViewModel(@NonNull Application application,
                          DbManager dbManager,
                          RoomsDbManager roomsDbManager,
                          SessionManager sessionManager,
                          SettingsManager settingsManager,
                          PushNotificationManager pushNotificationManager,
                          MobileConfigRepository mobileConfigRepository,
                          UserRepository userRepository,
                          AuthenticationRepository authenticationRepository,
                          CallManagementRepository callManagementRepository,
                          SchedulerProvider schedulerProvider,
                          XMPPConnectionActionManager xmppConnectionActionManager,
                          SharedPreferencesManager sharedPreferencesManager,
                          PlatformRepository platformRepository,
                          SmsManagementRepository smsManagementRepository,
                          ProductsRepository productsRepository,
                          ConversationRepository conversationRepository,
                          PresenceRepository presenceRepository,
                          ContactManagementPolicyRepository contactManagementPolicyRepository,
                          ConfigManager configManager,
                          NotificationManager notificationManager,
                          NetManager netManager) {

        super(application);

        mDbManager = dbManager;
        mRoomsDbManager = roomsDbManager;
        mSettingsManager = settingsManager;
        mSessionManager = sessionManager;
        mPushNotificationManager = pushNotificationManager;
        mMobileConfigRepository = mobileConfigRepository;
        mUserRepository = userRepository;
        mAuthenticationServiceRepository = authenticationRepository;
        mCallManagementRepository = callManagementRepository;
        mSchedulerProvider = schedulerProvider;
        mXMPPConnectionActionManager = xmppConnectionActionManager;
        mSharedPreferencesManager = sharedPreferencesManager;
        mPlatformRepository = platformRepository;
        mSmsManagementRepository = smsManagementRepository;
        mProductsRepository = productsRepository;
        mConversationRepository = conversationRepository;
        mPresenceRepository = presenceRepository;
        mContactManagementPolicyRepository = contactManagementPolicyRepository;
        mConfigManager = configManager;
        mNotificationManager = notificationManager;
        mNetManager = netManager;
    }

    public boolean hasLicenseOrPhoneNumber() {
        return (mSessionManager.getIsLicenseApproved() || !TextUtils.isEmpty(mSettingsManager.getPhoneNumber()));
    }

    public void processExistingDialingServiceSetting() {
        boolean callBackConflict = mSettingsManager.getDialingService() == Enums.Service.DialingServiceTypes.CALL_BACK &&
                !mSessionManager.getIsCallBackEnabled(mSessionManager.getRemoteOfficeServiceSettings(), mSessionManager.getNextivaAnywhereServiceSettings());
        boolean callThroughConflict = mSettingsManager.getDialingService() == Enums.Service.DialingServiceTypes.CALL_THROUGH &&
                !mSessionManager.getIsCallThroughEnabled(mSessionManager.getNextivaAnywhereServiceSettings(), mSettingsManager.getPhoneNumber());

        if (callBackConflict || callThroughConflict) {
            mSettingsManager.setDialingService(Enums.Service.DialingServiceTypes.VOIP);
        }
    }

    public void getBroadworksCredentials(Activity activity) {
        if (mSessionId == null) {
            LogUtil.e("Session ID is null. Cannot get Broadworks credentials.");
            presentLoginFailed();
            return;
        }
        String sessionId = mSessionId;
        String corpAcctId;
        if(mUserInfo != null && mUserInfo.getComNextivaCorpAccountNumber() != null)
            corpAcctId = String.valueOf(mUserInfo.getComNextivaCorpAccountNumber());
        else {
            corpAcctId = "";
        }

        if(TextUtils.isEmpty(corpAcctId))
            LogUtil.d("Login View Model corpAcctId is missing");


        if(mIdentityVoice != null && mIdentityVoice.getUsername() != null && mIdentityVoice.getPassword() != null) {
            authenticateUser(mIdentityVoice.getUsername(), mIdentityVoice.getPassword());
        }
        else {
        mCompositeDisposable.add(
                mPlatformRepository
                        .getBroadworksCredentials(activity, sessionId, corpAcctId)
                        .subscribe(broadworksCredentials -> {
                            if (broadworksCredentials != null &&
                                    !TextUtils.isEmpty(broadworksCredentials.getUsername()) &&
                                    !TextUtils.isEmpty(broadworksCredentials.getPassword())) {
                                authenticateUser(broadworksCredentials.getUsername(), broadworksCredentials.getPassword());
                            } else {
                                    mCompositeDisposable.add(
                                            mPlatformRepository
                                                    .getBroadworksCredentials(activity, sessionId, corpAcctId)
                                                    .subscribe(broadworksCredentials2 -> {
                                                        if (broadworksCredentials2 != null &&
                                                                !TextUtils.isEmpty(broadworksCredentials2.getUsername()) &&
                                                                !TextUtils.isEmpty(broadworksCredentials2.getPassword())) {
                                                            authenticateUser(broadworksCredentials2.getUsername(), broadworksCredentials2.getPassword());
                                                        } else {
                                                            presentLoginFailed();
                                                        }
                                                    })
                                    );
                                }
                            }
                        )
        );
        }
    }


    public void authenticateUser(String userName, String password) {
        LogUtil.d("authenticateUser");
        if (!userName.contains("@")) {
            userName += mSharedPreferencesManager.getString(SharedPreferencesManager.HOSTNAME, BuildConfig.HOSTNAME);
        }

        mPushNotificationManager.enableFCM();

        mAuthenticationServiceRepository.authenticateUser(userName, password)
                .flatMap(mMobileConfigFunction)
                .flatMap(mUserDetailsFunction)
                .flatMap(mFeatureAccessCodesFunction)
                .flatMap(mProductsFunction)
                .flatMap(mFeatureFlagFunction)
                .flatMap(mUsersTeamsFunction)
                .flatMap(mAccountInformationFunction)
                .flatMap(mServiceSettingsFunction)
                .flatMap(mPresencePingFunction)
                .flatMap(mContactManagementPolicyFunction)
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new DisposableSingleObserver<BaseResponseEvent>() {
                    @Override
                    public void onSuccess(BaseResponseEvent event) {
                        if (event.isSuccessful()) {
                            mSharedPreferencesManager.setBoolean(SharedPreferencesManager.ENABLE_CALL_LOG_DATADOG_EVENT,false);
                            mFinalizeLoginProcessLiveData.setValue(true);

                        } else {
                            mPushNotificationManager.disableFCM();
                            presentLoginFailed();
                        }

                        dispose();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mPushNotificationManager.disableFCM();

                        if (e instanceof NoAccessDeviceFoundException) {
                            presentNoAccessDeviceFound();
                        } else {
                            presentLoginFailed();
                        }

                        dispose();
                    }
                });
    }

    private void presentNoAccessDeviceFound() {
        mCompositeDisposable.add(
                Completable.fromAction(mDbManager::clearAndResetAllTables).subscribeOn(mSchedulerProvider.io())
                        .observeOn(mSchedulerProvider.ui())
                        .subscribe(() -> {
                            mSessionManager.setRememberPassword(false);
                            mNoAccessDeviceFoundMutableLiveData.setValue(new SingleEvent<>(true));
                        }));
        mCompositeDisposable.add(
                Completable.fromAction(mRoomsDbManager::clearAndResetAllTables).subscribeOn(mSchedulerProvider.io())
                        .observeOn(mSchedulerProvider.ui())
                        .subscribe());
    }

    private void presentLoginFailed() {
        mCompositeDisposable.add(
                Completable.fromAction(mDbManager::clearAndResetAllTables).subscribeOn(mSchedulerProvider.io())
                        .observeOn(mSchedulerProvider.ui())
                        .subscribe(() -> {
                            mSessionManager.setRememberPassword(false);
                            mLoginFailedMutableLiveData.setValue(new SingleEvent<>(true));
                        }));
        mCompositeDisposable.add(
                Completable.fromAction(mRoomsDbManager::clearAndResetAllTables).subscribeOn(mSchedulerProvider.io())
                        .observeOn(mSchedulerProvider.ui())
                        .subscribe());
    }

    public LiveData<SingleEvent<Boolean>> getLoginFailedLiveData() {
        return mLoginFailedMutableLiveData;
    }

    public LiveData<SingleEvent<Boolean>> getNoAccessDeviceFoundLiveData() {
        return mNoAccessDeviceFoundMutableLiveData;
    }

    public LiveData<Boolean> getFinalizeLoginProcessLiveData() {
        return mFinalizeLoginProcessLiveData;
    }

    public void clearAndResetAllTables() {
        // Reset flag in preferences so contact list is completely refreshed when contact are loaded
        mSharedPreferencesManager.setLong(SharedPreferencesManager.LAST_CONTACTS_REFRESHED_TIMESTAMP, 0L);

        Completable.fromAction(mDbManager::clearAndResetAllTables)
                .subscribeOn(mSchedulerProvider.io())
                .subscribe();
    }

    // --------------------------------------------------------------------------------------------
    // SessionManager Methods
    // --------------------------------------------------------------------------------------------
    public String getSessionUsernameWithoutHostname() {
        if (!TextUtils.isEmpty(mSessionManager.getLastLoggedUsername())) {
            if (mSessionManager.getLastLoggedUsername().contains("@")) {
                return mSessionManager.getLastLoggedUsername().split("@")[0];

            } else {
                return mSessionManager.getLastLoggedUsername();
            }
        }

        return null;
    }

    public String getSessionPassword() {
        return mSessionManager.getLastLoggedPassword();
    }

    public boolean shouldRememberPassword() {
        return mSessionManager.getRememberPassword();
    }
    // --------------------------------------------------------------------------------------------

//Open Id Sign Out
    public void processSignOut() {
        mCompositeDisposable.add(
                Completable
                        .fromAction(() -> {
                            mDbManager.expireContactCache();
                            mDbManager.clearAndResetAllTables();
                            mConfigManager.setMobileConfig(null);
                            getApplication().getCacheDir().deleteOnExit();
                            mXMPPConnectionActionManager.stopConnection();
                            mPushNotificationManager.disableFCM();
                            mNotificationManager.cancelAllNotifications();
                            mSharedPreferencesManager.setInt(SharedPreferencesManager.PENDO_CALL_TRACKING_COUNTER, 0);
                            mNetManager.clearBroadsoftUserApiManager();
                            try {
                                ShortcutBadger.removeCountOrThrow(getApplication());
                            }
                            catch (ShortcutBadgeException e)
                            {
                                FirebaseCrashlytics.getInstance().recordException(e);
                            }
                        })
                        .subscribeOn(mSchedulerProvider.io())
                        .observeOn(mSchedulerProvider.ui())
                        .onErrorComplete()
                        .subscribe());
    }

    public void setSessionId(@NonNull String sessionId) {
        if (!sessionId.isEmpty()) {
            mSessionId = sessionId;
            mSessionManager.setSessionId(sessionId);
        }
    }

    public String getSessionId() {
        return mSessionId;
    }

    public void setSessionTenant(@NonNull String sessionTenant) {
        mSessionTenant = sessionTenant;
        mSessionManager.setSelectedTenant(sessionTenant);
    }

    public String getSessionTenant() {
        return mSessionTenant;
    }

    public void setUserInfo(@NonNull String userInfo) {
        mUserInfo = GsonUtil.getObject(UserInfo.class, userInfo);
        mSessionManager.setUserInfo(mUserInfo);

        CurrentUser currentUser = new CurrentUser(
                mUserInfo.getComNextivaUseruuid(),
                mUserInfo.getComNextivaCorpAccountNumber()!= null ? mUserInfo.getComNextivaCorpAccountNumber().toString() : "",
                null,
                mUserInfo.getComNextivaFirstName()+" "+mUserInfo.getComNextivaLastName(),
                mUserInfo.getComNextivaEmail(),
                null,
                mUserInfo.getComNextivaLastName(),
                null,
                null,
                null,
                mUserInfo.getComNextivaFirstName()
        );

        mDbManager.setSessionSetting(Enums.Session.DatabaseKey.CURRENT_USER, GsonUtil.getJSON(currentUser));
    }

    public UserInfo getUserInfo() {
        return mUserInfo;
    }

    public void setVoiceIdentity(@NonNull String identityVoice) {
        mIdentityVoice = GsonUtil.getObject(IdentityVoice.class, identityVoice);
        mSessionManager.setIdentityVoice(mIdentityVoice);
    }

    public IdentityVoice getVoiceIdentity() {
        return mIdentityVoice;
    }

    public void logoutAuth(){
    }

    public void clearSession(){
        setSessionId("");
        setVoiceIdentity("");
        setUserInfo("");
        setSessionTenant("");
    }


}