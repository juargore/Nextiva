/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers.interfaces;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.constants.Enums;

/**
 * Created by adammacdonald on 3/19/18.
 */

public interface SettingsManager {

    void clearCache();

    boolean getEnableLogging();

    void setEnableLogging(boolean enableLogging);

    boolean getCrashReporting();

    void setCrashReporting(boolean crashReportingPref);


    boolean getFileLogging();

    void setFileLogging(boolean fileLogging);

    boolean getXMPPLogging();

    void setXMPPLogging(boolean xmppLogging);

    boolean getSipLogging();

    void setSipLogging(boolean sipLogging);

    boolean getDiagnosticPref();

    void setDiagnosticPref(boolean diagnosticPref);

    @Enums.Service.DialingServiceTypes.DialingServiceType
    int getDialingService();

    void setDialingService(@Enums.Service.DialingServiceTypes.DialingServiceType int dialingServiceType);

    @Nullable
    String getPhoneNumber();

    void setPhoneNumber(@Nullable String phoneNumber);

    String getCallCenterStatus();

    void setCallCenterStatus(String callCenterStatus);

    String getCallCenterUnavailableCodes();

    void setCallCenterUnavailableCodes(String callCenterUnavailableCodes);

    boolean getDisplayAudioVideoStats();

    void setDisplayAudioVideoStats(boolean audioVideoStats);

    void setOldActiveCallLayoutEnabled(boolean enableNewActiveCallLayout);

    boolean getIsOldActiveCallLayoutEnabled();

    boolean getDisplaySIPError();

    void setDisplaySIPError(boolean sipError);

    boolean getDisplaySIPState();

    void setDisplaySIPState(boolean sipState);

    boolean isSipEchoCancellationEnabled();

    boolean isShowDialogToDeleteSmsEnabled();

    boolean isSwipeActionsEnabled();

    boolean isBlockNumberForCallingEnabled();

    void setSipEchoCancellationEnabled(boolean enabled);

    void setShowDialogToDeleteSmsEnabled(boolean enabled);

    void setSwipeActionsEnabled(boolean enabled);

    void setBlockNumberForCallingEnabled(boolean enabled);

    void setNightModeState(@Enums.Session.NightModeState.State String nightModeState);

    @Enums.Session.NightModeState.State
    String getNightModeState();
}
