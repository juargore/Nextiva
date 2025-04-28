package com.nextiva.nextivaapp.android.models.mobileConfig;

/**
 * Created by Thaddeus Dannar on 3/6/19.
 */
public class Transport {
    private boolean mKeepAliveEnabled;
    private int mKeepAliveTimeOut;

    public Transport() {
    }

    public boolean getKeepAliveEnabled() {
        return mKeepAliveEnabled;
    }

    public void setKeepAliveEnabled(final boolean keepAliveEnabled) {
        this.mKeepAliveEnabled = keepAliveEnabled;
    }

    public int getKeepAliveTimeOut() {
        return mKeepAliveTimeOut;
    }

    public void setKeepAliveTimeOut(final int keepAliveTimeOut) {
        this.mKeepAliveTimeOut = keepAliveTimeOut;
    }
}
