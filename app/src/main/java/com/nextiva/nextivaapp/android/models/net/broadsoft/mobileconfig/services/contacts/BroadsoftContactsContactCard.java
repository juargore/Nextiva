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
public class BroadsoftContactsContactCard implements Serializable {

    @Nullable
    @Attribute(name = "sync-from", required = false)
    private String mSyncFrom;

    public BroadsoftContactsContactCard() {
    }

    @Nullable
    public String getSyncFrom() {
        return mSyncFrom;
    }
}
