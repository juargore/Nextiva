/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.BroadsoftServicesCallHistory;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.BroadsoftServicesCalls;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.BroadsoftServicesChat;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.BroadsoftServicesContacts;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.BroadsoftServicesDeployment;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.BroadsoftServicesDirectoryIntegration;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.BroadsoftServicesEmergencyDialing;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.BroadsoftServicesForcedLogout;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.BroadsoftServicesMwi;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.BroadsoftServicesPresence;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.BroadsoftServicesRooms;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.BroadsoftServicesServiceSettings;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.BroadsoftServicesSupplementaryServices;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.BroadsoftServicesTestServices;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.BroadsoftServicesVersionControl;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigGeneralSetting;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by joedephillipo on 2/21/18.
 */

@Root
public class BroadsoftMobileConfigServices implements Serializable {

    @Nullable
    @Element(name = "presence", required = false)
    private BroadsoftServicesPresence mPresence;
    @Nullable
    @Element(name = "calls", required = false)
    private BroadsoftServicesCalls mCalls;
    @Nullable
    @Element(name = "chat", required = false)
    private BroadsoftServicesChat mChat;
    @Nullable
    @Element(name = "rooms", required = false)
    private BroadsoftServicesRooms mRooms;
    @Nullable
    @Element(name = "version-control", required = false)
    private BroadsoftServicesVersionControl mVersionControl;
    @Nullable
    @Element(name = "forced-logout", required = false)
    private BroadsoftServicesForcedLogout mForcedLogout;
    @Nullable
    @Element(name = "contacts", required = false)
    private BroadsoftServicesContacts mContacts;
    @Nullable
    @Element(name = "mwi", required = false)
    private BroadsoftServicesMwi mMwi;
    @Nullable
    @Element(name = "supplementary-services", required = false)
    private BroadsoftServicesSupplementaryServices mSupplementaryServices;
    @Nullable
    @Element(name = "service-settings", required = false)
    private BroadsoftServicesServiceSettings mServiceSettings;
    @Nullable
    @Element(name = "call-history", required = false)
    private BroadsoftServicesCallHistory mCallHistory;
    @Nullable
    @Element(name = "emergency-number", required = false)
    private String mEmergencyNumber;
    @Nullable
    @Element(name = "emergency-dialing", required = false)
    private BroadsoftServicesEmergencyDialing mEmergencyDialing;
    @Nullable
    @Element(name = "calls-circuit-switched", required = false)
    private BroadsoftMobileConfigGeneralSetting mCallsCircuitSwitched;
    @Nullable
    @Element(name = "directory-integration", required = false)
    private BroadsoftServicesDirectoryIntegration mDirectoryIntegration;
    @Nullable
    @Element(name = "test-services", required = false)
    private BroadsoftServicesTestServices mTestServices;
    @Nullable
    @Element(name = "call-center-agent", required = false)
    private BroadsoftMobileConfigGeneralSetting mCallCenterAgent;
    @Nullable
    @Element(name = "webcollab", required = false)
    private BroadsoftMobileConfigGeneralSetting mWebcollab;
    @Nullable
    @Element(name = "push-notifications-for-calls", required = false)
    private BroadsoftMobileConfigGeneralSetting mPushNotificationsForCalls;
    @Nullable
    @Element(name = "push-notifications-for-chat", required = false)
    private BroadsoftMobileConfigGeneralSetting mPushNotificationsForChat;
    @Nullable
    @Element(name = "deployment", required = false)
    private BroadsoftServicesDeployment mDeployment;

    public BroadsoftMobileConfigServices() {
    }

    @VisibleForTesting
    public BroadsoftMobileConfigServices(@Nullable BroadsoftServicesSupplementaryServices supplementaryServices) {
        mSupplementaryServices = supplementaryServices;
    }

    @VisibleForTesting
    public BroadsoftMobileConfigServices(@Nullable BroadsoftServicesCalls calls) {
        mCalls = calls;
    }

    @Nullable
    public BroadsoftServicesPresence getPresence() {
        return mPresence;
    }

    @Nullable
    public BroadsoftServicesCalls getCalls() {
        return mCalls;
    }

    @Nullable
    public BroadsoftServicesChat getChat() {
        return mChat;
    }

    @Nullable
    public BroadsoftServicesRooms getRooms() {
        return mRooms;
    }

    @Nullable
    public BroadsoftServicesVersionControl getVersionControl() {
        return mVersionControl;
    }

    @Nullable
    public BroadsoftServicesForcedLogout getForcedLogout() {
        return mForcedLogout;
    }

    @Nullable
    public BroadsoftServicesContacts getContacts() {
        return mContacts;
    }

    @Nullable
    public BroadsoftServicesMwi getMwi() {
        return mMwi;
    }

    @Nullable
    public BroadsoftServicesSupplementaryServices getSupplementaryServices() {
        return mSupplementaryServices;
    }

    @Nullable
    public BroadsoftServicesServiceSettings getServiceSettings() {
        return mServiceSettings;
    }

    @Nullable
    public BroadsoftServicesCallHistory getCallHistory() {
        return mCallHistory;
    }

    @Nullable
    public String getEmergencyNumber() {
        return mEmergencyNumber;
    }

    @Nullable
    public BroadsoftServicesEmergencyDialing getEmergencyDialing() {
        return mEmergencyDialing;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getCallsCircuitSwitched() {
        return mCallsCircuitSwitched;
    }

    @Nullable
    public BroadsoftServicesDirectoryIntegration getDirectoryIntegration() {
        return mDirectoryIntegration;
    }

    @Nullable
    public BroadsoftServicesTestServices getTestServices() {
        return mTestServices;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getCallCenterAgent() {
        return mCallCenterAgent;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getWebcollab() {
        return mWebcollab;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getPushNotificationsForCalls() {
        return mPushNotificationsForCalls;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getPushNotificationsForChat() {
        return mPushNotificationsForChat;
    }

    @Nullable
    public BroadsoftServicesDeployment getDeployment() {
        return mDeployment;
    }
}
