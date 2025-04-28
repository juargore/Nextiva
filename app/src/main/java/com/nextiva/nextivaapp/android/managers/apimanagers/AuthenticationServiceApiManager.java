/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers.apimanagers;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.AuthenticationRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository;
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager;
import com.nextiva.nextivaapp.android.models.CallCenter;
import com.nextiva.nextivaapp.android.models.NextivaAnywhereLocation;
import com.nextiva.nextivaapp.android.models.ServiceSettings;
import com.nextiva.nextivaapp.android.models.net.AuthenticationBody;
import com.nextiva.nextivaapp.android.models.net.broadsoft.voicemail.BroadsoftVoiceMessageDetailsResponse;
import com.nextiva.nextivaapp.android.net.AuthenticationServiceApi;
import com.nextiva.nextivaapp.android.net.buses.RxBus;
import com.nextiva.nextivaapp.android.net.buses.RxEvents;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.AuthenticationResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.CallBackCallResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.CallThroughCallResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.FeatureAccessCodesResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.NextivaAnywhereLocationDeleteResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.NextivaAnywhereLocationGetResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.NextivaAnywhereLocationSaveResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.RegisterForPushNotificationsResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.ServiceSettingsGetResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.ServiceSettingsMapResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.ServiceSettingsPutResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.UnregisterForPushNotificationsResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.UserDetailsResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.VoicemailMessageSummaryResponseEvent;
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by adammacdonald on 2/2/18.
 */

public class AuthenticationServiceApiManager extends BaseApiManager implements
        AuthenticationRepository,
        UserRepository {

    private final SessionManager mSessionManager;
    private final SchedulerProvider mSchedulerProvider;

    private final AuthenticationServiceApi mAuthenticationServiceApi;

    @Inject
    public AuthenticationServiceApiManager(Application application,
                                           SessionManager sessionManager,
                                           SchedulerProvider schedulerProvider,
                                           AuthenticationServiceApi authenticationServiceApi,
                                           LogManager logManager) {

        super(application, logManager);

        mSessionManager = sessionManager;
        mSchedulerProvider = schedulerProvider;
        mAuthenticationServiceApi = authenticationServiceApi;
    }

    // --------------------------------------------------------------------------------------------
    // AuthenticationRepository Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public Single<AuthenticationResponseEvent> authenticateUser(String username, String password) {
        AuthenticationBody body = new AuthenticationBody(username, password, "nextiva");

        return mAuthenticationServiceApi.postAuthentication(getAppVersionHeader(), body)
                .subscribeOn(mSchedulerProvider.io())
                .flatMap(response -> {
                    mSessionManager.setToken(response.getTokenId());
                    return mAuthenticationServiceApi.getValidateToken(getAppVersionHeader(), response.getTokenId());
                })
                .flatMap(response -> mAuthenticationServiceApi.getUserDetails(getAppVersionHeader(), mSessionManager.getUsername()))
                .map(userDetails -> {
                    if (userDetails != null) {
                        mSessionManager.setUserDetails(userDetails);
                        return new AuthenticationResponseEvent(true, true);
                    }

                    return new AuthenticationResponseEvent(false, false);
                })
                .onErrorReturn(throwable -> {
                    mSessionManager.setToken(null);
                    FirebaseCrashlytics.getInstance().recordException(throwable);
                    return new AuthenticationResponseEvent(false, false);
                })
                .observeOn(mSchedulerProvider.ui());
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // UserRepository Methods
    // --------------------------------------------------------------------------------------------
    // TODO Maybe this isn't needed to be a separate call, maybe this is just part of the login process
    // TODO and the AuthenticationResponseEvent can return the UserDetails object inside it?
    // TODO Would we ever need to get the user details _not_ as part of the login process?
    @Override
    public Single<UserDetailsResponseEvent> getUserDetails() {
        return mAuthenticationServiceApi.getUserDetails(
                        getAppVersionHeader(),
                        mSessionManager.getUsername())
                .subscribeOn(mSchedulerProvider.io())
                .map(userDetails -> {
                    if (userDetails != null) {
                        mSessionManager.setUserDetails(userDetails);

                        RxBus.INSTANCE.publish(new UserDetailsResponseEvent(true, userDetails));
                    }

                    return new UserDetailsResponseEvent(false, null);
                })
                .onErrorReturn(throwable -> {
                    FirebaseCrashlytics.getInstance().recordException(throwable);
                    return new UserDetailsResponseEvent(false, null);
                })
                .observeOn(mSchedulerProvider.ui());
    }

    @Override
    public Single<ServiceSettingsMapResponseEvent> getServiceSettingsFiltered(
            String[] filterServiceSettings) {

        return null;
    }

    @Override
    public Observable<ServiceSettingsGetResponseEvent> getSingleServiceSettings(
            @NonNull String serviceType,
            @NonNull String url) {

        return null;
    }

    @Override
    public Single<ServiceSettingsPutResponseEvent> putServiceSettings(
            @NonNull ServiceSettings serviceSettings) {

        return null;
    }

    @Override
    public Single<NextivaAnywhereLocationGetResponseEvent> getNextivaAnywhereLocation(
            @NonNull String phoneNumber) {

        return null;
    }

    @Override
    public Single<NextivaAnywhereLocationSaveResponseEvent> putNextivaAnywhereLocation(
            @NonNull ServiceSettings nextivaAnywhereServiceSettings,
            @NonNull NextivaAnywhereLocation location,
            @NonNull String oldPhoneNumber) {

        return null;
    }

    @Override
    public Single<NextivaAnywhereLocationSaveResponseEvent> postNextivaAnywhereLocation(
            @NonNull ServiceSettings nextivaAnywhereServiceSettings,
            @NonNull NextivaAnywhereLocation location) {

        return null;
    }

    @Override
    public Single<NextivaAnywhereLocationDeleteResponseEvent> deleteNextivaAnywhereLocation(
            @NonNull ServiceSettings nextivaAnywhereServiceSettings,
            @NonNull NextivaAnywhereLocation location) {

        return null;
    }

    @Override
    public Single<RxEvents.BroadsoftMeetMeConferenceResponseEvent> getMeetMeConference() {
        return null;
    }

    @Override
    public Single<Boolean> setAllowTermination(boolean allowTermination) {
        return null;
    }

    @Override
    public Single<RxEvents.BroadsoftCallCenterResponseEvent> getCallCenterService() {
        return null;
    }

    @Override
    public Single<RxEvents.BroadsoftCallCenterPutResponseEvent> putCallCenterService(final CallCenter CallCenter) {
        return null;
    }

    @Override
    public Single<RxEvents.BroadsoftCallCenterUnavailableCodesResponseEvent> getCallCenterServiceUnavailableCodes() {
        return null;
    }

    @Override
    public Single<RxEvents.BroadsoftMeetMeConferencingUserConferencesResponseEvent> getMeetMeConferencingUserConferences(String bridgeId) {
        return null;
    }

    @Override
    public Single<RxEvents.BroadsoftMeetMeConferencingConferenceResponseEvent> getMeetMeConferencingConference(String bridgeId, String conferenceId) {
        return null;
    }

    @Override
    public Single<AuthenticationResponseEvent> getAuthenticationResponseEvent(String username) {
        return null;
    }

    @Override
    public Single<Boolean> getPollingDeviceSettings(String username) {
        return null;
    }

    @Override
    public Single<CallBackCallResponseEvent> postNewCallBackCall(
            @NonNull String recipientPhoneNumber) {

        return null;
    }

    @Override
    public Single<CallThroughCallResponseEvent> postNewCallThroughCall(
            @NonNull String userPhoneNumber,
            @NonNull String recipientPhoneNumber) {

        return null;
    }

    @Override
    public Single<FeatureAccessCodesResponseEvent> getFeatureAccessCodes() {
        return null;
    }

    @Override
    public Single<VoicemailMessageSummaryResponseEvent> getVoicemailMessageSummary() {
        return null;
    }

    @Override
    public Single<RegisterForPushNotificationsResponseEvent> registerForPushNotifications(String firebaseToken) {
        return null;
    }

    @Override
    public Single<UnregisterForPushNotificationsResponseEvent> unregisterForPushNotifications() {
        return null;
    }

    @Override
    public void removeExpiredPushNotificationRegistrations(CompositeDisposable compositeDisposable) {

    }

    @Override
    public Single<Boolean> refreshVoicemails() {
        return null;
    }

    @Override
    public Single<Boolean> getVoicemails() {
        return null;
    }

    @Override
    public Single<BroadsoftVoiceMessageDetailsResponse> getVoicemailDetails(String messageDetailsPath) {
        return null;
    }

    @Override
    public Single<Boolean> markVoicemailRead(String messageDetailsPath, String messageId) {
        return null;
    }

    @Override
    public Single<Boolean> doesPushNotificationExist() {
        return null;
    }

    @Override
    public Single<Boolean> deleteVoicemail(String messageId) {
        return null;
    }

    @Override
    public Single<Boolean> markAllVoicemailsRead() {
        return null;
    }

    @Override
    public Single<Boolean> markVoicemailUnread(String messageUuid, String messagePath) {
        return null;
    }

    // --------------------------------------------------------------------------------------------
}
