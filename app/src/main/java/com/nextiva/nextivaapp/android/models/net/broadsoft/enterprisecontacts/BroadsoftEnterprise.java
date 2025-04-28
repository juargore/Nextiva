/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.enterprisecontacts;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

/**
 * Created by joedephillipo on 2/16/18.
 */

@Root(name = "Enterprise", strict = false)
public class BroadsoftEnterprise {

    @Element(name = "startIndex", required = false)
    private int mStartIndex;
    @Element(name = "numberOfRecords", required = false)
    private int mNumberOfRecords;
    @Element(name = "totalAvailableRecords", required = false)
    private int mTotalAvailableRecords;
    @Nullable
    @ElementList(name = "enterpriseDirectory", required = false)
    private ArrayList<BroadsoftEnterpriseDirectoryDetails> mEnterpriseDirectoryDetailsList;

    public BroadsoftEnterprise() {
    }

    public int getStartIndex() {
        return mStartIndex;
    }

    public int getNumberOfRecords() {
        return mNumberOfRecords;
    }

    public int getTotalAvailableRecords() {
        return mTotalAvailableRecords;
    }

    @Nullable
    public ArrayList<BroadsoftEnterpriseDirectoryDetails> getEnterpriseDirectoryDetailsList() {
        return mEnterpriseDirectoryDetailsList;
    }
}
