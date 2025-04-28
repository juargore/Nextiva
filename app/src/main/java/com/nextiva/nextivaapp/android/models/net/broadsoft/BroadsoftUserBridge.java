package com.nextiva.nextivaapp.android.models.net.broadsoft;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thaddeus Dannar on 5/1/20.
 */
@Root(name = "userBridge", strict = false)
public class BroadsoftUserBridge {

    @Nullable
    @Element(name = "bridgeId", required = false)
    private String mBridgeId;
    @Nullable
    @Element(name = "name", required = false)
    private String mName;
    @Nullable
    @Element(name = "phoneNumber", required = false)
    private String mPhoneNumber;
    @Nullable
    @Element(name = "extension", required = false)
    private String mExtension;
    @Nullable
    @Element(name = "ports", required = false)
    private BroadsoftPorts mPorts;
    @Nullable
    @Element(name = "isActive", required = false)
    private Boolean mIsActive;
    @Nullable
    @Element(name = "allowIndividualOutDial", required = false)
    private String mAllowIndividualOutDial;
    @Nullable
    @Element(name = "bridgeUri", required = false)
    private String mBridgeUri;

    public BroadsoftUserBridge() {
    }

    @Nullable
    public String getBridgeId() {
        return mBridgeId;
    }

    public void setBridgeId(@Nullable final String bridgeId) {
        mBridgeId = bridgeId;
    }

    @Nullable
    public String getName() {
        return mName;
    }

    public void setName(@Nullable final String name) {
        mName = name;
    }

    @Nullable
    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(@Nullable final String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    @Nullable
    public String getExtension() {
        return mExtension;
    }

    public void setExtension(@Nullable final String extension) {
        mExtension = extension;
    }

    @Nullable
    public BroadsoftPorts getPorts() {
        return mPorts;
    }

    public void setPorts(@Nullable final BroadsoftPorts ports) {
        mPorts = ports;
    }

    @Nullable
    public Boolean getActive() {
        return mIsActive;
    }

    public void setActive(@Nullable final Boolean active) {
        mIsActive = active;
    }

    @Nullable
    public String getAllowIndividualOutDial() {
        return mAllowIndividualOutDial;
    }

    public void setAllowIndividualOutDial(@Nullable final String allowIndividualOutDial) {
        mAllowIndividualOutDial = allowIndividualOutDial;
    }

    @Nullable
    public String getBridgeUri() {
        return mBridgeUri;
    }

    public void setBridgeUri(@Nullable final String bridgeUri) {
        mBridgeUri = bridgeUri;
    }
}
