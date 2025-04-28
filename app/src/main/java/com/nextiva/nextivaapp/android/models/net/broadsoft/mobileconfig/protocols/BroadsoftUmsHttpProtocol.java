/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.umshttp.BroadsoftUmsHttpDomain;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.umshttp.BroadsoftUmsHttpServer;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.umshttp.BroadsoftUmsHttpSsl;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by joedephillipo on 2/21/18.
 */

@Root
public class BroadsoftUmsHttpProtocol implements Serializable {

    @Nullable
    @Element(name = "srv", required = false)
    private BroadsoftUmsHttpServer mServer;
    @Nullable
    @Element(name = "domain", required = false)
    private BroadsoftUmsHttpDomain mDomain;
    @Nullable
    @Element(name = "ssl", required = false)
    private BroadsoftUmsHttpSsl mSsl;

    public BroadsoftUmsHttpProtocol() {
    }

    @Nullable
    public BroadsoftUmsHttpServer getServer() {
        return mServer;
    }

    public void setServer(@Nullable BroadsoftUmsHttpServer server) {
        mServer = server;
    }

    @Nullable
    public BroadsoftUmsHttpDomain getDomain() {
        return mDomain;
    }

    public void setDomain(@Nullable BroadsoftUmsHttpDomain domain) {
        mDomain = domain;
    }

    @Nullable
    public BroadsoftUmsHttpSsl getSsl() {
        return mSsl;
    }

    public void setSsl(@Nullable BroadsoftUmsHttpSsl ssl) {
        mSsl = ssl;
    }
}
