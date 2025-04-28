package com.nextiva.nextivaapp.android.models;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;


/**
 * Created by Thaddeus Dannar on 2/16/18.
 */

class ContactFilterListItem {

    @DrawableRes
    private final int mIcon;
    @StringRes
    private final int mTitle;
    @StringRes
    private final int mTitleFull;
    private final boolean mIsHeader;
    private boolean mIsSelected;

    public ContactFilterListItem(@DrawableRes int icon, @StringRes int title, @StringRes int titleFull, boolean isSelected) {
        mIcon = icon;
        mTitle = title;
        mTitleFull = titleFull;
        mIsSelected = isSelected;
        mIsHeader = false;
    }

    public ContactFilterListItem(boolean isHeader, @StringRes int title) {
        mIcon = 0;
        mTitle = title;
        mTitleFull = 0;
        mIsHeader = isHeader;
    }

    @DrawableRes
    public int getIcon() {
        return mIcon;
    }

    @StringRes
    public int getTitle() {
        return mTitle;
    }

    public int getTitleFull() {
        return mTitleFull;
    }

    public boolean getIsHeader() {
        return mIsHeader;
    }

    public boolean getIsSelected() {
        return mIsSelected;
    }

    public void setIsSelected(boolean isSelected) {
        mIsSelected = isSelected;
    }
}
