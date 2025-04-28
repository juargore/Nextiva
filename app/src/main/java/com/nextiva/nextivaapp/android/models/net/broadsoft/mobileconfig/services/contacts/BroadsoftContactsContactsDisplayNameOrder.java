/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.contacts;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftContactsContactsDisplayNameOrder implements Serializable {

    @Nullable
    @Attribute(name = "visible", required = false)
    private String mVisible;
    @Nullable
    @Attribute(name = "default", required = false)
    private String mDefault;

    public BroadsoftContactsContactsDisplayNameOrder() {
    }

    @Nullable
    public String getVisible() {
        return mVisible;
    }

    @Nullable
    public String getDefault() {
        return mDefault;
    }
}
