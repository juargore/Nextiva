/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.xmpp.BroadsoftXmppCredentials;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.xmpp.BroadsoftXmppDomain;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.xmpp.BroadsoftXmppSsl;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigGeneralSetting;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by joedephillipo on 2/21/18.
 */

@Root
public class BroadsoftXmppProtocol implements Serializable {

    @Nullable
    @Element(name = "credentials", required = false)
    private BroadsoftXmppCredentials mXmppCredentials;
    @Nullable
    @Element(name = "domain", required = false)
    private BroadsoftXmppDomain mXmppDomain;
    @Nullable
    @Element(name = "ssl", required = false)
    private BroadsoftXmppSsl mXmppSsl;
    @Nullable
    @Element(name = "sasl", required = false)
    private BroadsoftMobileConfigGeneralSetting mSasl;
    @Nullable
    @Element(name = "chat-recording", required = false)
    private BroadsoftMobileConfigGeneralSetting mChatRecording;
    @Nullable
    @Element(name = "security-classification", required = false)
    private BroadsoftMobileConfigGeneralSetting mSecurityClassification;
    @Nullable
    @Element(name = "auto-subscribe", required = false)
    private BroadsoftMobileConfigGeneralSetting mAutoSubscribe;
    @Nullable
    @Element(name = "keep-alive-interval-sec", required = false)
    private String mKeepAliveIntervalSec;

    public BroadsoftXmppProtocol() {
    }

    @VisibleForTesting
    public BroadsoftXmppProtocol(@Nullable BroadsoftXmppCredentials xmppCredentials, @Nullable BroadsoftXmppDomain xmppDomain, @Nullable String keepAliveIntervalSec) {
        mXmppCredentials = xmppCredentials;
        mXmppDomain = xmppDomain;
        mKeepAliveIntervalSec = keepAliveIntervalSec;
    }

    @Nullable
    public BroadsoftXmppCredentials getXmppCredentials() {
        return mXmppCredentials;
    }

    @Nullable
    public BroadsoftXmppDomain getXmppDomain() {
        return mXmppDomain;
    }

    @Nullable
    public String getKeepAliveIntervalSec() {
        return mKeepAliveIntervalSec;
    }
}
