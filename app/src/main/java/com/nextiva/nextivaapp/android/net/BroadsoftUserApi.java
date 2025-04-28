/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.net;

import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.models.CallCenter;
import com.nextiva.nextivaapp.android.models.net.broadsoft.BroadsoftBroadWorksAnywhereLocationResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.BroadsoftCallThroughResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.BroadsoftFeatureAccessCodesResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.BroadsoftMeetMeConferencingConference;
import com.nextiva.nextivaapp.android.models.net.broadsoft.BroadsoftMeetMeConferencingUserBridges;
import com.nextiva.nextivaapp.android.models.net.broadsoft.BroadsoftMeetMeConferencingUserConferences;
import com.nextiva.nextivaapp.android.models.net.broadsoft.BroadsoftProfileResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.BroadsoftServiceSettingsResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.BroadsoftServicesResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.BroadsoftVoicemailMessageSummaryResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.accessdevices.BroadsoftAccessDevicesResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.accessdevices.BroadsoftAllowTerminationPutBody;
import com.nextiva.nextivaapp.android.models.net.broadsoft.calllogs.BroadsoftAllCallLogsResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.calllogs.BroadsoftMissedCallLogsResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.calls.BroadsoftCallDetails;
import com.nextiva.nextivaapp.android.models.net.broadsoft.calls.BroadsoftCallsResponseBody;
import com.nextiva.nextivaapp.android.models.net.broadsoft.conference.Conference;
import com.nextiva.nextivaapp.android.models.net.broadsoft.enterprisecontacts.BroadsoftEnterprise;
import com.nextiva.nextivaapp.android.models.net.broadsoft.notifications.PushNotificationRegistrationBody;
import com.nextiva.nextivaapp.android.models.net.broadsoft.notifications.response.PushNotificationsResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftBaseServiceSettings;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftBroadWorksAnywhereLocationBody;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftBroadWorksAnywhereLocationPostBody;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftCallCenter;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftCallCenterUnavailableCodes;
import com.nextiva.nextivaapp.android.models.net.broadsoft.voicemail.BroadsoftVoiceMessageDetailsResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.voicemail.BroadsoftVoicemailListResponse;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by adammacdonald on 2/14/18.
 */

public interface BroadsoftUserApi {

    @Headers("Accept: application/xml")
    @GET("v2.0/user/{userId}/services")
    Observable<Response<BroadsoftServicesResponse>> getServices(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId);

    @Headers("Accept: application/xml")
    @GET()
    Single<Response<BroadsoftServiceSettingsResponse>> getServiceSettings(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Url String url);

    @Headers("Accept: application/xml")
    @PUT()
    Single<Response<ResponseBody>> putServiceSettings(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Url String url,
            @Body BroadsoftBaseServiceSettings body);

    @Headers("Accept: application/xml")
    @GET("v2.0/user/{userId}/services/broadworksanywhere/location/{phoneNumber}")
    Single<Response<BroadsoftBroadWorksAnywhereLocationResponse>> getNextivaAnywhereLocation(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId,
            @Path("phoneNumber") String phoneNumber);

    @Headers("Accept: application/xml")
    @PUT("v2.0/user/{userId}/services/broadworksanywhere/location/{phoneNumber}")
    Single<Response<ResponseBody>> putNextivaAnywhereLocation(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId,
            @Path("phoneNumber") String phoneNumber,
            @Body BroadsoftBroadWorksAnywhereLocationBody body);

    @Headers("Accept: application/xml")
    @POST("v2.0/user/{userId}/services/broadworksanywhere/location")
    Single<Response<ResponseBody>> postNextivaAnywhereLocation(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId,
            @Body BroadsoftBroadWorksAnywhereLocationPostBody body);

    @Headers("Accept: application/xml")
    @DELETE("v2.0/user/{userId}/services/broadworksanywhere/location/{phoneNumber}")
    Single<Response<ResponseBody>> deleteNextivaAnywhereLocation(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId,
            @Path("phoneNumber") String phoneNumber);

    @Headers("Accept: application/xml")
    @GET("v2.0/user/{userId}/services/meetmeconference")
    Single<Response<BroadsoftMeetMeConferencingUserBridges>> getMeetMeConference(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId);

    @Headers("Accept: application/xml")
    @GET("v2.0/user/{userId}/services/meetmeconference/{bridgeId}")
    Single<Response<BroadsoftMeetMeConferencingUserConferences>> getMeetMeConferencingUserConferences(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId,
            @Path("bridgeId") String bridgeId);

    @Headers("Accept: application/xml")
    @GET("v2.0/user/{userId}/services/meetmeconference/{bridgeId}/conference/{conferenceId}")
    Single<Response<BroadsoftMeetMeConferencingConference>> getMeetMeConferencingConference(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId,
            @Path("bridgeId") String bridgeId,
            @Path("conferenceId") String conferenceId);

    @Headers("Accept: application/xml")
    @GET("v2.0/user/{userId}/services/CallCenter")
    Single<Response<BroadsoftCallCenter>> getCallCenter(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId);

    @Headers("Accept: application/xml")
    @PUT("v2.0/user/{userId}/services/CallCenter")
    Single<Response<CallCenter>> putCallCenter(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId,
            @Body CallCenter body);

    @Headers("Accept: application/xml")
    @GET("v2.0/group/{groupId}/services/CallCenter/UnavailableCodes")
    Single<Response<BroadsoftCallCenterUnavailableCodes>> getCallCenterUnavailableCodes(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("groupId") String groupId,
            @Query("enterpriseId") String enterpriseId);

    @Headers("Accept: application/xml")
    @GET("v2.0/user/{userId}/profile")
    Single<Response<BroadsoftProfileResponse>> getProfile(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId);

    @Headers("Accept: application/xml")
    @GET("v2.0/user/{userId}/directories/calllogs")
    Single<Response<BroadsoftAllCallLogsResponse>> getAllCallLogEntries(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId);

    @Headers("Accept: application/xml")
    @GET("v2.0/user/{userId}/directories/calllogs/missed")
    Single<Response<BroadsoftMissedCallLogsResponse>> getMissedCallLogEntries(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId);

    @DELETE("v2.0/user/{userId}/directories/calllogs/{callType}/{callId}")
    Single<Response<ResponseBody>> deleteCall(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId,
            @Path("callType") @Enums.Calls.CallTypes.Type String callType,
            @Path("callId") String callId);

    @DELETE("v2.0/user/{userId}/directories/calllogs")
    Single<Response<ResponseBody>> deleteAllCalls(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId);

    @Headers("Accept: application/xml")
    @GET("v2.0/user/{userId}/directories/enterprise")
    Observable<Response<BroadsoftEnterprise>> getEnterpriseContacts(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId,
            @Query("start") int start,
            @Query("sortColumn") String sortColumn,
            @Query("results") int resultsPerPage);

    @Headers("Accept: application/xml")
    @GET("v2.0/user/{userId}/directories/enterprise?results=1")
    Observable<Response<BroadsoftEnterprise>> getEnterpriseContactsCount(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId);

    @Headers("Accept: application/xml")
    @GET("v2.0/user/{userId}/directories/enterprise")
    Single<Response<BroadsoftEnterprise>> getEnterpriseContactByImpId(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId,
            @Query("impId") String impId);

    @Headers("Accept: application/xml")
    @GET("v2.0/user/{userId}/directories/enterprise")
    Single<Response<BroadsoftEnterprise>> getEnterpriseContactByPhoneNumber(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId,
            @Query("number") String number);

    @Headers("Accept: application/xml")
    @GET("v2.0/user/{userId}/directories/enterprise")
    Single<Response<BroadsoftEnterprise>> getEnterpriseContactByExtension(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId,
            @Query("extension") String extension);

    @Headers("Accept: application/xml")
    @GET("v2.0/user/{userId}/directories/enterprise")
    Single<Response<BroadsoftEnterprise>> getEnterpriseContactByName(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId,
            @Query("name") String name);

    @Headers("Accept: application/xml")
    @GET("v2.0/user/{userId}/profile/device")
    Single<Response<BroadsoftAccessDevicesResponse>> getAccessDevices(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId);

    @Headers("Accept: application/xml")
    @PUT("v2.0/user/{userId}/profile/device/{linePort}")
    Single<Response<ResponseBody>> setAllowTermination(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId,
            @Path("linePort") String linePort,
            @Body BroadsoftAllowTerminationPutBody body);

    @Headers("Accept: application/xml")
    @POST("v2.0/user/{userId}/calls/new")
    Single<Response<ResponseBody>> postNewCallBackCall(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId,
            @Query("address") String recipientPhoneNumber);

    @Headers("Accept: application/xml")
    @POST("v2.0/user/{userId}/calls/imrn")
    Single<Response<BroadsoftCallThroughResponse>> postNewCallThroughCall(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId,
            @Query("callingPartyAddress") String userPhoneNumber,
            @Query("calledPartyAddress") String recipientPhoneNumber);

    @Headers("Accept: application/xml")
    @GET("v2.0/user/{userId}/profile/Fac")
    Single<Response<BroadsoftFeatureAccessCodesResponse>> getFeatureAccessCodes(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId);

    @Headers("Accept: application/xml")
    @GET("v2.0/user/{userId}/calls/messagesummary")
    Single<Response<BroadsoftVoicemailMessageSummaryResponse>> getVoicemailMessageSummary(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId);

    @Headers("Accept: application/xml")
    @POST("v2.0/user/{userId}/profile/PushNotificationRegistrations/new")
    Single<Response<ResponseBody>> registerForPushNotifications(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId,
            @Body PushNotificationRegistrationBody body);

    @Headers("Accept: application/xml")
    @DELETE("v2.0/user/{userId}/profile/PushNotificationRegistrations")
    Single<Response<ResponseBody>> unregisterForPushNotifications(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId,
            @Query("registrationId") String registrationId,
            @Query("token") String token);

    @Headers("Accept: application/xml")
    @GET("v2.0/user/{userId}/profile/PushNotificationRegistrations")
    Single<Response<PushNotificationsResponse>> getPushNotificationRegistrations(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId);

    @Headers("Accept: application/xml")
    @GET("v2.0/user/{userId}/calls/conference")
    Single<Response<Conference>> getConferenceCalls(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId);

    @Headers("Accept: application/xml")
    @DELETE("v2.0/user/{userId}/calls/conference")
    Single<Response<Conference>> clearConferenceCalls(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId);

    @Headers("Accept: application/xml")
    @GET("v2.0/user/{userId}/calls")
    Single<Response<BroadsoftCallsResponseBody>> getActiveCalls(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId);

    @Headers("Accept: application/xml")
    @GET("v2.0/user/{userId}/calls/{callId}")
    Single<Response<BroadsoftCallDetails>> getActiveCall(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId,
            @Path("callId") String callId);

    @Headers("Accept: application/xml")
    @DELETE("v2.0/user/{userId}/calls/{callId}")
    Single<Response<ResponseBody>> deleteActiveCall(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId,
            @Path("callId") String callId);

    @Headers("Accept: application/xml")
    @DELETE("v2.0/user/{userId}/calls/{callId}")
    Single<Response<ResponseBody>> rejectCall(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId,
            @Path("callId") String callId,
            @Query("decline") Boolean decline);


    @Headers("Accept: application/xml")
    @PUT("v2.0/user/{userId}/voicemessagingmessages/refresh")
    Single<Response<ResponseBody>> refreshVoicemails(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId);

    @Headers("Accept: application/xml")
    @GET("v2.0/user/{userId}/voicemessagingmessages/")
    Single<Response<BroadsoftVoicemailListResponse>> getVoicemails(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId);

    @Headers("Accept: application/xml")
    @GET("{messageDetailPath}")
    Single<Response<BroadsoftVoiceMessageDetailsResponse>> getVoicemailDetails(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path(value = "messageDetailPath", encoded = true) String messageDetailPath);

    @Headers("Accept: application/xml")
    @PUT("v2.0/user/{userId}/voicemessagingmessages/{messageId}/markAsRead")
    Single<Response<ResponseBody>> markVoicemailRead(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId,
            @Path("messageId") String messageId);

    @Headers("Accept: application/xml")
    @PUT("v2.0/user/{userId}/voicemessagingmessages/{messageId}/markAsUnread")
    Single<Response<ResponseBody>> markVoicemailUnread(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId,
            @Path("messageId") String messageId);

    @Headers("Accept: application/xml")
    @PUT("v2.0/user/{userId}/voicemessagingmessages/markAllAsRead")
    Single<Response<ResponseBody>> markAllVoicemailsRead(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId);

    @Headers("Accept: application/xml")
    @DELETE("{messageDetailPath}")
    Single<Response<ResponseBody>> deleteVoicemail(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path(value = "messageDetailPath", encoded = true) String messageDetailPath);
}
