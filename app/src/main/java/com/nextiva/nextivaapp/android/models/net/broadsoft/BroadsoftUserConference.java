package com.nextiva.nextivaapp.android.models.net.broadsoft;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thaddeus Dannar on 5/1/20.
 */
@Root(name = "userConference", strict = false)
public class BroadsoftUserConference {

    @Nullable
    @Element(name = "bridgeId", required = false)
    private String mBridgeId;
    @Nullable
    @Element(name = "conferenceId", required = false)
    private String mConferenceId;
    @Nullable
    @Element(name = "title", required = false)
    private String mTitle;
    @Nullable
    @Element(name = "bridgeName", required = false)
    private String mBridgeName;
    @Nullable
    @Element(name = "status", required = false)
    private String mStatus;
    @Nullable
    @Element(name = "type", required = false)
    private String mType;
    @Nullable
    @Element(name = "startTime", required = false)
    private String mStartTime;
    @Nullable
    @Element(name = "isActive", required = false)
    private Boolean mIsActive;
    @Nullable
    @Element(name = "conferenceUri", required = false)
    private String mConferenceUri;

    public BroadsoftUserConference() {
    }

    @Nullable
    public String getBridgeId() {
        return mBridgeId;
    }

    public void setBridgeId(@Nullable final String bridgeId) {
        mBridgeId = bridgeId;
    }

    @Nullable
    public String getConferenceId() {
        return mConferenceId;
    }

    public void setConferenceId(@Nullable final String conferenceId) {
        mConferenceId = conferenceId;
    }

    @Nullable
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(@Nullable final String title) {
        mTitle = title;
    }

    @Nullable
    public String getBridgeName() {
        return mBridgeName;
    }

    public void setBridgeName(@Nullable final String bridgeName) {
        mBridgeName = bridgeName;
    }

    @Nullable
    public String getStatus() {
        return mStatus;
    }

    public void setStatus(@Nullable final String status) {
        mStatus = status;
    }

    @Nullable
    public String getType() {
        return mType;
    }

    public void setType(@Nullable final String type) {
        mType = type;
    }

    @Nullable
    public String getStartTime() {
        return mStartTime;
    }

    public void setStartTime(@Nullable final String startTime) {
        mStartTime = startTime;
    }

    @Nullable
    public Boolean getActive() {
        return mIsActive;
    }

    public void setActive(@Nullable final Boolean active) {
        mIsActive = active;
    }

    @Nullable
    public String getConferenceUri() {
        return mConferenceUri;
    }

    public void setConferenceUri(@Nullable final String conferenceUri) {
        mConferenceUri = conferenceUri;
    }
}
