package com.nextiva.nextivaapp.android.models;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Created by Thaddeus Dannar on 9/21/18.
 */
public class VideoDimensionsTest {

    @Test
    public void getWidth() {
        VideoDimensions videoDimensions = new VideoDimensions(100, 200);
        assertEquals(100, videoDimensions.getWidth());
    }

    @Test
    public void setWidth() {
        VideoDimensions videoDimensions = new VideoDimensions(100, 200);
        videoDimensions.setWidth(300);
        assertEquals(300, videoDimensions.getWidth());
    }

    @Test
    public void getHeight() {
        VideoDimensions videoDimensions = new VideoDimensions(100, 200);
        assertEquals(200, videoDimensions.getHeight());
    }

    @Test
    public void setHeight() {
        VideoDimensions videoDimensions = new VideoDimensions(100, 200);
        videoDimensions.setHeight(400);
        assertEquals(400, videoDimensions.getHeight());
    }

    @Test
    public void setWidthHeight() {
        VideoDimensions videoDimensions = new VideoDimensions(100, 200);
        videoDimensions.setWidthHeight(300, 400);
        assertEquals(300, videoDimensions.getWidth());
        assertEquals(400, videoDimensions.getHeight());
    }
}