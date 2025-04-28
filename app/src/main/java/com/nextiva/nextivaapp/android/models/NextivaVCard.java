/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Root(name = "vCard", strict = false)
@Namespace(reference = "vcard-temp")
public class NextivaVCard {

    @Nullable
    @Element(name = "PRODID", required = false)
    private String mProdId;

    @Nullable
    @Element(name = "PHOTO", required = false)
    private NextivaVCardPhoto mPhoto;

    public NextivaVCard(@Nullable String prodId, @Nullable NextivaVCardPhoto photo) {
        mProdId = prodId;
        mPhoto = photo;
    }

    public NextivaVCard() {
    }

    @Nullable
    public String getProdId() {
        return mProdId;
    }

    public void setProdId(@Nullable String prodId) {
        mProdId = prodId;
    }

    @Nullable
    public NextivaVCardPhoto getPhoto() {
        return mPhoto;
    }

    public void setPhoto(@Nullable NextivaVCardPhoto photo) {
        mPhoto = photo;
    }
}
