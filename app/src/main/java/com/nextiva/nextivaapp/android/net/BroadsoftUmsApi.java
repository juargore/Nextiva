/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.net;

import com.nextiva.nextivaapp.android.models.net.broadsoft.BroadsoftVCardBody;
import com.nextiva.nextivaapp.android.models.net.broadsoft.BroadsoftVCardResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.contactstorage.BroadsoftContactStorageResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.contactstorage.BroadsoftContactStorageSetBody;
import com.nextiva.nextivaapp.android.models.net.broadsoft.ondemandpresence.BroadsoftOnDemandPresencePostBody;
import com.nextiva.nextivaapp.android.models.net.broadsoft.ondemandpresence.BroadsoftOnDemandPresenceResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.superpresence.BroadsoftSuperPresenceResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.superpresence.BroadsoftSuperPresenceShowPostBody;
import com.nextiva.nextivaapp.android.models.net.broadsoft.superpresence.BroadsoftSuperPresenceStatusPostBody;
import com.nextiva.nextivaapp.android.models.net.broadsoft.ums.BroadsoftChatMessageBody;
import com.nextiva.nextivaapp.android.models.net.broadsoft.ums.BroadsoftDeviceRegistrationBody;
import com.nextiva.nextivaapp.android.models.net.broadsoft.ums.BroadsoftMarkMessagesReadBody;
import com.nextiva.nextivaapp.android.models.net.broadsoft.ums.BroadsoftSendChatMessageResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.ums.BroadsoftSetVCardBody;
import com.nextiva.nextivaapp.android.models.net.broadsoft.ums.BroadsoftUmsBaseResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.ums.BroadsoftUmsChatMessagesResponse;

import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by adammacdonald on 3/7/18.
 */

public interface BroadsoftUmsApi {

    @Headers("Content-Type: application/json")
    @PUT("gateway/v2/registration/{userId}/{udid}")
    Single<Response<BroadsoftUmsBaseResponse>> putRegisterDevice(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId,
            @Path("udid") String udid,
            @Body BroadsoftDeviceRegistrationBody body);

    @Headers("Content-Type: application/json")
    @PUT("gateway/v3/registration/{userId}/{udid}")
    Single<Response<BroadsoftUmsBaseResponse>> putRegisterDeviceV3(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId,
            @Path("udid") String udid,
            @Body BroadsoftDeviceRegistrationBody body);

    @Headers("Content-Type: application/json")
    @DELETE("gateway/v2/registration/{userId}/{udid}")
    Single<Response<BroadsoftUmsBaseResponse>> deleteUnregisterDevice(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("userId") String userId,
            @Path("udid") String udid);

    @Headers("Content-Type: application/json; charset=utf-8")
    @GET("gateway/v2/msg/history/{udid}/{timeStamp}")
    Single<Response<BroadsoftUmsChatMessagesResponse>> getChatMessageHistory(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("udid") String udid,
            @Path("timeStamp") long timeStamp);

    @Headers("Content-Type: application/json")
    @POST("gateway/userinfo/vcard/")
    Single<Response<BroadsoftVCardResponse>> getVCards(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Body BroadsoftVCardBody body);

    @Headers("Content-Type: application/json")
    @PUT("gateway/v2/userinfo/vcard/{jid}")
    Single<Response<BroadsoftUmsBaseResponse>> setVCard(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("jid") String jid,
            @Body BroadsoftSetVCardBody setVCardBody);

    @Headers("Content-Type: application/json")
    @POST("gateway/v2/msg/read/{udid}")
    Single<Response<BroadsoftUmsBaseResponse>> markAllMessagesRead(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("udid") String udid,
            @Body BroadsoftMarkMessagesReadBody markAllMessagesReadBody);

    @Headers("Content-Type: application/json")
    @GET("gateway/userinfo/contactstorage/{time}")
    Single<Response<BroadsoftContactStorageResponse>> getContactStorage(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("time") String time);

    @Headers("Content-Type: application/json")
    @PUT("gateway/v2/userinfo/contactstorage/{jid}")
    Single<Response<BroadsoftUmsBaseResponse>> setContactStorage(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("jid") String jid,
            @Body BroadsoftContactStorageSetBody body);

    @Headers("Content-Type: application/json")
    @POST("gateway/presence/get")
    Single<Response<BroadsoftOnDemandPresenceResponse>> getOnDemandPresences(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Body BroadsoftOnDemandPresencePostBody onDemandPresencePostBody);

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("gateway/v2/msg/send/{udid}")
    Single<Response<BroadsoftSendChatMessageResponse>> sendChatMessage(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("udid") String udid,
            @Body BroadsoftChatMessageBody body);

    @Headers("Content-Type: application/json; charset=utf-8")
    @GET("gateway/superpresence/{res}")
    Single<Response<BroadsoftSuperPresenceResponse>> getSuperPresence(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("res") String res);

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("gateway/superpresence/show/{res}")
    Single<Response<BroadsoftUmsBaseResponse>> setPresenceAvailability(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("res") String res,
            @Body BroadsoftSuperPresenceShowPostBody body);

    @Headers("Content-Type: application/json; charset=utf-8")
    @DELETE("gateway/superpresence/show/{res}")
    Single<Response<BroadsoftUmsBaseResponse>> deletePresenceAvailability(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("res") String res);

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("gateway/superpresence/freetext/{res}")
    Single<Response<BroadsoftUmsBaseResponse>> setPresenceStatusText(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("res") String res,
            @Body BroadsoftSuperPresenceStatusPostBody body);

    @Headers("Content-Type: application/json; charset=utf-8")
    @DELETE("gateway/superpresence/freetext/{res}")
    Single<Response<BroadsoftUmsBaseResponse>> deletePresenceStatusText(
            @Header("X-AppVersion") String appVersionHeader,
            @Header("Authorization") String authorizationHeader,
            @Path("res") String res);
}
