/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.view.DetailItemView;

/**
 * Created by joedephillipo on 2/22/18.
 */

public abstract class DetailItemViewListItem extends BaseListItem {

    @NonNull
    private String mTitle;
    @Nullable
    private String mSubTitle;
    @DrawableRes
    private final int mActionButtonOneResId;
    @DrawableRes
    private final int mActionButtonTwoResId;
    private final boolean mIsClickable;
    private final boolean mIsLongClickable;
    private DetailItemView mItemView;

    public DetailItemViewListItem(
            @NonNull String title,
            @Nullable String subTitle,
            @DrawableRes int actionButtonOneResId,
            @DrawableRes int actionButtonTwoResId,
            boolean isClickable,
            boolean isLongClickable) {

        mTitle = title;
        mSubTitle = subTitle;
        mActionButtonOneResId = actionButtonOneResId;
        mActionButtonTwoResId = actionButtonTwoResId;
        mIsClickable = isClickable;
        mIsLongClickable = isLongClickable;
    }

    @NonNull
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(@NonNull String title) {
        mTitle = title;
    }

    @Nullable
    public String getSubTitle() {
        return mSubTitle;
    }

    public void setSubTitle(@Nullable String subTitle) {
        mSubTitle = subTitle;
    }

    @DrawableRes
    public int getActionButtonOneResId() {
        return mActionButtonOneResId;
    }


    @DrawableRes
    public int getActionButtonTwoResId() {
        return mActionButtonTwoResId;
    }


    public boolean isClickable() {
        return mIsClickable;
    }

    public boolean isLongClickable() {
        return mIsLongClickable;
    }

    public void setItemView(DetailItemView itemView) {
        mItemView = itemView;
    }

    public void setActionButtonTwoEnabled(boolean isEnabled) {
        if (mItemView != null) {
            mItemView.setAction2Enabled(isEnabled);
        }
    }
}
