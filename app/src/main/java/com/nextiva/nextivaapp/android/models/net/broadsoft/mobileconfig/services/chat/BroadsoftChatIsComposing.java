/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.chat;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigGeneralSetting;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftChatIsComposing extends BroadsoftMobileConfigGeneralSetting implements Serializable {

    @Nullable
    @Element(name = "chat", required = false)
    private BroadsoftMobileConfigGeneralSetting mChat;
    @Nullable
    @Element(name = "groupchat", required = false)
    private BroadsoftMobileConfigGeneralSetting mGroupChat;

    public BroadsoftChatIsComposing() {
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getChat() {
        return mChat;
    }

    @Nullable
    public BroadsoftMobileConfigGeneralSetting getGroupChat() {
        return mGroupChat;
    }
}
