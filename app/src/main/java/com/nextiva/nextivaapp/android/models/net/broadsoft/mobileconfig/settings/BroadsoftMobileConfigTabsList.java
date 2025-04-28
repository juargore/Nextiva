/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/22/18.
 */

@Root
public class BroadsoftMobileConfigTabsList implements Serializable {

    @Nullable
    @Attribute(name = "selected-tab", required = false)
    private String mSelectedTab;
    @Nullable
    @Attribute(name = "reordering-enabled", required = false)
    private String mReorderingEnabled;
    @Nullable
    @Element(name = "contacts-tab", required = false)
    private BroadsoftMobileConfigTabSetting mContactsTab;
    @Nullable
    @Element(name = "call-tab", required = false)
    private BroadsoftMobileConfigTabSetting mDialerTab;
    @Nullable
    @Element(name = "im-tab", required = false)
    private BroadsoftMobileConfigTabSetting mChatTab;
    @Nullable
    @Element(name = "history-tab", required = false)
    private BroadsoftMobileConfigTabSetting mCallHistoryTab;
    @Nullable
    @Element(name = "myroom-tab", required = false)
    private BroadsoftMobileConfigTabSetting mRoomTab;

    public BroadsoftMobileConfigTabsList() {
    }

    @Nullable
    public String getSelectedTab() {
        return mSelectedTab;
    }

    @Nullable
    public String getReorderingEnabled() {
        return mReorderingEnabled;
    }

    @Nullable
    public BroadsoftMobileConfigTabSetting getContactsTab() {
        return mContactsTab;
    }

    @Nullable
    public BroadsoftMobileConfigTabSetting getDialerTab() {
        return mDialerTab;
    }

    @Nullable
    public BroadsoftMobileConfigTabSetting getChatTab() {
        return mChatTab;
    }

    @Nullable
    public BroadsoftMobileConfigTabSetting getCallHistoryTab() {
        return mCallHistoryTab;
    }

    @Nullable
    public BroadsoftMobileConfigTabSetting getRoomTab() {
        return mRoomTab;
    }
}
