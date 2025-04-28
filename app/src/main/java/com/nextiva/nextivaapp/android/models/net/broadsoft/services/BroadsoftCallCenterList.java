package com.nextiva.nextivaapp.android.models.net.broadsoft.services;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thaddeus Dannar on 11/6/20.
 */

@Root(name = "callCenterDetails", strict = false)
public class BroadsoftCallCenterList {

    @Nullable
    @Element(name = "serviceUserId", required = false)
    private String serviceUserId;

    @Nullable
    @Element(name = "available", required = false)
    private Boolean available;

    @Nullable
    @Element(name = "isLogOffAllowed", required = false)
    private Boolean isLogOffAllowed;

    @Nullable
    @Element(name = "phoneNumber", required = false)
    private String phoneNumber;

    @Nullable
    @Element(name = "extension", required = false)
    private String extension;


    public BroadsoftCallCenterList() {
    }

    @Nullable
    public String getServiceUserId() {
        return serviceUserId;
    }

    public void setServiceUserId(@Nullable final String serviceUserId) {
        this.serviceUserId = serviceUserId;
    }

    @Nullable
    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(@Nullable final Boolean available) {
        this.available = available;
    }

    @Nullable
    public Boolean getLogOffAllowed() {
        return isLogOffAllowed;
    }

    public void setLogOffAllowed(@Nullable final Boolean logOffAllowed) {
        isLogOffAllowed = logOffAllowed;
    }

    @Nullable
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(@Nullable final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Nullable
    public String getExtension() {
        return extension;
    }

    public void setExtension(@Nullable final String extension) {
        this.extension = extension;
    }
}
