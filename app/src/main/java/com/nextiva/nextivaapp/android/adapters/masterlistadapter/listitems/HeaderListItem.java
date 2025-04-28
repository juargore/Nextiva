/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.db.model.DbGroup;
import com.nextiva.nextivaapp.android.models.ListHeaderRow;

import java.util.ArrayList;

/**
 * Created by adammacdonald on 2/8/18.
 */

public class HeaderListItem extends SimpleBaseListItem<ListHeaderRow> {

    @Nullable
    private ArrayList<BaseListItem> mBaseListItemsList;
    private final boolean mIsClickable;
    private DbGroup mGroup;
    private boolean mIsExpanded;
    private final boolean mListItemLongClickable;

    public HeaderListItem(@NonNull ListHeaderRow data,
                          @Nullable ArrayList<BaseListItem> baseListItemsList,
                          boolean isClickable,
                          boolean listItemLongClickable) {
        super(data);
        mBaseListItemsList = baseListItemsList;
        mIsClickable = isClickable;
        mIsExpanded = true;
        mListItemLongClickable = listItemLongClickable;
    }

    public HeaderListItem(@NonNull ListHeaderRow data,
                          @Nullable ArrayList<BaseListItem> baseListItemsList,
                          boolean isClickable,
                          DbGroup group,
                          boolean listItemLongClickable) {
        super(data);
        mBaseListItemsList = baseListItemsList;
        mIsClickable = isClickable;
        mIsExpanded = true;
        mGroup = group;
        mListItemLongClickable = listItemLongClickable;
    }

    public boolean isListItemLongClickable() {
        return mListItemLongClickable;
    }

    @Nullable
    public ArrayList<BaseListItem> getBaseListItemsList() {
        return mBaseListItemsList;
    }

    public DbGroup getGroup() {
        return mGroup;
    }

    public void addToListItems(BaseListItem listItem) {
        if (mBaseListItemsList == null) {
            mBaseListItemsList = new ArrayList<>();
        }

        mBaseListItemsList.add(listItem);
    }

    public boolean isClickable() {
        return mIsClickable;
    }

    public boolean isExpanded() {
        return mIsExpanded;
    }

    public void setExpanded(boolean expanded) {
        mIsExpanded = expanded;
    }
}
