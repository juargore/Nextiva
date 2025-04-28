/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers.apimanagers;

import static com.nextiva.nextivaapp.android.constants.Enums.Logging.UserDatas.IS_AUTH_DEVICE;
import static com.nextiva.nextivaapp.android.constants.Enums.Logging.UserDatas.IS_AUTH_PASSWORD;
import static com.nextiva.nextivaapp.android.constants.Enums.Logging.UserDatas.IS_AUTH_USERNAME;
import static com.nextiva.nextivaapp.android.constants.Enums.ResponseCodes.ClientFailureResponses.BAD_REQUEST;
import static com.nextiva.nextivaapp.android.constants.Enums.ResponseCodes.ClientFailureResponses.UNAUTHORIZED;

import android.app.Application;
import android.os.Build;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nextiva.nextivaapp.android.BuildConfig;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.DbManager;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.AuthenticationRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.CallManagementRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ContactManagementRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository;
import com.nextiva.nextivaapp.android.managers.interfaces.CallSettingsManager;
import com.nextiva.nextivaapp.android.managers.interfaces.ConfigManager;
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager;
import com.nextiva.nextivaapp.android.managers.interfaces.NetManager;
import com.nextiva.nextivaapp.android.managers.interfaces.PushNotificationManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager;
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager;
import com.nextiva.nextivaapp.android.models.AccessDevice;
import com.nextiva.nextivaapp.android.models.CallCenter;
import com.nextiva.nextivaapp.android.models.CallLogEntry;
import com.nextiva.nextivaapp.android.models.FeatureAccessCode;
import com.nextiva.nextivaapp.android.models.FeatureAccessCodes;
import com.nextiva.nextivaapp.android.models.NextivaAnywhereLocation;
import com.nextiva.nextivaapp.android.models.NextivaContact;
import com.nextiva.nextivaapp.android.models.Service;
import com.nextiva.nextivaapp.android.models.ServiceSettings;
import com.nextiva.nextivaapp.android.models.UserDetails;
import com.nextiva.nextivaapp.android.models.net.broadsoft.BroadsoftCallThroughResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.BroadsoftErrorResponseBody;
import com.nextiva.nextivaapp.android.models.net.broadsoft.BroadsoftFeatureAccessCode;
import com.nextiva.nextivaapp.android.models.net.broadsoft.BroadsoftFeatureAccessCodesResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.BroadsoftProfileResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.BroadsoftServiceSettingsResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.BroadsoftVoicemailMessageSummaryDetails;
import com.nextiva.nextivaapp.android.models.net.broadsoft.BroadsoftVoicemailMessageSummaryResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.accessdevices.BroadsoftAccessDevicesResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.accessdevices.BroadsoftAllowTerminationPutBody;
import com.nextiva.nextivaapp.android.models.net.broadsoft.calllogs.BroadsoftAllCallLogsResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.calls.BroadsoftCall;
import com.nextiva.nextivaapp.android.models.net.broadsoft.calls.BroadsoftCallDetails;
import com.nextiva.nextivaapp.android.models.net.broadsoft.calls.BroadsoftCallsResponseBody;
import com.nextiva.nextivaapp.android.models.net.broadsoft.conference.Conference;
import com.nextiva.nextivaapp.android.models.net.broadsoft.enterprisecontacts.BroadsoftEnterprise;
import com.nextiva.nextivaapp.android.models.net.broadsoft.notifications.DeviceToken;
import com.nextiva.nextivaapp.android.models.net.broadsoft.notifications.PushNotificationRegistrationBody;
import com.nextiva.nextivaapp.android.models.net.broadsoft.notifications.response.PushNotificationsResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.notifications.response.PushNotificationsResponseDetails;
import com.nextiva.nextivaapp.android.models.net.broadsoft.notifications.response.PushNotificationsResponseDeviceToken;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftBroadWorksAnywhereLocationBody;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftBroadWorksAnywhereLocationPostBody;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftCallCenter;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftCallCenterUnavailableCodes;
import com.nextiva.nextivaapp.android.models.net.broadsoft.voicemail.BroadsoftVoiceMessageDetailsResponse;
import com.nextiva.nextivaapp.android.net.buses.RxEvents;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.AuthenticationResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.CallBackCallResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.CallHistoryResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.CallThroughCallResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.DeleteAllCallsResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.DeleteCallResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.EnterpriseContactByImpIdResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.EnterpriseContactByNameResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.EnterpriseContactByNumberResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.FeatureAccessCodesResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.NextivaAnywhereLocationDeleteResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.NextivaAnywhereLocationGetResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.NextivaAnywhereLocationSaveResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.RegisterForPushNotificationsResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.ResetConferenceCallResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.ServiceSettingsGetResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.ServiceSettingsMapResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.ServiceSettingsPutResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.ServicesResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.UnregisterForPushNotificationsResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.UserDetailsResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.VoicemailMessageSummaryResponseEvent;
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider;
import com.nextiva.nextivaapp.android.util.BroadsoftUtil;
import com.nextiva.nextivaapp.android.util.CallUtil;
import com.nextiva.nextivaapp.android.util.LogUtil;
import com.nextiva.nextivaapp.android.util.StringUtil;
import com.nextiva.nextivaapp.android.util.XmlUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Lazy;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import retrofit2.HttpException;
import retrofit2.Response;

/**
 * Created by adammacdonald on 2/14/18.
 */

@Singleton
public class BroadsoftUserApiManager extends BaseApiManager implements
        AuthenticationRepository,
        UserRepository,
        CallManagementRepository,
        ContactManagementRepository {

    private static final int MAX_RETRIES = 2;

    private static final String SORT_COLUMN_FIRST_NAME = "firstName/i";
    private static final String CASE_INSENSITIVE_SEARCH_PARAM = "%1$s/i";
    private static final int RESULTS_PER_PAGE = 50;
    private static final BiFunction<Throwable, Integer, Integer> sRetryApiCallBiFunction =
            (throwable, attempt) -> {
                FirebaseCrashlytics.getInstance().recordException(throwable);
                return attempt;
            };
    private static final Function<Observable<? extends Throwable>, Observable<?>> sRetryApiCallFunction =
            throwableObservable -> throwableObservable.zipWith(Observable.range(1, MAX_RETRIES), sRetryApiCallBiFunction);
    private final SessionManager mSessionManager;
    private final DbManager mDbManager;
    private final SchedulerProvider mSchedulerProvider;
    private final SettingsManager mSettingsManager;
    private final PushNotificationManager mPushNotificationManager;
    private final Lazy<CallSettingsManager> mLazyCallSettingsManager;
    private final ConfigManager mConfigManager;
    private final NetManager mNetManager;
    private String mPreviousCallThrough = "";
    private String mUserPhoneNumber = "";
    private String mRecipientPhoneNumber = "";

    @Inject
    public BroadsoftUserApiManager(Application application,
                                   SessionManager sessionManager,
                                   DbManager dbManager,
                                   SchedulerProvider schedulerProvider,
                                   SettingsManager settingsManager,
                                   LogManager logManager,
                                   PushNotificationManager pushNotificationManager,
                                   Lazy<CallSettingsManager> lazyCallSettingsManager,
                                   ConfigManager configManager,
                                   NetManager netManager) {

        super(application, logManager);

        mSessionManager = sessionManager;
        mDbManager = dbManager;
        mSchedulerProvider = schedulerProvider;
        mSettingsManager = settingsManager;
        mLazyCallSettingsManager = lazyCallSettingsManager;
        mPushNotificationManager = pushNotificationManager;
        mConfigManager = configManager;
        mNetManager = netManager;
    }

    private void logUsernameNull(String methodName) {
        //TODO This is used to log the user's username to try to debug the issues we get from the API which clear our user
        if (BuildConfig.DEBUG) {
            mLogManager.logToFile(Enums.Logging.STATE_INFO, "Method: " + methodName + "; Username: " + mSessionManager.getUsername() + "; Password Is Null: " + (mSessionManager.getPassword() == null));
        }
    }

    // --------------------------------------------------------------------------------------------
    // AuthenticationRepository Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public Single<AuthenticationResponseEvent> authenticateUser(
            final String username,
            final String password) {

        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);


        setSessionManagerUsernamePassword(username, password);

        if (mNetManager.getBroadsoftUserApi() == null || username == null || password == null) {
            return Single.just(new AuthenticationResponseEvent(false, false));
        }
        Single<AuthenticationResponseEvent> firstAttemptAuthenticationResponseEventSingle = getAuthenticationResponseEvent(username);
        return firstAttemptAuthenticationResponseEventSingle.flatMap(
                firstResponse -> {
                    if (!firstResponse.isSuccessful()) {
                        Exception exception = new Exception(firstResponse.toString());
                        FirebaseCrashlytics.getInstance().recordException(exception);
                        Single<AuthenticationResponseEvent> secondAttemptAuthenticationResponseEventSingle = getAuthenticationResponseEvent(username);
                        return secondAttemptAuthenticationResponseEventSingle.flatMap(
                                secondResponse -> {
                                    if (!secondResponse.isSuccessful()) {
                                        Exception secondException = new Exception(secondResponse.toString());
                                        FirebaseCrashlytics.getInstance().recordException(secondException);
                                        return getAuthenticationResponseEvent(username);

                                    } else {
                                        return secondAttemptAuthenticationResponseEventSingle;
                                    }
                                });
                    }

                    return firstAttemptAuthenticationResponseEventSingle;
                }
        );

    }

    @Override
    public Single<AuthenticationResponseEvent> getAuthenticationResponseEvent(final String username) {
        return mNetManager.getBroadsoftUserApi().getAccessDevices(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        username)
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    BroadsoftAccessDevicesResponse broadsoftAccessDevicesResponse = response.body();

                    if (response.isSuccessful() && broadsoftAccessDevicesResponse != null) {
                        AccessDevice accessDevice = broadsoftAccessDevicesResponse.getAccessDevice(Enums.AccessDeviceTypes.MOBILE);

                        logServerSuccess(response);

                        if (accessDevice != null && accessDevice.getDeviceTypeUrl() != null && !accessDevice.getDeviceTypeUrl().contains("null")) {
                            setSessionManagerAccessDeviceUserValues(accessDevice);

                            return new AuthenticationResponseEvent(true, true);

                        } else {
                            FirebaseCrashlytics.getInstance().setCustomKey(IS_AUTH_DEVICE, (accessDevice == null));

                            if (accessDevice != null) {
                                FirebaseCrashlytics.getInstance().setCustomKey(IS_AUTH_USERNAME, !(accessDevice.getUsername() != null && !accessDevice.getUsername().isEmpty()));
                                FirebaseCrashlytics.getInstance().setCustomKey(IS_AUTH_PASSWORD, !(accessDevice.getPassword() != null && !accessDevice.getPassword().isEmpty()));
                            }

                            FirebaseCrashlytics.getInstance().recordException(new Exception("Failed to Login, Access Device Exception"));
                            return new AuthenticationResponseEvent(true, false);
                        }
                    }

                    logServerParseFailure(response);
                    nullSessionMangerUserValues();

                    FirebaseCrashlytics.getInstance().recordException(new Exception("Failed to Login, Authentication Response Exception: " + response));
                    Toast.makeText(mApplication.getBaseContext(), mApplication.getString(R.string.login_error_toast_message_attempting_authenticate), Toast.LENGTH_LONG).show();

                    return new AuthenticationResponseEvent(false, false);

                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);

                    nullSessionMangerUserValues();

                    return new AuthenticationResponseEvent(false, false);
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<Boolean> getPollingDeviceSettings(final String username) {
        return mNetManager.getBroadsoftUserApi().getAccessDevices(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        username)
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {

                    BroadsoftAccessDevicesResponse broadsoftAccessDevicesResponse = response.body();

                    if (response.isSuccessful() && broadsoftAccessDevicesResponse != null) {
                        AccessDevice accessDevice = broadsoftAccessDevicesResponse.getAccessDevice(Enums.AccessDeviceTypes.MOBILE);
                        if (accessDevice != null && accessDevice.getDeviceTypeUrl() != null && !accessDevice.getDeviceTypeUrl().contains("null")) {
                            logServerSuccess(response);
                            setSessionManagerAllowTermination(accessDevice);
                            return true;
                        }
                    }
                    return false;

                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return false;
                })
                .observeOn(mSchedulerProvider.ui());
    }

    private void setSessionManagerUsernamePassword(String username, String password) {
        mSessionManager.setUsername(username);
        mSessionManager.setLastLoggedUsername(username);
        mSessionManager.setPassword(password);
        mSessionManager.setLastLoggedPassword(password);

    }

    private void setSessionManagerAccessDeviceUserValues(@Nullable AccessDevice accessDevice) {
        mSessionManager.setAccessDeviceUsername(accessDevice != null ? accessDevice.getUsername() : null);
        mSessionManager.setAccessDevicePassword(accessDevice != null ? accessDevice.getPassword() : null);
        mSessionManager.setAccessDeviceVersion(accessDevice != null ? accessDevice.getVersion() : null);
        mSessionManager.setAccessDeviceTypeUrl(accessDevice != null ? accessDevice.getDeviceTypeUrl() : null);
        mSessionManager.setAccessDeviceLinePort(accessDevice != null ? accessDevice.getLinePort() : null);
        mSessionManager.setAllowTermination(accessDevice != null && accessDevice.isAllowTermination());
    }

    private void setSessionManagerAllowTermination(@Nullable AccessDevice accessDevice) {
        mSessionManager.setAllowTermination(accessDevice != null && accessDevice.isAllowTermination());
    }

    private void nullSessionMangerUserValues() {
        mSessionManager.setUsername(null);
        mSessionManager.setPassword(null);
        mSessionManager.setLastLoggedUsername(null);
        mSessionManager.setLastLoggedPassword(null);
        setSessionManagerAccessDeviceUserValues(null);
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // UserRepository Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public Single<UserDetailsResponseEvent> getUserDetails() {
        logUsernameNull("getUserDetails");

        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null || TextUtils.isEmpty(mSessionManager.getUsername()) || TextUtils.isEmpty(mSessionManager.getPassword())) {
            return Single.just(new UserDetailsResponseEvent(false, null));
        }

        return mNetManager.getBroadsoftUserApi().getProfile(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        mSessionManager.getUsername())
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    BroadsoftProfileResponse broadsoftProfileResponse = response.body();

                    UserDetails userDetails = BroadsoftUtil.getUserDetails(broadsoftProfileResponse);

                    if(response.code() == UNAUTHORIZED)
                    {
                        return new UserDetailsResponseEvent(false, null);
                    }

                    if (response.isSuccessful() && userDetails != null) {
                        logServerSuccess(response);

                        mSessionManager.setUserDetails(userDetails);
                        return new UserDetailsResponseEvent(true, userDetails);

                    } else {
                        logServerParseFailure(response);
                        Exception exception = new Exception(response.toString());
                        FirebaseCrashlytics.getInstance().recordException(exception);
                        mLogManager.logToFile(Enums.Logging.STATE_FAILURE, "Error Pulling User Details; Nullifying User Details");
                        return new UserDetailsResponseEvent(false, null);
                    }
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    mLogManager.logToFile(Enums.Logging.STATE_FAILURE, "Error Pulling User Details; Nullifying User Details");
                    return new UserDetailsResponseEvent(false, null);
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<ServiceSettingsMapResponseEvent> getServiceSettingsFiltered(
            @Enums.Service.Type final String[] filterServiceSettings) {

        logUsernameNull("getServiceSettingsFiltered");

        if (mNetManager.getBroadsoftUserApi() == null || TextUtils.isEmpty(mSessionManager.getUsername()) || TextUtils.isEmpty(mSessionManager.getPassword())) {
            return Single.just(new ServiceSettingsMapResponseEvent(false, null));
        }

        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        final List<String> filteredServiceSettingsList = Arrays.asList(filterServiceSettings);

        return mNetManager.getBroadsoftUserApi().getServices(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        mSessionManager.getUsername())
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {

                    if(response.code() == UNAUTHORIZED)
                    {
                        return new ServicesResponseEvent(false, null);
                    }

                    if (response.body() != null) {
                        return new ServicesResponseEvent(true, BroadsoftUtil.getServices(response.body()));
                    }

                    return new ServicesResponseEvent(false, null);
                })
                .onErrorReturnItem(new ServicesResponseEvent(false, null))
                .flatMap((Function<ServicesResponseEvent, ObservableSource<HashMap<String, ServiceSettings>>>) event -> {
                    if (!event.isSuccessful() || event.getServices() == null) {
                        return Observable.just(new HashMap<>());
                    }

                    List<Observable<ServiceSettingsGetResponseEvent>> observables = new ArrayList<>();

                    if (event.isSuccessful() && event.getServices() != null) {
                        for (Service service : event.getServices()) {
                            if (service.getType() != null &&
                                    service.getUri() != null &&
                                    filteredServiceSettingsList.contains(service.getType()) &&
                                    !TextUtils.isEmpty(service.getType()) &&
                                    !TextUtils.isEmpty(service.getUri())) {
                                observables.add(getSingleServiceSettings(service.getType(), service.getUri())
                                                        .retryWhen(sRetryApiCallFunction)
                                                        .subscribeOn(mSchedulerProvider.newThread())
                                                        .onErrorReturn(throwable -> {
                                                            FirebaseCrashlytics.getInstance().recordException(throwable);
                                                            return new ServiceSettingsGetResponseEvent(false, null);
                                                        }));
                            }
                        }
                    }

                    return Observable.zip(observables,
                                          objects -> {
                                              HashMap<String, ServiceSettings> serviceSettingsMap = new HashMap<>();

                                              if (objects != null) {
                                                  ServiceSettingsGetResponseEvent event1;

                                                  for (Object object : objects) {
                                                      if (object instanceof ServiceSettingsGetResponseEvent) {
                                                          event1 = (ServiceSettingsGetResponseEvent) object;

                                                          if (event1.isSuccessful() && event1.getServiceSettings() != null && !TextUtils.isEmpty(event1.getServiceSettings().getType())) {
                                                              serviceSettingsMap.put(event1.getServiceSettings().getType(), event1.getServiceSettings());
                                                          }
                                                      }
                                                  }
                                              }

                                              return serviceSettingsMap;
                                          });
                })
                .onErrorReturnItem(new HashMap<>())
                .flatMap((Function<HashMap<String, ServiceSettings>, ObservableSource<ServiceSettingsMapResponseEvent>>) serviceSettingsMap -> {
                    if (!serviceSettingsMap.isEmpty()) {
                        mLogManager.logToFile(Enums.Logging.STATE_INFO, "Success has service settings list");
                    } else {
                        mLogManager.logToFile(Enums.Logging.STATE_ERROR, "Success empty service settings list");
                        return Observable.just(new ServiceSettingsMapResponseEvent(false, serviceSettingsMap));
                    }

                    boolean requestedRemoteOffice = false;
                    boolean requestedNextivaAnywhere = false;

                    for (String filterServiceSetting : filterServiceSettings) {
                        if (TextUtils.equals(filterServiceSetting, Enums.Service.TYPE_REMOTE_OFFICE)) {
                            requestedRemoteOffice = true;
                        } else if (TextUtils.equals(filterServiceSetting, Enums.Service.TYPE_BROADWORKS_ANYWHERE)) {
                            requestedNextivaAnywhere = true;
                        }

                        if (!serviceSettingsMap.containsKey(filterServiceSetting) || serviceSettingsMap.get(filterServiceSetting) == null) {
                            mLazyCallSettingsManager.get().putServiceSetting(filterServiceSetting, null);
                        }
                    }

                    if (requestedRemoteOffice) {
                        mSessionManager.setRemoteOfficeServiceSettings(serviceSettingsMap.get(Enums.Service.TYPE_REMOTE_OFFICE));
                    }
                    if (requestedNextivaAnywhere) {
                        mSessionManager.setNextivaAnywhereServiceSettings(serviceSettingsMap.get(Enums.Service.TYPE_BROADWORKS_ANYWHERE));
                    }

                    boolean callBackConflict = mSettingsManager.getDialingService() == Enums.Service.DialingServiceTypes.CALL_BACK &&
                            !mSessionManager.getIsCallBackEnabled(mSessionManager.getRemoteOfficeServiceSettings(), mSessionManager.getNextivaAnywhereServiceSettings());
                    boolean callThroughConflict = mSettingsManager.getDialingService() == Enums.Service.DialingServiceTypes.CALL_THROUGH &&
                            !mSessionManager.getIsCallThroughEnabled(mSessionManager.getNextivaAnywhereServiceSettings(), mSettingsManager.getPhoneNumber());

                    if (callBackConflict || callThroughConflict) {
                        mSettingsManager.setDialingService(Enums.Service.DialingServiceTypes.VOIP);
                    }

                    return Observable.just(new ServiceSettingsMapResponseEvent(true, serviceSettingsMap));
                })
                .single(new ServiceSettingsMapResponseEvent(true, null))
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return new ServiceSettingsMapResponseEvent(false, null);
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Observable<ServiceSettingsGetResponseEvent> getSingleServiceSettings(
            @NonNull final String serviceType,
            @NonNull String url) {

        logUsernameNull("getSingleServiceSettings");

        if (mNetManager.getBroadsoftUserApi() == null) {
            return Observable.never();
        }

        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (!TextUtils.isEmpty(url) && url.startsWith("/")) {
            url = url.substring(1);
        }

        if (!url.isEmpty()) {
            final String finalUrl = url;

            return mNetManager.getBroadsoftUserApi().getServiceSettings(
                            getAppVersionHeader(),
                            mSessionManager.getAuthorizationHeader(),
                            finalUrl)
                    .subscribeOn(mSchedulerProvider.io())
                    .flatMap((Function<Response<BroadsoftServiceSettingsResponse>, Single<ServiceSettingsGetResponseEvent>>) response -> {
                        if (response.isSuccessful()) {
                            logServerSuccess(response);

                            if (response.body() != null) {
                                ServiceSettings serviceSettings = BroadsoftUtil.getServiceSettings(serviceType, finalUrl, response.body());

                                if (TextUtils.equals(serviceType, Enums.Service.TYPE_REMOTE_OFFICE)) {
                                    mSessionManager.setRemoteOfficeServiceSettings(serviceSettings);

                                } else if (TextUtils.equals(serviceType, Enums.Service.TYPE_BROADWORKS_ANYWHERE)) {
                                    mSessionManager.setNextivaAnywhereServiceSettings(serviceSettings);
                                }

                                mLazyCallSettingsManager.get().putServiceSetting(serviceType, serviceSettings);

                                return Single.just(new ServiceSettingsGetResponseEvent(true, serviceSettings));

                            } else {
                                return Single.just(new ServiceSettingsGetResponseEvent(false, null));
                            }

                        } else {
                            try {
                                throw new HttpException(response);
                            } catch (HttpException exception) {
                                logHttpException(exception);
                                return Single.just(new ServiceSettingsGetResponseEvent(false, null));
                            }
                        }
                    })
                    .toObservable()
                    .onErrorReturn(throwable -> {
                        logServerResponseError(throwable);
                        return new ServiceSettingsGetResponseEvent(false, null);
                    })
                    .observeOn(mSchedulerProvider.ui());
        }

        return null;
    }

    @Override
    public Single<ServiceSettingsPutResponseEvent> putServiceSettings(
            @NonNull final ServiceSettings serviceSettings) {

        logUsernameNull("putServiceSettings");

        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null) {
            return Single.never();
        }

        final String url;

        if (!TextUtils.isEmpty(serviceSettings.getUri()) && serviceSettings.getUri().startsWith("/")) {
            url = serviceSettings.getUri().substring(1);
        } else {
            url = serviceSettings.getUri();
        }

        return mNetManager.getBroadsoftUserApi().putServiceSettings(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        url,
                        BroadsoftUtil.getBroadsoftBaseServiceSettings(serviceSettings))
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    if (response.isSuccessful()) {
                        logServerSuccess(response);

                        if (TextUtils.equals(serviceSettings.getType(), Enums.Service.TYPE_REMOTE_OFFICE)) {
                            mSessionManager.setRemoteOfficeServiceSettings(serviceSettings);

                        } else if (TextUtils.equals(serviceSettings.getType(), Enums.Service.TYPE_BROADWORKS_ANYWHERE)) {
                            mSessionManager.setNextivaAnywhereServiceSettings(serviceSettings);
                        }

                        mLazyCallSettingsManager.get().putServiceSetting(serviceSettings.getType(), serviceSettings);

                        return new ServiceSettingsPutResponseEvent(true, null, serviceSettings);

                    } else {
                        logServerResponseError(new HttpException(response));

                        if (response.errorBody() != null) {
                            return new ServiceSettingsPutResponseEvent(false,
                                                                       XmlUtil.getBroadsoftErrorResponseBody(response.errorBody().string()),
                                                                       serviceSettings);

                        } else {
                            return new ServiceSettingsPutResponseEvent(false, null, null);
                        }
                    }
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return new ServiceSettingsPutResponseEvent(false, null, null);
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<Boolean> setAllowTermination(boolean allowTermination) {

        logUsernameNull("allowTermination");

        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null) {
            return Single.never();
        }

        return mNetManager.getBroadsoftUserApi().setAllowTermination(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        mSessionManager.getUsername(),
                        mSessionManager.getAccessDeviceLinePort(),
                        new BroadsoftAllowTerminationPutBody(allowTermination))
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    if (response.isSuccessful()) {
                        logServerSuccess(response);
                        mSessionManager.setAllowTermination(allowTermination);
                        return true;

                    } else {
                        logServerResponseError(new HttpException(response));
                        return false;
                    }
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return false;
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<NextivaAnywhereLocationGetResponseEvent> getNextivaAnywhereLocation(
            @NonNull final String phoneNumber) {

        logUsernameNull("getNextivaAnywhereLocation");

        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null) {
            return Single.never();
        }

        return mNetManager.getBroadsoftUserApi().getNextivaAnywhereLocation(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        mSessionManager.getUsername(),
                        phoneNumber)
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    if (response.isSuccessful()) {
                        logServerSuccess(response);

                        if (response.body() != null) {
                            return new NextivaAnywhereLocationGetResponseEvent(
                                    true,
                                    BroadsoftUtil.getNextivaAnywhereLocation(response.body(), response.body().getDescription()));
                        } else {
                            return new NextivaAnywhereLocationGetResponseEvent(false, null);
                        }

                    } else {
                        try {
                            throw new HttpException(response);
                        } catch (HttpException exception) {
                            logHttpException(exception);
                            return new NextivaAnywhereLocationGetResponseEvent(false, null);
                        }
                    }
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return new NextivaAnywhereLocationGetResponseEvent(false, null);
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<NextivaAnywhereLocationSaveResponseEvent> putNextivaAnywhereLocation(
            @NonNull final ServiceSettings nextivaAnywhereServiceSettings,
            @NonNull final NextivaAnywhereLocation location,
            @NonNull final String oldPhoneNumber) {

        logUsernameNull("putNextivaAnywhereLocation");

        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null) {
            return Single.never();
        }

        return mNetManager.getBroadsoftUserApi().putNextivaAnywhereLocation(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        mSessionManager.getUsername(),
                        oldPhoneNumber,
                        new BroadsoftBroadWorksAnywhereLocationBody(location))
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    if (response.isSuccessful()) {
                        logServerSuccess(response);
                        return new NextivaAnywhereLocationSaveResponseEvent(true, null, nextivaAnywhereServiceSettings, location, oldPhoneNumber);

                    } else {
                        logServerResponseError(new HttpException(response));

                        if (response.errorBody() != null) {
                            return new NextivaAnywhereLocationSaveResponseEvent(false,
                                                                                XmlUtil.getBroadsoftErrorResponseBody(response.errorBody().string()),
                                                                                nextivaAnywhereServiceSettings,
                                                                                location,
                                                                                null);

                        }

                        return new NextivaAnywhereLocationSaveResponseEvent(false, null, nextivaAnywhereServiceSettings, location, oldPhoneNumber);
                    }
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return new NextivaAnywhereLocationSaveResponseEvent(false, null, nextivaAnywhereServiceSettings, location, oldPhoneNumber);
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<NextivaAnywhereLocationSaveResponseEvent> postNextivaAnywhereLocation(
            @NonNull final ServiceSettings nextivaAnywhereServiceSettings,
            @NonNull final NextivaAnywhereLocation location) {

        logUsernameNull("postNextivaAnywhereLocation");

        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null) {
            return Single.never();
        }

        //Looks like Error Code 8251 indicates dupe <?xml version="1.0" encoding="ISO-8859-1"?> <ErrorInfo xmlns="http://schema.broadsoft.com/xsi"><summary> Phone Number already exists +1123456789.</summary><summaryEnglish> Phone Number already exists +1123456789.</summaryEnglish><errorCode>8251</errorCode></ErrorInfo>

        return mNetManager.getBroadsoftUserApi().postNextivaAnywhereLocation(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        mSessionManager.getUsername(),
                        new BroadsoftBroadWorksAnywhereLocationPostBody(location))
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    if (response.isSuccessful()) {
                        logServerSuccess(response);
                        return new NextivaAnywhereLocationSaveResponseEvent(true, null, nextivaAnywhereServiceSettings, location, null);

                    } else {
                        logServerParseFailure(response);

                        if (response.errorBody() != null) {
                            return new NextivaAnywhereLocationSaveResponseEvent(false,
                                                                                XmlUtil.getBroadsoftErrorResponseBody(response.errorBody().string()),
                                                                                nextivaAnywhereServiceSettings,
                                                                                location,
                                                                                null);
                        }

                        return new NextivaAnywhereLocationSaveResponseEvent(false, null, nextivaAnywhereServiceSettings, location, null);
                    }
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return new NextivaAnywhereLocationSaveResponseEvent(false, null, nextivaAnywhereServiceSettings, location, null);
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<RegisterForPushNotificationsResponseEvent> registerForPushNotifications(String firebaseToken) {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null || TextUtils.isEmpty(firebaseToken)) {
            return Single.never();
        }

        String pushNotificationRegistrationId;

        if (TextUtils.isEmpty(mSessionManager.getPushNotificationRegistrationId())) {
            pushNotificationRegistrationId = UUID.randomUUID().toString().replace("-", "");
            mSessionManager.setPushNotificationRegistrationId(pushNotificationRegistrationId);

        } else {
            pushNotificationRegistrationId = mSessionManager.getPushNotificationRegistrationId();
        }

        ArrayList<String> events = new ArrayList<>();
        events.add(Enums.PushNotifications.Events.NEW_CALL);
        events.add(Enums.PushNotifications.Events.CALL_UPDATED);
        events.add(Enums.PushNotifications.Events.RING_SPLASH);
        events.add(Enums.PushNotifications.Events.MESSAGE_WAITING_INDICATOR);
        events.add(Enums.PushNotifications.Events.DEREGISTERED);

        String newToken = mPushNotificationManager.getToken(); // use push notification manager's token
        if(TextUtils.isEmpty(newToken)) {
            newToken = firebaseToken; // use token from function's param
        }

        ArrayList<DeviceToken> deviceTokenList = new ArrayList<>();
        deviceTokenList.add(new DeviceToken(newToken, events));

        PushNotificationRegistrationBody body = new PushNotificationRegistrationBody(mApplication.getPackageName(),
                                                                                     BuildConfig.VERSION_NAME,
                                                                                     pushNotificationRegistrationId,
                                                                                     "Android",
                                                                                     String.valueOf(Build.VERSION.SDK_INT),
                                                                                     deviceTokenList);
        return mNetManager.getBroadsoftUserApi().registerForPushNotifications(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        mSessionManager.getUsername(),
                        body)
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .map(responseBodyResponse -> new RegisterForPushNotificationsResponseEvent(responseBodyResponse.isSuccessful()))
                .onErrorReturn(throwable -> {
                    FirebaseCrashlytics.getInstance().recordException(throwable);
                    return new RegisterForPushNotificationsResponseEvent(false);
                });
    }

    @Override
    public Single<UnregisterForPushNotificationsResponseEvent> unregisterForPushNotifications() {
        return unregisterForPushNotifications(null, null);
    }

    private Single<UnregisterForPushNotificationsResponseEvent> unregisterForPushNotifications(String token, String registrationId) {


        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null) {
            return Single.never();
        }

        return mNetManager.getBroadsoftUserApi().unregisterForPushNotifications(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        mSessionManager.getUsername(),
                        TextUtils.isEmpty(registrationId) ? mSessionManager.getPushNotificationRegistrationId() : registrationId,
                        TextUtils.isEmpty(token) ? mPushNotificationManager.getToken() : token)
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .map(responseBodyResponse -> {
                    mSessionManager.setPushNotificationRegistrationId(null);
                    return new UnregisterForPushNotificationsResponseEvent(responseBodyResponse.isSuccessful());
                })
                .onErrorReturn(throwable -> {
                    FirebaseCrashlytics.getInstance().recordException(throwable);
                    return new UnregisterForPushNotificationsResponseEvent(false);
                });
    }

    @Override
    public void removeExpiredPushNotificationRegistrations(CompositeDisposable compositeDisposable) {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null || TextUtils.isEmpty(mSessionManager.getUsername()) || TextUtils.isEmpty(mSessionManager.getPassword())) {
            return;
        }

        compositeDisposable.add(
                mNetManager.getBroadsoftUserApi().getPushNotificationRegistrations(
                                getAppVersionHeader(),
                                mSessionManager.getAuthorizationHeader(),
                                mSessionManager.getUsername())
                        .subscribeOn(mSchedulerProvider.io())
                        .observeOn(mSchedulerProvider.ui())
                        .subscribe(response -> {

                            if(response.code() == UNAUTHORIZED)
                            {
                                return;
                            }

                            if (response.isSuccessful()) {
                                logServerSuccess(response);

                                if (response.body() != null && response.body().getPushNotificationRegistrations() != null) {
                                    for (PushNotificationsResponseDetails details : response.body().getPushNotificationRegistrations()) {

                                        if (details.getDeviceTokenList() != null) {
                                            mLogManager.logToFile(Enums.Logging.STATE_INFO, "Total available tokens: " + details.getDeviceTokenList().size());
                                        }

                                        if (details.getDeviceTokenList() != null &&
                                                !details.getDeviceTokenList().isEmpty() &&
                                                !TextUtils.isEmpty(mPushNotificationManager.getToken()) &&
                                                !TextUtils.equals(mPushNotificationManager.getToken(), details.getDeviceTokenList().get(0).getToken()) &&
                                                !TextUtils.isEmpty(details.getApplicationVersion()) &&
                                                !details.getApplicationVersion().contains("iPad") &&
                                                details.getDeviceTokenList().size() > 14
                                        ) {

                                            mLogManager.logToFile(Enums.Logging.STATE_INFO, "Started unregisterForPushNotifications()");

                                            String selectedToken = details.getDeviceTokenList().get(0).getToken();

                                            if (selectedToken != null && !selectedToken.isEmpty()) {
                                                compositeDisposable.add(unregisterForPushNotifications(selectedToken, details.getRegistrationId())
                                                                                .subscribe(unregisterResponse -> {
                                                                                    if (unregisterResponse.isSuccessful()) {
                                                                                        mLogManager.logToFile(Enums.Logging.STATE_INFO, "Unregister token (Success): " + selectedToken);
                                                                                    } else {
                                                                                        mLogManager.logToFile(Enums.Logging.STATE_ERROR, "Unregister token (Error): " + selectedToken);
                                                                                    }
                                                                                }, this::logServerResponseError));
                                            } else {
                                                mLogManager.logToFile(Enums.Logging.STATE_INFO, "Selected token is null or empty");
                                            }
                                        }
                                    }
                                }
                            } else {
                                logServerParseFailure(response);
                            }
                        }, this::logServerResponseError));

        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_success);
    }

    @Override
    public Single<Boolean> doesPushNotificationExist() {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null) {
            return Single.just(false);
        }

        LogUtil.d("doesPushNotificationExist getAuthorizationHeader(): " + mSessionManager.getAuthorizationHeader());

        LogUtil.d("doesPushNotificationExist getUsername(): " + mSessionManager.getUsername());

        LogUtil.d("doesPushNotificationExist getPassword(): " + mSessionManager.getPassword());

        return mNetManager.getBroadsoftUserApi().getPushNotificationRegistrations(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        mSessionManager.getUsername())
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .flatMap(response -> response.isSuccessful() ? Single.just(doesPushNotificationExistInResponse(response.body())) : Single.just(false))
                .onErrorReturnItem(false);
    }

    private boolean doesPushNotificationExistInResponse(PushNotificationsResponse response) {
        boolean found = false;

        if (response != null && response.getPushNotificationRegistrations() != null) {
            for (PushNotificationsResponseDetails details : response.getPushNotificationRegistrations()) {
                if (details.getDeviceTokenList() != null &&
                        !details.getDeviceTokenList().isEmpty() &&
                        TextUtils.equals(mPushNotificationManager.getToken(), details.getDeviceTokenList().get(0).getToken())) {
                    found = true;
                }
            }
        }

        return found;
    }

    @Override
    public Single<NextivaAnywhereLocationDeleteResponseEvent> deleteNextivaAnywhereLocation(
            @NonNull final ServiceSettings nextivaAnywhereServiceSettings,
            @NonNull final NextivaAnywhereLocation location) {

        logUsernameNull("deleteNextivaAnywhereLocation");

        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null) {
            return Single.never();
        }

        return mNetManager.getBroadsoftUserApi().deleteNextivaAnywhereLocation(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        mSessionManager.getUsername(),
                        location.getPhoneNumber())
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    if (response.isSuccessful()) {
                        logServerSuccess(response);
                        return new NextivaAnywhereLocationDeleteResponseEvent(true, nextivaAnywhereServiceSettings, location);

                    } else {
                        logServerParseFailure(response);
                        return new NextivaAnywhereLocationDeleteResponseEvent(false, nextivaAnywhereServiceSettings, location);
                    }
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return new NextivaAnywhereLocationDeleteResponseEvent(false, nextivaAnywhereServiceSettings, location);
                })
                .observeOn(mSchedulerProvider.ui());
    }


    @Override
    public Single<RxEvents.BroadsoftCallCenterResponseEvent> getCallCenterService() {

        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        logUsernameNull("getCallCenterService");

        if (mNetManager.getBroadsoftUserApi() == null) {
            return Single.never();
        }

        return mNetManager.getBroadsoftUserApi().getCallCenter(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        mSessionManager.getUsername())
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                         if (response.isSuccessful()) {
                             logServerSuccess(response);

                             if (response.body() != null) {
                                 return new RxEvents.BroadsoftCallCenterResponseEvent(true, response.body());
                             }
                         } else {
                             /*
                             The endpoint returns 400 or 401 when the user is not part of the call center service.
                             In that specific case, don't log it as an issue in Crashlytics.
                             */
                             if (response.code() == 400 || response.code() == 401) {
                                 mLogManager.logToFile(Enums.Logging.STATE_INFO, "Call Center Service -> false");
                             } else {
                                 logServerParseFailure(response);
                             }
                         }
                         return new RxEvents.BroadsoftCallCenterResponseEvent(false, new BroadsoftCallCenter());
                     }
                )
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return new RxEvents.BroadsoftCallCenterResponseEvent(false, new BroadsoftCallCenter());
                })
                .observeOn(mSchedulerProvider.ui());
    }


    @Override
    public Single<RxEvents.BroadsoftCallCenterPutResponseEvent> putCallCenterService(CallCenter CallCenter) {

        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        logUsernameNull("putCallCenterService");

        if (mNetManager.getBroadsoftUserApi() == null) {
            return Single.never();
        }

        return mNetManager.getBroadsoftUserApi().putCallCenter(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        mSessionManager.getUsername(),
                        CallCenter)
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                         if (response.isSuccessful()) {
                             logServerSuccess(response);

                             if (response.body() != null) {

                                 return new RxEvents.BroadsoftCallCenterPutResponseEvent(true);
                             }


                         } else {
                             logServerParseFailure(response);
                         }
                         return new RxEvents.BroadsoftCallCenterPutResponseEvent(false);
                     }
                )
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return new RxEvents.BroadsoftCallCenterPutResponseEvent(false);
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<RxEvents.BroadsoftCallCenterUnavailableCodesResponseEvent> getCallCenterServiceUnavailableCodes() {

        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null || mSessionManager.getUserDetails() == null || mSessionManager.getUserDetails().getGroupId() == null) {
            return Single.never();
        }

        return mNetManager.getBroadsoftUserApi().getCallCenterUnavailableCodes(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        mSessionManager.getUserDetails().getGroupId(),
                        mSessionManager.getUserDetails().getServiceProvider())
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                         if (response.isSuccessful()) {
                             logServerSuccess(response);

                             if (response.body() != null) {
                                 return new RxEvents.BroadsoftCallCenterUnavailableCodesResponseEvent(true, response.body());
                             }


                         } else {
                             logServerParseFailure(response);
                         }
                         return new RxEvents.BroadsoftCallCenterUnavailableCodesResponseEvent(false, new BroadsoftCallCenterUnavailableCodes());
                     }
                )
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return new RxEvents.BroadsoftCallCenterUnavailableCodesResponseEvent(false, new BroadsoftCallCenterUnavailableCodes());
                })
                .observeOn(mSchedulerProvider.ui());
    }


    @Override
    public Single<RxEvents.BroadsoftMeetMeConferenceResponseEvent> getMeetMeConference() {

        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        logUsernameNull("getMeetMeConference");

        if (mNetManager.getBroadsoftUserApi() == null) {
            return Single.never();
        }

        return mNetManager.getBroadsoftUserApi().getMeetMeConference(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        mSessionManager.getUsername())
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                         if (response.isSuccessful()) {
                             logServerSuccess(response);

                             if (response.body() != null &&
                                     response.body().getUserBridge() != null &&
                                     response.body().getUserBridge().getBridgeId() != null &&
                                     response.body().getUserBridge().getPhoneNumber() != null) {
                                 return new RxEvents.BroadsoftMeetMeConferenceResponseEvent(true, response.body().getUserBridge().getPhoneNumber(), response.body().getUserBridge().getBridgeId());
                             }


                             return new RxEvents.BroadsoftMeetMeConferenceResponseEvent(true, "", "");

                         } else {
                             logServerParseFailure(response);
                             return new RxEvents.BroadsoftMeetMeConferenceResponseEvent(true, "", "");
                         }
                     }
                )
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return new RxEvents.BroadsoftMeetMeConferenceResponseEvent(false, "", "");
                })
                .observeOn(mSchedulerProvider.ui());
    }


    @Override
    public Single<RxEvents.BroadsoftMeetMeConferencingUserConferencesResponseEvent> getMeetMeConferencingUserConferences(String bridgeId) {

        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        logUsernameNull("getMeetMeConferencingUserConferences");

        if (mNetManager.getBroadsoftUserApi() == null) {
            return Single.never();
        }

        return mNetManager.getBroadsoftUserApi().getMeetMeConferencingUserConferences(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        mSessionManager.getUsername(),
                        bridgeId
                )
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                         if (response.isSuccessful()) {
                             logServerSuccess(response);

                             if (response.body() != null &&
                                     response.body().getUserConference() != null) {
                                 return new RxEvents.BroadsoftMeetMeConferencingUserConferencesResponseEvent(true, response.body().getUserConference());
                             }

                             return new RxEvents.BroadsoftMeetMeConferencingUserConferencesResponseEvent(true, new ArrayList<>());

                         } else {
                             logServerParseFailure(response);
                             return new RxEvents.BroadsoftMeetMeConferencingUserConferencesResponseEvent(true, new ArrayList<>());
                         }
                     }
                )
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return new RxEvents.BroadsoftMeetMeConferencingUserConferencesResponseEvent(false, new ArrayList<>());
                })
                .observeOn(mSchedulerProvider.ui());
    }


    @Override
    public Single<RxEvents.BroadsoftMeetMeConferencingConferenceResponseEvent> getMeetMeConferencingConference(String bridgeId, String conferenceId) {

        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        logUsernameNull("getMeetMeConferencingConference");

        if (mNetManager.getBroadsoftUserApi() == null) {
            return Single.never();
        }

        return mNetManager.getBroadsoftUserApi().getMeetMeConferencingConference(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        mSessionManager.getUsername(),
                        bridgeId,
                        conferenceId
                )
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                         if (response.isSuccessful()) {
                             logServerSuccess(response);

                             if (response.body() != null &&
                                     response.body().getModeratorPin() != null &&
                                     response.body().getConferenceTitle() != null) {
                                 return new RxEvents.BroadsoftMeetMeConferencingConferenceResponseEvent(true, response.body().getConferenceTitle(), response.body().getModeratorPin());
                             }

                             return new RxEvents.BroadsoftMeetMeConferencingConferenceResponseEvent(true, "", "");

                         } else {
                             logServerParseFailure(response);
                             return new RxEvents.BroadsoftMeetMeConferencingConferenceResponseEvent(true, "", "");
                         }
                     }
                )
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return new RxEvents.BroadsoftMeetMeConferencingConferenceResponseEvent(false, "", "");
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<CallBackCallResponseEvent> postNewCallBackCall(
            @NonNull String recipientPhoneNumber) {

        logUsernameNull("postNewCallBackCall");

        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null) {
            return Single.never();
        }

        return mNetManager.getBroadsoftUserApi().postNewCallBackCall(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        mSessionManager.getUsername(),
                        CallUtil.getStrippedPhoneNumber(recipientPhoneNumber))
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    if (response.isSuccessful()) {
                        logServerSuccess(response);
                        return new CallBackCallResponseEvent(true);
                    } else {
                        try {
                            throw new HttpException(response);
                        } catch (HttpException exception) {
                            logHttpException(exception);
                            return new CallBackCallResponseEvent(false);
                        }
                    }
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return new CallBackCallResponseEvent(false);
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<CallThroughCallResponseEvent> postNewCallThroughCall(
            @NonNull String userPhoneNumber,
            @NonNull String recipientPhoneNumber) {

        logUsernameNull("postNewCallThroughCall");

        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null) {
            return Single.never();
        }

        return mNetManager.getBroadsoftUserApi().postNewCallThroughCall(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        mSessionManager.getUsername(),
                        CallUtil.cleanPhoneNumber(userPhoneNumber),
                        CallUtil.cleanPhoneNumber(recipientPhoneNumber))
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    BroadsoftCallThroughResponse broadsoftCallThroughResponse = response.body();

                    if (response.isSuccessful() && broadsoftCallThroughResponse != null && !TextUtils.isEmpty(broadsoftCallThroughResponse.getCallThroughNumber())) {
                        logServerSuccess(response);
                        mUserPhoneNumber = userPhoneNumber;
                        mRecipientPhoneNumber = recipientPhoneNumber;
                        mPreviousCallThrough = broadsoftCallThroughResponse.getCallThroughNumber();
                        return new CallThroughCallResponseEvent(true, broadsoftCallThroughResponse.getCallThroughNumber());

                    } else {
                        logServerParseFailure(response);
                        if (response.code() == BAD_REQUEST && response.errorBody() != null) {
                            BroadsoftErrorResponseBody broadsoftErrorResponseBody = XmlUtil.getBroadsoftErrorResponseBody(response.errorBody().string());

                            if (broadsoftErrorResponseBody != null
                                    && broadsoftErrorResponseBody.getErrorCode() != null
                                    && broadsoftErrorResponseBody.getErrorCode().equals("111023")
                                    && !mPreviousCallThrough.isEmpty() && !mUserPhoneNumber.isEmpty()
                                    && userPhoneNumber.equals(mUserPhoneNumber) && !mRecipientPhoneNumber.isEmpty()
                                    && recipientPhoneNumber.equals(mRecipientPhoneNumber)) {
                                return new CallThroughCallResponseEvent(true, mPreviousCallThrough);
                            }

                            return new CallThroughCallResponseEvent(false, null);
                        } else {
                            return new CallThroughCallResponseEvent(false, null);
                        }
                    }
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return new CallThroughCallResponseEvent(false, null);
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<VoicemailMessageSummaryResponseEvent> getVoicemailMessageSummary() {
        logUsernameNull("getVoicemailMessageSummary");

        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null) {
            return Single.just(new VoicemailMessageSummaryResponseEvent(false, 0, 0));
        }

        return mNetManager.getBroadsoftUserApi().getVoicemailMessageSummary(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        mSessionManager.getUsername())
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    BroadsoftVoicemailMessageSummaryResponse broadsoftVoicemailMessageSummaryResponse = response.body();

                    if (response.isSuccessful()) {
                        logServerSuccess(response);

                        int newMessagesCount = 0;
                        int oldMessagesCount = 0;

                        if (broadsoftVoicemailMessageSummaryResponse != null &&
                                broadsoftVoicemailMessageSummaryResponse.getBroadsoftVoicemailMessageSummaryDetails() != null) {

                            BroadsoftVoicemailMessageSummaryDetails details = broadsoftVoicemailMessageSummaryResponse.getBroadsoftVoicemailMessageSummaryDetails();

                            newMessagesCount = details.getNewMessagesCount() != null ? details.getNewMessagesCount() : 0;
                            oldMessagesCount = details.getOldMessagesCount() != null ? details.getOldMessagesCount() : 0;
                        }

                        mSessionManager.setNewVoicemailMessagesCount(newMessagesCount);

                        return new VoicemailMessageSummaryResponseEvent(true, newMessagesCount, oldMessagesCount);

                    } else {
                        logServerParseFailure(response);
                        return new VoicemailMessageSummaryResponseEvent(false, 0, 0);
                    }
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return new VoicemailMessageSummaryResponseEvent(false, 0, 0);
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<FeatureAccessCodesResponseEvent> getFeatureAccessCodes() {
        logUsernameNull("getFeatureAccessCodes");

        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null || TextUtils.isEmpty(mSessionManager.getUsername()) || TextUtils.isEmpty(mSessionManager.getPassword())) {
            return Single.just(new FeatureAccessCodesResponseEvent(false, null));
        }

        return mNetManager.getBroadsoftUserApi().getFeatureAccessCodes(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        mSessionManager.getUsername())
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    if(response.code() == UNAUTHORIZED)
                    {
                        return new FeatureAccessCodesResponseEvent(false, null);
                    }

                    BroadsoftFeatureAccessCodesResponse broadsoftFeatureAccessCodesResponse = response.body();

                    if (response.isSuccessful() && broadsoftFeatureAccessCodesResponse != null) {
                        ArrayList<FeatureAccessCode> featureAccessCodesList = new ArrayList<>();

                        for (BroadsoftFeatureAccessCode broadsoftFeatureAccessCode : broadsoftFeatureAccessCodesResponse.getBroadsoftFeatureAccessCodes()) {
                            featureAccessCodesList.add(new FeatureAccessCode(broadsoftFeatureAccessCode.getCode(),
                                                                             broadsoftFeatureAccessCode.getCodeName()));
                        }

                        FeatureAccessCodes featureAccessCodes = new FeatureAccessCodes(featureAccessCodesList);
                        mSessionManager.setFeatureAccessCodes(featureAccessCodes);

                        logServerSuccess(response);
                        return new FeatureAccessCodesResponseEvent(true, featureAccessCodes);

                    } else {
                        logServerParseFailure(response);
                        return new FeatureAccessCodesResponseEvent(false, null);
                    }
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);

                    return new FeatureAccessCodesResponseEvent(false, null);
                })
                .observeOn(mSchedulerProvider.ui());
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // CallManagementRepository Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public Single<CallHistoryResponseEvent> getAllCallLogEntries() {
        logUsernameNull("getAllCallLogEntries");

        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null) {
            return Single.never();
        }

        return mNetManager.getBroadsoftUserApi().getAllCallLogEntries(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        mSessionManager.getUsername())
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    BroadsoftAllCallLogsResponse broadsoftCallLogsResponse = response.body();

                    if (response.isSuccessful() && broadsoftCallLogsResponse != null) {
                        logServerSuccess(response);

                        ArrayList<CallLogEntry> callLogEntriesList = BroadsoftUtil.getCallLogEntries(broadsoftCallLogsResponse);

                        mDbManager.insertBWCallLogs(callLogEntriesList);
                        return new CallHistoryResponseEvent(true, callLogEntriesList);

                    } else {
                        logServerParseFailure(response);
                        return new CallHistoryResponseEvent(false, null);
                    }
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return new CallHistoryResponseEvent(false, null);
                });
    }


    @Override
    public Single<ResetConferenceCallResponseEvent> getResetConferenceCall() {

        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null) {
            return Single.never();
        }

        if (mConfigManager.getMobileConfig() != null && mConfigManager.getMobileConfig().getSip() != null && mConfigManager.getMobileConfig().getSip().getUsername() != null) {
            String configUsername = mConfigManager.getMobileConfig().getSip().getUsername();

            return mNetManager.getBroadsoftUserApi().getConferenceCalls(
                            getAppVersionHeader(),
                            mSessionManager.getAuthorizationHeader(),
                            mSessionManager.getUsername())
                    .subscribeOn(mSchedulerProvider.io())
                    .map(response -> {
                        Conference conference = response.body();

                        if (response.isSuccessful() && conference != null && conference.getEndpoint() != null && !TextUtils.isEmpty(conference.getEndpoint().getAddressOfRecord()) && conference.getEndpoint().getAddressOfRecord().contains(configUsername)) {
                            logServerSuccess(response);
                            clearConferenceCall().subscribe();

                            return new ResetConferenceCallResponseEvent(true, conference.getEndpoint().getAddressOfRecord());

                        } else {
                            return new ResetConferenceCallResponseEvent(false, null);
                        }
                    })
                    .onErrorReturn(throwable -> {
                        logServerResponseError(throwable);
                        return new ResetConferenceCallResponseEvent(false, "");
                    })
                    .observeOn(mSchedulerProvider.ui());
        }

        return Single.never();
    }

    @Override
    public Single<RxEvents.ClearConferenceCallsResponseEvent> clearConferenceCall() {

        logUsernameNull("clearConferenceCall");

        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null) {
            return Single.never();
        }

        return mNetManager.getBroadsoftUserApi().clearConferenceCalls(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        mSessionManager.getUsername())
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    if (response.isSuccessful()) {
                        logServerSuccess(response);
                        return new RxEvents.ClearConferenceCallsResponseEvent(response.body() != null);
                    } else {
                        try {
                            throw new HttpException(response);
                        } catch (HttpException exception) {
                            logHttpException(exception);
                            return new RxEvents.ClearConferenceCallsResponseEvent(false);
                        }
                    }
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return new RxEvents.ClearConferenceCallsResponseEvent(false);
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<RxEvents.IsExistingActiveCallResponseEvent> isExistingActiveCall() {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null) {
            return Single.never();
        }

        return mNetManager.getBroadsoftUserApi().getActiveCalls(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        mSessionManager.getUsername())
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    if (response.isSuccessful() && response.body().getCalls().size() > 0) {
                        logServerSuccess(response);
                        return new RxEvents.IsExistingActiveCallResponseEvent(true);
                    } else {
                        try {
                            throw new HttpException(response);
                        } catch (HttpException exception) {
                            logHttpException(exception);
                            return new RxEvents.IsExistingActiveCallResponseEvent(false);
                        }
                    }
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return new RxEvents.IsExistingActiveCallResponseEvent(false);
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<BroadsoftCallDetails> getActiveCallInformation(String callId) {
        if (mNetManager.getBroadsoftUserApi() == null || TextUtils.isEmpty(mSessionManager.getUsername()) || TextUtils.isEmpty(mSessionManager.getPassword())) {
            return Single.never();
        }

        return mNetManager.getBroadsoftUserApi().getActiveCall(getAppVersionHeader(),
                                                               mSessionManager.getAuthorizationHeader(),
                                                               mSessionManager.getUsername(),
                                                               callId)
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .flatMap(response -> {

                    if (response.code() == UNAUTHORIZED) {
                        return Single.just(new BroadsoftCallDetails());
                    }

                    if (response.isSuccessful()) {
                        logServerSuccess(response);
                    } else {
                        mLogManager.logToFile(Enums.Logging.STATE_FAILURE, R.string.active_call_information_failed);
                        return Single.just(new BroadsoftCallDetails());
                    }

                    return response.body() != null ? Single.just(response.body()) : Single.just(new BroadsoftCallDetails());
                })
                .onErrorReturn(throwable -> new BroadsoftCallDetails());
    }

    @Override
    public void clearActiveCalls() {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null) {
            return;
        }

        mNetManager.getBroadsoftUserApi().getActiveCalls(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        mSessionManager.getUsername())
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new DisposableSingleObserver<Response<BroadsoftCallsResponseBody>>() {
                    @Override
                    public void onSuccess(Response<BroadsoftCallsResponseBody> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getCalls().size() > 0) {
                                for (BroadsoftCall call : response.body().getCalls()) {
                                    deleteCall(call.getCallId());
                                }
                            }
                            logServerSuccess(response);

                        } else {
                            logServerResponseError(new HttpException(response));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        logServerResponseError(e);
                    }
                });
    }

    @Override
    public Single<Integer> getActiveCallCount() {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null) {
            return Single.never();
        }

        return mNetManager.getBroadsoftUserApi().getActiveCalls(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        mSessionManager.getUsername())
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .map(response -> {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            return response.body().getCalls().size();
                        }

                        logServerSuccess(response);

                    } else {
                        logServerResponseError(new HttpException(response));
                    }

                    return -1;
                });
    }

    private void deleteCall(String callId) {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null) {
            return;
        }

        mNetManager.getBroadsoftUserApi().getActiveCall(getAppVersionHeader(),
                                                        mSessionManager.getAuthorizationHeader(),
                                                        mSessionManager.getUsername(),
                                                        callId)
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .doOnError(this::logServerResponseError)
                .flatMap(response -> {

                    if(response.code() == UNAUTHORIZED)
                    {
                        return Single.just(new BroadsoftCallDetails());
                    }

                    if (response != null && response.isSuccessful()) {
                        if (response.body() != null &&
                                response.body().getEndpoint() != null &&
                                !TextUtils.isEmpty(response.body().getEndpoint().getAddressOfRecord()) &&
                                mConfigManager.getMobileConfig() != null &&
                                mConfigManager.getMobileConfig().getSip() != null &&
                                !TextUtils.isEmpty(mConfigManager.getMobileConfig().getSip().getUsername()) &&
                                response.body().getEndpoint().getAddressOfRecord().startsWith(mConfigManager.getMobileConfig().getSip().getUsername())) {

                            mNetManager.getBroadsoftUserApi().deleteActiveCall(
                                            getAppVersionHeader(),
                                            mSessionManager.getAuthorizationHeader(),
                                            mSessionManager.getUsername(),
                                            callId)
                                    .subscribeOn(mSchedulerProvider.io())
                                    .observeOn(mSchedulerProvider.ui())
                                    .doOnSuccess(responseBodyResponse -> {
                                        if (response.isSuccessful()) {
                                            logServerSuccess(response);
                                        }
                                    })
                                    .doOnError(this::logServerResponseError)
                                    .subscribe();
                        }

                        logServerSuccess(response);

                    } else if (response != null) {
                        try {
                            throw new HttpException(response);
                        } catch (HttpException exception) {
                            logHttpException(exception);
                        }

                    }

                    return response != null ? Single.just(response) : Single.never();
                })
                .subscribe();
    }

    public void rejectCall(String callId) {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null) {
            return;
        }

        mNetManager.getBroadsoftUserApi().rejectCall(getAppVersionHeader(),
                                                     mSessionManager.getAuthorizationHeader(),
                                                     mSessionManager.getUsername(),
                                                     callId,
                                                     Boolean.TRUE)
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .doOnError(this::logServerResponseError)
                .flatMap(response -> {
                    if (response != null && response.isSuccessful()) {
                        logServerSuccess(response);
                    } else if (response != null) {
                        try {
                            throw new HttpException(response);
                        } catch (HttpException exception) {
                            logHttpException(exception);
                        }
                    }
                    return response != null ? Single.just(response) : Single.never();
                })
                .subscribe();
    }

    @Override
    public Single<DeleteCallResponseEvent> deleteCall(
            @NonNull final String callType,
            @NonNull final String callId) {

        logUsernameNull("deleteCall");

        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null) {
            return Single.never();
        }

        return mNetManager.getBroadsoftUserApi().deleteCall(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        mSessionManager.getUsername(),
                        callType,
                        callId)
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    if (response.isSuccessful()) {
                        logServerSuccess(response);
                        return new DeleteCallResponseEvent(response.body() != null, callType, callId);
                    } else {
                        try {
                            throw new HttpException(response);
                        } catch (HttpException exception) {
                            logHttpException(exception);
                            return new DeleteCallResponseEvent(false, callType, callId);
                        }
                    }
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return new DeleteCallResponseEvent(false, callType, callId);
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<DeleteAllCallsResponseEvent> deleteAllCalls() {
        logUsernameNull("deleteAllCalls");

        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null) {
            return Single.never();
        }

        return mNetManager.getBroadsoftUserApi().deleteAllCalls(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        mSessionManager.getUsername())
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    if (response.isSuccessful()) {
                        logServerSuccess(response);
                        return new DeleteAllCallsResponseEvent(response.body() != null);
                    } else {
                        try {
                            throw new HttpException(response);
                        } catch (HttpException exception) {
                            logHttpException(exception);
                            return new DeleteAllCallsResponseEvent(false);
                        }
                    }
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return new DeleteAllCallsResponseEvent(false);
                })
                .observeOn(mSchedulerProvider.ui());
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // ContactManagementRepository Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public Single<EnterpriseContactByImpIdResponseEvent> getEnterpriseContactByImpId(
            @NonNull final String impId,
            @Enums.Sip.CallTypes.Type @Nullable final Integer callType) {

        logUsernameNull("getEnterpriseContactByImpId");

        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null) {
            return Single.never();
        }

        return mNetManager.getBroadsoftUserApi().getEnterpriseContactByImpId(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        mSessionManager.getUsername(),
                        String.format(CASE_INSENSITIVE_SEARCH_PARAM, impId))
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    BroadsoftEnterprise broadsoftEnterprise = response.body();

                    if (response.isSuccessful() &&
                            broadsoftEnterprise != null &&
                            broadsoftEnterprise.getEnterpriseDirectoryDetailsList() != null) {

                        logServerSuccess(response);

                        if (broadsoftEnterprise.getEnterpriseDirectoryDetailsList().size() > 0) {
                            return new EnterpriseContactByImpIdResponseEvent(
                                    true,
                                    BroadsoftUtil.getNextivaContact(broadsoftEnterprise.getEnterpriseDirectoryDetailsList().get(0)),
                                    callType);

                        } else {
                            return new EnterpriseContactByImpIdResponseEvent(true, null, null);
                        }
                    }

                    logServerParseFailure(response);
                    return new EnterpriseContactByImpIdResponseEvent(false, null, null);
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return new EnterpriseContactByImpIdResponseEvent(false, null, null);
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<EnterpriseContactByNameResponseEvent> getEnterpriseContactByName(
            final String name) {

        logUsernameNull("getEnterpriseContactByName");

        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null) {
            return Single.never();
        }

        return mNetManager.getBroadsoftUserApi().getEnterpriseContactByName(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        mSessionManager.getUsername(),
                        String.format(CASE_INSENSITIVE_SEARCH_PARAM, name))
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    BroadsoftEnterprise broadsoftEnterprise = response.body();

                    if (response.isSuccessful() &&
                            broadsoftEnterprise != null &&
                            broadsoftEnterprise.getEnterpriseDirectoryDetailsList() != null) {

                        logServerSuccess(response);

                        if (broadsoftEnterprise.getEnterpriseDirectoryDetailsList().size() > 0) {
                            return new EnterpriseContactByNameResponseEvent(
                                    true,
                                    BroadsoftUtil.getNextivaContact(broadsoftEnterprise.getEnterpriseDirectoryDetailsList().get(0)));

                        } else {
                            return new EnterpriseContactByNameResponseEvent(true, null);
                        }
                    }

                    logServerParseFailure(response);
                    return new EnterpriseContactByNameResponseEvent(false, null);
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return new EnterpriseContactByNameResponseEvent(false, null);
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<EnterpriseContactByNumberResponseEvent> getEnterpriseContactByPhoneNumber(
            @NonNull final String phoneNumber,
            @NonNull @Enums.Sip.CallTypes.Type final Integer callType) {

        logUsernameNull("getEnterpriseContactByPhoneNumber");

        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null) {
            return Single.never();
        }

        return mNetManager.getBroadsoftUserApi().getEnterpriseContactByPhoneNumber(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        mSessionManager.getUsername(),
                        phoneNumber)
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    BroadsoftEnterprise broadsoftEnterprise = response.body();

                    if (response.isSuccessful() &&
                            broadsoftEnterprise != null &&
                            broadsoftEnterprise.getEnterpriseDirectoryDetailsList() != null) {

                        logServerSuccess(response);

                        if (broadsoftEnterprise.getEnterpriseDirectoryDetailsList().size() > 0) {
                            return new EnterpriseContactByNumberResponseEvent(
                                    true,
                                    BroadsoftUtil.getNextivaContact(broadsoftEnterprise.getEnterpriseDirectoryDetailsList().get(0)),
                                    phoneNumber,
                                    callType);

                        } else {
                            return new EnterpriseContactByNumberResponseEvent(true, null, phoneNumber, callType);
                        }
                    }

                    logServerParseFailure(response);
                    return new EnterpriseContactByNumberResponseEvent(false, null, phoneNumber, callType);
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return new EnterpriseContactByNumberResponseEvent(false, null, phoneNumber, callType);
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<EnterpriseContactByNumberResponseEvent> getEnterpriseContactByExtension(
            @NonNull final String extension,
            @NonNull @Enums.Sip.CallTypes.Type final Integer callType) {

        logUsernameNull("getEnterpriseContactByExtension");

        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null) {
            return Single.never();
        }

        return mNetManager.getBroadsoftUserApi().getEnterpriseContactByExtension(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        mSessionManager.getUsername(),
                        extension)
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    BroadsoftEnterprise broadsoftEnterprise = response.body();

                    if (response.isSuccessful() &&
                            broadsoftEnterprise != null &&
                            broadsoftEnterprise.getEnterpriseDirectoryDetailsList() != null) {

                        logServerSuccess(response);

                        if (broadsoftEnterprise.getEnterpriseDirectoryDetailsList().size() > 0) {
                            return new EnterpriseContactByNumberResponseEvent(
                                    true,
                                    BroadsoftUtil.getNextivaContact(broadsoftEnterprise.getEnterpriseDirectoryDetailsList().get(0)),
                                    extension,
                                    callType);

                        } else {
                            return new EnterpriseContactByNumberResponseEvent(true, null, extension, callType);
                        }
                    }

                    logServerParseFailure(response);
                    return new EnterpriseContactByNumberResponseEvent(false, null, extension, callType);
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return new EnterpriseContactByNumberResponseEvent(false, null, extension, callType);
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @NonNull
    @Override
    public Single<Boolean> getEnterpriseContacts(final boolean forceRefresh) {
        logUsernameNull("getEnterpriseContacts");
        String transactionId = UUID.randomUUID().toString();

        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null) {
            return Single.never();
        }

        if (forceRefresh || mDbManager.isCacheExpired(SharedPreferencesManager.ENTERPRISE_CONTACTS)) {
            final String appVersionHeader = getAppVersionHeader();
            final String authorizationHeader = mSessionManager.getAuthorizationHeader();
            final String username = mSessionManager.getUsername();
            final ArrayList<NextivaContact> mEnterpriseContactsToSave = new ArrayList<>();

            return mNetManager.getBroadsoftUserApi().getEnterpriseContactsCount(appVersionHeader, authorizationHeader, username)
                    .retryWhen(sRetryApiCallFunction)
                    .subscribeOn(mSchedulerProvider.io())
                    .flatMap((Function<Response<BroadsoftEnterprise>, ObservableSource<Integer>>) response -> {
                        if (response.isSuccessful()) {
                            BroadsoftEnterprise broadsoftEnterprise = response.body();

                            if (broadsoftEnterprise != null) {
                                ArrayList<Integer> startParamsList = new ArrayList<>();

                                int startParam = 1;

                                while (startParam < broadsoftEnterprise.getTotalAvailableRecords()) {
                                    startParamsList.add(startParam);
                                    startParam += RESULTS_PER_PAGE;
                                }

                                logServerSuccess(response);
                                return Observable.fromIterable(startParamsList);

                            } else {
                                logServerParseFailure(response);
                                return Observable.just(-1);
                            }

                        } else {
                            return Observable.error(new HttpException(response));
                        }
                    })
                    .flatMap((Function<Integer, ObservableSource<Boolean>>) integer -> {
                        if (integer > 0) {
                            return mNetManager.getBroadsoftUserApi().getEnterpriseContacts(appVersionHeader, authorizationHeader, username, integer, SORT_COLUMN_FIRST_NAME, RESULTS_PER_PAGE)
                                    .retryWhen(sRetryApiCallFunction)
                                    .subscribeOn(mSchedulerProvider.io())
                                    .flatMap((Function<Response<BroadsoftEnterprise>, ObservableSource<Boolean>>) response -> {
                                        if (response.isSuccessful()) {
                                            BroadsoftEnterprise broadsoftEnterprise = response.body();

                                            if (broadsoftEnterprise != null &&
                                                    broadsoftEnterprise.getEnterpriseDirectoryDetailsList() != null &&
                                                    !broadsoftEnterprise.getEnterpriseDirectoryDetailsList().isEmpty()) {

                                                logServerSuccess(response);
                                                mEnterpriseContactsToSave.addAll(BroadsoftUtil.getNextivaContacts(response.body()));

                                                return Observable.just(true);

                                            } else {
                                                logServerParseFailure(response);
                                                return Observable.just(true);
                                            }

                                        } else {
                                            return Observable.error(new HttpException(response));
                                        }
                                    })
                                    .onErrorReturn(throwable -> {
                                        logServerResponseError(throwable);
                                        return false;
                                    });

                        } else {
                            return Observable.just(true);
                        }
                    })
                    .doOnComplete(() -> mDbManager.saveEnterpriseContactsInThread(new ArrayList<>(mEnterpriseContactsToSave), transactionId))
                    .toList()
                    .onErrorReturn(throwable -> {
                        logServerResponseError(throwable);
                        return Collections.singletonList(false);
                    })
                    .map(booleansList -> Collections.disjoint(booleansList, Collections.singletonList(false)))
                    .observeOn(mSchedulerProvider.ui());
        }

        mLogManager.logToFile(Enums.Logging.STATE_INFO, "Success getEnterpriseContacts load cache");
        return Single.just(true);
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // Voicemail Methods
    // --------------------------------------------------------------------------------------------

    @Override
    public Single<Boolean> refreshVoicemails() {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null) {
            return Single.just(false);
        }

        return mNetManager.getBroadsoftUserApi().refreshVoicemails(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        mSessionManager.getUsername())
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    if (response.isSuccessful()) {
                        logServerSuccess(response);

                        return true;

                    } else {
                        logServerParseFailure(response);
                        return false;
                    }
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return false;
                });
    }

    @Override
    public Single<Boolean> getVoicemails() {
        if (mNetManager.getBroadsoftUserApi() == null) {
            return Single.never();
        }

        return mNetManager.getBroadsoftUserApi().getVoicemails(getAppVersionHeader(),
                                                               mSessionManager.getAuthorizationHeader(),
                                                               mSessionManager.getUsername())
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    if (response.isSuccessful()) {

                        if (response.body() != null) {
                            mDbManager.insertVoicemails(response.body().getVoicemails()).subscribe();
                        }

                        logServerSuccess(response);

                        return true;

                    } else {
                        logServerParseFailure(response);
                        return false;
                    }
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return false;
                });
    }

    @Override
    public Single<BroadsoftVoiceMessageDetailsResponse> getVoicemailDetails(String messageDetailsPath) {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null) {
            return Single.never();
        }

        return mNetManager.getBroadsoftUserApi().getVoicemailDetails(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        messageDetailsPath)
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    if (response.isSuccessful()) {

                        logServerSuccess(response);

                        return response.body();

                    } else {
                        logServerParseFailure(response);
                        return new BroadsoftVoiceMessageDetailsResponse();
                    }
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return new BroadsoftVoiceMessageDetailsResponse();
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<Boolean> markVoicemailRead(String messageUuid, String messagePath) {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null) {
            return Single.never();
        }

        return mNetManager.getBroadsoftUserApi().markVoicemailRead(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        mSessionManager.getUsername(),
                        messageUuid)
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    if (response.isSuccessful()) {
                        logServerSuccess(response);

                        mDbManager.markVoicemailRead(messagePath);

                        return true;

                    } else {
                        logServerParseFailure(response);
                        return false;
                    }
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return false;
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<Boolean> markVoicemailUnread(String messageUuid, String messagePath) {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null) {
            return Single.never();
        }

        return mNetManager.getBroadsoftUserApi().markVoicemailUnread(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        mSessionManager.getUsername(),
                        messageUuid)
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    if (response.isSuccessful()) {
                        logServerSuccess(response);

                        mDbManager.markVoicemailUnread(messagePath);

                        return true;

                    } else {
                        logServerParseFailure(response);
                        return false;
                    }
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return false;
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<Boolean> deleteVoicemail(String messageId) {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null) {
            return Single.never();
        }

        return mNetManager.getBroadsoftUserApi().deleteVoicemail(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        messageId)
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    if (response.isSuccessful()) {
                        logServerSuccess(response);

                        mDbManager.deleteVoicemail(messageId);

                        return true;

                    } else {
                        logServerParseFailure(response);
                        return false;
                    }
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return false;
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<Boolean> markAllVoicemailsRead() {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        if (mNetManager.getBroadsoftUserApi() == null) {
            return Single.just(false);
        }

        return mNetManager.getBroadsoftUserApi().markAllVoicemailsRead(
                        getAppVersionHeader(),
                        mSessionManager.getAuthorizationHeader(),
                        mSessionManager.getUsername())
                .subscribeOn(mSchedulerProvider.io())
                .map(response -> {
                    if (response.isSuccessful()) {
                        logServerSuccess(response);
                        mDbManager.markAllVoicemailsRead();
                        return true;

                    } else {
                        logServerParseFailure(response);
                        return false;
                    }
                })
                .onErrorReturn(throwable -> {
                    logServerResponseError(throwable);
                    return false;
                })
                .observeOn(mSchedulerProvider.ui());
    }

    // --------------------------------------------------------------------------------------------
    private void logHttpException(HttpException exception) {
        logHttpException(exception, true);
    }

    private void logHttpException(HttpException exception, Boolean isSendToCrashlytics) {
        mLogManager.logToFile(Enums.Logging.STATE_ERROR, exception.response().raw().request().method() + " " + StringUtil.redactApiUrl(exception.response().raw().request().url().toString()) + " " + StringUtil.redactApiUrl(exception.getMessage()));
        if(isSendToCrashlytics)
            FirebaseCrashlytics.getInstance().recordException(exception);
    }
}