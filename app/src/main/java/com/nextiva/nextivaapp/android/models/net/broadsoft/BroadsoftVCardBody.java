/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by joedephillipo on 4/4/18.
 */

public class BroadsoftVCardBody implements Serializable {

    @NonNull
    @SerializedName("jids")
    private ArrayList<String> mJid;

    public BroadsoftVCardBody(@NonNull ArrayList<String> jid) {
        mJid = jid;
    }

    @NonNull
    public ArrayList<String> getJid() {
        return mJid;
    }

    public void setJid(@NonNull ArrayList<String> jid) {
        mJid = jid;
    }
}
