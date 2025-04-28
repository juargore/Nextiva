/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.supplementaryservices;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigGeneralSetting;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftSupplementaryServicesXsiBroadworksMobility extends BroadsoftMobileConfigGeneralSetting implements Serializable {

    @Nullable
    @Element(name = "call-control", required = false)
    private BroadsoftMobileConfigGeneralSetting mCallControl;
    @Nullable
    @Element(name = "diversion-inhibitor", required = false)
    private BroadsoftMobileConfigGeneralSetting mDiversionInhibitor;
    @Nullable
    @Element(name = "answer-confirmation", required = false)
    private BroadsoftMobileConfigGeneralSetting mAnswerConfirmation;
    @Nullable
    @Element(name = "group-paging-calls", required = false)
    private BroadsoftMobileConfigGeneralSetting mGroupPagingCalls;
    @Nullable
    @Element(name = "click-to-dial-calls", required = false)
    private BroadsoftMobileConfigGeneralSetting mClickToDialCalls;
    @Nullable
    @Element(name = "phones-to-ring", required = false)
    private BroadsoftMobileConfigGeneralSetting mPhonesToRing;
    @Nullable
    @Element(name = "persona-management", required = false)
    private BroadsoftMobileConfigGeneralSetting mPersonaManagement;

    public BroadsoftSupplementaryServicesXsiBroadworksMobility() {
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getCallControl() {
        return mCallControl;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getDiversionInhibitor() {
        return mDiversionInhibitor;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getAnswerConfirmation() {
        return mAnswerConfirmation;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getGroupPagingCalls() {
        return mGroupPagingCalls;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getClickToDialCalls() {
        return mClickToDialCalls;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getPhonesToRing() {
        return mPhonesToRing;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getPersonaManagement() {
        return mPersonaManagement;
    }
}
