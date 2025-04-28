/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.databinding.DialogFragmentDialerKeypadBinding;
import com.nextiva.nextivaapp.android.databinding.IncludeDialerPadBinding;
import com.nextiva.nextivaapp.android.util.LogUtil;
import com.nextiva.nextivaapp.android.view.DialerPadView;
import com.nextiva.nextivaapp.android.viewmodels.DialerKeypadDialogViewModel;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Created by adammacdonald on 3/2/18.
 */

@AndroidEntryPoint
public class DialerKeypadDialogFragment extends BaseDialogFragment implements
        DialerPadView.DialerPadClickListener {

    protected HorizontalScrollView mInputHorizontalScrollView;
    protected TextView mInputTextView;
    protected DialerPadView mDialerPadView;


    private DialerKeypadDialogViewModel mViewModel;

    private DialerPadView.DialerPadClickListener mDialerPadClickListener;

    public static DialerKeypadDialogFragment newInstance() {
        Bundle args = new Bundle();

        DialerKeypadDialogFragment fragment = new DialerKeypadDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() == null) {
            return;
        }

        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme_DialogFragment);

        mViewModel = new ViewModelProvider(this).get(DialerKeypadDialogViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = bindViews(inflater, container);

        mInputHorizontalScrollView.setHorizontalScrollBarEnabled(false);
        mInputTextView.setSelected(true);
        mDialerPadView.setVoicemailEnabled(false);
        mDialerPadView.setDialerPadClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = getResources().getDimensionPixelSize(R.dimen.dial_pad_dialog_fragment_width);
            int height = getResources().getDimensionPixelSize(R.dimen.dial_pad_dialog_fragment_height);
            getDialog().getWindow().setLayout(width, height);

            /* This will make the dialog fill the screen but requires the back button to close it.
            LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = TableLayout.LayoutParams.MATCH_PARENT;
            params.height = TableLayout.LayoutParams.MATCH_PARENT;
            getDialog().getWindow().setAttributes(params);*/
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mDialerPadClickListener = (DialerPadView.DialerPadClickListener) getParentFragment();
        } catch (ClassCastException e) {
            if (getParentFragment() != null) {
                LogUtil.log(getParentFragment().getClass().getSimpleName() + " is probably meant to implement DialerPadView.DialerPadClickListener.");
            }
        }
    }

    private View bindViews(LayoutInflater inflater, ViewGroup container) {
        DialogFragmentDialerKeypadBinding binding = DialogFragmentDialerKeypadBinding.inflate(inflater, container, false);
        IncludeDialerPadBinding mergeBinding = IncludeDialerPadBinding.bind(binding.getRoot());

        mInputHorizontalScrollView = binding.dialogDialerKeypadInputScrollView;
        mInputTextView = binding.dialogDialerKeypadInputTextView;
        mDialerPadView = mergeBinding.dialerPadIncludeDialerPadView;

        return binding.getRoot();
    }

    // --------------------------------------------------------------------------------------------
    // DialerPadView.DialerPadClickListener Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void onKeyPressed(@NonNull String key) {
        mInputTextView.append(key);

        if (mDialerPadClickListener != null) {
            mDialerPadClickListener.onKeyPressed(key);
        }

        mInputHorizontalScrollView.post(() -> {
            mInputTextView.setGravity(mInputTextView.getWidth() > mInputHorizontalScrollView.getWidth() ?
                                              Gravity.START : Gravity.CENTER);
            mInputHorizontalScrollView.fullScroll(View.FOCUS_RIGHT);
        });
    }

    @Override
    public void onVoiceMailPressed() {
    }
    // --------------------------------------------------------------------------------------------

    @VisibleForTesting
    public void setDialerPadClickListener(DialerPadView.DialerPadClickListener listener) {
        mDialerPadClickListener = listener;
    }
}
