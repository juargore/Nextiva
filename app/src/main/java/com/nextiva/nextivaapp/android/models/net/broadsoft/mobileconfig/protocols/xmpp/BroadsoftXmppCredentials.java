/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.xmpp;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by joedephillipo on 2/21/18.
 */

@Root
public class BroadsoftXmppCredentials implements Serializable {

    @Nullable
    @Element(name = "username", required = false)
    private String mXmppUsername;
    @Nullable
    @Element(name = "password", required = false)
    private String mXmppPassword;

    public BroadsoftXmppCredentials() {
    }

    @VisibleForTesting
    public BroadsoftXmppCredentials(@Nullable String xmppUsername, @Nullable String xmppPassword) {
        mXmppUsername = xmppUsername;
        mXmppPassword = xmppPassword;
    }

    @Nullable
    public String getXmppUsername() {
        return mXmppUsername;
    }

    @Nullable
    public String getXmppPassword() {
        return mXmppPassword;
    }
}
