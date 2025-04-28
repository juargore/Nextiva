package com.nextiva.nextivaapp.android.models;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Created by Thaddeus Dannar on 9/21/18.
 */
public class VideoCodecTest {

    @Test
    public void setFramerate() {
        VideoCodec videoCodec = new VideoCodec();
        videoCodec.setFramerate("15");
        assertEquals("15", videoCodec.getFramerate());
    }

    @Test
    public void setPayload() {
        VideoCodec videoCodec = new VideoCodec();
        videoCodec.setPayload("1");

        assertEquals("1", videoCodec.getPayload());
    }

    @Test
    public void setBitrate() {
        VideoCodec videoCodec = new VideoCodec();
        videoCodec.setBitrate("200");

        assertEquals("200", videoCodec.getBitrate());
    }

    @Test
    public void setPriority() {
        VideoCodec videoCodec = new VideoCodec();
        videoCodec.setPriority("200");

        assertEquals("200", videoCodec.getPriority());
    }

    @Test
    public void setName() {
        VideoCodec videoCodec = new VideoCodec();
        videoCodec.setName("Test");

        assertEquals("Test", videoCodec.getName());
    }

    @Test
    public void setResolution() {
        VideoCodec videoCodec = new VideoCodec();
        videoCodec.setResolution("CIF");

        assertEquals("CIF", videoCodec.getResolution());
    }

    @Test
    public void getResolution() {
        VideoCodec videoCodec = new VideoCodec();
        videoCodec.setResolution("CIF");

        assertEquals("CIF", videoCodec.getResolution());
    }

    @Test
    public void getFramerate() {
        VideoCodec videoCodec = new VideoCodec();
        videoCodec.setFramerate("15");
        assertEquals("15", videoCodec.getFramerate());
    }

    @Test
    public void getBitrate() {
        VideoCodec videoCodec = new VideoCodec();
        videoCodec.setBitrate("200");

        assertEquals("200", videoCodec.getBitrate());
    }

    @Test
    public void getPayload() {
        VideoCodec videoCodec = new VideoCodec();
        videoCodec.setPayload("1");

        assertEquals("1", videoCodec.getPayload());
    }

    @Test
    public void getPriority() {
        VideoCodec videoCodec = new VideoCodec();
        videoCodec.setPriority("200");

        assertEquals("200", videoCodec.getPriority());
    }

    @Test
    public void getName() {
        VideoCodec videoCodec = new VideoCodec();
        videoCodec.setName("Test");

        assertEquals("Test", videoCodec.getName());
    }
}