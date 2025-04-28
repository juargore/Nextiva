package com.nextiva.nextivaapp.android.models.net.broadsoft.conference;

/**
 * Created by Thaddeus Dannar on 2020-02-19.
 */

import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "Conference", strict = false)
public class Conference {

    @Nullable
    @Element(name = "state", required = false)
    private String mState;
    @Nullable
    @Element(name = "endpoint", required = false)
    private Endpoint mEndpoint;
    @Nullable
    @Element(name = "appearance", required = false)
    private String mAppearance;

    public Conference() {
    }

    @Nullable
    public String getState() {
        return mState;
    }

    public void setState(@Nullable final String state) {
        mState = state;
    }

    @Nullable
    public Endpoint getEndpoint() {
        return mEndpoint;
    }

    public void setEndpoint(@Nullable final Endpoint endpoint) {
        mEndpoint = endpoint;
    }

    @Nullable
    public String getAppearance() {
        return mAppearance;
    }

    public void setAppearance(@Nullable final String appearance) {
        mAppearance = appearance;
    }

}
