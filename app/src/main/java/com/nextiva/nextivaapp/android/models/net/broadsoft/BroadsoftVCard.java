/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by joedephillipo on 4/4/18.
 */

public class BroadsoftVCard implements Serializable {

    @Nullable
    @SerializedName("jid")
    private String mJid;
    @Nullable
    @SerializedName("vcard")
    private String mVCard;

    public BroadsoftVCard(@Nullable String jid, @Nullable String VCard) {
        mJid = jid;
        mVCard = VCard;
    }

    @Nullable
    public String getJid() {
        return mJid;
    }

    public void setJid(@Nullable String jid) {
        mJid = jid;
    }

    @Nullable
    public String getVCard() {
        return mVCard;
    }

    public void setVCard(@Nullable String VCard) {
        mVCard = VCard;
    }
}
