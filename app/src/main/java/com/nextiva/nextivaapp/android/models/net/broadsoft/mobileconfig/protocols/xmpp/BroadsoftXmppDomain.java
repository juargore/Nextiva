/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.xmpp;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/22/18.
 */

@Root
public class BroadsoftXmppDomain implements Serializable {

    @Nullable
    @Attribute(name = "srv-enabled", required = false)
    private String mIsServerEnabled;
    @Nullable
    @Attribute(name = "use-for-ssl-verification", required = false)
    private String mUseForSslVerification;
    @Nullable
    @Text(required = false)
    private String mDomain;

    public BroadsoftXmppDomain() {
    }


    @VisibleForTesting
    public BroadsoftXmppDomain(@Nullable String xmppDomain) {
        mDomain = xmppDomain;
    }

    @Nullable
    public String getIsServerEnabled() {
        return mIsServerEnabled;
    }

    @Nullable
    public String getUseForSslVerification() {
        return mUseForSslVerification;
    }

    @Nullable
    public String getDomain() {
        return mDomain;
    }
}
