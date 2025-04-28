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
public class BroadsoftSupplementaryServicesXsiBroadworksAnywhere extends BroadsoftMobileConfigGeneralSetting implements Serializable {

    @Nullable
    @Element(name = "description", required = false)
    private BroadsoftMobileConfigGeneralSetting mDescription;
    @Nullable
    @Element(name = "call-control", required = false)
    private BroadsoftSupplementaryServicesXsiBroadworksAnywhereCallControl mCallControl;
    @Nullable
    @Element(name = "diversion-inhibitor", required = false)
    private BroadsoftSupplementaryServicesXsiBroadworksAnywhereDiversionInhibitor mDiversionInhibitor;
    @Nullable
    @Element(name = "answer-confirmation", required = false)
    private BroadsoftSupplementaryServicesXsiBroadworksAnywhereAnswerConfirmation mAnswerConfirmation;

    public BroadsoftSupplementaryServicesXsiBroadworksAnywhere() {
    }

    @VisibleForTesting
    public BroadsoftSupplementaryServicesXsiBroadworksAnywhere(@Nullable String isEnabled) {
        super(isEnabled);
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getDescription() {
        return mDescription;
    }

    @Nullable
    public BroadsoftSupplementaryServicesXsiBroadworksAnywhereCallControl getCallControl() {
        return mCallControl;
    }

    @Nullable
    public BroadsoftSupplementaryServicesXsiBroadworksAnywhereDiversionInhibitor getDiversionInhibitor() {
        return mDiversionInhibitor;
    }

    @Nullable
    public BroadsoftSupplementaryServicesXsiBroadworksAnywhereAnswerConfirmation getAnswerConfirmation() {
        return mAnswerConfirmation;
    }
}
