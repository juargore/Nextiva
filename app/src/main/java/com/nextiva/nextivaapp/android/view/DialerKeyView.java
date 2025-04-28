/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager;
import com.nextiva.nextivaapp.android.util.ApplicationUtil;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Created by adammacdonald on 2/5/18.
 */

@AndroidEntryPoint
public class DialerKeyView extends FrameLayout {

    @Inject
    protected SettingsManager mSettingsManager;

    private TextView mSubTitleTextView;
    private ImageView mSubTitleImageView;

    public DialerKeyView(@NonNull Context context) {
        this(context, null);
    }

    public DialerKeyView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DialerKeyView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DialerKeyView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            inflater.inflate(R.layout.view_dialer_key, this, true);
        }


        final TextView titleTextView = findViewById(R.id.dialer_key_title_text_view);
        mSubTitleTextView = findViewById(R.id.dialer_key_subtitle_text_view);
        mSubTitleImageView = findViewById(R.id.dialer_key_subtitle_image_view);

        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.DialerKeyView);

            titleTextView.setText(typedArray.getString(R.styleable.DialerKeyView_titleText));

            if (!TextUtils.isEmpty(typedArray.getString(R.styleable.DialerKeyView_subTitleText))) {
                mSubTitleTextView.setText(typedArray.getString(R.styleable.DialerKeyView_subTitleText));
                mSubTitleImageView.setVisibility(View.GONE);

            } else if (typedArray.hasValue(R.styleable.DialerKeyView_subTitleSrc)) {
                mSubTitleImageView.setImageResource(typedArray.getResourceId(R.styleable.DialerKeyView_subTitleSrc, 0));
                mSubTitleTextView.setVisibility(View.GONE);
                mSubTitleImageView.setVisibility(View.VISIBLE);

            } else {
                if (titleTextView.getText().equals("*")){
                    mSubTitleImageView.setVisibility(View.GONE);
                    mSubTitleTextView.setVisibility(View.GONE);
                }else {
                    mSubTitleImageView.setVisibility(View.VISIBLE);
                    mSubTitleTextView.setVisibility(View.GONE);
                }
            }

            if (typedArray.hasValue(R.styleable.DialerKeyView_titleTint)) {
                @ColorInt int titleColor = typedArray.getColor(R.styleable.DialerKeyView_titleTint, 0);

                titleTextView.setTextColor(ApplicationUtil.isNightModeEnabled(context, mSettingsManager) ? ContextCompat.getColor(getContext(), R.color.white) : titleColor);
            }

            if (typedArray.hasValue(R.styleable.DialerKeyView_subTitleTint)) {
                @ColorInt int subTitleColor = typedArray.getColor(R.styleable.DialerKeyView_subTitleTint, 0);

                mSubTitleTextView.setTextColor(subTitleColor);
                mSubTitleImageView.setColorFilter(subTitleColor);
            }

            if (typedArray.hasValue(R.styleable.DialerKeyView_titleTextSize)) {
                titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, typedArray.getDimensionPixelSize(R.styleable.DialerKeyView_titleTextSize, 0));
            }

            if (mSubTitleTextView.getText().equals("+")){
                mSubTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.material_text_title));
            }

            typedArray.recycle();
        }

        titleTextView.setContentDescription(titleTextView.getText());
        mSubTitleTextView.setContentDescription(mSubTitleTextView.getText());
        setBackgroundResource(R.drawable.selector_nextiva_grey03);
    }

    public void setSubTitleImageResource(@DrawableRes int resId) {
        if (resId != 0) {
            mSubTitleImageView.setImageResource(resId);
            mSubTitleTextView.setVisibility(View.GONE);
            mSubTitleImageView.setVisibility(View.VISIBLE);

        } else {
            mSubTitleImageView.setVisibility(View.INVISIBLE);
        }
    }
}
