package com.nextiva.nextivaapp.android.xmpp;

import io.reactivex.annotations.Nullable;

/**
 * Created by Thaddeus Dannar on 2/18/19.
 */
public class XMPPUser {
    @Nullable
    final private String mUserName;
    @Nullable
    final private String mPassword;
    @Nullable
    final private String mDomain;
    @Nullable
    final private int mKeepAliveIntervalSec;
    @Nullable
    final private String mResource;

    public XMPPUser(@Nullable final String userName, @Nullable final String password, @Nullable final String domain, @Nullable final int keepAliveIntervalSec, @Nullable final String resource) {
        mUserName = userName;
        mPassword = password;
        mDomain = domain;
        mKeepAliveIntervalSec = keepAliveIntervalSec;
        mResource = resource;
    }

    @Nullable
    public String getUserName() {
        return mUserName;
    }

    @Nullable
    public String getPassword() {
        return mPassword;
    }

    @Nullable
    public String getDomain() {
        return mDomain;
    }

    @Nullable
    public int getKeepAliveIntervalSec() {
        return mKeepAliveIntervalSec;
    }

    @Nullable
    public String getResource() {
        return mResource;
    }
}
