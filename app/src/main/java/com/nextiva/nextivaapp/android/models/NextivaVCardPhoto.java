/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models;


import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class NextivaVCardPhoto {

    @Nullable
    @Element(name = "TYPE", required = false)
    private String mType;

    @Nullable
    @Element(name = "BINVAL", required = false)
    private String mBinVal;

    public NextivaVCardPhoto() {
    }

    public NextivaVCardPhoto(@Nullable String type, @Nullable String binVal) {
        mType = type;
        mBinVal = binVal;
    }

    @Nullable
    public String getType() {
        return mType;
    }

    public void setType(@Nullable String type) {
        mType = type;
    }

    @Nullable
    public String getBinVal() {
        return mBinVal;
    }

    public void setBinVal(@Nullable String binVal) {
        mBinVal = binVal;
    }
}
