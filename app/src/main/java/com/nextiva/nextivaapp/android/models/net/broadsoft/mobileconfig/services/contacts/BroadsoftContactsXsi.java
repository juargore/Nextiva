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
public class BroadsoftContactsXsi extends BroadsoftMobileConfigGeneralSetting implements Serializable {

    @Nullable
    @Element(name = "search-sources", required = false)
    private BroadsoftContactsSearchSources mSearchSources;
    @Nullable
    @Element(name = "defer-period", required = false)
    private Integer mDeferPeriod;
    @Nullable
    @Element(name = "min-size", required = false)
    private Integer mMinSize;

    public BroadsoftContactsXsi() {
    }

    @Nullable
    public BroadsoftContactsSearchSources getSearchSources() {
        return mSearchSources;
    }

    @Nullable
    public Integer getDeferPeriod() {
        return mDeferPeriod;
    }

    @Nullable
    public Integer getMinSize() {
        return mMinSize;
    }
}
