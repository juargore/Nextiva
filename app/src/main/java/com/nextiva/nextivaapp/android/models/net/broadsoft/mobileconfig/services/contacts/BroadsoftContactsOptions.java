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
public class BroadsoftContactsOptions extends BroadsoftMobileConfigGeneralSetting implements Serializable {

    @Nullable
    @Element(name = "proactive", required = false)
    private BroadsoftMobileConfigGeneralSetting mProactive;
    @Nullable
    @Element(name = "tags", required = false)
    private BroadsoftContactsOptionsTags mTags;

    public BroadsoftContactsOptions() {
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getProactive() {
        return mProactive;
    }

    @Nullable
    public BroadsoftContactsOptionsTags getTags() {
        return mTags;
    }
}
