/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers;

import android.content.Context;
import android.net.http.HttpException;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresExtension;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Constants;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.AuthenticationRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.CallManagementRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ConversationRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.MobileConfigRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformContactsRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PresenceRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ProductsRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.SmsManagementRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository;
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager;
import com.nextiva.nextivaapp.android.managers.interfaces.ConnectionStateManager;
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager;
import com.nextiva.nextivaapp.android.managers.interfaces.PollingManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager;
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager;
import com.nextiva.nextivaapp.android.models.net.broadsoft.accessdevices.BroadsoftAccessDevicesResponse;
import com.nextiva.nextivaapp.android.net.buses.RxEvents;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.BaseResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.FeatureAccessCodesResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.ServiceSettingsMapResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.UserDetailsResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.VoicemailMessageSummaryResponseEvent;
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider;
import com.nextiva.nextivaapp.android.util.LogUtil;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;


/*
Note on 20/Feb/2025
TODO: DEPRECATED : REMOVE FILE once new NextivaPollingManager works as expected

@Singleton
public class NextivaPollingManager implements PollingManager {

    private static final long CACHE_EXPIRY_MILLIS = Constants.ONE_MINUTE_IN_MILLIS * 5;
    private static final long TEN_MINUTE_CACHE_EXPIRY_MILLIS = Constants.ONE_MINUTE_IN_MILLIS * 10;

    private final UserRepository mUserRepository;
    private final MobileConfigRepository mMobileConfigRepository;
    private final SharedPreferencesManager mSharedPrefsManager;
    private final CalendarManager mCalendarManager;
    final CallManagementRepository mCallManagementRepository;
    private final LogManager mLogManager;
    private final ConnectionStateManager mConnectionStateManager;
    private final ProductsRepository mProductsRepository;
    private final PlatformRepository mPlatformRepository;
    private final PresenceRepository mPresenceRepository;
    final SessionManager mSessionManager;
    final ConversationRepository mConversationRepository;
    final SmsManagementRepository mSmsRespository;
    private final SchedulerProvider mSchedulerProvider;
    final PlatformContactsRepository mPlatformContactsRepository;
    private final AuthenticationRepository mAuthenticationRepository;
    private Context mContext;

    private int http401ErrorCount = 0;

    private final MutableLiveData<Boolean> mPollingCompleteMutableLiveData = new MutableLiveData<>();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

            refreshSettings();
        }
    };

    private final Runnable mTenMinuteRefreshRunnable = new Runnable() {
        @Override
        public void run() {
            mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

            tenMinuteRefresh();
        }
    };

    private final Function<BaseResponseEvent, SingleSource<UserDetailsResponseEvent>> mUserDetailsFunction =
            new Function<BaseResponseEvent, SingleSource<UserDetailsResponseEvent>>() {
                @Override
                public SingleSource<UserDetailsResponseEvent> apply(BaseResponseEvent responseEvent) {
                    mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

                    return mUserRepository.getUserDetails();
                }
            };

    private final Function<BaseResponseEvent, SingleSource<RxEvents.AuthenticationResponseEvent>> mAccessDeviceFunction =
            new Function<BaseResponseEvent, SingleSource<RxEvents.AuthenticationResponseEvent>>() {
                @Override
                public SingleSource<RxEvents.AuthenticationResponseEvent> apply(BaseResponseEvent baseResponseEvent) {
                    mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

                    return mAuthenticationRepository.getAuthenticationResponseEvent(mSessionManager.getUsername());
                }
            };

    private final Function<BaseResponseEvent, SingleSource<FeatureAccessCodesResponseEvent>> mFeatureAccessCodesFunction =
            new Function<BaseResponseEvent, SingleSource<FeatureAccessCodesResponseEvent>>() {
                @Override
                public SingleSource<FeatureAccessCodesResponseEvent> apply(BaseResponseEvent responseEvent) {
                    mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

                    return mUserRepository.getFeatureAccessCodes();
                }
            };

    private final Function<BaseResponseEvent, SingleSource<ServiceSettingsMapResponseEvent>> mServiceSettingsFunction =
            new Function<BaseResponseEvent, SingleSource<ServiceSettingsMapResponseEvent>>() {
                @Override
                public SingleSource<ServiceSettingsMapResponseEvent> apply(BaseResponseEvent responseEvent) {
                    mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

                    return mUserRepository.getServiceSettingsFiltered(
                            new String[]{Enums.Service.TYPE_REMOTE_OFFICE, Enums.Service.TYPE_BROADWORKS_ANYWHERE});
                }
            };

    private final Function<BaseResponseEvent, SingleSource<VoicemailMessageSummaryResponseEvent>> mVoicemailMessageSummaryFunction =
            new Function<BaseResponseEvent, SingleSource<VoicemailMessageSummaryResponseEvent>>() {
                @Override
                public SingleSource<VoicemailMessageSummaryResponseEvent> apply(BaseResponseEvent responseEvent) {
                    mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

                    return mUserRepository.getVoicemailMessageSummary();
                }
            };

    private final Function<BaseResponseEvent, SingleSource<RxEvents.PhoneInformationResponseEvent>> mProductsFunction =
            new Function<BaseResponseEvent, SingleSource<RxEvents.PhoneInformationResponseEvent>>() {
                @Override
                public SingleSource<RxEvents.PhoneInformationResponseEvent> apply(BaseResponseEvent responseEvent) {
                    if (!responseEvent.isSuccessful()) {
                        return Single.error(new Exception());
                    } else {
                        return mProductsRepository.refreshLicenses();
                    }
                }
            };

    private final Function<BaseResponseEvent, SingleSource<RxEvents.PresenceResponseEvent>> mPresenceFunction =
            new Function<BaseResponseEvent, SingleSource<RxEvents.PresenceResponseEvent>>() {
                @Override
                public SingleSource<RxEvents.PresenceResponseEvent> apply(BaseResponseEvent baseResponseEvent) {

                    if (mSessionManager.isNextivaConnectEnabled()) {
                        mPresenceRepository.getPresences();
                    }

                    return Single.just(new RxEvents.PresenceResponseEvent());
                }
            };

    private final Function<BaseResponseEvent, SingleSource<RxEvents.PresenceSentResponseEvent>> mPresencePingFunction =
            new Function<BaseResponseEvent, SingleSource<RxEvents.PresenceSentResponseEvent>>() {
                @Override
                public SingleSource<RxEvents.PresenceSentResponseEvent> apply(BaseResponseEvent baseResponseEvent) {

                    if (mSessionManager.isNextivaConnectEnabled()) {
                        mPresenceRepository.sendPresencePing();
                    }

                    return Single.just(new RxEvents.PresenceSentResponseEvent());
                }
            };

    private final Function<BaseResponseEvent, SingleSource<RxEvents.AccountInformationResponseEvent>> mAccountInformationFunction =
            new Function<BaseResponseEvent, SingleSource<RxEvents.AccountInformationResponseEvent>>() {
                @Override
                public SingleSource<RxEvents.AccountInformationResponseEvent> apply(@io.reactivex.annotations.NonNull BaseResponseEvent baseResponseEvent) {
                    return mPlatformRepository.getAccountInformation();
                }
            };

    private final Function<BaseResponseEvent, SingleSource<BaseResponseEvent>> mUsersTeamsFunction =
            new Function<BaseResponseEvent, SingleSource<BaseResponseEvent>>() {
                @Override
                public SingleSource<BaseResponseEvent> apply(BaseResponseEvent baseResponseEvent) throws Exception {
                    return mSmsRespository.getUsersTeams();
                }
            };

    private final Action mFinalAction = new Action() {
        @Override
        public void run() {
            mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

            mHandler.postDelayed(mRunnable, CACHE_EXPIRY_MILLIS + 1);
            mSharedPrefsManager.setLong(SharedPreferencesManager.POLLED_SETTINGS_LAST_CACHE_TIMESTAMP, mCalendarManager.getNowMillis());
            mPollingCompleteMutableLiveData.postValue(true);
        }
    };

    private final Action mFinalTenMinuteAction = new Action() {
        @Override
        public void run() {
            mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

            mHandler.postDelayed(mTenMinuteRefreshRunnable, TEN_MINUTE_CACHE_EXPIRY_MILLIS + 1);
            mSharedPrefsManager.setLong(SharedPreferencesManager.POLLED_SETTINGS_LAST_TEN_MINUTE_CACHE_TIMESTAMP, mCalendarManager.getNowMillis());
            mPollingCompleteMutableLiveData.setValue(true);
        }
    };

    @Inject
    public NextivaPollingManager(UserRepository userRepository,
                                 MobileConfigRepository mobileConfigRepository,
                                 SharedPreferencesManager sharedPrefsManager,
                                 CalendarManager calendarManager,
                                 CallManagementRepository callManagementRepository,
                                 LogManager logManager,
                                 ConnectionStateManager connectionStateManager,
                                 ProductsRepository productsRepository,
                                 PlatformRepository platformRepository,
                                 PresenceRepository presenceRepository,
                                 SessionManager sessionManager,
                                 ConversationRepository conversationRepository,
                                 SchedulerProvider schedulerProvider,
                                 SmsManagementRepository smsRespository,
                                 PlatformContactsRepository platformContactsRepository,
                                 AuthenticationRepository authenticationRepository) {

        mUserRepository = userRepository;
        mMobileConfigRepository = mobileConfigRepository;
        mSharedPrefsManager = sharedPrefsManager;
        mCalendarManager = calendarManager;
        mCallManagementRepository = callManagementRepository;
        mLogManager = logManager;
        mConnectionStateManager = connectionStateManager;
        mProductsRepository = productsRepository;
        mPlatformRepository = platformRepository;
        mPresenceRepository = presenceRepository;
        mSessionManager = sessionManager;
        mConversationRepository = conversationRepository;
        mSchedulerProvider = schedulerProvider;
        mSmsRespository = smsRespository;
        mPlatformContactsRepository = platformContactsRepository;
        mAuthenticationRepository = authenticationRepository;
    }

    // --------------------------------------------------------------------------------------------
    // PollingManager Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public LiveData<Boolean> getPollingCompleteLiveData() {
        return mPollingCompleteMutableLiveData;
    }

    public int getHttp401ErrorCount() {
        return http401ErrorCount;
    }

    private void tenMinuteRefresh() {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        long lastCacheTimestampMillis = mSharedPrefsManager.getLong(SharedPreferencesManager.POLLED_SETTINGS_LAST_TEN_MINUTE_CACHE_TIMESTAMP, 0);
        long lastCacheExpirationMillis = mCalendarManager.getNowMillis() - TEN_MINUTE_CACHE_EXPIRY_MILLIS;

        if (lastCacheTimestampMillis < lastCacheExpirationMillis && mConnectionStateManager.isInternetConnected()) {
            mPlatformRepository.getFeatureFlags()
                    .flatMap(mAccountInformationFunction)
                    .flatMap(mUsersTeamsFunction)
                    .doFinally(mFinalTenMinuteAction)
                    .subscribe();

        } else {
            mHandler.postDelayed(mTenMinuteRefreshRunnable, TEN_MINUTE_CACHE_EXPIRY_MILLIS + 1);
        }
    }

    private void refreshSettings() {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        long lastCacheTimestampMillis = mSharedPrefsManager.getLong(SharedPreferencesManager.POLLED_SETTINGS_LAST_CACHE_TIMESTAMP, 0);
        long lastCacheExpirationMillis = mCalendarManager.getNowMillis() - CACHE_EXPIRY_MILLIS;

        if (lastCacheTimestampMillis < lastCacheExpirationMillis &&
                mConnectionStateManager.isInternetConnected() &&
                !TextUtils.isEmpty(mSessionManager.getAccessDeviceTypeUrl()) &&
                !mConnectionStateManager.isCallActive()) {
            //mMobileConfigRepository.getMobileConfig()
                    //.flatMap(mUserDetailsFunction)
                    //.flatMap(mAccessDeviceFunction)
                    //.flatMap(mFeatureAccessCodesFunction)
                    //.flatMap(mServiceSettingsFunction)
                    //.flatMap(mProductsFunction)
            mAuthenticationRepository.getAuthenticationResponseEvent(mSessionManager.getUsername())
                    .flatMap(mPresenceFunction)
                    .flatMap(mPresencePingFunction)
                    .doFinally(mFinalAction)
                    .subscribe(new DisposableSingleObserver<RxEvents.PresenceSentResponseEvent>() {
                        @Override
                        public void onSuccess(RxEvents.PresenceSentResponseEvent presenceSentResponseEvent) {
                            if (mSessionManager.isNextivaConnectEnabled()) {
                                mConversationRepository.fetchVoiceConversationMessages().subscribe();
                                mPlatformContactsRepository.fetchContacts(false, () -> null, () -> null);
                            } else {
                                mCallManagementRepository.getAllCallLogEntries()
                                        .subscribe(new DisposableSingleObserver<RxEvents.CallHistoryResponseEvent>() {
                                            @Override
                                            public void onSuccess(RxEvents.CallHistoryResponseEvent callHistoryResponseEvent) {
                                                dispose();
                                            }

                                            @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
                                            @Override
                                            public void onError(Throwable e) {
                                                if (e instanceof HttpException) {
                                                    http401ErrorCount++;
                                                    LogUtil.d("NextivaPollingManager", "HTTP 401 Error Count: " + http401ErrorCount);
                                                }
                                                dispose();
                                            }
                                        });
                            }
                        }

                        @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
                        @Override
                        public void onError(Throwable e) {
                            if (e instanceof HttpException) {
                                http401ErrorCount++;
                                LogUtil.d("NextivaPollingManager", "HTTP 401 Error Count: " + http401ErrorCount);
                            }
                            dispose();
                        }
                    });

        } else {
            mHandler.postDelayed(mRunnable, CACHE_EXPIRY_MILLIS + 1);
        }
    }

    @Override
    public void refreshCallHistory() {
        if (mSessionManager.isNextivaConnectEnabled()) {
            mConversationRepository.fetchVoiceConversationMessages()
                    .subscribe();

            if(mContext != null) mSessionManager.updateNotificationsCount(mConversationRepository, mContext);

        } else {
            mCallManagementRepository.getAllCallLogEntries()
                    .doFinally(mFinalAction)
                    .subscribe(new DisposableSingleObserver<RxEvents.CallHistoryResponseEvent>() {
                        @Override
                        public void onSuccess(RxEvents.CallHistoryResponseEvent callHistoryResponseEvent) {
                            dispose();
                        }

                        @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
                        @Override
                        public void onError(Throwable e) {
                            if (e instanceof HttpException) {
                                http401ErrorCount++;
                                LogUtil.d("NextivaPollingManager", "HTTP 401 Error Count: " + http401ErrorCount);
                            }
                            dispose();
                        }
                    });
        }
    }

    @Override
    public void onPause() {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        mHandler.removeCallbacks(mRunnable);
        mHandler.removeCallbacks(mTenMinuteRefreshRunnable);
    }

    @Override
    public void onResume(@NonNull Context context) {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        mHandler.removeCallbacks(mRunnable);
        mHandler.post(mRunnable);

        mHandler.removeCallbacks(mTenMinuteRefreshRunnable);
        mHandler.post(mTenMinuteRefreshRunnable);

        mContext = context;

        if (!mSessionManager.isNextivaConnectEnabled()) {
            mSmsRespository.getSmsConversations().subscribe();
        }
    }
    // --------------------------------------------------------------------------------------------
}

 */