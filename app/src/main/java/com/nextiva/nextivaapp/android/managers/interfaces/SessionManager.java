/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers.interfaces;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.nextiva.androidNextivaAuth.data.datasource.network.dto.UserInfo;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.model.DbPresence;
import com.nextiva.nextivaapp.android.db.model.DbSession;
import com.nextiva.nextivaapp.android.db.model.SmsTeam;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ConversationRepository;
import com.nextiva.nextivaapp.android.models.CurrentUser;
import com.nextiva.nextivaapp.android.models.FeatureAccessCodes;
import com.nextiva.nextivaapp.android.models.IdentityVoice;
import com.nextiva.nextivaapp.android.models.ServiceSettings;
import com.nextiva.nextivaapp.android.models.UserDetails;
import com.nextiva.nextivaapp.android.models.net.platform.AccountInformation;
import com.nextiva.nextivaapp.android.models.net.platform.PhoneNumberInformation;
import com.nextiva.nextivaapp.android.models.net.platform.Products;
import com.nextiva.nextivaapp.android.models.net.platform.featureFlags.FeatureFlags;
import com.nextiva.pjsip.pjsip_lib.sipservice.EnabledCodecs;

import java.util.ArrayList;
import java.util.List;

import kotlinx.coroutines.flow.Flow;

/**
 * Created by adammacdonald on 2/7/18.
 */

public interface SessionManager {

    @Nullable
    UserDetails getUserDetails();

    void setUserDetails(@Nullable UserDetails userDetails);

    @Nullable
    DbPresence getUserPresence();

    @Nullable
    boolean isUserPresenceAutomatic();

    void setUserPresence(@Nullable DbPresence nextivaPresence, boolean isAutomatic);

    @Nullable
    DbPresence getConnectUserPresence();

    @Nullable
    boolean isConnectUserPresenceAutomatic();

    void setConnectUserPresence(@Nullable DbPresence nextivaPresence, boolean isAutomatic);

    @Nullable
    String getToken();

    void setToken(@Nullable String token);

    @Nullable
    String getSessionId();

    void setSessionId(@Nullable String sessionId);

    @Nullable
    String getSelectedTenant();

    void setSelectedTenant(@Nullable String selectedTenant);

    @Nullable
    UserInfo getUserInfo();

    void setUserInfo(@Nullable UserInfo userInfo);

    @Nullable
    IdentityVoice getIdentityVoice();

    void setIdentityVoice(@Nullable IdentityVoice identityVoice);

    boolean getIsLicenseApproved();

    void setIsLicenseApproved(boolean isLicenseApproved);

    @Nullable
    String getUsername();

    void setUsername(@Nullable String username);

    @Nullable
    String getPassword();

    void setPassword(@Nullable String password);

    @Nullable
    String getLastLoggedUsername();

    void setLastLoggedUsername(String lastLoggedUsername);

    @Nullable
    String getLastLoggedPassword();

    void setLastLoggedPassword(String lastLoggedPassword);

    boolean getRememberPassword();

    void setRememberPassword(boolean rememberPassword);

    @Nullable
    String getAccessDeviceUsername();

    void setAccessDeviceUsername(@Nullable String accessDeviceUsername);

    @Nullable
    String getAccessDevicePassword();

    void setAccessDevicePassword(@Nullable String accessDevicePassword);

    @Nullable
    String getAccessDeviceVersion();

    void setAccessDeviceVersion(@Nullable String accessDeviceVersion);

    @Nullable
    String getAccessDeviceTypeUrl();

    void setAccessDeviceTypeUrl(@Nullable String accessDeviceTypeUrl);

    @Nullable
    String getAccessDeviceLinePort();

    void setAccessDeviceLinePort(@Nullable String accessDeviceLinePort);

    boolean getAllowTermination();

    void setAllowTermination(boolean allowTermination);

    @Nullable
    String getPushNotificationRegistrationId();

    void setPushNotificationRegistrationId(@Nullable String umsUdid);

    @Nullable
    ServiceSettings getRemoteOfficeServiceSettings();

    void setRemoteOfficeServiceSettings(@Nullable ServiceSettings remoteOfficeServiceSettings);

    @Nullable
    ServiceSettings getNextivaAnywhereServiceSettings();

    void setNextivaAnywhereServiceSettings(@Nullable ServiceSettings nextivaAnywhereServiceSettings);

    boolean getIsCallBackEnabled(@Nullable ServiceSettings remoteOfficeServiceSettings, @Nullable ServiceSettings nextivaAnywhereServiceSettings);

    boolean getIsCallThroughEnabled(@Nullable ServiceSettings nextivaAnywhereServiceSettings, @Nullable String thisPhoneNumber);

    @Nullable
    String getLastDialedPhoneNumber();

    void setLastDialedPhoneNumber(@Nullable String phoneNumber);

    @Nullable
    FeatureAccessCodes getFeatureAccessCodes();

    void setFeatureAccessCodes(FeatureAccessCodes featureAccessCodes);

    byte[] getOwnAvatar();

    int getTotalUnreadNotificationsCount();

    int getNewVoicemailMessagesCount();

    LiveData<Integer> getNewVoiceCallMessagesCountLiveData();

    LiveData<DbSession> getNewVoicemailMessagesCountLiveData();

    LiveData<DbSession> getSmsMessagesCountLiveData();

    LiveData<Integer> getRoomsMessagesCountLiveData(List<String> types);

    int getChatMessagesCount();

    int getSmsMessagesCount();

    int getVoiceCallMessagesCount();

    int getRoomsMessagesCount();

    LiveData<List<DbSession>> getChatSmsCount();

    LiveData<List<DbSession>> getVoiceCallVoicemailCount();


    @NonNull
    LiveData<Integer> getTotalUnreadNotificationCountLiveData(@NonNull List<String> keys);

    LiveData<Integer> getTotalUnreadNotificationCountLiveData();

    void setNewVoicemailMessagesCount(int newVoicemailMessagesCount);

    void updateNotificationsCount(ConversationRepository conversationRepository, Context context);

    void deleteMessageCountCacheVoicemail(ConversationRepository conversationRepository);

    void tempIncreaseChannelMessagesCount(@Enums.Messages.Channels.Channel String type, Integer tempIncreaseAmount, Context context);

    String getAuthorizationHeader();

    void setUmsHost(@Nullable String umsHost);

    String getUmsHost();

    boolean isSmsEnabled();

    boolean isTeamSmsEnabled();

    boolean canSendSms();

    boolean shouldBroadsoftChatShow();

    boolean isTeamchatEnabled(Context context);

    boolean isSmsLicenseEnabled();

    boolean isTeamSmsLicenseEnabled();

    boolean isSmsProvisioningEnabled();

    boolean isShowSms();

    boolean isVoicemailTranscriptionEnabled();

    boolean isNextivaConnectEnabled();

    FeatureFlags getFeatureFlags();

    void setFeatureFlags(FeatureFlags featureFlags);

    AccountInformation getAccountInformation();

    void setAccountInformation(AccountInformation accountInformation);

    List<SmsTeam> getUsersTeams();

    void setUsersTeams(ArrayList<SmsTeam> teams);

    List<SmsTeam> getAllTeams();

    void setAllTeams(ArrayList<SmsTeam> teams);

    PhoneNumberInformation getPhoneNumberInformation();

    void setPhoneNumberInformation(PhoneNumberInformation information);

    boolean isCustomToneEnabled();

    Products getProducts();

    void setProducts(Products products);

    boolean hasContactManagementPrivilege();

    void setContactManagementPrivilege(boolean privilege);

    boolean isMeetingEnabled(Context context);

    CurrentUser getCurrentUser();

    boolean isVoiceLargePageEnabled();

    boolean  isCommunicationsBulkDeletesEnabled();

    boolean isCommunicationsBulkUpdatesEnabled();

    boolean isCommunicationsBulkActionsUpdateEnabled();

    boolean isSmsCampaignValidationEnabled();

    boolean isBlockNumberForCallingEnabled();

    void setEnabledAudioCodecs(EnabledCodecs audioCodecs);

    Flow<DbSession> getEnabledAudioCodecsFlow();

    EnabledCodecs getEnabledAudioCodecs();

    Flow<DbSession> getEchoCancellationFlow();

    String getEchoCancellation();

    void setEchoCancellation(int echoCancellation);

    Flow<DbSession> getAECAggressivenessFlow();

    String getAECAggressiveness();

    void setAECAggressiveness(int aecAggressiveness);

    boolean isNoiseSuppressionEnabled();

    void setNoiseSuppressionEnabled(boolean privilege);
}
