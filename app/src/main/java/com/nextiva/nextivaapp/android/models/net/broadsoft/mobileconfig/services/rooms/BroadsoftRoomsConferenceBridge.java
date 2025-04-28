/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.rooms;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by adammacdonald on 2/23/18.
 */

@Root
public class BroadsoftRoomsConferenceBridge implements Serializable {

    @Nullable
    @Attribute(name = "autodetect", required = false)
    private String mAutoDetect;
    @Nullable
    @Attribute(name = "media", required = false)
    private String mMedia;
    @Nullable
    @Attribute(name = "type", required = false)
    private String mType;
    @Nullable
    @Attribute(name = "title", required = false)
    private String mTitle;
    @Nullable
    @Attribute(name = "default-bridge", required = false)
    private String mDefaultBridge;

    public BroadsoftRoomsConferenceBridge() {
    }

    @Nullable
    public String getAutoDetect() {
        return mAutoDetect;
    }

    @Nullable
    public String getMedia() {
        return mMedia;
    }

    @Nullable
    public String getType() {
        return mType;
    }

    @Nullable
    public String getTitle() {
        return mTitle;
    }

    @Nullable
    public String getDefaultBridge() {
        return mDefaultBridge;
    }
}
