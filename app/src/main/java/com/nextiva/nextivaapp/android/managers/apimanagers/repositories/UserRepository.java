/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers.apimanagers.repositories;

import androidx.annotation.NonNull;

import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.models.CallCenter;
import com.nextiva.nextivaapp.android.models.NextivaAnywhereLocation;
import com.nextiva.nextivaapp.android.models.ServiceSettings;
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

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by adammacdonald on 2/15/18.
 * <p>
 * Repository used to get/update data about the currently logged in user
 */

public interface UserRepository {

    Single<UserDetailsResponseEvent> getUserDetails();

    Single<ServiceSettingsMapResponseEvent> getServiceSettingsFiltered(
            @Enums.Service.Type String[] filterServiceSettings);

    Observable<ServiceSettingsGetResponseEvent> getSingleServiceSettings(
            @NonNull @Enums.Service.Type String serviceType,
            @NonNull String url);

    Single<ServiceSettingsPutResponseEvent> putServiceSettings(
            @NonNull ServiceSettings serviceSettings);

    Single<Boolean> setAllowTermination(boolean allowTermination);

    Single<NextivaAnywhereLocationGetResponseEvent> getNextivaAnywhereLocation(
            @NonNull String phoneNumber);

    Single<NextivaAnywhereLocationSaveResponseEvent> putNextivaAnywhereLocation(
            @NonNull ServiceSettings nextivaAnywhereServiceSettings,
            @NonNull NextivaAnywhereLocation location,
            @NonNull String oldPhoneNumber);

    Single<NextivaAnywhereLocationSaveResponseEvent> postNextivaAnywhereLocation(
            @NonNull ServiceSettings nextivaAnywhereServiceSettings,
            @NonNull NextivaAnywhereLocation location);

    Single<NextivaAnywhereLocationDeleteResponseEvent> deleteNextivaAnywhereLocation(
            @NonNull ServiceSettings nextivaAnywhereServiceSettings,
            @NonNull NextivaAnywhereLocation location);

    Single<RxEvents.BroadsoftCallCenterResponseEvent> getCallCenterService();

    Single<RxEvents.BroadsoftCallCenterPutResponseEvent> putCallCenterService(CallCenter CallCenter);

    Single<RxEvents.BroadsoftCallCenterUnavailableCodesResponseEvent> getCallCenterServiceUnavailableCodes();

    Single<RxEvents.BroadsoftMeetMeConferenceResponseEvent> getMeetMeConference();

    Single<RxEvents.BroadsoftMeetMeConferencingUserConferencesResponseEvent> getMeetMeConferencingUserConferences(String bridgeId);

    Single<RxEvents.BroadsoftMeetMeConferencingConferenceResponseEvent> getMeetMeConferencingConference(String bridgeId, String conferenceId);

    Single<CallBackCallResponseEvent> postNewCallBackCall(
            @NonNull String recipientPhoneNumber);

    Single<CallThroughCallResponseEvent> postNewCallThroughCall(
            @NonNull String userPhoneNumber,
            @NonNull String recipientPhoneNumber);

    Single<FeatureAccessCodesResponseEvent> getFeatureAccessCodes();

    Single<VoicemailMessageSummaryResponseEvent> getVoicemailMessageSummary();

    Single<RegisterForPushNotificationsResponseEvent> registerForPushNotifications(String firebaseToken);

    Single<UnregisterForPushNotificationsResponseEvent> unregisterForPushNotifications();

    void removeExpiredPushNotificationRegistrations(CompositeDisposable compositeDisposable);

    Single<Boolean> doesPushNotificationExist();

    Single<Boolean> refreshVoicemails();

    Single<Boolean> getVoicemails();

    Single<BroadsoftVoiceMessageDetailsResponse> getVoicemailDetails(String messageDetailsPath);

    Single<Boolean> markAllVoicemailsRead();

    Single<Boolean> markVoicemailRead(String messageDetailsPath, String messagePath);

    Single<Boolean> markVoicemailUnread(String messageUuid, String messagePath);

    Single<Boolean> deleteVoicemail(String messageId);

}
