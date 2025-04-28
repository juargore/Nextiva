/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by joedephillipo on 4/4/18.
 */

public class BroadsoftVCardResponse implements Serializable {

    @Nullable
    @SerializedName("vcards")
    private ArrayList<BroadsoftVCard> mBroadsoftVCards;

    public BroadsoftVCardResponse(@Nullable ArrayList<BroadsoftVCard> broadsoftVCards) {
        mBroadsoftVCards = broadsoftVCards;
    }

    @Nullable
    public ArrayList<BroadsoftVCard> getBroadsoftVCards() {
        return mBroadsoftVCards;
    }

    public void setBroadsoftVCards(@Nullable ArrayList<BroadsoftVCard> broadsoftVCards) {
        mBroadsoftVCards = broadsoftVCards;
    }
}
