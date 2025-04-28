package com.nextiva.nextivaapp.android.xmpp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Created by Thaddeus Dannar on 2/20/19.
 */
public class XMPPUserTest {

    XMPPUser mXMPPUser = new XMPPUser("User", "Password", "Domain", 1000, "Resource");

    @Test
    public void getUserName_returnNull() {
        XMPPUser xmppUser = new XMPPUser(null, null, null, 1, null);
        assertNull(xmppUser.getUserName());
    }

    @Test
    public void getUserName_returnValue() {
        assertEquals("User", mXMPPUser.getUserName());
    }

    @Test
    public void getPassword_returnNull() {
        XMPPUser xmppUser = new XMPPUser(null, null, null, 1, null);
        assertNull(xmppUser.getPassword());
    }

    @Test
    public void getPassword_returnValue() {
        assertEquals("Password", mXMPPUser.getPassword());
    }

    @Test
    public void getDomain_returnNull() {
        XMPPUser xmppUser = new XMPPUser(null, null, null, 1, null);
        assertNull(xmppUser.getDomain());
    }

    @Test
    public void getDomain_returnValue() {
        assertEquals("Domain", mXMPPUser.getDomain());
    }

    @Test
    public void getKeepAliveIntervalSec_returnValue() {
        assertEquals(1000, mXMPPUser.getKeepAliveIntervalSec());
    }

    @Test
    public void getResource_returnNull() {
        XMPPUser xmppUser = new XMPPUser(null, null, null, 1, null);
        assertNull(xmppUser.getResource());
    }

    @Test
    public void getResource_returnValue() {
        assertEquals("Resource", mXMPPUser.getResource());
    }
}