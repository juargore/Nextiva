/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import kotlinx.coroutines.flow.Flow;
import me.leolin.shortcutbadger.ShortcutBadgeException;
import me.leolin.shortcutbadger.ShortcutBadger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nextiva.androidNextivaAuth.data.datasource.network.dto.UserInfo;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.DbManager;
import com.nextiva.nextivaapp.android.db.model.DbPresence;
import com.nextiva.nextivaapp.android.db.model.DbSession;
import com.nextiva.nextivaapp.android.db.model.SmsTeam;
import com.nextiva.nextivaapp.android.db.util.DbConstants;
import com.nextiva.nextivaapp.android.features.rooms.db.RoomsDbManager;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ConversationRepository;
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager;
import com.nextiva.nextivaapp.android.managers.interfaces.KeyStoreManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager;
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager;
import com.nextiva.nextivaapp.android.models.CurrentUser;
import com.nextiva.nextivaapp.android.models.FeatureAccessCodes;
import com.nextiva.nextivaapp.android.models.IdentityVoice;
import com.nextiva.nextivaapp.android.models.NextivaAnywhereLocation;
import com.nextiva.nextivaapp.android.models.ServiceSettings;
import com.nextiva.nextivaapp.android.models.UserDetails;
import com.nextiva.nextivaapp.android.models.net.platform.AccountInformation;
import com.nextiva.nextivaapp.android.models.net.platform.PhoneNumberInformation;
import com.nextiva.nextivaapp.android.models.net.platform.Products;
import com.nextiva.nextivaapp.android.models.net.platform.featureFlags.FeatureFlags;
import com.nextiva.nextivaapp.android.util.CallUtil;
import com.nextiva.nextivaapp.android.util.GsonUtil;
import com.nextiva.nextivaapp.android.util.LogUtil;
import com.nextiva.pjsip.pjsip_lib.sipservice.EnabledCodecs;

import io.reactivex.observers.DisposableSingleObserver;

/**
 * Created by adammacdonald on 2/7/18.
 */
@Singleton
public class NextivaSessionManager implements SessionManager {

    private final SharedPreferencesManager mSharedPreferencesManager;
    private final KeyStoreManager mKeyStoreManager;
    private final DbManager mDbManager;
    private final AvatarManager mAvatarManager;
    private final RoomsDbManager mRoomsDbManager;

    private final Object lock = new Object();

    LiveData<Integer> mTotalUnreadNotificationsCountLiveData = new MutableLiveData<>();
    private volatile int mUnreadMissedCalls = 0;
    private volatile int mUnreadVoicemails = 0;
    private volatile FeatureFlags mFeatureFlags;
    private volatile Products mProducts;
    private volatile DbPresence mConnectUserPresence;
    private volatile Boolean mIsConnectUserPresenceAutomatic = null;
    private volatile UserDetails mUserDetails = null;
    private volatile String mUserName = "";
    private volatile String mPassword = "";
    private volatile ServiceSettings mNextivaAnywhereServiceSettings = null;
    private volatile String mAccessDevicePassword = "";
    private volatile String mAccessDeviceUsername = "";
    private volatile String mSessionId = "";
    private volatile String mSelectedTenant = "";
    private volatile UserInfo mUserInfo = null;
    private volatile IdentityVoice mIdentityVoice = null;
    private volatile String mToken = "";
    private volatile DbPresence mUserPresence = null;
    private volatile Boolean mIsUserPresenceAutomatic;
    private volatile int mUnreadChatMessages = 0;
    private volatile int mUnreadSmsMessages = 0;
    private volatile int mTotalUnreadNotificationsCountCached = 0;
    private volatile int mUnreadRoomsMessages = 0;

    @Inject
    public NextivaSessionManager(SharedPreferencesManager sharedPreferencesManager,
                                 KeyStoreManager keyStoreManager,
                                 DbManager dbManager,
                                 AvatarManager avatarManager,
                                 RoomsDbManager roomsDbManager) {

        mSharedPreferencesManager = sharedPreferencesManager;
        mKeyStoreManager = keyStoreManager;
        mDbManager = dbManager;
        mAvatarManager = avatarManager;
        mRoomsDbManager = roomsDbManager;
    }

    @Nullable
    @Override
    public UserDetails getUserDetails() {
        if (mUserDetails != null) {
            return mUserDetails;
        }
        synchronized (lock) {
            if (mUserDetails == null) {
                String userDetails = mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.USER_DETAILS);
                if (userDetails != null && !userDetails.isEmpty()) {
                    mUserDetails = GsonUtil.getObject(UserDetails.class, userDetails);
                    LogUtil.d("session getUserDetails mDbManager");
                }
            }
        }
        return mUserDetails;
    }

    @Override
    public void setUserDetails(@Nullable UserDetails userDetails) {
        synchronized (lock) {
            mUserDetails = userDetails;
            mDbManager.setSessionSetting(Enums.Session.DatabaseKey.USER_DETAILS, GsonUtil.getJSON(userDetails));
        }
    }

    @Nullable
    @Override
    public DbPresence getUserPresence() {
        if (mUserPresence != null) {
            return mUserPresence;
        }
        synchronized (lock) {
            if (mUserPresence == null) {
                String presence = mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.USER_PRESENCE);
                if (!TextUtils.isEmpty(presence)) {
                    mUserPresence = GsonUtil.getObject(DbPresence.class, presence);
                    LogUtil.d("session getUserPresence mDbManager");
                }
            }
        }
        return mUserPresence;
    }

    @Override
    public boolean isUserPresenceAutomatic() {
        if (mIsUserPresenceAutomatic != null) {
            return mIsUserPresenceAutomatic;
        }
        synchronized (lock) {
            if (mIsUserPresenceAutomatic == null) {
                String automatic = mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.IS_USER_PRESENCE_AUTOMATIC);
                mIsUserPresenceAutomatic = TextUtils.equals(automatic, DbConstants.DB_TRUE_STRING_VALUE);
            }
        }
        return mIsUserPresenceAutomatic;
    }

    @Override
    public void setUserPresence(@Nullable DbPresence nextivaPresence, boolean isAutomatic) {
        synchronized (lock) {
            mUserPresence = nextivaPresence;
            mIsUserPresenceAutomatic = isAutomatic;

            mDbManager.setSessionSetting(Enums.Session.DatabaseKey.USER_PRESENCE, GsonUtil.getJSON(nextivaPresence));
            mDbManager.setSessionSetting(Enums.Session.DatabaseKey.IS_USER_PRESENCE_AUTOMATIC, isAutomatic ?
                    DbConstants.DB_TRUE_STRING_VALUE : DbConstants.DB_FALSE_STRING_VALUE);
        }
    }

    @Nullable
    @Override
    public DbPresence getConnectUserPresence() {
        if (mConnectUserPresence != null) {
            return mConnectUserPresence;
        }
        synchronized (lock) {
            if (mConnectUserPresence == null) {
                String presence = mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.USER_PRESENCE_CONNECT);
                if (!TextUtils.isEmpty(presence)) {
                    mConnectUserPresence = GsonUtil.getObject(DbPresence.class, presence);
                }
            }
        }
        return mConnectUserPresence;
    }

    @Override
    public boolean isConnectUserPresenceAutomatic() {
        if (mIsConnectUserPresenceAutomatic != null) {
            return mIsConnectUserPresenceAutomatic;
        }
        synchronized (lock) {
            if (mIsConnectUserPresenceAutomatic == null) {
                String automatic = mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.IS_CONNECT_USER_PRESENCE_AUTOMATIC);
                mIsConnectUserPresenceAutomatic = TextUtils.equals(automatic, DbConstants.DB_TRUE_STRING_VALUE);
            }
        }
        return mIsConnectUserPresenceAutomatic;
    }

    @Override
    public void setConnectUserPresence(@Nullable DbPresence nextivaPresence, boolean isAutomatic) {
        synchronized (lock) {
            mConnectUserPresence = nextivaPresence;
            mIsConnectUserPresenceAutomatic = isAutomatic;

            mDbManager.setSessionSetting(Enums.Session.DatabaseKey.USER_PRESENCE_CONNECT, GsonUtil.getJSON(nextivaPresence));
            mDbManager.setSessionSetting(Enums.Session.DatabaseKey.IS_CONNECT_USER_PRESENCE_AUTOMATIC, isAutomatic ?
                    DbConstants.DB_TRUE_STRING_VALUE : DbConstants.DB_FALSE_STRING_VALUE);
        }
    }

    @Nullable
    @Override
    public String getToken() {
        if (!TextUtils.isEmpty(mToken)) {
            return mToken;
        }
        synchronized (lock) {
            if (TextUtils.isEmpty(mToken)) {
                String token = mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.TOKEN);
                if (token != null && !token.isEmpty()) {
                    try {
                        mToken = mKeyStoreManager.decryptString(NextivaKeyStoreManager.TOKEN, token);
                    } catch (Exception e) {
                        LogUtil.e("Error decrypting token", String.valueOf(e));
                        FirebaseCrashlytics.getInstance().recordException(e);
                        return null;
                    }
                }
            }
        }
        return mToken;
    }

    @Override
    public void setToken(@Nullable String token) {
        synchronized (lock) {
            if (TextUtils.isEmpty(token)) {
                mKeyStoreManager.deleteAlias(NextivaKeyStoreManager.TOKEN);
                mDbManager.setSessionSetting(Enums.Session.DatabaseKey.TOKEN, null);
                mToken = "";
            } else {
                String encryptedToken = mKeyStoreManager.encryptString(NextivaKeyStoreManager.TOKEN, token);
                if (encryptedToken != null) {
                    mToken = encryptedToken;
                    mKeyStoreManager.addAlias(NextivaKeyStoreManager.TOKEN);
                    mDbManager.setSessionSetting(Enums.Session.DatabaseKey.TOKEN, mToken);
                } else {
                    LogUtil.e("Encrypt failed on token");
                }
            }
        }
    }

    @Nullable
    @Override
    public String getSessionId() {
        if (!TextUtils.isEmpty(mSessionId)) {
            return mSessionId;
        }
        synchronized (lock) {
            if (TextUtils.isEmpty(mSessionId)) {
                mSessionId = mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.SESSION_ID);
            }
        }
        return mSessionId;
    }

    @Override
    public void setSessionId(@Nullable String sessionId) {
        synchronized (lock) {
            mSessionId = sessionId;
            mDbManager.setSessionSetting(Enums.Session.DatabaseKey.SESSION_ID, sessionId);
        }
    }

    @Nullable
    @Override
    public String getSelectedTenant() {
        if (!TextUtils.isEmpty(mSelectedTenant)) {
            return mSelectedTenant;
        }
        synchronized (lock) {
            if (TextUtils.isEmpty(mSelectedTenant)) {
                mSelectedTenant = mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.SELECTED_TENANT);
            }
        }
        return mSelectedTenant;
    }

    @Override
    public void setSelectedTenant(@Nullable String selectedTenant) {
        synchronized (lock) {
            mSelectedTenant = selectedTenant;
            mDbManager.setSessionSetting(Enums.Session.DatabaseKey.SELECTED_TENANT, selectedTenant);
        }
    }

    @Nullable
    @Override
    public UserInfo getUserInfo() {
        if (mUserInfo != null) {
            return mUserInfo;
        }
        synchronized (lock) {
            if (mUserInfo == null) {
                String userInfo = mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.USER_INFO);
                if (!TextUtils.isEmpty(userInfo)) {
                    try {
                        mUserInfo = GsonUtil.getObject(UserInfo.class, userInfo);
                    } catch (Exception e) {
                        LogUtil.e("KeyStoreManager", "Error decrypting USER_INFO: " + e.getMessage());
                        FirebaseCrashlytics.getInstance().recordException(e);
                        return null;
                    }
                }
            }
        }
        return mUserInfo;
    }

    @Override
    public void setUserInfo(@Nullable UserInfo userInfo) {
        synchronized (lock) {
            mUserInfo = userInfo;
            mDbManager.setSessionSetting(Enums.Session.DatabaseKey.USER_INFO, GsonUtil.getJSON(userInfo));
        }
    }

    @Nullable
    @Override
    public IdentityVoice getIdentityVoice() {
        if (mIdentityVoice != null) {
            return mIdentityVoice;
        }
        synchronized (lock) {
            if (mIdentityVoice == null) {
                String identityVoice = mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.IDENTITY_VOICE);
                if (!TextUtils.isEmpty(identityVoice)) {
                    try {
                        mIdentityVoice = GsonUtil.getObject(IdentityVoice.class, identityVoice);
                    } catch (Exception e) {
                        LogUtil.e("KeyStoreManager", "Error decrypting IDENTITY_VOICE: " + e.getMessage());
                        FirebaseCrashlytics.getInstance().recordException(e);
                        return null;
                    }
                }
            }
        }
        return mIdentityVoice;
    }

    @Override
    public void setIdentityVoice(@Nullable IdentityVoice identityVoice) {
        synchronized (lock) {
            mIdentityVoice = identityVoice;
            mDbManager.setSessionSetting(Enums.Session.DatabaseKey.IDENTITY_VOICE, GsonUtil.getJSON(identityVoice));
        }
    }

    @Override
    public boolean getIsLicenseApproved() {
        return mSharedPreferencesManager.getBoolean(SharedPreferencesManager.LICENSE_APPROVED, false);
    }

    @Override
    public void setIsLicenseApproved(boolean isLicenseApproved) {
        mSharedPreferencesManager.setBoolean(SharedPreferencesManager.LICENSE_APPROVED, isLicenseApproved);
    }

    @Nullable
    @Override
    public String getUsername() {
        if (!TextUtils.isEmpty(mUserName)) {
            return mUserName;
        }
        synchronized (lock) {
            if (TextUtils.isEmpty(mUserName)) {
                mUserName = mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.USERNAME);
            }
        }
        return mUserName;
    }

    @Override
    public void setUsername(@Nullable String username) {
        synchronized (lock) {
            mUserName = username;
            mDbManager.setSessionSetting(Enums.Session.DatabaseKey.USERNAME, username);
        }
    }

    @Nullable
    @Override
    public String getPassword() {
        if (!TextUtils.isEmpty(mPassword)) {
            return mPassword;
        }
        synchronized (lock) {
            if (TextUtils.isEmpty(mPassword)) {
                String encryptedPassword = mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.PASSWORD);
                if (encryptedPassword != null) {
                    mPassword = mKeyStoreManager.decryptString(NextivaKeyStoreManager.PASSWORD, encryptedPassword);
                }else {
                    // adding this to support issue in upgrade 26.10.0 -> 26.11.0, can be removed in later releases
                    mPassword = getLastLoggedPassword();
                }
            }
        }
        return mPassword;
    }

    @Override
    public void setPassword(@Nullable String password) {
        synchronized (lock) {
            if (TextUtils.isEmpty(password)) {
                mKeyStoreManager.deleteAlias(NextivaKeyStoreManager.PASSWORD);
                mDbManager.setSessionSetting(Enums.Session.DatabaseKey.PASSWORD, null);
                mPassword = "";
            } else {
                mPassword = password;
                mKeyStoreManager.addAlias(NextivaKeyStoreManager.PASSWORD);
                mDbManager.setSessionSetting(Enums.Session.DatabaseKey.PASSWORD, mKeyStoreManager.encryptString(NextivaKeyStoreManager.PASSWORD, password));
            }
        }
    }

    @Override
    public String getLastLoggedUsername() {
        return mSharedPreferencesManager.getString(SharedPreferencesManager.LAST_LOGGED_USERNAME, null);
    }

    @Override
    public void setLastLoggedUsername(String lastLoggedUsername) {
        mSharedPreferencesManager.setString(SharedPreferencesManager.LAST_LOGGED_USERNAME, lastLoggedUsername);
    }

    @Override
    public String getLastLoggedPassword() {
        return mKeyStoreManager.decryptString(NextivaKeyStoreManager.PASSWORD,
                                              mSharedPreferencesManager.getString(SharedPreferencesManager.LAST_LOGGED_PASSWORD, null));
    }

    @Override
    public void setLastLoggedPassword(String lastLoggedPassword) {
        if (TextUtils.isEmpty(lastLoggedPassword)) {
            mKeyStoreManager.deleteAlias(NextivaKeyStoreManager.PASSWORD);
            mSharedPreferencesManager.setString(SharedPreferencesManager.LAST_LOGGED_PASSWORD, null);

        } else {
            mKeyStoreManager.addAlias(NextivaKeyStoreManager.PASSWORD);
            mSharedPreferencesManager.setString(SharedPreferencesManager.LAST_LOGGED_PASSWORD,
                                                mKeyStoreManager.encryptString(NextivaKeyStoreManager.PASSWORD, lastLoggedPassword));
        }
    }

    @Override
    public boolean getRememberPassword() {
        return mSharedPreferencesManager.getBoolean(SharedPreferencesManager.REMEMBER_PASSWORD, false);
    }

    @Override
    public void setRememberPassword(boolean rememberPassword) {
        mSharedPreferencesManager.setBoolean(SharedPreferencesManager.REMEMBER_PASSWORD, rememberPassword);
    }

    @Nullable
    @Override
    public String getAccessDeviceUsername() {
        if (!TextUtils.isEmpty(mAccessDeviceUsername)) {
            return mAccessDeviceUsername;
        }
        synchronized (lock) {
            if (TextUtils.isEmpty(mAccessDeviceUsername)) {
                mAccessDeviceUsername = mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.ACCESS_DEVICE_USERNAME);
            }
        }
        return mAccessDeviceUsername;
    }

    @Override
    public void setAccessDeviceUsername(@Nullable String accessDeviceUsername) {
        synchronized (lock) {
            mAccessDeviceUsername = accessDeviceUsername;
            mDbManager.setSessionSetting(Enums.Session.DatabaseKey.ACCESS_DEVICE_USERNAME, accessDeviceUsername);
        }
    }

    @Nullable
    @Override
    public String getAccessDevicePassword() {
        if (!TextUtils.isEmpty(mAccessDevicePassword)) {
            return mAccessDevicePassword;
        }
        synchronized (lock) {
            if (TextUtils.isEmpty(mAccessDevicePassword)) {
                String encryptedPassword = mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.ACCESS_DEVICE_PASSWORD);
                if (encryptedPassword != null) {
                    mAccessDevicePassword = mKeyStoreManager.decryptString(NextivaKeyStoreManager.ACCESS_DEVICE_PASSWORD, encryptedPassword);
                }
            }
        }
        return mAccessDevicePassword;
    }

    @Override
    public void setAccessDevicePassword(@Nullable String accessDevicePassword) {
        synchronized (lock) {
            if (TextUtils.isEmpty(accessDevicePassword)) {
                mKeyStoreManager.deleteAlias(NextivaKeyStoreManager.ACCESS_DEVICE_PASSWORD);
                mDbManager.setSessionSetting(Enums.Session.DatabaseKey.ACCESS_DEVICE_PASSWORD, null);
                mAccessDevicePassword = "";

            } else {
                mAccessDevicePassword = accessDevicePassword;
                mDbManager.setSessionSetting(Enums.Session.DatabaseKey.ACCESS_DEVICE_PASSWORD, mKeyStoreManager.encryptString(NextivaKeyStoreManager.ACCESS_DEVICE_PASSWORD, accessDevicePassword));
                mKeyStoreManager.addAlias(NextivaKeyStoreManager.ACCESS_DEVICE_PASSWORD);
            }
        }
    }

    @Nullable
    @Override
    public String getAccessDeviceVersion() {
        return mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.ACCESS_DEVICE_VERSION);
    }

    @Override
    public void setAccessDeviceVersion(@Nullable String accessDeviceVersion) {
        mDbManager.setSessionSetting(Enums.Session.DatabaseKey.ACCESS_DEVICE_VERSION, accessDeviceVersion);
    }

    @Nullable
    @Override
    public String getAccessDeviceTypeUrl() {
        return mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.ACCESS_DEVICE_TYPE_URL);
    }

    @Override
    public void setAccessDeviceTypeUrl(@Nullable String accessDeviceTypeUrl) {
        mDbManager.setSessionSetting(Enums.Session.DatabaseKey.ACCESS_DEVICE_TYPE_URL, accessDeviceTypeUrl);
    }

    @Nullable
    @Override
    public String getAccessDeviceLinePort() {
        return mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.ACCESS_DEVICE_LINE_PORT);
    }

    @Override
    public void setAccessDeviceLinePort(@Nullable String accessDeviceLinePort) {
        mDbManager.setSessionSetting(Enums.Session.DatabaseKey.ACCESS_DEVICE_LINE_PORT, accessDeviceLinePort);
    }

    @Override
    public boolean getAllowTermination() {
        String value = mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.ALLOW_TERMINATION);
        return value == null || "true".equals(value);
    }

    @Override
    public void setAllowTermination(boolean allowTermination) {
        mDbManager.setSessionSetting(Enums.Session.DatabaseKey.ALLOW_TERMINATION, allowTermination ? "true" : "false");
    }

    @Nullable
    @Override
    public String getPushNotificationRegistrationId() {
        return mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.PUSH_NOTIFICATION_REGISTRATION_ID);
    }

    @Override
    public void setPushNotificationRegistrationId(@Nullable String registrationId) {
        mDbManager.setSessionSetting(Enums.Session.DatabaseKey.PUSH_NOTIFICATION_REGISTRATION_ID, registrationId);
    }

    @Nullable
    @Override
    public ServiceSettings getRemoteOfficeServiceSettings() {
        String serviceSettings = mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.REMOTE_OFFICE_SERVICE_SETTINGS);
        return !TextUtils.isEmpty(serviceSettings) ? GsonUtil.getObject(ServiceSettings.class, serviceSettings) : null;
    }

    @Override
    public void setRemoteOfficeServiceSettings(@Nullable ServiceSettings remoteOfficeServiceSettings) {
        mDbManager.setSessionSetting(Enums.Session.DatabaseKey.REMOTE_OFFICE_SERVICE_SETTINGS, GsonUtil.getJSON(remoteOfficeServiceSettings));
    }

    @Nullable
    @Override
    public ServiceSettings getNextivaAnywhereServiceSettings() {
        if (mNextivaAnywhereServiceSettings != null) {
            return mNextivaAnywhereServiceSettings;
        }
        synchronized (lock) {
            if (mNextivaAnywhereServiceSettings == null) {
                String serviceSettings = mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.NEXTIVA_ANYWHERE_SERVICE_SETTINGS);
                if (!TextUtils.isEmpty(serviceSettings)) {
                    mNextivaAnywhereServiceSettings = GsonUtil.getObject(ServiceSettings.class, serviceSettings);
                }
            }
        }
        return mNextivaAnywhereServiceSettings;
    }

    @Override
    public void setNextivaAnywhereServiceSettings(@Nullable ServiceSettings nextivaAnywhereServiceSettings) {
        synchronized (lock) {
            mNextivaAnywhereServiceSettings = nextivaAnywhereServiceSettings;
            mDbManager.setSessionSetting(Enums.Session.DatabaseKey.NEXTIVA_ANYWHERE_SERVICE_SETTINGS, GsonUtil.getJSON(nextivaAnywhereServiceSettings));
        }
    }

    @Override
    public boolean getIsCallBackEnabled(@Nullable ServiceSettings remoteOfficeServiceSettings, @Nullable ServiceSettings nextivaAnywhereServiceSettings) {
        boolean isRemoteOfficeSetup = false;
        boolean isNextivaAnywhereSetup = false;

        if (remoteOfficeServiceSettings != null) {
            isRemoteOfficeSetup = remoteOfficeServiceSettings.getActive() &&
                    !TextUtils.isEmpty(remoteOfficeServiceSettings.getRemoteOfficeNumber());
        }

        if (nextivaAnywhereServiceSettings != null && nextivaAnywhereServiceSettings.getNextivaAnywhereLocationsList() != null) {
            for (NextivaAnywhereLocation location : nextivaAnywhereServiceSettings.getNextivaAnywhereLocationsList()) {
                if (location != null && location.getActive()) {
                    isNextivaAnywhereSetup = true;
                    break;
                }
            }
        }

        return isRemoteOfficeSetup || isNextivaAnywhereSetup;
    }

    @Override
    public boolean getIsCallThroughEnabled(@Nullable ServiceSettings nextivaAnywhereServiceSettings, @Nullable String thisPhoneNumber) {
        if (!TextUtils.isEmpty(thisPhoneNumber) &&
                nextivaAnywhereServiceSettings != null &&
                nextivaAnywhereServiceSettings.getNextivaAnywhereLocationsList() != null) {

            for (NextivaAnywhereLocation location : nextivaAnywhereServiceSettings.getNextivaAnywhereLocationsList()) {
                if (location != null &&
                        !TextUtils.isEmpty(location.getPhoneNumber()) &&
                        TextUtils.equals(CallUtil.cleanPhoneNumber(thisPhoneNumber), CallUtil.cleanPhoneNumber(location.getPhoneNumber()))) {

                    return true;
                }
            }
        }

        return false;
    }

    @Nullable
    @Override
    public String getLastDialedPhoneNumber() {
        return mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.LAST_DIALED_PHONE_NUMBER);
    }

    @Override
    public void setLastDialedPhoneNumber(@Nullable String phoneNumber) {
        mDbManager.setSessionSetting(Enums.Session.DatabaseKey.LAST_DIALED_PHONE_NUMBER, phoneNumber);
    }

    @Nullable
    @Override
    public FeatureAccessCodes getFeatureAccessCodes() {
        return GsonUtil.getObject(FeatureAccessCodes.class, mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.FEATURE_ACCESS_CODES));
    }

    @Override
    public void setFeatureAccessCodes(FeatureAccessCodes featureAccessCodes) {
        mDbManager.setSessionSetting(Enums.Session.DatabaseKey.FEATURE_ACCESS_CODES, GsonUtil.getJSON(featureAccessCodes));
    }

    @Override
    public byte[] getOwnAvatar() {
        String avatarString = mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.USER_AVATAR);
        return !TextUtils.isEmpty(avatarString) ? mAvatarManager.stringToByteArray(avatarString) : null;
    }

    @Override
    public int getNewVoicemailMessagesCount() {
        if (mUnreadVoicemails != 0) {
            return mUnreadVoicemails;
        }
        synchronized (lock) {
            if (mUnreadVoicemails == 0) {
                try {
                    mUnreadVoicemails = Integer.parseInt(mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.NEW_VOICEMAIL_MESSAGES_COUNT));
                } catch (NumberFormatException e) {
                    mUnreadVoicemails = 0;
                }
            }
        }
        return mUnreadVoicemails;
    }

    @Override
    public void setNewVoicemailMessagesCount(int newVoicemailMessagesCount) {
        synchronized (lock) {
            mUnreadVoicemails = newVoicemailMessagesCount;
            mDbManager.setSessionSetting(Enums.Session.DatabaseKey.NEW_VOICEMAIL_MESSAGES_COUNT, newVoicemailMessagesCount != 0 ? String.valueOf(newVoicemailMessagesCount) : "");
        }
    }

    @NonNull
    @Override
    public LiveData<DbSession> getNewVoicemailMessagesCountLiveData() {
        return mDbManager.getSessionLiveDataFromKey(Enums.Session.DatabaseKey.NEW_VOICEMAIL_MESSAGES_COUNT);
    }

    @NonNull
    @Override
    public LiveData<Integer> getNewVoiceCallMessagesCountLiveData() {
        return mDbManager.getUnreadMissedCallLogEntriesCount();
    }

    @Override
    public LiveData<DbSession> getSmsMessagesCountLiveData() {
        return mDbManager.getSessionLiveDataFromKey(Enums.Session.DatabaseKey.NEW_SMS_MESSAGES_COUNT);
    }

    @Override
    public LiveData<Integer> getRoomsMessagesCountLiveData(List<String> types) {
        return mRoomsDbManager.getUnreadMessageLiveData(types);
    }

    @Override
    public int getChatMessagesCount() {
        if (mUnreadChatMessages != 0) {
            return mUnreadChatMessages;
        }
        synchronized (lock) {
            if (mUnreadChatMessages == 0) {
                try {
                    mUnreadChatMessages = Integer.parseInt(mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.NEW_CHAT_MESSAGES_COUNT));
                } catch (NumberFormatException e) {
                    mUnreadChatMessages = 0;
                }
            }
        }
        return mUnreadChatMessages;
    }

    @Override
    public int getTotalUnreadNotificationsCount() {
        if (mTotalUnreadNotificationsCountCached != 0) {
            return mTotalUnreadNotificationsCountCached;
        }
        synchronized (lock) {
            if (mTotalUnreadNotificationsCountCached == 0) {
                mTotalUnreadNotificationsCountCached = getVoiceCallMessagesCount() + getNewVoicemailMessagesCount() + getChatMessagesCount() + getSmsMessagesCount() + getRoomsMessagesCount();
            }
        }
        return mTotalUnreadNotificationsCountCached;
    }

    @Override
    public int getSmsMessagesCount() {
        if (mUnreadSmsMessages != 0) {
            return mUnreadSmsMessages;
        }
        synchronized (lock) {
            if (mUnreadSmsMessages == 0) {
                try {
                    mUnreadSmsMessages = Integer.parseInt(mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.NEW_SMS_MESSAGES_COUNT));
                } catch (NumberFormatException e) {
                    mUnreadSmsMessages = 0;
                }
            }
        }
        return mUnreadSmsMessages;
    }

    @Override
    public int getVoiceCallMessagesCount() {
        if (mUnreadMissedCalls != 0) {
            return mUnreadMissedCalls;
        }
        synchronized (lock) {
            if (mUnreadMissedCalls == 0) {
                try {
                    mUnreadMissedCalls = Integer.parseInt(mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.NEW_VOICE_CALL_MESSAGES_COUNT));
                } catch (NumberFormatException e) {
                    mUnreadMissedCalls = 0;
                }
            }
        }
        return mUnreadMissedCalls;
    }

    @Override
    public int getRoomsMessagesCount() {
        if (mUnreadRoomsMessages != 0) {
            return mUnreadRoomsMessages;
        }

        if (mUnreadRoomsMessages == 0) {
            try {
                mUnreadRoomsMessages = mRoomsDbManager.getUnreadMessageCountInThread();
            } catch (NumberFormatException e) {
                    mUnreadRoomsMessages = 0;
            }
        }

        return mUnreadRoomsMessages;
    }

    @Override
    public LiveData<List<DbSession>> getChatSmsCount() {
        return mDbManager.getSessionLiveDataFromMultipleKeys(Arrays.asList(Enums.Session.DatabaseKey.NEW_CHAT_MESSAGES_COUNT, Enums.Session.DatabaseKey.NEW_SMS_MESSAGES_COUNT));
    }

    @Override
    public LiveData<List<DbSession>> getVoiceCallVoicemailCount() {
        return mDbManager.getSessionLiveDataFromMultipleKeys(Arrays.asList(Enums.Session.DatabaseKey.NEW_VOICE_CALL_MESSAGES_COUNT, Enums.Session.DatabaseKey.NEW_VOICEMAIL_MESSAGES_COUNT));
    }

    @NonNull
    @Override
    public LiveData<Integer> getTotalUnreadNotificationCountLiveData(@NonNull List<String> keys) {
        return mDbManager.getTotalUnreadNotificationsLiveDataFromMultipleKeys(keys);
    }

    @NonNull
    @Override
    public LiveData<Integer> getTotalUnreadNotificationCountLiveData() {
        return mTotalUnreadNotificationsCountLiveData;
    }

    @Override
    public void updateNotificationsCount(ConversationRepository conversationRepository, Context context) {
        LogUtil.d("updateNotificationsCount");
        conversationRepository.getChannelMessagesCount(Enums.Messages.Channels.SMS, Enums.Messages.ReadStatus.UNREAD)
                .subscribe(channelMessagesCountSingleObserver(Enums.Messages.Channels.SMS, context));

        // disabled until this feature is working again on app
        /*
        if (isTeamchatEnabled(context)) {
            conversationRepository.getChannelMessagesCount(Enums.Messages.Channels.CHAT, Enums.Messages.ReadStatus.UNREAD)
                    .subscribe(channelMessagesCountSingleObserver(Enums.Messages.Channels.CHAT, context));
        }
        */

        conversationRepository.getChannelMessagesCount(Enums.Messages.Channels.VOICE, Enums.Messages.ReadStatus.UNREAD)
                .subscribe(channelMessagesCountSingleObserver(Enums.Messages.Channels.VOICE, context));

        conversationRepository.getChannelMessagesCount(Enums.Messages.Channels.VOICEMAIL, Enums.Messages.ReadStatus.UNREAD)
                .subscribe(channelMessagesCountSingleObserver(Enums.Messages.Channels.VOICEMAIL, context));

        // disabled until this feature is working again on app
        /*
        if (isTeamchatEnabled(context)) {
            mRoomsDbManager.getUnreadMessageCount()
                    .subscribe(channelMessagesCountSingleObserver(Enums.Messages.Channels.ROOMS, context));
        }
        */
    }

    public void deleteMessageCountCacheVoicemail(ConversationRepository conversationRepository) {
        conversationRepository.deleteMessagesCountCache(Enums.Messages.Channels.VOICEMAIL).subscribe();
    }

    private DisposableSingleObserver<Integer> channelMessagesCountSingleObserver(@Enums.Messages.Channels.Channel String type, Context context) {
        return new DisposableSingleObserver<Integer>() {
            @Override
            public void onSuccess(Integer count) {
                synchronized (lock) {
                    switch (type) {
                        case Enums.Messages.Channels.VOICE:
                            mUnreadMissedCalls = count;
                            mDbManager.updateUnreadMissedCallCount(count);
                            break;
                        case Enums.Messages.Channels.VOICEMAIL:
                            mUnreadVoicemails = count;
                            mDbManager.updateUnreadVoicemailCount(count);
                            break;
                        case Enums.Messages.Channels.SMS:
                            mUnreadSmsMessages = count;
                            mDbManager.updateUnreadSMSCount(count);
                            break;
                        case Enums.Messages.Channels.CHAT:
                            mUnreadChatMessages = count;
                            mDbManager.updateUnreadChatCount(count);
                            break;
                        case Enums.Messages.Channels.ROOMS:
                            mUnreadRoomsMessages = count;
                            break;
                        default:
                            LogUtil.e("Channel Messages Count Error type: " + type);
                    }
                    mTotalUnreadNotificationsCountLiveData = mDbManager.getTotalUnreadNotificationsLiveDataFromMultipleKeys(Arrays.asList(
                            Enums.Session.DatabaseKey.NEW_CHAT_MESSAGES_COUNT,
                            Enums.Session.DatabaseKey.NEW_SMS_MESSAGES_COUNT,
                            Enums.Session.DatabaseKey.NEW_VOICEMAIL_MESSAGES_COUNT,
                            Enums.Session.DatabaseKey.NEW_VOICE_CALL_MESSAGES_COUNT));

                    mTotalUnreadNotificationsCountCached = getVoiceCallMessagesCount() + getNewVoicemailMessagesCount() + getChatMessagesCount() + getSmsMessagesCount() + getRoomsMessagesCount();

                    try {
                        ShortcutBadger.applyCountOrThrow(context.getApplicationContext(), mTotalUnreadNotificationsCountCached);
                    } catch (ShortcutBadgeException e) {
                        LogUtil.e("ShortcutBadger Notification Count Error: " + e);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                FirebaseCrashlytics.getInstance().recordException(e);
            }
        };
    }

    @Override
    public void tempIncreaseChannelMessagesCount(@Enums.Messages.Channels.Channel String type, Integer tempIncreaseAmount, Context context) {
        synchronized (lock) {
            switch (type) {
                case Enums.Messages.Channels.VOICE:
                    mUnreadMissedCalls += tempIncreaseAmount;
                    mDbManager.updateUnreadMissedCallCount(mUnreadMissedCalls);
                    break;
                case Enums.Messages.Channels.VOICEMAIL:
                    mUnreadVoicemails = tempIncreaseAmount;
                    mDbManager.updateUnreadVoicemailCount(mUnreadVoicemails);
                    break;
                case Enums.Messages.Channels.SMS:
                    mUnreadSmsMessages = tempIncreaseAmount;
                    mDbManager.updateUnreadSMSCount(mUnreadSmsMessages);
                    break;
                case Enums.Messages.Channels.CHAT:
                    mUnreadChatMessages = tempIncreaseAmount;
                    mDbManager.updateUnreadChatCount(mUnreadChatMessages);
                    break;
                default:
                    LogUtil.e("Channel Messages Temp Count Error");
            }
            mTotalUnreadNotificationsCountLiveData = mDbManager.getTotalUnreadNotificationsLiveDataFromMultipleKeys(Arrays.asList(
                    Enums.Session.DatabaseKey.NEW_CHAT_MESSAGES_COUNT,
                    Enums.Session.DatabaseKey.NEW_SMS_MESSAGES_COUNT,
                    Enums.Session.DatabaseKey.NEW_VOICEMAIL_MESSAGES_COUNT,
                    Enums.Session.DatabaseKey.NEW_VOICE_CALL_MESSAGES_COUNT));

            mTotalUnreadNotificationsCountCached = getVoiceCallMessagesCount() + getNewVoicemailMessagesCount() + getChatMessagesCount() + getSmsMessagesCount() + getRoomsMessagesCount();

            try {
                LogUtil.d("tempIncreaseChannelMessagesCount Badge: " + mTotalUnreadNotificationsCountCached);
                ShortcutBadger.applyCountOrThrow(context.getApplicationContext(), mTotalUnreadNotificationsCountCached);
            } catch (ShortcutBadgeException e) {
                LogUtil.e("ShortcutBadger Notification Count Error: " + e);
            }
        }
    }

    @Override
    public String getAuthorizationHeader() {
        return "Basic " + Base64.encodeToString((getUsername() + ":" + getPassword()).getBytes(), Base64.NO_WRAP);
    }

    @Override
    public String getUmsHost() {
        return mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.UMS_HOST);
    }

    @Override
    public void setUmsHost(@Nullable String umsHost) {
        mDbManager.setSessionSetting(Enums.Session.DatabaseKey.UMS_HOST, umsHost);
    }

    @Override
    public boolean isSmsEnabled() {
        Products products = getProducts();
        PhoneNumberInformation phoneNumberInformation = getPhoneNumberInformation();
        boolean isProductsEnabled = products != null && products.isFeatureEnabled(Enums.License.Features.SMS_BASE);
        boolean isPhoneNumberInformationSmsEnabled = phoneNumberInformation != null && phoneNumberInformation.getMetadata() != null &&
                phoneNumberInformation.getMetadata().getSmsEnabled() != null && phoneNumberInformation.getMetadata().getSmsEnabled();


        FeatureFlags featureFlags = getFeatureFlags();
        boolean isSMSFeatureFlagEnabled = featureFlags != null && featureFlags.isFeatureEnabled(Enums.Platform.FeatureFlags.SMS);

        return isSMSFeatureFlagEnabled && isProductsEnabled && isPhoneNumberInformationSmsEnabled;

    }

    @Override
    public boolean isTeamSmsLicenseEnabled() {
        return getProducts() != null && getProducts().isFeatureEnabled(Enums.License.Features.TEAM_SMS);
    }

    @Override
    public boolean isTeamSmsEnabled() {
        if (isTeamSmsLicenseEnabled()) {
            for (SmsTeam smsTeam : getUsersTeams()) {
                if (Boolean.TRUE.equals(smsTeam.getSmsEnabled())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean canSendSms() {
        return isSmsEnabled() || isTeamSmsEnabled();
    }

    @Override
    public boolean shouldBroadsoftChatShow() {
        FeatureFlags featureFlags = getFeatureFlags();
        return featureFlags != null && !featureFlags.isFeatureEnabled(Enums.Platform.FeatureFlags.TEAM_CHAT);
    }

    @Override
    public boolean isTeamchatEnabled(Context context) {
        FeatureFlags featureFlags = getFeatureFlags();
        return featureFlags != null && featureFlags.isFeatureEnabled(Enums.Platform.FeatureFlags.TEAM_CHAT) &&
                !isProdOrRC(context);
    }

    @Override
    public boolean isSmsLicenseEnabled() {
        return getProducts() != null && getProducts().isFeatureEnabled(Enums.License.Features.SMS_BASE);
    }

    @Override
    public boolean isSmsProvisioningEnabled() {
        PhoneNumberInformation phoneNumberInformation = getPhoneNumberInformation();
        return phoneNumberInformation != null && phoneNumberInformation.getMetadata() != null &&
                phoneNumberInformation.getMetadata().getSmsEnabled() != null && phoneNumberInformation.getMetadata().getSmsEnabled();
    }

    @Override
    public boolean isShowSms() {
        FeatureFlags featureFlags = getFeatureFlags();
        return featureFlags != null && featureFlags.isFeatureEnabled(Enums.Platform.FeatureFlags.SMS);
    }

    @Override
    public boolean isVoicemailTranscriptionEnabled() {
        FeatureFlags featureFlags = getFeatureFlags();
        Products products = getProducts();
        return featureFlags != null && featureFlags.isFeatureEnabled(Enums.Platform.FeatureFlags.VISUAL_VOICE_MAIL) &&
                products != null && products.isFeatureEnabled(Enums.License.Features.VOICEMAIL_TRANSCRIPTION);
    }

    @Override
    public boolean isNextivaConnectEnabled() {
        return true;
    }

    @Nullable
    @Override
    public FeatureFlags getFeatureFlags() {
        if (mFeatureFlags != null) {
            return mFeatureFlags;
        }
        synchronized (lock) {
            if (mFeatureFlags == null) {
                mFeatureFlags = GsonUtil.getObject(FeatureFlags.class, mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.FEATURE_FLAGS));
            }
        }
        return mFeatureFlags != null ? mFeatureFlags : new FeatureFlags(new ArrayList<>());
    }

    @Override
    public void setFeatureFlags(FeatureFlags featureFlags) {
        synchronized (lock) {
            mFeatureFlags = featureFlags;
            mDbManager.setSessionSetting(Enums.Session.DatabaseKey.FEATURE_FLAGS, GsonUtil.getJSON(featureFlags));
        }
    }

    @Nullable
    @Override
    public AccountInformation getAccountInformation() {
        AccountInformation accountInformation = GsonUtil.getObject(AccountInformation.class, mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.ACCOUNT_INFORMATION));
        return accountInformation != null ? accountInformation : new AccountInformation();
    }

    @Override
    public void setAccountInformation(AccountInformation accountInformation) {
        mDbManager.setSessionSetting(Enums.Session.DatabaseKey.ACCOUNT_INFORMATION, GsonUtil.getJSON(accountInformation));
    }

    @Override
    public List<SmsTeam> getUsersTeams() {
        SmsTeam[] teams = GsonUtil.getObject(SmsTeam[].class, mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.USERS_TEAMS));
        return teams != null ? Arrays.asList(teams) : new ArrayList<>();
    }

    @Override
    public void setUsersTeams(ArrayList<SmsTeam> teams) {
        synchronized (lock) {
            mDbManager.setSessionSetting(Enums.Session.DatabaseKey.USERS_TEAMS, GsonUtil.getJSON(teams));
        }
    }

    @Override
    public List<SmsTeam> getAllTeams() {
        SmsTeam[] teams = GsonUtil.getObject(SmsTeam[].class, mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.ALL_TEAMS));
        return teams != null ? Arrays.asList(teams) : new ArrayList<>();
    }

    @Override
    public void setAllTeams(ArrayList<SmsTeam> teams) {
        synchronized (lock) {
            mDbManager.setSessionSetting(Enums.Session.DatabaseKey.ALL_TEAMS, GsonUtil.getJSON(teams));
        }
    }

    @Override
    public PhoneNumberInformation getPhoneNumberInformation() {
        PhoneNumberInformation phoneInformation = GsonUtil.getObject(PhoneNumberInformation.class, mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.PHONE_NUMBER_INFORMATION));
        return phoneInformation != null ? phoneInformation : new PhoneNumberInformation();
    }

    @Override
    public void setPhoneNumberInformation(PhoneNumberInformation information) {
        synchronized (lock) {
            mDbManager.setSessionSetting(Enums.Session.DatabaseKey.PHONE_NUMBER_INFORMATION, GsonUtil.getJSON(information));
        }
    }

    @Override
    public boolean isCustomToneEnabled() {
        FeatureFlags featureFlags = getFeatureFlags();
        return featureFlags != null && featureFlags.isFeatureEnabled(Enums.Platform.FeatureFlags.CUSTOM_TONE);
    }

    @Override
    public boolean isVoiceLargePageEnabled() {
        FeatureFlags featureFlags = getFeatureFlags();
        return featureFlags != null && featureFlags.isFeatureEnabled(Enums.Platform.FeatureFlags.VOICE_LARGE_PAGE);
    }

    @Override
    public boolean isCommunicationsBulkDeletesEnabled() {
        FeatureFlags featureFlags = getFeatureFlags();
        return featureFlags != null && featureFlags.isFeatureEnabled(Enums.Platform.FeatureFlags.COMMUNICATIONS_BULK_DELETES);
    }

    @Override
    public boolean isCommunicationsBulkUpdatesEnabled() {
        FeatureFlags featureFlags = getFeatureFlags();
        return featureFlags != null && featureFlags.isFeatureEnabled(Enums.Platform.FeatureFlags.COMMUNICATIONS_BULK_UPDATES);
    }

    @Override
    public boolean isCommunicationsBulkActionsUpdateEnabled() {
        FeatureFlags featureFlags = getFeatureFlags();
        return featureFlags != null && featureFlags.isFeatureEnabled(Enums.Platform.FeatureFlags.COMMUNICATIONS_BULK_ACTIONS_UPDATE);
    }

    @Override
    public boolean isSmsCampaignValidationEnabled() {
        FeatureFlags featureFlags = getFeatureFlags();
        return featureFlags != null && featureFlags.isFeatureEnabled(Enums.Platform.FeatureFlags.SMS_CAMPAIGN_VALIDATION);
    }

    @Override
    public boolean isBlockNumberForCallingEnabled() {
        FeatureFlags featureFlags = getFeatureFlags();
        return featureFlags != null && featureFlags.isFeatureEnabled(Enums.Platform.FeatureFlags.BLOCK_NUMBER_FOR_CALLING);
    }

    @Override
    public Products getProducts() {
        if (mProducts != null) {
            return mProducts;
        }
        synchronized (lock) {
            if (mProducts == null) {
                mProducts = GsonUtil.getObject(Products.class, mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.PRODUCTS));
            }
        }
        return mProducts != null ? mProducts : new Products(new ArrayList<>());
    }

    @Override
    public void setProducts(Products products) {
        synchronized (lock) {
            mProducts = products;
            mDbManager.setSessionSetting(Enums.Session.DatabaseKey.PRODUCTS, GsonUtil.getJSON(products));
        }
    }

    @Override
    public boolean hasContactManagementPrivilege() {
        return "true".equals(mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.CONTACT_MANAGEMENT_PRIVILEGE));
    }

    @Override
    public void setContactManagementPrivilege(boolean privilege) {
        mDbManager.setSessionSetting(Enums.Session.DatabaseKey.CONTACT_MANAGEMENT_PRIVILEGE, privilege ? "true" : "false");
    }

    @Nullable
    @Override
    public CurrentUser getCurrentUser() {
        CurrentUser currentUser = GsonUtil.getObject(CurrentUser.class, mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.CURRENT_USER));
        return currentUser != null ? currentUser : new CurrentUser();
    }

    @Override
    public Flow<DbSession> getEnabledAudioCodecsFlow() {
        return mDbManager.getSessionFlowFromKey(Enums.Session.DatabaseKey.ENABLED_AUDIO_CODECS);
    }

    @Override
    public EnabledCodecs getEnabledAudioCodecs() {
        return GsonUtil.getObject(EnabledCodecs.class, mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.ENABLED_AUDIO_CODECS));
    }

    @Override
    public void setEnabledAudioCodecs(EnabledCodecs audioCodecs) {
        mDbManager.setSessionSetting(Enums.Session.DatabaseKey.ENABLED_AUDIO_CODECS, GsonUtil.getJSON(audioCodecs));
    }

    @Override
    public boolean isMeetingEnabled(Context context) {
        return (getFeatureFlags() != null &&
                getFeatureFlags().isFeatureEnabled(Enums.Platform.FeatureFlags.VIDEO_CALLS) &&
                !isProdOrRC(context));
    }

    public boolean isProdOrRC(Context context) {
        boolean isProd = (TextUtils.equals(context.getString(R.string.app_environment), context.getString(R.string.environment_prod)));
        boolean isProdBeta = (TextUtils.equals(context.getString(R.string.app_environment), context.getString(R.string.environment_prodBeta)));
        boolean isRC = (TextUtils.equals(context.getString(R.string.app_environment), context.getString(R.string.environment_rc)));
        return isProd || isProdBeta || isRC;
    }

    @Override
    public Flow<DbSession> getEchoCancellationFlow() {
        return mDbManager.getSessionFlowFromKey(Enums.Session.DatabaseKey.ECHO_CANCELLATION);
    }

    @Override
    public String getEchoCancellation() {
        return mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.ECHO_CANCELLATION);
    }

    @Override
    public void setEchoCancellation(int echoCancellation) {
        mDbManager.setSessionSetting(Enums.Session.DatabaseKey.ECHO_CANCELLATION, String.valueOf(echoCancellation));
    }

    @Override
    public Flow<DbSession> getAECAggressivenessFlow() {
        return mDbManager.getSessionFlowFromKey(Enums.Session.DatabaseKey.AEC_AGGRESSIVENESS);
    }

    @Override
    public String getAECAggressiveness() {
        return mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.AEC_AGGRESSIVENESS);
    }

    @Override
    public void setAECAggressiveness(int aecAggressiveness) {
        mDbManager.setSessionSetting(Enums.Session.DatabaseKey.AEC_AGGRESSIVENESS, String.valueOf(aecAggressiveness));
    }

    @Override
    public boolean isNoiseSuppressionEnabled() {
        return "true".equals(mDbManager.getSessionSettingValue(Enums.Session.DatabaseKey.NOISE_SUPPRESSION));
    }

    @Override
    public void setNoiseSuppressionEnabled(boolean privilege) {
        mDbManager.setSessionSetting(Enums.Session.DatabaseKey.NOISE_SUPPRESSION, privilege ? "true" : "false");
    }
}