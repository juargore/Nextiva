/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.enterprisecontacts;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by joedephillipo on 2/16/18.
 */

@Root
class BroadsoftEnterpriseDirectory {

    @Nullable
    @Element(name = "directoryDetails", required = false)
    private BroadsoftEnterpriseDirectoryDetails mBroadsoftEnterpriseDirectoryDetails;

    public BroadsoftEnterpriseDirectory() {
    }

    @Nullable
    public BroadsoftEnterpriseDirectoryDetails getBroadsoftEnterpriseDirectoryDetails() {
        return mBroadsoftEnterpriseDirectoryDetails;
    }
}
