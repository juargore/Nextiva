/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "summary", strict = false)
public class BroadsoftVoicemailMessageSummaryDetails {

    @Nullable
    @Element(name = "newMessages", required = false)
    private Integer mNewMessagesCount;
    @Nullable
    @Element(name = "oldMessages", required = false)
    private Integer mOldMessagesCount;

    public BroadsoftVoicemailMessageSummaryDetails() {
    }

    @Nullable
    public Integer getNewMessagesCount() {
        return mNewMessagesCount;
    }

    @Nullable
    public Integer getOldMessagesCount() {
        return mOldMessagesCount;
    }
}
