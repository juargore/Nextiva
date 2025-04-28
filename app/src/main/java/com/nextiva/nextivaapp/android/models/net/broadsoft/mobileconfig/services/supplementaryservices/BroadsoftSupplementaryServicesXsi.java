/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.supplementaryservices;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigGeneralSetting;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftSupplementaryServicesXsi implements Serializable {

    @Nullable
    @Element(name = "broadworks-anywhere", required = false)
    private BroadsoftSupplementaryServicesXsiBroadworksAnywhere mBroadworksAnywhere;
    @Nullable
    @Element(name = "remote-office", required = false)
    private BroadsoftMobileConfigGeneralSetting mRemoteOffice;
    @Nullable
    @Element(name = "call-forwarding-always", required = false)
    private BroadsoftMobileConfigGeneralSetting mCallForwardingAlways;
    @Nullable
    @Element(name = "call-forwarding-busy", required = false)
    private BroadsoftMobileConfigGeneralSetting mCallForwardingBusy;
    @Nullable
    @Element(name = "call-forwarding-not-reachable", required = false)
    private BroadsoftMobileConfigGeneralSetting mCallForwardingNotReachable;
    @Nullable
    @Element(name = "call-forwarding-no-answer", required = false)
    private BroadsoftMobileConfigGeneralSetting mCallForwardingNoAnswer;
    @Nullable
    @Element(name = "do-not-disturb", required = false)
    private BroadsoftMobileConfigGeneralSetting mDoNotDisturb;
    @Nullable
    @Element(name = "calling-line-id-delivery-blocking", required = false)
    private BroadsoftMobileConfigGeneralSetting mCallingLineIdDeliveryBlocking;
    @Nullable
    @Element(name = "simultaneous-ring-personal", required = false)
    private BroadsoftMobileConfigGeneralSetting mSimultaneousRingPersonal;
    @Nullable
    @Element(name = "connected-line-identification-presentation", required = false)
    private BroadsoftMobileConfigGeneralSetting mConnectedLineIdentificationPresentation;
    @Nullable
    @Element(name = "connected-line-identification-restriction", required = false)
    private BroadsoftMobileConfigGeneralSetting mConnectedLineIdentificationRestriction;
    @Nullable
    @Element(name = "default-call-type", required = false)
    private BroadsoftSupplementaryServicesXsiDefaultCallType mDefaultCallType;
    @Nullable
    @Element(name = "broadworks-mobility", required = false)
    private BroadsoftSupplementaryServicesXsiBroadworksMobility mBroadworksMobility;
    @Nullable
    @Element(name = "prevent-phone-number-duplication", required = false)
    private BroadsoftMobileConfigGeneralSetting mPreventPhoneNumberDuplication;
    @Nullable
    @Element(name = "call-waiting", required = false)
    private BroadsoftMobileConfigGeneralSetting mCallWaiting;
    @Nullable
    @Element(name = "personal-assistant", required = false)
    private BroadsoftMobileConfigGeneralSetting mPersonalAssistant;
    @Nullable
    @Element(name = "my-telephone-number", required = false)
    private BroadsoftMobileConfigGeneralSetting mMyTelephoneNumber;

    public BroadsoftSupplementaryServicesXsi() {
    }

    @VisibleForTesting
    public BroadsoftSupplementaryServicesXsi(@Nullable BroadsoftSupplementaryServicesXsiBroadworksAnywhere broadworksAnywhere,
                                             @Nullable BroadsoftMobileConfigGeneralSetting remoteOffice) {
        mBroadworksAnywhere = broadworksAnywhere;
        mRemoteOffice = remoteOffice;
    }

    @Nullable
    public BroadsoftSupplementaryServicesXsiBroadworksAnywhere getBroadworksAnywhere() {
        return mBroadworksAnywhere;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getRemoteOffice() {
        return mRemoteOffice;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getCallForwardingAlways() {
        return mCallForwardingAlways;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getCallForwardingBusy() {
        return mCallForwardingBusy;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getCallForwardingNotReachable() {
        return mCallForwardingNotReachable;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getCallForwardingNoAnswer() {
        return mCallForwardingNoAnswer;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getDoNotDisturb() {
        return mDoNotDisturb;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getCallingLineIdDeliveryBlocking() {
        return mCallingLineIdDeliveryBlocking;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getSimultaneousRingPersonal() {
        return mSimultaneousRingPersonal;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getConnectedLineIdentificationPresentation() {
        return mConnectedLineIdentificationPresentation;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getConnectedLineIdentificationRestriction() {
        return mConnectedLineIdentificationRestriction;
    }

    @Nullable
    public BroadsoftSupplementaryServicesXsiDefaultCallType getDefaultCallType() {
        return mDefaultCallType;
    }

    @Nullable
    public BroadsoftSupplementaryServicesXsiBroadworksMobility getBroadworksMobility() {
        return mBroadworksMobility;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getPreventPhoneNumberDuplication() {
        return mPreventPhoneNumberDuplication;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getCallWaiting() {
        return mCallWaiting;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getPersonalAssistant() {
        return mPersonalAssistant;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getMyTelephoneNumber() {
        return mMyTelephoneNumber;
    }
}
