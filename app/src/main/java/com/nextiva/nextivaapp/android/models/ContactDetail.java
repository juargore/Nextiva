/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.constants.Enums;

/**
 * Created by joedephillipo on 2/22/18.
 */

class ContactDetail {

    @Enums.Contacts.DetailViewTypes.Type
    private int mViewType;
    @Nullable
    private String mTitleText;
    @Nullable
    private String mSubTitleText;
    @DrawableRes
    private int mActionButtonOne;
    @DrawableRes
    private int mActionButtonTwo;

    public ContactDetail(@Enums.Contacts.DetailViewTypes.Type int viewType, @Nullable String titleText, @Nullable String subTitleText, @DrawableRes int actionButtonOne, @DrawableRes int actionButtonTwo) {
        mViewType = viewType;
        mTitleText = titleText;
        mSubTitleText = subTitleText;
        mActionButtonOne = actionButtonOne;
        mActionButtonTwo = actionButtonTwo;
    }

    public ContactDetail() {
    }

    @Enums.Contacts.DetailViewTypes.Type
    public int getViewType() {
        return mViewType;
    }

    public void setViewType(@Enums.Contacts.DetailViewTypes.Type int viewType) {
        mViewType = viewType;
    }

    @Nullable
    public String getTitleText() {
        return mTitleText;
    }

    public void setTitleText(@Nullable String titleText) {
        mTitleText = titleText;
    }

    @Nullable
    public String getSubTitleText() {
        return mSubTitleText;
    }

    public void setSubTitleText(@Nullable String subTitleText) {
        mSubTitleText = subTitleText;
    }

    @DrawableRes
    public int getActionButtonOneDrawable() {
        return mActionButtonOne;
    }

    public void setActionButtonOneDrawable(@DrawableRes int actionButtonOne) {
        mActionButtonOne = actionButtonOne;
    }

    @DrawableRes
    public int getActionButtonTwoDrawable() {
        return mActionButtonTwo;
    }

    public void setActionButtonTwoDrawable(@DrawableRes int actionButtonTwo) {
        mActionButtonTwo = actionButtonTwo;
    }
}
