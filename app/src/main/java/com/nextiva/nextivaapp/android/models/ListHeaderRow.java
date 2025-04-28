package com.nextiva.nextivaapp.android.models;

import androidx.annotation.Nullable;

/**
 * Created by Thaddeus Dannar on 3/1/18.
 */

public class ListHeaderRow {

    @Nullable
    private String mTitle;

    public ListHeaderRow() {
    }

    public ListHeaderRow(@Nullable String title) {
        mTitle = title;
    }

    @Nullable
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(@Nullable String title) {
        mTitle = title;
    }
}
