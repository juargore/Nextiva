package com.nextiva.nextivaapp.android.models;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Created by Thaddeus Dannar on 9/21/18.
 */
public class AudioCodecTest {

    @Test
    public void setName_setsNameCorrectly() {
        AudioCodec audioCodec = new AudioCodec();
        audioCodec.setName("Test");

        assertEquals("Test", audioCodec.getName());
    }

    @Test
    public void setPriority_setsPriorityCorrectly() {
        AudioCodec audioCodec = new AudioCodec();
        audioCodec.setPriority("0.1");

        assertEquals("0.1", audioCodec.getPriority());
    }

    @Test
    public void setPayload_setPayloadCorrectly() {
        AudioCodec audioCodec = new AudioCodec();
        audioCodec.setPayload("1");

        assertEquals("1", audioCodec.getPayload());
    }

    @Test
    public void setInBand_setInBandCorrectly() {
        AudioCodec audioCodec = new AudioCodec();
        audioCodec.setInBand("TestBand");

        assertEquals("TestBand", audioCodec.getInBand());
    }

    @Test
    public void getName_getCorrectName() {
        AudioCodec audioCodec = new AudioCodec();
        audioCodec.setName("Test");

        assertEquals("Test", audioCodec.getName());
    }

    @Test
    public void getPriority_getCorrectPriority() {
        AudioCodec audioCodec = new AudioCodec();
        audioCodec.setPriority("0.1");

        assertEquals("0.1", audioCodec.getPriority());
    }

    @Test
    public void getPayload_getCorrectPayload() {
        AudioCodec audioCodec = new AudioCodec();
        audioCodec.setPayload("1");

        assertEquals("1", audioCodec.getPayload());
    }

    @Test
    public void getInBand_getCorrectInBand() {
        AudioCodec audioCodec = new AudioCodec();
        audioCodec.setInBand("TestBand");

        assertEquals("TestBand", audioCodec.getInBand());
    }
}