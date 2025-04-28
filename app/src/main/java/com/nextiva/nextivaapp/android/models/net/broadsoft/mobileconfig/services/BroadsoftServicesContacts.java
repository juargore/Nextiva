/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.contacts.BroadsoftContactsContactCard;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.contacts.BroadsoftContactsContactsDisplayNameOrder;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.contacts.BroadsoftContactsContactsShortNames;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.contacts.BroadsoftContactsContactsSortOrder;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.contacts.BroadsoftContactsOptions;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.contacts.BroadsoftContactsVCardExpiration;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.contacts.BroadsoftContactsXsi;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigGeneralSetting;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftServicesContacts extends BroadsoftMobileConfigGeneralSetting implements Serializable {

    @Nullable
    @Attribute(name = "type", required = false)
    private String mType;
    @Nullable
    @Element(name = "xsi", required = false)
    private BroadsoftContactsXsi mXsi;
    @Nullable
    @Element(name = "contact-card", required = false)
    private BroadsoftContactsContactCard mContactCard;
    @Nullable
    @Element(name = "vcard-expiration", required = false)
    private BroadsoftContactsVCardExpiration mVCardExpiration;
    @Nullable
    @Element(name = "options", required = false)
    private BroadsoftContactsOptions mOptions;
    @Nullable
    @Element(name = "contacts-sort-order", required = false)
    private BroadsoftContactsContactsSortOrder mContactsSortOrder;
    @Nullable
    @Element(name = "contacts-display-name-order", required = false)
    private BroadsoftContactsContactsDisplayNameOrder mContactsDisplayNameOrder;
    @Nullable
    @Element(name = "contacts-short-names", required = false)
    private BroadsoftContactsContactsShortNames mContactsShortNames;
    @Nullable
    @Element(name = "hiragana-names", required = false)
    private BroadsoftMobileConfigGeneralSetting mHiraganaNames;
    @Nullable
    @Element(name = "title", required = false)
    private BroadsoftMobileConfigGeneralSetting mTitle;
    @Nullable
    @Element(name = "department", required = false)
    private BroadsoftMobileConfigGeneralSetting mDepartment;
    @Nullable
    @Element(name = "group_id", required = false)
    private BroadsoftMobileConfigGeneralSetting mGroupId;

    public BroadsoftServicesContacts() {
    }

    @Nullable
    public String getType() {
        return mType;
    }
}
