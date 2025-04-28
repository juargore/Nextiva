/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers;

import android.os.Build;

import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager;
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by adammacdonald on 3/19/18.
 */


@Singleton
public class NextivaSettingsManager implements SettingsManager {

    private final SharedPreferencesManager mSharedPreferencesManager;

    @Inject
    public NextivaSettingsManager(SharedPreferencesManager sharedPreferencesManager) {
        mSharedPreferencesManager = sharedPreferencesManager;
    }

    // --------------------------------------------------------------------------------------------
    // SettingsManager Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void clearCache() {
        setEnableLogging(false);
        setFileLogging(false);
        setXMPPLogging(false);
        setCrashReporting(false);
        setDisplayAudioVideoStats(false);
        setOldActiveCallLayoutEnabled(false);
        setDisplaySIPState(false);
        setDisplaySIPError(false);
    }

    @Override
    public boolean getEnableLogging() {
        return mSharedPreferencesManager.getBoolean(SharedPreferencesManager.ENABLE_LOGGING, false);
    }

    @Override
    public void setEnableLogging(boolean enableLogging) {
        mSharedPreferencesManager.setBoolean(SharedPreferencesManager.ENABLE_LOGGING, enableLogging);
    }

    @Override
    public boolean getFileLogging() {
        return mSharedPreferencesManager.getBoolean(SharedPreferencesManager.FILE_LOGGING, false);
    }

    @Override
    public void setFileLogging(boolean fileLogging) {
        mSharedPreferencesManager.setBoolean(SharedPreferencesManager.FILE_LOGGING, fileLogging);
    }

    @Override
    public boolean getXMPPLogging() {
        return mSharedPreferencesManager.getBoolean(SharedPreferencesManager.XMPP_LOGGING, false);
    }

    @Override
    public void setXMPPLogging(boolean xmppLogging) {
        mSharedPreferencesManager.setBoolean(SharedPreferencesManager.XMPP_LOGGING, xmppLogging);
    }

    @Override
    public boolean getSipLogging() {
        return mSharedPreferencesManager.getBoolean(SharedPreferencesManager.SIP_LOGGING, false);
    }

    @Override
    public void setSipLogging(boolean sipLogging) {
        mSharedPreferencesManager.setBoolean(SharedPreferencesManager.SIP_LOGGING, sipLogging);
    }

    @Override
    public boolean getCrashReporting() {
        return mSharedPreferencesManager.getBoolean(SharedPreferencesManager.CRASH_REPORTING, false);
    }

    @Override
    public void setCrashReporting(boolean crashReportingPref) {
        mSharedPreferencesManager.setBoolean(NextivaSharedPreferencesManager.CRASH_REPORTING, crashReportingPref);
    }

    @Override
    public boolean getDiagnosticPref() {
        return mSharedPreferencesManager.getBoolean(SharedPreferencesManager.DIAGNOSTIC_INFO, false);
    }

    @Override
    public void setDiagnosticPref(boolean diagnosticPref) {
        mSharedPreferencesManager.setBoolean(SharedPreferencesManager.DIAGNOSTIC_INFO, diagnosticPref);
    }

    @Override
    @Enums.Service.DialingServiceTypes.DialingServiceType
    public int getDialingService() {
        return mSharedPreferencesManager.getInt(SharedPreferencesManager.DIALING_SERVICE, Enums.Service.DialingServiceTypes.VOIP);
    }

    @Override
    public void setDialingService(@Enums.Service.DialingServiceTypes.DialingServiceType int dialingServiceType) {
        mSharedPreferencesManager.setInt(SharedPreferencesManager.DIALING_SERVICE, dialingServiceType);
    }

    @Override
    public String getPhoneNumber() {
        return mSharedPreferencesManager.getString(SharedPreferencesManager.THIS_PHONE_NUMBER, null);
    }

    @Override
    public void setPhoneNumber(String phoneNumber) {
        mSharedPreferencesManager.setString(SharedPreferencesManager.THIS_PHONE_NUMBER, phoneNumber);
    }

    @Override
    public String getCallCenterStatus() {
        return mSharedPreferencesManager.getString(SharedPreferencesManager.CALL_CENTER_STATUS, "");
    }

    @Override
    public void setCallCenterStatus(String callCenterStatus) {
        mSharedPreferencesManager.setString(SharedPreferencesManager.CALL_CENTER_STATUS, callCenterStatus);
    }

    @Override
    public String getCallCenterUnavailableCodes() {
        return mSharedPreferencesManager.getString(SharedPreferencesManager.CALL_CENTER_UNAVAILABLE_CODES, "");
    }

    @Override
    public void setCallCenterUnavailableCodes(String callCenterUnavailableCodes) {
        mSharedPreferencesManager.setString(SharedPreferencesManager.CALL_CENTER_UNAVAILABLE_CODES, callCenterUnavailableCodes);
    }

    @Override
    public boolean getDisplayAudioVideoStats() {
        return mSharedPreferencesManager.getBoolean(SharedPreferencesManager.AUDIO_VIDEO_STATS, false);
    }

    @Override
    public void setDisplayAudioVideoStats(boolean audioVideoStats) {
        mSharedPreferencesManager.setBoolean(SharedPreferencesManager.AUDIO_VIDEO_STATS, audioVideoStats);
    }

    @Override
    public void setOldActiveCallLayoutEnabled(boolean shouldShowOldActiveCallState) {
        mSharedPreferencesManager.setBoolean(SharedPreferencesManager.ENABLE_OLD_ACTIVE_CALL_LAYOUT, shouldShowOldActiveCallState);
    }

    @Override
    public boolean getIsOldActiveCallLayoutEnabled() {
        return mSharedPreferencesManager.getBoolean(SharedPreferencesManager.ENABLE_OLD_ACTIVE_CALL_LAYOUT, false);
    }


    @Override
    public boolean getDisplaySIPState() {
        return mSharedPreferencesManager.getBoolean(SharedPreferencesManager.SIP_STATE, false);
    }

    @Override
    public void setDisplaySIPState(boolean sipState) {
        mSharedPreferencesManager.setBoolean(SharedPreferencesManager.SIP_STATE, sipState);
    }

    @Override
    public boolean getDisplaySIPError() {
        return mSharedPreferencesManager.getBoolean(SharedPreferencesManager.SIP_ERROR, false);
    }

    @Override
    public void setDisplaySIPError(boolean sipError) {
        mSharedPreferencesManager.setBoolean(SharedPreferencesManager.SIP_ERROR, sipError);
    }

    @Override
    public boolean isSipEchoCancellationEnabled() {
        return mSharedPreferencesManager.getBoolean(SharedPreferencesManager.SIP_ECHO_CANCELLATION, false);
    }

    @Override
    public boolean isShowDialogToDeleteSmsEnabled() {
        return mSharedPreferencesManager.getBoolean(SharedPreferencesManager.SMS_DELETE_SMS_CONFIRMATION_DIALOG, true);
    }

    @Override
    public boolean isSwipeActionsEnabled() {
        return mSharedPreferencesManager.getBoolean(SharedPreferencesManager.SMS_SWIPE_ACTIONS_ENABLED, true);
    }

    @Override
    public boolean isBlockNumberForCallingEnabled() {
        return mSharedPreferencesManager.getBoolean(SharedPreferencesManager.BLOCK_NUMBER_FOR_CALLING_ENABLED, true);
    }

    @Override
    public void setSipEchoCancellationEnabled(boolean enabled) {
        mSharedPreferencesManager.setBoolean(SharedPreferencesManager.SIP_ECHO_CANCELLATION, enabled);
    }

    @Override
    public void setShowDialogToDeleteSmsEnabled(boolean enabled) {
        mSharedPreferencesManager.setBoolean(SharedPreferencesManager.SMS_DELETE_SMS_CONFIRMATION_DIALOG, enabled);
    }

    @Override
    public void setSwipeActionsEnabled(boolean enabled) {
        mSharedPreferencesManager.setBoolean(SharedPreferencesManager.SMS_SWIPE_ACTIONS_ENABLED, enabled);
    }

    @Override
    public void setBlockNumberForCallingEnabled(boolean enabled) {
        mSharedPreferencesManager.setBoolean(SharedPreferencesManager.BLOCK_NUMBER_FOR_CALLING_ENABLED, enabled);
    }

    @Override
    @Enums.Session.NightModeState.State
    public String getNightModeState() {
        return mSharedPreferencesManager.getString(SharedPreferencesManager.NIGHT_MODE, Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ? Enums.Session.NightModeState.NIGHT_MODE_STATE_SYSTEM_DEFAULT : Enums.Session.NightModeState.NIGHT_MODE_STATE_AUTO);
    }

    @Override
    public void setNightModeState(@Enums.Session.NightModeState.State String nightModeState) {
        mSharedPreferencesManager.setString(SharedPreferencesManager.NIGHT_MODE, nightModeState);
    }
    // --------------------------------------------------------------------------------------------
}
