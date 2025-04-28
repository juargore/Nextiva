/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.util;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.FontRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Constants;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.model.DbPresence;
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager;
import com.nextiva.nextivaapp.android.models.AvatarInfo;

import java.io.ByteArrayOutputStream;

import javax.inject.Inject;

/**
 * Created by joedephillipo on 3/1/18.
 */

public class NextivaAvatarManager implements AvatarManager {

    private final Application mApplication;
    private final SettingsManager mSettingsManager;

    private final Paint mTextPaint;
    private final Paint mAvatarPaint;
    private final Paint mPresencePaint;

    private final int mStrokeWidth;
    private final PorterDuffXfermode mRoundBitmapXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);

    @Inject
    public NextivaAvatarManager(Application application,
                                SettingsManager settingsManager) {
        mApplication = application;
        mSettingsManager = settingsManager;

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAvatarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPresencePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mStrokeWidth = mApplication.getResources().getDimensionPixelSize(R.dimen.hairline_small);
    }

    @Override
    public Bitmap byteArrayToBitmap(@Nullable byte[] avatarByteArray) {
        if (avatarByteArray != null) {
            try {
                return BitmapFactory.decodeByteArray(avatarByteArray, 0, avatarByteArray.length);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        } else {
            return null;
        }
    }


    @NonNull
    private String getInitials(@Nullable String displayName) {
        if (!TextUtils.isEmpty(displayName)) {
            if (!Patterns.EMAIL_ADDRESS.matcher(displayName).matches()) {
                String[] split = displayName.toLowerCase().split("\\s+");

                String firstLetter = null;
                String secondLetter = null;
                String initials = "";

                if (split.length > 0) {
                    for (String substring : split) {
                        if (!TextUtils.isEmpty(substring) && Character.isLetter(substring.charAt(0))) {
                            String initial = Character.toString(substring.charAt(0)).toUpperCase();

                            if (TextUtils.isEmpty(firstLetter)) {
                                firstLetter = initial;
                            } else {
                                secondLetter = initial;
                            }
                        }
                    }

                    if (!TextUtils.isEmpty(firstLetter)) {
                        initials += firstLetter;
                    }

                    if (!TextUtils.isEmpty(secondLetter)) {
                        initials += secondLetter;
                    }

                    if (!TextUtils.isEmpty(initials)) {
                        return initials;
                    }
                }
            }
        }

        return "";
    }

    @NonNull
    private Bitmap getBitmap(String initials, int width, int height, int textSize, int textColor, boolean isConnect, int strokeWidthId) {
        Bitmap avatarBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(avatarBitmap);
        float canvasHalfHeight = getRadius(canvas.getHeight());
        float canvasHalfWidth = getRadius(canvas.getWidth());

        drawAvatarColorBackground(canvas, canvasHalfWidth, canvasHalfHeight, width, isConnect);

        if (strokeWidthId != 0) {
            drawStroke(canvas, canvasHalfWidth, canvasHalfHeight, width, strokeWidthId);
        }

        // Draw initials text
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(ContextCompat.getColor(mApplication, textColor));
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        canvas.drawText(
                initials,
                canvasHalfWidth,
                ((canvasHalfHeight) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2)),
                mTextPaint);

        return avatarBitmap;
    }

    @NonNull
    private Bitmap getBitmap(String initials, int width, int height, int textSize, int textColor, int backgroundColor, int strokeWidthId) {
        Bitmap avatarBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(avatarBitmap);
        float canvasHalfHeight = getRadius(canvas.getHeight());
        float canvasHalfWidth = getRadius(canvas.getWidth());

        drawConnectColoredAvatars(canvas, canvasHalfWidth, canvasHalfHeight, width, backgroundColor);

        if (strokeWidthId != 0) {
            drawStroke(canvas, canvasHalfWidth, canvasHalfHeight, width, strokeWidthId);
        }

        // Draw initials text
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(ContextCompat.getColor(mApplication, textColor));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        canvas.drawText(
                initials,
                canvasHalfWidth,
                ((canvasHalfHeight) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2)),
                mTextPaint);

        return avatarBitmap;
    }

    public byte[] scaleImageByteArrayUnderTwoMB(byte[] bytes) {
        try {
            Bitmap bitmap = byteArrayToBitmap(bytes);
            if (bitmap != null) {
                if (bytes != null && bitmap.getByteCount() > 2 * 1024 * 1024) {
                    while (bitmap.getByteCount() > 2 * 1024 * 1024) {
                        bitmap = reduceBitmapSize(bitmap, 512 * 512);
                    }
                    return bitmapToByteArray(bitmap);
                }

                return bitmapToByteArray(bitmap);
            }
        } catch (Exception e) {
            Log.d("", "scaleImageByteArrayUnderTwoMB: ");
        }

        return null;
    }

    @Override
    public Bitmap scaleBitmapForMMS(Bitmap bitmap, int fileSize) {
        if (bitmap != null && fileSize > Constants.MMS.MAX_FILE_SIZE) {
            while (bitmap.getAllocationByteCount() > Constants.MMS.MAX_FILE_SIZE) {
                bitmap = reduceBitmapSize(bitmap, 512 * 512);
            }
        }

        return bitmap;
    }

    private Bitmap reduceBitmapSize(Bitmap bitmap, int MAX_SIZE) {
        double ratioSquare;
        int bitmapHeight = bitmap.getHeight();
        int bitmapWidth = bitmap.getWidth();

        ratioSquare = (bitmapHeight * bitmapWidth) / (double) MAX_SIZE;

        if (ratioSquare <= 1) {
            return bitmap;
        }

        double ratio = Math.sqrt(ratioSquare);
        int requiredHeight = (int) Math.round(bitmapHeight / ratio);
        int requiredWidth = (int) Math.round(bitmapWidth / ratio);

        return Bitmap.createScaledBitmap(bitmap, requiredWidth, requiredHeight, true);
    }

    @NonNull
    private Bitmap getConnectBitmap(@StringRes int fontAwesomeResId, @FontRes int fontAwesomeFontId, int width, int height, int textSize, int strokeWidthId) {
        return getConnectBitmap(
                fontAwesomeResId,
                fontAwesomeFontId,
                width,
                height,
                textSize,
                strokeWidthId,
                ContextCompat.getColor(mApplication, R.color.connectGrey08),
                ContextCompat.getColor(mApplication, R.color.connectGrey03));
    }

    @NonNull
    private Bitmap getConnectBitmap(@StringRes int fontAwesomeResId, @FontRes int fontAwesomeFontId, int width, int height, int textSize, int strokeWidthId, int textColor, int backgroundColor) {
        Bitmap avatarBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(avatarBitmap);
        float canvasHalfHeight = getRadius(canvas.getHeight());
        float canvasHalfWidth = getRadius(canvas.getWidth());

        drawConnectColoredAvatars(canvas, canvasHalfWidth, canvasHalfHeight, width, backgroundColor);

        if (strokeWidthId != 0) {
            drawStroke(canvas, canvasHalfWidth, canvasHalfHeight, width, strokeWidthId);
        }

        // Draw initials text
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(textColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTypeface(ResourcesCompat.getFont(mApplication, fontAwesomeFontId));

        canvas.drawText(
                mApplication.getString(fontAwesomeResId),
                canvasHalfWidth,
                ((canvasHalfHeight) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2)),
                mTextPaint);

        return avatarBitmap;
    }

    @NonNull
    private Bitmap getBitmap(@DrawableRes int drawableResId, int width, int height, boolean whiteBackground, boolean isConnect, int strokeWidthId) {
        Bitmap avatarBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(avatarBitmap);
        float canvasHalfHeight = getRadius(canvas.getHeight());
        float canvasHalfWidth = getRadius(canvas.getWidth());

        if (whiteBackground) {
            drawWhiteColorBackground(canvas, canvasHalfWidth, canvasHalfHeight, width, isConnect);
        } else {
            drawAvatarColorBackground(canvas, canvasHalfWidth, canvasHalfHeight, width, isConnect);
        }

        if (strokeWidthId != 0) {
            drawStroke(canvas, canvasHalfWidth, canvasHalfHeight, width, strokeWidthId);
        }

        boolean isConference = drawableResId == R.drawable.ic_phone;
        boolean isLarge = width == mApplication.getResources().getDimensionPixelSize(R.dimen.general_view_xxxxxxxlarge);

        int mConferenceDrawableLargePadding = 20;
        int mConferenceDrawableSmallPadding = 12;
        int drawableStartBounds = isConference ? isLarge ? mConferenceDrawableLargePadding : mConferenceDrawableSmallPadding : 0;
        int drawableEndBounds = isConference ? isLarge ? canvas.getWidth() - mConferenceDrawableLargePadding : canvas.getWidth() - mConferenceDrawableSmallPadding : canvas.getWidth();

        // Draw icon
        Drawable drawable = AppCompatResources.getDrawable(mApplication, drawableResId);
        if (drawable != null) {
            drawable.setColorFilter(ContextCompat.getColor(mApplication, whiteBackground ? R.color.avatarBackground : R.color.white), PorterDuff.Mode.SRC_ATOP);
            drawable.setBounds(drawableStartBounds,
                               drawableStartBounds,
                               drawableEndBounds,
                               drawableEndBounds);
            drawable.draw(canvas);
        }

        return avatarBitmap;
    }

    @NonNull
    private Bitmap getBitmap(@DrawableRes int drawableResId, int width, int height, int strokeWidthId) {
        Bitmap avatarBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(avatarBitmap);
        float canvasHalfHeight = getRadius(canvas.getHeight());
        float canvasHalfWidth = getRadius(canvas.getWidth());

        boolean isConference = drawableResId == R.drawable.ic_phone;
        boolean isLarge = width == mApplication.getResources().getDimensionPixelSize(R.dimen.general_view_xxxxxxxlarge);

        int mConferenceDrawableLargePadding = 20;
        int mConferenceDrawableSmallPadding = 12;
        int drawableStartBounds = isConference ? isLarge ? mConferenceDrawableLargePadding : mConferenceDrawableSmallPadding : 0;
        int drawableEndBounds = isConference ? isLarge ? canvas.getWidth() - mConferenceDrawableLargePadding : canvas.getWidth() - mConferenceDrawableSmallPadding : canvas.getWidth();

        // Draw icon
        Drawable drawable = AppCompatResources.getDrawable(mApplication, drawableResId);
        if (drawable != null) {
            drawable.setBounds(drawableStartBounds,
                               drawableStartBounds,
                               drawableEndBounds,
                               drawableEndBounds);
            drawable.draw(canvas);
        }

        if (strokeWidthId != 0 && isLarge) {
            drawStroke(canvas, canvasHalfWidth, canvasHalfHeight, width, strokeWidthId);
        }

        return avatarBitmap;
    }

    @NonNull
    private Bitmap getBitmap(@Nullable Bitmap bitmap, int width, int height, boolean isConnect, int strokeWidthId) {
        Bitmap avatarBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(avatarBitmap);
        float bitmapMargin = mStrokeWidth * 1.5f;

        drawRoundedBitmap(canvas, bitmap, width, height, bitmapMargin, isConnect);

        if (strokeWidthId != 0) {
            drawStroke(canvas, width / 2f, height / 2f, width, strokeWidthId);
        }

        return avatarBitmap;
    }

    @NonNull
    private Bitmap getPresenceBitmap(int width, int height, @Nullable DbPresence presence, float sizeMultiplier) {
        Bitmap avatarBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(avatarBitmap);
        float canvasHalfHeight = getRadius(canvas.getHeight());
        float canvasHalfWidth = getRadius(canvas.getWidth());

        if (presence != null) {
            drawPresence(canvas, canvasHalfWidth, canvasHalfHeight, presence, sizeMultiplier);
        }

        return avatarBitmap;
    }

    private void drawWhiteColorBackground(@NonNull Canvas canvas, float x, float y, int width, boolean isConnect) {
        mAvatarPaint.setAntiAlias(true);
        mAvatarPaint.setColor(ContextCompat.getColor(mApplication, isConnect ? R.color.connectWhite : R.color.white));
        mAvatarPaint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(x, y, getRadius(width), mAvatarPaint);
    }

    private void drawAvatarColorBackground(@NonNull Canvas canvas, float x, float y, int width, boolean isConnect) {
        mAvatarPaint.setAntiAlias(true);
        mAvatarPaint.setColor(ContextCompat.getColor(mApplication, isConnect ? R.color.connectGrey03 : R.color.avatarBackground));
        mAvatarPaint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(x, y, getRadius(width), mAvatarPaint);
    }

    private void drawConnectColoredAvatars(@NonNull Canvas canvas, float x, float y, int width, int backgroundColor) {
        mAvatarPaint.setAntiAlias(true);
        mAvatarPaint.setColor(backgroundColor);
        mAvatarPaint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(x, y, getRadius(width), mAvatarPaint);
    }

    private void drawConnectAvatarColorBackground(@NonNull Canvas canvas, float x, float y, int width) {
        mAvatarPaint.setAntiAlias(true);
        mAvatarPaint.setColor(ContextCompat.getColor(mApplication, R.color.connectGrey03));
        mAvatarPaint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(x, y, getRadius(width), mAvatarPaint);
    }

    private void drawStroke(@NonNull Canvas canvas, float x, float y, int width, int strokeWidthId) {
        mAvatarPaint.setAntiAlias(false);
        mAvatarPaint.setColor(ContextCompat.getColor(mApplication, R.color.connectGrey01));
        mAvatarPaint.setStyle(Paint.Style.STROKE);
        mAvatarPaint.setStrokeWidth(mApplication.getResources().getDimensionPixelSize(strokeWidthId));

        canvas.drawCircle(x, y, getRadius(width - mApplication.getResources().getDimensionPixelSize(strokeWidthId)), mAvatarPaint);
    }

    private void drawPresence(@NonNull Canvas canvas, float x, float y, DbPresence presence, float sizeMultiplier) {
        if (getPresenceColor(presence) != -1) {
            float presenceImageOffset = 1.45f;
            float presenceRadius = x / 2.5f;

            float presenceStrokeWidth = mStrokeWidth * 2.6f;
            float xLocation = x * presenceImageOffset;
            float yLocation = y * presenceImageOffset;
            float iconWidth = mApplication.getResources().getDimensionPixelSize(R.dimen.general_view_xxsmall) * sizeMultiplier;
            float iconHeight = mApplication.getResources().getDimensionPixelSize(R.dimen.general_view_xxsmall) * sizeMultiplier;

            if (sizeMultiplier == 1.7f) { // large icon
                presenceImageOffset = 1.65f;
                presenceRadius = x / 3.5f;

                xLocation = x * presenceImageOffset;
                yLocation = y * presenceImageOffset;
                iconWidth = mApplication.getResources().getDimensionPixelSize(R.dimen.hairline_large) * sizeMultiplier;
                iconHeight = mApplication.getResources().getDimensionPixelSize(R.dimen.hairline_large) * sizeMultiplier;
            }

            mPresencePaint.setAntiAlias(true);
            mPresencePaint.setColor(getPresenceColor(presence));
            mPresencePaint.setStyle(Paint.Style.FILL);

            float canvasHalfHeight = getRadius(canvas.getHeight()) * presenceImageOffset;
            float canvasHalfWidth = getRadius(canvas.getWidth()) * presenceImageOffset;

            Drawable drawable;

            switch (presence.getState()) {
                case Enums.Contacts.PresenceStates.AVAILABLE:
                case Enums.Contacts.PresenceStates.CONNECT_ACTIVE:
                case Enums.Contacts.PresenceStates.CONNECT_ONLINE:
                    canvas.drawCircle(xLocation, yLocation, presenceRadius, mPresencePaint);

                    drawable = AppCompatResources.getDrawable(mApplication, R.drawable.ic_online);
                    if (drawable != null) {
                        drawable.setColorFilter(ContextCompat.getColor(mApplication, R.color.white), PorterDuff.Mode.SRC_ATOP);
                        drawable.setBounds((int) (canvasHalfWidth - iconWidth),
                                           (int) (canvasHalfHeight - iconHeight),
                                           (int) (canvasHalfWidth + iconWidth),
                                           (int) (canvasHalfHeight + iconHeight));
                        drawable.draw(canvas);
                    }
                    drawPresenceRing(canvas, Enums.Contacts.PresenceStates.CONNECT_ONLINE, xLocation, yLocation, presenceRadius, presenceStrokeWidth, getPresenceColor(presence));
                    break;
                case Enums.Contacts.PresenceStates.AWAY:
                case Enums.Contacts.PresenceStates.CONNECT_AWAY:
                    canvas.drawCircle(xLocation, yLocation, presenceRadius, mPresencePaint);

                    drawable = AppCompatResources.getDrawable(mApplication, R.drawable.ic_clock);
                    if (drawable != null) {
                        drawable.setColorFilter(ContextCompat.getColor(mApplication, R.color.white), PorterDuff.Mode.SRC_ATOP);
                        drawable.setBounds((int) (canvasHalfWidth - iconWidth),
                                           (int) (canvasHalfHeight - (iconHeight)),
                                           (int) (canvasHalfWidth + iconWidth),
                                           (int) (canvasHalfHeight + (iconHeight)));
                        drawable.draw(canvas);
                    }
                    drawPresenceRing(canvas, Enums.Contacts.PresenceStates.CONNECT_AWAY, xLocation, yLocation, presenceRadius, presenceStrokeWidth, getPresenceColor(presence));
                    break;
                case Enums.Contacts.PresenceStates.BUSY:
                    canvas.drawCircle(xLocation, yLocation, presenceRadius, mPresencePaint);

                    drawable = AppCompatResources.getDrawable(mApplication, R.drawable.ic_busy);
                    if (drawable != null) {
                        drawable.setColorFilter(ContextCompat.getColor(mApplication, R.color.white), PorterDuff.Mode.SRC_ATOP);
                        drawable.setBounds((int) (canvasHalfWidth - iconWidth),
                                           (int) (canvasHalfHeight - iconHeight),
                                           (int) (canvasHalfWidth + iconWidth),
                                           (int) (canvasHalfHeight + iconHeight));
                        drawable.draw(canvas);
                    }
                    drawPresenceRing(canvas, Enums.Contacts.PresenceStates.BUSY, xLocation, yLocation, presenceRadius, presenceStrokeWidth, getPresenceColor(presence));
                    break;
                case Enums.Contacts.PresenceStates.CONNECT_DND:
                    canvas.drawCircle(xLocation, yLocation, presenceRadius, mPresencePaint);

                    drawable = AppCompatResources.getDrawable(mApplication, R.drawable.ic_dnd);
                    if (drawable != null) {
                        drawable.setBounds((int) (canvasHalfWidth - iconWidth),
                                (int) (canvasHalfHeight - iconHeight),
                                (int) (canvasHalfWidth + iconWidth),
                                (int) (canvasHalfHeight + iconHeight));
                        drawable.draw(canvas);
                    }
                    drawPresenceRing(canvas, Enums.Contacts.PresenceStates.CONNECT_DND, xLocation, yLocation, presenceRadius, presenceStrokeWidth, getPresenceColor(presence));
                    break;
                case Enums.Contacts.PresenceStates.OFFLINE:
                case Enums.Contacts.PresenceStates.CONNECT_OFFLINE:
                    canvas.drawCircle(xLocation, yLocation, presenceRadius, mPresencePaint);
                    drawPresenceRing(canvas, Enums.Contacts.PresenceStates.CONNECT_OFFLINE, xLocation, yLocation, presenceRadius, presenceStrokeWidth, ContextCompat.getColor(mApplication, R.color.avatarConnectOfflinePresenceStroke));
                    break;
                case Enums.Contacts.PresenceStates.PENDING:
                    drawable = AppCompatResources.getDrawable(mApplication, R.drawable.ic_invite);
                    if (drawable != null) {
                        drawable.setBounds((int) (canvasHalfWidth - (iconWidth * 2)),
                                           (int) (canvasHalfHeight - (iconHeight * 2)),
                                           (int) (canvasHalfWidth + (iconWidth * 2)),
                                           (int) (canvasHalfHeight + (iconHeight * 2)));
                        drawable.draw(canvas);
                    }
                    break;
                case Enums.Contacts.PresenceStates.CONNECT_BE_RIGHT_BACK:
                case Enums.Contacts.PresenceStates.CONNECT_BUSY:
                case Enums.Contacts.PresenceStates.CONNECT_OUT_OF_OFFICE:
                case Enums.Contacts.PresenceStates.CONNECT_AUTOMATIC:
                    canvas.drawCircle(xLocation, yLocation, presenceRadius, mPresencePaint);
                    drawPresenceRing(canvas, Enums.Contacts.PresenceStates.CONNECT_BUSY, xLocation, yLocation, presenceRadius, presenceStrokeWidth, getPresenceColor(presence));
                    break;
            }
        }
    }

    private void drawPresenceRing(@NonNull Canvas canvas, int state, float x, float y, float width, float strokeWidth, int color) {
        boolean nightModeEnabled = ApplicationUtil.isNightModeEnabled(mApplication.getApplicationContext(), mSettingsManager);

        mAvatarPaint.setAntiAlias(true);
        mAvatarPaint.setColor(ContextCompat.getColor(mApplication, R.color.connectWhite));
        mAvatarPaint.setStyle(Paint.Style.STROKE);

        if (state == Enums.Contacts.PresenceStates.CONNECT_OFFLINE && nightModeEnabled) {
            // draw gray circle for the offline status only if night mode is enabled
            mAvatarPaint.setStrokeWidth(strokeWidth-2);
            canvas.drawCircle(x, y, (int) width-2, mAvatarPaint);
        } else {
            // draw white circle for any other status
            mAvatarPaint.setStrokeWidth(strokeWidth);
            canvas.drawCircle(x, y, (int) width, mAvatarPaint);
        }

        // draw internal gray border for the offline status
        if (state == Enums.Contacts.PresenceStates.CONNECT_OFFLINE) {
            mAvatarPaint.setColor(color);
            mAvatarPaint.setStrokeWidth(strokeWidth);
            mAvatarPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(x, y, (int) width-9f, mAvatarPaint);
        }
    }

    private int getPresenceColor(DbPresence presence) {
        switch (presence.getState()) {
            case Enums.Contacts.PresenceStates.AVAILABLE: {
                return ContextCompat.getColor(mApplication, R.color.avatarOnlinePresence);
            }
            case Enums.Contacts.PresenceStates.CONNECT_ACTIVE:
            case Enums.Contacts.PresenceStates.CONNECT_ONLINE: {
                return ContextCompat.getColor(mApplication, R.color.connectPrimaryGreen);
            }
            case Enums.Contacts.PresenceStates.AWAY: {
                return ContextCompat.getColor(mApplication, R.color.avatarAwayPresence);
            }
            case Enums.Contacts.PresenceStates.CONNECT_BUSY:
            case Enums.Contacts.PresenceStates.CONNECT_DND:
            case Enums.Contacts.PresenceStates.CONNECT_OUT_OF_OFFICE: {
                return ContextCompat.getColor(mApplication, R.color.connectPrimaryRed);
            }
            case Enums.Contacts.PresenceStates.BUSY: {
                return ContextCompat.getColor(mApplication, R.color.avatarBusyPresence);
            }
            case Enums.Contacts.PresenceStates.OFFLINE:
            case Enums.Contacts.PresenceStates.CONNECT_OFFLINE: {
                return ContextCompat.getColor(mApplication, R.color.avatarConnectOfflinePresenceFill);
            }
            case Enums.Contacts.PresenceStates.CONNECT_AUTOMATIC: {
                return ContextCompat.getColor(mApplication, R.color.connectSecondaryGrey);
            }
            case Enums.Contacts.PresenceStates.CONNECT_AWAY:
            case Enums.Contacts.PresenceStates.CONNECT_BE_RIGHT_BACK: {
                return ContextCompat.getColor(mApplication, R.color.connectPrimaryYellow);
            }
            case Enums.Contacts.PresenceStates.PENDING: {
                return ContextCompat.getColor(mApplication, R.color.avatarPendingPresence);
            }
            case Enums.Contacts.PresenceStates.NONE:
                return -1;
        }

        return -1;
    }

    private void drawRoundedBitmap(@NonNull Canvas canvas, @Nullable Bitmap bitmap, float width, float height, float bitmapMargin, boolean isConnect) {
        if (bitmap != null) {
            final Rect srcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF dstRectF = new RectF(bitmapMargin, bitmapMargin, width - bitmapMargin, height - bitmapMargin);

            mAvatarPaint.setAntiAlias(true);
            mAvatarPaint.setColor(ContextCompat.getColor(mApplication, isConnect ? R.color.connectWhite : R.color.white));
            mAvatarPaint.setStyle(Paint.Style.FILL);
            canvas.drawARGB(0, 0, 0, 0);
            canvas.drawOval(dstRectF, mAvatarPaint);

            mAvatarPaint.setXfermode(mRoundBitmapXfermode);
            canvas.drawBitmap(bitmap, srcRect, dstRectF, mAvatarPaint);
            mAvatarPaint.setXfermode(null);

            bitmap.recycle();
        }
    }

    private float getRadius(float diameter) {
        return diameter / 2f;
    }

    @ColorInt
    private int getBackgroundColor() {
        return ContextCompat.getColor(mApplication, (ApplicationUtil.isNightModeEnabled(mApplication, mSettingsManager)) ? R.color.black : R.color.white);
    }

    // --------------------------------------------------------------------------------------------
    // AvatarManager Methods
    // --------------------------------------------------------------------------------------------

    @Nullable
    @Override
    public Bitmap getPresenceBitmap(@NonNull AvatarInfo avatarInfo) {
        Bitmap output = null;
        int width, height;
        float sizeMultiplier;

        switch (avatarInfo.getSize()) {
            case AvatarInfo.SIZE_LARGE: {
                width = mApplication.getResources().getDimensionPixelSize(R.dimen.general_view_xxxxxxlarge);
                height = mApplication.getResources().getDimensionPixelSize(R.dimen.general_view_xxxxxxlarge);
                sizeMultiplier = 1.7f;
                break;
            }
            default:
            case AvatarInfo.SIZE_SMALL: {
                width = mApplication.getResources().getDimensionPixelSize(R.dimen.general_view_xxlarge);
                height = mApplication.getResources().getDimensionPixelSize(R.dimen.general_view_xxlarge);
                sizeMultiplier = 1;
                break;
            }
        }

        if (avatarInfo.getPresence() != null) {
            output = getPresenceBitmap(width, height, avatarInfo.getPresence(), sizeMultiplier);
        }

        return output;
    }

    @Nullable
    @Override
    public Bitmap getBitmap(@NonNull AvatarInfo avatarInfo) {
        Bitmap output = null;
        int width, height, textSize;

        switch (avatarInfo.getSize()) {
            case AvatarInfo.SIZE_LARGE: {
                width = mApplication.getResources().getDimensionPixelSize(R.dimen.general_view_xxxxxxxlarge);
                height = mApplication.getResources().getDimensionPixelSize(R.dimen.general_view_xxxxxxxlarge);
                textSize = mApplication.getResources().getDimensionPixelSize(R.dimen.avatar_initials_text_size_large);
                break;
            }
            case AvatarInfo.SIZE_MENU_ITEM: {
                width = mApplication.getResources().getDimensionPixelSize(R.dimen.general_view_large);
                height = mApplication.getResources().getDimensionPixelSize(R.dimen.general_view_large);
                textSize = mApplication.getResources().getDimensionPixelSize(R.dimen.avatar_initials_text_size_xsmall);
                break;
            }
            default:
            case AvatarInfo.SIZE_SMALL: {
                width = mApplication.getResources().getDimensionPixelSize(R.dimen.general_view_xxlarge);
                height = mApplication.getResources().getDimensionPixelSize(R.dimen.general_view_xxlarge);
                textSize = mApplication.getResources().getDimensionPixelSize(R.dimen.avatar_initials_text_size_small);
                break;
            }
        }

        if (avatarInfo.isCounter()) {
            if (avatarInfo.getStrokeWidthResId() == R.dimen.hairline_xlarge) {
                textSize = mApplication.getResources().getDimensionPixelSize(R.dimen.general_view_small);
            }
            output = getBitmap(avatarInfo.getDisplayName(), width, height, textSize, avatarInfo.getTextColor(), avatarInfo.isConnect(), avatarInfo.getStrokeWidthResId());

        } else if (avatarInfo.getPhotoData() != null) {
            output = getBitmap(byteArrayToBitmap(avatarInfo.getPhotoData()), width, height, avatarInfo.isConnect(), avatarInfo.getStrokeWidthResId());

        } else if (avatarInfo.getIconResId() != 0 &&
                (avatarInfo.getIconResId() == R.drawable.avatar_callflow ||
                        avatarInfo.getIconResId() == R.drawable.avatar_team ||
                        avatarInfo.getIconResId() == R.drawable.avatar_callcenter ||
                        avatarInfo.getIconResId() == R.drawable.xbert_avatar)) {
            output = getBitmap(avatarInfo.getIconResId(), width, height, avatarInfo.getStrokeWidthResId());
        } 
        else if (!TextUtils.isEmpty(getInitials(avatarInfo.getDisplayName()))) {
            if (avatarInfo.isConnect()) {
                setAvatarTextAndBackgroundColor(avatarInfo);
                if (avatarInfo.getAlwaysShowIcon()) {
                    output = getConnectBitmap(
                            avatarInfo.getFontAwesomeIconResId(),
                            avatarInfo.getFontAwesomeFontResId(),
                            width,
                            height,
                            textSize,
                            avatarInfo.getStrokeWidthResId(),
                            ContextCompat.getColor(mApplication, R.color.connectSecondaryDarkBlue),
                            avatarInfo.getBackgroundColor());
                } else {
                    output = getBitmap(getInitials(avatarInfo.getDisplayName()), width, height, textSize, avatarInfo.getTextColor(), avatarInfo.getBackgroundColor(), avatarInfo.getStrokeWidthResId());
                }
            } else {
                output = getBitmap(getInitials(avatarInfo.getDisplayName()), width, height, textSize, avatarInfo.getTextColor(), avatarInfo.isConnect(), avatarInfo.getStrokeWidthResId());
            }
        } else if (avatarInfo.getIconResId() != 0 && avatarInfo.getIconResId() == R.drawable.avatar_group) {
            output = getBitmap(avatarInfo.getIconResId(), width, height, true, avatarInfo.isConnect(), avatarInfo.getStrokeWidthResId());

        } else if (avatarInfo.getIconResId() != 0 && avatarInfo.getIconResId() == R.drawable.ic_phone) {
            output = getBitmap(avatarInfo.getIconResId(), width, height, false, avatarInfo.isConnect(), avatarInfo.getStrokeWidthResId());

        } else if (avatarInfo.getIconResId() != 0) {
            output = getBitmap(avatarInfo.getIconResId(), width, height, true, avatarInfo.isConnect(), avatarInfo.getStrokeWidthResId());

        } else if (avatarInfo.getFontAwesomeIconResId() != 0) {
            output = getConnectBitmap(avatarInfo.getFontAwesomeIconResId(), avatarInfo.getFontAwesomeFontResId(), width, height, textSize, avatarInfo.getStrokeWidthResId());
        }

        if (output == null) {
            if (avatarInfo.getFontAwesomeIconResId() != 0) {
                output = getConnectBitmap(avatarInfo.getFontAwesomeIconResId(), avatarInfo.getFontAwesomeFontResId(), width, height, textSize, avatarInfo.getStrokeWidthResId());

            } else {
                output = getBitmap(R.drawable.avatar_single_contact, width, height, true, avatarInfo.isConnect(), avatarInfo.getStrokeWidthResId());
            }
        }

        return output;
    }

    private void setAvatarTextAndBackgroundColor(AvatarInfo avatarInfo) {
        try {
            String initials = getInitials(avatarInfo.getDisplayName());
            int backgroundColorOptions = 9;
            int backgroundColor = R.color.avatarDefaultBgColor;
            int textColor = R.color.avatarDefaultTextColor;
            if (!TextUtils.isEmpty(initials)) {
                if (initials.length() <= 2 && initials.length() > 0) {
                    char[] arr = initials.toCharArray();
                    if (arr.length == 1) {
                        backgroundColor = ((int) arr[0]) % backgroundColorOptions;

                    } else {
                        backgroundColor = ((int) arr[0] + (int) arr[1]) % backgroundColorOptions;

                    }
                    textColor = R.color.secondaryDarkBlue;

                }
                avatarInfo.setTextColor(textColor);
                int[] rainbow = mApplication.getResources().getIntArray(R.array.ContactAvatar);
                if (rainbow != null && rainbow.length == backgroundColorOptions) {
                    avatarInfo.setBackgroundColor(rainbow[backgroundColor]);
                }

            }

        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    @Nullable
    @Override
    public String byteArrayToString(@Nullable byte[] bytes) {
        if (bytes != null) {
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } else {
            return null;
        }
    }

    @Override
    public byte[] bitmapToScaledDownByteArray(@NonNull Bitmap bitmap) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 96, 96, false);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.PNG, 70, out);

        return out.toByteArray();
    }

    @Override
    public byte[] bitmapToByteArray(@NonNull Bitmap bitmap) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), false);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

        return out.toByteArray();
    }

    @Nullable
    @Override
    public byte[] stringToByteArray(@Nullable String avatarString) {
        if (!TextUtils.isEmpty(avatarString)) {
            return Base64.decode(avatarString, Base64.DEFAULT);
        } else {
            return null;
        }
    }

    @Override
    public boolean isByteArrayNotEmpty(@Nullable byte[] bytes) {
        return bytes != null && bytes.length > 0;
    }
    // --------------------------------------------------------------------------------------------
}
