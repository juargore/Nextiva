package com.nextiva.nextivaapp.android.models.mobileConfig;

/**
 * Created by Thaddeus Dannar on 3/6/19.
 */
public class Udp extends Transport {

    public Udp(boolean isEnabled, int timeoutSec) {
        setKeepAliveEnabled(isEnabled);
        setKeepAliveTimeOut(timeoutSec);
    }
}
