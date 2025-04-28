package com.nextiva.nextivaapp.android.models;

/**
 * Created by Thaddeus Dannar on 9/14/18.
 */
public class VideoDimensions {

    private
    int width;
    private
    int height;

    public VideoDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(final int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(final int height) {
        this.height = height;
    }


    public void setWidthHeight(final int width, final int height) {
        this.width = width;
        this.height = height;
    }

}
