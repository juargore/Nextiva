/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.mocks.managers;

import androidx.annotation.NonNull;

import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository;
import com.nextiva.nextivaapp.android.models.CallCenter;
import com.nextiva.nextivaapp.android.models.FeatureAccessCode;
import com.nextiva.nextivaapp.android.models.FeatureAccessCodes;
import com.nextiva.nextivaapp.android.models.NextivaAnywhereLocation;
import com.nextiva.nextivaapp.android.models.ServiceSettings;
import com.nextiva.nextivaapp.android.models.UserDetails;
import com.nextiva.nextivaapp.android.models.net.broadsoft.voicemail.BroadsoftVoiceMessageDetailsResponse;
import com.nextiva.nextivaapp.android.net.buses.RxEvents;
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

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by adammacdonald on 2/9/18.
 */

public class FakeUserApiManager implements UserRepository {

    private boolean mUserDetailsSuccessful = true;
    private boolean mUserFeatureAccessCodesSuccessful = true;
    private boolean mUserServiceSettingsFilteredSuccessful = true;
    private boolean mSingleUserServiceSettingsSuccessful = true;
    private boolean mVoicemailMessageSummarySuccessful = true;
    private boolean mNewCallBackCallSuccessful = true;
    private boolean mNewCallThroughCallSuccessful = true;

    public FakeUserApiManager() {
    }

    public void setUserDetailsSuccessful(boolean userDetailsSuccessful) {
        mUserDetailsSuccessful = userDetailsSuccessful;
    }

    public void setUserServiceSettingsFilteredSuccessful(boolean userServiceSettingsFilteredSuccessful) {
        mUserServiceSettingsFilteredSuccessful = userServiceSettingsFilteredSuccessful;
    }

    public void setSingleUserServiceSettingsSuccessful(boolean singleUserServiceSettingsSuccessful) {
        mSingleUserServiceSettingsSuccessful = singleUserServiceSettingsSuccessful;
    }

    public void setUserFeatureAccessCodesSuccessful(boolean userFeatureAccessCodesSuccessful) {
        mUserFeatureAccessCodesSuccessful = userFeatureAccessCodesSuccessful;
    }

    public void setVoicemailMessageSummarySuccessful(boolean voicemailMessageSummarySuccessful) {
        mVoicemailMessageSummarySuccessful = voicemailMessageSummarySuccessful;
    }

    public void setNewCallBackCallSuccessful(boolean newCallBackCallSuccessful) {
        mNewCallBackCallSuccessful = newCallBackCallSuccessful;
    }

    public void setNewCallThroughCallSuccessful(boolean newCallThroughCallSuccessful) {
        mNewCallThroughCallSuccessful = newCallThroughCallSuccessful;
    }

    // --------------------------------------------------------------------------------------------
    // UserRepository Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public Single<UserDetailsResponseEvent> getUserDetails() {
        if (mUserDetailsSuccessful) {
            UserDetails userDetails = new UserDetails();
            userDetails.setFirstName("First");
            userDetails.setLastName("Last");

            return Single.just(new UserDetailsResponseEvent(true, userDetails));

        } else {
            return Single.just(new UserDetailsResponseEvent(false, null));
        }
    }

    @Override
    public Single<FeatureAccessCodesResponseEvent> getFeatureAccessCodes() {
        if (mUserFeatureAccessCodesSuccessful) {
            ArrayList<FeatureAccessCode> featureAccessCodeList = new ArrayList<>();
            featureAccessCodeList.add(new FeatureAccessCode("Code One", "CodeName One"));
            featureAccessCodeList.add(new FeatureAccessCode("Code Two", "CodeName Two"));

            FeatureAccessCodes featureAccessCodes = new FeatureAccessCodes(featureAccessCodeList);

            return Single.just(new FeatureAccessCodesResponseEvent(true, featureAccessCodes));

        } else {
            return Single.just(new FeatureAccessCodesResponseEvent(false, null));
        }
    }

    @Override
    public Single<ServiceSettingsMapResponseEvent> getServiceSettingsFiltered(String[] filterServiceSettings) {
        if (mUserServiceSettingsFilteredSuccessful) {
            ServiceSettings remoteOfficeServiceSettings = new ServiceSettings(Enums.Service.TYPE_REMOTE_OFFICE, "remoteOfficeUri", true, null, null, "0987", null, null, null, null, null, null);
            ServiceSettings callForwardingAlwaysServiceSettings = new ServiceSettings(Enums.Service.TYPE_CALL_FORWARDING_ALWAYS, "callForwardingAlwaysUri", true, false, null, null, "1234", null, null, null, null, null);

            ArrayList<NextivaAnywhereLocation> locationsList = new ArrayList<NextivaAnywhereLocation>() {{
                add(new NextivaAnywhereLocation("1111", "description1", true, false, false, false));
                add(new NextivaAnywhereLocation("2222", "description2", false, true, false, false));
                add(new NextivaAnywhereLocation("3333", "description3", false, false, true, false));
                add(new NextivaAnywhereLocation("4444", "description4", false, false, false, true));
                add(new NextivaAnywhereLocation("5555", null, false, false, false, false));
            }};

            ServiceSettings nextivaAnywhereServiceSettings = new ServiceSettings(Enums.Service.TYPE_BROADWORKS_ANYWHERE, "broadworksAnywhereUri", null, null, null, null, null, true, false, locationsList, null, null);

            HashMap<String, ServiceSettings> serviceSettingsMap = new HashMap<>();
            serviceSettingsMap.put(Enums.Service.TYPE_REMOTE_OFFICE, remoteOfficeServiceSettings);
            serviceSettingsMap.put(Enums.Service.TYPE_CALL_FORWARDING_ALWAYS, callForwardingAlwaysServiceSettings);
            serviceSettingsMap.put(Enums.Service.TYPE_BROADWORKS_ANYWHERE, nextivaAnywhereServiceSettings);

            return Single.just(new ServiceSettingsMapResponseEvent(true, serviceSettingsMap));

        } else {
            return Single.just(new ServiceSettingsMapResponseEvent(false, null));
        }
    }

    @Override
    public Observable<ServiceSettingsGetResponseEvent> getSingleServiceSettings(@NonNull String serviceType, @NonNull String url) {
        if (mSingleUserServiceSettingsSuccessful) {
            return Observable.just(new ServiceSettingsGetResponseEvent(true, new ServiceSettings(serviceType, url)));
        } else {
            return Observable.just(new ServiceSettingsGetResponseEvent(false, null));
        }
    }

    @Override
    public Single<ServiceSettingsPutResponseEvent> putServiceSettings(@NonNull ServiceSettings serviceSettings) {
        return null;
    }

    @Override
    public Single<Boolean> setAllowTermination(boolean allowTermination) {
        return null;
    }

    @Override
    public Single<NextivaAnywhereLocationGetResponseEvent> getNextivaAnywhereLocation(@NonNull String phoneNumber) {
        return null;
    }

    @Override
    public Single<NextivaAnywhereLocationSaveResponseEvent> putNextivaAnywhereLocation(@NonNull ServiceSettings nextivaAnywhereServiceSettings, @NonNull NextivaAnywhereLocation location, @NonNull String oldPhoneNumber) {
        return null;
    }

    @Override
    public Single<NextivaAnywhereLocationSaveResponseEvent> postNextivaAnywhereLocation(@NonNull ServiceSettings nextivaAnywhereServiceSettings, @NonNull NextivaAnywhereLocation location) {
        return null;
    }

    @Override
    public Single<NextivaAnywhereLocationDeleteResponseEvent> deleteNextivaAnywhereLocation(@NonNull ServiceSettings nextivaAnywhereServiceSettings, @NonNull NextivaAnywhereLocation location) {
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
    public Single<RxEvents.BroadsoftMeetMeConferenceResponseEvent> getMeetMeConference() {
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
    public Single<CallBackCallResponseEvent> postNewCallBackCall(@NonNull String recipientPhoneNumber) {
        return Single.just(new CallBackCallResponseEvent(mNewCallBackCallSuccessful));
    }

    @Override
    public Single<CallThroughCallResponseEvent> postNewCallThroughCall(@NonNull String userPhoneNumber, @NonNull String recipientPhoneNumber) {
        if (mNewCallThroughCallSuccessful) {
            return Single.just(new CallThroughCallResponseEvent(true, "1112223333"));
        } else {
            return Single.just(new CallThroughCallResponseEvent(false, null));
        }
    }

    @Override
    public Single<RegisterForPushNotificationsResponseEvent> registerForPushNotifications(String token) {
        return Single.just(new RegisterForPushNotificationsResponseEvent(true));
    }

    @Override
    public Single<UnregisterForPushNotificationsResponseEvent> unregisterForPushNotifications() {
        return Single.just(new UnregisterForPushNotificationsResponseEvent(true));
    }

    @Override
    public Single<VoicemailMessageSummaryResponseEvent> getVoicemailMessageSummary() {
        if (mVoicemailMessageSummarySuccessful) {
            return Single.just(new VoicemailMessageSummaryResponseEvent(true, 100, 200));
        } else {
            return Single.just(new VoicemailMessageSummaryResponseEvent(false, 0, 0));
        }
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
    public Single<Boolean> markVoicemailRead(String messageDetailsPath, String messagePath) {
        return null;
    }

    @Override
    public Single<Boolean> doesPushNotificationExist() {
        return null;
    }

    @Override
    public Single<Boolean> markAllVoicemailsRead() {
        return null;
    }

    @Override
    public Single<Boolean> deleteVoicemail(String messageId) {
        return null;
    }

    @Override
    public Single<Boolean> markVoicemailUnread(String messageUuid, String messagePath) {
        return null;
    }

    // --------------------------------------------------------------------------------------------
}
