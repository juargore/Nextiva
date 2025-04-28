package com.nextiva.nextivaapp.android.models.mobileConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.nextiva.nextivaapp.android.models.AudioCodec;
import com.nextiva.nextivaapp.android.models.VideoCodec;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by Thaddeus Dannar on 3/7/19.
 */
public class SipTest {

    private Sip mSip;
    private Udp mUdp = new Udp(false, 1);
    private Tcp mTcp = new Tcp(false, 1);

    private ArrayList<AudioCodec> mAudioCodecs;
    private ArrayList<VideoCodec> mVideoCodecs;

    @Before
    public void setup() {
        mSip = new Sip();
        mSip.setUdp(mUdp);
        mSip.setTcp(mTcp);
        AudioCodec audioCodec = new AudioCodec();
        audioCodec.setName("PCMU");
        VideoCodec videoCodec = new VideoCodec();
        videoCodec.setName("PCMU");

        mAudioCodecs = new ArrayList<>();
        mAudioCodecs.add(audioCodec);
        mVideoCodecs = new ArrayList<>();
        mVideoCodecs.add(videoCodec);
        mSip.setAudioCodecs(mAudioCodecs);
        mSip.setVideoCodecs(mVideoCodecs);
    }

    @Test
    public void getAudioCodecs_returnsCorrectValue() {
        assertEquals(mAudioCodecs, mSip.getAudioCodecs());
    }

    @Test
    public void setAudioCodecs_setsCorrectValue() {
        ArrayList<AudioCodec> audioCodecs = new ArrayList<>();
        AudioCodec audioCodec = new AudioCodec();
        audioCodec.setName("TEST");
        audioCodecs.add(audioCodec);
        assertNotEquals(audioCodecs, mSip.getAudioCodecs());
        mSip.setAudioCodecs(audioCodecs);
        assertEquals(audioCodecs, mSip.getAudioCodecs());
    }

    @Test
    public void getAuthorizationUsername_returnsCorrectValue() {
        assertNull(mSip.getAuthorizationUsername());
    }

    @Test
    public void setAuthorizationUsername_setsCorrectValue() {
        mSip.setAuthorizationUsername("user");
        assertEquals("user", mSip.getAuthorizationUsername());
    }

    @Test
    public void getDomain_returnsCorrectValue() {
        assertNull(mSip.getDomain());
    }

    @Test
    public void setDomain_setsCorrectValue() {
        mSip.setDomain("domain");
        assertEquals("domain", mSip.getDomain());
    }

    @Test
    public void getUserAgent_returnsCorrectValue() {
        assertNull(mSip.getUserAgent());
    }

    @Test
    public void setUserAgent_setsCorrectValue() {
        mSip.setUserAgent("useragent");
        assertEquals("useragent", mSip.getUserAgent());
    }

    @Test
    public void getPreferredPort_returnsCorrectValue() {
        assertEquals(0, mSip.getPreferredPort());
    }

    @Test
    public void setPreferredPort_setsCorrectValue() {
        mSip.setPreferredPort(5000);
        assertEquals(5000, mSip.getPreferredPort());
    }

    @Test
    public void getProxyDomain_returnsCorrectValue() {
        assertNull(mSip.getDomain());
    }

    @Test
    public void setProxyDomain_setsCorrectValue() {
        mSip.setProxyDomain("proxyDomain");
        assertEquals("proxyDomain", mSip.getProxyDomain());
    }

    @Test
    public void getProxyPort_returnsCorrectValue() {
        assertEquals(0, mSip.getProxyPort());
    }

    @Test
    public void setProxyPort_setsCorrectValue() {
        mSip.setProxyPort(5000);
        assertEquals(5000, mSip.getProxyPort());
    }

    @Test
    public void getRegistrarPort_returnsCorrectValue() {
        assertEquals(0, mSip.getRegistrarPort());
    }

    @Test
    public void setRegistrarPort_setsCorrectValue() {
        mSip.setRegistrarPort(5000);
        assertEquals(5000, mSip.getRegistrarPort());
    }

    @Test
    public void getRegistrationRefreshInterval_returnsCorrectValue() {
        assertNull(mSip.getRegistrationRefreshInterval());
    }

    @Test
    public void setRegistrationRefreshInterval_setsCorrectValue() {
        mSip.setRegistrationRefreshInterval("1000");
        assertEquals("1000", mSip.getRegistrationRefreshInterval());
    }

    @Test
    public void getRegistrationUri_returnsCorrectValue() {
        assertNull(mSip.getRegistrationUri());
    }

    @Test
    public void setRegistrationUri_setsCorrectValue() {
        mSip.setRegistrationRefreshInterval("URI");
        assertEquals("URI", mSip.getRegistrationRefreshInterval());
    }

    @Test
    public void getSessionExpiresSec_returnsCorrectValue() {
        assertEquals(0, mSip.getSessionExpiresSec());
    }

    @Test
    public void setSessionExpiresSec_setsCorrectValue() {
        mSip.setSessionExpiresSec(5);
        assertEquals(5, mSip.getSessionExpiresSec());
    }

    @Test
    public void getTcp_returnsCorrectValue() {
        assertEquals(mTcp, mSip.getTcp());
    }

    @Test
    public void setTcp_setsCorrectValue() {
        Tcp tcp = new Tcp(true, 100);
        assertNotEquals(tcp, mSip.getTcp());
        mSip.setTcp(tcp);
        assertEquals(tcp, mSip.getTcp());
    }

    @Test
    public void getTransportType_returnsCorrectValue() {
        assertNull(mSip.getRegistrationUri());
    }

    @Test
    public void setTransportType_setsCorrectValue() {
        mSip.setTransportType("Transportation");
        assertEquals("Transportation", mSip.getTransportType());
    }

    @Test
    public void getUdp_returnsCorrectValue() {
        assertEquals(mUdp, mSip.getUdp());
    }

    @Test
    public void setUdp_setsCorrectValue() {
        Udp udp = new Udp(true, 100);
        assertNotEquals(udp, mSip.getUdp());
        mSip.setUdp(udp);
        assertEquals(udp, mSip.getUdp());
    }

    @Test
    public void getUsername_returnsCorrectValue() {
        assertNull(mSip.getUsername());
    }

    @Test
    public void setUsername_setsCorrectValue() {
        mSip.setUsername("User");
        assertEquals("User", mSip.getUsername());
    }

    @Test
    public void getPassword_returnsCorrectValue() {
        assertNull(mSip.getPassword());
    }

    @Test
    public void setPassword_setsCorrectValue() {
        mSip.setPassword("Password");
        assertEquals("Password", mSip.getPassword());
    }

    @Test
    public void isUseRport_returnsCorrectValue() {
        assertFalse(mSip.isUseRport());
    }

    @Test
    public void setUseRport_setsCorrectValue() {
        mSip.setUseRport(true);
        assertTrue(mSip.isUseRport());
    }

    @Test
    public void isUseSipInfo_returnsCorrectValue() {
        assertFalse(mSip.isUseSipInfo());
    }

    @Test
    public void setUseSipInfo_setsCorrectValue() {
        mSip.setUseSipInfo(true);
        assertTrue(mSip.isUseSipInfo());
    }

    @Test
    public void getVideoCodecs_returnsCorrectValue() {
        assertEquals(mVideoCodecs, mSip.getVideoCodecs());
    }

    @Test
    public void setVideoCodecs_setsCorrectValue() {
        ArrayList<VideoCodec> videoCodecs = new ArrayList<>();
        VideoCodec videoCodec = new VideoCodec();
        videoCodec.setName("PCMU");
        videoCodecs.add(videoCodec);
        assertNotEquals(videoCodecs, mSip.getVideoCodecs());
        mSip.setVideoCodecs(videoCodecs);
        assertEquals(videoCodecs, mSip.getVideoCodecs());
    }
}