package com.nextiva.nextivaapp.android.models.mobileConfig;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by Thaddeus Dannar on 3/7/19.
 */
public class XsiTest {
    Xsi mXsi;


    @Before
    public void setup() {
        mXsi = new Xsi();
    }


    @Test
    public void isNextivaAnywhereEnabled_returnsCorrectValue() {
        assertFalse(mXsi.isNextivaAnywhereEnabled());
    }

    @Test
    public void setNextivaAnywhereEnabled_setsCorrectValue() {
        mXsi.setNextivaAnywhereEnabled(true);
        assertTrue(mXsi.isNextivaAnywhereEnabled());
    }

    @Test
    public void isRemoteOfficeEnabled_returnsCorrectValue() {
        assertFalse(mXsi.isRemoteOfficeEnabled());
    }

    @Test
    public void setRemoteOfficeEnabled_setsCorrectValue() {
        mXsi.setRemoteOfficeEnabled(true);
        assertTrue(mXsi.isRemoteOfficeEnabled());
    }
}