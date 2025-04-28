package com.nextiva.nextivaapp.android.models.mobileConfig;

/**
 * Created by Thaddeus Dannar on 3/6/19.
 */
public class Xsi {
    private boolean mNextivaAnywhereEnabled;
    private boolean mRemoteOfficeEnabled;
    private String mXsiRoot;
    private String mXsiActions;
    private String mXsiEvents;

    public Xsi() {
    }

    public String getXsiRoot() {
        return mXsiRoot;
    }

    public void setXsiRoot(String xsiRoot) {
        mXsiRoot = xsiRoot;
    }

    public String getXsiActions() {
        return mXsiActions;
    }

    public void setXsiActions(String xsiActions) {
        mXsiActions = xsiActions;
    }

    public String getXsiEvents() {
        return mXsiEvents;
    }

    public void setXsiEvents(String xsiEvents) {
        mXsiEvents = xsiEvents;
    }

    public boolean isNextivaAnywhereEnabled() {
        return mNextivaAnywhereEnabled;
    }

    public void setNextivaAnywhereEnabled(final boolean nextivaAnywhereEnabled) {
        this.mNextivaAnywhereEnabled = nextivaAnywhereEnabled;
    }

    public boolean isRemoteOfficeEnabled() {
        return mRemoteOfficeEnabled;
    }

    public void setRemoteOfficeEnabled(final boolean remoteOfficeEnabled) {
        this.mRemoteOfficeEnabled = remoteOfficeEnabled;
    }
}
