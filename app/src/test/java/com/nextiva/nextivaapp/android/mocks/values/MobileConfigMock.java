package com.nextiva.nextivaapp.android.mocks.values;

import com.nextiva.nextivaapp.android.models.AudioCodec;
import com.nextiva.nextivaapp.android.models.VideoCodec;
import com.nextiva.nextivaapp.android.models.mobileConfig.Calls;
import com.nextiva.nextivaapp.android.models.mobileConfig.MobileConfig;
import com.nextiva.nextivaapp.android.models.mobileConfig.Sip;
import com.nextiva.nextivaapp.android.models.mobileConfig.Tcp;
import com.nextiva.nextivaapp.android.models.mobileConfig.Udp;
import com.nextiva.nextivaapp.android.models.mobileConfig.Xmpp;
import com.nextiva.nextivaapp.android.models.mobileConfig.Xsi;

import java.util.ArrayList;

/**
 * Created by Thaddeus Dannar on 3/8/19.
 */
public class MobileConfigMock {
    public static MobileConfig getMobileConfig() {
        MobileConfig mobileConfig = new MobileConfig();
        Sip sip = new Sip();
        Xmpp xmpp = new Xmpp();
        Xsi xsi = new Xsi();
        Calls calls = new Calls();

        ArrayList<AudioCodec> audioCodecs = new ArrayList<>();
        AudioCodec audioCodec = new AudioCodec();
        audioCodec.setName("TEST");
        audioCodecs.add(audioCodec);

        ArrayList<VideoCodec> videoCodecs = new ArrayList<>();
        VideoCodec videoCodec = new VideoCodec();
        videoCodec.setName("PCMU");
        videoCodecs.add(videoCodec);

        Tcp tcp = new Tcp(true, 1);
        Udp udp = new Udp(true, 1);


        sip.setProxyPort(5000);
        sip.setRegistrarPort(5001);
        sip.setRegistrarPort(5003);
        sip.setSessionExpiresSec(1);
        sip.setUseRport(true);
        sip.setUseSipInfo(true);
        sip.setPassword("Password");
        sip.setUsername("User");
        sip.setDomain("www.google.com");
        sip.setTransportType("type");
        sip.setRegistrationRefreshInterval("1000");
        sip.setUserAgent("useragent");
        sip.setAuthorizationUsername("auth");
        sip.setRegistrationUri("uri");


        sip.setAudioCodecs(audioCodecs);
        sip.setVideoCodecs(videoCodecs);
        sip.setTcp(tcp);
        sip.setUdp(udp);

        calls.setRejectWith486(true);
        calls.setVideoQos(true);
        calls.setVideoQos(true);

        xsi.setRemoteOfficeEnabled(true);
        xsi.setNextivaAnywhereEnabled(true);

        xmpp.setPassword("password");
        xmpp.setUsername("username@fake.com");
        xmpp.setDomain("domain");
        xmpp.setKeepAliveTimeOut(1);


        mobileConfig.setCalls(calls);
        mobileConfig.setXsi(xsi);
        mobileConfig.setSip(sip);
        mobileConfig.setXmpp(xmpp);
        mobileConfig.setVoicemailPhoneNumber("9999");


        return mobileConfig;
    }


    public static MobileConfig getMobileConfigEmpty() {
        MobileConfig mobileConfig = new MobileConfig();
        Sip sip = new Sip();
        Xmpp xmpp = new Xmpp();
        Xsi xsi = new Xsi();
        Calls calls = new Calls();

        ArrayList<AudioCodec> audioCodecs = new ArrayList<>();
        AudioCodec audioCodec = new AudioCodec();
        audioCodec.setName("");
        audioCodecs.add(audioCodec);

        ArrayList<VideoCodec> videoCodecs = new ArrayList<>();
        VideoCodec videoCodec = new VideoCodec();
        videoCodec.setName("");
        videoCodecs.add(videoCodec);

        Tcp tcp = new Tcp(false, 0);
        Udp udp = new Udp(false, 0);


        sip.setProxyPort(0);
        sip.setRegistrarPort(0);
        sip.setRegistrarPort(0);
        sip.setSessionExpiresSec(0);
        sip.setUseRport(false);
        sip.setUseSipInfo(false);
        sip.setPassword("");
        sip.setUsername("");
        sip.setDomain("");
        sip.setTransportType("");
        sip.setRegistrationRefreshInterval("");
        sip.setUserAgent("");
        sip.setAuthorizationUsername("");
        sip.setRegistrationUri("");


        sip.setAudioCodecs(audioCodecs);
        sip.setVideoCodecs(videoCodecs);
        sip.setTcp(tcp);
        sip.setUdp(udp);

        calls.setRejectWith486(false);
        calls.setVideoQos(false);
        calls.setVideoQos(false);

        xsi.setRemoteOfficeEnabled(false);
        xsi.setNextivaAnywhereEnabled(false);

        xmpp.setPassword("");
        xmpp.setUsername("");
        xmpp.setDomain("");
        xmpp.setKeepAliveTimeOut(0);


        mobileConfig.setCalls(calls);
        mobileConfig.setXsi(xsi);
        mobileConfig.setSip(sip);
        mobileConfig.setXmpp(xmpp);

        return mobileConfig;
    }

}
