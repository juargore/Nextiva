/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.databinding.FragmentAboutLegalNoticesBinding;
import com.nextiva.nextivaapp.android.util.StringUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutLegalNoticesFragment extends BaseFragment {

    protected TextView mLegalNoticesTextView;

    public AboutLegalNoticesFragment() {
        // Required empty public constructor
    }

    public static AboutLegalNoticesFragment newInstance() {
        Bundle args = new Bundle();

        AboutLegalNoticesFragment fragment = new AboutLegalNoticesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = bindViews(inflater, container);

        mLegalNoticesTextView.setText(StringUtil.fromHtml(getString(R.string.about_legal_notice)));
        // Inflate the layout for this fragment
        return view;
    }

    public View bindViews(LayoutInflater inflater, ViewGroup container) {
        FragmentAboutLegalNoticesBinding binding = FragmentAboutLegalNoticesBinding.inflate(inflater, container, false);

        mLegalNoticesTextView = binding.aboutLegalNoticesTextView;

        return binding.getRoot();
    }
}
