/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.xmpp;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/22/18.
 */

@Root
public class BroadsoftXmppSsl implements Serializable {

    @Nullable
    @Attribute(name = "enabled", required = false)
    private String mIsEnabled;
    @Nullable
    @Attribute(name = "allow-self-signed-certificates", required = false)
    private String mAllowSelfSignedCertificates;

    public BroadsoftXmppSsl() {
    }

    @Nullable
    public String getIsEnabled() {
        return mIsEnabled;
    }

    @Nullable
    public String getAllowSelfSignedCertificates() {
        return mAllowSelfSignedCertificates;
    }
}
