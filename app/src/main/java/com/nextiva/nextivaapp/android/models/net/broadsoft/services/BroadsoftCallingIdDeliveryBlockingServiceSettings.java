/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.services;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

/**
 * Created by adammacdonald on 3/21/18.
 */

@Root(name = "CallingLineIDDeliveryBlocking", strict = false)
@Namespace(reference = "http://schema.broadsoft.com/xsi")
public class BroadsoftCallingIdDeliveryBlockingServiceSettings extends BroadsoftGeneralServiceSettings {

    public BroadsoftCallingIdDeliveryBlockingServiceSettings() {
    }

    public BroadsoftCallingIdDeliveryBlockingServiceSettings(@Nullable Boolean active) {
        super(active);
    }
}
