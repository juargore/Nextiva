package com.nextiva.nextivaapp.android.models.mobileConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by Thaddeus Dannar on 3/7/19.
 */
public class MobileConfigTest {

    private MobileConfig mMobileConfig;
    private Calls mCalls = new Calls();
    private Xmpp mXmpp = new Xmpp();
    private Xsi mXsi = new Xsi();
    private Sip mSip = new Sip();

    @Before
    public void setup() {
        mMobileConfig = new MobileConfig();
        mMobileConfig.setCalls(mCalls);
        mMobileConfig.setSip(mSip);
        mMobileConfig.setXmpp(mXmpp);
        mMobileConfig.setXsi(mXsi);

    }

    @Test
    public void getCalls_returnsCorrectValue() {
        assertEquals(mCalls, mMobileConfig.getCalls());
    }

    @Test
    public void setCalls_setsCorrectValue() {
        Calls calls = new Calls();
        assertNotEquals(calls, mMobileConfig.getCalls());
        mMobileConfig.setCalls(calls);
        assertEquals(calls, mMobileConfig.getCalls());
    }

    @Test
    public void getSip_returnsCorrectValue() {
        assertEquals(mSip, mMobileConfig.getSip());
    }

    @Test
    public void setSip_setsCorrectValue() {
        Sip sip = new Sip();
        assertNotEquals(sip, mMobileConfig.getSip());
        mMobileConfig.setSip(sip);
        assertEquals(sip, mMobileConfig.getSip());
    }

    @Test
    public void getVoicemailPhoneNumber_returnsCorrectValue() {
        assertNull(mMobileConfig.getVoicemailPhoneNumber());
    }

    @Test
    public void setVoicemailPhoneNumber_setsCorrectValue() {
        mMobileConfig.setVoicemailPhoneNumber("Voicemail");
        assertEquals("Voicemail", mMobileConfig.getVoicemailPhoneNumber());
    }

    @Test
    public void getXmpp_returnsCorrectValue() {
        assertEquals(mXmpp, mMobileConfig.getXmpp());
    }

    @Test
    public void setXmpp_setsCorrectValue() {
        Xmpp xmpp = new Xmpp();
        assertNotEquals(xmpp, mMobileConfig.getXmpp());
        mMobileConfig.setXmpp(xmpp);
        assertEquals(xmpp, mMobileConfig.getXmpp());
    }

    @Test
    public void getXsi_returnsCorrectValue() {

        assertEquals(mXsi, mMobileConfig.getXsi());
    }

    @Test
    public void setXsi_setsCorrectValue() {
        Xsi xsi = new Xsi();
        assertNotEquals(xsi, mMobileConfig.getXsi());
        mMobileConfig.setXsi(xsi);
        assertEquals(xsi, mMobileConfig.getXsi());
    }
}