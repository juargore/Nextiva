/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.sip;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by joedephillipo on 2/22/18.
 */

@Root
public class BroadsoftSipFeatureTags implements Serializable {

    @Nullable
    @Element(name = "chatgchat", required = false)
    private String mSipFeatureChatGChat;
    @Nullable
    @Element(name = "sms", required = false)
    private String mSipFeatureSMS;

    public BroadsoftSipFeatureTags() {
    }

    @Nullable
    public String getSipFeatureChatGChat() {
        return mSipFeatureChatGChat;
    }

    @Nullable
    public String getSipFeatureSMS() {
        return mSipFeatureSMS;
    }
}
