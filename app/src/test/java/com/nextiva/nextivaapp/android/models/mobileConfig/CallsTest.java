package com.nextiva.nextivaapp.android.models.mobileConfig;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by Thaddeus Dannar on 3/7/19.
 */
public class CallsTest {
    private Calls mCalls;

    @Before
    public void setup() {
        mCalls = new Calls();
    }

    @Test
    public void isAudioQos_returnsCorrectValue() {
        assertFalse(mCalls.isAudioQos());
    }

    @Test
    public void setAudioQos_setsCorrectValue() {
        mCalls.setAudioQos(true);
        assertTrue(mCalls.isAudioQos());
    }

    @Test
    public void isRejectWith486_returnsCorrectValue() {
        assertFalse(mCalls.isRejectWith486());
    }

    @Test
    public void setRejectWith486_setsCorrectValue() {
        mCalls.setRejectWith486(true);
        assertTrue(mCalls.isRejectWith486());
    }

    @Test
    public void isVideoQos_returnsCorrectValue() {
        assertFalse(mCalls.isVideoQos());
    }

    @Test
    public void setVideoQos_setsCorrectValue() {
        mCalls.setVideoQos(true);
        assertTrue(mCalls.isVideoQos());
    }
}