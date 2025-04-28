package com.nextiva.nextivaapp.android.models.net.broadsoft.services;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thaddeus Dannar on 11/8/20.
 */

@Root(name = "unavailableCodeDetail", strict = false)
public class BroadsoftUnavailableDetail {

    @Nullable
    @Attribute(name = "isDefault", required = false)
    private Boolean mIsDefault;

    @Nullable
    @Element(name = "active", required = false)
    private Boolean active;

    @Nullable
    @Element(name = "code", required = false)
    private String code;

    @Nullable
    @Element(name = "description", required = false)
    private String description;


    @Nullable
    public Boolean getDefault() {
        return mIsDefault;
    }

    public void setDefault(@Nullable final Boolean aDefault) {
        mIsDefault = aDefault;
    }

    @Nullable
    public Boolean getActive() {
        return active;
    }

    public void setActive(@Nullable final Boolean active) {
        this.active = active;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable final String description) {
        this.description = description;
    }
}
