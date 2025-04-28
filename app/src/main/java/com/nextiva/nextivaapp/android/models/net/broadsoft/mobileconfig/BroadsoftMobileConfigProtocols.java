/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.BroadsoftRtpProtocol;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.BroadsoftSipProtocol;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.BroadsoftUmsHttpProtocol;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.BroadsoftVoicemailProtocol;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.BroadsoftXmppProtocol;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.BroadsoftXsiProtocol;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by joedephillipo on 2/21/18.
 */

@Root
public class BroadsoftMobileConfigProtocols implements Serializable {

    @Nullable
    @Element(name = "sip", required = false)
    private BroadsoftSipProtocol mBroadsoftSipProtocol;
    @Nullable
    @Element(name = "rtp", required = false)
    private BroadsoftRtpProtocol mBroadsoftRtpProtocol;
    @Nullable
    @Element(name = "xmpp", required = false)
    private BroadsoftXmppProtocol mBroadsoftXmppProtocol;
    @Nullable
    @Element(name = "ums-http", required = false)
    private BroadsoftUmsHttpProtocol mBroadsoftUmsHttpProtocol;
    @Nullable
    @Element(name = "xsi", required = false)
    private BroadsoftXsiProtocol mBroadsoftXsiProtocol;
    @Nullable
    @Element(name = "voice-mail", required = false)
    private BroadsoftVoicemailProtocol mBroadsoftVoicemailProtocol;

    public BroadsoftMobileConfigProtocols() {
    }

    @VisibleForTesting
    public BroadsoftMobileConfigProtocols(
            @Nullable BroadsoftSipProtocol broadsoftSipProtocol,
            @Nullable BroadsoftRtpProtocol broadsoftRtpProtocol,
            @Nullable BroadsoftXmppProtocol broadsoftXmppProtocol,
            @Nullable BroadsoftUmsHttpProtocol broadsoftUmsHttpProtocol,
            @Nullable BroadsoftXsiProtocol broadsoftXsiProtocol,
            @Nullable BroadsoftVoicemailProtocol broadsoftVoicemailProtocol) {

        mBroadsoftSipProtocol = broadsoftSipProtocol;
        mBroadsoftRtpProtocol = broadsoftRtpProtocol;
        mBroadsoftXmppProtocol = broadsoftXmppProtocol;
        mBroadsoftUmsHttpProtocol = broadsoftUmsHttpProtocol;
        mBroadsoftXsiProtocol = broadsoftXsiProtocol;
        mBroadsoftVoicemailProtocol = broadsoftVoicemailProtocol;
    }

    @Nullable
    public BroadsoftSipProtocol getBroadsoftSipProtocol() {
        return mBroadsoftSipProtocol;
    }

    @Nullable
    public BroadsoftRtpProtocol getBroadsoftRtpProtocol() {
        return mBroadsoftRtpProtocol;
    }

    @Nullable
    public BroadsoftXmppProtocol getBroadsoftXmppProtocol() {
        return mBroadsoftXmppProtocol;
    }

    @Nullable
    public BroadsoftUmsHttpProtocol getBroadsoftUmsHttpProtocol() {
        return mBroadsoftUmsHttpProtocol;
    }

    @Nullable
    public BroadsoftXsiProtocol getBroadsoftXsiProtocol() {
        return mBroadsoftXsiProtocol;
    }

    @Nullable
    public BroadsoftVoicemailProtocol getBroadsoftVoicemailProtocol() {
        return mBroadsoftVoicemailProtocol;
    }
}
