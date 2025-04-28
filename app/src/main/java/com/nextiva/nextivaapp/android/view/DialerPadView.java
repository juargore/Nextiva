/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.databinding.ViewDialerPadBinding;

/**
 * Created by adammacdonald on 2/5/18.
 */

public class DialerPadView extends FrameLayout {

    protected DialerKeyView mKey0DialerKeyView;
    protected DialerKeyView mKey1DialerKeyView;
    protected DialerKeyView mKey2DialerKeyView;
    protected DialerKeyView mKey3DialerKeyView;
    protected DialerKeyView mKey4DialerKeyView;
    protected DialerKeyView mKey5DialerKeyView;
    protected DialerKeyView mKey6DialerKeyView;
    protected DialerKeyView mKey7DialerKeyView;
    protected DialerKeyView mKey8DialerKeyView;
    protected DialerKeyView mKey9DialerKeyView;
    protected DialerKeyView mKeyStarDialerKeyView;
    protected DialerKeyView mKeyPoundDialerKeyView;

    private DialerPadClickListener mDialerPadClickListener;

    private boolean mIsVoicemailEnabled = true;

    public DialerPadView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public DialerPadView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DialerPadView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DialerPadView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(@NonNull Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        bindViews(inflater);
    }

    private void bindViews(LayoutInflater inflater) {
        ViewDialerPadBinding binding = ViewDialerPadBinding.inflate(inflater, this, true);

        mKey0DialerKeyView = binding.dialerPad0Key;
        mKey1DialerKeyView = binding.dialerPad1Key;
        mKey2DialerKeyView = binding.dialerPad2Key;
        mKey3DialerKeyView = binding.dialerPad3Key;
        mKey4DialerKeyView = binding.dialerPad4Key;
        mKey5DialerKeyView = binding.dialerPad5Key;
        mKey6DialerKeyView = binding.dialerPad6Key;
        mKey7DialerKeyView = binding.dialerPad7Key;
        mKey8DialerKeyView = binding.dialerPad8Key;
        mKey9DialerKeyView = binding.dialerPad9Key;
        mKeyStarDialerKeyView = binding.dialerPadStarKey;
        mKeyPoundDialerKeyView = binding.dialerPadPoundKey;

        binding.dialerPad0Key.setOnClickListener(v -> onKey0Clicked());
        binding.dialerPad0Key.setOnLongClickListener(v -> {onKey0LongClicked(v); return false;});
        binding.dialerPad1Key.setOnClickListener(v -> onKey1Clicked());
        binding.dialerPad1Key.setOnLongClickListener(this::onKey1LongClicked);
        binding.dialerPad2Key.setOnClickListener(v -> onKey2Clicked());
        binding.dialerPad3Key.setOnClickListener(v -> onKey3Clicked());
        binding.dialerPad4Key.setOnClickListener(v -> onKey4Clicked());
        binding.dialerPad5Key.setOnClickListener(v -> onKey5Clicked());
        binding.dialerPad6Key.setOnClickListener(v -> onKey6Clicked());
        binding.dialerPad7Key.setOnClickListener(v -> onKey7Clicked());
        binding.dialerPad8Key.setOnClickListener(v -> onKey8Clicked());
        binding.dialerPad9Key.setOnClickListener(v -> onKey9Clicked());
        binding.dialerPadStarKey.setOnClickListener(v -> onKeyStarClicked());
        binding.dialerPadStarKey.setOnLongClickListener(v -> {onKeyStarLongClicked(v); return false;});
        binding.dialerPadPoundKey.setOnClickListener(v -> onKeyPoundClicked());
        binding.dialerPadPoundKey.setOnLongClickListener(v -> {onKeyPoundLongClicked(v); return false;});
    }

    protected void onKey1Clicked() {
        if (mDialerPadClickListener != null) {
            mDialerPadClickListener.onKeyPressed("1");
        }
    }

    protected boolean onKey1LongClicked(View view) {
        if (mIsVoicemailEnabled) {
            if (mDialerPadClickListener != null) {
                mDialerPadClickListener.onVoiceMailPressed();
            }

            view.onTouchEvent(MotionEvent.obtain(0, 0, MotionEvent.ACTION_CANCEL, 0, 0, 0));

            return true;

        } else {
            return false;
        }
    }

    protected void onKey2Clicked() {
        if (mDialerPadClickListener != null) {
            mDialerPadClickListener.onKeyPressed("2");
        }
    }

    protected void onKey3Clicked() {
        if (mDialerPadClickListener != null) {
            mDialerPadClickListener.onKeyPressed("3");
        }
    }

    protected void onKey4Clicked() {
        if (mDialerPadClickListener != null) {
            mDialerPadClickListener.onKeyPressed("4");
        }
    }

    protected void onKey5Clicked() {
        if (mDialerPadClickListener != null) {
            mDialerPadClickListener.onKeyPressed("5");
        }
    }

    protected void onKey6Clicked() {
        if (mDialerPadClickListener != null) {
            mDialerPadClickListener.onKeyPressed("6");
        }
    }

    protected void onKey7Clicked() {
        if (mDialerPadClickListener != null) {
            mDialerPadClickListener.onKeyPressed("7");
        }
    }

    protected void onKey8Clicked() {
        if (mDialerPadClickListener != null) {
            mDialerPadClickListener.onKeyPressed("8");
        }
    }

    protected void onKey9Clicked() {
        if (mDialerPadClickListener != null) {
            mDialerPadClickListener.onKeyPressed("9");
        }
    }

    protected void onKey0Clicked() {
        if (mDialerPadClickListener != null) {
            mDialerPadClickListener.onKeyPressed("0");
        }
    }

    protected void onKey0LongClicked(View view) {
        if (mDialerPadClickListener != null) {
            mDialerPadClickListener.onKeyPressed("+");
        }

        view.onTouchEvent(MotionEvent.obtain(0, 0, MotionEvent.ACTION_CANCEL, 0, 0, 0));

    }

    protected void onKeyStarClicked() {
        if (mDialerPadClickListener != null) {
            mDialerPadClickListener.onKeyPressed("*");
        }
    }

    protected void onKeyStarLongClicked(View view) {
        if (mDialerPadClickListener != null) {
            mDialerPadClickListener.onKeyPressed(",");
        }

        view.onTouchEvent(MotionEvent.obtain(0, 0, MotionEvent.ACTION_CANCEL, 0, 0, 0));

    }

    protected void onKeyPoundClicked() {
        if (mDialerPadClickListener != null) {
            mDialerPadClickListener.onKeyPressed("#");
        }
    }

    protected void onKeyPoundLongClicked(View view) {
        if (mDialerPadClickListener != null) {
            mDialerPadClickListener.onKeyPressed(";");
        }

        view.onTouchEvent(MotionEvent.obtain(0, 0, MotionEvent.ACTION_CANCEL, 0, 0, 0));

    }

    public void setVoicemailEnabled(boolean voicemailEnabled) {
        mIsVoicemailEnabled = voicemailEnabled;
        mKey1DialerKeyView.setSubTitleImageResource(mIsVoicemailEnabled ? R.drawable.ic_voicemail : 0);
    }

    public void setDialerPadClickListener(DialerPadClickListener dialerPadClickListener) {
        mDialerPadClickListener = dialerPadClickListener;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setKeyTouchListeners(OnTouchListener onTouchListener) {
        setOnTouchListener(onTouchListener);
        mKey0DialerKeyView.setOnTouchListener(onTouchListener);
        mKey1DialerKeyView.setOnTouchListener(onTouchListener);
        mKey2DialerKeyView.setOnTouchListener(onTouchListener);
        mKey3DialerKeyView.setOnTouchListener(onTouchListener);
        mKey4DialerKeyView.setOnTouchListener(onTouchListener);
        mKey5DialerKeyView.setOnTouchListener(onTouchListener);
        mKey6DialerKeyView.setOnTouchListener(onTouchListener);
        mKey7DialerKeyView.setOnTouchListener(onTouchListener);
        mKey8DialerKeyView.setOnTouchListener(onTouchListener);
        mKey9DialerKeyView.setOnTouchListener(onTouchListener);
        mKeyStarDialerKeyView.setOnTouchListener(onTouchListener);
        mKeyPoundDialerKeyView.setOnTouchListener(onTouchListener);
    }

    public interface DialerPadClickListener {
        void onKeyPressed(@NonNull String key);

        void onVoiceMailPressed();
    }
}
