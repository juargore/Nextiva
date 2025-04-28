/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.xsi.BroadsoftXsiPath;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by joedephillipo on 2/21/18.
 */

@Root
public class BroadsoftXsiProtocol implements Serializable {

    @Nullable
    @Element(name = "root", required = false)
    private String mXsiRoot;
    @Nullable
    @Element(name = "paths", required = false)
    private BroadsoftXsiPath mXsiPath;

    public BroadsoftXsiProtocol() {
    }

    @Nullable
    public String getXsiRoot() {
        return mXsiRoot;
    }

    @Nullable
    public BroadsoftXsiPath getXsiPath() {
        return mXsiPath;
    }
}
