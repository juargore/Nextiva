/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Constants;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.model.DbPresence;

/**
 * Created by adammacdonald on 3/5/18.
 */

public class PresenceView extends FrameLayout {

    private static final int VISIBLE_TEXT_VIEW_NONE = 0;
    private static final int VISIBLE_TEXT_VIEW_LEFT = 1;
    private static final int VISIBLE_TEXT_VIEW_RIGHT = 2;

    private TextView mLeftTextView;
    private TextView mRightTextView;
    private ImageView mImageView;

    public PresenceView(@NonNull Context context) {
        this(context, null);
    }

    @SuppressWarnings("WeakerAccess")
    public PresenceView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressWarnings("WeakerAccess")
    public PresenceView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PresenceView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            inflater.inflate(R.layout.view_presence, this, true);
        }

        mLeftTextView = findViewById(R.id.presence_left_text_view);
        mRightTextView = findViewById(R.id.presence_right_text_view);
        mImageView = findViewById(R.id.presence_image_view);

        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PresenceView);

            if (typedArray.hasValue(R.styleable.PresenceView_visibleTextView)) {
                int value = typedArray.getInt(R.styleable.PresenceView_visibleTextView, -1);

                if (value == VISIBLE_TEXT_VIEW_LEFT) {
                    mLeftTextView.setVisibility(View.VISIBLE);
                    mRightTextView.setVisibility(View.GONE);

                } else if (value == VISIBLE_TEXT_VIEW_RIGHT) {
                    mLeftTextView.setVisibility(View.GONE);
                    mRightTextView.setVisibility(View.VISIBLE);

                } else {
                    mLeftTextView.setVisibility(View.INVISIBLE);
                    mRightTextView.setVisibility(View.GONE);
                }
            }

            if (typedArray.hasValue(R.styleable.PresenceView_textColor)) {
                @ColorInt int color = typedArray.getColor(R.styleable.PresenceView_textColor, 0);
                mLeftTextView.setTextColor(color);
                mRightTextView.setTextColor(color);
            }

            typedArray.recycle();
        }
    }

    public void setPresence(DbPresence presence, boolean isNightModeEnabled) {
        if (presence != null) {
            switch (presence.getState()) {
                case Enums.Contacts.PresenceStates.AVAILABLE: {
                    mImageView.setVisibility(VISIBLE);
                    mImageView.setImageResource(isNightModeEnabled ? R.drawable.status_mobile_night_square : R.drawable.status_mobile_square);

                    if (presence.getPriority() == Constants.PRESENCE_MOBILE_PRIORITY) {
                        setPresenceText(R.string.general_status_mobile);
                    } else {
                        setPresenceText(R.string.general_status_available);
                    }
                    break;
                }
                case Enums.Contacts.PresenceStates.AWAY: {
                    mImageView.setVisibility(VISIBLE);
                    mImageView.setImageResource(isNightModeEnabled ? R.drawable.status_away_night_square : R.drawable.status_away_square);
                    setPresenceText(R.string.general_status_away);
                    break;
                }
                case Enums.Contacts.PresenceStates.BUSY: {
                    mImageView.setVisibility(VISIBLE);
                    mImageView.setImageResource(isNightModeEnabled ? R.drawable.status_busy_night_square : R.drawable.status_busy_square);
                    setPresenceText(presence.getPriority() == Constants.PRESENCE_ON_CALL_PRIORITY ? R.string.general_status_call : R.string.general_status_busy);
                    break;
                }
                case Enums.Contacts.PresenceStates.OFFLINE: {
                    mImageView.setVisibility(VISIBLE);
                    mImageView.setImageResource(isNightModeEnabled ? R.drawable.status_none_night_square : R.drawable.status_none_square);
                    setPresenceText("");
                    break;
                }
                case Enums.Contacts.PresenceStates.PENDING: {
                    mImageView.setVisibility(VISIBLE);
                    mImageView.setImageResource(isNightModeEnabled ? R.drawable.status_pending_night_square : R.drawable.status_pending_square);
                    setPresenceText(R.string.general_status_pending);
                    break;
                }
                case Enums.Contacts.PresenceStates.NONE:
                    mImageView.setVisibility(INVISIBLE);
                    setPresenceText("");
            }
        }
    }

    private void setPresenceText(@StringRes int resId) {
        setPresenceText(getContext().getText(resId).toString());
    }

    private void setPresenceText(String text) {
        mLeftTextView.setText(mLeftTextView.getVisibility() == View.INVISIBLE ? "" : text);
        mRightTextView.setText(text);
    }

    public void setContentDescriptions(String prefix, Context context) {
        mLeftTextView.setContentDescription(prefix + " " + context.getString(R.string.presence_list_item_text_content_description));
        mRightTextView.setContentDescription(prefix + " " + context.getString(R.string.presence_list_item_text_content_description));
        mImageView.setContentDescription(prefix + " " + context.getString(R.string.presence_image_view_content_description));
    }
}
