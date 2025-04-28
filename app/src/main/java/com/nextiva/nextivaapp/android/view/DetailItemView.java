/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.databinding.ViewDetailItemBinding;

/**
 * Created by adammacdonald on 2/19/18.
 */

@SuppressWarnings("ALL")
public class DetailItemView extends FrameLayout {

    private TextView mTitleTextView;
    private TextView mSubTitleTextView;
    private ImageButton mAction1ImageButton;
    private ImageButton mAction2ImageButton;

    public DetailItemView(@NonNull Context context) {
        this(context, null);
    }

    public DetailItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DetailItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DetailItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        bindViews(inflater);

        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.DetailItemView);

            mTitleTextView.setText(typedArray.getString(R.styleable.DetailItemView_titleText));
            mSubTitleTextView.setText(typedArray.getString(R.styleable.DetailItemView_subTitleText));

            if (typedArray.getResourceId(R.styleable.DetailItemView_action1Drawable, 0) != 0) {
                mAction1ImageButton.setImageResource(typedArray.getResourceId(R.styleable.DetailItemView_action1Drawable, 0));
                mAction1ImageButton.setVisibility(View.VISIBLE);

            } else {
                mAction1ImageButton.setVisibility(View.GONE);
            }

            if (typedArray.getResourceId(R.styleable.DetailItemView_action2Drawable, 0) != 0) {
                mAction2ImageButton.setImageResource(typedArray.getResourceId(R.styleable.DetailItemView_action2Drawable, 0));
                mAction2ImageButton.setVisibility(View.VISIBLE);

            } else {
                mAction2ImageButton.setVisibility(View.GONE);
            }

            if (typedArray.getBoolean(R.styleable.DetailItemView_subTitleModeEnabled, false)) {
                mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.material_text_body1));
                mSubTitleTextView.setVisibility(View.VISIBLE);

            } else {
                mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.material_text_subhead));
                mSubTitleTextView.setVisibility(View.GONE);
            }

            typedArray.recycle();
        }
    }

    private void bindViews(LayoutInflater inflater) {
        ViewDetailItemBinding binding = ViewDetailItemBinding.inflate(inflater, this, true);

        mTitleTextView = binding.detailItemViewTitleTextView;
        mSubTitleTextView = binding.detailItemViewSubTitleTextView;
        mAction1ImageButton = binding.detailItemViewAction1ImageButton;
        mAction2ImageButton = binding.detailItemViewAction2ImageButton;
    }

    public void setAction1Drawable(int drawableInt) {
        if (drawableInt != 0) {
            mAction1ImageButton.setTag(drawableInt);
            mAction1ImageButton.setImageDrawable(getResources().getDrawable(drawableInt));
        } else {
            mAction1ImageButton.setTag(null);
            mAction1ImageButton.setVisibility(View.GONE);
        }
    }

    public void setOnAction1ClickListener(View.OnClickListener clickListener) {
        mAction1ImageButton.setOnClickListener(clickListener);
    }

    public void setAction2Drawable(int drawableInt) {
        if (drawableInt != 0) {
            mAction2ImageButton.setTag(drawableInt);
            mAction2ImageButton.setImageDrawable(getResources().getDrawable(drawableInt));
        } else {
            mAction2ImageButton.setTag(null);
            mAction2ImageButton.setVisibility(View.GONE);
        }
    }

    public void setOnAction2ClickListener(View.OnClickListener clickListener) {
        mAction2ImageButton.setOnClickListener(clickListener);
    }

    public void setAction2Enabled(boolean isEnabled) {
        mAction2ImageButton.setEnabled(isEnabled);
    }

    public String getTitleText() {
        return mTitleTextView.getText().toString();
    }

    public String getSubTitleText() {
        return mSubTitleTextView.getText().toString();
    }

    public void setTitleText(String text) {
        mTitleTextView.setText(text);
    }

    public void setSubTitleEnabled(boolean enabled) {
        if (enabled) {
            mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.material_text_body1));
            mSubTitleTextView.setVisibility(View.VISIBLE);

        } else {
            mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.material_text_subhead));
            mSubTitleTextView.setVisibility(View.GONE);
        }
    }

    public boolean isSubTitleEnabled() {
        return mSubTitleTextView.getVisibility() == View.VISIBLE;
    }

    public void setSubTitleText(String text) {
        mSubTitleTextView.setText(text);
    }

    public void setAction1Visible(boolean isVisible) {
        mAction1ImageButton.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public void setAction2Visible(boolean isVisible) {
        mAction2ImageButton.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public void addTitleTextWatcher(TextWatcher textWatcher) {
        mTitleTextView.addTextChangedListener(textWatcher);
    }

    public void addSubTitleTextWatcher(TextWatcher textWatcher) {
        mSubTitleTextView.addTextChangedListener(textWatcher);
    }

    public void setContentDescriptions(Context context) {
        mTitleTextView.setContentDescription(mTitleTextView.getText());
        mSubTitleTextView.setContentDescription(context.getString(R.string.detail_list_item_subtitle_content_description, mTitleTextView.getText()));

        if (mAction1ImageButton.getTag() != null && mAction1ImageButton.getTag() instanceof Integer && (Integer) mAction1ImageButton.getTag() == R.drawable.ic_call) {
            mAction1ImageButton.setContentDescription(context.getString(R.string.detail_list_item_voice_call_button_content_description, mTitleTextView.getText()));

        } else if (mAction1ImageButton.getTag() != null) {
            mAction1ImageButton.setContentDescription(context.getString(R.string.detail_list_item_button_content_description, mTitleTextView.getText()));
        }

        if (mAction2ImageButton.getTag() != null && mAction2ImageButton.getTag() instanceof Integer && (Integer) mAction2ImageButton.getTag() == R.drawable.ic_video) {
            mAction2ImageButton.setContentDescription(context.getString(R.string.detail_list_item_video_call_button_content_description, mTitleTextView.getText()));

        } else if (mAction2ImageButton.getTag() != null) {
            mAction2ImageButton.setContentDescription(context.getString(R.string.detail_list_item_button_content_description, mTitleTextView.getText()));
        }
    }
}
