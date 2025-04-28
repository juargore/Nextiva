package com.nextiva.nextivaapp.android.models.mobileConfig;

import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.AudioCodec;
import com.nextiva.nextivaapp.android.models.VideoCodec;

import java.util.ArrayList;

/**
 * Created by Thaddeus Dannar on 3/6/19.
 */
public class Sip {
    @Nullable
    private ArrayList<AudioCodec> mAudioCodecs;
    @Nullable
    private String mAuthorizationUsername;
    @Nullable
    private String mDomain;
    @Nullable
    private String mUserAgent;
    private int mPreferredPort;
    @Nullable
    private String mProxyDomain;
    private int mProxyPort;
    private int mRegistrarPort;
    @Nullable
    private String mRegistrationRefreshInterval;
    @Nullable
    private String mRegistrationUri;
    private int mSessionExpiresSec;
    @Nullable
    private Tcp mTcp;
    @Nullable
    private String mTransportType;
    @Nullable
    private Udp mUdp;
    @Nullable
    private String mUsername;
    @Nullable
    private String mPassword;
    private boolean mUseRport;
    private boolean mUseSipInfo;
    @Nullable
    private ArrayList<VideoCodec> mVideoCodecs;
    private Conference mConference;


    public Sip() {
    }

    @Nullable
    public ArrayList<AudioCodec> getAudioCodecs() {
        return mAudioCodecs;
    }

    public void setAudioCodecs(@Nullable final ArrayList<AudioCodec> audioCodecs) {
        mAudioCodecs = audioCodecs;
    }

    @Nullable
    public String getAuthorizationUsername() {
        return mAuthorizationUsername;
    }

    public void setAuthorizationUsername(@Nullable final String authorizationUsername) {
        mAuthorizationUsername = authorizationUsername;
    }

    @Nullable
    public String getDomain() {
        return mDomain;
    }

    public void setDomain(@Nullable final String domain) {
        mDomain = domain;
    }

    @Nullable
    public String getUserAgent() {
        return mUserAgent;
    }

    public void setUserAgent(@Nullable final String userAgent) {
        mUserAgent = userAgent;
    }

    public int getPreferredPort() {
        return mPreferredPort;
    }

    public void setPreferredPort(final int preferredPort) {
        mPreferredPort = preferredPort;
    }

    @Nullable
    public String getProxyDomain() {
        return mProxyDomain;
    }

    public void setProxyDomain(@Nullable final String proxyDomain) {
        mProxyDomain = proxyDomain;
    }

    public int getProxyPort() {
        return mProxyPort;
    }

    public void setProxyPort(final int proxyPort) {
        mProxyPort = proxyPort;
    }

    public int getRegistrarPort() {
        return mRegistrarPort;
    }

    public void setRegistrarPort(final int registrarPort) {
        mRegistrarPort = registrarPort;
    }

    @Nullable
    public String getRegistrationRefreshInterval() {
        return mRegistrationRefreshInterval;
    }

    public void setRegistrationRefreshInterval(@Nullable final String registrationRefreshInterval) {
        mRegistrationRefreshInterval = registrationRefreshInterval;
    }

    @Nullable
    public String getRegistrationUri() {
        return mRegistrationUri;
    }

    public void setRegistrationUri(@Nullable final String registrationUri) {
        mRegistrationUri = registrationUri;
    }

    public int getSessionExpiresSec() {
        return mSessionExpiresSec;
    }

    public void setSessionExpiresSec(final int sessionExpiresSec) {
        mSessionExpiresSec = sessionExpiresSec;
    }

    @Nullable
    public Tcp getTcp() {
        return mTcp;
    }

    public void setTcp(@Nullable final Tcp tcp) {
        mTcp = tcp;
    }

    @Nullable
    public String getTransportType() {
        return mTransportType;
    }

    public void setTransportType(@Nullable final String transportType) {
        mTransportType = transportType;
    }

    @Nullable
    public Udp getUdp() {
        return mUdp;
    }

    public void setUdp(@Nullable final Udp udp) {
        mUdp = udp;
    }

    @Nullable
    public String getUsername() {
        return mUsername;
    }

    public void setUsername(@Nullable final String username) {
        mUsername = username;
    }

    @Nullable
    public String getPassword() {
        return mPassword;
    }

    public void setPassword(@Nullable final String password) {
        mPassword = password;
    }

    public boolean isUseRport() {
        return mUseRport;
    }

    public void setUseRport(final boolean useRport) {
        this.mUseRport = useRport;
    }

    public boolean isUseSipInfo() {
        return mUseSipInfo;
    }

    public void setUseSipInfo(final boolean useSipInfo) {
        this.mUseSipInfo = useSipInfo;
    }

    @Nullable
    public ArrayList<VideoCodec> getVideoCodecs() {
        return mVideoCodecs;
    }

    public void setVideoCodecs(@Nullable final ArrayList<VideoCodec> videoCodecs) {
        mVideoCodecs = videoCodecs;
    }

    public Conference getConference() {
        return mConference;
    }

    public void setConference(final Conference conference) {
        mConference = conference;
    }
}
