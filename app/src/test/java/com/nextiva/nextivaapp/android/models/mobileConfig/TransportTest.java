package com.nextiva.nextivaapp.android.models.mobileConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by Thaddeus Dannar on 3/7/19.
 */
public class TransportTest {
    Transport mTransport;


    @Before
    public void setup() {
        mTransport = new Transport();
    }

    @Test
    public void getKeepAliveEnabled_returnsCorrectValue() {
        assertFalse(mTransport.getKeepAliveEnabled());
    }

    @Test
    public void setKeepAliveEnabled_setsCorrectValue() {
        mTransport.setKeepAliveEnabled(true);
        assertTrue(mTransport.getKeepAliveEnabled());
    }

    @Test
    public void getKeepAliveTimeOut_returnsCorrectValue() {
        assertEquals(0, mTransport.getKeepAliveTimeOut());
    }

    @Test
    public void setKeepAliveTimeOut_setsCorrectValue() {
        mTransport.setKeepAliveTimeOut(5);
        assertEquals(5, mTransport.getKeepAliveTimeOut());
    }
}