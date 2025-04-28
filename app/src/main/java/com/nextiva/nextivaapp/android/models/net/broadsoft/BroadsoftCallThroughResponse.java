/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "IMRNInfo", strict = false)
public class BroadsoftCallThroughResponse {

    @Element(name = "imrn", required = false)
    private String mCallThroughNumber;

    public BroadsoftCallThroughResponse() {
    }

    public String getCallThroughNumber() {
        return mCallThroughNumber;
    }
}
