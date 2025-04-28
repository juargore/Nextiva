/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.contacts;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigGeneralSetting;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftContactsSearchSources extends BroadsoftMobileConfigGeneralSetting implements Serializable {

    @Nullable
    @Element(name = "enterprise", required = false)
    private BroadsoftMobileConfigGeneralSetting mEnterprise;
    @Nullable
    @Element(name = "enterprise-common", required = false)
    private BroadsoftMobileConfigGeneralSetting mEnterpriseCommon;
    @Nullable
    @Element(name = "personal", required = false)
    private BroadsoftMobileConfigGeneralSetting mPersonal;
    @Nullable
    @Element(name = "group-common", required = false)
    private BroadsoftMobileConfigGeneralSetting mGroupCommon;

    public BroadsoftContactsSearchSources() {
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getEnterprise() {
        return mEnterprise;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getEnterpriseCommon() {
        return mEnterpriseCommon;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getPersonal() {
        return mPersonal;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getGroupCommon() {
        return mGroupCommon;
    }
}
