package com.nextiva.nextivaapp.android.models.mobileConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by Thaddeus Dannar on 3/7/19.
 */
public class XmppTest {
    Xmpp mXmpp = new Xmpp();

    @Before
    public void setup() {
        mXmpp = new Xmpp();
    }

    @Test
    public void getDomain_returnsCorrectValue() {
        assertNull(mXmpp.getDomain());
    }

    @Test
    public void setDomain_setsCorrectValue() {
        mXmpp.setDomain("domain");
        assertEquals("domain", mXmpp.getDomain());
    }

    @Test
    public void getKeepAliveTimeOut_returnsCorrectValue() {
        assertEquals(0, mXmpp.getKeepAliveTimeOut());
    }

    @Test
    public void setKeepAliveTimeOut_setsCorrectValue() {
        mXmpp.setKeepAliveTimeOut(5);
        assertEquals(5, mXmpp.getKeepAliveTimeOut());
    }

    @Test
    public void getUsername_returnsCorrectValue() {
        assertNull(mXmpp.getUsername());
    }

    @Test
    public void setUsername_setsCorrectValue() {
        mXmpp.setUsername("user");
        assertEquals("user", mXmpp.getUsername());
    }

    @Test
    public void getPassword_returnsCorrectValue() {
        assertNull(mXmpp.getPassword());
    }

    @Test
    public void setPassword_setsCorrectValue() {
        mXmpp.setPassword("password");
        assertEquals("password", mXmpp.getPassword());
    }
}