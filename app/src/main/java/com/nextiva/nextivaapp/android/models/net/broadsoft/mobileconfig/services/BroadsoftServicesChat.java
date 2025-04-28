/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.chat.BroadsoftChatIsComposing;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigGeneralSetting;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftServicesChat extends BroadsoftMobileConfigGeneralSetting implements Serializable {

    @Nullable
    @Attribute(name = "type", required = false)
    private String mType;
    @Nullable
    @Element(name = "group-chat", required = false)
    private BroadsoftMobileConfigGeneralSetting mGroupChat;
    @Nullable
    @Element(name = "is-composing", required = false)
    private BroadsoftChatIsComposing mIsComposing;

    public BroadsoftServicesChat() {
    }

    @Nullable
    public String getType() {
        return mType;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getGroupChat() {
        return mGroupChat;
    }

    @Nullable
    public BroadsoftChatIsComposing getIsComposing() {
        return mIsComposing;
    }
}
