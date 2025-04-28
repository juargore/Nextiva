package com.nextiva.nextivaapp.android.models.mobileConfig;

/**
 * Created by Thaddeus Dannar on 2019-08-16.
 */
public class Conference {

    private boolean mIsEnabled;
    private boolean mIsXSIEnabled;
    private String mServiceURI;
    private boolean mCallParticipants;
    private boolean mIsSubscribeConferenceInfo;
    private boolean isDoNotHoldConferenceBeforeRefers;

    public Conference() {
    }

    public boolean isEnabled() {
        return mIsEnabled;
    }

    public void setEnabled(final boolean enabled) {
        mIsEnabled = enabled;
    }

    public boolean isXSIEnabled() {
        return mIsXSIEnabled;
    }

    public void setXSIEnabled(final boolean XSIEnabled) {
        mIsXSIEnabled = XSIEnabled;
    }

    public String getServiceURI() {
        return mServiceURI;
    }

    public void setServiceURI(final String serviceURI) {
        mServiceURI = serviceURI;
    }

    public boolean isCallParticipants() {
        return mCallParticipants;
    }

    public void setCallParticipants(final boolean callParticipants) {
        mCallParticipants = callParticipants;
    }

    public boolean isSubscribeConferenceInfo() {
        return mIsSubscribeConferenceInfo;
    }

    public void setSubscribeConferenceInfo(final boolean subscribeConferenceInfo) {
        mIsSubscribeConferenceInfo = subscribeConferenceInfo;
    }

    public boolean isDoNotHoldConferenceBeforeRefers() {
        return isDoNotHoldConferenceBeforeRefers;
    }

    public void setDoNotHoldConferenceBeforeRefers(final boolean doNotHoldConferenceBeforeRefers) {
        isDoNotHoldConferenceBeforeRefers = doNotHoldConferenceBeforeRefers;
    }
}
