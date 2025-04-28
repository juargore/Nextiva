package com.nextiva.nextivaapp.android.models.mobileConfig;

import android.text.TextUtils;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Thaddeus Dannar on 3/6/19.
 */
public class Xmpp {
    @Nullable
    private String mDomain;
    private int mKeepAliveTimeOut;
    @Nullable
    private String mUsername;
    @Nullable
    private String mPassword;


    public Xmpp() {
    }

    @Nullable
    public String getDomain() {
        return mDomain;
    }

    public void setDomain(@Nullable final String domain) {
        this.mDomain = domain;
    }

    public int getKeepAliveTimeOut() {
        return mKeepAliveTimeOut;
    }

    public void setKeepAliveTimeOut(final int keepAliveTimeOut) {
        this.mKeepAliveTimeOut = keepAliveTimeOut;
    }

    @Nullable
    public String getUsername() {
        return mUsername;
    }

    public void setUsername(@Nullable final String username) {
        this.mUsername = username;
    }

    @Nullable
    public String getPassword() {
        return mPassword;
    }

    public void setPassword(@Nullable final String password) {
        mPassword = password;
    }


    @NonNull
    public String getXmppAuthorizationHeader() {
        if (!TextUtils.isEmpty(getUsername()) && !TextUtils.isEmpty(getPassword())) {
            return "Basic " + Base64.encodeToString((getUsername() + ":" + getPassword()).getBytes(), Base64.NO_WRAP);
        }

        return "";
    }
}
