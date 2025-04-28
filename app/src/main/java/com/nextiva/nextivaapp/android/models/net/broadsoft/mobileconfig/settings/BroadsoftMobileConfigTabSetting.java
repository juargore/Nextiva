/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/22/18.
 */

@Root
public class BroadsoftMobileConfigTabSetting implements Serializable {

    @Nullable
    @Attribute(name = "position", required = false)
    private String mPosition;
    @Nullable
    @Attribute(name = "default-subtab", required = false)
    private String mDefaultSubTab;

    public BroadsoftMobileConfigTabSetting() {
    }

    @Nullable
    public String getPosition() {
        return mPosition;
    }

    @Nullable
    public String getDefaultSubTab() {
        return mDefaultSubTab;
    }
}
