/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "VoiceMailMessageSummary", strict = false)
public class BroadsoftVoicemailMessageSummaryResponse {

    @Nullable
    @Element(name = "summary", required = false)
    private BroadsoftVoicemailMessageSummaryDetails mBroadsoftVoicemailMessageSummaryDetails;

    public BroadsoftVoicemailMessageSummaryResponse() {
    }

    @Nullable
    public BroadsoftVoicemailMessageSummaryDetails getBroadsoftVoicemailMessageSummaryDetails() {
        return mBroadsoftVoicemailMessageSummaryDetails;
    }
}
