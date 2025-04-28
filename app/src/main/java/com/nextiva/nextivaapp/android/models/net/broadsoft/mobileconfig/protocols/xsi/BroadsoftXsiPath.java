/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.xsi;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by joedephillipo on 2/21/18.
 */

@Root
public class BroadsoftXsiPath implements Serializable {

    @Nullable
    @Element(name = "root", required = false)
    private String mXsiPathRoot;
    @Nullable
    @Element(name = "actions", required = false)
    private String mXsiActions;
    @Nullable
    @Element(name = "events", required = false)
    private String mXsiEvents;

    public BroadsoftXsiPath() {
    }

    @Nullable
    public String getXsiEvents() {
        return mXsiEvents;
    }

    @Nullable
    public String getXsiPathRoot() {
        return mXsiPathRoot;
    }

    @Nullable
    public String getXsiActions() {
        return mXsiActions;
    }
}
