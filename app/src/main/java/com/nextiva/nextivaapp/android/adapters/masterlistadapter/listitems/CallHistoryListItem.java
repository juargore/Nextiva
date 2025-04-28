/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.CallLogEntry;

/**
 * Created by adammacdonald on 2/12/18.
 */

public class CallHistoryListItem extends SimpleBaseListItem<CallLogEntry> {

    private String mFormattedDateTime;
    private final boolean mActionButtonVisible;
    private final boolean mListItemLongClickable;
    @Nullable
    private final String mSearchTerm;

    public CallHistoryListItem(
            @NonNull CallLogEntry data,
            boolean actionButtonVisible,
            boolean listItemLongClickable,
            @Nullable String searchTerm) {

        super(data);
        mActionButtonVisible = actionButtonVisible;
        mListItemLongClickable = listItemLongClickable;
        mSearchTerm = searchTerm;
    }

    public String getFormattedDateTime() {
        return mFormattedDateTime;
    }

    public void setFormattedDateTime(String formattedDateTime) {
        mFormattedDateTime = formattedDateTime;
    }

    public boolean isActionButtonVisible() {
        return mActionButtonVisible;
    }

    public boolean isListItemLongClickable() {
        return mListItemLongClickable;
    }

    @Nullable
    public String getSearchTerm() {
        return mSearchTerm;
    }
}
