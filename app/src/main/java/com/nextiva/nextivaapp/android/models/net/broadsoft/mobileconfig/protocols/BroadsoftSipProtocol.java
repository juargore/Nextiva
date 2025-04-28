/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.sip.BroadsoftSipCredentials;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.sip.BroadsoftSipFeatureTags;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.sip.BroadsoftSipProxy;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.sip.BroadsoftSipProxyDiscovery;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.sip.BroadsoftSipRegistrar;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.sip.BroadsoftSipSession;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.sip.BroadsoftSipSignInTimer;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.sip.BroadsoftSipTimers;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.sip.BroadsoftSipTransports;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigGeneralSetting;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by joedephillipo on 2/21/18.
 */

@Root
public class BroadsoftSipProtocol implements Serializable {

    @Nullable
    @Element(name = "credentials", required = false)
    private BroadsoftSipCredentials mCredentials;
    @Nullable
    @Element(name = "use-sip-info", required = false)
    private BroadsoftMobileConfigGeneralSetting mUseSipInfo;
    @Nullable
    @Element(name = "allow-login-with-tel-uri", required = false)
    private BroadsoftMobileConfigGeneralSetting mAllowLoginWithTelUri;
    @Nullable
    @Element(name = "use-rport", required = false)
    private BroadsoftMobileConfigGeneralSetting mUseRport;
    @Nullable
    @Element(name = "registrar", required = false)
    private BroadsoftSipRegistrar mRegistrar;
    @Nullable
    @Element(name = "proxy", required = false)
    private BroadsoftSipProxy mProxy;
    @Nullable
    @Element(name = "proxy-discovery", required = false)
    private BroadsoftSipProxyDiscovery mProxyDiscovery;
    @Nullable
    @Element(name = "domain", required = false)
    private String mDomain;
    @Nullable
    @Element(name = "preferred-port", required = false)
    private String mPreferredPort;
    @Nullable
    @Element(name = "user-agent", required = false)
    private String mUserAgent;
    @Nullable
    @Element(name = "q-value", required = false)
    private String mQValue;
    @Nullable
    @Element(name = "feature-tags", required = false)
    private BroadsoftSipFeatureTags mFeatureTags;
    @Nullable
    @Element(name = "use-alternative-identities", required = false)
    private BroadsoftMobileConfigGeneralSetting mUseAlternativeIdentities;
    @Nullable
    @Element(name = "subscription-refresh-interval-sec", required = false)
    private String mSubscriptionRefreshInterval;
    @Nullable
    @Element(name = "subscription-retry-interval-sec", required = false)
    private String mSubscriptionRetryInterval;
    @Nullable
    @Element(name = "publish-refresh-interval-sec", required = false)
    private String mPublishRefreshInterval;
    @Nullable
    @Element(name = "registration-refresh-interval-sec", required = false)
    private String mRegistrationRefreshInterval;
    @Nullable
    @Element(name = "fast-unsubscribe", required = false)
    private BroadsoftMobileConfigGeneralSetting mFastUnsubscribe;
    @Nullable
    @Element(name = "session", required = false)
    private BroadsoftSipSession mSession;
    @Nullable
    @Element(name = "transports", required = false)
    private BroadsoftSipTransports mTransports;
    @Nullable
    @Element(name = "timers", required = false)
    private BroadsoftSipTimers mTimers;
    @Nullable
    @Element(name = "sign-in-timer", required = false)
    private BroadsoftSipSignInTimer mSignInTimer;
    @Nullable
    @Element(name = "expires-hack", required = false)
    private BroadsoftMobileConfigGeneralSetting mExpiresHack;
    @Nullable
    @Element(name = "use-mediasec", required = false)
    private BroadsoftMobileConfigGeneralSetting mUseMediaSec;
    @Nullable
    @Element(name = "support-update", required = false)
    private BroadsoftMobileConfigGeneralSetting mSupportUpdate;

    public BroadsoftSipProtocol() {
    }

    @VisibleForTesting
    public BroadsoftSipProtocol(
            @Nullable BroadsoftSipCredentials credentials,
            @Nullable BroadsoftSipProxy proxy,
            @Nullable String domain,
            @Nullable String userAgent,
            @Nullable BroadsoftMobileConfigGeneralSetting useRport,
            @Nullable BroadsoftSipRegistrar registrar,
            @Nullable String preferredPort,
            @Nullable BroadsoftSipTransports transports,
            @Nullable BroadsoftSipSession session,
            @Nullable String registrationRefreshInterval) {

        mCredentials = credentials;
        mProxy = proxy;
        mDomain = domain;
        mUserAgent = userAgent;
        mUseRport = useRport;
        mPreferredPort = preferredPort;
        mRegistrar = registrar;
        mTransports = transports;
        mSession = session;
        mRegistrationRefreshInterval = registrationRefreshInterval;
    }

    @Nullable
    public BroadsoftSipCredentials getCredentials() {
        return mCredentials;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getUseSipInfo() {
        return mUseSipInfo;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getAllowLoginWithTelUri() {
        return mAllowLoginWithTelUri;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getUseRport() {
        return mUseRport;
    }

    @Nullable
    public BroadsoftSipRegistrar getRegistrar() {
        return mRegistrar;
    }

    @Nullable
    public BroadsoftSipProxy getProxy() {
        return mProxy;
    }

    @Nullable
    public BroadsoftSipProxyDiscovery getProxyDiscovery() {
        return mProxyDiscovery;
    }

    @Nullable
    public String getDomain() {
        return mDomain;
    }

    @Nullable
    public String getPreferredPort() {
        return mPreferredPort;
    }

    @Nullable
    public String getUserAgent() {
        return mUserAgent;
    }

    @Nullable
    public String getQValue() {
        return mQValue;
    }

    @Nullable
    public BroadsoftSipFeatureTags getFeatureTags() {
        return mFeatureTags;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getUseAlternativeIdentities() {
        return mUseAlternativeIdentities;
    }

    @Nullable
    public String getSubscriptionRefreshInterval() {
        return mSubscriptionRefreshInterval;
    }

    @Nullable
    public String getSubscriptionRetryInterval() {
        return mSubscriptionRetryInterval;
    }

    @Nullable
    public String getPublishRefreshInterval() {
        return mPublishRefreshInterval;
    }

    @Nullable
    public String getRegistrationRefreshInterval() {
        return mRegistrationRefreshInterval;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getFastUnsubscribe() {
        return mFastUnsubscribe;
    }

    @Nullable
    public BroadsoftSipSession getSession() {
        return mSession;
    }

    @Nullable
    public BroadsoftSipTransports getTransports() {
        return mTransports;
    }

    @Nullable
    public BroadsoftSipTimers getTimers() {
        return mTimers;
    }

    @Nullable
    public BroadsoftSipSignInTimer getSignInTimer() {
        return mSignInTimer;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getExpiresHack() {
        return mExpiresHack;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getUseMediaSec() {
        return mUseMediaSec;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getSupportUpdate() {
        return mSupportUpdate;
    }
}
