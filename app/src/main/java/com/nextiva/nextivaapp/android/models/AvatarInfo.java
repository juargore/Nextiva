package com.nextiva.nextivaapp.android.models;

import static java.lang.annotation.RetentionPolicy.SOURCE;

import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.FontRes;
import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.db.model.DbPresence;

import java.lang.annotation.Retention;

public class AvatarInfo {

    public static final int SIZE_SMALL = 0;
    public static final int SIZE_LARGE = 1;
    public static final int SIZE_MENU_ITEM = 2;

    @Retention(SOURCE)
    @IntDef( {
            SIZE_SMALL,
            SIZE_LARGE,
            SIZE_MENU_ITEM
    })
    private @interface Size {
    }

    @Nullable
    private byte[] mPhotoData;
    @Nullable
    private String mDisplayName;
    @DrawableRes
    private int mIconResId;
    @StringRes
    private int mFontAwesomeIconResId;
    @FontRes
    private int mFontAwesomeFontResId = R.font.fa_solid_900;
    @Size
    private int mSize;
    @Nullable
    private DbPresence mPresence;
    private boolean isCounter = false;
    private boolean mAlwaysShowIcon = false;
    private int textColor = R.color.white;
    private int backgroundColor = R.color.avatarBackground;
    private boolean mIsConnect;
    @DimenRes
    private int mStrokeWidthResId;

    private AvatarInfo(@Nullable byte[] photoData,
                       @Nullable String displayName,
                       int iconResId,
                       int fontAwesomeIconResId,
                       int fontAwesomeFontResId,
                       @Size int size,
                       @Nullable DbPresence presence,
                       boolean isConnect,
                       boolean alwaysShowIcon,
                       int strokeWidthResId) {

        mPhotoData = photoData;
        mDisplayName = displayName;
        mIconResId = iconResId;
        mFontAwesomeIconResId = fontAwesomeIconResId;
        mFontAwesomeFontResId = fontAwesomeFontResId;
        mSize = size;
        mPresence = presence;
        mIsConnect = isConnect;
        mAlwaysShowIcon = alwaysShowIcon;
        mStrokeWidthResId = strokeWidthResId;

        if (isConnect) {
            textColor = R.color.connectSecondaryDarkBlue;
            backgroundColor = R.color.connectGrey03;
        }
    }

    @Nullable
    public byte[] getPhotoData() {
        return mPhotoData;
    }

    public void setPhotoData(@Nullable byte[] photoData) {
        mPhotoData = photoData;
    }

    @Nullable
    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(@Nullable String displayName) {
        isCounter = false;
        mDisplayName = displayName;
    }

    public int getIconResId() {
        return mIconResId;
    }

    public void setIconResId(int iconResId) {
        mIconResId = iconResId;
    }

    public int getFontAwesomeIconResId() {
        return mFontAwesomeIconResId;
    }

    public void setFontAwesomeIconResId(int fontAwesomeIconResId) {
        mFontAwesomeIconResId = fontAwesomeIconResId;
    }

    public int getFontAwesomeFontResId() {
        return mFontAwesomeFontResId;
    }

    public void setFontAwesomeFontResId(int fontAwesomeFontResId) {
        mFontAwesomeFontResId = fontAwesomeFontResId;
    }

    @Size
    public int getSize() {
        return mSize;
    }

    public void setSize(@Size int size) {
        mSize = size;
    }

    @Nullable
    public DbPresence getPresence() {
        return mPresence;
    }

    public void setPresence(@Nullable DbPresence mPresence) {
        this.mPresence = mPresence;
    }

    public boolean isCounter() {
        return isCounter;
    }

    public void setCounter(int count) {
        this.isCounter = true;
        this.mDisplayName = String.valueOf(count);
    }

    public void setPlusCounter(int count){
        this.isCounter = true;
        this.mDisplayName = "+" + count;
    }

    public boolean getAlwaysShowIcon() {
        return mAlwaysShowIcon;
    }

    public void setAlwaysShowIcon(boolean alwaysShowIcon) {
        this.mAlwaysShowIcon = alwaysShowIcon;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public boolean isConnect() {
        return mIsConnect;
    }

    public void setIsConnect(boolean isConnect) {
        mIsConnect = isConnect;
    }

    public int getStrokeWidthResId() {
        return mStrokeWidthResId;
    }

    public void setStrokeWidthResId(int strokeWidthResId) {
        mStrokeWidthResId = strokeWidthResId;
    }

    public static class Builder {

        @Nullable
        private byte[] mPhotoData;
        @Nullable
        private String mDisplayName;
        @DrawableRes
        private int mIconResId = 0;
        @StringRes
        private int mFontAwesomeIconResId = 0;
        @FontRes
        private int mFontAwesomeFontResId = R.font.fa_solid_900;
        @Size
        private int mSize = SIZE_SMALL;
        @Nullable
        private DbPresence mPresence;
        private boolean mIsConnect = false;
        private boolean mAlwaysShowIcon = false;
        private int mStrokeWidthResId = 0;

        public Builder() {
        }

        public Builder setPhotoData(@Nullable byte[] photoData) {
            mPhotoData = photoData;
            return this;
        }

        public Builder setDisplayName(@Nullable String displayName) {
            mDisplayName = displayName;
            return this;
        }

        public Builder setIconResId(int iconResId) {
            mIconResId = iconResId;
            return this;
        }

        public Builder setFontAwesomeIconResId(int fontAwesomeIconResId) {
            mFontAwesomeIconResId = fontAwesomeIconResId;
            return this;
        }

        public Builder setFontAwesomeFontResId(int fontAwesomeFontResId) {
            mFontAwesomeFontResId = fontAwesomeFontResId;
            return this;
        }

        public Builder setSize(@Size int size) {
            mSize = size;
            return this;
        }

        public Builder setPresence(@Nullable DbPresence presence) {
            mPresence = presence;
            return this;
        }

        public Builder isConnect(boolean isConnect) {
            mIsConnect = isConnect;
            return this;
        }

        public Builder setAlwaysShowIcon(boolean alwaysShowIcon) {
            mAlwaysShowIcon = alwaysShowIcon;
            return this;
        }

        public Builder setStrokeWidthResId(int strokeWidthResId) {
            mStrokeWidthResId = strokeWidthResId;
            return this;
        }

        public AvatarInfo build() {
            return new AvatarInfo(mPhotoData,
                    mDisplayName,
                    mIconResId,
                    mFontAwesomeIconResId,
                    mFontAwesomeFontResId,
                    mSize,
                    mPresence,
                    mIsConnect,
                    mAlwaysShowIcon,
                    mStrokeWidthResId);
        }
    }
}
